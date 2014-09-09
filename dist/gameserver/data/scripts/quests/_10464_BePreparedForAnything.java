package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * Created by Kiet Thanh Vo on 5:08 PM 9/7/2014.
 * Project: ertheia
 */
public class _10464_BePreparedForAnything extends Quest implements ScriptFile
{
    private static final int MIN_LEVEL = 58;
    private static final int MAX_LEVEL = 65;
    // Quest's NPCs
    private static final int PATERSON = 33864;
    private static final int OLF_KANORE = 32610;
    // Quest's Items
    private static final int PRACTICE_LEATHER_BELT = 36724;
    private static final int PRACTICE_MAGIC_PIN_C = 36725;
    private static final int FLUTTER_MAGIC_PIN_LEATHER_BELT = 36726;
    // Quest's Reward
    private static final int EXP = 781410;
    private static final int SP = 187;
    private static final int CLOTH_BELT = 13894;

    public _10464_BePreparedForAnything()
    {
        super(false);
        this.addLevelCheck(MIN_LEVEL, MAX_LEVEL);
        this.addRaceCheck(true, true, true, true, true, true, false);
        this.addStartNpc(PATERSON);
        this.addTalkId(OLF_KANORE);
        this.addQuestItem(PRACTICE_LEATHER_BELT, PRACTICE_MAGIC_PIN_C, FLUTTER_MAGIC_PIN_LEATHER_BELT);
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
                htmlText = "02.htm";
            }
            else if(event.equalsIgnoreCase("task_accept"))
            {
                qs.giveItems(PRACTICE_MAGIC_PIN_C,1);
                qs.playSound(SOUND_MIDDLE);
                qs.setCond(2);
                htmlText = "08.htm";
            }
            else if(event.equalsIgnoreCase("task_complete"))
            {
                qs.setCond(3);
                qs.playSound(SOUND_MIDDLE);
                htmlText = "11.htm";
            }
            else if(event.equalsIgnoreCase("get_reward"))
            {
                qs.takeAllItems(PRACTICE_LEATHER_BELT, PRACTICE_MAGIC_PIN_C, FLUTTER_MAGIC_PIN_LEATHER_BELT);
                qs.addExpAndSp(EXP, SP);
                qs.giveItems(CLOTH_BELT,1);
                qs.setState(COMPLETED);
                qs.exitCurrentQuest(false);
                qs.playSound(SOUND_FINISH);
                htmlText = "13.htm";
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
                    if(npcId == PATERSON)
                    {
                        if(cond == 0)
                        {
                            htmlText = "00.htm";
                        }
                        else if(cond == 1)
                        {
                            htmlText = "04.htm";
                        }
                        else if(cond == 3)
                        {
                            htmlText = "12.htm";
                        }
                    }
                    else if (npcId == OLF_KANORE)
                    {
                        if(cond == 1)
                        {
                            htmlText = "05.htm";
                        }
                        else if(cond == 2)
                        {
                            if(qs.getQuestItemsCount(FLUTTER_MAGIC_PIN_LEATHER_BELT) >= 1)
                            {
                                htmlText = "10.htm";
                            }
                            else
                            {
                                htmlText = "09.htm";
                            }
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
