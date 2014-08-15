package l2s.gameserver.stats.conditions;

import l2s.gameserver.model.entity.residence.ResidenceSide;
import l2s.gameserver.stats.Env;

public class ConditionPlayerCastleType extends Condition
{
	private final int _type;

	public ConditionPlayerCastleType(int type)
	{
		_type = type;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(!env.character.isPlayer())
			return false;
		if(env.character.getPlayer().getClan() == null || env.character.getPlayer().getClan().getCastle() <= 0)
			return false;
		if(_type == 0 && env.character.getPlayer().getCastle().getResidenceSide() != ResidenceSide.LIGHT)
			return false;
		if(_type == 1 && env.character.getPlayer().getCastle().getResidenceSide() != ResidenceSide.DARK)	
			return false;	
		return true;
	}
}