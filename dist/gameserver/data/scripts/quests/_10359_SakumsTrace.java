package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;
import org.apache.commons.lang3.ArrayUtils;

//By Evil_dnk dev.fairytale-world.ru

public class _10359_SakumsTrace extends Quest implements ScriptFile {

	private static final int guild = 31795;
	private static final int fred = 33179;
	private static final int reins = 30288;  //Human Warrior
	private static final int raimon = 30289;  //Human Mag
	private static final int tobias = 30297;  //   Dark Elf
	private static final int Drikus = 30505;  //  Orc
	private static final int mendius = 30504;  //  Dwarf
	private static final int gershfin = 32196;  // Kamael
	private static final int elinia = 30155;  //  Elf mag
	private static final int ershandel = 30158;  // Elf warrior

	private static final int frag = 17586;

	private static final int[] huntl = {20067, 20070, 20072};
	private static final int[] hunth = {23097, 23098, 23026, 20192};

	@Override
	public void onLoad() {
	}

	@Override
	public void onReload() {
	}

	@Override
	public void onShutdown() {
	}

	public _10359_SakumsTrace() {
		super(false);
		addStartNpc(guild);
		addTalkId(fred);
		addTalkId(guild);
		addTalkId(elinia);
		addTalkId(raimon);
		addTalkId(reins);						
		addTalkId(ershandel);					
		addTalkId(gershfin);	
		addTalkId(tobias);
		addTalkId(Drikus);
		addTalkId(mendius);		

		addKillId(huntl);
		addKillId(hunth);
		addQuestItem(frag);


		addLevelCheck(23, 40);
		addQuestCompletedCheck(_10336_DividedSakumKanilov.class);
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
			st.getPlayer().addExpAndSp(900000, 216);
			st.giveItems(57, 1080);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
			if(st.getPlayer().getRace() == Race.HUMAN) {
				if(st.getPlayer().isMageClass())
					htmltext = "2-3re.htm";
				else
					htmltext = "2-3r.htm";
			} else if(st.getPlayer().getRace() == Race.ELF) {
				if(st.getPlayer().isMageClass())
					htmltext = "2-3e.htm";
				else
					htmltext = "2-3ew.htm";
			} else if(st.getPlayer().getRace() == Race.DARKELF)
				htmltext = "2-3t.htm";
			else if(st.getPlayer().getRace() == Race.ORC)
				htmltext = "2-3d.htm";
			else if(st.getPlayer().getRace() == Race.DWARF)
				htmltext = "2-3m.htm";
			else if(st.getPlayer().getRace() == Race.KAMAEL)
				htmltext = "2-3g.htm";
		}


		if(event.equalsIgnoreCase("1-3.htm"))
			st.setCond(2);

		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) {
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = "noquest";

		if(npcId == guild) {
			if(st.isCompleted())
				return htmltext;
			else if(cond == 0 && checkStartCondition(st.getPlayer()))
				htmltext = "0-1.htm";
			else if(cond == 1 || cond == 2 || cond == 3)
				htmltext = "0-4.htm";
		} else if(npcId == fred) {
			if(st.isCompleted())
				return htmltext;
			else if(cond == 0)
				return htmltext;
			else if(cond == 1)
				htmltext = "1-1.htm";
			else if(cond == 2)
				htmltext = "1-4.htm";
			else if(cond == 3) {
				if(st.getPlayer().getRace() == Race.HUMAN) {
					if(st.getPlayer().isMageClass()) {
						htmltext = "1-5re.htm";
						st.setCond(4);
					} else {
						htmltext = "1-5r.htm";
						st.setCond(5);
					}
				} else if(st.getPlayer().getRace() == Race.ELF) {
					if(st.getPlayer().isMageClass()) {
						htmltext = "1-5e.htm";
						st.setCond(11);
						
					} else {
						htmltext = "1-5ew.htm";
						st.setCond(10);
					}
				} else if(st.getPlayer().getRace() == Race.DARKELF) {
					htmltext = "1-5t.htm";
					st.setCond(6);

				} else if(st.getPlayer().getRace() == Race.ORC) {
					htmltext = "1-5d.htm";
					st.setCond(7);

				} else if(st.getPlayer().getRace() == Race.DWARF) {
					htmltext = "1-5m.htm";
					st.setCond(8);
				} else if(st.getPlayer().getRace() == Race.KAMAEL) {
					htmltext = "1-5g.htm";
					st.setCond(9);
				}
			}

		} else if(npcId == raimon && st.getPlayer().getRace() == Race.HUMAN && st.getPlayer().isMageClass()) {
			if(st.isCompleted())
				htmltext = "2re-c.htm";
			else if(cond == 0)
				return htmltext;
			else if(cond == 4)
				htmltext = "2-1re.htm";
		} else if(npcId == reins && st.getPlayer().getRace() == Race.HUMAN && !st.getPlayer().isMageClass()) {
			if(st.isCompleted())
				htmltext = "2r-c.htm";
			else if(cond == 0)
				return htmltext;
			else if(cond == 5)
				htmltext = "2-1r.htm";
		} else if(npcId == tobias && st.getPlayer().getRace() == Race.DARKELF) {
			if(st.isCompleted())
				htmltext = "2t-c.htm";
			else if(cond == 0)
				return htmltext;
			else if(cond == 6)
				htmltext = "2-1t.htm";
		} else if(npcId == Drikus && st.getPlayer().getRace() == Race.ORC) {
			if(st.isCompleted())
				htmltext = "2d-c.htm";
			else if(cond == 0)
				return htmltext;
			else if(cond == 7)
				htmltext = "2-1d.htm";
		} else if(npcId == gershfin && st.getPlayer().getRace() == Race.KAMAEL) {
			if(st.isCompleted())
				htmltext = "2g-c.htm";
			else if(cond == 0)
				return htmltext;
			else if(cond == 9)
				htmltext = "2-1g.htm";
		} else if(npcId == elinia && st.getPlayer().getRace() == Race.ELF && !st.getPlayer().isMageClass()) {
			if(st.isCompleted())
				htmltext = "2ew-c.htm";
			else if(cond == 0)
				return htmltext;
			else if(cond == 10)
				htmltext = "2-1e.htm";
		} else if(npcId == ershandel && st.getPlayer().getRace() == Race.ELF && st.getPlayer().isMageClass()) {
			if(st.isCompleted())
				htmltext = "2e-c.htm";
			else if(cond == 0)
				return htmltext;
			else if(cond == 11)
				htmltext = "2-1ew.htm";
		} else if(npcId == mendius && st.getPlayer().getRace() == Race.DWARF) {
			if(st.isCompleted())
				htmltext = "2m-c.htm";
			else if(cond == 0)
				return htmltext;
			else if(cond == 8)
				htmltext = "2-1m.htm";
		}

		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st) {
		int cond = st.getCond();
		int npcId = npc.getNpcId();

		if(cond == 2 && st.getQuestItemsCount(frag) < 20) {
			if(ArrayUtils.contains(huntl, npcId))
				st.rollAndGive(frag, 1, 15);

			else if(ArrayUtils.contains(hunth, npcId))
				st.rollAndGive(frag, 1, 35);
		}
		if(st.getQuestItemsCount(frag) >= 20)
			st.setCond(3);

		return null;
	}
}