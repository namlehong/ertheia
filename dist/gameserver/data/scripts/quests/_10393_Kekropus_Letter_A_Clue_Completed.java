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
 * Created by Archer on 9/5/2014.
 * Project ertheia
 */
public class _10393_Kekropus_Letter_A_Clue_Completed extends Quest implements ScriptFile, OnPlayerEnterListener, OnLevelChangeListener
{
    private static final int MIN_LEVEL = 46;
    private static final int MAX_LEVEL = 51;
    // Quest's NPCs
    private static final int FLUTTER = 30677;
    private static final int KELIOS = 33862;
    // Quest's Items
    private static final int SOE_OUTLAW_FOREST = 37026;
    private static final int SOE_OREN = 37133;
    // Quest's Rewards
    private static final int EXP = 483840;
    private static final int SP = 116;
    private static final int EAC = 22011;
    private static final int EAC_COUNT = 4;
    private static final int STEEL_DOOR_GUILD_COIN = 37045;
    private static final int STEEL_DOOR_GUILD_COIN_COUNT = 15;

    private static final int QUEST_START_DELAY = 10000;
    private static final String LETTER_ALERT_STRING = "Kekropus có tin nhắn cho bạn.\nClick vào biểu tượng Question-Mark để xem.";
    private static final String NEXT_LETTER_ALERT_STRING = "Hãy tập luyện để trở nên mạnh mẽ hơn cho đến khi bạn nhận được lá thư kế tiếp ở level 52";

    public _10393_Kekropus_Letter_A_Clue_Completed()
    {
        super(false);
        this.addLevelCheck(MIN_LEVEL,MAX_LEVEL);
        this.addRaceCheck(true,true,true,true,true,true,false);
        CharListenerList.addGlobal(this);
        this.addStartNpc(FLUTTER);
        this.addTalkId(KELIOS);
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
        return (player.getLevel() >= MIN_LEVEL && player.getLevel() <= MAX_LEVEL && player.getRace() != Race.ERTHEIA && player.getQuestState(_10393_Kekropus_Letter_A_Clue_Completed.class.getSimpleName()) == null);
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
                                qs.showQuestHTML(qs.getQuest(),"00-letter.htm");
                                qs.setState(STARTED);
                                qs.setCond(1);
                                qs.playSound(SOUND_ACCEPT);
                                htmlText = null;
                            }
                        }
                    }
                    catch (Throwable t)
                    {
                    }
                }
                else if(event.equalsIgnoreCase("Quest _10393_Kekropus_Letter_A_Clue_Completed to_hunter"))
                {
                    player.teleToLocation(-13896, 123720, -3151);
                    player.sendPacket(TutorialCloseHtmlPacket.STATIC);
                    htmlText = null;
                }
                else if(event.equalsIgnoreCase("Quest _10393_Kekropus_Letter_A_Clue_Completed close_window"))
                {
                    qs.giveItems(SOE_OREN,1);
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
                    qs.giveItems(SOE_OUTLAW_FOREST,1);
                    htmlText = "03.htm";
                }
                else if(event.equalsIgnoreCase("get_reward"))
                {
                    qs.setState(COMPLETED);
                    qs.exitCurrentQuest(false);
                    qs.playSound(SOUND_FINISH);
                    qs.addExpAndSp(EXP,SP);
                    qs.giveItems(EAC,EAC_COUNT);
                    qs.giveItems(STEEL_DOOR_GUILD_COIN,STEEL_DOOR_GUILD_COIN_COUNT);
                    htmlText = "06.htm";
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
            int npcId = npc.getNpcId();
            int cond = qs.getCond();
            Player player = qs.getPlayer();
            if(player != null)
            {
                if(npcId == FLUTTER)
                {
                    if(cond == 1)
                    {
                        htmlText = "01.htm";
                    }
                    else if(cond == 2)
                    {
                        htmlText = "04.htm";
                    }
                }
                else if(npcId == KELIOS)
                {
                    if(cond == 2)
                    {
                        htmlText = "05.htm";
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
            Quest quest = QuestManager.getQuest(_10393_Kekropus_Letter_A_Clue_Completed.class);
            player.processQuestEvent(quest.getName(),"start_quest",null);
        }
    }

    @Override
    public void onPlayerEnter(final Player player)
    {
        if (player != null && this.checkStartCondition(player))
        {
            Quest quest = QuestManager.getQuest(_10393_Kekropus_Letter_A_Clue_Completed.class);
            player.processQuestEvent(quest.getName(),"start_quest_delay",null);
        }
    }
}
