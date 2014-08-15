package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author GodWorld & Bonux
**/
public class _10382_DayofLiberation extends Quest implements ScriptFile 
{
	// NPC'S
	private static final int SIZRAK = 33669;
	private static final int TAUTI = 29236;

	// Item's
	private static final int TAUTIS_BRACELET = 35293;

	public _10382_DayofLiberation()
	{
		super(true);
		addStartNpc(SIZRAK);
		addTalkId(SIZRAK);
		addKillNpcWithLog(1, "TAUTI", 1, TAUTI);
		addLevelCheck(97);
		addQuestCompletedCheck(_10381_TotheSeedofHellfire.class);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("sofa_sizraku_q10382_03.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("sofa_sizraku_q10382_10.htm"))
		{
			st.addExpAndSp(951127800, 435041400);
			st.giveItems(ADENA_ID, 3256740, true);
			st.giveItems(TAUTIS_BRACELET, 1);
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
		if(npcId == SIZRAK)
		{
			if(st.isCompleted())
				htmltext = "sofa_sizraku_q10382_06.htm";
			else if(st.isStarted())
			{
				if(cond == 1)
					htmltext = "sofa_sizraku_q10382_07.htm";
				else if(cond == 2)
					htmltext = "sofa_sizraku_q10382_08.htm";
			}
			else
			{
				Player player = st.getPlayer();
				QuestState pst = player.getQuestState(_10381_TotheSeedofHellfire.class);
				if(player.getLevel() < 97)
					htmltext = "sofa_sizraku_q10382_04.htm";
				else if(pst == null || !pst.isCompleted())
					htmltext = "sofa_sizraku_q10382_05.htm";
				else
					htmltext = "sofa_sizraku_q10382_01.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st) 
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == TAUTI)
		{
			if(cond == 1)
			{
				st.setCond(2);
				st.playSound(SOUND_MIDDLE);
			}
		}
		return null;
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