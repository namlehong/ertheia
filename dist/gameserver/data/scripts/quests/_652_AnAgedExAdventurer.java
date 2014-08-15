package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _652_AnAgedExAdventurer extends Quest implements ScriptFile
{
	//NPC
	private static final int Tantan = 32012;
	private static final int Sara = 30180;
	//Item
	private static final int SoulshotCgrade = 1464;
	private static final int ScrollEnchantArmorD = 956;

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

	public _652_AnAgedExAdventurer()
	{
		super(false);

		addStartNpc(Tantan);
		addTalkId(Sara);
		addLevelCheck(46);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("retired_oldman_tantan_q0652_03.htm") && st.getQuestItemsCount(SoulshotCgrade) >= 100)
		{
			st.setCond(1);
			st.setState(STARTED);
			st.takeItems(SoulshotCgrade, 100);
			st.playSound(SOUND_ACCEPT);
			htmltext = "retired_oldman_tantan_q0652_04.htm";
		}
		else
		{
			htmltext = "retired_oldman_tantan_q0652_03.htm";
			st.exitCurrentQuest(true);
			st.playSound(SOUND_GIVEUP);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int cond = st.getCond();
		if(npcId == Tantan)
		{
			if(cond == 0)
				if(st.getPlayer().getLevel() < 46)
				{
					htmltext = "retired_oldman_tantan_q0652_01a.htm";
					st.exitCurrentQuest(true);
				}
				else
					htmltext = "retired_oldman_tantan_q0652_01.htm";
		}
		else if(npcId == Sara && cond == 1)
		{
			htmltext = "sara_q0652_01.htm";
			if(Rnd.chance(10))
			{
				st.giveItems(22033, 1, false);
				st.giveItems(952, 1, false);
			}	
			else
				st.giveItems(8630, 1, false);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		return htmltext;
	}
}