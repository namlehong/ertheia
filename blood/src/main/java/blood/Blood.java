package blood;

import l2mq.L2MQ;
import l2s.gameserver.Config;
import l2s.gameserver.GameServer;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.handler.admincommands.AdminCommandHandler;
import l2s.gameserver.network.authcomm.AuthServerCommunication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import blood.base.FPCRole;
import blood.base.FPCSpawnStatus;
import blood.dao.FakePlayerDAO;
import blood.data.parser.FPItemParser;
import blood.data.parser.FarmLocationParser;
import blood.data.parser.NamePatternParser;
import blood.data.parser.NpcHelperParser;
import blood.handler.admincommands.AdminFakePlayers;
import blood.handler.admincommands.AdminManipulateAI;
import blood.utils.PIDHelper;
public
class Blood {
	
    private static final Logger 		_log = LoggerFactory.getLogger(Blood.class);
    private static Blood 	_instance;
    
    public static Blood getInstance() {
        if (_instance == null) {
            _instance =
                new Blood();
        }
        return _instance;
    }
    
    
    private Blood() {
    	_log.info("Initiate BloodFakePlayers.");
    	BloodConfig.loadConfig();
//    	FPCMerchantTable.getInstance();
    	FPItemParser.getInstance().load();
    	FarmLocationParser.getInstance().load();
    	NpcHelperParser.getInstance().load();
    	NamePatternParser.getInstance().load();
    	
    	FakePlayerDAO.loadStoredFPC();
    	if(BloodConfig.MQ_ENABLE)
    		L2MQ.getInstance();
    	
    	// add new comment
    	
        AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminFakePlayers());
        AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminManipulateAI());
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new ManagerTask(), BloodConfig.FPC_POPULATION_CONTROLL_INVERTAL, BloodConfig.FPC_POPULATION_CONTROLL_INVERTAL); //every 30 seconds
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new DisconnectTask(), BloodConfig.FPC_DISCONNECT_INTERVAL, BloodConfig.FPC_DISCONNECT_INTERVAL);
    }
    
    public static void populationControl()
    {
    	int max_giving_birth = BloodConfig.MAX_GIVING_BIRTH;
    	int max_move_role = BloodConfig.MAX_MOVE_ROLE;
    	int diff, i;
    	// birth
    	diff = FPCSpawnStatus.getDiff(); 
//    	_log.info("diff birth:"+diff);
    	i = 0;
    	while( i < diff && i < max_giving_birth && diff > 0)
    	{
    		FPCCreator.createNewChar();
    		i++;
    	}
    	
    	// check all group
    	for (FPCRole role : FPCRole.values()) 
    	{
    		diff = role.getPadding();
//    		_log.info("diff role:"+diff+" "+role);
        	i = 0;
        	
        	while( i < Math.abs(diff) && i < max_move_role)
        	{
        		//_log.info("i < Math.abs(diff) && i < max_move_role");
        		if(role == FPCRole.IDLE) // spawn/kick task
        		{
        			//_log.info("role ==  FPCRole.IDLE");
        			if(diff > 0) // spawn more
        			{
        				//_log.info("diff > 0");
        				FPCSpawnStatus.OFFLINE.getRandom().spawn();
        			}
        			else // kick more
        			{
        				//_log.info("kick");
        				role.getRandom().kick();
        			}
        				
        		}
        		else // change role
        		{
        			if(diff > 0 && FPCRole.IDLE.getSize() > 0) // idle -> role
        				FPCRole.IDLE.getRandom().setRole(role);
        			else if(diff < 0 && role.getSize() > 0) // role -> idle
        				role.getRandom().setRole(FPCRole.IDLE);
        		}
        		i++;
        	}
    	}
    	
    }
    
    public static void disconnectControl()
	{
		//make sure all the online FPC is totally replace after 6 hours
    	int disconnectCount	 	= FPCRole.getCCU()/36;
    	
    	for(int i=0;i<disconnectCount;i++)
    	{
    		FPCInfo actor = FPCSpawnStatus.ONLINE.getRandom();
    		actor.kick(); 
    	}
	
	}

    public static void debug()
    {
    	for(FPCSpawnStatus status: FPCSpawnStatus.values())
    	{
    		_log.info("Status: " + status + " size: " + status.getSize());
    	}
    	
    	for(FPCRole role: FPCRole.values())
    	{
    		_log.info("Role: " + role + " size: " + role.getSize() + " quota: "+role.getQuota());
    	}
    	
    }
    
    public static class ManagerTask implements Runnable
	{
		@SuppressWarnings("synthetic-access")
		@Override
		public void run()
		{
			try
			{
				populationControl();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
    
    public static class DisconnectTask implements Runnable
   	{
   		@SuppressWarnings("synthetic-access")
   		@Override
   		public void run()
   		{
   			try
   			{
   				for(FPCRole role: FPCRole.values())
   				{
   					role.quotaPadding();
   				}
   				//disconnectControl();
   				//mainEventChecker();
   			}
   			catch (Exception e)
   			{
   				e.printStackTrace();
   			}
   		}

   	}
    
    public static void main(String[] args) throws Exception
    {
    	PIDHelper.writePID();
    	BloodConfig.loadConfig();
    	if(BloodConfig.IS_FENCE){
    		_log.info("Fence is active");
    		Config.load();
    		AuthServerCommunication.getInstance().start();
    	}else{
    		GameServer.main(args);
    	    Blood.getInstance();
    	}
    }
    
}