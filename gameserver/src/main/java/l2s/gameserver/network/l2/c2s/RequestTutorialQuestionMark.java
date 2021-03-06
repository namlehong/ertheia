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
		Quest q10752 = QuestManager.getQuest(10752);
		Quest q10753 = QuestManager.getQuest(10753);
		Quest q10755 = QuestManager.getQuest(10755);
		Quest q10760 = QuestManager.getQuest(10760);
		Quest q10769 = QuestManager.getQuest(10769);
		Quest q10774 = QuestManager.getQuest(10774);
		Quest q10779 = QuestManager.getQuest(10779);
		Quest q10782 = QuestManager.getQuest(10782);
		Quest q10785 = QuestManager.getQuest(10785);
		Quest q10390 = QuestManager.getQuest(10390);
        Quest q10393 = QuestManager.getQuest(10393);
        Quest q10397 = QuestManager.getQuest(10397);
        Quest q10401 = QuestManager.getQuest(10401);
        Quest q10404 = QuestManager.getQuest(10404);
        Quest q10408 = QuestManager.getQuest(10408);
		
		if(q10750 != null && _number == 10750)
			player.processQuestEvent(q10751.getName(), "QM" + _number, null);
		if(q10752 != null && _number == 10752)
			player.processQuestEvent(q10752.getName(), "QM" + _number, null);
		if(q10753 != null && _number == 10753)
			player.processQuestEvent(q10753.getName(), "QM" + _number, null);
		else if(q10751 != null && _number == 10751)
			player.processQuestEvent(q10751.getName(), "QM" + _number, null);
		else if(q10755 != null && _number == 10755)
			player.processQuestEvent(q10755.getName(), "QM" + _number, null);
		else if(q10760 != null && _number == 10760)
			player.processQuestEvent(q10760.getName(), "QM" + _number, null);
		else if(q10769 != null && _number == 10769)
			player.processQuestEvent(q10769.getName(), "QM" + _number, null);
		else if(q10774 != null && _number == 10774)
			player.processQuestEvent(q10774.getName(), "QM" + _number, null);
		else if(q10779 != null && _number == 10779)
			player.processQuestEvent(q10779.getName(), "QM" + _number, null);
		else if(q10782 != null && _number == 10782)
			player.processQuestEvent(q10782.getName(), "QM" + _number, null);
		else if(q10785 != null && _number == 10785)
			player.processQuestEvent(q10785.getName(), "QM" + _number, null);

        else if(q10390 != null && _number == 10390)
        {
            player.processQuestEvent(q10390.getName(), "QM" + _number, null);
        }
        else if(q10393 != null && _number == 10393)
        {
            player.processQuestEvent(q10393.getName(), "QM" + _number, null);
        }
        else if(q10397 != null && _number == 10397)
        {
            player.processQuestEvent(q10397.getName(), "QM" + _number, null);
        }
        else if(q10401 != null && _number == 10401)
        {
            player.processQuestEvent(q10401.getName(), "QM" + _number, null);
        }
        else if(q10404 != null && _number == 10404)
        {
            player.processQuestEvent(q10404.getName(), "QM" + _number, null);
        }
        else if(q10408 != null && _number == 10408)
        {
            player.processQuestEvent(q10408.getName(), "QM" + _number, null);
        }
        
		else if(q255 != null)
			player.processQuestEvent(q255.getName(), "QM" + _number, null);
		
	}
}