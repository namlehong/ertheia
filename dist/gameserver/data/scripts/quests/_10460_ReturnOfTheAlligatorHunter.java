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
public class _10460_ReturnOfTheAlligatorHunter extends Quest implements ScriptFile
{
    private static final int MIN_LEVEL = 40;
    private static final int MAX_LEVEL = 46;
    private static final int ENRON = 33860;
    private static final int ALLIGATOR = 20135;
    private static final int CROKIAN_LAD = 20804;
    private static final int DAILAON_LAD = 20805;
    private static final int CROKIAN_LAD_WARRIOR = 20806;
    private static final int FARHITE_LAD = 20807;
    private static final int NOS_LAD = 20808;
    private static final int ALLIGATOR_LEATHER = 4337;
    private static final int BLUE_ALLIGATOR_LEATHER = 4338;
    private static final int BEJEWELED_ALLIGATOR_LEATHER = 4339;
    private static final int EAC = 22011;
    private static final int STEEL_DOOR_GUILD_COIN = 37045;

    public _10460_ReturnOfTheAlligatorHunter()
    {
        super(false);
        this.addStartNpc(ENRON);
        this.addLevelCheck(MIN_LEVEL, MAX_LEVEL);
        this.addKillId(ALLIGATOR, CROKIAN_LAD, DAILAON_LAD, CROKIAN_LAD_WARRIOR, FARHITE_LAD, NOS_LAD);
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
                htmlText = "3.htm";
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
                if(npcId == ENRON)
                {
                    if (!qs.isCompleted())
                    {
                        if (checkStartCondition(player))
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
