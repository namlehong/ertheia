package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _493_KickingOutUnwelcomeGuests extends Quest implements ScriptFile
{
	//npc
	public static final int JORJINO = 33515;
	
	public static final String A_LIST = "a_list";
	public static final String B_LIST = "b_list";
	public static final String C_LIST = "c_list";
	public static final String D_LIST = "d_list";
	public static final String E_LIST = "e_list";

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _493_KickingOutUnwelcomeGuests()
	{
		super(true);
		addStartNpc(JORJINO);
		addTalkId(JORJINO);
		
		addKillNpcWithLog(1, A_LIST, 20, 23147);
		addKillNpcWithLog(1, B_LIST, 20, 23148);
		addKillNpcWithLog(1, C_LIST, 20, 23149);
		addKillNpcWithLog(1, D_LIST, 20, 23150);
		addKillNpcWithLog(1, E_LIST, 20, 23151);

		addLevelCheck(95);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("33515-4.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		if(event.equalsIgnoreCase("33515-6.htm"))
		{
			st.unset("cond");
			st.addExpAndSp(560000000, 134400);
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
		if(npcId == JORJINO)
		{
			if(state == 1)
			{
				if(player.getLevel() < 95)
					return "noquest";
				if(!st.isNowAvailable())
					return "noquest";
				return "33515.htm";
			}
			if(state == 2)
			{			
				if(cond == 2)
					return "33515-5.htm";			
			}
		}
		return "noquest";
	}
	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond != 1)
			return null;
		boolean doneKill = updateKill(npc, st);
		if(doneKill)
		{
			st.unset(A_LIST);
			st.unset(B_LIST);
			st.unset(C_LIST);
			st.unset(D_LIST);
			st.unset(E_LIST);
			st.setCond(2);
		}
		return null;
	}	
}