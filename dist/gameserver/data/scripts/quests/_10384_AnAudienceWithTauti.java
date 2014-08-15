package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author GodWorld & Bonux
**/
public class _10384_AnAudienceWithTauti extends Quest implements ScriptFile 
{
	// NPC'S
	private static final int FERGASON = 33681;
	private static final int AKU = 33671;

	// Monster's
	private static final int TAUTI = 29237;

	// Item's
	private static final int TAUTIS_FRAGMENT = 34960;
	private static final int BOTTLE_OF_TAUTIS_SOUL = 35295;

	public _10384_AnAudienceWithTauti()
	{
		super(true);
		addStartNpc(FERGASON);
		addTalkId(FERGASON, AKU);
		addKillId(TAUTI);
		addQuestItem(TAUTIS_FRAGMENT);
		addLevelCheck(97);
		addQuestCompletedCheck(_10383_FergasonsOffer.class);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("maestro_ferguson_q10384_04.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("sofa_aku_q10384_02.htm"))
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("maestro_ferguson_q10384_11.htm"))
		{
			st.addExpAndSp(951127800, 435041400);
			st.giveItems(ADENA_ID, 3256740, true);
			st.giveItems(BOTTLE_OF_TAUTIS_SOUL, 1);
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
		if(npcId == FERGASON)
		{
			if(st.isCompleted())
				htmltext = "maestro_ferguson_q10384_07.htm";
			else if(st.isStarted())
			{
				if(cond == 1 || cond == 2)
					htmltext = "maestro_ferguson_q10384_08.htm";
				else if(cond == 3 && st.haveQuestItem(TAUTIS_FRAGMENT))
					htmltext = "maestro_ferguson_q10384_09.htm";
			}
			else
			{
				Player player = st.getPlayer();
				QuestState pst = player.getQuestState(_10383_FergasonsOffer.class);
				if(player.getLevel() < 97)
					htmltext = "maestro_ferguson_q10384_05.htm";
				else if(pst == null || !pst.isCompleted())
					htmltext = "maestro_ferguson_q10384_06.htm";
				else
					htmltext = "maestro_ferguson_q10384_01.htm";
			}
		}
		else if(npcId == AKU)
		{
			if(st.isStarted())
				htmltext = "sofa_aku_q10384_01.htm";
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
			if(cond == 2)
			{
				st.setCond(3);
				st.playSound(SOUND_MIDDLE);
				st.giveItems(TAUTIS_FRAGMENT, 1);
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