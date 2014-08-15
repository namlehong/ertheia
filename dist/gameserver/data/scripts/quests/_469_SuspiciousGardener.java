package quests;


import org.apache.commons.lang3.ArrayUtils;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _469_SuspiciousGardener extends Quest implements ScriptFile
{
	//npc
	public static final int GOFINA = 33031;
	
	public static final String A_LIST = "a_list";

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _469_SuspiciousGardener()
	{
		super(true);
		addStartNpc(GOFINA);
		addTalkId(GOFINA);
		
		addKillNpcWithLog(1, A_LIST, 30, 22964);

		addLevelCheck(90);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("33031-3.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		if(event.equalsIgnoreCase("33031-6.htm"))
		{
			st.unset("cond");
			st.giveItems(30385, 6);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(this);
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
		if(npcId == GOFINA)
		{
			if(state == 1)
			{
				if(player.getLevel() < 90)
					return "33031-lvl.htm";
				if(!st.isNowAvailable())
					return "33031-comp.htm";
				return "33031.htm";
			}
			if(state == 2)
			{
				if(cond == 1)
					return "33031-4.htm";
					
				if(cond == 2)
					return "33031-5.htm";			
			}
		}
		return "noquest";
	}
	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		boolean doneKill = updateKill(npc, st);
		if(doneKill)
		{
			st.unset(A_LIST);
			st.setCond(2);
		}
		return null;
	}	
}