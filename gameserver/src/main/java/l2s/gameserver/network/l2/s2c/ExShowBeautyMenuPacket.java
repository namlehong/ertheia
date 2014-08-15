package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.network.l2.s2c.L2GameServerPacket;

/**
 * @author Bonux
**/
public class ExShowBeautyMenuPacket extends L2GameServerPacket
{
	public static final L2GameServerPacket CHANGE_STYLE_PACKET = new ExShowBeautyMenuPacket(0x00);
	public static final L2GameServerPacket CANCEL_STYLE_PACKET = new ExShowBeautyMenuPacket(0x01);

	private final int _type;

	public ExShowBeautyMenuPacket(int type)
	{
		_type = type;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_type);  // 0x00 - изменение стиля, 0x01 отмена стиля
	}
}
