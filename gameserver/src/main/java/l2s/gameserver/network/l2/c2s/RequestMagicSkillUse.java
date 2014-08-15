package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.items.attachment.FlagItemAttachment;
import l2s.gameserver.tables.SkillTable;

public class RequestMagicSkillUse extends L2GameClientPacket
{
	private Integer _magicId;
	private boolean _ctrlPressed;
	private boolean _shiftPressed;

	/**
	 * packet type id 0x39
	 * format:		cddc
	 */
	@Override
	protected void readImpl()
	{
		_magicId = readD();
		_ctrlPressed = readD() != 0;
		_shiftPressed = readC() != 0;
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		activeChar.setActive();

		if(activeChar.isOutOfControl())
		{
			activeChar.sendActionFailed();
			return;
		}

		//[BONUX FIX THIS HARDCODING] Не работало сетактивкомбо вообще, удалил нахер ибо любой чар может скастовать комбо если оно возможно.
		Skill skill = null;
		if(isComboSkill(_magicId))
			skill = SkillTable.getInstance().getInfo(_magicId, 1);
		else	
			skill = SkillTable.getInstance().getInfo(_magicId, activeChar.getSkillLevel(_magicId));
			
		if(activeChar.getPendingLfcEnd())
		{
			if(skill != null && skill.isOffensive())
			{
				//activeChar.setMacroSkill(null);?
				activeChar.sendActionFailed();
				return;		
			}
		}				
		if(skill != null)
		{
			skill = skill.getElementalSkill(activeChar);
			if(!(skill.isActive() || skill.isToggle()))
			{
				activeChar.sendActionFailed();
				return;
			}

			FlagItemAttachment attachment = activeChar.getActiveWeaponFlagAttachment();
			if(attachment != null && !attachment.canCast(activeChar, skill))
			{
				activeChar.sendActionFailed();
				return;
			}

			// В режиме трансформации доступны только скилы трансформы
			if(activeChar.isTransformed() && !activeChar.getAllSkills().contains(skill))
			{
				activeChar.sendActionFailed();
				return;
			}

			if(skill.isToggle())
			{
				if(activeChar.getEffectList().containsEffects(skill))
				{
					if(skill.isSwitchable())
					{
						activeChar.getEffectList().stopEffects(skill.getId());
						activeChar.sendActionFailed();
					}
					return;
				}
			}

			Creature target = skill.getAimingTarget(activeChar, activeChar.getTarget());

			activeChar.setGroundSkillLoc(null);
			activeChar.getAI().Cast(skill, target, _ctrlPressed, _shiftPressed);
		}
		else
			activeChar.sendActionFailed();
	}

	private static final boolean isComboSkill(int magicId)
	{
		switch(magicId)
		{
			case 10500:
			case 10499:
			case 10749:
			case 10750:
			case 10250:
			case 10249:
			case 11000:
			case 10999:
			case 11249:
			case 11250:
			case 11500:
			case 11499:
			case 11750:
			case 11749:
			case 12000:
			case 11999:	
			case 15606:
				return true;
		}
		return false;
	}
}