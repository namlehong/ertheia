package l2s.gameserver.network.l2.s2c;

import java.util.Collection;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;

public class ExAlchemySkillList extends L2GameServerPacket
{
	private Collection<Skill> _alchemySkills;

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
			writeD(0);
			writeC(0);
			writeC(0);
			writeC(0);
			if(skill.getId() == 17943)
				writeC(0);
			else
				writeC(1);
		}
	}
}