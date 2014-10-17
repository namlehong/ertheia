package l2s.gameserver.network.l2.s2c;

import java.util.List;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Skill.SkillType;

public class ExAlchemySkillList extends L2GameServerPacket
{
	private List<Skill> _alchemySkills;

	public ExAlchemySkillList(Player player)
	{
		_alchemySkills = player.getAlchemySkills();
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_alchemySkills.size());
		for(Skill skill : _alchemySkills)
		{
			writeD(skill.getId());
			writeD(skill.getLevel());
			writeQ(0);
			if(skill.getSkillType() == SkillType.COMBINE)
				writeC(0);
			else
				writeC(1);
		}
	}
}