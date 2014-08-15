package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public class EffectCPDamPercent extends Effect
{
	public EffectCPDamPercent(final Env env, final EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();

		if(_effected.isDead() || !_effected.isPlayer())
			return;

		double newCp = (100. - calc()) * _effected.getMaxCp() / 100.;
		newCp = Math.min(_effected.getCurrentCp(), Math.max(0, newCp));
		_effected.setCurrentCp(newCp);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}