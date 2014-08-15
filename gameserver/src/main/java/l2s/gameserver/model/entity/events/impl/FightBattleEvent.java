package l2s.gameserver.model.entity.events.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import l2s.commons.collections.MultiValueSet;
import l2s.commons.time.cron.SchedulingPattern;
import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.listener.actor.OnCurrentHpDamageListener;
import l2s.gameserver.listener.actor.OnKillListener;
import l2s.gameserver.listener.actor.player.OnPlayerExitListener;
import l2s.gameserver.listener.actor.player.OnTeleportListener;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.RestartType;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.entity.events.EventAction;
import l2s.gameserver.model.entity.events.GlobalEvent;
import l2s.gameserver.model.entity.events.actions.StartStopAction;
import l2s.gameserver.model.entity.events.objects.FightBattleArenaObject;
import l2s.gameserver.model.entity.events.objects.FightBattlePlayerObject;
import l2s.gameserver.model.entity.events.objects.RewardObject;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.templates.DoorTemplate;
import l2s.gameserver.templates.ZoneTemplate;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.ReflectionUtils;

import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;

/**
 * @author Bonux
**/
public class FightBattleEvent extends GlobalEvent
{
	private class ParticipantListeners implements OnCurrentHpDamageListener, OnPlayerExitListener, OnKillListener, OnTeleportListener
	{
		@Override
		public void onCurrentHpDamage(Creature actor, double damage, Creature attacker, Skill skill)
		{
			if(!actor.isPlayer())
				return;

			Player player = actor.getPlayer();
			FightBattleArenaObject arena = getArena(player);
			if(arena == null)
				return;

			arena.onDamage(attacker, player, damage);
		}

		@Override
		public void onPlayerExit(Player player)
		{
			FightBattleArenaObject arena = getArena(player);
			if(arena == null)
				return;

			arena.onExit(player);
		}

		@Override
		public void onKill(Creature actor, Creature victim)
		{
			if(!victim.isPlayer())
				return;

			Player player = victim.getPlayer();
			FightBattleArenaObject arena = getArena(player);
			if(arena == null)
				return;

			arena.onKill(actor, player);
		}

		@Override
		public boolean ignorePetOrSummon()
		{
			return true;
		}

		@Override
		public void onTeleport(Player player, int x, int y, int z, Reflection reflection)
		{
			FightBattleArenaObject arena = getArena(player);
			if(arena == null)
				return;

			arena.onTeleport(player, x, y, z, reflection);
		}
	}

	public static class ArenaInfo
	{
		private final int _mapX;
		private final int _mapY;

		private final Location _teleportLoc1;
		private final Location _teleportLoc2;
		private final Location _observerLoc;
		
		private final Map<String, ZoneTemplate> _zones = new HashMap<String, ZoneTemplate>();
		private final IntObjectMap<DoorTemplate> _doors = new HashIntObjectMap<DoorTemplate>();

		public ArenaInfo(int mapX, int mapY, Location teleportLoc1, Location teleportLoc2, Location observerLoc)
		{
			_mapX = mapX;
			_mapY = mapY;

			_teleportLoc1 = teleportLoc1;
			_teleportLoc2 = teleportLoc2;
			_observerLoc = observerLoc;
		}

		public int getMapX()
		{
			return _mapX;
		}

		public int getMapY()
		{
			return _mapY;
		}

		public Location getTeleportLoc1()
		{
			return _teleportLoc1;
		}

		public Location getTeleportLoc2()
		{
			return _teleportLoc2;
		}

		public Location getObserverLoc()
		{
			return _observerLoc;
		}

		public void addZone(ZoneTemplate zone)
		{
			_zones.put(zone.getName(), zone);
		}

		public Map<String, ZoneTemplate> getZones()
		{
			return _zones;
		}

		public void addDoor(DoorTemplate door)
		{
			_doors.put(door.getId(), door);
		}

		public IntObjectMap<DoorTemplate> getDoors()
		{
			return _doors;
		}
	}

	private static final String REGISTRATION = "registration";
	private static final String REGISTERED = "registered";
	private static final String REWARDS = "rewards";

	private final ParticipantListeners _participantListeners = new ParticipantListeners();

	private final Calendar _calendar = Calendar.getInstance();

	private final boolean _allow;
	private final SchedulingPattern _datePattern;
	private final int _minParticipants;
	private final int _maxParticipants;

	private int _firstActionTime;
	private int _duration;

	private final int _minLevel;
	private final ClassLevel _minClassLevel;
	private final boolean _multiRegistration;

	private final int _battleDelay;
	private final boolean _allowObserv;

	private AtomicBoolean _isRegistrationActive = new AtomicBoolean(false);
	private AtomicBoolean _isInProgress = new AtomicBoolean(false);

	private final List<ArenaInfo> _arenaInfos = new ArrayList<ArenaInfo>();

	private final Collection<FightBattleArenaObject> _arenas = new ConcurrentLinkedQueue<FightBattleArenaObject>();

	public FightBattleEvent(MultiValueSet<String> set)
	{
		super(set);

		_allow = set.getBool("allow");
		_datePattern = new SchedulingPattern(set.getString("start_time"));
		_minParticipants = set.getInteger("min_participants");
		_maxParticipants = set.getInteger("max_participants");

		_minLevel = set.getInteger("min_level");
		_minClassLevel = set.getEnum("min_class_level", ClassLevel.class);
		_multiRegistration = set.getBool("multi_registration");

		_battleDelay = set.getInteger("battle_delay");
		_allowObserv = set.getBool("allow_observ");

		ArenaInfo arena = new ArenaInfo(17, 10, new Location(-89368, -252840, -3340), new Location(-86792, -252840, -3340), new Location(-88136, -253224, -3340));
		arena.addZone(ReflectionUtils.getZone("[olympiad_arena_147]").getTemplate());
		arena.addDoor(ReflectionUtils.getDoor(17100001).getTemplate());
		arena.addDoor(ReflectionUtils.getDoor(17100002).getTemplate());

		_arenaInfos.add(arena);

		arena = new ArenaInfo(17, 10, new Location(-76728, -252456, -7741), new Location(-74200, -252456, -7741), new Location(-75064, -252856, -7741));
		arena.addZone(ReflectionUtils.getZone("[olympiad_arena_148]").getTemplate());
		arena.addDoor(ReflectionUtils.getDoor(17100101).getTemplate());
		arena.addDoor(ReflectionUtils.getDoor(17100102).getTemplate());

		_arenaInfos.add(arena);

		arena = new ArenaInfo(17, 10, new Location(-89176, -239352, -8474), new Location(-87000, -239352, -8474), new Location(-87382, -238952, -8474));
		arena.addZone(ReflectionUtils.getZone("[olympiad_arena_149]").getTemplate());
		arena.addDoor(ReflectionUtils.getDoor(17100201).getTemplate());
		arena.addDoor(ReflectionUtils.getDoor(17100202).getTemplate());

		_arenaInfos.add(arena);

		arena = new ArenaInfo(17, 10, new Location(-76632, -239176, -8218), new Location(-74280, -239176, -8218), new Location(-75448, -238712, -8218));
		arena.addZone(ReflectionUtils.getZone("[olympiad_arena_150]").getTemplate());
		arena.addDoor(ReflectionUtils.getDoor(17100301).getTemplate());
		arena.addDoor(ReflectionUtils.getDoor(17100302).getTemplate());

		_arenaInfos.add(arena);
	}

	@Override
	public void addOnTimeAction(int time, EventAction action)
	{
		if(_firstActionTime > time)
			_firstActionTime = time;

		if(action instanceof StartStopAction)
		{
			StartStopAction ssAction = (StartStopAction) action;
			if(ssAction.getName().equalsIgnoreCase(StartStopAction.EVENT) && !ssAction.isStart())
				_duration = time;
		}

		super.addOnTimeAction(time, action);
	}

	@Override
	public boolean isInProgress()
	{
		return _isInProgress.get();
	}

	@Override
	public void startEvent()
	{
		if(!_isInProgress.compareAndSet(false, true))
			return;

		_isRegistrationActive.set(false);

		super.startEvent();

		final List<FightBattlePlayerObject> registeredPlayers = getObjects(REGISTERED);
		final List<FightBattlePlayerObject> registeredPlayersTemp = new ArrayList<FightBattlePlayerObject>();
		registeredPlayersTemp.addAll(registeredPlayers);

		for(FightBattlePlayerObject registeredPlayer : registeredPlayersTemp)
		{
			Player player = registeredPlayer.getPlayer();
			if(player == null)
			{
				removeObject(REGISTERED, registeredPlayer);
				continue;
			}

			if(!checkParticipationCond(player))
			{
				String text = new CustomMessage("l2s.gameserver.model.entity.events.impl.FightBattleEvent.5", player).toString();
				player.sendPacket(new ExShowScreenMessage(text, 5000, ScreenMessageAlign.TOP_CENTER, true));
				player.sendMessage(text);
				removeObject(REGISTERED, registeredPlayer);
				continue;
			}

			player.addEvent(this);
		}

		registeredPlayersTemp.clear();
		registeredPlayersTemp.addAll(registeredPlayers);

		if(_minParticipants > registeredPlayersTemp.size())
		{
			stopEvent(true);
			return;
		}

		Collections.sort(registeredPlayersTemp);

		List<FightBattleArenaObject> arenas = new ArrayList<FightBattleArenaObject>();

		FightBattlePlayerObject[] participants = registeredPlayersTemp.toArray(new FightBattlePlayerObject[registeredPlayersTemp.size()]);
		for(int i = 0; i < participants.length; i++)
		{
			FightBattleArenaObject arena = new FightBattleArenaObject(this, arenas.size(), Rnd.get(_arenaInfos));
			arena.setMember1(participants[i]);

			i++;

			if(participants.length > i)
				arena.setMember2(participants[i]);

			arena.init();

			arenas.add(arena);
		}

		_arenas.addAll(arenas);
	}

	@Override
	public void stopEvent()
	{
		stopEvent(false);
	}

	private void stopEvent(boolean force)
	{
		if(!_isInProgress.compareAndSet(true, false))
			return;

		for(FightBattleArenaObject arena : getArenas())
			arena.onStopEvent();

		for(FightBattlePlayerObject participant : getParticlePlayers())
		{
			Player player = participant.getPlayer();
			if(player == null)
				continue;

			player.removeEvent(this);
		}

		if(force)
		{
			for(Player player : GameObjectsStorage.getAllPlayersForIterate())
			{
				String text = new CustomMessage("l2s.gameserver.model.entity.events.impl.FightBattleEvent.6", player).toString();
				player.sendPacket(new ExShowScreenMessage(text, 5000, ScreenMessageAlign.TOP_CENTER, true));
				player.sendMessage(text);
			}
		}

		super.stopEvent();

		removeObjects(REGISTERED);
		_arenas.clear();

		reCalcNextTime(false);
	}

	@Override
	protected void printInfo()
	{
		if(startTimeMillis() == 0)
			info(getName() + " time - off");
		else
			super.printInfo();
	}

	public boolean isRegistrationActive()
	{
		return _isRegistrationActive.get();
	}

	private void startRegistration()
	{
		if(isInProgress())
			return;

		if(!_isRegistrationActive.compareAndSet(false, true))
			return;
	}

	private void stopRegistration()
	{
		if(!_isRegistrationActive.compareAndSet(true, false))
			return;
	}

	public boolean forceStartEvent()
	{
		if(isRegistrationActive())
			return false;

		if(isInProgress())
			return false;

		clearActions();

		_calendar.setTimeInMillis(System.currentTimeMillis() + (-_firstActionTime * 1000L));
		printInfo();

		registerActions();
		return true;
	}

	@Override
	public void reCalcNextTime(boolean onInit)
	{
		clearActions();

		long time = 0L;
		if(_allow)
		{
			time = _datePattern.next(System.currentTimeMillis());
			if(time < System.currentTimeMillis()) // Заглушка, крон не умеет работать с секундами.
				time = _datePattern.next(System.currentTimeMillis() + 60000L);
		}

		_calendar.setTimeInMillis(time);

		registerActions();
	}

	@Override
	protected long startTimeMillis()
	{
		return _calendar.getTimeInMillis();
	}

	@Override
	public void action(String name, boolean start)
	{
		if(name.equalsIgnoreCase(REGISTRATION))
		{
			if(start)
				startRegistration();
			else
				stopRegistration();
		}
		else
			super.action(name, start);
	}

	@Override
	public void announce(SystemMsg msgId, int a, int time)
	{
		switch(a)
		{
			case 0:
				for(Player player : GameObjectsStorage.getAllPlayersForIterate())
				{
					String text;
					text = new CustomMessage("l2s.gameserver.model.entity.events.impl.FightBattleEvent.0.0", player).toString();
					text += "\n";
					text += new CustomMessage("l2s.gameserver.model.entity.events.impl.FightBattleEvent.0.1", player).toString();
					player.sendPacket(new ExShowScreenMessage(text, 5000, ScreenMessageAlign.TOP_CENTER, true));

					text = new CustomMessage("l2s.gameserver.model.entity.events.impl.FightBattleEvent.0.0", player).toString();
					player.sendMessage(text);

					text = new CustomMessage("l2s.gameserver.model.entity.events.impl.FightBattleEvent.0.1", player).toString();
					player.sendMessage(text);
				}
				break;
			case 1:
				for(Player player : GameObjectsStorage.getAllPlayersForIterate())
				{
					String text;
					if(-time >= 60)
						text = new CustomMessage("l2s.gameserver.model.entity.events.impl.FightBattleEvent.1.0", player).addNumber(-time / 60).toString();
					else
						text = new CustomMessage("l2s.gameserver.model.entity.events.impl.FightBattleEvent.1.1", player).addNumber(-time).toString();
					player.sendPacket(new ExShowScreenMessage(text, 5000, ScreenMessageAlign.TOP_CENTER, true));
					player.sendMessage(text);
				}
				break;
			case 2:
				if(!isInProgress())
					break;

				int participantsCount = getParticlePlayers().size();
				for(Player player : GameObjectsStorage.getAllPlayersForIterate())
				{
					String text;
					if(isParticle(player))
					{
						text = new CustomMessage("l2s.gameserver.model.entity.events.impl.FightBattleEvent.2.0.0", player).toString();
						text += "\n";
						text += new CustomMessage("l2s.gameserver.model.entity.events.impl.FightBattleEvent.2.0.1", player).addNumber(participantsCount).toString();
						player.sendPacket(new ExShowScreenMessage(text, 5000, ScreenMessageAlign.TOP_CENTER, true));

						text = new CustomMessage("l2s.gameserver.model.entity.events.impl.FightBattleEvent.2.0.0", player).toString();
						text += " ";
						text += new CustomMessage("l2s.gameserver.model.entity.events.impl.FightBattleEvent.2.0.1", player).addNumber(participantsCount).toString();
						player.sendMessage(text);
						continue;
					}
					else
						text = new CustomMessage("l2s.gameserver.model.entity.events.impl.FightBattleEvent.2.1", player).toString();
					player.sendPacket(new ExShowScreenMessage(text, 5000, ScreenMessageAlign.TOP_CENTER, true));
					player.sendMessage(text);
				}
				break;
			case 3:
				int endEventDelay = _duration - time;
				for(Player player : GameObjectsStorage.getAllPlayersForIterate())
				{
					if(!isParticle(player))
						continue;

					String text;
					if(endEventDelay >= 60)
						text = new CustomMessage("l2s.gameserver.model.entity.events.impl.FightBattleEvent.3.0", player).addNumber(endEventDelay / 60).toString();
					else
						text = new CustomMessage("l2s.gameserver.model.entity.events.impl.FightBattleEvent.3.1", player).addNumber(endEventDelay).toString();
					player.sendPacket(new ExShowScreenMessage(text, 5000, ScreenMessageAlign.TOP_CENTER, true));
					player.sendMessage(text);
				}
				break;
			case 4:
				for(Player player : GameObjectsStorage.getAllPlayersForIterate())
				{
					if(!isParticle(player))
						continue;

					String text = new CustomMessage("l2s.gameserver.model.entity.events.impl.FightBattleEvent.4", player).toString();
					player.sendPacket(new ExShowScreenMessage(text, 5000, ScreenMessageAlign.TOP_CENTER, true));
					player.sendMessage(text);
				}
				break;
		}
	}

	@Override
	public boolean canRessurect(Player resurrectPlayer, Creature creature, boolean force)
	{
		return false;
	}

	@Override
	public void checkRestartLocs(Player player, Map<RestartType, Boolean> r)
	{
		r.clear();
	}

	@Override
	public boolean canUseTeleport(Player player)
	{
		return !isParticle(player);
	}

	@Override
	public void broadcastStatusUpdate(Creature creature)
	{
		if(!creature.isPlayer())
			return;

		Player player = creature.getPlayer();
		FightBattleArenaObject arena = getArena(player);
		if(arena == null)
			return;

		arena.broadcastStatusUpdate(player);
	}

	@Override
	public void onAddEvent(GameObject o)
	{
		if(!o.isPlayer())
			return;

		o.getPlayer().addListener(_participantListeners);
	}

	@Override
	public void onRemoveEvent(GameObject o)
	{
		if(!o.isPlayer())
			return;

		o.getPlayer().removeListener(_participantListeners);

		FightBattleArenaObject arena = getArena(o.getPlayer());
		if(arena == null)
			return;

		arena.removeMember(arena.getMember(o.getPlayer()));
	}

	public List<FightBattlePlayerObject> getParticlePlayers()
	{
		List<FightBattlePlayerObject> participants = new ArrayList<FightBattlePlayerObject>();
		for(FightBattleArenaObject arena : getArenas())
		{
			if(arena.getMember1() != null)
				participants.add(arena.getMember1());

			if(arena.getMember2() != null)
				participants.add(arena.getMember2());
		}
		return participants;
	}

	public FightBattlePlayerObject getParticlePlayer(Player player)
	{
		for(FightBattlePlayerObject participant : getParticlePlayers())
		{
			if(participant.getPlayer() == player)
				return participant;
		}
		return null;
	}

	@Override
	public boolean isParticle(Player player)
	{
		return getParticlePlayer(player) != null;
	}

	public Collection<FightBattleArenaObject> getArenas()
	{
		return _arenas;
	}

	public FightBattleArenaObject getArena(Player player)
	{
		for(FightBattleArenaObject arena : getArenas())
		{
			for(FightBattlePlayerObject member : arena.getMembers())
			{
				if(member == null)
					continue;

				if(member.getPlayer() == player)
					return arena;
			}
		}
		return null;
	}

	public FightBattleArenaObject getArena(int id)
	{
		for(FightBattleArenaObject arena : getArenas())
		{
			if(arena.getId() == id)
				return arena;
		}
		return null;
	}

	public void removeArena(FightBattleArenaObject arena)
	{
		_arenas.remove(arena);
	}

	public boolean checkParticipationCond(Player player)
	{
		if(player.isGM()) // Для дебага.
			return true;

		if(player.getLevel() < _minLevel)
			return false;

		if(player.getClassLevel() < _minClassLevel.ordinal())
			return false;

		if(!_multiRegistration)
		{
			String HWID = player.getNetConnection().getHWID();
			if(HWID != null && !HWID.isEmpty())
			{
				for(FightBattlePlayerObject participant : getParticlePlayers())
				{
					Player p = participant.getPlayer();
					if(p == null)
						continue;

					String h = p.getNetConnection().getHWID();
					if(h == null || h.isEmpty())
						continue;

					if(HWID.equals(h))
						return false;
				}
			}
		}

		return true;
	}

	public boolean tryAddParticipant(Player player)
	{
		if(!isRegistrationActive())
			return false;

		if(isParticle(player))
			return false;

		if(!checkParticipationCond(player))
			return false;

		if(getParticlePlayers().size() >= getMaxParticipants())
			return false;

		addObject(REGISTERED, new FightBattlePlayerObject(player));
		return true;
	}

	public boolean removeParticipant(Player player)
	{
		FightBattlePlayerObject participant = getParticlePlayer(player);
		if(participant == null)
			return false;

		removeObject(REGISTERED, participant);
		return true;
	}

	public int getMaxParticipants()
	{
		return _maxParticipants;
	}

	public int getBattleDelay()
	{
		return _battleDelay;
	}

	public boolean isAllowObserv()
	{
		return _allowObserv;
	}

	public List<RewardObject> getRewards(int winsCount)
	{
		List<RewardObject> rewards = Collections.<RewardObject> emptyList();
		for(int i = 0; i <= winsCount; i++)
		{
			rewards = getObjects(REWARDS + "_" + winsCount);
			if(!rewards.isEmpty())
				break;
		}
		return rewards;
	}
}
