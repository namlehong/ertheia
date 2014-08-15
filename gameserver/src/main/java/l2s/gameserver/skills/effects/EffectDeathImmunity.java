package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * Target is immune to death.
 *
 * @author Yorie
 */
public final class EffectDeathImmunity extends Effect
{
	public EffectDeathImmunity(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public boolean checkCondition()
	{
		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		getEffected().startDeathImmunity();
	}

	@Override
	public void onExit()
	{
		getEffected().stopDeathImmunity();
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}