package l2s.gameserver.model.entity.events.impl;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import l2s.commons.collections.JoinedIterator;
import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.listener.actor.player.OnPlayerExitListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.base.TeamType;
import l2s.gameserver.model.entity.events.GlobalEvent;
import l2s.gameserver.model.entity.events.objects.DuelSnapshotObject;
import l2s.gameserver.model.entity.olympiad.Olympiad;
import l2s.gameserver.network.l2.components.IStaticPacket;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExDuelStart;
import l2s.gameserver.network.l2.s2c.ExDuelUpdateUserInfo;
import l2s.gameserver.network.l2.s2c.PlaySoundPacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;

/**
 * @author VISTALL
 * @date 2:10/26.06.2011
 */
public abstract class DuelEvent extends GlobalEvent implements Iterable<DuelSnapshotObject>
{
	private class OnPlayerExitListenerImpl implements OnPlayerExitListener
	{
		@Override
		public void onPlayerExit(Player player)
		{
			playerExit(player);
		}
	}

	public static final String RED_TEAM = TeamType.RED.name();
	public static final String BLUE_TEAM = TeamType.BLUE.name();

	protected OnPlayerExitListener _playerExitListener = new OnPlayerExitListenerImpl();
	protected TeamType _winner = TeamType.NONE;
	protected boolean _aborted;
	protected AtomicBoolean _ended = new AtomicBoolean(false);

	public DuelEvent(MultiValueSet<String> set)
	{
		super(set);
	}

	protected DuelEvent(int id, String name)
	{
		super(id, name);
	}

	@Override
	public void initEvent()
	{
		//
	}

	public abstract boolean canDuel(Player player, Player target, boolean first);

	public abstract void askDuel(Player player, GameObject target);

	public abstract void createDuel(Player player, Player target);

	public abstract void playerExit(Player player);

	public abstract void packetSurrender(Player player);

	public abstract void onDie(Player player);

	public abstract int getDuelType();

	@Override
	public void startEvent()
	{
		updatePlayers(true, false);

		sendPackets(new ExDuelStart(this), PlaySoundPacket.B04_S01, SystemMsg.LET_THE_DUEL_BEGIN);

		for(DuelSnapshotObject $snapshot : this)
			sendPacket(new ExDuelUpdateUserInfo($snapshot.getPlayer()), $snapshot.getTeam().revert().name());

		for(DuelSnapshotObject $snapshot : this)
		{
			if($snapshot.getPlayer().isInZonePeace())
			{
				DuelEvent duelEvent = $snapshot.getPlayer().getEvent(DuelEvent.class);
				duelEvent.abortDuel($snapshot.getPlayer());
			}
		}
	}

	public void sendPacket(IStaticPacket packet, String... ar)
	{
		for(String a : ar)
		{
			List<DuelSnapshotObject> objs = getObjects(a);

			for(DuelSnapshotObject obj : objs)
				obj.getPlayer().sendPacket(packet);
		}
	}

	public void sendPacket(IStaticPacket packet)
	{
		sendPackets(packet);
	}

	public void sendPackets(IStaticPacket... packet)
	{
		for(DuelSnapshotObject d : this)
			d.getPlayer().sendPacket(packet);
	}

	public void abortDuel(Player player)
	{
		_aborted = true;
		_winner = TeamType.NONE;

		stopEvent();
	}

	protected IStaticPacket canDuel0(Player requestor, Player target)
	{
		IStaticPacket packet = null;
		if(target.isInCombat())
			packet = new SystemMessagePacket(SystemMsg.C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_ENGAGED_IN_BATTLE).addName(target);
		else if(target.isDead() || target.isAlikeDead() || target.getCurrentHpPercents() < 50 || target.getCurrentMpPercents() < 50 || target.getCurrentCpPercents() < 50)
			packet = new SystemMessagePacket(SystemMsg.C1_CANNOT_DUEL_BECAUSE_C1S_HP_OR_MP_IS_BELOW_50).addName(target);
		else if(target.getEvent(DuelEvent.class) != null)
			packet = new SystemMessagePacket(SystemMsg.C1_CANNOT_DUEL_BECAUSE_C1_IS_ALREADY_ENGAGED_IN_A_DUEL).addName(target);
		else if(target.getEvent(ClanHallSiegeEvent.class) != null || target.getEvent(ClanHallNpcSiegeEvent.class) != null)
			packet = new SystemMessagePacket(SystemMsg.C1_CANNOT_DUEL_BECAUSE_C1_IS_PARTICIPATING_IN_A_CLAN_HALL_WAR).addName(target);
		else if(target.getEvent(SiegeEvent.class) != null)
			packet = new SystemMessagePacket(SystemMsg.C1_CANNOT_DUEL_BECAUSE_C1_IS_PARTICIPATING_IN_A_SIEGE_WAR).addName(target);
		else if(target.isInOlympiadMode() || Olympiad.isRegisteredInComp(target) || requestor.isInOlympiadMode() || Olympiad.isRegisteredInComp(requestor) || target.isChaosFestivalParticipant() || target.isRegisteredInChaosFestival() || target.getLfcGame() != null)
			packet = new SystemMessagePacket(SystemMsg.C1_CANNOT_DUEL_BECAUSE_C1_IS_PARTICIPATING_IN_THE_OLYMPIAD).addName(target);
		else if(target.isCursedWeaponEquipped() || target.isPK() || target.getPvpFlag() > 0)
			packet = new SystemMessagePacket(SystemMsg.C1_CANNOT_DUEL_BECAUSE_C1_IS_IN_A_CHAOTIC_STATE).addName(target);
		else if(target.isInStoreMode())
			packet = new SystemMessagePacket(SystemMsg.C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_ENGAGED_IN_A_PRIVATE_STORE_OR_MANUFACTURE).addName(target);
		else if(target.isMounted() || target.isInBoat())
			packet = new SystemMessagePacket(SystemMsg.C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_RIDING_A_BOAT_STEED_OR_STRIDER).addName(target);
		else if(target.isFishing())
			packet = new SystemMessagePacket(SystemMsg.C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_FISHING).addName(target);
		else if(target.isInCombatZone() || target.isInPeaceZone() || target.isInWater() || target.isInZone(Zone.ZoneType.no_restart))
			packet = new SystemMessagePacket(SystemMsg.C1_CANNOT_MAKE_A_CHALLENGE_TO_A_DUEL_BECAUSE_C1_IS_CURRENTLY_IN_A_DUELPROHIBITED_AREA_PEACEFUL_ZONE__SEVEN_SIGNS_ZONE__NEAR_WATER__RESTART_PROHIBITED_AREA).addName(target);
		else if(!requestor.isInRangeZ(target, 1200))
			packet = new SystemMessagePacket(SystemMsg.C1_CANNOT_RECEIVE_A_DUEL_CHALLENGE_BECAUSE_C1_IS_TOO_FAR_AWAY).addName(target);
		else if(target.isTransformed())
			packet = new SystemMessagePacket(SystemMsg.C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_POLYMORPHED).addName(target);
		return packet;
	}

	protected void updatePlayers(boolean start, boolean teleport)
	{
		for(DuelSnapshotObject $snapshot : this)
		{
			if(teleport)
				$snapshot.teleport();
			else
			{
				if(start)
				{
					$snapshot.getPlayer().addEvent(this);
					$snapshot.store();

					$snapshot.getPlayer().setTeam($snapshot.getTeam());
				}
				else
				{
					$snapshot.getPlayer().removeEvent(this);
					$snapshot.restore(_aborted);

					$snapshot.getPlayer().setTeam(TeamType.NONE);
				}
			}
		}
	}

	@Override
	public SystemMsg checkForAttack(Creature target, Creature attacker, Skill skill, boolean force)
	{
		if(target.getTeam() == TeamType.NONE || attacker.getTeam() == TeamType.NONE || target.getTeam() == attacker.getTeam())
			return SystemMsg.INVALID_TARGET;

		DuelEvent duelEvent = target.getEvent(DuelEvent.class);
		if(duelEvent == null || duelEvent != this)
			return SystemMsg.INVALID_TARGET;

		return null;
	}

	@Override
	public boolean canAttack(Creature target, Creature attacker, Skill skill, boolean force)
	{
		if(target.getTeam() == TeamType.NONE || attacker.getTeam() == TeamType.NONE || target.getTeam() == attacker.getTeam())
			return false;

		DuelEvent duelEvent = target.getEvent(DuelEvent.class);
		return !(duelEvent == null || duelEvent != this);
	}

	@Override
	public void onAddEvent(GameObject o)
	{
		if(o.isPlayer())
			o.getPlayer().addListener(_playerExitListener);
	}

	@Override
	public void onRemoveEvent(GameObject o)
	{
		if(o.isPlayer())
			o.getPlayer().removeListener(_playerExitListener);
	}

	@Override
	public Iterator<DuelSnapshotObject> iterator()
	{
		List<DuelSnapshotObject> blue = getObjects(BLUE_TEAM);
		List<DuelSnapshotObject> red = getObjects(RED_TEAM);
		return new JoinedIterator<DuelSnapshotObject>(blue.iterator(), red.iterator());
	}

	@Override
	public void reCalcNextTime(boolean onInit)
	{
		registerActions();
	}

	@Override
	public void announce(SystemMsg msgId, int i, int time)
	{
		sendPacket(new SystemMessagePacket(SystemMsg.THE_DUEL_WILL_BEGIN_IN_S1_SECONDS).addInteger(i));
	}

	@Override
	public void broadcastStatusUpdate(Creature creature)
	{
		if(!creature.isPlayer())
			return;

		Player player = creature.getPlayer();
		sendPacket(new ExDuelUpdateUserInfo(player), player.getTeam().revert().name());
	}
}
