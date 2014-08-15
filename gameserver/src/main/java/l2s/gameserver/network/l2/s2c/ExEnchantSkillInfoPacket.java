package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.model.base.EnchantSkillLearn;
import l2s.gameserver.tables.SkillTreeTable;

public class ExEnchantSkillInfoPacket extends L2GameServerPacket
{
	private List<Integer> _routes;

	private int _id, _level, _canAdd, canDecrease;

	public ExEnchantSkillInfoPacket(int id, int level)
	{
		_routes = new ArrayList<Integer>();
		_id = id;
		_level = level;

		// skill already enchanted?
		if(_level > 100)
		{
			canDecrease = 1;
			// get detail for next level
			EnchantSkillLearn esd = SkillTreeTable.getSkillEnchant(_id, _level + 1);

			// if it exists add it
			if(esd != null)
			{
				addEnchantSkillDetail(esd.getLevel());
				_canAdd = 1;
			}

			for(EnchantSkillLearn el : SkillTreeTable.getEnchantsForChange(_id, _level))
				addEnchantSkillDetail(el.getLevel());
		}
		else
			// not already enchanted
			for(EnchantSkillLearn esd : SkillTreeTable.getFirstEnchantsForSkill(_id))
			{
				addEnchantSkillDetail(esd.getLevel());
				_canAdd = 1;
			}
	}

	public void addEnchantSkillDetail(int level)
	{
		_routes.add(level);
	}

	@Override
	protected void writeImpl()
	{
		writeD(_id);
		writeD(_level);
		writeD(_canAdd); // can add enchant
		writeD(canDecrease); // can decrease enchant

		writeD(_routes.size());
		for(Integer route : _routes)
			writeD(route);
	}
}