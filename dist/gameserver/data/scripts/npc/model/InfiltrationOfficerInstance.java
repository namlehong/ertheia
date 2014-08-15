package npc.model;

import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

public class InfiltrationOfficerInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public InfiltrationOfficerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	protected void onDeath(Creature killer)
	{
		Reflection reflection = getReflection();
		if(reflection != ReflectionManager.DEFAULT)
			reflection.collapse();

		super.onDeath(killer);
	}

	@Override
	public boolean isPeaceNpc()
	{
		return false;
	}
}
