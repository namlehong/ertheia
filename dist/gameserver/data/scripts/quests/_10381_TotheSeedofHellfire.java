package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author GodWorld & Bonux
**/
public class _10381_TotheSeedofHellfire extends Quest implements ScriptFile 
{
	// NPC'S
	private static final int KEUCEREUS = 32548;
	private static final int KBALDIR = 32733;
	private static final int SIZRAK = 33669;

	// Item's
	private static final int KBALDIRS_LETTER = 34957;

	public _10381_TotheSeedofHellfire()
	{
		super(false);
		addStartNpc(KEUCEREUS);
		addTalkId(KEUCEREUS, KBALDIR, SIZRAK);
		addQuestItem(KBALDIRS_LETTER);
		addLevelCheck(97);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("kserth_q10381_03.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("kbarldire_q10381_03.htm"))
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
			st.giveItems(KBALDIRS_LETTER, 1);
		}
		else if(event.equalsIgnoreCase("sofa_sizraku_q10381_03.htm"))
		{
			st.addExpAndSp(951127800, 435041400);
			st.giveItems(ADENA_ID, 3256740, true);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) 
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		String htmltext = "noquest";
		if(npcId == KEUCEREUS)
		{
			if(st.isCompleted())
				htmltext = "kserth_q10381_05.htm";
			else if(st.isStarted())
				htmltext = "kserth_q10381_06.htm";
			else
			{
				if(checkStartCondition(st.getPlayer()))
					htmltext = "kserth_q10381_01.htm";
				else
					htmltext = "kserth_q10381_04.htm";
			}
		}
		else if(npcId == KBALDIR)
		{
			if(cond == 1)
				htmltext = "kbarldire_q10381_01.htm";
			else if(cond == 2)
				htmltext = "kbarldire_q10381_04.htm";
		}
		else if(npcId == SIZRAK)
		{
			if(cond == 2)
				htmltext = "sofa_sizraku_q10381_01.htm";
		}
		return htmltext;
	}

	@Override
	public void onLoad()
	{
		//
	}

	@Override
	public void onReload()
	{
		//
	}

	@Override
	public void onShutdown()
	{
		//
	}
}