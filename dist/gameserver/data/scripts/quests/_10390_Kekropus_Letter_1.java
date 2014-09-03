package quests;

import l2s.gameserver.instancemanager.QuestManager;
import l2s.gameserver.listener.actor.player.OnLevelChangeListener;
import l2s.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.s2c.TutorialCloseHtmlPacket;
import l2s.gameserver.scripts.ScriptFile;

/**
 * Created by Archer on 9/3/2014.
 * Project ertheia
 */
public class _10390_Kekropus_Letter_1 extends Quest implements ScriptFile, OnPlayerEnterListener, OnLevelChangeListener
{
    private static final int RAINS = 30288;  // Human Warrior
    private static final int RAYMOND = 30289;  // Human Mage
    private static final int TOBIAS = 30297;  // Dark Elf
    private static final int DRIKUS = 30505;  // Orc
    private static final int MENDIO = 30504;  // Dwarf
    private static final int GERSHWIN = 32196;  // Kamael
    private static final int ELLENIA = 30155;  // Elf Mage
    private static final int ESRANDELL = 30158;  // Elf Warrior
    private static final int BATHIS = 30332;

    private static final int STEEL_DOOR_COIN = 37045;
    private static final int MIN_LEVEL = 40;
    private static final int MAX_LEVEL = 45;

    public _10390_Kekropus_Letter_1()
    {
        super(false);
        this.addLevelCheck(MIN_LEVEL, MAX_LEVEL);
        this.addRaceCheck(true,true,true,true,true,true,false);
        CharListenerList.addGlobal(this);
        this.addTalkId(RAINS);
        this.addTalkId(RAYMOND);
        this.addTalkId(TOBIAS);
        this.addTalkId(DRIKUS);
        this.addTalkId(MENDIO);
        this.addTalkId(GERSHWIN);
        this.addTalkId(ELLENIA);
        this.addTalkId(ESRANDELL);
        this.addTalkId(BATHIS);
    }

    @Override
    public String onEvent(final String event, final QuestState qs, final NpcInstance npc)
    {
        if(event != null && qs != null && npc != null)
        {
            Player player = qs.getPlayer();
            if (player != null)
            {
                if(event.startsWith("UC"))
                {
                    if(checkStartCondition(player))
                    {
                        Quest current = QuestManager.getQuest(_10390_Kekropus_Letter_1.class);
                        player.processQuestEvent(current.getName(),"start_quest",null);
                    }
                    return null;
                }
                else if(event.startsWith("QM"))
                {
                    try
                    {
                        int id = Integer.valueOf(event.substring(2));
                        if(id == 10390)
                        {
                            if(player.getRace() != Race.ERTHEIA)
                            {
                                qs.showQuestHTML(qs.getQuest(),"kekropus_letter");
                                return null;
                            }
                        }
                    }
                    catch (Throwable t)
                    {
                    }
                }
                else if(event.equalsIgnoreCase("to_gludio"))
                {

                }
                else if(event.equalsIgnoreCase("close_window"))
                {
                    player.sendPacket(TutorialCloseHtmlPacket.STATIC);
                    return null;
                }
            }
        }
        return super.onEvent(event, qs, npc);
    }

    @Override
    public String onTalk(final NpcInstance npc, final QuestState qs)
    {
        return super.onTalk(npc, qs);
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
    public void onLevelChange(final Player player, final int oldLvl, final int newLvl)
    {
        if(player != null && !player.getVarBoolean("@received_kekropus_letter_1") && checkStartCondition(player))
        {
            Quest quest = QuestManager.getQuest(_10390_Kekropus_Letter_1.class);
            player.processQuestEvent(quest.getName(),"start_quest_delay",null);
        }
    }

    @Override
    public void onPlayerEnter(final Player player)
    {
        if (player != null && !player.getVarBoolean("@received_kekropus_letter_1") && this.checkStartCondition(player))
        {
            Quest quest = QuestManager.getQuest(_10390_Kekropus_Letter_1.class);
            player.processQuestEvent(quest.getName(),"start_quest_delay",null);
        }
    }
}
