package l2s.gameserver.network.l2.s2c;

import java.util.Collection;

import l2s.gameserver.model.entity.events.objects.ChaosFestivalPlayerObject;

//ExCuriousHouseResult dh(Sddd)
public class ExCuriousHouseResult extends L2GameServerPacket
{
	public static enum ResultState
	{
		TIE, 
		WIN, 
		LOSE;
	}

	private final int _memberId;
	private final ResultState _state;
	private final Collection<ChaosFestivalPlayerObject> _members;

	public ExCuriousHouseResult(int memberId, ResultState state, Collection<ChaosFestivalPlayerObject> members)
	{
		_memberId = memberId;
		_state = state;
		_members = members;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_memberId);
		writeH(_state.ordinal());
		writeD(_members.size());
		for(ChaosFestivalPlayerObject member : _members)
		{
			writeD(member.getObjectId());
			writeD(member.getId());
			writeD(member.getActiveClassId());
			writeD(member.getLifeTime());
			writeD(member.getKills());
		}
	}
}


