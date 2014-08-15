package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * Quest "Divided Sakum, Kanilov"
 *
 * @author Darvin
 */
public class _10336_DividedSakumKanilov extends Quest implements ScriptFile
{
	private static final int ZENATH = 33509;
	private static final int GUILDSMAN = 31795;

	private static final int KANILOV = 27451;

	private static final int SAKUM_SKETCH_A = 17584;
	private static final int SCROLL_ENCHANT_WEAPON_D = 22006;

	public _10336_DividedSakumKanilov()
	{
		super(false);

		addStartNpc(ZENATH);
		addTalkId(GUILDSMAN);
		addKillId(KANILOV);
		addLevelCheck(27, 40);
	}

	@Override
	public String onEvent(String event, QuestState qs, NpcInstance npc)
	{
		String htmltext = event;
		int cond = qs.getCond();
		// Displays call dialog when player reaches 27 level
		// TODO: В окне, которое появляется при нажатии кнопки "Да"/"Нет" окно не закрывается
		if(event.equalsIgnoreCase("zenath_call"))
		{
			htmltext = "zenath_call.htm";
			qs.onTutorialClientEvent(0);
		}
		else if(event.equalsIgnoreCase("quest_accept"))
		{
			htmltext = "zenath_q10336_3.htm";
			qs.setState(STARTED);
			qs.playSound(SOUND_ACCEPT);
			qs.setCond(1);
		}
		else if(cond == 3 && event.equalsIgnoreCase("quest_done"))
		{
			htmltext = "guildsman_q10336_3.htm";
			qs.giveItems(ADENA_ID, 100000);
			qs.giveItems(SCROLL_ENCHANT_WEAPON_D, 3);
			qs.getPlayer().addExpAndSp(350000, 150000);
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
		QuestState reqQuest = qs.getPlayer().getQuestState(_10335_RequestToFindSakum.class);
		switch(npcId)
		{
			case ZENATH:
				if(reqQuest == null || !reqQuest.isCompleted())
					htmltext = "You need to complete quest Request to Find Sakum";
				else if(cond == 0 && (qs.getPlayer().getLevel() < 27 || qs.getPlayer().getLevel() > 40))
					htmltext = "Only characters under level 27 to 40 can accept this quest"; // TODO: Unknown text here
				else if(cond == 0)
					htmltext = "zenath_q10336_1.htm";
				else if(cond == 2)
				{
					htmltext = "zenath_q10336_4.htm";
					qs.setCond(3);
					qs.takeAllItems(SAKUM_SKETCH_A);
					qs.giveItems(SAKUM_SKETCH_A, 1);
				}
				else
					htmltext = "zenath_q10336_taken.htm";
				break;
			case GUILDSMAN:
				if(cond == 3)
					htmltext = "guildsman_q10336_1.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 1 && npc.getNpcId() == KANILOV)
			qs.setCond(2);
		return "";
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
