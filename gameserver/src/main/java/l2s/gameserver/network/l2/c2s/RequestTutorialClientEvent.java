package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.instancemanager.QuestManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.quest.Quest;

public class RequestTutorialClientEvent extends L2GameClientPacket
{
	// format: cd
	public int _event = 0;

	/**
	 * Пакет от клиента, если вы в туториале подергали мышкой как надо - клиент пришлет его со значением 1 ну или нужным ивентом
	 */
	@Override
	protected void readImpl()
	{
		_event = readD();
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

		if(q255 != null)
			player.processQuestEvent(q255.getName(), "CE" + _event, null);
		if(q10750 != null)
			player.processQuestEvent(q10750.getName(), "CE" + _event, null);
		if(q10751 != null)
			player.processQuestEvent(q10751.getName(), "CE" + _event, null);
		if(q10755 != null)
			player.processQuestEvent(q10755.getName(), "CE" + _event, null);
		if(q10760 != null)
			player.processQuestEvent(q10760.getName(), "CE" + _event, null);
        if(q10390 != null)
        {
            player.processQuestEvent(q10390.getName(), "CE" + _event, null);
        }
	}
}