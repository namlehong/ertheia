package instances;


import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.listener.actor.OnCurrentHpDamageListener;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.commons.geometry.Polygon;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Territory;
import l2s.gameserver.network.l2.components.*;
import l2s.gameserver.network.l2.s2c.*;
import l2s.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Класс контролирует Валлока, рб 99 лвл
 *
 * @author Awakeninger
 * Логгеры поставлены исключительно на время тестов.
 * Чтобы видеть стадии инстанса.
 *
 * По факту - это просто болванка инстанса.
 */

public class Vullock extends Reflection 
{
	private static final Logger _log = LoggerFactory.getLogger(Vullock.class);
    private static final int Vullock = 29218;
	private static final int VullockSlave = 29219;
	private static final int Golem1 = 23123;
	private static final int Golem2 = 23123;   
	private static final int Golem3 = 23123;
	private static final int Golem4 = 23123;
	private Player player;
	private Location Golem1Loc = new Location(152712,142936,-12762,57343);
	private Location Golem2Loc = new Location(154360,142936,-12762,40959);
	private Location Golem3Loc = new Location(154360,141288,-12762,25029);
	private Location Golem4Loc = new Location(152712,141288,-12762,8740);
    private Location vullockspawn = new Location(153576,142088,-12762,16383);
	private DeathListener _deathListener = new DeathListener();
	private CurrentHpListener _currentHpListener = new CurrentHpListener();
	private static final long BeforeDelay = 60 * 1000L;
	private static final long BeforeDelayVDO = 42 * 1000L;
	private static Territory centralRoomPoint = new Territory().add(new Polygon().add(152712,142936).add(154360,142936).add(154360,141288).add(152712,141288).setZmax(-12862).setZmin(-12700));
	
	@Override
	protected void onCreate()
	{
		super.onCreate();
		ThreadPoolManager.getInstance().schedule(new FirstStage(), BeforeDelayVDO);
		ThreadPoolManager.getInstance().schedule(new VullockSpawn(), BeforeDelay);
	}	
	
	private class DeathListener implements OnDeathListener 
	{
        @Override
        public void onDeath(Creature self, Creature killer) 
		{
            if(self.isNpc() && self.getNpcId() == Vullock) 
			{
				for(Player p : getPlayers())
				{
					p.sendPacket(new SystemMessagePacket(SystemMsg.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addInteger(5));
				}
				startCollapseTimer(5 * 60 * 1000L);			
			}
		}
	}
			
	private class FirstStage extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for(Player player : getPlayers())
				player.startScenePlayer(SceneMovie.SINEMA_BARLOG_STORY);			
		}
	}
	
	public class CurrentHpListener implements OnCurrentHpDamageListener
	{
		@Override
		public void onCurrentHpDamage(Creature actor, double damage, Creature attacker, Skill skill)
		{
			if(actor.getNpcId() == Vullock)
			{
				if(actor == null || actor.isDead())
					return;

				if(actor.getCurrentHp() <= 7850000) // С потолка.
				{	
					ThreadPoolManager.getInstance().schedule(new CaveStage(),0);
					actor.removeListener(_currentHpListener);
				}
			}
		}


	}
	
	public class CaveStage extends RunnableImpl 
	{
        @Override
        public void runImpl() 
		{
			NpcInstance Slave1 = addSpawnWithoutRespawn(VullockSlave, Territory.getRandomLoc(centralRoomPoint, getGeoIndex()), 150);
			Slave1.getAI().Attack(player, true, false);
			NpcInstance Slave2 = addSpawnWithoutRespawn(VullockSlave, Territory.getRandomLoc(centralRoomPoint, getGeoIndex()), 150);
			Slave2.getAI().Attack(player, true, false);
			NpcInstance Slave3 = addSpawnWithoutRespawn(VullockSlave, Territory.getRandomLoc(centralRoomPoint, getGeoIndex()), 150);
			Slave3.getAI().Attack(player, true, false);
			NpcInstance Slave4 = addSpawnWithoutRespawn(VullockSlave, Territory.getRandomLoc(centralRoomPoint, getGeoIndex()), 150);
			Slave4.getAI().Attack(player, true, false);
			NpcInstance Slave5 = addSpawnWithoutRespawn(VullockSlave, Territory.getRandomLoc(centralRoomPoint, getGeoIndex()), 150);
			Slave5.getAI().Attack(player, true, false);
			NpcInstance Slave6 = addSpawnWithoutRespawn(VullockSlave, Territory.getRandomLoc(centralRoomPoint, getGeoIndex()), 150);
			Slave6.getAI().Attack(player, true, false);

        }
    }
	
	public class VullockSpawn extends RunnableImpl 
	{
        @Override
        public void runImpl() 
		{
			Location Loc1 = Golem1Loc;
			Location Loc2 = Golem2Loc;
			Location Loc3 = Golem3Loc;
			Location Loc4 = Golem4Loc;
            Location Loc = vullockspawn;
			NpcInstance VullockStay = addSpawnWithoutRespawn(Vullock, Loc, 0);
			VullockStay.addListener(_deathListener);
			VullockStay.addListener(_currentHpListener);
			addSpawnWithoutRespawn(Golem1, Loc1, 0);
			addSpawnWithoutRespawn(Golem2, Loc2, 0);
			addSpawnWithoutRespawn(Golem3, Loc3, 0);
			addSpawnWithoutRespawn(Golem4, Loc4, 0);
        }
    }
}