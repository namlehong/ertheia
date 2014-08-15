package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.worldstatistics.CharacterStatisticElement;

import java.util.List;

/**
 * @author KilRoy
 */
public class ExLoadStatUser extends L2GameServerPacket
{
	private List<CharacterStatisticElement> _list;

	public ExLoadStatUser(List<CharacterStatisticElement> list)
	{
		_list = list;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_list.size());
		for(CharacterStatisticElement stat : _list)
		{
			writeD(stat.getCategoryType().getClientId());
			writeD(stat.getCategoryType().getSubcat());
			writeQ(stat.getMonthlyValue());
			writeQ(stat.getValue());
		}
	}
}