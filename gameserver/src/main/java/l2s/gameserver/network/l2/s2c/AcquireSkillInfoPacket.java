package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2s.gameserver.model.SkillLearn;
import l2s.gameserver.model.base.AcquireType;

/**
 * Reworked: Hien Son
 */
public class AcquireSkillInfoPacket extends L2GameServerPacket
{
	private SkillLearn _learn;
	private AcquireType _type;
	private List<Require> _reqs = Collections.emptyList();

	public AcquireSkillInfoPacket(AcquireType type, SkillLearn learn)
	{
		_type = type;
		_learn = learn;
		if(_learn.getItemId() != 0)
		{
			_reqs = new ArrayList<Require>(1);
			_reqs.add(new Require(4, _learn.getItemId(), _learn.getItemCount(), 0));
		}
	}

	@Override
	public void writeImpl()
	{
		writeD(_learn.getId());
		writeD(_learn.getLevel());
		writeQ(_learn.getCost()); // sp/rep
		writeD(_type.ordinal());

		writeD(_reqs.size()); //requires size

		for(Require temp : _reqs)
		{
			writeD(temp.type);
			writeD(temp.itemId);
			writeQ(temp.count);
			writeQ(temp.unk);
		}
	}

	private static class Require
	{
		public int itemId;
		public long count;
		public int type;
		public int unk;

		public Require(int pType, int pItemId, long pCount, int pUnk)
		{
			itemId = pItemId;
			type = pType;
			count = pCount;
			unk = pUnk;
		}
	}
}