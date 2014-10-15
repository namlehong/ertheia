package l2s.gameserver.network.l2.s2c;

public class ExAlchemyCombinationResult extends L2GameServerPacket
{
	private int _itemId;
	private long _itemCount;

	public ExAlchemyCombinationResult(int itemId, long itemCount)
	{
		_itemId = itemId;
		_itemCount = itemCount;
	}

	@Override
	protected void writeImpl()
	{
		writeH(256); //doesn't know why :-<
		writeD(0); //doesn't know why either
		writeD(_itemId);
		writeQ(_itemCount);
	}
}