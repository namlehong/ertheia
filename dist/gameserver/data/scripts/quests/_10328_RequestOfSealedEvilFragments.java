package quests;

import l2s.gameserver.model.Player;
//import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;
import quests._10327_BookOfGiants;

public class _10328_RequestOfSealedEvilFragments extends Quest implements ScriptFile
{
	private static final int PANTEON = 32972;
	private static final int KEKIY = 30565;

	public _10328_RequestOfSealedEvilFragments()
	{
		super(false);
		addStartNpc(PANTEON);
		addTalkId(KEKIY);
		addRaceCheck(true, true, true, true, true, true, false);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		//Player player = st.getPlayer();
		//Reflection r = player.getReflection();
		if(event.equalsIgnoreCase("4.htm"))
		{
			st.set("cond", "1", true);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		} 
		else if(event.equalsIgnoreCase("7.htm"))
		{
			st.giveItems(57, 20000);
			st.addExpAndSp(13000, 4000);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		Player player = st.getPlayer();
		if(npcId == PANTEON)
		{
			if(cond == 0)
			{
				if(checkStartCondition(player))
					htmltext = "1.htm";
				//else TODO
			}
			else if(cond == 1)
				htmltext = "4.htm";
		}
		else if(npcId == KEKIY)
		{
			if(cond == 1) 
				htmltext = "5.htm";
		}
		return htmltext;
	}

	@Override
	public boolean checkStartCondition(Player player)
	{
		QuestState qs = player.getQuestState(_10327_BookOfGiants.class);
		return player.getLevel() <= 20 && qs != null && qs.getState() == COMPLETED;
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
