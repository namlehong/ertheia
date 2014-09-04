package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * Created by Archer on 9/4/2014.
 * Project ertheia
 */
public class _10460_ReturnOfTheAlligatorHunter extends Quest implements ScriptFile
{
    // Quest's Conditions
    private static final int MIN_LEVEL = 40;
    private static final int MAX_LEVEL = 46;
    // Quest's NPCs
    private static final int ENRON = 33860;
    // Quest's Monsters
    private static final int ALLIGATOR = 20135;
    private static final int CROKIAN_LAD = 20804;
    private static final int DAILAON_LAD = 20805;
    private static final int CROKIAN_LAD_WARRIOR = 20806;
    private static final int FARHITE_LAD = 20807;
    private static final int NOS_LAD = 20808;
    // Quest's Items
    private static final int ALLIGATOR_LEATHER = 4337;
    private static final int BLUE_ALLIGATOR_LEATHER = 4338;
    private static final int BEJEWELED_ALLIGATOR_LEATHER = 4339;
    // Quest's Reward
    private static final int EXP = 2795688;
    private static final int SP = 670;
    private static final int EAC = 22011;
    private static final int EAC_COUNT = 7;
    private static final int STEEL_DOOR_GUILD_COIN = 37045;
    private static final int STEEL_DOOR_GUILD_COIN_COUNT = 26;

    public _10460_ReturnOfTheAlligatorHunter()
    {
        super(false);
        this.addStartNpc(ENRON);
        this.addLevelCheck(MIN_LEVEL, MAX_LEVEL);
        this.addKillId(ALLIGATOR, CROKIAN_LAD, DAILAON_LAD, CROKIAN_LAD_WARRIOR, FARHITE_LAD, NOS_LAD);
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
            else if(event.equalsIgnoreCase("get_reward"))
            {
                qs.takeAllItems(ALLIGATOR_LEATHER);
                qs.takeAllItems(BLUE_ALLIGATOR_LEATHER);
                qs.takeAllItems(BEJEWELED_ALLIGATOR_LEATHER);
                qs.addExpAndSp(EXP,SP);
                qs.giveItems(EAC,EAC_COUNT);
                qs.giveItems(STEEL_DOOR_GUILD_COIN,STEEL_DOOR_GUILD_COIN_COUNT);
                qs.setState(COMPLETED);
                qs.exitCurrentQuest(false);
                qs.playSound(SOUND_FINISH);
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
                    if (npcId == ENRON)
                    {
                        if(cond == 0)
                        {
                            if (checkStartCondition(player))
                            {
                                htmlText = "start.htm";
                            }
                        }
                        else if (cond == 1)
                        {
                                htmlText = "4.htm";
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
}