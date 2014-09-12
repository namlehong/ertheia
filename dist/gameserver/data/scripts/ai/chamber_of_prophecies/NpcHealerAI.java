package ai.chamber_of_prophecies;

import java.util.List;

import l2s.commons.util.Rnd;

import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.Priest;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.geodata.GeoEngine;

/**
 * @author Hien Son
 */
public class NpcHealerAI extends Priest
{
	private NpcInstance target = null;

	public NpcHealerAI(NpcInstance actor)
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
		
		if(Rnd.chance(3))
			Functions.npcSay(actor, NpcString.DID_SOMEONE_CRY_MEDIC_HERE_BE_HEALED);				
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
						if(target == null || actor.getDistance3D(npc) < actor.getDistance3D(target))
							target = npc;
					}
				}
			}
		}

		if(target != null && !actor.isAttackingNow() && !actor.isCastingNow() && !target.isDead() && GeoEngine.canSeeTarget(actor, target, false) && target.isVisible())
		{
			actor.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, target, 1);
			return true;
		}
		
		if(target != null && (!target.isVisible() || target.isDead() || !GeoEngine.canSeeTarget(actor, target, false)))
		{
			target = null;
			return false;
		}
		
		else if(defaultThinkBuff(10, 5))
			return true;
			
		return false;
	}

	private boolean checkTarget(NpcInstance target)
	{
		if(target == null)
			return false;
		
		if(target.isPlayable()) 
			return false;
		
		if(target.getFaction() == getActor().getFaction())
			return false;
			
		return true;
	}
}