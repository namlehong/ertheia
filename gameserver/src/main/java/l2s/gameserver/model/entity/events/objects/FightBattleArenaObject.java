package l2s.gameserver.model.entity.events.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.data.xml.holder.InstantZoneHolder;
import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.entity.events.impl.FightBattleEvent;
import l2s.gameserver.model.entity.events.impl.FightBattleEvent.ArenaInfo;
import l2s.gameserver.model.instances.DoorInstance;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.components.IStaticPacket;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExCuriousHouseRemainTime;
import l2s.gameserver.network.l2.s2c.ExOlympiadUserInfoPacket;
import l2s.gameserver.network.l2.s2c.ExOlympiadMatchEndPacket;
import l2s.gameserver.network.l2.s2c.ExOlympiadModePacket;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.network.l2.s2c.L2GameServerPacket;
import l2s.gameserver.utils.ItemFunctions;

/**
 * @author Bonux
**/
public final class FightBattleArenaObject implements Serializable, Comparable<FightBattleArenaObject>
{
	private class BattleTask extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if(getMember1() == null && getMember2() == null)
			{
				removeArenaFromEvent();
				stopBattleTask();
				return;
			}

			if(isOpponentSearching())
			{
				// Не нашли нового оппонента, воюем с предыдущим.
				if((_opponentSearchingStartTime + _event.getBattleDelay()) < (System.currentTimeMillis() / 1000L))
				{
					stopOpponentSearching();
					teleportPlayers();
					return;
				}

				for(FightBattlePlayerObject member : getMembers())
				{
					if(member == null)
						continue;

					Player player = member.getPlayer();
					if(player == null)
						return;

					String text = new CustomMessage("l2s.gameserver.model.entity.events.objects.FightBattleArenaObject.0.0", player).toString();
					for(int i = 0; i < _taskTemp; i++)
						text += ".";

					player.sendPacket(new ExShowScreenMessage(text, 1000, ScreenMessageAlign.MIDDLE_LEFT, false));
				}

				_taskTemp++;
				if(_taskTemp > 3)
					_taskTemp = 0;

				if(_opponentSearchingTemp == 5 && _canFindFreeArena)
				{
					if(findFreeArena(getMember1() == null ? getMember2() : getMember1()))
					{
						stopBattleTask();
						stopOpponentSearching();
						startBattleTask();
						return;
					}
				}

				_opponentSearchingTemp++;
				return;
			}
			else if(getMember1() == null || getMember2() == null)
			{
				startOpponentSearching();
				return;
			}

			if(_completed)
			{
				if(_taskTemp == 5)
					finishBattle();
			}
			else
			{
				switch(_taskTemp)
				{
					case 3:
					case 18:
					case 23:
					case 24:
					case 25:
					case 26:
					case 27:
					case 28:
					case 29:
					case 30:
					case 31:
					case 32:
						for(FightBattlePlayerObject member : getMembers())
						{
							if(member == null)
								continue;

							Player player = member.getPlayer();
							if(player == null)
								continue;

							String text = new CustomMessage("l2s.gameserver.model.entity.events.objects.FightBattleArenaObject.1", player).addNumber(33 - _taskTemp).toString();
							player.sendPacket(new ExShowScreenMessage(text, 5000, ScreenMessageAlign.TOP_CENTER, true));
							player.sendMessage(text);
						}
						break;
					case 33:
						startBattle();
						break;
				}

				final int battleDelay = _event.getBattleDelay() + 33;
				if(_taskTemp == battleDelay)
					stopBattle();
				else if(_taskTemp > 33)
					broadcastPacket(new ExCuriousHouseRemainTime(battleDelay - _taskTemp), true);
			}

			_taskTemp++;
		}
	}

	private static final long serialVersionUID = 1L;

	private final FightBattleEvent _event;
	private final int _id;
	private final ArenaInfo _info;
	private final Reflection _reflection;

	private FightBattlePlayerObject _member1 = null;
	private FightBattlePlayerObject _member2 = null;

	private int _winner = 0;

	private ScheduledFuture<?> _battleTask = null;
	private int _taskTemp = 0;
	private long _opponentSearchingStartTime = 0L;
	private AtomicBoolean _opponentSearching = new AtomicBoolean(false);
	private int _opponentSearchingTemp = 0;
	private boolean _canFindFreeArena = false;

	private AtomicBoolean _isBattleBegin = new AtomicBoolean(false);
	private boolean _completed = false;

	private final List<Player> _observers = new CopyOnWriteArrayList<Player>();

	public FightBattleArenaObject(FightBattleEvent event, int id, ArenaInfo info)
	{
		_event = event;
		_id = id;
		_info = info;
		_reflection = new Reflection();

		int geoIndex = GeoEngine.NextGeoIndex(info.getMapX(), info.getMapY(), _reflection.getId());

		_reflection.setGeoIndex(geoIndex);
		_reflection.init(info.getDoors(), info.getZones());
	}

	public FightBattleEvent getEvent()
	{
		return _event;
	}

	public int getId()
	{
		return _id;
	}

	public ArenaInfo getInfo()
	{
		return _info;
	}

	public Reflection getReflection()
	{
		return _reflection;
	}

	public void setMember1(FightBattlePlayerObject member)
	{
		_member1 = member;
	}

	public void setMember2(FightBattlePlayerObject member)
	{
		_member2 = member;
	}

	public FightBattlePlayerObject getMember1()
	{
		return _member1;
	}

	public FightBattlePlayerObject getMember2()
	{
		return _member2;
	}

	public FightBattlePlayerObject getMember(Player player)
	{
		for(FightBattlePlayerObject member : getMembers())
		{
			if(member == null)
				continue;

			if(member.getPlayer() == player)
				return member;
		}
		return null;
	}

	public FightBattlePlayerObject[] getMembers()
	{
		return new FightBattlePlayerObject[]{ _member1, _member2 };
	}

	public void removeMember(FightBattlePlayerObject member)
	{
		if(_member1 == member)
			_member1 = null;

		if(_member2 == member)
			_member2 = null;
	}

	public void init()
	{
		closeDoors();
		teleportPlayers();
		startBattleTask();
	}

	private void openDoors()
	{
		for(DoorInstance door : _reflection.getDoors())
			door.openMe();
	}

	private void closeDoors()
	{
		for(DoorInstance door : _reflection.getDoors())
			door.closeMe();
	}

	private void startBattleTask()
	{
		_battleTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new BattleTask(), 0L, 1000L);
	}

	public void stopBattleTask()
	{
		if(_battleTask != null)
		{
			_battleTask.cancel(false);
			_battleTask = null;
		}
		_taskTemp = 0;
	}

	public boolean isBattleBegin()
	{
		return _isBattleBegin.get();
	}

	private void startBattle()
	{
		if(!_isBattleBegin.compareAndSet(false, true))
			return;

		_opponentSearchingStartTime = 0L;
		_opponentSearching.set(false);
		_opponentSearchingTemp = 0;

		openDoors();

		for(FightBattlePlayerObject member : getMembers())
		{
			if(member == null)
				continue;

			Player player = member.getPlayer();
			if(player == null)
				continue;

			String text = new CustomMessage("l2s.gameserver.model.entity.events.objects.FightBattleArenaObject.2", player).toString();
			player.sendPacket(new ExShowScreenMessage(text, 5000, ScreenMessageAlign.TOP_CENTER, true));
			player.sendMessage(text);

			broadcastPacket(new ExOlympiadUserInfoPacket(player, member == getMember1() ? 1 : 2), true);
		}
	}

	public void stopBattle()
	{
		if(!_isBattleBegin.compareAndSet(true, false))
			return;

		calcWinner();

		if(_winner != 0)
			broadcastPacket(new SystemMessagePacket(SystemMsg.CONGRATULATIONS_C1_YOU_WIN_THE_MATCH).addString(_winner == 1 ? getMember1().getName() : getMember2().getName()), true);
		else
			broadcastPacket(SystemMsg.THERE_IS_NO_VICTOR_THE_MATCH_ENDS_IN_A_TIE, true);

		giveReward();

		_completed = true;
		_taskTemp = 0;

		//broadcastPacket(new ExOlympiadModePacket(0), false);
		broadcastPacket(ExOlympiadMatchEndPacket.STATIC, false);
	}

	private void finishBattle()
	{
		for(Player player : _observers)
		{
			if(player == null)
				continue;

			player.leaveObserverMode();
		}

		_winner = 0;
		_completed = false;

		closeDoors();
		teleportPlayers();

		_canFindFreeArena = true;

		startOpponentSearching();
	}

	public void onStopEvent()
	{
		for(Player player : _observers)
		{
			if(player == null)
				continue;

			player.leaveObserverMode();
		}

		stopOpponentSearching();
		stopBattleTask();
		removeArenaFromEvent();

		//broadcastPacket(new ExOlympiadModePacket(0), false);
		broadcastPacket(ExOlympiadMatchEndPacket.STATIC, false);

		for(FightBattlePlayerObject member : getMembers())
		{
			if(member == null)
				continue;

			member.onStopEvent(_event);
		}
	}

	private void startOpponentSearching()
	{
		if(!_opponentSearching.compareAndSet(false, true))
			return;

		_opponentSearchingStartTime = System.currentTimeMillis() / 1000L;
		_opponentSearchingTemp = 0;
		_taskTemp = 0;
	}

	public void stopOpponentSearching()
	{
		if(!_opponentSearching.compareAndSet(true, false))
			return;

		_canFindFreeArena = false;
		_opponentSearchingStartTime = 0;
		_opponentSearchingTemp = 0;
		_taskTemp = 0;
	}

	public boolean isOpponentSearching()
	{
		return _opponentSearching.get();
	}

	private boolean findFreeArena(FightBattlePlayerObject member)
	{
		if(member == null)
			return false;

		for(FightBattleArenaObject arena : _event.getArenas())
		{
			if(!isOpponentSearching())
				break;

			if(arena == this)
				continue;

			if(!arena.isOpponentSearching())
				continue;

			if(arena.getMember1() == null && arena.getMember2() == null)
				continue;

			arena.stopBattleTask();
			arena.stopOpponentSearching();

			removeMember(member);

			boolean twoArenasFormed = false;

			if(arena.getMember1() == null)
				arena.setMember1(member);
			else if(arena.getMember2() == null)
				arena.setMember2(member);
			else
			{
				FightBattlePlayerObject newMember = null;
				if(Rnd.chance(50))
				{
					newMember = arena.getMember1();
					if(getMember1() == null)
						setMember1(newMember);
					else if(getMember2() == null)
						setMember2(newMember);
					arena.setMember1(member);
				}
				else
				{
					newMember = arena.getMember2();
					if(getMember1() == null)
						setMember1(newMember);
					else if(getMember2() == null)
						setMember2(newMember);
					arena.setMember2(member);
				}
				teleportPlayer(newMember);
				twoArenasFormed = true;
			}

			arena.teleportPlayer(member);
			arena.startBattleTask();
			return twoArenasFormed;
		}
		return false;
	}

	private void broadcastPacket(L2GameServerPacket packet, FightBattlePlayerObject exception, boolean withObservers)
	{
		for(FightBattlePlayerObject member : getMembers())
		{
			if(member == null || member == exception)
				continue;

			Player player = member.getPlayer();
			if(player == null)
				continue;

			player.sendPacket(packet);
		}

		if(withObservers)
		{
			for(Player player : _observers)
			{
				if(player == null)
					continue;

				player.sendPacket(packet);
			}
		}
	}

	private void broadcastPacket(L2GameServerPacket packet, boolean withObservers)
	{
		broadcastPacket(packet, null, withObservers);
	}

	private void broadcastPacket(IStaticPacket packet, FightBattlePlayerObject exception, boolean withObservers)
	{
		for(FightBattlePlayerObject member : getMembers())
		{
			if(member == null || member == exception)
				continue;

			Player player = member.getPlayer();
			if(player == null)
				continue;

			player.sendPacket(packet);
		}

		if(withObservers)
		{
			for(Player player : _observers)
			{
				if(player == null)
					continue;

				player.sendPacket(packet);
			}
		}
	}

	private void broadcastPacket(IStaticPacket packet, boolean withObservers)
	{
		broadcastPacket(packet, null, withObservers);
	}

	private void teleportPlayers()
	{
		for(FightBattlePlayerObject member : getMembers())
			teleportPlayer(member);
	}

	public void teleportPlayer(FightBattlePlayerObject member)
	{
		if(member == null)
			return;

		Player player = member.getPlayer();
		if(player == null)
			return;

		member.teleportPlayer(this);

		player.sendPacket(new ExOlympiadModePacket(member == getMember1() ? 1 : 2));
	}

	public void broadcastStatusUpdate(Player player)
	{
		if(!isBattleBegin())
			return;

		FightBattlePlayerObject member = getMember(player);
		if(member == null)
			return;

		broadcastPacket(new ExOlympiadUserInfoPacket(player, member == getMember1() ? 1 : 2), member, true);
	}

	public void onDamage(Creature attacker, Player target, double damage)
	{
		FightBattlePlayerObject targetMember = getMember(target);
		if(targetMember == null)
			return;

		FightBattlePlayerObject attackerMember = getMember(attacker.getPlayer());
		if(attackerMember == null)
			return;

		attackerMember.onDamage(damage);
	}

	public void onKill(Creature killer, Player victim)
	{
		FightBattlePlayerObject killerMember = getMember(killer.getPlayer());
		FightBattlePlayerObject victimMember = getMember(victim);
		if(victimMember == null)
			return;

		victimMember.onKill(getEvent(), killerMember);

		stopBattle();
	}

	public void onTeleport(Player player, int x, int y, int z, Reflection reflection)
	{
		if(reflection == _reflection)
			return;

		FightBattlePlayerObject member = getMember(player);
		if(member == null)
			return;

		member.onTeleport(_event, player, x, y, z, reflection);

		stopBattle();
	}

	public void onExit(Player player)
	{
		FightBattlePlayerObject member = getMember(player);
		if(member == null)
			return;

		member.onExit(_event);

		stopBattle();
	}

	private void calcWinner()
	{
		if(getMember1() == null && getMember2() == null)
			return;

		if(getMember1() == null)
		{
			_winner = 2;
			return;
		}

		if(getMember2() == null)
		{
			_winner = 1;
			return;
		}

		if(getMember1().isKilled())
		{
			_winner = 2;
			return;
		}

		if(getMember2().isKilled())
		{
			_winner = 1;
			return;
		}

		if(getMember1().getDamage() == getMember2().getDamage())
			return;

		if(getMember2().getDamage() > getMember1().getDamage())
		{
			_winner = 2;
			return;
		}

		if(getMember1().getDamage() > getMember2().getDamage())
		{
			_winner = 1;
			return;
		}
	}

	private void giveReward()
	{
		if(_winner == 0)
			return;

		FightBattlePlayerObject winner = _winner == 1 ? getMember1() : getMember2();
		if(winner != null)
		{
			winner.setWinCount(winner.getWinCount() + 1);

			Player player = winner.getPlayer();
			if(player != null)
			{
				for(RewardObject reward : _event.getRewards(winner.getWinCount()))
				{
					if(!Rnd.chance(reward.getChance()))
						continue;

					ItemFunctions.addItem(player, reward.getItemId(), Rnd.get(reward.getMinCount(), reward.getMaxCount()), true);
				}
			}
		}

		FightBattlePlayerObject looser = _winner == 1 ? getMember2() : getMember1();
		if(looser != null)
		{
			looser.setWinCount(0);

			Player player = looser.getPlayer();
			if(player != null)
			{
				for(RewardObject reward : _event.getRewards(0))
				{
					if(!Rnd.chance(reward.getChance()))
						continue;

					ItemFunctions.addItem(player, reward.getItemId(), Rnd.get(reward.getMinCount(), reward.getMaxCount()), true);
				}
			}
		}
	}

	private void removeArenaFromEvent()
	{
		_event.removeArena(this);
	}

	public void onEnterObserverMode(Player observer)
	{
		Servitor[] servitors = observer.getServitors();
		if(servitors.length > 0)
		{
			for(Servitor servitor : servitors)
				servitor.unSummon(false);
		}
	}

	public void onAppearObserverMode(Player observer)
	{
		_observers.add(observer);

		for(FightBattlePlayerObject member : getMembers())
		{
			if(member == null)
				continue;

			Player player = member.getPlayer();
			if(player == null)
				continue;

			observer.sendPacket(new ExOlympiadUserInfoPacket(player, member == getMember1() ? 1 : 2));
		}
	}

	public void onLeaveObserverMode(Player observer)
	{
		_observers.remove(observer);
	}

	public void onChangeObserverArena(Player observer)
	{
		onLeaveObserverMode(observer);
	}

	@Override
	public int compareTo(FightBattleArenaObject o)
	{
		return getId() - o.getId();
	}
}
