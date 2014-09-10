package blood.ai.impl;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.stats.Stats;
import blood.ai.EventFPC;
import blood.ai.impl.FPSkills.GhostSentinel;
import blood.ai.impl.FPSkills.MoonlightSentinel;
import blood.ai.impl.FPSkills.Sagittarius;
import blood.ai.impl.FPSkills.Trickster;

public class ArcherPC extends EventFPC
{
	public ArcherPC(Player actor)
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
		_allowSelfBuffSkills.add(Sagittarius.SKILL_RAPID_SHOT);
		_allowSelfBuffSkills.add(Sagittarius.SKILL_SPIRIT_OF_SAGITTARIUS);
		_allowSelfBuffSkills.add(Sagittarius.SKILL_BLESSING_OF_SAGITTARIUS);
		_allowSelfBuffSkills.add(MoonlightSentinel.SKILL_RAPID_FIRE);
		_allowSelfBuffSkills.add(GhostSentinel.SKILL_DEAD_EYE);
		_allowSelfBuffSkills.add(Trickster.SKILL_FURIOUS_SOUL);
		_allowSelfBuffSkills.add(Trickster.SKILL_FAST_SHOT);
	}

	protected boolean defaultSubFightTask(Creature target)
	{
		archerFightTask(target);
		return true;
	}
	
	protected boolean archerFightTask(Creature target)
	{
		Player actor = getActor();
		
		double distance = actor.getDistance(target);
		
		// TODO - add archer skill task
		
		return chooseTaskAndTargets(null, target, distance);
	}
	
}