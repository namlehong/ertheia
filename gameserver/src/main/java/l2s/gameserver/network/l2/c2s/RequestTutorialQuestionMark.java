package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.instancemanager.QuestManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.quest.Quest;

public class RequestTutorialQuestionMark extends L2GameClientPacket
{
	// format: cd
	private int _number = 0;

	@Override
	protected void readImpl()
	{
		_number = readD();
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
		Quest q10769 = QuestManager.getQuest(10769);
		Quest q10774 = QuestManager.getQuest(10774);
		Quest q10779 = QuestManager.getQuest(10779);
		Quest q10785 = QuestManager.getQuest(10785);
		
		
		if(q255 != null)
			player.processQuestEvent(q255.getName(), "QM" + _number, null);
		if(q10750 != null)
			player.processQuestEvent(q10751.getName(), "QM" + _number, null);
		if(q10751 != null)
			player.processQuestEvent(q10751.getName(), "QM" + _number, null);
		if(q10755 != null)
			player.processQuestEvent(q10755.getName(), "QM" + _number, null);
		if(q10760 != null)
			player.processQuestEvent(q10760.getName(), "QM" + _number, null);
		if(q10769 != null)
			player.processQuestEvent(q10769.getName(), "QM" + _number, null);
		if(q10774 != null)
			player.processQuestEvent(q10774.getName(), "QM" + _number, null);
		if(q10779 != null)
			player.processQuestEvent(q10779.getName(), "QM" + _number, null);
		if(q10785 != null)
			player.processQuestEvent(q10785.getName(), "QM" + _number, null);
		
	}
}