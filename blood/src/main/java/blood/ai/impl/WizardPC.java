package blood.ai.impl;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import blood.ai.impl.FPSkills.Archmage;
import blood.ai.impl.FPSkills.MysticMuse;
import blood.ai.impl.FPSkills.Soultaker;
import blood.ai.impl.FPSkills.StormScreamer;

/**
 * 
 * @author mylove1412
 *
 * Logic inside Feoh AI
 * 
 * Select stance in situation ? -> not buff -> active by situation
 *
 */

public class WizardPC extends MysticPC
{	
	// should add auto learn skill
	
	public WizardPC(Player actor)
	{
		super(actor);
//		System.out.println("init new FPCWizzard");
	}
	
	protected boolean isAllowClass()
	{
		return getActor().getClassId().isOfLevel(ClassLevel.SECOND) || getActor().getClassId().isOfLevel(ClassLevel.THIRD);
	}
	
	public void prepareSkillsSetup() {
		_allowSelfBuffSkills.add(Archmage.SKILL_ARCANE_POWER);
		_allowSelfBuffSkills.add(Archmage.SKILL_BLAZING_SKIN);
		_allowSelfBuffSkills.add(Archmage.SKILL_FLAME_ARMOR);
		_allowSelfBuffSkills.add(Archmage.SKILL_SEED_OF_FIRE);
		
		_allowSelfBuffSkills.add(MysticMuse.SKILL_FREEZING_SKIN);
		_allowSelfBuffSkills.add(MysticMuse.SKILL_FROST_ARMOR);
		_allowSelfBuffSkills.add(MysticMuse.SKILL_SEED_OF_WATER);
		_allowSelfBuffSkills.add(MysticMuse.SKILL_MANA_REGENERATION);
		_allowSelfBuffSkills.add(MysticMuse.SKILL_RESIST_AQUA);
		
		_allowSelfBuffSkills.add(StormScreamer.SKILL_HURRICANE_ARMOR);
		_allowSelfBuffSkills.add(StormScreamer.SKILL_SEED_OF_WIND);
		_allowSelfBuffSkills.add(StormScreamer.SKILL_EMPOWERING_ECHO);
		
		_allowSkills.add(Soultaker.SKILL_SUMMON_CURSED_MAN);
		_allowSkills.add(Soultaker.SKILL_SUMMON_REANIMATED_MAN);
	}
	
	protected boolean wizardFightTask(Creature target)
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
		
		if(playerHP < 80)
		{
			if(canUseSkill(StormScreamer.SKILL_DARK_VORTEX, target, distance))
				return tryCastSkill(StormScreamer.SKILL_DARK_VORTEX, target, distance);
			
			if(canUseSkill(StormScreamer.SKILL_VAMPIRIC_CLAW, target, distance))
				return tryCastSkill(StormScreamer.SKILL_VAMPIRIC_CLAW, target, distance);
		}
		
		if(playerHP > 50 && playerMP < 80 && canUseSkill(StormScreamer.SKILL_BODY_TO_MIND, player))
			return tryCastSkill(StormScreamer.SKILL_BODY_TO_MIND, player);
		
		if(canUseSkill(Soultaker.SKILL_CURSE_GLOOM, target, distance))
			return tryCastSkill(Soultaker.SKILL_CURSE_GLOOM, target, distance);
		
		if(canUseSkill(Archmage.SKILL_SURRENDER_TO_FIRE, target, distance))
			return tryCastSkill(Archmage.SKILL_SURRENDER_TO_FIRE, target, distance);
		
		if(canUseSkill(MysticMuse.SKILL_SURRENDER_TO_WATER, target, distance))
			return tryCastSkill(MysticMuse.SKILL_SURRENDER_TO_WATER, target, distance);
		
		if(canUseSkill(StormScreamer.SKILL_SURRENDER_TO_WIND, target, distance) && !player.getClassId().equalsOrChildOf(ClassId.NECROMANCER))
			return tryCastSkill(StormScreamer.SKILL_SURRENDER_TO_WIND, target, distance);
		
		if(canUseSkill(StormScreamer.SKILL_DEATH_SPIKE, target, distance))
			return tryCastSkill(StormScreamer.SKILL_DEATH_SPIKE, target, distance);
		
		if(canUseSkill(Archmage.SKILL_PROMINENCE, target, distance))
			return tryCastSkill(Archmage.SKILL_PROMINENCE, target, distance);
		
		if(canUseSkill(MysticMuse.SKILL_HYDRO_BLAST, target, distance))
			return tryCastSkill(MysticMuse.SKILL_HYDRO_BLAST, target, distance);
		
		if(canUseSkill(StormScreamer.SKILL_HURRICANE, target, distance))
			return tryCastSkill(StormScreamer.SKILL_HURRICANE, target, distance);
		
		tryMoveToTarget(target, 600);
		return false;
	}
	
	protected boolean defaultSubFightTask(Creature target)
	{
		wizardFightTask(target);
		return true;
	}
	
}

