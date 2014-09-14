package ai.chamber_of_prophecies;

import java.util.List;

import l2s.commons.collections.CollectionUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.World;
import l2s.gameserver.model.AggroList.AggroInfo;
import l2s.gameserver.model.instances.MinionInstance;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.scripts.Functions;

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
		NpcInstance actor = getActor();
		
		return startAttack();
	}

	private boolean startAttack()
	{
		System.out.println("Kain startAttack");
		NpcInstance actor = getActor();
		if(target == null)
		{
			List<NpcInstance> around = actor.getAroundNpc(3000, 150);
			if(around != null && !around.isEmpty())
			{
				System.out.println("npcaround size " + around.size());
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
	public boolean canAttackCharacter(Creature target)
	{
		System.out.println("Kain check canAttackCharacter");
		NpcInstance actor = getActor();
		if(getIntention() == CtrlIntention.AI_INTENTION_ATTACK)
		{
			AggroInfo ai = actor.getAggroList().get(target);
			return ai != null && ai.hate > 0;
		}
		return target.isMonster() || target.isPlayable();
	}

	@Override
	protected boolean isGlobalAggro()
	{
		return true;
	}

	@Override
	public boolean checkAggression(Creature target)
	{
		System.out.println("Kain checkAggression");
		NpcInstance actor = getActor();
		if(getIntention() != CtrlIntention.AI_INTENTION_ACTIVE || !isGlobalAggro())
		{
			System.out.println("Kain checkAggression " + getIntention() + " isGlobalAggro " + isGlobalAggro());
			return false;
		}
		if(target.isPlayable())
		{
			System.out.println("Kain checkAggression target.isPlayable true");
			return false;
		}
		
		if (target.isNpc())
		{
			if (((NpcInstance) target).isInFaction(actor))
			{
				System.out.println("Kain checkAggression target.isInFaction true");
				return false;
			}
		}
		
		if(target.isMonster())
		{
			System.out.println("Kain checkAggression target.isMonster true");
			return true;
		}

		return super.checkAggression(target);
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