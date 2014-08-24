package npc.model;

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
import l2s.gameserver.model.quest.Quest.DeSpawnScheduleTimerTask;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;


public class TurekOrcInstance extends MonsterInstance
{
	private static final long serialVersionUID = 1L;

	private static final int CHERTUBA_MIRAGE = 23421;
	
	private static final String ALERT_STRONG_MONSTER = "Quái vật mạnh xuất hiện!";
	
	public TurekOrcInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	protected void onDeath(Creature killer)
	{
		super.onDeath(killer);
		
		if(killer.isPlayer())
			killer.sendPacket(new ExShowScreenMessage(ALERT_STRONG_MONSTER, 7000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
		
		addSpawn(CHERTUBA_MIRAGE, getLoc(), 0);
	}
	
	public NpcInstance addSpawn(int npcId, Location loc, int randomOffset)
	{
		NpcInstance result = Functions.spawn(randomOffset > 50 ? Location.findPointToStay(loc, 0, randomOffset, ReflectionManager.DEFAULT.getGeoIndex()) : loc, npcId);
		return result;
	}
}