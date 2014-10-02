package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;

public class RecipeShopStart extends L2GameServerPacket
{
	private int _objectId;

	public RecipeShopStart(Player player)
	{
		_objectId = player.getObjectId();
	}

	@Override
	protected boolean writeOpcodes()
	{
		writeC(225); //0xE1

		return true;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeD(_objectId);
		//re-create the raw packet
		//0B 00 00 00 17 00 01 00 00 06 00 00 05 00 01 
		writeD(11); 
		writeD(65559);
		writeD(1536);
		writeD(65541);
	}
}