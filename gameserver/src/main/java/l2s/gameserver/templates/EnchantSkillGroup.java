package l2s.gameserver.templates;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * @author Bonux
**/
public class EnchantSkillGroup
{
	public static class EnchantSkillLevel
	{
		private final int _level;
		private final int _adena;
		private final int _sp;
		private final int[] _chances;

		public EnchantSkillLevel(int level, int adena, int sp, int[] chances)
		{
			_level = level;
			_adena = adena;
			_sp = sp;
			_chances = chances;
		}

		public int getLevel()
		{
			return _level;
		}

		public int getAdena()
		{
			return _adena;
		}

		public int getSP()
		{
			return _sp;
		}

		public int[] getChances()
		{
			return _chances;
		}

		public int getChance(int playerLevel)
		{
			int index = playerLevel - 76;
			if(index < 0)
				return 0;

			return _chances[Math.min(index, (_chances.length - 1))];
		}
	}

	private final int _id;
	private final TIntObjectHashMap<EnchantSkillLevel> _levels;

	public EnchantSkillGroup(int id, TIntObjectHashMap<EnchantSkillLevel> levels)
	{
		_id = id;
		_levels = levels;
	}

	public int getId()
	{
		return _id;
	}

	public EnchantSkillLevel getEnchantLevel(int level)
	{
		return _levels.get(level);
	}
}