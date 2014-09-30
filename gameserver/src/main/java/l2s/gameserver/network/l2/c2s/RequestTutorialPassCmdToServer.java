package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.instancemanager.QuestManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.quest.Quest;

public class RequestTutorialPassCmdToServer extends L2GameClientPacket
{
	// format: cS

	String _bypass = null;

	@Override
	protected void readImpl()
	{
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
		if(q255 != null)
			player.processQuestEvent(q255.getName(), _bypass, null);
		
		if(_bypass.split("_").length < 2) return;
		
		if(q10750 != null && _bypass.split("_")[1].equalsIgnoreCase("10750"))
			player.processQuestEvent(q10750.getName(), _bypass, null);
		
		if(q10751 != null && _bypass.split("_")[1].equalsIgnoreCase("10751"))
			player.processQuestEvent(q10751.getName(), _bypass, null);

		if(q10752 != null && _bypass.split("_")[1].equalsIgnoreCase("10752"))
			player.processQuestEvent(q10752.getName(), _bypass, null);

		if(q10753 != null && _bypass.split("_")[1].equalsIgnoreCase("10753"))
			player.processQuestEvent(q10753.getName(), _bypass, null);
		
		if(q10755 != null && _bypass.split("_")[1].equalsIgnoreCase("10755"))
			player.processQuestEvent(q10755.getName(), _bypass, null);
		
		if(q10760 != null && _bypass.split("_")[1].equalsIgnoreCase("10760"))
			player.processQuestEvent(q10760.getName(), _bypass, null);
		
		if(q10769 != null && _bypass.split("_")[1].equalsIgnoreCase("10769"))
			player.processQuestEvent(q10769.getName(), _bypass, null);
		
		if(q10774 != null && _bypass.split("_")[1].equalsIgnoreCase("10774"))
			player.processQuestEvent(q10774.getName(), _bypass, null);
		
		if(q10779 != null && _bypass.split("_")[1].equalsIgnoreCase("10779"))
			player.processQuestEvent(q10779.getName(), _bypass, null);
		
		if(q10782 != null && _bypass.split("_")[1].equalsIgnoreCase("10782"))
			player.processQuestEvent(q10782.getName(), _bypass, null);
		
		if(q10785 != null && _bypass.split("_")[1].equalsIgnoreCase("10785"))
			player.processQuestEvent(q10785.getName(), _bypass, null);

        if(q10390 != null && _bypass.split("_")[1].equalsIgnoreCase("10390"))
        {
            player.processQuestEvent(q10390.getName(), _bypass, null);
        }
        if(q10393 != null && _bypass.split("_")[1].equalsIgnoreCase("10393"))
        {
            player.processQuestEvent(q10393.getName(), _bypass, null);
        }
        if(q10397 != null && _bypass.split("_")[1].equalsIgnoreCase("10397"))
        {
            player.processQuestEvent(q10397.getName(), _bypass, null);
        }
        if(q10401 != null && _bypass.split("_")[1].equalsIgnoreCase("10401"))
        {
            player.processQuestEvent(q10401.getName(), _bypass, null);
        }
        if(q10404 != null && _bypass.split("_")[1].equalsIgnoreCase("10404"))
        {
            player.processQuestEvent(q10404.getName(), _bypass, null);
        }
        if(q10408 != null && _bypass.split("_")[1].equalsIgnoreCase("10408"))
        {
            player.processQuestEvent(q10408.getName(), _bypass, null);
        }
	}
}