package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.quest.dynamic.DynamicQuestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestDynamicQuestProgressInfo extends L2GameClientPacket
{
	private static final Logger _log = LoggerFactory.getLogger(RequestDynamicQuestProgressInfo.class);
	private int id;
	private int step;

	@Override
	protected void readImpl()
	{
		id = readD();
		step = readD();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();

		if(player == null)
		{
			return;

		}

		DynamicQuestController.getInstance().requestQuestProgressInfo(id, step, player);
	}
}