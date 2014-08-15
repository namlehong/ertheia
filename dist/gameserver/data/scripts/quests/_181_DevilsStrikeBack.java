package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _181_DevilsStrikeBack extends Quest implements ScriptFile 
{
	private static final int Fioren = 33044; //ok
	private static final int Balok = 29218; //ok
	private static final int BELCONTRACT = 17592; //ok
	private static final int REWARD = 19450;

	public _181_DevilsStrikeBack() 
	{
		super(COMMAND_CHANNEL);
		addStartNpc(Fioren);
		addKillId(Balok);
		addQuestItem(BELCONTRACT);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("1.htm")) 
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) 
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == Fioren) 
		{
			if(cond == 0) 
			{
				if(st.getPlayer().getLevel() < 97)
					htmltext = "2.htm";
				else if(st.isNowAvailable())
					htmltext = "11.htm";
				else
					htmltext = "4.htm";
			}
			else if(cond == 1) 
			{
					htmltext = "6.htm";
			}	
			else if(cond == 2) 
			{
					htmltext = "6.htm";
					st.takeAllItems(BELCONTRACT);
					st.giveItems(57, 37128000);
					st.addExpAndSp(414855000, 886750000);
					st.playSound(SOUND_FINISH);
					st.exitCurrentQuest(this);
			}
			else
					htmltext = "7.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st) 
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		if(cond == 1 && npcId == Balok) 
		{
			st.setCond(2);
			st.giveItems(BELCONTRACT, 1);
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