package l2s.gameserver.network.l2.c2s;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.EnchantSkillLearn;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExEnchantSkillInfoPacket;
import l2s.gameserver.network.l2.s2c.ExEnchantSkillResult;
import l2s.gameserver.network.l2.s2c.SkillListPacket;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.tables.SkillTreeTable;
import l2s.gameserver.utils.Log;

/**
 * Format (ch) dd
 */
public final class RequestExEnchantSkillSafe extends L2GameClientPacket
{
	private int _skillId;
	private int _skillLvl;

	@Override
	protected void readImpl()
	{
		_skillId = readD();
		_skillLvl = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.isTransformed() || activeChar.isMounted() || activeChar.isInCombat())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_USE_THE_SKILL_ENHANCING_FUNCTION_IN_THIS_CLASS_YOU_CAN_USE_THE_SKILL_ENHANCING_FUNCTION_UNDER_OFFBATTLE_STATUS_AND_CANNOT_USE_THE_FUNCTION_WHILE_TRANSFORMING_BATTLING_AND_ONBOARD);
			return;
		}

		if(activeChar.getLevel() < 76)
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_USE_THE_SKILL_ENHANCING_FUNCTION_ON_THIS_LEVEL_YOU_CAN_USE_THE_CORRESPONDING_FUNCTION_ON_LEVELS_HIGHER_THAN_76LV_);
			return;
		}

		if(!activeChar.getClassId().isOfLevel(ClassLevel.THIRD) && !activeChar.getClassId().isOfLevel(ClassLevel.AWAKED))
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_USE_THE_SKILL_ENHANCING_FUNCTION_IN_THIS_CLASS_YOU_CAN_USE_CORRESPONDING_FUNCTION_WHEN_COMPLETING_THE_THIRD_CLASS_CHANGE);
			return;
		}

		Skill oldSkill = activeChar.getKnownSkill(_skillId);
		if(oldSkill != null && activeChar.isSkillDisabled(oldSkill))
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_USE_THE_SKILL_ENHANCING_FUNCTION_IN_THIS_CLASS_YOU_CAN_USE_THE_SKILL_ENHANCING_FUNCTION_UNDER_OFFBATTLE_STATUS_AND_CANNOT_USE_THE_FUNCTION_WHILE_TRANSFORMING_BATTLING_AND_ONBOARD);
			return;
		}

		EnchantSkillLearn sl = SkillTreeTable.getSkillEnchant(_skillId, _skillLvl);

		if(sl == null)
			return;

		int slevel = activeChar.getSkillLevel(_skillId);
		if(slevel == -1)
			return;

		int enchantLevel = SkillTreeTable.convertEnchantLevel(sl.getBaseLevel(), _skillLvl, sl.getMaxLevel());

		// already knows the skill with this level
		if(slevel >= enchantLevel)
			return;

		// Можем ли мы перейти с текущего уровня скилла на данную заточку
		if(slevel == sl.getBaseLevel() ? _skillLvl % 100 != 1 : slevel != enchantLevel - 1)
		{
			activeChar.sendMessage("Incorrect enchant level.");
			return;
		}

		Skill skill = SkillTable.getInstance().getInfo(_skillId, enchantLevel);
		if(skill == null)
			return;

		int[] cost = sl.getCost();
		int requiredSp = cost[1] * SkillTreeTable.SAFE_ENCHANT_COST_MULTIPLIER;
		int requiredAdena = cost[0] * SkillTreeTable.SAFE_ENCHANT_COST_MULTIPLIER;

		int rate = sl.getRate(activeChar);

		if(activeChar.getSp() < requiredSp)
		{
			activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SP_TO_ENCHANT_THAT_SKILL);
			return;
		}

		if(activeChar.getAdena() < requiredAdena)
		{
			activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}

		int bookId = SkillTreeTable.SAFE_ENCHANT_BOOK;
		if(skill.getEnchantGrade() == Skill.EnchantGrade.AWEKE)
			bookId = SkillTreeTable.AWAKE_SAFE_ENCHANT_BOOK;

		if(Functions.getItemCount(activeChar, bookId) == 0)
		{
			activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ALL_OF_THE_ITEMS_NEEDED_TO_ENCHANT_THAT_SKILL);
			return;
		}

		Functions.removeItem(activeChar, bookId, 1);

		if(Rnd.chance(rate))
		{
			activeChar.addSkill(skill, true);
			activeChar.addExpAndSp(0, -1 * requiredSp);
			Functions.removeItem(activeChar, 57, requiredAdena);
			activeChar.sendPacket(new SystemMessage(SystemMessage.SP_HAS_DECREASED_BY_S1).addNumber(requiredSp), new SystemMessage(SystemMessage.SUCCEEDED_IN_ENCHANTING_SKILL_S1).addSkillName(_skillId, _skillLvl), new ExEnchantSkillResult(1));
			activeChar.sendPacket(new SkillListPacket(activeChar));
			RequestExEnchantSkill.updateSkillShortcuts(activeChar, _skillId, _skillLvl);
			Log.add(activeChar.getName() + "|Successfully safe enchanted|" + _skillId + "|to+" + _skillLvl + "|" + rate, "enchant_skills");
		}
		else
		{
			activeChar.sendPacket(new SystemMessage(SystemMessage.Skill_enchant_failed_Current_level_of_enchant_skill_S1_will_remain_unchanged).addSkillName(_skillId, _skillLvl), new ExEnchantSkillResult(0));
			Log.add(activeChar.getName() + "|Failed to safe enchant|" + _skillId + "|to+" + _skillLvl + "|" + rate, "enchant_skills");
		}

		activeChar.sendPacket(new ExEnchantSkillInfoPacket(_skillId, activeChar.getSkillDisplayLevel(_skillId)));
	}
}