package ai.chamber_of_prophecies;

import java.util.List;

import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.WorldRegion;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.geodata.GeoEngine;

/**
 * @author Hien Son
 */
public class NpcWarriorAI extends Fighter
{
	private NpcInstance target = null;

	public NpcWarriorAI(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public boolean isGlobalAI()
	{
		return false;
	}
	
	@Override
	protected void onEvtSpawn()
	{
		startAttack();
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		
		return startAttack();
	}
	
	@Override
	protected void returnHome(boolean clearAggro, boolean teleport)
	{
		
	}
	
	@Override
	public void runImpl() throws Exception
	{
		if(_aiTask == null)
			return;
		
		if(!isGlobalAI() && System.currentTimeMillis() - _lastActiveCheck > 5000L)
		{
			_lastActiveCheck = System.currentTimeMillis();
			NpcInstance actor = getActor();
			WorldRegion region = actor == null ? null : actor.getCurrentRegion();
			if(region == null || !region.isActive())
			{
				stopAITask();
				return;
			}
		}
		onEvtThink();
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
						if(target == null || actor.getDistance3D(npc) < actor.getDistance3D(target))
							target = npc;
					}
				}
			}
		}

		if(target != null && !actor.isAttackingNow() && !actor.isCastingNow() && !target.isDead() && GeoEngine.canSeeTarget(actor, target, false) && target.isVisible())
		{
			actor.getAggroList().addDamageHate(target, 10000, 10000);
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

	private boolean checkTarget(NpcInstance target)
	{
		if(target == null)
			return false;
		
		if(target.isPlayable()) 
			return false;
		
		if(target.getFaction().equals(getActor().getFaction()))
			return false;
			
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
		return 0;
	}

	@Override
	public int getRateDAM()
	{
		return 50;
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