package quests;

import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;
import l2s.gameserver.utils.Location;

/**
 * Created by Archer on 9/9/2014.
 * Project ertheia
 */
public class _10710_LifeEnergyRepository extends Quest implements ScriptFile
{
    private static final int MIN_LEVEL = 61;
    private static final int MAX_LEVEL = 65;
    // Quest's NPCs
    private static final int SHUVANN = 33867;
    private static final int LIFE_ENERGY_REPOSITORY = 33962;
    // Quest's Monster
    private static final int EMBRYO = 27521;
    private static final int EMBRYO_COUNT = 2;
    // Quest's Items
    private static final int SHINE_STONE_FRAGMENT = 39512;
    // Quest's Reward
    private static final int EXP = 3125586;
    private static final int SP = 750;
    private static final int EAA = 730;
    private static final int EAA_COUNT = 2;
    private static final int STEEL_DOOR_GUILD_COIN = 37045;
    private static final int STEEL_DOOR_GUILD_COIN_COUNT = 19;

    public _10710_LifeEnergyRepository()
    {
        super(false);
        this.addLevelCheck(MIN_LEVEL, MAX_LEVEL);
        this.addRaceCheck(true, true, true, true, true, true, false);
        this.addQuestCompletedCheck(_10406_BeforeDarknessBearFruit.class);
        this.addStartNpc(SHUVANN);
        this.addTalkId(LIFE_ENERGY_REPOSITORY);
        this.addQuestItem(SHINE_STONE_FRAGMENT);
        this.addKillId(EMBRYO);
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
            else if(event.equalsIgnoreCase("destroy_crystal"))
            {
                qs.setCond(2);
                qs.giveItems(SHINE_STONE_FRAGMENT,1);
                qs.playSound(SOUND_MIDDLE);
                Player player = qs.getPlayer();
                if(player != null)
                {
                    for(int i = 0; i < EMBRYO_COUNT; i++)
                    {
                        Location loc = Location.findAroundPosition(player.getLoc(),100, player.getGeoIndex());
                        NpcInstance mob = qs.addSpawn(EMBRYO, loc.getX(),loc.getY(), loc.getZ());
                        mob.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, player, 100000);
                    }
                }
                htmlText = null;
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
                            htmlText = "05.htm";
                        }
                    }
                    else if(npcId == LIFE_ENERGY_REPOSITORY)
                    {
                        if(cond == 1)
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
