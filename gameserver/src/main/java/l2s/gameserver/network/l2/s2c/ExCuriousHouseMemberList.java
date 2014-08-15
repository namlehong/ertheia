package l2s.gameserver.network.l2.s2c;

import java.util.List;

import l2s.gameserver.model.entity.events.objects.ChaosFestivalArenaObject;
import l2s.gameserver.model.entity.events.objects.ChaosFestivalPlayerObject;

/**
 * @author Bonux
**/
public class ExCuriousHouseMemberList extends L2GameServerPacket
{
	private final int _arenaId;
	private List<ChaosFestivalPlayerObject> _members;

	public ExCuriousHouseMemberList(int arenaId, List<ChaosFestivalPlayerObject> members)
	{
		_arenaId = arenaId;
		_members = members;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_arenaId);
		writeD(_members.size()); // ???
		writeD(_members.size());
		for(ChaosFestivalPlayerObject member : _members)
		{
			if(member == null)
				continue;
			writeD(member.getObjectId());
			writeD(member.getId());
			writeD(member.getMaxHp());
			writeD(member.getMaxCp());
			writeD(member.getCurrentHp());
			writeD(member.getCurrentCp());
		}
	}
}
