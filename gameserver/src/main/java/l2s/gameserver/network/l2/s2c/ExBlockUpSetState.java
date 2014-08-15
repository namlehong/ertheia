package l2s.gameserver.network.l2.s2c;

public class ExBlockUpSetState extends L2GameServerPacket
{
	private int BlockUpStateType = 0; //TODO

	@Override
	protected void writeImpl()
	{
		writeD(BlockUpStateType);
		switch(BlockUpStateType)
		{
			case 0:
				//dddddd
				break;
			case 1:
				//dd
				break;
			case 2:
				//ddd
				break;
		}
	}
}