package l2s.gameserver.model.entity.events.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicBoolean;

import l2s.commons.collections.MultiValueSet;
import l2s.commons.time.cron.SchedulingPattern;
import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.listener.actor.OnCurrentHpDamageListener;
import l2s.gameserver.listener.actor.OnKillListener;
import l2s.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2s.gameserver.listener.actor.player.OnPlayerExitListener;
import l2s.gameserver.listener.actor.player.OnTeleportListener;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.base.RestartType;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.entity.events.GlobalEvent;
import l2s.gameserver.model.entity.events.objects.ChaosFestivalArenaObject;
import l2s.gameserver.model.entity.events.objects.ChaosFestivalPlayerObject;
import l2s.gameserver.model.entity.events.objects.RewardObject;
import l2s.gameserver.model.entity.olympiad.Olympiad;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExCuriousHouseLeave;
import l2s.gameserver.network.l2.s2c.ExCuriousHouseRemainTime;
import l2s.gameserver.network.l2.s2c.ExCuriousHouseState;
import l2s.gameserver.network.l2.s2c.L2GameServerPacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.TimeUtils;

/**
 * @author Bonux
**/
public class ChaosFestivalEvent extends GlobalEvent
{
	private class RemainingTimeTask extends RunnableImpl
	{
		private final int _remainingTime;

		public RemainingTimeTask(int remainingTime)
		{
			_remainingTime = remainingTime;
		}

		@Override
		public void runImpl()
		{
			if(!isBattleBegin())
				return;

			broadcastPacket(new ExCuriousHouseRemainTime(_remainingTime));
			ThreadPoolManager.getInstance().schedule(new RemainingTimeTask(_remainingTime - 1), 1000L);
		}
	}

	private class EnterListener implements OnPlayerEnterListener
	{
		@Override
		public void onPlayerEnter(Player player)
		{
			if(isRegistrationActive())
				invitePlayer(player);
		}
	}

	private class ParticipantListeners implements OnCurrentHpDamageListener, OnPlayerExitListener, OnKillListener, OnTeleportListener
	{
		@Override
		public void onCurrentHpDamage(Creature actor, double damage, Creature attacker, Skill skill)
		{
			if(!actor.isPlayer())
				return;

			Player player = actor.getPlayer();
			ChaosFestivalArenaObject arena = getArena(player);
			if(arena == null)
				return;

			arena.onDamage(attacker, player, damage);
		}

		@Override
		public void onPlayerExit(Player player)
		{
			ChaosFestivalArenaObject arena = getArena(player);
			if(arena == null)
				return;

			arena.onExit(player);
		}

		@Override
		public void onKill(Creature actor, Creature victim)
		{
			ChaosFestivalEvent.this.onKill(actor, victim);
		}

		@Override
		public boolean ignorePetOrSummon()
		{
			return true;
		}

		@Override
		public void onTeleport(Player player, int x, int y, int z, Reflection reflection)
		{
			ChaosFestivalArenaObject arena = getArena(player);
			if(arena == null)
				return;

			arena.onTeleport(player, x, y, z, reflection);
		}
	}

	public static final int MYSTERIOUS_MARK_ITEM_ID = 34900; // Item Name: Mysterious Mark

	private static final String REGISTRATION = "registration";
	private static final String BATTLE = "battle";
	private static final String REGISTERED_PLAYERS = "registered_players";
	private static final String ARENAS = "arenas";
	private static final String REWARDS = "rewards";

	// Arena's info
	private static final int[] ARENA_INSTANCE_IDS = new int[]{ 220, 221, 222, 223 };

	private final EnterListener _enterListener = new EnterListener();
	private final ParticipantListeners _patricipantListeners = new ParticipantListeners();

	private final Calendar _calendar = Calendar.getInstance();

	private final SchedulingPattern _datePattern;
	private final int _maxMembersInArena;
	private final double _pveArenaChance;

	private final int _rewardBoxId;
	private final int _rewardBoxSpawnMinCount;
	private final int _rewardBoxSpawnMaxCount;

	private AtomicBoolean _isRegistrationActive = new AtomicBoolean(false);
	private AtomicBoolean _isInProgress = new AtomicBoolean(false);
	private AtomicBoolean _isBattleBegin = new AtomicBoolean(false);

	private int _battleStartTime = 0;

	public ChaosFestivalEvent(MultiValueSet<String> set)
	{
		super(set);

		_datePattern = new SchedulingPattern(set.getString("start_time"));
		_maxMembersInArena = set.getInteger("max_members_in_arena");
		_pveArenaChance = set.getDouble("pve_arena_chance");

		_rewardBoxId = set.getInteger("reward_box_id");

		int[] rewardBoxSpawnCount = set.getIntegerArray("reward_box_spawn_count", "-");
		_rewardBoxSpawnMinCount = rewardBoxSpawnCount[0];
		_rewardBoxSpawnMaxCount = rewardBoxSpawnCount[1];
	}

	@Override
	public void startEvent()
	{
		if(!_isInProgress.compareAndSet(false, true))
			return;

		_isRegistrationActive.set(false);

		super.startEvent();

		final List<ChaosFestivalPlayerObject> particlePlayers = new ArrayList<ChaosFestivalPlayerObject>();

		List<ChaosFestivalPlayerObject> registeredPlayers = getObjects(REGISTERED_PLAYERS);
		for(ChaosFestivalPlayerObject registeredPlayer : registeredPlayers)
		{
			Player player = registeredPlayer.getPlayer();
			if(player == null || !player.isOnline())
			{
				// TODO: Выдаем бан фестиваля хаоса.
				continue;
			}

			if(!checkParticipationCond(player, false))
			{
				// TODO: Выдаем бан фестиваля хаоса.
				continue;
			}

			if(player.getReflection() != ReflectionManager.DEFAULT)
			{
				// TODO: Выдаем бан фестиваля хаоса.
				continue;
			}

			if(!player.isQuestContinuationPossible(false))
			{
				// TODO: Выдаем бан фестиваля хаоса.
				continue;
			}

			particlePlayers.add(new ChaosFestivalPlayerObject(player));
		}

		Collections.sort(particlePlayers, new ChaosFestivalPlayerObject.LevelComparator());

		List<ChaosFestivalArenaObject> arenas = new ArrayList<ChaosFestivalArenaObject>();

		int participantsCount = particlePlayers.size();
		while(participantsCount > 0)
		{
			boolean pvp = true/*participantsCount > 1 && !Rnd.chance(_pveArenaChance)*/; // TODO: Раскомминтить после реализации PvE режима.
			int membersCount = participantsCount > 1 ? Rnd.get(pvp ? 2 : 1, Math.min(_maxMembersInArena, participantsCount)) : 1;

			if((participantsCount - membersCount) == 1) // Убрать после реализации PvE режима.
				membersCount = participantsCount;

			ChaosFestivalArenaObject arena = new ChaosFestivalArenaObject(this, arenas.size(), pvp, ARENA_INSTANCE_IDS[Rnd.get(ARENA_INSTANCE_IDS.length)]);

			for(int i = 0; i < membersCount; i++)
			{
				ChaosFestivalPlayerObject participant = particlePlayers.remove(0);
				if(participant == null)
					continue;

				Player player = participant.getPlayer();
				if(player == null)
					continue;

				arena.addMember(participant);
			}

			if(!pvp || pvp && arena.getMembers().size() > 1)
			{
				arenas.add(arena);
				arena.teleportPlayers();
			}

			participantsCount = particlePlayers.size();
		}

		if(arenas.isEmpty())
		{
			stopEvent();
			return;
		}

		addObjects(ARENAS, arenas);
	}

	@Override
	public void stopEvent()
	{
		if(!_isInProgress.compareAndSet(true, false))
			return;

		super.stopEvent();

		removeObjects(REGISTERED_PLAYERS);
		removeObjects(ARENAS);

		reCalcNextTime(false);
	}

	private void startBattle()
	{
		if(!_isBattleBegin.compareAndSet(false, true))
			return;

		_battleStartTime = (int) (System.currentTimeMillis() / 1000L);

		List<ChaosFestivalArenaObject> arenas = getObjects(ARENAS);
		for(ChaosFestivalArenaObject arena : arenas)
			arena.startBattle();
	}

	private void stopBattle()
	{
		if(!_isBattleBegin.compareAndSet(true, false))
			return;

		List<ChaosFestivalArenaObject> arenas = getObjects(ARENAS);
		for(ChaosFestivalArenaObject arena : arenas)
			arena.stopBattle();
	}

	@Override
	public boolean isInProgress()
	{
		return _isInProgress.get();
	}

	@Override
	public void reCalcNextTime(boolean onInit)
	{
		clearActions();

		_calendar.setTimeInMillis(_datePattern.next(System.currentTimeMillis()));

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
		else if(name.equalsIgnoreCase(BATTLE))
		{
			if(start)
				startBattle();
			else
				stopBattle();
		}
		else
			super.action(name, start);
	}

	@Override
	public void announce(SystemMsg msgId, int a, int time)
	{
		if(msgId == SystemMsg.YOU_WILL_BE_MOVED_TO_THE_ARENA_IN_S1_SECONDS)
		{
			List<ChaosFestivalPlayerObject> registeredPlayers = getObjects(REGISTERED_PLAYERS);
			for(ChaosFestivalPlayerObject registeredPlayer : registeredPlayers)
				registeredPlayer.getPlayer().sendPacket(new SystemMessagePacket(SystemMsg.YOU_WILL_BE_MOVED_TO_THE_ARENA_IN_S1_SECONDS).addInteger(a));
		}
		else if(msgId == SystemMsg.THE_MATCH_WILL_START_IN_S1_SECONDS)
			broadcastPacket(new SystemMessagePacket(SystemMsg.THE_MATCH_WILL_START_IN_S1_SECONDS).addInteger(a));
		else if(msgId == SystemMsg.THE_MATCH_HAS_STARTED)
			broadcastPacket(new SystemMessagePacket(SystemMsg.THE_MATCH_HAS_STARTED));
		else if(msgId == null && isBattleBegin())
		{
			broadcastPacket(new ExCuriousHouseRemainTime(a));
			ThreadPoolManager.getInstance().schedule(new RemainingTimeTask(a - 1), 1000L);
		}
	}

	@Override
	protected void printInfo()
	{
		final long startTimeMillis = startTimeMillis();

		if(startTimeMillis == 0)
			info(getName() + " time - undefined");
		else
			info(getName() + " time - " + TimeUtils.toSimpleFormat(startTimeMillis));
	}

	@Override
	public void onAddEvent(GameObject o)
	{
		if(!o.isPlayer())
			return;

		o.getPlayer().addListener(_patricipantListeners);
	}

	@Override
	public void onRemoveEvent(GameObject o)
	{
		if(!o.isPlayer())
			return;

		o.getPlayer().removeListener(_patricipantListeners);

		ChaosFestivalArenaObject arena = getArena(o.getPlayer());
		if(arena == null)
			return;

		arena.removeMember(arena.getMember(o.getPlayer()));
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
	public SystemMsg checkForAttack(Creature target, Creature attacker, Skill skill, boolean force)
	{
		if(!isBattleBegin())
			return SystemMsg.INVALID_TARGET;

		return null;
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

		ChaosFestivalArenaObject arena = getArena(player);
		if(arena == null)
			return;

		arena.broadcastStatusUpdate(player);
	}

	public ChaosFestivalPlayerObject getRegisteredPlayer(Player player)
	{
		List<ChaosFestivalPlayerObject> registeredPlayers = getObjects(REGISTERED_PLAYERS);
		for(ChaosFestivalPlayerObject p : registeredPlayers)
		{
			if(p.getPlayer() == player)
				return p;
		}
		return null;
	}

	public boolean isRegistered(Player player)
	{
		return getRegisteredPlayer(player) != null;
	}

	public List<ChaosFestivalPlayerObject> getParticlePlayers()
	{
		List<ChaosFestivalPlayerObject> players = new ArrayList<ChaosFestivalPlayerObject>();

		List<ChaosFestivalArenaObject> arenas = getObjects(ARENAS);
		for(ChaosFestivalArenaObject arena : arenas)
		{
			for(ChaosFestivalPlayerObject member : arena.getMembers())
				players.add(member);
		}

		return players;
	}

	public ChaosFestivalPlayerObject getParticlePlayer(Player player)
	{
		List<ChaosFestivalArenaObject> arenas = getObjects(ARENAS);
		for(ChaosFestivalArenaObject arena : arenas)
		{
			ChaosFestivalPlayerObject member = arena.getMember(player);
			if(member == null)
				continue;

			return member;
		}
		return null;
	}

	public ChaosFestivalArenaObject getArena(Player player)
	{
		List<ChaosFestivalArenaObject> arenas = getObjects(ARENAS);
		for(ChaosFestivalArenaObject arena : arenas)
		{
			for(ChaosFestivalPlayerObject member : arena.getMembers())
			{
				if(member.getPlayer() == player)
					return arena;
			}
		}
		return null;
	}

	public void removeArena(ChaosFestivalArenaObject arena)
	{
		removeObject(ARENAS, arena);
	}

	@Override
	public boolean isParticle(Player player)
	{
		return getParticlePlayer(player) != null;
	}

	private void startRegistration()
	{
		if(isInProgress())
			return;

		if(!_isRegistrationActive.compareAndSet(false, true))
			return;

		CharListenerList.addGlobal(_enterListener);

		for(Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			player.sendPacket(SystemMsg.REGISTRATION_FOR_THE_CEREMONY_OF_CHAOS_HAS_BEGUN);
			invitePlayer(player);
		}
	}

	private void stopRegistration()
	{
		if(!_isRegistrationActive.compareAndSet(true, false))
			return;

		CharListenerList.removeGlobal(_enterListener);

		for(Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			player.sendPacket(SystemMsg.REGISTRATION_FOR_THE_CEREMONY_OF_CHAOS_HAS_ENDED);

			if(!isRegistered(player))
				player.sendPacket(ExCuriousHouseState.IDLE);
		}
	}

	public boolean isRegistrationActive()
	{
		return _isRegistrationActive.get();
	}

	public boolean isBattleBegin()
	{
		return _isBattleBegin.get();
	}

	public int getBattleStartTime()
	{
		return _battleStartTime;
	}

	private boolean checkParticipationCond(Player player, boolean msg)
	{
		if(player.isGM()) // Для дебага.
			return true;

		if(player.getClan() == null)
			return false;

		if(player.getClan().getLevel() < 3)
			return false;

		if(player.getClassLevel() < 3)
			return false;

		if(player.getLevel() < 76)
		{
			if(msg)
				player.sendPacket(SystemMsg.ONLY_CHARACTERS_LEVEL_76_OR_ABOVE_MAY_PARTICIPATE_IN_THE_TOURNAMENT);
			return false;
		}

		if(player.isInFlyingTransform())
		{
			if(msg)
				player.sendPacket(SystemMsg.YOU_CANNOT_PARTICIPATE_IN_THE_CEREMONY_OF_CHAOS_AS_A_FLYING_TRANSFORMED_OBJECT);
			return false;
		}

		if(player.isInOlympiadMode() || Olympiad.isRegisteredInComp(player))
		{
			if(msg)
				player.sendPacket(SystemMsg.YOU_CANNOT_REGISTER_IN_THE_WAITING_LIST_WHILE_PARTICIPATING_IN_OLYMPIAD); // TODO: Найти сообщение, которое используется на оффе.
			return false;
		}
		if(player.isCursedWeaponEquipped())
			return false;

		return true;
	}

	private void invitePlayer(Player player)
	{
		if(!checkParticipationCond(player, false))
			return;

		if(isRegistered(player))
			return;

		player.sendPacket(ExCuriousHouseState.INVITE);
	}

	public void acceptInvite(Player player)
	{
		if(!isRegistrationActive())
			return;

		if(!checkParticipationCond(player, true))
			return;

		if(isRegistered(player))
			return;

		addObject(REGISTERED_PLAYERS, new ChaosFestivalPlayerObject(player));

		player.sendPacket(ExCuriousHouseState.PREPARE);
	}

	public void onBypassCommand(Player player, String bypass)
	{
		StringTokenizer st = new StringTokenizer(bypass, "_");
		String cmd = st.nextToken();

		if(cmd.equalsIgnoreCase("invite"))
		{
			acceptInvite(player);
		}
	}

	public void showHtmlDialog(Player player)
	{
		if(isRegistrationActive() && checkParticipationCond(player, false) && !isRegistered(player))
			Functions.show("chaos_festival/invitation.htm", player, null);
	}

	public void cancelRegistration(Player player)
	{
		if(!isRegistrationActive())
			return;

		if(!isRegistered(player))
			return;

		player.sendPacket(ExCuriousHouseState.IDLE);
	}

	public void broadcastPacket(L2GameServerPacket packet)
	{
		broadcastPacket(packet, -1);
	}

	public void broadcastPacket(L2GameServerPacket packet, int arenaId)
	{
		List<ChaosFestivalArenaObject> arenas = getObjects(ARENAS);
		for(ChaosFestivalArenaObject arena : arenas)
		{
			if(arenaId != -1 && arenaId != arena.getId())
				continue;

			if(isBattleBegin() && !arena.isBattleBegin())
				continue;

			arena.broadcastPacket(packet);
		}
	}

	public int getRewardBoxId()
	{
		return _rewardBoxId;
	}

	public int getRewardBoxSpawnMinCount()
	{
		return _rewardBoxSpawnMinCount;
	}

	public int getRewardBoxSpawnMaxCount()
	{
		return _rewardBoxSpawnMaxCount;
	}

	private void onKill(Creature actor, Creature victim)
	{
		if(victim == null)
			return;

		if(victim.isNpc())
		{
			if(actor == null)
				return;

			Player killer = actor.getPlayer();
			if(killer == null)
				return;

			if(!isParticle(killer))
				return;

			NpcInstance npc = (NpcInstance) victim;
			if(npc.getNpcId() != _rewardBoxId)
				return;

			final List<RewardObject> rewards = getObjects(REWARDS);
			for(RewardObject reward : rewards)
			{
				if(!Rnd.chance(reward.getChance()))
					continue;

				ItemFunctions.addItem(killer, reward.getItemId(), Rnd.get(reward.getMinCount(), reward.getMaxCount()), true);
			}
		}
		else if(victim.isPlayer())
		{
			ChaosFestivalArenaObject arena = getArena(victim.getPlayer());
			if(arena == null)
				return;

			arena.onKill(actor, victim.getPlayer());
		}
	}
}
