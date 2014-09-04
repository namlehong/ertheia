package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;
import quests._10337_SakumImpact;

//By Evil_dnk dev.fairytale-world.ru
public class _10358_DividedSakumPoslof extends Quest implements ScriptFile {

	private static final int guild = 31795;
	private static final int lef = 33510;

	private static final int vilan = 20402;
	private static final int zombi = 20458;
	private static final int poslov = 27452;

	private static final String vilan_item = "vilan";
	private static final String zombi_item = "zombi";
	private int killedposlov;


	@Override
	public void onLoad() {
	}

	@Override
	public void onReload() {
	}

	@Override
	public void onShutdown() {
	}

	public _10358_DividedSakumPoslof() {
		super(false);
		addStartNpc(lef);
		addTalkId(guild);
		addTalkId(lef);
		addKillId(poslov);
		addKillNpcWithLog(1, vilan_item, 23, vilan);
		addKillNpcWithLog(1, zombi_item, 20, zombi);
		addLevelCheck(32, 40);
		addQuestCompletedCheck(_10337_SakumImpact.class);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) {
		String htmltext = event;
		if(event.equalsIgnoreCase("quest_ac")) {
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
			htmltext = "0-3.htm";
		}
		if(event.equalsIgnoreCase("qet_rev")) {
			htmltext = "1-3.htm";
			st.takeAllItems(17585);
			st.getPlayer().addExpAndSp(750000, 180);
			st.giveItems(57, 1050);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}
		if(event.equalsIgnoreCase("1-3.htm")) {
			htmltext = "1-3.htm";
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) {
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = "noquest";

		if(npcId == lef) {
			if(st.isCompleted())
				return htmltext;
			else if(cond == 0 && checkStartCondition(st.getPlayer()))
				htmltext = "0-1.htm";
			else if(cond == 1)
				htmltext = "0-4.htm";
			else if(cond == 2) {
				htmltext = "0-5.htm";
				st.giveItems(17585, 1, false);
				st.setCond(3);
			} else if(cond == 3)
				return htmltext;
			else if(cond == 4)
				return htmltext;
			else
				return htmltext;
		} else if(npcId == guild) {
			if(st.isCompleted())
				htmltext = "1-c.htm";
			else if(cond == 0 || cond == 1 || cond == 2 || cond == 3)
				return htmltext;
			else if(cond == 4)
				htmltext = "1-1.htm";
		}

		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st) {
		boolean doneKill = updateKill(npc, st);

		if(doneKill) {
			st.unset(vilan_item);
			st.unset(zombi_item);
			st.setCond(2);
		}

		int npcId = npc.getNpcId();

		if(npcId == poslov && (st.getCond() == 3)) {
			++killedposlov;
			if(killedposlov >= 1) {
				st.setCond(4);
				st.playSound(SOUND_MIDDLE);
				killedposlov = 0;
			}
		}
		return null;
	}
}