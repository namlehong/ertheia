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
		Quest q10755 = QuestManager.getQuest(10755);
		Quest q10760 = QuestManager.getQuest(10760);
        Quest q10390 = QuestManager.getQuest(10390);
        Quest q10393 = QuestManager.getQuest(10393);
        Quest q10397 = QuestManager.getQuest(10397);

		if(q255 != null)
			player.processQuestEvent(q255.getName(), _bypass, null);
		if(q10750 != null)
			player.processQuestEvent(q10750.getName(), _bypass, null);
		if(q10751 != null)
			player.processQuestEvent(q10751.getName(), _bypass, null);
		if(q10755 != null)
			player.processQuestEvent(q10755.getName(), _bypass, null);
		if(q10760 != null)
			player.processQuestEvent(q10760.getName(), _bypass, null);
        if(q10390 != null)
        {
            player.processQuestEvent(q10390.getName(), _bypass, null);
        }
        if (q10393 != null)
        {
            player.processQuestEvent(q10393.getName(), _bypass, null);
        }
        if(q10397 != null)
        {
            player.processQuestEvent(q10397.getName(), "CM" + _bypass, null);
        }
	}
}