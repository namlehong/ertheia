package l2s.gameserver.model.entity.events.impl;

import java.util.Iterator;
import java.util.List;

import l2s.commons.collections.CollectionUtils;
import l2s.commons.collections.JoinedIterator;
import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.data.xml.holder.InstantZoneHolder;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Party;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Request;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.World;
import l2s.gameserver.model.base.TeamType;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.entity.events.objects.DuelSnapshotObject;
import l2s.gameserver.network.l2.components.IStaticPacket;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExDuelAskStart;
import l2s.gameserver.network.l2.s2c.ExDuelEnd;
import l2s.gameserver.network.l2.s2c.ExDuelReady;
import l2s.gameserver.network.l2.s2c.SocialActionPacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.templates.InstantZone;

/**
 * @author VISTALL
 * @date 3:22/29.06.2011
 */
public class PartyVsPartyDuelEvent extends DuelEvent
{
	public PartyVsPartyDuelEvent(MultiValueSet<String> set)
	{
		super(set);
	}

	protected PartyVsPartyDuelEvent(int id, String name)
	{
		super(id, name);
	}

	@Override
	public void stopEvent()
	{
		if(_ended.compareAndSet(true, false))
			return;

		clearActions();

		updatePlayers(false, false);

		for(DuelSnapshotObject d : this)
		{
			d.getPlayer().sendPacket(new ExDuelEnd(this));
			GameObject target = d.getPlayer().getTarget();
			if(target != null)
				d.getPlayer().getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, target);
		}

		switch(_winner)
		{
			case NONE:
				sendPacket(SystemMsg.THE_DUEL_HAS_ENDED_IN_A_TIE);
				break;
			case RED:
			case BLUE:
				List<DuelSnapshotObject> winners = getObjects(_winner.name());
				List<DuelSnapshotObject> lossers = getObjects(_winner.revert().name());

				DuelSnapshotObject winner = CollectionUtils.safeGet(winners, 0);
				if(winner != null)
				{
					sendPacket(new SystemMessagePacket(SystemMsg.C1S_PARTY_HAS_WON_THE_DUEL).addName(winners.get(0).getPlayer()));

					for(DuelSnapshotObject d : lossers)
						d.getPlayer().broadcastPacket(new SocialActionPacket(d.getPlayer().getObjectId(), SocialActionPacket.BOW));
				}
				else
					sendPacket(SystemMsg.THE_DUEL_HAS_ENDED_IN_A_TIE);
				break;
		}

		updatePlayers(false, true);
		removeObjects(RED_TEAM);
		removeObjects(BLUE_TEAM);
	}

	@Override
	public void teleportPlayers(String name)
	{
		InstantZone instantZone = InstantZoneHolder.getInstance().getInstantZone(1);

		Reflection reflection = new Reflection();
		reflection.init(instantZone);

		List<DuelSnapshotObject> team = getObjects(BLUE_TEAM);

		for(int i = 0; i < team.size(); i++)
		{
			DuelSnapshotObject $member = team.get(i);

			$member.getPlayer().addEvent(this);
			$member.getPlayer().setStablePoint($member.getLoc());
			$member.getPlayer().teleToLocation(instantZone.getTeleportCoords().get(i), reflection);
		}

		team = getObjects(RED_TEAM);

		for(int i = 0; i < team.size(); i++)
		{
			DuelSnapshotObject $member = team.get(i);

			$member.getPlayer().addEvent(this);
			$member.getPlayer().setStablePoint($member.getLoc());
			$member.getPlayer().teleToLocation(instantZone.getTeleportCoords().get(9 + i), reflection);
		}
	}

	@Override
	public boolean canDuel(Player player, Player target, boolean first)
	{
		if(player.getParty() == null)
		{
			player.sendPacket(SystemMsg.YOU_ARE_UNABLE_TO_REQUEST_A_DUEL_AT_THIS_TIME);
			return false;
		}

		if(target.getParty() == null)
		{
			player.sendPacket(SystemMsg.SINCE_THE_PERSON_YOU_CHALLENGED_IS_NOT_CURRENTLY_IN_A_PARTY_THEY_CANNOT_DUEL_AGAINST_YOUR_PARTY);
			return false;
		}

		Party party1 = player.getParty();
		Party party2 = target.getParty();
		if(player != party1.getPartyLeader() || target != party2.getPartyLeader())
		{
			player.sendPacket(SystemMsg.YOU_ARE_UNABLE_TO_REQUEST_A_DUEL_AT_THIS_TIME);
			return false;
		}

		Iterator<Player> iterator = new JoinedIterator<Player>(party1.iterator(), party2.iterator());
		while(iterator.hasNext())
		{
			Player $member = iterator.next();

			IStaticPacket packet = null;
			if((packet = canDuel0(player, $member)) != null)
			{
				player.sendPacket(packet);
				target.sendPacket(packet);
				return false;
			}
		}
		return true;
	}

	@Override
	public void askDuel(Player player, GameObject target)
	{
		Request request = new Request(Request.L2RequestType.DUEL, player, target.getPlayer()).setTimeout(10000L);
		request.set("duelType", 1);
		player.setRequest(request);
		player.sendPacket(new SystemMessagePacket(SystemMsg.C1S_PARTY_HAS_BEEN_CHALLENGED_TO_A_DUEL).addName(target));
		if(target.isPlayer())
		{
			target.getPlayer().setRequest(request);
			target.getPlayer().sendPacket(new SystemMessagePacket(SystemMsg.C1S_PARTY_HAS_CHALLENGED_YOUR_PARTY_TO_A_DUEL).addName(player), new ExDuelAskStart(player.getName(), 1));
		}
	}

	@Override
	public void createDuel(Player player, Player target)
	{
		PartyVsPartyDuelEvent duelEvent = new PartyVsPartyDuelEvent(getDuelType(), player.getObjectId() + "_" + target.getObjectId() + "_duel");
		cloneTo(duelEvent);

		for(Player $member : player.getParty())
			duelEvent.addObject(BLUE_TEAM, new DuelSnapshotObject($member, TeamType.BLUE));

		for(Player $member : target.getParty())
			duelEvent.addObject(RED_TEAM, new DuelSnapshotObject($member, TeamType.RED));

		duelEvent.sendPacket(new ExDuelReady(this));
		duelEvent.reCalcNextTime(false);
	}

	@Override
	public void playerExit(Player player)
	{
		for(DuelSnapshotObject $snapshot : this)
		{
			if($snapshot.getPlayer() == player)
				removeObject($snapshot.getTeam().name(), $snapshot);

			List<DuelSnapshotObject> objects = getObjects($snapshot.getTeam().name());
			if(objects.isEmpty())
			{
				_winner = $snapshot.getTeam().revert();
				stopEvent();
			}
		}
	}

	@Override
	public void packetSurrender(Player player)
	{
		//
	}

	@Override
	public void onDie(Player player)
	{
		TeamType team = player.getTeam();
		if(team == TeamType.NONE || _aborted)
			return;

		sendPacket(SystemMsg.THE_OTHER_PARTY_IS_FROZEN, team.revert().name());

		player.stopAttackStanceTask();
		player.startFrozen();
		player.setTeam(TeamType.NONE);

		for(Player $player : World.getAroundPlayers(player))
		{
			$player.getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, player);
			Servitor[] servitors = player.getServitors();
			if(servitors.length > 0)
			{
				for(Servitor servitor : servitors)
					$player.getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, servitor);
			}
		}
		player.sendChanges();

		boolean allDead = true;
		List<DuelSnapshotObject> objs = getObjects(team.name());
		for(DuelSnapshotObject obj : objs)
		{
			if(obj.getPlayer() == player)
				obj.setDead();

			if(!obj.isDead())
				allDead = false;
		}

		if(allDead)
		{
			_winner = team.revert();

			stopEvent();
		}
	}

	@Override
	public int getDuelType()
	{
		return 1;
	}

	@Override
	protected long startTimeMillis()
	{
		return System.currentTimeMillis() + 30000L;
	}
}
