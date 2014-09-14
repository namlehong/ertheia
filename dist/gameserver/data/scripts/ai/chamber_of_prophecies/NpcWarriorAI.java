package ai.chamber_of_prophecies;

import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.AggroList.AggroInfo;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.model.instances.NpcInstance;

/**
 * @author Hien Son
 */
public class NpcWarriorAI extends Fighter
{
	public NpcWarriorAI(NpcInstance actor)
	{
		super(actor);
	}

	
	/*

	@Override
	protected void thinkAttack()
	{
		NpcInstance actor = getActor();
		if(actor.isDead())
			return;

		System.out.println("Kain thinks attack");
		if(doTask() && !actor.isAttackingNow() && !actor.isCastingNow())
		{
			startAttack();
		}
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
	*/
	@Override
	public boolean canAttackCharacter(Creature target)
	{
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
		NpcInstance actor = getActor();
		if(getIntention() != CtrlIntention.AI_INTENTION_ACTIVE || !isGlobalAggro())
			return false;

		if(target.isPlayable())
		{
			return false;
		}
		
		if (target.isNpc())
		{
			if (((NpcInstance) target).isInFaction(actor))
			{
				return false;
			}
		}
		
		if(target.isMonster())
		{
			return true;
		}

		return super.checkAggression(target);
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