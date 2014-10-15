package l2s.gameserver.network.l2.s2c;

public class ExAlchemyTransmuteResult extends L2GameServerPacket
{
	private long item_amount;

	public ExAlchemyTransmuteResult(long amount)
	{
		item_amount = amount;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0);
		writeQ(item_amount);
	}
}