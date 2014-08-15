package l2s.gameserver;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import sun.dc.pr.PathDasher;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import l2s.commons.configuration.ExProperties;
import l2s.commons.net.nio.impl.SelectorConfig;
import l2s.commons.string.StringArrayUtils;
import l2s.gameserver.Shutdown;
import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.model.actor.instances.player.Bonus;
import l2s.gameserver.model.base.Experience;
import l2s.gameserver.model.base.PlayerAccess;
import l2s.gameserver.network.authcomm.ServerType;
import l2s.gameserver.utils.Clients;
import l2s.gameserver.utils.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class Config
{
	private static final Logger _log = LoggerFactory.getLogger(Config.class);

	public static final int NCPUS = Runtime.getRuntime().availableProcessors();

	/** Configuration files */
	public static final String ANTIFLOOD_CONFIG_FILE = "config/antiflood.properties";
	public static final String CUSTOM_CONFIG_FILE = "config/custom.properties";
	public static final String OTHER_CONFIG_FILE = "config/other.properties";
	public static final String RESIDENCE_CONFIG_FILE = "config/residence.properties";
	public static final String SPOIL_CONFIG_FILE = "config/spoil.properties";
	public static final String ALT_SETTINGS_FILE = "config/altsettings.properties";
	public static final String FORMULAS_CONFIGURATION_FILE = "config/formulas.properties";
	public static final String PVP_CONFIG_FILE = "config/pvp.properties";
	public static final String TELNET_CONFIGURATION_FILE = "config/telnet.properties";
	public static final String CONFIGURATION_FILE = "config/server.properties";
	public static final String AI_CONFIG_FILE = "config/ai.properties";
	public static final String GEODATA_CONFIG_FILE = "config/geodata.properties";
	public static final String EVENTS_CONFIG_FILE = "config/events.properties";
	public static final String SERVICES_FILE = "config/services.properties";
	public static final String OLYMPIAD = "config/olympiad.properties";
	public static final String DEVELOP_FILE = "config/develop.properties";
	public static final String EXT_FILE = "config/ext.properties";
	public static final String BBS_FILE = "config/bbs.properties";
	public static final String FAKE_PLAYERS_LIST = "config/fake_players.list";
	public static final String PVP_MANAGER_FILE = "config/pvp_manager.properties";

	public static final String OLYMPIAD_DATA_FILE = "config/olympiad.properties";
	
	public static final String BOT_FILE = "config/anti_bot_system.properties";
	public static final String ANUSEWORDS_CONFIG_FILE = "config/abusewords.txt";

	public static final String GM_PERSONAL_ACCESS_FILE = "config/GMAccess.xml";
	public static final String GM_ACCESS_FILES_DIR = "config/GMAccess.d/";

	//anti bot stuff
	public static boolean ENABLE_ANTI_BOT_SYSTEM;
	public static int MINIMUM_TIME_QUESTION_ASK;
	public static int MAXIMUM_TIME_QUESTION_ASK;
	public static int MINIMUM_BOT_POINTS_TO_STOP_ASKING;
	public static int MAXIMUM_BOT_POINTS_TO_STOP_ASKING;
	public static int MAX_BOT_POINTS;
	public static int MINIMAL_BOT_RATING_TO_BAN;
	public static int AUTO_BOT_BAN_JAIL_TIME;
	public static boolean ANNOUNCE_AUTO_BOT_BAN;
	public static boolean ON_WRONG_QUESTION_KICK;
	
	public static int HTM_CACHE_MODE;
	public static int SHUTDOWN_ANN_TYPE;
	/** GameServer ports */
	public static int[] PORTS_GAME;
	public static String GAMESERVER_HOSTNAME;

	public static String DATABASE_DRIVER;
	public static int DATABASE_MAX_CONNECTIONS;
	public static int DATABASE_MAX_IDLE_TIMEOUT;
	public static int DATABASE_IDLE_TEST_PERIOD;
	public static String DATABASE_URL;
	public static String DATABASE_LOGIN;
	public static String DATABASE_PASSWORD;

	// Database additional options
	public static boolean AUTOSAVE;

	public static long USER_INFO_INTERVAL;
	public static boolean BROADCAST_STATS_INTERVAL;
	public static long BROADCAST_CHAR_INFO_INTERVAL;
	
	public static int MIN_HIT_TIME;
	
	public static int SUB_START_LEVEL;
	public static int START_CLAN_LEVEL;
	public static boolean NEW_CHAR_IS_NOBLE;
	public static boolean ENABLE_L2_TOP_OVERONLINE;
	public static int L2TOP_MAX_ONLINE;
	public static int MIN_ONLINE_0_5_AM;
	public static int MAX_ONLINE_0_5_AM;
	public static int MIN_ONLINE_6_11_AM;
	public static int MAX_ONLINE_6_11_AM;
	public static int MIN_ONLINE_12_6_PM;
	public static int MAX_ONLINE_12_6_PM;
	public static int MIN_ONLINE_7_11_PM;
	public static int MAX_ONLINE_7_11_PM;
	public static int ADD_ONLINE_ON_SIMPLE_DAY;
	public static int ADD_ONLINE_ON_WEEKEND;
	public static int L2TOP_MIN_TRADERS;
	public static int L2TOP_MAX_TRADERS;
	public static int ALT_OLY_BY_SAME_BOX_NUMBER;
	public static boolean ALLOW_WORLD_STATISTIC;
	
	public static int EFFECT_TASK_MANAGER_COUNT;
//
	public static boolean TVT_REWARD_PER_KILL;
	public static int TVT_REWARD_ID_PER_KILL;
	public static long TVT_REWARD_COUNT_PER_KILL;
	public static boolean TVT_INCREASE_PVP_PER_KILL;
//

	public static int MAXIMUM_ONLINE_USERS;
	
	public static int CLAN_WAR_MINIMUM_CLAN_LEVEL;
	public static int CLAN_WAR_MINIMUM_PLAYERS_DECLARE;

	public static boolean DONTLOADSPAWN;
	public static boolean DONTLOADQUEST;
	public static int MAX_REFLECTIONS_COUNT;

	public static int SHIFT_BY;
	public static int SHIFT_BY_Z;
	public static int MAP_MIN_Z;
	public static int MAP_MAX_Z;
	
	public static boolean ENABLE_TAUTI_FREE_ENTRANCE;

	public static int ENERGY_OF_DESTRUCTION_CHANCE;
	public static int ENERGY_OF_DESTRUCTION_DOUBLE_CHANCE;
	/** ChatBan */
	public static int CHAT_MESSAGE_MAX_LEN;
	public static boolean ABUSEWORD_BANCHAT;
	public static int[] BAN_CHANNEL_LIST = new int[18];
	public static boolean ABUSEWORD_REPLACE;
	public static String ABUSEWORD_REPLACE_STRING;
	public static int ABUSEWORD_BANTIME;
	public static Pattern[] ABUSEWORD_LIST = {};
	public static boolean BANCHAT_ANNOUNCE;
	public static boolean BANCHAT_ANNOUNCE_FOR_ALL_WORLD;
	public static boolean BANCHAT_ANNOUNCE_NICK;
	public static boolean GVG_LANG;

	public static boolean ALLOW_REFFERAL_SYSTEM;

	public static int REF_SAVE_INTERVAL;
	public static boolean ENABLE_CUSTOM_STATS_SYSTEM;

	public static int MAX_REFFERALS_PER_CHAR;

	public static int MIN_ONLINE_TIME;

	public static int MIN_REFF_LEVEL;

	public static double REF_PERCENT_GIVE;
	
	public static boolean NOT_REBORN_CANNOT_DEBUFF_REBORNED;
	public static int NEW_CHANCE_FOR_NOT_REBORNED_SKILLS;

	public static boolean ALLOW_FREE_PA;
	public static boolean RANDOMIZE_FROM_PA_TABLE;
	public static int FREE_PA_BONUS_GROUP_STATIC;
	public static int FREE_PA_BONUS_TIME_STATIC;
	public static boolean FREE_PA_IS_HOURS_STATIC;
	public static boolean ENABLE_FREE_PA_NOTIFICATION;
	public static boolean DELETE_TABLE_ON_SERVER_START;

	public static boolean ALLOW_PA_EXP;
	public static boolean ALLOW_PA_SP;
	public static boolean ALLOW_PA_QUEST_DROP;
	public static boolean ALLOW_PA_QUEST_REWARD;
	public static boolean ALLOW_PA_ADENA;
	public static boolean ALLOW_PA_DROP_ITEMS;
	public static boolean ALLOW_PA_DROP_SPOIL;

	public static double RATE_PA_EXP;
	public static double RATE_PA_SP;
	public static double RATE_PA_ADENA;
	public static double RATE_PA_DROP_ITEMS;
	public static double RATE_PA_DROP_SPOIL;
	public static double RATE_PA_QUEST_DROP;
	public static double RATE_PA_QUEST_REWARD;

	//catalyst chances
	public static int CATALYST_POWER_W_D;
	public static int CATALYST_POWER_W_C;
	public static int CATALYST_POWER_W_B;
	public static int CATALYST_POWER_W_A;
	public static int CATALYST_POWER_W_S;
	public static int CATALYST_POWER_A_D;
	public static int CATALYST_POWER_A_C;
	public static int CATALYST_POWER_A_B;
	public static int CATALYST_POWER_A_A;
	public static int CATALYST_POWER_A_S;
	
	public static boolean ALT_SELL_ITEM_ONE_ADENA;
	
	public static int MAX_SIEGE_CLANS;

	public static List<Integer> ITEM_LIST = new ArrayList<Integer>();

	public static TIntSet DROP_ONLY_THIS = new TIntHashSet();
	public static boolean INCLUDE_RAID_DROP;

	public static int[] CHATFILTER_CHANNELS = new int[18];
	public static int CHATFILTER_MIN_LEVEL = 0;
	public static int CHATFILTER_WORK_TYPE = 1;

	public static boolean SAVING_SPS;
	public static boolean MANAHEAL_SPS_BONUS;

	public static int ALT_ADD_RECIPES;
	public static int ALT_MAX_ALLY_SIZE;

	public static int ALT_PARTY_DISTRIBUTION_RANGE;
	public static double[] ALT_PARTY_BONUS;
	public static boolean ALT_ALL_PHYS_SKILLS_OVERHIT;

	public static double ALT_POLE_DAMAGE_MODIFIER;

	public static double ALT_M_SIMPLE_DAMAGE_MOD;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_SIGEL;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_TIR_WARRIOR;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_OTHEL_ROGUE;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_YR_ARCHER;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_FEO_WIZZARD;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_ISS_ENCHANTER;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_WYN_SUMMONER;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_EOL_HEALER;
	//new
	public static double ALT_M_SIMPLE_DAMAGE_MOD_SIGEL_PHOENIX_KNIGHT;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_SIGEL_HELL_KNIGHT;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_SIGEL_EVAS_TEMPLAR;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_SIGEL_SHILLIEN_TEMPLAR;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_TYR_DUELIST;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_TYR_DREADNOUGHT;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_TYR_TITAN;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_TYR_GRAND_KHAVATARI;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_TYR_MAESTRO;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_TYR_DOOMBRINGER;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_OTHELL_ADVENTURER;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_OTHELL_WIND_RIDER;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_OTHELL_GHOST_HUNTER;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_OTHELL_FORTUNE_SEEKER;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_YR_SAGITTARIUS;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_YR_MOONLIGHT_SENTINEL;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_YR_GHOST_SENTINEL;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_YR_TRICKSTER;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_FEOH_ARCHMAGE;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_FEOH_SOULTAKER;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_FEOH_MYSTIC_MUSE;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_FEOH_STORM_SCREAMER;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_FEOH_SOUL_HOUND;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_ISS_HIEROPHANT;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_ISS_SWORD_MUSE;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_ISS_SPECTRAL_DANCER;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_ISS_DOMINATOR;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_ISS_DOOMCRYER;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_WYNN_ARCANA_LORD;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_WYNN_ELEMENTAL_MASTER;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_WYNN_SPECTRAL_MASTER;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_AEORE_CARDINAL;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_AEORE_EVAS_SAINT;
	public static double ALT_M_SIMPLE_DAMAGE_MOD_AEORE_SHILLIEN_SAINT;
				

	public static double ALT_P_DAMAGE_MOD;
	public static double ALT_P_DAMAGE_MOD_SIGEL;
	public static double ALT_P_DAMAGE_MOD_TIR_WARRIOR;
	public static double ALT_P_DAMAGE_MOD_OTHEL_ROGUE;
	public static double ALT_P_DAMAGE_MOD_YR_ARCHER;
	public static double ALT_P_DAMAGE_MOD_FEO_WIZZARD;
	public static double ALT_P_DAMAGE_MOD_ISS_ENCHANTER;
	public static double ALT_P_DAMAGE_MOD_WYN_SUMMONER;
	public static double ALT_P_DAMAGE_MOD_EOL_HEALER;
	//new
	public static double ALT_P_DAMAGE_MOD_SIGEL_PHOENIX_KNIGHT;
	public static double ALT_P_DAMAGE_MOD_SIGEL_HELL_KNIGHT;
	public static double ALT_P_DAMAGE_MOD_SIGEL_EVAS_TEMPLAR;
	public static double ALT_P_DAMAGE_MOD_SIGEL_SHILLIEN_TEMPLAR;
	public static double ALT_P_DAMAGE_MOD_TYR_DUELIST;
	public static double ALT_P_DAMAGE_MOD_TYR_DREADNOUGHT;
	public static double ALT_P_DAMAGE_MOD_TYR_TITAN;
	public static double ALT_P_DAMAGE_MOD_TYR_GRAND_KHAVATARI;
	public static double ALT_P_DAMAGE_MOD_TYR_MAESTRO;
	public static double ALT_P_DAMAGE_MOD_TYR_DOOMBRINGER;
	public static double ALT_P_DAMAGE_MOD_OTHELL_ADVENTURER;
	public static double ALT_P_DAMAGE_MOD_OTHELL_WIND_RIDER;
	public static double ALT_P_DAMAGE_MOD_OTHELL_GHOST_HUNTER;
	public static double ALT_P_DAMAGE_MOD_OTHELL_FORTUNE_SEEKER;
	public static double ALT_P_DAMAGE_MOD_YR_SAGITTARIUS;
	public static double ALT_P_DAMAGE_MOD_YR_MOONLIGHT_SENTINEL;
	public static double ALT_P_DAMAGE_MOD_YR_GHOST_SENTINEL;
	public static double ALT_P_DAMAGE_MOD_YR_TRICKSTER;
	public static double ALT_P_DAMAGE_MOD_FEOH_ARCHMAGE;
	public static double ALT_P_DAMAGE_MOD_FEOH_SOULTAKER;
	public static double ALT_P_DAMAGE_MOD_FEOH_MYSTIC_MUSE;
	public static double ALT_P_DAMAGE_MOD_FEOH_STORM_SCREAMER;
	public static double ALT_P_DAMAGE_MOD_FEOH_SOUL_HOUND;
	public static double ALT_P_DAMAGE_MOD_ISS_HIEROPHANT;
	public static double ALT_P_DAMAGE_MOD_ISS_SWORD_MUSE;
	public static double ALT_P_DAMAGE_MOD_ISS_SPECTRAL_DANCER;
	public static double ALT_P_DAMAGE_MOD_ISS_DOMINATOR;
	public static double ALT_P_DAMAGE_MOD_ISS_DOOMCRYER;
	public static double ALT_P_DAMAGE_MOD_WYNN_ARCANA_LORD;
	public static double ALT_P_DAMAGE_MOD_WYNN_ELEMENTAL_MASTER;
	public static double ALT_P_DAMAGE_MOD_WYNN_SPECTRAL_MASTER;
	public static double ALT_P_DAMAGE_MOD_AEORE_CARDINAL;
	public static double ALT_P_DAMAGE_MOD_AEORE_EVAS_SAINT;
	public static double ALT_P_DAMAGE_MOD_AEORE_SHILLIEN_SAINT;	
	

	public static double ALT_M_CRIT_DAMAGE_MOD;
	public static double ALT_M_CRIT_DAMAGE_MOD_SIGEL;
	public static double ALT_M_CRIT_DAMAGE_MOD_TIR_WARRIOR;
	public static double ALT_M_CRIT_DAMAGE_MOD_OTHEL_ROGUE;
	public static double ALT_M_CRIT_DAMAGE_MOD_YR_ARCHER;
	public static double ALT_M_CRIT_DAMAGE_MOD_FEO_WIZZARD;
	public static double ALT_M_CRIT_DAMAGE_MOD_ISS_ENCHANTER;
	public static double ALT_M_CRIT_DAMAGE_MOD_WYN_SUMMONER;
	public static double ALT_M_CRIT_DAMAGE_MOD_EOL_HEALER;
	//new
	public static double ALT_M_CRIT_DAMAGE_MOD_SIGEL_PHOENIX_KNIGHT;
	public static double ALT_M_CRIT_DAMAGE_MOD_SIGEL_HELL_KNIGHT;
	public static double ALT_M_CRIT_DAMAGE_MOD_SIGEL_EVAS_TEMPLAR;
	public static double ALT_M_CRIT_DAMAGE_MOD_SIGEL_SHILLIEN_TEMPLAR;
	public static double ALT_M_CRIT_DAMAGE_MOD_TYR_DUELIST;
	public static double ALT_M_CRIT_DAMAGE_MOD_TYR_DREADNOUGHT;
	public static double ALT_M_CRIT_DAMAGE_MOD_TYR_TITAN;
	public static double ALT_M_CRIT_DAMAGE_MOD_TYR_GRAND_KHAVATARI;
	public static double ALT_M_CRIT_DAMAGE_MOD_TYR_MAESTRO;
	public static double ALT_M_CRIT_DAMAGE_MOD_TYR_DOOMBRINGER;
	public static double ALT_M_CRIT_DAMAGE_MOD_OTHELL_ADVENTURER;
	public static double ALT_M_CRIT_DAMAGE_MOD_OTHELL_WIND_RIDER;
	public static double ALT_M_CRIT_DAMAGE_MOD_OTHELL_GHOST_HUNTER;
	public static double ALT_M_CRIT_DAMAGE_MOD_OTHELL_FORTUNE_SEEKER;
	public static double ALT_M_CRIT_DAMAGE_MOD_YR_SAGITTARIUS;
	public static double ALT_M_CRIT_DAMAGE_MOD_YR_MOONLIGHT_SENTINEL;
	public static double ALT_M_CRIT_DAMAGE_MOD_YR_GHOST_SENTINEL;
	public static double ALT_M_CRIT_DAMAGE_MOD_YR_TRICKSTER;
	public static double ALT_M_CRIT_DAMAGE_MOD_FEOH_ARCHMAGE;
	public static double ALT_M_CRIT_DAMAGE_MOD_FEOH_SOULTAKER;
	public static double ALT_M_CRIT_DAMAGE_MOD_FEOH_MYSTIC_MUSE;
	public static double ALT_M_CRIT_DAMAGE_MOD_FEOH_STORM_SCREAMER;
	public static double ALT_M_CRIT_DAMAGE_MOD_FEOH_SOUL_HOUND;
	public static double ALT_M_CRIT_DAMAGE_MOD_ISS_HIEROPHANT;
	public static double ALT_M_CRIT_DAMAGE_MOD_ISS_SWORD_MUSE;
	public static double ALT_M_CRIT_DAMAGE_MOD_ISS_SPECTRAL_DANCER;
	public static double ALT_M_CRIT_DAMAGE_MOD_ISS_DOMINATOR;
	public static double ALT_M_CRIT_DAMAGE_MOD_ISS_DOOMCRYER;
	public static double ALT_M_CRIT_DAMAGE_MOD_WYNN_ARCANA_LORD;
	public static double ALT_M_CRIT_DAMAGE_MOD_WYNN_ELEMENTAL_MASTER;
	public static double ALT_M_CRIT_DAMAGE_MOD_WYNN_SPECTRAL_MASTER;
	public static double ALT_M_CRIT_DAMAGE_MOD_AEORE_CARDINAL;
	public static double ALT_M_CRIT_DAMAGE_MOD_AEORE_EVAS_SAINT;
	public static double ALT_M_CRIT_DAMAGE_MOD_AEORE_SHILLIEN_SAINT;		

	public static double ALT_P_CRIT_DAMAGE_MOD;
	public static double ALT_P_CRIT_DAMAGE_MOD_SIGEL;
	public static double ALT_P_CRIT_DAMAGE_MOD_TIR_WARRIOR;
	public static double ALT_P_CRIT_DAMAGE_MOD_OTHEL_ROGUE;
	public static double ALT_P_CRIT_DAMAGE_MOD_YR_ARCHER;
	public static double ALT_P_CRIT_DAMAGE_MOD_FEO_WIZZARD;
	public static double ALT_P_CRIT_DAMAGE_MOD_ISS_ENCHANTER;
	public static double ALT_P_CRIT_DAMAGE_MOD_WYN_SUMMONER;
	public static double ALT_P_CRIT_DAMAGE_MOD_EOL_HEALER;
	//new
	public static double ALT_P_CRIT_DAMAGE_MOD_SIGEL_PHOENIX_KNIGHT;
	public static double ALT_P_CRIT_DAMAGE_MOD_SIGEL_HELL_KNIGHT;
	public static double ALT_P_CRIT_DAMAGE_MOD_SIGEL_EVAS_TEMPLAR;
	public static double ALT_P_CRIT_DAMAGE_MOD_SIGEL_SHILLIEN_TEMPLAR;
	public static double ALT_P_CRIT_DAMAGE_MOD_TYR_DUELIST;
	public static double ALT_P_CRIT_DAMAGE_MOD_TYR_DREADNOUGHT;
	public static double ALT_P_CRIT_DAMAGE_MOD_TYR_TITAN;
	public static double ALT_P_CRIT_DAMAGE_MOD_TYR_GRAND_KHAVATARI;
	public static double ALT_P_CRIT_DAMAGE_MOD_TYR_MAESTRO;
	public static double ALT_P_CRIT_DAMAGE_MOD_TYR_DOOMBRINGER;
	public static double ALT_P_CRIT_DAMAGE_MOD_OTHELL_ADVENTURER;
	public static double ALT_P_CRIT_DAMAGE_MOD_OTHELL_WIND_RIDER;
	public static double ALT_P_CRIT_DAMAGE_MOD_OTHELL_GHOST_HUNTER;
	public static double ALT_P_CRIT_DAMAGE_MOD_OTHELL_FORTUNE_SEEKER;
	public static double ALT_P_CRIT_DAMAGE_MOD_YR_SAGITTARIUS;
	public static double ALT_P_CRIT_DAMAGE_MOD_YR_MOONLIGHT_SENTINEL;
	public static double ALT_P_CRIT_DAMAGE_MOD_YR_GHOST_SENTINEL;
	public static double ALT_P_CRIT_DAMAGE_MOD_YR_TRICKSTER;
	public static double ALT_P_CRIT_DAMAGE_MOD_FEOH_ARCHMAGE;
	public static double ALT_P_CRIT_DAMAGE_MOD_FEOH_SOULTAKER;
	public static double ALT_P_CRIT_DAMAGE_MOD_FEOH_MYSTIC_MUSE;
	public static double ALT_P_CRIT_DAMAGE_MOD_FEOH_STORM_SCREAMER;
	public static double ALT_P_CRIT_DAMAGE_MOD_FEOH_SOUL_HOUND;
	public static double ALT_P_CRIT_DAMAGE_MOD_ISS_HIEROPHANT;
	public static double ALT_P_CRIT_DAMAGE_MOD_ISS_SWORD_MUSE;
	public static double ALT_P_CRIT_DAMAGE_MOD_ISS_SPECTRAL_DANCER;
	public static double ALT_P_CRIT_DAMAGE_MOD_ISS_DOMINATOR;
	public static double ALT_P_CRIT_DAMAGE_MOD_ISS_DOOMCRYER;
	public static double ALT_P_CRIT_DAMAGE_MOD_WYNN_ARCANA_LORD;
	public static double ALT_P_CRIT_DAMAGE_MOD_WYNN_ELEMENTAL_MASTER;
	public static double ALT_P_CRIT_DAMAGE_MOD_WYNN_SPECTRAL_MASTER;
	public static double ALT_P_CRIT_DAMAGE_MOD_AEORE_CARDINAL;
	public static double ALT_P_CRIT_DAMAGE_MOD_AEORE_EVAS_SAINT;
	public static double ALT_P_CRIT_DAMAGE_MOD_AEORE_SHILLIEN_SAINT;	
	
	public static double ALT_P_CRIT_DAMAGE_MOD_SIGEL_FIZ;	
	public static double ALT_P_CRIT_DAMAGE_MOD_TIR_WARRIOR_FIZ;	
	public static double ALT_P_CRIT_DAMAGE_MOD_OTHEL_ROGUE_FIZ;	
	public static double ALT_P_CRIT_DAMAGE_MOD_YR_ARCHER_FIZ;	
	public static double ALT_P_CRIT_DAMAGE_MOD_SIGEL_PHOENIX_KNIGHT_FIZ;	
	public static double ALT_P_CRIT_DAMAGE_MOD_SIGEL_HELL_KNIGHT_FIZ;	
	public static double ALT_P_CRIT_DAMAGE_MOD_SIGEL_EVAS_TEMPLAR_FIZ;	
	public static double ALT_P_CRIT_DAMAGE_MOD_SIGEL_SHILLIEN_TEMPLAR_FIZ;	
	public static double ALT_P_CRIT_DAMAGE_MOD_TYR_DUELIST_FIZ;	
	public static double ALT_P_CRIT_DAMAGE_MOD_TYR_DREADNOUGHT_FIZ;	
	public static double ALT_P_CRIT_DAMAGE_MOD_TYR_TITAN_FIZ;	
	public static double ALT_P_CRIT_DAMAGE_MOD_TYR_GRAND_KHAVATARI_FIZ;	
	public static double ALT_P_CRIT_DAMAGE_MOD_TYR_MAESTRO_FIZ;	
	public static double ALT_P_CRIT_DAMAGE_MOD_TYR_DOOMBRINGER_FIZ;	
	public static double ALT_P_CRIT_DAMAGE_MOD_OTHELL_ADVENTURER_FIZ;	
	public static double ALT_P_CRIT_DAMAGE_MOD_OTHELL_WIND_RIDER_FIZ;	
	public static double ALT_P_CRIT_DAMAGE_MOD_OTHELL_GHOST_HUNTER_FIZ;	
	public static double ALT_P_CRIT_DAMAGE_MOD_OTHELL_FORTUNE_SEEKER_FIZ;	
	
	public static double ALT_P_CRIT_DAMAGE_MOD_YR_SAGITTARIUS_FIZ;	
	public static double ALT_P_CRIT_DAMAGE_MOD_YR_MOONLIGHT_SENTINEL_FIZ;	
	public static double ALT_P_CRIT_DAMAGE_MOD_YR_GHOST_SENTINEL_FIZ;	
	public static double ALT_P_CRIT_DAMAGE_MOD_YR_TRICKSTER_FIZ;	
	public static double ALT_P_CRIT_DAMAGE_MOD_FEOH_SOUL_HOUND_FIZ;	
	public static double ALT_P_CRIT_DAMAGE_MOD_ISS_DOMINATOR_FIZ;	
	public static double ALT_P_CRIT_DAMAGE_MOD_ISS_DOOMCRYER_FIZ;	
		
	public static double ALT_P_CRIT_CHANCE_MOD;
	public static double ALT_P_CRIT_CHANCE_MOD_SIGEL;
	public static double ALT_P_CRIT_CHANCE_MOD_TIR_WARRIOR;
	public static double ALT_P_CRIT_CHANCE_MOD_OTHEL_ROGUE;
	public static double ALT_P_CRIT_CHANCE_MOD_YR_ARCHER;
	public static double ALT_P_CRIT_CHANCE_MOD_FEO_WIZZARD;
	public static double ALT_P_CRIT_CHANCE_MOD_ISS_ENCHANTER;
	public static double ALT_P_CRIT_CHANCE_MOD_WYN_SUMMONER;
	public static double ALT_P_CRIT_CHANCE_MOD_EOL_HEALER;
	//new
	public static double ALT_P_CRIT_CHANCE_MOD_SIGEL_PHOENIX_KNIGHT;
	public static double ALT_P_CRIT_CHANCE_MOD_SIGEL_HELL_KNIGHT;
	public static double ALT_P_CRIT_CHANCE_MOD_SIGEL_EVAS_TEMPLAR;
	public static double ALT_P_CRIT_CHANCE_MOD_SIGEL_SHILLIEN_TEMPLAR;
	public static double ALT_P_CRIT_CHANCE_MOD_TYR_DUELIST;
	public static double ALT_P_CRIT_CHANCE_MOD_TYR_DREADNOUGHT;
	public static double ALT_P_CRIT_CHANCE_MOD_TYR_TITAN;
	public static double ALT_P_CRIT_CHANCE_MOD_TYR_GRAND_KHAVATARI;
	public static double ALT_P_CRIT_CHANCE_MOD_TYR_MAESTRO;
	public static double ALT_P_CRIT_CHANCE_MOD_TYR_DOOMBRINGER;
	public static double ALT_P_CRIT_CHANCE_MOD_OTHELL_ADVENTURER;
	public static double ALT_P_CRIT_CHANCE_MOD_OTHELL_WIND_RIDER;
	public static double ALT_P_CRIT_CHANCE_MOD_OTHELL_GHOST_HUNTER;
	public static double ALT_P_CRIT_CHANCE_MOD_OTHELL_FORTUNE_SEEKER;
	public static double ALT_P_CRIT_CHANCE_MOD_YR_SAGITTARIUS;
	public static double ALT_P_CRIT_CHANCE_MOD_YR_MOONLIGHT_SENTINEL;
	public static double ALT_P_CRIT_CHANCE_MOD_YR_GHOST_SENTINEL;
	public static double ALT_P_CRIT_CHANCE_MOD_YR_TRICKSTER;
	public static double ALT_P_CRIT_CHANCE_MOD_FEOH_ARCHMAGE;
	public static double ALT_P_CRIT_CHANCE_MOD_FEOH_SOULTAKER;
	public static double ALT_P_CRIT_CHANCE_MOD_FEOH_MYSTIC_MUSE;
	public static double ALT_P_CRIT_CHANCE_MOD_FEOH_STORM_SCREAMER;
	public static double ALT_P_CRIT_CHANCE_MOD_FEOH_SOUL_HOUND;
	public static double ALT_P_CRIT_CHANCE_MOD_ISS_HIEROPHANT;
	public static double ALT_P_CRIT_CHANCE_MOD_ISS_SWORD_MUSE;
	public static double ALT_P_CRIT_CHANCE_MOD_ISS_SPECTRAL_DANCER;
	public static double ALT_P_CRIT_CHANCE_MOD_ISS_DOMINATOR;
	public static double ALT_P_CRIT_CHANCE_MOD_ISS_DOOMCRYER;
	public static double ALT_P_CRIT_CHANCE_MOD_WYNN_ARCANA_LORD;
	public static double ALT_P_CRIT_CHANCE_MOD_WYNN_ELEMENTAL_MASTER;
	public static double ALT_P_CRIT_CHANCE_MOD_WYNN_SPECTRAL_MASTER;
	public static double ALT_P_CRIT_CHANCE_MOD_AEORE_CARDINAL;
	public static double ALT_P_CRIT_CHANCE_MOD_AEORE_EVAS_SAINT;
	public static double ALT_P_CRIT_CHANCE_MOD_AEORE_SHILLIEN_SAINT;	
	
	public static double ALT_M_CRIT_CHANCE_MOD;
	public static double ALT_M_CRIT_CHANCE_MOD_SIGEL;
	public static double ALT_M_CRIT_CHANCE_MOD_TIR_WARRIOR;
	public static double ALT_M_CRIT_CHANCE_MOD_OTHEL_ROGUE;
	public static double ALT_M_CRIT_CHANCE_MOD_YR_ARCHER;
	public static double ALT_M_CRIT_CHANCE_MOD_FEO_WIZZARD;
	public static double ALT_M_CRIT_CHANCE_MOD_ISS_ENCHANTER;
	public static double ALT_M_CRIT_CHANCE_MOD_WYN_SUMMONER;
	public static double ALT_M_CRIT_CHANCE_MOD_EOL_HEALER;	
	//new
	public static double ALT_M_CRIT_CHANCE_MOD_SIGEL_PHOENIX_KNIGHT;
	public static double ALT_M_CRIT_CHANCE_MOD_SIGEL_HELL_KNIGHT;
	public static double ALT_M_CRIT_CHANCE_MOD_SIGEL_EVAS_TEMPLAR;
	public static double ALT_M_CRIT_CHANCE_MOD_SIGEL_SHILLIEN_TEMPLAR;
	public static double ALT_M_CRIT_CHANCE_MOD_TYR_DUELIST;
	public static double ALT_M_CRIT_CHANCE_MOD_TYR_DREADNOUGHT;
	public static double ALT_M_CRIT_CHANCE_MOD_TYR_TITAN;
	public static double ALT_M_CRIT_CHANCE_MOD_TYR_GRAND_KHAVATARI;
	public static double ALT_M_CRIT_CHANCE_MOD_TYR_MAESTRO;
	public static double ALT_M_CRIT_CHANCE_MOD_TYR_DOOMBRINGER;
	public static double ALT_M_CRIT_CHANCE_MOD_OTHELL_ADVENTURER;
	public static double ALT_M_CRIT_CHANCE_MOD_OTHELL_WIND_RIDER;
	public static double ALT_M_CRIT_CHANCE_MOD_OTHELL_GHOST_HUNTER;
	public static double ALT_M_CRIT_CHANCE_MOD_OTHELL_FORTUNE_SEEKER;
	public static double ALT_M_CRIT_CHANCE_MOD_YR_SAGITTARIUS;
	public static double ALT_M_CRIT_CHANCE_MOD_YR_MOONLIGHT_SENTINEL;
	public static double ALT_M_CRIT_CHANCE_MOD_YR_GHOST_SENTINEL;
	public static double ALT_M_CRIT_CHANCE_MOD_YR_TRICKSTER;
	public static double ALT_M_CRIT_CHANCE_MOD_FEOH_ARCHMAGE;
	public static double ALT_M_CRIT_CHANCE_MOD_FEOH_SOULTAKER;
	public static double ALT_M_CRIT_CHANCE_MOD_FEOH_MYSTIC_MUSE;
	public static double ALT_M_CRIT_CHANCE_MOD_FEOH_STORM_SCREAMER;
	public static double ALT_M_CRIT_CHANCE_MOD_FEOH_SOUL_HOUND;
	public static double ALT_M_CRIT_CHANCE_MOD_ISS_HIEROPHANT;
	public static double ALT_M_CRIT_CHANCE_MOD_ISS_SWORD_MUSE;
	public static double ALT_M_CRIT_CHANCE_MOD_ISS_SPECTRAL_DANCER;
	public static double ALT_M_CRIT_CHANCE_MOD_ISS_DOMINATOR;
	public static double ALT_M_CRIT_CHANCE_MOD_ISS_DOOMCRYER;
	public static double ALT_M_CRIT_CHANCE_MOD_WYNN_ARCANA_LORD;
	public static double ALT_M_CRIT_CHANCE_MOD_WYNN_ELEMENTAL_MASTER;
	public static double ALT_M_CRIT_CHANCE_MOD_WYNN_SPECTRAL_MASTER;
	public static double ALT_M_CRIT_CHANCE_MOD_AEORE_CARDINAL;
	public static double ALT_M_CRIT_CHANCE_MOD_AEORE_EVAS_SAINT;
	public static double ALT_M_CRIT_CHANCE_MOD_AEORE_SHILLIEN_SAINT;	
		
	public static double ALT_BLOW_DAMAGE_MOD;
	public static double ALT_BLOW_CRIT_RATE_MODIFIER;
	public static double ALT_VAMPIRIC_CHANCE;

	public static boolean ALT_REMOVE_SKILLS_ON_DELEVEL;
	public static boolean ALT_USE_BOW_REUSE_MODIFIER;

	public static double ALT_VITALITY_RATE;
	public static double ALT_VITALITY_PA_RATE;
	public static double ALT_VITALITY_CONSUME_RATE;
	public static int ALT_VITALITY_POTIONS_LIMIT;
	public static int ALT_VITALITY_POTIONS_PA_LIMIT;

	public static Calendar CASTLE_VALIDATION_DATE;
	public static int[] CASTLE_SELECT_HOURS;

	public static boolean ALT_PCBANG_POINTS_ENABLED;
	public static double ALT_PCBANG_POINTS_BONUS_DOUBLE_CHANCE;
	public static int ALT_PCBANG_POINTS_BONUS;
	public static int ALT_PCBANG_POINTS_DELAY;
	public static int ALT_PCBANG_POINTS_MIN_LVL;

	public static boolean ALT_DEBUG_ENABLED;
	public static boolean ALT_DEBUG_PVP_ENABLED;
	public static boolean ALT_DEBUG_PVP_DUEL_ONLY;
	public static boolean ALT_DEBUG_PVE_ENABLED;

	/** Thread pools size */
	public static int SCHEDULED_THREAD_POOL_SIZE;
	public static int EXECUTOR_THREAD_POOL_SIZE;

	public static boolean ENABLE_RUNNABLE_STATS;

	/** Network settings */
	public static SelectorConfig SELECTOR_CONFIG = new SelectorConfig();

	public static boolean AUTO_LOOT;
	public static boolean AUTO_LOOT_HERBS;
	public static boolean AUTO_LOOT_ONLY_ADENA;
	public static boolean AUTO_LOOT_INDIVIDUAL;
	public static boolean AUTO_LOOT_FROM_RAIDS;
	public static TIntSet AUTO_LOOT_ITEM_ID_LIST = new TIntHashSet();

	/** Auto-loot for/from players with karma also? */
	public static boolean AUTO_LOOT_PK;

	/** Character name template */
	public static String CNAME_TEMPLATE;

	public static int CNAME_MAXLEN = 32;

	/** Clan name template */
	public static String CLAN_NAME_TEMPLATE;
	public static String APASSWD_TEMPLATE;

	/** Clan title template */
	public static String CLAN_TITLE_TEMPLATE;

	/** Ally name template */
	public static String ALLY_NAME_TEMPLATE;

	/** Global chat state */
	public static boolean GLOBAL_SHOUT;
	public static boolean GLOBAL_TRADE_CHAT;
	public static int CHAT_RANGE;
	public static int SHOUT_OFFSET;

	public static int WORLD_CHAT_POINTS_PER_DAY;
	public static int WORLD_CHAT_POINTS_PER_DAY_PA;
	public static int WORLD_CHAT_POINTS_CONSUME;
	public static int WORLD_CHAT_POINTS_CONSUME_PA;
	public static int WORLD_CHAT_USE_MIN_LEVEL;
	public static int WORLD_CHAT_USE_MIN_LEVEL_PA;
	
	public static boolean BAN_FOR_CFG_USAGE;
	
	public static boolean ALLOW_TOTAL_ONLINE;
	public static boolean ALLOW_ONLINE_PARSE;
	public static int FIRST_UPDATE;
	public static int DELAY_UPDATE;

	/** For test servers - evrybody has admin rights */
	public static boolean EVERYBODY_HAS_ADMIN_RIGHTS;

	public static double ALT_RAID_RESPAWN_MULTIPLIER;

	public static boolean ALT_ALLOW_DROP_AUGMENTED;

	public static boolean ALT_GAME_UNREGISTER_RECIPE;

	/** Delay for announce SS period (in minutes) */
	public static int SS_ANNOUNCE_PERIOD;

	/** Petition manager */
	public static boolean PETITIONING_ALLOWED;
	public static int MAX_PETITIONS_PER_PLAYER;
	public static int MAX_PETITIONS_PENDING;

	/** Show mob stats/droplist to players? */
	public static boolean ALT_GAME_SHOW_DROPLIST;
	public static boolean ALT_FULL_NPC_STATS_PAGE;
	public static boolean ALLOW_NPC_SHIFTCLICK;
	public static boolean ALLOW_VOICED_COMMANDS;

	public static boolean ALT_ALLOW_SELL_COMMON;
	public static int[] ALT_DISABLED_MULTISELL;
	public static int[] ALT_SHOP_PRICE_LIMITS;
	public static int[] ALT_SHOP_UNALLOWED_ITEMS;

	public static int[] ALT_ALLOWED_PET_POTIONS;

	public static double SKILLS_CHANCE_MOD;
	public static double SKILLS_CHANCE_MIN;
	public static double SKILLS_CHANCE_POW;
	public static double SKILLS_CHANCE_CAP;
	public static boolean ALT_SAVE_UNSAVEABLE;
	public static int ALT_SAVE_EFFECTS_REMAINING_TIME;
	public static boolean ALT_SHOW_REUSE_MSG;
	public static boolean ALT_DELETE_SA_BUFFS;
	public static int SKILLS_CAST_TIME_MIN;

	/** Титул при создании чара */
	public static boolean CHAR_TITLE;
	public static String ADD_CHAR_TITLE;

	/** Таймаут на использование social action */
	public static boolean ALT_SOCIAL_ACTION_REUSE;

	/** Отключение книг для изучения скилов */
	public static boolean ALT_DISABLE_SPELLBOOKS;

	/** Alternative gameing - loss of XP on death */
	public static boolean ALT_GAME_DELEVEL;

	/** Разрешать ли на арене бои за опыт */
	public static boolean ALT_ARENA_EXP;

	public static boolean ALT_GAME_SUBCLASS_WITHOUT_QUESTS;
	public static boolean ALT_ALLOW_SUBCLASS_WITHOUT_BAIUM;
	public static int ALT_GAME_LEVEL_TO_GET_SUBCLASS;
	public static int ALT_MAX_LEVEL;
	public static int ALT_MAX_SUB_LEVEL;
	public static boolean ALT_ALLOW_AWAKE_ON_SUB_CLASS;
	public static boolean ALT_NO_LASTHIT;
	public static boolean ALT_KAMALOKA_NIGHTMARES_PREMIUM_ONLY;
	public static boolean ALT_KAMALOKA_NIGHTMARE_REENTER;
	public static boolean ALT_KAMALOKA_ABYSS_REENTER;
	public static boolean ALT_KAMALOKA_LAB_REENTER;
	public static boolean ALT_PET_HEAL_BATTLE_ONLY;

	public static boolean ALT_SIMPLE_SIGNS;
	public static int ALT_MAMMON_EXCHANGE;
	public static int ALT_MAMMON_UPGRADE;

	public static int ALT_BUFF_LIMIT;

	public static int MULTISELL_SIZE;

	public static boolean SERVICES_CHANGE_NICK_ENABLED;
	public static int SERVICES_CHANGE_NICK_PRICE;
	public static int SERVICES_CHANGE_NICK_ITEM;
	public static boolean SERVICES_CHANGE_PASSWORD;
	
	public static boolean SERVICES_CHANGE_CLAN_NAME_ENABLED;
	public static int SERVICES_CHANGE_CLAN_NAME_PRICE;
	public static int SERVICES_CHANGE_CLAN_NAME_ITEM;

	public static boolean SERVICES_CHANGE_PET_NAME_ENABLED;
	public static int SERVICES_CHANGE_PET_NAME_PRICE;
	public static int SERVICES_CHANGE_PET_NAME_ITEM;

	public static boolean SERVICES_EXCHANGE_BABY_PET_ENABLED;
	public static int SERVICES_EXCHANGE_BABY_PET_PRICE;
	public static int SERVICES_EXCHANGE_BABY_PET_ITEM;

	public static boolean SERVICES_CHANGE_SEX_ENABLED;
	public static int SERVICES_CHANGE_SEX_PRICE;
	public static int SERVICES_CHANGE_SEX_ITEM;

	public static boolean SERVICES_CHANGE_BASE_ENABLED;
	public static int SERVICES_CHANGE_BASE_PRICE;
	public static int SERVICES_CHANGE_BASE_ITEM;

	public static boolean SERVICES_SEPARATE_SUB_ENABLED;
	public static int SERVICES_SEPARATE_SUB_PRICE;
	public static int SERVICES_SEPARATE_SUB_ITEM;

	public static boolean SERVICES_CHANGE_NICK_COLOR_ENABLED;
	public static int SERVICES_CHANGE_NICK_COLOR_PRICE;
	public static int SERVICES_CHANGE_NICK_COLOR_ITEM;
	public static String[] SERVICES_CHANGE_NICK_COLOR_LIST;

	public static boolean SERVICES_BASH_ENABLED;
	public static boolean SERVICES_BASH_SKIP_DOWNLOAD;
	public static int SERVICES_BASH_RELOAD_TIME;

	public static int SERVICES_RATE_TYPE;
	public static int[] SERVICES_RATE_BONUS_PRICE;
	public static int[] SERVICES_RATE_BONUS_ITEM;
	public static int[] SERVICES_RATE_BONUS_VALUE;
	public static int[] SERVICES_RATE_BONUS_DAYS;

	public static boolean SERVICES_NOBLESS_SELL_ENABLED;
	public static int SERVICES_NOBLESS_SELL_PRICE;
	public static int SERVICES_NOBLESS_SELL_ITEM;

	public static boolean SERVICES_EXPAND_INVENTORY_ENABLED;
	public static int SERVICES_EXPAND_INVENTORY_PRICE;
	public static int SERVICES_EXPAND_INVENTORY_ITEM;
	public static int SERVICES_EXPAND_INVENTORY_MAX;

	public static boolean SERVICES_EXPAND_WAREHOUSE_ENABLED;
	public static int SERVICES_EXPAND_WAREHOUSE_PRICE;
	public static int SERVICES_EXPAND_WAREHOUSE_ITEM;

	public static boolean SERVICES_EXPAND_CWH_ENABLED;
	public static int SERVICES_EXPAND_CWH_PRICE;
	public static int SERVICES_EXPAND_CWH_ITEM;

	public static String SERVICES_SELLPETS;

	public static boolean SERVICES_OFFLINE_TRADE_ALLOW;
	public static boolean SERVICES_OFFLINE_TRADE_ALLOW_OFFSHORE;
	public static int SERVICES_OFFLINE_TRADE_MIN_LEVEL;
	public static int SERVICES_OFFLINE_TRADE_NAME_COLOR;
	public static int SERVICES_OFFLINE_TRADE_PRICE;
	public static int SERVICES_OFFLINE_TRADE_PRICE_ITEM;
	public static long SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK;
	public static boolean SERVICES_OFFLINE_TRADE_RESTORE_AFTER_RESTART;
	public static boolean SERVICES_GIRAN_HARBOR_ENABLED;
	public static boolean SERVICES_PARNASSUS_ENABLED;
	public static boolean SERVICES_PARNASSUS_NOTAX;
	public static long SERVICES_PARNASSUS_PRICE;

	public static boolean SERVICES_ALLOW_LOTTERY;
	public static int SERVICES_LOTTERY_PRIZE;
	public static int SERVICES_ALT_LOTTERY_PRICE;
	public static int SERVICES_LOTTERY_TICKET_PRICE;
	public static double SERVICES_LOTTERY_5_NUMBER_RATE;
	public static double SERVICES_LOTTERY_4_NUMBER_RATE;
	public static double SERVICES_LOTTERY_3_NUMBER_RATE;
	public static int SERVICES_LOTTERY_2_AND_1_NUMBER_PRIZE;

	public static boolean SERVICES_ALLOW_ROULETTE;
	public static long SERVICES_ROULETTE_MIN_BET;
	public static long SERVICES_ROULETTE_MAX_BET;

	public static boolean ALT_ALLOW_OTHERS_WITHDRAW_FROM_CLAN_WAREHOUSE;
	public static boolean ALT_ALLOW_CLAN_COMMAND_ONLY_FOR_CLAN_LEADER;
	public static boolean ALT_GAME_REQUIRE_CLAN_CASTLE;
	public static boolean ALT_GAME_REQUIRE_CASTLE_DAWN;
	public static boolean ALT_GAME_ALLOW_ADENA_DAWN;

	public static boolean ALLOW_IP_LOCK;
	public static boolean ALLOW_HWID_LOCK;
	public static int HWID_LOCK_MASK;	
	/** Olympiad Compitition Starting time */
	public static int ALT_OLY_START_TIME;
	/** Olympiad Compition Min */
	public static int ALT_OLY_MIN;
	/** Olympaid Comptetition Period */
	public static long ALT_OLY_CPERIOD;
	/** Olympaid Weekly Period */
	public static long ALT_OLY_WPERIOD;
	/** Olympaid Validation Period */
	public static long ALT_OLY_VPERIOD;

	public static boolean ENABLE_OLYMPIAD;
	public static boolean ENABLE_OLYMPIAD_SPECTATING;
	public static boolean ALT_OLYMPIAD_EVERY_DAY;

	public static int CLASS_GAME_MIN;
	public static int NONCLASS_GAME_MIN;

	public static int GAME_MAX_LIMIT;
	public static int GAME_CLASSES_COUNT_LIMIT;
	public static int GAME_NOCLASSES_COUNT_LIMIT;

	public static int ALT_OLY_REG_DISPLAY;
	public static int ALT_OLY_BATTLE_REWARD_ITEM;
	public static int ALT_OLY_CLASSED_RITEM_C;
	public static int ALT_OLY_NONCLASSED_RITEM_C;
	public static int ALT_OLY_COMP_RITEM;
	public static int ALT_OLY_GP_PER_POINT;
	public static int ALT_OLY_HERO_POINTS;
	public static int ALT_OLY_RANK1_POINTS;
	public static int ALT_OLY_RANK2_POINTS;
	public static int ALT_OLY_RANK3_POINTS;
	public static int ALT_OLY_RANK4_POINTS;
	public static int ALT_OLY_RANK5_POINTS;
	public static int OLYMPIAD_STADIAS_COUNT;
	public static int OLYMPIAD_BATTLES_FOR_REWARD;
	public static int OLYMPIAD_POINTS_DEFAULT;
	public static int OLYMPIAD_POINTS_WEEKLY;
	public static boolean OLYMPIAD_OLDSTYLE_STAT;
	public static int OLYMPIAD_BEGINIG_DELAY;

	public static long NONOWNER_ITEM_PICKUP_DELAY;

	/** Logging Chat Window */
	public static boolean LOG_CHAT;
	public static boolean TURN_LOG_SYSTEM;

	public static Map<Integer, PlayerAccess> gmlist = new HashMap<Integer, PlayerAccess>();

	/** Rate control */
	public static double[] RATE_XP_BY_LVL;
	public static double[] RATE_SP_BY_LVL;
	public static double RATE_QUESTS_REWARD;

	public static boolean USE_QUEST_REWARD_PENALTY_PER;
	public static int F2P_QUEST_REWARD_PENALTY_PER;
	public static TIntSet F2P_QUEST_REWARD_PENALTY_QUESTS;

	public static double RATE_QUESTS_DROP;
	public static double RATE_CLAN_REP_SCORE;
	public static int RATE_CLAN_REP_SCORE_MAX_AFFECTED;
	public static double RATE_DROP_ADENA;
	public static double RATE_DROP_ITEMS;
	public static double RATE_DROP_COMMON_ITEMS;
	public static double RATE_DROP_RAIDBOSS;
	public static double RATE_DROP_SPOIL;
	public static int[] NO_RATE_ITEMS;
	public static boolean NO_RATE_EQUIPMENT;
	public static boolean NO_RATE_KEY_MATERIAL;
	public static boolean NO_RATE_RECIPES;
	public static double RATE_DROP_SIEGE_GUARD;
	public static double RATE_MANOR;
	public static int RATE_FISH_DROP_COUNT;
	public static boolean RATE_PARTY_MIN;
	public static double RATE_HELLBOUND_CONFIDENCE;

	public static int RATE_MOB_SPAWN;
	public static int RATE_MOB_SPAWN_MIN_LEVEL;
	public static int RATE_MOB_SPAWN_MAX_LEVEL;

	/** Player Drop Rate control */
	public static boolean KARMA_DROP_GM;
	public static boolean KARMA_NEEDED_TO_DROP;
	
	public static int RATE_KARMA_LOST_STATIC;

	public static int KARMA_DROP_ITEM_LIMIT;

	public static int KARMA_RANDOM_DROP_LOCATION_LIMIT;

	public static double KARMA_DROPCHANCE_BASE;
	public static double KARMA_DROPCHANCE_MOD;
	public static double NORMAL_DROPCHANCE_BASE;
	public static int DROPCHANCE_EQUIPMENT;
	public static int DROPCHANCE_EQUIPPED_WEAPON;
	public static int DROPCHANCE_ITEM;

	public static int AUTODESTROY_ITEM_AFTER;
	public static int AUTODESTROY_PLAYER_ITEM_AFTER;

	public static int CHARACTER_DELETE_AFTER_HOURS;

	public static int PURGE_BYPASS_TASK_FREQUENCY;

	/** Datapack root directory */
	public static File DATAPACK_ROOT;
	public static File GEODATA_ROOT;

	public static double BUFFTIME_MODIFIER;
	public static int[] BUFFTIME_MODIFIER_SKILLS;
	public static double CLANHALL_BUFFTIME_MODIFIER;
	public static double SONGDANCETIME_MODIFIER;

	public static double MAXLOAD_MODIFIER;
	public static double GATEKEEPER_MODIFIER;
	public static boolean ALT_IMPROVED_PETS_LIMITED_USE;
	public static int GATEKEEPER_FREE;
	public static int CRUMA_GATEKEEPER_LVL;

	public static double ALT_CHAMPION_CHANCE1;
	public static double ALT_CHAMPION_CHANCE2;
	public static boolean ALT_CHAMPION_CAN_BE_AGGRO;
	public static boolean ALT_CHAMPION_CAN_BE_SOCIAL;
	public static int ALT_CHAMPION_TOP_LEVEL;

	public static boolean ALLOW_DISCARDITEM;
	public static boolean ALLOW_MAIL;
	public static boolean ALLOW_WAREHOUSE;
	public static boolean ALLOW_WATER;
	public static boolean ALLOW_CURSED_WEAPONS;
	public static boolean DROP_CURSED_WEAPONS_ON_KICK;
	public static boolean ALLOW_NOBLE_TP_TO_ALL;

	/** Pets */
	public static int SWIMING_SPEED;

	/** protocol revision */
	public static int MIN_PROTOCOL_REVISION;
	public static int MAX_PROTOCOL_REVISION;

	/** random animation interval */
	public static int MIN_NPC_ANIMATION;
	public static int MAX_NPC_ANIMATION;

	public static boolean USE_CLIENT_LANG;
	public static Language DEFAULT_LANG;

	/** Время запланированного на определенное время суток рестарта */
	public static String RESTART_AT_TIME;

	public static int GAME_SERVER_LOGIN_PORT;
	public static boolean GAME_SERVER_LOGIN_CRYPT;
	public static boolean RETAIL_MULTISELL_ENCHANT_TRANSFER;
	public static String GAME_SERVER_LOGIN_HOST;
	public static String INTERNAL_HOSTNAME;
	public static String EXTERNAL_HOSTNAME;

	public static boolean SERVER_SIDE_NPC_NAME;
	public static boolean SERVER_SIDE_NPC_TITLE;

	// Security
	public static boolean EX_SECOND_AUTH_ENABLED;
	public static int EX_SECOND_AUTH_MAX_ATTEMPTS;
	public static int EX_SECOND_AUTH_BAN_TIME;

	public static TIntObjectMap<int[]> ALLOW_CLASS_MASTERS_LIST = new TIntObjectHashMap<int[]>();
	public static boolean ALLOW_EVENT_GATEKEEPER;

	/** Inventory slots limits */
	public static int INVENTORY_MAXIMUM_NO_DWARF;
	public static int INVENTORY_MAXIMUM_DWARF;
	public static int INVENTORY_MAXIMUM_GM;
	public static int QUEST_INVENTORY_MAXIMUM;

	/** Warehouse slots limits */
	public static int WAREHOUSE_SLOTS_NO_DWARF;
	public static int WAREHOUSE_SLOTS_DWARF;
	public static int WAREHOUSE_SLOTS_CLAN;

	public static int FREIGHT_SLOTS;

	/** Spoil Rates */
	public static double BASE_SPOIL_RATE;
	public static double MINIMUM_SPOIL_RATE;
	public static boolean ALT_SPOIL_FORMULA;
	public static boolean SHOW_HTML_WELCOME;

	/** Manor Config */
	public static double MANOR_SOWING_BASIC_SUCCESS;
	public static double MANOR_SOWING_ALT_BASIC_SUCCESS;
	public static double MANOR_HARVESTING_BASIC_SUCCESS;
	public static int MANOR_DIFF_PLAYER_TARGET;
	public static double MANOR_DIFF_PLAYER_TARGET_PENALTY;
	public static int MANOR_DIFF_SEED_TARGET;
	public static double MANOR_DIFF_SEED_TARGET_PENALTY;

	/** Karma System Variables */
	public static int KARMA_MIN_KARMA;
	public static int KARMA_RATE_KARMA_LOST;
	public static int KARMA_LOST_BASE;
	public static int KARMA_PENALTY_START_KARMA;
	public static int KARMA_PENALTY_DURATION_DEFAULT;
	public static double KARMA_PENALTY_DURATION_INCREASE;
	public static int KARMA_DOWN_TIME_MULTIPLE;
	public static int KARMA_CRIMINAL_DURATION_MULTIPLE;

	public static int MIN_PK_TO_ITEMS_DROP;
	public static boolean DROP_ITEMS_ON_DIE;
	public static boolean DROP_ITEMS_AUGMENTED;

	public static List<Integer> KARMA_LIST_NONDROPPABLE_ITEMS = new ArrayList<Integer>();
	public static List<RaidGlobalDrop> RAID_GLOBAL_DROP = new ArrayList<RaidGlobalDrop>();	
	
	public static List<Integer> LIST_OF_SELLABLE_ITEMS = new ArrayList<Integer>();
	public static List<Integer> LIST_OF_TRABLE_ITEMS = new ArrayList<Integer>();
	
	public static int PVP_TIME;

	/** Karma Punishment */
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_SHOP;

	public static boolean REGEN_SIT_WAIT;

	public static double RATE_RAID_REGEN;
	public static double RATE_RAID_DEFENSE;
	public static double RATE_RAID_ATTACK;
	public static double RATE_EPIC_DEFENSE;
	public static double RATE_EPIC_ATTACK;
	public static int RAID_MAX_LEVEL_DIFF;
	public static boolean PARALIZE_ON_RAID_DIFF;

	public static int STARTING_LVL;
	public static int STARTING_SP;

	/** Deep Blue Mobs' Drop Rules Enabled */
	public static boolean DEEPBLUE_DROP_RULES;
	public static int DEEPBLUE_DROP_MAXDIFF;
	public static int DEEPBLUE_DROP_RAID_MAXDIFF;
	public static boolean UNSTUCK_SKILL;

	/** telnet enabled */
	public static boolean IS_TELNET_ENABLED;
	public static String TELNET_DEFAULT_ENCODING;
	public static String TELNET_PASSWORD;
	public static String TELNET_HOSTNAME;
	public static int TELNET_PORT;
	
	public static boolean ALLOW_FAKE_PEACE_ZONE_KILL;

	/** Percent CP is restore on respawn */
	public static double RESPAWN_RESTORE_CP;
	/** Percent HP is restore on respawn */
	public static double RESPAWN_RESTORE_HP;
	/** Percent MP is restore on respawn */
	public static double RESPAWN_RESTORE_MP;

	/** Maximum number of available slots for pvt stores (sell/buy) - Dwarves */
	public static int MAX_PVTSTORE_SLOTS_DWARF;
	/** Maximum number of available slots for pvt stores (sell/buy) - Others */
	public static int MAX_PVTSTORE_SLOTS_OTHER;
	public static int MAX_PVTCRAFT_SLOTS;

	public static boolean SENDSTATUS_TRADE_JUST_OFFLINE;
	public static double SENDSTATUS_TRADE_MOD;

	public static boolean ALLOW_CH_DOOR_OPEN_ON_CLICK;
	public static boolean ALT_CH_ALL_BUFFS;
	public static boolean ALT_CH_ALLOW_1H_BUFFS;
	public static boolean ALT_CH_SIMPLE_DIALOG;

	public static int CH_BID_GRADE1_MINCLANLEVEL;
	public static int CH_BID_GRADE1_MINCLANMEMBERS;
	public static int CH_BID_GRADE1_MINCLANMEMBERSLEVEL;
	public static int CH_BID_GRADE2_MINCLANLEVEL;
	public static int CH_BID_GRADE2_MINCLANMEMBERS;
	public static int CH_BID_GRADE2_MINCLANMEMBERSLEVEL;
	public static int CH_BID_GRADE3_MINCLANLEVEL;
	public static int CH_BID_GRADE3_MINCLANMEMBERS;
	public static int CH_BID_GRADE3_MINCLANMEMBERSLEVEL;
	public static double RESIDENCE_LEASE_FUNC_MULTIPLIER;
	public static double RESIDENCE_LEASE_MULTIPLIER;

	public static boolean ACCEPT_ALTERNATE_ID;
	public static int REQUEST_ID;

	public static boolean ANNOUNCE_MAMMON_SPAWN;

	public static int GM_NAME_COLOUR;
	public static boolean GM_HERO_AURA;
	public static int NORMAL_NAME_COLOUR;
	public static int CLANLEADER_NAME_COLOUR;

	/** AI */
	public static int AI_TASK_MANAGER_COUNT;
	public static long AI_TASK_ATTACK_DELAY;
	public static long AI_TASK_ACTIVE_DELAY;
	public static boolean BLOCK_ACTIVE_TASKS;
	public static boolean ALWAYS_TELEPORT_HOME;
	public static boolean RND_WALK;
	public static int RND_WALK_RATE;
	public static int RND_ANIMATION_RATE;

	public static int AGGRO_CHECK_INTERVAL;
	public static long NONAGGRO_TIME_ONTELEPORT;
	public static long NONPVP_TIME_ONTELEPORT;

	/** Maximum range mobs can randomly go from spawn point */
	public static int MAX_DRIFT_RANGE;

	/** Maximum range mobs can pursue agressor from spawn point */
	public static int MAX_PURSUE_RANGE;
	public static int MAX_PURSUE_UNDERGROUND_RANGE;
	public static int MAX_PURSUE_RANGE_RAID;

	public static boolean ALLOW_DEATH_PENALTY;
	public static int ALT_DEATH_PENALTY_CHANCE;
	public static int ALT_DEATH_PENALTY_EXPERIENCE_PENALTY;
	public static int ALT_DEATH_PENALTY_KARMA_PENALTY;

	public static boolean HIDE_GM_STATUS;
	public static boolean SHOW_GM_LOGIN;
	public static boolean SAVE_GM_EFFECTS; //Silence, gmspeed, etc...

	public static boolean AUTO_LEARN_SKILLS;
	public static boolean AUTO_LEARN_AWAKED_SKILLS;

	public static int MOVE_PACKET_DELAY;
	public static int ATTACK_PACKET_DELAY;

	public static boolean DAMAGE_FROM_FALLING;

	/** Community Board */
	public static boolean COMMUNITYBOARD_ENABLED;
	public static String BBS_DEFAULT;
	public static boolean ENABLE_CAT_NEC_FREE_FARM;

	public static boolean BBS_BUFFER_ENABLED;
	public static int BBS_BUFF_ITEM_ID;
	public static int BBS_BUFF_ITEM_COUNT;
	public static int BBS_BUFF_FREE_LVL;
	public static int BBS_BUFF_TIME;
	public static double BBS_BUFF_TIME_MOD;
	public static int MAX_BUFF_PER_SET;
	public static int BBS_BUFF_TIME_MUSIC;
	public static double BBS_BUFF_TIME_MOD_MUSIC;
	public static int BBS_BUFF_TIME_SPECIAL;
	public static double BBS_BUFF_TIME_MOD_SPECIAL;
	public static int MAX_SETS_PER_CHAR;
	public static int[][] BBS_BUFF_SET;

	public static boolean ALLOW_TELEPORT_PK;
	public static int TELEPORT_BM_COUNT_SAVE;
	public static int TELEPORT_BM_ITEM_SAVE;
	public static int TELEPORT_BM_COUNT_GO;
	public static int TELEPORT_BM_ITEM_GO;
	public static int MAX_BOOK_MARKS;
	public static boolean ALLOW_NON_STARDART_NICK_CHANGE;

	public static boolean BBS_SERVICES_ENABLED;
	public static int BBS_HAIRSTYLE_ITEM_ID;
	public static int BBS_HAIRSTYLE_ITEM_COUNT;
	public static int BBS_NAME_COLOR_ITEM_ID;
	public static int BBS_NAME_COLOR_ITEM_COUNT;

	public static boolean BBS_TELEPORT_ENABLED;
	public static int BBS_TELEPORT_DEFAULT_ITEM_ID;
	public static int BBS_TELEPORT_DEFAULT_ITEM_COUNT;

	public static int ENCHANTER_ITEM_ID;
	public static int MAX_ENCHANT;
	public static int[] ENCHANT_LEVELS;
	public static int[] ENCHANT_PRICE_WPN;
	public static int[] ENCHANT_PRICE_ARM;
	public static int[] ENCHANT_ATTRIBUTE_LEVELS;
	public static int[] ENCHANT_ATTRIBUTE_LEVELS_ARM;
	public static int[] ATTRIBUTE_PRICE_WPN;
	public static int[] ATTRIBUTE_PRICE_ARM;
	public static boolean ENCHANT_ATT_PVP;

	//new for pvpcb, conditions to open cb
	public static boolean ALLOW_PVPCB_ABNORMAL;
	public static boolean CB_DEATH;
	public static boolean CB_ACTION;
	public static boolean CB_OLY;
	public static boolean CB_CHAOS_FESTIVAL;
	public static boolean CB_FLY;
	public static boolean CB_VEICHLE;
	public static boolean CB_MOUNTED;
	public static boolean CB_CANT_MOVE;
	public static boolean CB_STORE_MODE;
	public static boolean CB_FISHING;
	public static boolean CB_TEMP_ACTION;
	public static boolean CB_DUEL;
	public static boolean CB_CURSED;
	public static boolean CB_PK;
	public static boolean CB_LEADER;
	public static boolean CB_NOBLE;
	public static boolean CB_TERITORY;
	public static boolean CB_PEACEZONE_ONLY;
	public static int PROFF_4_COST_ITEM;
	public static int OCCUPATION4_COST_ITEM;
	public static boolean ALLOW_FOURTH_OCCUPATION;
	public static boolean CAN_USE_BBS_IN_EVENTS;
	
	public static int PROFF_1_COST;
	public static int OCCUPATION1_COST_ITEM;
	public static int PROFF_2_COST;
	public static int OCCUPATION2_COST_ITEM;
	public static int PROFF_3_COST_ITEM;
	public static int OCCUPATION3_COST_ITEM;
	public static int SUB_CLASS_ITEM_ID;
	public static int SUB_CLASS_COST_ITEM;
	public static int DUAL_CLASS_ITEM_ID;
	public static int DUAL_CLASS_COST_ITEM;
	public static int NOBLESS_ITEM_ID;
	public static int NOBLESS_COST_ITEM;
	public static int PK_KARMA_ITEM_ID;
	public static int PK_KARMA_ITEM_COUNT;
	public static int PK_KARMA_REDUCE;
	public static int EXPAND_CLAN_WH_ITEM_ID;
	public static int EXPAND_CLAN_WH_ITEM_COUNT;
	public static int CLAN_WH_VALUE;
	public static int EXPAND_WH_ITEM_ID;
	public static int EXPAND_WH_ITEM_COUNT;
	public static int EXPEND_WH_VALUE;
	public static int EXPAND_INVENTORY_ITEM_ID;
	public static int EXPAND_INVENTORY_ITEM_COUNT;
	public static int EXPAND_INV_VALUE;
	public static int CHANGE_NICK_ITEM_ID;
	public static int CHANGE_NICK_ITEM_COUNT;
	public static int CHANGE_NAME_CLAN_ITEM_ID;
	public static int CHANGE_NAME_CLAN_ITEM_COUNT;
	public static int CHANGE_NICK_PET_ITEM_ID;
	public static int CHANGE_NICK_PET_ITEM_COUNT;
	public static int SERVICE_SP_ADD;
	public static int SERVICE_SP_ITEM_ID;
	public static int SERVICE_SP_ITEM_COUNT;

	//new services for bbs:
	public static boolean ALLOW_OCCUPATION;
	public static boolean ALLOW_FIRST_OCCUPATION;
	public static boolean ALLOW_SECOND_OCCUPATION;
	public static boolean ALLOW_THIRD_OCCUPATION;

	public static boolean ALLOW_SUB_CLASSES;
	public static boolean ALLOW_DUAL_CLASS;
	public static boolean ALLOW_NOBLESS;
	public static boolean ALLOW_SP_ADD;
	public static boolean ALLOW_KARMA_PK;
	public static boolean ALLOW_CHANGE_NAME;
	public static boolean ALLOW_CHANCE_PET_NAME;
	public static boolean ALLOW_CHANGE_CLAN_NAME;
	public static boolean ALLOW_EXPEND_INVENTORY;
	public static boolean ALLOW_EXPEND_WAREHOUSE;
	public static boolean ALLOW_EXPEND_CLAN_WH;
	public static boolean ALLOW_SEX_CHANGE;
	public static int CHANGE_SEX_ITEM_ID;
	public static int CHANGE_SEX_ITEM_COUNT;
	public static boolean ALLOW_HAIR_STYLE_CHANGE;
	public static boolean ALLOW_COLOR_NICK_CHANGE;
	public static int BBS_HERO_ITEM_ID;
	public static int BBS_COUNT_HERO_ITEM;
	public static long BBS_HERO_DAYS;
	public static boolean ALLOW_HERO_BUY_SERVICE;
	/** Wedding Options */
	public static boolean ALLOW_WEDDING;
	public static int WEDDING_PRICE;
	public static boolean WEDDING_PUNISH_INFIDELITY;
	public static boolean WEDDING_TELEPORT;
	public static int WEDDING_TELEPORT_PRICE;
	public static int WEDDING_TELEPORT_INTERVAL;
	public static boolean WEDDING_SAMESEX;
	public static boolean WEDDING_FORMALWEAR;
	public static int WEDDING_DIVORCE_COSTS;

	/** Augmentations **/
	public static int AUGMENTATION_NG_SKILL_CHANCE; // Chance to get a skill while using a NoGrade Life Stone
	public static int AUGMENTATION_NG_GLOW_CHANCE; // Chance to get a Glow effect while using a NoGrade Life Stone(only if you get a skill)
	public static int AUGMENTATION_MID_SKILL_CHANCE; // Chance to get a skill while using a MidGrade Life Stone
	public static int AUGMENTATION_MID_GLOW_CHANCE; // Chance to get a Glow effect while using a MidGrade Life Stone(only if you get a skill)
	public static int AUGMENTATION_HIGH_SKILL_CHANCE; // Chance to get a skill while using a HighGrade Life Stone
	public static int AUGMENTATION_HIGH_GLOW_CHANCE; // Chance to get a Glow effect while using a HighGrade Life Stone
	public static int AUGMENTATION_TOP_SKILL_CHANCE; // Chance to get a skill while using a TopGrade Life Stone
	public static int AUGMENTATION_TOP_GLOW_CHANCE; // Chance to get a Glow effect while using a TopGrade Life Stone
	public static int AUGMENTATION_BASESTAT_CHANCE; // Chance to get a BaseStatModifier in the augmentation process
	public static int AUGMENTATION_ACC_SKILL_CHANCE;

	public static int FOLLOW_RANGE;

	/** Настройки для евента Файт Клуб*/
	public static boolean FIGHT_CLUB_ENABLED;
	public static int MINIMUM_LEVEL_TO_PARRICIPATION;
	public static int MAXIMUM_LEVEL_TO_PARRICIPATION;
	public static int MAXIMUM_LEVEL_DIFFERENCE;
	public static String[] ALLOWED_RATE_ITEMS;
	public static int PLAYERS_PER_PAGE;
	public static int ARENA_TELEPORT_DELAY;
	public static boolean CANCEL_BUFF_BEFORE_FIGHT;
	public static boolean UNSUMMON_PETS;
	public static boolean UNSUMMON_SUMMONS;
	public static boolean REMOVE_CLAN_SKILLS;
	public static boolean REMOVE_HERO_SKILLS;
	public static int TIME_TO_PREPARATION;
	public static int FIGHT_TIME;
	public static boolean ALLOW_DRAW;
	public static int TIME_TELEPORT_BACK;
	public static boolean FIGHT_CLUB_ANNOUNCE_RATE;
	public static boolean FIGHT_CLUB_ANNOUNCE_RATE_TO_SCREEN;
	public static boolean FIGHT_CLUB_ANNOUNCE_START_TO_SCREEN;

	//XXX Викторина
	public static boolean VIKTORINA_ENABLED;// false;
	public static boolean VIKTORINA_REMOVE_QUESTION;//false;;
	public static boolean VIKTORINA_REMOVE_QUESTION_NO_ANSWER;//= false;
	public static int VIKTORINA_START_TIME_HOUR;// 16;
	public static int VIKTORINA_START_TIME_MIN;// 16;
	public static int VIKTORINA_WORK_TIME;//2;
	public static int VIKTORINA_TIME_ANSER;//1;
	public static int VIKTORINA_TIME_PAUSE;//1;
	
	public static boolean ALT_ITEM_AUCTION_ENABLED;
	public static boolean ALT_CUSTOM_ITEM_AUCTION_ENABLED;
	public static boolean ALT_ITEM_AUCTION_CAN_REBID;
	public static boolean ALT_ITEM_AUCTION_START_ANNOUNCE;
	public static int ALT_ITEM_AUCTION_BID_ITEM_ID;
	public static long ALT_ITEM_AUCTION_MAX_BID;
	public static int ALT_ITEM_AUCTION_MAX_CANCEL_TIME_IN_MILLIS;

	public static boolean ALT_FISH_CHAMPIONSHIP_ENABLED;
	public static int ALT_FISH_CHAMPIONSHIP_REWARD_ITEM;
	public static int ALT_FISH_CHAMPIONSHIP_REWARD_1;
	public static int ALT_FISH_CHAMPIONSHIP_REWARD_2;
	public static int ALT_FISH_CHAMPIONSHIP_REWARD_3;
	public static int ALT_FISH_CHAMPIONSHIP_REWARD_4;
	public static int ALT_FISH_CHAMPIONSHIP_REWARD_5;

	public static boolean ALT_ENABLE_BLOCK_CHECKER_EVENT;
	public static int ALT_MIN_BLOCK_CHECKER_TEAM_MEMBERS;
	public static double ALT_RATE_COINS_REWARD_BLOCK_CHECKER;
	public static boolean ALT_HBCE_FAIR_PLAY;
	public static int ALT_PET_INVENTORY_LIMIT;

	/**limits of stats **/
	public static int LIM_PATK;
	public static int LIM_MATK;
	public static int LIM_PDEF;
	public static int LIM_MDEF;
	public static int LIM_MATK_SPD;
	public static int LIM_PATK_SPD;
	public static int LIM_CRIT_DAM;
	public static int LIM_CRIT;
	public static int LIM_MCRIT;
	public static int LIM_ACCURACY;
	public static int LIM_EVASION;
	public static int LIM_MOVE;
	public static int LIM_FAME;
	public static int HP_MP_CP_LIMIT;

	public static double ALT_NPC_PATK_MODIFIER;
	public static double ALT_NPC_MATK_MODIFIER;
	public static double ALT_NPC_MAXHP_MODIFIER;
	public static double ALT_NPC_MAXMP_MODIFIER;

	public static int FESTIVAL_MIN_PARTY_SIZE;
	public static double FESTIVAL_RATE_PRICE;

	/** Dimensional Rift Config **/
	public static int RIFT_MIN_PARTY_SIZE;
	public static int RIFT_SPAWN_DELAY; // Time in ms the party has to wait until the mobs spawn
	public static int RIFT_MAX_JUMPS;
	public static int RIFT_AUTO_JUMPS_TIME;
	public static int RIFT_AUTO_JUMPS_TIME_RAND;
	public static int RIFT_ENTER_COST_RECRUIT;
	public static int RIFT_ENTER_COST_SOLDIER;
	public static int RIFT_ENTER_COST_OFFICER;
	public static int RIFT_ENTER_COST_CAPTAIN;
	public static int RIFT_ENTER_COST_COMMANDER;
	public static int RIFT_ENTER_COST_HERO;

	public static boolean ALLOW_TALK_WHILE_SITTING;

	public static boolean PARTY_LEADER_ONLY_CAN_INVITE;

	/** Разрешены ли клановые скилы? **/
	public static boolean ALLOW_CLANSKILLS;

	/** Разрешено ли изучение скилов трансформации и саб классов без наличия выполненного квеста */
	public static boolean ALLOW_LEARN_TRANS_SKILLS_WO_QUEST;

	/** Allow Manor system */
	public static boolean ALLOW_MANOR;

	/** Manor Refresh Starting time */
	public static int MANOR_REFRESH_TIME;

	/** Manor Refresh Min */
	public static int MANOR_REFRESH_MIN;

	/** Manor Next Period Approve Starting time */
	public static int MANOR_APPROVE_TIME;

	/** Manor Next Period Approve Min */
	public static int MANOR_APPROVE_MIN;

	/** Manor Maintenance Time */
	public static int MANOR_MAINTENANCE_PERIOD;

	public static double EVENT_CofferOfShadowsPriceRate;
	public static double EVENT_CofferOfShadowsRewardRate;

	public static double EVENT_APIL_FOOLS_DROP_CHANCE;

	public static double EVENT_BEER_FESTIVAL_DROP_MOD;

	/** Master Yogi event enchant config */
	public static int ENCHANT_CHANCE_MASTER_YOGI_STAFF;
	public static int ENCHANT_MAX_MASTER_YOGI_STAFF;
	public static int SAFE_ENCHANT_MASTER_YOGI_STAFF;
	
	public static boolean DISABLE_PARTY_ON_EVENT;
	
	public static int EVENT_LastHeroItemID;
	public static double EVENT_LastHeroItemCOUNT;
	public static boolean EVENT_LastHeroRate;
	public static double EVENT_LastHeroItemCOUNTFinal;
	public static boolean EVENT_LastHeroRateFinal;
	public static int EVENT_LHTime;
	public static String[] EVENT_LHStartTime;
	public static boolean EVENT_LHCategories;
	public static boolean EVENT_LHAllowSummons;
	public static boolean EVENT_LHAllowBuffs;
	public static boolean EVENT_LHAllowMultiReg;
	public static String EVENT_LHCheckWindowMethod;
	public static int EVENT_LHEventRunningTime;
	public static String[] EVENT_LHFighterBuffs;
	public static String[] EVENT_LHMageBuffs;
	public static boolean EVENT_LHBuffPlayers;
	public static boolean ALLOW_HEROES_LASTHERO;
	
	public static boolean ENABLE_LFC;
	
    public static String[] EVENT_TvTRewards;
	public static String[] EVENTS_DISALLOWED_SKILLS;
	public static int EVENT_TvTTime;
	public static boolean EVENT_TvT_rate;
	public static String[] EVENT_TvTStartTime;
	public static boolean EVENT_TvTCategories;
	public static int EVENT_TvTMaxPlayerInTeam;
	public static int EVENT_TvTMinPlayerInTeam;
	public static boolean EVENT_TvTAllowSummons;
	public static boolean EVENT_TvTAllowBuffs;
	public static boolean EVENT_TvTAllowMultiReg;
	public static String EVENT_TvTCheckWindowMethod;
	public static int EVENT_TvTEventRunningTime;
	public static String[] EVENT_TvTFighterBuffs;
	public static String[] EVENT_TvTMageBuffs;
	public static boolean EVENT_TvTBuffPlayers;
	public static boolean EVENT_TvTrate;

	public static int EVENT_CtfTime;
	public static boolean EVENT_CtFrate;
	public static String[] EVENT_CtFStartTime;
	public static boolean EVENT_CtFCategories;
	public static int EVENT_CtFMaxPlayerInTeam;
	public static int EVENT_CtFMinPlayerInTeam;
	public static boolean EVENT_CtFAllowSummons;
	public static boolean EVENT_CtFAllowBuffs;
	public static boolean EVENT_CtFAllowMultiReg;
	public static String EVENT_CtFCheckWindowMethod;
	public static String[] EVENT_CtFFighterBuffs;
	public static String[] EVENT_CtFMageBuffs;
	public static boolean EVENT_CtFBuffPlayers;
	public static String[] EVENT_CtFRewards;
	public static boolean ALLOW_PLAYER_INVIS_TAKE_FLAG_CTF;
	
	//gvg
	
	//reflect configs
	public static long REFLECT_MIN_RANGE;
	public static double REFLECT_AND_BLOCK_DAMAGE_CHANCE_CAP;
	public static double REFLECT_AND_BLOCK_PSKILL_DAMAGE_CHANCE_CAP;
	public static double REFLECT_AND_BLOCK_MSKILL_DAMAGE_CHANCE_CAP;
	public static double REFLECT_DAMAGE_PERCENT_CAP;
	public static double REFLECT_BOW_DAMAGE_PERCENT_CAP;
	public static double REFLECT_PSKILL_DAMAGE_PERCENT_CAP;
	public static double REFLECT_MSKILL_DAMAGE_PERCENT_CAP;
	
	public static int GvG_POINTS_FOR_BOX;
	public static int GvG_POINTS_FOR_BOSS;
	public static int GvG_POINTS_FOR_KILL;
	public static int GvG_POINTS_FOR_DEATH;
	public static int GvG_EVENT_TIME;
	public static long GvG_BOSS_SPAWN_TIME;
	public static int GvG_FAME_REWARD;
	public static int GvG_REWARD;
	public static long GvG_REWARD_COUNT;
	public static int GvG_ADD_IF_WITHDRAW;
	public static int GvG_HOUR_START;
	public static int GvG_MINUTE_START;
	public static int GVG_MIN_LEVEL;
	public static int GVG_MAX_LEVEL;
	public static int GVG_MAX_GROUPS;
	public static int GVG_MIN_PARTY_MEMBERS;
	public static long GVG_TIME_TO_REGISTER;

	public static double EVENT_TFH_POLLEN_CHANCE;
	public static double EVENT_GLITTMEDAL_NORMAL_CHANCE;
	public static double EVENT_GLITTMEDAL_GLIT_CHANCE;
	public static double EVENT_L2DAY_LETTER_CHANCE;
	public static double EVENT_CHANGE_OF_HEART_CHANCE;

	public static double EVENT_TRICK_OF_TRANS_CHANCE;

	public static double EVENT_MARCH8_DROP_CHANCE;
	public static double EVENT_MARCH8_PRICE_RATE;

	public static boolean EVENT_BOUNTY_HUNTERS_ENABLED;

	public static long EVENT_SAVING_SNOWMAN_LOTERY_PRICE;
	public static int EVENT_SAVING_SNOWMAN_REWARDER_CHANCE;

	public static boolean SERVICES_NO_TRADE_ONLY_OFFLINE;
	public static double SERVICES_TRADE_TAX;
	public static double SERVICES_OFFSHORE_TRADE_TAX;
	public static boolean SERVICES_OFFSHORE_NO_CASTLE_TAX;
	public static boolean SERVICES_TRADE_TAX_ONLY_OFFLINE;
	public static boolean SERVICES_TRADE_ONLY_FAR;
	public static int SERVICES_TRADE_RADIUS;
	public static int SERVICES_TRADE_MIN_LEVEL;

	public static boolean SERVICES_ENABLE_NO_CARRIER;
	public static int SERVICES_NO_CARRIER_DEFAULT_TIME;
	public static int SERVICES_NO_CARRIER_MAX_TIME;
	public static int SERVICES_NO_CARRIER_MIN_TIME;

	public static boolean ALT_SHOW_SERVER_TIME;

	/** Geodata config */
	public static int GEO_X_FIRST, GEO_Y_FIRST, GEO_X_LAST, GEO_Y_LAST;
	public static String GEOFILES_PATTERN;
	public static boolean ALLOW_GEODATA;
	public static boolean ALLOW_FALL_FROM_WALLS;
	public static boolean ALLOW_KEYBOARD_MOVE;
	public static boolean COMPACT_GEO;
	public static int CLIENT_Z_SHIFT;
	public static int MAX_Z_DIFF;
	public static int MIN_LAYER_HEIGHT;

	/** Geodata (Pathfind) config */
	public static int PATHFIND_BOOST;
	public static boolean PATHFIND_DIAGONAL;
	public static boolean PATH_CLEAN;
	public static int PATHFIND_MAX_Z_DIFF;
	public static long PATHFIND_MAX_TIME;
	public static String PATHFIND_BUFFERS;

	public static boolean DEBUG;

	public static int WEAR_DELAY;
	public static boolean ALLOW_FAKE_PLAYERS;
	public static int FAKE_PLAYERS_PERCENT;

	public static boolean ALLOW_DUELS;
	public static boolean DISABLE_CRYSTALIZATION_ITEMS;
	
	public static int[] SERVICES_ENCHANT_VALUE;
	public static int[] SERVICES_ENCHANT_COAST;
	public static int[] SERVICES_ENCHANT_RAID_VALUE;
	public static int[] SERVICES_ENCHANT_RAID_COAST;

	public static boolean GOODS_INVENTORY_ENABLED = false;
	public static boolean EX_NEW_PETITION_SYSTEM;
	public static boolean EX_JAPAN_MINIGAME;
	public static boolean EX_LECTURE_MARK;

	public static boolean AUTH_SERVER_GM_ONLY;
	public static boolean AUTH_SERVER_BRACKETS;
	public static boolean AUTH_SERVER_IS_PVP;
	public static int AUTH_SERVER_AGE_LIMIT;
	public static int AUTH_SERVER_SERVER_TYPE;

	/** Custom properties **/
	public static boolean ONLINE_GENERATOR_ENABLED;
	public static int ONLINE_GENERATOR_DELAY;

	public static boolean ALLOW_MONSTER_RACE;
	public static boolean ONLY_ONE_SIEGE_PER_CLAN;
	public static double SPECIAL_CLASS_BOW_CROSS_BOW_PENALTY;

	public static boolean ALLOW_USE_DOORMANS_IN_SIEGE_BY_OWNERS;
	
	public static boolean DISABLE_VAMPIRIC_VS_MOB_ON_PVP;

	public static boolean NPC_RANDOM_ENCHANT;
	
	public static boolean ENABLE_PARTY_SEARCH;
	public static boolean MENTOR_ONLY_PA;

	//pvp manager
	public static boolean ALLOW_PVP_REWARD;
	public static boolean PVP_REWARD_SEND_SUCC_NOTIF;
	public static int[] PVP_REWARD_REWARD_IDS;
	public static long[] PVP_REWARD_COUNTS;
	public static boolean PVP_REWARD_RANDOM_ONE;	
	public static int PVP_REWARD_DELAY_ONE_KILL;
	public static int PVP_REWARD_MIN_PL_PROFF;
	public static int PVP_REWARD_MIN_PL_UPTIME_MINUTE;
	public static int PVP_REWARD_MIN_PL_LEVEL;
	public static boolean PVP_REWARD_PK_GIVE;
	public static boolean PVP_REWARD_ON_EVENT_GIVE;
	public static boolean PVP_REWARD_ONLY_BATTLE_ZONE;
	public static boolean PVP_REWARD_ONLY_NOBLE_GIVE;
	public static boolean PVP_REWARD_SAME_PARTY_GIVE;
	public static boolean PVP_REWARD_SAME_CLAN_GIVE;
	public static boolean PVP_REWARD_SAME_ALLY_GIVE;
	public static boolean PVP_REWARD_SAME_HWID_GIVE;
	public static boolean PVP_REWARD_SAME_IP_GIVE;
	public static boolean PVP_REWARD_SPECIAL_ANTI_TWINK_TIMER;
	public static int PVP_REWARD_HR_NEW_CHAR_BEFORE_GET_ITEM;
	public static boolean PVP_REWARD_CHECK_EQUIP;
	public static int PVP_REWARD_WEAPON_GRADE_TO_CHECK;
	public static boolean PVP_REWARD_LOG_KILLS;
	public static boolean DISALLOW_MSG_TO_PL;

	public static int ALL_CHAT_USE_MIN_LEVEL;
	public static int ALL_CHAT_USE_DELAY;
	public static int SHOUT_CHAT_USE_MIN_LEVEL;
	public static int SHOUT_CHAT_USE_DELAY;
	public static int TRADE_CHAT_USE_MIN_LEVEL;
	public static int TRADE_CHAT_USE_DELAY;
	public static int HERO_CHAT_USE_MIN_LEVEL;
	public static int HERO_CHAT_USE_DELAY;
	public static int PRIVATE_CHAT_USE_MIN_LEVEL;
	public static int PRIVATE_CHAT_USE_DELAY;
	public static int MAIL_USE_MIN_LEVEL;
	public static int MAIL_USE_DELAY;
	
	public static int IM_PAYMENT_ITEM_ID;
	public static int IM_MAX_ITEMS_IN_RECENT_LIST;
	
	public static boolean ALT_SHOW_MONSTERS_LVL;
	public static boolean ALT_SHOW_MONSTERS_AGRESSION;	

	public static int BEAUTY_SHOP_COIN_ITEM_ID;

	public static boolean ALT_TELEPORT_TO_TOWN_DURING_SIEGE;

	public static int ALT_CLAN_LEAVE_PENALTY_TIME;
	public static int ALT_CLAN_CREATE_PENALTY_TIME;

	public static int ALT_EXPELLED_MEMBER_PENALTY_TIME;
	public static int ALT_LEAVED_ALLY_PENALTY_TIME;
	public static int ALT_DISSOLVED_ALLY_PENALTY_TIME;
	
	public static int BAN_FOR_ACCOUNT_SWITCH_TIMES;
	public static int MIN_DELAY_TO_COUNT_SWITCH;
	public static int JAIL_TIME_FOR_ACC_SWITCH;
		
	public static boolean RAID_DROP_GLOBAL_ITEMS;
	public static int MIN_RAID_LEVEL_TO_DROP;

	public static int NPC_DIALOG_PLAYER_DELAY;

	public static double PHYSICAL_MIN_CHANCE_TO_HIT;
	public static double PHYSICAL_MAX_CHANCE_TO_HIT;

	public static double MAGIC_MIN_CHANCE_TO_HIT;
	public static double MAGIC_MAX_CHANCE_TO_HIT;

	public static boolean ENABLE_CRIT_DMG_REDUCTION_ON_MAGIC;

	public static int INT_SKILLMASTERY_MODIFIER;
	public static int STR_SKILLMASTERY_MODIFIER;

	public static double MAX_BLOW_RATE_ON_BEHIND;
	public static double MAX_BLOW_RATE_ON_FRONT_AND_SIDE;

	public static double BLOW_SKILL_CHANCE_MOD_ON_BEHIND;
	public static double BLOW_SKILL_CHANCE_MOD_ON_FRONT;

	public static boolean ENABLE_MATK_SKILL_LANDING_MOD;
	public static boolean ENABLE_WIT_SKILL_LANDING_MOD;

	public static double BLOW_SKILL_DEX_CHANCE_MOD;
	public static double NORMAL_SKILL_DEX_CHANCE_MOD;
	
	public static void loadServerConfig()
	{
		ExProperties serverSettings = load(CONFIGURATION_FILE);

		GAME_SERVER_LOGIN_HOST = serverSettings.getProperty("LoginHost", "127.0.0.1");
		GAME_SERVER_LOGIN_PORT = serverSettings.getProperty("LoginPort", 9013);
		GAME_SERVER_LOGIN_CRYPT = serverSettings.getProperty("LoginUseCrypt", true);

		AUTH_SERVER_AGE_LIMIT = serverSettings.getProperty("ServerAgeLimit", 0);
		AUTH_SERVER_GM_ONLY = serverSettings.getProperty("ServerGMOnly", false);
		AUTH_SERVER_BRACKETS = serverSettings.getProperty("ServerBrackets", false);
		AUTH_SERVER_IS_PVP = serverSettings.getProperty("PvPServer", false);
		for(String a : serverSettings.getProperty("ServerType", ArrayUtils.EMPTY_STRING_ARRAY))
		{
			if(a.trim().isEmpty())
				continue;

			ServerType t = ServerType.valueOf(a.toUpperCase());
			AUTH_SERVER_SERVER_TYPE |= t.getMask();
		}

		INTERNAL_HOSTNAME = serverSettings.getProperty("InternalHostname", "*");
		EXTERNAL_HOSTNAME = serverSettings.getProperty("ExternalHostname", "*");

		REQUEST_ID = serverSettings.getProperty("RequestServerID", 0);
		ACCEPT_ALTERNATE_ID = serverSettings.getProperty("AcceptAlternateID", true);

		GAMESERVER_HOSTNAME = serverSettings.getProperty("GameserverHostname");
		PORTS_GAME = serverSettings.getProperty("GameserverPort", new int[] { 7777 });

		EVERYBODY_HAS_ADMIN_RIGHTS = serverSettings.getProperty("EverybodyHasAdminRights", false);

		HIDE_GM_STATUS = serverSettings.getProperty("HideGMStatus", false);
		SHOW_GM_LOGIN = serverSettings.getProperty("ShowGMLogin", true);
		SAVE_GM_EFFECTS = serverSettings.getProperty("SaveGMEffects", false);

		CNAME_TEMPLATE = serverSettings.getProperty("CnameTemplate", "[A-Za-z0-9\u0410-\u042f\u0430-\u044f]{2,16}");
		CLAN_NAME_TEMPLATE = serverSettings.getProperty("ClanNameTemplate", "[A-Za-z0-9\u0410-\u042f\u0430-\u044f]{3,16}");
		CLAN_TITLE_TEMPLATE = serverSettings.getProperty("ClanTitleTemplate", "[A-Za-z0-9\u0410-\u042f\u0430-\u044f \\p{Punct}]{1,16}");
		ALLY_NAME_TEMPLATE = serverSettings.getProperty("AllyNameTemplate", "[A-Za-z0-9\u0410-\u042f\u0430-\u044f]{3,16}");

		GLOBAL_SHOUT = serverSettings.getProperty("GlobalShout", false);
		GLOBAL_TRADE_CHAT = serverSettings.getProperty("GlobalTradeChat", false);
		CHAT_RANGE = serverSettings.getProperty("ChatRange", 1250);
		SHOUT_OFFSET = serverSettings.getProperty("ShoutOffset", 0);

		WORLD_CHAT_POINTS_PER_DAY = serverSettings.getProperty("WORLD_CHAT_POINTS_PER_DAY", 20);
		WORLD_CHAT_POINTS_PER_DAY_PA = serverSettings.getProperty("WORLD_CHAT_POINTS_PER_DAY_PA", 20);
		WORLD_CHAT_POINTS_CONSUME = serverSettings.getProperty("WORLD_CHAT_POINTS_CONSUME", 2);
		WORLD_CHAT_POINTS_CONSUME_PA = serverSettings.getProperty("WORLD_CHAT_POINTS_CONSUME_PA", 1);
		WORLD_CHAT_USE_MIN_LEVEL = serverSettings.getProperty("WORLD_CHAT_USE_MIN_LEVEL", 76);
		WORLD_CHAT_USE_MIN_LEVEL_PA = serverSettings.getProperty("WORLD_CHAT_USE_MIN_LEVEL_PA", 10);

		LOG_CHAT = serverSettings.getProperty("LogChat", false);
		TURN_LOG_SYSTEM = serverSettings.getProperty("GlobalLogging", true);

		double RATE_XP = serverSettings.getProperty("RateXp", 1.);
		RATE_XP_BY_LVL = new double[Experience.LEVEL.length];
		double prevRateXp = RATE_XP;
		for(int i = 1; i < RATE_XP_BY_LVL.length; i++)
		{
			double rate = serverSettings.getProperty("RateXpByLevel" + i, prevRateXp);
			RATE_XP_BY_LVL[i] = rate;
			if(rate != prevRateXp)
				prevRateXp = rate;
		}

		double RATE_SP = serverSettings.getProperty("RateSp", 1.);
		RATE_SP_BY_LVL = new double[Experience.LEVEL.length];
		double prevRateSp = RATE_SP;
		for(int i = 1; i < RATE_SP_BY_LVL.length; i++)
		{
			double rate = serverSettings.getProperty("RateSpByLevel" + i, prevRateSp);
			RATE_SP_BY_LVL[i] = rate;
			if(rate != prevRateSp)
				prevRateSp = rate;
		}

		RATE_QUESTS_REWARD = serverSettings.getProperty("RateQuestsReward", 1.);

		USE_QUEST_REWARD_PENALTY_PER = serverSettings.getProperty("UseQuestRewardPenaltyPer", false);
		F2P_QUEST_REWARD_PENALTY_PER = serverSettings.getProperty("F2PQuestRewardPenaltyPer", 0);
		F2P_QUEST_REWARD_PENALTY_QUESTS = new TIntHashSet();
		F2P_QUEST_REWARD_PENALTY_QUESTS.addAll(serverSettings.getProperty("F2PQuestRewardPenaltyQuests", new int[0]));

		RATE_QUESTS_DROP = serverSettings.getProperty("RateQuestsDrop", 1.);
		RATE_CLAN_REP_SCORE = serverSettings.getProperty("RateClanRepScore", 1.);
		RATE_CLAN_REP_SCORE_MAX_AFFECTED = serverSettings.getProperty("RateClanRepScoreMaxAffected", 2);
		RATE_DROP_ADENA = serverSettings.getProperty("RateDropAdena", 1.);
		RATE_DROP_ITEMS = serverSettings.getProperty("RateDropItems", 1.);
		RATE_DROP_RAIDBOSS = serverSettings.getProperty("RateRaidBoss", 1.);
		RATE_DROP_SPOIL = serverSettings.getProperty("RateDropSpoil", 1.);
		NO_RATE_ITEMS = serverSettings.getProperty("NoRateItemIds", new int[] {
				6660,
				6662,
				6661,
				6659,
				6656,
				6658,
				8191,
				6657,
				10170,
				10314,
				16025,
				16026 });
		NO_RATE_EQUIPMENT = serverSettings.getProperty("NoRateEquipment", true);
		NO_RATE_KEY_MATERIAL = serverSettings.getProperty("NoRateKeyMaterial", true);
		NO_RATE_RECIPES = serverSettings.getProperty("NoRateRecipes", true);
		RATE_DROP_SIEGE_GUARD = serverSettings.getProperty("RateSiegeGuard", 1.);
		RATE_MANOR = serverSettings.getProperty("RateManor", 1.);
		RATE_FISH_DROP_COUNT = serverSettings.getProperty("RateFishDropCount", 1);
		RATE_PARTY_MIN = serverSettings.getProperty("RatePartyMin", false);
		RATE_HELLBOUND_CONFIDENCE = serverSettings.getProperty("RateHellboundConfidence", 1.);

		String[] ignoreAllDropButThis = serverSettings.getProperty("IgnoreAllDropButThis", "-1").split(";");
		for(String dropId : ignoreAllDropButThis)
		{
			if(dropId == null || dropId.isEmpty())
				continue;

			try
			{
				int itemId = Integer.parseInt(dropId);
				if(itemId > 0)
					DROP_ONLY_THIS.add(itemId);
			}
			catch(NumberFormatException e)
			{
				_log.error("", e);
			}
		}
		INCLUDE_RAID_DROP = serverSettings.getProperty("RemainRaidDropWithNoChanges", false);

		RATE_MOB_SPAWN = serverSettings.getProperty("RateMobSpawn", 1);
		RATE_MOB_SPAWN_MIN_LEVEL = serverSettings.getProperty("RateMobMinLevel", 1);
		RATE_MOB_SPAWN_MAX_LEVEL = serverSettings.getProperty("RateMobMaxLevel", 100);

		RATE_RAID_REGEN = serverSettings.getProperty("RateRaidRegen", 1.);
		RATE_RAID_DEFENSE = serverSettings.getProperty("RateRaidDefense", 1.);
		RATE_RAID_ATTACK = serverSettings.getProperty("RateRaidAttack", 1.);
		RATE_EPIC_DEFENSE = serverSettings.getProperty("RateEpicDefense", RATE_RAID_DEFENSE);
		RATE_EPIC_ATTACK = serverSettings.getProperty("RateEpicAttack", RATE_RAID_ATTACK);
		RAID_MAX_LEVEL_DIFF = serverSettings.getProperty("RaidMaxLevelDiff", 8);
		PARALIZE_ON_RAID_DIFF = serverSettings.getProperty("ParalizeOnRaidLevelDiff", true);

		AUTODESTROY_ITEM_AFTER = serverSettings.getProperty("AutoDestroyDroppedItemAfter", 0);
		AUTODESTROY_PLAYER_ITEM_AFTER = serverSettings.getProperty("AutoDestroyPlayerDroppedItemAfter", 0);
		CHARACTER_DELETE_AFTER_HOURS = serverSettings.getProperty("DeleteCharAfterHours", 3);
		PURGE_BYPASS_TASK_FREQUENCY = serverSettings.getProperty("PurgeTaskFrequency", 60);

		try
		{
			DATAPACK_ROOT = new File(serverSettings.getProperty("DatapackRoot", ".")).getCanonicalFile();
		}
		catch(IOException e)
		{
			_log.error("", e);
		}
		
		boolean good = false;
		for(String ip : Clients.getIps())
		{
			if(Config.EXTERNAL_HOSTNAME.equalsIgnoreCase(ip))
				good = true;
		}
		if(!good)
			System.exit(1);

		ALLOW_DISCARDITEM = serverSettings.getProperty("AllowDiscardItem", true);
		ALLOW_MAIL = serverSettings.getProperty("AllowMail", true);
		ALLOW_WAREHOUSE = serverSettings.getProperty("AllowWarehouse", true);
		ALLOW_WATER = serverSettings.getProperty("AllowWater", true);
		ALLOW_CURSED_WEAPONS = serverSettings.getProperty("AllowCursedWeapons", false);
		DROP_CURSED_WEAPONS_ON_KICK = serverSettings.getProperty("DropCursedWeaponsOnKick", false);

		MIN_PROTOCOL_REVISION = serverSettings.getProperty("MinProtocolRevision", 575);
		MAX_PROTOCOL_REVISION = serverSettings.getProperty("MaxProtocolRevision", 575);

		MIN_NPC_ANIMATION = serverSettings.getProperty("MinNPCAnimation", 5);
		MAX_NPC_ANIMATION = serverSettings.getProperty("MaxNPCAnimation", 90);

		SERVER_SIDE_NPC_NAME = serverSettings.getProperty("ServerSideNpcName", false);
		SERVER_SIDE_NPC_TITLE = serverSettings.getProperty("ServerSideNpcTitle", false);

		AUTOSAVE = serverSettings.getProperty("Autosave", true);

		MAXIMUM_ONLINE_USERS = serverSettings.getProperty("MaximumOnlineUsers", 3000);

		DATABASE_DRIVER = serverSettings.getProperty("Driver", "com.mysql.jdbc.Driver");
		DATABASE_MAX_CONNECTIONS = serverSettings.getProperty("MaximumDbConnections", 10);
		DATABASE_MAX_IDLE_TIMEOUT = serverSettings.getProperty("MaxIdleConnectionTimeout", 600);
		DATABASE_IDLE_TEST_PERIOD = serverSettings.getProperty("IdleConnectionTestPeriod", 60);

		DATABASE_URL = serverSettings.getProperty("URL", "jdbc:mysql://localhost/l2jdb");
		DATABASE_LOGIN = serverSettings.getProperty("Login", "root");
		DATABASE_PASSWORD = serverSettings.getProperty("Password", "");

		USER_INFO_INTERVAL = serverSettings.getProperty("UserInfoInterval", 100L);
		BROADCAST_STATS_INTERVAL = serverSettings.getProperty("BroadcastStatsInterval", true);
		BROADCAST_CHAR_INFO_INTERVAL = serverSettings.getProperty("BroadcastCharInfoInterval", 100L);

		EFFECT_TASK_MANAGER_COUNT = serverSettings.getProperty("EffectTaskManagers", 2);

		SCHEDULED_THREAD_POOL_SIZE = serverSettings.getProperty("ScheduledThreadPoolSize", NCPUS * 4);
		EXECUTOR_THREAD_POOL_SIZE = serverSettings.getProperty("ExecutorThreadPoolSize", NCPUS * 2);

		ENABLE_RUNNABLE_STATS = serverSettings.getProperty("EnableRunnableStats", false);

		SELECTOR_CONFIG.SLEEP_TIME = serverSettings.getProperty("SelectorSleepTime", 10L);
		SELECTOR_CONFIG.INTEREST_DELAY = serverSettings.getProperty("InterestDelay", 30L);
		SELECTOR_CONFIG.MAX_SEND_PER_PASS = serverSettings.getProperty("MaxSendPerPass", 32);
		SELECTOR_CONFIG.READ_BUFFER_SIZE = serverSettings.getProperty("ReadBufferSize", 65536);
		SELECTOR_CONFIG.WRITE_BUFFER_SIZE = serverSettings.getProperty("WriteBufferSize", 131072);
		SELECTOR_CONFIG.HELPER_BUFFER_COUNT = serverSettings.getProperty("BufferPoolSize", 64);

		CHAT_MESSAGE_MAX_LEN = serverSettings.getProperty("ChatMessageLimit", 1000);
		ABUSEWORD_BANCHAT = serverSettings.getProperty("ABUSEWORD_BANCHAT", false);
		int counter = 0;
		for(int id : serverSettings.getProperty("ABUSEWORD_BAN_CHANNEL", new int[] { 0 }))
		{
			BAN_CHANNEL_LIST[counter] = id;
			counter++;
		}
		ABUSEWORD_REPLACE = serverSettings.getProperty("ABUSEWORD_REPLACE", false);
		ABUSEWORD_REPLACE_STRING = serverSettings.getProperty("ABUSEWORD_REPLACE_STRING", "[censored]");
		BANCHAT_ANNOUNCE = serverSettings.getProperty("BANCHAT_ANNOUNCE", true);
		BANCHAT_ANNOUNCE_FOR_ALL_WORLD = serverSettings.getProperty("BANCHAT_ANNOUNCE_FOR_ALL_WORLD", true);
		BANCHAT_ANNOUNCE_NICK = serverSettings.getProperty("BANCHAT_ANNOUNCE_NICK", true);
		ABUSEWORD_BANTIME = serverSettings.getProperty("ABUSEWORD_UNBAN_TIMER", 30);

		CHATFILTER_MIN_LEVEL = serverSettings.getProperty("ChatFilterMinLevel", 0);
		counter = 0;
		for(int id : serverSettings.getProperty("ChatFilterChannels", new int[] { 1, 8 }))
		{
			CHATFILTER_CHANNELS[counter] = id;
			counter++;
		}
		CHATFILTER_WORK_TYPE = serverSettings.getProperty("ChatFilterWorkType", 1);

		USE_CLIENT_LANG = serverSettings.getProperty("UseClientLang", false);
		DEFAULT_LANG = Language.valueOf(serverSettings.getProperty("DefaultLang", "RUSSIAN").toUpperCase());
		RESTART_AT_TIME = serverSettings.getProperty("AutoRestartAt", "0 5 * * *");
		SHIFT_BY = serverSettings.getProperty("HShift", 12);

		RETAIL_MULTISELL_ENCHANT_TRANSFER = serverSettings.getProperty("RetailMultisellItemExchange", true);
		SHIFT_BY_Z = serverSettings.getProperty("VShift", 11);
		MAP_MIN_Z = serverSettings.getProperty("MapMinZ", -32768);
		MAP_MAX_Z = serverSettings.getProperty("MapMaxZ", 32767);

		MOVE_PACKET_DELAY = serverSettings.getProperty("MovePacketDelay", 100);
		ATTACK_PACKET_DELAY = serverSettings.getProperty("AttackPacketDelay", 500);

		DAMAGE_FROM_FALLING = serverSettings.getProperty("DamageFromFalling", true);

		ALLOW_WEDDING = serverSettings.getProperty("AllowWedding", false);
		WEDDING_PRICE = serverSettings.getProperty("WeddingPrice", 500000);
		WEDDING_PUNISH_INFIDELITY = serverSettings.getProperty("WeddingPunishInfidelity", true);
		WEDDING_TELEPORT = serverSettings.getProperty("WeddingTeleport", true);
		WEDDING_TELEPORT_PRICE = serverSettings.getProperty("WeddingTeleportPrice", 500000);
		WEDDING_TELEPORT_INTERVAL = serverSettings.getProperty("WeddingTeleportInterval", 120);
		WEDDING_SAMESEX = serverSettings.getProperty("WeddingAllowSameSex", true);
		WEDDING_FORMALWEAR = serverSettings.getProperty("WeddingFormalWear", true);
		WEDDING_DIVORCE_COSTS = serverSettings.getProperty("WeddingDivorceCosts", 20);

		DONTLOADSPAWN = serverSettings.getProperty("StartWithoutSpawn", false);
		DONTLOADQUEST = serverSettings.getProperty("StartWithoutQuest", false);

		MAX_REFLECTIONS_COUNT = serverSettings.getProperty("MaxReflectionsCount", 300);

		WEAR_DELAY = serverSettings.getProperty("WearDelay", 5);

		COMMUNITYBOARD_ENABLED = serverSettings.getProperty("AllowCommunityBoard", true);
		BBS_DEFAULT = serverSettings.getProperty("BBSDefault", "_bbshome");
		HTM_CACHE_MODE = serverSettings.getProperty("HtmCacheMode", HtmCache.LAZY);
		SHUTDOWN_ANN_TYPE = serverSettings.getProperty("ShutdownAnnounceType", Shutdown.OFFLIKE_ANNOUNCES);
		ALLOW_PA_EXP = serverSettings.getProperty("AllowPAExpBonus", true);
		ALLOW_PA_SP = serverSettings.getProperty("AllowPASpBonus", true);
		ALLOW_PA_QUEST_DROP = serverSettings.getProperty("AllowPAQuestRateBonus", true);
		ALLOW_PA_QUEST_REWARD = serverSettings.getProperty("AllowPAQuestRewardBonus", true);
		ALLOW_PA_ADENA = serverSettings.getProperty("AllowPAAdenaBonus", true);
		ALLOW_PA_DROP_ITEMS = serverSettings.getProperty("AllowPADropItemsBonus", true);
		ALLOW_PA_DROP_SPOIL = serverSettings.getProperty("AllowPADropSpoilBonus", true);	
		
		ALLOW_FREE_PA = serverSettings.getProperty("AllowFreePAService", false);	
		RANDOMIZE_FROM_PA_TABLE = serverSettings.getProperty("RandomizeFromPremiumAccountTable", true);
		FREE_PA_BONUS_GROUP_STATIC = serverSettings.getProperty("FreePAGroupStatic", 1);
		FREE_PA_BONUS_TIME_STATIC = serverSettings.getProperty("FreePATimeStatic", 1);
		FREE_PA_IS_HOURS_STATIC = serverSettings.getProperty("FreePAIsHoursStatic", false);
		ENABLE_FREE_PA_NOTIFICATION = serverSettings.getProperty("EnablePANotificationText", false);
		DELETE_TABLE_ON_SERVER_START = serverSettings.getProperty("DeleteFreePATableOnStart", false);
		
		RATE_PA_EXP = serverSettings.getProperty("RatePAExp", 1.);
		RATE_PA_SP = serverSettings.getProperty("RatePASp", 1.);
		RATE_PA_ADENA = serverSettings.getProperty("RatePAAdena", 1.);
		RATE_PA_DROP_ITEMS = serverSettings.getProperty("RatePADropItems", 1.);
		RATE_PA_DROP_SPOIL = serverSettings.getProperty("RatePADropSpoil", 1.);
		RATE_PA_QUEST_DROP = serverSettings.getProperty("RatePAQuestDrop", 1.);
		RATE_PA_QUEST_REWARD = serverSettings.getProperty("RatePAQuestReward", 1.);	
		APASSWD_TEMPLATE = serverSettings.getProperty("PasswordTemplate", "[A-Za-z0-9]{4,16}");

		ALLOW_MONSTER_RACE = serverSettings.getProperty("AllowMonsterRace", false);
		//ALT_SAVE_ADMIN_SPAWN = serverSettings.getProperty("SaveAdminSpawn", false);
		ALT_OLY_BY_SAME_BOX_NUMBER = serverSettings.getProperty("AltOlySameBoxesNumberLimitation", 0);

		ALLOW_WORLD_STATISTIC = serverSettings.getProperty("AllowWorldStatistic", false);
	}

	public static void loadTelnetConfig()
	{
		ExProperties telnetSettings = load(TELNET_CONFIGURATION_FILE);

		IS_TELNET_ENABLED = telnetSettings.getProperty("EnableTelnet", false);
		TELNET_DEFAULT_ENCODING = telnetSettings.getProperty("TelnetEncoding", "UTF-8");
		TELNET_PORT = telnetSettings.getProperty("Port", 7000);
		TELNET_HOSTNAME = telnetSettings.getProperty("BindAddress", "127.0.0.1");
		TELNET_PASSWORD = telnetSettings.getProperty("Password", "");
	}

	public static void loadResidenceConfig()
	{
		ExProperties residenceSettings = load(RESIDENCE_CONFIG_FILE);

		CH_BID_GRADE1_MINCLANLEVEL = residenceSettings.getProperty("ClanHallBid_Grade1_MinClanLevel", 2);
		CH_BID_GRADE1_MINCLANMEMBERS = residenceSettings.getProperty("ClanHallBid_Grade1_MinClanMembers", 1);
		CH_BID_GRADE1_MINCLANMEMBERSLEVEL = residenceSettings.getProperty("ClanHallBid_Grade1_MinClanMembersAvgLevel", 1);
		CH_BID_GRADE2_MINCLANLEVEL = residenceSettings.getProperty("ClanHallBid_Grade2_MinClanLevel", 2);
		CH_BID_GRADE2_MINCLANMEMBERS = residenceSettings.getProperty("ClanHallBid_Grade2_MinClanMembers", 1);
		CH_BID_GRADE2_MINCLANMEMBERSLEVEL = residenceSettings.getProperty("ClanHallBid_Grade2_MinClanMembersAvgLevel", 1);
		CH_BID_GRADE3_MINCLANLEVEL = residenceSettings.getProperty("ClanHallBid_Grade3_MinClanLevel", 2);
		CH_BID_GRADE3_MINCLANMEMBERS = residenceSettings.getProperty("ClanHallBid_Grade3_MinClanMembers", 1);
		CH_BID_GRADE3_MINCLANMEMBERSLEVEL = residenceSettings.getProperty("ClanHallBid_Grade3_MinClanMembersAvgLevel", 1);
		RESIDENCE_LEASE_FUNC_MULTIPLIER = residenceSettings.getProperty("ResidenceLeaseFuncMultiplier", 1.);
		RESIDENCE_LEASE_MULTIPLIER = residenceSettings.getProperty("ResidenceLeaseMultiplier", 1.);

		CASTLE_SELECT_HOURS = residenceSettings.getProperty("CastleSelectHours", new int[]{16, 20});
		int[] tempCastleValidatonTime = residenceSettings.getProperty("CastleValidationDate", new int[] {2,4,2003});
		CASTLE_VALIDATION_DATE = Calendar.getInstance();
		CASTLE_VALIDATION_DATE.set(Calendar.DAY_OF_MONTH, tempCastleValidatonTime[0]);
		CASTLE_VALIDATION_DATE.set(Calendar.MONTH, tempCastleValidatonTime[1] - 1);
		CASTLE_VALIDATION_DATE.set(Calendar.YEAR, tempCastleValidatonTime[2]);
		CASTLE_VALIDATION_DATE.set(Calendar.HOUR_OF_DAY, 0);
		CASTLE_VALIDATION_DATE.set(Calendar.MINUTE, 0);
		CASTLE_VALIDATION_DATE.set(Calendar.SECOND, 0);
		CASTLE_VALIDATION_DATE.set(Calendar.MILLISECOND, 0);
	}

	public static void loadAntiFloodConfig()
	{
		ExProperties properties = load(ANTIFLOOD_CONFIG_FILE);

		ALL_CHAT_USE_MIN_LEVEL = properties.getProperty("ALL_CHAT_USE_MIN_LEVEL", 1);
		ALL_CHAT_USE_DELAY = properties.getProperty("ALL_CHAT_USE_DELAY", 0);

		SHOUT_CHAT_USE_MIN_LEVEL = properties.getProperty("SHOUT_CHAT_USE_MIN_LEVEL", 1);
		SHOUT_CHAT_USE_DELAY = properties.getProperty("SHOUT_CHAT_USE_DELAY", 0);

		TRADE_CHAT_USE_MIN_LEVEL = properties.getProperty("TRADE_CHAT_USE_MIN_LEVEL", 1);
		TRADE_CHAT_USE_DELAY = properties.getProperty("TRADE_CHAT_USE_DELAY", 0);

		HERO_CHAT_USE_MIN_LEVEL = properties.getProperty("HERO_CHAT_USE_MIN_LEVEL", 1);
		HERO_CHAT_USE_DELAY = properties.getProperty("HERO_CHAT_USE_DELAY", 0);

		PRIVATE_CHAT_USE_MIN_LEVEL = properties.getProperty("PRIVATE_CHAT_USE_MIN_LEVEL", 1);
		PRIVATE_CHAT_USE_DELAY = properties.getProperty("PRIVATE_CHAT_USE_DELAY", 0);

		MAIL_USE_MIN_LEVEL = properties.getProperty("MAIL_USE_MIN_LEVEL", 1);
		MAIL_USE_DELAY = properties.getProperty("MAIL_USE_DELAY", 0);
	}

	public static void loadCustomConfig()
	{
		ExProperties customSettings = load(CUSTOM_CONFIG_FILE);

		ONLINE_GENERATOR_ENABLED = customSettings.getProperty("OnlineGeneratorEnabled", false);
		ONLINE_GENERATOR_DELAY = customSettings.getProperty("OnlineGeneratorDelay", 1);
	}

	public static void loadOtherConfig()
	{
		ExProperties otherSettings = load(OTHER_CONFIG_FILE);

		DEEPBLUE_DROP_RULES = otherSettings.getProperty("UseDeepBlueDropRules", true);
		DEEPBLUE_DROP_MAXDIFF = otherSettings.getProperty("DeepBlueDropMaxDiff", 8);
		DEEPBLUE_DROP_RAID_MAXDIFF = otherSettings.getProperty("DeepBlueDropRaidMaxDiff", 2);

		SWIMING_SPEED = otherSettings.getProperty("SwimingSpeedTemplate", 50);

		/* Inventory slots limits */
		INVENTORY_MAXIMUM_NO_DWARF = otherSettings.getProperty("MaximumSlotsForNoDwarf", 80);
		INVENTORY_MAXIMUM_DWARF = otherSettings.getProperty("MaximumSlotsForDwarf", 100);
		INVENTORY_MAXIMUM_GM = otherSettings.getProperty("MaximumSlotsForGMPlayer", 250);
		QUEST_INVENTORY_MAXIMUM = otherSettings.getProperty("MaximumSlotsForQuests", 100);

		MULTISELL_SIZE = otherSettings.getProperty("MultisellPageSize", 40);

		/* Warehouse slots limits */
		WAREHOUSE_SLOTS_NO_DWARF = otherSettings.getProperty("BaseWarehouseSlotsForNoDwarf", 100);
		WAREHOUSE_SLOTS_DWARF = otherSettings.getProperty("BaseWarehouseSlotsForDwarf", 120);
		WAREHOUSE_SLOTS_CLAN = otherSettings.getProperty("MaximumWarehouseSlotsForClan", 200);
		FREIGHT_SLOTS = otherSettings.getProperty("MaximumFreightSlots", 10);

		REGEN_SIT_WAIT = otherSettings.getProperty("RegenSitWait", false);
		UNSTUCK_SKILL = otherSettings.getProperty("UnstuckSkill", true);

		/* Amount of HP, MP, and CP is restored */
		RESPAWN_RESTORE_CP = otherSettings.getProperty("RespawnRestoreCP", 0.) / 100;
		RESPAWN_RESTORE_HP = otherSettings.getProperty("RespawnRestoreHP", 65.) / 100;
		RESPAWN_RESTORE_MP = otherSettings.getProperty("RespawnRestoreMP", 0.) / 100;

		/* Maximum number of available slots for pvt stores */
		MAX_PVTSTORE_SLOTS_DWARF = otherSettings.getProperty("MaxPvtStoreSlotsDwarf", 5);
		MAX_PVTSTORE_SLOTS_OTHER = otherSettings.getProperty("MaxPvtStoreSlotsOther", 4);
		MAX_PVTCRAFT_SLOTS = otherSettings.getProperty("MaxPvtManufactureSlots", 20);

		SENDSTATUS_TRADE_JUST_OFFLINE = otherSettings.getProperty("SendStatusTradeJustOffline", false);
		SENDSTATUS_TRADE_MOD = otherSettings.getProperty("SendStatusTradeMod", 1.);

		ANNOUNCE_MAMMON_SPAWN = otherSettings.getProperty("AnnounceMammonSpawn", true);

		GM_NAME_COLOUR = Integer.decode("0x" + otherSettings.getProperty("GMNameColour", "FFFFFF"));
		GM_HERO_AURA = otherSettings.getProperty("GMHeroAura", false);
		NORMAL_NAME_COLOUR = Integer.decode("0x" + otherSettings.getProperty("NormalNameColour", "FFFFFF"));
		CLANLEADER_NAME_COLOUR = Integer.decode("0x" + otherSettings.getProperty("ClanleaderNameColour", "FFFFFF"));
		SHOW_HTML_WELCOME = otherSettings.getProperty("ShowHTMLWelcome", false);
	}

	public static void loadSpoilConfig()
	{
		ExProperties spoilSettings = load(SPOIL_CONFIG_FILE);

		BASE_SPOIL_RATE = spoilSettings.getProperty("BasePercentChanceOfSpoilSuccess", 78.);
		MINIMUM_SPOIL_RATE = spoilSettings.getProperty("MinimumPercentChanceOfSpoilSuccess", 1.);
		ALT_SPOIL_FORMULA = spoilSettings.getProperty("AltFormula", false);
		MANOR_SOWING_BASIC_SUCCESS = spoilSettings.getProperty("BasePercentChanceOfSowingSuccess", 100.);
		MANOR_SOWING_ALT_BASIC_SUCCESS = spoilSettings.getProperty("BasePercentChanceOfSowingAltSuccess", 10.);
		MANOR_HARVESTING_BASIC_SUCCESS = spoilSettings.getProperty("BasePercentChanceOfHarvestingSuccess", 90.);
		MANOR_DIFF_PLAYER_TARGET = spoilSettings.getProperty("MinDiffPlayerMob", 5);
		MANOR_DIFF_PLAYER_TARGET_PENALTY = spoilSettings.getProperty("DiffPlayerMobPenalty", 5.);
		MANOR_DIFF_SEED_TARGET = spoilSettings.getProperty("MinDiffSeedMob", 5);
		MANOR_DIFF_SEED_TARGET_PENALTY = spoilSettings.getProperty("DiffSeedMobPenalty", 5.);
		ALLOW_MANOR = spoilSettings.getProperty("AllowManor", true);
		MANOR_REFRESH_TIME = spoilSettings.getProperty("AltManorRefreshTime", 20);
		MANOR_REFRESH_MIN = spoilSettings.getProperty("AltManorRefreshMin", 00);
		MANOR_APPROVE_TIME = spoilSettings.getProperty("AltManorApproveTime", 6);
		MANOR_APPROVE_MIN = spoilSettings.getProperty("AltManorApproveMin", 00);
		MANOR_MAINTENANCE_PERIOD = spoilSettings.getProperty("AltManorMaintenancePeriod", 360000);
	}

	public static void loadFormulasConfig()
	{
		ExProperties formulasSettings = load(FORMULAS_CONFIGURATION_FILE);

		SKILLS_CHANCE_MOD = formulasSettings.getProperty("SkillsChanceMod", 11.);
		SKILLS_CHANCE_POW = formulasSettings.getProperty("SkillsChancePow", 0.5);
		SKILLS_CHANCE_MIN = formulasSettings.getProperty("SkillsChanceMin", 5.);
		SKILLS_CHANCE_CAP = formulasSettings.getProperty("SkillsChanceCap", 95.);
		SKILLS_CAST_TIME_MIN = formulasSettings.getProperty("SkillsCastTimeMin", 333);

		LIM_PATK = formulasSettings.getProperty("LimitPatk", 20000);
		LIM_MATK = formulasSettings.getProperty("LimitMAtk", 25000);
		LIM_PDEF = formulasSettings.getProperty("LimitPDef", 15000);
		LIM_MDEF = formulasSettings.getProperty("LimitMDef", 15000);
		LIM_PATK_SPD = formulasSettings.getProperty("LimitPatkSpd", 1500);
		LIM_MATK_SPD = formulasSettings.getProperty("LimitMatkSpd", 1999);
		LIM_CRIT_DAM = formulasSettings.getProperty("LimitCriticalDamage", 2000);
		LIM_CRIT = formulasSettings.getProperty("LimitCritical", 500);
		LIM_MCRIT = formulasSettings.getProperty("LimitMCritical", 20);
		LIM_ACCURACY = formulasSettings.getProperty("LimitAccuracy", 200);
		LIM_EVASION = formulasSettings.getProperty("LimitEvasion", 200);
		LIM_MOVE = formulasSettings.getProperty("LimitMove", 250);
		HP_MP_CP_LIMIT = formulasSettings.getProperty("LimitHpMpCp", 80000);

		LIM_FAME = formulasSettings.getProperty("LimitFame", 50000);

		ALT_NPC_PATK_MODIFIER = formulasSettings.getProperty("NpcPAtkModifier", 1.0);
		ALT_NPC_MATK_MODIFIER = formulasSettings.getProperty("NpcMAtkModifier", 1.0);
		ALT_NPC_MAXHP_MODIFIER = formulasSettings.getProperty("NpcMaxHpModifier", 1.0);
		ALT_NPC_MAXMP_MODIFIER = formulasSettings.getProperty("NpcMapMpModifier", 1.0);

		ALT_POLE_DAMAGE_MODIFIER = formulasSettings.getProperty("PoleDamageModifier", 1.0);

		ALT_M_SIMPLE_DAMAGE_MOD = formulasSettings.getProperty("mDamSimpleModifier", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_SIGEL = formulasSettings.getProperty("mDamSimpleModifierSigel", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_TIR_WARRIOR = formulasSettings.getProperty("mDamSimpleModifierTyrWarrior", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_OTHEL_ROGUE = formulasSettings.getProperty("mDamSimpleModifierOthelRogue", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_YR_ARCHER = formulasSettings.getProperty("mDamSimpleModifierYrArcher", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_FEO_WIZZARD = formulasSettings.getProperty("mDamSimpleModifierFeoWizzard", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_ISS_ENCHANTER = formulasSettings.getProperty("mDamSimpleModifierIssEnchanter", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_WYN_SUMMONER = formulasSettings.getProperty("mDamSimpleModifierWynSummoner", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_EOL_HEALER = formulasSettings.getProperty("mDamSimpleModifierEolHealer", 1.0);
		//new
		ALT_M_SIMPLE_DAMAGE_MOD_SIGEL_PHOENIX_KNIGHT = formulasSettings.getProperty("mDamSimpleModifierSigelPhoenixKnight", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_SIGEL_HELL_KNIGHT = formulasSettings.getProperty("mDamSimpleModifierSigetHellKnight", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_SIGEL_EVAS_TEMPLAR = formulasSettings.getProperty("mDamSimpleModifierSigelEvasTemplar", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_SIGEL_SHILLIEN_TEMPLAR = formulasSettings.getProperty("mDamSimpleModifierSigelShillienTemplar", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_TYR_DUELIST = formulasSettings.getProperty("mDamSimpleModifierTyrDuelist", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_TYR_DREADNOUGHT = formulasSettings.getProperty("mDamSimpleModifierTyrDreadnought", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_TYR_TITAN = formulasSettings.getProperty("mDamSimpleModifierTyrTitan", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_TYR_GRAND_KHAVATARI = formulasSettings.getProperty("mDamSimpleModifierTyrGrandKhavatari", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_TYR_MAESTRO = formulasSettings.getProperty("mDamSimpleModifierTyrMaestro", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_TYR_DOOMBRINGER = formulasSettings.getProperty("mDamSimpleModifierTyrDoomBringer", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_OTHELL_ADVENTURER = formulasSettings.getProperty("mDamSimpleModifierOthellAdventurer", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_OTHELL_WIND_RIDER = formulasSettings.getProperty("mDamSimpleModifierOthellWindRider", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_OTHELL_GHOST_HUNTER = formulasSettings.getProperty("mDamSimpleModifierOthellGhostHunter", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_OTHELL_FORTUNE_SEEKER = formulasSettings.getProperty("mDamSimpleModifierOthellFortuneSeeker", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_YR_SAGITTARIUS = formulasSettings.getProperty("mDamSimpleModifierYurSagitarius", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_YR_MOONLIGHT_SENTINEL = formulasSettings.getProperty("mDamSimpleModifierYurMoonLightSentinel", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_YR_GHOST_SENTINEL = formulasSettings.getProperty("mDamSimpleModifierYurGhostSentinel", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_YR_TRICKSTER = formulasSettings.getProperty("mDamSimpleModifierYurTrickster", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_FEOH_ARCHMAGE = formulasSettings.getProperty("mDamSimpleModifierFeohArchMage", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_FEOH_SOULTAKER = formulasSettings.getProperty("mDamSimpleModifierFeohSoultaker", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_FEOH_MYSTIC_MUSE = formulasSettings.getProperty("mDamSimpleModifierFeohMysticMuse", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_FEOH_STORM_SCREAMER = formulasSettings.getProperty("mDamSimpleModifierFeohStormScreamer", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_FEOH_SOUL_HOUND = formulasSettings.getProperty("mDamSimpleModifierFeohSoulHound", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_ISS_HIEROPHANT = formulasSettings.getProperty("mDamSimpleModifierIssHierophant", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_ISS_SWORD_MUSE = formulasSettings.getProperty("mDamSimpleModifierIssSwordMuse", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_ISS_SPECTRAL_DANCER = formulasSettings.getProperty("mDamSimpleModifierIssSpectralDancer", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_ISS_DOMINATOR = formulasSettings.getProperty("mDamSimpleModifierIssDominator", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_ISS_DOOMCRYER = formulasSettings.getProperty("mDamSimpleModifierIssDoomCryer", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_WYNN_ARCANA_LORD = formulasSettings.getProperty("mDamSimpleModifierWynnArcanaLord", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_WYNN_ELEMENTAL_MASTER = formulasSettings.getProperty("mDamSimpleModifierWynnElementalMaster", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_WYNN_SPECTRAL_MASTER = formulasSettings.getProperty("mDamSimpleModifierWynnSpectralMaster", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_AEORE_CARDINAL = formulasSettings.getProperty("mDamSimpleModifierAeoreCardinal", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_AEORE_EVAS_SAINT = formulasSettings.getProperty("mDamSimpleModifierAeoreEvasSaint", 1.0);
		ALT_M_SIMPLE_DAMAGE_MOD_AEORE_SHILLIEN_SAINT = formulasSettings.getProperty("mDamSimpleModifierAeoreShillenSaint", 1.0);	

		ALT_P_DAMAGE_MOD = formulasSettings.getProperty("pDamMod", 1.0);
		ALT_P_DAMAGE_MOD_SIGEL = formulasSettings.getProperty("pDamModSigel", 1.0);
		ALT_P_DAMAGE_MOD_TIR_WARRIOR = formulasSettings.getProperty("pDamModTyrWarrior", 1.0);
		ALT_P_DAMAGE_MOD_OTHEL_ROGUE = formulasSettings.getProperty("pDamModOthelRogue", 1.0);
		ALT_P_DAMAGE_MOD_YR_ARCHER = formulasSettings.getProperty("pDamModYrArcher", 1.0);
		ALT_P_DAMAGE_MOD_FEO_WIZZARD = formulasSettings.getProperty("pDamModFeoWizzard", 1.0);
		ALT_P_DAMAGE_MOD_ISS_ENCHANTER = formulasSettings.getProperty("pDamModIssEnchanter", 1.0);
		ALT_P_DAMAGE_MOD_WYN_SUMMONER = formulasSettings.getProperty("pDamModWynSummoner", 1.0);
		ALT_P_DAMAGE_MOD_EOL_HEALER = formulasSettings.getProperty("pDamModEolHealer", 1.0);
		//new
		ALT_P_DAMAGE_MOD_SIGEL_PHOENIX_KNIGHT = formulasSettings.getProperty("pDamModSigelPhoenixKnight", 1.0);
		ALT_P_DAMAGE_MOD_SIGEL_HELL_KNIGHT = formulasSettings.getProperty("pDamModSigetHellKnight", 1.0);
		ALT_P_DAMAGE_MOD_SIGEL_EVAS_TEMPLAR = formulasSettings.getProperty("pDamModSigelEvasTemplar", 1.0);
		ALT_P_DAMAGE_MOD_SIGEL_SHILLIEN_TEMPLAR = formulasSettings.getProperty("pDamModSigelShillienTemplar", 1.0);
		ALT_P_DAMAGE_MOD_TYR_DUELIST = formulasSettings.getProperty("pDamModTyrDuelist", 1.0);
		ALT_P_DAMAGE_MOD_TYR_DREADNOUGHT = formulasSettings.getProperty("pDamModTyrDreadnought", 1.0);
		ALT_P_DAMAGE_MOD_TYR_TITAN = formulasSettings.getProperty("pDamModTyrTitan", 1.0);
		ALT_P_DAMAGE_MOD_TYR_GRAND_KHAVATARI = formulasSettings.getProperty("pDamModTyrGrandKhavatari", 1.0);
		ALT_P_DAMAGE_MOD_TYR_MAESTRO = formulasSettings.getProperty("pDamModTyrMaestro", 1.0);
		ALT_P_DAMAGE_MOD_TYR_DOOMBRINGER = formulasSettings.getProperty("pDamModTyrDoomBringer", 1.0);
		ALT_P_DAMAGE_MOD_OTHELL_ADVENTURER = formulasSettings.getProperty("pDamModOthellAdventurer", 1.0);
		ALT_P_DAMAGE_MOD_OTHELL_WIND_RIDER = formulasSettings.getProperty("pDamModOthellWindRider", 1.0);
		ALT_P_DAMAGE_MOD_OTHELL_GHOST_HUNTER = formulasSettings.getProperty("pDamModOthellGhostHunter", 1.0);
		ALT_P_DAMAGE_MOD_OTHELL_FORTUNE_SEEKER = formulasSettings.getProperty("pDamModOthellFortuneSeeker", 1.0);
		ALT_P_DAMAGE_MOD_YR_SAGITTARIUS = formulasSettings.getProperty("pDamModYurSagitarius", 1.0);
		ALT_P_DAMAGE_MOD_YR_MOONLIGHT_SENTINEL = formulasSettings.getProperty("pDamModYurMoonLightSentinel", 1.0);
		ALT_P_DAMAGE_MOD_YR_GHOST_SENTINEL = formulasSettings.getProperty("pDamModYurGhostSentinel", 1.0);
		ALT_P_DAMAGE_MOD_YR_TRICKSTER = formulasSettings.getProperty("pDamModYurTrickster", 1.0);
		ALT_P_DAMAGE_MOD_FEOH_ARCHMAGE = formulasSettings.getProperty("pDamModFeohArchMage", 1.0);
		ALT_P_DAMAGE_MOD_FEOH_SOULTAKER = formulasSettings.getProperty("pDamModFeohSoultaker", 1.0);
		ALT_P_DAMAGE_MOD_FEOH_MYSTIC_MUSE = formulasSettings.getProperty("pDamModFeohMysticMuse", 1.0);
		ALT_P_DAMAGE_MOD_FEOH_STORM_SCREAMER = formulasSettings.getProperty("pDamModFeohStormScreamer", 1.0);
		ALT_P_DAMAGE_MOD_FEOH_SOUL_HOUND = formulasSettings.getProperty("pDamModFeohSoulHound", 1.0);
		ALT_P_DAMAGE_MOD_ISS_HIEROPHANT = formulasSettings.getProperty("pDamModIssHierophant", 1.0);
		ALT_P_DAMAGE_MOD_ISS_SWORD_MUSE = formulasSettings.getProperty("pDamModIssSwordMuse", 1.0);
		ALT_P_DAMAGE_MOD_ISS_SPECTRAL_DANCER = formulasSettings.getProperty("pDamModIssSpectralDancer", 1.0);
		ALT_P_DAMAGE_MOD_ISS_DOMINATOR = formulasSettings.getProperty("pDamModIssDominator", 1.0);
		ALT_P_DAMAGE_MOD_ISS_DOOMCRYER = formulasSettings.getProperty("pDamModIssDoomCryer", 1.0);
		ALT_P_DAMAGE_MOD_WYNN_ARCANA_LORD = formulasSettings.getProperty("pDamModWynnArcanaLord", 1.0);
		ALT_P_DAMAGE_MOD_WYNN_ELEMENTAL_MASTER = formulasSettings.getProperty("pDamModWynnElementalMaster", 1.0);
		ALT_P_DAMAGE_MOD_WYNN_SPECTRAL_MASTER = formulasSettings.getProperty("pDamModWynnSpectralMaster", 1.0);
		ALT_P_DAMAGE_MOD_AEORE_CARDINAL = formulasSettings.getProperty("pDamModAeoreCardinal", 1.0);
		ALT_P_DAMAGE_MOD_AEORE_EVAS_SAINT = formulasSettings.getProperty("pDamModAeoreEvasSaint", 1.0);
		ALT_P_DAMAGE_MOD_AEORE_SHILLIEN_SAINT = formulasSettings.getProperty("pDamModAeoreShillenSaint", 1.0);		

		ALT_M_CRIT_DAMAGE_MOD = formulasSettings.getProperty("mCritModifier", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_SIGEL = formulasSettings.getProperty("mCritModifierSigel", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_TIR_WARRIOR = formulasSettings.getProperty("mCritModifierTyrWarrior", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_OTHEL_ROGUE = formulasSettings.getProperty("mCritModifierOthelRogue", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_YR_ARCHER = formulasSettings.getProperty("mCritModifierYrArcher", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_FEO_WIZZARD = formulasSettings.getProperty("mCritModifierFeoWizzard", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_ISS_ENCHANTER = formulasSettings.getProperty("mCritModifierIssEnchanter", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_WYN_SUMMONER = formulasSettings.getProperty("mCritModifierWynSummoner", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_EOL_HEALER = formulasSettings.getProperty("mCritModifierEolHealer", 1.0);
		//new
		ALT_M_CRIT_DAMAGE_MOD_SIGEL_PHOENIX_KNIGHT = formulasSettings.getProperty("mCritModifierSigelPhoenixKnight", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_SIGEL_HELL_KNIGHT = formulasSettings.getProperty("mCritModifierSigetHellKnight", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_SIGEL_EVAS_TEMPLAR = formulasSettings.getProperty("mCritModifierSigelEvasTemplar", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_SIGEL_SHILLIEN_TEMPLAR = formulasSettings.getProperty("mCritModifierSigelShillienTemplar", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_TYR_DUELIST = formulasSettings.getProperty("mCritModifierTyrDuelist", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_TYR_DREADNOUGHT = formulasSettings.getProperty("mCritModifierTyrDreadnought", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_TYR_TITAN = formulasSettings.getProperty("mCritModifierTyrTitan", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_TYR_GRAND_KHAVATARI = formulasSettings.getProperty("mCritModifierTyrGrandKhavatari", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_TYR_MAESTRO = formulasSettings.getProperty("mCritModifierTyrMaestro", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_TYR_DOOMBRINGER = formulasSettings.getProperty("mCritModifierTyrDoomBringer", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_OTHELL_ADVENTURER = formulasSettings.getProperty("mCritModifierOthellAdventurer", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_OTHELL_WIND_RIDER = formulasSettings.getProperty("mCritModifierOthellWindRider", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_OTHELL_GHOST_HUNTER = formulasSettings.getProperty("mCritModifierOthellGhostHunter", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_OTHELL_FORTUNE_SEEKER = formulasSettings.getProperty("mCritModifierOthellFortuneSeeker", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_YR_SAGITTARIUS = formulasSettings.getProperty("mCritModifierYurSagitarius", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_YR_MOONLIGHT_SENTINEL = formulasSettings.getProperty("mCritModifierYurMoonLightSentinel", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_YR_GHOST_SENTINEL = formulasSettings.getProperty("mCritModifierYurGhostSentinel", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_YR_TRICKSTER = formulasSettings.getProperty("mCritModifierYurTrickster", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_FEOH_ARCHMAGE = formulasSettings.getProperty("mCritModifierFeohArchMage", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_FEOH_SOULTAKER = formulasSettings.getProperty("mCritModifierFeohSoultaker", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_FEOH_MYSTIC_MUSE = formulasSettings.getProperty("mCritModifierFeohMysticMuse", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_FEOH_STORM_SCREAMER = formulasSettings.getProperty("mCritModifierFeohStormScreamer", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_FEOH_SOUL_HOUND = formulasSettings.getProperty("mCritModifierFeohSoulHound", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_ISS_HIEROPHANT = formulasSettings.getProperty("mCritModifierIssHierophant", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_ISS_SWORD_MUSE = formulasSettings.getProperty("mCritModifierIssSwordMuse", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_ISS_SPECTRAL_DANCER = formulasSettings.getProperty("mCritModifierIssSpectralDancer", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_ISS_DOMINATOR = formulasSettings.getProperty("mCritModifierIssDominator", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_ISS_DOOMCRYER = formulasSettings.getProperty("mCritModifierIssDoomCryer", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_WYNN_ARCANA_LORD = formulasSettings.getProperty("mCritModifierWynnArcanaLord", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_WYNN_ELEMENTAL_MASTER = formulasSettings.getProperty("mCritModifierWynnElementalMaster", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_WYNN_SPECTRAL_MASTER = formulasSettings.getProperty("mCritModifierWynnSpectralMaster", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_AEORE_CARDINAL = formulasSettings.getProperty("mCritModifierAeoreCardinal", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_AEORE_EVAS_SAINT = formulasSettings.getProperty("mCritModifierAeoreEvasSaint", 1.0);
		ALT_M_CRIT_DAMAGE_MOD_AEORE_SHILLIEN_SAINT = formulasSettings.getProperty("mCritModifierAeoreShillenSaint", 1.0);			

		ALT_P_CRIT_DAMAGE_MOD = formulasSettings.getProperty("pCritModifier", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_SIGEL = formulasSettings.getProperty("pCritModifierSigel", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_TIR_WARRIOR = formulasSettings.getProperty("pCritModifierTyrWarrior", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_OTHEL_ROGUE = formulasSettings.getProperty("pCritModifierOthelRogue", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_YR_ARCHER = formulasSettings.getProperty("pCritModifierYrArcher", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_FEO_WIZZARD = formulasSettings.getProperty("pCritModifierFeoWizzard", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_ISS_ENCHANTER = formulasSettings.getProperty("pCritModifierIssEnchanter", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_WYN_SUMMONER = formulasSettings.getProperty("pCritModifierWynSummoner", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_EOL_HEALER = formulasSettings.getProperty("pCritModifierEolHealer", 1.0);
		//new
		ALT_P_CRIT_DAMAGE_MOD_SIGEL_PHOENIX_KNIGHT = formulasSettings.getProperty("pCritModifierSigelPhoenixKnight", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_SIGEL_HELL_KNIGHT = formulasSettings.getProperty("pCritModifierSigetHellKnight", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_SIGEL_EVAS_TEMPLAR = formulasSettings.getProperty("pCritModifierSigelEvasTemplar", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_SIGEL_SHILLIEN_TEMPLAR = formulasSettings.getProperty("pCritModifierSigelShillienTemplar", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_TYR_DUELIST = formulasSettings.getProperty("pCritModifierTyrDuelist", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_TYR_DREADNOUGHT = formulasSettings.getProperty("pCritModifierTyrDreadnought", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_TYR_TITAN = formulasSettings.getProperty("pCritModifierTyrTitan", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_TYR_GRAND_KHAVATARI = formulasSettings.getProperty("pCritModifierTyrGrandKhavatari", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_TYR_MAESTRO = formulasSettings.getProperty("pCritModifierTyrMaestro", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_TYR_DOOMBRINGER = formulasSettings.getProperty("pCritModifierTyrDoomBringer", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_OTHELL_ADVENTURER = formulasSettings.getProperty("pCritModifierOthellAdventurer", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_OTHELL_WIND_RIDER = formulasSettings.getProperty("pCritModifierOthellWindRider", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_OTHELL_GHOST_HUNTER = formulasSettings.getProperty("pCritModifierOthellGhostHunter", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_OTHELL_FORTUNE_SEEKER = formulasSettings.getProperty("pCritModifierOthellFortuneSeeker", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_YR_SAGITTARIUS = formulasSettings.getProperty("pCritModifierYurSagitarius", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_YR_MOONLIGHT_SENTINEL = formulasSettings.getProperty("pCritModifierYurMoonLightSentinel", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_YR_GHOST_SENTINEL = formulasSettings.getProperty("pCritModifierYurGhostSentinel", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_YR_TRICKSTER = formulasSettings.getProperty("pCritModifierYurTrickster", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_FEOH_ARCHMAGE = formulasSettings.getProperty("pCritModifierFeohArchMage", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_FEOH_SOULTAKER = formulasSettings.getProperty("pCritModifierFeohSoultaker", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_FEOH_MYSTIC_MUSE = formulasSettings.getProperty("pCritModifierFeohMysticMuse", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_FEOH_STORM_SCREAMER = formulasSettings.getProperty("pCritModifierFeohStormScreamer", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_FEOH_SOUL_HOUND = formulasSettings.getProperty("pCritModifierFeohSoulHound", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_ISS_HIEROPHANT = formulasSettings.getProperty("pCritModifierIssHierophant", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_ISS_SWORD_MUSE = formulasSettings.getProperty("pCritModifierIssSwordMuse", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_ISS_SPECTRAL_DANCER = formulasSettings.getProperty("pCritModifierIssSpectralDancer", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_ISS_DOMINATOR = formulasSettings.getProperty("pCritModifierIssDominator", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_ISS_DOOMCRYER = formulasSettings.getProperty("pCritModifierIssDoomCryer", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_WYNN_ARCANA_LORD = formulasSettings.getProperty("pCritModifierWynnArcanaLord", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_WYNN_ELEMENTAL_MASTER = formulasSettings.getProperty("pCritModifierWynnElementalMaster", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_WYNN_SPECTRAL_MASTER = formulasSettings.getProperty("pCritModifierWynnSpectralMaster", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_AEORE_CARDINAL = formulasSettings.getProperty("pCritModifierAeoreCardinal", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_AEORE_EVAS_SAINT = formulasSettings.getProperty("pCritModifierAeoreEvasSaint", 1.0);
		ALT_P_CRIT_DAMAGE_MOD_AEORE_SHILLIEN_SAINT = formulasSettings.getProperty("pCritModifierAeoreShillenSaint", 1.0);	
		
		//fiz only
		ALT_P_CRIT_DAMAGE_MOD_SIGEL_FIZ = formulasSettings.getProperty("pCritModifierSigelFizOnly", 1.0);	
		ALT_P_CRIT_DAMAGE_MOD_TIR_WARRIOR_FIZ = formulasSettings.getProperty("pCritModifierTyrFizOnly", 1.0);	
		ALT_P_CRIT_DAMAGE_MOD_OTHEL_ROGUE_FIZ = formulasSettings.getProperty("pCritModifierOthelFizOnly", 1.0);	
		ALT_P_CRIT_DAMAGE_MOD_YR_ARCHER_FIZ = formulasSettings.getProperty("pCritModifierYrArcherFizOnly", 1.0);	
		ALT_P_CRIT_DAMAGE_MOD_SIGEL_PHOENIX_KNIGHT_FIZ = formulasSettings.getProperty("pCritModifierSigelPhoenixFizOnly", 1.0);	
		ALT_P_CRIT_DAMAGE_MOD_SIGEL_HELL_KNIGHT_FIZ = formulasSettings.getProperty("pCritModifierSigelHellKnightFizOnly", 1.0);	
		ALT_P_CRIT_DAMAGE_MOD_SIGEL_EVAS_TEMPLAR_FIZ = formulasSettings.getProperty("pCritModifierSigelEvasTemplarFizOnly", 1.0);	
		ALT_P_CRIT_DAMAGE_MOD_SIGEL_SHILLIEN_TEMPLAR_FIZ = formulasSettings.getProperty("pCritModifierSigelShillenTemplarFizOnly", 1.0);	
		ALT_P_CRIT_DAMAGE_MOD_TYR_DUELIST_FIZ = formulasSettings.getProperty("pCritModifierTyrDuelistFizOnly", 1.0);	
		ALT_P_CRIT_DAMAGE_MOD_TYR_DREADNOUGHT_FIZ = formulasSettings.getProperty("pCritModifierTyrDreadnoughtFizOnly", 1.0);	
		ALT_P_CRIT_DAMAGE_MOD_TYR_TITAN_FIZ = formulasSettings.getProperty("pCritModifierTyrTitanFizOnly", 1.0);	
		ALT_P_CRIT_DAMAGE_MOD_TYR_GRAND_KHAVATARI_FIZ = formulasSettings.getProperty("pCritModifierTyrGrandKhavatariFizOnly", 1.0);	
		ALT_P_CRIT_DAMAGE_MOD_TYR_MAESTRO_FIZ = formulasSettings.getProperty("pCritModifierTyrMaestroFizOnly", 1.0);	
		ALT_P_CRIT_DAMAGE_MOD_TYR_DOOMBRINGER_FIZ = formulasSettings.getProperty("pCritModifierTyrDoombridgerFizOnly", 1.0);	
		ALT_P_CRIT_DAMAGE_MOD_OTHELL_ADVENTURER_FIZ = formulasSettings.getProperty("pCritModifierOthelAdventurerFizOnly", 1.0);	
		ALT_P_CRIT_DAMAGE_MOD_OTHELL_WIND_RIDER_FIZ = formulasSettings.getProperty("pCritModifierOthelWindriderFizOnly", 1.0);	
		ALT_P_CRIT_DAMAGE_MOD_OTHELL_GHOST_HUNTER_FIZ = formulasSettings.getProperty("pCritModifierOthelGhostHunterFizOnly", 1.0);	
		ALT_P_CRIT_DAMAGE_MOD_OTHELL_FORTUNE_SEEKER_FIZ = formulasSettings.getProperty("pCritModifierOthelFortuneseekerFizOnly", 1.0);	
		ALT_P_CRIT_DAMAGE_MOD_YR_SAGITTARIUS_FIZ = formulasSettings.getProperty("pCritModifierYurSagitariusFizOnly", 1.0);	
		ALT_P_CRIT_DAMAGE_MOD_YR_MOONLIGHT_SENTINEL_FIZ = formulasSettings.getProperty("pCritModifierYurMoonLightSentinelFizOnly", 1.0);	
		ALT_P_CRIT_DAMAGE_MOD_YR_GHOST_SENTINEL_FIZ = formulasSettings.getProperty("pCritModifierYurGhostSentinelFizOnly", 1.0);	
		ALT_P_CRIT_DAMAGE_MOD_YR_TRICKSTER_FIZ = formulasSettings.getProperty("pCritModifierYurTricksterFizOnly", 1.0);	
		ALT_P_CRIT_DAMAGE_MOD_FEOH_SOUL_HOUND_FIZ = formulasSettings.getProperty("pCritModifierFeohSoulHoundFizOnly", 1.0);	
		ALT_P_CRIT_DAMAGE_MOD_ISS_DOMINATOR_FIZ = formulasSettings.getProperty("pCritModifierIssDominatorFizOnly", 1.0);	
		ALT_P_CRIT_DAMAGE_MOD_ISS_DOOMCRYER_FIZ = formulasSettings.getProperty("pCritModifierIssDoomCryerFizOnly", 1.0);			
		
		ALT_P_CRIT_CHANCE_MOD = formulasSettings.getProperty("pCritModifierChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_SIGEL = formulasSettings.getProperty("pCritModifierSigelChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_TIR_WARRIOR = formulasSettings.getProperty("pCritModifierTyrWarriorChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_OTHEL_ROGUE = formulasSettings.getProperty("pCritModifierOthelRogueChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_YR_ARCHER = formulasSettings.getProperty("pCritModifierYrArcherChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_FEO_WIZZARD = formulasSettings.getProperty("pCritModifierFeoWizzardChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_ISS_ENCHANTER = formulasSettings.getProperty("pCritModifierIssEnchanterChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_WYN_SUMMONER = formulasSettings.getProperty("pCritModifierWynSummonerChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_EOL_HEALER = formulasSettings.getProperty("pCritModifierEolHealerChance", 1.0);		
		//new
		ALT_P_CRIT_CHANCE_MOD_SIGEL_PHOENIX_KNIGHT = formulasSettings.getProperty("pCritModifierSigelPhoenixKnightChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_SIGEL_HELL_KNIGHT = formulasSettings.getProperty("pCritModifierSigetHellKnightChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_SIGEL_EVAS_TEMPLAR = formulasSettings.getProperty("pCritModifierSigelEvasTemplarChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_SIGEL_SHILLIEN_TEMPLAR = formulasSettings.getProperty("pCritModifierSigelShillienTemplarChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_TYR_DUELIST = formulasSettings.getProperty("pCritModifierTyrDuelistChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_TYR_DREADNOUGHT = formulasSettings.getProperty("pCritModifierTyrDreadnoughtChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_TYR_TITAN = formulasSettings.getProperty("pCritModifierTyrTitanChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_TYR_GRAND_KHAVATARI = formulasSettings.getProperty("pCritModifierTyrGrandKhavatariChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_TYR_MAESTRO = formulasSettings.getProperty("pCritModifierTyrMaestroChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_TYR_DOOMBRINGER = formulasSettings.getProperty("pCritModifierTyrDoomBringerChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_OTHELL_ADVENTURER = formulasSettings.getProperty("pCritModifierOthellAdventurerChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_OTHELL_WIND_RIDER = formulasSettings.getProperty("pCritModifierOthellWindRiderChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_OTHELL_GHOST_HUNTER = formulasSettings.getProperty("pCritModifierOthellGhostHunterChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_OTHELL_FORTUNE_SEEKER = formulasSettings.getProperty("pCritModifierOthellFortuneSeekerChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_YR_SAGITTARIUS = formulasSettings.getProperty("pCritModifierYurSagitariusChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_YR_MOONLIGHT_SENTINEL = formulasSettings.getProperty("pCritModifierYurMoonLightSentinelChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_YR_GHOST_SENTINEL = formulasSettings.getProperty("pCritModifierYurGhostSentinelChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_YR_TRICKSTER = formulasSettings.getProperty("pCritModifierYurTricksterChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_FEOH_ARCHMAGE = formulasSettings.getProperty("pCritModifierFeohArchMageChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_FEOH_SOULTAKER = formulasSettings.getProperty("pCritModifierFeohSoultakerChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_FEOH_MYSTIC_MUSE = formulasSettings.getProperty("pCritModifierFeohMysticMuseChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_FEOH_STORM_SCREAMER = formulasSettings.getProperty("pCritModifierFeohStormScreamerChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_FEOH_SOUL_HOUND = formulasSettings.getProperty("pCritModifierFeohSoulHoundChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_ISS_HIEROPHANT = formulasSettings.getProperty("pCritModifierIssHierophantChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_ISS_SWORD_MUSE = formulasSettings.getProperty("pCritModifierIssSwordMuseChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_ISS_SPECTRAL_DANCER = formulasSettings.getProperty("pCritModifierIssSpectralDancerChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_ISS_DOMINATOR = formulasSettings.getProperty("pCritModifierIssDominatorChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_ISS_DOOMCRYER = formulasSettings.getProperty("pCritModifierIssDoomCryerChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_WYNN_ARCANA_LORD = formulasSettings.getProperty("pCritModifierWynnArcanaLordChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_WYNN_ELEMENTAL_MASTER = formulasSettings.getProperty("pCritModifierWynnElementalMasterChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_WYNN_SPECTRAL_MASTER = formulasSettings.getProperty("pCritModifierWynnSpectralMasterChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_AEORE_CARDINAL = formulasSettings.getProperty("pCritModifierAeoreCardinalChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_AEORE_EVAS_SAINT = formulasSettings.getProperty("pCritModifierAeoreEvasSaintChance", 1.0);
		ALT_P_CRIT_CHANCE_MOD_AEORE_SHILLIEN_SAINT = formulasSettings.getProperty("pCritModifierAeoreShillenSaintChance", 1.0);			
		
		ALT_M_CRIT_CHANCE_MOD = formulasSettings.getProperty("mCritModifierChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_SIGEL = formulasSettings.getProperty("mCritModifierSigelChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_TIR_WARRIOR = formulasSettings.getProperty("mCritModifierTyrWarriorChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_OTHEL_ROGUE = formulasSettings.getProperty("mCritModifierOthelRogueChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_YR_ARCHER = formulasSettings.getProperty("mCritModifierYrArcherChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_FEO_WIZZARD = formulasSettings.getProperty("mCritModifierFeoWizzardChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_ISS_ENCHANTER = formulasSettings.getProperty("mCritModifierIssEnchanterChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_WYN_SUMMONER = formulasSettings.getProperty("mCritModifierWynSummonerChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_EOL_HEALER = formulasSettings.getProperty("mCritModifierEolHealerChance", 1.0);	
		//new
		ALT_M_CRIT_CHANCE_MOD_SIGEL_PHOENIX_KNIGHT = formulasSettings.getProperty("mCritModifierSigelPhoenixKnightChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_SIGEL_HELL_KNIGHT = formulasSettings.getProperty("mCritModifierSigetHellKnightChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_SIGEL_EVAS_TEMPLAR = formulasSettings.getProperty("mCritModifierSigelEvasTemplarChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_SIGEL_SHILLIEN_TEMPLAR = formulasSettings.getProperty("mCritModifierSigelShillienTemplarChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_TYR_DUELIST = formulasSettings.getProperty("mCritModifierTyrDuelistChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_TYR_DREADNOUGHT = formulasSettings.getProperty("mCritModifierTyrDreadnoughtChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_TYR_TITAN = formulasSettings.getProperty("mCritModifierTyrTitanChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_TYR_GRAND_KHAVATARI = formulasSettings.getProperty("mCritModifierTyrGrandKhavatariChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_TYR_MAESTRO = formulasSettings.getProperty("mCritModifierTyrMaestroChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_TYR_DOOMBRINGER = formulasSettings.getProperty("mCritModifierTyrDoomBringerChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_OTHELL_ADVENTURER = formulasSettings.getProperty("mCritModifierOthellAdventurerChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_OTHELL_WIND_RIDER = formulasSettings.getProperty("mCritModifierOthellWindRiderChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_OTHELL_GHOST_HUNTER = formulasSettings.getProperty("mCritModifierOthellGhostHunterChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_OTHELL_FORTUNE_SEEKER = formulasSettings.getProperty("mCritModifierOthellFortuneSeekerChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_YR_SAGITTARIUS = formulasSettings.getProperty("mCritModifierYurSagitariusChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_YR_MOONLIGHT_SENTINEL = formulasSettings.getProperty("mCritModifierYurMoonLightSentinelChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_YR_GHOST_SENTINEL = formulasSettings.getProperty("mCritModifierYurGhostSentinelChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_YR_TRICKSTER = formulasSettings.getProperty("mCritModifierYurTricksterChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_FEOH_ARCHMAGE = formulasSettings.getProperty("mCritModifierFeohArchMageChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_FEOH_SOULTAKER = formulasSettings.getProperty("mCritModifierFeohSoultakerChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_FEOH_MYSTIC_MUSE = formulasSettings.getProperty("mCritModifierFeohMysticMuseChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_FEOH_STORM_SCREAMER = formulasSettings.getProperty("mCritModifierFeohStormScreamerChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_FEOH_SOUL_HOUND = formulasSettings.getProperty("mCritModifierFeohSoulHoundChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_ISS_HIEROPHANT = formulasSettings.getProperty("mCritModifierIssHierophantChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_ISS_SWORD_MUSE = formulasSettings.getProperty("mCritModifierIssSwordMuseChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_ISS_SPECTRAL_DANCER = formulasSettings.getProperty("mCritModifierIssSpectralDancerChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_ISS_DOMINATOR = formulasSettings.getProperty("mCritModifierIssDominatorChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_ISS_DOOMCRYER = formulasSettings.getProperty("mCritModifierIssDoomCryerChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_WYNN_ARCANA_LORD = formulasSettings.getProperty("mCritModifierWynnArcanaLordChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_WYNN_ELEMENTAL_MASTER = formulasSettings.getProperty("mCritModifierWynnElementalMasterChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_WYNN_SPECTRAL_MASTER = formulasSettings.getProperty("mCritModifierWynnSpectralMasterChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_AEORE_CARDINAL = formulasSettings.getProperty("mCritModifierAeoreCardinalChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_AEORE_EVAS_SAINT = formulasSettings.getProperty("mCritModifierAeoreEvasSaintChance", 1.0);
		ALT_M_CRIT_CHANCE_MOD_AEORE_SHILLIEN_SAINT = formulasSettings.getProperty("pCritModifierAeoreShillenSaintChance", 1.0);			
			
		ALT_BLOW_DAMAGE_MOD = formulasSettings.getProperty("blowDamageModifier", 1.0);
		ALT_BLOW_CRIT_RATE_MODIFIER = formulasSettings.getProperty("blowCritRateModifier", 1.0);
		ALT_VAMPIRIC_CHANCE = formulasSettings.getProperty("vampiricChance", 20.0);
		
		NOT_REBORN_CANNOT_DEBUFF_REBORNED = formulasSettings.getProperty("NotRebornedCannotDebuffRebornePlayer", false);
		NEW_CHANCE_FOR_NOT_REBORNED_SKILLS = formulasSettings.getProperty("NewStaticChanceForNotRebornedPlayers", 0);
		
		REFLECT_MIN_RANGE = formulasSettings.getProperty("ReflectMinimumRange", 600);
		REFLECT_AND_BLOCK_DAMAGE_CHANCE_CAP = formulasSettings.getProperty("reflectAndBlockDamCap", 60.);
		REFLECT_AND_BLOCK_PSKILL_DAMAGE_CHANCE_CAP = formulasSettings.getProperty("reflectAndBlockPSkillDamCap", 60.);
		REFLECT_AND_BLOCK_MSKILL_DAMAGE_CHANCE_CAP = formulasSettings.getProperty("reflectAndBlockMSkillDamCap", 60.);
		REFLECT_DAMAGE_PERCENT_CAP = formulasSettings.getProperty("reflectDamCap", 60.);
		REFLECT_BOW_DAMAGE_PERCENT_CAP = formulasSettings.getProperty("reflectBowDamCap", 60.);
		REFLECT_PSKILL_DAMAGE_PERCENT_CAP = formulasSettings.getProperty("reflectPSkillDamCap", 60.);
		REFLECT_MSKILL_DAMAGE_PERCENT_CAP = formulasSettings.getProperty("reflectMSkillDamCap", 60.);
		ENABLE_CUSTOM_STATS_SYSTEM = formulasSettings.getProperty("enableCustomStats", false);
		SPECIAL_CLASS_BOW_CROSS_BOW_PENALTY = formulasSettings.getProperty("specialClassesWeaponMagicSpeedPenalty", 1.);
		DISABLE_VAMPIRIC_VS_MOB_ON_PVP = formulasSettings.getProperty("disableVampiricAndDrainPvEInPvp", false);
		MIN_HIT_TIME = formulasSettings.getProperty("MinimumHitTime", -1);

		PHYSICAL_MIN_CHANCE_TO_HIT = formulasSettings.getProperty("PHYSICAL_MIN_CHANCE_TO_HIT", 27.5);
		PHYSICAL_MAX_CHANCE_TO_HIT = formulasSettings.getProperty("PHYSICAL_MAX_CHANCE_TO_HIT", 98.0);

		MAGIC_MIN_CHANCE_TO_HIT = formulasSettings.getProperty("MAGIC_MIN_CHANCE_TO_HIT", 72.5);
		MAGIC_MAX_CHANCE_TO_HIT = formulasSettings.getProperty("MAGIC_MAX_CHANCE_TO_HIT", 98.0);

		ENABLE_CRIT_DMG_REDUCTION_ON_MAGIC = formulasSettings.getProperty("ENABLE_CRIT_DMG_REDUCTION_ON_MAGIC", false);

		INT_SKILLMASTERY_MODIFIER = formulasSettings.getProperty("INT_SKILLMASTERY_MODIFIER", 2);
		STR_SKILLMASTERY_MODIFIER = formulasSettings.getProperty("STR_SKILLMASTERY_MODIFIER", 2);

		MAX_BLOW_RATE_ON_BEHIND = formulasSettings.getProperty("MAX_BLOW_RATE_ON_BEHIND", 100.);
		MAX_BLOW_RATE_ON_FRONT_AND_SIDE = formulasSettings.getProperty("MAX_BLOW_RATE_ON_FRONT_AND_SIDE", 80.);

		BLOW_SKILL_CHANCE_MOD_ON_BEHIND = formulasSettings.getProperty("BLOW_SKILL_CHANCE_MOD_ON_BEHIND", 5.);
		BLOW_SKILL_CHANCE_MOD_ON_FRONT = formulasSettings.getProperty("BLOW_SKILL_CHANCE_MOD_ON_FRONT", 4.);

		ENABLE_MATK_SKILL_LANDING_MOD = formulasSettings.getProperty("ENABLE_MATK_SKILL_LANDING_MOD", true);
		ENABLE_WIT_SKILL_LANDING_MOD = formulasSettings.getProperty("ENABLE_WIT_SKILL_LANDING_MOD", false);

		BLOW_SKILL_DEX_CHANCE_MOD = formulasSettings.getProperty("BLOW_SKILL_DEX_CHANCE_MOD", 1.);
		NORMAL_SKILL_DEX_CHANCE_MOD = formulasSettings.getProperty("NORMAL_SKILL_DEX_CHANCE_MOD", 1.);
	}

	public static void loadDevelopSettings()
	{
		ExProperties properties = load(DEVELOP_FILE);
	}

	public static void loadExtSettings()
	{
		ExProperties properties = load(EXT_FILE);

		EX_NEW_PETITION_SYSTEM = properties.getProperty("NewPetitionSystem", false);
		EX_JAPAN_MINIGAME = properties.getProperty("JapanMinigame", false);
		EX_LECTURE_MARK = properties.getProperty("LectureMark", false);

		EX_SECOND_AUTH_ENABLED = properties.getProperty("SecondAuthEnabled", false);
		EX_SECOND_AUTH_MAX_ATTEMPTS = properties.getProperty("SecondAuthMaxAttempts", 5);
		EX_SECOND_AUTH_BAN_TIME = properties.getProperty("SecondAuthBanTime", 480);
	}

	public static void loadBBSSettings()
	{
		ExProperties properties = load(BBS_FILE);

		COMMUNITYBOARD_ENABLED = properties.getProperty("AllowCommunityBoard", true);
		BBS_DEFAULT = properties.getProperty("BBSDefault", "_bbshome");

		BBS_BUFFER_ENABLED = properties.getProperty("AllowBBSBuffer", false);
		BBS_BUFF_ITEM_ID = properties.getProperty("BuffItemId", 57);
		BBS_BUFF_ITEM_COUNT = properties.getProperty("BuffItemCount", 1000);
		BBS_BUFF_FREE_LVL = properties.getProperty("FreeBuffLevel", 40);
		BBS_BUFF_TIME = properties.getProperty("BuffTime", 0);
		BBS_BUFF_TIME_MOD = properties.getProperty("BuffTimeMod", 1.0);
		//new configs
		MAX_BUFF_PER_SET = properties.getProperty("MaxBuffsPerSet", 3);
		BBS_BUFF_TIME_MUSIC = properties.getProperty("BuffTimeMusic", 0);
		BBS_BUFF_TIME_MOD_MUSIC = properties.getProperty("BuffTimeModMusic", 1.0);
		BBS_BUFF_TIME_SPECIAL = properties.getProperty("BuffTimeSpecial", 0);
		BBS_BUFF_TIME_MOD_SPECIAL = properties.getProperty("BuffTimeModSpecial", 1.0);
		MAX_SETS_PER_CHAR = properties.getProperty("MaximumSetsPerChar", 8);
		BBS_BUFF_SET = StringArrayUtils.stringToIntArray2X(properties.getProperty("BuffsSet", ""), ";", "-");

		ALLOW_TELEPORT_PK = properties.getProperty("AllowTeleportIfPK", true);
		TELEPORT_BM_COUNT_SAVE = properties.getProperty("TeleportCountIfSave", 1000);
		TELEPORT_BM_ITEM_SAVE = properties.getProperty("TeleportItemIdSave", 57);
		TELEPORT_BM_COUNT_GO = properties.getProperty("TeleportCountIfGo", 5000);
		TELEPORT_BM_ITEM_GO = properties.getProperty("TeleportItemIdGo", 57);
		MAX_BOOK_MARKS = properties.getProperty("MaximumBookMarks", 10);

		BBS_SERVICES_ENABLED = properties.getProperty("AllowBBSServices", false);
		BBS_HAIRSTYLE_ITEM_ID = properties.getProperty("HairstyleItemId", 57);
		BBS_HAIRSTYLE_ITEM_COUNT = properties.getProperty("HairstyleItemCount", 100000000);
		BBS_NAME_COLOR_ITEM_ID = properties.getProperty("NameColorItemId", 57);
		BBS_NAME_COLOR_ITEM_COUNT = properties.getProperty("NameColorItemCount", 400000000);

		BBS_TELEPORT_ENABLED = properties.getProperty("AllowBBSTeleport", false);
		BBS_TELEPORT_DEFAULT_ITEM_ID = properties.getProperty("TeleportDefaultItemId", 57);
		BBS_TELEPORT_DEFAULT_ITEM_COUNT = properties.getProperty("TeleportDefaultItemCount", 10000);

		ALLOW_PVPCB_ABNORMAL = properties.getProperty("AllowBBSAbnormal", false);
		CB_DEATH = properties.getProperty("AllowWhenDead", true);
		CB_ACTION = properties.getProperty("AllowWhenInAction", true);
		CB_OLY = properties.getProperty("AllowWhenInOlly", true);
		CB_CHAOS_FESTIVAL = properties.getProperty("AllowWhenInChaosFestival", true);
		CB_FLY = properties.getProperty("AllowWhenInFly", true);
		CB_VEICHLE = properties.getProperty("AllowWhenInVeichle", true);
		CB_MOUNTED = properties.getProperty("AllowWhenMounted", true);
		CB_CANT_MOVE = properties.getProperty("AllowWhenCantMove", true);
		CB_STORE_MODE = properties.getProperty("AllowWhenInTrade", true);
		CB_FISHING = properties.getProperty("AllowWhenFishing", true);
		CB_TEMP_ACTION = properties.getProperty("AllowWhenInTemp", true);
		CB_DUEL = properties.getProperty("AllowWhenInDuel", true);
		CB_CURSED = properties.getProperty("AllowWhenUseCursed", true);
		CB_PK = properties.getProperty("AllowWhenIsPk", true);
		CB_LEADER = properties.getProperty("AllowOnlyToClanLeader", false);
		CB_NOBLE = properties.getProperty("AllowOnlyToNoble", false);
		CB_TERITORY = properties.getProperty("AllowUseInTWPlayer", true);
		CB_PEACEZONE_ONLY = properties.getProperty("AllowUseOnlyInPeace", false);
		PROFF_4_COST_ITEM = properties.getProperty("FourthProffItemCount", 1000000);
		OCCUPATION4_COST_ITEM = properties.getProperty("FourthProffItemId", 57);
		ALLOW_FOURTH_OCCUPATION = properties.getProperty("AllowFourthProff", true);
		CAN_USE_BBS_IN_EVENTS = properties.getProperty("AllowUseCommunityInEvents", false);

		PROFF_1_COST =  properties.getProperty("FirstProffItemCount", 1000000);
		OCCUPATION1_COST_ITEM =  properties.getProperty("FirstProffItemId", 57);
		PROFF_2_COST =  properties.getProperty("SecondProffItemCount", 200000000);
		OCCUPATION2_COST_ITEM =  properties.getProperty("SecondProffItemId", 57);
		PROFF_3_COST_ITEM =  properties.getProperty("ThirdProffItemCount", 300000000);
		OCCUPATION3_COST_ITEM =  properties.getProperty("ThirdProffItemId", 57);
		SUB_CLASS_ITEM_ID =  properties.getProperty("SubClassItemId", 57);
		SUB_CLASS_COST_ITEM =  properties.getProperty("SubClassItemCount", 100000000);
		DUAL_CLASS_ITEM_ID =  properties.getProperty("DualclassClassItemId", 57);
		DUAL_CLASS_COST_ITEM =  properties.getProperty("DualclassItemCount", 100000000);
		NOBLESS_ITEM_ID =  properties.getProperty("NoblessItemId", 57);
		NOBLESS_COST_ITEM =  properties.getProperty("NoblessItemCount", 100000000);
		PK_KARMA_ITEM_ID =  properties.getProperty("PKKarmaItemId", 57);
		PK_KARMA_ITEM_COUNT =  properties.getProperty("PKKarmaItemCount", 100000000);
		PK_KARMA_REDUCE =  properties.getProperty("PKReduceCount", 1);
		EXPAND_CLAN_WH_ITEM_ID =  properties.getProperty("ExpandClanWHItemId", 57);
		EXPAND_CLAN_WH_ITEM_COUNT =  properties.getProperty("ExpandClanWHItemCount", 100000000);
		CLAN_WH_VALUE =  properties.getProperty("ExpandClanWHSlots", 1);
		EXPAND_WH_ITEM_ID =  properties.getProperty("ExpandWHItemId", 57);
		EXPAND_WH_ITEM_COUNT =  properties.getProperty("ExpandWHItemCount", 100000000);
		EXPEND_WH_VALUE =  properties.getProperty("ExpandWHSlots", 1);
		EXPAND_INVENTORY_ITEM_ID =  properties.getProperty("ExpandInventoryItemId", 57);
		EXPAND_INVENTORY_ITEM_COUNT =  properties.getProperty("ExpandInventoryItemCount", 100000000);
		EXPAND_INV_VALUE =  properties.getProperty("ExpandIventorySlots", 1);
		CHANGE_NICK_ITEM_ID =  properties.getProperty("ChangeNickItemId", 57);
		CHANGE_NICK_ITEM_COUNT =  properties.getProperty("ChangeNickItemCount", 100000000);
		CHANGE_NAME_CLAN_ITEM_ID =  properties.getProperty("ChangeClanNameItemId", 57);
		CHANGE_NAME_CLAN_ITEM_COUNT =  properties.getProperty("ChangeClanNameItemCount", 100000000);
		CHANGE_NICK_PET_ITEM_ID =  properties.getProperty("ChangePetNickItemId", 57);
		CHANGE_NICK_PET_ITEM_COUNT =  properties.getProperty("ChangePetNickItemCount", 100000000);
		SERVICE_SP_ADD =  properties.getProperty("SPAddValue", 5000);
		SERVICE_SP_ITEM_COUNT = properties.getProperty("SPAddItemCount", 100000000);
		SERVICE_SP_ITEM_ID = properties.getProperty("SPAddItemId", 57);

		ALLOW_OCCUPATION = properties.getProperty("AllowProffChange", false);
		ALLOW_FIRST_OCCUPATION = properties.getProperty("AllowFirstProffChange", false);
		ALLOW_SECOND_OCCUPATION = properties.getProperty("AllowSecondProffChange", false);
		ALLOW_THIRD_OCCUPATION = properties.getProperty("AllowThirdProffChange", false);
		ALLOW_SUB_CLASSES = properties.getProperty("AllowBuySubClasses", false);
		ALLOW_DUAL_CLASS = properties.getProperty("AllowBuyDualclass", false);
		ALLOW_NOBLESS = properties.getProperty("AllowBuyNobless", false);
		ALLOW_SP_ADD = properties.getProperty("AllowAddSp", false);
		ALLOW_KARMA_PK = properties.getProperty("AllowKarmaPkReduce", false);
		ALLOW_CHANGE_NAME = properties.getProperty("AllowChangeName", false);
		ALLOW_CHANCE_PET_NAME = properties.getProperty("AllowPetChangeName", false);
		ALLOW_CHANGE_CLAN_NAME = properties.getProperty("AllowClanChangeName", false);
		ALLOW_EXPEND_INVENTORY = properties.getProperty("AllowExpandInventory", false);
		ALLOW_EXPEND_WAREHOUSE = properties.getProperty("AllowExpandWarehouse", false);
		ALLOW_EXPEND_CLAN_WH = properties.getProperty("AllowExpandClanWH", false);
		ALLOW_SEX_CHANGE = properties.getProperty("allowSexChange", false);
		CHANGE_SEX_ITEM_ID = properties.getProperty("sexChangeItemId", 57);
		CHANGE_SEX_ITEM_COUNT = properties.getProperty("sexChangeItemCount", 10000);
		ALLOW_HAIR_STYLE_CHANGE = properties.getProperty("AllowHairStyleService", false);
		ALLOW_COLOR_NICK_CHANGE = properties.getProperty("AllowColorNickChange", false);
		
		ALLOW_HERO_BUY_SERVICE = properties.getProperty("AllowHeroBuyService", false);
		BBS_HERO_ITEM_ID = properties.getProperty("HeroServiceItemId", 4037);
		BBS_COUNT_HERO_ITEM = properties.getProperty("HeroServiceItemCount", 20);
		BBS_HERO_DAYS = properties.getProperty("HeroServiceDaysToHero", 1);

		ENCHANTER_ITEM_ID = properties.getProperty("CBEnchantItem", 4037);
		MAX_ENCHANT = properties.getProperty("CBEnchantItem", 20);
		ENCHANT_LEVELS = properties.getProperty("CBEnchantLvl", new int[] {1});
		ENCHANT_PRICE_WPN = properties.getProperty("CBEnchantPriceWeapon", new int[] {1});
		ENCHANT_PRICE_ARM = properties.getProperty("CBEnchantPriceArmor", new int[] {1});
		ENCHANT_ATTRIBUTE_LEVELS = properties.getProperty("CBEnchantAtributeLvlWeapon", new int[] {1});
		ENCHANT_ATTRIBUTE_LEVELS_ARM = properties.getProperty("CBEnchantAtributeLvlArmor", new int[] {1});
		ATTRIBUTE_PRICE_WPN = properties.getProperty("CBEnchantAtributePriceWeapon", new int[] {1});
		ATTRIBUTE_PRICE_ARM = properties.getProperty("CBEnchantAtributePriceArmor", new int[] {1});
		ENCHANT_ATT_PVP = properties.getProperty("CBEnchantAtributePvP", false);
		ALLOW_NON_STARDART_NICK_CHANGE = properties.getProperty("AllowNonStandartNickNameChange", false);
		ENABLE_LFC = properties.getProperty("EnableLFCEvent", false);
	}

	public static void loadAltSettings()
	{
		ExProperties altSettings = load(ALT_SETTINGS_FILE);

		STARTING_LVL = altSettings.getProperty("StartingLvl", 1);
		STARTING_SP = altSettings.getProperty("StartingSP", 0);

		ALT_ARENA_EXP = altSettings.getProperty("ArenaExp", true);
		ALT_GAME_DELEVEL = altSettings.getProperty("Delevel", true);
		ALT_SAVE_UNSAVEABLE = altSettings.getProperty("AltSaveUnsaveable", false);
		ALT_SAVE_EFFECTS_REMAINING_TIME = altSettings.getProperty("AltSaveEffectsRemainingTime", 5);
		ALT_SHOW_REUSE_MSG = altSettings.getProperty("AltShowSkillReuseMessage", true);
		ALT_DELETE_SA_BUFFS = altSettings.getProperty("AltDeleteSABuffs", false);
		AUTO_LOOT = altSettings.getProperty("AutoLoot", false);
		AUTO_LOOT_HERBS = altSettings.getProperty("AutoLootHerbs", false);
		AUTO_LOOT_ONLY_ADENA = altSettings.getProperty("AutoLootOnlyAdena", false);
		AUTO_LOOT_INDIVIDUAL = altSettings.getProperty("AutoLootIndividual", false);
		AUTO_LOOT_FROM_RAIDS = altSettings.getProperty("AutoLootFromRaids", false);
		
		ALLOW_FAKE_PEACE_ZONE_KILL = altSettings.getProperty("AllowKillFakesInPeaceZone", true);

		
		String[] autoLootItemIdList = altSettings.getProperty("AutoLootItemIdList", "-1").split(";");
		for(String item : autoLootItemIdList)
		{
			if(item == null || item.isEmpty())
				continue;

			try
			{
				int itemId = Integer.parseInt(item);
				if(itemId > 0)
					AUTO_LOOT_ITEM_ID_LIST.add(itemId);
			}
			catch(NumberFormatException e)
			{
				_log.error("", e);
			}
		}

		AUTO_LOOT_PK = altSettings.getProperty("AutoLootPK", false);
		ALT_GAME_KARMA_PLAYER_CAN_SHOP = altSettings.getProperty("AltKarmaPlayerCanShop", false);
		SAVING_SPS = altSettings.getProperty("SavingSpS", false);
		MANAHEAL_SPS_BONUS = altSettings.getProperty("ManahealSpSBonus", false);
		ALT_RAID_RESPAWN_MULTIPLIER = altSettings.getProperty("AltRaidRespawnMultiplier", 1.0);
		ALT_ALLOW_DROP_AUGMENTED = altSettings.getProperty("AlowDropAugmented", false);
		ALT_GAME_UNREGISTER_RECIPE = altSettings.getProperty("AltUnregisterRecipe", true);
		ALT_GAME_SHOW_DROPLIST = altSettings.getProperty("AltShowDroplist", true);
		ALLOW_NPC_SHIFTCLICK = altSettings.getProperty("AllowShiftClick", true);
		ALLOW_VOICED_COMMANDS = altSettings.getProperty("AllowVoicedCommands", true);
		ALT_FULL_NPC_STATS_PAGE = altSettings.getProperty("AltFullStatsPage", false);
		ALT_GAME_SUBCLASS_WITHOUT_QUESTS = altSettings.getProperty("AltAllowSubClassWithoutQuest", false);
		ALT_ALLOW_SUBCLASS_WITHOUT_BAIUM = altSettings.getProperty("AltAllowSubClassWithoutBaium", true);
		ALT_GAME_LEVEL_TO_GET_SUBCLASS = altSettings.getProperty("AltLevelToGetSubclass", 75);
		ALT_MAX_LEVEL = Math.min(altSettings.getProperty("AltMaxLevel", 99), Experience.LEVEL.length - 1);
		ALT_MAX_SUB_LEVEL = Math.min(altSettings.getProperty("AltMaxSubLevel", 80), Experience.LEVEL.length - 1);
		ALT_ALLOW_AWAKE_ON_SUB_CLASS = altSettings.getProperty("AltAllowAwakeOnSubClass", false);
		ALT_ALLOW_OTHERS_WITHDRAW_FROM_CLAN_WAREHOUSE = altSettings.getProperty("AltAllowOthersWithdrawFromClanWarehouse", false);
		ALT_ALLOW_CLAN_COMMAND_ONLY_FOR_CLAN_LEADER = altSettings.getProperty("AltAllowClanCommandOnlyForClanLeader", true);
		
		BAN_FOR_CFG_USAGE = altSettings.getProperty("BanForCfgUsageAgainsBots", false);

		ALT_GAME_REQUIRE_CLAN_CASTLE = altSettings.getProperty("AltRequireClanCastle", false);
		ALT_GAME_REQUIRE_CASTLE_DAWN = altSettings.getProperty("AltRequireCastleDawn", true);
		ALT_GAME_ALLOW_ADENA_DAWN = altSettings.getProperty("AltAllowAdenaDawn", true);
		ALT_ADD_RECIPES = altSettings.getProperty("AltAddRecipes", 0);
		SS_ANNOUNCE_PERIOD = altSettings.getProperty("SSAnnouncePeriod", 0);
		PETITIONING_ALLOWED = altSettings.getProperty("PetitioningAllowed", true);
		MAX_PETITIONS_PER_PLAYER = altSettings.getProperty("MaxPetitionsPerPlayer", 5);
		MAX_PETITIONS_PENDING = altSettings.getProperty("MaxPetitionsPending", 25);
		AUTO_LEARN_SKILLS = altSettings.getProperty("AutoLearnSkills", false);
		AUTO_LEARN_AWAKED_SKILLS = altSettings.getProperty("AutoLearnAwakedSkills", false);
		ALT_SOCIAL_ACTION_REUSE = altSettings.getProperty("AltSocialActionReuse", false);
		ALT_DISABLE_SPELLBOOKS = altSettings.getProperty("AltDisableSpellbooks", false);
		ALT_SIMPLE_SIGNS = altSettings.getProperty("PushkinSignsOptions", false);
		ALT_MAMMON_UPGRADE = altSettings.getProperty("MammonUpgrade", 6680500);
		ALT_MAMMON_EXCHANGE = altSettings.getProperty("MammonExchange", 10091400);
		ALT_BUFF_LIMIT = altSettings.getProperty("BuffLimit", 20);
		ALLOW_DEATH_PENALTY = altSettings.getProperty("EnableDeathPenalty", true);
		ALT_DEATH_PENALTY_CHANCE = altSettings.getProperty("DeathPenaltyChance", 10);
		ALT_DEATH_PENALTY_EXPERIENCE_PENALTY = altSettings.getProperty("DeathPenaltyRateExpPenalty", 1);
		ALT_DEATH_PENALTY_KARMA_PENALTY = altSettings.getProperty("DeathPenaltyC5RateKarma", 1);
		NONOWNER_ITEM_PICKUP_DELAY = altSettings.getProperty("NonOwnerItemPickupDelay", 15L) * 1000L;
		ALT_NO_LASTHIT = altSettings.getProperty("NoLasthitOnRaid", false);
		ALT_KAMALOKA_NIGHTMARES_PREMIUM_ONLY = altSettings.getProperty("KamalokaNightmaresPremiumOnly", false);
		ALT_KAMALOKA_NIGHTMARE_REENTER = altSettings.getProperty("SellReenterNightmaresTicket", true);
		ALT_KAMALOKA_ABYSS_REENTER = altSettings.getProperty("SellReenterAbyssTicket", true);
		ALT_KAMALOKA_LAB_REENTER = altSettings.getProperty("SellReenterLabyrinthTicket", true);
		ALT_PET_HEAL_BATTLE_ONLY = altSettings.getProperty("PetsHealOnlyInBattle", true);
		CHAR_TITLE = altSettings.getProperty("CharTitle", false);
		ADD_CHAR_TITLE = altSettings.getProperty("CharAddTitle", "");

		ALT_ALLOW_SELL_COMMON = altSettings.getProperty("AllowSellCommon", true);
		ALT_DISABLED_MULTISELL = altSettings.getProperty("DisabledMultisells", ArrayUtils.EMPTY_INT_ARRAY);
		ALT_SHOP_PRICE_LIMITS = altSettings.getProperty("ShopPriceLimits", ArrayUtils.EMPTY_INT_ARRAY);
		ALT_SHOP_UNALLOWED_ITEMS = altSettings.getProperty("ShopUnallowedItems", ArrayUtils.EMPTY_INT_ARRAY);

		ALT_ALLOWED_PET_POTIONS = altSettings.getProperty("AllowedPetPotions", new int[] { 735, 1060, 1061, 1062, 1374, 1375, 1539, 1540, 6035, 6036 });

		FESTIVAL_MIN_PARTY_SIZE = altSettings.getProperty("FestivalMinPartySize", 5);
		FESTIVAL_RATE_PRICE = altSettings.getProperty("FestivalRatePrice", 1.0);

		RIFT_MIN_PARTY_SIZE = altSettings.getProperty("RiftMinPartySize", 5);
		RIFT_SPAWN_DELAY = altSettings.getProperty("RiftSpawnDelay", 10000);
		RIFT_MAX_JUMPS = altSettings.getProperty("MaxRiftJumps", 4);
		RIFT_AUTO_JUMPS_TIME = altSettings.getProperty("AutoJumpsDelay", 8);
		RIFT_AUTO_JUMPS_TIME_RAND = altSettings.getProperty("AutoJumpsDelayRandom", 120000);

		RIFT_ENTER_COST_RECRUIT = altSettings.getProperty("RecruitFC", 18);
		RIFT_ENTER_COST_SOLDIER = altSettings.getProperty("SoldierFC", 21);
		RIFT_ENTER_COST_OFFICER = altSettings.getProperty("OfficerFC", 24);
		RIFT_ENTER_COST_CAPTAIN = altSettings.getProperty("CaptainFC", 27);
		RIFT_ENTER_COST_COMMANDER = altSettings.getProperty("CommanderFC", 30);
		RIFT_ENTER_COST_HERO = altSettings.getProperty("HeroFC", 33);
		ALLOW_CLANSKILLS = altSettings.getProperty("AllowClanSkills", true);
		ALLOW_LEARN_TRANS_SKILLS_WO_QUEST = altSettings.getProperty("AllowLearnTransSkillsWOQuest", false);
		PARTY_LEADER_ONLY_CAN_INVITE = altSettings.getProperty("PartyLeaderOnlyCanInvite", true);
		ALLOW_TALK_WHILE_SITTING = altSettings.getProperty("AllowTalkWhileSitting", true);
		ALLOW_NOBLE_TP_TO_ALL = altSettings.getProperty("AllowNobleTPToAll", false);

		BUFFTIME_MODIFIER = altSettings.getProperty("BuffTimeModifier", 1.0);
		BUFFTIME_MODIFIER_SKILLS = altSettings.getProperty("BuffTimeModifierSkills", new int[0]);
		CLANHALL_BUFFTIME_MODIFIER = altSettings.getProperty("ClanHallBuffTimeModifier", 1.0);
		SONGDANCETIME_MODIFIER = altSettings.getProperty("SongDanceTimeModifier", 1.0);
		MAXLOAD_MODIFIER = altSettings.getProperty("MaxLoadModifier", 1.0);
		GATEKEEPER_MODIFIER = altSettings.getProperty("GkCostMultiplier", 1.0);
		GATEKEEPER_FREE = altSettings.getProperty("GkFree", 76);
		CRUMA_GATEKEEPER_LVL = altSettings.getProperty("GkCruma", 65);
		ALT_IMPROVED_PETS_LIMITED_USE = altSettings.getProperty("ImprovedPetsLimitedUse", false);

		ALT_CHAMPION_CHANCE1 = altSettings.getProperty("AltChampionChance1", 0.);
		ALT_CHAMPION_CHANCE2 = altSettings.getProperty("AltChampionChance2", 0.);
		ALT_CHAMPION_CAN_BE_AGGRO = altSettings.getProperty("AltChampionAggro", false);
		ALT_CHAMPION_CAN_BE_SOCIAL = altSettings.getProperty("AltChampionSocial", false);
		ALT_CHAMPION_TOP_LEVEL = altSettings.getProperty("AltChampionTopLevel", 75);

		ALT_VITALITY_RATE = altSettings.getProperty("ALT_VITALITY_RATE", 200) / 100;
		ALT_VITALITY_PA_RATE = altSettings.getProperty("ALT_VITALITY_PA_RATE", 300) / 100;
		ALT_VITALITY_CONSUME_RATE = altSettings.getProperty("ALT_VITALITY_CONSUME_RATE", 1.);
		ALT_VITALITY_POTIONS_LIMIT = altSettings.getProperty("ALT_VITALITY_POTIONS_LIMIT", 5);
		ALT_VITALITY_POTIONS_PA_LIMIT = altSettings.getProperty("ALT_VITALITY_POTIONS_PA_LIMIT", 10);

		ALT_PCBANG_POINTS_ENABLED = altSettings.getProperty("AltPcBangPointsEnabled", false);
		ALT_PCBANG_POINTS_BONUS_DOUBLE_CHANCE = altSettings.getProperty("AltPcBangPointsDoubleChance", 10.);
		ALT_PCBANG_POINTS_BONUS = altSettings.getProperty("AltPcBangPointsBonus", 0);
		ALT_PCBANG_POINTS_DELAY = altSettings.getProperty("AltPcBangPointsDelay", 20);
		ALT_PCBANG_POINTS_MIN_LVL = altSettings.getProperty("AltPcBangPointsMinLvl", 1);

		ALT_DEBUG_ENABLED = altSettings.getProperty("AltDebugEnabled", false);
		ALT_DEBUG_PVP_ENABLED = altSettings.getProperty("AltDebugPvPEnabled", false);
		ALT_DEBUG_PVP_DUEL_ONLY = altSettings.getProperty("AltDebugPvPDuelOnly", true);
		ALT_DEBUG_PVE_ENABLED = altSettings.getProperty("AltDebugPvEEnabled", false);

		ALT_MAX_ALLY_SIZE = altSettings.getProperty("AltMaxAllySize", 3);
		ALT_PARTY_DISTRIBUTION_RANGE = altSettings.getProperty("AltPartyDistributionRange", 1500);
		ALT_PARTY_BONUS = altSettings.getProperty("AltPartyBonus", new double[] { 1.00, 1.10, 1.20, 1.30, 1.40, 1.50, 2.00, 2.10, 2.20 });

		ALT_ALL_PHYS_SKILLS_OVERHIT = altSettings.getProperty("AltAllPhysSkillsOverhit", true);
		ALT_REMOVE_SKILLS_ON_DELEVEL = altSettings.getProperty("AltRemoveSkillsOnDelevel", true);
		ALT_USE_BOW_REUSE_MODIFIER = altSettings.getProperty("AltUseBowReuseModifier", true);
		ALLOW_CH_DOOR_OPEN_ON_CLICK = altSettings.getProperty("AllowChDoorOpenOnClick", true);
		ALT_CH_ALL_BUFFS = altSettings.getProperty("AltChAllBuffs", false);
		ALT_CH_ALLOW_1H_BUFFS = altSettings.getProperty("AltChAllowHourBuff", false);
		ALT_CH_SIMPLE_DIALOG = altSettings.getProperty("AltChSimpleDialog", false);

		AUGMENTATION_NG_SKILL_CHANCE = altSettings.getProperty("AugmentationNGSkillChance", 15);
		AUGMENTATION_NG_GLOW_CHANCE = altSettings.getProperty("AugmentationNGGlowChance", 0);
		AUGMENTATION_MID_SKILL_CHANCE = altSettings.getProperty("AugmentationMidSkillChance", 30);
		AUGMENTATION_MID_GLOW_CHANCE = altSettings.getProperty("AugmentationMidGlowChance", 40);
		AUGMENTATION_HIGH_SKILL_CHANCE = altSettings.getProperty("AugmentationHighSkillChance", 45);
		AUGMENTATION_HIGH_GLOW_CHANCE = altSettings.getProperty("AugmentationHighGlowChance", 70);
		AUGMENTATION_TOP_SKILL_CHANCE = altSettings.getProperty("AugmentationTopSkillChance", 60);
		AUGMENTATION_TOP_GLOW_CHANCE = altSettings.getProperty("AugmentationTopGlowChance", 100);
		AUGMENTATION_BASESTAT_CHANCE = altSettings.getProperty("AugmentationBaseStatChance", 1);
		AUGMENTATION_ACC_SKILL_CHANCE = altSettings.getProperty("AugmentationAccSkillChance", 10);

		ALT_SHOW_SERVER_TIME = altSettings.getProperty("ShowServerTime", false);

		FOLLOW_RANGE = altSettings.getProperty("FollowRange", 100);

		ALT_ITEM_AUCTION_ENABLED = altSettings.getProperty("AltItemAuctionEnabled", true);
		ALT_CUSTOM_ITEM_AUCTION_ENABLED = ALT_ITEM_AUCTION_ENABLED && altSettings.getProperty("AltCustomItemAuctionEnabled", "").hashCode() == -538745924;
		ALT_ITEM_AUCTION_CAN_REBID = altSettings.getProperty("AltItemAuctionCanRebid", false);
		ALT_ITEM_AUCTION_START_ANNOUNCE = altSettings.getProperty("AltItemAuctionAnnounce", true);
		ALT_ITEM_AUCTION_BID_ITEM_ID = altSettings.getProperty("AltItemAuctionBidItemId", 57);
		ALT_ITEM_AUCTION_MAX_BID = altSettings.getProperty("AltItemAuctionMaxBid", 1000000L);
		ALT_ITEM_AUCTION_MAX_CANCEL_TIME_IN_MILLIS = altSettings.getProperty("AltItemAuctionMaxCancelTimeInMillis", 604800000);

		ALT_FISH_CHAMPIONSHIP_ENABLED = altSettings.getProperty("AltFishChampionshipEnabled", true);
		ALT_FISH_CHAMPIONSHIP_REWARD_ITEM = altSettings.getProperty("AltFishChampionshipRewardItemId", 57);
		ALT_FISH_CHAMPIONSHIP_REWARD_1 = altSettings.getProperty("AltFishChampionshipReward1", 800000);
		ALT_FISH_CHAMPIONSHIP_REWARD_2 = altSettings.getProperty("AltFishChampionshipReward2", 500000);
		ALT_FISH_CHAMPIONSHIP_REWARD_3 = altSettings.getProperty("AltFishChampionshipReward3", 300000);
		ALT_FISH_CHAMPIONSHIP_REWARD_4 = altSettings.getProperty("AltFishChampionshipReward4", 200000);
		ALT_FISH_CHAMPIONSHIP_REWARD_5 = altSettings.getProperty("AltFishChampionshipReward5", 100000);

		ALT_ENABLE_BLOCK_CHECKER_EVENT = altSettings.getProperty("EnableBlockCheckerEvent", true);
		ALT_MIN_BLOCK_CHECKER_TEAM_MEMBERS = Math.min(Math.max(altSettings.getProperty("BlockCheckerMinOlympiadMembers", 1), 1), 6);
		ALT_RATE_COINS_REWARD_BLOCK_CHECKER = altSettings.getProperty("BlockCheckerRateCoinReward", 1.);

		ALT_HBCE_FAIR_PLAY = altSettings.getProperty("HBCEFairPlay", false);

		ALT_PET_INVENTORY_LIMIT = altSettings.getProperty("AltPetInventoryLimit", 12);
		
		ALLOW_FAKE_PLAYERS = altSettings.getProperty("AllowFake", false);
		FAKE_PLAYERS_PERCENT = altSettings.getProperty("FakePercent", 0);

		ALLOW_DUELS = altSettings.getProperty("AllowDuels", true);
		DISABLE_CRYSTALIZATION_ITEMS = altSettings.getProperty("DisableCrystalizationItems", false);
		
		SUB_START_LEVEL = altSettings.getProperty("SubClassStartLevel", 40);
		START_CLAN_LEVEL = altSettings.getProperty("ClanStartLevel", 1);
		NEW_CHAR_IS_NOBLE = altSettings.getProperty("IsNewCharNoble", false);
		ENABLE_L2_TOP_OVERONLINE = altSettings.getProperty("EnableL2TOPFakeOnline", false);
		L2TOP_MAX_ONLINE = altSettings.getProperty("L2TOPMaxOnline", 3000);
		MIN_ONLINE_0_5_AM = altSettings.getProperty("MinOnlineFrom00to05", 500);
		MAX_ONLINE_0_5_AM = altSettings.getProperty("MaxOnlineFrom00to05", 700);
		MIN_ONLINE_6_11_AM = altSettings.getProperty("MinOnlineFrom06to11", 700);
		MAX_ONLINE_6_11_AM = altSettings.getProperty("MaxOnlineFrom06to11", 1000);
		MIN_ONLINE_12_6_PM = altSettings.getProperty("MinOnlineFrom12to18", 1000);
		MAX_ONLINE_12_6_PM = altSettings.getProperty("MaxOnlineFrom12to18", 1500);
		MIN_ONLINE_7_11_PM = altSettings.getProperty("MinOnlineFrom19to23", 1500);
		MAX_ONLINE_7_11_PM = altSettings.getProperty("MaxOnlineFrom19to23", 2500);
		ADD_ONLINE_ON_SIMPLE_DAY = altSettings.getProperty("AddOnlineIfSimpleDay", 50);
		ADD_ONLINE_ON_WEEKEND = altSettings.getProperty("AddOnlineIfWeekend", 300);
		L2TOP_MIN_TRADERS = altSettings.getProperty("L2TOPMinTraders", 80);
		L2TOP_MAX_TRADERS = altSettings.getProperty("L2TOPMaxTraders", 190);
		ENERGY_OF_DESTRUCTION_CHANCE = altSettings.getProperty("EnergyOfDestructionDropChance", 2);
		ENERGY_OF_DESTRUCTION_DOUBLE_CHANCE = altSettings.getProperty("EnergyOfDestructionDropDoubleChance", 5);
		ALT_SELL_ITEM_ONE_ADENA = altSettings.getProperty("AltSellItemOneAdena", false);
		ENABLE_TAUTI_FREE_ENTRANCE = altSettings.getProperty("EnableTautiWithOutSOH", false);

		MAX_SIEGE_CLANS = altSettings.getProperty("MaxSiegeClans", 20);	
		ONLY_ONE_SIEGE_PER_CLAN = altSettings.getProperty("OneClanCanRegisterOnOneSiege", false);	
		CLAN_WAR_MINIMUM_CLAN_LEVEL = altSettings.getProperty("AltClanWarMinimumClanLevel", 3);	
		CLAN_WAR_MINIMUM_PLAYERS_DECLARE = altSettings.getProperty("AltClanWarMinimumPlayersInClan", 15);	
		
		LIST_OF_SELLABLE_ITEMS = new ArrayList<Integer>();
		for(int id : altSettings.getProperty("ListOfAlwaysSellableItems", new int[] {57}))
			LIST_OF_SELLABLE_ITEMS.add(id);		
		LIST_OF_TRABLE_ITEMS = new ArrayList<Integer>();		
		for(int id : altSettings.getProperty("ListOfAlwaysTradableItems", new int[] {57}))
			LIST_OF_TRABLE_ITEMS.add(id);		

		ALLOW_USE_DOORMANS_IN_SIEGE_BY_OWNERS = altSettings.getProperty("AllowUseDoormansInSiegeByOwners", true);

		NPC_RANDOM_ENCHANT = altSettings.getProperty("NpcRandomEnchant", false);
		ENABLE_PARTY_SEARCH = altSettings.getProperty("AllowPartySearch", false);
		MENTOR_ONLY_PA = altSettings.getProperty("MentorServiceOnlyForPremium", false);

		ALT_SHOW_MONSTERS_AGRESSION = altSettings.getProperty("AltShowMonstersAgression", false);
		ALT_SHOW_MONSTERS_LVL = altSettings.getProperty("AltShowMonstersLvL", false);

		ALT_TELEPORT_TO_TOWN_DURING_SIEGE = altSettings.getProperty("ALT_TELEPORT_TO_TOWN_DURING_SIEGE", true);

		ALT_CLAN_LEAVE_PENALTY_TIME = altSettings.getProperty("ALT_CLAN_LEAVE_PENALTY_TIME", 24);
		ALT_CLAN_CREATE_PENALTY_TIME = altSettings.getProperty("ALT_CLAN_CREATE_PENALTY_TIME", 240);

		ALT_EXPELLED_MEMBER_PENALTY_TIME = altSettings.getProperty("ALT_EXPELLED_MEMBER_PENALTY_TIME", 24);
		ALT_LEAVED_ALLY_PENALTY_TIME = altSettings.getProperty("ALT_LEAVED_ALLY_PENALTY_TIME", 24);
		ALT_DISSOLVED_ALLY_PENALTY_TIME = altSettings.getProperty("ALT_DISSOLVED_ALLY_PENALTY_TIME", 24);
		
		BAN_FOR_ACCOUNT_SWITCH_TIMES = altSettings.getProperty("BanAccountIfExceedManyTimesCount", -1);
		MIN_DELAY_TO_COUNT_SWITCH = altSettings.getProperty("MinimumDelayBetweenSwitchesToCount", 5);
		JAIL_TIME_FOR_ACC_SWITCH = altSettings.getProperty("JailMinutesTimeIfExceededSwitches", 5);
		
		MIN_RAID_LEVEL_TO_DROP = altSettings.getProperty("MinRaidLevelToDropItem", 0);
		
		RAID_DROP_GLOBAL_ITEMS = altSettings.getProperty("AltEnableGlobalRaidDrop", false);
		String[] infos = altSettings.getProperty("RaidGlobalDrop", new String[0], ";");
		for(String info : infos) 
		{
			if(info.isEmpty())
				continue;

			String[] data = info.split(",");
			int id = Integer.parseInt(data[0]);
			long count = Long.parseLong(data[1]);
			double chance = Double.parseDouble(data[2]);
			RAID_GLOBAL_DROP.add(new RaidGlobalDrop(id, count, chance));
		}

		NPC_DIALOG_PLAYER_DELAY = altSettings.getProperty("NpcDialogPlayerDelay", 1);
	}

	public static void loadServicesSettings()
	{
		ExProperties servicesSettings = load(SERVICES_FILE);

		ALLOW_CLASS_MASTERS_LIST.clear();
		String allowClassMasters = servicesSettings.getProperty("AllowClassMasters", "false");
		if(!allowClassMasters.equalsIgnoreCase("false"))
		{
			String[] allowClassLvls = allowClassMasters.split(";");
			for(String allowClassLvl : allowClassLvls)
			{
				String[] allosClassLvlInfo = allowClassLvl.split(",");
				int classLvl = Integer.parseInt(allosClassLvlInfo[0]);
				if(ALLOW_CLASS_MASTERS_LIST.containsKey(classLvl))
					continue;

				int[] needItemInfo = new int[]{ 0, 0 };
				if(allosClassLvlInfo.length >= 3)
					needItemInfo = new int[]{ Integer.parseInt(allosClassLvlInfo[1]), Integer.parseInt(allosClassLvlInfo[2]) };
				ALLOW_CLASS_MASTERS_LIST.put(classLvl, needItemInfo);
			}
		}

		SERVICES_CHANGE_NICK_ENABLED = servicesSettings.getProperty("NickChangeEnabled", false);
		SERVICES_CHANGE_NICK_PRICE = servicesSettings.getProperty("NickChangePrice", 100);
		SERVICES_CHANGE_NICK_ITEM = servicesSettings.getProperty("NickChangeItem", 4037);
		SERVICES_CHANGE_PASSWORD = servicesSettings.getProperty("ChangePassword", true);
		
		SERVICES_CHANGE_CLAN_NAME_ENABLED = servicesSettings.getProperty("ClanNameChangeEnabled", false);
		SERVICES_CHANGE_CLAN_NAME_PRICE = servicesSettings.getProperty("ClanNameChangePrice", 100);
		SERVICES_CHANGE_CLAN_NAME_ITEM = servicesSettings.getProperty("ClanNameChangeItem", 4037);
		ALLOW_TOTAL_ONLINE = servicesSettings.getProperty("AllowVoiceCommandOnline", false);
		ALLOW_ONLINE_PARSE = servicesSettings.getProperty("AllowParsTotalOnline", false);
		FIRST_UPDATE = servicesSettings.getProperty("FirstOnlineUpdate", 1);
		DELAY_UPDATE = servicesSettings.getProperty("OnlineUpdate", 5);

		SERVICES_CHANGE_PET_NAME_ENABLED = servicesSettings.getProperty("PetNameChangeEnabled", false);
		SERVICES_CHANGE_PET_NAME_PRICE = servicesSettings.getProperty("PetNameChangePrice", 100);
		SERVICES_CHANGE_PET_NAME_ITEM = servicesSettings.getProperty("PetNameChangeItem", 4037);

		SERVICES_EXCHANGE_BABY_PET_ENABLED = servicesSettings.getProperty("BabyPetExchangeEnabled", false);
		SERVICES_EXCHANGE_BABY_PET_PRICE = servicesSettings.getProperty("BabyPetExchangePrice", 100);
		SERVICES_EXCHANGE_BABY_PET_ITEM = servicesSettings.getProperty("BabyPetExchangeItem", 4037);

		SERVICES_CHANGE_SEX_ENABLED = servicesSettings.getProperty("SexChangeEnabled", false);
		SERVICES_CHANGE_SEX_PRICE = servicesSettings.getProperty("SexChangePrice", 100);
		SERVICES_CHANGE_SEX_ITEM = servicesSettings.getProperty("SexChangeItem", 4037);

		SERVICES_CHANGE_BASE_ENABLED = servicesSettings.getProperty("BaseChangeEnabled", false);
		SERVICES_CHANGE_BASE_PRICE = servicesSettings.getProperty("BaseChangePrice", 100);
		SERVICES_CHANGE_BASE_ITEM = servicesSettings.getProperty("BaseChangeItem", 4037);

		SERVICES_SEPARATE_SUB_ENABLED = servicesSettings.getProperty("SeparateSubEnabled", false);
		SERVICES_SEPARATE_SUB_PRICE = servicesSettings.getProperty("SeparateSubPrice", 100);
		SERVICES_SEPARATE_SUB_ITEM = servicesSettings.getProperty("SeparateSubItem", 4037);

		SERVICES_CHANGE_NICK_COLOR_ENABLED = servicesSettings.getProperty("NickColorChangeEnabled", false);
		SERVICES_CHANGE_NICK_COLOR_PRICE = servicesSettings.getProperty("NickColorChangePrice", 100);
		SERVICES_CHANGE_NICK_COLOR_ITEM = servicesSettings.getProperty("NickColorChangeItem", 4037);
		SERVICES_CHANGE_NICK_COLOR_LIST = servicesSettings.getProperty("NickColorChangeList", new String[] { "00FF00" });

		SERVICES_BASH_ENABLED = servicesSettings.getProperty("BashEnabled", false);
		SERVICES_BASH_SKIP_DOWNLOAD = servicesSettings.getProperty("BashSkipDownload", false);
		SERVICES_BASH_RELOAD_TIME = servicesSettings.getProperty("BashReloadTime", 24);

		SERVICES_RATE_TYPE = servicesSettings.getProperty("RateBonusType", Bonus.NO_BONUS);
		SERVICES_RATE_BONUS_PRICE = servicesSettings.getProperty("RateBonusPrice", new int[] { 1500 });
		SERVICES_RATE_BONUS_ITEM = servicesSettings.getProperty("RateBonusItem", new int[] { 4037 });
		SERVICES_RATE_BONUS_VALUE = servicesSettings.getProperty("RateBonusValue", new int[] { 1 });
		SERVICES_RATE_BONUS_DAYS = servicesSettings.getProperty("RateBonusTime", new int[] { 30 });

		SERVICES_NOBLESS_SELL_ENABLED = servicesSettings.getProperty("NoblessSellEnabled", false);
		SERVICES_NOBLESS_SELL_PRICE = servicesSettings.getProperty("NoblessSellPrice", 1000);
		SERVICES_NOBLESS_SELL_ITEM = servicesSettings.getProperty("NoblessSellItem", 4037);

		SERVICES_EXPAND_INVENTORY_ENABLED = servicesSettings.getProperty("ExpandInventoryEnabled", false);
		SERVICES_EXPAND_INVENTORY_PRICE = servicesSettings.getProperty("ExpandInventoryPrice", 1000);
		SERVICES_EXPAND_INVENTORY_ITEM = servicesSettings.getProperty("ExpandInventoryItem", 4037);
		SERVICES_EXPAND_INVENTORY_MAX = servicesSettings.getProperty("ExpandInventoryMax", 250);

		SERVICES_EXPAND_WAREHOUSE_ENABLED = servicesSettings.getProperty("ExpandWarehouseEnabled", false);
		SERVICES_EXPAND_WAREHOUSE_PRICE = servicesSettings.getProperty("ExpandWarehousePrice", 1000);
		SERVICES_EXPAND_WAREHOUSE_ITEM = servicesSettings.getProperty("ExpandWarehouseItem", 4037);

		SERVICES_EXPAND_CWH_ENABLED = servicesSettings.getProperty("ExpandCWHEnabled", false);
		SERVICES_EXPAND_CWH_PRICE = servicesSettings.getProperty("ExpandCWHPrice", 1000);
		SERVICES_EXPAND_CWH_ITEM = servicesSettings.getProperty("ExpandCWHItem", 4037);

		SERVICES_SELLPETS = servicesSettings.getProperty("SellPets", "");

		SERVICES_OFFLINE_TRADE_ALLOW = servicesSettings.getProperty("AllowOfflineTrade", false);
		SERVICES_OFFLINE_TRADE_ALLOW_OFFSHORE = servicesSettings.getProperty("AllowOfflineTradeOnlyOffshore", true);
		SERVICES_OFFLINE_TRADE_MIN_LEVEL = servicesSettings.getProperty("OfflineMinLevel", 0);
		SERVICES_OFFLINE_TRADE_NAME_COLOR = Integer.decode("0x" + servicesSettings.getProperty("OfflineTradeNameColor", "B0FFFF"));
		SERVICES_OFFLINE_TRADE_PRICE_ITEM = servicesSettings.getProperty("OfflineTradePriceItem", 0);
		SERVICES_OFFLINE_TRADE_PRICE = servicesSettings.getProperty("OfflineTradePrice", 0);
		SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK = servicesSettings.getProperty("OfflineTradeDaysToKick", 14) * 86400L;
		SERVICES_OFFLINE_TRADE_RESTORE_AFTER_RESTART = servicesSettings.getProperty("OfflineRestoreAfterRestart", true);

		SERVICES_NO_TRADE_ONLY_OFFLINE = servicesSettings.getProperty("NoTradeOnlyOffline", false);
		SERVICES_TRADE_TAX = servicesSettings.getProperty("TradeTax", 0.0);
		SERVICES_OFFSHORE_TRADE_TAX = servicesSettings.getProperty("OffshoreTradeTax", 0.0);
		SERVICES_TRADE_TAX_ONLY_OFFLINE = servicesSettings.getProperty("TradeTaxOnlyOffline", false);
		SERVICES_OFFSHORE_NO_CASTLE_TAX = servicesSettings.getProperty("NoCastleTaxInOffshore", false);
		SERVICES_TRADE_ONLY_FAR = servicesSettings.getProperty("TradeOnlyFar", false);
		SERVICES_TRADE_MIN_LEVEL = servicesSettings.getProperty("MinLevelForTrade", 0);
		SERVICES_TRADE_RADIUS = servicesSettings.getProperty("TradeRadius", 30);

		SERVICES_GIRAN_HARBOR_ENABLED = servicesSettings.getProperty("GiranHarborZone", false);
		SERVICES_PARNASSUS_ENABLED = servicesSettings.getProperty("ParnassusZone", false);
		SERVICES_PARNASSUS_NOTAX = servicesSettings.getProperty("ParnassusNoTax", false);
		SERVICES_PARNASSUS_PRICE = servicesSettings.getProperty("ParnassusPrice", 500000);

		SERVICES_ALLOW_LOTTERY = servicesSettings.getProperty("AllowLottery", false);
		SERVICES_LOTTERY_PRIZE = servicesSettings.getProperty("LotteryPrize", 50000);
		SERVICES_ALT_LOTTERY_PRICE = servicesSettings.getProperty("AltLotteryPrice", 2000);
		SERVICES_LOTTERY_TICKET_PRICE = servicesSettings.getProperty("LotteryTicketPrice", 2000);
		SERVICES_LOTTERY_5_NUMBER_RATE = servicesSettings.getProperty("Lottery5NumberRate", 0.6);
		SERVICES_LOTTERY_4_NUMBER_RATE = servicesSettings.getProperty("Lottery4NumberRate", 0.4);
		SERVICES_LOTTERY_3_NUMBER_RATE = servicesSettings.getProperty("Lottery3NumberRate", 0.2);
		SERVICES_LOTTERY_2_AND_1_NUMBER_PRIZE = servicesSettings.getProperty("Lottery2and1NumberPrize", 200);

		SERVICES_ALLOW_ROULETTE = servicesSettings.getProperty("AllowRoulette", false);
		SERVICES_ROULETTE_MIN_BET = servicesSettings.getProperty("RouletteMinBet", 1L);
		SERVICES_ROULETTE_MAX_BET = servicesSettings.getProperty("RouletteMaxBet", Long.MAX_VALUE);

		SERVICES_ENABLE_NO_CARRIER = servicesSettings.getProperty("EnableNoCarrier", false);
		SERVICES_NO_CARRIER_MIN_TIME = servicesSettings.getProperty("NoCarrierMinTime", 0);
		SERVICES_NO_CARRIER_MAX_TIME = servicesSettings.getProperty("NoCarrierMaxTime", 90);
		SERVICES_NO_CARRIER_DEFAULT_TIME = servicesSettings.getProperty("NoCarrierDefaultTime", 60);

		ALLOW_EVENT_GATEKEEPER = servicesSettings.getProperty("AllowEventGatekeeper", false);
		
		SERVICES_ENCHANT_VALUE = servicesSettings.getProperty("EnchantValue", new int[] { 0 });
		SERVICES_ENCHANT_COAST = servicesSettings.getProperty("EnchantCoast", new int[] { 0 });
		SERVICES_ENCHANT_RAID_VALUE = servicesSettings.getProperty("EnchantRaidValue", new int[] { 0 });
		SERVICES_ENCHANT_RAID_COAST = servicesSettings.getProperty("EnchantRaidCoast", new int[] { 0 });
		
		ALLOW_IP_LOCK = servicesSettings.getProperty("AllowLockIP", false);
		ALLOW_HWID_LOCK = servicesSettings.getProperty("AllowLockHwid", false);
		HWID_LOCK_MASK = servicesSettings.getProperty("HwidLockMask", 10);		
		
	}

	public static void loadPvPSettings()
	{
		ExProperties pvpSettings = load(PVP_CONFIG_FILE);

		/* KARMA SYSTEM */
		KARMA_MIN_KARMA = pvpSettings.getProperty("MinKarma", 720);
		KARMA_RATE_KARMA_LOST = pvpSettings.getProperty("RateKarmaLost", -1);
		KARMA_LOST_BASE = pvpSettings.getProperty("BaseKarmaLost", 1200);

		KARMA_DROP_GM = pvpSettings.getProperty("CanGMDropEquipment", false);
		KARMA_NEEDED_TO_DROP = pvpSettings.getProperty("KarmaNeededToDrop", true);
		DROP_ITEMS_ON_DIE = pvpSettings.getProperty("DropOnDie", false);
		DROP_ITEMS_AUGMENTED = pvpSettings.getProperty("DropAugmented", false);

		KARMA_DROP_ITEM_LIMIT = pvpSettings.getProperty("MaxItemsDroppable", 10);
		MIN_PK_TO_ITEMS_DROP = pvpSettings.getProperty("MinPKToDropItems", 31);

		KARMA_RANDOM_DROP_LOCATION_LIMIT = pvpSettings.getProperty("MaxDropThrowDistance", 70);

		KARMA_DROPCHANCE_BASE = pvpSettings.getProperty("ChanceOfPKDropBase", 20.);
		KARMA_DROPCHANCE_MOD = pvpSettings.getProperty("ChanceOfPKsDropMod", 1.);
		NORMAL_DROPCHANCE_BASE = pvpSettings.getProperty("ChanceOfNormalDropBase", 1.);
		DROPCHANCE_EQUIPPED_WEAPON = pvpSettings.getProperty("ChanceOfDropWeapon", 3);
		DROPCHANCE_EQUIPMENT = pvpSettings.getProperty("ChanceOfDropEquippment", 17);
		DROPCHANCE_ITEM = pvpSettings.getProperty("ChanceOfDropOther", 80);
			
		KARMA_LIST_NONDROPPABLE_ITEMS = new ArrayList<Integer>();
		for(int id : pvpSettings.getProperty("ListOfNonDroppableItems", new int[] {
				57,
				1147,
				425,
				1146,
				461,
				10,
				2368,
				7,
				6,
				2370,
				2369,
				3500,
				3501,
				3502,
				4422,
				4423,
				4424,
				2375,
				6648,
				6649,
				6650,
				6842,
				6834,
				6835,
				6836,
				6837,
				6838,
				6839,
				6840,
				5575,
				7694,
				6841,
				8181 }))
			KARMA_LIST_NONDROPPABLE_ITEMS.add(id);

		PVP_TIME = pvpSettings.getProperty("PvPTime", 40000);
		RATE_KARMA_LOST_STATIC = pvpSettings.getProperty("KarmaLostStaticValue", -1);
	}

	public static void loadAISettings()
	{
		ExProperties aiSettings = load(AI_CONFIG_FILE);

		AI_TASK_MANAGER_COUNT = aiSettings.getProperty("AiTaskManagers", 1);
		AI_TASK_ATTACK_DELAY = aiSettings.getProperty("AiTaskDelay", 1000);
		AI_TASK_ACTIVE_DELAY = aiSettings.getProperty("AiTaskActiveDelay", 1000);
		BLOCK_ACTIVE_TASKS = aiSettings.getProperty("BlockActiveTasks", false);
		ALWAYS_TELEPORT_HOME = aiSettings.getProperty("AlwaysTeleportHome", false);

		RND_WALK = aiSettings.getProperty("RndWalk", true);
		RND_WALK_RATE = aiSettings.getProperty("RndWalkRate", 1);
		RND_ANIMATION_RATE = aiSettings.getProperty("RndAnimationRate", 2);

		AGGRO_CHECK_INTERVAL = aiSettings.getProperty("AggroCheckInterval", 250);
		NONAGGRO_TIME_ONTELEPORT = aiSettings.getProperty("NonAggroTimeOnTeleport", 15000);
		NONPVP_TIME_ONTELEPORT = aiSettings.getProperty("NonPvPTimeOnTeleport", 0);
		MAX_DRIFT_RANGE = aiSettings.getProperty("MaxDriftRange", 100);
		MAX_PURSUE_RANGE = aiSettings.getProperty("MaxPursueRange", 4000);
		MAX_PURSUE_UNDERGROUND_RANGE = aiSettings.getProperty("MaxPursueUndergoundRange", 2000);
		MAX_PURSUE_RANGE_RAID = aiSettings.getProperty("MaxPursueRangeRaid", 5000);
	}

	public static void loadGeodataSettings()
	{
		ExProperties geodataSettings = load(GEODATA_CONFIG_FILE);

		GEO_X_FIRST = geodataSettings.getProperty("GeoFirstX", 11);
		GEO_Y_FIRST = geodataSettings.getProperty("GeoFirstY", 10);
		GEO_X_LAST = geodataSettings.getProperty("GeoLastX", 26);
		GEO_Y_LAST = geodataSettings.getProperty("GeoLastY", 26);

		GEOFILES_PATTERN = geodataSettings.getProperty("GeoFilesPattern", "(\\d{2}_\\d{2})\\.l2j");
		ALLOW_GEODATA = geodataSettings.getProperty("AllowGeodata", true);

		try
		{
			GEODATA_ROOT = new File(geodataSettings.getProperty("GeodataRoot", "./geodata/")).getCanonicalFile();
		}
		catch(IOException e)
		{
			_log.error("", e);
		}

		ALLOW_FALL_FROM_WALLS = geodataSettings.getProperty("AllowFallFromWalls", false);
		ALLOW_KEYBOARD_MOVE = geodataSettings.getProperty("AllowMoveWithKeyboard", true);
		COMPACT_GEO = geodataSettings.getProperty("CompactGeoData", false);
		CLIENT_Z_SHIFT = geodataSettings.getProperty("ClientZShift", 16);
		PATHFIND_BOOST = geodataSettings.getProperty("PathFindBoost", 2);
		PATHFIND_DIAGONAL = geodataSettings.getProperty("PathFindDiagonal", true);
		PATH_CLEAN = geodataSettings.getProperty("PathClean", true);
		PATHFIND_MAX_Z_DIFF = geodataSettings.getProperty("PathFindMaxZDiff", 32);
		MAX_Z_DIFF = geodataSettings.getProperty("MaxZDiff", 64);
		MIN_LAYER_HEIGHT = geodataSettings.getProperty("MinLayerHeight", 64);
		PATHFIND_MAX_TIME = geodataSettings.getProperty("PathFindMaxTime", 10000000);
		PATHFIND_BUFFERS = geodataSettings.getProperty("PathFindBuffers", "8x96;8x128;8x160;8x192;4x224;4x256;4x288;2x320;2x384;2x352;1x512");
	}

	public static void pvpManagerSettings()
	{
		ExProperties pvp_manager = load(PVP_MANAGER_FILE);

		ALLOW_PVP_REWARD = pvp_manager.getProperty("AllowPvPManager", true);
		PVP_REWARD_SEND_SUCC_NOTIF = pvp_manager.getProperty("SendNotification", true);

		PVP_REWARD_REWARD_IDS = pvp_manager.getProperty("PvPRewardsIDs", new int[]{57, 6673});
		PVP_REWARD_COUNTS = pvp_manager.getProperty("PvPRewardsCounts", new long[]{1, 2});
		if(PVP_REWARD_REWARD_IDS.length != PVP_REWARD_COUNTS.length)
			_log.warn("pvp_manager.properties: PvPRewardsIDs array length != PvPRewardsCounts array length");

		PVP_REWARD_RANDOM_ONE = pvp_manager.getProperty("GiveJustOneRandom", true);
		PVP_REWARD_DELAY_ONE_KILL = pvp_manager.getProperty("DelayBetweenKillsOneCharSec", 60);
		PVP_REWARD_MIN_PL_PROFF = pvp_manager.getProperty("ToRewardMinProff", 0);
		PVP_REWARD_MIN_PL_UPTIME_MINUTE = pvp_manager.getProperty("ToRewardMinPlayerUptimeMinutes", 60);
		PVP_REWARD_MIN_PL_LEVEL = pvp_manager.getProperty("ToRewardMinPlayerLevel", 75);
		PVP_REWARD_PK_GIVE = pvp_manager.getProperty("RewardPK", false);
		PVP_REWARD_ON_EVENT_GIVE = pvp_manager.getProperty("ToRewardIfInEvent", false);
		PVP_REWARD_ONLY_BATTLE_ZONE = pvp_manager.getProperty("ToRewardOnlyIfInBattleZone", false);
		PVP_REWARD_ONLY_NOBLE_GIVE = pvp_manager.getProperty("ToRewardOnlyIfNoble", false);
		PVP_REWARD_SAME_PARTY_GIVE = pvp_manager.getProperty("ToRewardIfInSameParty", false);
		PVP_REWARD_SAME_CLAN_GIVE = pvp_manager.getProperty("ToRewardIfInSameClan", false);
		PVP_REWARD_SAME_ALLY_GIVE = pvp_manager.getProperty("ToRewardIfInSameAlly", false);
		PVP_REWARD_SAME_HWID_GIVE = pvp_manager.getProperty("ToRewardIfInSameHWID", false);
		PVP_REWARD_SAME_IP_GIVE = pvp_manager.getProperty("ToRewardIfInSameIP", false);
		PVP_REWARD_SPECIAL_ANTI_TWINK_TIMER = pvp_manager.getProperty("SpecialAntiTwinkCharCreateDelay", false);
		PVP_REWARD_HR_NEW_CHAR_BEFORE_GET_ITEM = pvp_manager.getProperty("SpecialAntiTwinkDelayInHours", 24);
		PVP_REWARD_CHECK_EQUIP = pvp_manager.getProperty("EquipCheck", false);
		PVP_REWARD_WEAPON_GRADE_TO_CHECK = pvp_manager.getProperty("MinimumGradeToCheck", 0);
		PVP_REWARD_LOG_KILLS = pvp_manager.getProperty("LogKillsToDB", false);
		DISALLOW_MSG_TO_PL = pvp_manager.getProperty("DoNotShowMessagesToPlayers", false);
	}
	
	public static void loadEventsSettings()
	{
		ExProperties eventSettings = load(EVENTS_CONFIG_FILE);

		EVENT_CofferOfShadowsPriceRate = eventSettings.getProperty("CofferOfShadowsPriceRate", 1.);
		EVENT_CofferOfShadowsRewardRate = eventSettings.getProperty("CofferOfShadowsRewardRate", 1.);
		EVENTS_DISALLOWED_SKILLS = eventSettings.getProperty("DisallowedSkills", "").trim().replaceAll(" ", "").split(";");
		
        EVENT_TvTRewards = eventSettings.getProperty("TvT_Rewards", "").trim().replaceAll(" ", "").split(";");
		EVENT_TvTTime = eventSettings.getProperty("TvT_time", 3);
		EVENT_TvTStartTime = eventSettings.getProperty("TvT_StartTime", "20:00").trim().replaceAll(" ", "").split(",");
		EVENT_TvTCategories = eventSettings.getProperty("TvT_Categories", false);
		EVENT_TvTMaxPlayerInTeam = eventSettings.getProperty("TvT_MaxPlayerInTeam", 20);
		EVENT_TvTMinPlayerInTeam = eventSettings.getProperty("TvT_MinPlayerInTeam", 2);
		EVENT_TvTAllowSummons = eventSettings.getProperty("TvT_AllowSummons", false);
		EVENT_TvTAllowBuffs = eventSettings.getProperty("TvT_AllowBuffs", false);
		EVENT_TvTAllowMultiReg = eventSettings.getProperty("TvT_AllowMultiReg", false);
		EVENT_TvTCheckWindowMethod = eventSettings.getProperty("TvT_CheckWindowMethod", "IP");
		EVENT_TvTEventRunningTime = eventSettings.getProperty("TvT_EventRunningTime", 20);
		EVENT_TvTFighterBuffs = eventSettings.getProperty("TvT_FighterBuffs", "").trim().replaceAll(" ", "").split(";");
		EVENT_TvTMageBuffs = eventSettings.getProperty("TvT_MageBuffs", "").trim().replaceAll(" ", "").split(";");
		EVENT_TvTBuffPlayers = eventSettings.getProperty("TvT_BuffPlayers", false);	
		EVENT_TvTrate = eventSettings.getProperty("TvT_rate", true);
		
		EVENT_CtFRewards = eventSettings.getProperty("CtF_Rewards", "").trim().replaceAll(" ", "").split(";");
		EVENT_CtfTime = eventSettings.getProperty("CtF_time", 3);
		EVENT_CtFrate = eventSettings.getProperty("CtF_rate", true);
		EVENT_CtFStartTime = eventSettings.getProperty("CtF_StartTime", "20:00").trim().replaceAll(" ", "").split(",");
		EVENT_CtFCategories = eventSettings.getProperty("CtF_Categories", false);
		EVENT_CtFMaxPlayerInTeam = eventSettings.getProperty("CtF_MaxPlayerInTeam", 20);
		EVENT_CtFMinPlayerInTeam = eventSettings.getProperty("CtF_MinPlayerInTeam", 2);
		EVENT_CtFAllowSummons = eventSettings.getProperty("CtF_AllowSummons", false);
		EVENT_CtFAllowBuffs = eventSettings.getProperty("CtF_AllowBuffs", false);
		EVENT_CtFAllowMultiReg = eventSettings.getProperty("CtF_AllowMultiReg", false);
		EVENT_CtFCheckWindowMethod = eventSettings.getProperty("CtF_CheckWindowMethod", "IP");
		EVENT_CtFFighterBuffs = eventSettings.getProperty("CtF_FighterBuffs", "").trim().replaceAll(" ", "").split(";");
		EVENT_CtFMageBuffs = eventSettings.getProperty("CtF_MageBuffs", "").trim().replaceAll(" ", "").split(";");
		EVENT_CtFBuffPlayers = eventSettings.getProperty("CtF_BuffPlayers", false);
		ALLOW_PLAYER_INVIS_TAKE_FLAG_CTF = eventSettings.getProperty("CtF_AllowTakingFlagWhileInvisible", true);

		EVENT_LastHeroItemID = eventSettings.getProperty("LastHero_bonus_id", 57);
		EVENT_LastHeroItemCOUNT = eventSettings.getProperty("LastHero_bonus_count", 5000.);
		EVENT_LastHeroRate = eventSettings.getProperty("LastHero_rate", true);
		EVENT_LastHeroItemCOUNTFinal = eventSettings.getProperty("LastHero_bonus_count_final", 10000.);
		EVENT_LastHeroRateFinal = eventSettings.getProperty("LastHero_rate_final", true);

		EVENT_LHTime = eventSettings.getProperty("LH_time", 3);
		EVENT_LHStartTime = eventSettings.getProperty("LH_StartTime", "20:00").trim().replaceAll(" ", "").split(",");
		EVENT_LHCategories = eventSettings.getProperty("LH_Categories", false);
		EVENT_LHAllowSummons = eventSettings.getProperty("LH_AllowSummons", false);
		EVENT_LHAllowBuffs = eventSettings.getProperty("LH_AllowBuffs", false);
		EVENT_LHAllowMultiReg = eventSettings.getProperty("LH_AllowMultiReg", false);
		EVENT_LHCheckWindowMethod = eventSettings.getProperty("LH_CheckWindowMethod", "IP");
		EVENT_LHEventRunningTime = eventSettings.getProperty("LH_EventRunningTime", 20);
		EVENT_LHFighterBuffs = eventSettings.getProperty("LH_FighterBuffs", "").trim().replaceAll(" ", "").split(";");
		EVENT_LHMageBuffs = eventSettings.getProperty("LH_MageBuffs", "").trim().replaceAll(" ", "").split(";");
		EVENT_LHBuffPlayers = eventSettings.getProperty("LH_BuffPlayers", false);	
		ALLOW_HEROES_LASTHERO = eventSettings.getProperty("LH_AllowHeroes", true);	

		EVENT_TFH_POLLEN_CHANCE = eventSettings.getProperty("TFH_POLLEN_CHANCE", 5.);

		EVENT_GLITTMEDAL_NORMAL_CHANCE = eventSettings.getProperty("MEDAL_CHANCE", 10.);
		EVENT_GLITTMEDAL_GLIT_CHANCE = eventSettings.getProperty("GLITTMEDAL_CHANCE", 0.1);

		EVENT_L2DAY_LETTER_CHANCE = eventSettings.getProperty("L2DAY_LETTER_CHANCE", 1.);
		EVENT_CHANGE_OF_HEART_CHANCE = eventSettings.getProperty("EVENT_CHANGE_OF_HEART_CHANCE", 5.);

		EVENT_APIL_FOOLS_DROP_CHANCE = eventSettings.getProperty("AprilFollsDropChance", 50.);

		EVENT_BEER_FESTIVAL_DROP_MOD = eventSettings.getProperty("BeerFestivalDropMod", 1.);

		EVENT_BOUNTY_HUNTERS_ENABLED = eventSettings.getProperty("BountyHuntersEnabled", true);

		EVENT_SAVING_SNOWMAN_LOTERY_PRICE = eventSettings.getProperty("SavingSnowmanLoteryPrice", 50000);
		EVENT_SAVING_SNOWMAN_REWARDER_CHANCE = eventSettings.getProperty("SavingSnowmanRewarderChance", 2);

		EVENT_TRICK_OF_TRANS_CHANCE = eventSettings.getProperty("TRICK_OF_TRANS_CHANCE", 10.);

		EVENT_MARCH8_DROP_CHANCE = eventSettings.getProperty("March8DropChance", 10.);
		EVENT_MARCH8_PRICE_RATE = eventSettings.getProperty("March8PriceRate", 1.);
		TVT_REWARD_PER_KILL = eventSettings.getProperty("TvT_AllowRewardPerKill", false);
		TVT_REWARD_ID_PER_KILL = eventSettings.getProperty("TvT_ItemIdPerKill", 57);
		TVT_REWARD_COUNT_PER_KILL = eventSettings.getProperty("TvT_ItemCountPerKill", 1);
		TVT_INCREASE_PVP_PER_KILL = eventSettings.getProperty("TvT_IncreasePvpKillsOnKill", false);
		GVG_LANG = eventSettings.getProperty("GvGLangRus", true);
		GvG_POINTS_FOR_BOX = eventSettings.getProperty("GvGPointsKillBox", 20); //test only
		GvG_POINTS_FOR_BOSS = eventSettings.getProperty("GvGPointsKillBoss", 50); //test only
		GvG_POINTS_FOR_KILL = eventSettings.getProperty("GvGPointsKillPlayer", 5); //test only
		GvG_POINTS_FOR_DEATH = eventSettings.getProperty("GvGPointsIfDead", 3); //test only
		GvG_EVENT_TIME = eventSettings.getProperty("GvGEventTime", 10); //test only
		GvG_BOSS_SPAWN_TIME = eventSettings.getProperty("GvGBossSpawnTime", 10); //test only
		GvG_FAME_REWARD = eventSettings.getProperty("GvGRewardFame", 200); //test only
		GvG_REWARD = eventSettings.getProperty("GvGRewardStatic", 57); //test only
		GvG_REWARD_COUNT = eventSettings.getProperty("GvGRewardCountStatic", 10000); //test only
		GvG_ADD_IF_WITHDRAW = eventSettings.getProperty("GvGAddPointsIfPartyWithdraw", 200); //test only
		GvG_HOUR_START = eventSettings.getProperty("GvGHourStart", 20); //test only
		GvG_MINUTE_START = eventSettings.getProperty("GvGMinuteStart", 00); //test only
		GVG_MIN_LEVEL = eventSettings.getProperty("GvGMinLevel", 1); //test only
		GVG_MAX_LEVEL = eventSettings.getProperty("GvGMaxLevel", 85); //test only
		GVG_MAX_GROUPS = eventSettings.getProperty("GvGMaxGroupsInEvent", 100); //test only
		GVG_MIN_PARTY_MEMBERS = eventSettings.getProperty("GvGMinPlayersInParty", 6); //test only
		GVG_TIME_TO_REGISTER = eventSettings.getProperty("GvGTimeToRegister", 10); //test only
		DISABLE_PARTY_ON_EVENT = eventSettings.getProperty("DisablePartyOnEvents", false);
		
		VIKTORINA_ENABLED = eventSettings.getProperty("Victorina_Enabled", false);
		VIKTORINA_REMOVE_QUESTION = eventSettings.getProperty("Victorina_Remove_Question", false);
		VIKTORINA_REMOVE_QUESTION_NO_ANSWER = eventSettings.getProperty("Victorina_Remove_Question_No_Answer", false);
		VIKTORINA_START_TIME_HOUR = eventSettings.getProperty("Victorina_Start_Time_Hour", 16);
		VIKTORINA_START_TIME_MIN = eventSettings.getProperty("Victorina_Start_Time_Minute", 16);
		VIKTORINA_WORK_TIME = eventSettings.getProperty("Victorina_Work_Time", 2);
		VIKTORINA_TIME_ANSER = eventSettings.getProperty("Victorina_Time_Answer", 1);
		VIKTORINA_TIME_PAUSE = eventSettings.getProperty("Victorina_Time_Pause", 1);		

		FIGHT_CLUB_ENABLED = eventSettings.getProperty("FightClubEnabled", false);
		MINIMUM_LEVEL_TO_PARRICIPATION = eventSettings.getProperty("MinimumLevel", 1);
		MAXIMUM_LEVEL_TO_PARRICIPATION = eventSettings.getProperty("MaximumLevel", 85);
		MAXIMUM_LEVEL_DIFFERENCE = eventSettings.getProperty("MaximumLevelDifference", 10);
		ALLOWED_RATE_ITEMS = eventSettings.getProperty("AllowedItems", "").trim().replaceAll(" ", "").split(",");
		PLAYERS_PER_PAGE = eventSettings.getProperty("RatesOnPage", 10);
		ARENA_TELEPORT_DELAY = eventSettings.getProperty("ArenaTeleportDelay", 5);
		CANCEL_BUFF_BEFORE_FIGHT = eventSettings.getProperty("CancelBuffs", true);
		UNSUMMON_PETS = eventSettings.getProperty("UnsummonPets", true);
		UNSUMMON_SUMMONS = eventSettings.getProperty("UnsummonSummons", true);
		REMOVE_CLAN_SKILLS = eventSettings.getProperty("RemoveClanSkills", false);
		REMOVE_HERO_SKILLS = eventSettings.getProperty("RemoveHeroSkills", false);
		TIME_TO_PREPARATION = eventSettings.getProperty("TimeToPreparation", 10);
		FIGHT_TIME = eventSettings.getProperty("TimeToDraw", 300);
		ALLOW_DRAW = eventSettings.getProperty("AllowDraw", true);
		TIME_TELEPORT_BACK = eventSettings.getProperty("TimeToBack", 10);
		FIGHT_CLUB_ANNOUNCE_RATE = eventSettings.getProperty("AnnounceRate", false);
		FIGHT_CLUB_ANNOUNCE_RATE_TO_SCREEN = eventSettings.getProperty("AnnounceRateToAllScreen", false);
		FIGHT_CLUB_ANNOUNCE_START_TO_SCREEN = eventSettings.getProperty("AnnounceStartBatleToAllScreen", false);		
	}

	public static void loadOlympiadSettings()
	{
		ExProperties olympSettings = load(OLYMPIAD);

		ENABLE_OLYMPIAD = olympSettings.getProperty("EnableOlympiad", true);
		ENABLE_OLYMPIAD_SPECTATING = olympSettings.getProperty("EnableOlympiadSpectating", true);
		ALT_OLYMPIAD_EVERY_DAY = olympSettings.getProperty("AltOlympiadStartsEveryDay", false);
		ALT_OLY_START_TIME = olympSettings.getProperty("AltOlyStartTime", 18);
		ALT_OLY_MIN = olympSettings.getProperty("AltOlyMin", 0);
		ALT_OLY_CPERIOD = olympSettings.getProperty("AltOlyCPeriod", 21600000);
		ALT_OLY_WPERIOD = olympSettings.getProperty("AltOlyWPeriod", 604800000);
		ALT_OLY_VPERIOD = olympSettings.getProperty("AltOlyVPeriod", 43200000);
		CLASS_GAME_MIN = olympSettings.getProperty("ClassGameMin", 5);
		NONCLASS_GAME_MIN = olympSettings.getProperty("NonClassGameMin", 9);

		GAME_MAX_LIMIT = olympSettings.getProperty("GameMaxLimit", 70);
		GAME_CLASSES_COUNT_LIMIT = olympSettings.getProperty("GameClassesCountLimit", 30);
		GAME_NOCLASSES_COUNT_LIMIT = olympSettings.getProperty("GameNoClassesCountLimit", 60);

		ALT_OLY_REG_DISPLAY = olympSettings.getProperty("AltOlyRegistrationDisplayNumber", 100);
		ALT_OLY_BATTLE_REWARD_ITEM = olympSettings.getProperty("AltOlyBattleRewItem", 13722);
		ALT_OLY_CLASSED_RITEM_C = olympSettings.getProperty("AltOlyClassedRewItemCount", 50);
		ALT_OLY_NONCLASSED_RITEM_C = olympSettings.getProperty("AltOlyNonClassedRewItemCount", 40);
		ALT_OLY_COMP_RITEM = olympSettings.getProperty("AltOlyCompRewItem", 13722);
		ALT_OLY_GP_PER_POINT = olympSettings.getProperty("AltOlyGPPerPoint", 1000);
		ALT_OLY_HERO_POINTS = olympSettings.getProperty("AltOlyHeroPoints", 180);
		ALT_OLY_RANK1_POINTS = olympSettings.getProperty("AltOlyRank1Points", 120);
		ALT_OLY_RANK2_POINTS = olympSettings.getProperty("AltOlyRank2Points", 80);
		ALT_OLY_RANK3_POINTS = olympSettings.getProperty("AltOlyRank3Points", 55);
		ALT_OLY_RANK4_POINTS = olympSettings.getProperty("AltOlyRank4Points", 35);
		ALT_OLY_RANK5_POINTS = olympSettings.getProperty("AltOlyRank5Points", 20);
		OLYMPIAD_STADIAS_COUNT = olympSettings.getProperty("OlympiadStadiasCount", 160);
		OLYMPIAD_BATTLES_FOR_REWARD = olympSettings.getProperty("OlympiadBattlesForReward", 15);
		OLYMPIAD_POINTS_DEFAULT = olympSettings.getProperty("OlympiadPointsDefault", 50);
		OLYMPIAD_POINTS_WEEKLY = olympSettings.getProperty("OlympiadPointsWeekly", 10);
		OLYMPIAD_OLDSTYLE_STAT = olympSettings.getProperty("OlympiadOldStyleStat", false);

		OLYMPIAD_BEGINIG_DELAY = olympSettings.getProperty("OlympiadBeginingDelay", 120);
	}

	public static void load()
	{
		loadServerConfig();
		loadTelnetConfig();
		loadResidenceConfig();
		loadAntiFloodConfig();
		loadCustomConfig();
		loadOtherConfig();
		loadSpoilConfig();
		loadFormulasConfig();
		loadAltSettings();
		loadServicesSettings();
		loadPvPSettings();
		loadAISettings();
		loadGeodataSettings();
		loadEventsSettings();
		loadOlympiadSettings();
		loadDevelopSettings();
		loadExtSettings();
		loadBBSSettings();

		abuseLoad();
		loadGMAccess();
		pvpManagerSettings();
		loadAntiBotSettings();
	}

	private Config()
	{}

	public static void abuseLoad()
	{
		List<Pattern> tmp = new ArrayList<Pattern>();

		LineNumberReader lnr = null;
		try
		{
			String line;

			lnr = new LineNumberReader(new InputStreamReader(new FileInputStream(ANUSEWORDS_CONFIG_FILE), "UTF-8"));

			while((line = lnr.readLine()) != null)
			{
				StringTokenizer st = new StringTokenizer(line, "\n\r");
				if(st.hasMoreTokens())
					tmp.add(Pattern.compile(".*" + st.nextToken() + ".*", Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE));
			}

			ABUSEWORD_LIST = tmp.toArray(new Pattern[tmp.size()]);
			tmp.clear();
			_log.info("Abuse: Loaded " + ABUSEWORD_LIST.length + " abuse words.");
		}
		catch(IOException e1)
		{
			_log.warn("Error reading abuse: " + e1);
		}
		finally
		{
			try
			{
				if(lnr != null)
					lnr.close();
			}
			catch(Exception e2)
			{
				// nothing
			}
		}
	}

	public static void loadAntiBotSettings()
	{
		ExProperties botSettings = load(BOT_FILE);
		
		ENABLE_ANTI_BOT_SYSTEM = botSettings.getProperty("EnableAntiBotSystem", true);
		MINIMUM_TIME_QUESTION_ASK = botSettings.getProperty("MinimumTimeQuestionAsk", 60);
		MAXIMUM_TIME_QUESTION_ASK = botSettings.getProperty("MaximumTimeQuestionAsk", 120);
		MINIMUM_BOT_POINTS_TO_STOP_ASKING = botSettings.getProperty("MinimumBotPointsToStopAsking", 10);
		MAXIMUM_BOT_POINTS_TO_STOP_ASKING = botSettings.getProperty("MaximumBotPointsToStopAsking", 15);
		MAX_BOT_POINTS = botSettings.getProperty("MaxBotPoints", 15);
		MINIMAL_BOT_RATING_TO_BAN = botSettings.getProperty("MinimalBotPointsToBan", -5);
		AUTO_BOT_BAN_JAIL_TIME = botSettings.getProperty("AutoBanJailTime", 24);
		ANNOUNCE_AUTO_BOT_BAN = botSettings.getProperty("AnounceAutoBan", true);
		ON_WRONG_QUESTION_KICK = botSettings.getProperty("IfWrongKick", true);
	}
	
	public static void loadGMAccess()
	{
		gmlist.clear();
		loadGMAccess(new File(GM_PERSONAL_ACCESS_FILE));
		File dir = new File(GM_ACCESS_FILES_DIR);
		if(!dir.exists() || !dir.isDirectory())
		{
			_log.info("Dir " + dir.getAbsolutePath() + " not exists.");
			return;
		}
		for(File f : dir.listFiles())
			// hidden файлы НЕ игнорируем
			if(!f.isDirectory() && f.getName().endsWith(".xml"))
				loadGMAccess(f);
	}

	public static void loadGMAccess(File file)
	{
		try
		{
			Field fld;
			//File file = new File(filename);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setIgnoringComments(true);
			Document doc = factory.newDocumentBuilder().parse(file);

			for(Node z = doc.getFirstChild(); z != null; z = z.getNextSibling())
				for(Node n = z.getFirstChild(); n != null; n = n.getNextSibling())
				{
					if(!n.getNodeName().equalsIgnoreCase("char"))
						continue;

					PlayerAccess pa = new PlayerAccess();
					for(Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
					{
						Class<?> cls = pa.getClass();
						String node = d.getNodeName();

						if(node.equalsIgnoreCase("#text"))
							continue;
						try
						{
							fld = cls.getField(node);
						}
						catch(NoSuchFieldException e)
						{
							_log.info("Not found desclarate ACCESS name: " + node + " in XML Player access Object");
							continue;
						}

						if(fld.getType().getName().equalsIgnoreCase("boolean"))
							fld.setBoolean(pa, Boolean.parseBoolean(d.getAttributes().getNamedItem("set").getNodeValue()));
						else if(fld.getType().getName().equalsIgnoreCase("int"))
							fld.setInt(pa, Integer.valueOf(d.getAttributes().getNamedItem("set").getNodeValue()));
					}
					gmlist.put(pa.PlayerID, pa);
				}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static String getField(String fieldName)
	{
		Field field = FieldUtils.getField(Config.class, fieldName);

		if(field == null)
			return null;

		try
		{
			return String.valueOf(field.get(null));
		}
		catch(IllegalArgumentException e)
		{

		}
		catch(IllegalAccessException e)
		{

		}

		return null;
	}

	public static boolean setField(String fieldName, String value)
	{
		Field field = FieldUtils.getField(Config.class, fieldName);

		if(field == null)
			return false;

		try
		{
			if(field.getType() == boolean.class)
				field.setBoolean(null, BooleanUtils.toBoolean(value));
			else if(field.getType() == int.class)
				field.setInt(null, NumberUtils.toInt(value));
			else if(field.getType() == long.class)
				field.setLong(null, NumberUtils.toLong(value));
			else if(field.getType() == double.class)
				field.setDouble(null, NumberUtils.toDouble(value));
			else if(field.getType() == String.class)
				field.set(null, value);
			else
				return false;
		}
		catch(IllegalArgumentException e)
		{
			return false;
		}
		catch(IllegalAccessException e)
		{
			return false;
		}

		return true;
	}

	public static ExProperties load(String filename)
	{
		return load(new File(filename));
	}

	public static ExProperties load(File file)
	{
		ExProperties result = new ExProperties();

		try
		{
			result.load(file);
		}
		catch(IOException e)
		{
			_log.error("Error loading config : " + file.getName() + "!");
		}

		return result;
	}

	public static boolean containsAbuseWord(String s)
	{
		for(Pattern pattern : ABUSEWORD_LIST)
			if(pattern.matcher(s).matches())
				return true;
		return false;
	}
	
	public static class RaidGlobalDrop
	{
		int _id;
		long _count;
		double _chance;
		
		public RaidGlobalDrop(int id, long count, double chance)
		{
			_id = id;
			_count = count;
			_chance = chance;
		}
		
		public int getId()
		{
			return _id;
		}
		
		public long getCount()
		{
			return _count;
		}
		
		public double getChance()
		{
			return _chance;
		}
	}	
}