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
		writeC(50); //0x32

		return true;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeD(_objectId);
		//re-create the raw packet
		//0B 00 00 00 17 00 01 00 00 06 00 00 05 00 01 
		writeB(new byte[]{
				(byte) 0x0B, 
				(byte) 0x00, 
				(byte) 0x00, 
				(byte) 0x00, 
				(byte) 0x17, 
				(byte) 0x00, 
				(byte) 0x01, 
				(byte) 0x00, 
				(byte) 0x00, 
				(byte) 0x06, 
				(byte) 0x00, 
				(byte) 0x00, 
				(byte) 0x05, 
				(byte) 0x00, 
				(byte) 0x01});
	}
}