package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;


//By Evil_dnk dev.fairytale-world.ru

public class _10366_RuinsStatusUpdate extends Quest implements ScriptFile {
	private static final int revian = 32147;
	private static final int tuk = 32150;
	private static final int prana = 32153;
	private static final int devon = 32160;
	private static final int moka = 32157;
	private static final int valpor = 32146;
	private static final int sebion = 32978;

	@Override
	public void onLoad() {
	}

	@Override
	public void onReload() {
	}

	@Override
	public void onShutdown() {
	}

	public _10366_RuinsStatusUpdate() {

		super(false);
		addStartNpc(sebion);
		addTalkId(revian);
		addTalkId(tuk);
		addTalkId(prana);
		addTalkId(devon);
		addTalkId(moka);
		addTalkId(valpor);
		addTalkId(sebion);

		addLevelCheck(16, 25);
		//addQuestCompletedCheck(_10365_SeekerEscort.class);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) {
		String htmltext = event;

		if(event.equalsIgnoreCase("qet_rev")) {
			st.getPlayer().addExpAndSp(150000, 30000);
			st.giveItems(57, 75000);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
			{
				if(st.getPlayer().getRace() == Race.HUMAN)
					htmltext = "1-5h.htm";
				else if(st.getPlayer().getRace() == Race.ELF)
					htmltext = "1-5e.htm";
				else if(st.getPlayer().getRace() == Race.DARKELF)
					htmltext = "1-5de.htm";
				else if(st.getPlayer().getRace() == Race.DWARF)
					htmltext = "1-5d.htm";
				else if(st.getPlayer().getRace() == Race.KAMAEL)
					htmltext = "1-5k.htm";
				else if(st.getPlayer().getRace() == Race.ORC)
					htmltext = "1-5o.htm";
			}

		} else if(event.equalsIgnoreCase("quest_ac")) {
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);

			if(st.getPlayer().getRace() == Race.HUMAN) {
				st.setCond(2);
				htmltext = "0-4h.htm";
			} else if(st.getPlayer().getRace() == Race.ELF) {
				st.setCond(3);
				htmltext = "0-4e.htm";
			} else if(st.getPlayer().getRace() == Race.DARKELF) {
				st.setCond(4);
				htmltext = "0-4de.htm";
			} else if(st.getPlayer().getRace() == Race.DWARF) {
				st.setCond(6);
				htmltext = "0-4d.htm";
			} else if(st.getPlayer().getRace() == Race.KAMAEL) {
				st.setCond(7);
				htmltext = "0-4k.htm";
			} else if(st.getPlayer().getRace() == Race.ORC) {
				st.setCond(5);
				htmltext = "0-4o.htm";
			}

		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) {
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = "noquest";

		if(npcId == sebion) {
			if(st.isCompleted())
				return htmltext;
			else if(cond == 0 && checkStartCondition(st.getPlayer()))
				htmltext = "start.htm";
			else if(cond == 2 || cond == 3 || cond == 4 || cond == 5 || cond == 6 || cond == 7) {
				if(st.getPlayer().getRace() == Race.HUMAN)
					htmltext = "0-5h.htm";
				else if(st.getPlayer().getRace() == Race.ELF)
					htmltext = "0-5e.htm";
				else if(st.getPlayer().getRace() == Race.DARKELF)
					htmltext = "0-5de.htm";
				else if(st.getPlayer().getRace() == Race.DWARF)
					htmltext = "0-5d.htm";
				else if(st.getPlayer().getRace() == Race.KAMAEL)
					htmltext = "0-5k.htm";
				else if(st.getPlayer().getRace() == Race.ORC)
					htmltext = "0-5o.htm";
			} else
				htmltext = "0-nc.htm";
		} else if(npcId == devon) {
			if(st.isCompleted())
				htmltext = "1-5dec.htm";
			else if(st.getPlayer().getRace() == Race.DARKELF) {
				if(cond == 0)
					return htmltext;
				else if(cond == 4)
					htmltext = "1-3de.htm";
			} else
				htmltext = "1-2de.htm";
		} else if(npcId == revian) {
			if(st.isCompleted())
				htmltext = "1-5ec.htm";
			else if(st.getPlayer().getRace() == Race.ELF) {
				if(cond == 0)
					return htmltext;
				else if(cond == 3)
					htmltext = "1-3e.htm";
			} else
				htmltext = "1-2e.htm";
		} else if(npcId == prana) {
			if(st.isCompleted())
				htmltext = "1-5hc.htm";
			else if(st.getPlayer().getRace() == Race.HUMAN) {
				if(cond == 0)
					return htmltext;
				else if(cond == 2)
					htmltext = "1-3h.htm";
			} else
				htmltext = "1-2h.htm";
		} else if(npcId == valpor) {
			if(st.isCompleted())
				htmltext = "1-5kc.htm";
			else if(st.getPlayer().getRace() == Race.KAMAEL) {
				if(cond == 0)
					return htmltext;
				else if(cond == 7)
					htmltext = "1-3k.htm";
			} else
				htmltext = "1-2k.htm";
		} else if(npcId == moka) {
			if(st.isCompleted())
				htmltext = "1-5dc.htm";
			else if(st.getPlayer().getRace() == Race.DWARF) {
				if(cond == 0)
					return htmltext;
				else if(cond == 6)
					htmltext = "1-3d.htm";
			} else
				htmltext = "1-2d.htm";
		} else if(npcId == tuk) {
			if(st.isCompleted())
				htmltext = "1-5oc.htm";
			else if(st.getPlayer().getRace() == Race.ORC) {
				if(cond == 0)
					return htmltext;
				else if(cond == 5)
					htmltext = "1-3o.htm";
			} else
				htmltext = "1-2o.htm";
		}
		return htmltext;
	}
}
