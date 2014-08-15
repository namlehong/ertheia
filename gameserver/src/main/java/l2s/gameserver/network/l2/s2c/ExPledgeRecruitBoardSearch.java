package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.pledge.Clan;

/**
 * @author Bonux
**/
public class ExPledgeRecruitBoardSearch extends L2GameServerPacket
{
	public final String _leaderName;
	public final String _clanName;
	public final String _notice;
	public final int _clanLvl;
	public final int _clanSize;
	public final int _clanCount;

	public ExPledgeRecruitBoardSearch(Player player)
	{
		Clan clan = player.getClan();
		//if(clan != null)
		//{
			_leaderName = clan.getLeaderName();
			_clanName = clan.getName();
			_notice = clan.getNotice();
			_clanLvl = clan.getLevel();
			_clanSize = clan.getAllSize();

			_clanCount = 0;
		//}
	}

	protected void writeImpl()
	{
		writeD(0);
		writeD(0);
		writeD(0);
		writeD(0);
		writeD(_clanCount);
		for(int i = 0; i < _clanCount; i++)
		{
			writeD(0);
			writeD(0);
			writeS(_clanName);
			writeS(_leaderName);
			writeD(_clanLvl);
			writeD(_clanSize);
			writeD(0);
			writeS(_notice);
		}
	}
}