package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author Bonux
 */
public class _10321_QualificationsOfTheSeeker extends Quest implements ScriptFile
{
	//NPC's
	private static final int TEODOR = 32975;
	private static final int SHENON = 32974;

	public _10321_QualificationsOfTheSeeker()
	{
		super(false);
		addStartNpc(TEODOR);
		addTalkId(TEODOR, SHENON);
		addRaceCheck(true, true, true, true, true, true, false);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		//String htmltext = event;
		if(event.equalsIgnoreCase("32975_03.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("32974_02.htm"))
		{
			st.giveItems(ADENA_ID, 5000);
			st.addExpAndSp(40, 500);
			st.setState(COMPLETED);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == TEODOR)
		{
			if(cond == 0)
			{
				if(checkStartCondition(st.getPlayer()))
					htmltext = "32975_01.htm";
				else
					htmltext = "32975_00.htm";
			}
			else if(cond == 1)
				htmltext = "32975_04.htm";
		}
		else if(npcId == SHENON && st.getCond() == 1)
		{
			htmltext = "32974_01.htm";
		}
		return htmltext;
	}

	@Override
	public boolean checkStartCondition(Player player)
	{
		QuestState qs = player.getQuestState(_10320_LetsGoToTheCentralSquare.class);
		return player.getLevel() <= 20 && qs != null && qs.getState() == COMPLETED;
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