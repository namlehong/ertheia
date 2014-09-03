package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * Quest "Disappeared Sakum"
 *
 * @author 
 */
public class _10333_DisappearedSakum extends Quest implements ScriptFile
{
	private static final int BATHIS = 30332;
	private static final int VENT = 33176;
	private static final int SCHUNAIN = 33508;

	private static final int LANGK_LIZARDMAN = 20030;
	private static final int VUKU_ORC_FIGHTER = 20017;
	private static final int POISONOUS_SPIDER = 23094;
	private static final int VENOMOUS_SPIDER = 20038;
	private static final int ARACHNID_PREDATOR = 20050;

	private static final int LANGK_LIZARDMAN_ITEM = 1611;
	private static final int VUKU_ORC_ITEM = 1609;
	private static final int SUSPICIOUS_MARK = 17583;

	public _10333_DisappearedSakum()
	{
		super(false);
		addStartNpc(BATHIS);
		addTalkId(VENT, SCHUNAIN);
		addKillId(LANGK_LIZARDMAN, VUKU_ORC_FIGHTER, POISONOUS_SPIDER, VENOMOUS_SPIDER, ARACHNID_PREDATOR);
		addQuestItem(LANGK_LIZARDMAN_ITEM, VUKU_ORC_ITEM, SUSPICIOUS_MARK);
		addLevelCheck(18, 40);
	}

	@Override
	public String onEvent(String event, QuestState qs, NpcInstance npc)
	{
		String htmltext = event;
		int cond = qs.getCond();
		if(event.equalsIgnoreCase("quest_accept"))
		{
			htmltext = "bathis_q10333_5.htm";
			qs.setState(STARTED);
			qs.playSound(SOUND_ACCEPT);
			qs.setCond(1);
		}
		else if(cond == 1 && event.equalsIgnoreCase("vent_accept"))
		{
			htmltext = "vent_q10333_3.htm";
			qs.setCond(2);
		}
		else if(cond == 3 && event.equalsIgnoreCase("quest_done"))
		{
			htmltext = "schunain_q10333_3.htm";
			qs.getPlayer().addExpAndSp(180000, 43);
			qs.giveItems(ADENA_ID, 800);
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
				if(cond == 0 && qs.getPlayer().getLevel() < 18 || qs.getPlayer().getLevel() > 40)
					htmltext = "0-nc.htm";
				else if(cond == 0)
					htmltext = "bathis_q10333_1.htm";
				else
					htmltext = "bathis_q10333_taken.htm";
				break;
			case VENT:
				if(cond == 1)
					htmltext = "vent_q10333_1.htm";
				break;
			case SCHUNAIN:
				if(cond == 3)
					htmltext = "schunain_q10333_1.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		int npcId = npc.getNpcId();
		if(qs.getCond() != 2)
			return "";

		switch(npcId)
		{
			case LANGK_LIZARDMAN:
				if(!qs.haveQuestItem(LANGK_LIZARDMAN_ITEM, 7))
					qs.rollAndGive(LANGK_LIZARDMAN_ITEM, 1, 100);
				break;
			case VUKU_ORC_FIGHTER:
				if(!qs.haveQuestItem(VUKU_ORC_ITEM, 5))
					qs.rollAndGive(VUKU_ORC_ITEM, 1, 100);
				break;
			case POISONOUS_SPIDER:
			case VENOMOUS_SPIDER:
			case ARACHNID_PREDATOR:
				if(!qs.haveQuestItem(SUSPICIOUS_MARK, 5))
					qs.rollAndGive(SUSPICIOUS_MARK, 1, 100);
				break;
		}

		if(qs.haveQuestItem(LANGK_LIZARDMAN_ITEM, 7) && qs.haveQuestItem(VUKU_ORC_ITEM, 5) && qs.haveQuestItem(SUSPICIOUS_MARK, 5))
			qs.setCond(3);

		return "";
	}

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}
}
