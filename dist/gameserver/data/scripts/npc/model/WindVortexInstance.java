package npc.model;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.Location;
import l2s.gameserver.model.World;
import l2s.gameserver.ThreadPoolManager;

import java.util.concurrent.ScheduledFuture;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;


public class WindVortexInstance extends MonsterInstance
{
	private static final long serialVersionUID = 1L;

	private static final int GIANT_WINDIMA = 23419;
	
	private static final String ALERT_STRONG_MONSTER = "Quái vật mạnh xuất hiện!";
	
	public WindVortexInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	protected void onDeath(Creature killer)
	{
		super.onDeath(killer);
		
		if(killer.isPlayer())
			killer.sendPacket(new ExShowScreenMessage(ALERT_STRONG_MONSTER, 7000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
		
		addSpawn(GIANT_WINDIMA, getLoc(), 0, 60000); //despawn after 1 minutes
	}
	

	public NpcInstance addSpawn(int npcId, Location loc, int randomOffset, int despawnDelay)
	{
		NpcInstance result = Functions.spawn(randomOffset > 50 ? Location.findPointToStay(loc, 0, randomOffset, ReflectionManager.DEFAULT.getGeoIndex()) : loc, npcId);
		if(despawnDelay > 0 && result != null)
			ThreadPoolManager.getInstance().schedule(new DeSpawnScheduleTimerTask(result), despawnDelay);
		return result;
	}
	
	public class DeSpawnScheduleTimerTask extends RunnableImpl
	{
		NpcInstance _npc = null;

		public DeSpawnScheduleTimerTask(NpcInstance npc)
		{
			_npc = npc;
		}

		@Override
		public void runImpl() throws Exception
		{
			if(_npc != null)
				if(_npc.getSpawn() != null)
					_npc.getSpawn().deleteAll();
				else
					_npc.deleteMe();
		}
	}
}