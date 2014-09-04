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
public class _10392_FailureAndItsConsequences extends Quest implements ScriptFile
{
    private static final int IASON_HEINE = 33859;
    private static final int SWAMP_TRIBE = 20991;
    private static final int SWAMP_ALLIGATOR = 20992;
    private static final int SWAMP_WARRIOR = 20993;
    private static final int EAC = 22011;
    private static final int STEEL_DOOR_GUILD_COIN = 37045;

    public _10392_FailureAndItsConsequences()
    {
        super(false);
        this.addStartNpc(IASON_HEINE);
        this.addKillId(SWAMP_TRIBE,SWAMP_ALLIGATOR,SWAMP_WARRIOR);
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
                htmlText = "4.htm";
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
                if (npcId == IASON_HEINE)
                {
                    if (!qs.isCompleted())
                    {
                        if(checkStartCondition(player))
                        {
                            htmlText = "start.htm";
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
