package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author pchayka
 */

public class _453_NotStrongEnough extends Quest implements ScriptFile
{
	private static final int Klemis = 32734;

	public static final String A_MOBS = "a_mobs";
	public static final String B_MOBS = "b_mobs";
	public static final String C_MOBS = "c_mobs";
	public static final String E_MOBS = "e_mobs";

	private static final int[] Rewards = 
	{
		18103,
		18104,
		18105,
		18106,
		18107,
		18108,
		18109,
		18110,
		18111,
		18112,
		18113,
		18137,
		18138,
		18139,
		18140,
		18141,
		18142,
		18143,
		18144,
		18145,
		18146,
		18147,
		22425,
		22428,
		22635,
		22636,
		22637,
		22638,
		22639,
		22640,
		22641,
		22642,
		22643,
		22644,
		22645,
		22646
	};

	public _453_NotStrongEnough()
	{
		super(true);
		addStartNpc(Klemis);

		// bistakon 4	"1022746|1022747|1022748|1022749"	4	"15|15|15|15"
		addKillNpcWithLog(2, A_MOBS, 15, 22746, 22750);
		addKillNpcWithLog(2, B_MOBS, 15, 22747, 22751);
		addKillNpcWithLog(2, C_MOBS, 15, 22748, 22752);
		addKillNpcWithLog(2, E_MOBS, 15, 22749, 22753);
		// reptilikon 3	"1022754|1022755|1022756"	3	"20|20|20"
		addKillNpcWithLog(3, A_MOBS, 20, 22754, 22757);
		addKillNpcWithLog(3, B_MOBS, 20, 22755, 22758);
		addKillNpcWithLog(3, C_MOBS, 20, 22756, 22759);
		// cokrakon  3	"1022760|1022761|1022762"	3	"20|20|20"
		addKillNpcWithLog(4, A_MOBS, 20, 22760, 22763);
		addKillNpcWithLog(4, B_MOBS, 20, 22761, 22764);
		addKillNpcWithLog(4, C_MOBS, 20, 22762, 22765);
		addLevelCheck(84);
		addQuestCompletedCheck(_10282_ToTheSeedOfAnnihilation.class);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("klemis_q453_03.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("bistakon"))
		{
			htmltext = "klemis_q453_05.htm";
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("reptilicon"))
		{
			htmltext = "klemis_q453_06.htm";
			st.setCond(3);
		}
		else if(event.equalsIgnoreCase("cokrakon"))
		{
			htmltext = "klemis_q453_07.htm";
			st.setCond(4);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == Klemis)
		{
			switch(st.getState())
			{
				case CREATED:
				{
					QuestState qs = st.getPlayer().getQuestState(_10282_ToTheSeedOfAnnihilation.class);
					if(st.getPlayer().getLevel() >= 84 && qs != null && qs.isCompleted())
					{
						if(st.isNowAvailable())
							htmltext = "klemis_q453_01.htm";
						else
							htmltext = "klemis_q453_00a.htm";
					}
					else
						htmltext = "klemis_q453_00.htm";
					break;
				}
				case STARTED:
				{
					if(cond == 1)
						htmltext = "klemis_q453_03.htm";
					else if(cond == 2)
						htmltext = "klemis_q453_09.htm";
					else if(cond == 3)
						htmltext = "klemis_q453_10.htm";
					else if(cond == 4)
						htmltext = "klemis_q453_11.htm";
					else if(cond == 5)
					{
						htmltext = "klemis_q453_12.htm";
						int rewardId = Rewards[Rnd.get(Rewards.length)];
						st.giveItems(rewardId, 1);
						st.playSound(SOUND_FINISH);
						st.exitCurrentQuest(this);
					}
					break;
				}
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		boolean doneKill = updateKill(npc, st);
		if(doneKill)
		{
			st.unset(A_MOBS);
			st.unset(B_MOBS);
			st.unset(C_MOBS);
			st.unset(E_MOBS);
			st.setCond(5);
		}
		return null;
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