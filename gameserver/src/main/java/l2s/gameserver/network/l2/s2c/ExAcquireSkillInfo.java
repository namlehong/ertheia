package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.SkillLearn;
import l2s.gameserver.tables.SkillTable;

public class ExAcquireSkillInfo extends L2GameServerPacket
{
	private Skill _skill;
	private SkillLearn _learn;
	private Skill[] _analogSkills;

	public ExAcquireSkillInfo(Player player, SkillLearn learn)
	{
		_learn = learn;
		_skill = SkillTable.getInstance().getInfo(_learn.getId(), _learn.getLevel());
		if(_skill == null)
			return;

		if(_skill.haveAnalogSkills())
			_analogSkills = _skill.getAnalogSkills(player);
		else
			_analogSkills = new Skill[0];
	}

	@Override
	public void writeImpl()
	{
		writeD(_learn.getId());
		writeD(_learn.getLevel());
		writeQ(_learn.getCost());
		writeH(_learn.getMinLevel());
		writeH(_learn.getDualClassMinLvl()); // Dual-class min level.
		boolean haveItem = _learn.getItemId() > 0;
		writeD(haveItem ? 1 : 0); //getRequiredItems()
		if(haveItem)
		{
			writeD(_learn.getItemId());
			writeQ(_learn.getItemCount());
		}

		writeD(_analogSkills.length);
		for(Skill analogSkill : _analogSkills)
		{
			writeD(analogSkill.getId());
			writeD(analogSkill.getLevel());
		}
	}
}