package l2s.gameserver.model.instances;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

import l2s.commons.collections.MultiValueSet;
import l2s.commons.lang.reference.HardReference;
import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.CharacterAI;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.dao.CustomStatsDAO;
import l2s.gameserver.dao.CustomStatsDAO.StatInfo;
import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.data.xml.holder.MultiSellHolder;
import l2s.gameserver.data.xml.holder.ResidenceHolder;
import l2s.gameserver.data.xml.holder.SkillAcquireHolder;
import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.idfactory.IdFactory;
import l2s.gameserver.instancemanager.DimensionalRiftManager;
import l2s.gameserver.instancemanager.QuestManager;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.listener.NpcListener;
import l2s.gameserver.model.AggroList;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObjectTasks.NotifyAITask;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.MinionList;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.SkillLearn;
import l2s.gameserver.model.Spawner;
import l2s.gameserver.model.Territory;
import l2s.gameserver.model.World;
import l2s.gameserver.model.Zone.ZoneType;
import l2s.gameserver.model.actor.listener.NpcListenerList;
import l2s.gameserver.model.actor.recorder.NpcStatsChangeRecorder;
import l2s.gameserver.model.base.AcquireType;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.MountType;
import l2s.gameserver.model.entity.DimensionalRift;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.entity.events.GlobalEvent;
import l2s.gameserver.model.entity.residence.Castle;
import l2s.gameserver.model.entity.residence.ClanHall;
import l2s.gameserver.model.entity.residence.Fortress;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.model.pledge.SubUnit;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestEventType;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.AcquireSkillDonePacket;
import l2s.gameserver.network.l2.s2c.AutoAttackStartPacket;
import l2s.gameserver.network.l2.s2c.ExAcquirableSkillListByClass;
import l2s.gameserver.network.l2.s2c.ExChangeNPCState;
import l2s.gameserver.network.l2.s2c.ExShowBaseAttributeCancelWindow;
import l2s.gameserver.network.l2.s2c.ExShowStatPage;
import l2s.gameserver.network.l2.s2c.ExShowVariationCancelWindow;
import l2s.gameserver.network.l2.s2c.ExShowVariationMakeWindow;
import l2s.gameserver.network.l2.s2c.L2GameServerPacket;
import l2s.gameserver.network.l2.s2c.NpcHtmlMessagePacket;
import l2s.gameserver.network.l2.s2c.NpcInfoPacket;
import l2s.gameserver.network.l2.s2c.RadarControlPacket;
import l2s.gameserver.network.l2.s2c.SocialActionPacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.scripts.Events;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.tables.ClanTable;
import l2s.gameserver.taskmanager.DecayTaskManager;
import l2s.gameserver.taskmanager.LazyPrecisionTaskManager;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.templates.TeleportLocation;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.item.WeaponTemplate;
import l2s.gameserver.templates.npc.Faction;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.templates.spawn.SpawnRange;
import l2s.gameserver.utils.HtmlUtils;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NpcInstance extends Creature
{
	private static final long serialVersionUID = 1L;

	public static final int BASE_CORPSE_TIME = 7;
	public static final String CORPSE_TIME = "corpse_time";

	public static final String NO_CHAT_WINDOW = "noChatWindow";
	public static final String NO_RANDOM_WALK = "noRandomWalk";
	public static final String NO_RANDOM_ANIMATION = "noRandomAnimation";
	public static final String TARGETABLE = "targetable";
	public static final String SHOW_NAME = "show_name";
	public static final String NO_SLEEP_MODE = "no_sleep_mode";
	public static final String IS_IMMORTAL = "is_immortal";

	private static final Logger _log = LoggerFactory.getLogger(NpcInstance.class);

	private int _personalAggroRange = -1;
	private int _level = 0;

	private long _dieTime = 0L;

	protected int _spawnAnimation = 2;

	private int _currentLHandId;
	private int _currentRHandId;

	private double _collisionHeightModifier = 1.0;
	private double _collisionRadiusModifier = 1.0;

	private int npcState = 0;

	protected boolean _hasRandomAnimation;
	protected boolean _hasRandomWalk;
	protected boolean _hasChatWindow;

	private Future<?> _decayTask;
	private Future<?> _animationTask;

	private AggroList _aggroList;

	private boolean _showName;

	private Castle _nearestCastle;
	private Fortress _nearestFortress;
	private ClanHall _nearestClanHall;

	private NpcString _nameNpcString = NpcString.NONE;
	private NpcString _titleNpcString = NpcString.NONE;

	private Spawner _spawn;
	private Location _spawnedLoc = new Location();
	private SpawnRange _spawnRange;

	private MultiValueSet<String> _parameters = StatsSet.EMPTY;

	private final int _enchantEffect;

	@SuppressWarnings("unused")
	private final boolean _isNoSleepMode;

	private final int _corpseTime;

	private final boolean _isImmortal;

	public NpcInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);

		if(template == null)
			throw new NullPointerException("No template for Npc. Please check your datapack is setup correctly.");

		setParameters(template.getAIParams());

		_hasRandomAnimation = !getParameter(NO_RANDOM_ANIMATION, false) && Config.MAX_NPC_ANIMATION > 0;
		_hasRandomWalk = !getParameter(NO_RANDOM_WALK, false);
		setHasChatWindow(!getParameter(NO_CHAT_WINDOW, false));
		setTargetable(getParameter(TARGETABLE, true));
		setShowName(getParameter(SHOW_NAME, true));

		_isImmortal = getParameter(IS_IMMORTAL, false);

		if(template.getSkills().size() > 0)
			for(TIntObjectIterator<Skill> iterator = template.getSkills().iterator(); iterator.hasNext();)
			{
				iterator.advance();
				addSkill(iterator.value());
			}

		setName(template.name);
		
		String customTitle = template.title;
		if(isMonster() && Config.ALT_SHOW_MONSTERS_LVL)
		{
			customTitle = "LvL: " + this.getLevel();
			if(Config.ALT_SHOW_MONSTERS_AGRESSION && this.isAggressive())
				customTitle += " A";
		}		
		setTitle(customTitle);

		// инициализация параметров оружия
		setLHandId(getTemplate().lhand);
		setRHandId(getTemplate().rhand);

		_aggroList = new AggroList(this);

		setFlying(getParameter("isFlying", false));

		
		int enchant = Math.min(127, getTemplate().getEnchantEffect());
		if(enchant == 0 && Config.NPC_RANDOM_ENCHANT)
			enchant = Rnd.get(0, 18);

		_enchantEffect = enchant;

		_isNoSleepMode = getParameter(NO_SLEEP_MODE, false);
		_corpseTime = getParameter(CORPSE_TIME, BASE_CORPSE_TIME);
	}

	@SuppressWarnings("unchecked")
	@Override
	public HardReference<NpcInstance> getRef()
	{
		return (HardReference<NpcInstance>) super.getRef();
	}

	@Override
	public CharacterAI getAI()
	{
		if(_ai == null)
			synchronized (this)
			{
				if(_ai == null)
					_ai = getTemplate().getNewAI(this);
			}

		return _ai;
	}

	/**
	 * Return the position of the spawned point.<BR><BR>
	 * Может возвращать случайную точку, поэтому всегда следует кешировать результат вызова!
	 */
	public Location getSpawnedLoc()
	{
		return _spawnedLoc;
	}

	public void setSpawnedLoc(Location loc)
	{
		_spawnedLoc = loc;
	}

	public int getRightHandItem()
	{
		return _currentRHandId;
	}

	public int getLeftHandItem()
	{
		return _currentLHandId;
	}

	public void setLHandId(int newWeaponId)
	{
		_currentLHandId = newWeaponId;
	}

	public void setRHandId(int newWeaponId)
	{
		_currentRHandId = newWeaponId;
	}

	@Override
	protected void onReduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp)
	{
		if(attacker.isPlayable())
			getAggroList().addDamageHate(attacker, (int) damage, 0);

		super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp);
	}

	@Override
	protected void onDeath(Creature killer)
	{
		_dieTime = System.currentTimeMillis();

		if(isMonster() && (((MonsterInstance) this).isSeeded() || ((MonsterInstance) this).isSpoiled()))
			startDecay(20000L);
		else
			startDecay(_corpseTime * 1000L);

		// установка параметров оружия и коллизий по умолчанию
		setLHandId(getTemplate().lhand);
		setRHandId(getTemplate().rhand);

		getAI().stopAITask();
		stopRandomAnimation();

		super.onDeath(killer);
	}

	public long getDeadTime()
	{
		if(_dieTime <= 0L)
			return 0L;
		return System.currentTimeMillis() - _dieTime;
	}

	public AggroList getAggroList()
	{
		return _aggroList;
	}

	public MinionList getMinionList()
	{
		return null;
	}

	public boolean hasMinions()
	{
		return false;
	}

	public void dropItem(Player lastAttacker, int itemId, long itemCount)
	{
		if(itemCount == 0 || lastAttacker == null)
			return;

		ItemInstance item;

		for(long i = 0; i < itemCount; i++)
		{
			item = ItemFunctions.createItem(itemId);
			for(GlobalEvent e : getEvents())
				item.addEvent(e);

			// Set the Item quantity dropped if L2ItemInstance is stackable
			if(item.isStackable())
			{
				i = itemCount; // Set so loop won't happent again
				item.setCount(itemCount); // Set item count
			}

			if(isRaid() || this instanceof ReflectionBossInstance)
			{
				SystemMessagePacket sm;
				if(itemId == 57)
				{
					sm = new SystemMessagePacket(SystemMsg.C1_HAS_DIED_AND_DROPPED_S2_ADENA);
					sm.addName(this);
					sm.addLong(item.getCount());
				}
				else
				{
					sm = new SystemMessagePacket(SystemMsg.C1_DIED_AND_DROPPED_S3_S2);
					sm.addName(this);
					sm.addItemName(itemId);
					sm.addLong(item.getCount());
				}
				broadcastPacket(sm);
			}

			lastAttacker.doAutoLootOrDrop(item, this);
		}
	}

	public void dropItem(Player lastAttacker, ItemInstance item)
	{
		if(item.getCount() == 0)
			return;

		if(isRaid() || this instanceof ReflectionBossInstance)
		{
			SystemMessagePacket sm;
			if(item.getItemId() == 57)
			{
				sm = new SystemMessagePacket(SystemMsg.C1_HAS_DIED_AND_DROPPED_S2_ADENA);
				sm.addName(this);
				sm.addLong(item.getCount());
			}
			else
			{
				sm = new SystemMessagePacket(SystemMsg.C1_DIED_AND_DROPPED_S3_S2);
				sm.addName(this);
				sm.addItemName(item.getItemId());
				sm.addLong(item.getCount());
			}
			broadcastPacket(sm);
		}

		lastAttacker.doAutoLootOrDrop(item, this);
	}

	@Override
	public boolean isAttackable(Creature attacker)
	{
		if(isFakePlayer() && isInZonePeace() && !Config.ALLOW_FAKE_PEACE_ZONE_KILL)
			return false;	
		return true;
	}

	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return false;
	}

	@Override
	protected void onSpawn()
	{
		super.onSpawn();

		_dieTime = 0L;
		_spawnAnimation = 0;

		if(getAI().isGlobalAI() || getCurrentRegion() != null && getCurrentRegion().isActive())
		{
			getAI().startAITask();
			startRandomAnimation();
		}

		ThreadPoolManager.getInstance().execute(new NotifyAITask(this, CtrlEvent.EVT_SPAWN));

		getListeners().onSpawn();
	}

	@Override
	protected void onDespawn()
	{
		getAggroList().clear();

		getAI().onEvtDeSpawn();
		getAI().stopAITask();
		getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
		stopRandomAnimation();

		super.onDespawn();
	}

	@Override
	public NpcTemplate getTemplate()
	{
		return (NpcTemplate) super.getTemplate();
	}

	@Override
	public int getNpcId()
	{
		return getTemplate().getId();
	}

	protected boolean _unAggred = false;

	public void setUnAggred(boolean state)
	{
		_unAggred = state;
	}

	/**
	 * Return True if the L2NpcInstance is aggressive (ex : L2MonsterInstance in function of aggroRange).<BR><BR>
	 */
	public boolean isAggressive()
	{
		return getAggroRange() > 0;
	}

	public int getAggroRange()
	{
		if(_unAggred)
			return 0;

		if(_personalAggroRange >= 0)
			return _personalAggroRange;

		return getTemplate().aggroRange;
	}

	/**
	 * Устанавливает данному npc новый aggroRange.
	 * Если установленый aggroRange < 0, то будет братся аггрорейндж с темплейта.
	 * @param aggroRange новый agrroRange
	 */
	public void setAggroRange(int aggroRange)
	{
		_personalAggroRange = aggroRange;
	}

	/**
	 * Возвращает группу социальности
	 */
	public Faction getFaction()
	{
		return getTemplate().getFaction();
	}

	public boolean isInFaction(NpcInstance npc)
	{
		return getFaction().equals(npc.getFaction()) && !getFaction().isIgnoreNpcId(npc.getNpcId());
	}

	//1
	@Override
	public int getMaxHp()
	{
		if(isMonster() && Config.ENABLE_CUSTOM_STATS_SYSTEM)
			return calcCustomStats(1, this, super.getMaxHp(), isRaid()); //1 for HP index		
		return super.getMaxHp();
	}

	//2
	@Override
	public int getMAtk(Creature target, Skill skill)
	{
		if(isMonster() && Config.ENABLE_CUSTOM_STATS_SYSTEM)
			return calcCustomStats(2, this, super.getMAtk(target, skill), isRaid()); //2 for MATK index			
		return super.getMAtk(target, skill);
	}

	//3
	@Override
	public int getMAtkSpd()
	{
		if(isMonster() && Config.ENABLE_CUSTOM_STATS_SYSTEM)
			return calcCustomStats(3, this, super.getMAtkSpd(), isRaid()); //3 for MATKSPD index		
		return super.getMAtkSpd();
	}

	//4	
	@Override
	public int getMDef(Creature target, Skill skill)
	{
		if(isMonster() && Config.ENABLE_CUSTOM_STATS_SYSTEM)
			return calcCustomStats(4, this, super.getMDef(target, skill), isRaid()); //4 for MDEF index		
		return super.getMDef(target, skill);
	}

	//5
	@Override
	public int getMaxMp()
	{
		if(isMonster() && Config.ENABLE_CUSTOM_STATS_SYSTEM)
			return calcCustomStats(5, this, super.getMaxMp(), isRaid()); //5 for MAXMP index			
		return super.getMaxMp();
	}

	//6
	@Override
	public int getPAtk(Creature target)
	{
		if(isMonster() && Config.ENABLE_CUSTOM_STATS_SYSTEM)
			return calcCustomStats(6, this, super.getPAtk(target), isRaid()); //6 for PATK index		
		return super.getPAtk(target);
	}

	//7
	@Override
	public int getPAtkSpd()
	{
		if(isMonster() && Config.ENABLE_CUSTOM_STATS_SYSTEM)
			return calcCustomStats(7, this, super.getPAtkSpd(), isRaid()); //7 for PATKSPD index		
		return super.getPAtkSpd();
	}

	//8
	@Override	
	public int getPDef(Creature target)
	{
		if(isMonster() && Config.ENABLE_CUSTOM_STATS_SYSTEM)
			return calcCustomStats(8, this, super.getPDef(target), isRaid()); //8 for PDEF index			
		return super.getPDef(target);
	}	

	public long getExpReward()
	{
		return (long) calcStat(Stats.EXP, getTemplate().rewardExp, null, null);
	}

	public long getSpReward()
	{
		return (long) calcStat(Stats.SP, getTemplate().rewardSp, null, null);
	}

	@Override
	protected void onDelete()
	{
		stopDecay();
		if(_spawn != null)
			_spawn.stopRespawn();
		setSpawn(null);

		super.onDelete();
	}

	public Spawner getSpawn()
	{
		return _spawn;
	}

	public void setSpawn(Spawner spawn)
	{
		_spawn = spawn;
	}

	@Override
	protected void onDecay()
	{
		super.onDecay();

		_spawnAnimation = 2;

		if(_spawn != null)
			_spawn.decreaseCount(this);
		else
			deleteMe(); // Если этот моб заспавнен не через стандартный механизм спавна значит посмертие ему не положено и он умирает насовсем
	}

	/**
	 * Запустить задачу "исчезновения" после смерти
	 */
	protected void startDecay(long delay)
	{
		stopDecay();
		_decayTask = DecayTaskManager.getInstance().addDecayTask(this, delay);
	}

	/**
	 * Отменить задачу "исчезновения" после смерти
	 */
	public void stopDecay()
	{
		if(_decayTask != null)
		{
			_decayTask.cancel(false);
			_decayTask = null;
		}
	}

	/**
	 * Отменить и завершить задачу "исчезновения" после смерти
	 */
	public void endDecayTask()
	{
		if(_decayTask != null)
		{
			_decayTask.cancel(false);
			_decayTask = null;
		}
		doDecay();
	}

	@Override
	public boolean isUndead()
	{
		return getTemplate().isUndead();
	}

	public void setLevel(int level)
	{
		_level = level;
	}

	@Override
	public int getLevel()
	{
		return _level == 0 ? getTemplate().level : _level;
	}

	private int _displayId = 0;

	public void setDisplayId(int displayId)
	{
		_displayId = displayId;
	}

	public int getDisplayId()
	{
		return _displayId > 0 ? _displayId : getTemplate().displayId;
	}

	@Override
	public ItemInstance getActiveWeaponInstance()
	{
		// regular NPCs dont have weapons instancies
		return null;
	}

	@Override
	public WeaponTemplate getActiveWeaponTemplate()
	{
		// Get the weapon identifier equipped in the right hand of the L2NpcInstance
		int weaponId = getTemplate().rhand;

		if(weaponId < 1)
			return null;

		// Get the weapon item equipped in the right hand of the L2NpcInstance
		ItemTemplate item = ItemHolder.getInstance().getTemplate(getTemplate().rhand);

		if(!(item instanceof WeaponTemplate))
			return null;

		return (WeaponTemplate) item;
	}

	@Override
	public ItemInstance getSecondaryWeaponInstance()
	{
		// regular NPCs dont have weapons instances
		return null;
	}

	@Override
	public WeaponTemplate getSecondaryWeaponTemplate()
	{
		// Get the weapon identifier equipped in the right hand of the L2NpcInstance
		int weaponId = getTemplate().lhand;

		if(weaponId < 1)
			return null;

		// Get the weapon item equipped in the right hand of the L2NpcInstance
		ItemTemplate item = ItemHolder.getInstance().getTemplate(getTemplate().lhand);

		if(!(item instanceof WeaponTemplate))
			return null;

		return (WeaponTemplate) item;
	}

	@Override
	public void sendChanges()
	{
		if(isFlying()) // FIXME
			return;
		super.sendChanges();
	}

	private ScheduledFuture<?> _broadcastCharInfoTask;

	public void onMenuSelect(Player player, int ask, int reply)
	{
		if(getAI() != null)
			getAI().notifyEvent(CtrlEvent.EVT_MENU_SELECTED, player, ask, reply);
	}

	public class BroadcastCharInfoTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			broadcastCharInfoImpl();
			_broadcastCharInfoTask = null;
		}
	}

	@Override
	public void broadcastCharInfo()
	{
		if(!isVisible())
			return;

		if(_broadcastCharInfoTask != null)
			return;

		_broadcastCharInfoTask = ThreadPoolManager.getInstance().schedule(new BroadcastCharInfoTask(), Config.BROADCAST_CHAR_INFO_INTERVAL);
	}

	@Override
	public void broadcastCharInfoImpl()
	{
		for(Player player : World.getAroundPlayers(this))
			player.sendPacket(new NpcInfoPacket(this, player).update());
	}

	// У NPC всегда 2
	public void onRandomAnimation()
	{
		if(System.currentTimeMillis() - _lastSocialAction > 10000L)
		{
			broadcastPacket(new SocialActionPacket(getObjectId(), 2));
			_lastSocialAction = System.currentTimeMillis();
		}
	}

	public void startRandomAnimation()
	{
		if(!hasRandomAnimation())
			return;
		_animationTask = LazyPrecisionTaskManager.getInstance().addNpcAnimationTask(this);
	}

	public void stopRandomAnimation()
	{
		if(_animationTask != null)
		{
			_animationTask.cancel(false);
			_animationTask = null;
		}
	}

	public boolean hasRandomAnimation()
	{
		return _hasRandomAnimation;
	}

	public void setHaveRandomAnim(boolean value)
	{
		_hasRandomAnimation = value;
	}

	public boolean hasRandomWalk()
	{
		return _hasRandomWalk;
	}

	public void setRandomWalk(boolean value)
	{
		_hasRandomWalk = value;
	}

	public Castle getCastle()
	{
		if(getReflection() == ReflectionManager.PARNASSUS && Config.SERVICES_PARNASSUS_NOTAX)
			return null;
		if(Config.SERVICES_OFFSHORE_NO_CASTLE_TAX && getReflection() == ReflectionManager.GIRAN_HARBOR)
			return null;
		if(Config.SERVICES_OFFSHORE_NO_CASTLE_TAX && getReflection() == ReflectionManager.PARNASSUS)
			return null;
		if(Config.SERVICES_OFFSHORE_NO_CASTLE_TAX && isInZone(ZoneType.offshore))
			return null;
		if(_nearestCastle == null)
			_nearestCastle = ResidenceHolder.getInstance().getResidence(getTemplate().getCastleId());
		return _nearestCastle;
	}

	public Castle getCastle(Player player)
	{
		return getCastle();
	}

	public Fortress getFortress()
	{
		if(_nearestFortress == null)
			_nearestFortress = ResidenceHolder.getInstance().findNearestResidence(Fortress.class, getX(), getY(), getZ(), getReflection(), 32768);

		return _nearestFortress;
	}

	public ClanHall getClanHall()
	{
		if(_nearestClanHall == null)
			_nearestClanHall = ResidenceHolder.getInstance().findNearestResidence(ClanHall.class, getX(), getY(), getZ(), getReflection(), 32768);

		return _nearestClanHall;
	}

	protected long _lastSocialAction;

	@Override
	public void onAction(Player player, boolean shift)
	{
		if(!isTargetable(player))
		{
			player.sendActionFailed();
			return;
		}

		if(player.getTarget() != this)
		{
			player.setNpcTarget(this);
			return;
		}

		if(Events.onAction(player, this, shift))
		{
			player.sendActionFailed();
			return;
		}

		if(isAutoAttackable(player))
		{
			player.getAI().Attack(this, false, shift);
			return;
		}

		if(!isInRange(player, INTERACTION_DISTANCE))
		{
			if(player.getAI().getIntention() != CtrlIntention.AI_INTENTION_INTERACT)
				player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this, null);
			return;
		}

		if(!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && player.isPK() && !player.isGM() && !(this instanceof WarehouseInstance))
		{
			player.sendActionFailed();
			return;
		}

		// С NPC нельзя разговаривать мертвым и сидя
		if(!Config.ALLOW_TALK_WHILE_SITTING && player.isSitting() || player.isAlikeDead())
			return;

		if(hasRandomAnimation())
			onRandomAnimation();

		player.sendActionFailed();
		player.stopMove(false);

		if(_isBusy)
			showBusyWindow(player);
		else if(isHasChatWindow())
		{
			boolean flag = false;
			Quest[] qlst = getTemplate().getEventQuests(QuestEventType.NPC_FIRST_TALK);
			if(qlst != null && qlst.length > 0)
				for(Quest element : qlst)
				{
					QuestState qs = player.getQuestState(element.getName());
					if((qs == null || !qs.isCompleted()) && element.notifyFirstTalk(this, player))
						flag = true;
				}
			if(!flag)
			{
				showChatWindow(player, 0);
				if(Config.NPC_DIALOG_PLAYER_DELAY > 0)
					player.setNpcDialogEndTime((int) (System.currentTimeMillis() / 1000L) + Config.NPC_DIALOG_PLAYER_DELAY);
			}
		}
	}

	public void showQuestWindow(Player player, String questId)
	{
		if(!player.isQuestContinuationPossible(true))
			return;

		int count = 0;
		for(QuestState quest : player.getAllQuestsStates())
			if(quest != null && quest.getQuest().isVisible(player) && quest.isStarted() && quest.getCond() > 0)
				count++;

		if(count > 40)
		{
			showChatWindow(player, "quest-limit.htm");
			return;
		}

		try
		{
			// Get the state of the selected quest
			QuestState qs = player.getQuestState(questId);
			if(qs != null)
			{
				if(qs.isCompleted() && !qs.isSecondTimeAvailable(player))
				{
					showChatWindow(player, "completed-quest.htm");
					return;
				}
				if(qs.getQuest().notifyTalk(this, qs))
					return;
			}
			else
			{
				Quest q = QuestManager.getQuest(questId);
				if(q != null)
				{
					// check for start point
					Quest[] qlst = getTemplate().getEventQuests(QuestEventType.QUEST_START);
					if(qlst != null && qlst.length > 0)
						for(Quest element : qlst)
							if(element == q)
							{
								qs = q.newQuestState(player, Quest.CREATED);
								if(qs.getQuest().notifyTalk(this, qs))
									return;
								break;
							}
				}
			}

			showChatWindow(player, "no-quest.htm");
		}
		catch(Exception e)
		{
			_log.warn("problem with npc text(questId: " + questId + ") " + e);
			_log.error("", e);
		}

		player.sendActionFailed();
	}

	public static boolean canBypassCheck(Player player, NpcInstance npc)
	{
		if(npc == null || player.isActionsDisabled() || !Config.ALLOW_TALK_WHILE_SITTING && player.isSitting() || !npc.isInRange(player, INTERACTION_DISTANCE))
		{
			player.sendActionFailed();
			return false;
		}
		return true;
	}

	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		try
		{
			StringTokenizer st = new StringTokenizer(command, "_");
			String cmd = st.nextToken();
			if(command.equalsIgnoreCase("TerritoryStatus"))
			{
				NpcHtmlMessagePacket html = new NpcHtmlMessagePacket(player, this);
				html.setFile("merchant/territorystatus.htm");
				html.replace("%npcname%", getName());

				Castle castle = getCastle(player);
				if(castle != null && castle.getId() > 0)
				{
					html.replace("%castlename%", HtmlUtils.htmlResidenceName(castle.getId()));
					html.replace("%taxpercent%", String.valueOf(castle.getTaxPercent()));

					if(castle.getOwnerId() > 0)
					{
						Clan clan = ClanTable.getInstance().getClan(castle.getOwnerId());
						if(clan != null)
						{
							html.replace("%clanname%", clan.getName());
							html.replace("%clanleadername%", clan.getLeaderName());
						}
						else
						{
							html.replace("%clanname%", "unexistant clan");
							html.replace("%clanleadername%", "None");
						}
					}
					else
					{
						html.replace("%clanname%", "NPC");
						html.replace("%clanleadername%", "None");
					}
				}
				else
				{
					html.replace("%castlename%", "Open");
					html.replace("%taxpercent%", "0");

					html.replace("%clanname%", "No");
					html.replace("%clanleadername%", getName());
				}

				player.sendPacket(html);
			}
			else if(command.startsWith("Quest"))
			{
				String quest = command.substring(5).trim();
				if(quest.length() == 0)
					showQuestWindow(player);
				else
					showQuestWindow(player, quest);
			}
			else if(command.startsWith("Chat"))
				try
				{
					int val = Integer.parseInt(command.substring(5));
					showChatWindow(player, val);
				}
				catch(NumberFormatException nfe)
				{
					String filename = command.substring(5).trim();
					if(filename.length() == 0)
						showChatWindow(player, "npcdefault.htm");
					else
						showChatWindow(player, filename);
				}
			else if(command.startsWith("AttributeCancel"))
				player.sendPacket(new ExShowBaseAttributeCancelWindow(player));
			else if(command.startsWith("NpcLocationInfo"))
			{
				int val = Integer.parseInt(command.substring(16));
				NpcInstance npc = GameObjectsStorage.getByNpcId(val);
				if(npc != null)
				{
					// Убираем флажок на карте и стрелку на компасе
					player.sendPacket(new RadarControlPacket(2, 2, npc.getLoc()));
					// Ставим флажок на карте и стрелку на компасе
					player.sendPacket(new RadarControlPacket(0, 1, npc.getLoc()));
				}
			}
			else if(command.startsWith("Multisell") || command.startsWith("multisell"))
			{
				String listId = command.substring(9).trim();
				Castle castle = getCastle(player);
				MultiSellHolder.getInstance().SeparateAndSend(Integer.parseInt(listId), player, castle != null ? castle.getTaxRate() : 0);
			}
			else if(command.startsWith("EnterRift"))
			{
				StringTokenizer st2 = new StringTokenizer(command);
				st2.nextToken(); //no need for "enterRift"

				Integer b1 = Integer.parseInt(st2.nextToken()); //type

				DimensionalRiftManager.getInstance().start(player, b1, this);
			}
			else if(command.startsWith("ChangeRiftRoom"))
			{
				if(player.isInParty() && player.getParty().isInReflection() && player.getParty().getReflection() instanceof DimensionalRift)
					((DimensionalRift) player.getParty().getReflection()).manualTeleport(player, this);
				else
					DimensionalRiftManager.getInstance().teleportToWaitingRoom(player);
			}
			else if(command.startsWith("ExitRift"))
			{
				if(player.isInParty() && player.getParty().isInReflection() && player.getParty().getReflection() instanceof DimensionalRift)
					((DimensionalRift) player.getParty().getReflection()).manualExitRift(player, this);
				else
					DimensionalRiftManager.getInstance().teleportToWaitingRoom(player);
			}
			else if(command.equalsIgnoreCase("ClanSkillList"))
				showClanSkillList(player);
			else if(command.startsWith("SubUnitSkillList"))
				showSubUnitSkillList(player);
			else if(command.equalsIgnoreCase("TransformationSkillList"))
				showTransformationSkillList(player, AcquireType.TRANSFORMATION);
			else if(command.equalsIgnoreCase("CertificationSkillList"))
				showTransformationSkillList(player, AcquireType.CERTIFICATION);
			else if(command.equalsIgnoreCase("CollectionSkillList"))
				showCollectionSkillList(player);
			else if(command.equalsIgnoreCase("BuyTransformation"))
				showTransformationMultisell(player);
			else if(command.startsWith("Augment"))
			{
				int cmdChoice = Integer.parseInt(command.substring(8, 9).trim());
				if(cmdChoice == 1)
					player.sendPacket(SystemMsg.SELECT_THE_ITEM_TO_BE_AUGMENTED, ExShowVariationMakeWindow.STATIC);
				else if(cmdChoice == 2)
					player.sendPacket(SystemMsg.SELECT_THE_ITEM_FROM_WHICH_YOU_WISH_TO_REMOVE_AUGMENTATION, ExShowVariationCancelWindow.STATIC);
			}
			else if(command.startsWith("Link"))
				showChatWindow(player, command.substring(5));
			else if(cmd.equalsIgnoreCase("teleport"))
			{
				if(!st.hasMoreTokens())
				{
					errorBypass(command, player);
					return;
				}

				String cmd2 = st.nextToken();
				if(cmd2.equalsIgnoreCase("list"))
				{
					int listId = 1;
					if(st.hasMoreTokens())
						listId = Integer.parseInt(st.nextToken());

					showTeleportList(player, listId);
				}
				else if(cmd2.equalsIgnoreCase("id"))
				{
					int listId = Integer.parseInt(st.nextToken());
					int teleportNameId = Integer.parseInt(st.nextToken());

					TIntObjectMap<TeleportLocation> list = getTemplate().getTeleportList(listId);
					if(list == null || list.isEmpty())
					{
						errorBypass(command, player);
						return;
					}

					TeleportLocation teleportLocation = list.get(teleportNameId);
					if(teleportLocation == null)
					{
						errorBypass(command, player);
						return;
					}

					long itemCount = teleportLocation.getPrice();
					if(st.hasMoreTokens())
						itemCount = Long.parseLong(st.nextToken());

					teleportPlayer(player, teleportLocation, itemCount);
				}
				else if(cmd2.equalsIgnoreCase("noble"))
				{
					if(player.isNoble() || Config.ALLOW_NOBLE_TP_TO_ALL)
						showChatWindow(player, "common/fornobless.htm");
					else
						showChatWindow(player, "common/fornonobless.htm");
				}
				else if(cmd2.equalsIgnoreCase("mdt"))
				{
					if(!st.hasMoreTokens())
					{
						errorBypass(command, player);
						return;
					}

					String cmd3 = st.nextToken();
					if(cmd3.equalsIgnoreCase("to"))
					{
						player.setVar("@mdt_back_cords", player.getLoc().toXYZString(), -1);
						player.teleToLocation(12661, 181687, -3540);
					}
					else if(cmd3.equalsIgnoreCase("from"))
					{
						String var = player.getVar("@mdt_back_cords");
						if(var == null || var.isEmpty())
						{
							player.teleToLocation(12902, 181011, -3563);
							return;
						}
						player.teleToLocation(Location.parseLoc(var));
					}
				}
				else if(cmd2.equalsIgnoreCase("fi"))
				{
					if(!st.hasMoreTokens())
					{
						errorBypass(command, player);
						return;
					}

					String cmd3 = st.nextToken();
					if(cmd3.equalsIgnoreCase("to"))
					{
						player.setVar("@fi_back_cords", player.getLoc().toXYZString(), -1);
						switch(Rnd.get(4))
						{
							case 1:
								player.teleToLocation(-60695, -56896, -2032);
								break;
							case 2:
								player.teleToLocation(-59716, -55920, -2032);
								break;
							case 3:
								player.teleToLocation(-58752, -56896, -2032);
								break;
							default :
								player.teleToLocation(-59716, -57864, -2032);
								break;
						}
					}
					else if(cmd3.equalsIgnoreCase("from"))
					{
						String var = player.getVar("@fi_back_cords");
						if(var == null || var.isEmpty())
						{
							player.teleToLocation(12902, 181011, -3563);
							return;
						}
						player.teleToLocation(Location.parseLoc(var));
					}
				}
				else
				{
					if(st.countTokens() < 2)
					{
						errorBypass(command, player);
						return;
					}

					int x = Integer.parseInt(cmd2);
					int y = Integer.parseInt(st.nextToken());
					int z = Integer.parseInt(st.nextToken());

					int itemId = 0;
					if(st.hasMoreTokens())
						itemId = Integer.parseInt(st.nextToken());

					int itemCount = 0;
					if(st.hasMoreTokens())
						itemCount = Integer.parseInt(st.nextToken());

					int castleId = 0;
					if(st.hasMoreTokens())
						castleId = Integer.parseInt(st.nextToken());

					int reflectionId = 0;
					if(st.hasMoreTokens())
						reflectionId = Integer.parseInt(st.nextToken());

					teleportPlayer(player, x, y, z, itemId, itemCount, castleId, reflectionId);
				}
			}
			else if(command.startsWith("open_gate"))
			{
				int val = Integer.parseInt(command.substring(10));
				ReflectionUtils.getDoor(val).openMe();
				player.sendActionFailed();
			}
			else if(command.equalsIgnoreCase("TransferSkillList"))
				showTransferSkillList(player);
			else if(command.startsWith("RemoveTransferSkill"))
			{
				AcquireType type = AcquireType.transferType(player.getActiveClassId());
				if(type == null)
					return;

				Collection<SkillLearn> skills = SkillAcquireHolder.getInstance().getAvailableSkills(null, type);
				if(skills.isEmpty())
				{
					player.sendActionFailed();
					return;
				}

				boolean reset = false;
				for(SkillLearn skill : skills)
					if(player.getKnownSkill(skill.getId()) != null)
					{
						reset = true;
						break;
					}

				if(!reset)
				{
					player.sendActionFailed();
					return;
				}

				if(!player.reduceAdena(10000000L, true))
				{
					showChatWindow(player, "common/skill_share_healer_no_adena.htm");
					return;
				}

				for(SkillLearn skill : skills)
					if(player.removeSkill(skill.getId(), true) != null)
						ItemFunctions.addItem(player, skill.getItemId(), skill.getItemCount(), true);
			}
			else if(command.startsWith("WorldStatistic"))
			{
				player.sendPacket(new ExShowStatPage(-2));
			}
			else if(command.startsWith("ExitFromQuestInstance"))
			{
				Reflection r = player.getReflection();
				if(r.isDefault())
					return;
				r.startCollapseTimer(60000);
				player.teleToLocation(r.getReturnLoc(), 0);
				if(command.length() > 22)
					try
					{
						int val = Integer.parseInt(command.substring(22));
						showChatWindow(player, val);
					}
					catch(NumberFormatException nfe)
					{
						String filename = command.substring(22).trim();
						if(filename.length() > 0)
							showChatWindow(player, filename);
					}
			}
		}
		catch(StringIndexOutOfBoundsException sioobe)
		{
			_log.info("Incorrect htm bypass! npcId=" + getNpcId() + " command=[" + command + "]");
		}
		catch(NumberFormatException nfe)
		{
			_log.info("Invalid bypass to Server command parameter! npcId=" + getNpcId() + " command=[" + command + "]");
		}
	}

	public void errorBypass(String bypass, Player player)
	{
		player.sendMessage(new CustomMessage("l2s.gameserver.model.instance.NpcInstance.ErrorBypass", player).addNumber(getNpcId()).addString(bypass));
	}

	public boolean teleportPlayer(Player player, int x, int y, int z, int itemId, long itemCount, int castleId, int reflectionId)
	{
		if(player == null)
			return false;

		if(player.getMountType() == MountType.WYVERN)
		{
			//player.sendMessage("Телепортация верхом на виверне невозможна."); //TODO: [Bonux] Найти нужное сообщение!
			return false;
		}

		/* Затычка, npc Mozella не ТПшит чаров уровень которых превышает заданный в конфиге
		 * Off Like >= 56 lvl, данные по ограничению lvl'a устанавливаются в altsettings.properties.
		 */
		switch(getNpcId())
		{
			case 30483:
				if(player.getLevel() >= Config.CRUMA_GATEKEEPER_LVL)
				{
					showChatWindow(player, "teleporter/" + getNpcId() + "-no.htm");
					return false;
				}
				break;
			case 32864:
			case 32865:
			case 32866:
			case 32867:
			case 32868:
			case 32869:
			case 32870:
				if(player.getLevel() < 80)
				{
					showChatWindow(player, "teleporter/" + getNpcId() + "-no.htm");
					return false;
				}
				break;
		}

		if(itemId > 0 && itemCount > 0)
		{
			if(ItemFunctions.getItemCount(player, itemId) < itemCount)
			{
				if(itemId == ItemTemplate.ITEM_ID_ADENA)
					player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				else if(itemId == ItemTemplate.ITEM_ID_OLYMPIAD_TOKEN)
					showChatWindow(player, "common/fornonoblessitem.htm");
				else
					player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
				return false;
			}
		}

		if(castleId > 0 && player.getReflection().isDefault() && !Config.ALT_TELEPORT_TO_TOWN_DURING_SIEGE)
		{
			Castle castle = ResidenceHolder.getInstance().getResidence(Castle.class, castleId);
			if(castle != null && castle.getSiegeEvent().isInProgress())	// Нельзя телепортироваться в города, где идет осада
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_TELEPORT_TO_A_VILLAGE_THAT_IS_IN_A_SIEGE);
				return false;
			}
		}

		if(itemId > 0 && itemCount > 0)
		{
			if(ItemFunctions.removeItem(player, itemId, itemCount, true) < itemCount)
				return false;
		}

		Location pos = Location.findPointToStay(x, y, z, 50, 100, player.getGeoIndex());
		if(reflectionId > -1)
			player.teleToLocation(pos, reflectionId);
		else 
			player.teleToLocation(pos);
		return true;
	}

	public boolean teleportPlayer(Player player, Location loc, int itemId, long itemCount, int castleId, int reflectionId)
	{
		return teleportPlayer(player, loc.getX(), loc.getY(), loc.getZ(), itemId, itemCount, castleId, reflectionId);
	}

	public boolean teleportPlayer(Player player, int x, int y, int z, int itemId, long itemCount)
	{
		return teleportPlayer(player, x, y, z, itemId, itemCount, 0, -1);
	}

	public boolean teleportPlayer(Player player, Location loc, int itemId, long itemCount)
	{
		return teleportPlayer(player, loc.getX(), loc.getY(), loc.getZ(), itemId, itemCount, 0, -1);
	}

	private boolean teleportPlayer(Player player, TeleportLocation loc, long itemCount)
	{
		if(teleportPlayer(player, loc, loc.getItemId(), itemCount, loc.getCastleId(), -1))
		{
			player.removeQuestTeleportMark(loc.getName());
			return true;
		}
		return false;
	}

	private void showTeleportList(Player player, int listId)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("&$556;").append("<br><br>");

		TIntObjectMap<TeleportLocation> list = getTemplate().getTeleportList(listId);
		if(list != null && !list.isEmpty() && player.getPlayerAccess().UseTeleport)
		{
			for(TeleportLocation tl : list.valueCollection())
			{
				if(tl.getItemId() == ItemTemplate.ITEM_ID_ADENA)
				{
					double pricemod = (tl.isPrimeHours() && player.getLevel() <= Config.GATEKEEPER_FREE) ? 0. : Config.GATEKEEPER_MODIFIER;
					if(tl.getPrice() > 0 && pricemod > 0)
					{
						//On Saturdays and Sundays from 8 PM to 12 AM, gatekeeper teleport fees decrease by 50%.
						Calendar calendar = Calendar.getInstance();
						int day = calendar.get(Calendar.DAY_OF_WEEK);
						int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
						if((day == Calendar.SUNDAY || day == Calendar.SATURDAY) && (hour >= 20 && hour <= 12))
							pricemod /= 2;
					}

					sb.append("<Button ALIGN=LEFT ICON=\"");
					if(player.haveQuestTeleportMark(tl.getName()))
						sb.append("QUEST");
					else
						sb.append("TELEPORT");

					long price = (long) (tl.getPrice() * pricemod);
					sb.append("\" action=\"bypass -h npc_%objectId%_teleport_id_").append(listId).append("_").append(tl.getName()).append("_").append(price).append("\" msg=\"811;F;").append(tl.getName()).append("\">").append(HtmlUtils.htmlNpcString(tl.getName()));
					if(price > 0)
						sb.append(" - ").append(price).append(" ").append(HtmlUtils.htmlItemName(ItemTemplate.ITEM_ID_ADENA));
					sb.append("</button>");
				}
				else
				{
					sb.append("<Button ALIGN=LEFT ICON=\"");
					if(player.haveQuestTeleportMark(tl.getName()))
						sb.append("QUEST");
					else
						sb.append("TELEPORT");

					sb.append("\" action=\"bypass -h npc_%objectId%_teleport_id_").append(listId).append("_").append(tl.getName()).append("\" msg=\"811;F;").append(tl.getName()).append("\">").append(HtmlUtils.htmlNpcString(tl.getName()));
					if(tl.getItemId() > 0 && tl.getPrice() > 0)
						sb.append(" - ").append(tl.getPrice()).append(" ").append(HtmlUtils.htmlItemName(tl.getItemId()));
					sb.append("</button>");
				}
			}
		}
		else
			sb.append("No teleports available for you.");

		NpcHtmlMessagePacket html = new NpcHtmlMessagePacket(player, this);
		html.setHtml(HtmlUtils.bbParse(sb.toString()));
		player.sendPacket(html);
	}

	private class QuestInfo implements Comparable<QuestInfo>
	{
		private final Quest quest;
		private final Player player;
		private final boolean isStart;

		public QuestInfo(Quest quest, Player player, boolean isStart)
		{
			this.quest = quest;
			this.player = player;
			this.isStart = isStart;
		}

		public final Quest getQuest()
		{
			return quest;
		}

		public final boolean isStart()
		{
			return isStart;
		}

		@Override
		public int compareTo(QuestInfo info)
		{
			int quest1 = quest.getDescrState(player, isStart);
			int quest2 = info.getQuest().getDescrState(player, isStart);
			int questId1 = quest.getQuestIntId();
			int questId2 = info.getQuest().getQuestIntId();

			if(quest1 == 1 && quest2 == 2)
				return 1;
			else if(quest1 == 2 && quest2 == 1)
				return -1;
			else if(quest1 == 3 && quest2 == 4)
				return 1;
			else if(quest1 == 4 && quest2 == 3)
				return -1;
			else if(quest1 > quest2)
				return 1;
			else if(quest1 < quest2)
				return -1;
			else
			{
				if(questId1 > questId2)
					return 1;
				else if(questId1 < questId2)
					return -1;
				else
					// Недостижимая ситуация.
					return 0;
			}
		}
	}

	public void showQuestWindow(Player player)
	{
		// collect awaiting quests and start points
		TIntObjectMap<QuestInfo> options = new TIntObjectHashMap<QuestInfo>();

		Quest[] starts = getTemplate().getEventQuests(QuestEventType.QUEST_START);
		if(starts != null)
		{
			for(Quest x : starts)
			{
				if(x.getQuestIntId() > 0 && x.checkStartNpc(this, player) && !options.containsKey(x.getQuestIntId()))
					options.put(x.getQuestIntId(), new QuestInfo(x, player, true));
			}
		}

		List<QuestState> awaits = player.getQuestsForEvent(this, QuestEventType.QUEST_TALK);
		if(awaits != null)
		{
			for(QuestState x : awaits)
			{
				Quest quest = x.getQuest();
				if(quest.getQuestIntId() > 0 && quest.checkTalkNpc(this, x) && !options.containsKey(quest.getQuestIntId()))
					options.put(quest.getQuestIntId(), new QuestInfo(quest, player, false));
			}
		}

		// Display a QuestChooseWindow (if several quests are available) or QuestWindow
		if(options.size() > 1)
		{
			List<QuestInfo> list = new ArrayList<QuestInfo>();
			list.addAll(options.valueCollection());
			Collections.sort(list);
			showQuestChooseWindow(player, list);
		}
		else if(options.size() == 1)
			showQuestWindow(player, options.values(new QuestInfo[1])[0].getQuest().getName());
		else
			showQuestWindow(player, "");
	}

	public void showQuestChooseWindow(Player player, List<QuestInfo> quests)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("<html><body>");

		for(QuestInfo info : quests)
		{
			Quest q = info.getQuest();
			if(!q.isVisible(player))
				continue;

			sb.append("<button icon=quest align=left action=\"bypass -h npc_").append(getObjectId()).append("_Quest ").append(q.getName()).append("\">").append(q.getDescr(player, info.isStart())).append("</button>");
		}

		sb.append("</body></html>");

		NpcHtmlMessagePacket html = new NpcHtmlMessagePacket(player, this);
		html.setHtml(sb.toString());
		player.sendPacket(html);
	}

	public void showChatWindow(Player player, int val, Object... replace)
	{
		String filename = getHtmlPath(getNpcId(), val, player);

		NpcHtmlMessagePacket packet = new NpcHtmlMessagePacket(player, this, filename, val);
		if(replace.length % 2 == 0)
			for(int i = 0; i < replace.length; i += 2)
				packet.replace(String.valueOf(replace[i]), String.valueOf(replace[i + 1]));
		player.sendPacket(packet);
	}

	public void showChatWindow(Player player, String filename, Object... replace)
	{
		NpcHtmlMessagePacket packet;
		if(filename.endsWith(".htm"))
			packet = new NpcHtmlMessagePacket(player, this, filename, 0);
		else
		{
			packet = new NpcHtmlMessagePacket(player, this);
			packet.setHtml(filename);
		}

		if(replace.length % 2 == 0)
		{
			for(int i = 0; i < replace.length; i += 2)
				packet.replace(String.valueOf(replace[i]), String.valueOf(replace[i + 1]));
		}

		player.sendPacket(packet);
	}

	public String getHtmlPath(int npcId, int val, Player player)
	{
		String pom;
		if(val == 0)
			pom = "" + npcId;
		else
			pom = npcId + "-" + val;

		if(getTemplate().getHtmRoot() != null)
			return getTemplate().getHtmRoot() + pom + ".htm";

		String temp = "default/" + pom + ".htm";
		if(HtmCache.getInstance().getNullable(temp, player) != null)
			return temp;

		// If the file is not found, the standard message "I have nothing to say to you" is returned
		return "npcdefault.htm";
	}

	private boolean _isBusy;
	private String _busyMessage = "";

	public final boolean isBusy()
	{
		return _isBusy;
	}

	public void setBusy(boolean isBusy)
	{
		_isBusy = isBusy;
	}

	public final String getBusyMessage()
	{
		return _busyMessage;
	}

	public void setBusyMessage(String message)
	{
		_busyMessage = message;
	}

	public void showBusyWindow(Player player)
	{
		NpcHtmlMessagePacket html = new NpcHtmlMessagePacket(player, this);
		html.setFile("npcbusy.htm");
		html.replace("%npcname%", getName());
		html.replace("%playername%", player.getName());
		html.replace("%busymessage%", _busyMessage);
		player.sendPacket(html);
	}

	public void showTransferSkillList(Player player)
	{
		ClassId classId = player.getClassId();
		if(classId == null)
			return;

		if(player.getLevel() < 76 || !classId.isOfLevel(ClassLevel.THIRD))
		{
			NpcHtmlMessagePacket html = new NpcHtmlMessagePacket(player, this);
			StringBuilder sb = new StringBuilder();
			sb.append("<html><head><body>");
			sb.append("You must have 3rd class change quest completed.");
			sb.append("</body></html>");
			html.setHtml(sb.toString());
			player.sendPacket(html);
			return;
		}

		AcquireType type = AcquireType.transferType(player.getActiveClassId());
		if(type == null)
			return;

		showAcquireList(type, player);
	}

	public static void showCollectionSkillList(Player player)
	{
		showAcquireList(AcquireType.COLLECTION, player);
	}

	public static void showChaosSkillList(Player player)
	{
		if(player.isBaseClassActive())
			showAcquireList(AcquireType.CHAOS, player);
		else if(player.isDualClassActive())
			showAcquireList(AcquireType.DUAL_CHAOS, player);
	}

	public void showTransformationMultisell(Player player)
	{
		if(!Config.ALLOW_LEARN_TRANS_SKILLS_WO_QUEST)
			if(!player.isQuestCompleted("_136_MoreThanMeetsTheEye"))
			{
				showChatWindow(player, "default/" + getNpcId() + "-nobuy.htm");
				return;
			}

		Castle castle = getCastle(player);
		MultiSellHolder.getInstance().SeparateAndSend(32323, player, castle != null ? castle.getTaxRate() : 0);
		player.sendActionFailed();
	}

	public void showTransformationSkillList(Player player, AcquireType type)
	{
		if(!Config.ALLOW_LEARN_TRANS_SKILLS_WO_QUEST)
			if(!player.isQuestCompleted("_136_MoreThanMeetsTheEye"))
			{
				showChatWindow(player, "default/" + getNpcId() + "-noquest.htm");
				return;
			}

		showAcquireList(type, player);
	}

	public static void showFishingSkillList(Player player)
	{
		showAcquireList(AcquireType.FISHING, player);
	}

	public static void showClanSkillList(Player player)
	{
		if(player.getClan() == null || !player.isClanLeader())
		{
			player.sendPacket(SystemMsg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
			player.sendActionFailed();
			return;
		}

		showAcquireList(AcquireType.CLAN, player);
	}

	public static void showAcquireList(AcquireType t, Player player)
	{
		final Collection<SkillLearn> skills = SkillAcquireHolder.getInstance().getAvailableSkills(player, t);

		final ExAcquirableSkillListByClass asl = new ExAcquirableSkillListByClass(t, skills.size());

		for(SkillLearn s : skills)
			asl.addSkill(s.getId(), s.getLevel(), s.getLevel(), s.getMinLevel(), s.getCost(), 0);

		if(skills.size() == 0)
		{
			player.sendPacket(AcquireSkillDonePacket.STATIC);
			player.sendPacket(SystemMsg.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN);
		}
		else
			player.sendPacket(asl);

		player.sendActionFailed();
	}

	public static void showSubUnitSkillList(Player player)
	{
		Clan clan = player.getClan();
		if(clan == null)
			return;

		if((player.getClanPrivileges() & Clan.CP_CL_TROOPS_FAME) != Clan.CP_CL_TROOPS_FAME)
		{
			player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}

		Set<SkillLearn> learns = new TreeSet<SkillLearn>();
		for(SubUnit sub : player.getClan().getAllSubUnits())
			learns.addAll(SkillAcquireHolder.getInstance().getAvailableSkills(player, AcquireType.SUB_UNIT, sub));

		final ExAcquirableSkillListByClass asl = new ExAcquirableSkillListByClass(AcquireType.SUB_UNIT, learns.size());

		for(SkillLearn s : learns)
			asl.addSkill(s.getId(), s.getLevel(), s.getLevel(), s.getCost(), 1, Clan.SUBUNIT_KNIGHT4);

		if(learns.size() == 0)
		{
			player.sendPacket(AcquireSkillDonePacket.STATIC);
			player.sendPacket(SystemMsg.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN);
		}
		else
			player.sendPacket(asl);

		player.sendActionFailed();
	}

	/**
	 * Нужно для отображения анимации спауна, используется в пакете NpcInfo:
	 * 0=false, 1=true, 2=summoned (only works if model has a summon animation)
	 **/
	public int getSpawnAnimation()
	{
		return _spawnAnimation;
	}

	public int calculateLevelDiffForDrop(int charLevel)
	{
		if(!Config.DEEPBLUE_DROP_RULES)
			return 0;

		int mobLevel = getLevel();
		// According to official data (Prima), deep blue mobs are 9 or more levels below players
		int deepblue_maxdiff = this instanceof RaidBossInstance ? Config.DEEPBLUE_DROP_RAID_MAXDIFF : Config.DEEPBLUE_DROP_MAXDIFF;

		return Math.max(charLevel - mobLevel - deepblue_maxdiff, 0);
	}

	@Override
	public String toString()
	{
		return getNpcId() + " " + getName();
	}

	public void refreshID()
	{
		objectId = IdFactory.getInstance().getNextId();
		_storedId = GameObjectsStorage.refreshId(this);
	}

	private boolean _isUnderground = false;

	public void setUnderground(boolean b)
	{
		_isUnderground = b;
	}

	public boolean isUnderground()
	{
		return _isUnderground;
	}

	public boolean isShowName()
	{
		return _showName;
	}

	public void setShowName(boolean value)
	{
		_showName = value;
	}

	@Override
	public NpcListenerList getListeners()
	{
		if(listeners == null)
			synchronized (this)
			{
				if(listeners == null)
					listeners = new NpcListenerList(this);
			}

		return (NpcListenerList) listeners;
	}

	public <T extends NpcListener> boolean addListener(T listener)
	{
		return getListeners().add(listener);
	}

	public <T extends NpcListener> boolean removeListener(T listener)
	{
		return getListeners().remove(listener);
	}

	@Override
	public NpcStatsChangeRecorder getStatsRecorder()
	{
		if(_statsRecorder == null)
			synchronized (this)
			{
				if(_statsRecorder == null)
					_statsRecorder = new NpcStatsChangeRecorder(this);
			}

		return (NpcStatsChangeRecorder) _statsRecorder;
	}

	public void setNpcState(int stateId)
	{
		broadcastPacket(new ExChangeNPCState(getObjectId(), stateId));
		npcState = stateId;
	}

	public int getNpcState()
	{
		return npcState;
	}

	@Override
	public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper)
	{
		List<L2GameServerPacket> list = new ArrayList<L2GameServerPacket>(3);
		list.add(new NpcInfoPacket(this, forPlayer));

		if(isInCombat())
			list.add(new AutoAttackStartPacket(getObjectId()));

		if(isMoving || isFollow)
			list.add(movePacket());

		return list;
	}

	@Override
	public boolean isNpc()
	{
		return true;
	}

	@Override
	public int getGeoZ(Location loc)
	{
		if(isFlying() || isInWater() || isInBoat() || isBoat() || isDoor())
			return loc.z;
		if(isNpc())
		{
			if(_spawnRange instanceof Territory)
				return GeoEngine.getHeight(loc, getGeoIndex());
			return loc.z;
		}

		return super.getGeoZ(loc);
	}

	@Override
	public Clan getClan()
	{
		Castle castle = getCastle();
		if(castle == null)
			return null;
		return castle.getOwner();
	}

	public NpcString getNameNpcString()
	{
		return _nameNpcString;
	}

	public NpcString getTitleNpcString()
	{
		return _titleNpcString;
	}

	public void setNameNpcString(NpcString nameNpcString)
	{
		_nameNpcString = nameNpcString;
	}

	public void setTitleNpcString(NpcString titleNpcString)
	{
		_titleNpcString = titleNpcString;
	}

	public boolean isMerchantNpc()
	{
		return false;
	}

	public SpawnRange getSpawnRange()
	{
		return _spawnRange;
	}

	public void setSpawnRange(SpawnRange spawnRange)
	{
		_spawnRange = spawnRange;
	}

	public void setParameter(String str, Object val)
	{
		if(_parameters == StatsSet.EMPTY)
			_parameters = new StatsSet();

		_parameters.set(str, val);
	}

	public void setParameters(MultiValueSet<String> set)
	{
		if(set.isEmpty())
			return;

		if(_parameters == StatsSet.EMPTY)
			_parameters = new MultiValueSet<String>(set.size());

		_parameters.putAll(set);
	}

	public int getParameter(String str, int val)
	{
		return _parameters.getInteger(str, val);
	}

	public long getParameter(String str, long val)
	{
		return _parameters.getLong(str, val);
	}

	public boolean getParameter(String str, boolean val)
	{
		return _parameters.getBool(str, val);
	}

	public String getParameter(String str, String val)
	{
		return _parameters.getString(str, val);
	}

	public MultiValueSet<String> getParameters()
	{
		return _parameters;
	}

	@Override
	public boolean isPeaceNpc()
	{
		return true;
	}

	public boolean isHasChatWindow()
	{
		return _hasChatWindow;
	}

	public void setHasChatWindow(boolean hasChatWindow)
	{
		_hasChatWindow = hasChatWindow;
	}

	public boolean isServerObject()
	{
		return false;
	}
	
	private int calcCustomStats(int stat, Creature mob, int baseValue, boolean isRaid)
	{ 
		int _level = mob.getLevel();
		int minAttr = 0;
		int randomAddon = 0;
		double mult = 1.;
		
		StatInfo info = CustomStatsDAO.getRecord(_level, isRaid);
		if(info == null)
		{
			return baseValue; //no info ; return base stats
		}	
		switch(stat)	
		{
			case 1: 
				mult = info.getMultHP();
				minAttr = info.getMinAttrHPMP();
				randomAddon = 0;
				break;
			case 2: 
				mult = info.getMultMatk();
				minAttr = info.getMinAttrpAtkmAtk();
				randomAddon = info.getRandomAddonpAtkmAtk();
				break;		
			case 3: 
				mult = info.getMultMatkSpd();
				minAttr = info.getMinAttrpAtkmAtkSpd();
				randomAddon = info.getRandomAddonpAtkmAtkSpd();
				break;		
			case 4: 
				mult = info.getMultMdef();
				minAttr = info.getMinAttrMdef();
				randomAddon = info.getRandomAddonMdef();
				break;	
			case 5: 
				mult = info.getMultMP();
				minAttr = info.getMinAttrHPMP();
				randomAddon = 0;
				break;					
			case 6: 
				mult = info.getMultPatk();
				minAttr = info.getMinAttrpAtkmAtk();
				randomAddon = info.getRandomAddonpAtkmAtk();
				break;	
			case 7: 
				mult = info.getMultPatkSpd();
				minAttr = info.getMinAttrpAtkmAtkSpd();
				randomAddon = info.getRandomAddonpAtkmAtkSpd();
				break;	
			case 8: 
				mult = info.getMultPdef();
				minAttr = info.getMinAttrPdef();
				randomAddon = info.getRandomAddonPdef();
				break;					
		}
		if(minAttr < baseValue)
		{
			return baseValue; //if min attribute has been reached mob doesn't need to be corrected or mul
		}
		return ((int)(baseValue * mult)) + randomAddon;
	}

	@Override
	public double getCurrentCollisionRadius()
	{
		if(isVisualTransformed()) // TODO: Проверить, влияет ли еффект Grow на трансформации у НПС.
			return super.getCollisionRadius();
		return super.getCollisionRadius() * getCollisionRadiusModifier();
	}

	@Override
	public double getCurrentCollisionHeight()
	{
		if(isVisualTransformed()) // TODO: Проверить, влияет ли еффект Grow на трансформации у НПС.
			return super.getCollisionHeight();
		return super.getCollisionHeight() * getCollisionHeightModifier();
	}

	public final double getCollisionHeightModifier()
	{
		return _collisionHeightModifier;
	}

	public final void setCollisionHeightModifier(double value)
	{
		_collisionHeightModifier = value;
	}

	public final double getCollisionRadiusModifier()
	{
		return _collisionRadiusModifier;
	}

	public final void setCollisionRadiusModifier(double value)
	{
		_collisionRadiusModifier = value;
	}

	@Override
	public int getEnchantEffect()
	{
		return _enchantEffect;
	}

	@SuppressWarnings("unused")
	public final boolean isNoSleepMode()
	{
		return _isNoSleepMode;
	}

	@Override
	public boolean isImmortal()
	{
		return _isImmortal;
	}
}