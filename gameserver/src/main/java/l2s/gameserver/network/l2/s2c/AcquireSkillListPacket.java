package l2s.gameserver.network.l2.s2c;

import java.util.Collection;

import l2s.gameserver.data.xml.holder.SkillAcquireHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.SkillLearn;
import l2s.gameserver.tables.SkillTable;

/**
 * @author VISTALL
 * @date 22:22/25.05.2011
 */
public class AcquireSkillListPacket extends L2GameServerPacket
{
	private Player _player;
	private Collection<SkillLearn> _skills;

	public AcquireSkillListPacket(Player player)
	{
		_player = player;
		_skills = SkillAcquireHolder.getInstance().getAcquirableSkillListByClass(player);
	}

	@Override
	protected final void writeImpl()
	{
		writeH(_skills.size());
		for(SkillLearn sk : _skills)
		{
			Skill skill = SkillTable.getInstance().getInfo(sk.getId(), sk.getLevel());
			if(skill == null)
				continue;

			writeD(sk.getId());
			writeH(sk.getLevel());
			writeQ(sk.getCost());
			writeC(sk.getMinLevel());
			writeC(sk.getDualClassMinLvl()); // Dual-class min level.

			if(sk.getItemId() > 0 && sk.getItemCount() > 0)
			{
				writeC(0x01);
				writeD(sk.getItemId());
				writeQ(sk.getItemCount());
			}
			else
				writeC(0x00);

			Skill[] analogSkills = skill.getAnalogSkills(_player);

			writeC(analogSkills.length);
			for(Skill analogSkill : analogSkills)
			{
				writeD(analogSkill.getId());
				writeH(analogSkill.getLevel());
			}
		}
	}
}