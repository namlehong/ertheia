package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * Created by Archer on 9/9/2014.
 * Project ertheia
 */
public class _10406_BeforeDarknessBearFruit extends Quest implements ScriptFile
{
    private static final int MIN_LEVEL = 61;
    private static final int MAX_LEVEL = 65;
    // Quest's NPCs
    private static final int SHUVANN = 33867;
    // Quest's Monsters
    private static final int KARTIA_FLOWER = 19470;
    private static final int KARTIA_FLOWER_COUNT = 10;
    // Quest's Reward
    private static final int EXP = 3125586;
    private static final int SP = 750;
    private static final int EAA = 730;
    private static final int EAA_COUNT = 3;
    private static final int STEEL_DOOR_GUILD_COIN = 37045;
    private static final int STEEL_DOOR_GUILD_COIN_COUNT = 10;

    public _10406_BeforeDarknessBearFruit()
    {
        super(false);
        this.addLevelCheck(MIN_LEVEL, MAX_LEVEL);
        this.addRaceCheck(true, true, true, true, true, true, false);
        this.addQuestCompletedCheck(_10405_KartiaSeed.class);
        this.addStartNpc(SHUVANN);
        this.addKillId(KARTIA_FLOWER);
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
                qs.addExpAndSp(EXP, SP);
                qs.giveItems(EAA,EAA_COUNT);
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
            if (player != null)
            {
                if(qs.isCompleted())
                {
                    htmlText = "completed-quest.htm";
                }
                else
                {
                    if(npcId == SHUVANN)
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
            int cond = qs.getCond();
            if(cond == 1)
            {
                int counter = qs.getInt("counter");
                if(counter < KARTIA_FLOWER_COUNT)
                {
                    counter++;
                    qs.set("counter", counter);
                    qs.playSound(SOUND_ITEMGET);
                }
                if(counter >= KARTIA_FLOWER_COUNT)
                {
                    qs.setCond(2);
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
