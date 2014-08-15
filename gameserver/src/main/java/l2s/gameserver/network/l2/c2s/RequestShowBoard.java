package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.Config;
import l2s.gameserver.handler.bbs.CommunityBoardManager;
import l2s.gameserver.handler.bbs.ICommunityBoardHandler;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;

public class RequestShowBoard extends L2GameClientPacket
{
	@SuppressWarnings("unused")
	private int _unknown;

	/**
	 * packet type id 0x5E
	 *
	 * sample
	 *
	 * 5E
	 * 01 00 00 00
	 *
	 * format:		cd
	 */
	@Override
	public void readImpl()
	{
		_unknown = readD();
	}

	@Override
	public void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(Config.COMMUNITYBOARD_ENABLED)
		{
			ICommunityBoardHandler handler = CommunityBoardManager.getInstance().getCommunityHandler(Config.BBS_DEFAULT);
			if(handler != null)
				handler.onBypassCommand(activeChar, Config.BBS_DEFAULT);
		}
		else
			activeChar.sendPacket(new SystemMessagePacket(SystemMsg.THE_COMMUNITY_SERVER_IS_CURRENTLY_OFFLINE));
	}
}
