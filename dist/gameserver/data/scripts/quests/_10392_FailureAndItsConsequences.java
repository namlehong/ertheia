package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * Created by Archer on 9/4/2014.
 * Project ertheia
 */
public class _10392_FailureAndItsConsequences extends Quest implements ScriptFile
{
    private static final int MIN_LEVEL = 40;
    private static final int MAX_LEVEL = 46;
    // Quest's NPCs
    private static final int IASON_HEINE = 33859;
    private static final int ELI = 33858;
    // Quest's Monsters
    private static final int SWAMP_TRIBE = 20991;
    private static final int SWAMP_ALLIGATOR = 20992;
    private static final int SWAMP_WARRIOR = 20993;
    // Quest's Items
    private static final int SUSPICIOUS_FRAGMENT = 17586;
    private static final int SUSPICIOUS_FRAGMENT_COUNT = 30;
    private static final int SUSPICIOUS_FRAGMENT_CHANCE = 50;
    // Quest's Reward
    private static final int EXP = 2329740;
    private static final int SP = 559;
    private static final int EAC = 22011;
    private static final int EAC_COUNT = 5;
    private static final int STEEL_DOOR_GUILD_COIN = 37045;
    private static final int STEEL_DOOR_GUILD_COIN_COUNT = 17;

    public _10392_FailureAndItsConsequences()
    {
        super(false);
        this.addRaceCheck(true, true, true, true, true, true, false);
        this.addStartNpc(IASON_HEINE);
        this.addTalkId(ELI);
        this.addLevelCheck(MIN_LEVEL, MAX_LEVEL);
        this.addKillId(SWAMP_TRIBE, SWAMP_ALLIGATOR, SWAMP_WARRIOR);
        this.addQuestItem(SUSPICIOUS_FRAGMENT);
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

    @Override
    public String onEvent(final String event, final QuestState qs, final NpcInstance npc)
    {
        String htmlText = event;
        if(event != null)
        {
            if(event.equalsIgnoreCase("quest_accept"))
            {
                qs.setState(STARTED);
                qs.setCond(1);
                qs.playSound(SOUND_ACCEPT);
                htmlText = "3.htm";
            }
            else if(event.equalsIgnoreCase("give_fragment"))
            {
                qs.setCond(3);
                qs.playSound(SOUND_MIDDLE);
                htmlText = "6.htm";
            }
            else if(event.equalsIgnoreCase("get_reward"))
            {
                qs.takeAllItems(SUSPICIOUS_FRAGMENT);
                qs.addExpAndSp(EXP,SP);
                qs.giveItems(EAC,EAC_COUNT);
                qs.giveItems(STEEL_DOOR_GUILD_COIN,STEEL_DOOR_GUILD_COIN_COUNT);
                qs.setState(COMPLETED);
                qs.exitCurrentQuest(false);
                qs.playSound(SOUND_FINISH);
                htmlText = "11.htm";
            }
        }
        return htmlText;
    }

    @Override
    public String onTalk(final NpcInstance npc, final QuestState qs)
    {
        String htmlText = "noquest";
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
                    if (npcId == IASON_HEINE)
                    {
                        if (cond == 0)
                        {
                            if(checkStartCondition(player))
                            {
                                htmlText = "start.htm";
                            }
                        }
                        else if(cond == 1)
                        {
                            htmlText = "4.htm";
                        }
                        else if(cond == 2)
                        {
                            htmlText = "5.htm";
                        }
                        else if(cond == 3)
                        {
                            htmlText = "7.htm";
                        }
                    }
                    else if(npcId == ELI)
                    {
                        if(cond == 3)
                        {
                            htmlText = "8.htm";
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
            int cond =  qs.getCond();
            if(cond == 1)
            {
                if(qs.getQuestItemsCount(SUSPICIOUS_FRAGMENT) < SUSPICIOUS_FRAGMENT_COUNT && Rnd.chance(SUSPICIOUS_FRAGMENT_CHANCE))
                {
                    qs.giveItems(SUSPICIOUS_FRAGMENT,1);
                    qs.playSound(SOUND_ITEMGET);
                }
                if(qs.getQuestItemsCount(SUSPICIOUS_FRAGMENT) >= SUSPICIOUS_FRAGMENT_COUNT)
                {
                    qs.setCond(2);
                }
            }
        }
        return null;
    }
}
