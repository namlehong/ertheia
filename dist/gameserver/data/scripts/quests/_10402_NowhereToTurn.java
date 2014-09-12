package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * Created by Archer on 9/8/2014.
 * Project ertheia
 */
public class _10402_NowhereToTurn extends Quest implements ScriptFile
{
    private static final int MIN_LEVEL = 58;
    private static final int MAX_LEVEL = 61;
    // Quest's NPCs
    private static final int EBLUNE = 33865;
    // Quest's Monsters
    private static final int MARSH_STALKER = 20679;
    private static final int MARSH_DRAKE = 20680;
    private static final int FALLEN_ORC = 21017;
    private static final int ANCIENT_GARGOYLE = 21018;
    private static final int FALLEN_ORC_ARCHER = 21019;
    private static final int FALLEN_ORC_SHAMAN = 21020;
    private static final int SHARP_TALON_TIGER = 21021;
    private static final int FALLEN_ORC_CAPTAIN = 21022;
    private static final int MONSTER_KILL_COUNT = 40;
    // Quest's Reward
    private static final int EXP = 5482574;
    private static final int SP = 1315;
    private static final int EAB = 948;
    private static final int EAB_COUNT = 5;
    private static final int STEEL_DOOR_GUILD_COIN = 37045;
    private static final int STEEL_DOOR_GUILD_COIN_COUNT = 34;

    public _10402_NowhereToTurn()
    {
        super(false);
        this.addLevelCheck(MIN_LEVEL, MAX_LEVEL);

        this.addStartNpc(EBLUNE);
        this.addKillId(MARSH_STALKER, MARSH_DRAKE, FALLEN_ORC, ANCIENT_GARGOYLE, FALLEN_ORC_ARCHER, FALLEN_ORC_SHAMAN,
                SHARP_TALON_TIGER, FALLEN_ORC_CAPTAIN);
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
                qs.giveItems(EAB,EAB_COUNT);
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
                    if(npcId == EBLUNE)
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
                if(counter < MONSTER_KILL_COUNT)
                {
                    counter++;
                    qs.set("counter", counter);
                    qs.playSound(SOUND_ITEMGET);
                }
                if(counter >= MONSTER_KILL_COUNT)
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
