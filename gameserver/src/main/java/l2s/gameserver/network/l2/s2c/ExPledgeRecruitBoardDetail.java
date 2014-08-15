package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.pledge.Clan;

/**
 * @author Bonux
**/
public class ExPledgeRecruitBoardDetail extends L2GameServerPacket
{
	private int _clanId;
	private String _desc;
	private String _title;

	public ExPledgeRecruitBoardDetail(Clan clan)
	{
		_clanId = clan.getClanId();
		_desc = clan.getDesc();
		_title = clan.getTitle();
	}

	protected void writeImpl()
	{
		writeD(_clanId);
		writeD(0);
		writeS(_title);
		writeS(_desc);
	}
}