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

		Quest q255 = QuestManager.getQuest(255);
		Quest q10755 = QuestManager.getQuest(10755);
		
		if(q255 != null)
			player.processQuestEvent(q255.getName(), _bypass, null);
		
		if(q10755 != null)
			player.processQuestEvent(q10755.getName(), _bypass, null);
	}
}