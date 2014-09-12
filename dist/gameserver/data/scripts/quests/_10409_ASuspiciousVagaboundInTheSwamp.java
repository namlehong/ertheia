package quests;

import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.scripts.ScriptFile;

/**
 * Created by Archer on 9/9/2014.
 * Project ertheia
 */
public class _10409_ASuspiciousVagaboundInTheSwamp extends Quest implements ScriptFile
{
    private static final int MIN_LEVEL = 65;
    private static final int MAX_LEVEL = 70;

    public _10409_ASuspiciousVagaboundInTheSwamp()
    {
        super(false);
        this.addLevelCheck(MIN_LEVEL, MAX_LEVEL);
        this.addRaceCheck(true, true, true, true, true, true, false);

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
