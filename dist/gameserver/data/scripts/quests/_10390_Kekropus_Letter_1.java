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
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.TutorialCloseHtmlPacket;
import l2s.gameserver.scripts.ScriptFile;

/**
 * Created by Archer on 9/3/2014.
 * Project ertheia
 */
public class _10390_Kekropus_Letter_1 extends Quest implements ScriptFile, OnPlayerEnterListener, OnLevelChangeListener
{
    private static final int QUEST_ID = 10390;
    private static final int RAINS = 30288;  // Human Warrior
    private static final int TOBIAS = 30297;  // Dark Elf
    private static final int DRIKUS = 30505;  // Orc
    private static final int MENDIO = 30504;  // Dwarf
    private static final int GERSHWIN = 32196;  // Kamael
    private static final int ELLENIA = 30155;  // Elf Mage
    private static final int BATHIS = 30332;
    private static final int STEEL_DOOR_COIN = 37045;
    private static final int MIN_LEVEL = 40;
    private static final int MAX_LEVEL = 45;
    private static final int QUEST_START_DELAY = 10000;
    private static final String LETTER_ALERT_STRING = "Kekropus có tin nhắn cho bạn. Click vào biểu tượng Question-Mark để xem.";
    private static final String NEXT_LETTER_ALERT_STRING = "";

    public _10390_Kekropus_Letter_1()
    {
        super(false);
        this.addLevelCheck(MIN_LEVEL, MAX_LEVEL);
        this.addRaceCheck(true, true, true, true, true, true, false);
        CharListenerList.addGlobal(this);
        this.addTalkId(RAINS);
        this.addTalkId(TOBIAS);
        this.addTalkId(DRIKUS);
        this.addTalkId(MENDIO);
        this.addTalkId(GERSHWIN);
        this.addTalkId(ELLENIA);
        this.addTalkId(BATHIS);
    }

    private void receivedLetter(final QuestState qs)
    {
        if(qs != null)
        {
            qs.showQuestionMark(QUEST_ID);
            qs.playSound(SOUND_TUTORIAL);
            Player player = qs.getPlayer();
            if(player != null)
            {
                player.sendPacket(new ExShowScreenMessage(LETTER_ALERT_STRING, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
                player.setVar("@received_kekropus_letter_1", true);
            }
        }
    }

    @Override
    public String onEvent(final String event, final QuestState qs, final NpcInstance npc)
    {
        if(event != null && qs != null && npc != null)
        {
            Player player = qs.getPlayer();
            if (player != null)
            {
                if(event.startsWith("QM"))
                {
                    try
                    {
                        int id = Integer.valueOf(event.substring(2));
                        if(id == QUEST_ID)
                        {
                            if(player.getRace() != Race.ERTHEIA)
                            {
                                qs.showQuestHTML(qs.getQuest(),"0-question-mark.htm");
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
                    player.teleToLocation(-79592, 150824, -3066);
                    player.sendPacket(TutorialCloseHtmlPacket.STATIC);
                    return null;
                }
                else if(event.equalsIgnoreCase("close_window"))
                {
                    // 7123	Scroll of Escape: Town of Gludio
                    qs.giveItems(7123,1);
                    player.sendPacket(TutorialCloseHtmlPacket.STATIC);
                    return null;
                }
                else if(event.equalsIgnoreCase("start_quest_delay"))
                {
                    qs.startQuestTimer("start_quest_timeout", QUEST_START_DELAY);
                    return null;
                }
                else if(event.equalsIgnoreCase("start_quest") || event.equalsIgnoreCase("start_quest_timeout"))
                {
                    qs.setState(STARTED);
                    qs.setCond(1);
                    qs.playSound(SOUND_ACCEPT);
                    this.receivedLetter(qs);
                    return null;
                }
            }
        }
        return super.onEvent(event, qs, npc);
    }

    @Override
    public String onTalk(final NpcInstance npc, final QuestState qs)
    {
        String htmlText = "noquest";
        if(npc != null && qs != null)
        {
            int cond = qs.getCond();
            int npcId = npc.getNpcId();
            Player player = qs.getPlayer();
            if(player != null)
            {
                if (player.getRace() == Race.HUMAN && npcId == RAINS)
                {
                    if(cond == 1)
                    {
                        htmlText = "1-human.htm";
                        qs.setCond(2);
                    }
                    else if(cond == 2)
                    {

                    }
                }
                else if(player.getRace() == Race.ELF && npcId == ELLENIA)
                {
                    if(cond == 1)
                    {
                        htmlText = "1-elf.htm";
                        qs.setCond(2);
                    }
                    else if(cond == 2)
                    {

                    }
                }
                else if(player.getRace() == Race.DARKELF && npcId == TOBIAS)
                {
                    if(cond == 1)
                    {
                        htmlText = "1-dark-elf.htm";
                        qs.setCond(2);
                    }
                    else if(cond == 2)
                    {

                    }
                }
                else if(player.getRace() == Race.DWARF && npcId == MENDIO)
                {
                    if(cond == 1)
                    {
                        htmlText = "1-dwarf.htm";
                        qs.setCond(2);
                    }
                    else if(cond == 2)
                    {

                    }
                }
                else if(player.getRace() == Race.KAMAEL && npcId == GERSHWIN)
                {
                    if(cond == 1)
                    {
                        htmlText = "1-kamael.htm";
                        qs.setState(2);
                    }
                    else if(cond == 2)
                    {

                    }
                }
                else if(npcId == BATHIS)
                {

                }
            }
        }
        return htmlText;
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
            player.processQuestEvent(quest.getName(),"start_quest",null);
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
