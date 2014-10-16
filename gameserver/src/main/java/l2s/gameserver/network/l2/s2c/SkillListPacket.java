package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Skill.SkillType;
import l2s.gameserver.tables.SkillTreeTable;

/**
 * format   d (dddc)
			d  dddcc
 */
public class SkillListPacket extends L2GameServerPacket
{
	private List<Skill> _skills;
	private Player _player;
	private int _learnedSkillId;

	public SkillListPacket(Player player)
	{
		_skills = new ArrayList<Skill>(player.getAllSkills());
		_player = player;
		_learnedSkillId = 0;
	}

	public SkillListPacket(Player player, int learnedSkillId)
	{
		_skills = new ArrayList<Skill>(player.getAllSkills());
		_player = player;
		_learnedSkillId = learnedSkillId;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_skills.size());
		for(Skill temp : _skills)
		{
			if(temp.getSkillType() == SkillType.ALCHEMY)
				continue;
			
			writeD(temp.isActive() || temp.isToggle() ? 0 : 1); // deprecated? клиентом игнорируется
			if(temp.getChainIndex() != -1 && temp.getChainSkillId() != 0 && _player.getSkillChainDetails().containsKey(temp.getChainIndex()))
			{
				writeD(0x01);
				writeD(14612 + temp.getChainIndex());
			}
			else
			{
				writeD(temp.getDisplayLevel());
				writeD(temp.getDisplayId());
			}
			writeD(temp.getReuseSkillId());
			writeC(_player.isUnActiveSkill(temp.getId()) ? 0x01 : 0x00); // иконка скилла серая если не 0
			writeC(SkillTreeTable.isEnchantable(_player, temp)); // для заточки: если 1 скилл можно точить
		}
		writeD(_learnedSkillId);
	}
}