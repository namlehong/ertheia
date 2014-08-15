package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.EnchantSkillLearn;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExEnchantSkillInfoPacket;
import l2s.gameserver.network.l2.s2c.ExEnchantSkillResult;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.tables.SkillTreeTable;
import l2s.gameserver.utils.Log;

public final class RequestExEnchantSkillRouteChange extends L2GameClientPacket
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

		int slevel = activeChar.getSkillDisplayLevel(_skillId);
		if(slevel == -1)
			return;

		if(slevel <= sl.getBaseLevel() || slevel % 100 != _skillLvl % 100)
			return;

		int[] cost = sl.getCost();
		int requiredSp = cost[1] / SkillTreeTable.SAFE_ENCHANT_COST_MULTIPLIER;
		int requiredAdena = cost[0] / SkillTreeTable.SAFE_ENCHANT_COST_MULTIPLIER;

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

		Skill skill = SkillTable.getInstance().getInfo(_skillId, SkillTreeTable.convertEnchantLevel(sl.getBaseLevel(), _skillLvl, sl.getMaxLevel()));
		if(skill == null)
			return;

		int bookId = SkillTreeTable.CHANGE_ENCHANT_BOOK;
		if(skill.getEnchantGrade() == Skill.EnchantGrade.AWEKE)
			bookId = SkillTreeTable.AWAKE_CHANGE_ENCHANT_BOOK;

		if(Functions.getItemCount(activeChar, bookId) == 0)
		{
			activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ALL_OF_THE_ITEMS_NEEDED_TO_ENCHANT_THAT_SKILL);
			return;
		}

		Functions.removeItem(activeChar, bookId, 1);
		Functions.removeItem(activeChar, 57, requiredAdena);
		activeChar.addExpAndSp(0, -1 * requiredSp);

		activeChar.addSkill(skill, true);

		SystemMessage sm = new SystemMessage(SystemMessage.Enchant_skill_route_change_was_successful_Lv_of_enchant_skill_S1_will_remain);
		sm.addSkillName(_skillId, _skillLvl);
		activeChar.sendPacket(sm);

		Log.add(activeChar.getName() + "|Successfully changed route|" + _skillId + "|" + slevel + "|to+" + _skillLvl, "enchant_skills");

		activeChar.sendPacket(new ExEnchantSkillInfoPacket(_skillId, activeChar.getSkillDisplayLevel(_skillId)), new ExEnchantSkillResult(1));
		RequestExEnchantSkill.updateSkillShortcuts(activeChar, _skillId, _skillLvl);
	}
}