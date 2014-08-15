package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.ExCuriousHouseEnter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestJoinCuriousHouse extends L2GameClientPacket
{
	private static final Logger _log = LoggerFactory.getLogger(RequestJoinCuriousHouse.class);

	protected void readImpl()
	{
		//
	}

	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		//if(ChaosFestival.getInstance().canParticipate(activeChar) && ChaosFestival.getInstance().getStatus() == ChaosFestival.ChaosFestivalStatus.INVITING)
		{
			//ChaosFestival.getInstance().addMember(activeChar);
			activeChar.sendPacket(new ExCuriousHouseEnter());
		}

		_log.info("[IMPLEMENT ME!] RequestJoinCuriousHouse (maybe trigger)");
	}
}