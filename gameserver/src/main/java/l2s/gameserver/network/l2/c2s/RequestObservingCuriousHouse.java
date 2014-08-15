package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.ExCuriousHouseObserveMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestObservingCuriousHouse extends L2GameClientPacket
{
	private static final Logger _log = LoggerFactory.getLogger(RequestObservingCuriousHouse.class);
	
	private int _houseID;

	protected void readImpl()
	{
		_houseID = readD();
	}

	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		//if(!ChaosFestival.getInstance().isFightingNow(activeChar)) Добавить вторую часть проверки, 
		{
			activeChar.enterObserverMode(activeChar.getLoc());
			activeChar.sendPacket(new ExCuriousHouseObserveMode(true));
		}

		_log.info("[IMPLEMENT ME!] RequestObservingListCuriousHouse D[" + _houseID + "]");
	}
}

