package l2s.gameserver.model.entity.events.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import l2s.commons.geometry.Circle;
import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.data.xml.holder.InstantZoneHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Territory;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.entity.events.impl.ChaosFestivalEvent;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExCuriousHouseMemberList;
import l2s.gameserver.network.l2.s2c.ExCuriousHouseMemberUpdate;
import l2s.gameserver.network.l2.s2c.ExCuriousHouseResult;
import l2s.gameserver.network.l2.s2c.L2GameServerPacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.utils.NpcUtils;

/**
 * @author Bonux
**/
public final class ChaosFestivalArenaObject implements Serializable, Comparable<ChaosFestivalArenaObject>
{
	private class FinishTask extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			int finishDelay = 33 - _taskTemp;
			switch(finishDelay)
			{
				case 33:
					calcWinner();
					showResultBoard();
					spawnRewardBoxes();
					break;
				case 30:
				case 20:
				case 10:
				case 9:
				case 8:
				case 7:
				case 6:
				case 5:
				case 4:
				case 3:
				case 2:
				case 1:
					broadcastPacket(new SystemMessagePacket(SystemMsg.IN_S1_SECONDS_YOU_WILL_BE_MOVED_TO_WHERE_YOU_WERE_BEFORE_PARTICIPATING_IN_THE_CEREMONY_OF_CHAOS).addInteger(finishDelay));
					break;
				case 0:
					finishBattle();
					break;
			}
			_taskTemp++;
		}
	}

	private static final long serialVersionUID = 1L;

	private final ChaosFestivalEvent _event;
	private final int _id;
	private final boolean _pvp;
	private final Reflection _reflection;
	private final Collection<ChaosFestivalPlayerObject> _members = new ConcurrentLinkedQueue<ChaosFestivalPlayerObject>();

	private AtomicBoolean _isBattleBegin = new AtomicBoolean(false);

	private ScheduledFuture<?> _finishTask = null;
	private int _taskTemp = 0;

	private ChaosFestivalPlayerObject _winner = null;

	public ChaosFestivalArenaObject(ChaosFestivalEvent event, int id, boolean pvp, int instanceId)
	{
		_event = event;
		_id = id;
		_pvp = pvp;

		_reflection = new Reflection();
		_reflection.init(InstantZoneHolder.getInstance().getInstantZone(instanceId));
	}

	public ChaosFestivalEvent getEvent()
	{
		return _event;
	}

	public int getId()
	{
		return _id;
	}

	public boolean isPvP()
	{
		return _pvp;
	}

	public Reflection getReflection()
	{
		return _reflection;
	}

	public void addMember(ChaosFestivalPlayerObject member)
	{
		member.setId(_members.size() + 1);
		_members.add(member);
	}

	public ChaosFestivalPlayerObject getMember(Player player)
	{
		for(ChaosFestivalPlayerObject member : _members)
		{
			if(member.getPlayer() == player)
				return member;
		}
		return null;
	}

	public Collection<ChaosFestivalPlayerObject> getMembers()
	{
		return _members;
	}

	public boolean removeMember(ChaosFestivalPlayerObject member)
	{
		if(_winner == member)
			_winner = null;

		return _members.remove(member);
	}

	public ChaosFestivalPlayerObject getWinner()
	{
		return _winner;
	}

	public void broadcastPacket(L2GameServerPacket packet)
	{
		for(ChaosFestivalPlayerObject member : _members)
		{
			Player player = member.getPlayer();
			if(player == null)
				continue;

			player.sendPacket(packet);
		}
	}

	public void teleportPlayers()
	{
		if(_members.size() <= 1)
			return;

		List<ChaosFestivalPlayerObject> members = new ArrayList<ChaosFestivalPlayerObject>(_members);
		Collections.sort(members);

		ExCuriousHouseMemberList packet = new ExCuriousHouseMemberList(getId(), members);
		for(ChaosFestivalPlayerObject member : _members)
		{
			Player player = member.getPlayer();
			if(player == null)
				continue;

			member.teleportPlayer(_event, _reflection);
			player.sendPacket(packet);
		}
	}

	private void startFinishTask()
	{
		_taskTemp = 0;
		_finishTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new FinishTask(), 0L, 1000L);
	}

	private void stopFinishTask()
	{
		_taskTemp = 0;
		if(_finishTask != null)
		{
			_finishTask.cancel(false);
			_finishTask = null;
		}
	}

	public boolean isBattleBegin()
	{
		return _isBattleBegin.get();
	}

	public void startBattle()
	{
		if(!_isBattleBegin.compareAndSet(false, true))
			return;

		for(ChaosFestivalPlayerObject member : _members)
			member.onStartBattle();
	}

	public void stopBattle()
	{
		if(!_isBattleBegin.compareAndSet(true, false))
			return;

		startFinishTask();

		for(ChaosFestivalPlayerObject member : _members)
			member.onStopBattle(_event);
	}

	private void breakBattle()
	{
		removeArenaFromEvent();
		stopFinishTask();
	}

	private void finishBattle()
	{
		breakBattle();

		for(ChaosFestivalPlayerObject member : _members)
			member.onFinishBattle(this);
	}

	public boolean isAllKilled(ChaosFestivalPlayerObject exception)
	{
		for(ChaosFestivalPlayerObject member : _members)
		{
			if(member == exception)
				continue;

			if(member.getPlayer() != null && !member.isKilled())
				return false;
		}
		return true;
	}

	public void broadcastStatusUpdate(Player player)
	{
		ChaosFestivalPlayerObject member = getMember(player);
		if(member == null)
			return;

		broadcastPacket(new ExCuriousHouseMemberUpdate(player));
	}

	public void onDamage(Creature attacker, Player target, double damage)
	{
		ChaosFestivalPlayerObject targetMember = getMember(target);
		if(targetMember == null)
			return;

		ChaosFestivalPlayerObject attackerMember = getMember(attacker.getPlayer());
		if(attackerMember == null)
			return;

		attackerMember.onDamage(damage);
	}

	public void onKill(Creature killer, Player victim)
	{
		ChaosFestivalPlayerObject killerMember = getMember(killer.getPlayer());
		ChaosFestivalPlayerObject victimMember = getMember(victim);
		if(victimMember == null)
			return;

		victimMember.onKill(getEvent(), killerMember);

		if(isAllKilled(killerMember))
			stopBattle();
	}

	public void onTeleport(Player player, int x, int y, int z, Reflection reflection)
	{
		if(reflection == _reflection)
			return;

		ChaosFestivalPlayerObject member = getMember(player);
		if(member == null)
			return;

		member.onTeleport(_event, player, x, y, z, reflection);

		if(_members.size() == 1)
			stopBattle();
		else if(_members.size() == 0)
			breakBattle();
	}

	public void onExit(Player player)
	{
		ChaosFestivalPlayerObject member = getMember(player);
		if(member == null)
			return;

		member.onExit(_event);

		if(_members.size() == 1)
			stopBattle();
		else if(_members.size() == 0)
			breakBattle();
	}

	private void removeArenaFromEvent()
	{
		_event.removeArena(this);
	}

	private void calcWinner()
	{
		List<ChaosFestivalPlayerObject> members = new ArrayList<ChaosFestivalPlayerObject>(_members);
		Collections.sort(members, new ChaosFestivalPlayerObject.WinnerComparator());

		ChaosFestivalPlayerObject winner = members.get(0);
		if(winner.getKills() > 0)
			_winner = winner;
	}

	private void showResultBoard()
	{
		List<ChaosFestivalPlayerObject> members = new ArrayList<ChaosFestivalPlayerObject>(_members);
		Collections.sort(members);

		for(ChaosFestivalPlayerObject member : _members)
		{
			Player player = member.getPlayer();
			if(player == null)
				continue;

			ExCuriousHouseResult.ResultState state = ExCuriousHouseResult.ResultState.LOSE;
			if(_winner == null)
				state = ExCuriousHouseResult.ResultState.TIE;
			else if(_winner == member)
				state = ExCuriousHouseResult.ResultState.WIN;

			if(_winner != null)
				player.sendPacket(new SystemMessagePacket(SystemMsg.CONGRATULATIONS_C1_YOU_WIN_THE_MATCH).addString(new CustomMessage("chaos_festival.player", player).add(_winner.getId()).toString()));
			else
				player.sendPacket(SystemMsg.THERE_IS_NO_VICTOR_THE_MATCH_ENDS_IN_A_TIE);

			player.sendPacket(new ExCuriousHouseResult(member.getId(), state, members));
		}
	}

	private void spawnRewardBoxes()
	{
		if(_winner == null)
			return;

		Player player = _winner.getPlayer();
		if(player == null)
			return;

		Territory territory = new Territory();
		territory.add(new Circle(player.getX(), player.getY(), 150).setZmin(player.getZ() - 50).setZmax(player.getZ() + 50));

		int boxesCount = Rnd.get(_event.getRewardBoxSpawnMinCount(), _event.getRewardBoxSpawnMaxCount());
		if(boxesCount <= 0)
			return;

		for(int i = 0; i < boxesCount; i++)
		{
			NpcUtils.spawnSingle(_event.getRewardBoxId(), territory.getRandomLoc(_reflection.getGeoIndex()), _reflection);
		}
	}

	@Override
	public int compareTo(ChaosFestivalArenaObject o)
	{
		return getId() - o.getId();
	}
}
