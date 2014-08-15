package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.GameClient;

/**
 * @author Bonux
**/
public class RequestPledgeDraftListApply extends L2GameClientPacket
{
	private int _applyType;
	private int _searchType;

	protected void readImpl() throws Exception
	{
		_applyType = readD();
		_searchType = readD();
	}

	protected void runImpl() throws Exception
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		switch(_applyType)
		{
			case 0:
				break;
			case 1:
				break;
		}
	}
}