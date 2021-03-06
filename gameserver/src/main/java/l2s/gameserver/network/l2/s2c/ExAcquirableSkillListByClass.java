package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.model.base.AcquireType;

/**
 * Reworked: VISTALL
 */
public class ExAcquirableSkillListByClass extends L2GameServerPacket
{
	private AcquireType _type;
	private final List<Skill> _skills;

	class Skill
	{
		public int id;
		public int nextLevel;
		public int maxLevel;
		public int requireLevel;
		public int cost;
		public int requirements;
		public int subUnit;

		Skill(int id, int nextLevel, int maxLevel, int requireLevel, int cost, int requirements, int subUnit)
		{
			this.id = id;
			this.nextLevel = nextLevel;
			this.maxLevel = maxLevel;
			this.requireLevel = requireLevel;
			this.cost = cost;
			this.requirements = requirements;
			this.subUnit = subUnit;
		}
	}

	public ExAcquirableSkillListByClass(AcquireType type, int size)
	{
		_skills = new ArrayList<Skill>(size);
		_type = type;
	}

	public void addSkill(int id, int nextLevel, int maxLevel, int requireLevel, int Cost, int requirements, int subUnit)
	{
		_skills.add(new Skill(id, nextLevel, maxLevel, requireLevel, Cost, requirements, subUnit));
	}

	public void addSkill(int id, int nextLevel, int maxLevel, int requireLevel, int Cost, int requirements)
	{
		_skills.add(new Skill(id, nextLevel, maxLevel, requireLevel, Cost, requirements, 0));
	}

	@Override
	protected final void writeImpl()
	{
		writeH(_type.ordinal());
		writeH(_skills.size());

		for(Skill temp : _skills)
		{
			writeD(temp.id);
			writeH(temp.nextLevel);
			writeH(temp.maxLevel);
			writeC(temp.requireLevel);
			writeQ(temp.cost);
			writeC(0x01); // UNK
			/*writeD(temp.requirements);
			if(_type == AcquireType.SUB_UNIT)
				writeC(temp.subUnit);*/
		}
	}
}