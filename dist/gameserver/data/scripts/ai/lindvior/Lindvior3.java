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
Lindvior1 by iqman ID - 29240
 */

public class Lindvior3 extends Fighter
{
	public Lindvior3(NpcInstance actor)
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

		if(actor.getCurrentHpPercents() <= 40.0D && actor.getCurrentHpPercents() >= 21.0D)
		{
			actor.doDie(null);
			Reflection r = getActor().getReflection();	
			if (r != null)
			{
				if(r instanceof LindviorBoss)
				{
					LindviorBoss lInst = (LindviorBoss) r;	
					lInst.scheduleNextSpawnFor(19424, 10000L, actor.getSpawnedLoc(), 1);
					lInst.setStageDecr(0.4);
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
	
	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();
		if(killer == null)
		{
			super.onEvtDead(killer); //probably beetween sessions
			return;
		}
		Reflection r = getActor().getReflection();	
		if (r != null)
		{
			if(r instanceof LindviorBoss)
			{
				LindviorBoss lInst = (LindviorBoss) r;	
				lInst.announceToInstance(NpcString.HONORABLE_WARRIORS_HAVE_DRIVEN_OFF_LINDVIOR_THE_EVIL_WIND_DRAGON);
			}	
		}		
		super.onEvtDead(killer);
	}	
}