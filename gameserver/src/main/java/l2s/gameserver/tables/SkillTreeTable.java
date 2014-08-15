package l2s.gameserver.tables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import l2s.gameserver.data.xml.holder.SkillAcquireHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.SkillLearn;
import l2s.gameserver.model.base.AcquireType;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.EnchantSkillLearn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkillTreeTable
{
	public static final int NORMAL_ENCHANT_COST_MULTIPLIER = 1;
	public static final int SAFE_ENCHANT_COST_MULTIPLIER = 5;
	public static final int NORMAL_ENCHANT_BOOK = 6622;
	public static final int SAFE_ENCHANT_BOOK = 9627;
	public static final int CHANGE_ENCHANT_BOOK = 9626;
	public static final int UNTRAIN_ENCHANT_BOOK = 9625;
	public static final int AWAKE_ENCHANT_BOOK = 30297;
	public static final int AWAKE_SAFE_ENCHANT_BOOK = 30298;
	public static final int AWAKE_CHANGE_ENCHANT_BOOK = 30299;
	public static final int UNTRAIN_AWAKE_ENCHANT_BOOK = 30300;
	public static final int IMMORTAL_ENCHANT_SCROLL = 37044;

	private static final Logger _log = LoggerFactory.getLogger(SkillTreeTable.class);

	private static SkillTreeTable _instance;

	public static Map<Integer, List<EnchantSkillLearn>> _enchant = new ConcurrentHashMap<Integer, List<EnchantSkillLearn>>();

	public static SkillTreeTable getInstance()
	{
		if(_instance == null)
			_instance = new SkillTreeTable();
		return _instance;
	}

	private SkillTreeTable()
	{
		_log.info("SkillTreeTable: Loaded " + _enchant.size() + " enchanted skills.");
	}

	public static boolean checkSkill(Player player, Skill skill)
	{
		SkillLearn learn = SkillAcquireHolder.getInstance().getSkillLearn(player, skill.getId(), levelWithoutEnchant(skill), AcquireType.NORMAL);
		if(learn == null)
			return false;

		boolean update = false;

		int lvlDiff = learn.isFreeAutoGet() ? 1 : 4;
		if(learn.getMinLevel() >= (player.getLevel() + lvlDiff) || learn.getDualClassMinLvl() >= (player.getDualClassLevel() + lvlDiff))
		{
			player.removeSkill(skill, true);

			// если у нас низкий лвл для скила, то заточка обнуляется 100%
			// и ищем от большего к меньшему подходящий лвл для скила
			for(int i = skill.getBaseLevel(); i != 0; i--)
			{
				SkillLearn learn2 = SkillAcquireHolder.getInstance().getSkillLearn(player, skill.getId(), i, AcquireType.NORMAL);
				if(learn2 == null)
					continue;

				int lvlDiff2 = learn2.isFreeAutoGet() ? 1 : 4;
				if(learn2.getMinLevel() >= (player.getLevel() + lvlDiff2) || learn2.getDualClassMinLvl() >= (player.getDualClassLevel() + lvlDiff2))
					continue;

				Skill newSkill = SkillTable.getInstance().getInfo(skill.getId(), i);
				if(newSkill != null)
				{
					player.addSkill(newSkill, true);
					break;
				}
			}
			update = true;
		}

		if(player.isTransformed())
		{
			learn = player.getTransform().getAdditionalSkill(skill.getId(), skill.getLevel());
			if(learn == null)
				return false;

			if(learn.getMinLevel() >= player.getLevel() + 1)
			{
				player.removeTransformSkill(skill);
				player.removeSkill(skill, false);

				for(int i = skill.getBaseLevel(); i != 0; i--)
				{
					SkillLearn learn2 = player.getTransform().getAdditionalSkill(skill.getId(), i);
					if(learn2 == null)
						continue;

					if(learn2.getMinLevel() >= player.getLevel() + 1)
						continue;

					Skill newSkill = SkillTable.getInstance().getInfo(skill.getId(), i);
					if(newSkill != null)
					{
						player.addTransformSkill(newSkill);
						player.addSkill(newSkill, false);
						break;
					}
				}
				update = true;
			}
		}
		return update;
	}

	private static int levelWithoutEnchant(Skill skill)
	{
		return skill.getDisplayLevel() > 100 ? skill.getBaseLevel() : skill.getLevel();
	}

	public static List<EnchantSkillLearn> getFirstEnchantsForSkill(int skillid)
	{
		List<EnchantSkillLearn> result = new ArrayList<EnchantSkillLearn>();

		List<EnchantSkillLearn> enchants = _enchant.get(skillid);
		if(enchants == null)
			return result;

		for(EnchantSkillLearn e : enchants)
			if(e.getLevel() % 100 == 1)
				result.add(e);

		return result;
	}

	public static boolean isEnchantable(Player player, Skill skill)
	{
		if(skill == null)
			return false;

		if(player == null)
			return false;

		if(player.isTransformed())
			return false;

		List<EnchantSkillLearn> enchants = _enchant.get(skill.getId());
		if(enchants == null)
			return false;

		for(EnchantSkillLearn e : enchants)
		{
			if(e.getBaseLevel() <= skill.getLevel())
				return true;
		}

		return false;
	}

	public static List<EnchantSkillLearn> getEnchantsForChange(int skillid, int level)
	{
		List<EnchantSkillLearn> result = new ArrayList<EnchantSkillLearn>();

		List<EnchantSkillLearn> enchants = _enchant.get(skillid);
		if(enchants == null)
			return result;

		for(EnchantSkillLearn e : enchants)
			if(e.getLevel() % 100 == level % 100)
				result.add(e);

		return result;
	}

	public static EnchantSkillLearn getSkillEnchant(int skillid, int level)
	{
		List<EnchantSkillLearn> enchants = _enchant.get(skillid);
		if(enchants == null)
			return null;

		for(EnchantSkillLearn e : enchants)
			if(e.getLevel() == level)
				return e;
		return null;
	}

	/**
	 * Преобразует уровень скила из клиентского представления в серверное
	 * @param baseLevel базовый уровень скила - максимально возможный без заточки
	 * @param level - текущий уровень скила
	 * @param enchantlevels
	 * @return уровень скила
	 */
	public static int convertEnchantLevel(int baseLevel, int level, int enchantlevels)
	{
		if(level < 100)
			return level;
		return baseLevel + ((level - level % 100) / 100 - 1) * enchantlevels + level % 100;
	}

	public static void unload()
	{
		if(_instance != null)
			_instance = null;

		_enchant.clear();
	}
}