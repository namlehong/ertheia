package l2s.gameserver.network.l2.s2c;

/**
 * @author Bonux
**/
public class ExPledgeWaitingList extends L2GameServerPacket
{
	protected void writeImpl()
	{
		writeD(0);
		while(true)
		{
			writeD(0);
			writeS("");
			writeD(0);
			writeD(0);
		}
	}
}