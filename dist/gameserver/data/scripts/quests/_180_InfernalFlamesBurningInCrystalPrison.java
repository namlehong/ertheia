package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _180_InfernalFlamesBurningInCrystalPrison extends Quest implements ScriptFile
{
	private static final int FIOREN = 33044;
	
	private static final int BAYLOR = 29213; // is this the new baylor???
	

	private static final int SIGN_OF_BAYLOR = 17589;

	public _180_InfernalFlamesBurningInCrystalPrison()
	{
		super(PARTY_ALL);

		addStartNpc(FIOREN);

		addTalkId(FIOREN);
		
		addKillId(BAYLOR);
		
		addQuestItem(SIGN_OF_BAYLOR);
		
		addLevelCheck(97);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33044-5.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
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
		int id = st.getState();

		if(id == COMPLETED)
			return "33044-comp.htm";

		if(st.getPlayer().getLevel() < 97)
			return "33044-lvl.htm";

		if(npcId == FIOREN)
		{
			if(cond == 0)
			{
				return "33044.htm";
			}
			if(cond == 1)
				return "33044-6.htm";
			if(cond == 2)
			{
				st.takeItems(SIGN_OF_BAYLOR, -1);
				st.addExpAndSp(14000000, 6400000);
				st.giveItems(22428, 1); //enchant armor R grade
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(false);			
				return "33044-7.htm";
			}	
		}
		return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() == 1 && st.getQuestItemsCount(SIGN_OF_BAYLOR) == 0)
		{
			st.giveItems(SIGN_OF_BAYLOR,1);
			st.setCond(2);
		}	
		return null;	
			
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
