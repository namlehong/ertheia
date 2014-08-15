package l2s.gameserver.skills.effects;

import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public class i_dispel_all extends Effect
{
	public i_dispel_all(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();

		for(Effect effect : getEffected().getEffectList().getEffects())
		{
			if(!effect.isCancelable())
				continue;

			Skill effectSkill = effect.getSkill();
			if(effectSkill == null)
				continue;

			if(effectSkill.isToggle())
				continue;

			if(effectSkill.isPassive())
				continue;

			if(getEffected().isSpecialEffect(effectSkill))
				continue;

			effect.exit();
		}
	}

	@Override
	public void onExit()
	{
		super.onExit();
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}