package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.instancemanager.QuestManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.quest.Quest;

public class RequestTutorialLinkHtml extends L2GameClientPacket
{
	// format: cdS
	private int _unk;
	private String _bypass;

	@Override
	protected void readImpl()
	{
		_unk = readD(); //maybe itemId?
		_bypass = readS();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null)
			return;

		Quest q = QuestManager.getQuest(255);
		if(q != null)
			player.processQuestEvent(q.getName(), _bypass, null);
	}
}