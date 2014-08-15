package l2s.gameserver.stats.conditions;

import java.util.List;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.skills.EffectType;
import l2s.gameserver.stats.Env;

public final class ConditionTargetHasBuff extends Condition
{
	private final EffectType _effectType;
	private final int _level;

	public ConditionTargetHasBuff(EffectType effectType, int level)
	{
		_effectType = effectType;
		_level = level;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		Creature target = env.target;
		if(target == null)
			return false;

		for(Effect effect : target.getEffectList().getEffects())
		{
			if(effect.getEffectType() != _effectType)
				continue;

			if(_level == -1)
				return true;

			if(effect.getSkill().getLevel() >= _level)
				return true;
		}
		return false;
	}
}
