package l2s.gameserver.handler.usercommands.impl;

import l2s.gameserver.handler.usercommands.IUserCommandHandler;
import l2s.gameserver.instancemanager.MapRegionManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.templates.mapregion.RestartArea;

/**
 * Support for /loc command
 */
public class Loc implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS = { 0 };

	@Override
	public boolean useUserCommand(int id, Player activeChar)
	{
		if(COMMAND_IDS[0] != id)
			return false;

		if(activeChar.isChaosFestivalParticipant())
		{
			activeChar.sendPacket(new SystemMessagePacket(SystemMsg.CURRENT_LOCATION_S1_S2_S3_INSIDE_THE_CEREMONY_OF_CHAOS).addInteger(activeChar.getX()).addInteger(activeChar.getY()).addInteger(activeChar.getZ()));
			return true;
		}

		RestartArea ra = MapRegionManager.getInstance().getRegionData(RestartArea.class, activeChar);
		int msgId = ra != null ? ra.getRestartPoint().get(activeChar.getRace()).getMsgId() : 0;
		if(msgId > 0)
			activeChar.sendPacket(new SystemMessage(msgId).addNumber(activeChar.getX()).addNumber(activeChar.getY()).addNumber(activeChar.getZ()));
		else
			activeChar.sendPacket(new SystemMessage(SystemMessage.S1).addString("Current location : " + activeChar.getX() + ", " + activeChar.getY() + ", " + activeChar.getZ() + ", " + activeChar.getHeading()));

		return true;
	}

	@Override
	public final int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}