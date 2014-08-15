package l2s.gameserver.model.entity.olympiad;

import l2s.gameserver.Config;

public enum CompType
{
	TEAM(0, 0),
	NON_CLASSED(Config.ALT_OLY_NONCLASSED_RITEM_C, 5),
	CLASSED(Config.ALT_OLY_CLASSED_RITEM_C, 3);

	private int _reward;
	private int _looseMult;

	private CompType(int reward, int looseMult)
	{
		_reward = reward;
		_looseMult = looseMult;
	}

	public int getReward()
	{
		return _reward;
	}

	public int getLooseMult()
	{
		return _looseMult;
	}
}