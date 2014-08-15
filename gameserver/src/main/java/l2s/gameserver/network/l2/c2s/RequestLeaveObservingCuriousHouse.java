package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.ExCuriousHouseObserveMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestLeaveObservingCuriousHouse extends L2GameClientPacket
{
	private static final Logger _log = LoggerFactory.getLogger(RequestLeaveObservingCuriousHouse.class);

	protected void readImpl()
	{
		//
	}

	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		
		//if(activeChar.getObserverController().isObserving())
		{
			activeChar.leaveObserverMode();
			activeChar.sendPacket(new ExCuriousHouseObserveMode(false));
		}

		_log.info("[IMPLEMENT ME!] RequestLeaveObservingCuriousHouse (maybe trigger)");
	}
}
