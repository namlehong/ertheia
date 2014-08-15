package l2s.gameserver.skills.effects;

import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Skill.SkillType;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public final class EffectInvulnerable extends Effect
{
	public EffectInvulnerable(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if(_effected.isInvul())
			return false;

		Skill skill = _effected.getCastingSkill();
		if(skill != null && (skill.getSkillType() == SkillType.TAKECASTLE || skill.getSkillType() == SkillType.TAKEFORTRESS))
			return false;

		skill = _effected.getDualCastingSkill();
		if(skill != null && (skill.getSkillType() == SkillType.TAKECASTLE || skill.getSkillType() == SkillType.TAKEFORTRESS))
			return false;

		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		super.onStart();
		//_effected.startHealBlocked(); why?!
		_effected.setIsInvul(true);
	}

	@Override
	public void onExit()
	{
		//_effected.stopHealBlocked();
		_effected.setIsInvul(false);
		super.onExit();
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}