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
		Quest q10750 = QuestManager.getQuest(10750);
		Quest q10751 = QuestManager.getQuest(10751);
		Quest q10755 = QuestManager.getQuest(10755);
		Quest q10760 = QuestManager.getQuest(10760);
		
		Quest tempQuest = null;
		
		if(q255 != null)
			tempQuest = q255;
		if(q10750 != null)
			tempQuest = q10750;
		if(q10751 != null)
			tempQuest = q10751;
		if(q10755 != null)
			tempQuest = q10755;
		if(q10760 != null)
			tempQuest = q10760;
		
		if(tempQuest != null)
			player.processQuestEvent(tempQuest.getName(), _bypass, null);
		
	}
}