package l2s.gameserver.model;

import static l2s.gameserver.ai.CtrlIntention.AI_INTENTION_ACTIVE;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import l2s.commons.collections.LazyArrayList;
import l2s.commons.lang.reference.HardReference;
import l2s.commons.lang.reference.HardReferences;
import l2s.commons.listener.Listener;
import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.commons.util.concurrent.atomic.AtomicState;
import l2s.gameserver.Config;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.CharacterAI;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.PlayableAI.AINextAction;
import l2s.gameserver.data.xml.holder.NpcHolder;
import l2s.gameserver.data.xml.holder.TransformTemplateHolder;
import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.geodata.GeoMove;
import l2s.gameserver.instancemanager.DimensionalRiftManager;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.instancemanager.WorldStatisticsManager;
import l2s.gameserver.model.GameObjectTasks.CastEndTimeTask;
import l2s.gameserver.model.GameObjectTasks.HitTask;
import l2s.gameserver.model.GameObjectTasks.MagicLaunchedTask;
import l2s.gameserver.model.GameObjectTasks.MagicUseTask;
import l2s.gameserver.model.GameObjectTasks.NotifyAITask;
import l2s.gameserver.model.Skill.SkillTargetType;
import l2s.gameserver.model.Skill.SkillType;
import l2s.gameserver.model.Zone.ZoneType;
import l2s.gameserver.model.actor.basestats.CreatureBaseStats;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.model.actor.instances.creature.EffectList;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.actor.recorder.CharStatsChangeRecorder;
import l2s.gameserver.model.base.BaseStats;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.InvisibleType;
import l2s.gameserver.model.base.Sex;
import l2s.gameserver.model.base.TeamType;
import l2s.gameserver.model.base.TransformType;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.entity.events.GlobalEvent;
import l2s.gameserver.model.instances.MinionInstance;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.model.quest.QuestEventType;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.model.reference.L2Reference;
import l2s.gameserver.model.worldstatistics.CategoryType;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.components.IStaticPacket;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ActionFailPacket;
import l2s.gameserver.network.l2.s2c.AttackPacket;
import l2s.gameserver.network.l2.s2c.AutoAttackStartPacket;
import l2s.gameserver.network.l2.s2c.AutoAttackStopPacket;
import l2s.gameserver.network.l2.s2c.ChangeMoveTypePacket;
import l2s.gameserver.network.l2.s2c.MTLPacket;
import l2s.gameserver.network.l2.s2c.ExAbnormalStatusUpdateFromTargetPacket;
import l2s.gameserver.network.l2.s2c.ExRotation;
import l2s.gameserver.network.l2.s2c.ExTeleportToLocationActivate;
import l2s.gameserver.network.l2.s2c.FlyToLocationPacket;
import l2s.gameserver.network.l2.s2c.L2GameServerPacket;
import l2s.gameserver.network.l2.s2c.MagicSkillCanceled;
import l2s.gameserver.network.l2.s2c.MagicSkillLaunchedPacket;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
import l2s.gameserver.network.l2.s2c.MyTargetSelectedPacket;
import l2s.gameserver.network.l2.s2c.NpcInfoState;
import l2s.gameserver.network.l2.s2c.SetupGaugePacket;
import l2s.gameserver.network.l2.s2c.StatusUpdatePacket;
import l2s.gameserver.network.l2.s2c.StopMovePacket;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.network.l2.s2c.TeleportToLocationPacket;
import l2s.gameserver.network.l2.s2c.ValidateLocationPacket;
import l2s.gameserver.skills.AbnormalEffect;
import l2s.gameserver.skills.AbnormalType;
import l2s.gameserver.skills.EffectType;
import l2s.gameserver.skills.TimeStamp;
import l2s.gameserver.skills.combo.SkillComboType;
import l2s.gameserver.stats.Calculator;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.Formulas;
import l2s.gameserver.stats.Formulas.AttackInfo;
import l2s.gameserver.stats.StatFunctions;
import l2s.gameserver.stats.StatTemplate;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.stats.funcs.Func;
import l2s.gameserver.stats.triggers.RunnableTrigger;
import l2s.gameserver.stats.triggers.TriggerInfo;
import l2s.gameserver.stats.triggers.TriggerType;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.taskmanager.LazyPrecisionTaskManager;
import l2s.gameserver.taskmanager.RegenTaskManager;
import l2s.gameserver.templates.CreatureTemplate;
import l2s.gameserver.templates.item.WeaponTemplate;
import l2s.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.templates.player.transform.TransformTemplate;
import l2s.gameserver.utils.EffectsComparator;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.Log;
import l2s.gameserver.utils.PositionUtils;

import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CHashIntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Creature extends GameObject
{
	public class AbortCastDelayed extends RunnableImpl
	{
		private Creature _cha;
		
		public AbortCastDelayed (Creature cha)
		{
			_cha = cha;
		}
		@Override
		public void runImpl() throws Exception
		{
			if(_cha == null)
				return;
			_cha.abortCast(true, true);	
		}	
	}
	public class MoveNextTask extends RunnableImpl
	{
		private double alldist, donedist;

		public MoveNextTask setDist(double dist)
		{
			alldist = dist;
			donedist = 0.;
			return this;
		}

		@Override
		public void runImpl() throws Exception
		{
			if(!isMoving)
				return;

			moveLock.lock();
			try
			{
				if(!isMoving)
					return;

				if(isMovementDisabled())
				{
					stopMove();
					return;
				}

				Creature follow = null;
				int speed = getMoveSpeed();
				if(speed <= 0)
				{
					stopMove();
					return;
				}
				long now = System.currentTimeMillis();

				if(isFollow)
				{
					follow = getFollowTarget();
					if(follow == null)
					{
						stopMove();
						return;
					}
					if(isInRangeZ(follow, _offset) && GeoEngine.canSeeTarget(Creature.this, follow, false))
					{
						stopMove();
						ThreadPoolManager.getInstance().execute(new NotifyAITask(Creature.this, CtrlEvent.EVT_ARRIVED_TARGET));
						return;
					}
				}

				if(alldist <= 0)
				{
					moveNext(false);
					return;
				}

				double nowdist = (now - _startMoveTime) * _previousSpeed / 1000.;
				donedist += nowdist;
				double done = donedist / alldist;

				if(done < 0)
					done = 0;
				if(done >= 1)
				{
					moveNext(false);
					return;
				}

				if(isMovementDisabled())
				{
					stopMove();
					return;
				}

				Location loc = null;

				int index = (int) (moveList.size() * done);
				if(index >= moveList.size())
					index = moveList.size() - 1;
				if(index < 0)
					index = 0;

				loc = moveList.get(index).clone().geo2world();

				if(!isFlying() && !isInBoat() && !isInWater() && !isBoat())
					if(loc.z - getZ() > 256)
					{
						String bug_text = "geo bug 1 at: " + getLoc() + " => " + loc.x + "," + loc.y + "," + loc.z + "\tAll path: " + moveList.get(0) + " => " + moveList.get(moveList.size() - 1);
						Log.add(bug_text, "geo");
						stopMove();
						return;
					}

				// Проверяем, на всякий случай
				if(loc == null || isMovementDisabled())
				{
					stopMove();
					return;
				}

				setLoc(loc, true);

				// В процессе изменения координат, мы остановились
				if(isMovementDisabled())
				{
					stopMove();
					return;
				}

				if(isFollow && now - _followTimestamp > (_forestalling ? 500 : 1000) && follow != null && !follow.isInRange(movingDestTempPos, Math.max(100, _offset)))
				{
					if(Math.abs(getZ() - loc.z) > 1000 && !isFlying())
					{
						sendPacket(SystemMsg.CANNOT_SEE_TARGET);
						stopMove();
						return;
					}
					if(buildPathTo(follow.getX(), follow.getY(), follow.getZ(), _offset, follow, true, true))
						movingDestTempPos.set(follow.getX(), follow.getY(), follow.getZ());
					else
					{
						stopMove();
						return;
					}
					moveNext(true);
					return;
				}

				double doneTemp = (donedist + nowdist) / alldist;
				if(doneTemp >= 1)
				{
					moveNext(false);
					return;
				}

				_previousSpeed = speed;
				_startMoveTime = now;
				_moveTask = ThreadPoolManager.getInstance().schedule(this, getMoveTickInterval());
			}
			catch(Exception e)
			{
				_log.error("", e);
			}
			finally
			{
				moveLock.unlock();
			}
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(Creature.class);

	public static final double HEADINGS_IN_PI = 10430.378350470452724949566316381;
	public static final int INTERACTION_DISTANCE = 200;

	private Skill _castingSkill;
	private Skill _dualCastingSkill;

	private long _castInterruptTime;
	private long _dualCastInterruptTime;
	private long _animationEndTime;
	private long _dualAnimationEndTime;

	public int _scheduledCastCount;
	public int _scheduledDualCastCount;
	public int _scheduledCastInterval;
	public int _scheduledDualCastInterval;

	public Future<?> _skillTask;
	public Future<?> _skillLaunchedTask;
	public Future<?> _skillDualTask;
	public Future<?> _skillDualLaunchedTask;

	private Future<?> _stanceTask;
	private Runnable _stanceTaskRunnable;
	private long _stanceEndTime;

	public final static int CLIENT_BAR_SIZE = 352; // 352 - размер полоски CP/HP/MP в клиенте, в пикселях

	private int _lastCpBarUpdate = -1;
	private int _lastHpBarUpdate = -1;
	private int _lastMpBarUpdate = -1;

	protected double _currentCp = 0;
	protected double _currentHp = 1;
	protected double _currentMp = 1;

	protected boolean _isAttackAborted;
	protected long _attackEndTime;
	protected long _attackReuseEndTime;
	private int _poleAttackCount = 0;
	private static final double[] POLE_VAMPIRIC_MOD = { 1, 0.9, 0, 7, 0.2, 0.01 };

	/** HashMap(Integer, L2Skill) containing all skills of the L2Character */
	protected final Map<Integer, Skill> _skills = new ConcurrentSkipListMap<Integer, Skill>();
	protected Map<TriggerType, Set<TriggerInfo>> _triggers;

	protected IntObjectMap<TimeStamp> _skillReuses = new CHashIntObjectMap<TimeStamp>();

	protected volatile EffectList _effectList;

	protected volatile CharStatsChangeRecorder<? extends Creature> _statsRecorder;

	/** Map 32 bits (0x00000000) containing all abnormal effect in progress */
	private Set<AbnormalEffect> _abnormalEffects = new CopyOnWriteArraySet<AbnormalEffect>();

	protected AtomicBoolean isDead = new AtomicBoolean();
	protected AtomicBoolean isTeleporting = new AtomicBoolean();

	private Map<Integer, Integer> _skillMastery;

	protected boolean _isInvul;

	private boolean _fakeDeath;
	private boolean _isBlessedByNoblesse; // Восстанавливает все бафы после смерти
	private boolean _isSalvation; // Восстанавливает все бафы после смерти и полностью CP, MP, HP

	private boolean _meditated;
	private boolean _lockedTarget;

	private boolean _blocked;

	private AtomicState _afraid = new AtomicState();
	private AtomicState _muted = new AtomicState();
	private AtomicState _pmuted = new AtomicState();
	private AtomicState _amuted = new AtomicState();
	private AtomicState _paralyzed = new AtomicState();
	private AtomicState _rooted = new AtomicState();
	private AtomicState _sleeping = new AtomicState();
	private AtomicState _stunned = new AtomicState();
	private AtomicState _immobilized = new AtomicState();
	private AtomicState _confused = new AtomicState();
	private AtomicState _frozen = new AtomicState();

	private AtomicState _knockDowned = new AtomicState();
	private AtomicState _knockBacked = new AtomicState();
	private AtomicState _flyUp = new AtomicState();

	private AtomicState _healBlocked = new AtomicState();
	private AtomicState _damageBlocked = new AtomicState();
	private AtomicState _buffImmunity = new AtomicState(); // Иммунитет к бафам
	private AtomicState _debuffImmunity = new AtomicState(); // Иммунитет к дебафам
	private AtomicState _effectImmunity = new AtomicState(); // Иммунитет ко всем эффектам
	protected AtomicState _deathImmunity = new AtomicState();

	private AtomicState _weaponEquipBlocked = new AtomicState();

	private boolean _flying;

	private boolean _running;

	public boolean isMoving;
	public boolean isFollow;
	private final Lock moveLock = new ReentrantLock();
	private Future<?> _moveTask;
	private MoveNextTask _moveTaskRunnable;
	private List<Location> moveList;
	private Location destination;
	/**
	 * при moveToLocation используется для хранения геокоординат в которые мы двигаемся для того что бы избежать повторного построения одного и того же пути
	 * при followToCharacter используется для хранения мировых координат в которых находилась последний раз преследуемая цель для отслеживания необходимости перестраивания пути
	 */
	private final Location movingDestTempPos = new Location();
	private int _offset;

	private boolean _forestalling;

	private volatile HardReference<? extends GameObject> target = HardReferences.emptyRef();
	private volatile HardReference<? extends Creature> castingTarget = HardReferences.emptyRef();
	private volatile HardReference<? extends Creature> dualCastingTarget = HardReferences.emptyRef();
	private volatile HardReference<? extends Creature> followTarget = HardReferences.emptyRef();
	private volatile HardReference<? extends Creature> _aggressionTarget = HardReferences.emptyRef();

	private final List<List<Location>> _targetRecorder = new ArrayList<List<Location>>();
	private long _followTimestamp, _startMoveTime;
	private int _previousSpeed = 0;
	private int _rndCharges = 0;

	private int _heading;

	private final Calculator[] _calculators;

	private CreatureTemplate _template;

	protected volatile CharacterAI _ai;

	protected String _name;
	protected String _title;
	protected TeamType _team = TeamType.NONE;

	private boolean _isRegenerating;
	private final Lock regenLock = new ReentrantLock();
	private Future<?> _regenTask;
	private Runnable _regenTaskRunnable;

	private List<Zone> _zones = new LazyArrayList<Zone>();
	/** Блокировка для чтения/записи объектов из региона */
	private final ReadWriteLock zonesLock = new ReentrantReadWriteLock();
	private final Lock zonesRead = zonesLock.readLock();
	private final Lock zonesWrite = zonesLock.writeLock();

	protected volatile CharListenerList listeners;

	private final Lock statusListenersLock = new ReentrantLock();

	protected Long _storedId;

	public final Long getStoredId()
	{
		return _storedId;
	}

	protected HardReference<? extends Creature> reference;

	private boolean _isInTransformUpdate = false;
	private TransformTemplate _visualTransform = null;

	private boolean _isDualCastEnable = false;

	private boolean _isTargetable = true;

	protected CreatureBaseStats _baseStats = null;

	public Creature(int objectId, CreatureTemplate template)
	{
		super(objectId);

		_template = template;

		_calculators = new Calculator[Stats.NUM_STATS];

		StatFunctions.addPredefinedFuncs(this);

		reference = new L2Reference<Creature>(this);

		_storedId = GameObjectsStorage.put(this);
	}

	@Override
	public HardReference<? extends Creature> getRef()
	{
		return reference;
	}

	public boolean isAttackAborted()
	{
		return _isAttackAborted;
	}

	public final void abortAttack(boolean force, boolean message)
	{
		if(isAttackingNow())
		{
			_attackEndTime = 0;
			if(force)
				_isAttackAborted = true;

			getAI().setIntention(AI_INTENTION_ACTIVE);

			if(isPlayer() && message)
			{
				sendActionFailed();
				sendPacket(new SystemMessage(SystemMessage.C1S_ATTACK_FAILED).addName(this));
			}
		}
	}

	public final void abortCast(boolean force, boolean message)
	{
		boolean cancelled = false;

		if(isCastingNow() && (force || canAbortCast()))
		{
			final Skill castingSkill = _castingSkill;
			if(castingSkill != null && castingSkill.isAbortable())
			{
				final Future<?> skillTask = _skillTask;
				final Future<?> skillLaunchedTask = _skillLaunchedTask;

				finishFly(false); // Броадкаст пакета FlyToLoc уже выполнен, устанавливаем координаты чтобы не было визуальных глюков
				clearCastVars(false);

				if(skillTask != null)
					skillTask.cancel(false); // cancels the skill hit scheduled task

				if(skillLaunchedTask != null)
					skillLaunchedTask.cancel(false); // cancels the skill hit scheduled task
				
				if(castingSkill != null)
				{
					if(castingSkill.isUsingWhileCasting())
					{
						Creature target = getCastingTarget();
						if(target != null)
							target.getEffectList().stopEffects(castingSkill);
					}

					removeSkillMastery(castingSkill.getId());
				}

				cancelled = true;
			}
		}

		if(isDualCastingNow() && (force || canAbortDualCast()))
		{
			final Skill dualCastingSkill = _dualCastingSkill;
			if(dualCastingSkill != null && dualCastingSkill.isAbortable())
			{
				final Future<?> skillTask = _skillDualTask;
				final Future<?> skillLaunchedTask = _skillDualLaunchedTask;

				finishFly(true); // Броадкаст пакета FlyToLoc уже выполнен, устанавливаем координаты чтобы не было визуальных глюков
				clearCastVars(true);

				if(skillTask != null)
					skillTask.cancel(false); // cancels the skill hit scheduled task

				if(skillLaunchedTask != null)
					skillLaunchedTask.cancel(false); // cancels the skill hit scheduled task
				
				if(dualCastingSkill != null)
				{
					if(dualCastingSkill.isUsingWhileCasting())
					{
						Creature target = getDualCastingTarget();
						if(target != null)
							target.getEffectList().stopEffects(dualCastingSkill);
					}

					removeSkillMastery(dualCastingSkill.getId());
				}

				cancelled = true;
			}
		}

		if(cancelled)
		{
			broadcastPacket(new MagicSkillCanceled(getObjectId())); // broadcast packet to stop animations client-side

			getAI().setIntention(AI_INTENTION_ACTIVE);

			if(isPlayer() && message)
				sendPacket(SystemMsg.YOUR_CASTING_HAS_BEEN_INTERRUPTED);
		}
	}

	private final boolean canAbortCast()
	{
		return _castInterruptTime > System.currentTimeMillis();
	}

	private final boolean canAbortDualCast()
	{
		return _dualCastInterruptTime > System.currentTimeMillis();
	}

	private double reflectDamage(Creature attacker, Skill skill, double damage)
	{
		if(isDead())
			return 0.;

		if(damage <= 0)
			return 0.;

		final boolean bow = attacker.getBaseStats().getAttackType() == WeaponType.BOW || attacker.getBaseStats().getAttackType() == WeaponType.CROSSBOW;
		final double resistReflect = attacker.calcStat(Stats.RESIST_REFLECT_DAM, 0, null, null); 

		double value = 0.;
		if(skill != null && skill.isMagic() && attacker.checkRange(attacker, this))
			value = calcStat(Stats.REFLECT_AND_BLOCK_MSKILL_DAMAGE_CHANCE, 0, attacker, skill) - resistReflect;
		else if(skill != null && skill.isPhysic() && attacker.checkRange(attacker, this))
			value = calcStat(Stats.REFLECT_AND_BLOCK_PSKILL_DAMAGE_CHANCE, 0, attacker, skill) - resistReflect;
		else if(skill == null && !bow && attacker.checkRange(attacker, this))
			value = calcStat(Stats.REFLECT_AND_BLOCK_DAMAGE_CHANCE, 0, attacker, null) - resistReflect;

		//Цель отразила весь урон
		if(value > 0 && Rnd.chance(value))
			return damage;

		value = 0.;
		if(skill != null && skill.isMagic() && attacker.checkRange(attacker, this))
			value = calcStat(Stats.REFLECT_MSKILL_DAMAGE_PERCENT, 0, attacker, skill) - resistReflect;
		else if(skill != null && skill.isPhysic() && attacker.checkRange(attacker, this))
			value = calcStat(Stats.REFLECT_PSKILL_DAMAGE_PERCENT, 0, attacker, skill) - resistReflect;
		else if(skill == null && !bow && attacker.checkRange(attacker, this))
			value = calcStat(Stats.REFLECT_DAMAGE_PERCENT, 0, attacker, null) - resistReflect;
		else if(skill == null && bow && attacker.checkRange(attacker, this))
			value = calcStat(Stats.REFLECT_BOW_DAMAGE_PERCENT, 0, attacker, null) - resistReflect;

		if(value > 0)
		{
			if(getCurrentHp() + getCurrentCp() > damage) // Цель в состоянии отразить часть урона
				return value / 100. * damage;
		}
		return 0.;
	}

	private void absorbDamage(Creature target, Skill skill, double damage)
	{
		if(target.isDead())
			return;

		if(damage <= 0)
			return;

		final boolean bow = getBaseStats().getAttackType() == WeaponType.BOW || getBaseStats().getAttackType() == WeaponType.CROSSBOW;

		// вампирик
		//damage = (int) (damage - target.getCurrentCp() - target.getCurrentHp()); WTF?

		double absorb = 0;
		if(skill != null)
		{
			if(skill.isMagic())
				absorb = calcStat(Stats.ABSORB_MSKILL_DAMAGE_PERCENT, 0, this, skill);
			else
				absorb = calcStat(Stats.ABSORB_PSKILL_DAMAGE_PERCENT, 0, this, skill);
		}
		else if(skill == null && !bow)
			absorb = calcStat(Stats.ABSORB_DAMAGE_PERCENT, 0, this, null);
		else if(skill == null && bow)
			absorb = calcStat(Stats.ABSORB_BOW_DAMAGE_PERCENT, 0, this, null);

		final double poleMod = POLE_VAMPIRIC_MOD[Math.max(0, Math.min(_poleAttackCount, POLE_VAMPIRIC_MOD.length - 1))];

		absorb = poleMod * absorb;

		double limit;
		if(absorb > 0 && !target.isDamageBlocked() && Rnd.chance(Config.ALT_VAMPIRIC_CHANCE) && !target.isServitor() && !target.isSummon() && !target.isInvul() && !target.isDeathImmune())
		{
			limit = calcStat(Stats.HP_LIMIT, null, null) * getMaxHp() / 100.;
			if(getCurrentHp() < limit)
				setCurrentHp(Math.min(_currentHp + (damage * absorb / 100.), limit), false);
		}

		absorb = poleMod * calcStat(Stats.ABSORB_DAMAGEMP_PERCENT, 0, target, null);
		if(absorb > 0 && !target.isDamageBlocked() && !target.isServitor() && !target.isSummon() && !target.isInvul() && !target.isDeathImmune())
		{
			limit = calcStat(Stats.MP_LIMIT, null, null) * getMaxMp() / 100.;
			if(getCurrentMp() < limit)
				setCurrentMp(Math.min(_currentMp + damage * absorb / 100., limit));
		}
	}

	public double absorbToEffector(Creature attacker, double damage)
	{
		double transferToEffectorDam = calcStat(Stats.TRANSFER_TO_EFFECTOR_DAMAGE_PERCENT, 0.);
		if(transferToEffectorDam > 0)
		{
			Collection<Effect> effects = getEffectList().getEffects();
			if(effects.isEmpty())
				return damage;

			for(Effect effect : effects)
			{
				if(effect.getEffectType() != EffectType.AbsorbDamageToEffector)
					continue;

				Creature effector = effect.getEffector();
				// на мертвого чара, не онлайн игрока - не даем абсорб, и не на самого себя
				if(effector == this || effector.isDead() || !isInRange(effector, 1200))
					return damage;

				Player thisPlayer = getPlayer();
				Player effectorPlayer = effector.getPlayer();
				if(thisPlayer != null && effectorPlayer != null)
				{
					if(thisPlayer != effectorPlayer && (!thisPlayer.isOnline() || !thisPlayer.isInParty() || thisPlayer.getParty() != effectorPlayer.getParty()))
						return damage;
				}
				else
					return damage;

				double transferDamage = (damage * transferToEffectorDam) * .01;
				damage -= transferDamage;

				effector.reduceCurrentHp(transferDamage, effector, null, false, false, !attacker.isPlayable(), false, true, false, true);
			}
		}
		return damage;
	}

	public double absorbToMp(Creature attacker, double damage)
	{
		double transferToMpDamPercent = calcStat(Stats.TRANSFER_TO_MP_DAMAGE_PERCENT, 0.);
		if(transferToMpDamPercent > 0)
		{
			double transferDamage = (damage * transferToMpDamPercent) * .01;

			double currentMp = getCurrentMp();
			if(currentMp > transferDamage)
			{
				setCurrentMp(getCurrentMp() - transferDamage);
				return 0;
			}
			else
			{
				if(currentMp > 0)
				{
					damage -= currentMp;
					setCurrentMp(0);
					sendPacket(SystemMsg.MP_BECAME_0_AND_THE_ARCANE_SHIELD_IS_DISAPPEARING);
				}
				getEffectList().stopEffects(EffectType.AbsorbDamageToMp);
			}

			return damage;
		}
		return damage;
	}

	public Servitor getServitorForTransfereDamage(double damage)
	{
		return null;
	}

	public double getDamageForTransferToServitor(double damage)
	{
		return 0.;
	}

	public Skill addSkill(Skill newSkill)
	{
		if(newSkill == null)
			return null;

		Skill oldSkill = _skills.get(newSkill.getId());
		if(newSkill.equals(oldSkill))
			return oldSkill;

		if(isDisabledAnalogSkill(newSkill.getId()))
			return null;

		// Replace oldSkill by newSkill or Add the newSkill
		_skills.put(newSkill.getId(), newSkill);

		disableAnalogSkills(newSkill);

		if(oldSkill != null)
		{
			removeStatsOwner(oldSkill);
			removeTriggers(oldSkill);
		}

		addTriggers(newSkill);

		// Add Func objects of newSkill to the calculator set of the L2Character
		addStatFuncs(newSkill.getStatFuncs());

		return oldSkill;
	}

	public Calculator[] getCalculators()
	{
		return _calculators;
	}

	public final void addStatFunc(Func f)
	{
		if(f == null)
			return;
		int stat = f.stat.ordinal();
		synchronized (_calculators)
		{
			if(_calculators[stat] == null)
				_calculators[stat] = new Calculator(f.stat, this);
			_calculators[stat].addFunc(f);
		}
	}

	public final void addStatFuncs(Func[] funcs)
	{
		for(Func f : funcs)
			addStatFunc(f);
	}

	public final void removeStatFunc(Func f)
	{
		if(f == null)
			return;
		int stat = f.stat.ordinal();
		synchronized (_calculators)
		{
			if(_calculators[stat] != null)
				_calculators[stat].removeFunc(f);
		}
	}

	public final void removeStatFuncs(Func[] funcs)
	{
		for(Func f : funcs)
			removeStatFunc(f);
	}

	public final void removeStatsOwner(Object owner)
	{
		synchronized (_calculators)
		{
			for(int i = 0; i < _calculators.length; i++)
				if(_calculators[i] != null)
					_calculators[i].removeOwner(owner);
		}
	}

	public void altOnMagicUse(Creature aimingTarget, Skill skill)
	{
		if(isAlikeDead() || skill == null)
			return;
		int magicId = skill.getDisplayId();
		int level = Math.max(1, getSkillDisplayLevel(skill.getId()));
		List<Creature> targets = skill.getTargets(this, aimingTarget, true);
		broadcastPacket(new MagicSkillLaunchedPacket(getObjectId(), magicId, level, targets));
		double mpConsume2 = skill.getMpConsume2();
		if(mpConsume2 > 0)
		{
			double mpConsume2WithStats;
			if(skill.isMagic())
				mpConsume2WithStats = calcStat(Stats.MP_MAGIC_SKILL_CONSUME, mpConsume2, aimingTarget, skill);
			else
				mpConsume2WithStats = calcStat(Stats.MP_PHYSICAL_SKILL_CONSUME, mpConsume2, aimingTarget, skill);

			if(_currentMp < mpConsume2WithStats)
			{
				sendPacket(SystemMsg.NOT_ENOUGH_MP);
				return;
			}
			reduceCurrentMp(mpConsume2WithStats, null);
		}
		callSkill(skill, targets, false);
	}

	public void altUseSkill(Skill skill, Creature target)
	{
		if(skill == null)
			return;
		int magicId = skill.getId();
		if(isUnActiveSkill(magicId))
			return;
		if(isSkillDisabled(skill))
		{
			sendReuseMessage(skill);
			return;
		}
		if(target == null)
		{
			target = skill.getAimingTarget(this, getTarget());
			if(target == null)
				return;
		}

		getListeners().onMagicUse(skill, target, true);

		if(!skill.isHandler())
		{
			int itemConsume[] = skill.getItemConsume();
			if(itemConsume[0] > 0)
			{
				for(int i = 0; i < itemConsume.length; i++)
				{
					if(!consumeItem(skill.getItemConsumeId()[i], itemConsume[i]))
					{
						sendPacket(SystemMsg.THERE_ARE_NOT_ENOUGH_NECESSARY_ITEMS_TO_USE_THE_SKILL);
						return;
					}
				}
			}
		}

		if(skill.getReferenceItemId() > 0)
		{
			if(!consumeItemMp(skill.getReferenceItemId(), skill.getReferenceItemMpConsume()))
				return;
		}

		if(skill.getSoulsConsume() > getConsumedSouls())
		{
			sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SOULS);
			return;
		}

		if(skill.getEnergyConsume() > getAgathionEnergy())
		{
			sendPacket(SystemMsg.THE_SKILL_HAS_BEEN_CANCELED_BECAUSE_YOU_HAVE_INSUFFICIENT_ENERGY);
			return;
		}

		if(skill.getSoulsConsume() > 0)
			setConsumedSouls(getConsumedSouls() - skill.getSoulsConsume(), null);
		if(skill.getEnergyConsume() > 0)
			setAgathionEnergy(getAgathionEnergy() - skill.getEnergyConsume());

		int level = Math.max(1, getSkillDisplayLevel(magicId));
		Formulas.calcSkillMastery(skill, this);
		long reuseDelay = Formulas.calcSkillReuseDelay(this, skill);

		MagicSkillUse msu = new MagicSkillUse(this, target, skill.getDisplayId(), level, skill.getHitTime(), reuseDelay);
		msu.setReuseSkillId(skill.getReuseSkillId());
		broadcastPacket(msu);

		// Не показывать сообщение для хербов и кубиков
		if(!skill.isHideUseMessage())
		{
			if(skill.getSkillType() == SkillType.PET_SUMMON)
				sendPacket(new SystemMessage(SystemMessage.SUMMON_A_PET));
			else if(!skill.isHandler())
				sendPacket(new SystemMessagePacket(SystemMsg.YOU_USE_S1).addSkillName(magicId, level));
		}

		if(!skill.isHandler())
			disableSkill(skill, reuseDelay);

		altOnMagicUse(target, skill);
	}

	public void sendReuseMessage(Skill skill)
	{}

	public void broadcastPacket(L2GameServerPacket... packets)
	{
		sendPacket(packets);
		broadcastPacketToOthers(packets);
	}

	public void broadcastPacket(List<L2GameServerPacket> packets)
	{
		sendPacket(packets);
		broadcastPacketToOthers(packets);
	}

	public void broadcastPacketToOthers(L2GameServerPacket... packets)
	{
		if(!isVisible() || packets.length == 0)
			return;

		for(Player target : World.getAroundPlayers(this))
			target.sendPacket(packets);
	}

	public void broadcastPacketToOthers(List<L2GameServerPacket> packets)
	{
		broadcastPacketToOthers(packets.toArray(new L2GameServerPacket[packets.size()]));
	}

	public StatusUpdatePacket makeStatusUpdate(int... fields)
	{
		StatusUpdatePacket su = new StatusUpdatePacket(getObjectId());
		for(int field : fields)
			switch(field)
			{
				case StatusUpdatePacket.CUR_HP:
					su.addAttribute(field, (int) getCurrentHp());
					break;
				case StatusUpdatePacket.MAX_HP:
					su.addAttribute(field, getMaxHp());
					break;
				case StatusUpdatePacket.CUR_MP:
					su.addAttribute(field, (int) getCurrentMp());
					break;
				case StatusUpdatePacket.MAX_MP:
					su.addAttribute(field, getMaxMp());
					break;
				case StatusUpdatePacket.KARMA:
					su.addAttribute(field, getKarma());
					break;
				case StatusUpdatePacket.CUR_CP:
					su.addAttribute(field, (int) getCurrentCp());
					break;
				case StatusUpdatePacket.MAX_CP:
					su.addAttribute(field, getMaxCp());
					break;
				case StatusUpdatePacket.PVP_FLAG:
					su.addAttribute(field, getPvpFlag());
					break;
			}
		return su;
	}

	public void broadcastStatusUpdate()
	{
		if(!needStatusUpdate())
			return;

		StatusUpdatePacket su = makeStatusUpdate(StatusUpdatePacket.MAX_HP, StatusUpdatePacket.MAX_MP, StatusUpdatePacket.CUR_HP, StatusUpdatePacket.CUR_MP);
		broadcastPacket(su);

		for(GlobalEvent e : getEvents())
			e.broadcastStatusUpdate(this);
	}

	public int calcHeading(int x_dest, int y_dest)
	{
		return (int) (Math.atan2(getY() - y_dest, getX() - x_dest) * HEADINGS_IN_PI) + 32768;
	}

	public final double calcStat(Stats stat)
	{
		return calcStat(stat, null, null);
	}

	public final double calcStat(Stats stat, double init)
	{
		return calcStat(stat, init, null, null);
	}

	public final double calcStat(Stats stat, double init, Creature target, Skill skill)
	{
		int id = stat.ordinal();
		Calculator c = _calculators[id];
		if(c == null)
			return init;
		Env env = new Env();
		env.character = this;
		env.target = target;
		env.skill = skill;
		env.value = init;
		c.calc(env);
		return env.value;
	}

	public final double calcStat(Stats stat, Creature target, Skill skill)
	{
		Env env = new Env(this, target, skill);
		env.value = stat.getInit();
		int id = stat.ordinal();
		Calculator c = _calculators[id];
		if(c != null)
			c.calc(env);
		return env.value;
	}

	/**
	 * Return the Attack Speed of the L2Character (delay (in milliseconds) before next attack).
	 */
	public int calculateAttackDelay()
	{
		return Formulas.calcPAtkSpd(getPAtkSpd());
	}

	public void callSkill(Skill skill, List<Creature> targets, boolean useActionSkills)
	{
		try
		{
			if(useActionSkills && !skill.isUsingWhileCasting() && _triggers != null)
			{
				if(skill.isOffensive())
				{
					if(skill.isMagic())
						useTriggers(getTarget(), TriggerType.OFFENSIVE_MAGICAL_SKILL_USE, null, skill, 0);
					else
						useTriggers(getTarget(), TriggerType.OFFENSIVE_PHYSICAL_SKILL_USE, null, skill, 0);
				}
				else if(skill.isMagic()) // для АоЕ, пати/клан бафов и селфов триггер накладывается на кастера
				{
					final boolean targetSelf = skill.isAoE() || skill.isNotTargetAoE() || skill.getTargetType() == Skill.SkillTargetType.TARGET_SELF;
					useTriggers(targetSelf ? this : getTarget(), TriggerType.SUPPORT_MAGICAL_SKILL_USE, null, skill, 0);
				}
			}

			Player pl = getPlayer();
			Creature target;
			Iterator<Creature> itr = targets.iterator();
			while(itr.hasNext())
			{
				target = itr.next();

				if(target.isNpc())
				{
					if(skill.isOffensive())
					{
						if(!skill.isAI())
						{
							int damage = skill.getEffectPoint() != 0 ? skill.getEffectPoint() : 1;
							target.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, this, damage);
						}
					}
				}

				//Фильтруем неуязвимые цели
				if(skill.isOffensive() && target.isInvul())
				{
					Player pcTarget = target.getPlayer();
					if((!skill.isIgnoreInvul() || pcTarget != null && pcTarget.isGM()) && !target.isArtefact())
					{
						itr.remove();
						continue;
					}
				}
				//Рассчитываем игрорируемые скилы из спец.эффекта
				for(Effect ie : target.getEffectList().getEffects())
				{
					if(ie.getEffectType() != EffectType.IgnoreSkill)
						continue;

					if(ArrayUtils.contains(ie.getTemplate().getParam().getIntegerArray("skillId"), skill.getId()))
					{
						itr.remove();
						continue;
					}
				}

				target.getListeners().onMagicHit(skill, this);

				if(pl != null)
				{
					if(target != null && target.isNpc())
					{
						NpcInstance npc = (NpcInstance) target;
						List<QuestState> ql = pl.getQuestsForEvent(npc, QuestEventType.MOB_TARGETED_BY_SKILL);
						if(ql != null)
							for(QuestState qs : ql)
								qs.getQuest().notifySkillUse(npc, skill, qs);
					}
				}

				if(!target.isRaid() && skill.getCancelTarget() > 0)
				{
					if(Rnd.chance(skill.getCancelTarget()))
					{
						Skill castingSkill = target.getCastingSkill();
						if(castingSkill == null || !(castingSkill.getSkillType() == SkillType.TAKECASTLE || castingSkill.getSkillType() == SkillType.TAKEFORTRESS))
						{
							target.abortAttack(true, true);
							target.abortCast(true, true);
							target.setTarget(null);
						}

						castingSkill = target.getDualCastingSkill();
						if(castingSkill == null || !(castingSkill.getSkillType() == SkillType.TAKECASTLE || castingSkill.getSkillType() == SkillType.TAKEFORTRESS))
						{
							target.abortAttack(true, true);
							target.abortCast(true, true);
							target.setTarget(null);
						}
					}
				}
			}

			if(skill.isOffensive() && skill.getId() != 10029) //hardcore here till something more normal will be done.
				startAttackStanceTask();

			// Применяем селфэффекты на кастера
			// Особое условие для атакующих аура-скиллов (Vengeance 368):
			// если ни одна цель не задета то селфэффекты не накладываются
			if(!(skill.isNotTargetAoE() && skill.isOffensive() && targets.size() == 0))
				skill.getEffects(this, this, true);

			skill.useSkill(this, targets);
		}
		catch(Exception e)
		{
			_log.error("", e);
		}
	}

	public void useTriggers(GameObject target, TriggerType type, Skill ex, Skill owner, double damage)
	{
		useTriggers(target, type, ex, owner, owner, damage);
	}

	public void useTriggers(GameObject target, TriggerType type, Skill ex, Skill owner, StatTemplate triggersOwner, double damage)
	{
		Set<TriggerInfo> triggers = null;
		switch(type)
		{
			case ON_START_EFFECT:
			case ON_EXIT_EFFECT:
			case ON_SUCCES_FINISH_CAST:
				if(triggersOwner != null)
				{
					triggers = new CopyOnWriteArraySet<TriggerInfo>();
					for(TriggerInfo t : triggersOwner.getTriggerList())
					{
						if(t.getType() == type)
							triggers.add(t);
					}
				}
				break;
			default:
				if(_triggers != null)
					triggers = _triggers.get(type);
				break;
		}

		if(triggers != null && !triggers.isEmpty())
		{
			for(TriggerInfo t : triggers)
			{
				if(t.getSkill() != ex)
					useTriggerSkill(target == null ? getTarget() : target, null, t, owner, damage);
			}
		}
	}

	public void useTriggerSkill(GameObject target, List<Creature> targets, TriggerInfo trigger, Skill owner, double damage)
	{
		Skill skill = trigger.getSkill();
		if(skill == null)
			return;

		/*if(skill.getTargetType() == SkillTargetType.TARGET_SELF && !skill.isTrigger())
			_log.warn("Self trigger skill dont have trigger flag. SKILL ID[" + skill.getId() + "]");*/

		Creature aimTarget = skill.getAimingTarget(this, target);
		if(trigger.isIncreasing())
		{
			int increasedTriggerLvl = 0;
			for(Effect effect : aimTarget.getEffectList().getEffects())
			{
				if(effect.getSkill().getId() != skill.getId())
					continue;

				increasedTriggerLvl = effect.getSkill().getLevel(); //taking the first one only.
				break;
			}

			if(increasedTriggerLvl > 0)
			{
				Skill newSkill = SkillTable.getInstance().getInfo(skill.getId(), increasedTriggerLvl + 1);
				if(newSkill != null)
					skill = newSkill;
				else
					skill = SkillTable.getInstance().getInfo(skill.getId(), increasedTriggerLvl);
			}
		}

		if(skill.getReuseDelay() > 0 && isSkillDisabled(skill))
			return;

		// DS: Для шансовых скиллов с TARGET_SELF и условием "пвп" сам кастер будет являться aimTarget,
		// поэтому в условиях для триггера проверяем реальную цель.
		Creature realTarget = target != null && target.isCreature() ? (Creature) target : null;
		if(Rnd.chance(trigger.getChance()) && trigger.checkCondition(this, realTarget, aimTarget, owner, damage) && skill.checkCondition(this, aimTarget, true, true, true, true))
		{
			if(targets == null)
				targets = skill.getTargets(this, aimTarget, false);

			int displayId = 0, displayLevel = 0;

			if(skill.hasEffects())
			{
				displayId = skill.getEffectTemplates()[0]._displayId;
				displayLevel = skill.getEffectTemplates()[0]._displayLevel;
			}

			if(displayId == 0)
				displayId = skill.getDisplayId();
			if(displayLevel == 0)
				displayLevel = skill.getDisplayLevel();

			if(trigger.getType() != TriggerType.SUPPORT_MAGICAL_SKILL_USE && trigger.getType() != TriggerType.IDLE)
			{
				for(Creature cha : targets)
				{
					broadcastPacket(new MagicSkillUse(this, cha, displayId, displayLevel, 0, 0));
				}
			}

			Formulas.calcSkillMastery(skill, this);
			callSkill(skill, targets, false);
			disableSkill(skill, skill.getReuseDelay());
		}
	}

	private void triggerCancelEffects(TriggerInfo trigger)
	{
		Skill skill = trigger.getSkill();
		if(skill == null)
			return;

		getEffectList().stopEffects(skill);
	}

	public boolean checkReflectSkill(Creature attacker, Skill skill)
	{
		if(!skill.isReflectable())
			return false;
		// Не отражаем, если есть неуязвимость, иначе она может отмениться
		if(isInvul() || attacker.isInvul() || !skill.isOffensive())
			return false;
		// Из магических скилов отражаются только скилы наносящие урон по ХП.
		if(skill.isMagic() && skill.getSkillType() != SkillType.MDAM)
			return false;
		if(Rnd.chance(calcStat(skill.isMagic() ? Stats.REFLECT_MAGIC_SKILL : Stats.REFLECT_PHYSIC_SKILL, 0, attacker, skill)))
		{
			sendPacket(new SystemMessage(SystemMessage.YOU_COUNTERED_C1S_ATTACK).addName(attacker));
			attacker.sendPacket(new SystemMessage(SystemMessage.C1_DODGES_THE_ATTACK).addName(this));
			return true;
		}
		return false;
	}

	public void doCounterAttack(Skill skill, Creature attacker, boolean blow)
	{
		if(isDead()) // если персонаж уже мертв, контратаки быть не должно
			return;
		if(isDamageBlocked() || attacker.isDamageBlocked()) // Не контратакуем, если есть неуязвимость, иначе она может отмениться
			return;
		if(skill == null || skill.hasEffects() || skill.isMagic() || !skill.isOffensive() || skill.getCastRange() > 200)
			return;
		if(Rnd.chance(calcStat(Stats.COUNTER_ATTACK, 0, attacker, skill)))
		{
			double damage = 1189 * getPAtk(attacker) / Math.max(attacker.getPDef(this), 1);
			attacker.sendPacket(new SystemMessagePacket(SystemMsg.C1_IS_PERFORMING_A_COUNTERATTACK).addName(this));
			if(blow) // урон х2 для отражения blow скиллов
			{
				sendPacket(new SystemMessagePacket(SystemMsg.C1_IS_PERFORMING_A_COUNTERATTACK).addName(this));
				sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_DONE_S3_POINTS_OF_DAMAGE_TO_C2).addName(this).addName(attacker).addInteger((int) damage).addHpChange(getObjectId(), attacker.getObjectId(), (int) -damage));
				attacker.reduceCurrentHp(damage, this, skill, true, true, false, false, false, false, true);
			}
			else
				sendPacket(new SystemMessagePacket(SystemMsg.C1_IS_PERFORMING_A_COUNTERATTACK).addName(this));
			sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_DONE_S3_POINTS_OF_DAMAGE_TO_C2).addName(this).addName(attacker).addInteger((int) damage).addHpChange(getObjectId(), attacker.getObjectId(), (int) -damage));
			attacker.reduceCurrentHp(damage, this, skill, true, true, false, false, false, false, true);
		}
	}

	/**
	 * Disable this skill id for the duration of the delay in milliseconds.
	 *
	 * @param skill
	 * @param delay (seconds * 1000)
	 */
	public void disableSkill(Skill skill, long delay)
	{
		_skillReuses.put(skill.getReuseHash(), new TimeStamp(skill, delay));
	}

	public abstract boolean isAutoAttackable(Creature attacker);

	public void doAttack(Creature target)
	{
		if(target == null || isAMuted() || isAttackingNow() || isAlikeDead() || target.isDead() || !isInRange(target, 2000)) //why alikeDead?
			return;

		getListeners().onAttack(target);

		// Get the Attack Speed of the L2Character (delay (in milliseconds) before next attack)
		int sAtk = Math.max(calculateAttackDelay(), 333);
		int ssGrade = 0;

		WeaponTemplate weaponItem = getActiveWeaponTemplate();
		if(weaponItem != null)
		{
			if(isPlayer() && weaponItem.getAttackReuseDelay() > 0)
			{
				int reuse = (int) (weaponItem.getAttackReuseDelay() * getReuseModifier(target) * 666 * calcStat(Stats.BASE_P_ATK_SPD, 0, target, null) / 293. / getPAtkSpd());
				if(reuse > 0)
				{
					sendPacket(new SetupGaugePacket(this, SetupGaugePacket.Colors.RED_MINI, reuse));
					_attackReuseEndTime = reuse + System.currentTimeMillis() - 75;
					if(reuse > sAtk)
						ThreadPoolManager.getInstance().schedule(new NotifyAITask(this, CtrlEvent.EVT_READY_TO_ACT, null, null), reuse);
				}
			}

			ssGrade = weaponItem.getGrade().extOrdinal();
		}

		if(isNpc())
		{
			int baseReuseDelay = ((NpcTemplate) getTemplate()).getBaseReuseDelay();
			if(baseReuseDelay > 0)
			{
				int reuse = (int) (baseReuseDelay * getReuseModifier(target) * 666 * calcStat(Stats.BASE_P_ATK_SPD, 0, target, null) / 293. / getPAtkSpd());
				if(reuse > 0)
				{
					_attackReuseEndTime = reuse + System.currentTimeMillis() - 75;
					if(reuse > sAtk)
						ThreadPoolManager.getInstance().schedule(new NotifyAITask(this, CtrlEvent.EVT_READY_TO_ACT, null, null), reuse);
				}
			}
		}

		// DS: скорректировано на 1/100 секунды поскольку AI task вызывается с небольшой погрешностью
		// особенно на слабых машинах и происходит обрыв автоатаки по isAttackingNow() == true
		_attackEndTime = sAtk + System.currentTimeMillis() - 10;
		_isAttackAborted = false;

		AttackPacket attack = new AttackPacket(this, target, getChargedSoulShot(), ssGrade);

		setHeading(PositionUtils.calculateHeadingFrom(this, target));

		switch(getBaseStats().getAttackType())
		{
			case BOW:
			case CROSSBOW:
				doAttackHitByBow(attack, target, sAtk);
				break;
			case POLE:
				doAttackHitByPole(attack, target, sAtk);
				break;
			case DUAL:
			case DUALFIST:
			case DUALDAGGER:
				doAttackHitByDual(attack, target, sAtk);
				break;
			default:
				doAttackHitSimple(attack, target, 1., true, sAtk, true);
		}

		if(attack.hasHits())
			broadcastPacket(attack);
	}

	private void doAttackHitSimple(AttackPacket attack, Creature target, double multiplier, boolean unchargeSS, int sAtk, boolean notify)
	{
		int damage1 = 0;
		boolean shld1 = false;
		boolean crit1 = false;
		boolean miss1 = Formulas.calcHitMiss(this, target);

		if(!miss1)
		{
			AttackInfo info = Formulas.calcPhysDam(this, target, null, false, false, attack._soulshot, false);
			damage1 = (int) (info.damage * multiplier);
			shld1 = info.shld;
			crit1 = info.crit;
		}

		ThreadPoolManager.getInstance().schedule(new HitTask(this, target, damage1, crit1, miss1, attack._soulshot, shld1, unchargeSS, notify), sAtk);

		attack.addHit(target, damage1, miss1, crit1, shld1);
	}

	private void doAttackHitByBow(AttackPacket attack, Creature target, int sAtk)
	{
		int damage1 = 0;
		boolean shld1 = false;
		boolean crit1 = false;

		// Calculate if hit is missed or not
		boolean miss1 = Formulas.calcHitMiss(this, target);

		reduceArrowCount();

		if(!miss1)
		{
			AttackInfo info = Formulas.calcPhysDam(this, target, null, false, false, attack._soulshot, false);
			damage1 = (int) info.damage;
			shld1 = info.shld;
			crit1 = info.crit;

			/* В Lindvior атака теперь не зависит от расстояния.
			int range = getPhysicalAttackRange();
			damage1 *= Math.min(range, getDistance(target)) / range * .4 + 0.8; // разброс 20% в обе стороны
			*/
		}

		ThreadPoolManager.getInstance().schedule(new HitTask(this, target, damage1, crit1, miss1, attack._soulshot, shld1, true, true), sAtk);

		attack.addHit(target, damage1, miss1, crit1, shld1);
	}

	private void doAttackHitByDual(AttackPacket attack, Creature target, int sAtk)
	{
		int damage1 = 0;
		int damage2 = 0;
		boolean shld1 = false;
		boolean shld2 = false;
		boolean crit1 = false;
		boolean crit2 = false;

		boolean miss1 = Formulas.calcHitMiss(this, target);
		boolean miss2 = Formulas.calcHitMiss(this, target);

		if(!miss1)
		{
			AttackInfo info = Formulas.calcPhysDam(this, target, null, true, false, attack._soulshot, false);
			damage1 = (int) info.damage;
			shld1 = info.shld;
			crit1 = info.crit;
		}

		if(!miss2)
		{
			AttackInfo info = Formulas.calcPhysDam(this, target, null, true, false, attack._soulshot, false);
			damage2 = (int) info.damage;
			shld2 = info.shld;
			crit2 = info.crit;
		}

		// Create a new hit task with Medium priority for hit 1 and for hit 2 with a higher delay
		ThreadPoolManager.getInstance().schedule(new HitTask(this, target, damage1, crit1, miss1, attack._soulshot, shld1, true, false), sAtk / 2);
		ThreadPoolManager.getInstance().schedule(new HitTask(this, target, damage2, crit2, miss2, attack._soulshot, shld2, false, true), sAtk);

		attack.addHit(target, damage1, miss1, crit1, shld1);
		attack.addHit(target, damage2, miss2, crit2, shld2);
	}

	private void doAttackHitByPole(AttackPacket attack, Creature target, int sAtk)
	{
		int angle = (int) calcStat(Stats.POLE_ATTACK_ANGLE, 90, target, null);
		int range = getPhysicalAttackRange();

		// Используем Math.round т.к. обычный кастинг обрезает к меньшему
		// double d = 2.95. int i = (int)d, выйдет что i = 2
		// если 1% угла или 1 дистанции не играет огромной роли, то для
		// количества целей это критично
		int attackcountmax = (int) Math.round(calcStat(Stats.POLE_TARGET_COUNT, 0, target, null));

		if(isBoss())
			attackcountmax += 27;
		else if(isRaid())
			attackcountmax += 12;
		else if(isMonster() && getLevel() > 0)
			attackcountmax += getLevel() / 7.5;

		double mult = 1.;
		_poleAttackCount = 1;

		if(!isInZonePeace())// Гварды с пикой, будут атаковать только одиночные цели в городе
			for(Creature t : getAroundCharacters(range, 200))
				if(_poleAttackCount <= attackcountmax)
				{
					if(t == target || t.isDead() || !PositionUtils.isFacing(this, t, angle))
						continue;

					if(t.isAutoAttackable(this))
					{
						doAttackHitSimple(attack, t, mult, false, sAtk, false);
						mult *= Config.ALT_POLE_DAMAGE_MODIFIER;
						_poleAttackCount++;
					}
				}
				else
					break;

		_poleAttackCount = 0;
		doAttackHitSimple(attack, target, 1., true, sAtk, true);
	}

	public long getAnimationEndTime()
	{
		return _animationEndTime;
	}

	public long getDualAnimationEndTime()
	{
		return _dualAnimationEndTime;
	}

	public void doCast(Skill skill, Creature target, boolean forceUse)
	{
		if(skill == null)
			return;

		if(!skill.isHandler())
		{
			int itemConsume[] = skill.getItemConsume();
			if(itemConsume[0] > 0)
			{
				for(int i = 0; i < itemConsume.length; i++)
				{
					if(!consumeItem(skill.getItemConsumeId()[i], itemConsume[i]))
					{
						sendPacket(SystemMsg.THERE_ARE_NOT_ENOUGH_NECESSARY_ITEMS_TO_USE_THE_SKILL);
						return;
					}
				}
			}
		}

		if(skill.getReferenceItemId() > 0)
		{
			if(!consumeItemMp(skill.getReferenceItemId(), skill.getReferenceItemMpConsume()))
				return;
		}

		if(target == null)
			target = skill.getAimingTarget(this, getTarget());
		if(target == null)
			return;

		getListeners().onMagicUse(skill, target, false);

		if(this != target)
			setHeading(PositionUtils.calculateHeadingFrom(this, target));

		boolean dualCast = isCastingNow() && !isDualCastingNow() && isDualCastEnable();

		int magicId = skill.getId();
		int level = Math.max(1, getSkillDisplayLevel(magicId));

		int skillTime = skill.isSkillTimePermanent() ? skill.getHitTime() : Formulas.calcSkillCastSpd(this, skill, skill.getHitTime());
		int skillInterruptTime = skill.isSkillTimePermanent() ? skill.getSkillInterruptTime() : Formulas.calcSkillCastSpd(this, skill, skill.getSkillInterruptTime());

		int minCastTime = Math.min(Config.SKILLS_CAST_TIME_MIN, skill.getHitTime());
		if(skillTime < minCastTime)
		{
			skillTime = minCastTime;
			skillInterruptTime = 0;
		}

		if(dualCast)
			_dualAnimationEndTime = System.currentTimeMillis() + skillTime;
		else
			_animationEndTime = System.currentTimeMillis() + skillTime;

		if(skill.isMagic() && !skill.isSkillTimePermanent() && getChargedSpiritShot() > 0)
		{
			skillTime = (int) (0.70 * skillTime);
			skillInterruptTime = (int) (0.70 * skillInterruptTime);
		}

		Formulas.calcSkillMastery(skill, this); // Calculate skill mastery for current cast
		long reuseDelay = Math.max(0, Formulas.calcSkillReuseDelay(this, skill));
		MagicSkillUse msu = new MagicSkillUse(this, target, skill.getDisplayId(), level, skillTime, reuseDelay, isDualCastingNow());
		msu.setReuseSkillId(skill.getReuseSkillId());
		if(isServitor()) // TODO: [Bonux] Переделать.
		{
			Servitor.UsedSkill servitorUsedSkill = ((Servitor) this).getUsedSkill();
			if(servitorUsedSkill != null && servitorUsedSkill.getSkill() == skill)
			{
				msu.setServitorSkillInfo(servitorUsedSkill.getActionId());
				((Servitor) this).setUsedSkill(null);
			}
		}
		broadcastPacket(msu);

		if(!skill.isHandler())
			disableSkill(skill, reuseDelay);

		if(isPlayer())
		{
			if(skill.getSkillType() == SkillType.PET_SUMMON)
				sendPacket(SystemMsg.SUMMONING_YOUR_PET);
			else if(!skill.isHandler())
				sendPacket(new SystemMessagePacket(SystemMsg.YOU_USE_S1).addSkillName(magicId, level));
		}

		if(skill.getTargetType() == SkillTargetType.TARGET_HOLY)
			target.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, this, 1);

		double mpConsume1 = skill.isUsingWhileCasting() ? skill.getMpConsume() : skill.getMpConsume1();
		if(mpConsume1 > 0)
		{
			if(_currentMp < mpConsume1)
			{
				sendPacket(SystemMsg.NOT_ENOUGH_MP);
				onCastEndTime(dualCast, false);
				return;
			}
			reduceCurrentMp(mpConsume1, null);
		}

		if(dualCast)
			_dualFlyLoc = null;
		else
			_flyLoc = null;

		switch(skill.getFlyType())
		{
			case DUMMY:
			case CHARGE:
				Location flyLoc = getFlyLocation(target, skill);
				if(flyLoc != null)
				{
					if(dualCast)
						_dualFlyLoc = flyLoc;
					else
						_flyLoc = flyLoc;

					broadcastPacket(new FlyToLocationPacket(this, flyLoc, skill.getFlyType(), skill.getFlySpeed(), skill.getFlyDelay(), skill.getFlyAnimationSpeed()));
				}
				else
				{
					sendPacket(SystemMsg.CANNOT_SEE_TARGET);
					return;
				}
		}

		if(dualCast)
		{
			_dualCastingSkill = skill;
			_dualCastInterruptTime = System.currentTimeMillis() + skillInterruptTime;

			setDualCastingTarget(target);
		}
		else
		{
			_castingSkill = skill;
			_castInterruptTime = System.currentTimeMillis() + skillInterruptTime;

			setCastingTarget(target);
		}

		if(skill.isUsingWhileCasting())
			callSkill(skill, skill.getTargets(this, target, forceUse), true);

		if(isPlayer())
			sendPacket(new SetupGaugePacket(this, SetupGaugePacket.Colors.BLUE, skillTime));

		if(dualCast)
		{
			_scheduledDualCastCount = skill.getCastCount();
			_scheduledDualCastInterval = skill.getCastCount() > 0 ? skillTime / _scheduledDualCastCount : skillTime;
		}
		else
		{
			_scheduledCastCount = skill.getCastCount();
			_scheduledCastInterval = skill.getCastCount() > 0 ? skillTime / _scheduledCastCount : skillTime;
		}

		// Create a task MagicUseTask with Medium priority to launch the
		// MagicSkill at the end of the casting time
		if(dualCast)
		{
			_skillDualLaunchedTask = ThreadPoolManager.getInstance().schedule(new MagicLaunchedTask(this, forceUse, true), skillInterruptTime);
			_skillDualTask = ThreadPoolManager.getInstance().schedule(new MagicUseTask(this, forceUse, true), skill.getCastCount() > 0 ? skillTime / skill.getCastCount() : skillTime);
		}
		else
		{
			_skillLaunchedTask = ThreadPoolManager.getInstance().schedule(new MagicLaunchedTask(this, forceUse, false), skillInterruptTime);
			_skillTask = ThreadPoolManager.getInstance().schedule(new MagicUseTask(this, forceUse, false), skill.getCastCount() > 0 ? skillTime / skill.getCastCount() : skillTime);
		}
		
		if(skill.getOnCastSkill() != 0)
		{
			if(Formulas.calcSkillSuccess(this, target, skill, skill.getActivateRate()))
				for(Creature targets : skill.getTargets(this, target, true))
					SkillTable.getInstance().getInfo(skill.getOnCastSkill(), skill.getOnCastSkillLevel()).getEffects(this, targets, false, false);
			else
				ThreadPoolManager.getInstance().schedule(new AbortCastDelayed(this), 1000);
		}		
	}

	private Location _flyLoc;
	private Location _dualFlyLoc;

	private GameObject _flyTarget;
	private GameObject _dualFlyTarget;

	public Location getFlyLocation(GameObject target, Skill skill)
	{
		if(target != null && target != this)
		{
			Location loc;

			int heading = target.getHeading();
			if(!skill.isFlyDependsOnHeading())
				heading = PositionUtils.calculateHeadingFrom(target, this);

			double radian = PositionUtils.convertHeadingToDegree(heading) + skill.getFlyPositionDegree();
			if(radian > 360)
				radian -= 360;

			radian = (Math.PI * radian) / 180;

			loc = new Location(target.getX() + (int) (Math.cos(radian) * 40), target.getY() + (int) (Math.sin(radian) * 40), target.getZ());

			if(isFlying())
			{
				if(isInFlyingTransform() && ((loc.z <= 0) || (loc.z >= 6000)))
					return null;
				if(GeoEngine.moveCheckInAir(getX(), getY(), getZ(), loc.x, loc.y, loc.z, getCollisionRadius(), getGeoIndex()) == null)
					return null;
			}
			else
			{
				loc.correctGeoZ();

				if(!GeoEngine.canMoveToCoord(getX(), getY(), getZ(), loc.x, loc.y, loc.z, getGeoIndex()))
				{
					loc = target.getLoc(); // Если не получается встать рядом с объектом, пробуем встать прямо в него
					if(!GeoEngine.canMoveToCoord(getX(), getY(), getZ(), loc.x, loc.y, loc.z, getGeoIndex()))
						return null;
				}
			}

			if(!isDualCastingNow() && isDualCastEnable())
				_dualFlyTarget = target;
			else
				_flyTarget = target;

			return loc;
		}

		double radian = PositionUtils.convertHeadingToRadian(getHeading());
		int x1 = -(int) (Math.sin(radian) * skill.getFlyRadius());
		int y1 = (int) (Math.cos(radian) * skill.getFlyRadius());

		if(isFlying())
			return GeoEngine.moveCheckInAir(getX(), getY(), getZ(), getX() + x1, getY() + y1, getZ(), getCollisionRadius(), getGeoIndex());
		return GeoEngine.moveCheck(getX(), getY(), getZ(), getX() + x1, getY() + y1, getGeoIndex());
	}

	public final void doDie(Creature killer)
	{
		// killing is only possible one time
		if(!isDead.compareAndSet(false, true))
			return;

		onDeath(killer);
	}

	protected void onDeath(Creature killer)
	{
		if(killer != null)
		{
			Player killerPlayer = killer.getPlayer();
			if(killerPlayer != null)
			{
				killerPlayer.getListeners().onKillIgnorePetOrSummon(this);
				WorldStatisticsManager.getInstance().updateStat(killerPlayer, CategoryType.MONSTERS_KILLED, 1L);
			}

			killer.getListeners().onKill(this);

			if(isPlayer() && killer.isPlayable())
				_currentCp = 0;
		}

		setTarget(null);

		abortCast(true, false);
		abortAttack(true, false);

		stopMove();
		stopAttackStanceTask();
		stopRegeneration();

		_currentHp = 0;

		// Stop all active skills effects in progress on the L2Character
		if(isBlessedByNoblesse() || isSalvation())
		{
			if(isSalvation() && isPlayer() && !getPlayer().isInOlympiadMode() && getPlayer().getLfcGame() == null)
				getPlayer().reviveRequest(getPlayer(), 100, false);

			for(Effect e : getEffectList().getEffects())
			{
				// Noblesse Blessing Buff/debuff effects are retained after
				// death. However, Noblesse Blessing and Lucky Charm are lost as normal.
				if(e.getEffectType() == EffectType.BlessNoblesse || e.getSkill().getId() == Skill.SKILL_FORTUNE_OF_NOBLESSE || e.getSkill().getId() == Skill.SKILL_RAID_BLESSING)
					e.exit();
				else if(e.getEffectType() == EffectType.AgathionResurrect)
				{
					if(isPlayer())
						getPlayer().setAgathionRes(true);
					e.exit();
				}
			}
		}
		else
		{
			for(Effect e : getEffectList().getEffects())
			{
				// Некоторые эффекты сохраняются при смерти
				if(!e.getSkill().isPreservedOnDeath())
					e.exit();
			}
		}

		ThreadPoolManager.getInstance().execute(new NotifyAITask(this, CtrlEvent.EVT_DEAD, killer, null));

		getListeners().onDeath(killer);

		updateEffectIcons();
		updateStats();
		broadcastStatusUpdate();
	}

	protected void onRevive()
	{

	}

	public void enableSkill(Skill skill)
	{
		_skillReuses.remove(skill.getReuseHash());
	}

	/**
	 * Return a map of 32 bits (0x00000000) containing all abnormal effects
	 */
	public Set<AbnormalEffect> getAbnormalEffects()
	{
		return _abnormalEffects;
	}

	public AbnormalEffect[] getAbnormalEffectsArray()
	{
		return _abnormalEffects.toArray(new AbnormalEffect[_abnormalEffects.size()]);
	}

	public int getPAccuracy()
	{
		return (int) Math.round(calcStat(Stats.P_ACCURACY_COMBAT, 0, null, null));
	}

	public int getMAccuracy()
	{
		return (int) calcStat(Stats.M_ACCURACY_COMBAT, 0, null, null);
	}

	/**
	 * Возвращает коллекцию скиллов для быстрого перебора
	 */
	public Collection<Skill> getAllSkills()
	{
		return _skills.values();
	}

	/**
	 * Возвращает массив скиллов для безопасного перебора
	 */
	public final Skill[] getAllSkillsArray()
	{
		Collection<Skill> vals = _skills.values();
		return vals.toArray(new Skill[vals.size()]);
	}

	public final double getAttackSpeedMultiplier()
	{
		return 1.1 * getPAtkSpd() / getBaseStats().getPAtkSpd();
	}

	public int getBuffLimit()
	{
		return (int) calcStat(Stats.BUFF_LIMIT, Config.ALT_BUFF_LIMIT, null, null);
	}

	public Skill getCastingSkill()
	{
		return _castingSkill;
	}

	public Skill getDualCastingSkill()
	{
		return _dualCastingSkill;
	}

	/**
	 * Возвращает шанс физического крита (1000 == 100%)
	 */
	public int getPCriticalHit(Creature target)
	{
		return (int) Math.round(calcStat(Stats.BASE_P_CRITICAL_RATE, getBaseStats().getPCritRate(), target, null));
	}

	/**
	 * Возвращает шанс магического крита (1000 == 100%)
	 */
	public int getMCriticalHit(Creature target, Skill skill)
	{
		return (int) Math.round(calcStat(Stats.BASE_M_CRITICAL_RATE, getBaseStats().getMCritRate(), target, skill));
	}
	
	//return crit dam power for magic
	public double getMagicCriticalDmg(Creature target, Skill skill)
	{
		return calcStat(Stats.M_CRITICAL_DAMAGE, target, skill);
	}	
	/**
	 * Return the current CP of the L2Character.
	 *
	 */
	public double getCurrentCp()
	{
		return _currentCp;
	}

	public final double getCurrentCpRatio()
	{
		return getCurrentCp() / getMaxCp();
	}

	public final double getCurrentCpPercents()
	{
		return getCurrentCpRatio() * 100.;
	}

	public final boolean isCurrentCpFull()
	{
		return getCurrentCp() >= getMaxCp();
	}

	public final boolean isCurrentCpZero()
	{
		return getCurrentCp() < 1;
	}

	public double getCurrentHp()
	{
		return _currentHp;
	}

	public final double getCurrentHpRatio()
	{
		return getCurrentHp() / getMaxHp();
	}

	public final double getCurrentHpPercents()
	{
		return getCurrentHpRatio() * 100.;
	}

	public final boolean isCurrentHpFull()
	{
		return getCurrentHp() >= getMaxHp();
	}

	public final boolean isCurrentHpZero()
	{
		return getCurrentHp() < 1;
	}

	public double getCurrentMp()
	{
		return _currentMp;
	}

	public final double getCurrentMpRatio()
	{
		return getCurrentMp() / getMaxMp();
	}

	public final double getCurrentMpPercents()
	{
		return getCurrentMpRatio() * 100.;
	}

	public final boolean isCurrentMpFull()
	{
		return getCurrentMp() >= getMaxMp();
	}

	public final boolean isCurrentMpZero()
	{
		return getCurrentMp() < 1;
	}

	public Location getDestination()
	{
		if(destination == null)
			return new Location(0,0,0);
		return destination;
	}

	public int getINT()
	{
		return (int) calcStat(Stats.STAT_INT, getBaseStats().getINT(), null, null);
	}

	public int getSTR()
	{
		return (int) calcStat(Stats.STAT_STR, getBaseStats().getSTR(), null, null);
	}

	public int getCON()
	{
		return (int) calcStat(Stats.STAT_CON, getBaseStats().getCON(), null, null);
	}

	public int getMEN()
	{
		return (int) calcStat(Stats.STAT_MEN, getBaseStats().getMEN(), null, null);
	}

	public int getDEX()
	{
		return (int) calcStat(Stats.STAT_DEX, getBaseStats().getDEX(), null, null);
	}

	public int getWIT()
	{
		return (int) calcStat(Stats.STAT_WIT, getBaseStats().getWIT(), null, null);
	}

	public int getLUC()
	{
		return (int) calcStat(Stats.STAT_LUC, getBaseStats().getLUC(), null, null);
	}

	public int getCHA()
	{
		return (int) calcStat(Stats.STAT_CHA, getBaseStats().getCHA(), null, null);
	}

	public int getPEvasionRate(Creature target)
	{
		return (int) Math.round(calcStat(Stats.P_EVASION_RATE, 0, target, null));
	}

	public int getMEvasionRate(Creature target)
	{
		return (int) calcStat(Stats.M_EVASION_RATE, 0, target, null);
	}

	public List<Creature> getAroundCharacters(int radius, int height)
	{
		if(!isVisible())
			return Collections.emptyList();
		return World.getAroundCharacters(this, radius, height);
	}

	public List<NpcInstance> getAroundNpc(int range, int height)
	{
		if(!isVisible())
			return Collections.emptyList();
		return World.getAroundNpc(this, range, height);
	}

	public boolean knowsObject(GameObject obj)
	{
		return World.getAroundObjectById(this, obj.getObjectId()) != null;
	}

	public final Skill getKnownSkill(int skillId)
	{
		return _skills.get(skillId);
	}

	public final int getMagicalAttackRange(Skill skill)
	{
		if(skill != null)
			return (int) calcStat(Stats.MAGIC_ATTACK_RANGE, skill.getCastRange(), null, skill);
		return getBaseStats().getAtkRange();
	}

	public int getMAtk(Creature target, Skill skill)
	{
		if(skill != null && skill.getMatak() > 0)
			return skill.getMatak();
		return (int) (calcStat(Stats.MAGIC_ATTACK, getBaseStats().getMAtk(), target, skill)*BaseStats.CHA.calcBonus(this));
	}

	public int getMAtkSpd()
	{
		return (int) (calcStat(Stats.MAGIC_ATTACK_SPEED, getBaseStats().getMAtkSpd(), null, null)*BaseStats.CHA.calcBonus(this));
	}

	public int getMaxCp()
	{
		return (int) (calcStat(Stats.MAX_CP, getBaseStats().getCpMax(), null, null)*BaseStats.CHA.calcBonus(this));
	}

	public int getMaxHp()
	{
		return (int) (calcStat(Stats.MAX_HP, getBaseStats().getHpMax(), null, null)*BaseStats.CHA.calcBonus(this));
	}

	public int getMaxMp()
	{
		return (int) (calcStat(Stats.MAX_MP, getBaseStats().getMpMax(), null, null)*BaseStats.CHA.calcBonus(this));
	}

	public int getMDef(Creature target, Skill skill)
	{
		return Math.max((int) calcStat(Stats.MAGIC_DEFENCE, getBaseStats().getMDef(), target, skill), 1);
	}

	public double getMinDistance(GameObject obj)
	{
		double distance = getCollisionRadius();

		if(obj != null && obj.isCreature())
			distance += ((Creature) obj).getCollisionRadius();

		return distance;
	}

	@Override
	public String getName()
	{
		return StringUtils.defaultString(_name);
	}

	public int getPAtk(Creature target)
	{
		return (int) (calcStat(Stats.POWER_ATTACK, getBaseStats().getPAtk(), target, null)*BaseStats.CHA.calcBonus(this));
	}

	public int getPAtkSpd()
	{
		return (int) (calcStat(Stats.POWER_ATTACK_SPEED, getBaseStats().getPAtkSpd(), null, null)*BaseStats.CHA.calcBonus(this));
	}

	public int getPDef(Creature target)
	{
		return (int) (calcStat(Stats.POWER_DEFENCE, getBaseStats().getPDef(), target, null)*BaseStats.CHA.calcBonus(this));
	}

	public int getPhysicalAttackRange()
	{
		return (int) calcStat(Stats.POWER_ATTACK_RANGE, getBaseStats().getAtkRange(), null, null);
	}

	public int getRandomDamage()
	{
		WeaponTemplate weaponItem = getActiveWeaponTemplate();
		if(weaponItem == null)
			return getBaseStats().getRandDam();
		return weaponItem.getRandomDamage();
	}

	public double getReuseModifier(Creature target)
	{
		return calcStat(Stats.ATK_REUSE, 1, target, null);
	}

	public final int getShldDef()
	{
		return (int) calcStat(Stats.SHIELD_DEFENCE, getBaseStats().getShldDef(), null, null);
	}

	public final int getSkillDisplayLevel(Integer skillId)
	{
		Skill skill = _skills.get(skillId);
		if(skill == null)
			return -1;
		return skill.getDisplayLevel();
	}

	public int getSkillLevel(Integer skillId)
	{
		return getSkillLevel(skillId, -1);
	}

	public final int getSkillLevel(Integer skillId, int def)
	{
		Skill skill = _skills.get(skillId);
		if(skill == null)
			return def;
		return skill.getLevel();
	}

	public int getSkillMastery(Integer skillId)
	{
		if(_skillMastery == null)
			return 0;
		Integer val = _skillMastery.get(skillId);
		return val == null ? 0 : val.intValue();
	}

	public void removeSkillMastery(Integer skillId)
	{
		if(_skillMastery != null)
			_skillMastery.remove(skillId);
	}

	public GameObject getTarget()
	{
		return target.get();
	}

	public final int getTargetId()
	{
		GameObject target = getTarget();
		return target == null ? -1 : target.getObjectId();
	}

	public CreatureTemplate getTemplate()
	{
		return _template;
	}

	protected void setTemplate(CreatureTemplate template)
	{
		_template = template;
	}

	public String getTitle()
	{
		return StringUtils.defaultString(_title);
	}

	public double headingToRadians(int heading)
	{
		return (heading - 32768) / HEADINGS_IN_PI;
	}

	public boolean isAlikeDead()
	{
		return _fakeDeath || isDead();
	}

	public final boolean isAttackingNow()
	{
		return _attackEndTime > System.currentTimeMillis();
	}

	public final boolean isBlessedByNoblesse()
	{
		return _isBlessedByNoblesse;
	}

	public final boolean isSalvation()
	{
		return _isSalvation;
	}

	public boolean isEffectImmune()
	{
		return _effectImmunity.get();
	}

	public boolean isBuffImmune()
	{
		return _buffImmunity.get();
	}

	public boolean isDebuffImmune()
	{
		return _debuffImmunity.get() || isPeaceNpc();
	}

	public boolean isDead()
	{
		return _currentHp < 0.5 || isDead.get();
	}

	@Override
	public final boolean isFlying()
	{
		return _flying;
	}

	/**
	 * Находится ли персонаж в боевой позе
	 * @return true, если персонаж в боевой позе, атакован или атакует
	 */
	public final boolean isInCombat()
	{
		return System.currentTimeMillis() < _stanceEndTime;
	}

	public boolean isInvul()
	{
		return _isInvul;
	}

	public boolean isMageClass()
	{
		return getBaseStats().getMAtk() > 3;
	}

	public final boolean isRunning()
	{
		return _running;
	}

	public boolean isSkillDisabled(Skill skill)
	{
		TimeStamp sts = _skillReuses.get(skill.getReuseHash());
		if(sts == null)
			return false;
		if(sts.hasNotPassed())
			return true;
		_skillReuses.remove(skill.getReuseHash());
		return false;
	}

	public final boolean isTeleporting()
	{
		return isTeleporting.get();
	}

	/**
	 * Возвращает позицию цели, в которой она будет через пол секунды.
	 */
	public Location getIntersectionPoint(Creature target)
	{
		if(!PositionUtils.isFacing(this, target, 90))
			return new Location(target.getX(), target.getY(), target.getZ());
		double angle = PositionUtils.convertHeadingToDegree(target.getHeading()); // угол в градусах
		double radian = Math.toRadians(angle - 90); // угол в радианах
		double range = target.getMoveSpeed() / 2; // расстояние, пройденное за 1 секунду, равно скорости. Берем половину.
		return new Location((int) (target.getX() - range * Math.sin(radian)), (int) (target.getY() + range * Math.cos(radian)), target.getZ());
	}

	public Location applyOffset(Location point, int offset)
	{
		if(offset <= 0)
			return point;

		long dx = point.x - getX();
		long dy = point.y - getY();
		long dz = point.z - getZ();

		double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

		if(distance <= offset)
		{
			point.set(getX(), getY(), getZ());
			return point;
		}

		if(distance >= 1)
		{
			double cut = offset / distance;
			point.x -= (int) (dx * cut + 0.5);
			point.y -= (int) (dy * cut + 0.5);
			point.z -= (int) (dz * cut + 0.5);

			if(!isFlying() && !isInBoat() && !isInWater() && !isBoat())
				point.correctGeoZ();
		}

		return point;
	}

	public List<Location> applyOffset(List<Location> points, int offset)
	{
		offset = offset >> 4;
		if(offset <= 0)
			return points;

		long dx = points.get(points.size() - 1).x - points.get(0).x;
		long dy = points.get(points.size() - 1).y - points.get(0).y;
		long dz = points.get(points.size() - 1).z - points.get(0).z;

		double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
		if(distance <= offset)
		{
			Location point = points.get(0);
			points.clear();
			points.add(point);
			return points;
		}

		if(distance >= 1)
		{
			double cut = offset / distance;
			int num = (int) (points.size() * cut + 0.5);
			for(int i = 1; i <= num && points.size() > 0; i++)
				points.remove(points.size() - 1);
		}

		return points;
	}

	private boolean setSimplePath(Location dest)
	{
		List<Location> moveList = GeoMove.constructMoveList(getLoc(), dest);
		if(moveList.isEmpty())
			return false;
		_targetRecorder.clear();
		_targetRecorder.add(moveList);
		return true;
	}

	private boolean buildPathTo(int x, int y, int z, int offset, boolean pathFind)
	{
		return buildPathTo(x, y, z, offset, null, false, pathFind);
	}

	private boolean buildPathTo(int x, int y, int z, int offset, Creature follow, boolean forestalling, boolean pathFind)
	{
		int geoIndex = getGeoIndex();

		Location dest;

		if(forestalling && follow != null && follow.isMoving)
			dest = getIntersectionPoint(follow);
		else
			dest = new Location(x, y, z);

		if(isInBoat() || isBoat() || !Config.ALLOW_GEODATA)
		{
			applyOffset(dest, offset);
			return setSimplePath(dest);
		}

		if(isFlying() || isInWater())
		{
			applyOffset(dest, offset);

			Location nextloc;

			if(isFlying())
			{
				if(GeoEngine.canSeeCoord(this, dest.x, dest.y, dest.z, true))
					return setSimplePath(dest);

				nextloc = GeoEngine.moveCheckInAir(getX(), getY(), getZ(), dest.x, dest.y, dest.z, getCollisionRadius(), geoIndex);
				if(nextloc != null && !nextloc.equals(getX(), getY(), getZ()))
					return setSimplePath(nextloc);
			}
			else
			{
				int waterZ = getWaterZ();
				nextloc = GeoEngine.moveInWaterCheck(getX(), getY(), getZ(), dest.x, dest.y, dest.z, waterZ, geoIndex);
				if(nextloc == null)
					return false;

				List<Location> moveList = GeoMove.constructMoveList(getLoc(), nextloc.clone());
				_targetRecorder.clear();
				if(!moveList.isEmpty())
					_targetRecorder.add(moveList);

				int dz = dest.z - nextloc.z;
				// если пытаемся выбратся на берег, считаем путь с точки выхода до точки назначения
				if(dz > 0 && dz < 128)
				{
					moveList = GeoEngine.MoveList(nextloc.x, nextloc.y, nextloc.z, dest.x, dest.y, geoIndex, false);
					if(moveList != null) // null - до конца пути дойти нельзя
					{
						if(!moveList.isEmpty()) // уже стоим на нужной клетке
							_targetRecorder.add(moveList);
					}
				}

				return !_targetRecorder.isEmpty();
			}
			return false;
		}

		List<Location> moveList = GeoEngine.MoveList(getX(), getY(), getZ(), dest.x, dest.y, geoIndex, true); // onlyFullPath = true - проверяем весь путь до конца
		if(moveList != null) // null - до конца пути дойти нельзя
		{
			if(moveList.isEmpty()) // уже стоим на нужной клетке
				return false;
			applyOffset(moveList, offset);
			if(moveList.isEmpty()) // уже стоим на нужной клетке
				return false;
			_targetRecorder.clear();
			_targetRecorder.add(moveList);
			return true;
		}

		if(pathFind)
		{
			List<List<Location>> targets = GeoMove.findMovePath(getX(), getY(), getZ(), dest.clone(), this, true, geoIndex);
			if(!targets.isEmpty())
			{
				moveList = targets.remove(targets.size() - 1);
				applyOffset(moveList, offset);
				if(!moveList.isEmpty())
					targets.add(moveList);
				if(!targets.isEmpty())
				{
					_targetRecorder.clear();
					_targetRecorder.addAll(targets);
					return true;
				}
			}
		}

		if(follow != null)
			return false;

		applyOffset(dest, offset);

		moveList = GeoEngine.MoveList(getX(), getY(), getZ(), dest.x, dest.y, geoIndex, false); // onlyFullPath = false - идем до куда можем
		if(moveList != null && !moveList.isEmpty()) // null - нет геодаты, empty - уже стоим на нужной клетке
		{
			_targetRecorder.clear();
			_targetRecorder.add(moveList);
			return true;
		}

		return false;
	}

	public Creature getFollowTarget()
	{
		return followTarget.get();
	}

	public void setFollowTarget(Creature target)
	{
		followTarget = target == null ? HardReferences.<Creature> emptyRef() : target.getRef();
	}

	public boolean followToCharacter(Creature target, int offset, boolean forestalling)
	{
		return followToCharacter(target.getLoc(), target, offset, forestalling);
	}

	public boolean followToCharacter(Location loc, Creature target, int offset, boolean forestalling)
	{
		moveLock.lock();
		try
		{
			if(isMovementDisabled() || target == null || isInBoat() && !isInShuttle() || isInWater())
				return false;

			offset = Math.max(offset, 10);
			if(isFollow && target == getFollowTarget() && offset == _offset)
				return true;

			if(Math.abs(getZ() - target.getZ()) > 1000 && !isFlying())
			{
				sendPacket(SystemMsg.CANNOT_SEE_TARGET);
				return false;
			}

			getAI().clearNextAction();

			stopMove(false, false);

			if(buildPathTo(loc.x, loc.y, loc.z, offset, target, forestalling, !target.isDoor()))
				movingDestTempPos.set(loc.x, loc.y, loc.z);
			else
				return false;

			isMoving = true;
			isFollow = true;
			_forestalling = forestalling;
			_offset = offset;
			setFollowTarget(target);

			moveNext(true);

			return true;
		}
		finally
		{
			moveLock.unlock();
		}
	}

	public boolean moveToLocation(Location loc, int offset, boolean pathfinding)
	{
		return moveToLocation(loc.x, loc.y, loc.z, offset, pathfinding, true);
	}

	public boolean moveToLocation(Location loc, int offset, boolean pathfinding, boolean cancelNextAction)
	{
		return moveToLocation(loc.x, loc.y, loc.z, offset, pathfinding, cancelNextAction);
	}

	public boolean moveToLocation(int x_dest, int y_dest, int z_dest, int offset, boolean pathfinding)
	{
		return moveToLocation(x_dest, y_dest, z_dest, offset, pathfinding, true);
	}

	public boolean moveToLocation(int x_dest, int y_dest, int z_dest, int offset, boolean pathfinding, boolean cancelNextAction)
	{
		moveLock.lock();
		try
		{
			offset = Math.max(offset, 0);
			Location dst_geoloc = new Location(x_dest, y_dest, z_dest).world2geo();
			if(isMoving && !isFollow && movingDestTempPos.equals(dst_geoloc))
			{
				sendActionFailed();
				return true;
			}

			if(isMovementDisabled())
			{
				getAI().setNextAction(AINextAction.MOVE, new Location(x_dest, y_dest, z_dest), offset, pathfinding, false);
				sendActionFailed();
				return false;
			}

			getAI().clearNextAction();

			if(isPlayer())
			{
				if(cancelNextAction)
					getAI().changeIntention(AI_INTENTION_ACTIVE, null, null);
			}

			stopMove(false, false);

			if(buildPathTo(x_dest, y_dest, z_dest, offset, pathfinding))
				movingDestTempPos.set(dst_geoloc);
			else
			{
				sendActionFailed();
				return false;
			}

			isMoving = true;

			moveNext(true);

			return true;
		}
		finally
		{
			moveLock.unlock();
		}
	}

	private void moveNext(boolean firstMove)
	{
		if(!isMoving || isMovementDisabled())
		{
			stopMove();
			return;
		}

		_previousSpeed = getMoveSpeed();
		if(_previousSpeed <= 0)
		{
			stopMove();
			return;
		}

		if(!firstMove)
		{
			if(destination != null)
				setLoc(destination, true);
		}

		if(_targetRecorder.isEmpty())
		{
			CtrlEvent ctrlEvent = isFollow ? CtrlEvent.EVT_ARRIVED_TARGET : CtrlEvent.EVT_ARRIVED;
			stopMove(false, true);
			ThreadPoolManager.getInstance().execute(new NotifyAITask(this, ctrlEvent));
			return;
		}

		moveList = _targetRecorder.remove(0);
		Location begin = moveList.get(0).clone().geo2world();
		Location end = moveList.get(moveList.size() - 1).clone().geo2world();
		destination = end;
		double distance = (isFlying() || isInWater()) ? begin.distance3D(end) : begin.distance(end); //клиент при передвижении не учитывает поверхность

		if(distance != 0)
			setHeading(PositionUtils.calculateHeadingFrom(getX(), getY(), destination.x, destination.y));

		broadcastMove();

		_startMoveTime = _followTimestamp = System.currentTimeMillis();
		if(_moveTaskRunnable == null)
			_moveTaskRunnable = new MoveNextTask();
		_moveTask = ThreadPoolManager.getInstance().schedule(_moveTaskRunnable.setDist(distance), getMoveTickInterval());
	}

	private void broadcastMove()
	{
		validateLocation(isPlayer() ? 2 : 1);
		broadcastPacket(movePacket());
	}

	/**
	 * Останавливает движение и рассылает StopMove, ValidateLocation
	 */
	public void stopMove()
	{
		stopMove(true, true);
	}

	/**
	 * Останавливает движение и рассылает StopMove
	 * @param validate - рассылать ли ValidateLocation
	 */
	public void stopMove(boolean validate)
	{
		stopMove(true, validate);
	}

	/**
	 * Останавливает движение
	 *
	 * @param stop - рассылать ли StopMove
	 * @param validate - рассылать ли ValidateLocation
	 */
	public void stopMove(boolean stop, boolean validate)
	{
		if(!isMoving)
			return;

		moveLock.lock();
		try
		{
			if(!isMoving)
				return;

			isMoving = false;
			isFollow = false;

			if(_moveTask != null)
			{
				_moveTask.cancel(false);
				_moveTask = null;
			}

			destination = null;
			moveList = null;

			_targetRecorder.clear();

			if(validate)
				validateLocation(isPlayer() ? 2 : 1);
			if(stop)
				broadcastPacket(stopMovePacket());
		}
		finally
		{
			moveLock.unlock();
		}
	}

	/** Возвращает координаты поверхности воды, если мы находимся в ней, или над ней. */
	public int getWaterZ()
	{
		if(!isInWater())
			return Integer.MIN_VALUE;

		int waterZ = Integer.MIN_VALUE;
		zonesRead.lock();
		try
		{
			Zone zone;
			for(int i = 0; i < _zones.size(); i++)
			{
				zone = _zones.get(i);
				if(zone.getType() == ZoneType.water)
					if(waterZ == Integer.MIN_VALUE || waterZ < zone.getTerritory().getZmax())
						waterZ = zone.getTerritory().getZmax();
			}
		}
		finally
		{
			zonesRead.unlock();
		}

		return waterZ;
	}

	protected L2GameServerPacket stopMovePacket()
	{
		return new StopMovePacket(this);
	}

	public L2GameServerPacket movePacket()
	{
		return new MTLPacket(this);
	}

	public void updateZones()
	{
		if(isInObserverMode())
			return;

		Zone[] zones = isVisible() ? getCurrentRegion().getZones() : Zone.EMPTY_L2ZONE_ARRAY;

		LazyArrayList<Zone> entering = null;
		LazyArrayList<Zone> leaving = null;

		Zone zone;

		zonesWrite.lock();
		try
		{
			if(!_zones.isEmpty())
			{
				leaving = LazyArrayList.newInstance();
				for(int i = 0; i < _zones.size(); i++)
				{
					zone = _zones.get(i);
					// зоны больше нет в регионе, либо вышли за территорию зоны
					if(!ArrayUtils.contains(zones, zone) || !zone.checkIfInZone(getX(), getY(), getZ(), getReflection()))
						leaving.add(zone);
				}

				//Покинули зоны, убираем из списка зон персонажа
				if(!leaving.isEmpty())
				{
					for(int i = 0; i < leaving.size(); i++)
					{
						zone = leaving.get(i);
						_zones.remove(zone);
					}
				}
			}

			if(zones.length > 0)
			{
				entering = LazyArrayList.newInstance();
				for(int i = 0; i < zones.length; i++)
				{
					zone = zones[i];
					// в зону еще не заходили и зашли на территорию зоны
					if(!_zones.contains(zone) && zone.checkIfInZone(getX(), getY(), getZ(), getReflection()))
						entering.add(zone);
				}

				//Вошли в зоны, добавим в список зон персонажа
				if(!entering.isEmpty())
				{
					for(int i = 0; i < entering.size(); i++)
					{
						zone = entering.get(i);
						_zones.add(zone);
					}
				}
			}
		}
		finally
		{
			zonesWrite.unlock();
		}

		onUpdateZones(leaving, entering);

		if(leaving != null)
			LazyArrayList.recycle(leaving);

		if(entering != null)
			LazyArrayList.recycle(entering);

	}

	protected void onUpdateZones(List<Zone> leaving, List<Zone> entering)
	{
		Zone zone;

		if(leaving != null && !leaving.isEmpty())
		{
			for(int i = 0; i < leaving.size(); i++)
			{
				zone = leaving.get(i);
				zone.doLeave(this);
			}
		}

		if(entering != null && !entering.isEmpty())
		{
			for(int i = 0; i < entering.size(); i++)
			{
				zone = entering.get(i);
				zone.doEnter(this);
			}
		}
	}

	public boolean isInZonePeace()
	{
		return isInZone(ZoneType.peace_zone) && !isInZoneBattle();
	}

	public boolean isInZoneBattle()
	{
		return isInZone(ZoneType.battle_zone);
	}

	public boolean isInWater()
	{
		return isInZone(ZoneType.water) && !(isInBoat() || isBoat() || isFlying());
	}

	public boolean isInZone(ZoneType type)
	{
		zonesRead.lock();
		try
		{
			Zone zone;
			for(int i = 0; i < _zones.size(); i++)
			{
				zone = _zones.get(i);
				if(zone.getType() == type)
					return true;
			}
		}
		finally
		{
			zonesRead.unlock();
		}

		return false;
	}

	public boolean isInZone(String name)
	{
		zonesRead.lock();
		try
		{
			Zone zone;
			for(int i = 0; i < _zones.size(); i++)
			{
				zone = _zones.get(i);
				if(zone.getName().equals(name))
					return true;
			}
		}
		finally
		{
			zonesRead.unlock();
		}

		return false;
	}

	public boolean isInZone(Zone zone)
	{
		zonesRead.lock();
		try
		{
			return _zones.contains(zone);
		}
		finally
		{
			zonesRead.unlock();
		}
	}

	public Zone getZone(ZoneType type)
	{
		zonesRead.lock();
		try
		{
			Zone zone;
			for(int i = 0; i < _zones.size(); i++)
			{
				zone = _zones.get(i);
				if(zone.getType() == type)
					return zone;
			}
		}
		finally
		{
			zonesRead.unlock();
		}
		return null;
	}

	public Location getRestartPoint()
	{
		zonesRead.lock();
		try
		{
			Zone zone;
			for(int i = 0; i < _zones.size(); i++)
			{
				zone = _zones.get(i);
				if(zone.getRestartPoints() != null)
				{
					ZoneType type = zone.getType();
					if(type == ZoneType.battle_zone || type == ZoneType.peace_zone || type == ZoneType.offshore || type == ZoneType.dummy)
						return zone.getSpawn();
				}
			}
		}
		finally
		{
			zonesRead.unlock();
		}

		return null;
	}

	public Location getPKRestartPoint()
	{
		zonesRead.lock();
		try
		{
			Zone zone;
			for(int i = 0; i < _zones.size(); i++)
			{
				zone = _zones.get(i);
				if(zone.getRestartPoints() != null)
				{
					ZoneType type = zone.getType();
					if(type == ZoneType.battle_zone || type == ZoneType.peace_zone || type == ZoneType.offshore || type == ZoneType.dummy)
						return zone.getPKSpawn();
				}
			}
		}
		finally
		{
			zonesRead.unlock();
		}

		return null;
	}

	@Override
	public int getGeoZ(Location loc)
	{
		if(isFlying() || isInWater() || isInBoat() || isBoat() || isDoor())
			return loc.z;

		return super.getGeoZ(loc);
	}

	protected boolean needStatusUpdate()
	{
		if(!isVisible())
			return false;

		boolean result = false;

		int bar;
		bar = (int) (getCurrentHp() * CLIENT_BAR_SIZE / getMaxHp());
		if(bar == 0 || bar != _lastHpBarUpdate)
		{
			_lastHpBarUpdate = bar;
			result = true;
		}

		bar = (int) (getCurrentMp() * CLIENT_BAR_SIZE / getMaxMp());
		if(bar == 0 || bar != _lastMpBarUpdate)
		{
			_lastMpBarUpdate = bar;
			result = true;
		}

		if(isPlayer())
		{
			bar = (int) (getCurrentCp() * CLIENT_BAR_SIZE / getMaxCp());
			if(bar == 0 || bar != _lastCpBarUpdate)
			{
				_lastCpBarUpdate = bar;
				result = true;
			}
		}

		return result;
	}

	@Override
	public void onForcedAttack(Player player, boolean shift)
	{
		player.sendPacket(new MyTargetSelectedPacket(player, this));

		if(!isAttackable(player) || player.isConfused() || player.isBlocked())
		{
			player.sendActionFailed();
			return;
		}

		player.getAI().Attack(this, true, shift);
	}

	public void onHitTimer(Creature target, int damage, boolean crit, boolean miss, boolean soulshot, boolean shld, boolean unchargeSS)
	{
		if(isAlikeDead())
		{
			sendActionFailed();
			return;
		}

		if(target.isDead() || !isInRange(target, 2000))
		{
			sendActionFailed();
			return;
		}

		if(isPlayable() && target.isPlayable() && isInZoneBattle() != target.isInZoneBattle())
		{
			Player player = getPlayer();
			if(player != null)
			{
				player.sendPacket(SystemMsg.INVALID_TARGET);
				player.sendActionFailed();
			}
			return;
		}

		target.getListeners().onAttackHit(this);

		// if hitted by a cursed weapon, Cp is reduced to 0, if a cursed weapon is hitted by a Hero, Cp is reduced to 0
		if(!miss && target.isPlayer() && (isCursedWeaponEquipped() || getActiveWeaponInstance() != null && getActiveWeaponInstance().isHeroWeapon() && target.isCursedWeaponEquipped()))
			target.setCurrentCp(0);

		if(target.isStunned() && Formulas.calcStunBreak(crit))
			target.getEffectList().stopEffects(EffectType.Stun);

		ThreadPoolManager.getInstance().execute(new NotifyAITask(target, CtrlEvent.EVT_ATTACKED, this, damage));

		boolean checkPvP = checkPvP(target, null);

		// Reduce HP of the target and calculate reflection damage to reduce HP of attacker if necessary
		target.reduceCurrentHp(damage, this, null, true, true, false, true, false, false, true, true, crit, miss, shld, false);

		if(!miss && damage > 0)
		{
			// Скиллы, кастуемые при физ атаке
			if(!target.isDead())
			{
				if(crit)
					useTriggers(target, TriggerType.CRIT, null, null, damage);

				useTriggers(target, TriggerType.ATTACK, null, null, damage);

				// Manage attack or cast break of the target (calculating rate, sending message...)
				if(Formulas.calcCastBreak(target, crit))
					target.abortCast(false, true);
			}

			if(soulshot && unchargeSS)
				unChargeShots(false);
		}

		if(miss)
			target.useTriggers(this, TriggerType.UNDER_MISSED_ATTACK, null, null, damage);

		startAttackStanceTask();

		if(checkPvP)
			startPvPFlag(target);

		if(target.isPlayer())
		{
			Servitor[] servitors = target.getPlayer().getServitors();
			if(servitors.length > 0)
			{
				for(Servitor servitor : servitors)
					servitor.onOwnerGotAttacked(this);
			}
		}
		else if(target.isServitor())
			((Servitor) target).onAttacked(this);
		else if(isPlayer())
		{
			Servitor[] servitors = getPlayer().getServitors();
			if(servitors.length > 0)
			{
				for(Servitor servitor : servitors)
					servitor.onOwnerOfAttacks(target);
			}
		}
	}

	public void onMagicUseTimer(Creature aimingTarget, Skill skill, boolean forceUse, boolean dual)
	{
		if(skill == null || skill.isOnlyOnCastSkillEffect())
		{
			aimingTarget.getEffectList().stopEffects(skill);
			onCastEndTime(dual, false);
			sendActionFailed();
			return;
		}

		if(dual)
			_dualCastInterruptTime = 0;
		else
			_castInterruptTime = 0;

		if(skill.isUsingWhileCasting())
		{
			aimingTarget.getEffectList().stopEffects(skill);
			onCastEndTime(dual, false);
			return;
		}

		if(!skill.isOffensive() && getAggressionTarget() != null)
			forceUse = true;

		if(!skill.checkCondition(this, aimingTarget, forceUse, false, false))
		{
			if(skill.getSkillType() == SkillType.PET_SUMMON && isPlayer())
				getPlayer().setPetControlItem(null);
			onCastEndTime(dual, false);
			return;
		}

		if(skill.getCastRange() < 32767 && skill.getSkillType() != SkillType.TAKECASTLE && skill.getSkillType() != SkillType.TAKEFORTRESS && !GeoEngine.canSeeTarget(this, aimingTarget, isFlying()))
		{
			sendPacket(SystemMsg.CANNOT_SEE_TARGET);
			broadcastPacket(new MagicSkillCanceled(getObjectId()));
			onCastEndTime(dual, false);
			return;
		}

		List<Creature> targets = skill.getTargets(this, aimingTarget, forceUse);

		//must be player for usage with a clan.
		int clanRepConsume = skill.getClanRepConsume();
		if(clanRepConsume > 0)
			getPlayer().getClan().incReputation(-clanRepConsume, false, "clan skills");

		int fameConsume = skill.getFameConsume();
		if(fameConsume > 0)
			getPlayer().setFame(getPlayer().getFame() - fameConsume, "clan skills");

		int hpConsume = skill.getHpConsume();
		if(hpConsume > 0)
			setCurrentHp(Math.max(0, _currentHp - hpConsume), false);

		double mpConsume2 = skill.getMpConsume2();
		if(mpConsume2 > 0)
		{
			if(skill.isMusic())
			{
				double inc = mpConsume2 / 2;
				double add = 0;
				for(Effect e : getEffectList().getEffects())
				{
					if(e.getSkill().getId() != skill.getId() && e.getSkill().isMusic() && e.getTimeLeft() > 30)
						add += inc;
				}
				mpConsume2 += add;
				mpConsume2 = calcStat(Stats.MP_DANCE_SKILL_CONSUME, mpConsume2, aimingTarget, skill);
			}
			else if(skill.isMagic())
				mpConsume2 = calcStat(Stats.MP_MAGIC_SKILL_CONSUME, mpConsume2, aimingTarget, skill);
			else
				mpConsume2 = calcStat(Stats.MP_PHYSICAL_SKILL_CONSUME, mpConsume2, aimingTarget, skill);

			if(_currentMp < mpConsume2 && isPlayable())
			{
				sendPacket(SystemMsg.NOT_ENOUGH_MP);
				onCastEndTime(dual, false);
				return;
			}
			reduceCurrentMp(mpConsume2, null);
		}

		callSkill(skill, targets, true);

		if(skill.getNumCharges() > 0)
			setIncreasedForce(getIncreasedForce() - skill.getNumCharges());

		if(skill.getCondCharges() > 0 && getIncreasedForce() > 0)
		{
			int decreasedForce = skill.getCondCharges();
			if(decreasedForce > 15)
				decreasedForce = 5;
			setIncreasedForce(getIncreasedForce() - decreasedForce);
		}

		if(skill.isSoulBoost())
			setConsumedSouls(getConsumedSouls() - Math.min(getConsumedSouls(), 5), null);
		else if(skill.getSoulsConsume() > 0)
			setConsumedSouls(getConsumedSouls() - skill.getSoulsConsume(), null);

		if(dual)
		{
			if(_scheduledDualCastCount > 0)
			{
				_scheduledDualCastCount--;
				_skillDualLaunchedTask = ThreadPoolManager.getInstance().schedule(new MagicLaunchedTask(this, forceUse, true), _scheduledDualCastInterval);
				_skillDualTask = ThreadPoolManager.getInstance().schedule(new MagicUseTask(this, forceUse, true), _scheduledDualCastInterval);
				return;
			}
		}
		else
		{
			if(_scheduledCastCount > 0)
			{
				_scheduledCastCount--;
				_skillLaunchedTask = ThreadPoolManager.getInstance().schedule(new MagicLaunchedTask(this, forceUse, false), _scheduledCastInterval);
				_skillTask = ThreadPoolManager.getInstance().schedule(new MagicUseTask(this, forceUse, false), _scheduledCastInterval);
				return;
			}
		}

		int skillCoolTime = Formulas.calcSkillCastSpd(this, skill, skill.getCoolTime());
		if(skillCoolTime > 0)
			ThreadPoolManager.getInstance().schedule(new CastEndTimeTask(this, dual), skillCoolTime);
		else
			onCastEndTime(dual, true);
	}

	public void onCastEndTime(boolean dual, boolean success)
	{
		final Skill castingSkill = dual ? _dualCastingSkill : _castingSkill;

		finishFly(dual);

		int skillId = 0;
		try // TODO: [Bonux] Пересмотреть почему castingSkill выдает НПЕ.
		{
			if(castingSkill != null && castingSkill.getId() != 0)
			{
				skillId = castingSkill.getId();
				if(success)
					useTriggers(getTarget(), TriggerType.ON_SUCCES_FINISH_CAST, null, castingSkill, 0);
			}
		}
		
		catch(Exception e)
		{
			//
		}

		clearCastVars(dual);
		getAI().notifyEvent(CtrlEvent.EVT_FINISH_CASTING, skillId, success);
	}

	public void clearCastVars(boolean dual)
	{
		if(dual)
		{
			_dualAnimationEndTime = 0;
			_dualCastInterruptTime = 0;
			_scheduledDualCastCount = 0;
			_dualCastingSkill = null;
			_skillDualTask = null;
			_skillDualLaunchedTask = null;
			_dualFlyLoc = null;
		}
		else
		{
			_animationEndTime = 0;
			_castInterruptTime = 0;
			_scheduledCastCount = 0;
			_castingSkill = null;
			_skillTask = null;
			_skillLaunchedTask = null;
			_flyLoc = null;
		}
	}

	private void finishFly(boolean dual)
	{
		Location flyLoc;
		GameObject flyTarget;
		if(dual)
		{
			flyLoc = _dualFlyLoc;
			_dualFlyLoc = null;

			flyTarget = _dualFlyTarget;
			_dualFlyTarget = null;
		}
		else
		{
			flyLoc = _flyLoc;
			_flyLoc = null;

			flyTarget = _flyTarget;
			_flyTarget = null;
		}

		if(flyLoc != null)
		{
			setLoc(flyLoc);
			validateLocation(1);

			if(flyTarget != null)
			{
				setHeading(PositionUtils.calculateHeadingFrom(this, flyTarget));
				broadcastPacket(new ExRotation(getObjectId(), getHeading()));
			}
		}
	}

	public void reduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflectAndAbsorb, boolean transferDamage, boolean isDot, boolean sendReceiveMessage)
	{
		reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflectAndAbsorb, transferDamage, isDot, sendReceiveMessage, false, false, false, false, false);
	}

	public void reduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflectAndAbsorb, boolean transferDamage, boolean isDot, boolean sendReceiveMessage, boolean sendGiveMessage, boolean crit, boolean miss, boolean shld, boolean magic)
	{
		if(isImmortal())
			return;

		if(miss || damage <= 0)
			return;

		if(attacker == null || isDead() || (attacker.isDead() && !isDot))
			return;

		if(isDamageBlocked() && transferDamage)
			return;

		if(isDamageBlocked() && attacker != this)
		{
			if(attacker.isPlayer())
			{
				if(sendGiveMessage)
					attacker.sendPacket(SystemMsg.THE_ATTACK_HAS_BEEN_BLOCKED);
			}
			return; //return anyway, if damage is blocked it's blocked from everyone!!
		}

		double reflectedDamage = 0.;
		double transferedDamage = 0.;
		Servitor servitorForTransfereDamage = null;

		if(canReflectAndAbsorb)
		{
			reflectedDamage = reflectDamage(attacker, skill, damage);
			if(reflectedDamage == damage)
			{
				displayGiveDamageMessage(attacker, (int) reflectedDamage, null, 0, false, false, false, false);
				attacker.reduceCurrentHp(reflectedDamage, this, null, true, true, false, false, false, false, true);
				return;
			}

			if(canAbsorb(this, attacker))
			{
				attacker.absorbDamage(this, skill, damage);

				damage = absorbToEffector(attacker, damage);
				damage = absorbToMp(attacker, damage);

				transferedDamage = getDamageForTransferToServitor(damage);
				servitorForTransfereDamage = getServitorForTransfereDamage(transferedDamage);
				if(servitorForTransfereDamage != null)
					damage -= transferedDamage;
				else
					transferedDamage = 0.;
			}
		}

		// Damage can be limited by ultimate effects
		double damageLimit = -1;
		if(skill == null)
			damageLimit = calcStat(Stats.RECIEVE_DAMAGE_LIMIT, damage);
		else if(skill.isMagic())
			damageLimit = calcStat(Stats.RECIEVE_DAMAGE_LIMIT_M_SKILL, damage);
		else
			damageLimit = calcStat(Stats.RECIEVE_DAMAGE_LIMIT_P_SKILL, damage);

		if(damageLimit >= 0. && damage > damageLimit)
			damage = damageLimit;

		if(isDeathImmune())
			damage = Math.min(damage, Math.floor(getCurrentHp() - 10));

		getListeners().onCurrentHpDamage(damage, attacker, skill);

		if(attacker != this)
		{
			if(sendGiveMessage)
				attacker.displayGiveDamageMessage(this, (int) damage, servitorForTransfereDamage, (int) transferedDamage, crit, miss, shld, magic);

			if(sendReceiveMessage)
				displayReceiveDamageMessage(attacker, (int) damage);

			if(!isDot)
				useTriggers(attacker, TriggerType.RECEIVE_DAMAGE, null, null, damage);
		}

		if(servitorForTransfereDamage != null && transferedDamage > 0)
			servitorForTransfereDamage.reduceCurrentHp(transferedDamage, attacker, null, false, false, false, false, true, false, true);

		onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp);

		if(reflectedDamage > 0.)
		{
			displayGiveDamageMessage(attacker, (int) reflectedDamage, null, 0, false, false, false, false);
			attacker.reduceCurrentHp(reflectedDamage, this, null, true, true, false, false, false, false, true);
		}

		if(attacker.isPlayer())
		{
			WorldStatisticsManager.getInstance().updateStat(attacker.getPlayer(), CategoryType.DAMAGE_TO_MONSTERS, attacker.getPlayer().getClassId().getId(), (long) damage);
			WorldStatisticsManager.getInstance().updateStat(attacker.getPlayer(), CategoryType.DAMAGE_TO_MONSTERS_MAX, attacker.getPlayer().getClassId().getId(), (long) damage);
		}
	}

	protected void onReduceCurrentHp(final double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp)
	{
		for(Effect effect : getEffectList().getEffects())
		{
			if(effect.getSkill().isDispelOnDamage())
				effect.exit();
		}

		if(awake && isSleeping())
			getEffectList().stopEffects(EffectType.Sleep);

		if(attacker != this || (skill != null && skill.isOffensive()))
		{
			if(isMeditated())
				getEffectList().stopEffects(EffectType.Meditation);

			startAttackStanceTask();
			checkAndRemoveInvisible();

			if(getCurrentHp() - damage < 0.5)
				useTriggers(attacker, TriggerType.DIE, null, null, damage);
		}

		// GM undying mode
		if(isPlayer() && getPlayer().isGM() && getPlayer().isUndying() && damage + 0.5 >= getCurrentHp())
			return;

		setCurrentHp(Math.max(getCurrentHp() - damage, 0), false);

		if(getCurrentHp() < 0.5)
			doDie(attacker);
	}

	public void reduceCurrentMp(double i, Creature attacker)
	{
		if(attacker != null && attacker != this)
		{
			if(isSleeping())
				getEffectList().stopEffects(EffectType.Sleep);

			if(isMeditated())
				getEffectList().stopEffects(EffectType.Meditation);
		}

		if(isDamageBlocked() && attacker != null && attacker != this)
		{
			attacker.sendPacket(SystemMsg.THE_ATTACK_HAS_BEEN_BLOCKED);
			return;
		}

		// 5182 = Blessing of protection, работает если разница уровней больше 10 и не в зоне осады
		if(attacker != null && attacker.isPlayer() && Math.abs(attacker.getLevel() - getLevel()) > 10)
		{
			// ПК не может нанести урон чару с блессингом
			if(attacker.isPK() && getEffectList().containsEffects(5182) && !isInZone(ZoneType.SIEGE))
				return;
			// чар с блессингом не может нанести урон ПК
			if(isPK() && attacker.getEffectList().containsEffects(5182) && !attacker.isInZone(ZoneType.SIEGE))
				return;
		}

		i = _currentMp - i;

		if(i < 0)
			i = 0;

		setCurrentMp(i);

		if(attacker != null && attacker != this)
			startAttackStanceTask();
	}

	public void removeAllSkills()
	{
		for(Skill s : getAllSkillsArray())
		{
			if(s.getId() == 9256)
				continue; //maybe there is a better place for it? acc to GOD GD this skill not being removed while switching between subclasses.
			removeSkill(s);
		}
	}

	public Skill removeSkill(Skill skill)
	{
		if(skill == null)
			return null;
		return removeSkillById(skill.getId());
	}

	public Skill removeSkillById(Integer id)
	{
		// Remove the skill from the L2Character _skills
		Skill oldSkill = _skills.remove(id);

		// Remove all its Func objects from the L2Character calculator set
		if(oldSkill != null)
		{
			removeDisabledAnalogSkills(oldSkill);
			removeTriggers(oldSkill);
			removeStatsOwner(oldSkill);
			if(Config.ALT_DELETE_SA_BUFFS && (oldSkill.isItemSkill() || oldSkill.isHandler()))
			{
				// Завершаем все эффекты, принадлежащие старому скиллу
				getEffectList().stopEffects(oldSkill);

				// И с петов тоже
				Servitor[] servitors = getServitors();
				if(servitors.length > 0)
				{
					for(Servitor servitor : servitors)
						servitor.getEffectList().stopEffects(oldSkill);
				}
			}

			AINextAction nextAction = getAI().getNextAction();
			if(nextAction != null && nextAction == AINextAction.CAST)
			{
				Object args1 = getAI().getNextActionArgs()[0];
				if(args1 == oldSkill)
					getAI().clearNextAction();
			}
		}

		return oldSkill;
	}

	public void addTriggers(StatTemplate f)
	{
		if(f.getTriggerList().isEmpty())
			return;

		for(TriggerInfo t : f.getTriggerList())
		{
			addTrigger(t);
		}
	}

	public void addTrigger(TriggerInfo t)
	{
		if(_triggers == null)
			_triggers = new ConcurrentHashMap<TriggerType, Set<TriggerInfo>>();

		Set<TriggerInfo> hs = _triggers.get(t.getType());
		if(hs == null)
		{
			hs = new CopyOnWriteArraySet<TriggerInfo>();
			_triggers.put(t.getType(), hs);
		}

		hs.add(t);

		if(t.getType() == TriggerType.ADD)
			useTriggerSkill(this, null, t, null, 0);
		else if(t.getType() == TriggerType.IDLE)
			new RunnableTrigger(this, t).schedule();
	}

	public Map<TriggerType, Set<TriggerInfo>> getTriggers()
	{
		return _triggers;
	}

	public void removeTriggers(StatTemplate f)
	{
		if(_triggers == null || f.getTriggerList().isEmpty())
			return;

		for(TriggerInfo t : f.getTriggerList())
			removeTrigger(t);
	}

	public void removeTrigger(TriggerInfo t)
	{
		if(_triggers == null)
			return;
		Set<TriggerInfo> hs = _triggers.get(t.getType());
		if(hs == null)
			return;
		hs.remove(t);

		if(t.cancelEffectsOnRemove())
			triggerCancelEffects(t);
	}

	public void sendActionFailed()
	{
		sendPacket(ActionFailPacket.STATIC);
	}

	public boolean hasAI()
	{
		return _ai != null;
	}

	public CharacterAI getAI()
	{
		if(_ai == null)
			synchronized (this)
			{
				if(_ai == null)
					_ai = new CharacterAI(this);
			}

		return _ai;
	}

	public void setAI(CharacterAI newAI)
	{
		if(newAI == null)
			return;

		CharacterAI oldAI = _ai;

		synchronized (this)
		{
			_ai = newAI;
		}

		if(oldAI != null)
		{
			if(oldAI.isActive())
			{
				oldAI.stopAITask();
				newAI.startAITask();
				newAI.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			}
		}
	}

	public final void setCurrentHp(double newHp, boolean canRessurect, boolean sendInfo)
	{
		int maxHp = getMaxHp();

		newHp = Math.min(maxHp, Math.max(0, newHp));

		if(_currentHp == newHp)
			return;

		if(newHp >= 0.5 && isDead() && !canRessurect)
			return;

		double hpStart = _currentHp;

		_currentHp = newHp;

		if(isDead.compareAndSet(true, false))
			onRevive();

		checkHpMessages(hpStart, _currentHp);

		if(sendInfo)
		{
			broadcastStatusUpdate();
			sendChanges();
		}

		if(_currentHp < maxHp)
			startRegeneration();
	}

	public final void setCurrentHp(double newHp, boolean canRessurect)
	{
		setCurrentHp(newHp, canRessurect, true);
	}

	public final void setCurrentMp(double newMp, boolean sendInfo)
	{
		int maxMp = getMaxMp();

		newMp = Math.min(maxMp, Math.max(0, newMp));

		if(_currentMp == newMp)
			return;

		if(newMp >= 0.5 && isDead())
			return;

		_currentMp = newMp;

		if(sendInfo)
		{
			broadcastStatusUpdate();
			sendChanges();
		}

		if(_currentMp < maxMp)
			startRegeneration();
	}

	public final void setCurrentMp(double newMp)
	{
		setCurrentMp(newMp, true);
	}

	public final void setCurrentCp(double newCp, boolean sendInfo)
	{
		if(!isPlayer())
			return;

		int maxCp = getMaxCp();
		newCp = Math.min(maxCp, Math.max(0, newCp));

		if(_currentCp == newCp)
			return;

		if(newCp >= 0.5 && isDead())
			return;

		_currentCp = newCp;

		if(sendInfo)
		{
			broadcastStatusUpdate();
			sendChanges();
		}

		if(_currentCp < maxCp)
			startRegeneration();
	}

	public final void setCurrentCp(double newCp)
	{
		setCurrentCp(newCp, true);
	}

	public void setCurrentHpMp(double newHp, double newMp, boolean canRessurect)
	{
		int maxHp = getMaxHp();
		int maxMp = getMaxMp();

		newHp = Math.min(maxHp, Math.max(0, newHp));
		newMp = Math.min(maxMp, Math.max(0, newMp));

		if(_currentHp == newHp && _currentMp == newMp)
			return;

		if(newHp >= 0.5 && isDead() && !canRessurect)
			return;

		double hpStart = _currentHp;

		_currentHp = newHp;
		_currentMp = newMp;

		if(isDead.compareAndSet(true, false))
			onRevive();

		checkHpMessages(hpStart, _currentHp);

		broadcastStatusUpdate();
		sendChanges();

		if(_currentHp < maxHp || _currentMp < maxMp)
			startRegeneration();
	}

	public void setCurrentHpMp(double newHp, double newMp)
	{
		setCurrentHpMp(newHp, newMp, false);
	}

	public final void setFlying(boolean mode)
	{
		_flying = mode;
	}

	@Override
	public final int getHeading()
	{
		return _heading;
	}

	public final void setHeading(int heading)
	{
		setHeading(heading, false);
	}

	public final void setHeading(int heading, boolean broadcast)
	{
		_heading = heading;
		if(broadcast)
			broadcastPacket(new ExRotation(getObjectId(), heading));
	}

	public final void setIsTeleporting(boolean value)
	{
		isTeleporting.compareAndSet(!value, value);
	}

	public final void setName(String name)
	{
		_name = name;
	}

	public Creature getCastingTarget()
	{
		return castingTarget.get();
	}

	public Creature getDualCastingTarget()
	{
		return dualCastingTarget.get();
	}

	public void setCastingTarget(Creature target)
	{
		if(target == null)
			castingTarget = HardReferences.emptyRef();
		else
			castingTarget = target.getRef();
	}

	public void setDualCastingTarget(Creature target)
	{
		if(target == null)
			dualCastingTarget = HardReferences.emptyRef();
		else
			dualCastingTarget = target.getRef();
	}

	public final void setRunning()
	{
		if(!_running)
		{
			_running = true;
			broadcastPacket(new ChangeMoveTypePacket(this));
			if(isNpc() && !isDead())
				broadcastPacket(new NpcInfoState(getObjectId(), 4));
		}
	}

	public void setSkillMastery(Integer skill, int mastery)
	{
		if(_skillMastery == null)
			_skillMastery = new HashMap<Integer, Integer>();
		_skillMastery.put(skill, mastery);
	}

	public void setAggressionTarget(Creature target)
	{
		if(target == null)
			_aggressionTarget = HardReferences.emptyRef();
		else
			_aggressionTarget = target.getRef();
	}

	public Creature getAggressionTarget()
	{
		return _aggressionTarget.get();
	}

	public void setTarget(GameObject object)
	{
		if(object != null && !object.isVisible())
			object = null;

		/* DS: на оффе сброс текущей цели не отменяет атаку или каст.
		if(object == null)
		{
			if(isAttackingNow() && getAI().getAttackTarget() == getTarget())
				abortAttack(false, true);
			if(isCastingNow() && getAI().getCastTarget() == getTarget())
				abortCast(false, true);
		}
		*/

		if(object == null)
			target = HardReferences.emptyRef();
		else
			target = object.getRef();
	}

	public void setTitle(String title)
	{
		_title = title;
	}

	public void setWalking()
	{
		if(_running)
		{
			_running = false;
			broadcastPacket(new ChangeMoveTypePacket(this));
			if(isNpc())
				broadcastPacket(new NpcInfoState(getObjectId(), 0));
		}
	}

	public final void startAbnormalEffect(AbnormalEffect ae)
	{
		if(ae == AbnormalEffect.NONE)
			_abnormalEffects.clear();
		else
			_abnormalEffects.add(ae);
		sendChanges();
	}

	public void startAttackStanceTask()
	{
		startAttackStanceTask0();
	}

	/**
	 * Запускаем задачу анимации боевой позы. Если задача уже запущена, увеличиваем время, которое персонаж будет в боевой позе на 15с
	 */
	protected void startAttackStanceTask0()
	{
		// предыдущая задача еще не закончена, увеличиваем время
		if(isInCombat())
		{
			_stanceEndTime = System.currentTimeMillis() + 15000L;
			return;
		}

		_stanceEndTime = System.currentTimeMillis() + 15000L;

		broadcastPacket(new AutoAttackStartPacket(getObjectId()));

		// отменяем предыдущую
		final Future<?> task = _stanceTask;
		if(task != null)
			task.cancel(false);

		// Добавляем задачу, которая будет проверять, если истекло время нахождения персонажа в боевой позе,
		// отменяет задачу и останаливает анимацию.
		_stanceTask = LazyPrecisionTaskManager.getInstance().scheduleAtFixedRate(_stanceTaskRunnable == null ? _stanceTaskRunnable = new AttackStanceTask() : _stanceTaskRunnable, 1000L, 1000L);
	}

	/**
	 * Останавливаем задачу анимации боевой позы.
	 */
	public void stopAttackStanceTask()
	{
		_stanceEndTime = 0L;

		final Future<?> task = _stanceTask;
		if(task != null)
		{
			task.cancel(false);
			_stanceTask = null;

			broadcastPacket(new AutoAttackStopPacket(getObjectId()));
		}
	}

	private class AttackStanceTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			if(!isInCombat())
				stopAttackStanceTask();
		}
	}

	/**
	 * Остановить регенерацию
	 */
	protected void stopRegeneration()
	{
		regenLock.lock();
		try
		{
			if(_isRegenerating)
			{
				_isRegenerating = false;

				if(_regenTask != null)
				{
					_regenTask.cancel(false);
					_regenTask = null;
				}
			}
		}
		finally
		{
			regenLock.unlock();
		}
	}

	/**
	 * Запустить регенерацию
	 */
	protected void startRegeneration()
	{
		if(!isVisible() || isDead() || getRegenTick() == 0L)
			return;

		if(_isRegenerating)
			return;

		regenLock.lock();
		try
		{
			if(!_isRegenerating)
			{
				_isRegenerating = true;
				_regenTask = RegenTaskManager.getInstance().scheduleAtFixedRate(_regenTaskRunnable == null ? _regenTaskRunnable = new RegenTask() : _regenTaskRunnable, 0, getRegenTick());
			}
		}
		finally
		{
			regenLock.unlock();
		}
	}

	public long getRegenTick()
	{
		return 3000L;
	}

	private class RegenTask implements Runnable
	{
		@Override
		public void run()
		{
			if(isAlikeDead() || getRegenTick() == 0L)
				return;

			double hpStart = _currentHp;

			int maxHp = getMaxHp();
			int maxMp = getMaxMp();
			int maxCp = isPlayer() ? getMaxCp() : 0;

			double addHp = 0.;
			double addMp = 0.;

			regenLock.lock();
			try
			{
				if(_currentHp < maxHp)
					addHp += getHpRegen();

				if(_currentMp < maxMp)
					addMp += getMpRegen();

				// Added regen bonus when character is sitting
				if(isPlayer() && Config.REGEN_SIT_WAIT)
				{
					Player pl = (Player) Creature.this;
					if(pl.isSitting())
					{
						pl.updateWaitSitTime();
						if(pl.getWaitSitTime() > 5)
						{
							addHp += pl.getWaitSitTime();
							addMp += pl.getWaitSitTime();
						}
					}
				}
				else if(isRaid())
				{
					addHp *= Config.RATE_RAID_REGEN;
					addMp *= Config.RATE_RAID_REGEN;
				}

				_currentHp += Math.max(0, Math.min(addHp, calcStat(Stats.HP_LIMIT, null, null) * maxHp / 100. - _currentHp));
				_currentMp += Math.max(0, Math.min(addMp, calcStat(Stats.MP_LIMIT, null, null) * maxMp / 100. - _currentMp));

				_currentHp = Math.min(maxHp, _currentHp);
				_currentMp = Math.min(maxMp, _currentMp);

				if(isPlayer())
				{
					_currentCp += Math.max(0, Math.min(getCpRegen(), calcStat(Stats.CP_LIMIT, null, null) * maxCp / 100. - _currentCp));
					_currentCp = Math.min(maxCp, _currentCp);
				}

				//отрегенились, останавливаем задачу
				if(_currentHp == maxHp && _currentMp == maxMp && _currentCp == maxCp)
					stopRegeneration();
			}
			finally
			{
				regenLock.unlock();
			}

			broadcastStatusUpdate();
			sendChanges();

			checkHpMessages(hpStart, _currentHp);
		}
	}

	public final void stopAbnormalEffect(AbnormalEffect ae)
	{
		_abnormalEffects.remove(ae);
		sendChanges();
	}

	/**
	 * Блокируем персонажа
	 */
	public void block()
	{
		_blocked = true;
	}

	/**
	 * Разблокируем персонажа
	 */
	public void unblock()
	{
		_blocked = false;
	}

	/**
	 *
	 * @return предыдущее состояние
	 */
	public boolean startConfused()
	{
		return _confused.getAndSet(true);
	}

	/**
	 *
	 * @return текущее состояние
	 */
	public boolean stopConfused()
	{
		return _confused.setAndGet(false);
	}

	/**
	 *
	 * @return предыдущее состояние
	 */
	public boolean startFear()
	{
		return _afraid.getAndSet(true);
	}

	/**
	 *
	 * @return текущее состояние
	 */
	public boolean stopFear()
	{
		return _afraid.setAndGet(false);
	}

	/**
	 *
	 * @return предыдущее состояние
	 */
	public boolean startMuted()
	{
		return _muted.getAndSet(true);
	}

	/**
	 *
	 * @return текущее состояние
	 */
	public boolean stopMuted()
	{
		return _muted.setAndGet(false);
	}

	/**
	 *
	 * @return предыдущее состояние
	 */
	public boolean startPMuted()
	{
		return _pmuted.getAndSet(true);
	}

	/**
	 *
	 * @return текущее состояние
	 */
	public boolean stopPMuted()
	{
		return _pmuted.setAndGet(false);
	}

	/**
	 *
	 * @return предыдущее состояние
	 */
	public boolean startAMuted()
	{
		return _amuted.getAndSet(true);
	}

	/**
	 *
	 * @return текущее состояние
	 */
	public boolean stopAMuted()
	{
		return _amuted.setAndGet(false);
	}

	/**
	 *
	 * @return предыдущее состояние
	 */
	public boolean startRooted()
	{
		return _rooted.getAndSet(true);
	}

	/**
	 *
	 * @return текущее состояние
	 */
	public boolean stopRooted()
	{
		return _rooted.setAndGet(false);
	}

	/**
	 *
	 * @return предыдущее состояние
	 */
	public boolean startSleeping()
	{
		return _sleeping.getAndSet(true);
	}

	/**
	 *
	 * @return текущее состояние
	 */
	public boolean stopSleeping()
	{
		return _sleeping.setAndGet(false);
	}

	/**
	 *
	 * @return предыдущее состояние
	 */
	public boolean startStunning()
	{
		return _stunned.getAndSet(true);
	}

	/**
	 *
	 * @return текущее состояние
	 */
	public boolean stopStunning()
	{
		return _stunned.setAndGet(false);
	}

	/**
	 *
	 * @return предыдущее состояние
	 */
	public boolean startParalyzed()
	{
		return _paralyzed.getAndSet(true);
	}

	/**
	 *
	 * @return текущее состояние
	 */
	public boolean stopParalyzed()
	{
		return _paralyzed.setAndGet(false);
	}

	/**
	 *
	 * @return предыдущее состояние
	 */
	public boolean startImmobilized()
	{
		return _immobilized.getAndSet(true);
	}

	/**
	 *
	 * @return текущее состояние
	 */
	public boolean stopImmobilized()
	{
		return _immobilized.setAndGet(false);
	}

	/**
	 *
	 * @return предыдущее состояние
	 */
	public boolean startHealBlocked()
	{
		return _healBlocked.getAndSet(true);
	}

	/**
	 *
	 * @return текущее состояние
	 */
	public boolean stopHealBlocked()
	{
		return _healBlocked.setAndGet(false);
	}

	/**
	 *
	 * @return предыдущее состояние
	 */
	public boolean startDamageBlocked()
	{
		return _damageBlocked.getAndSet(true);
	}

	/**
	 *
	 * @return текущее состояние
	 */
	public boolean stopDamageBlocked()
	{
		return _damageBlocked.setAndGet(false);
	}

	/**
	 *
	 * @return предыдущее состояние
	 */
	public boolean startBuffImmunity()
	{
		return _buffImmunity.getAndSet(true);
	}

	/**
	 *
	 * @return текущее состояние
	 */
	public boolean stopBuffImmunity()
	{
		return _buffImmunity.setAndGet(false);
	}

	/**
	 *
	 * @return предыдущее состояние
	 */
	public boolean startDebuffImmunity()
	{
		return _debuffImmunity.getAndSet(true);
	}

	/**
	 *
	 * @return текущее состояние
	 */
	public boolean stopDebuffImmunity()
	{
		return _debuffImmunity.setAndGet(false);
	}

	/**
	 *
	 * @return предыдущее состояние
	 */
	public boolean startEffectImmunity()
	{
		return _effectImmunity.getAndSet(true);
	}

	/**
	 *
	 * @return текущее состояние
	 */
	public boolean stopEffectImmunity()
	{
		return _effectImmunity.setAndGet(false);
	}

	/**
	 *
	 * @return текущее состояние
	 */
	public boolean startWeaponEquipBlocked()
	{
		return _weaponEquipBlocked.getAndSet(true);
	}

	/**
	 *
	 * @return текущее состояние
	 */
	public boolean stopWeaponEquipBlocked()
	{
		return _weaponEquipBlocked.getAndSet(false);
	}

	public boolean startFrozen()
	{
		return _frozen.getAndSet(true);
	}

	public boolean stopFrozen()
	{
		return _frozen.setAndGet(false);
	}

	public void setFakeDeath(boolean value)
	{
		_fakeDeath = value;
	}

	public void breakFakeDeath()
	{
		getEffectList().stopEffects(EffectType.FakeDeath);
	}

	public void setMeditated(boolean value)
	{
		_meditated = value;
	}

	public final void setIsBlessedByNoblesse(boolean value)
	{
		_isBlessedByNoblesse = value;
	}

	public final void setIsSalvation(boolean value)
	{
		_isSalvation = value;
	}

	public void setIsInvul(boolean value)
	{
		_isInvul = value;
	}

	public void setLockedTarget(boolean value)
	{
		_lockedTarget = value;
	}

	public boolean isConfused()
	{
		return _confused.get();
	}

	public boolean isFakeDeath()
	{
		return _fakeDeath;
	}

	public boolean isAfraid()
	{
		return _afraid.get();
	}

	public boolean isBlocked()
	{
		return _blocked;
	}

	public boolean isMuted(Skill skill)
	{
		if(skill == null || skill.isNotAffectedByMute())
			return false;
		return isMMuted() && skill.isMagic() || isPMuted() && !skill.isMagic();
	}

	public boolean isPMuted()
	{
		return _pmuted.get();
	}

	public boolean isMMuted()
	{
		return _muted.get();
	}

	public boolean isAMuted()
	{
		return _amuted.get() || isTransformed() && !getTransform().getType().isCanAttack();
	}

	public boolean isRooted()
	{
		return _rooted.get();
	}

	public boolean isSleeping()
	{
		return _sleeping.get();
	}

	public boolean isStunned()
	{
		return _stunned.get();
	}

	public boolean isMeditated()
	{
		return _meditated;
	}

	public boolean isWeaponEquipBlocked()
	{
		return _weaponEquipBlocked.get();
	}

	public boolean isParalyzed()
	{
		return _paralyzed.get();
	}

	public boolean isFrozen()
	{
		return _frozen.get();
	}

	public boolean isImmobilized()
	{
		return _immobilized.get() || getRunSpeed() < 1;
	}

	public boolean isHealBlocked()
	{
		return isAlikeDead() || _healBlocked.get();
	}

	public boolean isDamageBlocked()
	{
		return isInvul() || _damageBlocked.get();
	}

	public boolean isCastingNow()
	{
		return _skillTask != null;
	}

	public boolean isDualCastingNow()
	{
		return _skillDualTask != null;
	}
	
	public boolean isLockedTarget()
	{
		return _lockedTarget;
	}

	public boolean isMovementDisabled()
	{
		return isBlocked() || isRooted() || isImmobilized() || isAlikeDead() || isStunned() || isSleeping() || isDecontrolled() || isAttackingNow() || isCastingNow() || isDualCastingNow() || isFrozen();
	}

	public final boolean isActionsDisabled()
	{
		return isActionsDisabled(true);
	}

	public boolean isActionsDisabled(boolean withCast)
	{
		return isBlocked() || isAlikeDead() || isStunned() || isSleeping() || isDecontrolled() || isAttackingNow() || withCast && (isCastingNow() || isDualCastingNow()) || isFrozen();
	}

	public final boolean isDecontrolled()
	{
		return isParalyzed() || isKnockDowned() || isKnockBacked() || isFlyUp();
	}

	public final boolean isAttackingDisabled()
	{
		return _attackReuseEndTime > System.currentTimeMillis();
	}

	public boolean isOutOfControl()
	{
		return isBlocked() || isConfused() || isAfraid();
	}

	public void teleToLocation(Location loc)
	{
		teleToLocation(loc.x, loc.y, loc.z, getReflection());
	}

	public void teleToLocation(Location loc, int refId)
	{
		teleToLocation(loc.x, loc.y, loc.z, refId);
	}

	public void teleToLocation(Location loc, Reflection r)
	{
		teleToLocation(loc.x, loc.y, loc.z, r);
	}

	public void teleToLocation(int x, int y, int z)
	{
		teleToLocation(x, y, z, getReflection());
	}

	public void checkAndRemoveInvisible()
	{
		InvisibleType invisibleType = getInvisibleType();
		if(invisibleType == InvisibleType.EFFECT)
			getEffectList().stopEffects(EffectType.Invisible);
	}

	public void teleToLocation(int x, int y, int z, int refId)
	{
		Reflection r = ReflectionManager.getInstance().get(refId);
		if(r == null)
			return;
		teleToLocation(x, y, z, r);
	}

	public void teleToLocation(int x, int y, int z, Reflection r)
	{
		if(!isTeleporting.compareAndSet(false, true))
			return;

		if(isFakeDeath())
			breakFakeDeath();

		abortCast(true, false);
		if(!isLockedTarget())
			setTarget(null);
		stopMove();

		if(!isBoat() && !isFlying() && !World.isWater(new Location(x, y, z), r))
			z = GeoEngine.getHeight(x, y, z, r.getGeoIndex());

		//TODO [G1ta0] убрать DimensionalRiftManager.teleToLocation
		if(isPlayer() && DimensionalRiftManager.getInstance().checkIfInRiftZone(getLoc(), true))
		{
			Player player = (Player) this;
			if(player.isInParty() && player.getParty().isInDimensionalRift())
			{
				Location newCoords = DimensionalRiftManager.getInstance().getRoom(0, 0).getTeleportCoords();
				x = newCoords.x;
				y = newCoords.y;
				z = newCoords.z;
				player.getParty().getDimensionalRift().usedTeleport(player);
			}
		}

		//TODO: [Bonux] Check ExTeleportToLocationActivate!
		if(isPlayer())
		{
			Player player = (Player) this;

			sendPacket(new TeleportToLocationPacket(this, x, y, z));

			player.getListeners().onTeleport(x, y, z, r);

			decayMe();

			setXYZ(x, y, z);

			setReflection(r);

			// Нужно при телепорте с более высокой точки на более низкую, иначе наносится вред от "падения"
			player.setLastClientPosition(null);
			player.setLastServerPosition(null);

			sendPacket(new ExTeleportToLocationActivate(this, x, y, z));
		}
		else
		{
			broadcastPacket(new TeleportToLocationPacket(this, x, y, z));

			setXYZ(x, y, z);

			setReflection(r);

			sendPacket(new ExTeleportToLocationActivate(this, x, y, z));

			onTeleported();
		}
	}

	public boolean onTeleported()
	{
		return isTeleporting.compareAndSet(true, false);
	}

	public void sendMessage(CustomMessage message)
	{

	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + "[" + getObjectId() + "]";
	}

	@Override
	public double getCollisionRadius()
	{
		return getBaseStats().getCollisionRadius();
	}

	@Override
	public double getCollisionHeight()
	{
		return getBaseStats().getCollisionHeight();
	}

	public double getCurrentCollisionRadius()
	{
		return getCollisionRadius();
	}

	public double getCurrentCollisionHeight()
	{
		return getCollisionHeight();
	}

	public EffectList getEffectList()
	{
		if(_effectList == null)
		{
			synchronized (this)
			{
				if(_effectList == null)
					_effectList = new EffectList(this);
			}
		}

		return _effectList;
	}

	public boolean paralizeOnAttack(Creature attacker)
	{
		int max_attacker_level = 0xFFFF;

		MonsterInstance leader;
		if(isRaid() || isMinion() && (leader = ((MinionInstance) this).getLeader()) != null && leader.isRaid())
			max_attacker_level = getLevel() + Config.RAID_MAX_LEVEL_DIFF;
		else if(isNpc())
		{
			int max_level_diff = ((NpcInstance) this).getParameter("ParalizeOnAttack", -1000);
			if(max_level_diff != -1000)
				max_attacker_level = getLevel() + max_level_diff;
		}

		if(attacker.getLevel() > max_attacker_level)
			return true;

		return false;
	}

	@Override
	protected void onDelete()
	{
		GameObjectsStorage.remove(_storedId);

		getEffectList().stopAllEffects();

		super.onDelete();
	}

	// ---------------------------- Not Implemented -------------------------------

	public void addExpAndSp(long exp, long sp)
	{}

	public void broadcastCharInfo()
	{}

	public void broadcastCharInfoImpl()
	{}

	public void checkHpMessages(double currentHp, double newHp)
	{}

	public boolean checkPvP(Creature target, Skill skill)
	{
		return false;
	}

	public boolean consumeItem(int itemConsumeId, long itemCount)
	{
		return true;
	}

	public boolean consumeItemMp(int itemId, int mp)
	{
		return true;
	}

	public boolean isFearImmune()
	{
		return isPeaceNpc();
	}

	public boolean isThrowAndKnockImmune()
	{
		return isPeaceNpc();
	}

	public boolean isTransformImmune()
	{
		return isPeaceNpc();
	}

	public boolean isLethalImmune()
	{
		return isBoss() || isRaid();
	}

	public boolean getChargedSoulShot()
	{
		return false;
	}

	public int getChargedSpiritShot()
	{
		return 0;
	}

	public int getIncreasedForce()
	{
		return 0;
	}

	public int getConsumedSouls()
	{
		return 0;
	}

	public int getAgathionEnergy()
	{
		return 0;
	}

	public void setAgathionEnergy(int val)
	{
		//
	}

	public int getKarma()
	{
		return 0;
	}

	public boolean isPK()
	{
		return getKarma() < 0;
	}

	public double getLevelBonus()
	{
		return 1;
	}

	public int getNpcId()
	{
		return 0;
	}

	public boolean isMyServitor(int objId)
	{
		return false;
	}

	public Servitor[] getServitors()
	{
		return new Servitor[0];
	}

	public int getPvpFlag()
	{
		return 0;
	}

	public void setTeam(TeamType t)
	{
		_team = t;
		sendChanges();
	}

	public TeamType getTeam()
	{
		return _team;
	}

	public boolean isUndead()
	{
		return false;
	}

	public boolean isParalyzeImmune()
	{
		return false;
	}

	public void reduceArrowCount()
	{}

	public void sendChanges()
	{
		getStatsRecorder().sendChanges();
	}

	public void sendMessage(String message)
	{}

	public void sendPacket(IStaticPacket mov)
	{}

	public void sendPacket(IStaticPacket... mov)
	{}

	public void sendPacket(List<? extends IStaticPacket> mov)
	{}

	public void setIncreasedForce(int i)
	{}

	public void setConsumedSouls(int i, NpcInstance monster)
	{}

	public void startPvPFlag(Creature target)
	{}

	public boolean unChargeShots(boolean spirit)
	{
		return false;
	}

	private Future<?> _updateEffectIconsTask;

	private class UpdateEffectIcons extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			updateEffectIconsImpl();
			_updateEffectIconsTask = null;
		}
	}

	public void updateEffectIcons()
	{
		if(Config.USER_INFO_INTERVAL == 0)
		{
			if(_updateEffectIconsTask != null)
			{
				_updateEffectIconsTask.cancel(false);
				_updateEffectIconsTask = null;
			}
			updateEffectIconsImpl();
			return;
		}

		if(_updateEffectIconsTask != null)
			return;

		_updateEffectIconsTask = ThreadPoolManager.getInstance().schedule(new UpdateEffectIcons(), Config.USER_INFO_INTERVAL);
	}

	public void updateEffectIconsImpl()
	{
		broadcastAbnormalStatus(getAbnormalStatusUpdate());
	}

	public ExAbnormalStatusUpdateFromTargetPacket getAbnormalStatusUpdate()
	{
		Effect[] effects = getEffectList().getFirstEffects();
		Arrays.sort(effects, EffectsComparator.getInstance());

		ExAbnormalStatusUpdateFromTargetPacket abnormalStatus = new ExAbnormalStatusUpdateFromTargetPacket(getObjectId());
		for(Effect effect : effects)
		{
			if(!effect.checkAbnormalType(AbnormalType.hp_recover))
				effect.addIcon(abnormalStatus);
		}
		return abnormalStatus;
	}

	public void broadcastAbnormalStatus(ExAbnormalStatusUpdateFromTargetPacket packet)
	{
		if(getTarget() == this)
			sendPacket(packet);

		if(!isVisible())
			return;

		List<Player> players = World.getAroundPlayers(this);
		Player target;
		for(int i = 0; i < players.size(); i++)
		{
			target = players.get(i);
			if(target.getTarget() == this)
				target.sendPacket(packet);
		}
	}

	/**
	 * Выставить предельные значения HP/MP/CP и запустить регенерацию, если в этом есть необходимость
	 */
	protected void refreshHpMpCp()
	{
		final int maxHp = getMaxHp();
		final int maxMp = getMaxMp();
		final int maxCp = isPlayer() ? getMaxCp() : 0;

		if(_currentHp > maxHp)
			setCurrentHp(maxHp, false);
		if(_currentMp > maxMp)
			setCurrentMp(maxMp, false);
		if(_currentCp > maxCp)
			setCurrentCp(maxCp, false);

		if(_currentHp < maxHp || _currentMp < maxMp || _currentCp < maxCp)
			startRegeneration();
	}

	public void updateStats()
	{
		refreshHpMpCp();
		sendChanges();
	}

	public void setOverhitAttacker(Creature attacker)
	{}

	public void setOverhitDamage(double damage)
	{}

	public boolean isCursedWeaponEquipped()
	{
		return false;
	}

	public boolean isHero()
	{
		return false;
	}

	public int getAccessLevel()
	{
		return 0;
	}

	public Clan getClan()
	{
		return null;
	}

	public int getFormId()
	{
		return 0;
	}

	public boolean isNameAbove()
	{
		return true;
	}

	@Override
	public void setLoc(Location loc)
	{
		setXYZ(loc.x, loc.y, loc.z);
	}

	public void setLoc(Location loc, boolean MoveTask)
	{
		setXYZ(loc.x, loc.y, loc.z, MoveTask);
	}

	@Override
	public void setXYZ(int x, int y, int z)
	{
		setXYZ(x, y, z, false);
	}

	public void setXYZ(int x, int y, int z, boolean MoveTask)
	{
		if(!MoveTask)
			stopMove();

		moveLock.lock();
		try
		{
			super.setXYZ(x, y, z);
		}
		finally
		{
			moveLock.unlock();
		}

		updateZones();
	}

	@Override
	protected void onSpawn()
	{
		super.onSpawn();

		updateStats();
		updateZones();
	}

	@Override
	public void spawnMe(Location loc)
	{
		if(loc.h > 0)
			setHeading(loc.h);
		super.spawnMe(loc);
	}

	@Override
	protected void onDespawn()
	{
		if(!isLockedTarget())
			setTarget(null);
		stopMove();
		stopAttackStanceTask();
		stopRegeneration();

		updateZones();

		super.onDespawn();
	}

	public final void doDecay()
	{
		if(!isDead())
			return;

		onDecay();
	}

	protected void onDecay()
	{
		decayMe();
	}

	public void validateLocation(int broadcast)
	{
		L2GameServerPacket sp = new ValidateLocationPacket(this);
		if(broadcast == 0)
			sendPacket(sp);
		else if(broadcast == 1)
			broadcastPacket(sp);
		else
			broadcastPacketToOthers(sp);
	}

	// Функция для дизактивации умений персонажа (если умение не активно, то он не дает статтов и имеет серую иконку).
	private TIntSet _unActiveSkills = new TIntHashSet();

	public void addUnActiveSkill(Skill skill)
	{
		if(skill == null || isUnActiveSkill(skill.getId()))
			return;

		removeStatsOwner(skill);
		removeTriggers(skill);

		_unActiveSkills.add(skill.getId());
	}

	public void removeUnActiveSkill(Skill skill)
	{
		if(skill == null || !isUnActiveSkill(skill.getId()))
			return;

		addStatFuncs(skill.getStatFuncs());
		addTriggers(skill);

		_unActiveSkills.remove(skill.getId());
	}

	public boolean isUnActiveSkill(int id)
	{
		return _unActiveSkills.contains(id);
	}

	public abstract int getLevel();

	public abstract ItemInstance getActiveWeaponInstance();

	public abstract WeaponTemplate getActiveWeaponTemplate();

	public abstract ItemInstance getSecondaryWeaponInstance();

	public abstract WeaponTemplate getSecondaryWeaponTemplate();

	public CharListenerList getListeners()
	{
		if(listeners == null)
			synchronized (this)
			{
				if(listeners == null)
					listeners = new CharListenerList(this);
			}
		return listeners;
	}

	public <T extends Listener<Creature>> boolean addListener(T listener)
	{
		return getListeners().add(listener);
	}

	public <T extends Listener<Creature>> boolean removeListener(T listener)
	{
		return getListeners().remove(listener);
	}

	public CharStatsChangeRecorder<? extends Creature> getStatsRecorder()
	{
		if(_statsRecorder == null)
			synchronized (this)
			{
				if(_statsRecorder == null)
					_statsRecorder = new CharStatsChangeRecorder<Creature>(this);
			}

		return _statsRecorder;
	}

	@Override
	public boolean isCreature()
	{
		return true;
	}

	public void displayGiveDamageMessage(Creature target, int damage, Servitor servitorTransferedDamage, int transferedDamage, boolean crit, boolean miss, boolean shld, boolean magic)
	{
		if(miss && target.isPlayer() && !target.isDamageBlocked())
			target.sendPacket(new SystemMessage(SystemMessage.C1_HAS_EVADED_C2S_ATTACK).addName(target).addName(this));
	}

	public void displayReceiveDamageMessage(Creature attacker, int damage)
	{
		//
	}

	public Collection<TimeStamp> getSkillReuses()
	{
		return _skillReuses.values();
	}

	public TimeStamp getSkillReuse(Skill skill)
	{
		return _skillReuses.get(skill.getReuseHash());
	}

	public Sex getSex()
	{
		return Sex.MALE;
	}

	public final boolean isInFlyingTransform()
	{
		if(isTransformed())
			return getTransform().getType() == TransformType.FLYING;
		return false;
	}

	public final boolean isVisualTransformed()
	{
		return getVisualTransform() != null;
	}

	public final int getVisualTransformId()
	{
		if(getVisualTransform() != null)
			return getVisualTransform().getId();

		return 0;
	}

	public final TransformTemplate getVisualTransform()
	{
		if(_isInTransformUpdate)
			return null;

		if(_visualTransform != null)
			return _visualTransform;

		return getTransform();
	}

	public final void setVisualTransform(int id)
	{
		TransformTemplate template = id > 0 ? TransformTemplateHolder.getInstance().getTemplate(getSex(), id) : null;
		setVisualTransform(template);
	}

	public void setVisualTransform(TransformTemplate template)
	{
		if(_visualTransform == template)
			return;

		if(template != null && isVisualTransformed() || template == null && isTransformed())
		{
			_isInTransformUpdate = true;
			_visualTransform = null;

			sendChanges();

			_isInTransformUpdate = false;
		}

		_visualTransform = template;

		sendChanges();
	}

	public boolean isTransformed()
	{
		return false;
	}

	public final int getTransformId()
	{
		if(isTransformed())
			return getTransform().getId();

		return 0;
	}

	public TransformTemplate getTransform()
	{
		return null;
	}

	public void setTransform(int id)
	{
		//
	}

	public void setTransform(TransformTemplate template)
	{
		//
	}

	public boolean isDeathImmune()
	{
		return _deathImmunity.get() || isPeaceNpc();
	}

	public boolean startDeathImmunity()
	{
		return _deathImmunity.getAndSet(true);
	}

	public boolean stopDeathImmunity()
	{
		return _deathImmunity.setAndGet(false);
	}

	public int getMoveTickInterval()
	{
		return (isPlayer() ? 16000 : 32000) / Math.max(getMoveSpeed(), 1);
	}

	public final double getMovementSpeedMultiplier()
	{
		return getRunSpeed() * 1. / getBaseStats().getRunSpd();
	}

	@Override
	public int getMoveSpeed()
	{
		if(isRunning())
			return getRunSpeed();

		return getWalkSpeed();
	}

	public int getRunSpeed()
	{
		if(isInWater())
			return getSwimRunSpeed();

		return getSpeed(getBaseStats().getRunSpd());
	}

	public final int getWalkSpeed()
	{
		if(isInWater())
			return getSwimWalkSpeed();

		return getSpeed(getBaseStats().getWalkSpd());
	}

	public final int getSwimRunSpeed()
	{
		return getSpeed(getBaseStats().getWaterRunSpd());
	}

	public final int getSwimWalkSpeed()
	{
		return getSpeed(getBaseStats().getWaterWalkSpd());
	}

	public double relativeSpeed(GameObject target)
	{
		return getMoveSpeed() - target.getMoveSpeed() * Math.cos(headingToRadians(getHeading()) - headingToRadians(target.getHeading()));
	}

	public final int getSpeed(double baseSpeed)
	{
		return (int) calcStat(Stats.RUN_SPEED, baseSpeed, null, null);
	}

	public final double getHpRegen()
	{
		return calcStat(Stats.REGENERATE_HP_RATE, getBaseStats().getHpReg());
	}

	public final double getMpRegen()
	{
		return calcStat(Stats.REGENERATE_MP_RATE, getBaseStats().getMpReg());
	}

	public final double getCpRegen()
	{
		return calcStat(Stats.REGENERATE_CP_RATE, getBaseStats().getCpReg());
	}

	public int getEnchantEffect()
	{
		return 0;
	}

	public boolean isDisabledAnalogSkill(int skillId)
	{
		return false;
	}

	public void disableAnalogSkills(Skill skill)
	{
		//
	}

	public void removeDisabledAnalogSkills(Skill skill)
	{
		//
	}

	public final boolean isKnockDowned()
	{
		return _knockDowned.get();
	}

	public final boolean stopKnockDown()
	{
		return _knockDowned.setAndGet(false);
	}

	public final boolean startKnockDown()
	{
		return _knockDowned.getAndSet(true);
	}

	public final boolean isKnockBacked()
	{
		return _knockBacked.get();
	}

	public final boolean startKnockBack()
	{
		return _knockBacked.getAndSet(true);
	}

	public final boolean stopKnockBack()
	{
		return _knockBacked.setAndGet(false);
	}

	public final boolean isFlyUp()
	{
		return _flyUp.get();
	}

	public final boolean startFlyUp()
	{
		return _flyUp.getAndSet(true);
	}

	public final boolean stopFlyUp()
	{
		return _flyUp.setAndGet(false);
	}

	public void setRndCharges(int value)
	{
		_rndCharges = value;
	}

	public int getRndCharges()
	{
		return _rndCharges;
	}

	public boolean isPeaceNpc()
	{
		return false;
	}

	public void setDualCastEnable(boolean val)
	{
		_isDualCastEnable = val;
	}

	public boolean isDualCastEnable()
	{
		return _isDualCastEnable;
	}

	public boolean isInTvT()
	{
		return false;
	}

	public boolean isInCtF()
	{
		return false;
	}

	public boolean isInLastHero() 
	{
		return false;
	}

	public boolean isTargetable(Creature creature)
	{
		if(creature != null)
		{
			if(creature.isPlayer())
			{
				if(creature.getPlayer().isGM())
					return true;
			}
		}
		return _isTargetable;
	}

	public void setTargetable(boolean value)
	{
		_isTargetable = value;
	}
	
	private boolean checkRange(Creature caster, Creature target)
	{
		return caster.isInRange(target, Config.REFLECT_MIN_RANGE);
	}
	
	private boolean canAbsorb(Creature attacked, Creature attacker)
	{
		if(attacked.isPlayable() || !Config.DISABLE_VAMPIRIC_VS_MOB_ON_PVP)
			return true;
		return attacker.getPvpFlag() == 0;		
	}

	public CreatureBaseStats getBaseStats()
	{
		if(_baseStats == null)
			_baseStats = new CreatureBaseStats(this);
		return _baseStats;
	}

	public boolean isSpecialEffect(Skill skill)
	{
		return false;
	}

	// Аналог isInvul, но оно не блокирует атаку, а просто не отнимает ХП.
	public boolean isImmortal()
	{
		return false;
	}

	public boolean isChargeBlocked()
	{
		return true;
	}	
}