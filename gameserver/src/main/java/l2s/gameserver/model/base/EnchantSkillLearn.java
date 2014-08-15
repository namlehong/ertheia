package l2s.gameserver.model.base;

import l2s.gameserver.data.xml.holder.EnchantSkillGroupHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.templates.EnchantSkillGroup;
import l2s.gameserver.templates.EnchantSkillGroup.EnchantSkillLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EnchantSkillLearn
{
	private static final Logger _log = LoggerFactory.getLogger(EnchantSkillLearn.class);

	// these two build the primary key
	private final int _id;
	private final int _level;

	// not needed, just for easier debug
	private final String _name;
	private final String _type;

	private final int _baseLvl;
	private final int _maxLvl;
	private final int _minSkillLevel;

	private final int _group;
	private EnchantSkillLevel _enchantInfo = null;

	public EnchantSkillLearn(int id, int lvl, String name, String type, int minSkillLvl, int baseLvl, int maxLvl, int group)
	{
		_id = id;
		_level = lvl;
		_baseLvl = baseLvl;
		_maxLvl = maxLvl;
		_minSkillLevel = minSkillLvl;
		_name = name.intern();
		_type = type.intern();
		_group = group;
	}

	/**
	 * @return Returns the id.
	 */
	public int getId()
	{
		return _id;
	}

	/**
	 * @return Returns the level.
	 */
	public int getLevel()
	{
		return _level;
	}

	/**
	 * @return Returns the minLevel.
	 */
	public int getBaseLevel()
	{
		return _baseLvl;
	}

	/**
	 * @return Returns the minSkillLevel.
	 */
	public int getMinSkillLevel()
	{
		return _minSkillLevel;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName()
	{
		return _name;
	}

	/**
	 * @return Returns the spCost.
	 */
	public int[] getCost()
	{
		int adena = getEnchantInfo().getAdena();
		int sp = getEnchantInfo().getSP();

		return new int[] { adena, sp };
	}

	/**
	 * Шанс успешной заточки
	 */
	public int getRate(Player ply)
	{
		return getEnchantInfo().getChance(ply.getLevel());
	}

	public int getMaxLevel()
	{
		return _maxLvl;
	}

	public String getType()
	{
		return _type;
	}

	private EnchantSkillLevel getEnchantInfo()
	{
		if(_enchantInfo == null)
		{
			EnchantSkillGroup group = EnchantSkillGroupHolder.getInstance().getGroup(_group);
			if(group == null)
			{
				_log.warn("Not found enchant skill group id: " + _group + "!");
				return null;
			}

			int enchantLevel = _level % 100;
			_enchantInfo = group.getEnchantLevel(enchantLevel);
			if(_enchantInfo == null)
			{
				_log.warn("Not found enchant skill level info for group id: " + _group + ", enchant level: " + enchantLevel + "!");
				return null;
			}
		}
		return _enchantInfo;
	}

	@Override
	public int hashCode()
	{
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + _id;
		result = PRIME * result + _level;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		if(!(obj instanceof EnchantSkillLearn))
			return false;
		EnchantSkillLearn other = (EnchantSkillLearn) obj;
		return getId() == other.getId() && getLevel() == other.getLevel();
	}
}