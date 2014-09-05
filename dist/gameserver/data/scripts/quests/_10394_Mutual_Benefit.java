package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * Created by Archer on 9/5/2014.
 * Project ertheia
 */
public class _10394_Mutual_Benefit extends Quest implements ScriptFile
{
    private static final int MIN_LEVEL = 46;
    private static final int MAX_LEVEL = 52;
    // Quest's NPCs
    private static final int KELIOS = 33862;
    // Quest's Monster
    private static final int HUNTER_GARGOYLE = 20241;
    private static final int HUNTER_GARGOYLE_COUNT = 15;
    private static final int TARLK_BASILISK = 20573;
    private static final int TARLK_BASILISK_COUNT = 20;
    private static final int ELDER_TARLK_BASILISK = 20574;
    private static final int ELDER_TARLK_BASILISK_COUNT = 20;
    // Quest's Rewards
    private static final int EXP = 3151312;
    private static final int SP = 756;
    private static final int EAC = 22011;
    private static final int EAC_COUNT = 6;
    private static final int STEEL_DOOR_GUILD_COIN = 37045;
    private static final int STEEL_DOOR_GUILD_COIN_COUNT = 26;

    public _10394_Mutual_Benefit()
    {
        super(false);
        this.addStartNpc(KELIOS);
        this.addLevelCheck(MIN_LEVEL, MAX_LEVEL);
        this.addKillId(HUNTER_GARGOYLE, TARLK_BASILISK, ELDER_TARLK_BASILISK);
    }

    @Override
    public String onEvent(final String event, final QuestState qs, final NpcInstance npc)
    {
        String htmlText = event;
        if(event != null && qs != null)
        {
            if(event.equalsIgnoreCase("quest_accept"))
            {
                qs.setState(STARTED);
                qs.setCond(1);
                qs.playSound(SOUND_ACCEPT);
                htmlText = "03.htm";
            }
            else if(event.equalsIgnoreCase("get_reward"))
            {
                qs.addExpAndSp(EXP,SP);
                qs.giveItems(EAC,EAC_COUNT);
                qs.giveItems(STEEL_DOOR_GUILD_COIN,STEEL_DOOR_GUILD_COIN_COUNT);
                qs.setState(COMPLETED);
                qs.exitCurrentQuest(false);
                qs.playSound(SOUND_FINISH);
                htmlText = "05.htm";
            }
        }
        return htmlText;
    }

    @Override
    public String onTalk(final NpcInstance npc, final QuestState qs)
    {
        String htmlText = NO_QUEST_DIALOG;
        if(npc != null && qs != null)
        {
            int npcId = npc.getNpcId();
            int cond = qs.getCond();
            Player player = qs.getPlayer();
            if(player != null)
            {
                if(qs.isCompleted())
                {
                    htmlText = "completed-quest.htm";
                }
                else
                {
                    if(npcId == KELIOS)
                    {
                        if(cond == 0)
                        {
                            htmlText = "00.htm";
                        }
                        else if(cond == 2)
                        {
                            htmlText = "04.htm";
                        }
                    }
                }
            }
        }
        return htmlText;
    }

    @Override
    public String onKill(final NpcInstance npc, final QuestState qs)
    {
        if(npc != null && qs != null)
        {
            int npcId = npc.getNpcId();
            int cond = qs.getCond();
            if(cond == 1)
            {
                int gargoyle = qs.getInt("hunter_gargoyle");
                int tarlk = qs.getInt("tarlk_basilisk");
                int elder = qs.getInt("elder_tarlk_basilisk");
                if (npcId == HUNTER_GARGOYLE && gargoyle < HUNTER_GARGOYLE_COUNT)
                {
                    qs.set("hunter_gargoyle", gargoyle + 1);
                }
                else if (npcId == TARLK_BASILISK && tarlk < TARLK_BASILISK_COUNT)
                {
                    qs.set("tarlk_basilisk", tarlk + 1);
                }
                else if(npcId == ELDER_TARLK_BASILISK && elder < ELDER_TARLK_BASILISK_COUNT)
                {
                    qs.set("elder_tarlk_basilisk", elder + 1);
                }
                if(gargoyle >= HUNTER_GARGOYLE_COUNT && tarlk >= TARLK_BASILISK_COUNT && elder >= ELDER_TARLK_BASILISK_COUNT)
                {
                    qs.setCond(2);
                    qs.unset("hunter_gargoyle");
                    qs.unset("tarlk_basilisk");
                    qs.unset("elder_tarlk_basilisk");
                    qs.playSound(SOUND_MIDDLE);
                }
            }
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
