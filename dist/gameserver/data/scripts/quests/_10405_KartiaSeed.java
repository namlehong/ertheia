package quests;

import l2s.gameserver.model.quest.Quest;
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
    // Quest's Items
    private static final int KARTIA_MUTATE_SEED = 36714;
    private static final int KARTIA_MUTATE_SEED_COUNT = 50;
    private static final int KARTIA_MUTATE_SEED_CHANCE = 75;
    // Quest's Reward
    private static final int EXP = 6251174;
    private static final int SP = 1500;
    private static final int EWA = 730;
    private static final int EWA_COUNT = 5;
    private static final int STEEL_DOOR_GUILD_COIN = 37045;
    private static final int STEEL_DOOR_GUILD_COIN_COUNT = 57;

    public _10405_KartiaSeed()
    {
        super(false);
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
