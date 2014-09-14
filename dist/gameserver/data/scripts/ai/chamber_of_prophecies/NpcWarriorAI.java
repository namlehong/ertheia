package ai.chamber_of_prophecies;

import java.util.List;

import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.model.instances.DecoyInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.Location;
/**
 * @author Hien Son
 */
public class NpcWarriorAI extends Fighter
{
	NpcInstance target = null;
	
	public NpcWarriorAI(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		return startAttack();
	}

	private boolean startAttack()
	{
		NpcInstance actor = getActor();
		if(target == null)
		{
			List<NpcInstance> around = actor.getAroundNpc(3000, 150);
			if(around != null && !around.isEmpty())
			{
				for(NpcInstance npc : around)
				{
					if(checkTarget(npc))
					{
						if(target == null)
							target = npc;
						
					}
				}
			}
		}

		if(target != null && !actor.isAttackingNow() && !actor.isCastingNow() && !target.isDead() && GeoEngine.canSeeTarget(actor, target, false) && target.isVisible())
		{
			actor.getAggroList().addDamageHate(target, 10, 10);
			actor.setAggressionTarget(target);
			actor.setRunning();
			setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
			return true;
		}

		if(target != null && (!target.isVisible() || target.isDead() || !GeoEngine.canSeeTarget(actor, target, false)))
		{
			target = null;
			return false;
		}
		
		return false;
	}
	
	@Override
	protected void thinkAttack()
	{
		NpcInstance actor = getActor();
		if(actor.isDead())
			return;

		if(doTask() && !actor.isAttackingNow() && !actor.isCastingNow())
		{
			createNewTask();
		}
	}


	@Override
	protected boolean isGlobalAggro()
	{
		return true;
	}
	
	private boolean checkTarget(NpcInstance target)
	{
		if(target == null)
			return false;
		
		if (((NpcInstance) target).isInFaction(getActor()))
			return false;
			
		return true;
	}

	@Override
	public int getMaxAttackTimeout()
	{
		return 0;
	}

	@Override
	protected boolean randomWalk()
	{
		return true;
	}
	

	@Override
	public int getRateDOT()
	{
		return 0;
	}

	@Override
	public int getRateDEBUFF()
	{
		return 50;
	}

	@Override
	public int getRateDAM()
	{
		return 80;
	}

	@Override
	public int getRateSTUN()
	{
		return 30;
	}

	@Override
	public int getRateBUFF()
	{
		return 0;
	}

	@Override
	public int getRateHEAL()
	{
		return 0;
	}
}