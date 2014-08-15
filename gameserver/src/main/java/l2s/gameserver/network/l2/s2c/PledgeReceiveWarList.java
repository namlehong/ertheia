package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.model.pledge.Clan;

public class PledgeReceiveWarList extends L2GameServerPacket
{
	private List<WarInfo> infos = new ArrayList<WarInfo>();
	private int _state;
	@SuppressWarnings("unused")
	private int _page;

	public PledgeReceiveWarList(Clan clan, int state, int page)
	{
		_state = state;
		_page = page;

		List<Clan> clans = _state == 1 ? clan.getAttackerClans() : clan.getEnemyClans();
		for(Clan _clan : clans)
		{
			if(_clan == null)
				continue;
			infos.add(new WarInfo(_clan.getName(), _clan.getAllSize()));
		}
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_page);
		writeD(infos.size());
		for(WarInfo _info : infos)
		{
			writeS(_info.clan_name);
			writeD(_state);
			writeD(86400);

			writeD(0);

			writeD(0);
			writeD(_info.members_count);
		}
	}

	static class WarInfo
	{
		public String clan_name;
		public int members_count;

		public WarInfo(String _clan_name, int _members_count)
		{
			clan_name = _clan_name;
			members_count = _members_count;
		}
	}
}