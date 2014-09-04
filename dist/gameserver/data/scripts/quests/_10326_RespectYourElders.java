package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _10326_RespectYourElders extends Quest implements ScriptFile
{
	private static final int GALLINT = 32980;
	private static final int PANTEON = 32972;

	public _10326_RespectYourElders()
	{
		super(false);
		addStartNpc(GALLINT);
		addTalkId(PANTEON);
		addLevelCheck(1, 20);
		addQuestCompletedCheck(_10325_SearchingForNewPower.class);
		addRaceCheck(true, true, true, true, true, true, false);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(event.equalsIgnoreCase("3.htm"))
		{
			st.set("cond", "1", true);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("5.htm"))
		{
			st.giveItems(57, 140);
			st.addExpAndSp(6700, 5);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		Player player = st.getPlayer();
		if(npcId == GALLINT)
		{
			if(cond == 0)
			{
				if(checkStartCondition(player))
					htmltext = "1.htm";
				//else TODO
			}
			else if(cond >= 8)
			{
				htmltext = "3.htm";
				st.giveItems(57, 12000);
				if(player.isMageClass())
					st.giveItems(2509, 1000);
				else
					st.giveItems(1835, 1000);
				st.addExpAndSp(3254, 2400);
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(false);
			}
		}
		else if(npcId == PANTEON)
		{
			if(cond == 1) 
				htmltext = "4.htm";
		}
		return htmltext;
	}

	@Override
	public void onLoad()
	{
		//
	}

	@Override
	public void onReload()
	{
		//
	}

	@Override
	public void onShutdown()
	{
		//
	}
}
