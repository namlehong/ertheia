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
Lindvior1 by iqman ID - 19424
 */

public class Lindvior2 extends Fighter
{
	public Lindvior2(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		double decr = getStageDecr();
		getActor().setCurrentHpMp(getActor().getMaxHp()*decr, getActor().getMaxMp(), false);
	}
	
	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
			
		if(actor.isDead())
			return;

		if(actor.getCurrentHpPercents() <= 60.0D && actor.getCurrentHpPercents() >= 41.0D)
		{
			actor.doDie(null);
			Reflection r = getActor().getReflection();	
			if (r != null)
			{
				if(r instanceof LindviorBoss)
				{
					LindviorBoss lInst = (LindviorBoss) r;	
					lInst.scheduleNextSpawnFor(29240, 10000L, actor.getSpawnedLoc(), 1);
					lInst.setStageDecr(0.6);
					lInst.announceToInstance(NpcString.LINDVIOR_HAS_LANDED);
				}	
			}				
		}
		else if(actor.getCurrentHpPercents() <= 20.0D)
		{
			actor.doDie(null);
			Reflection r = getActor().getReflection();	
			if (r != null)
			{
				if(r instanceof LindviorBoss)
				{
					LindviorBoss lInst = (LindviorBoss) r;	
					lInst.scheduleNextSpawnFor(29240, 10000L, actor.getSpawnedLoc(), 1);
					lInst.setStageDecr(0.2);
					lInst.announceToInstance(NpcString.LINDVIOR_HAS_LANDED);
				}	
			}				
		}
		
		super.onEvtAttacked(attacker, damage);
	}		
	
	private double getStageDecr()
	{
		Reflection r = getActor().getReflection();	
		if (r != null)
		{
			if(r instanceof LindviorBoss)
			{
				LindviorBoss lInst = (LindviorBoss) r;	
				return lInst.getStageDecr();
			}	
		}
		return 1.;	
	}
}