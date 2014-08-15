package l2s.gameserver.skills.effects;

import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public final class EffectDestroySummon extends Effect
{
	public EffectDestroySummon(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if(!_effected.isSummon())
			return false;
		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		super.onStart();
		((Servitor) _effected).unSummon(false);
	}

	@Override
	public boolean onActionTime()
	{
		// just stop this effect
		return false;
	}
}