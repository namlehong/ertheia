package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * Created by Kiet Thanh Vo on 10:07 AM 9/6/2014.
 * Project: ertheia
 */
public class _10398_A_Suspicious_Badge extends Quest implements ScriptFile
{
    private static final int MIN_LEVEL = 52;
    private static final int MAX_LEVEL = 58;
    // Quest's NPCs
    private static final int ANDY = 33845;
    private static final int BACON = 33846;
    // Quest's Monsters
    private static final int GIANT_FUNGUS = 20555;
    private static final int ROTTING_TREE = 20558;
    private static final int CORRODED_SKELETON = 23305;
    private static final int ROTTEN_CORPSE = 23306;
    private static final int CORPSE_SPIDER = 23307;
    private static final int EXPLOSIVE_SPIDER = 23308;
    // Quest's Items
    private static final int UNIDENTIFIED_SUSPICIOUS_BADGE = 36666;
    private static final int UNIDENTIFIED_SUSPICIOUS_BADGE_COUNT = 20;
    private static final int UNIDENTIFIED_SUSPICIOUS_BADGE_CHANGE = 50;
    // Quest's Reward
    private static final int EXP = 3811500;
    private static final int SP = 914;
    private static final int EAB = 948;
    private static final int EAB_COUNT = 5;
    private static final int STEEL_DOOR_GUILD_COIN = 37045;
    private static final int STEEL_DOOR_GUILD_COIN_COUNT = 36;

    public _10398_A_Suspicious_Badge()
    {
        super(false);
        this.addLevelCheck(MIN_LEVEL, MAX_LEVEL);
        this.addRaceCheck(true, true, true, true, true, true, false);
        this.addStartNpc(ANDY);
        this.addTalkId(BACON);
        this.addQuestItem(UNIDENTIFIED_SUSPICIOUS_BADGE);
        this.addKillId(GIANT_FUNGUS, ROTTING_TREE, CORRODED_SKELETON, ROTTEN_CORPSE, CORPSE_SPIDER, EXPLOSIVE_SPIDER);
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
                qs.takeAllItems(UNIDENTIFIED_SUSPICIOUS_BADGE);
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
            if (player != null)
            {
                if(qs.isCompleted())
                {
                    htmlText = "completed-quest.htm";
                }
                else
                {
                    if(npcId == ANDY)
                    {
                        if(cond == 0)
                        {
                            htmlText = "00.htm";
                        }
                    }
                    else if(npcId == BACON)
                    {
                        if(cond == 2)
                        {
                            htmlText = "03.htm";
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
                if(qs.getQuestItemsCount(UNIDENTIFIED_SUSPICIOUS_BADGE) < UNIDENTIFIED_SUSPICIOUS_BADGE_COUNT && Rnd.chance(UNIDENTIFIED_SUSPICIOUS_BADGE_CHANGE))
                {
                    qs.giveItems(UNIDENTIFIED_SUSPICIOUS_BADGE,1);
                    qs.playSound(SOUND_ITEMGET);
                }
                if(qs.getQuestItemsCount(UNIDENTIFIED_SUSPICIOUS_BADGE) >= UNIDENTIFIED_SUSPICIOUS_BADGE_COUNT)
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
