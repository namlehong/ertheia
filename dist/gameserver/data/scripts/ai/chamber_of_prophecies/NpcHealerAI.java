package ai.chamber_of_prophecies;

import java.util.List;

import org.dom4j.tree.LazyList;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.DecoyInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.utils.Location;
/**
 * @author Hien Son
 */
public class NpcHealerAI extends Fighter
{
	Creature attackTarget = null;
	List<Player> targetPlayers = null;
	int maxFollowDistance = 300;
	int minFollowDistance = 100;
	int followTargetIndex = 0;
	
	public NpcHealerAI(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		Player player;
		
		//follow the player
		double distance;
		
		if(targetPlayers != null && targetPlayers.size() > 0 && followTargetIndex > 0)
		{
			player = targetPlayers.get(followTargetIndex - 1);
			
			//in case the player set to follow is null, fall back to the first player in the list
			if(player == null)
				player = targetPlayers.get(0);
			
			distance = (int)actor.getDistance(player);
			
			if(distance > maxFollowDistance)
			{
				if(GeoEngine.canSeeTarget(actor, player, false))
				{
					//in case the NPC can see the player
					actor.setRunning();
					Location loc = new Location(player.getX() + Rnd.get(-60, 60), player.getY() + Rnd.get(-60, 60), player.getZ());
					actor.followToCharacter(loc, player, minFollowDistance, false);
				}
				else
				{
					//in case the NPC cannot see the player, then teleport straight to him
					actor.teleToLocation(player.getLoc().getRandomLoc(100));
				}
				return true;
			}
		}
		
		//if healling is not possible, then start attack monsters instead
		if(startHeal())
			return true;
		else
			return startAttack();
		
	}
	
	protected boolean canUseSkill(Skill skill, Creature target, double distance, boolean override)
	{

		NpcInstance actor = getActor();
		
		if ((skill == null) || skill.isNotUsedByAI())
		{
			return false;
		}
		
		if(!skill.checkCondition(actor, target, true, false, false))
		{
			return false;
		}
				
		if ((skill.getTargetType() == Skill.SkillTargetType.TARGET_SELF) && (target != actor))
		{
			return false;
		}
		
		int castRange = skill.getAOECastRange();
		if ((castRange <= 200) && (distance > 200))
		{
			return false;
		}
		
		if (actor.isSkillDisabled(skill) || actor.isMuted(skill) || actor.isUnActiveSkill(skill.getId()))
		{
			return false;
		}
		
		double mpConsume2 = skill.getMpConsume2();
		if (skill.isMagic())
		{
			mpConsume2 = actor.calcStat(Stats.MP_MAGIC_SKILL_CONSUME, mpConsume2, target, skill);
		}
		else
		{
			mpConsume2 = actor.calcStat(Stats.MP_PHYSICAL_SKILL_CONSUME, mpConsume2, target, skill);
		}
		if (actor.getCurrentMp() < mpConsume2)
		{
			return false;
		}
		
		if (!override && target.getEffectList().containsEffects(skill))
		{
			return false;
		}
		
		return true;
	}
	
	private boolean startHeal()
	{

		NpcInstance actor = getActor();
		for(Player player : targetPlayers)
		{
			if(player != null && !player.isDead() && !player.isAlikeDead() && player.isVisible())
			{
				Skill skill = null;
				Skill healHPSkill = null;
				Skill healMPSkill = null;
				
				double distance = actor.getDistance(player);
				if(player.getCurrentHpPercents() < 80)
				{
					Skill[] healSkillList = selectUsableSkills(player, distance, actor.getTemplate().getHealSkills());
					if(healSkillList != null)
					{
						int randomIndex = (int)Math.random()*healSkillList.length;
						healHPSkill = healSkillList[randomIndex];
					}
				}
				
				if(player.getCurrentMpPercents() < 80)
				{
					Skill[] rechargeSkillList = selectUsableSkills(player, distance, actor.getTemplate().getManaHealSkills());
					if(rechargeSkillList != null)
					{
						int randomIndex = (int)Math.random()*rechargeSkillList.length;
						healMPSkill = rechargeSkillList[randomIndex];
					}
				}
				
				//check to get the skill to cast, prioritize heal HP skill higher than heal MP skill
				if(healHPSkill != null) 
					skill = healHPSkill;
				else if(healMPSkill != null)
					skill = healMPSkill;
				
				//if cannot get any skill to cast (out of MP, skill still cooling down, etc.), 
				//then attack monsters instead
				if(skill == null)
					return false;
				
				if(distance > skill.getCastRange())
				{
					tryMoveToTarget(player, skill.getCastRange());
					return true;
				}
				
				if(canUseSkill(skill, player, distance, true))
				{
					System.out.println("Use skill " + skill.getName());
					actor.doCast(skill, player, true);
					actor.broadcastPacket(new MagicSkillUse(actor, player, skill.getId(), 1, 0, 0, false));
				}
				
			}
		}
		return true;
	}

	private boolean startAttack()
	{
		System.out.println("Start attack");
		NpcInstance actor = getActor();
		if(attackTarget == null || attackTarget.isDead())
		{
			List<NpcInstance> around = actor.getAroundNpc(2000, 150);
			if(around != null && !around.isEmpty() || !GeoEngine.canSeeTarget(actor, attackTarget, false))
			{
				for(NpcInstance npc : around)
				{
					if(checkattackTarget(npc))
					{
						System.out.println("Target " + npc.getName() + " is eligible to attack");
						if(attackTarget == null || actor.getDistance3D(npc) < actor.getDistance3D(attackTarget))
							attackTarget = npc;
						
					}
					else
					{
						System.out.println("Target " + npc.getName() + " is NOT eligible to attack");
					}
				}
			}
			else
			{
				attackTarget = null;
				System.out.println("there is no target around");
				return false;
			}
			

			if(attackTarget != null && !actor.isAttackingNow() && !actor.isCastingNow() && !attackTarget.isDead() && GeoEngine.canSeeTarget(actor, attackTarget, false) && attackTarget.isVisible())
			{
				System.out.println("actor is eligible to start attack");
				actor.getAggroList().addDamageHate(attackTarget, 10, 10);
				actor.setAggressionTarget(attackTarget);
				actor.setRunning();
				setIntention(CtrlIntention.AI_INTENTION_ATTACK, attackTarget);
				return true;
			}
			else
			{
				System.out.println("actor is busy");
			}
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
		

		if(attackTarget != null && (!attackTarget.isVisible() || attackTarget.isDead() || !GeoEngine.canSeeTarget(getActor(), attackTarget, false)))
		{
			return false;
		}
		
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
	
	public List<Player> getTargetPlayer()
	{
		return targetPlayers;
	}
	
	public void setTargetPlayer(Player... _targetPlayers)
	{
		targetPlayers = new LazyList<Player>();
		
		for(Player _targetPlayer : _targetPlayers)
			targetPlayers.add(_targetPlayer);
	}
	
	public int getMaxFollowDistance()
	{
		return maxFollowDistance;
	}
	
	public void setMaxFollowDistance(int distance)
	{
		maxFollowDistance = distance;
	}
	
	public int getMinFollowDistance()
	{
		return minFollowDistance;
	}
	
	public void setMinFollowDistance(int distance)
	{
		minFollowDistance = distance;
	}
	
	public int getFollow()
	{
		return followTargetIndex;
	}
	
	public void setFollow(int targetIndex)
	{
		followTargetIndex = targetIndex;
	}
}