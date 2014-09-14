package ai.chamber_of_prophecies;

import java.util.List;

import l2s.commons.collections.CollectionUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.World;
import l2s.gameserver.model.AggroList.AggroInfo;
import l2s.gameserver.model.instances.MinionInstance;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.model.instances.NpcInstance;

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
	protected void onEvtThink()
	{
		System.out.println("Kain onEvtThink");
		
		if(_randomAnimationEnd > System.currentTimeMillis())
			return;
		try
		{
			System.out.println("Kain getIntention " + getIntention());
			if(getIntention() == CtrlIntention.AI_INTENTION_ACTIVE)
				thinkActive();
			else if(getIntention() == CtrlIntention.AI_INTENTION_ATTACK)
				thinkAttack();
			else if(getIntention() == CtrlIntention.AI_INTENTION_FOLLOW)
				thinkFollow();
		}
		finally
		{
			
		}
		
	}
	
	@Override
	protected boolean thinkActive()
	{
		System.out.println("Kain thinkActive");
		NpcInstance actor = getActor();
		if(actor.isActionsDisabled())
			return true;

		if(_randomAnimationEnd > System.currentTimeMillis())
			return true;

		if(_def_think)
		{
			if(doTask())
				clearTasks();
			return true;
		}

		long now = System.currentTimeMillis();
		if(now - _checkAggroTimestamp > Config.AGGRO_CHECK_INTERVAL)
		{
			_checkAggroTimestamp = now;

			System.out.println("Kain checkAgrro");
			if(actor.getAggroList().isEmpty())
			{
				System.out.println("Kain getAggroList empty");
				List<NpcInstance> chars = actor.getAroundNpc(3000, 150);
				CollectionUtils.eqSort(chars, _nearestTargetComparator);
				for(Creature cha : chars)
				{
					System.out.println("Kain thinkActive checkAggression " + cha.getName() + " " + checkAggression(cha));
					if(checkAggression(cha))
						changeIntention(CtrlIntention.AI_INTENTION_ATTACK, cha, null);
				}
			}
		}

		if(randomAnimation())
			return true;

		if(randomWalk())
			return true;

		return false;
	}

	@Override
	protected void thinkAttack()
	{
		System.out.println("Kain thinkAttack");
		NpcInstance actor = getActor();
		if(actor.isDead())
			return;
		
		if(doTask() && !actor.isAttackingNow() && !actor.isCastingNow())
		{
			startAttack();
		}
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
				for(NpcInstance npc : around)
				{
					if(checkAggression(npc))
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