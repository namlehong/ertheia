package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;

public class ExUserInfoFishing extends L2GameServerPacket
{
	private Player _activeChar;

	public ExUserInfoFishing(Player character)
	{
		_activeChar = character;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_activeChar.getObjectId());
		writeC(_activeChar.isFishing() ? 1 : 0);
		writeD(_activeChar.getFishing().getFishLoc().getX());
		writeD(_activeChar.getFishing().getFishLoc().getY());
		writeD(_activeChar.getFishing().getFishLoc().getZ());
	}
}
