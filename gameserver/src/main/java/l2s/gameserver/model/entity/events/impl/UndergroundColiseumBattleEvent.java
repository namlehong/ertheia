package l2s.gameserver.model.entity.events.impl;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.events.GlobalEvent;
import l2s.gameserver.network.l2.components.SystemMsg;

/**
 * @author VISTALL
 * @date 15:25/23.07.2011
 */
public class UndergroundColiseumBattleEvent extends GlobalEvent
{
	protected UndergroundColiseumBattleEvent(Player player1, Player player2)
	{
		super(0, player1.getObjectId() + "_" + player2.getObjectId());
	}

	@Override
	public void announce(SystemMsg msgId, int val, int time)
	{
		switch(val)
		{
			case -180:
			case -120:
			case -60:
				break;
		}
	}

	@Override
	public void reCalcNextTime(boolean onInit)
	{
		registerActions();
	}

	@Override
	protected long startTimeMillis()
	{
		return System.currentTimeMillis() + 180000L;
	}
}
