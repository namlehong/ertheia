package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * Quest "Request to Find Sakum"
 *
 * @author 
 */
public class _10335_RequestToFindSakum extends Quest implements ScriptFile
{
	private static final int BATHIS = 30332;
	private static final int KALLESIN = 33177;
	private static final int ZENATH = 33509;

	private static final int SKELETON_TRACKER = 20035;
	private static final int SKELETON_BOWMAN = 20051;
	private static final int SKELETON_SCOUT = 20045;
	private static final int RUIN_SPARTOI = 20054;
	private static final int RUIN_ZOMBIE = 20026;
	private static final int RUIN_ZOMBIE_LEADER = 20029;

	public static final String TRACKER = "TRACKER";
	public static final String BOWMAN = "BOWMAN";
	public static final String SPARTOI = "SPARTOI";
	public static final String ZOMBIE = "ZOMBIE";

	public _10335_RequestToFindSakum()
	{
		super(false);

		addStartNpc(BATHIS);
		addTalkId(KALLESIN, ZENATH);
		addKillNpcWithLog(2, BOWMAN, 10, SKELETON_BOWMAN, SKELETON_SCOUT);
		addKillNpcWithLog(2, SPARTOI, 15, RUIN_SPARTOI);
		addKillNpcWithLog(2, TRACKER, 10, SKELETON_TRACKER);
		addKillNpcWithLog(2, ZOMBIE, 15, RUIN_ZOMBIE, RUIN_ZOMBIE_LEADER);
		addLevelCheck(23, 40);
		addQuestCompletedCheck(_10334_WindmillHilStatusReport.class);
	}

	@Override
	public String onEvent(String event, QuestState qs, NpcInstance npc)
	{
		String htmltext = event;

		int cond = qs.getCond();
		if(event.equalsIgnoreCase("quest_accept"))
		{
			htmltext = "bathis_q10335_3.htm";
			qs.setState(STARTED);
			qs.playSound(SOUND_ACCEPT);
			qs.setCond(1);
		}
		else if(cond == 1 && event.equalsIgnoreCase("kallesin_accept"))
		{
			htmltext = "kallesin_q10335_2.htm";
			qs.setCond(2);
		}
		else if(cond == 3 && event.equalsIgnoreCase("quest_done"))
		{
			htmltext = "zenath_q10335_3.htm";
			qs.getPlayer().addExpAndSp(350000, 84);
			qs.giveItems(ADENA_ID, 900);
			qs.exitCurrentQuest(false);
			qs.playSound(SOUND_FINISH);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState qs)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = qs.getCond();
		switch(npcId)
		{
			case BATHIS:
				if(qs.isCompleted())
					htmltext = "bathis_q10335_taken.htm";
				if(cond == 0)
				{
					if(!checkStartCondition(qs.getPlayer()))
						htmltext = "bathis_q10335_0.htm";
					else
						htmltext = "bathis_q10335_1.htm";
				}
				else if(cond == 1 || cond == 2 || cond == 3)
					htmltext = "bathis_q10335_3.htm";
				else
				break;
			case KALLESIN:
				if(cond == 1)
					htmltext = "kallesin_q10335_1.htm";
				break;
			case ZENATH:
				if(cond == 3)
					htmltext = "zenath_q10335_1.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() != 2)
			return "";

		if(updateKill(npc, qs)) 
		{
			qs.unset(BOWMAN);
			qs.unset(SPARTOI);
			qs.unset(TRACKER);
			qs.unset(ZOMBIE);
			qs.setCond(3);
		}

		return "";
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
