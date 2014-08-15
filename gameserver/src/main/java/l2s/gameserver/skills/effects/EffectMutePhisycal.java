package l2s.gameserver.skills.effects;

import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public class EffectMutePhisycal extends Effect
{
	public EffectMutePhisycal(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(!_effected.startPMuted())
		{
			Skill castingSkill = _effected.getCastingSkill();
			if(castingSkill != null && !castingSkill.isMagic())
			{
				_effected.abortCast(true, true);
				return;
			}

			castingSkill = _effected.getDualCastingSkill();
			if(castingSkill != null && !castingSkill.isMagic())
				_effected.abortCast(true, true);
		}
	}

	@Override
	public void onExit()
	{
		super.onExit();
		_effected.stopPMuted();
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}