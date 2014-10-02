package l2s.gameserver.model;

import static l2s.gameserver.network.l2.s2c.ExSetCompassZoneCode.ZONE_ALTERED_FLAG;
import static l2s.gameserver.network.l2.s2c.ExSetCompassZoneCode.ZONE_PEACE_FLAG;
import static l2s.gameserver.network.l2.s2c.ExSetCompassZoneCode.ZONE_PVP_FLAG;
import static l2s.gameserver.network.l2.s2c.ExSetCompassZoneCode.ZONE_SIEGE_FLAG;
import static l2s.gameserver.network.l2.s2c.ExSetCompassZoneCode.ZONE_SSQ_FLAG;
import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.iterator.TIntLongIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntLongMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import l2s.commons.collections.LazyArrayList;
import l2s.commons.dao.JdbcEntityState;
import l2s.commons.dbutils.DbUtils;
import l2s.commons.lang.reference.HardReference;
import l2s.commons.lang.reference.HardReferences;
import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.Announcements;
import l2s.gameserver.Config;
import l2s.gameserver.GameTimeController;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.PlayableAI.AINextAction;
import l2s.gameserver.ai.PlayerAI;
import l2s.gameserver.dao.AccountBonusDAO;
import l2s.gameserver.dao.AccountVariablesDAO;
import l2s.gameserver.dao.CharacterDAO;
import l2s.gameserver.dao.CharacterGroupReuseDAO;
import l2s.gameserver.dao.CharacterPostFriendDAO;
import l2s.gameserver.dao.CharacterSubclassDAO;
import l2s.gameserver.dao.CharacterVariablesDAO;
import l2s.gameserver.dao.CustomHeroDAO;
import l2s.gameserver.dao.EffectsDAO;
import l2s.gameserver.dao.LfcDAO;
import l2s.gameserver.dao.LfcDAO.Arenas;
import l2s.gameserver.dao.PremiumAccountRatesHolder;
import l2s.gameserver.dao.PremiumAccountRatesHolder.PremiumInfo;
import l2s.gameserver.dao.SummonsDAO;
import l2s.gameserver.data.xml.holder.EventHolder;
import l2s.gameserver.data.xml.holder.HennaHolder;
import l2s.gameserver.data.xml.holder.InstantZoneHolder;
import l2s.gameserver.data.xml.holder.LevelUpRewardHolder;
import l2s.gameserver.data.xml.holder.NpcHolder;
import l2s.gameserver.data.xml.holder.PetDataHolder;
import l2s.gameserver.data.xml.holder.PlayerTemplateHolder;
import l2s.gameserver.data.xml.holder.ProductDataHolder;
import l2s.gameserver.data.xml.holder.RecipeHolder;
import l2s.gameserver.data.xml.holder.ResidenceHolder;
import l2s.gameserver.data.xml.holder.SkillAcquireHolder;
import l2s.gameserver.data.xml.holder.TransformTemplateHolder;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.database.mysql;
import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.handler.bbs.CommunityBoardManager;
import l2s.gameserver.handler.bbs.ICommunityBoardHandler;
import l2s.gameserver.handler.items.IItemHandler;
import l2s.gameserver.idfactory.IdFactory;
import l2s.gameserver.instancemanager.BotCheckManager;
import l2s.gameserver.instancemanager.BotCheckManager.BotCheckQuestion;
import l2s.gameserver.instancemanager.BypassManager;
import l2s.gameserver.instancemanager.BypassManager.BypassType;
import l2s.gameserver.instancemanager.BypassManager.DecodedBypass;
import l2s.gameserver.instancemanager.CursedWeaponsManager;
import l2s.gameserver.instancemanager.DimensionalRiftManager;
import l2s.gameserver.instancemanager.LfcManager;
import l2s.gameserver.instancemanager.MatchingRoomManager;
import l2s.gameserver.instancemanager.PvPRewardManager;
import l2s.gameserver.instancemanager.PartySubstituteManager;
import l2s.gameserver.instancemanager.QuestManager;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.instancemanager.WorldStatisticsManager;
import l2s.gameserver.instancemanager.games.HandysBlockCheckerManager;
import l2s.gameserver.instancemanager.games.HandysBlockCheckerManager.ArenaParticipantsHolder;
import l2s.gameserver.listener.actor.player.OnAnswerListener;
import l2s.gameserver.listener.actor.player.impl.BotCheckAnswerListner;
import l2s.gameserver.listener.actor.player.impl.ReviveAnswerListener;
import l2s.gameserver.listener.actor.player.impl.ScriptAnswerListener;
import l2s.gameserver.listener.actor.player.impl.SummonAnswerListener;
import l2s.gameserver.model.GameObjectTasks.EndSitDownTask;
import l2s.gameserver.model.GameObjectTasks.EndStandUpTask;
import l2s.gameserver.model.GameObjectTasks.HourlyTask;
import l2s.gameserver.model.GameObjectTasks.KickTask;
import l2s.gameserver.model.GameObjectTasks.PvPFlagTask;
import l2s.gameserver.model.GameObjectTasks.RecomBonusTask;
import l2s.gameserver.model.GameObjectTasks.UnJailTask;
import l2s.gameserver.model.GameObjectTasks.WaterTask;
import l2s.gameserver.model.Request.L2RequestType;
import l2s.gameserver.model.Skill.AddedSkill;
import l2s.gameserver.model.Zone.ZoneType;
import l2s.gameserver.model.actor.basestats.PlayerBaseStats;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.model.actor.instances.player.AntiFlood;
import l2s.gameserver.model.actor.instances.player.BlockList;
import l2s.gameserver.model.actor.instances.player.Bonus;
import l2s.gameserver.model.actor.instances.player.BookMarkList;
import l2s.gameserver.model.actor.instances.player.CharacterVariable;
import l2s.gameserver.model.actor.instances.player.DeathPenalty;
import l2s.gameserver.model.actor.instances.player.FriendList;
import l2s.gameserver.model.actor.instances.player.Macro;
import l2s.gameserver.model.actor.instances.player.MacroList;
import l2s.gameserver.model.actor.instances.player.MenteeList;
import l2s.gameserver.model.actor.instances.player.Mount;
import l2s.gameserver.model.actor.instances.player.PremiumItem;
import l2s.gameserver.model.actor.instances.player.PremiumItemList;
import l2s.gameserver.model.actor.instances.player.RecomBonus;
import l2s.gameserver.model.actor.instances.player.ShortCut;
import l2s.gameserver.model.actor.instances.player.ShortCutList;
import l2s.gameserver.model.actor.instances.player.SubClass;
import l2s.gameserver.model.actor.instances.player.SubClassList;
import l2s.gameserver.model.actor.listener.PlayerListenerList;
import l2s.gameserver.model.actor.recorder.PlayerStatsChangeRecorder;
import l2s.gameserver.model.base.AcquireType;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.ClassType;
import l2s.gameserver.model.base.Element;
import l2s.gameserver.model.base.Experience;
import l2s.gameserver.model.base.InvisibleType;
import l2s.gameserver.model.base.MountType;
import l2s.gameserver.model.base.PetType;
import l2s.gameserver.model.base.PlayerAccess;
import l2s.gameserver.model.base.PledgeRank;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.base.RestartType;
import l2s.gameserver.model.base.Sex;
import l2s.gameserver.model.base.SubClassType;
import l2s.gameserver.model.base.TeamType;
import l2s.gameserver.model.base.TransformType;
import l2s.gameserver.model.entity.DimensionalRift;
import l2s.gameserver.model.entity.Hero;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.entity.boat.ClanAirShip;
import l2s.gameserver.model.entity.events.GlobalEvent;
import l2s.gameserver.model.entity.events.impl.ChaosFestivalEvent;
import l2s.gameserver.model.entity.events.impl.DuelEvent;
import l2s.gameserver.model.entity.events.impl.FightBattleEvent;
import l2s.gameserver.model.entity.events.impl.SiegeEvent;
import l2s.gameserver.model.entity.events.objects.ChaosFestivalPlayerObject;
import l2s.gameserver.model.entity.events.objects.FightBattleArenaObject;
import l2s.gameserver.model.entity.olympiad.Olympiad;
import l2s.gameserver.model.entity.olympiad.OlympiadGame;
import l2s.gameserver.model.entity.residence.Castle;
import l2s.gameserver.model.entity.residence.ClanHall;
import l2s.gameserver.model.entity.residence.Fortress;
import l2s.gameserver.model.entity.residence.Residence;
import l2s.gameserver.model.entity.residence.ResidenceSide;
import l2s.gameserver.model.instances.DecoyInstance;
import l2s.gameserver.model.instances.GuardInstance;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.instances.PetBabyInstance;
import l2s.gameserver.model.instances.PetInstance;
import l2s.gameserver.model.instances.ReflectionBossInstance;
import l2s.gameserver.model.instances.StaticObjectInstance;
import l2s.gameserver.model.instances.SummonInstance;
import l2s.gameserver.model.instances.SummonInstance.RestoredSummon;
import l2s.gameserver.model.instances.SymbolInstance;
import l2s.gameserver.model.instances.TamedBeastInstance;
import l2s.gameserver.model.instances.TrapInstance;
import l2s.gameserver.model.items.Inventory;
import l2s.gameserver.model.items.ItemContainer;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.items.LockType;
import l2s.gameserver.model.items.ManufactureItem;
import l2s.gameserver.model.items.PcFreight;
import l2s.gameserver.model.items.PcInventory;
import l2s.gameserver.model.items.PcRefund;
import l2s.gameserver.model.items.PcWarehouse;
import l2s.gameserver.model.items.TradeItem;
import l2s.gameserver.model.items.Warehouse;
import l2s.gameserver.model.items.Warehouse.WarehouseType;
import l2s.gameserver.model.items.attachment.FlagItemAttachment;
import l2s.gameserver.model.items.attachment.PickableAttachment;
import l2s.gameserver.model.matching.MatchingRoom;
import l2s.gameserver.model.petition.PetitionMainGroup;
import l2s.gameserver.model.pledge.Alliance;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.model.pledge.Privilege;
import l2s.gameserver.model.pledge.RankPrivs;
import l2s.gameserver.model.pledge.SubUnit;
import l2s.gameserver.model.pledge.UnitMember;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestEventType;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.model.worldstatistics.CategoryType;
import l2s.gameserver.network.authcomm.AuthServerCommunication;
import l2s.gameserver.network.authcomm.gs2as.ReduceAccountPoints;
import l2s.gameserver.network.l2.GameClient;
import l2s.gameserver.network.l2.components.ChatType;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.components.IStaticPacket;
import l2s.gameserver.network.l2.components.SceneMovie;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.AbnormalStatusUpdatePacket;
import l2s.gameserver.network.l2.s2c.ActionFailPacket;
import l2s.gameserver.network.l2.s2c.AutoAttackStartPacket;
import l2s.gameserver.network.l2.s2c.CameraMode;
import l2s.gameserver.network.l2.s2c.ChairSit;
import l2s.gameserver.network.l2.s2c.ChangeWaitTypePacket;
import l2s.gameserver.network.l2.s2c.CIPacket;
import l2s.gameserver.network.l2.s2c.ConfirmDlgPacket;
import l2s.gameserver.network.l2.s2c.EtcStatusUpdatePacket;
import l2s.gameserver.network.l2.s2c.AcquireSkillListPacket;
import l2s.gameserver.network.l2.s2c.ExAcquireAPSkillList;
import l2s.gameserver.network.l2.s2c.ExAdenaInvenCount;
import l2s.gameserver.network.l2.s2c.ExAlterSkillRequest;
import l2s.gameserver.network.l2.s2c.ExAutoSoulShot;
import l2s.gameserver.network.l2.s2c.ExBR_AgathionEnergyInfo;
import l2s.gameserver.network.l2.s2c.ExBasicActionList;
import l2s.gameserver.network.l2.s2c.ExNewSkillToLearnByLevelUp;
import l2s.gameserver.network.l2.s2c.ExNotifyPremiumItem;
import l2s.gameserver.network.l2.s2c.ExOlympiadMatchEndPacket;
import l2s.gameserver.network.l2.s2c.ExOlympiadModePacket;
import l2s.gameserver.network.l2.s2c.ExOlympiadSpelledInfoPacket;
import l2s.gameserver.network.l2.s2c.ExPCCafePointInfoPacket;
import l2s.gameserver.network.l2.s2c.ExQuestItemListPacket;
import l2s.gameserver.network.l2.s2c.ExSetCompassZoneCode;
import l2s.gameserver.network.l2.s2c.ExStartScenePlayer;
import l2s.gameserver.network.l2.s2c.ExStorageMaxCountPacket;
import l2s.gameserver.network.l2.s2c.ExSubjobInfo;
import l2s.gameserver.network.l2.s2c.ExTeleportToLocationActivate;
import l2s.gameserver.network.l2.s2c.ExUserInfoCubic;
import l2s.gameserver.network.l2.s2c.ExUserInfoInvenWeight;
import l2s.gameserver.network.l2.s2c.ExUseSharedGroupItem;
import l2s.gameserver.network.l2.s2c.ExVitalityEffectInfo;
import l2s.gameserver.network.l2.s2c.ExVitalityPointInfo;
import l2s.gameserver.network.l2.s2c.ExVoteSystemInfoPacket;
import l2s.gameserver.network.l2.s2c.ExWaitWaitingSubStituteInfo;
import l2s.gameserver.network.l2.s2c.ExWorldChatCnt;
import l2s.gameserver.network.l2.s2c.GetItemPacket;
import l2s.gameserver.network.l2.s2c.HennaInfoPacket;
import l2s.gameserver.network.l2.s2c.InventoryUpdatePacket;
import l2s.gameserver.network.l2.s2c.ItemListPacket;
import l2s.gameserver.network.l2.s2c.L2GameServerPacket;
import l2s.gameserver.network.l2.s2c.LogOutOkPacket;
import l2s.gameserver.network.l2.s2c.MagicSkillLaunchedPacket;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
import l2s.gameserver.network.l2.s2c.MyTargetSelectedPacket;
import l2s.gameserver.network.l2.s2c.NpcInfoPoly;
import l2s.gameserver.network.l2.s2c.ObserverEnd;
import l2s.gameserver.network.l2.s2c.ObserverStart;
import l2s.gameserver.network.l2.s2c.PartySmallWindowUpdatePacket;
import l2s.gameserver.network.l2.s2c.PartySpelledPacket;
import l2s.gameserver.network.l2.s2c.PetDeletePacket;
import l2s.gameserver.network.l2.s2c.PledgeShowMemberListDelete;
import l2s.gameserver.network.l2.s2c.PledgeShowMemberListDeleteAllPacket;
import l2s.gameserver.network.l2.s2c.PledgeShowMemberListUpdatePacket;
import l2s.gameserver.network.l2.s2c.PrivateStoreListBuy;
import l2s.gameserver.network.l2.s2c.PrivateStoreList;
import l2s.gameserver.network.l2.s2c.PrivateStoreMsgBuy;
import l2s.gameserver.network.l2.s2c.PrivateStoreMsg;
import l2s.gameserver.network.l2.s2c.QuestListPacket;
import l2s.gameserver.network.l2.s2c.RadarControlPacket;
import l2s.gameserver.network.l2.s2c.RecipeShopMsg;
import l2s.gameserver.network.l2.s2c.RecipeShopSellList;
import l2s.gameserver.network.l2.s2c.RelationChangedPacket;
import l2s.gameserver.network.l2.s2c.RevivePacket;
import l2s.gameserver.network.l2.s2c.TradeDonePacket;
import l2s.gameserver.network.l2.s2c.ServerCloseSocketPacket;
import l2s.gameserver.network.l2.s2c.SetupGaugePacket;
import l2s.gameserver.network.l2.s2c.ShortBuffStatusUpdatePacket;
import l2s.gameserver.network.l2.s2c.ShortCutInitPacket;
import l2s.gameserver.network.l2.s2c.ShortCutRegisterPacket;
import l2s.gameserver.network.l2.s2c.SkillCoolTimePacket;
import l2s.gameserver.network.l2.s2c.SkillListPacket;
import l2s.gameserver.network.l2.s2c.Snoop;
import l2s.gameserver.network.l2.s2c.SocialActionPacket;
import l2s.gameserver.network.l2.s2c.ExSpawnEmitterPacket;
import l2s.gameserver.network.l2.s2c.SpecialCamera;
import l2s.gameserver.network.l2.s2c.StatusUpdatePacket;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.network.l2.s2c.TargetSelectedPacket;
import l2s.gameserver.network.l2.s2c.TargetUnselectedPacket;
import l2s.gameserver.network.l2.s2c.TeleportToLocationPacket;
import l2s.gameserver.network.l2.s2c.UIPacket;
import l2s.gameserver.network.l2.s2c.ValidateLocationPacket;
import l2s.gameserver.scripts.Events;
import l2s.gameserver.skills.AbnormalEffect;
import l2s.gameserver.skills.AbnormalType;
import l2s.gameserver.skills.EffectType;
import l2s.gameserver.skills.TimeStamp;
import l2s.gameserver.skills.combo.SkillComboType;
import l2s.gameserver.skills.effects.EffectCubic;
import l2s.gameserver.skills.skillclasses.Charge;
import l2s.gameserver.skills.skillclasses.Summon;
import l2s.gameserver.skills.skillclasses.Transformation;
import l2s.gameserver.stats.Formulas;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.stats.funcs.FuncTemplate;
import l2s.gameserver.tables.ClanTable;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.tables.SkillTreeTable;
import l2s.gameserver.taskmanager.AutoSaveManager;
import l2s.gameserver.taskmanager.LazyPrecisionTaskManager;
import l2s.gameserver.templates.FishTemplate;
import l2s.gameserver.templates.Henna;
import l2s.gameserver.templates.InstantZone;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.templates.item.ArmorTemplate.ArmorType;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.item.ItemType;
import l2s.gameserver.templates.item.RecipeTemplate;
import l2s.gameserver.templates.item.WeaponTemplate;
import l2s.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2s.gameserver.templates.jump.JumpTrack;
import l2s.gameserver.templates.jump.JumpWay;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.AdminFunctions;
import l2s.gameserver.templates.CreatureTemplate;
import l2s.gameserver.templates.pet.PetData;
import l2s.gameserver.templates.player.PlayerTemplate;
import l2s.gameserver.templates.player.transform.TransformTemplate;
import l2s.gameserver.utils.Clients;
import l2s.gameserver.utils.EffectsComparator;
//import l2s.gameserver.utils.GameStats;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Language;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.Log;
import l2s.gameserver.utils.Mentoring;
import l2s.gameserver.utils.SiegeUtils;
import l2s.gameserver.utils.SqlBatch;
import l2s.gameserver.utils.Strings;
import l2s.gameserver.utils.TeleportUtils;

import org.napile.primitive.Containers;
import org.napile.primitive.maps.IntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Player extends Playable implements PlayerGroup
{
	private static final long serialVersionUID = 1L;

	public static final int DEFAULT_TITLE_COLOR = 0xFFFF77;
	public static final int MAX_POST_FRIEND_SIZE = 100;

	private static final Logger _log = LoggerFactory.getLogger(Player.class);

	public static final String NO_TRADERS_VAR = "notraders";
	public static final String NO_ANIMATION_OF_CAST_VAR = "notShowBuffAnim";
	public static final String MY_BIRTHDAY_RECEIVE_YEAR = "MyBirthdayReceiveYear";
	private static final String NOT_CONNECTED = "<not connected>";
	private static final String RECENT_PRODUCT_LIST_VAR = "recentProductList";
	private static final String LVL_UP_REWARD_VAR = "@lvl_up_reward";

	public static final int MAX_VITALITY_POINTS = 140000;

	private static final String PK_KILL_VAR = "@pk_kill";

	public final static int OBSERVER_NONE = 0;
	public final static int OBSERVER_STARTING = 1;
	public final static int OBSERVER_STARTED = 3;
	public final static int OBSERVER_LEAVING = 2;

	public static final int STORE_PRIVATE_NONE = 0;
	public static final int STORE_PRIVATE_SELL = 1;
	public static final int STORE_PRIVATE_BUY = 3;
	public static final int STORE_PRIVATE_MANUFACTURE = 5;
	public static final int STORE_OBSERVING_GAMES = 7;
	public static final int STORE_PRIVATE_SELL_PACKAGE = 8;

	public static final int[] EXPERTISE_LEVELS = { 0, 20, 40, 52, 61, 76, 80, 84, 85, 95, 99, Integer.MAX_VALUE };

	private PlayerTemplate _baseTemplate;

	private GameClient _connection;
	private String _login;

	private int _karma, _pkKills, _pvpKills;
	private int _face, _hairStyle, _hairColor;
	private int _beautyFace, _beautyHairStyle, _beautyHairColor;
	private int _recomHave, _recomLeftToday, _fame;
	private int _recomLeft = 20;
	private int _recomBonusTime = 3600;
	private boolean _isHourglassEffected, _isRecomTimerActive;
	private boolean _isUndying = false;
	private int _deleteTimer;
	private boolean _isVoting = false;
	private boolean _hasFlagCTF = false;

	private long _createTime, _onlineTime, _onlineBeginTime, _leaveClanTime, _deleteClanTime, _NoChannel, _NoChannelBegin;
	private long _uptime;
	private LfcManager lfcGame = null;
	/**
	 * Time on login in game
	 */
	private long _lastAccess;

	/**
	 * The Color of players name / title (white is 0xFFFFFF)
	 */
	private int _nameColor, _titlecolor;

	private boolean _overloaded;

	boolean sittingTaskLaunched;

	/**
	 * Time counter when L2Player is sitting
	 */
	private int _waitTimeWhenSit;

	private boolean _autoLoot = Config.AUTO_LOOT, AutoLootHerbs = Config.AUTO_LOOT_HERBS, _autoLootOnlyAdena = Config.AUTO_LOOT_ONLY_ADENA;

	private final PcInventory _inventory = new PcInventory(this);
	private final Warehouse _warehouse = new PcWarehouse(this);
	private final ItemContainer _refund = new PcRefund(this);
	private final PcFreight _freight = new PcFreight(this);

	private final BookMarkList _bookmarks = new BookMarkList(this, 0);

	private final AntiFlood _antiFlood = new AntiFlood(this);

	private final Map<Integer, RecipeTemplate> _recipebook = new TreeMap<Integer, RecipeTemplate>();
	private final Map<Integer, RecipeTemplate> _commonrecipebook = new TreeMap<Integer, RecipeTemplate>();

	/**
	 * The table containing all Quests began by the L2Player
	 */
	private final Map<String, QuestState> _quests = new HashMap<String, QuestState>();

	/**
	 * The list containing all shortCuts of this L2Player
	 */
	private final ShortCutList _shortCuts = new ShortCutList(this);

	/**
	 * The list containing all macroses of this L2Player
	 */
	private final MacroList _macroses = new MacroList(this);

	/**
	 * The list containing all subclasses of this L2Player
	 */
	private final SubClassList _subClassList = new SubClassList(this);

	/**
	 * The Private Store type of the L2Player (STORE_PRIVATE_NONE=0, STORE_PRIVATE_SELL=1, sellmanage=2, STORE_PRIVATE_BUY=3, buymanage=4, STORE_PRIVATE_MANUFACTURE=5)
	 */
	private int _privatestore;
	/**
	 * Данные для магазина рецептов
	 */
	private String _manufactureName;
	private List<ManufactureItem> _createList = Collections.emptyList();
	/**
	 * Данные для магазина продажи
	 */
	private String _sellStoreName;
	private List<TradeItem> _sellList = Collections.emptyList();
	private List<TradeItem> _packageSellList = Collections.emptyList();
	/**
	 * Данные для магазина покупки
	 */
	private String _buyStoreName;
	private List<TradeItem> _buyList = Collections.emptyList();
	/**
	 * Данные для обмена
	 */
	private List<TradeItem> _tradeList = Collections.emptyList();

	/**
	 * hennas
	 */
	private final Henna[] _henna = new Henna[3];
	private int _hennaSTR, _hennaINT, _hennaDEX, _hennaMEN, _hennaWIT, _hennaCON, _hennaLUC, _hennaCHA;
	private TIntObjectMap<Skill> _hennaSkills = new TIntObjectHashMap<Skill>();

	private Party _party;
	private Location _lastPartyPosition;
	private long _startingTimeInFullParty = 0;
	private long _startingTimeInParty = 0;

	private Clan _clan;
	private PledgeRank _pledgeRank = PledgeRank.VAGABOND;
	private int _pledgeType = Clan.SUBUNIT_NONE, _powerGrade = 0, _lvlJoinedAcademy = 0, _apprentice = 0;

	/**
	 * GM Stuff
	 */
	private int _accessLevel;
	private PlayerAccess _playerAccess = new PlayerAccess();

	private boolean _messageRefusal = false, _tradeRefusal = false, _blockAll = false, _pending_lfc = false, _pending_lfc_start = false;

	private boolean _InTvT = false;
	private boolean _inCtF = false;
	private boolean _inLastHero = false;

	/**
	 * The L2Summon of the L2Player
	 */
	public static final int MAX_SUMMON_COUNT = 4;
	private TIntObjectHashMap<SummonInstance> _summons = new TIntObjectHashMap<SummonInstance>(MAX_SUMMON_COUNT); // objId is index
	private PetInstance _pet = null;
	private SymbolInstance _symbol = null;

	private boolean _riding;
	
	private int _botRating;

	private List<DecoyInstance> _decoys = new CopyOnWriteArrayList<DecoyInstance>();

	private Map<Integer, EffectCubic> _cubics = null;
	private int _agathionId = 0;

	private Request _request;

	private ItemInstance _arrowItem;

	/**
	 * The fists L2Weapon of the L2Player (used when no weapon is equipped)
	 */
	private WeaponTemplate _fistsWeaponItem;

	private Map<Integer, String> _chars = new HashMap<Integer, String>(8);

	/**
	 * The current higher Expertise of the L2Player (None=0, D=1, C=2, B=3, A=4, S=5, S80=6, S84=7)
	 */
	public int expertiseIndex = 0;

	private ItemInstance _enchantScroll = null;
	private ItemInstance _appearanceStone = null;
	private ItemInstance _appearanceExtractItem = null;

	private WarehouseType _usingWHType;

	private boolean _isOnline = false;

	private AtomicBoolean _isLogout = new AtomicBoolean();

	/**
	 * The L2NpcInstance corresponding to the last Folk which one the player talked.
	 */
	private HardReference<NpcInstance> _lastNpc = HardReferences.emptyRef();
	/**
	 * тут храним мультиселл с которым работаем
	 */
	private MultiSellListContainer _multisell = null;

	private Set<Integer> _activeSoulShots = new CopyOnWriteArraySet<Integer>();

	private WorldRegion _observerRegion;
	private AtomicInteger _observerMode = new AtomicInteger(0);

	public int _telemode = 0;

	private int _handysBlockCheckerEventArena = -1;

	public boolean entering = true;

	/**
	 * Эта точка проверяется при нештатном выходе чара, и если не равна null чар возвращается в нее
	 * Используется например для возвращения при падении с виверны
	 * Поле heading используется для хранения денег возвращаемых при сбое
	 */
	private Location _stablePoint = null;

	/**
	 * new loto ticket *
	 */
	public int _loto[] = new int[5];
	/**
	 * new race ticket *
	 */
	public int _race[] = new int[2];

	private final BlockList _blockList = new BlockList(this);
	private final FriendList _friendList = new FriendList(this);
	private final MenteeList _menteeList = new MenteeList(this);
	private final PremiumItemList _premiumItemList = new PremiumItemList(this);

	private boolean _hero = false;

	private Bonus _bonus = new Bonus();
	private Future<?> _bonusExpiration;

	private boolean _isSitting;
	private StaticObjectInstance _sittingObject;

	private boolean _noble = false;

	private boolean _inOlympiadMode;
	private OlympiadGame _olympiadGame;
	private OlympiadGame _olympiadObserveGame;

	private int _olympiadSide = -1;

	/**
	 * ally with ketra or varka related wars
	 */
	private int _varka = 0;
	private int _ketra = 0;
	private int _ram = 0;

	private byte[] _keyBindings = ArrayUtils.EMPTY_BYTE_ARRAY;

	private int _cursedWeaponEquippedId = 0;

	private final Fishing _fishing = new Fishing(this);
	private boolean _isFishing;

	private Future<?> _taskWater;
	private Future<?> _autoSaveTask;
	private Future<?> _kickTask;

	private Future<?> _pcCafePointsTask;
	private Future<?> _unjailTask;

	private final Lock _storeLock = new ReentrantLock();

	private int _zoneMask;

	private boolean _offline = false;

	private boolean _registeredInEvent = false;
	
	private long _destructionCount = 0; //fix me should be on 6:30 every morning, right now set apon the server restart (not stored at all!)
	private long _markEndureCount = 0; //fix me should be on 6:30 every morning, right now set apon the server restart (not stored at all!)
	
	private int _pcBangPoints;

	private int _expandInventory = 0;
	private int _expandWarehouse = 0;
	private int _battlefieldChatId;
	private int _lectureMark;
	private InvisibleType _invisibleType = InvisibleType.NONE;

	private List<String> bypasses = null, bypasses_bbs = null;
	private IntObjectMap<String> _postFriends = Containers.emptyIntObjectMap();

	private List<String> _blockedActions = new ArrayList<String>();

	private boolean _notShowBuffAnim = false;
	private boolean _notShowTraders = false;
	private boolean _debug = false;

	private long _dropDisabled;
	private long _lastItemAuctionInfoRequest;

	private Pair<Integer, OnAnswerListener> _askDialog = null;

	private MatchingRoom _matchingRoom;
	private PetitionMainGroup _petitionGroup;
	private final Map<Integer, Long> _instancesReuses = new ConcurrentHashMap<Integer, Long>();

	private Language _language = Config.DEFAULT_LANG;

	private JumpTrack _currentJumpTrack = null;
	private JumpWay _currentJumpWay = null;

	private TIntSet _disabledAnalogSkills = new TIntHashSet();

	private int _npcDialogEndTime = 0;

	private Mount _mount = null;

	private final Map<String, CharacterVariable> _variables = new ConcurrentHashMap<String, CharacterVariable>();

	private final DeathPenalty _deathPenalty = new DeathPenalty(this);

	private List<RestoredSummon> _restoredSummons = null;

	private TIntObjectMap<SkillChain> _skillChainDetail = new TIntObjectHashMap<SkillChain>();

	private boolean _autoSearchParty;
	private Future<?> _substituteTask;

	private JumpState _jumpState = JumpState.NONE;

	private TransformTemplate _transform = null;

	private final TIntObjectMap<Skill> _transformSkills = new TIntObjectHashMap<Skill>();

	private long _lastMultisellBuyTime = 0L;
	private long _lastEnchantItemTime = 0L;

	private boolean _isInReplaceTeleport = false;

	private int _usedWorldChatPoints = 0;

	private final TIntSet _questTeleportMark = new TIntHashSet();

	public static enum JumpState
	{
		NONE,
		IN_PROGRESS,
		FINISHED;
	}

	/**
	 * Конструктор для L2Player. Напрямую не вызывается, для создания игрока используется PlayerManager.create
	 */
	public Player(final int objectId, final PlayerTemplate template, final String accountName)
	{
		super(objectId, template);

//		remove ip check
//		boolean good = false;
//		for(String ip : Clients.getIps())
//		{
//			if(Config.EXTERNAL_HOSTNAME.equalsIgnoreCase(ip))
//				good = true;
//		}
//		if(!good)
//			return;

		_baseTemplate = template;
		_login = accountName;
		_nameColor = 0xFFFFFF;
		_titlecolor = 0xFFFF77;
	}

	/**
	 * Constructor<?> of L2Player (use L2Character constructor).<BR><BR>
	 * <p/>
	 * <B><U> Actions</U> :</B><BR><BR>
	 * <li>Call the L2Character constructor to create an empty _skills slot and copy basic Calculator set to this L2Player </li>
	 * <li>Create a L2Radar object</li>
	 * <li>Retrieve from the database all items of this L2Player and add them to _inventory </li>
	 * <p/>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SET the account name of the L2Player</B></FONT><BR><BR>
	 *
	 * @param objectId Identifier of the object to initialized
	 * @param template The L2PlayerTemplate to apply to the L2Player
	 */
	private Player(final int objectId, final PlayerTemplate template)
	{
		this(objectId, template, null);

		_baseTemplate = template;
		_ai = new PlayerAI(this);

		if(!Config.EVERYBODY_HAS_ADMIN_RIGHTS)
			setPlayerAccess(Config.gmlist.get(objectId));
		else
			setPlayerAccess(Config.gmlist.get(0));
	}

	@SuppressWarnings("unchecked")
	@Override
	public HardReference<Player> getRef()
	{
		return (HardReference<Player>) super.getRef();
	}

	public String getAccountName()
	{
		if(_connection == null)
			return _login;
		return _connection.getLogin();
	}

	public String getIP()
	{
		if(_connection == null)
			return NOT_CONNECTED;
		return _connection.getIpAddr();
	}

	public String getLogin()
	{
		return _login;
	}

	public void setLogin(String val)
	{
		_login = val;
	}

	/**
	 * Возвращает список персонажей на аккаунте, за исключением текущего
	 *
	 * @return Список персонажей
	 */
	public Map<Integer, String> getAccountChars()
	{
		return _chars;
	}

	@Override
	public final PlayerTemplate getTemplate()
	{
		return (PlayerTemplate) super.getTemplate();
	}

	@Override
	public final void setTemplate(CreatureTemplate template)
	{
		if(isBaseClassActive())
			_baseTemplate = (PlayerTemplate) template;

		super.setTemplate(template);
	}

	public final PlayerTemplate getBaseTemplate()
	{
		return _baseTemplate;
	}

	@Override
	public final boolean isTransformed()
	{
		return _transform != null;
	}

	@Override
	public final TransformTemplate getTransform()
	{
		return _transform;
	}

	@Override
	public final void setTransform(int id)
	{
		TransformTemplate template = id > 0 ? TransformTemplateHolder.getInstance().getTemplate(getSex(), id) : null;
		setTransform(template);
	}

	@Override
	public final void setTransform(TransformTemplate transform)
	{
		if(transform == _transform || transform != null && _transform != null)
			return;

		boolean isFlying = false;
		final boolean isVisible = isVisible();

		// Для каждой трансформации свой набор скилов
		if(transform == null) // Обычная форма
		{
			isFlying = _transform.getType() == TransformType.FLYING;

			if(isFlying)
			{
				decayMe();
				setFlying(false);
				setLoc(getLoc().correctGeoZ());
			}

			if(!_transformSkills.isEmpty())
			{
				// Удаляем скилы трансформации
				for(Skill skill : _transformSkills.valueCollection())
				{
					if(!SkillAcquireHolder.getInstance().isSkillPossible(this, skill))
						super.removeSkill(skill);
				}
				_transformSkills.clear();
			}

			if(_transform.getItemCheckType() != LockType.NONE)
				getInventory().unlock();

			_transform = transform;

			checkActiveToggleEffects();

			// Останавливаем текущий эффект трансформации
			getEffectList().stopEffects(EffectType.Transformation);
		}
		else
		{
			isFlying = transform.getType() == TransformType.FLYING;

			if(isFlying)
			{
				Servitor[] servitors = getServitors();
				if(servitors.length > 0)
				{
					for(Servitor servitor : servitors)
						servitor.unSummon(false);
				}
	
				decayMe();
				setFlying(true);
				setLoc(getLoc().changeZ(transform.getSpawnHeight())); // Немного поднимаем чара над землей

				// Летучим трансформациям добавляем скиллы коллекционирования камней, если он выучен.
				for(Skill skill : getAllSkillsArray())
				{
					if(!SkillAcquireHolder.getInstance().isSkillPossible(this, skill, AcquireType.COLLECTION))
						continue;

					_transformSkills.put(skill.getId(), skill);
				}
			}

			// Добавляем скиллы трансформации
			for(SkillLearn sl : transform.getSkills())
			{
				Skill skill = SkillTable.getInstance().getInfo(sl.getId(), sl.getLevel());
				if(skill == null)
					continue;

				_transformSkills.put(skill.getId(), skill);
			}

			// Добавляем скиллы трансформации зависящие от уровня персонажа
			for(SkillLearn sl : transform.getAddtionalSkills())
			{
				if(sl.getMinLevel() > getLevel())
					continue;

				Skill skill = _transformSkills.get(sl.getId());
				if(skill != null && skill.getLevel() >= sl.getLevel())
					continue;

				skill = SkillTable.getInstance().getInfo(sl.getId(), sl.getLevel());
				if(skill == null)
					continue;

				_transformSkills.put(skill.getId(), skill);
			}

			if(!isInOlympiadMode() && isCursedWeaponEquipped() && isHero() && isBaseClassActive())
			{
				// Добавляем хиро скиллы проклятому трансформу
				for(SkillLearn sl : SkillAcquireHolder.getInstance().getAvailableMaxLvlSkills(null, AcquireType.HERO))
				{
					Skill skill = SkillTable.getInstance().getInfo(sl.getId(), sl.getLevel());
					if(skill == null)
						continue;

					skill = _transformSkills.get(sl.getId());
					if(skill != null && skill.getLevel() >= sl.getLevel())
						continue;

					_transformSkills.put(skill.getId(), skill);
				}
			}

			for(Skill skill : _transformSkills.valueCollection())
				addSkill(skill, false);

			if(transform.getItemCheckType() != LockType.NONE)
			{
				getInventory().unlock();
				getInventory().lockItems(transform.getItemCheckType(), transform.getItemCheckIDs());
			}

			checkActiveToggleEffects();

			_transform = transform;
		}

		sendPacket(new ExBasicActionList(this));
		sendPacket(new SkillListPacket(this));
		sendPacket(new ShortCutInitPacket(this));

		for(int shotId : getAutoSoulShot())
			sendPacket(new ExAutoSoulShot(shotId, true));

		if(isFlying && isVisible)
			spawnMe();

		sendChanges();
	}

	public void changeSex()
	{
		PlayerTemplate template = PlayerTemplateHolder.getInstance().getPlayerTemplate(getRace(), getClassId(), getSex().revert());
		if(template == null)
			return;

		setTemplate(template);
		if(isTransformed())
		{
			int transformId = getTransform().getId();
			setTransform(null);
			setTransform(transformId);
		}
	}

	@Override
	public PlayerAI getAI()
	{
		return (PlayerAI) _ai;
	}

	@Override
	public void doCast(final Skill skill, final Creature target, boolean forceUse)
	{
		if(skill == null)
			return;

		super.doCast(skill, target, forceUse);

		//if(getUseSeed() != 0 && skill.getSkillType() == SkillType.SOWING)
		//	sendPacket(new ExUseSharedGroupItem(getUseSeed(), getUseSeed(), 5000, 5000));
	}

	@Override
	public void sendReuseMessage(Skill skill)
	{
		if(isCastingNow() && !isDualCastEnable() || isCastingNow() && isDualCastEnable() && isDualCastingNow())
			return;

		TimeStamp sts = getSkillReuse(skill);
		if(sts == null || !sts.hasNotPassed())
			return;
		long timeleft = sts.getReuseCurrent();
		if(!Config.ALT_SHOW_REUSE_MSG && timeleft < 10000 || timeleft < 500)
			return;
		long hours = timeleft / 3600000;
		long minutes = (timeleft - hours * 3600000) / 60000;
		long seconds = (long) Math.ceil((timeleft - hours * 3600000 - minutes * 60000) / 1000.);
		if(hours > 0)
			sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S2_HOURS_S3_MINUTES_AND_S4_SECONDS_REMAINING_IN_S1S_REUSE_TIME).addSkillName(skill.getId(), skill.getDisplayLevel()).addNumber(hours).addNumber(minutes).addNumber(seconds));
		else if(minutes > 0)
			sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S2_MINUTES_S3_SECONDS_REMAINING_IN_S1S_REUSE_TIME).addSkillName(skill.getId(), skill.getDisplayLevel()).addNumber(minutes).addNumber(seconds));
		else
			sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S2_SECONDS_REMAINING_IN_S1S_REUSE_TIME).addSkillName(skill.getId(), skill.getDisplayLevel()).addNumber(seconds));
	}

	@Override
	public final int getLevel()
	{
		return getActiveSubClass() == null ? 1 : getActiveSubClass().getLevel();
	}

	@Override
	public final Sex getSex()
	{
		return getTemplate().getSex();
	}

	public int getFace()
	{
		return _face;
	}

	public void setFace(int face)
	{
		_face = face;
	}

	public int getBeautyFace()
	{
		return _beautyFace;
	}

	public void setBeautyFace(int face)
	{
		_beautyFace = face;
	}

	public int getHairColor()
	{
		return _hairColor;
	}

	public void setHairColor(int hairColor)
	{
		_hairColor = hairColor;
	}

	public int getBeautyHairColor()
	{
		return _beautyHairColor;
	}

	public void setBeautyHairColor(int hairColor)
	{
		_beautyHairColor = hairColor;
	}

	public int getHairStyle()
	{
		return _hairStyle;
	}

	public void setHairStyle(int hairStyle)
	{
		_hairStyle = hairStyle;
	}

	public int getBeautyHairStyle()
	{
		return _beautyHairStyle;
	}

	public void setBeautyHairStyle(int hairStyle)
	{
		_beautyHairStyle = hairStyle;
	}

	public void offline()
	{
		if(_connection != null)
		{
			_connection.setActiveChar(null);
			_connection.close(ServerCloseSocketPacket.STATIC);
			setNetConnection(null);
		}

		setNameColor(Config.SERVICES_OFFLINE_TRADE_NAME_COLOR);
		setOfflineMode(true);

		setVar("offline", (System.currentTimeMillis() / 1000L));

		if(Config.SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK > 0)
			startKickTask(Config.SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK * 1000L);

		Party party = getParty();
		if(party != null)
			leaveParty();

		if(isAutoSearchParty())
		{
			PartySubstituteManager.getInstance().removeWaitingPlayer(this);
		}

		Servitor[] servitors = getServitors();
		if(servitors.length > 0)
		{
			for(Servitor servitor : servitors)
				servitor.unSummon(false);
		}

		CursedWeaponsManager.getInstance().doLogout(this);

		if(isInOlympiadMode())
			Olympiad.logoutPlayer(this);

		if(getPendingLfcStart() && getLfcGame() == null) //--waiting for an oponent
			LfcManager.cancelArenaLogout(this, _arenaIdForLogout);

		if(getLfcGame() != null)
		{
			getLfcGame().endMatch(this);
			checkAndCancelLfcArena(getLfcGame().getArena()); //insurance
		}

		broadcastCharInfo();
		stopWaterTask();
		stopBonusTask();
		stopHourlyTask();
		stopPcBangPointsTask();
		stopAutoSaveTask();
		stopRecomBonusTask(true);
		stopQuestTimers();

		try
		{
			getInventory().store();
		}
		catch(Throwable t)
		{
			_log.error("", t);
		}

		try
		{
			store(false);
		}
		catch(Throwable t)
		{
			_log.error("", t);
		}
	}

	/**
	 * Соединение закрывается, клиент закрывается, персонаж сохраняется и удаляется из игры
	 */
	public void kick()
	{
		prepareToLogout1();
		if(_connection != null)
		{
			_connection.close(LogOutOkPacket.STATIC);
			setNetConnection(null);
		}
		prepareToLogout2();
		deleteMe();
	}

	/**
	 * Соединение не закрывается, клиент не закрывается, персонаж сохраняется и удаляется из игры
	 */
	public void restart()
	{
		prepareToLogout1();
		if(_connection != null)
		{
			_connection.setActiveChar(null);
			setNetConnection(null);
		}
		prepareToLogout2();
		deleteMe();
	}

	/**
	 * Соединение закрывается, клиент не закрывается, персонаж сохраняется и удаляется из игры
	 */
	public void logout()
	{
		prepareToLogout1();
		if(_connection != null)
		{
			_connection.close(ServerCloseSocketPacket.STATIC);
			setNetConnection(null);
		}
		prepareToLogout2();
		deleteMe();
	}

	private void prepareToLogout1()
	{
		Servitor[] servitors = getServitors();
		if(servitors.length > 0)
		{
			for(Servitor servitor : servitors)
				sendPacket(new PetDeletePacket(servitor.getObjectId(), servitor.getServitorType()));
		}
		World.removeObjectsFromPlayer(this);
	}

	private void prepareToLogout2()
	{
		if(_isLogout.getAndSet(true))
			return;

		if(getPendingLfcStart() && getLfcGame() == null) //--waiting for an oponent
			LfcManager.cancelArenaLogout(this, _arenaIdForLogout);

		if(getLfcGame() != null)
		{
			getLfcGame().endMatch(this);
			checkAndCancelLfcArena(getLfcGame().getArena()); //insurance
		}

		setNetConnection(null);
		setIsOnline(false);

		getListeners().onExit();

		if(isFlying() && !checkLandingState())
			_stablePoint = TeleportUtils.getRestartLocation(this, RestartType.TO_VILLAGE);

		if(isCastingNow() || isDualCastingNow())
			abortCast(true, true);

		Party party = getParty();
		if(party != null)
			leaveParty();

		CursedWeaponsManager.getInstance().doLogout(this);

		if(_olympiadObserveGame != null)
			_olympiadObserveGame.removeSpectator(this);

		if(isInOlympiadMode())
			Olympiad.logoutPlayer(this);

		stopFishing();

		if(_stablePoint != null)
			teleToLocation(_stablePoint);

		Servitor[] servitors = getServitors();
		if(servitors.length > 0)
		{
			for(Servitor servitor : servitors)
				servitor.unSummon(true);
		}

		if(isMounted())
			_mount.onLogout();

		_friendList.notifyFriends(false);
		mentoringLogoutConditions();

		if(getClan() != null)
			getClan().loginClanCond(this, false);

		if(isProcessingRequest())
			getRequest().cancel();

		stopAllTimers();

		if(isInBoat())
			getBoat().removePlayer(this);

		SubUnit unit = getSubUnit();
		UnitMember member = unit == null ? null : unit.getUnitMember(getObjectId());
		if(member != null)
		{
			int sponsor = member.getSponsor();
			int apprentice = getApprentice();
			PledgeShowMemberListUpdatePacket memberUpdate = new PledgeShowMemberListUpdatePacket(this);
			for(Player clanMember : _clan.getOnlineMembers(getObjectId()))
			{
				clanMember.sendPacket(memberUpdate);
				if(clanMember.getObjectId() == sponsor)
					clanMember.sendPacket(new SystemMessage(SystemMessage.S1_YOUR_CLAN_ACADEMYS_APPRENTICE_HAS_LOGGED_OUT).addString(_name));
				else if(clanMember.getObjectId() == apprentice)
					clanMember.sendPacket(new SystemMessage(SystemMessage.S1_YOUR_CLAN_ACADEMYS_SPONSOR_HAS_LOGGED_OUT).addString(_name));
			}
			member.setPlayerInstance(this, true);
		}

		FlagItemAttachment attachment = getActiveWeaponFlagAttachment();
		if(attachment != null)
			attachment.onLogout(this);

		if(CursedWeaponsManager.getInstance().getCursedWeapon(getCursedWeaponEquippedId()) != null)
			CursedWeaponsManager.getInstance().getCursedWeapon(getCursedWeaponEquippedId()).setPlayer(null);

		MatchingRoom room = getMatchingRoom();
		if(room != null)
		{
			if(room.getLeader() == this)
				room.disband();
			else
				room.removeMember(this, false);
		}
		setMatchingRoom(null);

		MatchingRoomManager.getInstance().removeFromWaitingList(this);

		destroyAllTraps();

		if(!_decoys.isEmpty())
		{
			for(DecoyInstance decoy : getDecoys())
			{
				decoy.unSummon();
				removeDecoy(decoy);
			}
		}

		stopPvPFlag();

		Reflection ref = getReflection();

		if(ref != ReflectionManager.DEFAULT)
		{
			if(ref.getReturnLoc() != null)
				_stablePoint = ref.getReturnLoc();

			ref.removeObject(this);
		}

		try
		{
			getInventory().store();
			getRefund().clear();
		}
		catch(Throwable t)
		{
			_log.error("", t);
		}

		try
		{
			store(false);
		}
		catch(Throwable t)
		{
			_log.error("", t);
		}
	}

	/**
	 * @return a table containing all L2RecipeList of the L2Player.<BR><BR>
	 */
	public Collection<RecipeTemplate> getDwarvenRecipeBook()
	{
		return _recipebook.values();
	}

	public Collection<RecipeTemplate> getCommonRecipeBook()
	{
		return _commonrecipebook.values();
	}

	public int recipesCount()
	{
		return _commonrecipebook.size() + _recipebook.size();
	}

	public boolean hasRecipe(final RecipeTemplate id)
	{
		return _recipebook.containsValue(id) || _commonrecipebook.containsValue(id);
	}

	public boolean findRecipe(final int id)
	{
		return _recipebook.containsKey(id) || _commonrecipebook.containsKey(id);
	}

	/**
	 * Add a new L2RecipList to the table _recipebook containing all L2RecipeList of the L2Player
	 */
	public void registerRecipe(final RecipeTemplate recipe, boolean saveDB)
	{
		if(recipe == null)
			return;

		if(recipe.isCommon())
			_commonrecipebook.put(recipe.getId(), recipe);
		else
			_recipebook.put(recipe.getId(), recipe);

		if(saveDB)
			mysql.set("REPLACE INTO character_recipebook (char_id, id) VALUES(?,?)", getObjectId(), recipe.getId());
	}

	/**
	 * Remove a L2RecipList from the table _recipebook containing all L2RecipeList of the L2Player
	 */
	public void unregisterRecipe(final int RecipeID)
	{
		if(_recipebook.containsKey(RecipeID))
		{
			mysql.set("DELETE FROM `character_recipebook` WHERE `char_id`=? AND `id`=? LIMIT 1", getObjectId(), RecipeID);
			_recipebook.remove(RecipeID);
		}
		else if(_commonrecipebook.containsKey(RecipeID))
		{
			mysql.set("DELETE FROM `character_recipebook` WHERE `char_id`=? AND `id`=? LIMIT 1", getObjectId(), RecipeID);
			_commonrecipebook.remove(RecipeID);
		}
		else
			_log.warn("Attempted to remove unknown RecipeList" + RecipeID);
	}

	public QuestState getQuestState(String quest)
	{
		questRead.lock();
		try
		{
			return _quests.get(quest);
		}
		finally
		{
			questRead.unlock();
		}
	}

	public QuestState getQuestState(Class<?> quest)
	{
		return getQuestState(quest.getSimpleName());
	}

	public boolean isQuestCompleted(String quest)
	{
		QuestState q = getQuestState(quest);
		return q != null && q.isCompleted();
	}

	public boolean isQuestCompleted(Class<?> quest)
	{
		QuestState q = getQuestState(quest);
		return q != null && q.isCompleted();
	}

	public void setQuestState(QuestState qs)
	{
		questWrite.lock();
		try
		{
			_quests.put(qs.getQuest().getName(), qs);
		}
		finally
		{
			questWrite.unlock();
		}
	}

	public void removeQuestState(String quest)
	{
		questWrite.lock();
		try
		{
			_quests.remove(quest);
		}
		finally
		{
			questWrite.unlock();
		}
	}

	public Quest[] getAllActiveQuests()
	{
		List<Quest> quests = new ArrayList<Quest>(_quests.size());
		questRead.lock();
		try
		{
			for(final QuestState qs : _quests.values())
				if(qs.isStarted())
					quests.add(qs.getQuest());
		}
		finally
		{
			questRead.unlock();
		}
		return quests.toArray(new Quest[quests.size()]);
	}

	public QuestState[] getAllQuestsStates()
	{
		questRead.lock();
		try
		{
			return _quests.values().toArray(new QuestState[_quests.size()]);
		}
		finally
		{
			questRead.unlock();
		}
	}

	public List<QuestState> getQuestsForEvent(NpcInstance npc, QuestEventType event)
	{
		List<QuestState> states = new ArrayList<QuestState>();
		Quest[] quests = npc.getTemplate().getEventQuests(event);
		QuestState qs;
		if(quests != null)
			for(Quest quest : quests)
			{
				qs = getQuestState(quest.getName());
				if(qs != null && !qs.isCompleted())
					states.add(getQuestState(quest.getName()));
			}
		return states;
	}

	public void processQuestEvent(String quest, String event, NpcInstance npc)
	{
		if(event == null)
			event = "";
		QuestState qs = getQuestState(quest);
		if(qs == null)
		{
			Quest q = QuestManager.getQuest(quest);
			if(q == null)
			{
				_log.warn("Quest " + quest + " not found!");
				return;
			}
			qs = q.newQuestState(this, Quest.CREATED);
		}
		if(qs == null || qs.isCompleted())
			return;
		qs.getQuest().notifyEvent(event, qs, npc);
		sendPacket(new QuestListPacket(this));
	}

	/**
	 * Проверка на переполнение инвентаря и перебор в весе для квестов и эвентов
	 *
	 * @return true если ве проверки прошли успешно
	 */
	public boolean isQuestContinuationPossible(boolean msg)
	{
		if(getWeightPenalty() >= 3 || getInventoryLimit() * 0.8 < getInventory().getSize() || Config.QUEST_INVENTORY_MAXIMUM * 0.8 < getInventory().getQuestSize())
		{
			if(msg)
				sendPacket(SystemMsg.PROGRESS_IN_A_QUEST_IS_POSSIBLE_ONLY_WHEN_YOUR_INVENTORYS_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
			return false;
		}
		return true;
	}

	/**
	 * Останавливаем и запоминаем все квестовые таймеры
	 */
	public void stopQuestTimers()
	{
		for(QuestState qs : getAllQuestsStates())
			if(qs.isStarted())
				qs.pauseQuestTimers();
			else
				qs.stopQuestTimers();
	}

	/**
	 * Восстанавливаем все квестовые таймеры
	 */
	public void resumeQuestTimers()
	{
		for(QuestState qs : getAllQuestsStates())
			qs.resumeQuestTimers();
	}

	// ----------------- End of Quest Engine -------------------

	public Collection<ShortCut> getAllShortCuts()
	{
		return _shortCuts.getAllShortCuts();
	}

	public ShortCut getShortCut(int slot, int page)
	{
		return _shortCuts.getShortCut(slot, page);
	}

	public void registerShortCut(ShortCut shortcut)
	{
		_shortCuts.registerShortCut(shortcut);
	}

	public void deleteShortCut(int slot, int page)
	{
		_shortCuts.deleteShortCut(slot, page);
	}

	public void registerMacro(Macro macro)
	{
		_macroses.registerMacro(macro);
	}

	public void deleteMacro(int id)
	{
		_macroses.deleteMacro(id);
	}

	public MacroList getMacroses()
	{
		return _macroses;
	}

	public boolean isCastleLord(int castleId)
	{
		return _clan != null && isClanLeader() && _clan.getCastle() == castleId;
	}

	/**
	 * Проверяет является ли этот персонаж владельцем крепости
	 *
	 * @param fortressId
	 * @return true если владелец
	 */
	public boolean isFortressLord(int fortressId)
	{
		return _clan != null && isClanLeader() && _clan.getHasFortress() == fortressId;
	}

	public int getPkKills()
	{
		return _pkKills;
	}

	public void setPkKills(final int pkKills)
	{
		_pkKills = pkKills;
	}

	public long getCreateTime()
	{
		return _createTime;
	}

	public void setCreateTime(final long createTime)
	{
		_createTime = createTime;
	}

	public int getDeleteTimer()
	{
		return _deleteTimer;
	}

	public void setDeleteTimer(final int deleteTimer)
	{
		_deleteTimer = deleteTimer;
	}

	@Override
	public int getCurrentLoad()
	{
		return getInventory().getTotalWeight();
	}

	public long getLastAccess()
	{
		return _lastAccess;
	}

	public void setLastAccess(long value)
	{
		_lastAccess = value;
	}

	public int getRecomHave()
	{
		return _recomHave;
	}

	public void setRecomHave(int value)
	{
		if(value > 255)
			_recomHave = 255;
		else if(value < 0)
			_recomHave = 0;
		else
			_recomHave = value;
	}

	public int getRecomBonusTime()
	{
		if(_recomBonusTask != null)
			return (int) Math.max(0, _recomBonusTask.getDelay(TimeUnit.SECONDS));
		return _recomBonusTime;
	}

	public void setRecomBonusTime(int val)
	{
		_recomBonusTime = val;
	}

	public int getRecomLeft()
	{
		return _recomLeft;
	}

	public void setRecomLeft(final int value)
	{
		_recomLeft = value;
	}

	public boolean isHourglassEffected()
	{
		return _isHourglassEffected;
	}

	public void setHourlassEffected(boolean val)
	{
		_isHourglassEffected = val;
	}

	public void startHourglassEffect()
	{
		setHourlassEffected(true);
		stopRecomBonusTask(true);
		sendVoteSystemInfo();
	}

	public void stopHourglassEffect()
	{
		setHourlassEffected(false);
		startRecomBonusTask();
		sendVoteSystemInfo();
	}

	public int addRecomLeft()
	{
		int recoms = 0;
		if(getRecomLeftToday() < 20)
			recoms = 10;
		else
			recoms = 1;
		setRecomLeft(getRecomLeft() + recoms);
		setRecomLeftToday(getRecomLeftToday() + recoms);
		sendUserInfo(true);
		return recoms;
	}

	public int getRecomLeftToday()
	{
		return _recomLeftToday;
	}

	public void setRecomLeftToday(final int value)
	{
		_recomLeftToday = value;
		setVar("recLeftToday", _recomLeftToday);
	}

	public void giveRecom(final Player target)
	{
		int targetRecom = target.getRecomHave();
		if(targetRecom < 255)
			target.addRecomHave(1);
		if(getRecomLeft() > 0)
			setRecomLeft(getRecomLeft() - 1);

		sendUserInfo(true);
	}

	public void addRecomHave(final int val)
	{
		setRecomHave(getRecomHave() + val);
		broadcastUserInfo(true);
		sendVoteSystemInfo();
	}

	public int getRecomBonus()
	{
		if(getRecomBonusTime() > 0 || isHourglassEffected())
			return RecomBonus.getRecoBonus(this);
		return 0;
	}

	public double getRecomBonusMul()
	{
		if(getRecomBonusTime() > 0 || isHourglassEffected())
			return RecomBonus.getRecoMultiplier(this);
		return 1;
	}

	public void sendVoteSystemInfo()
	{
		sendPacket(new ExVoteSystemInfoPacket(this));
	}

	public boolean isRecomTimerActive()
	{
		return _isRecomTimerActive;
	}

	public void setRecomTimerActive(boolean val)
	{
		if(_isRecomTimerActive == val)
			return;

		_isRecomTimerActive = val;

		if(val)
			startRecomBonusTask();
		else
			stopRecomBonusTask(true);

		sendVoteSystemInfo();
	}

	private ScheduledFuture<?> _recomBonusTask;

	public void startRecomBonusTask()
	{
		if(_recomBonusTask == null && getRecomBonusTime() > 0 && isRecomTimerActive() && !isHourglassEffected())
			_recomBonusTask = ThreadPoolManager.getInstance().schedule(new RecomBonusTask(this), getRecomBonusTime() * 1000);
	}

	public void stopRecomBonusTask(boolean saveTime)
	{
		if(_recomBonusTask != null)
		{
			if(saveTime)
				setRecomBonusTime((int) Math.max(0, _recomBonusTask.getDelay(TimeUnit.SECONDS)));
			_recomBonusTask.cancel(false);
			_recomBonusTask = null;
		}
	}

	@Override
	public int getKarma()
	{
		return _karma;
	}

	public void setKarma(int karma)
	{
		if(_karma == karma)
			return;

		_karma = karma;

		sendChanges();

		Servitor[] servitors = getServitors();
		if(servitors.length > 0)
		{
			for(Servitor servitor : servitors)
				servitor.broadcastCharInfo();
		}
	}

	@Override
	public int getMaxLoad()
	{
		return (int) calcStat(Stats.MAX_LOAD, 69000, this, null);
	}

	@Override
	public void updateEffectIcons()
	{
		if(entering || isLogoutStarted())
			return;

		super.updateEffectIcons();
	}

	@Override
	public void updateEffectIconsImpl()
	{
		Effect[] effects = getEffectList().getFirstEffects();
		Arrays.sort(effects, EffectsComparator.getInstance());

		PartySpelledPacket ps = new PartySpelledPacket(this, false);
		AbnormalStatusUpdatePacket abnormalStatus = new AbnormalStatusUpdatePacket();

		for(Effect effect : effects)
		{
			if(effect.checkAbnormalType(AbnormalType.hp_recover))
				sendPacket(new ShortBuffStatusUpdatePacket(effect));
			else
				effect.addIcon(abnormalStatus);
			if(_party != null)
				effect.addPartySpelledIcon(ps);
		}

		sendPacket(abnormalStatus);
		if(_party != null)
			_party.broadCast(ps);

		if(isInOlympiadMode() && isOlympiadCompStart())
		{
			OlympiadGame olymp_game = _olympiadGame;
			if(olymp_game != null)
			{
				ExOlympiadSpelledInfoPacket olympiadSpelledInfo = new ExOlympiadSpelledInfoPacket();

				for(Effect effect : effects)
					if(effect != null)
						effect.addOlympiadSpelledIcon(this, olympiadSpelledInfo);

				sendPacket(olympiadSpelledInfo);

				for(Player member : olymp_game.getSpectators())
					member.sendPacket(olympiadSpelledInfo);
			}
		}

		super.updateEffectIconsImpl();
	}

	@Override
	public int getWeightPenalty()
	{
		return getSkillLevel(4270, 0);
	}

	public void refreshOverloaded()
	{
		if(isLogoutStarted() || getMaxLoad() <= 0)
			return;

		setOverloaded(getCurrentLoad() > getMaxLoad());
		double weightproc = 100. * (getCurrentLoad() - calcStat(Stats.MAX_NO_PENALTY_LOAD, 0, this, null)) / getMaxLoad();
		int newWeightPenalty = 0;

		if(weightproc < 50)
			newWeightPenalty = 0;
		else if(weightproc < 66.6)
			newWeightPenalty = 1;
		else if(weightproc < 80)
			newWeightPenalty = 2;
		else if(weightproc < 100)
			newWeightPenalty = 3;
		else
			newWeightPenalty = 4;

		int current = getWeightPenalty();
		if(current == newWeightPenalty)
			return;

		if(newWeightPenalty > 0)
			addSkill(SkillTable.getInstance().getInfo(4270, newWeightPenalty));
		else
			super.removeSkill(getKnownSkill(4270));

		sendPacket(new SkillListPacket(this));
		sendPacket(new ExUserInfoInvenWeight(this));
		sendEtcStatusUpdate();
		updateStats();
	}

	public int getArmorsExpertisePenalty()
	{
		return getSkillLevel(6213, 0);
	}

	public int getWeaponsExpertisePenalty()
	{
		return getSkillLevel(6209, 0);
	}

	public int getExpertisePenalty(ItemInstance item)
	{
		if(item.getTemplate().getType2() == ItemTemplate.TYPE2_WEAPON)
			return getWeaponsExpertisePenalty();
		else if(item.getTemplate().getType2() == ItemTemplate.TYPE2_SHIELD_ARMOR || item.getTemplate().getType2() == ItemTemplate.TYPE2_ACCESSORY)
			return getArmorsExpertisePenalty();
		return 0;
	}

	public void refreshExpertisePenalty()
	{
		if(isLogoutStarted())
			return;

		// Calculate the current higher Expertise of the L2Player
		int level = (int) calcStat(Stats.GRADE_EXPERTISE_LEVEL, getLevel(), null, null);
		int i = 0;
		for(i = 0; i < EXPERTISE_LEVELS.length; i++)
			if(level < EXPERTISE_LEVELS[i + 1])
				break;

		boolean skillUpdate = false; // Для того, чтобы лишний раз не посылать пакеты
		// Add the Expertise skill corresponding to its Expertise level
		if(expertiseIndex != i)
		{
			expertiseIndex = i;
			if(expertiseIndex > 0)
			{
				int skillLvl = expertiseIndex;
				if(expertiseIndex == 7) //S84 пропускаем(проверить, используется ли на оффе)
					skillLvl--;
				addSkill(SkillTable.getInstance().getInfo(239, skillLvl), false);
				skillUpdate = true;
			}
		}

		int newWeaponPenalty = 0;
		int newArmorPenalty = 0;
		ItemInstance[] items = getInventory().getPaperdollItems();
		for(ItemInstance item : items)
			if(item != null)
			{
				int crystaltype = item.getTemplate().getGrade().ordinal();
				if(item.getTemplate().getType2() == ItemTemplate.TYPE2_WEAPON)
				{
					if(crystaltype > newWeaponPenalty)
						newWeaponPenalty = crystaltype;
				}
				else if(item.getTemplate().getType2() == ItemTemplate.TYPE2_SHIELD_ARMOR || item.getTemplate().getType2() == ItemTemplate.TYPE2_ACCESSORY)
				{
					if(crystaltype > expertiseIndex)
					{
						if(item.getBodyPart() == ItemTemplate.SLOT_FULL_ARMOR)
							newArmorPenalty++;
						newArmorPenalty++;
					}
				}
			}

		//Уровень штрафа оружия равен разнице между рангом оружия и допустим рангом для персонажа.
		newWeaponPenalty = newWeaponPenalty - expertiseIndex;
		newWeaponPenalty = Math.max(0, Math.min(4, newWeaponPenalty));

		//Уровень штрафа брони равен количеству одетой брони на персонажа не по рангу.
		newArmorPenalty = Math.max(0, Math.min(4, newArmorPenalty));

		int weaponExpertise = getWeaponsExpertisePenalty();
		int armorExpertise = getArmorsExpertisePenalty();

		if(weaponExpertise != newWeaponPenalty)
		{
			weaponExpertise = newWeaponPenalty;
			if(newWeaponPenalty > 0)
				addSkill(SkillTable.getInstance().getInfo(6209, weaponExpertise));
			else
				super.removeSkill(getKnownSkill(6209));
			skillUpdate = true;
		}
		if(armorExpertise != newArmorPenalty)
		{
			armorExpertise = newArmorPenalty;
			if(newArmorPenalty > 0)
				addSkill(SkillTable.getInstance().getInfo(6213, armorExpertise));
			else
				super.removeSkill(getKnownSkill(6213));
			skillUpdate = true;
		}

		if(skillUpdate)
		{
			getInventory().validateItemsSkills();

			sendPacket(new SkillListPacket(this));
			sendEtcStatusUpdate();
			updateStats();
		}
	}

	public int getPvpKills()
	{
		return _pvpKills;
	}

	public void setPvpKills(int pvpKills)
	{
		_pvpKills = pvpKills;
	}

	public int getClassLevel()
	{
		return getClassId().getClassLevel().ordinal();
	}

	public void addClanPointsOnProfession(final int id)
	{
		if(getLvlJoinedAcademy() != 0 && _clan != null && _clan.getLevel() >= 5 && ClassId.VALUES[id].isOfLevel(ClassLevel.FIRST))
			_clan.incReputation(100, true, "Academy"); //TODO: [Bonux] Сверить с GoD;
		else if(getLvlJoinedAcademy() != 0 && _clan != null && _clan.getLevel() >= 5 && ClassId.VALUES[id].isOfLevel(ClassLevel.SECOND))
			_clan.incReputation(200, true, "Academy"); //TODO: [Bonux] Сверить с GoD;
		else if(getLvlJoinedAcademy() != 0 && _clan != null && _clan.getLevel() >= 5 && ClassId.VALUES[id].isOfLevel(ClassLevel.THIRD))
		{
			int earnedPoints = Math.min((76 - getLvlJoinedAcademy()), 40) * 45 + 200;
			earnedPoints = Math.min(earnedPoints, 2000);
			earnedPoints = Math.max(earnedPoints, 290);
			if((_clan.getAcademyGraduatesCount() % 10) == 0)
				earnedPoints += 1000;

			_clan.removeClanMember(getObjectId());

			SystemMessage sm = new SystemMessage(SystemMessage.CLAN_ACADEMY_MEMBER_S1_HAS_SUCCESSFULLY_COMPLETED_THE_2ND_CLASS_TRANSFER_AND_OBTAINED_S2_CLAN_REPUTATION_POINTS);
			sm.addString(getName());
			sm.addNumber(_clan.incReputation(earnedPoints, true, "Academy"));
			_clan.broadcastToOnlineMembers(sm);
			_clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListDelete(getName()), this);

			setClan(null);
			setTitle("");
			sendPacket(SystemMsg.CONGRATULATIONS_YOU_WILL_NOW_GRADUATE_FROM_THE_CLAN_ACADEMY_AND_LEAVE_YOUR_CURRENT_CLAN);
			setLeaveClanTime(0);

			broadcastCharInfo();

			sendPacket(PledgeShowMemberListDeleteAllPacket.STATIC);
		}
	}

	/**
	 * Set the template of the L2Player.
	 *
	 * @param id The Identifier of the L2PlayerTemplate to set to the L2Player
	 */
	public synchronized void setClassId(final int id, boolean noban)
	{
		ClassId classId = ClassId.VALUES[id];
		if(!noban && !(classId.equalsOrChildOf(getClassId()) || getPlayerAccess().CanChangeClass || Config.EVERYBODY_HAS_ADMIN_RIGHTS))
		{
			Thread.dumpStack();
			return;
		}

		//Если новый ID не принадлежит имеющимся классам значит это новая профа
		if(!_subClassList.containsClassId(id))
		{
			final SubClass cclass = getActiveSubClass();
			final ClassId oldClass = ClassId.VALUES[cclass.getClassId()];

			_subClassList.changeSubClassId(oldClass.getId(), id);
			changeClassInDb(oldClass.getId(), id, cclass.getDefaultClassId());

			if(cclass.isBase())
			{
				addClanPointsOnProfession(id);

				if(isNoble())
				{
					StatsSet noble = Olympiad._nobles.get(getObjectId());
					if(noble != null)
					{
						noble.set(Olympiad.CLASS_ID, classId.getId());
						Olympiad._nobles.put(getObjectId(), noble);
					}
				}
			}

			// Выдача Holy Pomander
			switch(classId)
			{
				case CARDINAL:
					ItemFunctions.addItem(this, 15307, 7, true);
					break;
				case EVAS_SAINT:
					ItemFunctions.addItem(this, 15308, 7, true);
					break;
				case SHILLIEN_SAINT:
					ItemFunctions.addItem(this, 15309, 7, true);
					break;
			}

			//TODO: [Bonux] Пересмотреть.
			if(classId.isOfLevel(ClassLevel.AWAKED))
			{
				if(oldClass.isOfLevel(ClassLevel.AWAKED))
					getEffectList().stopAllEffects();

				removeAllSkills();

				restoreSkills();
				rewardSkills(false);
				checkSkills();

				refreshExpertisePenalty();

				getInventory().refreshEquip();
				getInventory().validateItems();

				checkActiveToggleEffects();

				sendUserInfo(true);
				sendPacket(new ExSubjobInfo(this, false));
				sendPacket(new SkillListPacket(this));
				sendPacket(new AcquireSkillListPacket(this));

				updateStats();
			}
			else
				rewardSkills(true);

			storeCharSubClasses();
		}

		PlayerTemplate template = PlayerTemplateHolder.getInstance().getPlayerTemplate(getRace(), classId, getSex());
		if(template == null)
		{
			_log.error("Missing template for classId: " + id);
			return;
		}
		setTemplate(template);

		broadcastCharInfo();

		// Update class icon in party and clan
		if(isInParty())
			getParty().broadCast(new PartySmallWindowUpdatePacket(this));
		if(getClan() != null)
			getClan().broadcastToOnlineMembers(new PledgeShowMemberListUpdatePacket(this));
		if(_matchingRoom != null)
			_matchingRoom.broadcastPlayerUpdate(this);

		sendPacket(new ExSubjobInfo(this, false));
	}

	public long getExp()
	{
		return getActiveSubClass() == null ? 0 : getActiveSubClass().getExp();
	}

	public long getMaxExp()
	{
		return getActiveSubClass() == null ? Experience.LEVEL[Experience.getMaxLevel() + 1] : getActiveSubClass().getMaxExp();
	}

	public void setEnchantScroll(final ItemInstance scroll)
	{
		_enchantScroll = scroll;
	}

	public ItemInstance getEnchantScroll()
	{
		return _enchantScroll;
	}

	public void setAppearanceStone(final ItemInstance stone)
	{
		_appearanceStone = stone;
	}

	public ItemInstance getAppearanceStone()
	{
		return _appearanceStone;
	}

	public void setAppearanceExtractItem(final ItemInstance item)
	{
		_appearanceExtractItem = item;
	}

	public ItemInstance getAppearanceExtractItem()
	{
		return _appearanceExtractItem;
	}

	public void addExpAndCheckBonus(MonsterInstance mob, final double noRateExp, double noRateSp, double partyVitalityMod)
	{
		if(getActiveSubClass() == null)
			return;

		// Начисление душ камаэлям
		double neededExp = calcStat(Stats.SOULS_CONSUME_EXP, 0, mob, null);
		if(neededExp > 0 && noRateExp > neededExp)
		{
			mob.broadcastPacket(new ExSpawnEmitterPacket(mob, this));
			ThreadPoolManager.getInstance().schedule(new GameObjectTasks.SoulConsumeTask(this), 1000);
		}

		if(getVitality() > 0 && noRateExp > 0)
		{
			if(!(getVarBoolean("NoExp") && getExp() == Experience.LEVEL[getLevel() + 1] - 1))
			{
				int points = (int) (noRateExp / 1000 * Config.ALT_VITALITY_CONSUME_RATE * partyVitalityMod); // TODO: [Bonux] Перепроверить расход на оффе.
				if(getEffectList().containsEffects(EffectType.Vitality))
					points = points / -4;

				setVitality(getVitality() - points);
			}
		}

		long normalExp = (long) (noRateExp * getRateExp());
		long normalSp = (long) (noRateSp * getRateSp());

		long expWithoutBonus = (long) (noRateExp * Config.RATE_XP_BY_LVL[getLevel()]);
		long spWithoutBonus = (long) (noRateSp * Config.RATE_SP_BY_LVL[getLevel()]);

		addExpAndSp(normalExp, normalSp, normalExp - expWithoutBonus, normalSp - spWithoutBonus, false, true, false);
	}

	@Override
	public void addExpAndSp(long exp, long sp)
	{
		addExpAndSp(exp, sp, 0, 0, false, false, false);
	}

	public void addExpAndSp(long exp, long sp, boolean delevel)
	{
		addExpAndSp(exp, sp, 0, 0, false, false, delevel);
	}

	public void addExpAndSp(long addToExp, long addToSp, long bonusAddExp, long bonusAddSp, boolean applyRate, boolean applyToPet, boolean delevel)
	{
		if(getActiveSubClass() == null)
			return;

		if(applyRate)
		{
			addToExp *= getRateExp();
			addToSp *= getRateSp();
		}

		PetInstance pet = getPet();
		if(addToExp > 0)
		{
			if(applyToPet)
			{
				if(pet != null && !pet.isDead() && !pet.getData().isOfType(PetType.SPECIAL))
				{
					// Sin Eater забирает всю экспу у персонажа
					if(pet.getData().isOfType(PetType.KARMA))
					{
						pet.addExpAndSp(addToExp, 0);
						addToExp = 0;
					}
					else if(pet.getExpPenalty() > 0f)
					{
						if(pet.getLevel() > getLevel() - 20 && pet.getLevel() < getLevel() + 5)
						{
							pet.addExpAndSp((long) (addToExp * pet.getExpPenalty()), 0);
							addToExp *= 1. - pet.getExpPenalty();
						}
						else
						{
							pet.addExpAndSp((long) (addToExp * pet.getExpPenalty() / 5.), 0);
							addToExp *= 1. - pet.getExpPenalty() / 5.;
						}
					}
					else if(pet.isSummon())
						addToExp *= 1. - pet.getExpPenalty();
				}
			}

			// Remove Karma when the player kills L2MonsterInstance
			//TODO [G1ta0] двинуть в метод начисления наград при убйистве моба
			if(isPK() && !isCursedWeaponEquipped() && !isInZoneBattle())
			{
				int karmaLost = Formulas.calculateKarmaLost(this, addToExp);
				if(karmaLost > 0)
				{
					_karma += karmaLost;
					if(_karma > 0)
						_karma = 0;

					sendPacket(new SystemMessagePacket(SystemMsg.YOUR_FAME_HAS_BEEN_CHANGED_TO_S1).addInteger(_karma));
				}
			}

			long max_xp = getVarBoolean("NoExp") ? Experience.LEVEL[getLevel() + 1] - 1 : getMaxExp();
			addToExp = Math.min(addToExp, max_xp - getExp());
		}

		int oldLvl = getActiveSubClass().getLevel();
		boolean oldIsAllowAbilities = isAllowAbilities();

		getActiveSubClass().addExp(addToExp, delevel);
		getActiveSubClass().addSp(addToSp);

		if(addToExp > 0 && addToSp > 0 && (bonusAddExp > 0 || bonusAddSp > 0))
			sendPacket(new SystemMessagePacket(SystemMsg.YOU_HAVE_ACQUIRED_S1_EXP_BONUS_S2_AND_S3_SP_BONUS_S4).addLong(addToExp).addLong(bonusAddExp).addInteger(addToSp).addInteger((int) bonusAddSp));
		else if(addToSp > 0 && addToExp == 0)
			sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_ACQUIRED_S1_SP).addNumber(addToSp));
		else if(addToSp > 0 && addToExp > 0)
			sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_EARNED_S1_EXPERIENCE_AND_S2_SP).addNumber(addToExp).addNumber(addToSp));
		else if(addToSp == 0 && addToExp > 0)
			sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_EARNED_S1_EXPERIENCE).addNumber(addToExp));

		int level = getActiveSubClass().getLevel();
		if(level != oldLvl)
		{
			levelSet(level - oldLvl);
			getListeners().onLevelChange(oldLvl, level);
		}

		if(oldIsAllowAbilities != isAllowAbilities())
			sendAbilitiesInfo();

		if(pet != null && pet.getData().isOfType(PetType.SPECIAL))
		{
			pet.setLevel(getLevel());
			pet.setExp(pet.getExpForNextLevel());
			pet.broadcastStatusUpdate();
		}

		updateStats();
	}

	private boolean _dontRewardSkills = false; // Глупя заглушка, но спасает.

	private void rewardSkills(boolean send)
	{
		if(getClassId().isOfLevel(ClassLevel.AWAKED))
			rewardSkills(send, true, Config.AUTO_LEARN_AWAKED_SKILLS, false);
		else
			rewardSkills(send, true, Config.AUTO_LEARN_SKILLS, false);
	}

	private void rewardSkills(boolean send, boolean send2)
	{
		if(getClassId().isOfLevel(ClassLevel.AWAKED))
			rewardSkills(send, true, Config.AUTO_LEARN_AWAKED_SKILLS, send2);
		else
			rewardSkills(send, true, Config.AUTO_LEARN_SKILLS, send2);
	}

	public int rewardSkills(boolean send, boolean checkShortCuts, boolean learnAllSkills, boolean send2)
	{
		if(_dontRewardSkills)
			return 0;

		boolean update = false;
		int addedSkillsCount = 0;
		for(SkillLearn sl : SkillAcquireHolder.getInstance().getAvailableMaxLvlSkills(this, AcquireType.NORMAL))
		{
//			if(sl.isAutoGet() && (learnAllSkills || sl.isFreeAutoGet()))
			if((sl.isAutoGet() && (learnAllSkills || sl.isFreeAutoGet())) || isFakePlayer())
			{
				Skill skill = SkillTable.getInstance().getInfo(sl.getId(), sl.getLevel());
				if(skill == null)
					continue;

				if(addSkill(skill, true) == null)
					addedSkillsCount++;

				if(checkShortCuts && getAllShortCuts().size() > 0 && skill.getLevel() > 1)
				{
					for(ShortCut sc : getAllShortCuts())
					{
						if(sc.getId() == skill.getId() && sc.getType() == ShortCut.TYPE_SKILL)
						{
							ShortCut newsc = new ShortCut(sc.getSlot(), sc.getPage(), sc.getType(), sc.getId(), skill.getLevel(), 1);
							sendPacket(new ShortCutRegisterPacket(this, newsc));
							registerShortCut(newsc);
						}
					}
				}
				update = true;
			}
		}

		if(isTransformed())
		{
			boolean added = false;
			// Добавляем скиллы трансформации зависящие от уровня персонажа
			for(SkillLearn sl : _transform.getAddtionalSkills())
			{
				if(sl.getMinLevel() > getLevel())
					continue;

				Skill skill = _transformSkills.get(sl.getId());
				if(skill != null && skill.getLevel() >= sl.getLevel())
					continue;

				skill = SkillTable.getInstance().getInfo(sl.getId(), sl.getLevel());
				if(skill == null)
					continue;

				_transformSkills.remove(skill.getId());
				_transformSkills.put(skill.getId(), skill);

				update = true;
				added = true;
			}

			if(added)
			{
				for(Skill skill : _transformSkills.valueCollection())
				{
					if(addSkill(skill, false) == null)
						addedSkillsCount++;
				}
			}
		}

		if(send && update)
			sendPacket(new SkillListPacket(this));

		updateStats();

		if(send && send2 || send && update)
		{
			sendUserInfo(true);
			sendPacket(new ExSubjobInfo(this, false));
			sendPacket(new AcquireSkillListPacket(this));
		}

		return addedSkillsCount;
	}

	public Race getRace()
	{
		return ClassId.VALUES[getBaseDefaultClassId()].getRace();
	}

	public ClassType getBaseClassType()
	{
		return ClassId.VALUES[getBaseDefaultClassId()].getType();
	}

	public int getIntSp()
	{
		return (int) getSp();
	}

	public long getSp()
	{
		return getActiveSubClass() == null ? 0 : getActiveSubClass().getSp();
	}

	public void setSp(long sp)
	{
		if(getActiveSubClass() != null)
			getActiveSubClass().setSp(sp);
	}

	public int getClanId()
	{
		return _clan == null ? 0 : _clan.getClanId();
	}

	public long getLeaveClanTime()
	{
		return _leaveClanTime;
	}

	public long getDeleteClanTime()
	{
		return _deleteClanTime;
	}

	public void setLeaveClanTime(final long time)
	{
		_leaveClanTime = time;
	}

	public void setDeleteClanTime(final long time)
	{
		_deleteClanTime = time;
	}

	public void setOnlineTime(final long time)
	{
		_onlineTime = time;
		_onlineBeginTime = System.currentTimeMillis();
	}

	public int getOnlineTime()
	{
		return (int) (_onlineBeginTime > 0 ? (_onlineTime + System.currentTimeMillis() - _onlineBeginTime) / 1000L : _onlineTime / 1000L);
	}

	public void setNoChannel(final long time)
	{
		_NoChannel = time;
		if(_NoChannel > 2145909600000L || _NoChannel < 0)
			_NoChannel = -1;

		if(_NoChannel > 0)
			_NoChannelBegin = System.currentTimeMillis();
		else
			_NoChannelBegin = 0;
	}

	public long getNoChannel()
	{
		return _NoChannel;
	}

	public long getNoChannelRemained()
	{
		if(_NoChannel == 0)
			return 0;
		else if(_NoChannel < 0)
			return -1;
		else
		{
			long remained = _NoChannel - System.currentTimeMillis() + _NoChannelBegin;
			if(remained < 0)
				return 0;

			return remained;
		}
	}

	public void setLeaveClanCurTime()
	{
		_leaveClanTime = System.currentTimeMillis();
	}

	public void setDeleteClanCurTime()
	{
		_deleteClanTime = System.currentTimeMillis();
	}

	public boolean canJoinClan()
	{
		if(_leaveClanTime == 0)
			return true;
		if(System.currentTimeMillis() - _leaveClanTime >= Config.ALT_CLAN_LEAVE_PENALTY_TIME * 60 * 60 * 1000L)
		{
			_leaveClanTime = 0;
			return true;
		}
		return false;
	}

	public boolean canCreateClan()
	{
		if(_deleteClanTime == 0)
			return true;
		if(System.currentTimeMillis() - _deleteClanTime >= Config.ALT_CLAN_CREATE_PENALTY_TIME * 60 * 60 * 1000L)
		{
			_deleteClanTime = 0;
			return true;
		}
		return false;
	}

	public IStaticPacket canJoinParty(Player inviter)
	{
		Request request = getRequest();
		if(request != null && request.isInProgress() && request.getOtherPlayer(this) != inviter)
			return SystemMsg.WAITING_FOR_ANOTHER_REPLY.packet(inviter); // занят
		if(isBlockAll() || getMessageRefusal()) // всех нафиг
			return SystemMsg.THAT_PERSON_IS_IN_MESSAGE_REFUSAL_MODE.packet(inviter);
		if(isInParty()) // уже
			return new SystemMessagePacket(SystemMsg.C1_IS_A_MEMBER_OF_ANOTHER_PARTY_AND_CANNOT_BE_INVITED).addName(this);
		if(inviter.getReflection() != getReflection()) // в разных инстантах
			if(inviter.getReflection() != ReflectionManager.DEFAULT && getReflection() != ReflectionManager.DEFAULT)
				return SystemMsg.INVALID_TARGET.packet(inviter);
		if(isCursedWeaponEquipped() || inviter.isCursedWeaponEquipped()) // зарич
			return SystemMsg.INVALID_TARGET.packet(inviter);
		if(inviter.isInOlympiadMode() || isInOlympiadMode() || inviter.getLfcGame() != null || getLfcGame() != null) // олимпиада
			return SystemMsg.A_USER_CURRENTLY_PARTICIPATING_IN_THE_OLYMPIAD_CANNOT_SEND_PARTY_AND_FRIEND_INVITATIONS.packet(inviter);
		if(!inviter.getPlayerAccess().CanJoinParty || !getPlayerAccess().CanJoinParty) // низя
			return SystemMsg.INVALID_TARGET.packet(inviter);
		if(getTeam() != TeamType.NONE && Config.DISABLE_PARTY_ON_EVENT && inviter.isInPvPEvent()) // участник пвп эвента или дуэли
			return SystemMsg.INVALID_TARGET.packet(inviter);
		return null;
	}

	@Override
	public PcInventory getInventory()
	{
		return _inventory;
	}

	@Override
	public long getWearedMask()
	{
		return _inventory.getWearedMask();
	}

	public PcFreight getFreight()
	{
		return _freight;
	}

	public void removeItemFromShortCut(final int objectId)
	{
		_shortCuts.deleteShortCutByObjectId(objectId);
	}

	public void removeSkillFromShortCut(final int skillId)
	{
		_shortCuts.deleteShortCutBySkillId(skillId);
	}

	public boolean isSitting()
	{
		return _isSitting;
	}

	public void setSitting(boolean val)
	{
		_isSitting = val;
	}

	public boolean getSittingTask()
	{
		return sittingTaskLaunched;
	}

	@Override
	public void sitDown(StaticObjectInstance throne)
	{
		if(isSitting() || sittingTaskLaunched || isAlikeDead())
			return;

		if(isStunned() || isSleeping() || isDecontrolled() || isAttackingNow() || isCastingNow() || isDualCastingNow() || isMoving)
		{
			getAI().setNextAction(AINextAction.REST, null, null, false, false);
			return;
		}

		resetWaitSitTime();
		getAI().setIntention(CtrlIntention.AI_INTENTION_REST, null, null);

		if(throne == null)
			broadcastPacket(new ChangeWaitTypePacket(this, ChangeWaitTypePacket.WT_SITTING));
		else
			broadcastPacket(new ChairSit(this, throne));

		_sittingObject = throne;
		setSitting(true);
		sittingTaskLaunched = true;
		ThreadPoolManager.getInstance().schedule(new EndSitDownTask(this), 2500);
	}

	@Override
	public void standUp()
	{
		if(!isSitting() || sittingTaskLaunched || isInStoreMode() || isAlikeDead())
			return;

		//FIXME [G1ta0] эффект сам отключается во время действия, если персонаж не сидит, возможно стоит убрать
		getEffectList().stopEffects(EffectType.Relax);

		getAI().clearNextAction();
		broadcastPacket(new ChangeWaitTypePacket(this, ChangeWaitTypePacket.WT_STANDING));

		_sittingObject = null;
		//setSitting(false);
		sittingTaskLaunched = true;
		ThreadPoolManager.getInstance().schedule(new EndStandUpTask(this), 2500);
	}

	public void updateWaitSitTime()
	{
		if(_waitTimeWhenSit < 200)
			_waitTimeWhenSit += 2;
	}

	public int getWaitSitTime()
	{
		return _waitTimeWhenSit;
	}

	public void resetWaitSitTime()
	{
		_waitTimeWhenSit = 0;
	}

	public Warehouse getWarehouse()
	{
		return _warehouse;
	}

	public ItemContainer getRefund()
	{
		return _refund;
	}

	public long getAdena()
	{
		return getInventory().getAdena();
	}

	public boolean reduceAdena(long adena)
	{
		return reduceAdena(adena, false);
	}

	/**
	 * Забирает адену у игрока.<BR><BR>
	 *
	 * @param adena  - сколько адены забрать
	 * @param notify - отображать системное сообщение
	 * @return true если сняли
	 */
	public boolean reduceAdena(long adena, boolean notify)
	{
		if(adena < 0)
			return false;
		if(adena == 0)
			return true;
		boolean result = getInventory().reduceAdena(adena);
		if(notify && result)
			sendPacket(SystemMessagePacket.removeItems(ItemTemplate.ITEM_ID_ADENA, adena));
		return result;
	}

	public ItemInstance addAdena(long adena)
	{
		return addAdena(adena, false);
	}

	/**
	 * Добавляет адену игроку.<BR><BR>
	 *
	 * @param adena  - сколько адены дать
	 * @param notify - отображать системное сообщение
	 * @return L2ItemInstance - новое количество адены
	 */
	public ItemInstance addAdena(long adena, boolean notify)
	{
		if(adena < 1)
			return null;
		ItemInstance item = getInventory().addAdena(adena);
		if(item != null && notify)
			sendPacket(SystemMessagePacket.obtainItems(ItemTemplate.ITEM_ID_ADENA, adena, 0));
		return item;
	}

	public GameClient getNetConnection()
	{
		return _connection;
	}

	public int getRevision()
	{
		return _connection == null ? 0 : _connection.getRevision();
	}

	public void setNetConnection(final GameClient connection)
	{
		_connection = connection;
	}

	public boolean isConnected()
	{
		return _connection != null && _connection.isConnected();
	}

	@Override
	public void onAction(final Player player, boolean shift)
	{
		if(!isTargetable(player))
		{
			player.sendActionFailed();
			return;
		}

		if(isFrozen())
		{
			player.sendPacket(ActionFailPacket.STATIC);
			return;
		}

		if(Events.onAction(player, this, shift))
		{
			player.sendPacket(ActionFailPacket.STATIC);
			return;
		}
		// Check if the other player already target this L2Player
		if(player.getTarget() != this)
		{
			player.setTarget(this);
			if(player.getTarget() != this)
				player.sendPacket(ActionFailPacket.STATIC);
		}
		else if(getPrivateStoreType() != Player.STORE_PRIVATE_NONE)
		{
			if(getDistance(player) > INTERACTION_DISTANCE && player.getAI().getIntention() != CtrlIntention.AI_INTENTION_INTERACT)
			{
				if(!shift)
					player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this, null);
				else
					player.sendPacket(ActionFailPacket.STATIC);
			}
			else
				player.doInteract(this);
		}
		else if(isAutoAttackable(player))
			player.getAI().Attack(this, false, shift);
		else if(player != this)
		{
			if(player.getAI().getIntention() != CtrlIntention.AI_INTENTION_FOLLOW)
			{
				if(!shift)
					player.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, this, Config.FOLLOW_RANGE);
				else
					player.sendPacket(ActionFailPacket.STATIC);
			}
			else
				player.sendPacket(ActionFailPacket.STATIC);
		}
		else
			player.sendPacket(ActionFailPacket.STATIC);
	}

	@Override
	public void broadcastStatusUpdate()
	{
		//if(!needStatusUpdate()) //По идее еше должно срезать траффик. Будут глюки с отображением - убрать это условие.
			//return;

		sendPacket(makeStatusUpdate(StatusUpdatePacket.MAX_HP, StatusUpdatePacket.MAX_MP, StatusUpdatePacket.MAX_CP, StatusUpdatePacket.CUR_HP, StatusUpdatePacket.CUR_MP, StatusUpdatePacket.CUR_CP, StatusUpdatePacket.DAMAGE));
		broadcastPacketToOthers(makeStatusUpdate(StatusUpdatePacket.MAX_HP, StatusUpdatePacket.MAX_MP, StatusUpdatePacket.MAX_CP, StatusUpdatePacket.CUR_HP, StatusUpdatePacket.CUR_MP, StatusUpdatePacket.CUR_CP, StatusUpdatePacket.DAMAGE));

		// Check if a party is in progress
		if(isInParty())
			// Send the Server->Client packet PartySmallWindowUpdatePacket with current HP, MP and Level to all other L2Player of the Party
			getParty().broadcastToPartyMembers(this, new PartySmallWindowUpdatePacket(this));

		if(isInOlympiadMode() && isOlympiadCompStart())
		{
			if(_olympiadGame != null)
				_olympiadGame.broadcastInfo(this, null, false);
		}

		for(GlobalEvent e : getEvents())
			e.broadcastStatusUpdate(this);
	}

	private ScheduledFuture<?> _broadcastCharInfoTask;

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
		broadcastUserInfo(false);
	}

	/**
	 * Отправляет UserInfo даному игроку и CIPacket всем окружающим.<BR><BR>
	 * <p/>
	 * <B><U> Концепт</U> :</B><BR><BR>
	 * Сервер шлет игроку UserInfo.
	 * Сервер вызывает метод {@link Creature#broadcastPacketToOthers(l2s.gameserver.network.l2.s2c.L2GameServerPacket...)} для рассылки CIPacket<BR><BR>
	 * <p/>
	 * <B><U> Действия</U> :</B><BR><BR>
	 * <li>Отсылка игроку UserInfo(личные и общие данные)</li>
	 * <li>Отсылка другим игрокам CIPacket(Public data only)</li><BR><BR>
	 * <p/>
	 * <FONT COLOR=#FF0000><B> <U>Внимание</U> : НЕ ПОСЫЛАЙТЕ UserInfo другим игрокам либо CIPacket даному игроку.<BR>
	 * НЕ ВЫЗЫВАЕЙТЕ ЭТОТ МЕТОД КРОМЕ ОСОБЫХ ОБСТОЯТЕЛЬСТВ(смена сабкласса к примеру)!!! Траффик дико кушается у игроков и начинаются лаги.<br>
	 * Используйте метод {@link Player#sendChanges()}</B></FONT><BR><BR>
	 */
	public void broadcastUserInfo(boolean force)
	{
		sendUserInfo(force);

		if(!isVisible() || isInvisible())
			return;

		if(Config.BROADCAST_CHAR_INFO_INTERVAL == 0)
			force = true;

		if(force)
		{
			if(_broadcastCharInfoTask != null)
			{
				_broadcastCharInfoTask.cancel(false);
				_broadcastCharInfoTask = null;
			}
			broadcastCharInfoImpl();
			return;
		}

		if(_broadcastCharInfoTask != null)
			return;

		_broadcastCharInfoTask = ThreadPoolManager.getInstance().schedule(new BroadcastCharInfoTask(), Config.BROADCAST_CHAR_INFO_INTERVAL);
	}

	private int _polyNpcId;

	public void setPolyId(int polyid)
	{
		_polyNpcId = polyid;

		teleToLocation(getLoc());
		broadcastUserInfo(true);
	}

	public boolean isPolymorphed()
	{
		return _polyNpcId != 0;
	}

	public int getPolyId()
	{
		return _polyNpcId;
	}

	@Override
	public void broadcastCharInfoImpl()
	{
		if(!isVisible() || isInvisible())
			return;

		for(Player player : World.getAroundPlayers(this))
		{
			if(player == this)
				continue;
	
			player.sendPacket(isPolymorphed() ? new NpcInfoPoly(this) : new CIPacket(this, player));
			player.sendPacket(RelationChangedPacket.update(player, this, player));
		}
	}

	public void broadcastRelationChanged()
	{
		if(!isVisible() || isInvisible())
			return;

		for(Player player : World.getAroundPlayers(this))
			player.sendPacket(RelationChangedPacket.update(player, this, player));
	}

	public void sendEtcStatusUpdate()
	{
		if(!isVisible())
			return;

		sendPacket(new EtcStatusUpdatePacket(this));
	}

	private Future<?> _userInfoTask;

	private class UserInfoTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			sendUserInfoImpl();
			_userInfoTask = null;
		}
	}

	private void sendUserInfoImpl()
	{
		sendPacket(new UIPacket(this));
	}

	public void sendUserInfo()
	{
		sendUserInfo(false);
	}

	public void sendUserInfo(boolean force)
	{
		if(!isVisible() || entering || isLogoutStarted())
			return;

		if(Config.USER_INFO_INTERVAL == 0 || force)
		{
			if(_userInfoTask != null)
			{
				_userInfoTask.cancel(false);
				_userInfoTask = null;
			}
			sendUserInfoImpl();
			return;
		}

		if(_userInfoTask != null)
			return;

		_userInfoTask = ThreadPoolManager.getInstance().schedule(new UserInfoTask(), Config.USER_INFO_INTERVAL);
	}

	@Override
	public StatusUpdatePacket makeStatusUpdate(int... fields)
	{
		StatusUpdatePacket su = new StatusUpdatePacket(getObjectId(), getObjectId());
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
				case StatusUpdatePacket.CUR_LOAD:
					su.addAttribute(field, getCurrentLoad());
					break;
				case StatusUpdatePacket.MAX_LOAD:
					su.addAttribute(field, getMaxLoad());
					break;
				case StatusUpdatePacket.PVP_FLAG:
					su.addAttribute(field, _pvpFlag);
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
			}
		return su;
	}

	public void sendStatusUpdate(boolean broadCast, boolean withPet, int... fields)
	{
		if(fields.length == 0 || entering && !broadCast)
			return;

		StatusUpdatePacket su = makeStatusUpdate(fields);
		if(!su.hasAttributes())
			return;

		List<L2GameServerPacket> packets = new ArrayList<L2GameServerPacket>(withPet ? 2 : 1);
		if(withPet)
		{
			Servitor[] servitors = getServitors();
			if(servitors.length > 0)
			{
				for(Servitor servitor : servitors)
					packets.add(servitor.makeStatusUpdate(fields));
			}
		}

		packets.add(su);

		if(!broadCast)
			sendPacket(packets);
		else if(entering)
			broadcastPacketToOthers(packets);
		else
			broadcastPacket(packets);
	}

	/**
	 * @return the Alliance Identifier of the L2Player.<BR><BR>
	 */
	public int getAllyId()
	{
		return _clan == null ? 0 : _clan.getAllyId();
	}

	@Override
	public void sendPacket(IStaticPacket p)
	{
		if(!isConnected())
			return;

		if(isPacketIgnored(p.packet(this)))
			return;

		_connection.sendPacket(p.packet(this));
	}

	@Override
	public void sendPacket(IStaticPacket... packets)
	{
		if(!isConnected())
			return;

		for(IStaticPacket p : packets)
		{
			if(isPacketIgnored(p))
				continue;

			_connection.sendPacket(p.packet(this));
		}
	}

	private boolean isPacketIgnored(IStaticPacket p)
	{
		if(p == null)
			return true;
		if(_notShowBuffAnim && (p.getClass() == MagicSkillUse.class || p.getClass() == MagicSkillLaunchedPacket.class))
			return true;

		//if(_notShowTraders && (p.getClass() == PrivateStoreMsgBuy.class || p.getClass() == PrivateStoreMsg.class || p.getClass() == RecipeShopMsg.class))
		//		return true;

		return false;
	}

	@Override
	public void sendPacket(List<? extends IStaticPacket> packets)
	{
		if(!isConnected())
			return;

		for(IStaticPacket p : packets)
			_connection.sendPacket(p.packet(this));
	}

	public void doInteract(GameObject target)
	{
		if(target == null || isActionsDisabled())
		{
			sendActionFailed();
			return;
		}
		if(target.isPlayer())
		{
			if(target.getDistance(this) <= INTERACTION_DISTANCE)
			{
				Player temp = (Player) target;

				if(temp.getPrivateStoreType() == STORE_PRIVATE_SELL || temp.getPrivateStoreType() == STORE_PRIVATE_SELL_PACKAGE)
				{
					sendPacket(new PrivateStoreList(this, temp));
					sendActionFailed();
				}
				else if(temp.getPrivateStoreType() == STORE_PRIVATE_BUY)
				{
					sendPacket(new PrivateStoreListBuy(this, temp));
					sendActionFailed();
				}
				else if(temp.getPrivateStoreType() == STORE_PRIVATE_MANUFACTURE)
				{
					sendPacket(new RecipeShopSellList(this, temp));
					sendActionFailed();
				}
				sendActionFailed();
			}
			else if(getAI().getIntention() != CtrlIntention.AI_INTENTION_INTERACT)
				getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this, null);
		}
		else
			target.onAction(this, false);
	}

	public void doAutoLootOrDrop(ItemInstance item, NpcInstance fromNpc)
	{
		boolean forceAutoloot = fromNpc.isFlying() || getReflection().isAutolootForced();

		if((fromNpc.isRaid() || fromNpc instanceof ReflectionBossInstance) && !Config.AUTO_LOOT_FROM_RAIDS && !item.isHerb() && !forceAutoloot)
		{
			item.dropToTheGround(this, fromNpc);
			return;
		}

		// Herbs
		if(item.isHerb())
		{
			if(!AutoLootHerbs && !forceAutoloot)
			{
				item.dropToTheGround(this, fromNpc);
				return;
			}
			Skill[] skills = item.getTemplate().getAttachedSkills();
			if(skills.length > 0)
				for(Skill skill : skills)
				{
					altUseSkill(skill, this);
					Servitor[] servitors = getServitors();
					if(servitors.length > 0)
					{
						for(Servitor servitor : servitors)
						{
							if(servitor.isSummon() && !servitor.isDead())
								servitor.altUseSkill(skill, servitor);
						}
					}
				}
			item.deleteMe();
			return;
		}

		if(!forceAutoloot && !(_autoLoot && (Config.AUTO_LOOT_ITEM_ID_LIST.isEmpty() || Config.AUTO_LOOT_ITEM_ID_LIST.contains(item.getItemId()))) && !(_autoLootOnlyAdena && item.getTemplate().isAdena()))
		{
			item.dropToTheGround(this, fromNpc);
			return;
		}

		// Check if the L2Player is in a Party
		if(!isInParty())
		{
			if(!pickupItem(item, Log.Pickup))
			{
				item.dropToTheGround(this, fromNpc);
				return;
			}
		}
		else
			getParty().distributeItem(this, item, fromNpc);

		broadcastPickUpMsg(item);
	}

	@Override
	public void doPickupItem(final GameObject object)
	{
		// Check if the L2Object to pick up is a L2ItemInstance
		if(!object.isItem())
		{
			_log.warn("trying to pickup wrong target." + getTarget());
			return;
		}

		sendActionFailed();
		stopMove();

		ItemInstance item = (ItemInstance) object;

		synchronized (item)
		{
			if(!item.isVisible())
				return;

			// Check if me not owner of item and, if in party, not in owner party and nonowner pickup delay still active
			if(!ItemFunctions.checkIfCanPickup(this, item))
			{
				SystemMessage sm;
				if(item.getItemId() == 57)
				{
					sm = new SystemMessage(SystemMessage.YOU_HAVE_FAILED_TO_PICK_UP_S1_ADENA);
					sm.addNumber(item.getCount());
				}
				else
				{
					sm = new SystemMessage(SystemMessage.YOU_HAVE_FAILED_TO_PICK_UP_S1);
					sm.addItemName(item.getItemId());
				}
				sendPacket(sm);
				return;
			}

			// Herbs
			if(item.isHerb())
			{
				Skill[] skills = item.getTemplate().getAttachedSkills();
				if(skills.length > 0)
				{
					for(Skill skill : skills)
						altUseSkill(skill, this);
				}

				broadcastPacket(new GetItemPacket(item, getObjectId()));
				item.deleteMe();
				return;
			}

			FlagItemAttachment attachment = item.getAttachment() instanceof FlagItemAttachment ? (FlagItemAttachment) item.getAttachment() : null;

			if(!isInParty() || attachment != null)
			{
				if(pickupItem(item, Log.Pickup))
				{
					broadcastPacket(new GetItemPacket(item, getObjectId()));
					broadcastPickUpMsg(item);
					item.pickupMe();
				}
			}
			else
				getParty().distributeItem(this, item, null);
		}
	}

	public boolean pickupItem(ItemInstance item, String log)
	{
		PickableAttachment attachment = item.getAttachment() instanceof PickableAttachment ? (PickableAttachment) item.getAttachment() : null;

		if(!ItemFunctions.canAddItem(this, item))
			return false;

		if(item.getItemId() == ItemTemplate.ITEM_ID_ADENA || item.getItemId() == 6353)//FIXME [G1ta0] хардкод
		{
			Quest q = QuestManager.getQuest(255);
			if(q != null)
				processQuestEvent(q.getName(), "CE" + item.getItemId(), null);
		}

		Log.LogItem(this, log, item);
		sendPacket(SystemMessagePacket.obtainItems(item));
		getInventory().addItem(item);

		if(attachment != null)
			attachment.pickUp(this);

		sendChanges();
		return true;
	}

	public void setNpcTarget(GameObject target)
	{
		setTarget(target);
		if(target == null)
			return;

		if(target == getTarget())
		{
			if(target.isNpc())
			{
				NpcInstance npc = (NpcInstance) target;
				sendPacket(npc.makeStatusUpdate(StatusUpdatePacket.CUR_HP, StatusUpdatePacket.MAX_HP));
				sendPacket(new ValidateLocationPacket(npc), ActionFailPacket.STATIC);
			}
		}
	}

	@Override
	public void setTarget(GameObject newTarget)
	{
		// Check if the new target is visible
		if(newTarget != null && !newTarget.isVisible())
			newTarget = null;

		Party party = getParty();

		// Can't target and attack rift invaders if not in the same room
		if(party != null && party.isInDimensionalRift())
		{
			int riftType = party.getDimensionalRift().getType();
			int riftRoom = party.getDimensionalRift().getCurrentRoom();
			if(newTarget != null && !DimensionalRiftManager.getInstance().getRoom(riftType, riftRoom).checkIfInZone(newTarget.getX(), newTarget.getY(), newTarget.getZ()))
				newTarget = null;
		}

		GameObject oldTarget = getTarget();

		if(oldTarget != null)
		{
			if(oldTarget.equals(newTarget))
				return;

			broadcastPacket(new TargetUnselectedPacket(this));
		}

		if(newTarget != null)
		{
			broadcastTargetSelected(newTarget);

			if(newTarget.isCreature())
				sendPacket(((Creature) newTarget).getAbnormalStatusUpdate());
		}

		if(newTarget != null && newTarget != this && getDecoys() != null && !getDecoys().isEmpty() && newTarget.isCreature())
		{
			for(DecoyInstance dec : getDecoys())
			{
				if(dec == null)
					continue;
				if(dec.getAI() == null)
				{
					_log.info("This decoy has NULL AI");
					continue;
				}
				if(newTarget.isCreature())
				{
					Creature _nt = (Creature) newTarget;
					if(_nt.isInZonePeace()) //won't attack in peace zone anyone.
						continue;
				}
				dec.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, newTarget, 1000);
				//dec.getAI().checkAggression(((Creature)newTarget));
				//dec.getAI().Attack(newTarget, true, false);
			}
		}

		super.setTarget(newTarget);
	}

	public void broadcastTargetSelected(GameObject newTarget)
	{
		sendPacket(new MyTargetSelectedPacket(this, newTarget));
		broadcastPacket(new TargetSelectedPacket(getObjectId(), newTarget.getObjectId(), getLoc()));
	}

	/**
	 * @return the active weapon instance (always equipped in the right hand).<BR><BR>
	 */
	@Override
	public ItemInstance getActiveWeaponInstance()
	{
		return getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
	}

	/**
	 * @return the active weapon item (always equipped in the right hand).<BR><BR>
	 */
	@Override
	public WeaponTemplate getActiveWeaponTemplate()
	{
		final ItemInstance weapon = getActiveWeaponInstance();

		if(weapon == null)
			return null;

		return (WeaponTemplate) weapon.getTemplate();
	}

	/**
	 * @return the secondary weapon instance (always equipped in the left hand).<BR><BR>
	 */
	@Override
	public ItemInstance getSecondaryWeaponInstance()
	{
		return getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
	}

	/**
	 * @return the secondary weapon item (always equipped in the left hand) or the fists weapon.<BR><BR>
	 */
	@Override
	public WeaponTemplate getSecondaryWeaponTemplate()
	{
		final ItemInstance weapon = getSecondaryWeaponInstance();

		if(weapon == null)
			return null;

		final ItemTemplate item = weapon.getTemplate();

		if(item instanceof WeaponTemplate)
			return (WeaponTemplate) item;

		return null;
	}

	public ArmorType getWearingArmorType()
	{
		final ItemInstance chest = getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
		if(chest == null)
			return ArmorType.NONE;

		final ItemType chestItemType = chest.getItemType();
		if(!(chestItemType instanceof ArmorType))
			return ArmorType.NONE;

		final ArmorType chestArmorType = (ArmorType) chestItemType;
		if(chest.getBodyPart() == ItemTemplate.SLOT_FULL_ARMOR)
			return chestArmorType;

		final ItemInstance legs = getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS);
		if(legs == null)
			return ArmorType.NONE;

		if(legs.getItemType() != chestArmorType)
			return ArmorType.NONE;

		return chestArmorType;
	}

	@Override
	public void reduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflectAndAbsorb, boolean transferDamage, boolean isDot, boolean sendReceiveMessage)
	{
		if(attacker == null || isDead() || (attacker.isDead() && !isDot))
			return;

		// 5182 = Blessing of protection, работает если разница уровней больше 10 и не в зоне осады
		if(attacker.isPlayer() && Math.abs(attacker.getLevel() - getLevel()) > 10)
		{
			// ПК не может нанести урон чару с блессингом
			if(attacker.isPK() && getEffectList().containsEffects(5182) && !isInZone(ZoneType.SIEGE))
				return;
			// чар с блессингом не может нанести урон ПК
			if(isPK() && attacker.getEffectList().containsEffects(5182) && !attacker.isInZone(ZoneType.SIEGE))
				return;
		}

		// Reduce the current HP of the L2Player
		super.reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflectAndAbsorb, transferDamage, isDot, sendReceiveMessage);
	}

	@Override
	protected void onReduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp)
	{
		if(standUp)
		{
			standUp();
			if(isFakeDeath())
				breakFakeDeath();
		}

		final double originDamage = damage;

		if(attacker.isPlayable())
		{
			if(!directHp && getCurrentCp() > 0)
			{
				double cp = getCurrentCp();
				if(cp >= damage)
				{
					cp -= damage;
					damage = 0;
				}
				else
				{
					damage -= cp;
					cp = 0;
				}

				setCurrentCp(cp);
			}
		}

		double hp = getCurrentHp();

		DuelEvent duelEvent = getEvent(DuelEvent.class);
		if(duelEvent != null)
		{
			if(hp - damage <= 1) // если хп <= 1 - убит
			{
				setCurrentHp(1, false);
				duelEvent.onDie(this);
				return;
			}
		}

		if(getLfcGame() != null && !getPendingLfcStart())
		{
			if(hp <= damage) // если хп <= 1 - убит
			{
				((Player) attacker).setPendingLfcEnd(true);
				setCurrentHp(1, true);
				attacker.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
				attacker.sendActionFailed();

				if(this != null)	
				{
					if(isDead())
						broadcastPacket(new RevivePacket(this));

					setPendingLfcEnd(true);
					getLfcGame().endMatch(this);
					setLfcGame(null);
					return;
				}		
			}		
		}

		if(isInOlympiadMode())
		{
			OlympiadGame game = _olympiadGame;
			if(this != attacker && (skill == null || skill.isOffensive())) // считаем дамаг от простых ударов и атакующих скиллов
				game.addDamage(this, Math.min(hp, originDamage));

			if(hp - damage <= 1) // если хп <= 1 - убит
			{
				game.setWinner(getOlympiadSide() == 1 ? 2 : 1);
				game.endGame(20000, false);
				setCurrentHp(1, false);
				attacker.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
				attacker.sendActionFailed();
				return;
			}
		}

		super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp);
	}

	private void altDeathPenalty(final Creature killer)
	{
		// Reduce the Experience of the L2Player in function of the calculated Death Penalty
		if(!Config.ALT_GAME_DELEVEL)
			return;
		if(isInZoneBattle())
			return;
		deathPenalty(killer);
	}

	public final boolean atWarWith(final Player player)
	{
		return _clan != null && player.getClan() != null && getPledgeType() != -1 && player.getPledgeType() != -1 && _clan.isAtWarWith(player.getClan().getClanId());
	}

	public boolean atMutualWarWith(Player player)
	{
		return _clan != null && player.getClan() != null && getPledgeType() != -1 && player.getPledgeType() != -1 && _clan.isAtWarWith(player.getClan().getClanId()) && player.getClan().isAtWarWith(_clan.getClanId());
	}

	public final void doPurePk(final Player killer)
	{
		// Check if the attacker has a PK counter greater than 0
		final int pkCountMulti = (int) Math.max(killer.getPkKills() * Config.KARMA_PENALTY_DURATION_INCREASE, 1);

		// Calculate the level difference Multiplier between attacker and killed L2Player
		//final int lvlDiffMulti = Math.max(killer.getLevel() / _level, 1);

		// Calculate the new Karma of the attacker : newKarma = baseKarma*pkCountMulti*lvlDiffMulti
		// Add karma to attacker and increase its PK counter
		killer.decreaseKarma(Config.KARMA_MIN_KARMA * pkCountMulti); // * lvlDiffMulti);
		killer.setPkKills(killer.getPkKills() + 1);
	}

	public final void doKillInPeace(final Player killer) // Check if the L2Player killed haven't Karma
	{
		if(!isPK())
			doPurePk(killer);
		else
		{
			String var = PK_KILL_VAR + "_" + getObjectId();
			if(!killer.getVarBoolean(var))
			{
				killer.increaseKarma(360); //TODO: [Bonux] Понадобности вынести в конфиг.
				// В течении 30 минут не выдаем карму за убийство данного ПК. (TODO: [Bonux] Проверить время на оффе.)
				long expirationTime = System.currentTimeMillis() + (30 * 60 * 1000);
				killer.setVar(var, true, expirationTime);
			}
		}
	}

	public void checkAddItemToDrop(List<ItemInstance> array, List<ItemInstance> items, int maxCount)
	{
		for(int i = 0; i < maxCount && !items.isEmpty(); i++)
			array.add(items.remove(Rnd.get(items.size())));
	}

	public FlagItemAttachment getActiveWeaponFlagAttachment()
	{
		ItemInstance item = getActiveWeaponInstance();
		if(item == null || !(item.getAttachment() instanceof FlagItemAttachment))
			return null;
		return (FlagItemAttachment) item.getAttachment();
	}

	protected void doPKPVPManage(Creature killer)
	{
		FlagItemAttachment attachment = getActiveWeaponFlagAttachment();
		if(attachment != null)
			attachment.onDeath(this, killer);

		if(killer == null || isMyServitor(killer.getObjectId()) || killer == this)
			return;

		if(isInZoneBattle() || killer.isInZoneBattle())
			return;

		if(killer.isServitor() && (killer = killer.getPlayer()) == null)
			return;

		// Processing Karma/PKCount/PvPCount for killer
		if(killer.isPlayer() || killer instanceof FakePlayer) //addon if killer is clone instance should do also this method.
		{
			final Player pk = killer.getPlayer();
			boolean war = atMutualWarWith(pk);

			//TODO [VISTALL] fix it
			if(war /*|| _clan.getSiege() != null && _clan.getSiege() == pk.getClan().getSiege() && (_clan.isDefender() && pk.getClan().isAttacker() || _clan.isAttacker() && pk.getClan().isDefender())*/)
				if(pk.getClan().getReputationScore() > 0 && _clan.getLevel() >= 5 && _clan.getReputationScore() > 0 && pk.getClan().getLevel() >= 5)
				{
					_clan.incReputation(-1, true, "ClanWar");
					_clan.broadcastToOtherOnlineMembers(new SystemMessagePacket(SystemMsg.BECAUSE_C1_WAS_KILLED_BY_A_CLAN_MEMBER_OF_S2_CLAN_FAME_POINTS_DECREASED_BY_1).addName(this).addString(pk.getClan().getName()), this);

					pk.getClan().incReputation(1, true, "ClanWar");
					pk.getClan().broadcastToOtherOnlineMembers(new SystemMessagePacket(SystemMsg.BECAUSE_A_CLAN_MEMBER_OF_S1_WAS_KILLED_BY_C2_CLAN_FAME_POINTS_INCREASED_BY_1).addString(_clan.getName()).addName(pk), pk);
				}

			if(isOnSiegeField())
				return;

			Castle castle = getCastle();
			if(_pvpFlag > 0 || war || castle != null && castle.getResidenceSide() == ResidenceSide.DARK)
			{
				pk.setPvpKills(pk.getPvpKills() + 1);
				PvPRewardManager.tryGiveReward(this, pk);
			}	
			else
			{
				doKillInPeace(pk);
			}
			pk.sendChanges();
		}

		if(isPK())
		{
			increaseKarma(Config.KARMA_LOST_BASE);
			if(_karma > 0)
				_karma = 0;
		}

		// в нормальных условиях вещи теряются только при смерти от гварда или игрока
		// кроме того, альт на потерю вещей при сметри позволяет терять вещи при смтери от монстра
		boolean isPvP = killer.isPlayable() || killer instanceof GuardInstance;

		if(killer.isMonster() && !Config.DROP_ITEMS_ON_DIE // если убил монстр и альт выключен
				|| isPvP // если убил игрок или гвард и
				&& (_pkKills < Config.MIN_PK_TO_ITEMS_DROP // количество пк слишком мало
				|| !isPK() && Config.KARMA_NEEDED_TO_DROP) // кармы нет
				|| !killer.isMonster() && !isPvP) // в прочих случаях тоже
			return;

		// No drop from GM's
		if(!Config.KARMA_DROP_GM && isGM())
			return;

		final int max_drop_count = isPvP ? Config.KARMA_DROP_ITEM_LIMIT : 1;

		double dropRate; // базовый шанс в процентах
		if(isPvP)
			dropRate = _pkKills * Config.KARMA_DROPCHANCE_MOD + Config.KARMA_DROPCHANCE_BASE;
		else
			dropRate = Config.NORMAL_DROPCHANCE_BASE;

		int dropEquipCount = 0, dropWeaponCount = 0, dropItemCount = 0;

		for(int i = 0; i < Math.ceil(dropRate / 100) && i < max_drop_count; i++)
			if(Rnd.chance(dropRate))
			{
				int rand = Rnd.get(Config.DROPCHANCE_EQUIPPED_WEAPON + Config.DROPCHANCE_EQUIPMENT + Config.DROPCHANCE_ITEM) + 1;
				if(rand > Config.DROPCHANCE_EQUIPPED_WEAPON + Config.DROPCHANCE_EQUIPMENT)
					dropItemCount++;
				else if(rand > Config.DROPCHANCE_EQUIPPED_WEAPON)
					dropEquipCount++;
				else
					dropWeaponCount++;
			}

		List<ItemInstance> drop = new LazyArrayList<ItemInstance>(), // общий массив с результатами выбора
		dropItem = new LazyArrayList<ItemInstance>(), dropEquip = new LazyArrayList<ItemInstance>(), dropWeapon = new LazyArrayList<ItemInstance>(); // временные

		getInventory().writeLock();
		try
		{
			for(ItemInstance item : getInventory().getItems())
			{
				if(!item.canBeDropped(this, true) || Config.KARMA_LIST_NONDROPPABLE_ITEMS.contains(item.getItemId()))
					continue;

				if(item.getTemplate().getType2() == ItemTemplate.TYPE2_WEAPON)
					dropWeapon.add(item);
				else if(item.getTemplate().getType2() == ItemTemplate.TYPE2_SHIELD_ARMOR || item.getTemplate().getType2() == ItemTemplate.TYPE2_ACCESSORY)
					dropEquip.add(item);
				else if(item.getTemplate().getType2() == ItemTemplate.TYPE2_OTHER)
					dropItem.add(item);
			}

			checkAddItemToDrop(drop, dropWeapon, dropWeaponCount);
			checkAddItemToDrop(drop, dropEquip, dropEquipCount);
			checkAddItemToDrop(drop, dropItem, dropItemCount);

			// Dropping items, if present
			if(drop.isEmpty())
				return;

			for(ItemInstance item : drop)
			{
				if(item.isAugmented() && !Config.ALT_ALLOW_DROP_AUGMENTED)
				{
					item.setVariationStoneId(0);
					item.setVariation1Id(0);
					item.setVariation2Id(0);
				}

				item = getInventory().removeItem(item);
				Log.LogItem(this, Log.PvPDrop, item);

				if(item.getEnchantLevel() > 0)
					sendPacket(new SystemMessage(SystemMessage.DROPPED__S1_S2).addNumber(item.getEnchantLevel()).addItemName(item.getItemId()));
				else
					sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_DROPPED_S1).addItemName(item.getItemId()));

				if(killer.isPlayable() && ((Config.AUTO_LOOT && Config.AUTO_LOOT_PK) || isInFlyingTransform()))
				{
					killer.getPlayer().getInventory().addItem(item);
					Log.LogItem(this, Log.Pickup, item);

					killer.getPlayer().sendPacket(SystemMessagePacket.obtainItems(item));
				}
				else
					item.dropToTheGround(this, Location.findAroundPosition(this, Config.KARMA_RANDOM_DROP_LOCATION_LIMIT));
			}
		}
		finally
		{
			getInventory().writeUnlock();
		}
	}

	@Override
	protected void onDeath(Creature killer)
	{
		//Check for active charm of luck for death penalty
		getDeathPenalty().checkCharmOfLuck();


		if(isInStoreMode())
			setPrivateStoreType(Player.STORE_PRIVATE_NONE);
		if(isProcessingRequest())
		{
			Request request = getRequest();
			if(isInTrade())
			{
				Player parthner = request.getOtherPlayer(this);
				sendPacket(TradeDonePacket.FAIL);
				parthner.sendPacket(TradeDonePacket.FAIL);
			}
			request.cancel();
		}

		setAgathion(0);

		boolean checkPvp = true;
		if(Config.ALLOW_CURSED_WEAPONS)
		{
			if(isCursedWeaponEquipped())
			{
				CursedWeaponsManager.getInstance().dropPlayer(this);
				checkPvp = false;
			}
			else if(killer != null && killer.isPlayer() && killer.isCursedWeaponEquipped())
			{
				CursedWeaponsManager.getInstance().increaseKills(((Player) killer).getCursedWeaponEquippedId());
				checkPvp = false;
			}
		}

		if(checkPvp)
		{
			doPKPVPManage(killer);

			altDeathPenalty(killer);
		}

		//And in the end of process notify death penalty that owner died :)
		getDeathPenalty().notifyDead(killer);

		setIncreasedForce(0);

		if(isInParty() && getParty().isInReflection() && getParty().getReflection() instanceof DimensionalRift)
			((DimensionalRift) getParty().getReflection()).memberDead(this);

		stopWaterTask();

		if(!isSalvation() && isOnSiegeField() && isCharmOfCourage())
		{
			ask(new ConfirmDlgPacket(SystemMsg.YOUR_CHARM_OF_COURAGE_IS_TRYING_TO_RESURRECT_YOU, 60000), new ReviveAnswerListener(this, 100, false));
			setCharmOfCourage(false);
		}

		if(getLevel() < 6)
		{
			Quest q = QuestManager.getQuest(255);
			if(q != null)
				processQuestEvent(q.getName(), "CE30", null);
		}

		if(isMounted())
			_mount.onDeath();

		Servitor[] servitors = getServitors();
		if(servitors.length > 0)
		{
			for(Servitor servitor : servitors)
				servitor.notifyMasterDeath();
		}			
			
		super.onDeath(killer);
	}

	public void restoreExp()
	{
		restoreExp(100.);
	}

	public void restoreExp(double percent)
	{
		if(percent == 0)
			return;

		int lostexp = 0;

		String lostexps = getVar("lostexp");
		if(lostexps != null)
		{
			lostexp = Integer.parseInt(lostexps);
			unsetVar("lostexp");
		}

		if(lostexp != 0)
			addExpAndSp((long) (lostexp * percent / 100), 0);
	}

	public void deathPenalty(Creature killer)
	{
		if(killer == null)
			return;

		final boolean atwar = killer.getPlayer() != null && atWarWith(killer.getPlayer());

		double deathPenaltyBonus = getDeathPenalty().getLevel() * Config.ALT_DEATH_PENALTY_EXPERIENCE_PENALTY;
		if(deathPenaltyBonus < 2)
			deathPenaltyBonus = 1;
		else
			deathPenaltyBonus = deathPenaltyBonus / 2;

		// The death steal you some Exp: 10-40 lvl 8% loose
		double percentLost = 8.0;

		int level = getLevel();
		if(level >= 79)
			percentLost = 1.0;
		else if(level >= 78)
			percentLost = 1.5;
		else if(level >= 76)
			percentLost = 2.0;
		else if(level >= 40)
			percentLost = 4.0;

		if(atwar)
			percentLost = percentLost / 4.0;

		if(isPK()) //TODO: [Bonux] Уточнить.
			percentLost *= 10.;

		// Calculate the Experience loss
		int lostexp = (int) Math.round((Experience.LEVEL[level + 1] - Experience.LEVEL[level]) * percentLost / 100);
		lostexp *= deathPenaltyBonus;

		lostexp = (int) calcStat(Stats.EXP_LOST, lostexp, killer, null);

		// На зарегистрированной осаде нет потери опыта, на чужой осаде - как при обычной смерти от *моба*
		if(isOnSiegeField())
		{
			SiegeEvent<?, ?> siegeEvent = getEvent(SiegeEvent.class);
			if(siegeEvent != null)
				lostexp = 0;

			if(siegeEvent != null)
			{
				int syndromeLvl = 0;
				for(Effect e : getEffectList().getEffects())
				{
					if(e.getSkill().getId() == Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME)
					{
						syndromeLvl = e.getSkill().getLevel();
						break;
					}
				}
	
				if(syndromeLvl == 0)
				{
					Skill skill = SkillTable.getInstance().getInfo(Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME, 1);
					if(skill != null)
						skill.getEffects(this, this, false);
				}
				else if(syndromeLvl < 5)
				{
					getEffectList().stopEffects(Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME);
					Skill skill = SkillTable.getInstance().getInfo(Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME, syndromeLvl + 1);
					skill.getEffects(this, this, false);
				}
				else if(syndromeLvl == 5)
				{
					getEffectList().stopEffects(Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME);
					Skill skill = SkillTable.getInstance().getInfo(Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME, 5);
					skill.getEffects(this, this, false);
				}
			}
		}

		long before = getExp();
		addExpAndSp(-lostexp, 0);
		long lost = before - getExp();

		if(lost > 0)
			setVar("lostexp", lost);
	}

	public void setRequest(Request transaction)
	{
		_request = transaction;
	}

	public Request getRequest()
	{
		return _request;
	}

	/**
	 * Проверка, занят ли игрок для ответа на зарос
	 *
	 * @return true, если игрок не может ответить на запрос
	 */
	public boolean isBusy()
	{
		if(!Config.DISABLE_PARTY_ON_EVENT && isInPvPEvent())
			return false;
		return isProcessingRequest() || isOutOfControl() || isInOlympiadMode() || getLfcGame() != null || getTeam() != TeamType.NONE || isInStoreMode() || isInDuel() || getMessageRefusal() || isBlockAll() || isInvisible();
	}

	public boolean isProcessingRequest()
	{
		if(_request == null)
			return false;
		if(!_request.isInProgress())
			return false;
		return true;
	}

	public boolean isInTrade()
	{
		return isProcessingRequest() && getRequest().isTypeOf(L2RequestType.TRADE);
	}

	public List<L2GameServerPacket> addVisibleObject(GameObject object, Creature dropper)
	{
		if(isLogoutStarted() || object == null || object.getObjectId() == getObjectId() || !object.isVisible())
			return Collections.emptyList();

		return object.addPacketList(this, dropper);
	}

	@Override
	public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper)
	{
		if(isInvisible() && forPlayer.getObjectId() != getObjectId())
			return Collections.emptyList();

		if(getPrivateStoreType() != STORE_PRIVATE_NONE && forPlayer.getVarBoolean("notraders"))
			return Collections.emptyList();

		// Если это фэйк обсервера - не показывать.
		if(isInObserverMode() && getCurrentRegion() != getObserverRegion() && getObserverRegion() == forPlayer.getCurrentRegion())
			return Collections.emptyList();

		List<L2GameServerPacket> list = new ArrayList<L2GameServerPacket>();
		if(forPlayer.getObjectId() != getObjectId())
			list.add(isPolymorphed() ? new NpcInfoPoly(this) : new CIPacket(this, forPlayer));

		if(isSitting() && _sittingObject != null)
			list.add(new ChairSit(this, _sittingObject));

		if(getPrivateStoreType() != STORE_PRIVATE_NONE)
		{
			if(getPrivateStoreType() == STORE_PRIVATE_BUY)
				list.add(new PrivateStoreMsgBuy(this));
			else if(getPrivateStoreType() == STORE_PRIVATE_SELL || getPrivateStoreType() == STORE_PRIVATE_SELL_PACKAGE)
				list.add(new PrivateStoreMsg(this));
			else if(getPrivateStoreType() == STORE_PRIVATE_MANUFACTURE)
				list.add(new RecipeShopMsg(this));
			if(forPlayer.isInZonePeace()) // Мирным торговцам не нужно посылать больше пакетов, для экономии траффика
				return list;
		}

		boolean dualCast = isDualCastingNow();
		if(isCastingNow())
		{
			Creature castingTarget = getCastingTarget();
			Skill castingSkill = getCastingSkill();
			long animationEndTime = getAnimationEndTime();
			if(castingSkill != null && castingTarget != null && castingTarget.isCreature() && animationEndTime > 0)
				list.add(new MagicSkillUse(this, castingTarget, castingSkill.getId(), castingSkill.getLevel(), (int) (animationEndTime - System.currentTimeMillis()), 0, dualCast));
		}

		if(dualCast)
		{
			Creature castingTarget = getDualCastingTarget();
			Skill castingSkill = getDualCastingSkill();
			long animationEndTime = getDualAnimationEndTime();
			if(castingSkill != null && castingTarget != null && castingTarget.isCreature() && animationEndTime > 0)
				list.add(new MagicSkillUse(this, castingTarget, castingSkill.getId(), castingSkill.getLevel(), (int) (animationEndTime - System.currentTimeMillis()), 0, dualCast));
		}

		if(isInCombat())
			list.add(new AutoAttackStartPacket(getObjectId()));

		list.add(RelationChangedPacket.update(forPlayer, this, forPlayer));

		if(isInBoat())
			list.add(getBoat().getOnPacket(this, getInBoatPosition()));
		else
		{
			if(isMoving || isFollow)
				list.add(movePacket());
		}
		return list;
	}

	public List<L2GameServerPacket> removeVisibleObject(GameObject object, List<L2GameServerPacket> list)
	{
		if(isLogoutStarted() || object == null || object.getObjectId() == getObjectId()) // FIXME  || isTeleporting()
			return null;

		List<L2GameServerPacket> result = list == null ? object.deletePacketList() : list;

		if(getParty() != null && object instanceof Creature)
			getParty().removeTacticalSign((Creature) object);

		getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, object);
		return result;
	}

	private void levelSet(int levels)
	{
		if(levels > 0)
		{
			checkLevelUpReward();

			sendPacket(SystemMsg.YOUR_LEVEL_HAS_INCREASED);
			broadcastPacket(new SocialActionPacket(getObjectId(), SocialActionPacket.LEVEL_UP));

			setCurrentHpMp(getMaxHp(), getMaxMp());
			setCurrentCp(getMaxCp());

			Quest q = QuestManager.getQuest(255);
			if(q != null)
				processQuestEvent(q.getName(), "CE40", null);

			// Give Expertise skill of this level
			rewardSkills(true, notifyNewSkills());

			//TODO: [Bonux] Пересмотреть.
			int mentorId = getMenteeList().getMentor();
			// FIXME: subclasses do not have mentor
			if(mentorId != 0 && isBaseClassActive())
			{
				Player mentorPlayer = World.getPlayer(mentorId);
				String mentorName = getMenteeList().get(mentorId).getName();
				if(mentorPlayer != null) // Выдача наставнику Sign of Tutor за лвлап ученика.
				{
					int level = getLevel();
					long itemsCount = 0;
					for(int i = (level - levels + 1); i <= level; i++)
					{
						if(Mentoring.SIGN_OF_TUTOR.containsKey(i))
							itemsCount += Mentoring.SIGN_OF_TUTOR.get(i);
					}
					if(itemsCount > 0)
						Mentoring.sendMentorMail(mentorPlayer, 33804, itemsCount);
				}

				// если ученик по наставничеству достиг 86 лвла
				if(getLevel() >= 76 && isBaseClassActive())
				{
					sendPacket(new SystemMessagePacket(SystemMsg.YOU_REACHED_LEVEL_86_RELATIONSHIP_WITH_S1_CAME_TO_AN_END).addString(mentorName));
					Mentoring.removeEffFromGraduatedMentee(this);
					ItemFunctions.addItem(this, 33800, 1, true);
					getMenteeList().remove(mentorName, false, true);

					if(mentorPlayer != null)
					{
						mentorPlayer.sendPacket(new SystemMessagePacket(SystemMsg.THE_MENTEE_S1_HAS_REACHED_LEVEL_86).addName(this));
						mentorPlayer.getMenteeList().remove(_name, true, false);
						Mentoring.applyMentoringCond(this, false);
						if(Mentoring.getGraduatedMenteesCount(mentorId) == -1) //first time
							Mentoring.setNewMenteesCount(mentorId, 1);	
						else if(Mentoring.getGraduatedMenteesCount(mentorId) == 2) //this time setting the penalty
						{
							Mentoring.unsetMenteesCount(mentorId);
							Mentoring.setTimePenalty(mentorId, System.currentTimeMillis() + 5 * 24 * 3600 * 1000L, -1);
						}
						else //adding new one
							Mentoring.setNewMenteesCount(mentorId, Mentoring.getGraduatedMenteesCount(mentorId));
					}
				}
			}
		}
		else if(levels < 0)
		{
			if(Config.ALT_REMOVE_SKILLS_ON_DELEVEL)
			{
				if(checkSkills())
				{
					sendUserInfo(true);
					sendPacket(new ExSubjobInfo(this, false));
					sendPacket(new SkillListPacket(this));
					sendPacket(new AcquireSkillListPacket(this));
				}
			}
		}

		// Recalculate the party level
		if(isInParty())
			getParty().recalculatePartyData();

		if(_clan != null)
			_clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdatePacket(this));

		if(_matchingRoom != null)
			_matchingRoom.broadcastPlayerUpdate(this);
	}

	public boolean notifyNewSkills()
	{
		final Collection<SkillLearn> skills = SkillAcquireHolder.getInstance().getAvailableSkills(this, AcquireType.NORMAL);
		for(SkillLearn s : skills)
		{
			if(s.isFreeAutoGet())
				continue;

			Skill sk = SkillTable.getInstance().getInfo(s.getId(), s.getLevel());
			if(sk == null)
				continue;

			sendPacket(ExNewSkillToLearnByLevelUp.STATIC);
			return true;
		}
		return false;
	}

	/**
	 * Удаляет все скиллы, которые учатся на уровне большем, чем текущий+maxDiff
	 */
	public boolean checkSkills()
	{
		boolean update = false;
		for(Skill sk : getAllSkillsArray())
		{
			if(SkillTreeTable.checkSkill(this, sk))
				update = true;
		}
		return update;
	}

	public void startTimers()
	{
		startAutoSaveTask();
		startPcBangPointsTask();
		startBonusTask();
		getInventory().startTimers();
		resumeQuestTimers();
	}

	public void stopAllTimers()
	{
		setAgathion(0);
		stopWaterTask();
		stopBonusTask();
		stopHourlyTask();
		stopKickTask();
		stopPcBangPointsTask();
		stopAutoSaveTask();
		stopRecomBonusTask(true);
		getInventory().stopAllTimers();
		stopQuestTimers();
	}

	@Override
	public boolean isMyServitor(int objId)
	{
		if(_pet == null && _summons.isEmpty())
			return false;

		if(_pet != null && _pet.getObjectId() == objId)
			return true;

		return _summons.containsKey(objId);
	}

	public int getServitorsCount()
	{
		int count = _summons.size();
		if(_pet != null)
			count++;
		return count;
	}

	@Override
	public Servitor[] getServitors()
	{
		List<Servitor> servitors = new ArrayList<Servitor>();
		if(!_summons.isEmpty())
		{
			for(TIntObjectIterator<SummonInstance> iterator = _summons.iterator(); iterator.hasNext();)
			{
				iterator.advance();
				servitors.add(iterator.value());
			}
		}

		if(_pet != null)
			servitors.add(_pet);

		if(!servitors.isEmpty())
		{
			Servitor[] result = servitors.toArray(new Servitor[servitors.size()]);
			Arrays.sort(result, Servitor.ServitorComparator.getInstance());
			return result;
		}

		return new Servitor[0];
	}

	public Servitor getServitor(int objId)
	{
		if(_pet != null && _pet.getObjectId() == objId)
			return _pet;

		return getSummon(objId);
	}

	public int getSummonsCount()
	{
		return _summons.size();
	}

	public SummonInstance[] getSummons()
	{
		SummonInstance[] result = _summons.values(new SummonInstance[_summons.size()]);
		Arrays.sort(result, Servitor.ServitorComparator.getInstance());
		return result;
	}

	public SummonInstance getSummon(int objId)
	{
		return _summons.get(objId);
	}

	public void addSummon(SummonInstance summon)
	{
		_summons.put(summon.getObjectId(), summon);
		autoShot();
	}

	public void deleteServitor(int objId)
	{
		if(_summons.containsKey(objId))
			deleteSummon(objId);
		else if(_pet != null && _pet.getObjectId() == objId)
			setPet(null);
	}

	public void deleteSummon(int objId)
	{
		_summons.remove(objId);
		autoShot(); //TODO: [Bonux] проверить, нужно ли.
		getEffectList().stopEffects(4140); //TODO: [Bonux] Проверить что это и с чем едят.
	}

	public PetInstance getPet()
	{
		return _pet;
	}

	public void setPet(PetInstance pet)
	{
		boolean petDeleted = _pet != null;
		_pet = pet;
		unsetVar("pet");
		autoShot();
		if(pet == null)
		{
			if(petDeleted)
			{
				if(isLogoutStarted())
				{
					if(getPetControlItem() != null)
						setVar("pet", getPetControlItem().getObjectId());
				}
				setPetControlItem(null);
			}
			getEffectList().stopEffects(4140); //TODO: [Bonux] Нужно ли у петов?
		}
	}

	public void scheduleDelete()
	{
		long time = 0L;

		if(Config.SERVICES_ENABLE_NO_CARRIER)
			time = NumberUtils.toInt(getVar("noCarrier"), Config.SERVICES_NO_CARRIER_DEFAULT_TIME);

		scheduleDelete(time * 1000L);
	}

	/**
	 * Удалит персонажа из мира через указанное время, если на момент истечения времени он не будет присоединен.
	 * <br><br>
	 * TODO: через минуту делать его неуязвимым.<br>
	 * TODO: сделать привязку времени к контексту, для зон с лимитом времени оставлять в игре на все время в зоне.<br>
	 * <br>
	 *
	 * @param time время в миллисекундах
	 */
	public void scheduleDelete(long time)
	{
		if(isLogoutStarted() || isInOfflineMode())
			return;

		broadcastCharInfo();

		ThreadPoolManager.getInstance().schedule(new RunnableImpl(){
			@Override
			public void runImpl() throws Exception
			{
				if(!isConnected())
				{
					prepareToLogout1();
					prepareToLogout2();
					deleteMe();
				}
			}
		}, time);
	}

	@Override
	protected void onDelete()
	{
		super.onDelete();

		// Убираем фэйк в точке наблюдения
		WorldRegion observerRegion = getObserverRegion();
		if(observerRegion != null)
			observerRegion.removeObject(this);

		//Send friendlists to friends that this player has logged off
		_friendList.notifyFriends(false);

		getBookMarkList().clear();

		_inventory.clear();
		_warehouse.clear();
		_summons.clear();
		_pet = null;
		_arrowItem = null;
		_fistsWeaponItem = null;
		_chars = null;
		_enchantScroll = null;
		_lastNpc = HardReferences.emptyRef();
		_observerRegion = null;
	}

	public void setTradeList(List<TradeItem> list)
	{
		_tradeList = list;
	}

	public List<TradeItem> getTradeList()
	{
		return _tradeList;
	}

	public String getSellStoreName()
	{
		return _sellStoreName;
	}

	public void setSellStoreName(String name)
	{
		_sellStoreName = Strings.stripToSingleLine(name);
	}

	public void setSellList(boolean packageSell, List<TradeItem> list)
	{
		if(packageSell)
			_packageSellList = list;
		else
			_sellList = list;
	}

	public List<TradeItem> getSellList()
	{
		return getSellList(_privatestore == STORE_PRIVATE_SELL_PACKAGE);
	}

	public List<TradeItem> getSellList(boolean packageSell)
	{
		return packageSell ? _packageSellList : _sellList;
	}

	public String getBuyStoreName()
	{
		return _buyStoreName;
	}

	public void setBuyStoreName(String name)
	{
		_buyStoreName = Strings.stripToSingleLine(name);
	}

	public void setBuyList(List<TradeItem> list)
	{
		_buyList = list;
	}

	public List<TradeItem> getBuyList()
	{
		return _buyList;
	}

	public void setManufactureName(String name)
	{
		_manufactureName = Strings.stripToSingleLine(name);
	}

	public String getManufactureName()
	{
		return _manufactureName;
	}

	public List<ManufactureItem> getCreateList()
	{
		return _createList;
	}

	public void setCreateList(List<ManufactureItem> list)
	{
		_createList = list;
	}

	public void setPrivateStoreType(final int type)
	{
		System.out.println("setPrivateStoreType " + type);
		_privatestore = type;
		if(type != STORE_PRIVATE_NONE)
			setVar("storemode", type);
		else
			unsetVar("storemode");
	}

	public boolean isInStoreMode()
	{
		return _privatestore != STORE_PRIVATE_NONE;
	}

	public int getPrivateStoreType()
	{
		return _privatestore;
	}

	/**
	 * Set the _clan object, _clanId, _clanLeader Flag and title of the L2Player.<BR><BR>
	 *
	 * @param clan the clat to set
	 */
	public void setClan(Clan clan)
	{
		if(_clan != clan && _clan != null)
			unsetVar("canWhWithdraw");

		Clan oldClan = _clan;
		if(oldClan != null && clan == null)
			for(Skill skill : oldClan.getAllSkills())
				removeSkill(skill, false);

		_clan = clan;

		if(clan == null)
		{
			_pledgeType = Clan.SUBUNIT_NONE;
			_pledgeRank = PledgeRank.VAGABOND;
			_powerGrade = 0;
			_apprentice = 0;
			getInventory().validateItems();
			return;
		}

		if(!clan.isAnyMember(getObjectId()))
		{
			setClan(null);
			if(!isNoble())
				setTitle("");
		}
	}

	@Override
	public Clan getClan()
	{
		return _clan;
	}

	public SubUnit getSubUnit()
	{
		return _clan == null ? null : _clan.getSubUnit(_pledgeType);
	}

	public ClanHall getClanHall()
	{
		int id = _clan != null ? _clan.getHasHideout() : 0;
		return ResidenceHolder.getInstance().getResidence(ClanHall.class, id);
	}

	public Castle getCastle()
	{
		int id = _clan != null ? _clan.getCastle() : 0;
		return ResidenceHolder.getInstance().getResidence(Castle.class, id);
	}

	public Fortress getFortress()
	{
		int id = _clan != null ? _clan.getHasFortress() : 0;
		return ResidenceHolder.getInstance().getResidence(Fortress.class, id);
	}

	public Alliance getAlliance()
	{
		return _clan == null ? null : _clan.getAlliance();
	}

	public boolean isClanLeader()
	{
		return _clan != null && getObjectId() == _clan.getLeaderId();
	}

	public boolean isAllyLeader()
	{
		return getAlliance() != null && getAlliance().getLeader().getLeaderId() == getObjectId();
	}

	@Override
	public void reduceArrowCount()
	{
		if(_arrowItem != null && _arrowItem.getTemplate().isQuiver())
			return;

		sendPacket(SystemMsg.YOU_CAREFULLY_NOCK_AN_ARROW);
		if(!getInventory().destroyItemByObjectId(getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LHAND), 1L))
		{
			getInventory().setPaperdollItem(Inventory.PAPERDOLL_LHAND, null);
			_arrowItem = null;
		}
	}

	/**
	 * Equip arrows needed in left hand and send a Server->Client packet ItemListPacket to the L2Player then return True.
	 */
	public boolean checkAndEquipArrows()
	{
		// Check if nothing is equipped in left hand
		if(getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND) == null)
		{
			ItemInstance activeWeapon = getActiveWeaponInstance();
			if(activeWeapon != null)
			{
				if(activeWeapon.getItemType() == WeaponType.BOW)
					_arrowItem = getInventory().findArrowForBow(activeWeapon.getTemplate());
				else if(activeWeapon.getItemType() == WeaponType.CROSSBOW)
					getInventory().findArrowForCrossbow(activeWeapon.getTemplate());
			}

			// Equip arrows needed in left hand
			if(_arrowItem != null)
				getInventory().setPaperdollItem(Inventory.PAPERDOLL_LHAND, _arrowItem);
		}
		else
			// Get the L2ItemInstance of arrows equipped in left hand
			_arrowItem = getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);

		return _arrowItem != null;
	}

	public void setUptime(final long time)
	{
		_uptime = time;
	}

	public long getUptime()
	{
		return System.currentTimeMillis() - _uptime;
	}

	public boolean isInParty()
	{
		return _party != null;
	}

	public void setParty(final Party party)
	{
		_party = party;
	}

	public void joinParty(final Party party)
	{
		if(party != null)
			party.addPartyMember(this);
	}

	public void leaveParty()
	{
		if(isInParty())
			_party.removePartyMember(this, false);
	}

	public Party getParty()
	{
		return _party;
	}

	public void setStartingTimeInFullParty(long time)
	{
		_startingTimeInFullParty = time;
	}

	public long getStartingTimeInFullParty()
	{
		return _startingTimeInFullParty;
	}

	public void setStartingTimeInParty(long time)
	{
		_startingTimeInParty = time;
	}

	public long getStartingTimeInParty()
	{
		return _startingTimeInParty;
	}

	public void setLastPartyPosition(Location loc)
	{
		_lastPartyPosition = loc;
	}

	public Location getLastPartyPosition()
	{
		return _lastPartyPosition;
	}

	public boolean isGM()
	{
		return _playerAccess == null ? false : _playerAccess.IsGM;
	}

	/**
	 * Нигде не используется, но может пригодиться для БД
	 */
	public void setAccessLevel(final int level)
	{
		_accessLevel = level;
	}

	/**
	 * Нигде не используется, но может пригодиться для БД
	 */
	@Override
	public int getAccessLevel()
	{
		return _accessLevel;
	}

	public void setPlayerAccess(final PlayerAccess pa)
	{
		if(pa != null)
			_playerAccess = pa;
		else
			_playerAccess = new PlayerAccess();

		setAccessLevel(isGM() || _playerAccess.Menu ? 100 : 0);
	}

	public PlayerAccess getPlayerAccess()
	{
		return _playerAccess;
	}

	/**
	 * Update Stats of the L2Player client side by sending Server->Client packet UserInfo/StatusUpdatePacket to this L2Player and CIPacket/StatusUpdatePacket to all players around (broadcast).<BR><BR>
	 */
	@Override
	public void updateStats()
	{
		if(entering || isLogoutStarted())
			return;

		refreshOverloaded();
		refreshExpertisePenalty();
		super.updateStats();
	}

	@Override
	public void sendChanges()
	{
		if(entering || isLogoutStarted())
			return;
		super.sendChanges();
	}

	/**
	 * Send a Server->Client StatusUpdatePacket packet with Karma to the L2Player and all L2Player to inform (broadcast).
	 */
	public void updateKarma(boolean flagChanged)
	{
		sendStatusUpdate(true, true, StatusUpdatePacket.KARMA);
		if(flagChanged)
			broadcastRelationChanged();
	}

	public boolean isOnline()
	{
		return _isOnline;
	}

	public void setIsOnline(boolean isOnline)
	{
		_isOnline = isOnline;
	}

	public void setOnlineStatus(boolean isOnline)
	{
		_isOnline = isOnline;
		updateOnlineStatus();
	}

	private void updateOnlineStatus()
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE characters SET online=?, lastAccess=? WHERE obj_id=?");
			statement.setInt(1, isOnline() && !isInOfflineMode() ? 1 : 0);
			statement.setLong(2, System.currentTimeMillis() / 1000L);
			statement.setInt(3, getObjectId());
			statement.execute();
		}
		catch(final Exception e)
		{
			_log.error("", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public void decreaseKarma(final long val)
	{
		boolean flagChanged = _karma >= 0;
		long new_karma = _karma - val;

		if(new_karma < Integer.MIN_VALUE)
			new_karma = Integer.MIN_VALUE;

		if(_karma >= 0 && new_karma < 0)
		{
			if(_pvpFlag > 0)
			{
				_pvpFlag = 0;
				if(_PvPRegTask != null)
				{
					_PvPRegTask.cancel(true);
					_PvPRegTask = null;
				}
				sendStatusUpdate(true, true, StatusUpdatePacket.PVP_FLAG);
			}

			_karma = (int) new_karma;
		}
		else
			_karma = (int) new_karma;

		updateKarma(flagChanged);
	}

	public void increaseKarma(final int val)
	{
		boolean flagChanged = _karma < 0;
		long new_karma = _karma + val;
		if(new_karma > Integer.MAX_VALUE)
			new_karma = Integer.MAX_VALUE;

		_karma = (int) new_karma;
		if(_karma > 0)
			updateKarma(flagChanged);
		else
			updateKarma(false);
	}

	public static Player create(int classId, int sex, String accountName, final String name, final int hairStyle, final int hairColor, final int face)
	{
		ClassId class_id = ClassId.VALUES[classId];
		PlayerTemplate template = PlayerTemplateHolder.getInstance().getPlayerTemplate(class_id.getRace(), class_id, Sex.VALUES[sex]);

		// Create a new L2Player with an account name
		Player player = new Player(IdFactory.getInstance().getNextId(), template, accountName);

		player.setName(name);
		player.setTitle("");
		player.setHairStyle(hairStyle);
		player.setHairColor(hairColor);
		player.setFace(face);
		player.setCreateTime(System.currentTimeMillis());

		// Add the player in the characters table of the database
		if(!CharacterDAO.getInstance().insert(player))
			return null;

		int level = Config.STARTING_LVL;
		double hp = class_id.getBaseHp(level);
		double mp = class_id.getBaseMp(level);
		double cp = class_id.getBaseCp(level);
		long exp = Experience.getExpForLevel(level);
		int sp = Config.STARTING_SP;
		boolean active = true;
		SubClassType type = SubClassType.BASE_CLASS;

		// Add the player subclass in the character_subclasses table of the database
		if(!CharacterSubclassDAO.getInstance().insert(player.getObjectId(), classId, classId, exp, sp, hp, mp, cp, hp, mp, cp, level, active, type, 0, 0, MAX_VITALITY_POINTS, 0, 0))
			return null;

		return player;
	}

	public static Player restore(final int objectId)
	{
		Player player = null;
		Connection con = null;
		Statement statement = null;
		Statement statement2 = null;
		PreparedStatement statement3 = null;
		ResultSet rset = null;
		ResultSet rset2 = null;
		ResultSet rset3 = null;
		try
		{
			// Retrieve the L2Player from the characters table of the database
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.createStatement();
			statement2 = con.createStatement();
			rset = statement.executeQuery("SELECT * FROM `characters` WHERE `obj_Id`=" + objectId + " LIMIT 1");
			rset2 = statement2.executeQuery("SELECT `class_id`, `default_class_id` FROM `character_subclasses` WHERE `char_obj_id`=" + objectId + " AND `type`=" + SubClassType.BASE_CLASS.ordinal() + " LIMIT 1");

			if(rset.next() && rset2.next())
			{
				final ClassId classId = ClassId.VALUES[rset2.getInt("class_id")];
				final ClassId defaultClassId = ClassId.VALUES[rset2.getInt("default_class_id")];
				final PlayerTemplate template = PlayerTemplateHolder.getInstance().getPlayerTemplate(defaultClassId.getRace(), classId, Sex.VALUES[rset.getInt("sex")]);;

				player = new Player(objectId, template);

				player.restoreVariables();
				player.loadInstanceReuses();
				player.getBookMarkList().setCapacity(rset.getInt("bookmarks"));
				player.getBookMarkList().restore();
				player.setBotRating(rset.getInt("bot_rating"));
				player.getFriendList().restore();
				player.getBlockList().restore();
				player.getPremiumItemList().restore();
				player.setPostFriends(CharacterPostFriendDAO.getInstance().select(player));
				CharacterGroupReuseDAO.getInstance().select(player);

				player.setLogin(rset.getString("account_name"));
				player.setName(rset.getString("char_name"));

				player.setFace(rset.getInt("face"));
				player.setBeautyFace(rset.getInt("beautyFace"));
				player.setHairStyle(rset.getInt("hairStyle"));
				player.setBeautyHairStyle(rset.getInt("beautyHairStyle"));
				player.setHairColor(rset.getInt("hairColor"));
				player.setBeautyHairColor(rset.getInt("beautyHairColor"));
				player.setHeading(0);

				player.setKarma(rset.getInt("karma"));
				player.setPvpKills(rset.getInt("pvpkills"));
				player.setPkKills(rset.getInt("pkkills"));
				player.setLeaveClanTime(rset.getLong("leaveclan") * 1000L);
				if(player.getLeaveClanTime() > 0 && player.canJoinClan())
					player.setLeaveClanTime(0);
				player.setDeleteClanTime(rset.getLong("deleteclan") * 1000L);
				if(player.getDeleteClanTime() > 0 && player.canCreateClan())
					player.setDeleteClanTime(0);

				player.setNoChannel(rset.getLong("nochannel") * 1000L);
				if(player.getNoChannel() > 0 && player.getNoChannelRemained() < 0)
					player.setNoChannel(0);

				player.setOnlineTime(rset.getLong("onlinetime") * 1000L);

				final int clanId = rset.getInt("clanid");
				if(clanId > 0)
				{
					player.setClan(ClanTable.getInstance().getClan(clanId));
					player.setPledgeType(rset.getInt("pledge_type"));
					player.setPowerGrade(rset.getInt("pledge_rank"));
					player.setLvlJoinedAcademy(rset.getInt("lvl_joined_academy"));
					player.setApprentice(rset.getInt("apprentice"));
				}

				player.setCreateTime(rset.getLong("createtime") * 1000L);
				player.setDeleteTimer(rset.getInt("deletetime"));

				player.setTitle(rset.getString("title"));

				if(player.getVar("titlecolor") != null)
					player.setTitleColor(Integer.decode("0x" + player.getVar("titlecolor")));

				if(player.getVar("namecolor") == null)
					if(player.isGM())
						player.setNameColor(Config.GM_NAME_COLOUR);
					else if(player.getClan() != null && player.getClan().getLeaderId() == player.getObjectId())
						player.setNameColor(Config.CLANLEADER_NAME_COLOUR);
					else
						player.setNameColor(Config.NORMAL_NAME_COLOUR);
				else
					player.setNameColor(Integer.decode("0x" + player.getVar("namecolor")));

				if(Config.AUTO_LOOT_INDIVIDUAL)
				{
					player._autoLoot = player.getVarBoolean("AutoLoot", Config.AUTO_LOOT);
					player._autoLootOnlyAdena = player.getVarBoolean("AutoLootOnlyAdena", Config.AUTO_LOOT);
					player.AutoLootHerbs = player.getVarBoolean("AutoLootHerbs", Config.AUTO_LOOT_HERBS);
				}

				player.setUptime(System.currentTimeMillis());
				player.setLastAccess(rset.getLong("lastAccess"));

				player.setRecomHave(rset.getInt("rec_have"));
				player.setRecomLeft(rset.getInt("rec_left"));
				player.setRecomBonusTime(rset.getInt("rec_bonus_time"));

				if(player.getVar("recLeftToday") != null)
					player.setRecomLeftToday(Integer.parseInt(player.getVar("recLeftToday")));
				else
					player.setRecomLeftToday(0);

				if(!Config.USE_CLIENT_LANG)
					player.setLanguage(player.getVar(Language.LANG_VAR));

				player.setKeyBindings(rset.getBytes("key_bindings"));
				player.setPcBangPoints(rset.getInt("pcBangPoints"));

				player.setFame(rset.getInt("fame"), null);

				player.setUsedWorldChatPoints(rset.getInt("used_world_chat_points"));

				player.restoreRecipeBook();

				if(Config.ENABLE_OLYMPIAD)
				{
					player.setHero(Hero.getInstance().isHero(player.getObjectId()));
					player.setNoble(Olympiad.isNoble(player.getObjectId()), false);
				}

				if(!player.isHero())
					player.setHero(CustomHeroDAO.getInstance().isCustomHero(player.getObjectId()));

				player.updatePledgeRank();

				int reflection = 0;

				if(player.getVar("jailed") != null && System.currentTimeMillis() / 1000 < Integer.parseInt(player.getVar("jailed")) + 60)
				{
					player.setXYZ(-114648, -249384, -2984);
					/*NOTUSED*String[] re = player.getVar("jailedFrom").split(";");
					Location loc = new Location(Integer.parseInt(re[0]), Integer.parseInt(re[1]), Integer.parseInt(re[2]));*/
					player.sitDown(null);
					player.block();
					player._unjailTask = ThreadPoolManager.getInstance().schedule(new UnJailTask(player), Integer.parseInt(player.getVar("jailed")) * 1000L);
				}
				else
				{
					player.setXYZ(rset.getInt("x"), rset.getInt("y"), rset.getInt("z"));

					//Если игрок вышел во время прыжка, то возвращаем его в стабильную точку (стартовую).
					String jumpSafeLoc = player.getVar("@safe_jump_loc");
					if(jumpSafeLoc != null)
					{
						player.setLoc(Location.parseLoc(jumpSafeLoc));
						player.unsetVar("@safe_jump_loc");
					}

					String ref = player.getVar("reflection");
					if(ref != null)
					{
						reflection = Integer.parseInt(ref);
						if(reflection > 0) // не портаем назад из ГХ, парнаса, джайла
						{
							String back = player.getVar("backCoords");
							if(back != null)
							{
								player.setLoc(Location.parseLoc(back));
								player.unsetVar("backCoords");
							}
							reflection = 0;
						}
					}
				}

				player.setReflection(reflection);

				EventHolder.getInstance().findEvent(player);

				//TODO [G1ta0] запускать на входе
				Quest.restoreQuestStates(player);

				player.getInventory().restore();

				player.getSubClassList().restore();

				player.setActiveSubClass(player.getActiveClassId(), false, true);

				player.getMenteeList().restore();

				player.restoreSummons();

				try
				{
					String var = player.getVar("ExpandInventory");
					if(var != null)
						player.setExpandInventory(Integer.parseInt(var));
				}
				catch(Exception e)
				{
					_log.error("", e);
				}

				try
				{
					String var = player.getVar("ExpandWarehouse");
					if(var != null)
						player.setExpandWarehouse(Integer.parseInt(var));
				}
				catch(Exception e)
				{
					_log.error("", e);
				}

				try
				{
					String var = player.getVar(NO_ANIMATION_OF_CAST_VAR);
					if(var != null)
						player.setNotShowBuffAnim(Boolean.parseBoolean(var));
				}
				catch(Exception e)
				{
					_log.error("", e);
				}

				try
				{
					String var = player.getVar(NO_TRADERS_VAR);
					if(var != null)
						player.setNotShowTraders(Boolean.parseBoolean(var));
				}
				catch(Exception e)
				{
					_log.error("", e);
				}

				try
				{
					String var = player.getVar("pet");
					if(var != null)
						player.setPetControlItem(Integer.parseInt(var));
				}
				catch(Exception e)
				{
					_log.error("", e);
				}

				statement3 = con.prepareStatement("SELECT obj_Id, char_name FROM characters WHERE account_name=? AND obj_Id!=?");
				statement3.setString(1, player._login);
				statement3.setInt(2, objectId);
				rset3 = statement3.executeQuery();
				while(rset3.next())
				{
					final Integer charId = rset3.getInt("obj_Id");
					final String charName = rset3.getString("char_name");
					player._chars.put(charId, charName);
				}

				DbUtils.close(statement3, rset3);

				//if(!player.isGM())
				{
					LazyArrayList<Zone> zones = LazyArrayList.newInstance();

					World.getZones(zones, player.getLoc(), player.getReflection());

					if(!zones.isEmpty())
						for(Zone zone : zones)
							if(zone.getType() == ZoneType.no_restart)
							{
								if(System.currentTimeMillis() / 1000L - player.getLastAccess() > zone.getRestartTime())
								{
									player.sendMessage(new CustomMessage("l2s.gameserver.clientpackets.EnterWorld.TeleportedReasonNoRestart", player));
									player.setLoc(TeleportUtils.getRestartLocation(player, RestartType.TO_VILLAGE));
								}
							}
							else if(zone.getType() == ZoneType.SIEGE)
							{
								SiegeEvent<?, ?> siegeEvent = player.getEvent(SiegeEvent.class);
								if(siegeEvent != null)
									player.setLoc(siegeEvent.getEnterLoc(player));
								else
								{
									Residence r = ResidenceHolder.getInstance().getResidence(zone.getParams().getInteger("residence"));
									player.setLoc(r.getNotOwnerRestartPoint(player));
								}
							}

					LazyArrayList.recycle(zones);

					if(DimensionalRiftManager.getInstance().checkIfInRiftZone(player.getLoc(), false))
						player.setLoc(DimensionalRiftManager.getInstance().getRoom(0, 0).getTeleportCoords());
				}

				player.getMacroses().restore();

				//FIXME [VISTALL] нужно ли?
				player.refreshExpertisePenalty();
				player.refreshOverloaded();

				player.getWarehouse().restore();
				player.getFreight().restore();

				player.restoreTradeList();
				if(player.getVar("storemode") != null)
				{
					player.setPrivateStoreType(Integer.parseInt(player.getVar("storemode")));
					player.setSitting(true);
				}

				player.updateKetraVarka();
				player.updateRam();
				player.checkRecom();
				player.checkVitality();
				player.checkWorldChatPoints();
			}
		}
		catch(final Exception e)
		{
			_log.error("Could not restore char data!", e);
		}
		finally
		{
			DbUtils.closeQuietly(statement2, rset2);
			DbUtils.closeQuietly(statement3, rset3);
			DbUtils.closeQuietly(con, statement, rset);
		}
		return player;
	}

	/**
	 * Update L2Player stats in the characters table of the database.
	 */
	public void store(boolean fast)
	{
		if(!_storeLock.tryLock())
			return;

		try
		{
			Connection con = null;
			PreparedStatement statement = null;
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement(//
				"UPDATE characters SET face=?,beautyFace=?,hairStyle=?,beautyHairStyle=?,hairColor=?,beautyHairColor=?,sex=?,x=?,y=?,z=?" + //
				",karma=?,pvpkills=?,pkkills=?,rec_have=?,rec_left=?,rec_bonus_time=?,clanid=?,deletetime=?," + //
				"title=?,accesslevel=?,online=?,leaveclan=?,deleteclan=?,nochannel=?," + //
				"onlinetime=?,pledge_type=?,pledge_rank=?,lvl_joined_academy=?,apprentice=?,key_bindings=?,pcBangPoints=?,char_name=?,fame=?,bookmarks=?,bot_rating=?,used_world_chat_points=? WHERE obj_Id=? LIMIT 1");
				statement.setInt(1, getFace());
				statement.setInt(2, getBeautyFace());
				statement.setInt(3, getHairStyle());
				statement.setInt(4, getBeautyHairStyle());
				statement.setInt(5, getHairColor());
				statement.setInt(6, getBeautyHairColor());
				statement.setInt(7, getSex().ordinal());
				if(_stablePoint == null) // если игрок находится в точке в которой его сохранять не стоит (например на виверне) то сохраняются последние координаты
				{
					statement.setInt(8, getX());
					statement.setInt(9, getY());
					statement.setInt(10, getZ());
				}
				else
				{
					statement.setInt(8, _stablePoint.x);
					statement.setInt(9, _stablePoint.y);
					statement.setInt(10, _stablePoint.z);
				}
				statement.setInt(11, getKarma());
				statement.setInt(12, getPvpKills());
				statement.setInt(13, getPkKills());
				statement.setInt(14, getRecomHave());
				if(getRecomLeft() > 255)
					setRecomLeft(255);
				statement.setInt(15, getRecomLeft());
				statement.setInt(16, getRecomBonusTime());
				statement.setInt(17, getClanId());
				statement.setInt(18, getDeleteTimer());
				statement.setString(19, _title);
				statement.setInt(20, _accessLevel);
				statement.setInt(21, isOnline() && !isInOfflineMode() ? 1 : 0);
				statement.setLong(22, getLeaveClanTime() / 1000L);
				statement.setLong(23, getDeleteClanTime() / 1000L);
				statement.setLong(24, _NoChannel > 0 ? getNoChannelRemained() / 1000 : _NoChannel);
				statement.setInt(25, getOnlineTime());
				statement.setInt(26, getPledgeType());
				statement.setInt(27, getPowerGrade());
				statement.setInt(28, getLvlJoinedAcademy());
				statement.setInt(29, getApprentice());
				statement.setBytes(30, getKeyBindings());
				statement.setInt(31, getPcBangPoints());
				statement.setString(32, getName());
				statement.setInt(33, getFame());
				statement.setInt(34, getBookMarkList().getCapacity());
				statement.setInt(35, getBotRating());
				statement.setInt(36, getUsedWorldChatPoints());
				statement.setInt(37, getObjectId());


				statement.executeUpdate();
				//GameStats.increaseUpdatePlayerBase();

				if(!fast)
				{
					EffectsDAO.getInstance().insert(this);
					CharacterGroupReuseDAO.getInstance().insert(this);
					storeDisableSkills();
				}

				storeCharSubClasses();
				getBookMarkList().store();
			}
			catch(Exception e)
			{
				_log.error("Could not store char data: " + this + "!", e);
			}
			finally
			{
				DbUtils.closeQuietly(con, statement);
			}
		}
		finally
		{
			_storeLock.unlock();
		}
	}

	/**
	 * Add a skill to the L2Player _skills and its Func objects to the calculator set of the L2Player and save update in the character_skills table of the database.
	 *
	 * @return The L2Skill replaced or null if just added a new L2Skill
	 */
	public Skill addSkill(final Skill newSkill, final boolean store)
	{
		if(newSkill == null)
			return null;

		// Add a skill to the L2Player _skills and its Func objects to the calculator set of the L2Player
		Skill oldSkill = addSkill(newSkill);
		if(newSkill.equals(oldSkill))
			return oldSkill;

		// Add or update a L2Player skill in the character_skills table of the database
		if(store)
			storeSkill(newSkill, oldSkill);

		return oldSkill;
	}

	public Skill removeSkill(Skill skill, boolean fromDB)
	{
		if(skill == null)
			return null;
		return removeSkill(skill.getId(), fromDB);
	}

	/**
	 * Remove a skill from the L2Character and its Func objects from calculator set of the L2Character and save update in the character_skills table of the database.
	 *
	 * @return The L2Skill removed
	 */
	public Skill removeSkill(int id, boolean fromDB)
	{
		// Remove a skill from the L2Character and its Func objects from calculator set of the L2Character
		Skill oldSkill = removeSkillById(id);

		if(!fromDB)
			return oldSkill;

		if(oldSkill != null)
		{
			Connection con = null;
			PreparedStatement statement = null;
			try
			{
				// Remove or update a L2Player skill from the character_skills table of the database
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("DELETE FROM character_skills WHERE skill_id=? AND char_obj_id=? AND (class_index=? OR class_index=-1 OR class_index=-2)");
				statement.setInt(1, oldSkill.getId());
				statement.setInt(2, getObjectId());
				statement.setInt(3, getActiveClassId());
				statement.execute();
			}
			catch(final Exception e)
			{
				_log.error("Could not delete skill!", e);
			}
			finally
			{
				DbUtils.closeQuietly(con, statement);
			}
		}

		return oldSkill;
	}

	/**
	 * Add or update a L2Player skill in the character_skills table of the database.
	 */
	private void storeSkill(final Skill newSkill, final Skill oldSkill)
	{
		if(newSkill == null) // вообще-то невозможно
		{
			_log.warn("could not store new skill. its NULL");
			return;
		}

		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();

			statement = con.prepareStatement("REPLACE INTO character_skills (char_obj_id,skill_id,skill_level,class_index) values(?,?,?,?)");
			statement.setInt(1, getObjectId());
			statement.setInt(2, newSkill.getId());
			statement.setInt(3, newSkill.getLevel());

			// Скиллы сертификации доступны на всех саб-классах.
			if(newSkill.isCertification())
				statement.setInt(4, -1);
			else if(newSkill.isDualCertification())
				statement.setInt(4, -2);
			else
				statement.setInt(4, getActiveClassId());

			statement.execute();
		}
		catch(final Exception e)
		{
			_log.error("Error could not store skills!", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	private void restoreSkills()
	{
		restoreSkills(false);
	}

	public void restoreSkills(boolean dualClassSkillsOnly)
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			if(dualClassSkillsOnly)
			{
				statement = con.prepareStatement("SELECT skill_id,skill_level FROM character_skills WHERE char_obj_id=? AND class_index=-2");
				statement.setInt(1, getObjectId());
			}
			else
			{
				statement = con.prepareStatement("SELECT skill_id,skill_level FROM character_skills WHERE char_obj_id=? AND (class_index=? OR class_index=-1 OR class_index=-2)");
				statement.setInt(1, getObjectId());
				statement.setInt(2, getActiveClassId());
			}
			rset = statement.executeQuery();

			while(rset.next())
			{
				final Skill skill = SkillTable.getInstance().getInfo(rset.getInt("skill_id"), rset.getInt("skill_level"));
				if(skill == null)
					continue;

				// Remove skill if not possible
				if(!isGM() && !SkillAcquireHolder.getInstance().isSkillPossible(this, skill))
				{
					removeSkill(skill, true);
					//removeSkillFromShortCut(skill.getId());
					//TODO audit
					continue;
				}

				if(!skill.isDualCertification() || skill.isDualCertification() && (isBaseClassActive() || isDualClassActive()))
					addSkill(skill);
			}

			if(dualClassSkillsOnly)
				return;

			// Restore noble skills
			checkNobleSkills();

			// Restore Hero skills at main class only
			checkHeroSkills();

			// Restore clan skills
			if(_clan != null)
			{
				_clan.addSkillsQuietly(this);

				// Restore clan leader siege skills
				if(_clan.getLeaderId() == getObjectId() && _clan.getLevel() >= 5)
					SiegeUtils.addSiegeSkills(this);
			}

			if(Config.UNSTUCK_SKILL && getSkillLevel(1050) < 0)
				addSkill(SkillTable.getInstance().getInfo(2099, 1));
		}
		catch(final Exception e)
		{
			_log.warn("Could not restore skills for player objId: " + getObjectId());
			_log.error("", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}

	public void storeDisableSkills()
	{
		Connection con = null;
		Statement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.createStatement();
			statement.executeUpdate("DELETE FROM character_skills_save WHERE char_obj_id = " + getObjectId() + " AND class_index=" + getActiveClassId() + " AND `end_time` < " + System.currentTimeMillis());

			if(_skillReuses.isEmpty())
				return;

			SqlBatch b = new SqlBatch("REPLACE INTO `character_skills_save` (`char_obj_id`,`skill_id`,`skill_level`,`class_index`,`end_time`,`reuse_delay_org`) VALUES");
			synchronized (_skillReuses)
			{
				StringBuilder sb;
				for(TimeStamp timeStamp : _skillReuses.values())
				{
					if(timeStamp.hasNotPassed())
					{
						sb = new StringBuilder("(");
						sb.append(getObjectId()).append(",");
						sb.append(timeStamp.getId()).append(",");
						sb.append(timeStamp.getLevel()).append(",");
						sb.append(getActiveClassId()).append(",");
						sb.append(timeStamp.getEndTime()).append(",");
						sb.append(timeStamp.getReuseBasic()).append(")");
						b.write(sb.toString());
					}
				}
			}
			if(!b.isEmpty())
				statement.executeUpdate(b.close());
		}
		catch(final Exception e)
		{
			_log.warn("Could not store disable skills data: " + e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public void restoreDisableSkills()
	{
		_skillReuses.clear();

		Connection con = null;
		Statement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.createStatement();
			rset = statement.executeQuery("SELECT skill_id,skill_level,end_time,reuse_delay_org FROM character_skills_save WHERE char_obj_id=" + getObjectId() + " AND class_index=" + getActiveClassId());
			while(rset.next())
			{
				int skillId = rset.getInt("skill_id");
				int skillLevel = rset.getInt("skill_level");
				long endTime = rset.getLong("end_time");
				long rDelayOrg = rset.getLong("reuse_delay_org");
				long curTime = System.currentTimeMillis();

				Skill skill = SkillTable.getInstance().getInfo(skillId, skillLevel);

				if(skill != null && endTime - curTime > 500)
					_skillReuses.put(skill.getReuseHash(), new TimeStamp(skill, endTime, rDelayOrg));
			}
			DbUtils.close(statement);

			statement = con.createStatement();
			statement.executeUpdate("DELETE FROM character_skills_save WHERE char_obj_id = " + getObjectId() + " AND class_index=" + getActiveClassId() + " AND `end_time` < " + System.currentTimeMillis());
		}
		catch(Exception e)
		{
			_log.error("Could not restore active skills data!", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}

	/**
	 * Retrieve from the database all Henna of this L2Player, add them to _henna and calculate stats of the L2Player.<BR><BR>
	 */
	private void restoreHenna()
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT slot, symbol_id FROM character_hennas WHERE char_obj_id=? AND class_index=?");
			statement.setInt(1, getObjectId());
			statement.setInt(2, getActiveClassId());
			rset = statement.executeQuery();

			for(int i = 0; i < 3; i++)
				_henna[i] = null;

			while(rset.next())
			{
				final int slot = rset.getInt("slot");
				if(slot < 1 || slot > 3)
					continue;

				final int symbol_id = rset.getInt("symbol_id");

				if(symbol_id != 0)
				{
					final Henna tpl = HennaHolder.getInstance().getHenna(symbol_id);
					if(tpl != null)
					{
						_henna[slot - 1] = tpl;
					}
				}
			}
		}
		catch(final Exception e)
		{
			_log.warn("could not restore henna: " + e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}

		// Calculate Henna modifiers of this L2Player
		recalcHennaStats();

	}

	public int getHennaEmptySlots()
	{
		int totalSlots = 1 + getClassLevel();
		for(int i = 0; i < 3; i++)
			if(_henna[i] != null)
				totalSlots--;

		if(totalSlots <= 0)
			return 0;

		return totalSlots;

	}

	/**
	 * Remove a Henna of the L2Player, save update in the character_hennas table of the database and send Server->Client HennaInfoPacket/UserInfo packet to this L2Player.<BR><BR>
	 */
	public boolean removeHenna(int slot)
	{
		if(slot < 1 || slot > 3)
			return false;

		slot--;

		if(_henna[slot] == null)
			return false;

		final Henna henna = _henna[slot];
		final int dyeID = henna.getDyeId();

		_henna[slot] = null;

		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM character_hennas WHERE char_obj_id=? AND slot=? AND class_index=?");
			statement.setInt(1, getObjectId());
			statement.setInt(2, slot + 1);
			statement.setInt(3, getActiveClassId());
			statement.execute();
		}
		catch(final Exception e)
		{
			_log.warn("could not remove char henna: " + e, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}

		// Calculate Henna modifiers of this L2Player
		recalcHennaStats();

		// Send Server->Client HennaInfoPacket packet to this L2Player
		sendPacket(new HennaInfoPacket(this));
		// Send Server->Client UserInfo packet to this L2Player
		sendUserInfo(true);

		// Add the recovered dyes to the player's inventory and notify them.
		ItemFunctions.addItem(this, dyeID, henna.getRemoveCount(), true);

		return true;
	}

	/**
	 * Add a Henna to the L2Player, save update in the character_hennas table of the database and send Server->Client HennaInfoPacket/UserInfo packet to this L2Player.<BR><BR>
	 *
	 * @param henna L2Henna РґР»СЏ РґРѕР±Р°РІР»РµРЅРёСЏ
	 */
	public boolean addHenna(Henna henna)
	{
		if(getHennaEmptySlots() == 0)
		{
			sendPacket(SystemMsg.NO_SLOT_EXISTS_TO_DRAW_THE_SYMBOL);
			return false;
		}

		// int slot = 0;
		for(int i = 0; i < 3; i++)
			if(_henna[i] == null)
			{
				_henna[i] = henna;

				// Calculate Henna modifiers of this L2Player
				recalcHennaStats();

				Connection con = null;
				PreparedStatement statement = null;
				try
				{
					con = DatabaseFactory.getInstance().getConnection();
					statement = con.prepareStatement("INSERT INTO `character_hennas` (char_obj_id, symbol_id, slot, class_index) VALUES (?,?,?,?)");
					statement.setInt(1, getObjectId());
					statement.setInt(2, henna.getSymbolId());
					statement.setInt(3, i + 1);
					statement.setInt(4, getActiveClassId());
					statement.execute();
				}
				catch(Exception e)
				{
					_log.warn("could not save char henna: " + e);
				}
				finally
				{
					DbUtils.closeQuietly(con, statement);
				}

				sendPacket(new HennaInfoPacket(this));
				sendUserInfo(true);

				return true;
			}

		return false;
	}

	/**
	 * Calculate Henna modifiers of this L2Player.
	 */
	private void recalcHennaStats()
	{
		_hennaINT = 0;
		_hennaSTR = 0;
		_hennaCON = 0;
		_hennaMEN = 0;
		_hennaWIT = 0;
		_hennaDEX = 0;
		_hennaLUC = 0;
		_hennaCHA = 0;

		boolean update = false;
		for(int skillId : _hennaSkills.keys())
		{
			if(removeSkill(skillId, false) != null)
				update = true;
		}

		_hennaSkills.clear();

		for(int i = 0; i < 3; i++)
		{
			Henna henna = _henna[i];
			if(henna == null)
				continue;

			if(!henna.isForThisClass(this))
				continue;

			_hennaINT += henna.getStatINT();
			_hennaSTR += henna.getStatSTR();
			_hennaMEN += henna.getStatMEN();
			_hennaCON += henna.getStatCON();
			_hennaWIT += henna.getStatWIT();
			_hennaDEX += henna.getStatDEX();
			_hennaLUC += henna.getStatLUC();
			_hennaCHA += henna.getStatCHA();

			for(TIntIntIterator iterator = henna.getSkills().iterator(); iterator.hasNext();)
			{
				iterator.advance();

				Skill skill = SkillTable.getInstance().getInfo(iterator.key(), iterator.value());
				if(skill == null)
					continue;

				addSkill(skill, false);

				_hennaSkills.put(skill.getId(), skill);
			}

			if(_hennaSkills.size() > 0)
				update = true;
		}

		if(_hennaINT > 15)
			_hennaINT = 15;
		if(_hennaSTR > 15)
			_hennaSTR = 15;
		if(_hennaMEN > 15)
			_hennaMEN = 15;
		if(_hennaCON > 15)
			_hennaCON = 15;
		if(_hennaWIT > 15)
			_hennaWIT = 15;
		if(_hennaDEX > 15)
			_hennaDEX = 15;
		if(_hennaLUC > 15)
			_hennaLUC = 15;
		if(_hennaCHA > 15)
			_hennaCHA = 15;

		if(update)
			sendPacket(new SkillListPacket(this));
	}

	/**
	 * @param slot id слота у перса
	 * @return the Henna of this L2Player corresponding to the selected slot.<BR><BR>
	 */
	public Henna getHenna(final int slot)
	{
		if(slot < 1 || slot > 3)
			return null;
		return _henna[slot - 1];
	}

	public int getHennaStatINT()
	{
		return _hennaINT;
	}

	public int getHennaStatSTR()
	{
		return _hennaSTR;
	}

	public int getHennaStatCON()
	{
		return _hennaCON;
	}

	public int getHennaStatMEN()
	{
		return _hennaMEN;
	}

	public int getHennaStatWIT()
	{
		return _hennaWIT;
	}

	public int getHennaStatDEX()
	{
		return _hennaDEX;
	}

	public int getHennaStatLUC()
	{
		return _hennaLUC;
	}

	public int getHennaStatCHA()
	{
		return _hennaCHA;
	}

	@Override
	public boolean consumeItem(int itemConsumeId, long itemCount)
	{
		if(getInventory().destroyItemByItemId(itemConsumeId, itemCount))
		{
			sendPacket(SystemMessagePacket.removeItems(itemConsumeId, itemCount));
			return true;
		}
		return false;
	}

	@Override
	public boolean consumeItemMp(int itemId, int mp)
	{
		for(ItemInstance item : getInventory().getPaperdollItems())
			if(item != null && item.getItemId() == itemId)
			{
				final int newMp = item.getLifeTime() - mp;
				if(newMp >= 0)
				{
					item.setLifeTime(newMp);
					sendPacket(new InventoryUpdatePacket().addModifiedItem(this, item));
					return true;
				}
				break;
			}
		return false;
	}

	/**
	 * @return True if the L2Player is a Mage.<BR><BR>
	 */
	@Override
	public boolean isMageClass()
	{
		return getClassId().isMage();
	}

	/**
	 * Проверяет, можно ли приземлиться в этой зоне.
	 *
	 * @return можно ли приземлится
	 */
	public boolean checkLandingState()
	{
		if(isInZone(ZoneType.no_landing))
			return false;

		SiegeEvent<?, ?> siege = getEvent(SiegeEvent.class);
		if(siege != null)
		{
			Residence unit = siege.getResidence();
			if(unit != null && getClan() != null && isClanLeader() && (getClan().getCastle() == unit.getId() || getClan().getHasFortress() == unit.getId()))
				return true;
			return false;
		}

		return true;
	}

	public void setMount(int controlItemObjId, int npcId, int level, int currentFeed)
	{
		Mount mount = Mount.create(this, controlItemObjId, npcId, level, currentFeed);
		if(mount != null)
			setMount(mount);
	}

	public void setMount(Mount mount)
	{
		if(_mount == mount)
			return;

		if(isCursedWeaponEquipped())
			return;

		Mount oldMount = _mount;
		_mount = null;
		if(oldMount != null) // Dismount
			oldMount.onUnride();

		if(mount != null)
		{
			_mount = mount;
			_mount.onRide();
		}
	}

	public boolean isMounted()
	{
		return _mount != null;
	}

	public Mount getMount()
	{
		return _mount;
	}

	public int getMountControlItemObjId()
	{
		return isMounted() ? _mount.getControlItemObjId() : 0;
	}

	public int getMountNpcId()
	{
		return isMounted() ? _mount.getNpcId() : 0;
	}

	public int getMountLevel()
	{
		return isMounted() ? _mount.getLevel() : 0;
	}

	public int getMountCurrentFeed()
	{
		return isMounted() ? _mount.getCurrentFeed() : 0;
	}

	public void unEquipWeapon()
	{
		ItemInstance wpn = getSecondaryWeaponInstance();
		if(wpn != null)
		{
			sendDisarmMessage(wpn);
			getInventory().unEquipItem(wpn);
		}

		wpn = getActiveWeaponInstance();
		if(wpn != null)
		{
			sendDisarmMessage(wpn);
			getInventory().unEquipItem(wpn);
		}

		abortAttack(true, true);
		abortCast(true, true);
	}

	public void sendDisarmMessage(ItemInstance wpn)
	{
		if(wpn.getEnchantLevel() > 0)
		{
			SystemMessage sm = new SystemMessage(SystemMessage.EQUIPMENT_OF__S1_S2_HAS_BEEN_REMOVED);
			sm.addNumber(wpn.getEnchantLevel());
			sm.addItemName(wpn.getItemId());
			sendPacket(sm);
		}
		else
		{
			SystemMessage sm = new SystemMessage(SystemMessage.S1__HAS_BEEN_DISARMED);
			sm.addItemName(wpn.getItemId());
			sendPacket(sm);
		}
	}

	/**
	 * Устанавливает тип используемого склада.
	 *
	 * @param type тип склада:<BR>
	 *             <ul>
	 *             <li>WarehouseType.PRIVATE
	 *             <li>WarehouseType.CLAN
	 *             <li>WarehouseType.CASTLE
	 *             </ul>
	 */
	public void setUsingWarehouseType(final WarehouseType type)
	{
		_usingWHType = type;
	}

	/**
	 * Р’РѕР·РІСЂР°С‰Р°РµС‚ С‚РёРї РёСЃРїРѕР»СЊР·СѓРµРјРѕРіРѕ СЃРєР»Р°РґР°.
	 *
	 * @return null РёР»Рё С‚РёРї СЃРєР»Р°РґР°:<br>
	 *         <ul>
	 *         <li>WarehouseType.PRIVATE
	 *         <li>WarehouseType.CLAN
	 *         <li>WarehouseType.CASTLE
	 *         </ul>
	 */
	public WarehouseType getUsingWarehouseType()
	{
		return _usingWHType;
	}

	public Collection<EffectCubic> getCubics()
	{
		return _cubics == null ? Collections.<EffectCubic> emptyList() : _cubics.values();
	}

	public void addCubic(EffectCubic cubic)
	{
		if(_cubics == null)
			_cubics = new ConcurrentHashMap<Integer, EffectCubic>(3);
		_cubics.put(cubic.getId(), cubic);

		sendPacket(new ExUserInfoCubic(this));
	}

	public void removeCubic(int id)
	{
		if(_cubics != null)
			_cubics.remove(id);

		sendPacket(new ExUserInfoCubic(this));
	}

	public EffectCubic getCubic(int id)
	{
		return _cubics == null ? null : _cubics.get(id);
	}

	@Override
	public String toString()
	{
		return getName() + "[" + getObjectId() + "]";
	}

	/**
	 * @return the modifier corresponding to the Enchant Effect of the Active Weapon (Min : 127).<BR><BR>
	 */
	@Override
	public int getEnchantEffect()
	{
		final ItemInstance wpn = getActiveWeaponInstance();

		if(wpn == null)
			return 0;

		return Math.min(127, wpn.getEnchantLevel());
	}

	/**
	 * Set the _lastFolkNpc of the L2Player corresponding to the last Folk witch one the player talked.<BR><BR>
	 */
	public void setLastNpc(final NpcInstance npc)
	{
		if(npc == null)
			_lastNpc = HardReferences.emptyRef();
		else
			_lastNpc = npc.getRef();
	}

	/**
	 * @return the _lastFolkNpc of the L2Player corresponding to the last Folk witch one the player talked.<BR><BR>
	 */
	public NpcInstance getLastNpc()
	{
		return _lastNpc.get();
	}

	public void setMultisell(MultiSellListContainer multisell)
	{
		_multisell = multisell;
	}

	public MultiSellListContainer getMultisell()
	{
		return _multisell;
	}

	@Override
	public boolean unChargeShots(boolean spirit)
	{
		ItemInstance weapon = getActiveWeaponInstance();
		if(weapon == null)
			return false;

		if(spirit)
			weapon.setChargedSpiritshot(ItemInstance.CHARGED_NONE);
		else
			weapon.setChargedSoulshot(ItemInstance.CHARGED_NONE);

		autoShot();
		return true;
	}

	public boolean unChargeFishShot()
	{
		ItemInstance weapon = getActiveWeaponInstance();
		if(weapon == null)
			return false;
		weapon.setChargedFishshot(false);
		autoShot();
		return true;
	}

	public void autoShot()
	{
		for(Integer shotId : _activeSoulShots)
		{
			ItemInstance item = getInventory().getItemByItemId(shotId);
			if(item == null)
			{
				removeAutoSoulShot(shotId);
				continue;
			}
			IItemHandler handler = item.getTemplate().getHandler();
			if(handler == null)
				continue;
			handler.useItem(this, item, false);
		}
	}

	public boolean getChargedFishShot()
	{
		ItemInstance weapon = getActiveWeaponInstance();
		return weapon != null && weapon.getChargedFishshot();
	}

	@Override
	public boolean getChargedSoulShot()
	{
		ItemInstance weapon = getActiveWeaponInstance();
		return weapon != null && weapon.getChargedSoulshot() == ItemInstance.CHARGED_SOULSHOT;
	}

	@Override
	public int getChargedSpiritShot()
	{
		ItemInstance weapon = getActiveWeaponInstance();
		if(weapon == null)
			return 0;
		return weapon.getChargedSpiritshot();
	}

	public void addAutoSoulShot(Integer itemId)
	{
		_activeSoulShots.add(itemId);
	}

	public void removeAutoSoulShot(Integer itemId)
	{
		_activeSoulShots.remove(itemId);
	}

	public Set<Integer> getAutoSoulShot()
	{
		return _activeSoulShots;
	}

	public void setInvisibleType(InvisibleType vis)
	{
		_invisibleType = vis;
	}

	@Override
	public InvisibleType getInvisibleType()
	{
		return _invisibleType;
	}

	public int getClanPrivileges()
	{
		if(_clan == null)
			return 0;
		if(isClanLeader())
			return Clan.CP_ALL;
		if(_powerGrade < 1 || _powerGrade > 9)
			return 0;
		RankPrivs privs = _clan.getRankPrivs(_powerGrade);
		if(privs != null)
			return privs.getPrivs();
		return 0;
	}

	public void teleToClosestTown()
	{
		teleToLocation(TeleportUtils.getRestartLocation(this, RestartType.TO_VILLAGE), ReflectionManager.DEFAULT);
	}

	public void teleToCastle()
	{
		teleToLocation(TeleportUtils.getRestartLocation(this, RestartType.TO_CASTLE), ReflectionManager.DEFAULT);
	}

	public void teleToFortress()
	{
		teleToLocation(TeleportUtils.getRestartLocation(this, RestartType.TO_FORTRESS), ReflectionManager.DEFAULT);
	}

	public void teleToClanhall()
	{
		teleToLocation(TeleportUtils.getRestartLocation(this, RestartType.TO_CLANHALL), ReflectionManager.DEFAULT);
	}

	@Override
	public void sendMessage(CustomMessage message)
	{
		sendMessage(message.toString());
	}

	public void teleToLocation(Location loc, boolean replace)
	{
		_isInReplaceTeleport = replace;

		teleToLocation(loc);

		_isInReplaceTeleport = false;
	}

	@Override
	public void teleToLocation(int x, int y, int z, int refId)
	{
		if(isDeleted())
			return;

		super.teleToLocation(x, y, z, refId);
	}

	@Override
	public boolean onTeleported()
	{
		if(!super.onTeleported())
			return false;

		if(isFakeDeath())
			breakFakeDeath();

		if(isInBoat())
			setLoc(getBoat().getLoc());

		// 15 секунд после телепорта на персонажа не агрятся мобы
		setNonAggroTime(System.currentTimeMillis() + Config.NONAGGRO_TIME_ONTELEPORT);
		setNonPvpTime(System.currentTimeMillis() + Config.NONPVP_TIME_ONTELEPORT);

		spawnMe();

		setLastClientPosition(getLoc());
		setLastServerPosition(getLoc());

		if(isPendingRevive())
			doRevive();

		sendActionFailed();

		getAI().notifyEvent(CtrlEvent.EVT_TELEPORTED);

		if(isLockedTarget() && getTarget() != null)
			sendPacket(new MyTargetSelectedPacket(this, getTarget()));

		sendUserInfo(true);

		if(!_isInReplaceTeleport)
		{
			Servitor[] servitors = getServitors();
			if(servitors.length > 0)
			{
				for(Servitor servitor : servitors)
					servitor.teleportToOwner();
			}
		}

		return true;
	}

	public boolean enterObserverMode(Location loc)
	{
		WorldRegion observerRegion = World.getRegion(loc);
		if(observerRegion == null)
			return false;
		if(!_observerMode.compareAndSet(OBSERVER_NONE, OBSERVER_STARTING))
			return false;

		setTarget(null);
		stopMove();
		sitDown(null);
		setFlying(true);

		// Очищаем все видимые обьекты
		World.removeObjectsFromPlayer(this);

		setObserverRegion(observerRegion);

		// Отображаем надпись над головой
		broadcastCharInfo();

		// Переходим в режим обсервинга
		sendPacket(new ObserverStart(loc));

		return true;
	}

	public void appearObserverMode()
	{
		if(!_observerMode.compareAndSet(OBSERVER_STARTING, OBSERVER_STARTED))
			return;

		WorldRegion currentRegion = getCurrentRegion();
		WorldRegion observerRegion = getObserverRegion();

		// Добавляем фэйк в точку наблюдения
		if(!observerRegion.equals(currentRegion))
			observerRegion.addObject(this);

		World.showObjectsToPlayer(this);

		OlympiadGame game = getOlympiadObserveGame();
		if(game != null)
		{
			game.addSpectator(this);
			game.broadcastInfo(null, this, true);
		}

		if(_fightBattleObserverArena != null)
			_fightBattleObserverArena.onAppearObserverMode(this);
	}

	public void leaveObserverMode()
	{
		if(_fightBattleObserverArena != null)
		{
			leaveOlympiadObserverMode(false);
			return;
		}

		if(!_observerMode.compareAndSet(OBSERVER_STARTED, OBSERVER_LEAVING))
			return;

		WorldRegion currentRegion = getCurrentRegion();
		WorldRegion observerRegion = getObserverRegion();

		// Убираем фэйк в точке наблюдения
		if(!observerRegion.equals(currentRegion))
			observerRegion.removeObject(this);

		// Очищаем все видимые обьекты
		World.removeObjectsFromPlayer(this);

		setObserverRegion(null);

		setTarget(null);
		stopMove();

		// Выходим из режима обсервинга
		sendPacket(new ObserverEnd(getLoc()));
	}

	public void returnFromObserverMode()
	{
		if(!_observerMode.compareAndSet(OBSERVER_LEAVING, OBSERVER_NONE))
			return;

		// Нужно при телепорте с более высокой точки на более низкую, иначе наносится вред от "падения"
		setLastClientPosition(null);
		setLastServerPosition(null);

		unblock();
		standUp();
		setFlying(false);

		broadcastCharInfo();

		World.showObjectsToPlayer(this);
	}

	public void enterOlympiadObserverMode(Location loc, OlympiadGame game, Reflection reflect)
	{
		WorldRegion observerRegion = World.getRegion(loc);
		if(observerRegion == null)
			return;

		OlympiadGame oldGame = getOlympiadObserveGame();
		if(!_observerMode.compareAndSet(oldGame != null ? OBSERVER_STARTED : OBSERVER_NONE, OBSERVER_STARTING))
			return;

		sendPacket(new TeleportToLocationPacket(this, loc));

		setTarget(null);
		stopMove();

		// Очищаем все видимые обьекты
		World.removeObjectsFromPlayer(this);

		WorldRegion oldObserverRegion = getObserverRegion();
		if(oldObserverRegion != null)
			oldObserverRegion.removeObject(this);

		setObserverRegion(observerRegion);

		if(oldGame != null)
		{
			oldGame.removeSpectator(this);
			sendPacket(ExOlympiadMatchEndPacket.STATIC);
		}
		else
		{
			block();

			// Отображаем надпись над головой
			broadcastCharInfo();

			// Меняем интерфейс
			sendPacket(new ExOlympiadModePacket(3));
		}

		setOlympiadObserveGame(game);

		// "Телепортируемся"
		setReflection(reflect);

		sendPacket(new ExTeleportToLocationActivate(this, loc));
	}

	public void leaveOlympiadObserverMode(boolean removeFromGame)
	{
		OlympiadGame game = getOlympiadObserveGame();
		if(_fightBattleObserverArena == null)
		{
			if(game == null)
				return;
		}
		else
			_fightBattleObserverArena.onLeaveObserverMode(this);

		if(!_observerMode.compareAndSet(OBSERVER_STARTED, OBSERVER_LEAVING))
			return;

		// "Телепортируемся"
		sendPacket(new TeleportToLocationPacket(this, getLoc()));

		if(_fightBattleObserverArena == null)
		{
			if(removeFromGame)
				game.removeSpectator(this);
			setOlympiadObserveGame(null);
		}

		_fightBattleObserverArena = null;

		WorldRegion currentRegion = getCurrentRegion();
		WorldRegion observerRegion = getObserverRegion();

		// Убираем фэйк в точке наблюдения
		if(observerRegion != null && currentRegion != null && !observerRegion.equals(currentRegion))
			observerRegion.removeObject(this);

		// Очищаем все видимые обьекты
		World.removeObjectsFromPlayer(this);

		setObserverRegion(null);

		setTarget(null);
		stopMove();

		// Меняем интерфейс
		sendPacket(new ExOlympiadModePacket(0));
		sendPacket(ExOlympiadMatchEndPacket.STATIC);

		setReflection(ReflectionManager.DEFAULT);

		sendPacket(new ExTeleportToLocationActivate(this, getLoc()));
	}

	public void setOlympiadSide(final int i)
	{
		_olympiadSide = i;
	}

	public int getOlympiadSide()
	{
		return _olympiadSide;
	}

	@Override
	public boolean isInObserverMode()
	{
		return _observerMode.get() > 0;
	}

	public int getObserverMode()
	{
		return _observerMode.get();
	}

	public WorldRegion getObserverRegion()
	{
		return _observerRegion;
	}

	public void setObserverRegion(WorldRegion region)
	{
		_observerRegion = region;
	}

	public int getTeleMode()
	{
		return _telemode;
	}

	public void setTeleMode(final int mode)
	{
		_telemode = mode;
	}

	public void setLoto(final int i, final int val)
	{
		_loto[i] = val;
	}

	public int getLoto(final int i)
	{
		return _loto[i];
	}

	public void setRace(final int i, final int val)
	{
		_race[i] = val;
	}

	public int getRace(final int i)
	{
		return _race[i];
	}

	public boolean getMessageRefusal()
	{
		return _messageRefusal;
	}

	public void setMessageRefusal(final boolean mode)
	{
		_messageRefusal = mode;
	}

	public void setTradeRefusal(final boolean mode)
	{
		_tradeRefusal = mode;
	}

	public boolean getTradeRefusal()
	{
		return _tradeRefusal;
	}

	public boolean isBlockAll()
	{
		return _blockAll;
	}

	public void setBlockAll(final boolean state)
	{
		_blockAll = state;
	}

	public void setHero(final boolean hero)
	{
		_hero = hero;
	}

	@Override
	public boolean isHero()
	{
		return _hero;
	}

	public void setIsInOlympiadMode(final boolean b)
	{
		_inOlympiadMode = b;
	}

	@Override
	public boolean isInOlympiadMode()
	{
		return _inOlympiadMode;
	}

	public boolean isOlympiadGameStart()
	{
		return _olympiadGame != null && _olympiadGame.getState() == 1;
	}

	public boolean isOlympiadCompStart()
	{
		return _olympiadGame != null && _olympiadGame.getState() == 2;
	}

	public final void setNoble(boolean noble)
	{
		setNoble(noble, true);
	}

	public final void setNoble(boolean noble, boolean send)
	{
		if(noble && send) //broadcast skill animation: Presentation - Attain Noblesse
		{
			broadcastPacket(new MagicSkillUse(this, this, 6673, 1, 1000, 0));
			sendAbilitiesInfo();
		}

		_noble = noble;
	}

	public boolean isNoble()
	{
		return _noble;
	}

	public int getSubLevel()
	{
		return isBaseClassActive() ? 0 : getLevel();
	}

	/* varka silenos and ketra orc quests related functions */
	public void updateKetraVarka()
	{
		if(ItemFunctions.getItemCount(this, 7215) > 0)
			_ketra = 5;
		else if(ItemFunctions.getItemCount(this, 7214) > 0)
			_ketra = 4;
		else if(ItemFunctions.getItemCount(this, 7213) > 0)
			_ketra = 3;
		else if(ItemFunctions.getItemCount(this, 7212) > 0)
			_ketra = 2;
		else if(ItemFunctions.getItemCount(this, 7211) > 0)
			_ketra = 1;
		else if(ItemFunctions.getItemCount(this, 7225) > 0)
			_varka = 5;
		else if(ItemFunctions.getItemCount(this, 7224) > 0)
			_varka = 4;
		else if(ItemFunctions.getItemCount(this, 7223) > 0)
			_varka = 3;
		else if(ItemFunctions.getItemCount(this, 7222) > 0)
			_varka = 2;
		else if(ItemFunctions.getItemCount(this, 7221) > 0)
			_varka = 1;
		else
		{
			_varka = 0;
			_ketra = 0;
		}
	}

	public int getVarka()
	{
		return _varka;
	}

	public int getKetra()
	{
		return _ketra;
	}

	public void updateRam()
	{
		if(ItemFunctions.getItemCount(this, 7247) > 0)
			_ram = 2;
		else if(ItemFunctions.getItemCount(this, 7246) > 0)
			_ram = 1;
		else
			_ram = 0;
	}

	public int getRam()
	{
		return _ram;
	}

	public void setPledgeType(final int typeId)
	{
		_pledgeType = typeId;
	}

	public int getPledgeType()
	{
		return _pledgeType;
	}

	public void setLvlJoinedAcademy(int lvl)
	{
		_lvlJoinedAcademy = lvl;
	}

	public int getLvlJoinedAcademy()
	{
		return _lvlJoinedAcademy;
	}

	public PledgeRank getPledgeRank()
	{
		return _pledgeRank;
	}

	public void updatePledgeRank()
	{
		if(isGM()) // Хай все ГМы будут императорами мира Lineage 2 ;)
		{
			_pledgeRank = PledgeRank.EMPEROR;
			return;
		}

		int CLAN_LEVEL = _clan == null ? -1 : _clan.getLevel();
		boolean IN_ACADEMY = _clan != null && Clan.isAcademy(_pledgeType);
		boolean IS_GUARD = _clan != null && Clan.isRoyalGuard(_pledgeType);
		boolean IS_KNIGHT = _clan != null && Clan.isOrderOfKnights(_pledgeType);

		boolean IS_GUARD_CAPTAIN = false, IS_KNIGHT_COMMANDER = false, IS_LEADER = false;

		SubUnit unit = getSubUnit();
		if(unit != null)
		{
			UnitMember unitMember = unit.getUnitMember(getObjectId());
			if(unitMember == null)
			{
				_log.warn("Player: unitMember null, clan: " + _clan.getClanId() + "; pledgeType: " + unit.getType());
				return;
			}
			IS_GUARD_CAPTAIN = Clan.isRoyalGuard(unitMember.getLeaderOf());
			IS_KNIGHT_COMMANDER = Clan.isOrderOfKnights(unitMember.getLeaderOf());
			IS_LEADER = unitMember.getLeaderOf() == Clan.SUBUNIT_MAIN_CLAN;
		}

		switch(CLAN_LEVEL)
		{
			case -1:
				_pledgeRank = PledgeRank.VAGABOND;
				break;
			case 0:
			case 1:
			case 2:
			case 3:
				_pledgeRank = PledgeRank.VASSAL;
				break;
			case 4:
				if(IS_LEADER)
					_pledgeRank = PledgeRank.KNIGHT;
				else
					_pledgeRank = PledgeRank.VASSAL;
				break;
			case 5:
				if(IS_LEADER)
					_pledgeRank = PledgeRank.WISEMAN;
				else if(IN_ACADEMY)
					_pledgeRank = PledgeRank.VASSAL;
				else
					_pledgeRank = PledgeRank.HEIR;
				break;
			case 6:
				if(IS_LEADER)
					_pledgeRank = PledgeRank.BARON;
				else if(IN_ACADEMY)
					_pledgeRank = PledgeRank.VASSAL;
				else if(IS_GUARD_CAPTAIN)
					_pledgeRank = PledgeRank.WISEMAN;
				else if(IS_GUARD)
					_pledgeRank = PledgeRank.HEIR;
				else
					_pledgeRank = PledgeRank.KNIGHT;
				break;
			case 7:
				if(IS_LEADER)
					_pledgeRank = PledgeRank.COUNT;
				else if(IN_ACADEMY)
					_pledgeRank = PledgeRank.VASSAL;
				else if(IS_GUARD_CAPTAIN)
					_pledgeRank = PledgeRank.VISCOUNT;
				else if(IS_GUARD)
					_pledgeRank = PledgeRank.KNIGHT;
				else if(IS_KNIGHT_COMMANDER)
					_pledgeRank = PledgeRank.BARON;
				else if(IS_KNIGHT)
					_pledgeRank = PledgeRank.HEIR;
				else
					_pledgeRank = PledgeRank.WISEMAN;
				break;
			case 8:
				if(IS_LEADER)
					_pledgeRank = PledgeRank.MARQUIS;
				else if(IN_ACADEMY)
					_pledgeRank = PledgeRank.VASSAL;
				else if(IS_GUARD_CAPTAIN)
					_pledgeRank = PledgeRank.COUNT;
				else if(IS_GUARD)
					_pledgeRank = PledgeRank.WISEMAN;
				else if(IS_KNIGHT_COMMANDER)
					_pledgeRank = PledgeRank.VISCOUNT;
				else if(IS_KNIGHT)
					_pledgeRank = PledgeRank.KNIGHT;
				else
					_pledgeRank = PledgeRank.BARON;
				break;
			case 9:
				if(IS_LEADER)
					_pledgeRank = PledgeRank.DUKE;
				else if(IN_ACADEMY)
					_pledgeRank = PledgeRank.VASSAL;
				else if(IS_GUARD_CAPTAIN)
					_pledgeRank = PledgeRank.MARQUIS;
				else if(IS_GUARD)
					_pledgeRank = PledgeRank.BARON;
				else if(IS_KNIGHT_COMMANDER)
					_pledgeRank = PledgeRank.COUNT;
				else if(IS_KNIGHT)
					_pledgeRank = PledgeRank.WISEMAN;
				else
					_pledgeRank = PledgeRank.VISCOUNT;
				break;
			case 10:
				if(IS_LEADER)
					_pledgeRank = PledgeRank.GRAND_DUKE;
				else if(IN_ACADEMY)
					_pledgeRank = PledgeRank.VASSAL;
				else if(IS_GUARD)
					_pledgeRank = PledgeRank.VISCOUNT;
				else if(IS_KNIGHT)
					_pledgeRank = PledgeRank.BARON;
				else if(IS_GUARD_CAPTAIN)
					_pledgeRank = PledgeRank.DUKE;
				else if(IS_KNIGHT_COMMANDER)
					_pledgeRank = PledgeRank.MARQUIS;
				else
					_pledgeRank = PledgeRank.COUNT;
				break;
			case 11:
				if(IS_LEADER)
					_pledgeRank = PledgeRank.DISTINGUISHED_KING;
				else if(IN_ACADEMY)
					_pledgeRank = PledgeRank.VASSAL;
				else if(IS_GUARD)
					_pledgeRank = PledgeRank.COUNT;
				else if(IS_KNIGHT)
					_pledgeRank = PledgeRank.VISCOUNT;
				else if(IS_GUARD_CAPTAIN)
					_pledgeRank = PledgeRank.GRAND_DUKE;
				else if(IS_KNIGHT_COMMANDER)
					_pledgeRank = PledgeRank.DUKE;
				else
					_pledgeRank = PledgeRank.MARQUIS;
				break;
		}

		if(_hero && _pledgeRank.ordinal() < PledgeRank.MARQUIS.ordinal())
			_pledgeRank = PledgeRank.MARQUIS;
		else if(_noble && _pledgeRank.ordinal() < PledgeRank.BARON.ordinal())
			_pledgeRank = PledgeRank.BARON;
	}

	public void setPowerGrade(final int grade)
	{
		_powerGrade = grade;
	}

	public int getPowerGrade()
	{
		return _powerGrade;
	}

	public void setApprentice(final int apprentice)
	{
		_apprentice = apprentice;
	}

	public int getApprentice()
	{
		return _apprentice;
	}

	public int getSponsor()
	{
		return _clan == null ? 0 : _clan.getAnyMember(getObjectId()).getSponsor();
	}
	
	@Override
	public int getNameColor()
	{
		if(isInObserverMode())
			return Color.black.getRGB();

		return _nameColor;
	}

	public void setNameColor(final int nameColor)
	{
		if(nameColor != Config.NORMAL_NAME_COLOUR && nameColor != Config.CLANLEADER_NAME_COLOUR && nameColor != Config.GM_NAME_COLOUR && nameColor != Config.SERVICES_OFFLINE_TRADE_NAME_COLOR)
			setVar("namecolor", Integer.toHexString(nameColor));
		else if(nameColor == Config.NORMAL_NAME_COLOUR)
			unsetVar("namecolor");
		_nameColor = nameColor;
	}

	public void setNameColor(final int red, final int green, final int blue)
	{
		_nameColor = (red & 0xFF) + ((green & 0xFF) << 8) + ((blue & 0xFF) << 16);
		if(_nameColor != Config.NORMAL_NAME_COLOUR && _nameColor != Config.CLANLEADER_NAME_COLOUR && _nameColor != Config.GM_NAME_COLOUR && _nameColor != Config.SERVICES_OFFLINE_TRADE_NAME_COLOR)
			setVar("namecolor", Integer.toHexString(_nameColor));
		else
			unsetVar("namecolor");
	}

	private void restoreVariables()
	{
		List<CharacterVariable> variables = CharacterVariablesDAO.getInstance().restore(getObjectId());
		for(CharacterVariable var : variables)
			_variables.put(var.getName(), var);
	}

	public Collection<CharacterVariable> getVariables()
	{
		return _variables.values();
	}

	public boolean setVar(String name, String value)
	{
		return setVar(name, value, -1);
	}

	public boolean setVar(String name, String value, long expirationTime)
	{
		CharacterVariable var = new CharacterVariable(name, value, expirationTime);
		if(CharacterVariablesDAO.getInstance().insert(getObjectId(), var))
		{
			_variables.put(name, var);
			return true;
		}
		return false;
	}

	public boolean setVar(String name, int value)
	{
		return setVar(name, value, -1);
	}

	public boolean setVar(String name, int value, long expirationTime)
	{
		return setVar(name, String.valueOf(value), expirationTime);
	}

	public boolean setVar(String name, long value)
	{
		return setVar(name, value, -1);
	}

	public boolean setVar(String name, long value, long expirationTime)
	{
		return setVar(name, String.valueOf(value), expirationTime);
	}

	public boolean setVar(String name, double value)
	{
		return setVar(name, value, -1);
	}

	public boolean setVar(String name, double value, long expirationTime)
	{
		return setVar(name, String.valueOf(value), expirationTime);
	}

	public boolean setVar(String name, boolean value)
	{
		return setVar(name, value, -1);
	}

	public boolean setVar(String name, boolean value, long expirationTime)
	{
		return setVar(name, String.valueOf(value), expirationTime);
	}

	public boolean unsetVar(String name)
	{
		if(name == null || name.isEmpty())
			return false;

		if(CharacterVariablesDAO.getInstance().delete(getObjectId(), name))
			return _variables.remove(name) != null;

		return false;
	}

	public String getVar(String name)
	{
		return getVar(name, null);
	}

	public String getVar(String name, String defaultValue)
	{
		CharacterVariable var = _variables.get(name);
		if(var != null && !var.isExpired())
			return var.getValue();

		return defaultValue;
	}

	public int getVarInt(String name)
	{
		return getVarInt(name, 0);
	}

	public int getVarInt(String name, int defaultValue)
	{
		String var = getVar(name);
		if(var != null)
			return Integer.parseInt(var);

		return defaultValue;
	}

	public long getVarLong(String name)
	{
		return getVarLong(name, 0L);
	}

	public long getVarLong(String name, long defaultValue)
	{
		String var = getVar(name);
		if(var != null)
			return Long.parseLong(var);

		return defaultValue;
	}

	public double getVarDouble(String name)
	{
		return getVarDouble(name, 0.);
	}

	public double getVarDouble(String name, double defaultValue)
	{
		String var = getVar(name);
		if(var != null)
			return Double.parseDouble(var);

		return defaultValue;
	}

	public boolean getVarBoolean(String name)
	{
		return getVarBoolean(name, false);
	}

	public boolean getVarBoolean(String name, boolean defaultValue)
	{
		String var = getVar(name);
		if(var != null)
			return !(var.equals("0") || var.equalsIgnoreCase("false"));

		return defaultValue;
	}

	public void setLanguage(String val)
	{
		_language = Language.getLanguage(val);
		setVar(Language.LANG_VAR, _language.getShortName(), -1);
	}

	public Language getLanguage()
	{
		if(Config.USE_CLIENT_LANG && getNetConnection() != null)
			return getNetConnection().getLanguage();
		return _language;
	}

	public int getLocationId()
	{
		if(getNetConnection() != null)
			return getNetConnection().getLanguage().getId();
		return -1;
	}

	public boolean isLangRus()
	{
		return getLanguage() == Language.RUSSIAN;
	}

	public int isAtWarWith(final Integer id)
	{
		return _clan == null || !_clan.isAtWarWith(id) ? 0 : 1;
	}

	public int isAtWar()
	{
		return _clan == null || _clan.isAtWarOrUnderAttack() <= 0 ? 0 : 1;
	}

	public void stopWaterTask()
	{
		if(_taskWater != null)
		{
			_taskWater.cancel(false);
			_taskWater = null;
			sendPacket(new SetupGaugePacket(this, SetupGaugePacket.Colors.BLUE_MINI, 0));
			sendChanges();
		}
	}

	public void startWaterTask()
	{
		if(isDead())
			stopWaterTask();
		else if(Config.ALLOW_WATER && _taskWater == null)
		{
			int timeinwater = (int) (calcStat(Stats.BREATH, getBaseStats().getBreathBonus(), null, null) * 1000L);
			sendPacket(new SetupGaugePacket(this, SetupGaugePacket.Colors.BLUE_MINI, timeinwater));
			if(isTransformed() && !getTransform().isCanSwim())
				setTransform(null);

			_taskWater = ThreadPoolManager.getInstance().scheduleAtFixedRate(new WaterTask(this), timeinwater, 1000L);
			sendChanges();
		}
	}

	public void doRevive(double percent)
	{
		restoreExp(percent);
		doRevive();
	}

	@Override
	public void doRevive()
	{
		super.doRevive();
		setAgathionRes(false);
		unsetVar("lostexp");
		updateEffectIcons();
		autoShot();
		if(isMounted())
			_mount.onRevive();
	}

	public void reviveRequest(Player reviver, double percent, boolean pet)
	{
		ReviveAnswerListener reviveAsk = _askDialog != null && _askDialog.getValue() instanceof ReviveAnswerListener ? (ReviveAnswerListener) _askDialog.getValue() : null;
		if(reviveAsk != null)
		{
			if(reviveAsk.isForPet() == pet && reviveAsk.getPower() >= percent)
			{
				reviver.sendPacket(SystemMsg.RESURRECTION_HAS_ALREADY_BEEN_PROPOSED);
				return;
			}
			if(pet && !reviveAsk.isForPet())
			{
				reviver.sendPacket(SystemMsg.A_PET_CANNOT_BE_RESURRECTED_WHILE_ITS_OWNER_IS_IN_THE_PROCESS_OF_RESURRECTING);
				return;
			}
			if(pet && isDead())
			{
				reviver.sendPacket(SystemMsg.WHILE_A_PET_IS_BEING_RESURRECTED_IT_CANNOT_HELP_IN_RESURRECTING_ITS_MASTER);
				return;
			}
		}

		if(pet && getPet() != null && getPet().isDead() || !pet && isDead())
		{

			ConfirmDlgPacket pkt = new ConfirmDlgPacket(SystemMsg.C1_IS_MAKING_AN_ATTEMPT_TO_RESURRECT_YOU_IF_YOU_CHOOSE_THIS_PATH_S2_EXPERIENCE_WILL_BE_RETURNED_FOR_YOU, 0);
			pkt.addName(reviver).addInteger(Math.round(percent));

			ask(pkt, new ReviveAnswerListener(this, percent, pet));
		}
	}

	public void requestCheckBot()
	{
		BotCheckQuestion question = BotCheckManager.generateRandomQuestion();
		int qId = question.getId();
		String qDescr = question.getDescr(isLangRus());
		
		ConfirmDlgPacket pkt = new ConfirmDlgPacket(SystemMsg.S1, 60000).addString(qDescr);
		//ConfirmDlgPacket pkt = new ConfirmDlgPacket(qDescr, 60000);
		ask(pkt, new BotCheckAnswerListner(this, qId));
	}
	
	public void increaseBotRating()
	{
		int bot_points = getBotRating();
		if(bot_points + 1 >= Config.MAX_BOT_POINTS)
			return;
		setBotRating(bot_points + 1);	
	}
	
	public void decreaseBotRating()
	{
		int bot_points = getBotRating();
		if(bot_points - 1 <= Config.MINIMAL_BOT_RATING_TO_BAN)
		{
			setVar("jailedFrom", getX() + ";" + getY() + ";" + getZ() + ";" + getReflectionId(), -1);
			setVar("jailed", Config.AUTO_BOT_BAN_JAIL_TIME, -1);
			startUnjailTask(this, Config.AUTO_BOT_BAN_JAIL_TIME);
			teleToLocation(Location.findPointToStay(this, AdminFunctions.JAIL_SPAWN, 50, 200), ReflectionManager.JAIL);
			sitDown(null);
			block();				
			setNoChannel(-1);
			sendMessage("You moved to jail, time to escape - " + Config.AUTO_BOT_BAN_JAIL_TIME + " minutes, reason - botting .");
			if(Config.ANNOUNCE_AUTO_BOT_BAN)
				Announcements.getInstance().announceToAll("Player " + getName() + " jailed for botting!");
		}
		else
		{
			setBotRating(bot_points - 1);
			if(Config.ON_WRONG_QUESTION_KICK)
				kick();
		}	
	}
	
	public void setBotRating(int rating)
	{
		_botRating = rating;
	}
	
	public int getBotRating()
	{
		return _botRating;
	}
	
	public void summonCharacterRequest(final Creature summoner, final Location loc, final int summonConsumeCrystal)
	{
		ConfirmDlgPacket cd = new ConfirmDlgPacket(SystemMsg.C1_WISHES_TO_SUMMON_YOU_FROM_S2, 60000);
		cd.addName(summoner).addZoneName(loc);

		ask(cd, new SummonAnswerListener(this, loc, summonConsumeCrystal));
	}

	public void scriptRequest(String text, String scriptName, Object... args)
	{
		ask(new ConfirmDlgPacket(SystemMsg.S1, 30000).addString(text), new ScriptAnswerListener(this, scriptName, args));
	}

	public void updateNoChannel(final long time)
	{
		setNoChannel(time);

		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();

			final String stmt = "UPDATE characters SET nochannel = ? WHERE obj_Id=?";
			statement = con.prepareStatement(stmt);
			statement.setLong(1, _NoChannel > 0 ? _NoChannel / 1000 : _NoChannel);
			statement.setInt(2, getObjectId());
			statement.executeUpdate();
		}
		catch(final Exception e)
		{
			_log.warn("Could not activate nochannel:" + e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}

		sendPacket(new EtcStatusUpdatePacket(this));
	}

	private void checkRecom()
	{
		Calendar temp = Calendar.getInstance();
		temp.set(Calendar.HOUR_OF_DAY, 6);
		temp.set(Calendar.MINUTE, 30);
		temp.set(Calendar.SECOND, 0);
		temp.set(Calendar.MILLISECOND, 0);
		long count = Math.round((System.currentTimeMillis() / 1000 - _lastAccess) / 86400);
		if(count == 0 && _lastAccess < temp.getTimeInMillis() / 1000 && System.currentTimeMillis() > temp.getTimeInMillis())
			count++;

		for(int i = 1; i < count; i++)
			setRecomHave(getRecomHave() - 20);

		if(count > 0)
			restartRecom();
	}

	public void restartRecom()
	{
		setRecomBonusTime(3600);
		setRecomLeftToday(0);
		setRecomLeft(20);
		setRecomHave(getRecomHave() - 20);
		stopRecomBonusTask(false);
		startRecomBonusTask();
		sendUserInfo(true);
		sendVoteSystemInfo();
	}

	public SubClassList getSubClassList()
	{
		return _subClassList;
	}

	public SubClass getBaseSubClass()
	{
		return _subClassList.getBaseSubClass();
	}

	public int getBaseClassId()
	{
		if(getBaseSubClass() != null)
			return getBaseSubClass().getClassId();

		return -1;
	}

	public int getBaseDefaultClassId()
	{
		if(getBaseSubClass() != null)
			return getBaseSubClass().getDefaultClassId();

		return -1;
	}

	public SubClass getActiveSubClass()
	{
		return _subClassList.getActiveSubClass();
	}

	public int getActiveClassId()
	{
		if(getActiveSubClass() != null)
			return getActiveSubClass().getClassId();

		return -1;
	}

	public int getActiveDefaultClassId()
	{
		if(getActiveSubClass() != null)
			return getActiveSubClass().getDefaultClassId();

		return -1;
	}

	public SubClass getDualClass()
	{
		return _subClassList.getDualClass();
	}

	public int getDualClassId()
	{
		if(getDualClass() != null)
			return getDualClass().getClassId();

		return -1;
	}

	public int getDualClassLevel()
	{
		if(getDualClass() != null)
			return getDualClass().getLevel();

		return 0;
	}

	public boolean isBaseClassActive()
	{
		return getActiveSubClass().isBase();
	}

	public boolean isDualClassActive()
	{
		return getActiveSubClass().isDual();
	}

	public ClassId getClassId()
	{
		return ClassId.VALUES[getActiveClassId()];
	}

	public int getMaxLevel()
	{
		if(getActiveSubClass() != null)
			return getActiveSubClass().getMaxLevel();

		return Experience.getMaxLevel();
	}

	/**
	 * Changing index of class in DB, used for changing class when finished professional quests
	 *
	 * @param oldclass
	 * @param newclass
	 */
	private synchronized void changeClassInDb(final int oldclass, final int newclass, final int defaultClass)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE character_subclasses SET class_id=?, default_class_id=? WHERE char_obj_id=? AND class_id=?");
			statement.setInt(1, newclass);
			statement.setInt(2, defaultClass);
			statement.setInt(3, getObjectId());
			statement.setInt(4, oldclass);
			statement.executeUpdate();
			DbUtils.close(statement);

			statement = con.prepareStatement("DELETE FROM character_hennas WHERE char_obj_id=? AND class_index=?");
			statement.setInt(1, getObjectId());
			statement.setInt(2, newclass);
			statement.executeUpdate();
			DbUtils.close(statement);

			statement = con.prepareStatement("UPDATE character_hennas SET class_index=? WHERE char_obj_id=? AND class_index=?");
			statement.setInt(1, newclass);
			statement.setInt(2, getObjectId());
			statement.setInt(3, oldclass);
			statement.executeUpdate();
			DbUtils.close(statement);

			statement = con.prepareStatement("DELETE FROM character_shortcuts WHERE object_id=? AND class_index=?");
			statement.setInt(1, getObjectId());
			statement.setInt(2, newclass);
			statement.executeUpdate();
			DbUtils.close(statement);

			statement = con.prepareStatement("UPDATE character_shortcuts SET class_index=? WHERE object_id=? AND class_index=?");
			statement.setInt(1, newclass);
			statement.setInt(2, getObjectId());
			statement.setInt(3, oldclass);
			statement.executeUpdate();
			DbUtils.close(statement);

			statement = con.prepareStatement("DELETE FROM character_skills WHERE char_obj_id=? AND class_index=?");
			statement.setInt(1, getObjectId());
			statement.setInt(2, newclass);
			statement.executeUpdate();
			DbUtils.close(statement);

			statement = con.prepareStatement("UPDATE character_skills SET class_index=? WHERE char_obj_id=? AND class_index=?");
			statement.setInt(1, newclass);
			statement.setInt(2, getObjectId());
			statement.setInt(3, oldclass);
			statement.executeUpdate();
			DbUtils.close(statement);

			statement = con.prepareStatement("DELETE FROM character_effects_save WHERE object_id=? AND id=?");
			statement.setInt(1, getObjectId());
			statement.setInt(2, newclass);
			statement.executeUpdate();
			DbUtils.close(statement);

			statement = con.prepareStatement("UPDATE character_effects_save SET id=? WHERE object_id=? AND id=?");
			statement.setInt(1, newclass);
			statement.setInt(2, getObjectId());
			statement.setInt(3, oldclass);
			statement.executeUpdate();
			DbUtils.close(statement);

			statement = con.prepareStatement("DELETE FROM character_skills_save WHERE char_obj_id=? AND class_index=?");
			statement.setInt(1, getObjectId());
			statement.setInt(2, newclass);
			statement.executeUpdate();
			DbUtils.close(statement);

			statement = con.prepareStatement("UPDATE character_skills_save SET class_index=? WHERE char_obj_id=? AND class_index=?");
			statement.setInt(1, newclass);
			statement.setInt(2, getObjectId());
			statement.setInt(3, oldclass);
			statement.executeUpdate();
			DbUtils.close(statement);
		}
		catch(final SQLException e)
		{
			_log.error("", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	/**
	 * Сохраняет информацию о классах в БД
	 */
	public void storeCharSubClasses()
	{
		SubClass main = getActiveSubClass();
		if(main != null)
		{
			main.setCp(getCurrentCp());
			main.setHp(getCurrentHp());
			main.setMp(getCurrentMp());
		}
		else
			_log.warn("Could not store char sub data, main class " + getActiveClassId() + " not found for " + this);

		CharacterSubclassDAO.getInstance().store(this);
	}

	/**
	 * Добавить класс, используется только для сабклассов
	 *
	 * @param storeOld
	 * @param certification
	 */
	public boolean addSubClass(final int classId, boolean storeOld, int certification, int dualCertification, long exp, int sp)
	{
		return addSubClass(classId, storeOld, certification, dualCertification, SubClassType.SUBCLASS, exp, sp);
	}

	public boolean addSubClass(final int classId, boolean storeOld, int certification, int dualCertification, SubClassType type, long exp, int sp)
	{
		final ClassId newId = ClassId.VALUES[classId];
		if(newId.isDummy() || newId.isOfLevel(ClassLevel.NONE) || newId.isOfLevel(ClassLevel.FIRST))
			return false;

		final SubClass newClass = new SubClass(this);
		newClass.setType(type);
		newClass.setClassId(classId);
		if(exp > 0L)
			newClass.setExp(exp, true);
		if(sp > 0)
			newClass.setSp(sp);
		newClass.setCertification(certification);
		newClass.setDualCertification(dualCertification);
		if(!getSubClassList().add(newClass))
			return false;

		final int level = newClass.getLevel();
		final double hp = newId.getBaseHp(level);
		final double mp = newId.getBaseMp(level);
		final double cp = newId.getBaseCp(level);
		if(!CharacterSubclassDAO.getInstance().insert(getObjectId(), classId, classId, newClass.getExp(), newClass.getSp(), hp, mp, cp, hp, mp, cp, level, false, type, certification, dualCertification, MAX_VITALITY_POINTS, 0, 0))
			return false;

		setActiveSubClass(classId, storeOld, false);

		rewardSkills(true, false, true, false);

		if(isGM())
			giveGMSkills();

		sendPacket(new SkillListPacket(this));
		setCurrentHpMp(getMaxHp(), getMaxMp(), true);
		setCurrentCp(getMaxCp());
		return true;
	}

	/**
	 * Удаляет всю информацию о классе и добавляет новую, только для сабклассов
	 */
	public boolean modifySubClass(final int oldClassId, final int newClassId, final boolean safeExpSp)
	{
		final SubClass originalClass = getSubClassList().getByClassId(oldClassId);
		if(originalClass == null || originalClass.isBase())
			return false;

		final int certification = originalClass.getCertification();
		final int dualCertification = originalClass.getDualCertification();
		final SubClassType type = originalClass.getType();
		long exp = 0L;
		int sp = 0;
		if(safeExpSp)
		{
			exp = originalClass.getExp();
			sp = originalClass.getSp();
		}

		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			// Remove all basic info stored about this sub-class.
			statement = con.prepareStatement("DELETE FROM character_subclasses WHERE char_obj_id=? AND class_id=? AND type != " + SubClassType.BASE_CLASS.ordinal());
			statement.setInt(1, getObjectId());
			statement.setInt(2, oldClassId);
			statement.execute();
			DbUtils.close(statement);

			// Remove all skill info stored for this sub-class.
			statement = con.prepareStatement("DELETE FROM character_skills WHERE char_obj_id=? AND class_index=? ");
			statement.setInt(1, getObjectId());
			statement.setInt(2, oldClassId);
			statement.execute();
			DbUtils.close(statement);

			// Remove all saved skills info stored for this sub-class.
			statement = con.prepareStatement("DELETE FROM character_skills_save WHERE char_obj_id=? AND class_index=? ");
			statement.setInt(1, getObjectId());
			statement.setInt(2, oldClassId);
			statement.execute();
			DbUtils.close(statement);

			// Remove all saved effects stored for this sub-class.
			statement = con.prepareStatement("DELETE FROM character_effects_save WHERE object_id=? AND id=? ");
			statement.setInt(1, getObjectId());
			statement.setInt(2, oldClassId);
			statement.execute();
			DbUtils.close(statement);

			// Remove all henna info stored for this sub-class.
			statement = con.prepareStatement("DELETE FROM character_hennas WHERE char_obj_id=? AND class_index=? ");
			statement.setInt(1, getObjectId());
			statement.setInt(2, oldClassId);
			statement.execute();
			DbUtils.close(statement);

			// Remove all shortcuts info stored for this sub-class.
			statement = con.prepareStatement("DELETE FROM character_shortcuts WHERE object_id=? AND class_index=? ");
			statement.setInt(1, getObjectId());
			statement.setInt(2, oldClassId);
			statement.execute();
			DbUtils.close(statement);
		}
		catch(final Exception e)
		{
			_log.warn("Could not delete char sub-class: " + e);
			_log.error("", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
		getSubClassList().removeByClassId(oldClassId);

		return newClassId <= 0 || addSubClass(newClassId, false, certification, dualCertification, type, exp, sp);
	}

	public void setActiveSubClass(final int subId, final boolean store, final boolean onRestore)
	{
		if(!onRestore)
		{
			SubClass oldActiveSub = getActiveSubClass();
			if(oldActiveSub != null)
			{
				storeDisableSkills();

				if(QuestManager.getQuest(422) != null)
				{
					String qn = QuestManager.getQuest(422).getName();
					if(qn != null)
					{
						QuestState qs = getQuestState(qn);
						if(qs != null)
							qs.exitCurrentQuest(true);
					}
				}

				if(store)
				{
					oldActiveSub.setCp(getCurrentCp());
					oldActiveSub.setHp(getCurrentHp());
					oldActiveSub.setMp(getCurrentMp());
				}
			}
		}

		SubClass newActiveSub = _subClassList.changeActiveSubClass(subId);

		setClassId(subId, false);

		removeAllSkills();

		getEffectList().stopAllEffects();

		Servitor[] servitors = getServitors();
		if(servitors.length > 0)
		{
			for(Servitor servitor : servitors)
			{
				if(servitor != null && (servitor.isSummon() || Config.ALT_IMPROVED_PETS_LIMITED_USE && (servitor.getNpcId() == PetDataHolder.IMPROVED_BABY_KOOKABURRA_ID && !isMageClass() || servitor.getNpcId() == PetDataHolder.IMPROVED_BABY_BUFFALO_ID && isMageClass())))
					servitor.unSummon(false);
			}
		}

		//setAgathion(0); TODO: Проверить на оффе.

		restoreSkills();
		rewardSkills(false);

		if(isGM())
			giveGMSkills();

		checkSkills();

		refreshExpertisePenalty();

		sendPacket(new SkillListPacket(this));

		getInventory().refreshEquip();
		getInventory().validateItems();

		for(int i = 0; i < 3; i++)
			_henna[i] = null;

		restoreHenna();
		sendPacket(new HennaInfoPacket(this));

		EffectsDAO.getInstance().restoreEffects(this);
		restoreDisableSkills();

		setCurrentHpMp(newActiveSub.getHp(), newActiveSub.getMp());
		setCurrentCp(newActiveSub.getCp());

		_shortCuts.restore();
		sendPacket(new ShortCutInitPacket(this));
		for(int shotId : getAutoSoulShot())
			sendPacket(new ExAutoSoulShot(shotId, true));
		sendPacket(new SkillCoolTimePacket(this));

		broadcastPacket(new SocialActionPacket(getObjectId(), SocialActionPacket.LEVEL_UP));

		setIncreasedForce(0);

		startHourlyTask();

		sendUserInfo(true);
		sendPacket(new ExSubjobInfo(this, false));
		sendPacket(new AcquireSkillListPacket(this));

		broadcastCharInfo();
		updateEffectIcons();
		updateStats();
	}

	/**
	 * Через delay миллисекунд выбросит игрока из игры
	 */
	public void startKickTask(long delayMillis)
	{
		stopKickTask();
		_kickTask = ThreadPoolManager.getInstance().schedule(new KickTask(this), delayMillis);
	}

	public void stopKickTask()
	{
		if(_kickTask != null)
		{
			_kickTask.cancel(false);
			_kickTask = null;
		}
	}

	public void startBonusTask()
	{
		if(Config.SERVICES_RATE_TYPE != Bonus.NO_BONUS)
		{
			if(this == null || getNetConnection() == null)
				return;
			int bonusExpire = getNetConnection().getBonusExpire();
			int bonus = getNetConnection().getBonusType();
			if(bonusExpire > System.currentTimeMillis() / 1000L)
			{
				PremiumInfo PremiumInf = PremiumAccountRatesHolder.getPremiumByGroupId(bonus);
				if(PremiumInf == null || bonus == 1)
				{
					getBonus().setRateXp(1.);
					getBonus().setRateSp(1.);
					getBonus().setDropAdena(1.);
					getBonus().setDropItems(1.);
					getBonus().setDropSpoil(1.);
					getBonus().setQuestDropRate(1.);
					getBonus().setQuestRewardRate(1.);
					getBonus().setEnchantAdd(0.);
				}
				else
				{
					getBonus().setRateXp(PremiumInf.getExp());
					getBonus().setRateSp(PremiumInf.getSp());
					getBonus().setDropAdena(PremiumInf.getAdena());
					getBonus().setDropItems(PremiumInf.getDrop());
					getBonus().setDropSpoil(PremiumInf.getSpoil());
					getBonus().setQuestDropRate(PremiumInf.getQDrop());
					getBonus().setQuestRewardRate(PremiumInf.getQReward());
					getBonus().setEnchantAdd(PremiumInf.getEnchantAdd());
					
				}

				getBonus().setBonusExpire(bonusExpire);

				if(_bonusExpiration == null)
					_bonusExpiration = LazyPrecisionTaskManager.getInstance().startBonusExpirationTask(this);
			}
			else if(bonus > 0 && Config.SERVICES_RATE_TYPE == Bonus.BONUS_GLOBAL_ON_GAMESERVER)
				AccountBonusDAO.getInstance().delete(getAccountName());
		}
	}

	public void stopBonusTask()
	{
		if(_bonusExpiration != null)
		{
			_bonusExpiration.cancel(false);
			_bonusExpiration = null;
		}
	}

	@Override
	public int getInventoryLimit()
	{
		return (int) calcStat(Stats.INVENTORY_LIMIT, 0, null, null);
	}

	public int getWarehouseLimit()
	{
		return (int) calcStat(Stats.STORAGE_LIMIT, 0, null, null);
	}

	public int getTradeLimit()
	{
		return (int) calcStat(Stats.TRADE_LIMIT, 0, null, null);
	}

	public int getDwarvenRecipeLimit()
	{
		return (int) calcStat(Stats.DWARVEN_RECIPE_LIMIT, 50, null, null) + Config.ALT_ADD_RECIPES;
	}

	public int getCommonRecipeLimit()
	{
		return (int) calcStat(Stats.COMMON_RECIPE_LIMIT, 50, null, null) + Config.ALT_ADD_RECIPES;
	}

	/**
	 * Возвращает тип атакующего элемента
	 */
	public Element getAttackElement()
	{
		return Formulas.getAttackElement(this, null);
	}

	/**
	 * Возвращает силу атаки элемента
	 *
	 * @return значение атаки
	 */
	public int getAttack(Element element)
	{
		if(element == Element.NONE)
			return 0;
		return (int) calcStat(element.getAttack(), 0., null, null);
	}

	/**
	 * Возвращает защиту от элемента
	 *
	 * @return значение защиты
	 */
	public int getDefence(Element element)
	{
		if(element == Element.NONE)
			return 0;
		return (int) calcStat(element.getDefence(), 0., null, null);
	}

	public boolean getAndSetLastItemAuctionRequest()
	{
		if(_lastItemAuctionInfoRequest + 2000L < System.currentTimeMillis())
		{
			_lastItemAuctionInfoRequest = System.currentTimeMillis();
			return true;
		}
		else
		{
			_lastItemAuctionInfoRequest = System.currentTimeMillis();
			return false;
		}
	}

	@Override
	public int getNpcId()
	{
		return -2;
	}

	public GameObject getVisibleObject(int id)
	{
		if(getObjectId() == id)
			return this;

		GameObject target = null;

		if(getTargetId() == id)
			target = getTarget();

		if(target == null && isInParty())
			for(Player p : _party.getPartyMembers())
				if(p != null && p.getObjectId() == id)
				{
					target = p;
					break;
				}

		if(target == null)
			target = World.getAroundObjectById(this, id);

		return target == null || target.isInvisible() ? null : target;
	}

	@Override
	public String getTitle()
	{
		return super.getTitle();
	}

	public int getTitleColor()
	{
		return _titlecolor;
	}

	public void setTitleColor(final int titlecolor)
	{
		if(titlecolor != DEFAULT_TITLE_COLOR)
			setVar("titlecolor", Integer.toHexString(titlecolor), -1);
		else
			unsetVar("titlecolor");
		_titlecolor = titlecolor;
	}

	@Override
	public final boolean isCursedWeaponEquipped()
	{
		return _cursedWeaponEquippedId != 0;
	}

	public final void setCursedWeaponEquippedId(int value)
	{
		_cursedWeaponEquippedId = value;
	}

	public final int getCursedWeaponEquippedId()
	{
		return _cursedWeaponEquippedId;
	}

	public final String getCursedWeaponName(Player activeChar)
	{
		if(isCursedWeaponEquipped())
			return new CustomMessage("cursed_weapon_name." + _cursedWeaponEquippedId, activeChar).toString();
		return null;
	}

	@Override
	public boolean isImmobilized()
	{
		return super.isImmobilized() || isOverloaded() || isSitting() || isFishing();
	}

	@Override
	public boolean isBlocked()
	{
		return super.isBlocked() || isInMovie() || isInObserverMode() || isTeleporting() || isLogoutStarted();
	}

	@Override
	public boolean isInvul()
	{
		return super.isInvul() || isInMovie();
	}

	/**
	 * if True, the L2Player can't take more item
	 */
	public void setOverloaded(boolean overloaded)
	{
		_overloaded = overloaded;
	}

	public boolean isOverloaded()
	{
		return _overloaded;
	}

	public boolean isFishing()
	{
		return _isFishing;
	}

	public Fishing getFishing()
	{
		return _fishing;
	}

	public void setFishing(boolean value)
	{
		_isFishing = value;
	}

	public void startFishing(FishTemplate fish, int lureId)
	{
		_fishing.setFish(fish);
		_fishing.setLureId(lureId);
		_fishing.startFishing();
	}

	public void stopFishing()
	{
		_fishing.stopFishing();
	}

	public Location getFishLoc()
	{
		return _fishing.getFishLoc();
	}

	public Bonus getBonus()
	{
		return _bonus;
	}

	public boolean hasBonus()
	{
		return _bonus.getBonusExpire() > System.currentTimeMillis() / 1000L;
	}

	public double getRateAdena()
	{
		return isInParty() ? _party._rateAdena : _bonus.getDropAdena();
	}

	public double getRateItems()
	{
		return isInParty() ? _party._rateDrop : _bonus.getDropItems();
	}

	public double getRateExp()
	{
		double rate = Config.RATE_XP_BY_LVL[getLevel()];
		rate *= isInParty() ? _party._rateExp : _bonus.getRateXp();
		rate *= getVitalityBonus() * getRecomBonusMul() + calcStat(Stats.EXP, 0, null, null);
		return rate;
	}

	public double getRateSp()
	{
		double rate = Config.RATE_SP_BY_LVL[getLevel()];
		rate *= isInParty() ? _party._rateSp : _bonus.getRateSp();
		rate *= getVitalityBonus() + calcStat(Stats.SP, 0, null, null);
		return rate;
	}

	public double getRateSpoil()
	{
		return isInParty() ? _party._rateSpoil : _bonus.getDropSpoil();
	}

	private boolean _maried = false;
	private int _partnerId = 0;
	private int _coupleId = 0;
	private boolean _maryrequest = false;
	private boolean _maryaccepted = false;

	public boolean isMaried()
	{
		return _maried;
	}

	public void setMaried(boolean state)
	{
		_maried = state;
	}

	public void setMaryRequest(boolean state)
	{
		_maryrequest = state;
	}

	public boolean isMaryRequest()
	{
		return _maryrequest;
	}

	public void setMaryAccepted(boolean state)
	{
		_maryaccepted = state;
	}

	public boolean isMaryAccepted()
	{
		return _maryaccepted;
	}

	public int getPartnerId()
	{
		return _partnerId;
	}

	public void setPartnerId(int partnerid)
	{
		_partnerId = partnerid;
	}

	public int getCoupleId()
	{
		return _coupleId;
	}

	public void setCoupleId(int coupleId)
	{
		_coupleId = coupleId;
	}

	public void setUndying(boolean val)
	{
		if(!isGM())
			return;
		_isUndying = val;
	}

	public boolean isUndying()
	{
		return _isUndying;
	}

	private List<Player> _snoopListener = new ArrayList<Player>();
	private List<Player> _snoopedPlayer = new ArrayList<Player>();

	public void broadcastSnoop(final ChatType type, final String name, final String _text)
	{
		if(_snoopListener.size() > 0)
		{
			final Snoop sn = new Snoop(getObjectId(), getName(), type.ordinal(), name, _text);
			for(Player pci : _snoopListener)
				if(pci != null)
					pci.sendPacket(sn);
		}
	}

	public void addSnooper(Player pci)
	{
		if(!_snoopListener.contains(pci))
			_snoopListener.add(pci);
	}

	public void removeSnooper(Player pci)
	{
		_snoopListener.remove(pci);
	}

	public void addSnooped(Player pci)
	{
		if(!_snoopedPlayer.contains(pci))
			_snoopedPlayer.add(pci);
	}

	public void removeSnooped(Player pci)
	{
		_snoopedPlayer.remove(pci);
	}

	/**
	 * Сброс реюза всех скилов персонажа.
	 */
	public void resetReuse()
	{
		_skillReuses.clear();
		_sharedGroupReuses.clear();
	}

	public DeathPenalty getDeathPenalty()
	{
		return _deathPenalty;
	}

	private boolean _charmOfCourage = false;

	public boolean isCharmOfCourage()
	{
		return _charmOfCourage;
	}

	public void setCharmOfCourage(boolean val)
	{
		_charmOfCourage = val;

		if(!val)
			getEffectList().stopEffects(Skill.SKILL_CHARM_OF_COURAGE);

		sendEtcStatusUpdate();
	}

	private int _increasedForce = 0;
	private int _consumedSouls = 0;

	@Override
	public int getIncreasedForce()
	{
		return _increasedForce;
	}

	@Override
	public int getConsumedSouls()
	{
		return _consumedSouls;
	}

	@Override
	public void setConsumedSouls(int i, NpcInstance monster)
	{
		if(i == _consumedSouls)
			return;

		int max = (int) calcStat(Stats.SOULS_LIMIT, 0, monster, null);

		if(i > max)
			i = max;

		if(i <= 0)
		{
			_consumedSouls = 0;
			sendEtcStatusUpdate();
			return;
		}

		if(_consumedSouls != i)
		{
			int diff = i - _consumedSouls;
			if(diff > 0)
			{
				SystemMessage sm = new SystemMessage(SystemMessage.YOUR_SOUL_HAS_INCREASED_BY_S1_SO_IT_IS_NOW_AT_S2);
				sm.addNumber(diff);
				sm.addNumber(i);
				sendPacket(sm);
			}
		}
		else if(max == i)
		{
			sendPacket(SystemMsg.SOUL_CANNOT_BE_ABSORBED_ANYMORE);
			return;
		}

		_consumedSouls = i;
		sendPacket(new EtcStatusUpdatePacket(this));
	}

	@Override
	public void setIncreasedForce(int i)
	{
		i = Math.min(i, getSkillLevel(10301) != -1 ? 15 : Charge.MAX_CHARGE);
		i = Math.max(i, 0);

		if(i != 0 && i > _increasedForce)
			sendPacket(new SystemMessage(SystemMessage.YOUR_FORCE_HAS_INCREASED_TO_S1_LEVEL).addNumber(i));

		_increasedForce = i;
		sendEtcStatusUpdate();
	}

	private long _lastFalling;

	public boolean isFalling()
	{
		return System.currentTimeMillis() - _lastFalling < 5000;
	}

	public void falling(int height)
	{
		if(!Config.DAMAGE_FROM_FALLING || isDead() || isFlying() || isInWater() || isInBoat())
			return;

		switch(getJumpState())
		{
			case IN_PROGRESS:
				return;
			case FINISHED:
				setJumpState(JumpState.NONE);
				return;
		}
		
		if(isInShuttle()) //we don't recieve dam from going down in shuttle
			return;
			
		_lastFalling = System.currentTimeMillis();
		int damage = (int) calcStat(Stats.FALL, getMaxHp() / 2000 * height, null, null);
		if(damage > 0)
		{
			int curHp = (int) getCurrentHp();
			if(curHp - damage < 1)
				setCurrentHp(1, false);
			else
				setCurrentHp(curHp - damage, false);
			sendPacket(new SystemMessage(SystemMessage.YOU_RECEIVED_S1_DAMAGE_FROM_TAKING_A_HIGH_FALL).addNumber(damage));
		}
	}

	/**
	 * Системные сообщения о текущем состоянии хп
	 */
	@Override
	public void checkHpMessages(double curHp, double newHp)
	{
		//сюда пасивные скиллы
		int[] _hp = { 30, 30 };
		int[] skills = { 290, 291 };

		//сюда активные эффекты
		int[] _effects_skills_id = { 139, 176, 292, 292, 420 };
		int[] _effects_hp = { 30, 30, 30, 60, 30 };

		double percent = getMaxHp() / 100;
		double _curHpPercent = curHp / percent;
		double _newHpPercent = newHp / percent;
		boolean needsUpdate = false;

		//check for passive skills
		for(int i = 0; i < skills.length; i++)
		{
			int level = getSkillLevel(skills[i]);
			if(level > 0)
				if(_curHpPercent > _hp[i] && _newHpPercent <= _hp[i])
				{
					sendPacket(new SystemMessage(SystemMessage.SINCE_HP_HAS_DECREASED_THE_EFFECT_OF_S1_CAN_BE_FELT).addSkillName(skills[i], level));
					needsUpdate = true;
				}
				else if(_curHpPercent <= _hp[i] && _newHpPercent > _hp[i])
				{
					sendPacket(new SystemMessage(SystemMessage.SINCE_HP_HAS_INCREASED_THE_EFFECT_OF_S1_WILL_DISAPPEAR).addSkillName(skills[i], level));
					needsUpdate = true;
				}
		}

		//check for active effects
		for(Integer i = 0; i < _effects_skills_id.length; i++)
			if(getEffectList().containsEffects(_effects_skills_id[i]))
				if(_curHpPercent > _effects_hp[i] && _newHpPercent <= _effects_hp[i])
				{
					sendPacket(new SystemMessage(SystemMessage.SINCE_HP_HAS_DECREASED_THE_EFFECT_OF_S1_CAN_BE_FELT).addSkillName(_effects_skills_id[i], 1));
					needsUpdate = true;
				}
				else if(_curHpPercent <= _effects_hp[i] && _newHpPercent > _effects_hp[i])
				{
					sendPacket(new SystemMessage(SystemMessage.SINCE_HP_HAS_INCREASED_THE_EFFECT_OF_S1_WILL_DISAPPEAR).addSkillName(_effects_skills_id[i], 1));
					needsUpdate = true;
				}

		if(needsUpdate)
			sendChanges();
	}

	/**
	 * Системные сообщения для темных эльфов о вкл/выкл ShadowSence (skill id = 294)
	 */
	public void checkDayNightMessages()
	{
		int level = getSkillLevel(294);
		if(level > 0)
			if(GameTimeController.getInstance().isNowNight())
				sendPacket(new SystemMessage(SystemMessage.IT_IS_NOW_MIDNIGHT_AND_THE_EFFECT_OF_S1_CAN_BE_FELT).addSkillName(294, level));
			else
				sendPacket(new SystemMessage(SystemMessage.IT_IS_DAWN_AND_THE_EFFECT_OF_S1_WILL_NOW_DISAPPEAR).addSkillName(294, level));
		sendChanges();
	}

	public int getZoneMask()
	{
		return _zoneMask;
	}

	//TODO [G1ta0] переработать в лисенер?
	@Override
	protected void onUpdateZones(List<Zone> leaving, List<Zone> entering)
	{
		super.onUpdateZones(leaving, entering);

		if((leaving == null || leaving.isEmpty()) && (entering == null || entering.isEmpty()))
			return;

		boolean lastInCombatZone = (_zoneMask & ZONE_PVP_FLAG) == ZONE_PVP_FLAG;
		boolean lastInDangerArea = (_zoneMask & ZONE_ALTERED_FLAG) == ZONE_ALTERED_FLAG;
		boolean lastOnSiegeField = (_zoneMask & ZONE_SIEGE_FLAG) == ZONE_SIEGE_FLAG;
		boolean lastInPeaceZone = (_zoneMask & ZONE_PEACE_FLAG) == ZONE_PEACE_FLAG;
		//FIXME G1ta0 boolean lastInSSQZone = (_zoneMask & ZONE_SSQ_FLAG) == ZONE_SSQ_FLAG;

		boolean isInCombatZone = isInCombatZone();
		boolean isInDangerArea = isInDangerArea() || isInZone(ZoneType.CHANGED_ZONE);
		boolean isOnSiegeField = isOnSiegeField();
		boolean isInPeaceZone = isInPeaceZone();
		boolean isInSSQZone = isInSSQZone();

		// обновляем компас, только если персонаж в мире
		int lastZoneMask = _zoneMask;
		_zoneMask = 0;

		if(isInCombatZone)
			_zoneMask |= ZONE_PVP_FLAG;
		if(isInDangerArea)
			_zoneMask |= ZONE_ALTERED_FLAG;
		if(isOnSiegeField)
			_zoneMask |= ZONE_SIEGE_FLAG;
		if(isInPeaceZone)
			_zoneMask |= ZONE_PEACE_FLAG;
		if(isInSSQZone)
			_zoneMask |= ZONE_SSQ_FLAG;

		if(lastZoneMask != _zoneMask)
			sendPacket(new ExSetCompassZoneCode(this));

		if(lastInCombatZone != isInCombatZone)
			broadcastRelationChanged();

		if(lastInDangerArea != isInDangerArea)
			sendPacket(new EtcStatusUpdatePacket(this));

		if(lastOnSiegeField != isOnSiegeField)
		{
			broadcastRelationChanged();
			if(isOnSiegeField)
				sendPacket(SystemMsg.YOU_HAVE_ENTERED_A_COMBAT_ZONE);
			else
			{
				//Если игрок выходит за территорию осады и у него есть флаг, то отбираем его и спавним в дефолтное место.
				//TODO: [Bonux] Проверить как на оффе.
				FlagItemAttachment attachment = getActiveWeaponFlagAttachment();
				if(attachment != null)
					attachment.onLeaveSiegeZone(this);

				sendPacket(SystemMsg.YOU_HAVE_LEFT_A_COMBAT_ZONE);
				if(!isTeleporting() && getPvpFlag() == 0)
					startPvPFlag(null);
			}
		}

		if(lastInPeaceZone != isInPeaceZone)
		{
			if(isInPeaceZone)
				setRecomTimerActive(false);
		}

		if(isInWater())
			startWaterTask();
		else
			stopWaterTask();
	}

	public void startAutoSaveTask()
	{
		if(!Config.AUTOSAVE)
			return;
		if(_autoSaveTask == null)
			_autoSaveTask = AutoSaveManager.getInstance().addAutoSaveTask(this);
	}

	public void stopAutoSaveTask()
	{
		if(_autoSaveTask != null)
			_autoSaveTask.cancel(false);
		_autoSaveTask = null;
	}

	public void startPcBangPointsTask()
	{
		if(!Config.ALT_PCBANG_POINTS_ENABLED || Config.ALT_PCBANG_POINTS_DELAY <= 0)
			return;
		if(_pcCafePointsTask == null)
			_pcCafePointsTask = LazyPrecisionTaskManager.getInstance().addPCCafePointsTask(this);
	}

	public void stopPcBangPointsTask()
	{
		if(_pcCafePointsTask != null)
			_pcCafePointsTask.cancel(false);
		_pcCafePointsTask = null;
	}

	public void startUnjailTask(Player player, int time)
	{
		if(_unjailTask != null)
			_unjailTask.cancel(false);
		_unjailTask = ThreadPoolManager.getInstance().schedule(new UnJailTask(player), time * 60000);
	}

	public void stopUnjailTask()
	{
		if(_unjailTask != null)
			_unjailTask.cancel(false);
		_unjailTask = null;
	}

	@Override
	public void sendMessage(String message)
	{
		sendPacket(new SystemMessage(message));
	}

	private Location _lastClientPosition;
	private Location _lastServerPosition;

	public void setLastClientPosition(Location position)
	{
		_lastClientPosition = position;
	}

	public Location getLastClientPosition()
	{
		return _lastClientPosition;
	}

	public void setLastServerPosition(Location position)
	{
		_lastServerPosition = position;
	}

	public Location getLastServerPosition()
	{
		return _lastServerPosition;
	}

	private int _useSeed = 0;

	public void setUseSeed(int id)
	{
		_useSeed = id;
	}

	public int getUseSeed()
	{
		return _useSeed;
	}

	public int getRelation(Player target)
	{
		int result = 0;

		if(getClan() != null)
		{
			result |= RelationChangedPacket.RELATION_CLAN_MEMBER;
			if(getClan() == target.getClan())
				result |= RelationChangedPacket.RELATION_CLAN_MATE;
			if(getClan().getAllyId() != 0)
				result |= RelationChangedPacket.RELATION_ALLY_MEMBER;
		}

		if(isClanLeader())
			result |= RelationChangedPacket.RELATION_LEADER;

		Party party = getParty();
		if(party != null && party == target.getParty())
		{
			result |= RelationChangedPacket.RELATION_HAS_PARTY;

			switch(party.getPartyMembers().indexOf(this))
			{
				case 0:
					result |= RelationChangedPacket.RELATION_PARTYLEADER; // 0x10
					break;
				case 1:
					result |= RelationChangedPacket.RELATION_PARTY4; // 0x8
					break;
				case 2:
					result |= RelationChangedPacket.RELATION_PARTY3 + RelationChangedPacket.RELATION_PARTY2 + RelationChangedPacket.RELATION_PARTY1; // 0x7
					break;
				case 3:
					result |= RelationChangedPacket.RELATION_PARTY3 + RelationChangedPacket.RELATION_PARTY2; // 0x6
					break;
				case 4:
					result |= RelationChangedPacket.RELATION_PARTY3 + RelationChangedPacket.RELATION_PARTY1; // 0x5
					break;
				case 5:
					result |= RelationChangedPacket.RELATION_PARTY3; // 0x4
					break;
				case 6:
					result |= RelationChangedPacket.RELATION_PARTY2 + RelationChangedPacket.RELATION_PARTY1; // 0x3
					break;
				case 7:
					result |= RelationChangedPacket.RELATION_PARTY2; // 0x2
					break;
				case 8:
					result |= RelationChangedPacket.RELATION_PARTY1; // 0x1
					break;
			}
		}

		Clan clan1 = getClan();
		Clan clan2 = target.getClan();
		if(clan1 != null && clan2 != null)
		{
			if(target.getPledgeType() != Clan.SUBUNIT_ACADEMY && getPledgeType() != Clan.SUBUNIT_ACADEMY)
				if(clan2.isAtWarWith(clan1.getClanId()))
				{
					result |= RelationChangedPacket.RELATION_1SIDED_WAR;
					if(clan1.isAtWarWith(clan2.getClanId()))
						result |= RelationChangedPacket.RELATION_MUTUAL_WAR;
				}
			if(getBlockCheckerArena() != -1)
			{
				result |= RelationChangedPacket.RELATION_IN_SIEGE;
				ArenaParticipantsHolder holder = HandysBlockCheckerManager.getInstance().getHolder(getBlockCheckerArena());
				if(holder.getPlayerTeam(this) == 0)
					result |= RelationChangedPacket.RELATION_ENEMY;
				else
					result |= RelationChangedPacket.RELATION_ALLY;
				result |= RelationChangedPacket.RELATION_ATTACKER;
			}
		}

		for(GlobalEvent e : getEvents())
			result = e.getRelation(this, target, result);

		return result;
	}

	/**
	 * 0=White, 1=Purple, 2=PurpleBlink
	 */
	protected int _pvpFlag;

	private Future<?> _PvPRegTask;
	private long _lastPvpAttack;

	public long getlastPvpAttack()
	{
		return _lastPvpAttack;
	}

	@Override
	public void startPvPFlag(Creature target)
	{
		if(isPK())
			return;

		long startTime = System.currentTimeMillis();
		if(target != null && target.getPvpFlag() != 0)
			startTime -= Config.PVP_TIME / 2;
		if(_pvpFlag != 0 && _lastPvpAttack > startTime)
			return;

		_lastPvpAttack = startTime;

		updatePvPFlag(1);

		if(_PvPRegTask == null)
			_PvPRegTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new PvPFlagTask(this), 1000, 1000);
	}

	public void stopPvPFlag()
	{
		if(_PvPRegTask != null)
		{
			_PvPRegTask.cancel(false);
			_PvPRegTask = null;
		}
		updatePvPFlag(0);
	}

	public void updatePvPFlag(int value)
	{
		if(_handysBlockCheckerEventArena != -1)
			return;
		if(_pvpFlag == value)
			return;

		setPvpFlag(value);

		sendStatusUpdate(true, true, StatusUpdatePacket.PVP_FLAG);

		broadcastRelationChanged();
	}

	public void setPvpFlag(int pvpFlag)
	{
		_pvpFlag = pvpFlag;
	}

	@Override
	public int getPvpFlag()
	{
		return _pvpFlag;
	}

	public boolean isInDuel()
	{
		return getEvent(DuelEvent.class) != null;
	}

	private Map<Integer, TamedBeastInstance> _tamedBeasts = new ConcurrentHashMap<Integer, TamedBeastInstance>();

	public Map<Integer, TamedBeastInstance> getTrainedBeasts()
	{
		return _tamedBeasts;
	}

	public void addTrainedBeast(TamedBeastInstance tamedBeast)
	{
		_tamedBeasts.put(tamedBeast.getObjectId(), tamedBeast);
	}

	public void removeTrainedBeast(int npcId)
	{
		_tamedBeasts.remove(npcId);
	}

	private long _lastAttackPacket = 0;

	public long getLastAttackPacket()
	{
		return _lastAttackPacket;
	}

	public void setLastAttackPacket()
	{
		_lastAttackPacket = System.currentTimeMillis();
	}

	private long _lastMovePacket = 0;

	public long getLastMovePacket()
	{
		return _lastMovePacket;
	}

	public void setLastMovePacket()
	{
		_lastMovePacket = System.currentTimeMillis();
	}

	public byte[] getKeyBindings()
	{
		return _keyBindings;
	}

	public void setKeyBindings(byte[] keyBindings)
	{
		if(keyBindings == null)
			keyBindings = ArrayUtils.EMPTY_BYTE_ARRAY;
		_keyBindings = keyBindings;
	}

	/**
	 * Возвращает коллекцию скиллов, с учетом текущей трансформации
	 */
	@Override
	public final Collection<Skill> getAllSkills()
	{
		// Трансформация неактивна
		if(!isTransformed())
			return super.getAllSkills();

		// Трансформация активна
		TIntObjectMap<Skill> temp = new TIntObjectHashMap<Skill>();
		for(Skill skill : super.getAllSkills())
		{
			if(skill != null && !skill.isActive() && !skill.isToggle())
				temp.put(skill.getId(), skill);
		}

		temp.putAll(_transformSkills); // Добавляем к пассивкам скилы текущей трансформации
		return temp.valueCollection();
	}

	public final void addTransformSkill(Skill skill)
	{
		_transformSkills.put(skill.getId(), skill);
	}

	public final void removeTransformSkill(Skill skill)
	{
		_transformSkills.remove(skill.getId());
	}

	public void setAgathion(int id)
	{
		if(_agathionId == id)
			return;

		_agathionId = id;

		sendPacket(new ExUserInfoCubic(this));
		broadcastCharInfo();
	}

	public int getAgathionId()
	{
		return _agathionId;
	}

	/**
	 * Возвращает количество PcBangPoint'ов даного игрока
	 *
	 * @return количество PcCafe Bang Points
	 */
	public int getPcBangPoints()
	{
		return _pcBangPoints;
	}

	/**
	 * Устанавливает количество Pc Cafe Bang Points для даного игрока
	 *
	 * @param val новое количество PcCafeBangPoints
	 */
	public void setPcBangPoints(int val)
	{
		_pcBangPoints = val;
	}

	public void addPcBangPoints(int count, boolean doublePoints)
	{
		if(doublePoints)
			count *= 2;

		_pcBangPoints += count;

		sendPacket(new SystemMessage(doublePoints ? SystemMessage.DOUBLE_POINTS_YOU_AQUIRED_S1_PC_BANG_POINT : SystemMessage.YOU_ACQUIRED_S1_PC_BANG_POINT).addNumber(count));
		sendPacket(new ExPCCafePointInfoPacket(this, count, 1, 2, 12));
	}

	public boolean reducePcBangPoints(int count)
	{
		if(_pcBangPoints < count)
			return false;

		_pcBangPoints -= count;
		sendPacket(new SystemMessage(SystemMessage.YOU_ARE_USING_S1_POINT).addNumber(count));
		sendPacket(new ExPCCafePointInfoPacket(this, 0, 1, 2, 12));
		return true;
	}

	private Location _groundSkillLoc;

	public void setGroundSkillLoc(Location location)
	{
		_groundSkillLoc = location;
	}

	public Location getGroundSkillLoc()
	{
		return _groundSkillLoc;
	}

	/**
	 * Персонаж в процессе выхода из игры
	 *
	 * @return возвращает true если процесс выхода уже начался
	 */
	public boolean isLogoutStarted()
	{
		return _isLogout.get();
	}

	public void setOfflineMode(boolean val)
	{
		if(!val)
			unsetVar("offline");
		_offline = val;
	}

	public boolean isInOfflineMode()
	{
		return _offline;
	}

	public void saveTradeList()
	{
		String val = "";

		if(_sellList == null || _sellList.isEmpty())
			unsetVar("selllist");
		else
		{
			for(TradeItem i : _sellList)
				val += i.getObjectId() + ";" + i.getCount() + ";" + i.getOwnersPrice() + ":";
			setVar("selllist", val, -1);
			val = "";
			if(_tradeList != null && getSellStoreName() != null)
				setVar("sellstorename", getSellStoreName(), -1);
		}

		if(_packageSellList == null || _packageSellList.isEmpty())
			unsetVar("packageselllist");
		else
		{
			for(TradeItem i : _packageSellList)
				val += i.getObjectId() + ";" + i.getCount() + ";" + i.getOwnersPrice() + ":";
			setVar("packageselllist", val, -1);
			val = "";
			if(_tradeList != null && getSellStoreName() != null)
				setVar("sellstorename", getSellStoreName(), -1);
		}

		if(_buyList == null || _buyList.isEmpty())
			unsetVar("buylist");
		else
		{
			for(TradeItem i : _buyList)
				val += i.getItemId() + ";" + i.getCount() + ";" + i.getOwnersPrice() + ":";
			setVar("buylist", val, -1);
			val = "";
			if(_tradeList != null && getBuyStoreName() != null)
				setVar("buystorename", getBuyStoreName(), -1);
		}

		if(_createList == null || _createList.isEmpty())
			unsetVar("createlist");
		else
		{
			for(ManufactureItem i : _createList)
				val += i.getRecipeId() + ";" + i.getCost() + ":";
			setVar("createlist", val, -1);
			if(getManufactureName() != null)
				setVar("manufacturename", getManufactureName(), -1);
		}
	}

	public void restoreTradeList()
	{
		String var;
		var = getVar("selllist");
		if(var != null)
		{
			_sellList = new CopyOnWriteArrayList<TradeItem>();
			String[] items = var.split(":");
			for(String item : items)
			{
				if(item.equals(""))
					continue;
				String[] values = item.split(";");
				if(values.length < 3)
					continue;

				int oId = Integer.parseInt(values[0]);
				long count = Long.parseLong(values[1]);
				long price = Long.parseLong(values[2]);

				ItemInstance itemToSell = getInventory().getItemByObjectId(oId);

				if(count < 1 || itemToSell == null)
					continue;

				if(count > itemToSell.getCount())
					count = itemToSell.getCount();

				TradeItem i = new TradeItem(itemToSell);
				i.setCount(count);
				i.setOwnersPrice(price);

				_sellList.add(i);
			}
			var = getVar("sellstorename");
			if(var != null)
				setSellStoreName(var);
		}
		var = getVar("packageselllist");
		if(var != null)
		{
			_packageSellList = new CopyOnWriteArrayList<TradeItem>();
			String[] items = var.split(":");
			for(String item : items)
			{
				if(item.equals(""))
					continue;
				String[] values = item.split(";");
				if(values.length < 3)
					continue;

				int oId = Integer.parseInt(values[0]);
				long count = Long.parseLong(values[1]);
				long price = Long.parseLong(values[2]);

				ItemInstance itemToSell = getInventory().getItemByObjectId(oId);

				if(count < 1 || itemToSell == null)
					continue;

				if(count > itemToSell.getCount())
					count = itemToSell.getCount();

				TradeItem i = new TradeItem(itemToSell);
				i.setCount(count);
				i.setOwnersPrice(price);

				_packageSellList.add(i);
			}
			var = getVar("sellstorename");
			if(var != null)
				setSellStoreName(var);
		}
		var = getVar("buylist");
		if(var != null)
		{
			_buyList = new CopyOnWriteArrayList<TradeItem>();
			String[] items = var.split(":");
			for(String item : items)
			{
				if(item.equals(""))
					continue;
				String[] values = item.split(";");
				if(values.length < 3)
					continue;
				TradeItem i = new TradeItem();
				i.setItemId(Integer.parseInt(values[0]));
				i.setCount(Long.parseLong(values[1]));
				i.setOwnersPrice(Long.parseLong(values[2]));
				_buyList.add(i);
			}
			var = getVar("buystorename");
			if(var != null)
				setBuyStoreName(var);
		}
		var = getVar("createlist");
		if(var != null)
		{
			_createList = new CopyOnWriteArrayList<ManufactureItem>();
			String[] items = var.split(":");
			for(String item : items)
			{
				if(item.equals(""))
					continue;
				String[] values = item.split(";");
				if(values.length < 2)
					continue;
				int recId = Integer.parseInt(values[0]);
				long price = Long.parseLong(values[1]);
				if(findRecipe(recId))
					_createList.add(new ManufactureItem(recId, price));
			}
			var = getVar("manufacturename");
			if(var != null)
				setManufactureName(var);
		}
	}

	public void restoreRecipeBook()
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT id FROM character_recipebook WHERE char_id=?");
			statement.setInt(1, getObjectId());
			rset = statement.executeQuery();

			while(rset.next())
			{
				int id = rset.getInt("id");
				RecipeTemplate recipe = RecipeHolder.getInstance().getRecipeByRecipeId(id);
				registerRecipe(recipe, false);
			}
		}
		catch(Exception e)
		{
			_log.warn("count not recipe skills:" + e);
			_log.error("", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}

	public List<DecoyInstance> getDecoys()
	{
		return _decoys;
	}

	public void addDecoy(DecoyInstance decoy)
	{
		_decoys.add(decoy);
	}

	public void removeDecoy(DecoyInstance decoy)
	{
		_decoys.remove(decoy);
	}

	public MountType getMountType()
	{
		return _mount == null ? MountType.NONE : _mount.getType();
	}

	@Override
	public void setReflection(Reflection reflection)
	{
		if(getReflection() == reflection)
			return;

		super.setReflection(reflection);

		Servitor[] servitors = getServitors();
		if(servitors.length > 0)
		{
			for(Servitor servitor : servitors)
			{
				if(!servitor.isDead())
					servitor.setReflection(reflection);
			}
		}

		if(reflection != ReflectionManager.DEFAULT)
		{
			String var = getVar("reflection");
			if(var == null || !var.equals(String.valueOf(reflection.getId())))
				setVar("reflection", String.valueOf(reflection.getId()), -1);
		}
		else
			unsetVar("reflection");

		if(getActiveSubClass() != null)
		{
			getInventory().validateItems();
			// Для квеста _129_PailakaDevilsLegacy
			if(servitors.length > 0)
			{
				for(Servitor servitor : servitors)
				{
					if(servitor != null && (servitor.getNpcId() == 14916 || servitor.getNpcId() == 14917))
						servitor.unSummon(false);
				}
			}
		}
	}

	public boolean isTerritoryFlagEquipped()
	{
		ItemInstance weapon = getActiveWeaponInstance();
		return weapon != null && weapon.getTemplate().isTerritoryFlag();
	}

	private int _buyListId;

	public void setBuyListId(int listId)
	{
		_buyListId = listId;
	}

	public int getBuyListId()
	{
		return _buyListId;
	}

	public int getFame()
	{
		return _fame;
	}

	public void setFame(int fame, String log)
	{
		fame = Math.min(Config.LIM_FAME, fame);
		if(log != null && !log.isEmpty())
			Log.add(_name + "|" + (fame - _fame) + "|" + fame + "|" + log, "fame");
		if(fame > _fame)
			sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_ACQUIRED_S1_REPUTATION_SCORE).addNumber(fame - _fame));
		_fame = fame;
		sendChanges();
	}

	public int getVitality()
	{
		return getActiveSubClass() == null ? 0 : getActiveSubClass().getVitality();
	}

	public void setVitality(int val)
	{
		setVitality(val, true);
	}

	public void setVitality(int val, boolean send)
	{
		if(getActiveSubClass() != null)
			getActiveSubClass().setVitality(val);

		if(send)
			sendPacket(new ExVitalityPointInfo(getVitality()));
	}

	public double getVitalityBonus()
	{
		return getVitality() > 0 ? (hasBonus() ? Config.ALT_VITALITY_PA_RATE : Config.ALT_VITALITY_RATE) : 1.;
	}

	public int getVitalityPotionsLimit()
	{
		return hasBonus() ? Config.ALT_VITALITY_POTIONS_PA_LIMIT : Config.ALT_VITALITY_POTIONS_LIMIT;
	}

	public void setUsedVitalityPotions(int val, boolean send)
	{
		if(getActiveSubClass() != null)
			getActiveSubClass().setUsedVitalityPotions(val);

		if(send)
			sendPacket(new ExVitalityEffectInfo(this));
	}

	public int getUsedVitalityPotions()
	{
		return getActiveSubClass() == null ? 0 : getActiveSubClass().getUsedVitalityPotions();
	}

	public int getVitalityPotionsLeft()
	{
		return Math.max(0, (getVitalityPotionsLimit() - getUsedVitalityPotions()));
	}

	private void checkVitality()
	{
		Calendar temp = Calendar.getInstance();
		if(temp.get(Calendar.DAY_OF_WEEK) > Calendar.WEDNESDAY)
		{
			temp.add(Calendar.DAY_OF_MONTH, 7);
		}
		temp.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
		temp.set(Calendar.HOUR_OF_DAY, 6);
		temp.set(Calendar.MINUTE, 30);
		temp.set(Calendar.SECOND, 0);
		temp.set(Calendar.MILLISECOND, 0);
		if(_lastAccess < temp.getTimeInMillis() / 1000 && System.currentTimeMillis() > temp.getTimeInMillis())
			restartVitality(true);
	}

	public void restartVitality(boolean onRestore)
	{
		for(SubClass sub : getSubClassList().values())
		{
			sub.setVitality(MAX_VITALITY_POINTS);
			sub.setUsedVitalityPotions(0);
		}

		if(!onRestore)
			sendPacket(new ExVitalityEffectInfo(this));
	}

	private final int _incorrectValidateCount = 0;

	public int getIncorrectValidateCount()
	{
		return _incorrectValidateCount;
	}

	public int setIncorrectValidateCount(int count)
	{
		return _incorrectValidateCount;
	}

	public int getExpandInventory()
	{
		return _expandInventory;
	}

	public void setExpandInventory(int inventory)
	{
		_expandInventory = inventory;
	}

	public int getExpandWarehouse()
	{
		return _expandWarehouse;
	}

	public void setExpandWarehouse(int warehouse)
	{
		_expandWarehouse = warehouse;
	}

	public boolean isNotShowBuffAnim()
	{
		return _notShowBuffAnim;
	}

	public void setNotShowBuffAnim(boolean value)
	{
		_notShowBuffAnim = value;
	}

	public void enterMovieMode()
	{
		if(isInMovie()) //already in movie
			return;

		setTarget(null);
		stopMove();
		setMovieId(-1);
		sendPacket(new CameraMode(1));
	}

	public void leaveMovieMode()
	{
		setMovieId(0);
		sendPacket(new CameraMode(0));
		broadcastCharInfo();
	}

	public void specialCamera(GameObject target, int dist, int yaw, int pitch, int time, int duration)
	{
		sendPacket(new SpecialCamera(target.getObjectId(), dist, yaw, pitch, time, duration));
	}

	public void specialCamera(GameObject target, int dist, int yaw, int pitch, int time, int duration, int turn, int rise, int widescreen, int unk)
	{
		sendPacket(new SpecialCamera(target.getObjectId(), dist, yaw, pitch, time, duration, turn, rise, widescreen, unk));
	}

	private int _movieId = 0;

	public void setMovieId(int id)
	{
		_movieId = id;
	}

	public int getMovieId()
	{
		return _movieId;
	}

	public boolean isInMovie()
	{
		return _movieId != 0;
	}

	public void startScenePlayer(SceneMovie movie)
	{
		if(isInMovie()) //already in movie
			return;

		sendActionFailed();
		setTarget(null);
		stopMove();
		setMovieId(movie.getId());
		sendPacket(movie.packet(this));
	}

	public void startScenePlayer(int movieId)
	{
		if(isInMovie()) //already in movie
			return;

		sendActionFailed();
		setTarget(null);
		stopMove();
		setMovieId(movieId);
		sendPacket(new ExStartScenePlayer(movieId));
	}

	public void endScenePlayer()
	{
		if(!isInMovie())
			return;

		setMovieId(0);
		decayMe();
		spawnMe();
	}

	public void setAutoLoot(boolean enable)
	{
		if(Config.AUTO_LOOT_INDIVIDUAL)
		{
			_autoLoot = enable;
			setVar("AutoLoot", String.valueOf(enable), -1);
		}
	}

	public void setAutoLootOnlyAdena(boolean enable)
	{
		if(Config.AUTO_LOOT_INDIVIDUAL && Config.AUTO_LOOT_ONLY_ADENA)
		{
			_autoLootOnlyAdena = enable;
			setVar("AutoLootOnlyAdena", String.valueOf(enable), -1);
		}
	}

	public void setAutoLootHerbs(boolean enable)
	{
		if(Config.AUTO_LOOT_INDIVIDUAL)
		{
			AutoLootHerbs = enable;
			setVar("AutoLootHerbs", String.valueOf(enable), -1);
		}
	}

	public boolean isAutoLootEnabled()
	{
		return _autoLoot;
	}

	public boolean isAutoLootOnlyAdenaEnabled()
	{
		return _autoLootOnlyAdena;
	}

	public boolean isAutoLootHerbsEnabled()
	{
		return AutoLootHerbs;
	}

	public final void reName(String name, boolean saveToDB)
	{
		setName(name);
		if(saveToDB)
			saveNameToDB();
		broadcastUserInfo(true);
	}

	public final void reName(String name)
	{
		reName(name, false);
	}

	public final void saveNameToDB()
	{
		Connection con = null;
		PreparedStatement st = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			st = con.prepareStatement("UPDATE characters SET char_name = ? WHERE obj_Id = ?");
			st.setString(1, getName());
			st.setInt(2, getObjectId());
			st.executeUpdate();
		}
		catch(Exception e)
		{
			_log.error("", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, st);
		}
	}

	@Override
	public Player getPlayer()
	{
		return this;
	}

	private List<String> getStoredBypasses(boolean bbs)
	{
		if(bbs)
		{
			if(bypasses_bbs == null)
				bypasses_bbs = new LazyArrayList<String>();
			return bypasses_bbs;
		}
		if(bypasses == null)
			bypasses = new LazyArrayList<String>();
		return bypasses;
	}

	public void cleanBypasses(boolean bbs)
	{
		List<String> bypassStorage = getStoredBypasses(bbs);
		synchronized (bypassStorage)
		{
			bypassStorage.clear();
		}
	}

	public String encodeBypasses(String htmlCode, boolean bbs)
	{
		List<String> bypassStorage = getStoredBypasses(bbs);
		synchronized (bypassStorage)
		{
			return BypassManager.encode(htmlCode, bypassStorage, bbs);
		}
	}

	public DecodedBypass decodeBypass(String bypass)
	{
		if(bypass.isEmpty())
			return null;

		BypassType bpType = BypassManager.getBypassType(bypass);
		boolean bbs = bpType == BypassType.ENCODED_BBS || bpType == BypassType.SIMPLE_BBS;
		List<String> bypassStorage = getStoredBypasses(bbs);

		switch(bpType)
		{
			case ENCODED:
			case ENCODED_BBS:
				return BypassManager.decode(bypass, bypassStorage, bbs, this);
			case SIMPLE:
				return new DecodedBypass(bypass, false).trim();
			case SIMPLE_BBS:
				return BypassManager.STATIC_BBS_SIMPLE.get(bypass);
		}

		_log.warn("Direct access to bypass: " + bypass + " / Player: " + getName());
		return null;
	}

	public int getTalismanCount()
	{
		return (int) calcStat(Stats.TALISMANS_LIMIT, 0, null, null);
	}

	public int getJewelsLimit()
	{
		return (int) calcStat(Stats.JEWELS_LIMIT, 0, null, null);
	}

	public final void disableDrop(int time)
	{
		_dropDisabled = System.currentTimeMillis() + time;
	}

	public final boolean isDropDisabled()
	{
		return _dropDisabled > System.currentTimeMillis();
	}

	private ItemInstance _petControlItem = null;

	public void setPetControlItem(int itemObjId)
	{
		setPetControlItem(getInventory().getItemByObjectId(itemObjId));
	}

	public void setPetControlItem(ItemInstance item)
	{
		_petControlItem = item;
	}

	public ItemInstance getPetControlItem()
	{
		return _petControlItem;
	}

	private AtomicBoolean isActive = new AtomicBoolean();

	public boolean isActive()
	{
		return isActive.get();
	}

	public void setActive()
	{
		setNonAggroTime(0);
		setNonPvpTime(0);

		if(isActive.getAndSet(true))
			return;

		onActive();
	}

	private void onActive()
	{
		setNonAggroTime(0);
		setNonPvpTime(0);
		sendPacket(SystemMsg.YOU_ARE_NO_LONGER_PROTECTED_FROM_AGGRESSIVE_MONSTERS);

		if(getPetControlItem() != null || _restoredSummons != null && !_restoredSummons.isEmpty())
		{
			ThreadPoolManager.getInstance().execute(new RunnableImpl(){
				@Override
				public void runImpl()
				{
					if(getPetControlItem() != null)
						summonPet();

					if(_restoredSummons != null && !_restoredSummons.isEmpty())
						spawnRestoredSummons();
				}
			});
		}
	}

	public void summonPet()
	{
		if(getPet() != null)
			return;

		ItemInstance controlItem = getPetControlItem();
		if(controlItem == null)
			return;

		PetData petTemplate = PetDataHolder.getInstance().getTemplateByItemId(controlItem.getItemId());
		if(petTemplate == null)
			return;

		NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(petTemplate.getNpcId());
		if(npcTemplate == null)
			return;

		PetInstance pet = PetInstance.restore(controlItem, npcTemplate, this);
		if(pet == null)
			return;

		setPet(pet);
		pet.setTitle(getName());

		if(!pet.isRespawned())
		{
			pet.setCurrentHp(pet.getMaxHp(), false);
			pet.setCurrentMp(pet.getMaxMp());
			pet.setCurrentFed(pet.getMaxFed(), false);
			pet.updateControlItem();
			pet.store();
		}

		pet.getInventory().restore();

		pet.setNonAggroTime(System.currentTimeMillis() + Config.NONAGGRO_TIME_ONTELEPORT);
		pet.setNonPvpTime(System.currentTimeMillis() + Config.NONPVP_TIME_ONTELEPORT);
		pet.setReflection(getReflection());
		pet.spawnMe(Location.findPointToStay(this, 50, 70));
		pet.setRunning();
		pet.setFollowMode(true);
		pet.getInventory().validateItems();

		if(pet instanceof PetBabyInstance)
			((PetBabyInstance) pet).startBuffTask();
	}

	public void restoreSummons()
	{
		_restoredSummons = SummonsDAO.getInstance().restore(this);
	}

	private void spawnRestoredSummons()
	{
		if(_restoredSummons == null || _restoredSummons.isEmpty())
			return;

		for(RestoredSummon summon : _restoredSummons)
		{
			Skill skill = SkillTable.getInstance().getInfo(summon.skillId, summon.skillLvl);
			if(skill == null)
				continue;

			if(skill instanceof Summon)
				((Summon) skill).summon(this, null, summon);
		}
		_restoredSummons.clear();
		_restoredSummons = null;
	}

	private Map<Integer, Long> _traps;

	public Collection<TrapInstance> getTraps()
	{
		if(_traps == null)
			return null;
		Collection<TrapInstance> result = new ArrayList<TrapInstance>(getTrapsCount());
		TrapInstance trap;
		for(Integer trapId : _traps.keySet())
			if((trap = (TrapInstance) GameObjectsStorage.get(_traps.get(trapId))) != null)
				result.add(trap);
			else
				_traps.remove(trapId);
		return result;
	}

	public int getTrapsCount()
	{
		return _traps == null ? 0 : _traps.size();
	}

	public void addTrap(TrapInstance trap)
	{
		if(_traps == null)
			_traps = new HashMap<Integer, Long>();
		_traps.put(trap.getObjectId(), trap.getStoredId());
	}

	public void removeTrap(TrapInstance trap)
	{
		Map<Integer, Long> traps = _traps;
		if(traps == null || traps.isEmpty())
			return;
		traps.remove(trap.getObjectId());
	}

	public void destroyFirstTrap()
	{
		Map<Integer, Long> traps = _traps;
		if(traps == null || traps.isEmpty())
			return;
		TrapInstance trap;
		for(Integer trapId : traps.keySet())
		{
			if((trap = (TrapInstance) GameObjectsStorage.get(traps.get(trapId))) != null)
			{
				trap.deleteMe();
				return;
			}
			return;
		}
	}
	
	public TrapInstance getFirstTrap()
	{
		Map<Integer, Long> traps = _traps;
		if(traps == null || traps.isEmpty())
			return null;
		TrapInstance trap;
		for(Integer trapId : traps.keySet())
		{
			if((trap = (TrapInstance) GameObjectsStorage.get(traps.get(trapId))) != null)
			{
				return trap;
			}
		}
		return null;	
	}
	
	public void destroyAllTraps()
	{
		Map<Integer, Long> traps = _traps;
		if(traps == null || traps.isEmpty())
			return;
		List<TrapInstance> toRemove = new ArrayList<TrapInstance>();
		for(Integer trapId : traps.keySet())
			toRemove.add((TrapInstance) GameObjectsStorage.get(traps.get(trapId)));
		for(TrapInstance t : toRemove)
			if(t != null)
				t.deleteMe();
	}

	public void setBlockCheckerArena(byte arena)
	{
		_handysBlockCheckerEventArena = arena;
	}

	public int getBlockCheckerArena()
	{
		return _handysBlockCheckerEventArena;
	}

	@Override
	public PlayerListenerList getListeners()
	{
		if(listeners == null)
			synchronized (this)
			{
				if(listeners == null)
					listeners = new PlayerListenerList(this);
			}
		return (PlayerListenerList) listeners;
	}

	@Override
	public PlayerStatsChangeRecorder getStatsRecorder()
	{
		if(_statsRecorder == null)
			synchronized (this)
			{
				if(_statsRecorder == null)
					_statsRecorder = new PlayerStatsChangeRecorder(this);
			}
		return (PlayerStatsChangeRecorder) _statsRecorder;
	}

	private Future<?> _hourlyTask;
	private int _hoursInGame = 0;

	public int getHoursInGame()
	{
		_hoursInGame++;
		return _hoursInGame;
	}

	public void startHourlyTask()
	{
		_hourlyTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new HourlyTask(this), 3600000L, 3600000L);
	}

	public void stopHourlyTask()
	{
		if(_hourlyTask != null)
		{
			_hourlyTask.cancel(false);
			_hourlyTask = null;
		}
	}

	public long getPremiumPoints()
	{
		if(Config.IM_PAYMENT_ITEM_ID > 0)
			return ItemFunctions.getItemCount(this, Config.IM_PAYMENT_ITEM_ID);

		if(getNetConnection() != null)
			return getNetConnection().getPoints();

		return 0;
	}

	public boolean reducePremiumPoints(final int val)
	{
		if(Config.IM_PAYMENT_ITEM_ID > 0)
		{
			if(ItemFunctions.removeItem(this, Config.IM_PAYMENT_ITEM_ID, val, true) == val)
				return true;
			return false;
		}

		if(getNetConnection() != null)
		{
			getNetConnection().setPoints((int) (getPremiumPoints() - val));
			AuthServerCommunication.getInstance().sendPacket(new ReduceAccountPoints(getAccountName(), val));
			return true;
		}
		return false;
	}

	private boolean _agathionResAvailable = false;

	public boolean isAgathionResAvailable()
	{
		return _agathionResAvailable;
	}

	public void setAgathionRes(boolean val)
	{
		_agathionResAvailable = val;
	}

	public boolean isClanAirShipDriver()
	{
		return isInBoat() && getBoat().isClanAirShip() && ((ClanAirShip) getBoat()).getDriver() == this;
	}

	/**
	 * _userSession - испольюзуется для хранения временных переменных.
	 */
	private Map<String, String> _userSession;

	public String getSessionVar(String key)
	{
		if(_userSession == null)
			return null;
		return _userSession.get(key);
	}

	public void setSessionVar(String key, String val)
	{
		if(_userSession == null)
			_userSession = new ConcurrentHashMap<String, String>();

		if(val == null || val.isEmpty())
			_userSession.remove(key);
		else
			_userSession.put(key, val);
	}

	public BlockList getBlockList()
	{
		return _blockList;
	}

	public FriendList getFriendList()
	{
		return _friendList;
	}

	public MenteeList getMenteeList()
	{
		return _menteeList;
	}

	public PremiumItemList getPremiumItemList()
	{
		return _premiumItemList;
	}

	public void mentoringLoginConditions()
	{
		if(getMenteeList().someOneOnline(true))
		{
			getMenteeList().notify(true);
			Mentoring.applyMentoringCond(this, true);
		}
	}

	public void mentoringLogoutConditions()
	{
		if(getMenteeList().someOneOnline(false))
		{
			getMenteeList().notify(false);
			Mentoring.applyMentoringCond(this, false);
		}
	}

	public boolean isNotShowTraders()
	{
		return _notShowTraders;
	}

	public void setNotShowTraders(boolean notShowTraders)
	{
		_notShowTraders = notShowTraders;
	}

	public boolean isDebug()
	{
		return _debug;
	}

	public void setDebug(boolean b)
	{
		_debug = b;
	}

	public void sendItemList(boolean show)
	{
		ItemInstance[] items = getInventory().getItems();
		LockType lockType = getInventory().getLockType();
		int[] lockItems = getInventory().getLockItems();

		int allSize = items.length;
		int questItemsSize = 0;
		int agathionItemsSize = 0;
		for(ItemInstance item : items)
		{
			if(item.getTemplate().isQuest())
				questItemsSize++;
			if(item.getTemplate().getAgathionEnergy() > 0)
				agathionItemsSize++;
		}

		sendPacket(new ItemListPacket(this, allSize - questItemsSize, items, show, lockType, lockItems));
		if(questItemsSize > 0)
			sendPacket(new ExQuestItemListPacket(questItemsSize, items, lockType, lockItems));
		if(agathionItemsSize > 0)
			sendPacket(new ExBR_AgathionEnergyInfo(agathionItemsSize, items));
	}

	public int getBeltInventoryIncrease()
	{
		ItemInstance item = getInventory().getPaperdollItem(Inventory.PAPERDOLL_BELT);
		if(item != null && item.getTemplate().getAttachedSkills() != null)
			for(Skill skill : item.getTemplate().getAttachedSkills())
				for(FuncTemplate func : skill.getAttachedFuncs())
					if(func._stat == Stats.INVENTORY_LIMIT)
						return (int) func._value;
		return 0;
	}

	@Override
	public boolean isPlayer()
	{
		return true;
	}

	public boolean checkCoupleAction(Player target)
	{
		if(target.getPrivateStoreType() != Player.STORE_PRIVATE_NONE)
		{
			sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IN_PRIVATE_STORE).addName(target));
			return false;
		}
		if(target.isFishing())
		{
			sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_FISHING).addName(target));
			return false;
		}
		if(target.isChaosFestivalParticipant())
		{
			sendPacket(new SystemMessagePacket(SystemMsg.C1_IS_IN_A_CHAOTIC_STATE_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION).addName(target));
			return false;
		}
		if(target.isTransformed())
		{
			sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_IN_TRANSFORM).addName(target));
			return false;
		}
		if(target.isInCombat() || target.isVisualTransformed())
		{
			sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_IN_COMBAT).addName(target));
			return false;
		}
		if(target.isCursedWeaponEquipped())
		{
			sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_CURSED_WEAPON_EQUIPED).addName(target));
			return false;
		}
		if(target.isInOlympiadMode() || getLfcGame() != null)
		{
			sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_IN_OLYMPIAD).addName(target));
			return false;
		}
		if(target.isOnSiegeField())
		{
			sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_IN_SIEGE).addName(target));
			return false;
		}
		if(target.isInBoat() || target.getMountNpcId() != 0)
		{
			sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_IN_VEHICLE_MOUNT_OTHER).addName(target));
			return false;
		}
		if(target.isTeleporting())
		{
			sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_TELEPORTING).addName(target));
			return false;
		}
		if(target.isDead())
		{
			sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_DEAD).addName(target));
			return false;
		}
		return true;
	}

	@Override
	public void startAttackStanceTask()
	{
		startAttackStanceTask0();
		Servitor[] servitors = getServitors();
		if(servitors.length > 0)
		{
			for(Servitor servitor : servitors)
				servitor.startAttackStanceTask0();
		}
	}

	@Override
	public void displayGiveDamageMessage(Creature target, int damage, Servitor servitorTransferedDamage, int transferedDamage, boolean crit, boolean miss, boolean shld, boolean magic)
	{
		super.displayGiveDamageMessage(target, damage, servitorTransferedDamage, transferedDamage, crit, miss, shld, magic);
		if(crit)
			if(magic)
				sendPacket(new SystemMessage(SystemMessage.MAGIC_CRITICAL_HIT).addName(this));
			else
				sendPacket(new SystemMessage(SystemMessage.C1_HAD_A_CRITICAL_HIT).addName(this));

		if(miss)
			sendPacket(new SystemMessage(SystemMessage.C1S_ATTACK_WENT_ASTRAY).addName(this));
		else if(!target.isDamageBlocked())
		{
			if(servitorTransferedDamage != null && transferedDamage > 0)
			{
				SystemMessagePacket sm = new SystemMessagePacket(SystemMsg.C1_INFLICTED_S3_DAMAGE_ON_C2_AND_S4_DAMAGE_ON_THE_DAMAGE_TRANSFER_TARGET);
				sm.addName(this);
				sm.addInteger(damage);
				sm.addName(target);
				sm.addInteger(transferedDamage);
				sm.addHpChange(target.getObjectId(), getObjectId(), -damage);
				sm.addHpChange(servitorTransferedDamage.getObjectId(), getObjectId(), -transferedDamage);
				sendPacket(sm);
			}
			else
				sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_DONE_S3_POINTS_OF_DAMAGE_TO_C2).addName(this).addName(target).addInteger(damage).addHpChange(target.getObjectId(), getObjectId(), -damage));
		}

		if(target.isPlayer())
		{
			if(shld && damage > 1)
				target.sendPacket(SystemMsg.YOUR_SHIELD_DEFENSE_HAS_SUCCEEDED);
			else if(shld && damage == 1)
				target.sendPacket(SystemMsg.YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS);
		}
	}

	@Override
	public void displayReceiveDamageMessage(Creature attacker, int damage)
	{
		if(attacker != this)
			sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_RECEIVED_S3_DAMAGE_FROM_C2).addName(this).addName(attacker).addInteger(damage).addHpChange(getObjectId(), attacker.getObjectId(), -damage));
	}

	public IntObjectMap<String> getPostFriends()
	{
		return _postFriends;
	}

	public void setPostFriends(IntObjectMap<String> val)
	{
		_postFriends = val;
	}

	public void sendReuseMessage(ItemInstance item)
	{
		TimeStamp sts = getSharedGroupReuse(item.getTemplate().getReuseGroup());
		if(sts == null || !sts.hasNotPassed())
			return;

		long timeleft = sts.getReuseCurrent();
		long hours = timeleft / 3600000;
		long minutes = (timeleft - hours * 3600000) / 60000;
		long seconds = (long) Math.ceil((timeleft - hours * 3600000 - minutes * 60000) / 1000.);

		if(hours > 0)
			sendPacket(new SystemMessagePacket(item.getTemplate().getReuseType().getMessages()[2]).addItemName(item.getTemplate().getItemId()).addInteger(hours).addInteger(minutes).addInteger(seconds));
		else if(minutes > 0)
			sendPacket(new SystemMessagePacket(item.getTemplate().getReuseType().getMessages()[1]).addItemName(item.getTemplate().getItemId()).addInteger(minutes).addInteger(seconds));
		else
			sendPacket(new SystemMessagePacket(item.getTemplate().getReuseType().getMessages()[0]).addItemName(item.getTemplate().getItemId()).addInteger(seconds));
	}

	public void ask(ConfirmDlgPacket dlg, OnAnswerListener listener)
	{
		if(_askDialog != null)
			return;
		int rnd = Rnd.nextInt();
		_askDialog = new ImmutablePair<Integer, OnAnswerListener>(rnd, listener);
		dlg.setRequestId(rnd);
		sendPacket(dlg);
	}

	public Pair<Integer, OnAnswerListener> getAskListener(boolean clear)
	{
		if(!clear)
			return _askDialog;
		else
		{
			Pair<Integer, OnAnswerListener> ask = _askDialog;
			_askDialog = null;
			return ask;
		}
	}

	@Override
	public boolean isDead()
	{
		return (isInOlympiadMode() || isInDuel() || getLfcGame() != null) ? getCurrentHp() <= 1. : super.isDead();
	}

	@Override
	public int getAgathionEnergy()
	{
		ItemInstance item = getInventory().getPaperdollItem(Inventory.PAPERDOLL_LBRACELET);
		return item == null ? 0 : item.getAgathionEnergy();
	}

	@Override
	public void setAgathionEnergy(int val)
	{
		ItemInstance item = getInventory().getPaperdollItem(Inventory.PAPERDOLL_LBRACELET);
		if(item == null)
			return;
		item.setAgathionEnergy(val);
		item.setJdbcState(JdbcEntityState.UPDATED);

		sendPacket(new ExBR_AgathionEnergyInfo(1, item));
	}

	public boolean hasPrivilege(Privilege privilege)
	{
		return _clan != null && (getClanPrivileges() & privilege.mask()) == privilege.mask();
	}

	public MatchingRoom getMatchingRoom()
	{
		return _matchingRoom;
	}

	public void setMatchingRoom(MatchingRoom matchingRoom)
	{
		_matchingRoom = matchingRoom;
	}

	public void dispelBuffs()
	{
		for(Effect e : getEffectList().getEffects())
			if(e.isOffensive() && !e.getSkill().isNewbie() && e.isCancelable() && !e.getSkill().isPreservedOnDeath() && !isSpecialEffect(e.getSkill()))
			{
				sendPacket(new SystemMessagePacket(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(e.getSkill().getId(), e.getSkill().getLevel()));
				e.exit();
			}

		Servitor[] servitors = getServitors();
		if(servitors.length > 0)
		{
			for(Servitor servitor : servitors)
			{
				for(Effect e : servitor.getEffectList().getEffects())
				{
					if(!e.isOffensive() && !e.getSkill().isNewbie() && e.isCancelable() && !e.getSkill().isPreservedOnDeath() && !servitor.isSpecialEffect(e.getSkill()))
						e.exit();
				}
			}
		}
	}

	public void setInstanceReuse(int id, long time)
	{
		final SystemMessage msg = new SystemMessage(SystemMessage.INSTANT_ZONE_FROM_HERE__S1_S_ENTRY_HAS_BEEN_RESTRICTED_YOU_CAN_CHECK_THE_NEXT_ENTRY_POSSIBLE).addString(getName());
		sendPacket(msg);
		_instancesReuses.put(id, time);
		mysql.set("REPLACE INTO character_instances (obj_id, id, reuse) VALUES (?,?,?)", getObjectId(), id, time);
	}

	public void removeInstanceReuse(int id)
	{
		if(_instancesReuses.remove(id) != null)
			mysql.set("DELETE FROM `character_instances` WHERE `obj_id`=? AND `id`=? LIMIT 1", getObjectId(), id);
	}

	public void removeAllInstanceReuses()
	{
		_instancesReuses.clear();
		mysql.set("DELETE FROM `character_instances` WHERE `obj_id`=?", getObjectId());
	}

	public void removeInstanceReusesByGroupId(int groupId)
	{
		for(int i : InstantZoneHolder.getInstance().getSharedReuseInstanceIdsByGroup(groupId))
			if(getInstanceReuse(i) != null)
				removeInstanceReuse(i);
	}

	public Long getInstanceReuse(int id)
	{
		return _instancesReuses.get(id);
	}

	public Map<Integer, Long> getInstanceReuses()
	{
		return _instancesReuses;
	}

	private void loadInstanceReuses()
	{
		Connection con = null;
		PreparedStatement offline = null;
		ResultSet rs = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			offline = con.prepareStatement("SELECT * FROM character_instances WHERE obj_id = ?");
			offline.setInt(1, getObjectId());
			rs = offline.executeQuery();
			while(rs.next())
			{
				int id = rs.getInt("id");
				long reuse = rs.getLong("reuse");
				_instancesReuses.put(id, reuse);
			}
		}
		catch(Exception e)
		{
			_log.error("", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, offline, rs);
		}
	}

	public Reflection getActiveReflection()
	{
		for(Reflection r : ReflectionManager.getInstance().getAll())
			if(r != null && ArrayUtils.contains(r.getVisitors(), getObjectId()))
				return r;
		return null;
	}

	public boolean canEnterInstance(int instancedZoneId)
	{
		InstantZone iz = InstantZoneHolder.getInstance().getInstantZone(instancedZoneId);

		if(isDead())
			return false;

		if(ReflectionManager.getInstance().size() > Config.MAX_REFLECTIONS_COUNT)
		{
			sendPacket(SystemMsg.THE_MAXIMUM_NUMBER_OF_INSTANCE_ZONES_HAS_BEEN_EXCEEDED);
			return false;
		}

		if(iz == null)
		{
			sendPacket(SystemMsg.SYSTEM_ERROR);
			return false;
		}

		if(ReflectionManager.getInstance().getCountByIzId(instancedZoneId) >= iz.getMaxChannels())
		{
			sendPacket(SystemMsg.THE_MAXIMUM_NUMBER_OF_INSTANCE_ZONES_HAS_BEEN_EXCEEDED);
			return false;
		}

		return iz.getEntryType().canEnter(this, iz);
	}

	public boolean canReenterInstance(int instancedZoneId)
	{
		InstantZone iz = InstantZoneHolder.getInstance().getInstantZone(instancedZoneId);
		if(getActiveReflection() != null && getActiveReflection().getInstancedZoneId() != instancedZoneId && getReflection() != ReflectionManager.DEFAULT)
		{
			sendPacket(SystemMsg.YOU_HAVE_ENTERED_ANOTHER_INSTANCE_ZONE_THEREFORE_YOU_CANNOT_ENTER_CORRESPONDING_DUNGEON);
			return false;
		}
		if(iz.isDispelBuffs())
			dispelBuffs();
		return iz.getEntryType().canReEnter(this, iz);
	}

	public int getBattlefieldChatId()
	{
		return _battlefieldChatId;
	}

	public void setBattlefieldChatId(int battlefieldChatId)
	{
		_battlefieldChatId = battlefieldChatId;
	}

	@Override
	public void broadCast(IStaticPacket... packet)
	{
		sendPacket(packet);
	}

	@Override
	public Iterator<Player> iterator()
	{
		return Collections.singleton(this).iterator();
	}

	public PlayerGroup getPlayerGroup()
	{
		if(getParty() != null)
		{
			if(getParty().getCommandChannel() != null)
				return getParty().getCommandChannel();
			else
				return getParty();
		}
		else
			return this;
	}

	public boolean isActionBlocked(String action)
	{
		return _blockedActions.contains(action);
	}

	public void blockActions(String... actions)
	{
		Collections.addAll(_blockedActions, actions);
	}

	public void unblockActions(String... actions)
	{
		for(String action : actions)
			_blockedActions.remove(action);
	}

	public OlympiadGame getOlympiadGame()
	{
		return _olympiadGame;
	}

	public void setOlympiadGame(OlympiadGame olympiadGame)
	{
		_olympiadGame = olympiadGame;
	}

	public OlympiadGame getOlympiadObserveGame()
	{
		return _olympiadObserveGame;
	}

	public void setOlympiadObserveGame(OlympiadGame olympiadObserveGame)
	{
		_olympiadObserveGame = olympiadObserveGame;
	}

	public void addRadar(int x, int y, int z)
	{
		sendPacket(new RadarControlPacket(0, 1, x, y, z));
	}

	public void addRadarWithMap(int x, int y, int z)
	{
		sendPacket(new RadarControlPacket(0, 2, x, y, z));
	}

	public PetitionMainGroup getPetitionGroup()
	{
		return _petitionGroup;
	}

	public void setPetitionGroup(PetitionMainGroup petitionGroup)
	{
		_petitionGroup = petitionGroup;
	}

	public int getLectureMark()
	{
		return _lectureMark;
	}

	public void setLectureMark(int lectureMark)
	{
		_lectureMark = lectureMark;
	}

	private int[] _recentProductList = null;

	public int[] getRecentProductList()
	{
		if(_recentProductList == null)
		{
			String value = getVar(RECENT_PRODUCT_LIST_VAR);
			if(value == null)
				return null;

			String[] products_str = value.split(";");
			int[] result = new int[0];
			for(int i = 0; i < products_str.length; i++)
			{
				int productId = Integer.parseInt(products_str[i]);
				if(ProductDataHolder.getInstance().getProduct(productId) == null)
					continue;

				result = ArrayUtils.add(result, productId);
			}
			_recentProductList = result;
		}
		return _recentProductList;
	}

	public void updateRecentProductList(final int productId)
	{
		if(_recentProductList == null)
		{
			_recentProductList = new int[1];
			_recentProductList[0] = productId;
		}
		else
		{
			int[] newProductList = new int[1];
			newProductList[0] = productId;
			for(int i = 0; i < _recentProductList.length; i++)
			{
				if(newProductList.length >= Config.IM_MAX_ITEMS_IN_RECENT_LIST)
					break;

				int itemId = _recentProductList[i];
				if(ArrayUtils.contains(newProductList, itemId))
					continue;

				newProductList = ArrayUtils.add(newProductList, itemId);
			}

			_recentProductList = newProductList;
		}

		String valueToUpdate = "";
		for(int itemId : _recentProductList)
		{
			valueToUpdate += itemId + ";";
		}
		setVar(RECENT_PRODUCT_LIST_VAR, valueToUpdate, -1);
	}

	@Override
	public int getINT()
	{
		return Math.max(getTemplate().getMinINT(), Math.min(getTemplate().getMaxINT(), super.getINT()));
	}

	@Override
	public int getSTR()
	{
		return Math.max(getTemplate().getMinSTR(), Math.min(getTemplate().getMaxSTR(), super.getSTR()));
	}

	@Override
	public int getCON()
	{
		return Math.max(getTemplate().getMinCON(), Math.min(getTemplate().getMaxCON(), super.getCON()));
	}

	@Override
	public int getMEN()
	{
		return Math.max(getTemplate().getMinMEN(), Math.min(getTemplate().getMaxMEN(), super.getMEN()));
	}

	@Override
	public int getDEX()
	{
		return Math.max(getTemplate().getMinDEX(), Math.min(getTemplate().getMaxDEX(), super.getDEX()));
	}

	@Override
	public int getWIT()
	{
		return Math.max(getTemplate().getMinWIT(), Math.min(getTemplate().getMaxWIT(), super.getWIT()));
	}

	public int getLUC()
	{
		int luc = (int) calcStat(Stats.STAT_LUC, getBaseStats().getLUC(), null, null);
		return Math.max(getTemplate().getMinLUC(), Math.min(getTemplate().getMaxLUC(), luc));
	}

	public int getCHA()
	{
		int cha = (int) calcStat(Stats.STAT_CHA, getBaseStats().getCHA(), null, null);
		return Math.max(getTemplate().getMinCHA(), Math.min(getTemplate().getMaxCHA(), cha));
	}
	
	public void changeClass(final int index)
	{
		if(isInDuel()) // На оффе нету сообщения.
			return;

		SystemMsg msg = checkChangeClassCondition();
		if(msg != null)
		{
			sendPacket(msg);
			return;
		}

		SubClass sub = _subClassList.getByIndex(index);
		if(sub == null)
			return;

		//TODO: Добавить отмену всех положительных (и наверное отрицательных) эффектов.
		int classId = sub.getClassId();
		int oldClassId = getActiveClassId();
		setActiveSubClass(classId, true, false);
		Skill skill = SkillTable.getInstance().getInfo(Skill.SKILL_CONFUSION, 1);
		skill.getEffects(this, this, false, false);
		// KIET: test apply mentoring condition on change class
        Mentoring.applyMentoringCond(this,true);
		sendPacket(new SystemMessage(SystemMessage.THE_TRANSFER_OF_SUB_CLASS_HAS_BEEN_COMPLETED).addClassName(oldClassId).addClassName(classId));
	}

	private SystemMsg checkChangeClassCondition()
	{
		if(getWeightPenalty() >= 3 || getInventoryLimit() * 0.8 < getInventory().getSize())
			return SystemMsg.A_SUBCLASS_CANNOT_BE_CREATED_OR_CHANGED_BECAUSE_YOU_HAVE_EXCEEDED_YOUR_INVENTORY_LIMIT;

		if(isInOlympiadMode() || isChaosFestivalParticipant()) //TODO: [Bonux] Добавить еще условия.
			return SystemMsg.THIS_TERRITORY_CAN_NOT_CHANGE_CLASS;

		if(getServitors().length > 0)
			return SystemMsg.A_SUBCLASS_MAY_NOT_BE_CREATED_OR_CHANGED_WHILE_A_SERVITOR_OR_PET_IS_SUMMONED;

		if(isTransformed())
			return SystemMsg.YOU_CAN_NOT_CHANGE_CLASS_IN_TRANSFORMATION;

		return null;
	}

	public JumpTrack getCurrentJumpTrack()
	{
		return _currentJumpTrack;
	}

	public void setCurrentJumpTrack(JumpTrack val)
	{
		_currentJumpTrack = val;
	}

	public JumpWay getCurrentJumpWay()
	{
		return _currentJumpWay;
	}

	public void setCurrentJumpWay(JumpWay val)
	{
		_currentJumpWay = val;
	}

	public boolean isInJumping()
	{
		return _currentJumpTrack != null;
	}

	public void onJumpingBreak()
	{
		sendActionFailed();
		unsetVar("@safe_jump_loc");
		setCurrentJumpTrack(null);
		setCurrentJumpWay(null);
		unblock();
		_jumpState = JumpState.FINISHED;
	}

	public BookMarkList getBookMarkList()
	{
		return _bookmarks;
	}

	public AntiFlood getAntiFlood()
	{
		return _antiFlood;
	}

	@Override
	public boolean isDisabledAnalogSkill(int skillId)
	{
		return _disabledAnalogSkills.contains(skillId);
	}

	@Override
	public void disableAnalogSkills(Skill skill)
	{
		if(!skill.haveAnalogSkills())
			return;

		for(int removeSkillId : skill.getAnalogSkillIDs())
		{
			removeSkillById(removeSkillId);
			_disabledAnalogSkills.add(removeSkillId);
		}
	}

	@Override
	public void removeDisabledAnalogSkills(Skill skill)
	{
		if(!skill.haveAnalogSkills())
			return;

		boolean analogSkillsRemoved = false;
		for(int analogSkillId : skill.getAnalogSkillIDs())
		{
			_disabledAnalogSkills.remove(analogSkillId);
			analogSkillsRemoved = true;
		}

		if(analogSkillsRemoved)
			rewardSkills(true, true, false, false);
	}

	public int getNpcDialogEndTime()
	{
		return _npcDialogEndTime;
	}

	public void setNpcDialogEndTime(int val)
	{
		_npcDialogEndTime = val;
	}

	@Override
	public boolean useItem(ItemInstance item, boolean ctrlPressed)
	{
		if(item == null)
			return false;

		ItemTemplate template = item.getTemplate();
		IItemHandler handler = template.getHandler();
		if(handler == null)
		{
			//_log.warn("Fail while use item. Not found handler for item ID[" + item.getItemId() + "]!");
			return false;
		}

		boolean success = handler.useItem(this, item, ctrlPressed);
		if(success)
		{
			long nextTimeUse = template.getReuseType().next(item);
			if(nextTimeUse > System.currentTimeMillis())
			{
				TimeStamp timeStamp = new TimeStamp(item.getItemId(), nextTimeUse, template.getReuseDelay());
				this.addSharedGroupReuse(template.getReuseGroup(), timeStamp);

				if(template.getReuseDelay() > 0)
					this.sendPacket(new ExUseSharedGroupItem(template.getDisplayReuseGroup(), timeStamp));
			}
		}
		return success;
	}

	public int getSkillsElementID()
	{
		return (int) calcStat(Stats.SKILLS_ELEMENT_ID, -1, null, null);
	}

	public int getAvailableSummonPoints()
	{
		int usedSummonPoints = 0;
		for(SummonInstance summon : getSummons())
			usedSummonPoints += summon.getSummonPoints();
		return getMaxSummonPoints() - usedSummonPoints;
	}

	public int getMaxSummonPoints()
	{
		return (int) calcStat(Stats.SUMMON_POINTS, 0, null, null);
	}

	public int getUsedSummonPoints()
	{
		return getMaxSummonPoints() - getAvailableSummonPoints();
	}

	public Location getStablePoint()
	{
		return _stablePoint;
	}

	public void setStablePoint(Location point)
	{
		_stablePoint = point;
	}

	public boolean isInSameParty(Player target)
	{
		return getParty() != null && target.getParty() != null && getParty() == target.getParty();
	}

	public boolean isInSameChannel(Player target)
	{
		Party activeCharP = getParty();
		Party targetP = target.getParty();
		if(activeCharP != null && targetP != null)
		{
			CommandChannel chan = activeCharP.getCommandChannel();
			if(chan != null && chan == targetP.getCommandChannel())
				return true;
		}
		return false;
	}

	public boolean isInSameClan(Player target)
	{
		return getClanId() != 0 && getClanId() == target.getClanId();
	}

	public final boolean isInSameAlly(Player target)
	{
		return getAllyId() != 0 && getAllyId() == target.getAllyId();
	}

	public boolean hasCTFflag()
	{
		return _hasFlagCTF;
	}

	public void setCTFflag(boolean set)
	{
		_hasFlagCTF = set;
	}

	//pvp events variables
	@Override
	public boolean isInTvT()
	{
		return _InTvT;
	}

	@Override
	public boolean isInCtF()
	{
		return _inCtF;
	}

	@Override
	public boolean isInLastHero() 
	{
		return _inLastHero;
	}

	public boolean isInPvPEvent() 
	{
		return !_InTvT && !_inCtF && !_inLastHero ? false : true; 
	}

	public void setIsInTvT(boolean param)
	{
		_InTvT = param;
	}

	public void setIsInCtF(boolean param) 
	{
		_inCtF = param;
	}

	public void setIsInLastHero(boolean param) 
	{
		_inLastHero = param;
	}

	public TIntObjectMap<SkillChain> getSkillChainDetails()
	{
		return _skillChainDetail;
	}

	public void removeChainDetail(int i)
	{
		if(!_skillChainDetail.isEmpty() && _skillChainDetail.containsKey(i))
		{
			SkillChain sco = _skillChainDetail.remove(i);
			if(sco != null)
			{
				if(getKnownSkill(sco.getCastingSkill()) != null)
				{
					removeUnActiveSkill(getKnownSkill(sco.getCastingSkill()));
					removeUnActiveSkill(getKnownSkill(getKnownSkill(sco.getCastingSkill()).getChainSkillId()));
				}
			}
			sendUserInfo(true);
			sendPacket(new ExSubjobInfo(this, false));
			sendPacket(new AcquireSkillListPacket(this));
		}
	}

	public void addChainDetail(Creature target, Skill skill, int duration)
	{
		_skillChainDetail.put(skill.getChainIndex(), new SkillChain(this, target,skill.getId(),skill.getChainSkillId()));

		addUnActiveSkill(skill);
		addUnActiveSkill(getKnownSkill(skill.getChainSkillId()));
		sendPacket(new ExAlterSkillRequest(14612 + skill.getChainIndex(), skill.getChainSkillId(), duration));
		sendUserInfo(true);
		sendPacket(new ExSubjobInfo(this, false));
		sendPacket(new AcquireSkillListPacket(this));
	}

	public boolean isRelatedTo(Creature character)
	{
		if(character == this)
			return true;

		if(character.isServitor()) 
		{
			if(isMyServitor(character.getObjectId()))
				return true;
			else if(character.getPlayer() != null)
			{
				Player Spc = character.getPlayer();
				if(isInSameParty(Spc) || isInSameChannel(Spc) || isInSameClan(Spc) || isInSameAlly(Spc))
					return true;
			}
		}
		else if(character.isPlayer())
		{
			Player pc = character.getPlayer();
			if(isInSameParty(pc) || isInSameChannel(pc) || isInSameClan(pc) || isInSameAlly(pc))
				return true;
		}
		return false;
	}

	public boolean isAutoSearchParty()
	{
		return _autoSearchParty;
	}

	public void enableAutoSearchParty()
	{
		_autoSearchParty = true;
		PartySubstituteManager.getInstance().addWaitingPlayer(this);
		sendPacket(ExWaitWaitingSubStituteInfo.OPEN);
	}

	public void disablePartySearch(boolean disableFlag)
	{
		if(_autoSearchParty)
		{
			PartySubstituteManager.getInstance().removeWaitingPlayer(this);
			sendPacket(ExWaitWaitingSubStituteInfo.CLOSE);
			_autoSearchParty = !disableFlag;
		}
	}

	public boolean refreshPartySearchStatus(boolean sendMsg)
	{
		if(!mayPartySearch(false,sendMsg))
		{
			disablePartySearch(false);
			return false;
		}

		if(isAutoSearchParty())
		{
			enableAutoSearchParty();
			return true;
		}
		return false;
	}

	public boolean mayPartySearch(boolean first, boolean msg)
	{
		if(getParty() != null)
			return false;

		if(isPK())
		{
			if(msg)
			{
				if(first)
					sendPacket(SystemMsg.WAITING_LIST_REGISTRATION_IS_NOT_ALLOWED_WHILE_THE_CURSED_SWORD_IS_BEING_USED_OR_THE_STATUS_IS_IN_A_CHAOTIC_STATE);
				else
					sendPacket(SystemMsg.WAITING_LIST_REGISTRATION_IS_CANCELLED_BECAUSE_THE_CURSED_SWORD_IS_BEING_USED_OR_THE_STATUS_IS_IN_A_CHAOTIC_STATE);
			}
			return false; 
		}

		if(isInDuel() && getTeam() != TeamType.NONE)
		{
			if(msg)
			{
				if(first)
					sendPacket(SystemMsg.YOU_CANNOT_REGISTER_IN_THE_WAITING_LIST_DURING_A_DUEL);
				else
					sendPacket(SystemMsg.WAITING_LIST_REGISTRATION_IS_CANCELLED_BECAUSE_YOU_ARE_IN_A_DUEL);
			}
			return false;
		}

		if(isInOlympiadMode())
		{
			if(msg)
			{
				if(first)
					sendPacket(SystemMsg.YOU_CANNOT_REGISTER_IN_THE_WAITING_LIST_WHILE_PARTICIPATING_IN_OLYMPIAD);
				else
					sendPacket(SystemMsg.WAITING_LIST_REGISTRATION_IS_CANCELLED_BECAUSE_YOU_ARE_CURRENTLY_PARTICIPATING_IN_OLYMPIAD);
			}
			return false;
		}

		if(isOnSiegeField())
		{
			if(msg && first)
				sendPacket(SystemMsg.YOU_CANNOT_REGISTER_IN_THE_WAITING_LIST_WHILE_BEING_INSIDE_OF_A_BATTLEGROUND_CASTLE_SIEGEFORTRESS_SIEGETERRITORY_WAR);

			return false;
		}

		if(isInCombatZone() || getReflectionId() != 0)
		{
			if(msg && first)
				sendPacket(SystemMsg.YOU_CANNOT_REGISTER_IN_THE_WAITING_LIST_WHILE_PARTICIPATING_IN_BLOCK_CHECKERCOLISEUMKRATEIS_CUBE);

			return false;
		}

		if(isInZone(ZoneType.no_escape) || isInZone(ZoneType.epic))
			return false;
			
		if(!Config.ENABLE_PARTY_SEARCH)
			return false;
		return true;
	}

	public void startSubstituteTask()
	{
		if(!isPartySubstituteStarted())
		{
			_substituteTask = PartySubstituteManager.getInstance().SubstituteSearchTask(this);
			sendUserInfo();
			if(isInParty())
				getParty().getPartyLeader().sendPacket(new PartySmallWindowUpdatePacket(this));
		}
	}

	public void stopSubstituteTask()
	{
		if(isPartySubstituteStarted())
		{
			PartySubstituteManager.getInstance().removePartyMember(this);
			_substituteTask.cancel(true);
			sendUserInfo();
			if(isInParty())
				getParty().getPartyLeader().sendPacket(new PartySmallWindowUpdatePacket(this));
		}
	}

	public boolean isPartySubstituteStarted()
	{
		return getParty() != null && _substituteTask != null && !_substituteTask.isDone() && !_substituteTask.isCancelled();
	}

	@Override
	public int getSkillLevel(Integer skillId)
	{
		switch(skillId)
		{
			case 1566:
			case 1567:
			case 1568:
			case 1569:
				return 1;
			case 14612:
			case 14613:
			case 14614:
				return !getSkillChainDetails().isEmpty() && getSkillChainDetails().containsKey(Math.abs(14612 - skillId)) ? 1 : -1;

		}
		return super.getSkillLevel(skillId);
	}

	public SymbolInstance getSymbol()
	{
		return _symbol;
	}

	public void setSymbol(SymbolInstance symbol)
	{
		_symbol = symbol;
	}

	public void setRegisteredInEvent(boolean inEvent)
	{
		_registeredInEvent = inEvent;
	}

	public boolean isRegisteredInEvent()
	{
		return _registeredInEvent;
	}

	public boolean checkActiveToggleEffects()
	{
		boolean dispelled = false;
		for(Effect effect : getEffectList().getEffects())
		{
			Skill skill = effect.getSkill();
			if(skill == null)
				continue;

			if(!skill.isToggle())
				continue;

			if(getAllSkills().contains(skill))
				continue;

			effect.exit();
		}
		return dispelled;
	}

	public JumpState getJumpState()
	{
		return _jumpState;
	}

	public void setJumpState(JumpState value)
	{
		_jumpState = value;
	}
	
	public long getDestructionCount()
	{
		return _destructionCount;
	}
	
	public void addDestructionCount(long count)
	{
		_destructionCount += count;
	}

	public long getMarkEndureCount()
	{
		return _markEndureCount;
	}
	
	public void addMarkEndureCount(long count)
	{
		_markEndureCount += count;
	}

	public void updateStat(CategoryType categoryType, int subCategory, long valueAdd)
	{
		WorldStatisticsManager.getInstance().updateStat(this, categoryType, subCategory, valueAdd);
	}

	public void updateStat(CategoryType categoryType, long valueAdd)
	{
		WorldStatisticsManager.getInstance().updateStat(this, categoryType, valueAdd);
	}

	@Override
	public Servitor getServitorForTransfereDamage(double transferDamage)
	{
		SummonInstance[] summons = getSummons();
		if(summons.length > 0)
		{
			for(Servitor servitor : summons)
			{
				if(servitor == null || servitor.isDead() || servitor.getCurrentHp() < transferDamage)
					continue; //try next summmon

				if(servitor.isInRangeZ(this, 1200))
					return servitor;
			}

			getEffectList().stopEffects(EffectType.AbsorbDamageToSummon);
		}
		return null;
	}

	@Override
	public double getDamageForTransferToServitor(double damage)
	{
		final double transferToSummonDam = calcStat(Stats.TRANSFER_TO_SUMMON_DAMAGE_PERCENT, 0.);
		if(transferToSummonDam > 0)
			return (damage * transferToSummonDam) * .01;
		return 0.;
	}

	public boolean canFixedRessurect()
	{
		if(getPlayerAccess().ResurectFixed)
			return true;

		if(!isOnSiegeField())
		{
			if(getInventory().getCountOf(10649) > 0)
				return true;
			if(getInventory().getCountOf(13300) > 0)
				return true;
		}
		else
		{
			int level = getLevel();
			if(level <= 19 && getInventory().getCountOf(8515) > 0)
				return true;

			if(level <= 39 && getInventory().getCountOf(8516) > 0)
				return true;

			if(level <= 51 && getInventory().getCountOf(8517) > 0)
				return true;

			if(level <= 60 && getInventory().getCountOf(8518) > 0)
				return true;

			if(level <= 75 && getInventory().getCountOf(8519) > 0)
				return true;

			if(level <= 84 && getInventory().getCountOf(8520) > 0)
				return true;
		}

		return false;
	}

	@Override
	public double getLevelBonus()
	{
		if(getTransform() != null && getTransform().getLevelBonus(getLevel()) > 0)
			return getTransform().getLevelBonus(getLevel());

		return super.getLevelBonus();
	}

	@Override
	public PlayerBaseStats getBaseStats()
	{
		if(_baseStats == null)
			_baseStats = new PlayerBaseStats(this);
		return (PlayerBaseStats) _baseStats;
	}

	public final boolean isChaosFestivalParticipant()
	{
		ChaosFestivalEvent event = getEvent(ChaosFestivalEvent.class);
		return event != null && event.isInProgress() && event.isParticle(this);
	}

	public final boolean isRegisteredInChaosFestival()
	{
		ChaosFestivalEvent event = getEvent(ChaosFestivalEvent.class);
		return event != null && !event.isInProgress() && event.isRegistered(this);
	}

	public final String getVisibleName(Player receiver)
	{
		if(isCursedWeaponEquipped())
		{
			String cursedName = getCursedWeaponName(receiver);
			if(cursedName == null || cursedName.isEmpty())
				return getName();

			return cursedName;
		}

		if(receiver != this)
		{
			if(isChaosFestivalParticipant())
			{
				ChaosFestivalEvent event = getEvent(ChaosFestivalEvent.class);
				if(event != null)
				{
					ChaosFestivalPlayerObject member = event.getParticlePlayer(this);
					if(member != null)
						return new CustomMessage("chaos_festival.player", receiver).add(member.getId()).toString();
				}
			}
		}

		return getName();
	}

	public final String getVisibleTitle(Player receiver)
	{
		if(getPrivateStoreType() != Player.STORE_PRIVATE_NONE)
			return "";

		if(isCursedWeaponEquipped())
			return "";

		if(receiver != this)
		{
			if(isChaosFestivalParticipant())
				return "";
		}

		return getTitle();
	}

	public final boolean isPledgeVisible(Player receiver)
	{
		if(isCursedWeaponEquipped())
			return false;

		if(getPrivateStoreType() != Player.STORE_PRIVATE_NONE)
		{
			if(getReflection() == ReflectionManager.GIRAN_HARBOR)
				return false;

			if(getReflection() == ReflectionManager.PARNASSUS)
				return false;
		}

		if(receiver != this)
		{
			if(isChaosFestivalParticipant())
				return false;
		}

		return true;
	}

	public void checkAndDeleteHeroWpn()
	{
		if(isHero())
			return;

		getInventory().writeLock();
		try
		{
			for(ItemInstance item : getInventory().getItems())
			{
				if(item.isHeroItem())
					getInventory().destroyItem(item);
			}
		}
		finally
		{
			getInventory().writeUnlock();
		}
	}

	public double getEnchantChanceModifier()
	{
		return calcStat(Stats.ENCHANT_CHANCE_MODIFIER);
	}

	@Override
	public boolean isSpecialEffect(Skill skill)
	{
		if(getClan() != null && getClan().isSpecialEffect(skill))
			return true;

		int skillId = skill.getId();
		if(skillId == Skill.SKILL_TRUE_FIRE && getSkillLevel(Skill.SKILL_TRUE_FIRE) > 0)
			return true;

		if(skillId == Skill.SKILL_TRUE_WATER && getSkillLevel(Skill.SKILL_TRUE_WATER) > 0)
			return true;

		if(skillId == Skill.SKILL_TRUE_WIND && getSkillLevel(Skill.SKILL_TRUE_WIND) > 0)
			return true;

		if(skillId == Skill.SKILL_TRUE_EARTH && getSkillLevel(Skill.SKILL_TRUE_EARTH) > 0)
			return true;

		return false;
	}

	@Override
	public void removeAllSkills()
	{
		_dontRewardSkills = true;

		super.removeAllSkills();

		_dontRewardSkills = false;
	}

	public void setLastMultisellBuyTime(long val)
	{
		_lastMultisellBuyTime = val;
	}

	public long getLastMultisellBuyTime()
	{
		return _lastMultisellBuyTime;
	}

	public void setLastEnchantItemTime(long val)
	{
		_lastEnchantItemTime = val;
	}

	public long getLastEnchantItemTime()
	{
		return _lastEnchantItemTime;
	}

	public void checkLevelUpReward()
	{
		int lastRewarded = getVarInt(LVL_UP_REWARD_VAR);
		int playerLvl = getLevel();
		if(playerLvl <= lastRewarded)
			return;

		boolean rewarded = false;
		for(int i = playerLvl; i > lastRewarded; i--)
		{
			TIntLongMap items = LevelUpRewardHolder.getInstance().getRewardData(i);
			if(items == null || items.isEmpty())
				continue;

			for(TIntLongIterator iterator = items.iterator(); iterator.hasNext();)
			{
				iterator.advance();
				getPremiumItemList().add(new PremiumItem(iterator.key(), iterator.value(), ""));
				rewarded = true;
			}
		}

		if(rewarded)
			sendPacket(ExNotifyPremiumItem.STATIC);

		setVar(LVL_UP_REWARD_VAR, playerLvl);
	}

	public void checkNobleSkills()
	{
		final boolean noble = isNoble();
		for(SkillLearn sl : SkillAcquireHolder.getInstance().getAvailableMaxLvlSkills(noble ? this : null, AcquireType.NOBLESSE))
		{
			Skill skill = SkillTable.getInstance().getInfo(sl.getId(), sl.getLevel());
			if(skill == null)
				continue;

			if(noble)
			{
				if(getSkillLevel(skill.getId()) < skill.getLevel())
					addSkill(skill, true);
			}
			else
				removeSkill(skill, true);
		}
	}

	public void checkHeroSkills()
	{
		final boolean hero = isHero() && isBaseClassActive();
		for(SkillLearn sl : SkillAcquireHolder.getInstance().getAvailableMaxLvlSkills(hero ? this : null, AcquireType.HERO))
		{
			Skill skill = SkillTable.getInstance().getInfo(sl.getId(), sl.getLevel());
			if(skill == null)
				continue;

			if(hero)
			{
				if(getSkillLevel(skill.getId()) < skill.getLevel())
					addSkill(skill, true);
			}
			else
				removeSkill(skill, true);
		}
	}

	public void activateHeroSkills(boolean activate)
	{
		for(SkillLearn sl : SkillAcquireHolder.getInstance().getAvailableMaxLvlSkills(null, AcquireType.HERO))
		{
			Skill skill = SkillTable.getInstance().getInfo(sl.getId(), sl.getLevel());
			if(skill == null)
				continue;

			if(!activate)
				addUnActiveSkill(skill);
			else
				removeUnActiveSkill(skill);
		}
	}

	public void giveGMSkills()
	{
		if(!isGM())
			return;

		for(SkillLearn sl : SkillAcquireHolder.getInstance().getAvailableMaxLvlSkills(this, AcquireType.GM))
		{
			Skill skill = SkillTable.getInstance().getInfo(sl.getId(), sl.getLevel());
			if(skill == null)
				continue;

			if(getSkillLevel(skill.getId()) < skill.getLevel())
				addSkill(skill, true);
		}
	}

	private FightBattleArenaObject _fightBattleObserverArena = null;

	public FightBattleArenaObject getFightBattleObserverArena()
	{
		return _fightBattleObserverArena;
	}

	public void enterFightBattleObserverMode(int arenaID)
	{
		List<FightBattleEvent> events = EventHolder.getInstance().getEvents(FightBattleEvent.class);
		if(events.isEmpty())
			return;

		FightBattleEvent event = events.get(0);
		if(event == null || !event.isInProgress())
			return;

		FightBattleArenaObject arena = event.getArena(arenaID);
		if(arena == null || !arena.isBattleBegin())
			return;

		if(_fightBattleObserverArena != null)
		{
			_fightBattleObserverArena.onChangeObserverArena(this);
			sendPacket(ExOlympiadMatchEndPacket.STATIC);
		}

		arena.onEnterObserverMode(this);

		Location loc = arena.getInfo().getObserverLoc();

		WorldRegion observerRegion = World.getRegion(loc);
		if(observerRegion == null)
			return;

		if(!_observerMode.compareAndSet(_fightBattleObserverArena != null ? OBSERVER_STARTED : OBSERVER_NONE, OBSERVER_STARTING))
			return;

		_fightBattleObserverArena = arena;

		sendPacket(new TeleportToLocationPacket(this, loc));

		setTarget(null);
		stopMove();

		// Очищаем все видимые обьекты
		World.removeObjectsFromPlayer(this);

		setObserverRegion(observerRegion);

		block();

		// Отображаем надпись над головой
		broadcastCharInfo();

		// Меняем интерфейс
		sendPacket(new ExOlympiadModePacket(3));

		// "Телепортируемся"
		setReflection(arena.getReflection());

		sendPacket(new ExTeleportToLocationActivate(this, loc));
	}

	public void setLfcGame(LfcManager game)
	{
		lfcGame = game;
	}

	public LfcManager getLfcGame()
	{
		return lfcGame;
	}

	public void setPendingLfcEnd(boolean pending)
	{
		_pending_lfc = pending;
	}

	public boolean getPendingLfcEnd()
	{
		return _pending_lfc;
	}

	public void setPendingLfcStart(boolean start)
	{
		_pending_lfc_start = start;
	}

	public boolean getPendingLfcStart()
	{
		return _pending_lfc_start;
	}

	public void checkAndCancelLfcArena(Arenas arena)
	{
		arena.setPlayerOne(null);
		arena.setPlayerTwo(null);
	}	

	public static int _arenaIdForLogout = 0;

	public void setArenaIdForLogout(int arenaId)
	{
		_arenaIdForLogout = arenaId;
	}	

	public int getArenaIdForLogout()
	{
		return _arenaIdForLogout;
	}

	/********************
	*** Ablity system.***
	******* START *******
	********************/
	public boolean isAllowAbilities()
	{
		return isNoble() && getLevel() >= 99;
	}

	public void sendAbilitiesInfo()
	{
		sendPacket(new ExAcquireAPSkillList(this));
	}

	public void setAllowAbilitiesPoints(int value)
	{
		if(getActiveSubClass() != null)
			getActiveSubClass().setAbilitiesPoints(value);
	}

	public int getAllowAbilitiesPoints()
	{
		return getActiveSubClass() == null ? 0 : getActiveSubClass().getAbilitiesPoints();
	}

	public static long getAbilitiesRefreshPrice()
	{
		return SkillAcquireHolder.getInstance().getAbilitiesRefreshPrice();
	}

	public static int getMaxAbilitiesPoints()
	{
		return SkillAcquireHolder.getInstance().getMaxAbilitiesPoints();
	}

	public long getAbilityPointSPCost()
	{
		return SkillAcquireHolder.getInstance().getAbilitiesPointPrice(getAllowAbilitiesPoints() + 1);
	}

	public int getUsedAbilitiesPoints() // TODO: [Bonux] Оптимизировать.
	{
		int result = 0;
		for(Skill skill : getLearnedAbilitiesSkills())
			result += skill.getLevel();
		return result;
	}

	public Collection<Skill> getLearnedAbilitiesSkills()
	{
		return SkillAcquireHolder.getInstance().getLearnedSkills(this, AcquireType.ABILITY);
	}
	/********************
	*** Ablity system.***
	******* END *******
	********************/

	public int getWorldChatPoints()
	{
		if(hasBonus())
			return Math.max(0, Config.WORLD_CHAT_POINTS_PER_DAY_PA - _usedWorldChatPoints);

		return Math.max(0, Config.WORLD_CHAT_POINTS_PER_DAY - _usedWorldChatPoints);
	}

	public int getUsedWorldChatPoints()
	{
		return _usedWorldChatPoints;
	}

	public void setUsedWorldChatPoints(int value)
	{
		_usedWorldChatPoints = value;
	}

	private void checkWorldChatPoints()
	{
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 6);
		calendar.set(Calendar.MINUTE, 30);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		if(_lastAccess < calendar.getTimeInMillis() / 1000 && System.currentTimeMillis() > calendar.getTimeInMillis())
		{
			_usedWorldChatPoints = 0;
			sendPacket(new ExWorldChatCnt(this));
		}
	}

	public boolean isAwaked()
	{
		return getClassId().isOfLevel(ClassLevel.AWAKED) || getLevel() >= 85 && getClassId().isOfRace(Race.ERTHEIA) && getClassId().isOfLevel(ClassLevel.THIRD);
	}

	public void addQuestTeleportMark(int teleportId)
	{
		_questTeleportMark.add(teleportId);
	}

	public void removeQuestTeleportMark(int teleportId)
	{
		_questTeleportMark.remove(teleportId);
	}

	public boolean haveQuestTeleportMark(int teleportId)
	{
		return _questTeleportMark.contains(teleportId);
	}
	
	/*
	 * New method
	 */
	
	protected boolean _fakePlayer = false;

	public void setFakePlayer() {
		_fakePlayer = true;
	}
	
	public void unSetFakePlayer(){
		_fakePlayer = false;
	}
	
	public boolean isFakePlayer(){
		return _fakePlayer;
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
			if(isInParty() && getParty().isInDimensionalRift())
			{
				Location newCoords = DimensionalRiftManager.getInstance().getRoom(0, 0).getTeleportCoords();
				x = newCoords.x;
				y = newCoords.y;
				z = newCoords.z;
				getParty().getDimensionalRift().usedTeleport(this);
			}
		}

		//TODO: [Bonux] Check ExTeleportToLocationActivate!
		if(isPlayer() && !isFakePlayer())
		{

			sendPacket(new TeleportToLocationPacket(this, x, y, z));

			getListeners().onTeleport(x, y, z, r);

			decayMe();

			setXYZ(x, y, z);

			setReflection(r);

			// Нужно при телепорте с более высокой точки на более низкую, иначе наносится вред от "падения"
			setLastClientPosition(null);
			setLastServerPosition(null);

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
}