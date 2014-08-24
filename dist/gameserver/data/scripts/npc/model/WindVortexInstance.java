package npc.model;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.model.World;
import l2s.gameserver.ThreadPoolManager;

import java.util.concurrent.ScheduledFuture;
import l2s.gameserver.model.instances.NpcInstance;


public class WindVortexInstance extends MonsterInstance
{
	private static final long serialVersionUID = 1L;

	public WindVortexInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	protected void onDeath(Creature killer)
	{
		super.onDeath(killer);
		
	}
	
}