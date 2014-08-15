package l2s.gameserver.network.l2.s2c;

//открывается   окошко и написано ничья, кароче лисп победителя
public class ExCuriousHouseObserveList extends L2GameServerPacket
{
	public ExCuriousHouseObserveList()
	{
		//
	}

	@Override
	protected void writeImpl()
	{
		writeD(0);

		for(; ; )
		{
			writeD(0);
			writeS("");

			writeH(0);

			writeD(0);
		}
	}
}
