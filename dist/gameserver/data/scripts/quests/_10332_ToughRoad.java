package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

//By Evil_dnk dev.fairytale-world.ru
public class _10332_ToughRoad extends Quest implements ScriptFile {
	private static final int batis = 30332;
	private static final int kakai = 30565;

	@Override
	public void onLoad() {
	}

	@Override
	public void onReload() {
	}

	@Override
	public void onShutdown() {
	}

	public _10332_ToughRoad() {
		super(false);
		addStartNpc(kakai);
		addTalkId(kakai);
		addTalkId(batis);

		addLevelCheck(20, 40);
		addQuestCompletedCheck(_10331_StartOfFate.class);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) {
		String htmltext = event;
		if(event.equalsIgnoreCase("quest_ac")) {
			st.setState(STARTED);
			st.setCond(1);
			st.giveItems(17582, 1, false);
			st.playSound(SOUND_ACCEPT);
			htmltext = "0-2.htm";
		}
		if(event.equalsIgnoreCase("qet_rev")) {
			htmltext = "1-3.htm";
			st.getPlayer().addExpAndSp(90000, 21);
			st.giveItems(57, 700);
			st.takeAllItems(17582);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) {
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = "noquest";

		if(npcId == kakai) {
			if(st.isCompleted())
				return htmltext;
			else if(cond == 0 && checkStartCondition(st.getPlayer()))
				htmltext = "0-1.htm";
			else if(cond == 1)
				htmltext = "0-3.htm";
		} else if(npcId == batis) {
			if(st.isCompleted())
				htmltext = "1-c.htm";
			else if(cond == 0)
				return htmltext;
			else if(cond == 1)
				htmltext = "1-1.htm";
		}
		return htmltext;
	}
}