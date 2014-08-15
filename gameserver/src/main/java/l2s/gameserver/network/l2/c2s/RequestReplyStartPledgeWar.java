package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;

public final class RequestReplyStartPledgeWar extends L2GameClientPacket
{
	private int _answer;

	@Override
	protected void readImpl()
	{
		String _reqName = readS();
		_answer = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(_answer == 0) //case delayed
		{
		 //TODO [bonux]
		}

	}
}