package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.network.l2.ServerPacketOpcodes;

/**
 * Reworked: Hien Son
 */
public class ExAcquirableSkillListAlchemy extends L2GameServerPacket
{
	private final List<Skill> _skills;

	class Skill
	{
		public int id;
		public int nextLevel;
		public int maxLevel;
		public int cost;
		public int requirements;
		public int subUnit;

		Skill(int id, int nextLevel, int maxLevel, int cost, int requirements, int subUnit)
		{
			this.id = id;
			this.nextLevel = nextLevel;
			this.maxLevel = maxLevel;
			this.cost = cost;
			this.requirements = requirements;
			this.subUnit = subUnit;
		}
	}

	public ExAcquirableSkillListAlchemy(int size)
	{
		_skills = new ArrayList<Skill>(size);
	}

	public void addSkill(int id, int nextLevel, int maxLevel, int Cost, int requirements, int subUnit)
	{
		_skills.add(new Skill(id, nextLevel, maxLevel, Cost, requirements, subUnit));
	}

	public void addSkill(int id, int nextLevel, int maxLevel, int Cost, int requirements)
	{
		_skills.add(new Skill(id, nextLevel, maxLevel, Cost, requirements, 0));
	}

	@Override
	protected boolean writeOpcodes()
	{
		writeC(0xFE);
		writeH(0xFA);
		return true;
	}

	@Override
	protected final void writeImpl()
	{
		writeH(180);
		writeH(_skills.size());

		for(Skill temp : _skills)
		{
			writeD(temp.id);
			writeH(temp.nextLevel);
			writeH(temp.maxLevel);
			writeC(0x00);
			writeQ(temp.cost);
			writeC(0x01); // UNK
			/*writeD(temp.requirements);
			if(_type == AcquireType.SUB_UNIT)
				writeC(temp.subUnit);*/
		}
	}
}