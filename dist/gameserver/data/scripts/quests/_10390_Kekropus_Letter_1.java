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
    private static final int BATHIS = 30332;
    private static final int GOSTA = 30916;
    private static final int ELI = 33858;

    private static final int EWC = 951;
    private static final int STEEL_DOOR_GUILD_COIN = 37045;
    private static final int SOE_GLUDIO = 7123;
    private static final int SOE_ALLIGATOR_ISLAND = 37025;

    private static final int MIN_LEVEL = 40;
    private static final int MAX_LEVEL = 45;
    private static final int QUEST_START_DELAY = 10000;
    private static final String LETTER_ALERT_STRING = "Kekropus có tin nhắn cho bạn.\nClick vào biểu tượng Question-Mark để xem.";
    private static final String NEXT_LETTER_ALERT_STRING = "";

    public _10390_Kekropus_Letter_1()
    {
        super(false);
        this.addLevelCheck(MIN_LEVEL, MAX_LEVEL);
        this.addRaceCheck(true, true, true, true, true, true, false);
        CharListenerList.addGlobal(this);
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
//                player.setVar("@received_kekropus_letter_1", true);
            }
        }
    }

    @Override
    public String onEvent(final String event, final QuestState qs, final NpcInstance npc)
    {
        if(event != null && qs != null)
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
                else if(event.equalsIgnoreCase("Quest _10390_Kekropus_Letter_1 to_gludio"))
                {
                    player.teleToLocation(-13896, 123720, -3151);
                    player.sendPacket(TutorialCloseHtmlPacket.STATIC);
                    return null;
                }
                else if(event.equalsIgnoreCase("Quest _10390_Kekropus_Letter_1 close_window"))
                {
                    // 7123	Scroll of Escape: Town of Gludio
                    qs.giveItems(SOE_GLUDIO,1);
                    player.sendPacket(TutorialCloseHtmlPacket.STATIC);
                    return null;
                }
                else if(event.equalsIgnoreCase("Quest _10390_Kekropus_Letter_1 close_letter"))
                {
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
                else if(event.equalsIgnoreCase("read_letter"))
                {
                    qs.showQuestHTML(qs.getQuest(),"3.htm");
                    qs.setState(3);
                    qs.playSound(SOUND_MIDDLE);
                }
                else if(event.equalsIgnoreCase("5.htm"))
                {
                    qs.setCond(4);
                    qs.playSound(SOUND_MIDDLE);
                }
            }
        }
        return event;
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
                if(npcId == BATHIS)
                {
                    if(cond == 1)
                    {
                        if(player.getRace() == Race.HUMAN)
                        {
                            htmlText = "1-human.htm";
                            qs.setCond(2);
                        }
                        else if(player.getRace() == Race.ELF)
                        {
                            htmlText = "1-elf.htm";
                            qs.setCond(2);
                        }
                        else if(player.getRace() == Race.DARKELF)
                        {
                            htmlText = "1-dark-elf.htm";
                            qs.setCond(2);
                        }
                        else if(player.getRace() == Race.DWARF)
                        {
                            htmlText = "1-dwarf.htm";
                            qs.setCond(2);
                        }
                        else if(player.getRace() == Race.KAMAEL)
                        {
                            htmlText = "1-kamael.htm";
                            qs.setCond(2);
                        }
                    }
                    else if(cond == 2)
                    {
                        htmlText = "2.htm";
                    }
                    else if(cond == 3)
                    {
                        htmlText = "4.htm";
                    }
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
