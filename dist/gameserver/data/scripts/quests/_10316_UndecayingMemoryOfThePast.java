package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _10316_UndecayingMemoryOfThePast extends Quest implements ScriptFile
{
	public static final String A_LIST = "a_list";
	//npc
	private static final int OPERA = 32946;
	

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _10316_UndecayingMemoryOfThePast()
	{
		super(true);
		addStartNpc(OPERA);
		addTalkId(OPERA);

		addLevelCheck(90);
		addQuestCompletedCheck(_10315_ToThePrisonOfDarkness.class);
		addKillNpcWithLog(1, A_LIST, 1, 25779);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("32946-5.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}		
		if(event.startsWith("give"))
		{
			st.addExpAndSp(54093924, 23947602);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);				
			if(event.equalsIgnoreCase("givegiants"))
			{
				st.giveItems(19305, 1);
				st.giveItems(19306, 1);
				st.giveItems(19307, 1);
				st.giveItems(19308, 1);
				return "32946-9.htm";
			}
			if(event.equalsIgnoreCase("givescrolls"))
			{
				st.giveItems(17527, 2);
				return "32946-10.htm";
			}			
			if(event.equalsIgnoreCase("givesacks"))
			{
				st.giveItems(34861, 2);
				return "32946-11.htm";
			}				
		}	
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		Player player = st.getPlayer();
		int npcId = npc.getNpcId();
		int state = st.getState();
		int cond = st.getCond();
		
		if(state == COMPLETED)
			return "32946-comp.htm";

		if(st.getPlayer().getLevel() < 90)
			return "32946-lvl.htm";		
		QuestState qs = st.getPlayer().getQuestState(_10315_ToThePrisonOfDarkness.class);
		if(qs == null || !qs.isCompleted())
			return "32946-lvl.htm";	
			
		if(npcId == OPERA)
		{
			if(cond == 0)
				return "32946.htm";
			else if(cond == 1)
				return "32946-6.htm";	
			else if(cond == 2)		
				return "32946-7.htm";
		}
		return "noquest";
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() != 1)
			return null;

		if(updateKill(npc, qs))
		{
			qs.unset(A_LIST);
			qs.setCond(2);
		}

		return null;
	}	
}