package instances;

import java.util.concurrent.ScheduledFuture;
import l2s.commons.util.Rnd;
import l2s.gameserver.scripts.Functions;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExSendUIEventPacket;
import l2s.gameserver.utils.Location;

import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.components.ChatType;
import l2s.gameserver.network.l2.components.SceneMovie;
/**
 * Класс контролирует инстанс LindviorBoss
 *
 * @author iqman
 */
 
public class LindviorBoss extends Reflection
{
	private ScheduledFuture<?> _announce1;
	private static int _total_charges = 0;
	private static double _percent_hp = 1.;
	private boolean _allow_cyclone = false;
	
	@Override
	protected void onCreate()
	{
		super.onCreate();
		_announce1 = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Announce(), 15000, 15000);
	}
	
	private class Announce implements Runnable
	{
		@Override
		public void run()
		{
			for(Player player : getPlayers())
			{
				if(Rnd.chance(50))
					player.sendPacket(new ExShowScreenMessage(NpcString.YOU_MUST_ACTIVATE_THE_4_GENERATORS, 7000, ScreenMessageAlign.TOP_CENTER));
				else	
					player.sendPacket(new ExShowScreenMessage(NpcString.PROTECT_THE_GENERATOR, 7000, ScreenMessageAlign.TOP_CENTER));
			}		
		}
	}	
	
	private class spawnMeLindviorServant implements Runnable
	{
		private int _id;
		private Location _loc;
		private int _count;
		
		public spawnMeLindviorServant(int id, Location loc, int count)
		{
			_id = id;
			_loc = loc;
			_count = count;
		}
		
		@Override
		public void run()
		{
			for(int i = 0 ; i < _count ; i++)
				addSpawnWithoutRespawn(_id, _loc, 300);
			if(_allow_cyclone)
				addSpawnWithoutRespawn(25898, _loc, 300);
		}
	}
	
	public void announceToInstance(NpcString string)
	{
		for(Player player : getPlayers())
			player.sendPacket(new ExShowScreenMessage(string, 7000, ScreenMessageAlign.TOP_CENTER));
	
	}
	
	public void endInstance()
	{
		startCollapseTimer(1 * 60 * 1000L);
		for(Player p : getPlayers())
		{
			p.sendPacket(new SystemMessagePacket(SystemMsg.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addInteger(1));
			p.sendPacket(new ExShowScreenMessage(NpcString.THE_GENERATOR_HAS_BEEN_DESTROYED, 10000, ScreenMessageAlign.TOP_CENTER));
		}		
	}
	
	public void FinishStage1()
	{
		for(NpcInstance npc : getNpcs())
		{
			if(npc.getNpcId() == 19479)
			{
				Functions.npcSay(npc, NpcString.ALL_4_GENERATORS_MUST_BE_ACTIVATED, ChatType.NPC_ALL, 5000);
				npc.deleteMe();
			}
			else if(npc.getNpcId() == 19477) //del generator
				npc.deleteMe();
			else if(npc.getNpcId() == 19423) //del flying lindvior
				npc.deleteMe();
		}
		_announce1.cancel(false);
		_announce1 = null;
		
		addSpawnWithoutRespawn(25899, new Location(46424, -26200,-1430), 0);
		announceToInstance(NpcString.LINDVIOR_HAS_FALLEN_FROM_THE_SKY);
	}
	
	public void scheduleNextSpawnFor(int id, long delay, Location loc, int count)
	{
		ThreadPoolManager.getInstance().schedule(new spawnMeLindviorServant(id, loc, count), delay);
	}
	
	public void increaseCharges()
	{
		_total_charges++;
		if(_total_charges == 1)
		{
			showMovie();
			addSpawnWithoutRespawn(19423, new Location(44472, -25528,-1432), 0); //starting to fly around
		}
		else if(_total_charges >= 4)
			FinishStage1();
	}
	
	private void showMovie()
	{
		for(Player player : getPlayers())
			player.startScenePlayer(SceneMovie.LINDVIOR_ARRIVE);
	}
	
	public void setStageDecr(double percent)
	{
		//System.out.println("%percent%: "+percent+"");
		_percent_hp = percent;
		if(percent == 0.4)
		{
			_allow_cyclone = true;
			announceToInstance(NpcString.A_GIGANTIC_WHIRLWIND_HAS_APPEARED);
		}	
		if(percent == 0.2)
		{
			startCollapseTimer(10 * 60 * 1000L);
			for(Player p : getPlayers())
				p.sendPacket(new SystemMessagePacket(SystemMsg.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addInteger(10));
		}	
	}
	
	public double getStageDecr()
	{
		return _percent_hp;
	}
}