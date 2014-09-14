package ai.chamber_of_prophecies;

import java.util.List;

import l2s.commons.math.random.RndSelector;
import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.Priest;
import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.World;
import l2s.gameserver.model.instances.DecoyInstance;
import l2s.gameserver.model.instances.NpcInstance;
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
		
		if(chance*100 < getRateHEAL())
		{
			return startHeal();
		}
		else
			return startAttack();
	}
	
	private boolean startHeal()
	{
		NpcInstance actor = getActor();
		if(healTarget == null || healTarget.isNpc() || healTarget.isDead())
		{
			List<Player> around = World.getAroundPlayers(actor);
			if(around != null && !around.isEmpty())
			{
				for(Player player : around)
				{
					if(checkHealattackTarget(player) != 0)
					{
						healTarget = player;
					}
				}
			}
		}

		if(healTarget != null && !actor.isAttackingNow() && !actor.isCastingNow() && !healTarget.isDead() && GeoEngine.canSeeTarget(actor, healTarget, false) && healTarget.isVisible())
		{
			if(checkHealattackTarget(healTarget) == 1)
			{
				Skill[] healSkillList = actor.getTemplate().getHealSkills();
				int randomIndex = (int)Math.random()*healSkillList.length;
				Skill healSkill = healSkillList[randomIndex];
				
				setIntention(CtrlIntention.AI_INTENTION_CAST, healSkill, healTarget);
			}
			else if(checkHealattackTarget(healTarget) == 2)
			{
				Skill[] rechargeSkillList = actor.getTemplate().getHealSkills();
				int randomIndex = (int)Math.random()*rechargeSkillList.length;
				Skill rechargeSkill = rechargeSkillList[randomIndex];
				setIntention(CtrlIntention.AI_INTENTION_CAST, rechargeSkill, healTarget);
				
				
			}
				
			return true;
		}

		if(healTarget != null && (!healTarget.isVisible() || healTarget.isDead() || !GeoEngine.canSeeTarget(actor, healTarget, false)))
		{
			healTarget = null;
			return false;
		}
		
		return false;
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
	protected boolean defaultThinkBuff(int rateSelf, int rateFriends)
	{
		System.out.println("Healer defaultThinkBuff");
		NpcInstance actor = getActor();
		if(actor.isDead())
			return true;

		if(Rnd.chance(rateSelf))
		{
			double actorHp = actor.getCurrentHpPercents();

			Skill[] skills = actorHp < 50 ? selectUsableSkills(actor, 0, _healSkills) : selectUsableSkills(actor, 0, _buffSkills);
			if(skills == null || skills.length == 0)
				return false;

			Skill skill = skills[Rnd.get(skills.length)];
			addTaskBuff(actor, skill);
			return true;
		}

		if(Rnd.chance(rateFriends))
		{
			for(NpcInstance npc : activeFactionTargets())
			{
				double targetHp = npc.getCurrentHpPercents();

				Skill[] skills = targetHp < 50 ? selectUsableSkills(actor, 0, _healSkills) : selectUsableSkills(actor, 0, _buffSkills);
				if(skills == null || skills.length == 0)
					continue;

				Skill skill = skills[Rnd.get(skills.length)];
				addTaskBuff(actor, skill);
				return true;
			}
		}

		return false;
	}

	@Override
	protected boolean defaultFightTask()
	{
		System.out.println("Healer defaultFightTask");
		clearTasks();

		NpcInstance actor = getActor();
		if(actor.isDead() || actor.isAMuted())
			return false;

		Creature target;
		if((target = prepareTarget()) == null)
			return false;

		double distance = actor.getDistance(target);
		double targetHp = target.getCurrentHpPercents();
		double actorHp = actor.getCurrentHpPercents();

		Skill[] dam = Rnd.chance(getRateDAM()) ? selectUsableSkills(target, distance, _damSkills) : null;
		Skill[] dot = Rnd.chance(getRateDOT()) ? selectUsableSkills(target, distance, _dotSkills) : null;
		Skill[] debuff = targetHp > 10 ? Rnd.chance(getRateDEBUFF()) ? selectUsableSkills(target, distance, _debuffSkills) : null : null;
		Skill[] stun = Rnd.chance(getRateSTUN()) ? selectUsableSkills(target, distance, _stunSkills) : null;
		Skill[] heal = actorHp < 50 ? Rnd.chance(getRateHEAL()) ? selectUsableSkills(actor, 0, _healSkills) : null : null;
		Skill[] buff = Rnd.chance(getRateBUFF()) ? selectUsableSkills(actor, 0, _buffSkills) : null;

		RndSelector<Skill[]> rnd = new RndSelector<Skill[]>();
		if(!actor.isAMuted())
			rnd.add(null, getRatePHYS());
		rnd.add(dam, getRateDAM());
		rnd.add(dot, getRateDOT());
		rnd.add(debuff, getRateDEBUFF());
		rnd.add(heal, getRateHEAL());
		rnd.add(buff, getRateBUFF());
		rnd.add(stun, getRateSTUN());

		Skill[] selected = rnd.select();
		if(selected != null)
		{
			if(selected == dam || selected == dot)
				return chooseTaskAndTargets(selectTopSkillByDamage(actor, target, distance, selected), target, distance);

			if(selected == debuff || selected == stun)
				return chooseTaskAndTargets(selectTopSkillByDebuff(actor, target, distance, selected), target, distance);

			if(selected == buff)
				return chooseTaskAndTargets(selectTopSkillByBuff(actor, selected), actor, distance);

			if(selected == heal)
				return chooseTaskAndTargets(selectTopSkillByHeal(actor, selected), actor, distance);
		}

		// TODO сделать лечение и баф дружественных целей

		return chooseTaskAndTargets(null, target, distance);
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