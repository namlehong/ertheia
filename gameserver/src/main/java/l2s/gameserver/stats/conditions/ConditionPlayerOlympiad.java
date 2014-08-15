package l2s.gameserver.stats.conditions;

import l2s.gameserver.stats.Env;

public class ConditionPlayerOlympiad extends Condition
{
	private final boolean _value;

	public ConditionPlayerOlympiad(boolean v)
	{
		_value = v;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(env.character.isPlayer() && env.character.getPlayer().getLfcGame() != null)
			return false;		
		return env.character.isInOlympiadMode() == _value;
	}
}