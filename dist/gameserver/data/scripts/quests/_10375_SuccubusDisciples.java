package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _10375_SuccubusDisciples extends Quest implements ScriptFile
{
	public static final String A_LIST = "a_list";
	public static final String B_LIST = "b_list";
	//npc
	private static final int ZANIJA = 32140;
	

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _10375_SuccubusDisciples()
	{
		super(false);
		addStartNpc(ZANIJA);
		addTalkId(ZANIJA);
		
		addClassLevelCheck(4);
		addLevelCheck(80);
		//addQuestCompletedCheck(_10310_CreationOfTwistedSpiral.class); missing one quest
		addKillNpcWithLog(1, A_LIST, 5, 23191);
		addKillNpcWithLog(1, B_LIST, 5, 23192);
		
		addKillNpcWithLog(3, A_LIST, 5, 23197);
		addKillNpcWithLog(3, B_LIST, 5, 23198);
		
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("32140-5.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}		

		if(event.equalsIgnoreCase("32140-8.htm"))
		{
			st.setCond(3);
			st.playSound(SOUND_MIDDLE);
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
			return "32140-comp.htm";

		if(st.getPlayer().getLevel() < 80)
			return "32140-lvl.htm";	
		if(!player.getClassId().isOfLevel(ClassLevel.THIRD))	
			return "32140-prof.htm";
		//QuestState qs = st.getPlayer().getQuestState(_10310_CreationOfTwistedSpiral.class); //missing quest
		//if(qs == null || !qs.isCompleted())
			//return "32140-lvl.htm";	
			
		if(npcId == ZANIJA)
		{
			if(cond == 0)
				return "32140.htm";
			else if(cond == 1)
				return "32140-6.htm";	
			else if(cond == 2)		
				return "32140-7.htm";
			else if(cond == 3)		
				return "32140-9.htm";
			else if(cond == 4)	
			{	
				st.addExpAndSp(24782300, 28102300);
				st.giveItems(57, 498700);
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(false);					
				return "32140-10.htm";		
			}		
		}
		return "noquest";
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(updateKill(npc, qs))
		{
			qs.unset(A_LIST);
			qs.unset(B_LIST);
			qs.setCond(qs.getCond() + 1);
		}

		return null;
	}	
}