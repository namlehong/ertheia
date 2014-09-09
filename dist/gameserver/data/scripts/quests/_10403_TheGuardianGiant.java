package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * Created by Archer on 9/8/2014.
 * Project ertheia
 */
public class _10403_TheGuardianGiant extends Quest implements ScriptFile
{
    private static final int MIN_LEVEL = 58;
    private static final int MAX_LEVEL = 61;
    // Quest's NPCs
    private static final int NOVAIN = 33866;
    // Quest's Monster
    private static final int KRANROT = 20650;
    private static final int PALIOTE = 20648;
    private static final int YINTZU = 20647;
    private static final int HAMRUT = 20649;
    private static final int GUARDIAN_GIANT_AKUM = 27504;
    // Quest's Items
    private static final int GUARDIAN_GIANT_NUCLEUS_FRAGMENT = 36713;
    private static final int GUARDIAN_GIANT_NUCLEUS_FRAGMENT_COUNT = 50;
    private static final int GUARDIAN_GIANT_NUCLEUS_FRAGMENT_CHANCE = 75;
    // Quest's Reward
    private static final int EXP = 6579090;
    private static final int SP = 1578;
    private static final int EAB = 948;
    private static final int EAB_COUNT = 5;
    private static final int STEEL_DOOR_GUILD_COIN = 37045;
    private static final int STEEL_DOOR_GUILD_COIN_COUNT = 40;

    public _10403_TheGuardianGiant()
    {
        super(false);
        this.addLevelCheck(MIN_LEVEL, MAX_LEVEL);
        this.addRaceCheck(true, true, true, true, true, true, false);
        this.addStartNpc(NOVAIN);
        this.addQuestItem(GUARDIAN_GIANT_NUCLEUS_FRAGMENT);
        this.addKillId(KRANROT, PALIOTE, YINTZU, HAMRUT, GUARDIAN_GIANT_AKUM);
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
                qs.takeAllItems(GUARDIAN_GIANT_NUCLEUS_FRAGMENT);
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
                    if(npcId == NOVAIN)
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
                if(qs.getQuestItemsCount(GUARDIAN_GIANT_NUCLEUS_FRAGMENT) < GUARDIAN_GIANT_NUCLEUS_FRAGMENT_COUNT && Rnd.chance(GUARDIAN_GIANT_NUCLEUS_FRAGMENT_CHANCE))
                {
                    qs.giveItems(GUARDIAN_GIANT_NUCLEUS_FRAGMENT,1);
                    qs.playSound(SOUND_ITEMGET);
                }
                if(qs.getQuestItemsCount(GUARDIAN_GIANT_NUCLEUS_FRAGMENT) >= GUARDIAN_GIANT_NUCLEUS_FRAGMENT_COUNT)
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
