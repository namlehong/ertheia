package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public class EffectIgnoreDeath extends Effect
{

	public EffectIgnoreDeath(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public boolean onActionTime()
	{
		if(getEffected().getCurrentHp() < 2)
			getEffected().setCurrentHp(2, false);
		return false;
	}

}
