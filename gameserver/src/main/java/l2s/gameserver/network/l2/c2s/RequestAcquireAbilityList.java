package l2s.gameserver.network.l2.c2s;

import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.data.xml.holder.SkillAcquireHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.base.AcquireType;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExAcquireAPSkillList;
import l2s.gameserver.tables.SkillTable;

/**
 * @author Bonux
**/
public class RequestAcquireAbilityList extends L2GameClientPacket
{
	private TIntIntMap _skills;

	@Override
	protected void readImpl()
	{
		readD(); // UNK == skills

		_skills = new TIntIntHashMap();

		int skillsCount = readD();
		for(int i = 0; i < skillsCount; i++) // Warrior Skills Learn
		{
			int skillId = readD();
			int skillLevel = readD();
			_skills.put(skillId, skillLevel);
		}

		skillsCount = readD();
		for(int i = 0; i < skillsCount; i++) // Berserk Skills Learn
		{
			int skillId = readD();
			int skillLevel = readD();
			_skills.put(skillId, skillLevel);
		}

		skillsCount = readD();
		for(int i = 0; i < skillsCount; i++) // Wizard Skills Learn
		{
			int skillId = readD();
			int skillLevel = readD();
			_skills.put(skillId, skillLevel);
		}
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(!activeChar.isAllowAbilities())
		{
			activeChar.sendPacket(SystemMsg.ABILITIES_CAN_BE_USED_BY_NOBLESSE_LV_99_OR_ABOVE);
			return;
		}

		int pointsCost = 0;

		List<Skill> learns = new ArrayList<Skill>();
		for(TIntIntIterator i = _skills.iterator(); i.hasNext();)
		{
			i.advance();

			int skillId = i.key();
			int skillLvl = i.value();

			Skill skill = SkillTable.getInstance().getInfo(skillId, skillLvl);
			if(skill == null)
				continue;

			if(!SkillAcquireHolder.getInstance().isSkillPossible(activeChar, skill, AcquireType.ABILITY))
				continue;

			int haveSkillLvl = activeChar.getSkillLevel(skillId);
			if(haveSkillLvl > 0)
			{
				if(haveSkillLvl >= skillLvl)
					continue;

				pointsCost += skillLvl - haveSkillLvl;
				learns.add(skill);
			}
			else
			{
				pointsCost += skillLvl;
				learns.add(skill);
			}
		}

		if(pointsCost > (activeChar.getAllowAbilitiesPoints() - activeChar.getUsedAbilitiesPoints()))
		{
			activeChar.sendPacket(SystemMsg.FAILED_TO_ACQUIRE_ABILITY_PLEASE_TRY_AGAIN);
			return;
		}

		if(!learns.isEmpty())
		{
			for(Skill skill : learns)
				activeChar.addSkill(skill, true);

			activeChar.sendPacket(SystemMsg.THE_SELECTED_ABILITY_WILL_BE_ACQUIRED);
		}
		activeChar.sendPacket(new ExAcquireAPSkillList(activeChar));
	}
}