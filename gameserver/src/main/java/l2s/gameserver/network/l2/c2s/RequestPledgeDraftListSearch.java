package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.GameClient;
import l2s.gameserver.network.l2.s2c.ExPledgeDraftListSearch;

/**
 * @author Bonux
**/
public class RequestPledgeDraftListSearch extends L2GameClientPacket
{
	private int _minLevel;
	private int _maxLevel;
	private int _role;
	private String _charName;
	private int _sortType;
	private int _sortOrder;

	protected void readImpl() throws Exception
	{
		_minLevel = readD();
		_maxLevel = readD();
		_role = readD();
		_charName = readS();
		_sortType = readD();
		_sortOrder = readD();
	}

	protected void runImpl() throws Exception
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		activeChar.sendPacket(new ExPledgeDraftListSearch(activeChar, _minLevel, _maxLevel, _role, _charName, _sortType, _sortOrder));
	}
}