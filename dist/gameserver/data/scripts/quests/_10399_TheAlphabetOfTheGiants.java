package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * Created by Kiet Thanh Vo on 1:16 PM 9/6/2014.
 * Project: ertheia
 */
public class _10399_TheAlphabetOfTheGiants extends Quest implements ScriptFile
{
    private static final int MIN_LEVEL = 53;
    private static final int MAX_LEVEL = 58;
    // Quest's NPCs
    private static final int BACON = 33846;
    // Quest's Monster
    private static final int CORPSE_LOOTER_STAKATO = 23309;
    private static final int LESSER_LAIKEL = 23310;
    // Quest's Items
    private static final int GIANT_ALPHABET = 36667;
    private static final int GIANT_ALPHABET_COUNT = 20;
    private static final int GIANT_ALPHABET_CHANCE = 50;
    // Quest's Reward
    private static final int EXP = 3811500;
    private static final int SP = 914;
    private static final int EAB = 948;
    private static final int EAB_COUNT = 5;
    private static final int STEEL_DOOR_GUILD_COIN = 37045;
    private static final int STEEL_DOOR_GUILD_COIN_COUNT = 37;

    public _10399_TheAlphabetOfTheGiants()
    {
        super(false);
        this.addLevelCheck(MIN_LEVEL,MAX_LEVEL);
        this.addRaceCheck(true, true, true, true, true, true, false);
        this.addStartNpc(BACON);
        this.addKillId(CORPSE_LOOTER_STAKATO,LESSER_LAIKEL);
        this.addQuestItem(GIANT_ALPHABET);
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
            else if(event.equalsIgnoreCase("get_reward"))
            {
                qs.takeAllItems(GIANT_ALPHABET);
                qs.addExpAndSp(EXP,SP);
                qs.giveItems(EAB,EAB_COUNT);
                qs.giveItems(STEEL_DOOR_GUILD_COIN,STEEL_DOOR_GUILD_COIN_COUNT);
                qs.setState(COMPLETED);
                qs.exitCurrentQuest(false);
                qs.playSound(SOUND_FINISH);
                htmlText = "04.htm";
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
                            return "03.htm";
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
                if(qs.getQuestItemsCount(GIANT_ALPHABET) < GIANT_ALPHABET_COUNT && Rnd.chance(GIANT_ALPHABET_CHANCE))
                {
                    qs.giveItems(GIANT_ALPHABET,1);
                    qs.playSound(SOUND_ITEMGET);
                }
                if(qs.getQuestItemsCount(GIANT_ALPHABET) >= GIANT_ALPHABET_COUNT)
                {
                    qs.setCond(2);
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
