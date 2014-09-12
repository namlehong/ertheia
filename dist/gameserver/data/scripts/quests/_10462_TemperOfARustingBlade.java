package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * Created by Archer on 9/5/2014.
 * Project ertheia
 */
public class _10462_TemperOfARustingBlade extends Quest implements ScriptFile
{
    private static final int MIN_LEVEL = 46;
    private static final int MAX_LEVEL = 52;
    // Quest's NPCs
    private static final int FLUTTER = 30677;
    // Quest's Items
    private static final int AUGMENTATION_PRACTICE_WEAPON = 36717;
    private static final int AUGMENTATION_PRACTICE_LIFE_STONE = 36718;
    private static final int AUGMENTATION_PRACTICE_GEMSTONE =36719;
    private static final int AUGMENTATION_PRACTICE_GEMSTONE_COUNT = 20;
    // Quest's RewardE
    private static final int EXP = 504210;
    private static final int SP = 121;
    private static final int GEMSTONE_D = 2130;
    private static final int GEMSTONE_D_COUNT = 20;
    private static final int LIFE_STONE_46 = 8723;

    public _10462_TemperOfARustingBlade()
    {
        super(false);
        this.addLevelCheck(MIN_LEVEL, MAX_LEVEL);
        this.addStartNpc(FLUTTER);
        this.addQuestItem(AUGMENTATION_PRACTICE_WEAPON, AUGMENTATION_PRACTICE_LIFE_STONE, AUGMENTATION_PRACTICE_GEMSTONE);
    }

    @Override
    public String onEvent(final String event, final QuestState qs, final NpcInstance npc)
    {
        String htmlString = event;
        return htmlString;
    }

    @Override
    public String onTalk(final NpcInstance npc, final QuestState qs)
    {
        String htmlString = NO_QUEST_DIALOG;
        return htmlString;
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
