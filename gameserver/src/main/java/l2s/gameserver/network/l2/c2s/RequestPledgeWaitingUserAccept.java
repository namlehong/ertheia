package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.GameClient;

public class RequestPledgeWaitingUserAccept extends L2GameClientPacket
{
	protected void readImpl() throws Exception
	{
		readD();
		readD();
		readD();
	}

	protected void runImpl() throws Exception
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

	}
}