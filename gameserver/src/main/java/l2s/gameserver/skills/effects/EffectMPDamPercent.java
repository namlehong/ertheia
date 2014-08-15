package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public class EffectMPDamPercent extends Effect
{
	public EffectMPDamPercent(final Env env, final EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();

		if(_effected.isDead())
			return;

		double newMp = (100. - calc()) * _effected.getMaxMp() / 100.;
		newMp = Math.min(_effected.getCurrentMp(), Math.max(0, newMp));
		_effected.setCurrentMp(newMp);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}