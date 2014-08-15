package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.GameClient;
import l2s.gameserver.network.l2.s2c.ExPledgeRecruitApplyInfo;

/**
 * @author Bonux
**/
public class RequestPledgeRecruitApplyInfo extends L2GameClientPacket
{
	protected void readImpl() throws Exception
	{
		//
	}

	protected void runImpl() throws Exception
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		activeChar.sendPacket(new ExPledgeRecruitApplyInfo(1));
	}
}