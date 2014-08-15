package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author GodWorld & Bonux
**/
public class _10383_FergasonsOffer extends Quest implements ScriptFile 
{
	// NPC'S
	private static final int SIZRAK = 33669;
	private static final int AKU = 33671;
	private static final int FERGASON = 33681;

	// Monster's
	private static final int[] MONSTERS = { 23213, 23214, 23215, 23216, 23217, 23218, 23219 };

	// Item's
	private static final int UNSTABLE_PETRA = 34958;

	public _10383_FergasonsOffer()
	{
		super(true);
		addStartNpc(SIZRAK);
		addTalkId(SIZRAK, AKU, FERGASON);
		addKillId(MONSTERS);
		addQuestItem(UNSTABLE_PETRA);
		addLevelCheck(97);
		addQuestCompletedCheck(_10381_TotheSeedofHellfire.class);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("sofa_sizraku_q10383_03.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("maestro_ferguson_q10383_04.htm"))
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("sofa_aku_q10383_03.htm"))
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
		if(npcId == SIZRAK)
		{
			if(st.isCompleted())
				htmltext = "sofa_sizraku_q10383_05.htm";
			else if(st.isStarted())
				htmltext = "sofa_sizraku_q10383_06.htm";
			else
			{
				Player player = st.getPlayer();
				QuestState pst = player.getQuestState(_10381_TotheSeedofHellfire.class);
				if(player.getLevel() < 97)
					htmltext = "sofa_sizraku_q10383_04.htm";
				else if(pst == null || !pst.isCompleted())
					htmltext = "sofa_sizraku_q10383_07.htm";
				else
					htmltext = "sofa_sizraku_q10383_01.htm";
			}
		}
		else if(npcId == FERGASON)
		{
			if(cond == 1)
				htmltext = "maestro_ferguson_q10383_01.htm";
			else if(cond == 2)
				htmltext = "maestro_ferguson_q10383_05.htm";
		}
		else if(npcId == AKU)
		{
			if(cond == 2)
				htmltext = "sofa_aku_q10383_01.htm";
			else if(cond == 3)
				htmltext = "sofa_aku_q10383_02.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st) 
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(ArrayUtils.contains(MONSTERS, npcId))
		{
			if(cond == 2)
			{
				st.giveItems(UNSTABLE_PETRA, 1);
				if(st.getQuestItemsCount(UNSTABLE_PETRA) >= 20)
				{
					st.setCond(3);
					st.playSound(SOUND_MIDDLE);
				}
				else
					st.playSound(SOUND_ITEMGET);
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