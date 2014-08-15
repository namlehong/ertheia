package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.network.l2.s2c.ExAcquireAPSkillList;

/**
 * @author Bonux
**/
public class RequestChangeAbilityPoint extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		//
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

		if(activeChar.getAllowAbilitiesPoints() == Player.getMaxAbilitiesPoints())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_ACQUIRE_ANY_MORE_ABILITY_POINTS);
			return;
		}

		long requiredSP = activeChar.getAbilityPointSPCost();
		if(requiredSP == 0)
		{
			activeChar.sendPacket(SystemMsg.POINT_CONVERSION_HAS_FAILED_PLEASE_TRY_AGAIN);
			return;
		}

		if(requiredSP > activeChar.getSp())
		{
			activeChar.sendPacket(new SystemMessagePacket(SystemMsg.YOU_CANNOT_ACQUIRE_ANY_MORE_ABILITY_POINTS).addLong(requiredSP));
			return;
		}

		activeChar.addExpAndSp(0, -1 * requiredSP);
		activeChar.setAllowAbilitiesPoints(activeChar.getAllowAbilitiesPoints() + 1);

		//TODO: [Bonux] Проверить, должно ли тут быть какое-то сообщение?!?
		activeChar.sendPacket(new ExAcquireAPSkillList(activeChar));
	}
}