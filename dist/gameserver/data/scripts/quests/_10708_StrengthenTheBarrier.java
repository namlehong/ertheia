package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * Created by Kiet Thanh Vo on 2:32 PM 9/7/2014.
 * Project: ertheia
 */
public class _10708_StrengthenTheBarrier extends Quest implements ScriptFile
{
    private static final int MIN_LEVEL = 53;
    private static final int MAX_LEVEL = 58;
    // Quest's NPCs
    private static final int BACON = 33846;
    private static final int BARRIER_ENFORCER = 33960;
    // Quest's Items
    private static final int BARRIER_ENFORCER_KEY = 39509;
    // Quest's Reward
    private static final int EXP = 635250;
    private static final int SP = 152;
    private static final int EWB = 947;
    private static final int EWB_COUNT = 1;
    private static final int STEEL_DOOR_GUILD_COIN = 37045;
    private static final int STEEL_DOOR_GUILD_COIN_COUNT = 17;

    public _10708_StrengthenTheBarrier()
    {
        super(false);
        this.addLevelCheck(MIN_LEVEL,MAX_LEVEL);
        this.addStartNpc(BACON);
        this.addTalkId(BARRIER_ENFORCER);
        this.addQuestItem(BARRIER_ENFORCER_KEY);
        this.addQuestCompletedCheck(_10399_TheAlphabetOfTheGiants.class);
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
                qs.giveItems(BARRIER_ENFORCER_KEY,1);
                htmlText = "02.htm";
            }
            else if(event.equalsIgnoreCase("use_key"))
            {
                qs.takeAllItems(BARRIER_ENFORCER_KEY);
                qs.setCond(2);
                qs.playSound(SOUND_MIDDLE);
                htmlText = "04.htm";
            }
            else if(event.equalsIgnoreCase("get_reward"))
            {
                qs.takeAllItems(BARRIER_ENFORCER_KEY);
                qs.addExpAndSp(EXP,SP);
                qs.giveItems(EWB,EWB_COUNT);
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
            if(player != null)
            {
                if(qs.isCompleted())
                {
                    htmlText = "completed-quest.htm";
                }
                else
                {
                    if(npcId == BACON)
                    {
                        if(cond == 0)
                        {
                            return "00.htm";
                        }
                        else if(cond == 2)
                        {
                            return "05.htm";
                        }
                    }
                    else if(npcId == BARRIER_ENFORCER)
                    {
                        if(cond == 1)
                        {
                            return "03.htm";
                        }
                    }
                }
            }
        }
        return htmlText;
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
