package ai.lindvior;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.network.l2.components.ChatType;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.model.entity.Reflection;

import instances.LindviorBoss;

/**
Lindvior1 by iqman ID - 25899
 */

public class Lindvior1 extends Fighter
{
	public Lindvior1(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
			
		if(actor.isDead())
			return;

		if(actor.getCurrentHpPercents() <= 80.0D)
		{
			actor.doDie(null);
			Reflection r = getActor().getReflection();	
			if (r != null)
			{
				if(r instanceof LindviorBoss)
				{
					LindviorBoss lInst = (LindviorBoss) r;	
					lInst.scheduleNextSpawnFor(19424, 10000L, actor.getSpawnedLoc(), 1);
					lInst.setStageDecr(0.8);
				}	
			}				
		}

		super.onEvtAttacked(attacker, damage);
	}		
}