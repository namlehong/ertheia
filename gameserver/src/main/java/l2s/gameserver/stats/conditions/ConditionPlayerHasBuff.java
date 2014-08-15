package l2s.gameserver.stats.conditions;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.skills.EffectType;
import l2s.gameserver.stats.Env;

public class ConditionPlayerHasBuff extends Condition
{
	private final EffectType _effectType;
	private final int _level;

	public ConditionPlayerHasBuff(EffectType effectType, int level)
	{
		_effectType = effectType;
		_level = level;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		Creature character = env.character;
		if(character == null)
			return false;

		for(Effect effect : character.getEffectList().getEffects())
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