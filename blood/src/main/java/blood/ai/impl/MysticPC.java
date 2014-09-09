package blood.ai.impl;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.base.ClassLevel;
import blood.ai.EventFPC;
import blood.ai.impl.FPSkills.DarkWizard;
import blood.ai.impl.FPSkills.ElvenWizard;
import blood.ai.impl.FPSkills.OrcShaman;
import blood.ai.impl.FPSkills.StormScreamer;
import blood.ai.impl.FPSkills.Wizzard;

public class MysticPC extends EventFPC
{
	public MysticPC(Player actor)
	{
		super(actor);
	}
	
	protected boolean isAllowClass()
	{
		return getActor().getClassId().isOfLevel(ClassLevel.NONE) || getActor().getClassId().isOfLevel(ClassLevel.FIRST);
	}
	
	public void prepareSkillsSetup() {
		
	}
	
	protected boolean mysticFightTask(Creature target)
	{
		Player player = getActor();
		double distance = player.getDistance(target);
		double playerHP = player.getCurrentHpPercents();
		double playerMP = player.getCurrentMpPercents();
		
		for(Servitor summon: player.getServitors())
		{
			summon.getAI().Attack(target, true, false);
		}
		
		if(isUseBow())
			return chooseTaskAndTargets(null, target, distance);
		
		if(playerHP < 70)
		{
			if(canUseSkill(DarkWizard.SKILL_VAMPIRIC_TOUCH, target, distance))
				return tryCastSkill(DarkWizard.SKILL_VAMPIRIC_TOUCH, target, distance);
		}
		
		if(playerHP > 50 && playerMP < 80 && canUseSkill(StormScreamer.SKILL_BODY_TO_MIND, player))
			return tryCastSkill(StormScreamer.SKILL_BODY_TO_MIND, player);
		
		if(canUseSkill(Wizzard.SKILL_BLAZE, target, distance))
			return tryCastSkill(Wizzard.SKILL_BLAZE, target, distance);
		
		if(canUseSkill(ElvenWizard.SKILL_AQUA_SWIRL, target, distance))
			return tryCastSkill(ElvenWizard.SKILL_AQUA_SWIRL, target, distance);
		
		if(canUseSkill(ElvenWizard.SKILL_SOLAR_SPARK, target, distance))
			return tryCastSkill(ElvenWizard.SKILL_SOLAR_SPARK, target, distance);
		
		if(canUseSkill(DarkWizard.SKILL_TWISTER, target, distance))
			return tryCastSkill(DarkWizard.SKILL_TWISTER, target, distance);
		
		if(canUseSkill(DarkWizard.SKILL_SHADOW_SPARK, target, distance))
			return tryCastSkill(DarkWizard.SKILL_SHADOW_SPARK, target, distance);
		
		if(canUseSkill(OrcShaman.SKILL_LIFE_DRAIN, target, distance))
			return tryCastSkill(OrcShaman.SKILL_LIFE_DRAIN, target, distance);
		
		tryMoveToTarget(target, 600);
		return false;
	}
	
	protected boolean defaultSubFightTask(Creature target)
	{
		mysticFightTask(target);
		return true;
	}
	
}