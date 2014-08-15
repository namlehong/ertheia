package l2s.gameserver.stats.conditions;

import l2s.gameserver.stats.Env;

public class ConditionPlayerChaosFestival extends Condition
{
	private final boolean _value;

	public ConditionPlayerChaosFestival(boolean v)
	{
		_value = v;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(!env.character.isPlayer())
			return !_value;

		return env.character.getPlayer().isChaosFestivalParticipant() == _value;
	}
}