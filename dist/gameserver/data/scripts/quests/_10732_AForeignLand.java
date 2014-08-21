package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.components.UsmVideo;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author Эванесса & Bonux
 */
public class _10732_AForeignLand extends Quest implements ScriptFile
{
	//NPC's
	private static final int NAVARI = 33931;
	private static final int GERETH = 33932;

	public _10732_AForeignLand()
	{
		super(false);
		addStartNpc(NAVARI);
		addFirstTalkId(NAVARI);
		addTalkId(NAVARI, GERETH);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33931-3.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			st.showTutorialClientHTML("QT_001_Radar_01");
			st.getPlayer().sendPacket(UsmVideo.HEROES.packet(st.getPlayer()));
		}
		else if(event.equalsIgnoreCase("33932-2.htm"))
		{
			st.giveItems(ADENA_ID, 3000);
			st.addExpAndSp(75, 2);
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
		if(npcId == NAVARI)
		{
			if(cond == 0)
			{
				if(checkStartCondition(st.getPlayer()))
					htmltext = "33931-1.htm";
			}
			else if(cond == 1)
				htmltext = "33931-4.htm";
		}
		else if(npcId == GERETH && st.getCond() == 1)
		{
			htmltext = "33932-1.htm";
		}
		return htmltext;
	}

	@Override
	public String onFirstTalk(NpcInstance npc, Player player)
	{
		if(npc.getNpcId() == NAVARI)
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