package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.TutorialShowHtmlPacket;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.scripts.ScriptFile;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;

public class _10323_TrainLikeItsReal extends Quest implements ScriptFile
{
	private NpcInstance npcInstance = null;
	private final static int SHENON = 32974;
	private final static int EVAIN = 33464;
	private final static int HOLDEN = 33194;
	private final static int SOLDER = 33014;
    private final static int TRAINING_GOLEM = 27532;

	private static final int[] SOLDER_START_POINT = {-110808, 253896, -1817};

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

	public _10323_TrainLikeItsReal()
	{
		super(false);
		addStartNpc(EVAIN);
		addTalkId(SHENON);
		addTalkId(HOLDEN);
		addKillId(TRAINING_GOLEM);
		addLevelCheck(1, 20);
		addQuestCompletedCheck(_10322_SearchingForTheMysteriousPower.class);
		addRaceCheck(true, true, true, true, true, true, false);
	}

	private void spawnSolder(QuestState st)
	{
		npcInstance = NpcUtils.spawnSingle(SOLDER, Location.findPointToStay(SOLDER_START_POINT[0], SOLDER_START_POINT[1], SOLDER_START_POINT[2], 50, 100, st.getPlayer().getGeoIndex()));
		npcInstance.setFollowTarget(st.getPlayer());
		Functions.npcSay(npcInstance, NpcString.S1_COME_WITH_ME_I_WILL_LEAD_YOU_TO_HOLDEN, st.getPlayer().getName());
	}

	private void despawnSolder()
	{
		if(npcInstance != null)
        {
            npcInstance.deleteMe();
        }
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
        if(event != null && st != null)
        {
            Player player = st.getPlayer();
            if (event.equalsIgnoreCase("2.htm"))
            {
                st.setState(STARTED);
                st.setCond(1);
                spawnSolder(st);
                st.playSound(SOUND_ACCEPT);
            }
            else if (event.equalsIgnoreCase("6.htm"))
            {
                st.setCond(2);
                st.playSound(SOUND_MIDDLE);
            }
            else if (event.equalsIgnoreCase("8.htm"))
            {
                st.playSound(SOUND_MIDDLE);
                if(player != null)
                {
                    player.sendPacket(new TutorialShowHtmlPacket(TutorialShowHtmlPacket.LARGE_WINDOW, "..\\L2Text\\QT_003_bullet_02.htm"));
                    player.sendPacket(new ExShowScreenMessage(NpcString.SPIRITSHOT_HAVE_BEEN_ADDED_TO_YOUR_INVENTORY, 4500, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
                    st.giveItems(5790, 500);
                    player.sendPacket(new ExShowScreenMessage(NpcString.AUTOMATE_SOULSHOT_AS_SHOWN_IN_THE_TUTORIAL, 4500, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
                    st.setCond(5);
                }
            }
            else if (event.equalsIgnoreCase("10.htm"))
            {
                st.playSound(SOUND_MIDDLE);
                if(player != null)
                {
                    player.sendPacket(new TutorialShowHtmlPacket(TutorialShowHtmlPacket.LARGE_WINDOW, "..\\L2Text\\QT_003_bullet_01.htm"));
                    player.sendPacket(new ExShowScreenMessage(NpcString.SOULSHOT_HAVE_BEEN_ADDED_TO_YOUR_INVENTORY, 4500, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
                    st.giveItems(5789, 500);
                    player.sendPacket(new ExShowScreenMessage(NpcString.AUTOMATE_SPIRITSHOT_AS_SHOWN_IN_THE_TUTORIAL, 4500, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
                    st.setCond(4);
                }
            }
            else if (event.equalsIgnoreCase("15.htm"))
            {
                if(player != null)
                {
                    player.addExpAndSp(300, 1500);
                }
                st.giveItems(57, 9000);
                st.setState(COMPLETED);
                st.exitCurrentQuest(false);
                st.playSound(SOUND_FINISH);
            }
        }
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) {
        String htmlText = "noquest";
        if(st != null && npc != null)
        {
            int cond = st.getCond();
            int npcId = npc.getNpcId();
            if (npcId == EVAIN)
            {
                QuestState qs = st.getPlayer().getQuestState(_10322_SearchingForTheMysteriousPower.class);
                if (qs == null || !qs.isCompleted())
                {
                    return "noqu.htm";
                }
                if (st.isCompleted())
                {
                    return "3.htm";
                }
                if (st.getPlayer().getLevel() > 20)
                {
                    return "Your Level is too high for this quest!";
                }
                else if (cond == 0)
                {
                    htmlText = "start.htm";
                }
            }
            else if (npcId == SHENON)
            {
                if (st.isCompleted())
                {
                    return "3.htm";
                }
                else if (cond == 9)
                {
                    htmlText = "14.htm";
                }
            }
            else if (npcId == HOLDEN)
            {
                if (cond == 1)
                {
                    htmlText = "4.htm";
                    despawnSolder();
                }
                else if (cond == 3)
                {
                    Player player = st.getPlayer();
                    if(player != null)
                    {
                        if (!player.isMageClass() || player.getRace() == Race.ORC)
                        {
                            htmlText = "9.htm";
                        }
                        else
                        {
                            htmlText = "7.htm";
                        }
                    }
                }
                else if (cond == 4 || cond == 5)
                {
                    htmlText = "11.htm";
                    st.setCond(st.getCond() + 2);
                }
                else if (cond == 8)
                {
                    htmlText = "12.htm";
                    st.playSound(SOUND_MIDDLE);
                    st.setCond(9);
                }
            }
        }
		return htmlText;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int killed = st.getInt("killed");
		if(npcId == TRAINING_GOLEM && (st.getCond() == 2 || st.getCond() == 6 || st.getCond() == 7))
		{
			if(killed > 3)
			{
                if(st.getCond() == 2)
                {
                    st.setCond(st.getCond() + 1);
                }
                else
                {
                    st.setCond(8);
                }
				st.unset("killed");
				st.playSound(SOUND_MIDDLE);
			}
			else
            {
                st.set("killed", killed + 1);
            }
		}
		return null;
	}
}