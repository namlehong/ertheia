package npc.model;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Hien Son
 */
public class FightingSupportNPCInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;


	public FightingSupportNPCInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return true;
	}
}
