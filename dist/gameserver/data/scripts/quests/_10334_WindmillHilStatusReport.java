package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.components.NpcString;

//By Evil_dnk dev.fairytale-world.ru
public class _10334_WindmillHilStatusReport extends Quest implements ScriptFile {
	private static final int batis = 30332;
	private static final int shnain = 33508;
	private static final int sword = 2499;
	private static final int atuba = 190;
	private static final int dagg = 225;


	@Override
	public void onLoad() {
	}

	@Override
	public void onReload() {
	}

	@Override
	public void onShutdown() {
	}

	public _10334_WindmillHilStatusReport() {
		super(false);
		addStartNpc(shnain);
		addTalkId(shnain);
		addTalkId(batis);

		addLevelCheck(22, 40);
		addQuestCompletedCheck(_10333_DisappearedSakum.class);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) {
		String htmltext = event;
		Player player = st.getPlayer();
		if(event.equalsIgnoreCase("quest_ac")) {
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
			htmltext = "0-3.htm";
		}
		if(event.equalsIgnoreCase("qet_rev")) {
			htmltext = "1-3.htm";
			player.sendPacket(new ExShowScreenMessage(NpcString.WEAPONS_HAVE_BEEN_ADDED_TO_YOUR_INVENTORY, 4500, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
			st.getPlayer().addExpAndSp(200000, 48);
			st.giveItems(57, 850);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
			if(player.isMageClass()) {
				st.giveItems(atuba, 1, false);
			} else if(player.getClassId().getId() == 7 || player.getClassId().getId() == 35 || player.getClassId().getId() == 7 || player.getClassId().getId() == 125 || player.getClassId().getId() == 126) {
				st.giveItems(dagg, 1, false);
			} else st.giveItems(sword, 1, false);

		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) {
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = "noquest";

		if(npcId == shnain) {
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