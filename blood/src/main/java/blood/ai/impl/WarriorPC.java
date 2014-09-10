package blood.ai.impl;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.base.ClassLevel;
import blood.ai.EventFPC;

public class WarriorPC extends EventFPC
{
	public WarriorPC(Player actor)
	{
		super(actor);
	}
	
	public void prepareSkillsSetup() {
		_allowSelfBuffSkills.add(FPCDreadnought.SKILL_WARCRY);
		_allowSelfBuffSkills.add(FPCDuelist.SKILL_DEFKECT_ARROW);
		_allowSelfBuffSkills.add(FPCDuelist.SKILL_MAJESTY);
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

	protected boolean defaultSubFightTask(Creature target)
	{
		lameFightTask(target);
		return true;
	}
	
}