package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.ExCuriousHouseResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestCuriousHouseRecord extends L2GameClientPacket
{
	private static final Logger _log = LoggerFactory.getLogger(RequestCuriousHouseRecord.class);

	protected void readImpl()
	{
		//
	}

	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		//activeChar.sendPacket(new ExCuriousHouseResult());

		_log.info("[IMPLEMENT ME!] RequestCuriousHouseRecord (maybe trigger)");
	}
}

