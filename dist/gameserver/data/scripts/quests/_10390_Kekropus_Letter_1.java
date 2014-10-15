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
    // Quest's Conditions
    private static final int MIN_LEVEL = 40;
    private static final int MAX_LEVEL = 45;
    // Quest's NPCs
    private static final int BATHIS = 30332;
    private static final int GOSTA = 30916;
    private static final int ELI = 33858;
    // Quest's Items
    private static final int LETTER = 36706;
    private static final int SOE_GLUDIO = 20378;
    private static final int SOE_ALLIGATOR_ISLAND = 37025;
    private static final int SOE_HEINE = 37112;
    // Quest's Reward
    private static final int EXP = 370440;
    private static final int SP = 88;
    private static final int EWC = 951;
    private static final int EWC_COUNT = 3;
    private static final int STEEL_DOOR_GUILD_COIN = 37045;
    private static final int STEEL_DOOR_GUILD_COIN_COUNT = 21;


    private static final int QUEST_ID = 10390;
    private static final int QUEST_START_DELAY = 10000;
    private static final String LETTER_ALERT_STRING = "Kekropus có tin nhắn cho bạn.\nClick vào biểu tượng Question-Mark để xem.";
    private static final String NEXT_LETTER_ALERT_STRING = "Hãy tập luyện để trở nên mạnh mẽ hơn cho đến khi bạn nhận được lá thư kế tiếp ở level 46";

    public _10390_Kekropus_Letter_1()
    {
        super(false);
        this.addLevelCheck(MIN_LEVEL, MAX_LEVEL);
        this.addRaceCheck(true, true, true, true, true, true, false);
        CharListenerList.addGlobal(this);
        this.addTalkId(BATHIS);
        this.addTalkId(GOSTA);
        this.addTalkId(ELI);
        this.addQuestItem(LETTER, SOE_HEINE, SOE_ALLIGATOR_ISLAND);
    }

    private void receivedLetter(final QuestState qs)
    {
        if(qs != null)
        {
            Player player = qs.getPlayer();
            qs.showQuestionMark(_questId);
            qs.playSound(SOUND_TUTORIAL);
            if (player != null)
            {
                player.sendPacket(new ExShowScreenMessage(LETTER_ALERT_STRING, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
            }
        }
    }

    @Override
    public String onEvent(final String event, final QuestState qs, final NpcInstance npc)
    {
        String htmlText = event;
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
                        if(id == _questId)
                        {
                            if(player.getRace() != Race.ERTHEIA)
                            {
                                qs.giveItems(SOE_GLUDIO,1);
                                qs.showQuestHTML(qs.getQuest(),"0-message.htm");
                                qs.setState(STARTED);
                                qs.setCond(1);
                                qs.playSound(SOUND_ACCEPT);
                            }
                        }
                    }
                    catch (Throwable t)
                    {
                    }
                    htmlText = null;
                }
                else if(event.startsWith("TE"))
                {
                    htmlText = null;
                }
                else if(event.equalsIgnoreCase("Quest _10390_Kekropus_Letter_1 to_gludio"))
                {
                    player.teleToLocation(-13896, 123720, -3151);
                    player.sendPacket(TutorialCloseHtmlPacket.STATIC);
                    htmlText = null;
                }
                else if(event.equalsIgnoreCase("Quest _10390_Kekropus_Letter_1 close_window"))
                {
                    // 7123	Scroll of Escape: Town of Gludio
                    player.sendPacket(TutorialCloseHtmlPacket.STATIC);
                    htmlText = null;
                }
                else if(event.equalsIgnoreCase("Quest _10390_Kekropus_Letter_1 close_letter"))
                {
                    player.sendPacket(TutorialCloseHtmlPacket.STATIC);
                    htmlText = null;
                }
                else if(event.equalsIgnoreCase("start_quest_delay"))
                {
                    qs.startQuestTimer("start_quest_timeout", QUEST_START_DELAY);
                    htmlText = null;
                }
                else if(event.equalsIgnoreCase("start_quest") || event.equalsIgnoreCase("start_quest_timeout"))
                {
                    this.receivedLetter(qs);
                    htmlText = null;
                }
                else if(event.equalsIgnoreCase("read_letter"))
                {
                    qs.showQuestHTML(qs.getQuest(),"3.htm");
                    qs.takeAllItems(LETTER);
                    qs.unset("letter");
                    qs.setCond(2);
                    qs.playSound(SOUND_MIDDLE);
                    htmlText = null;
                }
                else if(event.equalsIgnoreCase("5.htm"))
                {
                    qs.setCond(3);
                    qs.playSound(SOUND_MIDDLE);
                    qs.giveItems(SOE_HEINE,1);
                }
                else if(event.equalsIgnoreCase("8.htm"))
                {
                    qs.giveItems(SOE_ALLIGATOR_ISLAND,1);
                    qs.setCond(4);
                    qs.playSound(SOUND_MIDDLE);
                }
                else if(event.equalsIgnoreCase("11.htm"))
                {
                    qs.setState(COMPLETED);
                    qs.exitCurrentQuest(false);
                    qs.playSound(SOUND_FINISH);
                    player.addExpAndSp(EXP,SP);
                    qs.giveItems(EWC,EWC_COUNT);
                    qs.giveItems(STEEL_DOOR_GUILD_COIN,STEEL_DOOR_GUILD_COIN_COUNT);
                    player.sendPacket(new ExShowScreenMessage(NEXT_LETTER_ALERT_STRING, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
                }
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
            int cond = qs.getCond();
            int npcId = npc.getNpcId();
            Player player = qs.getPlayer();
            if(player != null)
            {
                if(npcId == BATHIS)
                {
                    if(cond == 1)
                    {
                        if (player.getRace() == Race.HUMAN)
                        {
                            htmlText = "1-human.htm";
                            qs.giveItems(LETTER,1);
                            qs.setCond(2);
                            qs.set("letter","true");
                        }
                        else if (player.getRace() == Race.ELF)
                        {
                            htmlText = "1-elf.htm";
                            qs.giveItems(LETTER,1);
                            qs.setCond(2);
                            qs.set("letter","true");
                        }
                        else if (player.getRace() == Race.DARKELF)
                        {
                            htmlText = "1-dark-elf.htm";
                            qs.giveItems(LETTER,1);
                            qs.setCond(2);
                            qs.set("letter","true");
                        }
                        else if (player.getRace() == Race.DWARF)
                        {
                            htmlText = "1-dwarf.htm";
                            qs.giveItems(LETTER,1);
                            qs.setCond(2);
                            qs.set("letter","true");
                        }
                        else if (player.getRace() == Race.KAMAEL)
                        {
                            htmlText = "1-kamael.htm";
                            qs.giveItems(LETTER,1);
                            qs.set("letter","true");
                        }
                    }
                    else if(cond == 2)
                    {
                        String letter = qs.get("letter");
                        if (letter == null)
                        {
                            htmlText = "4.htm";
                        }
                        else
                        {
                            htmlText = "2.htm";
                        }
                    }
                }
                else if (npcId == GOSTA)
                {
                    if(cond == 3)
                    {
                        return "6.htm";
                    }
                    else if (cond == 4)
                    {
                        return "9.htm";
                    }
                }
                else if (npcId == ELI)
                {
                    if(cond == 4)
                    {
                        return "10.htm";
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
        if(player != null && checkStartCondition(player))
        {
            Quest quest = QuestManager.getQuest(this.getClass());
            player.processQuestEvent(quest.getName(),"start_quest",null);
        }
    }

    @Override
    public void onPlayerEnter(final Player player)
    {
        if (player != null && this.checkStartCondition(player))
        {
            Quest quest = QuestManager.getQuest(this.getClass());
            player.processQuestEvent(quest.getName(),"start_quest_delay",null);
        }
    }

    @Override
    public boolean checkStartCondition(final Player player)
    {
        boolean result = true;
        if(player.getLevel() < MIN_LEVEL || player.getLevel() > MAX_LEVEL)
        {
            result = false;
        }
        if(player.getRace() == Race.ERTHEIA)
        {
            result = false;
        }
        QuestState state = player.getQuestState(this.getClass().getSimpleName());
        if((state != null && state.getCond() > 0))
        {
            result = false;
        }
        return result;
    }
}
