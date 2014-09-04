package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _10391_ASuspiciousHelper extends Quest implements ScriptFile
{
	private static final int ELI = 33858;
	private static final int CHEL = 33861;
	private static final int IASON_HEINE = 33859;
	private static final int FORGE_IDENTIFICATION_CARD = 36707;
	private static final int MATERIALS = 36708;
    private static final int EAC = 22011;
	private static final int STEEL_DOOR_GUILD_COIN = 37045;
	
	public _10391_ASuspiciousHelper()
	{
		super(false);

		addStartNpc(ELI);
		addTalkId(ELI);
		addTalkId(CHEL);
		addTalkId(IASON_HEINE);
		addQuestItem(FORGE_IDENTIFICATION_CARD);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmlText = event;
		if(event.equalsIgnoreCase("barons_personal_escort_eli_q10391_02"))
			return "barons_personal_escort_eli_q10391_02.htm";
		else if(event.equalsIgnoreCase("barons_personal_escort_eli_q10391_03"))
			return "barons_personal_escort_eli_q10391_03.htm";
		else if(event.equalsIgnoreCase("quest_accept"))
		{
			st.setCond(1);
			st.giveItems(FORGE_IDENTIFICATION_CARD, 1L);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			return "barons_personal_escort_eli_q10391_04.htm";
		}
		else if(event.equalsIgnoreCase("worker_chel_q10391_02"))
			return "worker_chel_q10391_02.htm";
		else if(event.equalsIgnoreCase("worker_chel_q10391_03"))
		{
			if(st.getQuestItemsCount(FORGE_IDENTIFICATION_CARD) > 0)
			{
				st.setCond(2);
				st.takeItems(FORGE_IDENTIFICATION_CARD, 1L);
				st.giveItems(MATERIALS, 1L);
				return "worker_chel_q10391_03.htm";	
			}
		}	
		else if(event.equalsIgnoreCase("iason_heine_q10391_02"))
			return "iason_heine_q10391_02.htm";	
		else if(event.equalsIgnoreCase("iason_heine_q10391_03"))
			return "iason_heine_q10391_03.htm";
		else if(event.equalsIgnoreCase("quest_finish"))	
		{
			st.giveItems(STEEL_DOOR_GUILD_COIN, 1);
            st.giveItems(EAC,1);
			st.addExpAndSp(388290, 93);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
			return "iason_heine_q10391_04.htm";
		}
		return htmlText;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmlText = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == ELI)
		{
			if(st.isCompleted())
            {
                return "This quest is completed";
            }
			else if(cond == 0)
            {
                return "barons_personal_escort_eli_q10391_01.htm";
            }
			else
            {
                return "barons_personal_escort_eli_q10391_04.htm";
            }
		}	
		else if(npcId == CHEL)
        {
            if (cond == 1)
            {
                return "worker_chel_q10391_01.htm";
            }
        }
		else if(npcId == IASON_HEINE)
        {
            if (cond == 2)
            {
                return "iason_heine_q10391_01.htm";
            }
        }
		return htmlText;
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