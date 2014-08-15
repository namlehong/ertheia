package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.ExCuriousHouseLeave;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestLeaveCuriousHouse extends L2GameClientPacket
{
	private static final Logger _log = LoggerFactory.getLogger(RequestLeaveCuriousHouse.class);

	protected void readImpl()
	{
		//
	}

	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		activeChar.sendPacket(new ExCuriousHouseLeave());
		//ChaosFestival.getInstance().exitChallenge(activeChar);

		_log.info("[IMPLEMENT ME!] RequestLeaveCuriousHouse (maybe trigger)");
	}
}
