package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author pchayka
 *         Repeatable
 */
public class _907_DragonTrophyValakas extends Quest implements ScriptFile
{
	private static final int Klein = 31540;
	private static final int Valakas = 29028;
	private static final int MedalofGlory = 21874;

	public _907_DragonTrophyValakas()
	{
		super(PARTY_ALL);
		addStartNpc(Klein);
		addKillId(Valakas);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("klein_q907_04.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}

		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getCond();
		if(npc.getNpcId() == Klein)
		{
			switch(st.getState())
			{
				case CREATED:
					if(st.isNowAvailable())
					{
						if(st.getPlayer().getLevel() >= 84)
						{
							if(st.getQuestItemsCount(7267) > 0)
								htmltext = "klein_q907_01.htm";
							else
								htmltext = "klein_q907_00b.htm";
						}
						else
						{
							htmltext = "klein_q907_00.htm";
							st.exitCurrentQuest(true);
						}
					}
					else
						htmltext = "theodric_q907_00a.htm";
					break;
				case STARTED:
					if(cond == 1)
						htmltext = "klein_q907_05.htm";
					break;
			}
		}
		else if(cond == 2)
		{
			htmltext = "klein_q907_06.htm";
			st.giveItems(MedalofGlory, 30);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(this);
		}

		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond == 1)
		{
			if(npc.getNpcId() == Valakas)
				st.setCond(2);
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