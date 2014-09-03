package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author Эванесса & Bonux
 */
public class _10320_LetsGoToTheCentralSquare extends Quest implements ScriptFile
{
	//NPC's
	private static final int PANTEON = 32972;
	private static final int TEODOR = 32975;

	public _10320_LetsGoToTheCentralSquare()
	{
		super(false);
		addStartNpc(PANTEON);
		addFirstTalkId(PANTEON);
		addTalkId(PANTEON, TEODOR);
		addRaceCheck(true, true, true, true, true, true, false);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(event.equalsIgnoreCase("32972_03.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			st.showTutorialClientHTML("QT_001_Radar_01");
		}
		else if(event.equalsIgnoreCase("32975_02.htm"))
		{
			st.giveItems(ADENA_ID, 30);
			st.addExpAndSp(30, 5);
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
		if(npcId == PANTEON)
		{
			if(cond == 0)
			{
				if(checkStartCondition(st.getPlayer()))
					htmltext = "32972_01.htm";
				else
					htmltext = "32972_00.htm";
			}
			else if(cond == 1)
				htmltext = "32972_04.htm";
		}
		else if(npcId == TEODOR && st.getCond() == 1)
		{
			htmltext = "32975_01.htm";
		}
		return htmltext;
	}

	@Override
	public String onFirstTalk(NpcInstance npc, Player player)
	{
		if(npc.getNpcId() == PANTEON)
		{
			QuestState st = player.getQuestState(getClass());
			if((st == null || st.getCond() == 0) && checkStartCondition(player))
				player.sendPacket(new ExShowScreenMessage(NpcString.BEGIN_TUTORIAL_QUESTS, 5000, ScreenMessageAlign.TOP_CENTER));
			return "";
		}
		return null;
	}

	@Override
	public boolean checkStartCondition(Player player)
	{
		return player.getLevel() <= 20;
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