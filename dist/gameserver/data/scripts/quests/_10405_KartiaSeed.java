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
public class _10405_KartiaSeed extends Quest implements ScriptFile
{
    private static final int MIN_LEVEL = 61;
    private static final int MAX_LEVEL = 65;
    // Quest's NPCs
    private static final int SHUVANN = 33867;
    // Quest's Monsters
    private static final int ARCHER_OF_DESTRUCTION = 21001;
    private static final int DOOM_SCOUT = 21002;
    private static final int GRAVEYARD_LICH = 21003;
    private static final int DISMAL_POLE = 21004;
    private static final int GRAVEYARD_PREDATOR	= 21005;
    private static final int DOOM_SERVANT = 21006;
    private static final int DOOM_GUARD	= 21007;
    private static final int DOOM_ARCHER = 21008;
    private static final int DOOM_TROOPER = 21009;
    private static final int DOOM_WARRIOR = 21010;
    private static final int DOOM_KNIGHT = 19341;
    private static final int SPITEFUL_SOUL_LEADER = 20974;
    private static final int SPITEFUL_SOUL_WIZARD = 20975;
    private static final int SPITEFUL_SOUL_WARRIOR = 20976;
    // Quest's Items
    private static final int KARTIA_MUTATE_SEED = 36714;
    private static final int KARTIA_MUTATE_SEED_COUNT = 50;
    private static final int KARTIA_MUTATE_SEED_CHANCE = 75;
    // Quest's Reward
    private static final int EXP = 6251174;
    private static final int SP = 1500;
    private static final int EAA = 730;
    private static final int EAA_COUNT = 5;
    private static final int STEEL_DOOR_GUILD_COIN = 37045;
    private static final int STEEL_DOOR_GUILD_COIN_COUNT = 57;

    public _10405_KartiaSeed()
    {
        super(false);
        this.addLevelCheck(MIN_LEVEL, MAX_LEVEL);
        this.addRaceCheck(true, true, true, true, true, true, false);
        this.addStartNpc(SHUVANN);
        this.addQuestItem(KARTIA_MUTATE_SEED);
        this.addKillId(ARCHER_OF_DESTRUCTION, DOOM_SCOUT, GRAVEYARD_LICH, DISMAL_POLE, GRAVEYARD_PREDATOR, DOOM_SERVANT
                , DOOM_GUARD, DOOM_ARCHER, DOOM_TROOPER, DOOM_WARRIOR, DOOM_KNIGHT, SPITEFUL_SOUL_LEADER
                ,SPITEFUL_SOUL_WIZARD,SPITEFUL_SOUL_WARRIOR);
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
                qs.takeAllItems(KARTIA_MUTATE_SEED);
                qs.giveItems(EAA,EAA_COUNT);
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
                    if(npcId == SHUVANN)
                    {
                        if(cond == 0)
                        {
                            htmlText = "00.htm";
                        }
                        else if(cond == 1)
                        {
                            htmlText = "04.htm";
                        }
                        else if(cond == 2)
                        {
                            htmlText = "05.htm";
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
                if(qs.getQuestItemsCount(KARTIA_MUTATE_SEED) < KARTIA_MUTATE_SEED_COUNT && Rnd.chance(KARTIA_MUTATE_SEED_CHANCE))
                {
                    qs.giveItems(KARTIA_MUTATE_SEED,1);
                    qs.playSound(SOUND_ITEMGET);
                }
                if(qs.getQuestItemsCount(KARTIA_MUTATE_SEED) >= KARTIA_MUTATE_SEED_COUNT)
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
