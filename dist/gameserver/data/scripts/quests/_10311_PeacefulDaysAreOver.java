package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _10311_PeacefulDaysAreOver extends Quest implements ScriptFile
{
	//npc
	private static final int SELINA = 33032;
	private static final int SLAKI = 32893;
	

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _10311_PeacefulDaysAreOver()
	{
		super(false);
		addStartNpc(SELINA);
		addTalkId(SELINA);
		addTalkId(SLAKI);

		addLevelCheck(90);
		addQuestCompletedCheck(_10312_AbandonedGodsCreature.class);
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
		if(event.equalsIgnoreCase("32893-4.htm"))
		{
			st.addExpAndSp(7168395, 3140085);
			st.giveItems(57, 489220);
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
		QuestState qs = st.getPlayer().getQuestState(_10312_AbandonedGodsCreature.class);
		if(qs == null || !qs.isCompleted())
			return "33032-lvl.htm";	
			
		if(npcId == SELINA)
		{
			if(cond == 0)
				return "33032.htm";
			else if(cond == 1)
				return "33032-6.htm";		
		}
		if(npcId == SLAKI)
		{
			if(cond == 1)
				return "32893.htm";
		}
		return "noquest";
	}
}