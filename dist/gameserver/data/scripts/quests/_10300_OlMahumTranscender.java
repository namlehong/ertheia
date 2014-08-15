package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;
import org.apache.commons.lang3.ArrayUtils;

//By Evil_dnk dev.fairytale-world.ru

public class _10300_OlMahumTranscender extends Quest implements ScriptFile {

	private static final int[] Adventurequid = {33463};

	private static final int mouen = 30196;


	private static final int[] Basilisk = {20573, 20574};
	private static final int[] gnols = {21261, 21262, 21263, 21264, 20241};
	private static final int[] OelMahum = {20575, 35428, 20576, 20161};

	private static final int markofbandit = 19484;
	private static final int markofshaman = 19485;
	private static final int proofmonstr = 19486;

	@Override
	public void onLoad() {
	}

	@Override
	public void onReload() {
	}

	@Override
	public void onShutdown() {
	}

	public _10300_OlMahumTranscender() {
		super(false);
		addStartNpc(Adventurequid);
		addTalkId(Adventurequid);
		addTalkId(mouen);

		addKillId(Basilisk);
		addKillId(gnols);
		addKillId(OelMahum);
		addQuestItem(markofbandit, markofshaman, proofmonstr);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) {
		String htmltext = event;
		if(event.equalsIgnoreCase("0-3.htm")) {
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		if(event.equalsIgnoreCase("1-2.htm"))
			st.setCond(1);

		return htmltext;
	}

	public String onTalk(NpcInstance npc, QuestState st) {
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = "noquest";

		if(ArrayUtils.contains(Adventurequid, npcId)) {
			if(st.getPlayer().getLevel() >= 50 && st.getPlayer().getLevel() <= 54 && cond == 0)
				htmltext = "start.htm";
			else if(cond == 1 && ArrayUtils.contains(Adventurequid, npcId))
				htmltext = "0-4.htm";
			else
				htmltext = "noquest";
		}
		if(npcId == mouen) {
			if(cond == 2 && st.getQuestItemsCount(markofshaman) >= 30) 
			{
				htmltext = "1-3.htm";
				st.takeAllItems(markofbandit);
				st.takeAllItems(markofshaman);
				st.takeAllItems(proofmonstr);
				st.getPlayer().addExpAndSp(2046093, 1618470);
				st.giveItems(57, 329556);
				st.exitCurrentQuest(false);
				st.playSound(SOUND_FINISH);
			}
			else if(cond == 1) 
			{
				htmltext = "1-2.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st) {
		int npcId = npc.getNpcId();

		if(st.getCond() == 1 && ArrayUtils.contains(Basilisk, npcId) && st.getQuestItemsCount(markofbandit) < 30) {
			st.giveItems(markofbandit, 1);
			st.playSound(SOUND_ITEMGET);
		} else if(st.getCond() == 1 && ArrayUtils.contains(gnols, npcId) && st.getQuestItemsCount(markofshaman) < 30) {
			st.giveItems(markofshaman, 1);
			st.playSound(SOUND_ITEMGET);
		} else if(st.getCond() == 1 && ArrayUtils.contains(OelMahum, npcId) && st.getQuestItemsCount(proofmonstr) < 30) {
			st.giveItems(proofmonstr, 1);
			st.playSound(SOUND_ITEMGET);
		}
		if(st.getQuestItemsCount(markofbandit) >= 30 && st.getQuestItemsCount(markofshaman) >= 30 && st.getQuestItemsCount(proofmonstr) >= 30)
			st.setCond(2);
		st.playSound(SOUND_MIDDLE);
		return null;
	}
}