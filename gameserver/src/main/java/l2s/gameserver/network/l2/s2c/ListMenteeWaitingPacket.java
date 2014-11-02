package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Darvin
 * Date: 02.07.12
 * Time: 0:46
 */
public class ListMenteeWaitingPacket extends L2GameServerPacket
{
	List<Player> mentees = new ArrayList<Player>();
	int page;
	int playersInPage;

	public ListMenteeWaitingPacket(Player activeChar, int _page, int minLevel, int maxLevel)
	{
		mentees = new ArrayList<Player>();
		page = _page;
		playersInPage = 64;

		for(Player player : World.getAroundPlayers(activeChar))
		{
			int mentorId = player.getMenteeList().getMentor();			
			if((player.getLevel() >= minLevel) && (player.getLevel() <= maxLevel) && mentorId != 0)
			{
				mentees.add(player);
			}
		}
	}
	@Override
	protected void writeImpl()
	{
		writeD(this.page);
		int i;
		if(!mentees.isEmpty())
		{
			writeD(mentees.size());
			writeD(mentees.size() % playersInPage);
			i = 1;
			for(Player player : mentees)
			{
				if((i <= playersInPage * page) && (i > playersInPage * (page - 1)))
				{
					writeS(player.getName());
					writeD(player.getClassId().ordinal());
					writeD(player.getLevel());
				}
			}
		}
		else
		{
			writeD(0);
			writeD(0);
		}
	}
}
