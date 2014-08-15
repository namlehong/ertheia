package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.model.Player;

/**
 * @author Bonux
**/
public class ExPledgeDraftListSearch extends L2GameServerPacket
{
	private int _searchType;
	private String Name;
	private List<Player> players;

	public ExPledgeDraftListSearch(Player player, int minLvl, int maxLvl, int classId, String charName, int sort, int order)
	{
		players = new ArrayList<Player>();
		players.add(player);
		_searchType = sort;
		Name = charName;
	}

	protected void writeImpl()
	{
		writeD(players.size());
		for(Player p : players)
		{
			writeD(p.getObjectId());
			writeS(p.getName());
			writeD(_searchType);
			writeD(p.getClassId().getId());
			writeD(p.getLevel());
		}
	}
}