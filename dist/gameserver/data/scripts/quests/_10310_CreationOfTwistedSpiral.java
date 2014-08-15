package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _10310_CreationOfTwistedSpiral extends Quest implements ScriptFile
{
	public static final String A_LIST = "a_list";
	public static final String B_LIST = "b_list";
	public static final String C_LIST = "c_list";
	public static final String D_LIST = "d_list";
	public static final String E_LIST = "e_list";
	//npc
	private static final int SELINA = 33032;
	private static final int GORFINA = 33031;
	

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _10310_CreationOfTwistedSpiral()
	{
		super(false);
		addStartNpc(SELINA);
		addTalkId(SELINA);
		addTalkId(GORFINA);

		addLevelCheck(90);
		addQuestCompletedCheck(_10302_UnsettlingShadowAndRumors.class);
		addKillNpcWithLog(2, A_LIST, 10, 22947);
		addKillNpcWithLog(2, B_LIST, 10, 22948);
		addKillNpcWithLog(2, C_LIST, 10, 22949);
		addKillNpcWithLog(2, D_LIST, 10, 22950);
		addKillNpcWithLog(2, E_LIST, 10, 22951);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("33032-5.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}	
		else if(event.equalsIgnoreCase("33031-2.htm"))
		{
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("33031-5.htm"))
		{
			st.addExpAndSp(50178765, 21980595);
			st.giveItems(57, 3424540);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);	
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
			return "33032-comp.htm";

		if(st.getPlayer().getLevel() < 90)
			return "33032-lvl.htm";		
		QuestState qs = st.getPlayer().getQuestState(_10302_UnsettlingShadowAndRumors.class);
		if(qs == null || !qs.isCompleted())
			return "33032-lvl.htm";	
			
		if(npcId == SELINA)
		{
			if(cond == 0)
				return "33032.htm";
			else
				return "33032-6.htm";
		}
		else if(npcId == GORFINA)
		{
			if(cond == 1)
				return "33031.htm";
			if(cond == 2)
				return "33031-3.htm";
			if(cond == 3)
				return "33031-4.htm";
		}
		return "noquest";
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() != 2)
			return null;

		if(updateKill(npc, qs))
		{
			qs.unset(A_LIST);
			qs.unset(B_LIST);
			qs.unset(C_LIST);
			qs.unset(D_LIST);
			qs.unset(E_LIST);
			qs.setCond(3);
		}

		return null;
	}	
}