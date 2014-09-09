package blood.ai.impl;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.base.ClassLevel;
import blood.ai.EventFPC;
import blood.ai.impl.FPSkills.Dominator;
import blood.ai.impl.FPSkills.Doomcryer;
import blood.ai.impl.FPSkills.SwordMuse;

public class EnchanterPC extends EventFPC
{
	public EnchanterPC(Player actor)
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
		_allowSelfBuffSkills.add(SwordMuse.SKILL_DEFLECT_MAGIC);
		_allowSelfBuffSkills.add(SwordMuse.SKILL_DEFLECT_ARROW);
		_allowSelfBuffSkills.add(Dominator.SKILL_ARCANE_POWER);
		_allowSelfBuffSkills.add(Doomcryer.SKILL_ARCANE_WISDOM);
	}

	protected boolean defaultSubFightTask(Creature target)
	{
		enchanterFightTask(target);
		return true;
	}
	
	public boolean enchanterFightTask(Creature target)
	{
		Player player = getActor();
		double distance = player.getDistance(target);
		
		if(canUseSkill(Dominator.SKILL_STEAL_ESSENCE, target, distance))
			return tryCastSkill(Dominator.SKILL_STEAL_ESSENCE, target, distance);
		
		return chooseTaskAndTargets(null, target, distance);
	}
	
}

