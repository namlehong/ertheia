package blood;

import l2s.commons.configuration.ExProperties;
import l2s.gameserver.Config;
import l2s.gameserver.utils.Location;

public class BloodConfig {

	public static boolean MQ_ENABLE = false;
	public static String MQ_SERVER = "125.212.219.44";
	public static int MQ_PORT = 4730;
	public static String MQ_PREFIX = "kain";
	public static boolean AI_ATTACK_ALLOW = true;
	public static boolean IS_FENCE = false;
	public static int FENCE_PARENT_ID = 0;
	public static int 	FPC_IDLE 			= 0;
	public static int	FPC_NEXUS 			= 0;
	public static int	FPC_MARKET 			= 0;
	public static int	MAX_GIVING_BIRTH 	= 5;
	public static int	MAX_MOVE_ROLE		= 50;
	public static long 	FPC_DISCONNECT_INTERVAL	= 10*60*1000L;  //every 60 minutes
	public static long 	FPC_POPULATION_CONTROLL_INVERTAL = 30*1000L; // every 30s
	public static Location FPC_CREATE_LOC = Location.parseLoc("-116059, -251145, -2992");
	public static String SUPPORTER_NAME = "[L2VH]HoTro";
	
	public static void loadConfig()
	{
		ExProperties bfconfig = Config.load("config/blood.ini");
		IS_FENCE = bfconfig.getProperty("fence", false);
		FENCE_PARENT_ID = bfconfig.getProperty("fenceParentId", 0);
		
		BloodConfig.FPC_IDLE = bfconfig.getProperty("FPC_IDLE", 10);
		BloodConfig.FPC_NEXUS = bfconfig.getProperty("FPC_NEXUS", 0);
		BloodConfig.FPC_MARKET = bfconfig.getProperty("FPC_MARKET", 0);
		BloodConfig.MAX_GIVING_BIRTH = bfconfig.getProperty("MAX_GIVING_BIRTH", 5);
		BloodConfig.MAX_MOVE_ROLE = bfconfig.getProperty("MAX_MOVE_ROLE", 50);
		MQ_ENABLE = bfconfig.getProperty("MQ_ENABLE", false);
		MQ_SERVER = bfconfig.getProperty("MQ_SERVER", "125.212.219.44");
		MQ_PORT = bfconfig.getProperty("MQ_PORT", 4730);
		MQ_PREFIX = bfconfig.getProperty("MQ_PREFIX", "kain");
	}

}
