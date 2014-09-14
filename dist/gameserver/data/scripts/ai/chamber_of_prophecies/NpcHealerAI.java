package ai.chamber_of_prophecies;

import java.util.List;

import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.Priest;
import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.World;
import l2s.gameserver.model.instances.DecoyInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
/**
 * @author Hien Son
 */
public class NpcHealerAI extends Priest
{
	Creature attackTarget = null;
	Player healTarget = null;
	
	public NpcHealerAI(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		double chance = Math.random();
		NpcInstance actor = getActor();
		
		if(chance*100 < getRateHEAL())
		{
			for(Player player : World.getAroundPlayers(actor, 600, 300))
			{
				if(player != null && !player.isDead() && !player.isAlikeDead() && !player.isVisible())
				{
					Skill skill = null;
					if(checkHealattackTarget(healTarget) == 1)
					{
						Skill[] healSkillList = actor.getTemplate().getHealSkills();
						int randomIndex = (int)Math.random()*healSkillList.length;
						skill = healSkillList[randomIndex];
						
					}
					else if(checkHealattackTarget(healTarget) == 2)
					{
						Skill[] rechargeSkillList = actor.getTemplate().getHealSkills();
						int randomIndex = (int)Math.random()*rechargeSkillList.length;
						skill = rechargeSkillList[randomIndex];
					}
					
					if(skill != null)
					{
						actor.doCast(skill, healTarget, true);
						actor.broadcastPacket(new MagicSkillUse(actor, healTarget, skill.getId(), 1, 0, 0, false));
					}
					
				}
			}
			return false;
		}
		else
			return startAttack();
	}
	
	private int checkHealattackTarget(Player player)
	{
		if(player == null)
			return 0;
		
		if(player.getCurrentHpPercents() < 80)
			return 1;
		
		if(player.getCurrentMpPercents() < 80)
			return 2;
		
		return 0;
	}

	private boolean startAttack()
	{
		NpcInstance actor = getActor();
		if(attackTarget == null || attackTarget.isDead())
		{
			List<NpcInstance> around = actor.getAroundNpc(2000, 150);
			if(around != null && !around.isEmpty())
			{
				for(NpcInstance npc : around)
				{
					if(checkattackTarget(npc))
					{
						if(attackTarget == null || actor.getDistance3D(npc) < actor.getDistance3D(attackTarget))
							attackTarget = npc;
						
					}
				}
			}
		}

		if(attackTarget != null && !actor.isAttackingNow() && !actor.isCastingNow() && !attackTarget.isDead() && GeoEngine.canSeeTarget(actor, attackTarget, false) && attackTarget.isVisible())
		{
			actor.getAggroList().addDamageHate(attackTarget, 10, 10);
			actor.setAggressionTarget(attackTarget);
			actor.setRunning();
			setIntention(CtrlIntention.AI_INTENTION_ATTACK, attackTarget);
			return true;
		}

		if(attackTarget != null && (!attackTarget.isVisible() || attackTarget.isDead() || !GeoEngine.canSeeTarget(actor, attackTarget, false)))
		{
			attackTarget = null;
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
			if(!createNewTask())
			{
				if(System.currentTimeMillis() > getAttackTimeout() && !(actor instanceof DecoyInstance))
					returnHome();
			}
		}
	}

	@Override
	protected boolean isGlobalAggro()
	{
		return true;
	}
	
	private boolean checkattackTarget(NpcInstance attackTarget)
	{
		if(attackTarget == null)
			return false;
		
		if (((NpcInstance) attackTarget).isInFaction(getActor()))
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
		return 30;
	}

	@Override
	public int getRateDAM()
	{
		return 30;
	}

	@Override
	public int getRateSTUN()
	{
		return 30;
	}

	@Override
	public int getRateBUFF()
	{
		return 90;
	}

	@Override
	public int getRateHEAL()
	{
		return 90;
	}
}