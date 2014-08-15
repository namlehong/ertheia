package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.SystemMsg;

/**
 * @author Bonux
**/
public class RequestDivideAdenaStart extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		//
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null)
			return;

		//TODO: [Bonux] Реализовать.
		if(player.getAdena() <= 0)
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_PROCEED_AS_THERE_IS_INSUFFICIENT_ADENA);
			return;
		}

		player.sendPacket(SystemMsg.YOU_CANNOT_PROCEED_AS_YOU_ARE_NOT_IN_AN_ALLIANCE_OR_PARTY);
	}
}