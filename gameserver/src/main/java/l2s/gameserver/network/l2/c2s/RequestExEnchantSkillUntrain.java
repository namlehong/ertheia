package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.EnchantSkillLearn;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExEnchantSkillInfoPacket;
import l2s.gameserver.network.l2.s2c.ExEnchantSkillResult;
import l2s.gameserver.network.l2.s2c.SkillListPacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.tables.SkillTreeTable;
import l2s.gameserver.utils.Log;

public final class RequestExEnchantSkillUntrain extends L2GameClientPacket
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

		int oldSkillLevel = activeChar.getSkillDisplayLevel(_skillId);
		if(oldSkillLevel == -1)
			return;

		if(_skillLvl != (oldSkillLevel - 1) || (_skillLvl / 100) != (oldSkillLevel / 100))
			return;

		EnchantSkillLearn sl = SkillTreeTable.getSkillEnchant(_skillId, oldSkillLevel);
		if(sl == null)
			return;

		Skill newSkill;

		if(_skillLvl % 100 == 0)
		{
			_skillLvl = sl.getBaseLevel();
			newSkill = SkillTable.getInstance().getInfo(_skillId, _skillLvl);
		}
		else
			newSkill = SkillTable.getInstance().getInfo(_skillId, SkillTreeTable.convertEnchantLevel(sl.getBaseLevel(), _skillLvl, sl.getMaxLevel()));

		if(newSkill == null)
			return;

		int bookId = SkillTreeTable.UNTRAIN_ENCHANT_BOOK;
		if(newSkill.getEnchantGrade() == Skill.EnchantGrade.AWEKE)
			bookId = SkillTreeTable.UNTRAIN_AWAKE_ENCHANT_BOOK;

		if(Functions.getItemCount(activeChar, bookId) == 0)
		{
			activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ALL_OF_THE_ITEMS_NEEDED_TO_ENCHANT_THAT_SKILL);
			return;
		}

		Functions.removeItem(activeChar, bookId, 1);

		activeChar.addExpAndSp(0, sl.getCost()[1]);
		activeChar.addSkill(newSkill, true);

		if(_skillLvl > 100)
		{
			SystemMessagePacket sm = new SystemMessagePacket(SystemMsg.UNTRAIN_OF_ENCHANT_SKILL_WAS_SUCCESSFUL_CURRENT_LEVEL_OF_ENCHANT_SKILL_S1_HAS_BEEN_DECREASED_BY_1);
			sm.addSkillName(_skillId, _skillLvl);
			activeChar.sendPacket(sm);
		}
		else
		{
			SystemMessagePacket sm = new SystemMessagePacket(SystemMsg.UNTRAIN_OF_ENCHANT_SKILL_WAS_SUCCESSFUL_CURRENT_LEVEL_OF_ENCHANT_SKILL_S1_BECAME_0_AND_ENCHANT_SKILL_WILL_BE_INITIALIZED);
			sm.addSkillName(_skillId, _skillLvl);
			activeChar.sendPacket(sm);
		}

		Log.add(activeChar.getName() + "|Successfully untranes|" + _skillId + "|to+" + _skillLvl + "|---", "enchant_skills");

		activeChar.sendPacket(new ExEnchantSkillInfoPacket(_skillId, newSkill.getDisplayLevel()), ExEnchantSkillResult.SUCCESS, new SkillListPacket(activeChar));
		RequestExEnchantSkill.updateSkillShortcuts(activeChar, _skillId, _skillLvl);
	}
}