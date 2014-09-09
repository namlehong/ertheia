package blood.ai.impl;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.utils.PositionUtils;
import blood.ai.EventFPC;
import blood.ai.impl.FPSkills.Adventurer;
import blood.ai.impl.FPSkills.FortuneSeeker;

public class RougePC extends EventFPC
{
	public RougePC(Player actor)
	{
		super(actor);
	}
	
	protected boolean isAllowClass()
	{
		return getActor().getClassId().isOfLevel(ClassLevel.SECOND) || getActor().getClassId().isOfLevel(ClassLevel.THIRD);
	}
	
	protected Skill getNpcSuperiorBuff()
	{
//		return getSkill(15648, 1); //tank
		return getSkill(15649, 1); //warrior
//		return getSkill(15650, 1); //wizzard
	}

	public void prepareSkillsSetup() {
		
	}
	
	protected boolean defaultSubFightTask(Creature target)
	{
		othellFightTask(target);
		return true;
	}
	
	protected boolean othellFightTask(Creature target)
	{
		Player player = getActor();
		
		double distance = player.getDistance(target);
		
		if(distance > 300 && canUseSkill(Adventurer.SKILL_SHADOW_STEP, target, distance))
			return tryCastSkill(Adventurer.SKILL_SHADOW_STEP, target, distance);
		
		if(distance > 300 && canUseSkill(FortuneSeeker.SKILL_BLAZING_BOOST, target, distance))
			return tryCastSkill(FortuneSeeker.SKILL_BLAZING_BOOST, target, distance);
		
		switch(PositionUtils.getDirectionTo(target, player)){
		case BEHIND:
			if(canUseSkill(Adventurer.SKILL_BACKSTAB, target, distance))
				return tryCastSkill(Adventurer.SKILL_BACKSTAB, target, distance);
			break;
		case SIDE:
			if(canUseSkill(Adventurer.SKILL_CRITICAL_BLOW, target, distance))
				return tryCastSkill(Adventurer.SKILL_CRITICAL_BLOW, target, distance);
			
			if(canUseSkill(Adventurer.SKILL_MORTAL_BLOW, target, distance))
				return tryCastSkill(Adventurer.SKILL_MORTAL_BLOW, target, distance);
			
			if(canUseSkill(Adventurer.SKILL_DEADLY_BLOW, target, distance))
				return tryCastSkill(Adventurer.SKILL_DEADLY_BLOW, target, distance);
			break;
		default:
			if(canUseSkill(FortuneSeeker.SKILL_LUCKY_BLOW, target, distance))
				return tryCastSkill(FortuneSeeker.SKILL_LUCKY_BLOW, target, distance);
		}
			
		return chooseTaskAndTargets(null, target, distance);
	}
	

	@Override
	protected void protectSelf(Creature attacker, int damage)
	{
		Player player = getActor();
		if(player.isDead() || attacker == null || player.getDistance(attacker) > 700)
			return;

		if(player.isMoving)
			return;
		
		double hpLevel	= player.getCurrentHpPercents();
		double distance = player.getDistance(attacker);
		
		if(hpLevel > 60D)
			return;
		
		if(canUseSkill(Adventurer.SKILL_ULTIMATE_EVASION, player) && tryCastSkill(Adventurer.SKILL_ULTIMATE_EVASION, player))
			return;
		
		if(canUseSkill(Adventurer.SKILL_SWITCH, attacker, distance) && tryCastSkill(Adventurer.SKILL_SWITCH, attacker, distance))
			return;
		
		if(canUseSkill(Adventurer.SKILL_TRICK, attacker, distance) && tryCastSkill(Adventurer.SKILL_TRICK, attacker, distance))
			return;
		
	}
}