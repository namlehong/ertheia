package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _10350_MotherOfMonstrosities extends Quest implements ScriptFile
{
	private static final int CHICHENEC = 30539;
	private static final int TRASKEN_COURPSE = 29232;
	
	private static final int TRASKEN_PART = 17734;
	

	public _10350_MotherOfMonstrosities()
	{
		super(false);

		addStartNpc(CHICHENEC);

		addTalkId(CHICHENEC);
		addTalkId(TRASKEN_COURPSE);
		addQuestItem(TRASKEN_PART);
		
		addLevelCheck(40, 75);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30539-6.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);			
		}
		else if(event.equalsIgnoreCase("29232-1.htm"))
		{
			st.setCond(2);
			st.giveItems(TRASKEN_PART, 1);	
			st.playSound(SOUND_MIDDLE);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		int id = st.getState();

		if(id == COMPLETED)
			return "30539-comp.htm";

		if(st.getPlayer().getLevel() < 40 || st.getPlayer().getLevel() > 75)
			return "30539-lvl.htm";

		if(npcId == CHICHENEC)
		{
			if(cond == 0)
			{
				return "30539.htm";
			}
			if(cond == 1)
				return "30539-7.htm";
			if(cond == 2)
			{
				st.takeItems(TRASKEN_PART, -1);
				st.addExpAndSp(200454, 135933);
				st.giveItems(57, 40299);
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(false);	
				return "30539-8.htm";
			}	
		}
		else if(npcId == TRASKEN_COURPSE)	
		{
			if(cond == 1)	
				return "29232.htm";
			else if(cond == 2)
				return "29232-2.htm";
				
		}
		return htmltext;
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
