package ai;

import instances.SpaciaNormal;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.ai.Mystic;
import l2s.gameserver.model.instances.NpcInstance;

/**
 * @author cruel
 */
public class SpaciaMonster extends Mystic
{

	public SpaciaMonster(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();
		if (actor.getNpcId() == 22985) 
		{
			Reflection r = actor.getReflection();
			if(r instanceof SpaciaNormal)
			{
				SpaciaNormal spezion = (SpaciaNormal) r;
				spezion.openGate(26190004);
			}
		}

		super.onEvtDead(killer);
	}
	
	@Override
	protected void thinkAttack()
	{
		NpcInstance actor = getActor();
		Creature randomHated = actor.getAggroList().getRandomHated();
		if (randomHated != null && actor.getNpcId() == 22971 || actor.getNpcId() == 22972)
			actor.doCast(SkillTable.getInstance().getInfo(14139, 1), randomHated, true);
		super.thinkAttack();
	}
}
