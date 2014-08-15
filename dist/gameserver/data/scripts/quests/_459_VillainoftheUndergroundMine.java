package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _459_VillainoftheUndergroundMine extends Quest implements ScriptFile 
{
	private static final int Filaur = 30535; //ok
	private static final int Teredor = 25785; //ok
	private static final int POF = 19450;

	public _459_VillainoftheUndergroundMine() 
	{
		super(PARTY_ALL);
		addStartNpc(Filaur);
		addTalkId(Filaur);
		addKillId(Teredor);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		int cond = st.getCond();
		if(event.equalsIgnoreCase("30535-04.htm")) 
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		if(event.equalsIgnoreCase("30535-07.htm")) 
		{
			if(cond == 2) 
			{
				st.giveItems(POF, 6);
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(this);
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) 
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == Filaur) 
		{
			if(cond == 0) 
			{
				if(st.getPlayer().getLevel() < 85)
					htmltext = "nolvl.htm";
				else if(st.isNowAvailable())
					htmltext = "30535-00.htm";
				else
					htmltext = "notnow.htm";
			}
			else if(cond == 1) 
				htmltext = "30535-04.htm";
			else if(cond == 2) 
			{
				htmltext = "30535-06.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st) 
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		if(cond == 1 && npcId == Teredor) 
		{
			st.setCond(2);
		}
		return null;
	}

	@Override
	public void onLoad() 
	{
		// null
	}

	@Override
	public void onReload()
	{
		// null
	}

	@Override
	public void onShutdown()
	{
		// null
	}
}