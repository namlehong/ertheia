package blood.ai;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ScheduledFuture;

import l2mq.L2MQ;
import l2s.commons.collections.CollectionUtils;
import l2s.commons.collections.LazyArrayList;
import l2s.commons.lang.reference.HardReference;
import l2s.commons.math.random.RndSelector;
import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.PlayerAI;
import l2s.gameserver.data.xml.holder.SkillAcquireHolder;
import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.SkillLearn;
import l2s.gameserver.model.World;
import l2s.gameserver.model.base.AcquireType;
import l2s.gameserver.model.base.RestartType;
import l2s.gameserver.model.instances.ChestInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
import l2s.gameserver.skills.EffectType;
//import l2s.gameserver.skills.effects.EffectTemplate;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.taskmanager.AiTaskManager;
import l2s.gameserver.templates.item.WeaponTemplate;
import l2s.gameserver.templates.skill.EffectTemplate;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.TeleportUtils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import blood.FPCInfo;
import blood.base.FPCParty;
import blood.base.FPCPveStyle;
import blood.data.holder.FPItemHolder;
import blood.data.holder.NpcHelper;
import blood.model.AggroListPC;
import blood.model.AggroListPC.AggroInfoPC;
import blood.model.FPRewardList;
import blood.model.FarmLocation;
import blood.utils.ClassFunctions;

public class FPCDefaultAI extends PlayerAI
{
	protected static final Logger _log = LoggerFactory.getLogger(FPCDefaultAI.class);
	public static enum TaskType
	{
		MOVE,
		TELE,
		SLEEP,
		ATTACK,
		CAST,
		BUFF
	}
	
	public static final int TaskDefaultWeight = 10000;
	public static long attackTime;
	
	public static class Task
	{
		public TaskType type;
		public Skill skill;
		public HardReference<? extends Creature> target;
		public Location loc;
		public int sleepTime = 0;
		public boolean forceMove = false;
		public boolean pathfind;
		public int weight = TaskDefaultWeight;
		public long createTime = System.currentTimeMillis();
	}
	
	public void addTaskCast(Creature target, Skill skill)
	{
		
		Task task = new Task();
		task.type = TaskType.CAST;
		task.target = target.getRef();
		task.skill = skill;
		_tasks.add(task);
		_def_think = true;
	}
	
	public void addTaskBuff(Creature target, Skill skill)
	{
		Task task = new Task();
		task.type = TaskType.BUFF;
		task.target = target.getRef();
		task.skill = skill;
		_tasks.add(task);
		_def_think = true;
	}
	
	public void addTaskAttack(Creature target)
	{
		Task task = new Task();
		task.type = TaskType.ATTACK;
		task.target = target.getRef();
		_tasks.add(task);
		_def_think = true;
	}
	
	public void addTaskAttack(Creature target, Skill skill, int weight)
	{
		Task task = new Task();
		task.type = skill.isOffensive() ? TaskType.CAST : TaskType.BUFF;
		task.target = target.getRef();
		task.skill = skill;
		task.weight = weight;
		_tasks.add(task);
		_def_think = true;
	}
	
	public void addTaskMove(Location loc, boolean pathfind, boolean force, int weight)
	{
		Task task = new Task();
		task.type = TaskType.MOVE;
		task.loc = loc;
		task.pathfind = pathfind;
		task.forceMove = force;
		task.weight = weight;
		_tasks.add(task);
		_def_think = true;
	}
	
	public void addTaskMove(Location loc, boolean pathfind, boolean force)
	{
		addTaskMove(loc, pathfind, force, TaskDefaultWeight);
	}
	
	public void addTaskMove(Location loc, boolean pathfind)
	{
		addTaskMove(loc, pathfind, false);
	}
	
	public void addTaskTele(Location loc, int weight)
	{
		Task task = new Task();
		task.type = TaskType.TELE;
		task.loc = loc;
		task.weight = weight;
		_tasks.add(task);
		_def_think = true;	
	}
	
	public void addTaskSleep(int sleepTime, int weight)
	{
		Task task = new Task();
		task.type = TaskType.SLEEP;
		task.sleepTime = sleepTime;
		task.weight = weight;
		_tasks.add(task);
		_def_think = true;
	}
	
	protected void addTaskMove(int locX, int locY, int locZ, boolean pathfind)
	{
		addTaskMove(new Location(locX, locY, locZ), pathfind);
	}
	
	private static class TaskComparator implements Comparator<Task>
	{
		private static final Comparator<Task> instance = new TaskComparator();
		
		public static final Comparator<Task> getInstance()
		{
			return instance;
		}
		
		@Override
		public int compare(Task o1, Task o2)
		{
			if ((o1 == null) || (o2 == null))
			{
				return 0;
			}
			return o2.weight - o1.weight;
		}
	}
	
	protected class Teleport extends RunnableImpl
	{
		Location _destination;
		
		public Teleport(Location destination)
		{
			_destination = destination;
		}
		
		@Override
		public void runImpl() throws Exception
		{
			Player actor = getActor();
			if (actor != null)
			{
				actor.teleToLocation(_destination);
			}
		}
	}
	
	protected class RunningTask extends RunnableImpl
	{

		@Override
		public void runImpl() throws Exception
		{
			Player actor = getActor();
			if (actor != null)
			{
				actor.setRunning();
			}
			_runningTask = null;
		}
	}
	
	protected class MadnessTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			Player actor = getActor();
			if (actor != null)
			{
				actor.stopConfused();
			}
			_madnessTask = null;
		}
	}
	
	protected class NearestTargetComparator implements Comparator<Creature>
	{
		private final Creature actor;
		
		public NearestTargetComparator(Creature actor)
		{
			this.actor = actor;
		}
		
		@Override
		public int compare(Creature o1, Creature o2)
		{
			
			double diff = actor.getDistance3D(o1) - actor.getDistance3D(o2);
			if (diff < 0)
			{
				return -1;
			}
			return diff > 0 ? 1 : 0;
		}
	}
	
	public enum FPCIntention {
		FARMING,
		MOVING,
		QUESTING,
		IDLE,
		WAITING_PARTY
	}
	
	
	protected long AI_TASK_ATTACK_DELAY = Config.AI_TASK_ATTACK_DELAY;
	protected long AI_TASK_ACTIVE_DELAY = Config.AI_TASK_ACTIVE_DELAY;
	protected long AI_TASK_DELAY_CURRENT = AI_TASK_ACTIVE_DELAY;
	protected int MAX_PURSUE_RANGE;
	
	protected ScheduledFuture<?> _aiTask;
	
	protected ScheduledFuture<?> _runningTask;
	protected ScheduledFuture<?> _madnessTask;
	
	/** The flag used to indicate that a thinking action is in progress */
	private boolean _thinking = false;
	/** Показывает, есть ли задания */
	protected boolean _def_think = false;
	
	/** The L2NpcInstance aggro counter */
	protected long _globalAggro;
	
	//protected long _randomAnimationEnd;
	protected int _pathfindFails;
	
	/** Список заданий */
	protected final NavigableSet<Task> _tasks = new ConcurrentSkipListSet<Task>(TaskComparator.getInstance());
	
	protected Skill[] 
			_damSkills 		= Skill.EMPTY_ARRAY, 
			_dotSkills 		= Skill.EMPTY_ARRAY, 
			_debuffSkills 	= Skill.EMPTY_ARRAY, 
			_healSkills 	= Skill.EMPTY_ARRAY, 
			_buffSkills 	= Skill.EMPTY_ARRAY,
			_cubicSkills 	= Skill.EMPTY_ARRAY,
			_sumSkills 		= Skill.EMPTY_ARRAY,
			_stunSkills 	= Skill.EMPTY_ARRAY;
	
	protected long _lastActiveCheck;
	protected long _checkAggroTimestamp 		= 0;
	protected long _checkSummonTimestamp 		= 0;
	protected long _checkBuffTimestamp 			= 0;
	protected long _checkStuckTimestamp		 	= 0;
	protected long _checkEquipTimestamp 		= 0;
	protected Location _stuckLocation 			= null;
	/** Время актуальности состояния атаки */
	protected long _attackTimeout;
	
	protected long _lastFactionNotifyTime = 0;
	protected long _minFactionNotifyInterval = 10000;
	
	protected final Comparator<Creature> _nearestTargetComparator;
	
	protected final AggroListPC _aggroList;
	
	protected FPCIntention _fpcIntention = FPCIntention.IDLE; 
	
	protected HashSet<Integer> 
		_allowSkills 				= new HashSet<Integer>(),
		_allowSelfBuffSkills 		= new HashSet<Integer>(),
		_allowPartyBuffSkills 		= new HashSet<Integer>(),
		_allowServitorBuffSkills 	= new HashSet<Integer>();
	
	protected HashSet<Skill> 
		_selfBuffSkills 		= new HashSet<Skill>(),
		_servitorBuffSkills		= new HashSet<Skill>(),
		_partyBuffSkills 		= new HashSet<Skill>(); 
	
	protected FPRewardList _reward_list = null;
	
	public FPCDefaultAI(Player actor) {
		super(actor);
		
		prepareSkillsSetup();
		
		for (Skill s : actor.getAllSkillsArray())
		{
			addSkill(s);
		}
		
		MAX_PURSUE_RANGE = 2000;
		_nearestTargetComparator = new NearestTargetComparator(actor);
		_aggroList = new AggroListPC(actor);
		
	}
	
	public void prepareSkillsSetup() {
		// just function use to override
	}

	protected boolean _is_debug = false;
	
	public void debug(String msg)
	{
		debug(msg, false);
	}
	
	public void debug(String msg, boolean force)
	{
		if(!_is_debug && !force)
			return;
		
		_log.info(getActor() + " -> " +msg);
	}
	
	public void toggleDebug()
	{
		_is_debug = !_is_debug;
		_log.info(getActor() + " new debug status: " + _is_debug);
	}
	
	public void setFPCIntention(FPCIntention newIntention)
	{
		_fpcIntention = newIntention;
	}
	
	public FPCIntention getFPCIntention()
	{
		return _fpcIntention;
	}
	
	public boolean isAllowSkill(int skill_id)
	{
		return true;
	}
	
	public void addSkill(Skill skill)
	{
		if(_allowSelfBuffSkills != null && _allowSelfBuffSkills.contains(skill.getId()))
			_selfBuffSkills.add(skill);
		
		if(_allowPartyBuffSkills != null && _allowPartyBuffSkills.contains(skill.getId()))
			_partyBuffSkills.add(skill);
		
		if(_allowServitorBuffSkills != null && _allowServitorBuffSkills.contains(skill.getId()))
			_servitorBuffSkills.add(skill);
		
		if(!isAllowSkill(skill.getId()))
			return;
		
		switch(skill.getSkillType())
		{
			case PDAM:
			case MANADAM:
			case MDAM:
			case DRAIN:
			case DRAIN_SOUL:
			{
				boolean added = false;
				
				if(skill.hasEffects())
					for(EffectTemplate eff : skill.getEffectTemplates())
						switch(eff.getEffectType())
						{
							case Stun:
								_stunSkills = ArrayUtils.add(_stunSkills, skill);
								added = true;
								break;
							case DamOverTime:
							case DamOverTimeLethal:
							case ManaDamOverTime:
							case LDManaDamOverTime:
								_dotSkills = ArrayUtils.add(_dotSkills, skill);
								added = true;
								break;
							default:
						}

				if(!added)
					_damSkills = ArrayUtils.add(_damSkills, skill);
				break;
			}
			case DOT:
			case MDOT:
			case POISON:
			case BLEED:
				_dotSkills = ArrayUtils.add(_dotSkills, skill);
				break;
			case DEBUFF:
			case SLEEP:
			case ROOT:
			case PARALYZE:
			case MUTE:
			case TELEPORT_NPC:
			case AGGRESSION:
				_debuffSkills = ArrayUtils.add(_debuffSkills, skill);
				break;
			case BUFF:
				for(EffectTemplate et: skill.getEffectTemplates())
				{
					if(et.getEffectType() == EffectType.Cubic)
					{
						_cubicSkills = ArrayUtils.add(_cubicSkills, skill);
						break;
					}
				}
				_buffSkills = ArrayUtils.add(_buffSkills, skill);
				break;
			case STUN:
				_stunSkills = ArrayUtils.add(_stunSkills, skill);
				break;
			case HEAL:
			case HEAL_PERCENT:
			case HOT:
				_healSkills = ArrayUtils.add(_healSkills, skill);
				break;
			case SUMMON:
				_sumSkills = ArrayUtils.add(_sumSkills, skill);
				break;
			default:
				
				break;
		}
	}
	
	@Override
	public void runImpl()
	{

		if (_aiTask == null)
		{
			_log.info("stop at runImpl");
			return;
		}
		onEvtThink();
	}
	
	@Override
	// public final synchronized void startAITask()
	public synchronized void startAITask()
	{
		if (_aiTask == null)
		{
			AI_TASK_DELAY_CURRENT = AI_TASK_ACTIVE_DELAY;
			_aiTask = AiTaskManager.getInstance().scheduleAtFixedRate(this, 0L, AI_TASK_DELAY_CURRENT);
		}
	}
	
	// protected final synchronized void switchAITask(long NEW_DELAY)
	protected synchronized void switchAITask(long NEW_DELAY)
	{

		if (_aiTask == null)
		{
			return;
		}
		
		if (AI_TASK_DELAY_CURRENT != NEW_DELAY)
		{
			_aiTask.cancel(false);
			AI_TASK_DELAY_CURRENT = NEW_DELAY;
			_aiTask = AiTaskManager.getInstance().scheduleAtFixedRate(this, 0L, AI_TASK_DELAY_CURRENT);
		}
	}
	
	@Override
	public final synchronized void stopAITask()
	{
		if (_aiTask != null)
		{
			_aiTask.cancel(false);
			_aiTask = null;
		}
	}
	
	/**
	 * Определяет, может ли этот тип АИ видеть персонажей в режиме Silent Move.
	 * @param target L2Playable цель
	 * @return true если цель видна в режиме Silent Move
	 */
	protected boolean canSeeInSilentMove(Playable target)
	{
		return true;
		
		//return !target.isSilentMoving();
	}
	
	protected boolean canSeeInHide(Playable target)
	{
		return !target.isInvisible();
	}
	
	protected boolean checkAggression(Creature target)
	{
		Player player = getActor();

		if ((getIntention() != CtrlIntention.AI_INTENTION_ACTIVE) || !isGlobalAggro())
			return false;
		
		if (target.isAlikeDead())
			return false;
		
		if(target.isBoss())
			return false;
		
		if (target.isNpc() && target.isInvul())
			return false;
		
		if(target.isNpc() && target instanceof ChestInstance)
			return false;
		
		if (!target.isMonster())
			return false;
		
		if(Math.abs(target.getLevel() - player.getLevel()) > 9)
			return false;
		
		if (target.isPlayable())
		{
			if (!canSeeInSilentMove((Playable) target))
				return false;

			if (!canSeeInHide((Playable) target))
				return false;
			
			if (target.isPlayer() && ((Player) target).isGM() && target.isInvisible())
				return false;
		}
		
		if (!GeoEngine.canSeeTarget(player, target, false))
			return false;
		
		_aggroList.addDamageHate(target, 0, 2);
		player.setTarget(target); // target monster before attack
		
		if ((target.isServitor() || target.isPet()))
		{
			_aggroList.addDamageHate(target.getPlayer(), 0, 1);
		}
		
		startRunningTask(AI_TASK_ATTACK_DELAY);
		setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
		
		return true;
	}
	
	/*
	 * Think Moving
	 */
	
	protected long _checkRandomWalkTimestamp;
	protected FarmLocation _farmLocation = null;
	
	public FarmLocation getFarmLocation()
	{
		return _farmLocation;
	}
	
	public void setFarmLocation(FarmLocation loc)
	{
		_farmLocation = loc;
	}
	
	protected int getMaxDriftRange()
	{
		// TODO add class specific
		return 500;
	}
	
	protected boolean maybeMoveToHome()
	{
		Player player = getActor();
		
		Location basePos = getFarmLocation();
		
		if(player == null)
			return false;
		
		if(basePos == null)
			return false;
		
		if(player.isInRange(basePos, getMaxDriftRange()))
			return false;
		
		Location pos = Location.findPointToStay(player, basePos, 0, getMaxDriftRange());
		
		if(!player.moveToLocation(pos.x, pos.y, pos.z, 0, true))
		{
			teleportHome();
		}
		
		return true;
	}
	
	protected void returnHome()
	{
		returnHome(true, Config.ALWAYS_TELEPORT_HOME);
	}

	protected void teleportHome()
	{
		returnHome(true, true);
	}

	protected void returnHome(boolean clearAggro, boolean teleport)
	{
		Player player = getActor();
			
		Location baseLoc = getFarmLocation();

		// Удаляем все задания
		clearTasks();
		player.stopMove();

		if(clearAggro)
			clearAggroList();

		setAttackTimeout(Long.MAX_VALUE);
		setAttackTarget(null);
		changeIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);

		if(teleport)
		{
			player.broadcastPacketToOthers(new MagicSkillUse(player, player, 2036, 1, 500, 0));
			player.teleToLocation(baseLoc.x, baseLoc.y, GeoEngine.getHeight(baseLoc, player.getGeoIndex()));
		}
		else
		{
			addTaskMove(baseLoc, false);
		}
	}
	
	protected boolean randomWalk()
	{
		return !_actor.isMoving && maybeMoveToHome();
	}
	
	protected boolean randomWalk(int range)
	{
		
		Player actor = getActor();
		
		long now = System.currentTimeMillis();
		if ((now - _checkRandomWalkTimestamp) < 3000 )
			return false;
		
		if(actor.isDead())
			return false;

		if(actor.isMoving)
			return false;
		
		Location loc = Location.findAroundPosition(actor, 100, range);
		addTaskMove(loc, true);
		_checkRandomWalkTimestamp = now;
		
		return !actor.isMoving;
	}
	
	protected boolean thinkAggro()
	{
		Player player = getActor();
		
		// Finish aggro list
		if(_aggroList.isEmpty())
			return false;
		
		List<Creature> chars = World.getAroundCharacters(player, MAX_PURSUE_RANGE, 500);
		CollectionUtils.eqSort(chars, _nearestTargetComparator);
		for (Creature cha : chars)
		{
			if (_aggroList.get(cha) == null)
				continue;
			
			if (checkAggression(cha))
				return true;
		}
		
		return false;
	}
	
	protected boolean thinkMadness()
	{
		Player player = getActor();
		
		if(getFPCInfo().getPveStyle() == FPCPveStyle.PARTY && player.getParty().isLeader(player))
			return false;
		
		if(player.isInPeaceZone())
			return false;
		
		if(getFPCIntention() != FPCIntention.FARMING)
			return false;
		
		// New madness
		long now = System.currentTimeMillis();
		if (now < _checkAggroTimestamp)
			return false;
		
		_checkAggroTimestamp = now + Rnd.get(2000, 5000);
		
		List<Creature> chars = World.getAroundCharacters(player, MAX_PURSUE_RANGE, 500);
		CollectionUtils.eqSort(chars, _nearestTargetComparator);
//		long seed = System.nanoTime();
//		Collections.shuffle(chars, new Random(seed));
		for (Creature cha : chars)
		{	
			// preventing ks
			if(cha.getCurrentHpPercents() < 100D)
				continue;

			if (checkAggression(cha))
				return true;
		}
		
		return false;
	}
	
	protected boolean thinkEquip() 
	{
		return false;
	}
	
	/**
	 * @return true if the action is performed, false if not
	 */
	protected void thinkActive()
	{
//		debug("i'm think active");
		Player player = getActor();
		
		if (player.isActionsDisabled())
		{
			debug("i'm isActionsDisabled");
			return;
		}
		
		if (_def_think)
		{
			if (doTask())
				clearTasks();
			return;
		}
		
		if(thinkClass())
			return;
		
		if(thinkEquip() || thinkBuff() || thinkSummon() || thinkCubic())
			return;
		
		if(thinkFPCIdle() || thinkFPCWaitingParty() || thinkFarming())
			return;
		
		if(thinkAggro() || thinkMadness())
		{
			_pathfindFails = 0;
			return;
		}
		
		if(player.isInParty())
		{
			Player leader = player.getParty().getPartyLeader();
			if(player != leader)
			{
				double distance = player.getDistance(leader.getX(), leader.getY());
				if(distance > 4000)
					player.teleToLocation(Location.findPointToStay(leader, 100, 250));
				else if(distance > 500)
					addTaskMove(Location.findPointToStay(leader, 100, 250), false, true);
				return;
			}
		}
		
		if(randomWalk())
		{
			return;
		}
		
	}
	
	private long _upClassLTS = 0L;
	private final int _upClassInteval = 1*60*1000;
	
	protected boolean isAllowClass()
	{
		return true;
	}
	
	private boolean thinkClass() {
		
		if(_upClassLTS > System.currentTimeMillis())
			return false;
		
		_upClassLTS = System.currentTimeMillis() + _upClassInteval;
		
		Player player = getActor();
		
		boolean hasNewClass = ClassFunctions.upClass(player);
		rewardSkillsFPC();
		
		if(hasNewClass)
			_reward_list = FPItemHolder.getRewardList(player, false);
		
		if(hasNewClass && !isAllowClass())
		{
			getFPCInfo().updateAI();
		}
		
		return true;
	}
	
	public int rewardSkillsFPC()
	{
		Player player = getActor();
		int addedSkillsCount = 0;
		for(SkillLearn sl : SkillAcquireHolder.getInstance().getAvailableMaxLvlSkills(player, AcquireType.NORMAL))
		{
			Skill skill = SkillTable.getInstance().getInfo(sl.getId(), sl.getLevel());
			if(skill == null)
				continue;

			if(player.addSkill(skill, true) == null)
			{
				addedSkillsCount++;
				addSkill(skill);
			}
		}

		player.updateStats();

		return addedSkillsCount;
	}

	protected boolean thinkFarming() {
		return false;
	}

	protected boolean thinkCubic() {
		return false;
	}

	protected boolean thinkSummon() {
		return false;
	}

	protected boolean thinkBuff() {
		return false;
	}

	protected boolean thinkFPCIdle() {
		return false;
	}
	
	protected boolean thinkFPCWaitingParty()
	{
		return false;
	}

	@Override
	protected void onIntentionIdle()
	{

		Player actor = getActor();
		
		// Remove all jobs
		clearTasks();
		
		actor.stopMove();
		_aggroList.clear(true);
		setAttackTimeout(Long.MAX_VALUE);
		setAttackTarget(null);
		
		changeIntention(CtrlIntention.AI_INTENTION_IDLE, null, null);
	}
	
	@Override
	protected void onIntentionActive()
	{

		Player actor = getActor();
		
		actor.stopMove();
		setAttackTimeout(Long.MAX_VALUE);
		
		if (getIntention() != CtrlIntention.AI_INTENTION_ACTIVE)
		{
			switchAITask(AI_TASK_ACTIVE_DELAY);
			changeIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
		}
		
		onEvtThink();
	}
	
	@Override
	protected void onIntentionAttack(Creature target)
	{

		Player actor = getActor();
		
		// Removes all jobs
		clearTasks();
		
		actor.stopMove();
		setAttackTarget(target);
		setAttackTimeout(getMaxAttackTimeout() + System.currentTimeMillis());
		setGlobalAggro(0);
		
		if (getIntention() != CtrlIntention.AI_INTENTION_ATTACK)
		{
			changeIntention(CtrlIntention.AI_INTENTION_ATTACK, target, null);
			switchAITask(AI_TASK_ATTACK_DELAY);
		}
		
		onEvtThink();
	}
	
	protected boolean canAttackCharacter(Creature target)
	{
		return target.isPlayable();
	}
	
	protected boolean checkTarget(Creature target, int range)
	{

		Player actor = getActor();
		if ((target == null) || target.isAlikeDead() || !actor.isInRangeZ(target, range))
		{
			return false;
		}
		
		// if you do not see the chars in hate - not attack them
		final boolean hided = target.isPlayable() && !canSeeInHide((Playable) target);
		
		if (!hided && actor.isConfused())
		{
			return true;
		}
		
		// In the state of attack, attack all on whom we have Hate
		if (getIntention() == CtrlIntention.AI_INTENTION_ATTACK)
		{
			AggroInfoPC ai = _aggroList.get(target);
			if (ai != null)
			{
				if (hided)
				{
					ai.hate = 0; //clear hate
					return false;
				}
				return ai.hate > 0;
			}
			return false;
		}
		
		debug("pass check hate");
		
		return canAttackCharacter(target);
	}
	
	public void setAttackTimeout(long time)
	{
		_attackTimeout = time;
	}
	
	protected long getAttackTimeout()
	{
		return _attackTimeout;
	}
	
	protected void thinkAttack()
	{
		Player player = getActor();
		if (player.isDead())
		{
			return;
		}
		
		if(player.isSitting())
		{
			player.standUp();
		}
		
		if (doTask() && !player.isAttackingNow() && !player.isCastingNow())
		{
			if(!createNewTask())
			{
				if(System.currentTimeMillis() > getAttackTimeout())
					returnHome();  // TODO check what it look like
			}
		}
	}
	
	@Override
	protected void onEvtReadyToAct()
	{
		onEvtThink();
	}
	
	@Override
	protected void onEvtArrivedTarget()
	{
		onEvtThink();
	}
	
	@Override
	protected void onEvtArrived()
	{
		onEvtThink();
	}
	
	protected boolean tryMoveToTarget(Creature target)
	{
		return tryMoveToTarget(target, 150);
	}
	
	protected boolean tryMoveToTarget(Creature target, int range)
	{
		Player actor = getActor();

		if(!actor.followToCharacter(target, actor.getPhysicalAttackRange(), true))
			_pathfindFails++;

		if(_pathfindFails >= getMaxPathfindFails() && (System.currentTimeMillis() > getAttackTimeout() - getMaxAttackTimeout() + getTeleportTimeout()) && actor.isInRange(target, MAX_PURSUE_RANGE))
		{
			_pathfindFails = 0;

//			if(target.isPlayable())
//			{
//				AggroInfo hate = actor.getAggroList().get(target);
//				if(hate == null || hate.hate < 100)
//				{
//					if(!(actor instanceof DecoyInstance))
//					{
//						returnHome();
//						return false;
//					}
//				}
//			}
			Location loc = GeoEngine.moveCheckForAI(target.getLoc(), actor.getLoc(), actor.getGeoIndex());
			if(!GeoEngine.canMoveToCoord(actor.getX(), actor.getY(), actor.getZ(), loc.x, loc.y, loc.z, actor.getGeoIndex())) // Для подстраховки
				loc = target.getLoc();
			actor.teleToLocation(loc);
		}

		return true;
	}
	
	protected boolean maybeNextTask(Task currentTask)
	{
//		_log.info("removed task", new Exception());
		// next task
		_tasks.remove(currentTask);
		// If there are no more jobs - define new one
		if (_tasks.size() == 0)
		{
			return true;
		}
		return false;
	}
	
	protected int getReuseDelay(Creature target)
	{
		Player actor = getActor();
		WeaponTemplate weaponItem = actor.getActiveWeaponTemplate();
		if(weaponItem != null)
		{
			if(weaponItem.getAttackReuseDelay() > 0)
			{
				int reuse = (int) (weaponItem.getAttackReuseDelay() * actor.getReuseModifier(target) * 666 * actor.calcStat(Stats.BASE_P_ATK_SPD, 0, target, null) / 293. / actor.getPAtkSpd());
				if(reuse > 0)
				{
					return reuse;
//					sendPacket(new SetupGauge(this, SetupGauge.Colors.RED_MINI, reuse));
//					_attackReuseEndTime = reuse + System.currentTimeMillis() - 75;
//					if(reuse > sAtk)
//						ThreadPoolManager.getInstance().schedule(new NotifyAITask(this, CtrlEvent.EVT_READY_TO_ACT, null, null), reuse);
				}
			}
		}
		//
		//(int) (actor.getActiveWeaponItem().getAttackReuseDelay() * actor.getReuseModifier(target) * 666 * actor.calcStat(Stats.ATK_BASE, 0, target, null) / 293. / actor.getPAtkSpd());
		return 0;
	}
	
	protected long _sleepEnd = 0L;
	
	protected boolean doTaskMove(Task currentTask)
	{
		
		return false;
	}
	
	protected boolean doTask()
	{
		long now = System.currentTimeMillis();
		
		if(_sleepEnd > now)
			return false;
		
		Player actor = getActor();
		
		if (!_def_think)
			return true;
		
		Task currentTask = _tasks.pollFirst();
		
		if (currentTask == null)
		{
			clearTasks();
			return true;
		}
		
		if (actor.isDead() || actor.isAttackingNow() || actor.isCastingNow())
		{
			return false;
		}
		
		debug("execute task type:"+currentTask.type +" size:"+_tasks.size());
		
		switch (currentTask.type)
		{
			// Task "come running at the given coordinates"
			case MOVE:
				if (actor.isMovementDisabled() || !getIsMobile())
					return true;
				
				if (actor.isInRange(currentTask.loc, 100))
					return maybeNextTask(currentTask);
				
				if (actor.isMoving)
				{
//					_tasks.add(currentTask);
					return false;
				}
				
				if (!actor.moveToLocation(currentTask.loc, 0, currentTask.pathfind))
				{
					clientStopMoving();
					_pathfindFails = 0;
					actor.teleToLocation(currentTask.loc);
					// actor.broadcastPacketToOthers(new MagicSkillUse(actor, actor, 2036, 1, 500, 600000));
					// ThreadPoolManager.getInstance().scheduleAi(new Teleport(currentTask.loc), 500, false);
					return maybeNextTask(currentTask);
				}
//				if(currentTask.forceMove)
//					_tasks.add(currentTask);
				break;
			case TELE:
				if (actor.isMovementDisabled() || !getIsMobile())
					return true;
				
				if (actor.isInRange(currentTask.loc, 100))
					return maybeNextTask(currentTask);
				
				if (actor.isMoving)
				{
					_tasks.add(currentTask);
					return false;
				}
				
				actor.teleToLocation(currentTask.loc);
				return maybeNextTask(currentTask);
			case SLEEP:
				if (actor.isMovementDisabled() || !getIsMobile())
					return true;
				
				if (actor.isMoving)
				{
					_tasks.add(currentTask);
					return false;
				}
				_sleepEnd = now + currentTask.sleepTime;
				return maybeNextTask(currentTask);
			// Task "to run - to strike"
			case ATTACK:
			{
				Creature target = currentTask.target.get();
				
				debug("ATTACK target " + target.getName());
				
				if (!checkTarget(target, MAX_PURSUE_RANGE))
				{
					debug("target is too far");
					return true;
				}
				setAttackTarget(target);
				
				if (actor.isMoving)
				{
					debug("is moving");
					return Rnd.chance(25);
				}
				debug("(actor.getRealDistance3D(target) <= (actor.getPhysicalAttackRange() + 40)) " + (actor.getRealDistance3D(target) <= (actor.getPhysicalAttackRange() + 40)) );
				debug("GeoEngine.canSeeTarget(actor, target, false) " + GeoEngine.canSeeTarget(actor, target, false));
				if ((actor.getRealDistance3D(target) <= (actor.getPhysicalAttackRange() + 40)) && GeoEngine.canSeeTarget(actor, target, false))
				{
					debug("client stop moving");
					clientStopMoving();
					_pathfindFails = 0;
					setAttackTimeout(getMaxAttackTimeout() + System.currentTimeMillis());
					
					if(attackTime > now)
					{
						debug("attackTime " + attackTime + " > now " + now);
						//return true;
					}
					else{
						debug("fuckyou ===================== attackTime " + attackTime + " > now " + now);
					}
					int reuse = getReuseDelay(target);
					debug("Now: " + now + " Attack Reuse: " + reuse);
					
					attackTime = now + reuse - 75;
					debug("do attack");
					actor.doAttack(target);
					//set the summon attack also
					
					if (actor.getServitors().length > 0){
						for (Servitor summon: actor.getServitors())
						{
							summon.doAttack(target);
						}
					}
					
					return maybeNextTask(currentTask);
				}
				
				if (actor.isMovementDisabled() || !getIsMobile())
					return true;
				debug("try move to target");
				tryMoveToTarget(target);
			}
				break;
			// Task 'go there - attack with skill "
			case CAST:
			{
				Creature target = currentTask.target.get();
				if(target != null)
					actor.setTarget(target);
				
				if (actor.isMuted(currentTask.skill) || actor.isSkillDisabled(currentTask.skill) || actor.isUnActiveSkill(currentTask.skill.getId()))
					return true;
				
				boolean isAoE = currentTask.skill.getTargetType() == Skill.SkillTargetType.TARGET_AURA;
				int castRange = currentTask.skill.getAOECastRange();
				
				if (!checkTarget(target, MAX_PURSUE_RANGE + castRange))
				{
					return true;
				}
				
				setAttackTarget(target);
				
				if ((actor.getRealDistance3D(target) <= (castRange + 60)) && GeoEngine.canSeeTarget(actor, target, false))
				{
					clientStopMoving();
					_pathfindFails = 0;
					setAttackTimeout(getMaxAttackTimeout() + System.currentTimeMillis());
					
					for(int item_id : currentTask.skill.getItemConsumeId())
					{
						ItemFunctions.addItem(actor, item_id, 500, false);
					}
					
					actor.doCast(currentTask.skill, isAoE ? actor : target, false);
					
					//set the summon physical attack					
					if (actor.getServitors().length > 0){
						for (Servitor summon: actor.getServitors())
						{
							summon.doAttack(target);
						}
					}
					
					//set the summon cast skill also
					/*
					Summon summon = actor.getPet();
					if(summon != null)
					{
						TIntObjectHashMap<Skill> petSkills = summon.getTemplate().getSkills();
						List<Integer> petSkillIdList = getPetSkillList();
						if(petSkillIdList != null)
							summon.doCast(petSkills.get(petSkillIdList.get(Rnd.get(petSkillIdList.size()))), target, false);
					}
					*/
					return maybeNextTask(currentTask);
				}
				
				if (actor.isMoving && Rnd.chance(10))
				{
					return Rnd.chance(10);
				}

				if (actor.isMovementDisabled() || !getIsMobile())
				{
					return true;
				}
				
				tryMoveToTarget(target, castRange);
			}
				break;
			// Task "to run - use skill"
			case BUFF:
			{
				
				Creature target = currentTask.target.get();
				
				if (actor.isMuted(currentTask.skill) || actor.isSkillDisabled(currentTask.skill) || actor.isUnActiveSkill(currentTask.skill.getId()))
				{
					return true;
				}
				
				if ((target == null) || target.isAlikeDead() || !actor.isInRange(target, 2000))
				{
					return true;
				}
				
				boolean isAoE = currentTask.skill.getTargetType() == Skill.SkillTargetType.TARGET_AURA;
				int castRange = currentTask.skill.getAOECastRange();
				
				if (actor.isMoving && Rnd.chance(10))
				{
					return true;
				}
				
				if ((actor.getRealDistance3D(target) <= (castRange + 60)) && GeoEngine.canSeeTarget(actor, target, false))
				{
					clientStopMoving();
					_pathfindFails = 0;
					for(int item_id : currentTask.skill.getItemConsumeId())
					{
						ItemFunctions.addItem(actor, item_id, 500, false);
					}
					actor.doCast(currentTask.skill, isAoE ? actor : target, !target.isPlayable());
					return maybeNextTask(currentTask);
				}
				
				if (actor.isMovementDisabled() || !getIsMobile())
				{
					return true;
				}
				
				tryMoveToTarget(target);
			}
				break;
		}
		
		return false;
	}
	
	protected boolean createNewTask()
	{
		return false;
	}
	
	protected boolean defaultNewTask()
	{

		clearTasks();
		
		Player actor = getActor();
		Creature target;
		if ((actor == null) || ((target = prepareTarget()) == null))
		{
			return false;
		}
		
		double distance = actor.getDistance(target);
		return chooseTaskAndTargets(null, target, distance);
	}
	
	protected int _countCasting = 0;
	
	@Override
	protected void onEvtThink()
	{
		Player actor = getActor();
		
//		debug("I'm on event think.... actor.isActionsDisabled():"+actor.isActionsDisabled());
		
		webUpdateStatus();
		
		if(thinkDead())
			return;
		
		if(actor.isCastingNow())
		{
			_countCasting++;
		}
		
		if(_countCasting > 20)
		{
			actor.clearCastVars(false);
			_countCasting = 0;
		}
		
		if (_thinking || (actor == null) || actor.isActionsDisabled() || actor.isAfraid())
			return;
		
		if(actor.isSitting()) 
			actor.standUp();
		
		_thinking = true;
		try
		{
			if (getIntention() == CtrlIntention.AI_INTENTION_ACTIVE)
			{
				thinkActive();
			}
			else if (getIntention() == CtrlIntention.AI_INTENTION_ATTACK)
			{
				thinkAttack();
			}
		}
		finally
		{
			_thinking = false;
		}
	}
	
	private long _webUpdateStatusTimestamp = 0;
	private long _webUpdateStatusInterval = 30000;
	
	
	private void webUpdateStatus() {
		
		if(_webUpdateStatusTimestamp > System.currentTimeMillis())
			return;
		
		_webUpdateStatusTimestamp = System.currentTimeMillis() + _webUpdateStatusInterval;
		
		// TODO Auto-generated method stub
		Player player = getActor();
		
		if(player == null)
			return;
		
		Class<?> enclosingClass = getClass().getEnclosingClass();
		
		StringBuilder builder = new StringBuilder();
		builder.append(player.getAccountName());
		builder.append(";");
		builder.append(player.getName());
		builder.append(";");
		builder.append(player.getClassId().toString());
		builder.append(";");
		builder.append(player.getLevel());
		builder.append(";");
		builder.append(player.getExp());
		builder.append(";");
		builder.append((int)player.getCurrentHp());
		builder.append(";");
		builder.append(player.getMaxHp());
		builder.append(";");
		builder.append((int)player.getCurrentMp());
		builder.append(";");
		builder.append(player.getMaxMp());
		builder.append(";");
		builder.append((int)player.getCurrentCp());
		builder.append(";");
		builder.append(player.getMaxCp());
		builder.append(";");
		builder.append(enclosingClass != null ? enclosingClass.getName() : getClass().getName());
		builder.append(";");
		builder.append(getFPCIntention().toString());
		L2MQ.addBackgroundJob("FPStatus", builder.toString());
	}

	protected boolean thinkDead() {
		return false;
	}

	@Override
	protected void onEvtDead(Creature killer)
	{	
		super.onEvtDead(killer);
		
		// clear aggrolist when dead
		_aggroList.clear();
	}
	
	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{

		Player actor = getActor();
		if ((attacker == null) || actor.isDead())
		{
			return;
		}
		
		// Adding only hate damage if an attacker - a game character will be added to L2NpcInstance.onReduceCurrentHp
		_aggroList.addDamageHate(attacker, damage, damage);
		
		// Usually 1 hate added summon the owner to death summon mob attacked the host.
		if ((damage > 0) && (attacker.isServitor() || attacker.isPet()))
		{
//			_aggroList.addDamageHate(attacker.getPlayer(), 0, actor.getParameter("searchingMaster", false) ? damage : 1);
			_aggroList.addDamageHate(attacker.getPlayer(), damage, damage);

		}
		
		if (getIntention() != CtrlIntention.AI_INTENTION_ATTACK)
		{
			if (!actor.isRunning())
			{
				startRunningTask(AI_TASK_ATTACK_DELAY);
			}
			setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
		}
		
	}
	
	@Override
	protected void onEvtAggression(Creature attacker, int aggro)
	{

		Player actor = getActor();
		if ((attacker == null) || actor.isDead())
		{
			return;
		}
		
		_aggroList.addDamageHate(attacker, 0, aggro);
		
		// Usually 1 hate added summon the owner to death summon mob attacked the host.
		if ((aggro > 0) && (attacker.isServitor() || attacker.isPet()))
		{
			_aggroList.addDamageHate(attacker.getPlayer(), 0, aggro);
		}
		
		if (getIntention() != CtrlIntention.AI_INTENTION_ATTACK)
		{
			if (!actor.isRunning())
			{
				startRunningTask(AI_TASK_ATTACK_DELAY);
			}
			setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
		}
	}
	
	
	protected Creature prepareTarget()
	{

//		Player actor = getActor();
				
		Creature target = getAttackTarget();
		
		if(target != null && checkTarget(target, MAX_PURSUE_RANGE))
		{
			return target;
		}
		
		// The new goal based on the aggressiveness
		List<Creature> hateList = _aggroList.getHateList();
		Creature hated = null;
		for (Creature cha : hateList)
		{
			// Does not fit, clear the hate
			if (!checkTarget(cha, MAX_PURSUE_RANGE))
			{
				_aggroList.remove(cha, true);
				continue;
			}
			hated = cha;
			break;
		}
		
		
		if (hated != null)
		{
			setAttackTarget(hated);
			return hated;
		}
		
		return null;
	}
	
	
	protected boolean canUseSkill(Skill skill, Creature target, double distance, boolean override)
	{

		Player actor = getActor();
		
		if ((skill == null) || skill.isNotUsedByAI())
		{
			return false;
		}
		
		if(!skill.checkCondition(actor, target, true, false, false))
		{
			return false;
		}
				
		if ((skill.getTargetType() == Skill.SkillTargetType.TARGET_SELF) && (target != actor))
		{
			return false;
		}
		
		int castRange = skill.getAOECastRange();
		if ((castRange <= 200) && (distance > 200))
		{
			return false;
		}
		
		if (actor.isSkillDisabled(skill) || actor.isMuted(skill) || actor.isUnActiveSkill(skill.getId()))
		{
			return false;
		}
		
		double mpConsume2 = skill.getMpConsume2();
		if (skill.isMagic())
		{
			mpConsume2 = actor.calcStat(Stats.MP_MAGIC_SKILL_CONSUME, mpConsume2, target, skill);
		}
		else
		{
			mpConsume2 = actor.calcStat(Stats.MP_PHYSICAL_SKILL_CONSUME, mpConsume2, target, skill);
		}
		if (actor.getCurrentMp() < mpConsume2)
		{
			return false;
		}
		
		if (!override && target.getEffectList().containsEffects(skill))
		{
			return false;
		}
		
		return true;
	}
	
	protected boolean canUseSkill(Skill skill, Creature target, double distance)
	{
		return canUseSkill(skill, target, distance, false);
	}
	
	protected boolean canUseSkill(Skill sk, Creature target)
	{
		return canUseSkill(sk, target, 0);
	}
	
	protected boolean canUseSkill(int sk, Creature target, double distance, boolean override)
	{
		return canUseSkill(getActor().getKnownSkill(sk), target, distance, override);
	}
	
	protected boolean canUseSkill(int sk, Creature target, double distance)
	{
		return canUseSkill(sk, target, distance, false);
	}
	
	protected boolean canUseSkill(int sk, Creature target)
	{
		return canUseSkill(sk, target, 0);
	}
	
	protected Skill[] selectUsableSkills(Creature target, double distance, Skill[] skills)
	{

		if ((skills == null) || (skills.length == 0) || (target == null))
		{
			return null;
		}
		
		Skill[] ret = null;
		int usable = 0;
		
		for (Skill skill : skills)
		{
			if (canUseSkill(skill, target, distance))
			{
				if (ret == null)
				{
					ret = new Skill[skills.length];
				}
				ret[usable++] = skill;
			}
		}
		
		if ((ret == null) || (usable == skills.length))
		{
			return ret;
		}
		
		if (usable == 0)
		{
			return null;
		}
		
		ret = Arrays.copyOf(ret, usable);
		return ret;
	}
	
	protected static Skill selectTopSkillByDamage(Creature actor, Creature target, double distance, Skill[] skills)
	{

		if ((skills == null) || (skills.length == 0))
		{
			return null;
		}
		
		if (skills.length == 1)
		{
			return skills[0];
		}
		
		RndSelector<Skill> rnd = new RndSelector<Skill>(skills.length);
		double weight;
		for (Skill skill : skills)
		{
			weight = (skill.getSimpleDamage(actor, target) * skill.getAOECastRange()) / distance;
			if (weight < 1.)
			{
				weight = 1.;
			}
			rnd.add(skill, (int) weight);
		}
		return rnd.select();
	}
	
	protected static Skill selectTopSkillByDebuff(Creature actor, Creature target, double distance, Skill[] skills)
	{

		if ((skills == null) || (skills.length == 0))
		{
			return null;
		}
		
		if (skills.length == 1)
		{
			return skills[0];
		}
		
		RndSelector<Skill> rnd = new RndSelector<Skill>(skills.length);
		double weight;
		for (Skill skill : skills)
		{
			if (skill.getSameByAbnormalType(target) != null)
			{
				continue;
			}
			if ((weight = (100. * skill.getAOECastRange()) / distance) <= 0)
			{
				weight = 1;
			}
			rnd.add(skill, (int) weight);
		}
		return rnd.select();
	}
	
	protected static Skill selectTopSkillByBuff(Creature target, Skill[] skills)
	{

		if ((skills == null) || (skills.length == 0))
		{
			return null;
		}
		
		if (skills.length == 1)
		{
			return skills[0];
		}
		
		RndSelector<Skill> rnd = new RndSelector<Skill>(skills.length);
		double weight;
		for (Skill skill : skills)
		{
			if (skill.getSameByAbnormalType(target) != null)
			{
				continue;
			}
			if ((weight = skill.getPower()) <= 0)
			{
				weight = 1;
			}
			rnd.add(skill, (int) weight);
		}
		return rnd.select();
	}
	
	protected static Skill selectTopSkillByHeal(Creature target, Skill[] skills)
	{

		if ((skills == null) || (skills.length == 0))
		{
			return null;
		}
		
		double hpReduced = target.getMaxHp() - target.getCurrentHp();
		if (hpReduced < 1)
		{
			return null;
		}
		
		if (skills.length == 1)
		{
			return skills[0];
		}
		
		RndSelector<Skill> rnd = new RndSelector<Skill>(skills.length);
		double weight;
		for (Skill skill : skills)
		{
			if ((weight = Math.abs(skill.getPower() - hpReduced)) <= 0)
			{
				weight = 1;
			}
			rnd.add(skill, (int) weight);
		}
		return rnd.select();
	}
	
	protected void addDesiredSkill(Map<Skill, Integer> skillMap, Creature target, double distance, Skill[] skills)
	{

		if ((skills == null) || (skills.length == 0) || (target == null))
		{
			return;
		}
		for (Skill sk : skills)
		{
			addDesiredSkill(skillMap, target, distance, sk);
		}
	}
	
	protected void addDesiredSkill(Map<Skill, Integer> skillMap, Creature target, double distance, Skill skill)
	{

		if ((skill == null) || (target == null) || !canUseSkill(skill, target))
		{
			return;
		}
		int weight = (int) -Math.abs(skill.getAOECastRange() - distance);
		if (skill.getAOECastRange() >= distance)
		{
			weight += 1000000;
		}
		else if (skill.isNotTargetAoE() && (skill.getTargets(getActor(), target, false).size() == 0))
		{
			return;
		}
		skillMap.put(skill, weight);
	}
	
	protected void addDesiredHeal(Map<Skill, Integer> skillMap, Skill[] skills)
	{

		if ((skills == null) || (skills.length == 0))
		{
			return;
		}
		Player actor = getActor();
		double hpReduced = actor.getMaxHp() - actor.getCurrentHp();
		double hpPercent = actor.getCurrentHpPercents();
		if (hpReduced < 1)
		{
			return;
		}
		int weight;
		for (Skill sk : skills)
		{
			if (canUseSkill(sk, actor) && (sk.getPower() <= hpReduced))
			{
				weight = (int) sk.getPower();
				if (hpPercent < 50)
				{
					weight += 1000000;
				}
				skillMap.put(sk, weight);
			}
		}
	}
	
	protected Skill selectTopSkill(Map<Skill, Integer> skillMap)
	{

		if ((skillMap == null) || skillMap.isEmpty())
		{
			return null;
		}
		int nWeight, topWeight = Integer.MIN_VALUE;
		for (Skill next : skillMap.keySet())
		{
			if ((nWeight = skillMap.get(next)) > topWeight)
			{
				topWeight = nWeight;
			}
		}
		if (topWeight == Integer.MIN_VALUE)
		{
			return null;
		}
		
		Skill[] skills = new Skill[skillMap.size()];
		nWeight = 0;
		for (Map.Entry<Skill, Integer> e : skillMap.entrySet())
		{
			if (e.getValue() < topWeight)
			{
				continue;
			}
			skills[nWeight++] = e.getKey();
		}
		return skills[Rnd.get(nWeight)];
	}
	
	protected boolean chooseTaskAndTargets(Skill skill, Creature target, double distance)
	{
		Player actor = getActor();
		
		// Use skill if you can, otherwise attack
		if (skill != null)
		{
			skill = skill.getElementalSkill(actor);
			// check skill delayed
			if (actor.isMuted(skill) || actor.isSkillDisabled(skill) || actor.isUnActiveSkill(skill.getId()))
			{
				return false;
			}
			
			// Test targets and changed if necessary
			if (actor.isMovementDisabled() && (distance > (skill.getAOECastRange() + 60)))
			{
				target = null;
				if (skill.isOffensive())
				{
					LazyArrayList<Creature> targets = LazyArrayList.newInstance();
					for (Creature cha : _aggroList.getHateList())
					{
						if (!checkTarget(cha, skill.getAOECastRange() + 60) || !canUseSkill(skill, cha))
						{
							continue;
						}
						targets.add(cha);
					}
					if (!targets.isEmpty())
					{
						target = targets.get(Rnd.get(targets.size()));
					}
					LazyArrayList.recycle(targets);
				}
			}
			
			if (target == null)
			{
				return false;
			}
			
			
			// Add a new task
			if (skill.isOffensive())
			{
				addTaskCast(target, skill);
			}
			else
			{
				addTaskBuff(target, skill);
			}
			return true;
		}
		
		// Changing target if necessary
		if (actor.isMovementDisabled() && (distance > (actor.getPhysicalAttackRange() + 40)))
		{
			target = null;
			LazyArrayList<Creature> targets = LazyArrayList.newInstance();
			for (Creature cha : _aggroList.getHateList())
			{
				if (!checkTarget(cha, actor.getPhysicalAttackRange() + 40))
				{
					continue;
				}
				targets.add(cha);
			}
			if (!targets.isEmpty())
			{
				target = targets.get(Rnd.get(targets.size()));
			}
			LazyArrayList.recycle(targets);
		}
		
		if (target == null)
		{
			return false;
		}
		
		//Add a new task
		addTaskAttack(target);
		return true;
	}
	
	@Override
	public boolean isActive()
	{
		return _aiTask != null;
	}
	
	protected void clearTasks()
	{
		_def_think = false;
		_tasks.clear();
	}
	
	/** переход в режим бега через определенный интервал времени */
	protected void startRunningTask(long interval)
	{
		Player actor = getActor();
		if ((actor != null) && (_runningTask == null) && !actor.isRunning())
		{
			_runningTask = ThreadPoolManager.getInstance().schedule(new RunningTask(), interval);
		}
	}
	
	protected boolean isGlobalAggro()
	{
		if (_globalAggro == 0)
		{
			return true;
		}
		if (_globalAggro <= System.currentTimeMillis())
		{
			_globalAggro = 0;
			return true;
		}
		return false;
	}
	
	public void setGlobalAggro(long value)
	{
		_globalAggro = value;
	}
	
	protected boolean defaultThinkBuff(int rateSelf)
	{
		return defaultThinkBuff(rateSelf, 0);
	}
	
	protected boolean defaultThinkBuff(int rateSelf, int rateFriends)
	{
		Player actor = getActor();
		if (actor.isDead())
		{
			return true;
		}
		
		if (Rnd.chance(rateSelf))
		{
			double actorHp = actor.getCurrentHpPercents();
			
			Skill[] skills = actorHp < 50 ? selectUsableSkills(actor, 0, _healSkills) : selectUsableSkills(actor, 0, _buffSkills);
			if ((skills == null) || (skills.length == 0))
			{
				return false;
			}
			
			Skill skill = skills[Rnd.get(skills.length)];
			addTaskBuff(actor, skill);
			return true;
		}
		
		if (Rnd.chance(rateFriends))
		{
			for (Player member : actor.getParty().getPartyMembers())
			{
				double targetHp = member.getCurrentHpPercents();
				
				Skill[] skills = targetHp < 50 ? selectUsableSkills(actor, 0, _healSkills) : selectUsableSkills(actor, 0, _buffSkills);
				if ((skills == null) || (skills.length == 0))
				{
					continue;
				}
				
				Skill skill = skills[Rnd.get(skills.length)];
				addTaskBuff(actor, skill);
				return true;
			}
		}
		
		return false;
	}
	
	protected boolean defaultSubFightTask(Creature target)
	{
		return false;
	}
	
	protected boolean defaultFightTask()
	{
		clearTasks();
		
		Player actor = getActor();
		if (actor.isDead() || actor.isAMuted())
		{
			return false;
		}
		
		Creature target;
		if ((target = prepareTarget()) == null)
		{
			setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			_checkAggroTimestamp = System.currentTimeMillis() + Rnd.get(2000, 5000);
			return false;
		}
		
		if(defaultSubFightTask(target))
			return true;
		
		if (actor.getServitors().length > 0){
			for (Servitor summon: actor.getServitors())
			{
				summon.getAI().Attack(target, true, false);
			}
		}
		
		double distance = actor.getDistance(target);
		double targetHp = target.getCurrentHpPercents();
		double actorHp = actor.getCurrentHpPercents();
		
		Skill[] dam = Rnd.chance(getRateDAM()) ? selectUsableSkills(target, distance, _damSkills) : null;
		Skill[] dot = Rnd.chance(getRateDOT()) ? selectUsableSkills(target, distance, _dotSkills) : null;
		Skill[] debuff = targetHp > 10 ? Rnd.chance(getRateDEBUFF()) ? selectUsableSkills(target, distance, _debuffSkills) : null : null;
		Skill[] stun = Rnd.chance(getRateSTUN()) ? selectUsableSkills(target, distance, _stunSkills) : null;
		Skill[] heal = actorHp < 50 ? Rnd.chance(getRateHEAL()) ? selectUsableSkills(actor, 0, _healSkills) : null : null;
		Skill[] buff = Rnd.chance(getRateBUFF()) ? selectUsableSkills(actor, 0, _buffSkills) : null;
		
		RndSelector<Skill[]> rnd = new RndSelector<Skill[]>();
		if (!actor.isAMuted())
		{
			rnd.add(null, getRatePHYS());
		}
		
		if(dam != null && dam.length > 0)
			rnd.add(dam, getRateDAM());
		
		if(dot != null && dot.length > 0)
		rnd.add(dot, getRateDOT());
		
		if(debuff != null && debuff.length > 0)
		rnd.add(debuff, getRateDEBUFF());
		
		if(heal != null && heal.length > 0)
		rnd.add(heal, getRateHEAL());
		
		if(buff != null && buff.length > 0)
		rnd.add(buff, getRateBUFF());
		
		if(stun != null && stun.length > 0)
		rnd.add(stun, getRateSTUN());
		
		Skill[] selected = rnd.select();
		if (selected != null)
		{
			if ((selected == dam) || (selected == dot))
			{
				return chooseTaskAndTargets(selectTopSkillByDamage(actor, target, distance, selected), target, distance);
			}
			
			if ((selected == debuff) || (selected == stun))
			{
				return chooseTaskAndTargets(selectTopSkillByDebuff(actor, target, distance, selected), target, distance);
			}
			
			if (selected == buff)
			{
				return chooseTaskAndTargets(selectTopSkillByBuff(actor, selected), actor, distance);
			}
			
			if (selected == heal)
			{
				return chooseTaskAndTargets(selectTopSkillByHeal(actor, selected), actor, distance);
			}
		}
		
		return chooseTaskAndTargets(null, target, distance);
	}
	
	public int getRatePHYS()
	{
		return 100;
	}
	
	public int getRateDOT()
	{
		return 0 ;
	}
	
	public int getRateDEBUFF()
	{
		return 0;
	}
	
	public int getRateDAM()
	{
		return 0;
	}
	
	public int getRateSTUN()
	{
		return 0;
	}
	
	public int getRateBUFF()
	{
		return 0;
	}
	
	public int getRateHEAL()
	{
		return 0;
	}
	
	public boolean getIsMobile()
	{
		return true;
	}
	
	public int getMaxPathfindFails()
	{
		return 3;
	}
	
	/**
	 * Задержка, перед переключением в активный режим после атаки, если цель не найдена (вне зоны досягаемости, убита, очищен хейт)
	 * @return
	 */
	public int getMaxAttackTimeout()
	{
		return 10000;
	}
	
	/**
	 * Задержка, перед телепортом к цели, если не удается дойти
	 * @return
	 */
	public int getTeleportTimeout()
	{
		return 10000;
	}
	
	public FPCInfo getFPCInfo() {
		return FPCInfo.getInstance(getActor());
	}

	public FPCParty getFPCParty() {
		return getFPCInfo().getParty();
	}

	public void runAwayFromTarget(Creature attacker)
	{
		Player actor = getActor();
		if(actor.isDead() || attacker == null || actor.getDistance(attacker) > 700)
			return;

		if(actor.isMoving)
			return;
		

		int posX = actor.getX();
		int posY = actor.getY();
		int posZ = actor.getZ();

		int old_posX = posX;
		int old_posY = posY;
		int old_posZ = posZ;

		int signx = posX < attacker.getX() ? -1 : 1;
		int signy = posY < attacker.getY() ? -1 : 1;

		// int range = (int) ((actor.calculateAttackSpeed()  /1000 * actor.getWalkSpeed() )* 0.71); // was "actor.getPhysicalAttackRange()"    0.71 = sqrt(2) / 2

		int range = 300;
		
		debug("Ranger move "+range);

		posX += signx * range;
		posY += signy * range;
		posZ = GeoEngine.getHeight(posX, posY, posZ, actor.getGeoIndex());

		if(GeoEngine.canMoveToCoord(old_posX, old_posY, old_posZ, posX, posY, posZ, actor.getGeoIndex()))
		{
			addTaskMove(posX, posY, posZ, false);
			addTaskAttack(attacker);
		}
	}
	
	public void clearAggroList()
	{
		_aggroList.clear();
	}
	
	public boolean tryMoveLongAwayToLocation(Location loc)
	{
		if(loc == null)
			return false;
		
		int weight = 100;
		
		Player player = getActor();
		
		Location myRestartLocation = player.getLoc();
		
		if(!player.isInPeaceZone())
		{
			myRestartLocation = TeleportUtils.getRestartLocation(player, RestartType.TO_VILLAGE);
			addTaskTele(myRestartLocation, weight--);
			addTaskSleep(3*1000, weight--);
		}
		
		if(player.getLevel() < 85)
		{
			NpcInstance buffer = NpcHelper.getClosestBuffer(myRestartLocation);
			if(myRestartLocation.distance(buffer.getLoc()) < 4000)
			{
				addTaskMove(Location.findAroundPosition(buffer, 150), true, true, weight--);
				addTaskSleep(5*1000, weight--);
			}
		}
		
		NpcInstance gk = NpcHelper.getClosestGatekeeper(myRestartLocation);
		addTaskMove(Location.findAroundPosition(gk, 150), true, true, weight--);
		addTaskSleep(5*1000, weight--);
		
		Location middleRestartLocation = TeleportUtils.getRestartLocation(player, loc, RestartType.TO_VILLAGE);
		NpcInstance middleGK = NpcHelper.getClosestGatekeeper(middleRestartLocation);
		
		if(!middleGK.equals(gk))
		{
			gk = middleGK;
			addTaskTele(Location.findAroundPosition(gk, 150), weight--);
			addTaskSleep(5*1000, weight--);
		}

		addTaskTele(loc, weight--);
		addTaskSleep(3*1000, weight--);
		
		return true;
	}
	
}