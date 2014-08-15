package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * Quest "Roles of the Seeker"
 *
 * @author Darvin
 */
public class _10361_RolesOfSeeker extends Quest implements ScriptFile {
    private static final int LAKCIS = 32977;
    private static final int CHESHA = 33449;

    public _10361_RolesOfSeeker() {
        super(false);

        addStartNpc(LAKCIS);
        addTalkId(CHESHA);
		addRaceCheck(true, true, true, true, true, true, false);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;

        if(event.equalsIgnoreCase("quest_accept")) {
            htmltext = "lakcis_q10361_3.htm";
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
            st.setCond(1);
        } else if(st.getCond() == 1 && event.equalsIgnoreCase("quest_done")) {
            htmltext = "chesha_q10361_3.htm";
            st.getPlayer().addExpAndSp(35000, 6500);
            st.giveItems(ADENA_ID, 34000);
            st.exitCurrentQuest(false);
            st.playSound(SOUND_FINISH);
        }

        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        QuestState reqQuest = st.getPlayer().getQuestState("_10330_ToTheYeSagiraRuins");

        switch (npcId) {
            case LAKCIS:
                htmltext = "lakcis_q10361_1.htm";
                if(cond > 0)
                    htmltext = "lakcis_q10361_taken.htm";
                else if(reqQuest == null || !reqQuest.isCompleted())
                    htmltext = "You need to complete quest To the Ye Sagira Ruins"; // TODO: Unknown text here
                else if(cond == 0 && st.getPlayer().getLevel() < 10 || st.getPlayer().getLevel() > 20)
                    htmltext = "Only characters under level 10 to 20 can accept this quest"; // TODO: Unknown text here
                break;
            case CHESHA:
                if(cond == 1)
                    htmltext = "chesha_q10361_1.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }
}
