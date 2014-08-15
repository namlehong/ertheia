package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.ListMenteeWaiting;

/**
 * Created with IntelliJ IDEA. User: Darvin Date: 02.07.12 Time: 0:48 Приходит при нажатии наставником кнопки "+" в окне учеников Ответом на пакет
 * является {@link l2s.gameserver.network.l2.s2c.ListMenteeWaiting}
 */
public class RequestMenteeWaitingList extends L2GameClientPacket
{
	private int maxLevel;
	private int minLevel;
	private int page;
	@Override
	protected void runImpl() throws Exception
	{
		Player activeChar = getClient().getActiveChar();

		if(activeChar == null)
		{
			return;
		}
		activeChar.sendPacket(new ListMenteeWaiting(activeChar, this.page, this.minLevel, this.maxLevel));
	}

	@Override
	protected void readImpl() throws Exception
	{
		this.page = readD();
		this.minLevel = readD();
		this.maxLevel = readD();
	}
}
