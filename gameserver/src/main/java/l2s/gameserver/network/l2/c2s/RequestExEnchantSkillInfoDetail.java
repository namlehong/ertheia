package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.EnchantSkillLearn;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExEnchantSkillInfoDetailPacket;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.tables.SkillTreeTable;

public final class RequestExEnchantSkillInfoDetail extends L2GameClientPacket
{
	private static final int TYPE_NORMAL_ENCHANT = 0;
	private static final int TYPE_SAFE_ENCHANT = 1;
	private static final int TYPE_UNTRAIN_ENCHANT = 2;
	private static final int TYPE_CHANGE_ENCHANT = 3;
	private static final int TYPE_IMMORTAL_ENCHANT = 4;

	private int _type;
	private int _skillId;
	private int _skillLvl;

	@Override
	protected void readImpl()
	{
		_type = readD();
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

		int bookId = 0;
		int bookCount = 1;
		int sp = 0;
		int adenaCount = 0;
		double costMult = SkillTreeTable.NORMAL_ENCHANT_COST_MULTIPLIER;

		EnchantSkillLearn esd = SkillTreeTable.getSkillEnchant(_skillId, _skillLvl);
		if(esd == null)
			return;

		int enchantLevel = SkillTreeTable.convertEnchantLevel(esd.getBaseLevel(), _skillLvl, esd.getMaxLevel());

		Skill skill = SkillTable.getInstance().getInfo(_skillId, enchantLevel);
		if(skill == null)
		{
			activeChar.sendMessage("Internal error: not found skill level");
			return;
		}

		if(skill.getEnchantGrade() != Skill.EnchantGrade.AWEKE)
		{
			switch(_type)
			{
				case TYPE_NORMAL_ENCHANT:
					if(_skillLvl % 100 != 1)
						bookCount = 0;
					bookId = SkillTreeTable.NORMAL_ENCHANT_BOOK;
					break;
				case TYPE_SAFE_ENCHANT:
					bookId = SkillTreeTable.SAFE_ENCHANT_BOOK;
					costMult = SkillTreeTable.SAFE_ENCHANT_COST_MULTIPLIER;
					break;
				case TYPE_UNTRAIN_ENCHANT:
					bookId = SkillTreeTable.UNTRAIN_ENCHANT_BOOK;
					esd = SkillTreeTable.getSkillEnchant(_skillId, _skillLvl + 1);
					break;
				case TYPE_CHANGE_ENCHANT:
					bookId = SkillTreeTable.CHANGE_ENCHANT_BOOK;
					esd = SkillTreeTable.getEnchantsForChange(_skillId, _skillLvl).get(0);
					costMult = 1f / SkillTreeTable.SAFE_ENCHANT_COST_MULTIPLIER;
					break;
				case TYPE_IMMORTAL_ENCHANT:
					bookId = SkillTreeTable.IMMORTAL_ENCHANT_SCROLL;
					costMult = 0;
					break;
			}
		}
		else
		{
			switch(_type)
			{
				case TYPE_NORMAL_ENCHANT:
					if(_skillLvl % 100 != 1)
						bookCount = 0;
					bookId = SkillTreeTable.AWAKE_ENCHANT_BOOK;
					break;
				case TYPE_SAFE_ENCHANT:
					bookId = SkillTreeTable.AWAKE_SAFE_ENCHANT_BOOK;
					costMult = SkillTreeTable.SAFE_ENCHANT_COST_MULTIPLIER;
					break;
				case TYPE_UNTRAIN_ENCHANT:
					bookId = SkillTreeTable.UNTRAIN_AWAKE_ENCHANT_BOOK;
					esd = SkillTreeTable.getSkillEnchant(_skillId, _skillLvl + 1);
					break;
				case TYPE_CHANGE_ENCHANT:
					bookId = SkillTreeTable.AWAKE_CHANGE_ENCHANT_BOOK;
					esd = SkillTreeTable.getEnchantsForChange(_skillId, _skillLvl).get(0);
					costMult = 1f / SkillTreeTable.SAFE_ENCHANT_COST_MULTIPLIER;
					break;
				case TYPE_IMMORTAL_ENCHANT:
					bookId = SkillTreeTable.IMMORTAL_ENCHANT_SCROLL;
					costMult = 0;
					break;
			}
		}

		if(esd == null)
			return;

		int[] cost = esd.getCost();

		sp = (int) (cost[1] * costMult);

		if(_type != TYPE_UNTRAIN_ENCHANT)
			adenaCount = (int) (cost[0] * costMult);

		// send skill enchantment detail
		activeChar.sendPacket(new ExEnchantSkillInfoDetailPacket(_skillId, _skillLvl, sp, esd.getRate(activeChar), bookId, bookCount, adenaCount));
	}
}