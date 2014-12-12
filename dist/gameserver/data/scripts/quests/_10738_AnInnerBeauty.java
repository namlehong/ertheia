package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.components.UsmVideo;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author Hien Son
 */
public class _10738_AnInnerBeauty  extends Quest implements ScriptFile
{
	//NPC's
	private static final int GRAKON = 33947;
	private static final int EVNA = 33935;
	private static final int GRAKON_NOTE = 39521;
	
	public _10738_AnInnerBeauty ()
	{
		super(false);
		addStartNpc(GRAKON);
		addTalkId(EVNA);
		
		addLevelCheck(5, 20);
		addRaceCheck(false, false, false, false, false, false, true);
		
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33947-4.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			st.giveItems(GRAKON_NOTE, 1);
		}
		
		else if(event.equalsIgnoreCase("33935-3.htm"))
		{
			st.takeItems(GRAKON_NOTE, 1);
			
			st.giveItems(ADENA_ID, 12000, true);
			
			st.addExpAndSp(2625,1);
			
			st.setState(COMPLETED);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == GRAKON)
		{
			if(cond == 0)
			{
				if(checkStartCondition(st.getPlayer()))
					htmltext = "33947-1.htm";
				else
					htmltext = "noquest";
			}
			else if(cond == 1)
				htmltext = "33947-5.htm";
		}
		else if(npcId == EVNA && st.getCond() == 1)
		{
			htmltext = "33935-1.htm";
		}
		return htmltext;
	}

	@Override
	public boolean checkStartCondition(Player player)
	{
		QuestState qs = player.getQuestState(_10737_GarkonsWarehouse.class);
		
		boolean result = false;
		
		result = (qs != null && qs.getState() == COMPLETED);
	
		return (player.getLevel() >= 5 && player.getLevel() <= 20 && result);
	}

	@Override
	public void onLoad()
	{
	}

	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{
	}
}