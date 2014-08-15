package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _10312_AbandonedGodsCreature extends Quest implements ScriptFile
{
	public static final String A_LIST = "a_list";
	//npc
	private static final int GOFINA = 33031;
	

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _10312_AbandonedGodsCreature()
	{
		super(true);
		addStartNpc(GOFINA);
		addTalkId(GOFINA);

		addLevelCheck(90);
		addQuestCompletedCheck(_10310_CreationOfTwistedSpiral.class);
		addKillNpcWithLog(1, A_LIST, 1, 25866);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("33031-5.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}		
		if(event.startsWith("give"))
		{
			st.addExpAndSp(46847289, 20739487);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);				
			if(event.equalsIgnoreCase("givegiants"))
			{
				st.giveItems(19305, 1);
				st.giveItems(19306, 1);
				st.giveItems(19307, 1);
				st.giveItems(19308, 1);
				return "33031-9.htm";
			}
			if(event.equalsIgnoreCase("givescrolls"))
			{
				st.giveItems(17527, 2);
				return "33031-10.htm";
			}			
			if(event.equalsIgnoreCase("givesacks"))
			{
				st.giveItems(34861, 2);
				return "33031-11.htm";
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
			return "33031-comp.htm";

		if(st.getPlayer().getLevel() < 90)
			return "33031-lvl.htm";		
		QuestState qs = st.getPlayer().getQuestState(_10310_CreationOfTwistedSpiral.class);
		if(qs == null || !qs.isCompleted())
			return "33031-lvl.htm";	
			
		if(npcId == GOFINA)
		{
			if(cond == 0)
				return "33031.htm";
			else if(cond == 1)
				return "33031-6.htm";	
			else if(cond == 2)		
				return "33031-8.htm";
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