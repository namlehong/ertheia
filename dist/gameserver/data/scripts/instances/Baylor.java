package instances;


import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.SceneMovie;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Класс контролирует Байлора, рб 98 лвл
 *
 * @author Awakeninger
 */

public class Baylor extends Reflection 
{
	private static final Logger _log = LoggerFactory.getLogger(Baylor.class);
    private static final int Baylor = 29213;
	private static final int Golem1 = 23123;
	private static final int Golem2 = 23123;   
	private static final int Golem3 = 23123;
	private static final int Golem4 = 23123; 
	private Location Golem1Loc = new Location(152648,142968,-12762);
	private Location Golem2Loc = new Location(152664,141160,-12762);
	private Location Golem3Loc = new Location(154488,141160,-12762);
	private Location Golem4Loc = new Location(154488,143000,-12762);
    private Location spawn1 = new Location(153256,142056,-12762,0);
	private static final long BeforeDelay = 19300L;
	private static final long BeforeDelayVDO = 47 * 1000L;
	private DeathListener _deathListener = new DeathListener();
	
	@Override
	protected void onCreate()
	{
		super.onCreate();
		ThreadPoolManager.getInstance().schedule(new FirstStage(), 10000);
	}	

	private class FirstStage extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{

			for(Player player : getPlayers())
				player.startScenePlayer(SceneMovie.SINEMA_BARLOG_OPENING);
			ThreadPoolManager.getInstance().schedule(new BaylorSpawn(), BeforeDelay);	
					
		}
	}
	
	public class BaylorSpawn extends RunnableImpl 
	{
        @Override
        public void runImpl() 
		{
            NpcInstance baylor = addSpawnWithoutRespawn(Baylor, spawn1, 0);
			baylor.addListener(_deathListener);
			addSpawnWithoutRespawn(Golem1, Golem1Loc, 0);
			addSpawnWithoutRespawn(Golem2, Golem2Loc, 0);
			addSpawnWithoutRespawn(Golem3, Golem3Loc, 0);
			addSpawnWithoutRespawn(Golem4, Golem4Loc, 0);
        }
    }
	
	private class DeathListener implements OnDeathListener 
	{
        @Override
        public void onDeath(Creature self, Creature killer) 
		{
            if(self.isNpc() && self.getNpcId() == Baylor) 
			{
				for(Player p : getPlayers())
				{
					p.sendPacket(new SystemMessagePacket(SystemMsg.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addInteger(5));
				}
				startCollapseTimer(5 * 60 * 1000L);			
			}
		}
	}	
}