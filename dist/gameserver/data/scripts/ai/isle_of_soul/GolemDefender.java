package ai.isle_of_soul;

import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.Defender;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;

/**
 * @author Bonux
 */
public class GolemDefender extends Defender
{
	public GolemDefender(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		super.onEvtAttacked(attacker, damage);

		NpcInstance actor = getActor();
		for(NpcInstance npc : actor.getAroundNpc(actor.getAggroRange(), 200))
		{
			if(!npc.isMonster())
				continue;

			if(npc.getAI().getIntention() != CtrlIntention.AI_INTENTION_ACTIVE && npc.getAI().getIntention() != CtrlIntention.AI_INTENTION_IDLE)
				continue;

			npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, actor, 500);
		}
	}
}