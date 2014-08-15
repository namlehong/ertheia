package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _10315_ToThePrisonOfDarkness extends Quest implements ScriptFile
{
	//npc
	private static final int SLAKI = 32893;
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

	public _10315_ToThePrisonOfDarkness()
	{
		super(false);
		addStartNpc(SLAKI);
		addTalkId(SLAKI);
		addTalkId(OPERA);

		addLevelCheck(90);
		//addQuestCompletedCheck(_10312_AbandonedGodsCreature.class); one of two is impossible for now =/
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("32893-5.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}	
		if(event.equalsIgnoreCase("32946-4.htm"))
		{
			st.addExpAndSp(4038093, 1708398);
			st.giveItems(57, 279513);
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
			return "32893-comp.htm";

		if(st.getPlayer().getLevel() < 90)
			return "32893-lvl.htm";		
		QuestState qs = st.getPlayer().getQuestState(_10311_PeacefulDaysAreOver.class);
		//QuestState qs2 = st.getPlayer().getQuestState(_10307_TheCorruptedLeaderHisTruth.class);
		if((qs == null || !qs.isCompleted())/* && (qs2 == null || !qs2.isCompleted())*/)
			return "32893-lvl.htm";	
			
		if(npcId == SLAKI)
		{
			if(cond == 0)
				return "32893.htm";
			else if(cond == 1)
				return "32893-6.htm";		
		}
		if(npcId == OPERA)
		{
			if(cond == 1)
				return "32946.htm";
		}
		return "noquest";
	}
}