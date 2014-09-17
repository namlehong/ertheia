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
 * Created by Archer on 9/9/2014.
 * Project ertheia
 */
    public class _10408_KekropusLetterTheSwampOfScreams extends Quest implements ScriptFile, OnPlayerEnterListener, OnLevelChangeListener
{
    private static final int MIN_LEVEL = 65;
    private static final int MAX_LEVEL = 69;
    // Quest's NPCs
    private static final int MATHIAS = 31340;
    private static final int DOKARA = 33847;
    // Quest's Items
    private static final int SOE_RUNE = 37117;
    private static final int SOE_SWAMP_OF_SCREAM = 37030;
    // Quest's Reward
    private static final int EXP = 942690;
    private static final int SP = 226;
    private static final int EWA = 729;
    private static final int EWA_COUNT = 2;
    private static final int STEEL_DOOR_GUILD_COIN = 37045;
    private static final int STEEL_DOOR_GUILD_COIN_COUNT = 91;

    private static final int QUEST_START_DELAY = 10000;
    private static final String LETTER_ALERT_STRING = "Kekropus có tin nhắn cho bạn.\nClick vào biểu tượng Question-Mark để xem.";
    private static final String NEXT_LETTER_ALERT_STRING = "Hãy tập luyện để trở nên mạnh mẽ hơn cho đến khi bạn nhận được lá thư kế tiếp ở level 70";

    public _10408_KekropusLetterTheSwampOfScreams()
    {
        super(false);
        this.addLevelCheck(MIN_LEVEL, MAX_LEVEL);
        this.addRaceCheck(true, true, true, true, true, true, false);
        CharListenerList.addGlobal(this);
        this.addTalkId(MATHIAS,DOKARA);
        this.addQuestItem(SOE_RUNE, SOE_SWAMP_OF_SCREAM);
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

    @Override
    public String onEvent(final String event, final QuestState qs, final NpcInstance npc)
    {
        String htmlText = event;
        if(event != null && qs != null)
        {
            Player player = qs.getPlayer();
            if(player != null)
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
                                qs.giveItems(SOE_RUNE,1);
                                qs.showQuestHTML(qs.getQuest(),"00.htm");
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
                else if(event.equalsIgnoreCase("Quest _10408_KekropusLetterTheSwampOfScreams to_rune"))
                {
                    player.teleToLocation(147554, 24648, -2017);
                    player.sendPacket(TutorialCloseHtmlPacket.STATIC);
                    htmlText = null;
                }
                else if(event.equalsIgnoreCase("Quest _10408_KekropusLetterTheSwampOfScreams close_window"))
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
                else if(event.equalsIgnoreCase("accept_task"))
                {
                    qs.setCond(2);
                    qs.playSound(SOUND_MIDDLE);
                    qs.giveItems(SOE_SWAMP_OF_SCREAM,1);
                    htmlText = "03.htm";
                }
                else if(event.equalsIgnoreCase("get_reward"))
                {
                    qs.setState(COMPLETED);
                    qs.exitCurrentQuest(false);
                    qs.playSound(SOUND_FINISH);
                    qs.takeAllItems(SOE_RUNE, SOE_SWAMP_OF_SCREAM);
                    qs.addExpAndSp(EXP,SP);
                    qs.giveItems(EWA,EWA_COUNT);
                    qs.giveItems(STEEL_DOOR_GUILD_COIN,STEEL_DOOR_GUILD_COIN_COUNT);
                    htmlText = "05.htm";
                    player.sendPacket(new ExShowScreenMessage(NEXT_LETTER_ALERT_STRING, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
                }
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
            if(player != null)
            {
                if(npcId == MATHIAS)
                {
                    if(cond == 1)
                    {
                        htmlText = "01.htm";
                    }
                }
                else if(npcId == DOKARA)
                {
                    if(cond == 2)
                    {
                        htmlText = "04.htm";
                    }
                }
            }
        }
        return htmlText;
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
