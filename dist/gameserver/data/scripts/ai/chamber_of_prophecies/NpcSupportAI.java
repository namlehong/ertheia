package ai.chamber_of_prophecies;

import java.util.List;

import org.dom4j.tree.LazyList;

import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.AggroList.AggroInfo;
import l2s.gameserver.model.instances.DecoyInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
import l2s.gameserver.stats.Stats;
/**
 * @author Hien Son
 */
public class NpcSupportAI extends DefaultAI
{
	Creature attackTarget = null;
	List<Player> targetPlayers = null;
	int maxFollowDistance = 300;
	int minFollowDistance = 100;
	int followTargetIndex = 0;
	
	public NpcSupportAI(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean createNewTask()
	{
		return defaultFightTask();
	}
	
	protected boolean startHeal()
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
					//System.out.println("Use skill " + skill.getName());
					actor.doCast(skill, player, true);
					actor.broadcastPacket(new MagicSkillUse(actor, player, skill.getId(), 1, 0, 0, false));
				}
				
			}
		}
		return true;
	}

	protected boolean startAttack()
	{
		NpcInstance actor = getActor();
		
		if(attackTarget !=null && (attackTarget.isDead() || !GeoEngine.canSeeTarget(actor, attackTarget, false))) 
			attackTarget = null;
		
		if(attackTarget == null)
		{
			//set new attack target
			List<NpcInstance> around = actor.getAroundNpc(2000, 150);
			if(around != null && !around.isEmpty())
			{
				for(NpcInstance npc : around)
				{
					if(checkAttackTarget(npc))
					{
						if(attackTarget == null)
							attackTarget = npc;
						else if(actor.getDistance3D(npc) < actor.getDistance3D(attackTarget))
							attackTarget = npc;
						
					}
				}
			}
			else
			{
				return false;
			}
			
			if(attackTarget != null && !actor.isAttackingNow() && !actor.isCastingNow())
			{
				actor.getAggroList().addDamageHate(attackTarget, 10, 10);
				actor.setAggressionTarget(attackTarget);
				actor.setRunning();
				setIntention(CtrlIntention.AI_INTENTION_ATTACK, attackTarget);
				return true;
			}
			
		}
		
		return false;
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
	
	private boolean checkAttackTarget(NpcInstance attackTarget)
	{
		if(attackTarget == null)
			return false;
		
		if (((NpcInstance) attackTarget).isInFaction(getActor()) ||
			!attackTarget.isVisible() || 
			attackTarget.isDead() || 
			!GeoEngine.canSeeTarget(getActor(), attackTarget, false))
		{
			return false;
		}
		
		return true;
	}
	
	@Override
	protected boolean checkTarget(Creature target, int range)
	{
		NpcInstance actor = getActor();
		if(target == null || target.isAlikeDead() || !actor.isInRangeZ(target, range))
			return false;

		if(target.isNpc() && !target.isAutoAttackable(actor))
			return false;
		
		if(((NpcInstance) target).isInFaction(actor))
			return false;
		
		if(target.isPlayable())
			return false;

		final boolean hided = target.isPlayable() && !canSeeInHide((Playable) target);

		if(!hided && actor.isConfused())
			return true;

		if(getIntention() == CtrlIntention.AI_INTENTION_ATTACK)
		{
			AggroInfo ai = actor.getAggroList().get(target);
			if(ai != null)
			{
				if(hided)
				{
					ai.hate = 0; // очищаем хейт
					return false;
				}
				return ai.hate > 0;
			}
			return false;
		}

		return canAttackCharacter(target);
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