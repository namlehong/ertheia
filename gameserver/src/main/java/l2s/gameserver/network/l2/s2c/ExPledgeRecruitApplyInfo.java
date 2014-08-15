package l2s.gameserver.network.l2.s2c;

/**
 * @author Bonux
**/
public class ExPledgeRecruitApplyInfo extends L2GameServerPacket
{
	public static final L2GameServerPacket STATUS_TYPE_DEFAULT = new ExPledgeRecruitApplyInfo(0x00);
	public static final L2GameServerPacket STATUS_TYPE_ORDER_LIST = new ExPledgeRecruitApplyInfo(0x01);
	public static final L2GameServerPacket STATUS_TYPE_CLAN_REG = new ExPledgeRecruitApplyInfo(0x02);
	public static final L2GameServerPacket STATUS_TYPE_UNKNOWN = new ExPledgeRecruitApplyInfo(0x03);
	public static final L2GameServerPacket STATUS_TYPE_WAITING = new ExPledgeRecruitApplyInfo(0x04);

	private final int _state;

	public ExPledgeRecruitApplyInfo(int state)
	{
		_state = state;
	}

	protected void writeImpl()
	{
		writeD(_state);
	}
}