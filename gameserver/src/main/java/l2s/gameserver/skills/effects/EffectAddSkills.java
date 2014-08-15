package l2s.gameserver.skills.effects;

import l2s.gameserver.model.Skill.AddedSkill;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public class EffectAddSkills extends Effect
{
	public EffectAddSkills(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		for(AddedSkill as : getSkill().getAddedSkills())
			getEffected().addSkill(as.getSkill());
	}

	@Override
	public void onExit()
	{
		super.onExit();
		for(AddedSkill as : getSkill().getAddedSkills())
			getEffected().removeSkill(as.getSkill());
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}