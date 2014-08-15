package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _149_PrimalMotherIstina extends Quest implements ScriptFile
{
	private static final int LIMIER = 33293;
	
	private static final int ISXINA_NORMAL = 29195;
	

	private static final int SIGN_OF_SHILEN = 17589;

	public _149_PrimalMotherIstina()
	{
		super(COMMAND_CHANNEL);

		addStartNpc(LIMIER);

		addTalkId(LIMIER);
		
		addKillId(ISXINA_NORMAL);
		
		addQuestItem(SIGN_OF_SHILEN);
		
		addLevelCheck(90);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33293-5.htm"))
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
			return "33293-comp.htm";

		if(st.getPlayer().getLevel() < 90)
			return "33293-lvl.htm";

		if(npcId == LIMIER)
		{
			if(cond == 0)
			{
				return "33293.htm";
			}
			if(cond == 1)
				return "33293-7.htm";
			if(cond == 2)
			{
				st.takeItems(SIGN_OF_SHILEN, -1);
				st.giveItems(19455, 1); // isxina bracelet GOD: harmony
				st.addExpAndSp(833065000, 368800464);
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(false);			
				return "33293-8.htm";
			}	
		}
		return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() == 1 && st.getQuestItemsCount(SIGN_OF_SHILEN) == 0)
		{
			st.giveItems(SIGN_OF_SHILEN,1);
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
