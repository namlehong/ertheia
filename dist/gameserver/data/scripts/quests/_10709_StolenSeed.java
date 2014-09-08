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
public class _10709_StolenSeed extends Quest implements ScriptFile
{
    private static final int MIN_LEVEL = 58;
    private static final int MAX_LEVEL = 61;
    // Quest's NPCs
    private static final int NOVAIN = 33866;
    private static final int MAGIC_CIRCLE_CONTROL_DEVICE = 33961;
    // Quest's Monster
    private static final int CURSED_GIANT_AKUM = 27520;
    // Quest's Items
    private static final int AKUM_MEMORY_FRAGMENT = 39510;
    private static final int NORMAL_FRAGMENT = 39511;
    // Quest's Reward
    private static final int EXP = 731010;
    private static final int SP = 175;
    private static final int EAB = 948;
    private static final int EAB_COUNT = 5;
    private static final int STEEL_DOOR_GUILD_COIN = 37045;
    private static final int STEEL_DOOR_GUILD_COIN_COUNT = 30;

    public _10709_StolenSeed()
    {
        super(false);
        this.addLevelCheck(MIN_LEVEL, MAX_LEVEL);
        this.addStartNpc(NOVAIN);
        this.addTalkId(MAGIC_CIRCLE_CONTROL_DEVICE);
        this.addQuestItem(AKUM_MEMORY_FRAGMENT, NORMAL_FRAGMENT);
        this.addKillId(CURSED_GIANT_AKUM);
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
            else if(event.equalsIgnoreCase("try_fragment"))
            {
                qs.setCond(2);
                qs.playSound(SOUND_MIDDLE);
                htmlText = null;
                qs.addSpawn(CURSED_GIANT_AKUM);
            }
            else if(event.equalsIgnoreCase("get_reward"))
            {
                qs.addExpAndSp(EXP,SP);
                qs.takeAllItems(AKUM_MEMORY_FRAGMENT,NORMAL_FRAGMENT);
                qs.giveItems(EAB,EAB_COUNT);
                qs.giveItems(STEEL_DOOR_GUILD_COIN,STEEL_DOOR_GUILD_COIN_COUNT);
                qs.setState(COMPLETED);
                qs.exitCurrentQuest(false);
                qs.playSound(SOUND_FINISH);
                htmlText = "06.htm";
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
                    if(npcId == NOVAIN)
                    {
                        if(cond == 0)
                        {
                            htmlText = "00.htm";
                        }
                        else if(cond == 3)
                        {
                            htmlText = "05.htm";
                        }
                    }
                    else if(npcId == MAGIC_CIRCLE_CONTROL_DEVICE)
                    {
                        htmlText = "04.htm";
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
            if(cond == 2)
            {
                qs.giveItems(NORMAL_FRAGMENT,1);
                qs.playSound(SOUND_MIDDLE);
                qs.setCond(3);
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
