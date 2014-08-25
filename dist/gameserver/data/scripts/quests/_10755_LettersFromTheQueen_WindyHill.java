package quests;

import l2s.gameserver.instancemanager.QuestManager;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.listener.actor.player.OnLevelChangeListener;
import l2s.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.s2c.ExCallToChangeClass;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.TutorialCloseHtmlPacket;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author Hien Son
 */

public class _10755_LettersFromTheQueen_WindyHill extends Quest implements ScriptFile, OnPlayerEnterListener, OnLevelChangeListener
{

	private static final int LEVIAN = 30037;
	private static final int PIO = 33963;
	
	private static final int SOE_GLUDIN = 39491;
	private static final int SOE_WINDY = 39492;
	private static final int STEEL_DOOR_COIN = 37045;
	
	private static final int minLevel = 20;
	private static final int maxLevel = 99;
	
	private static final String LETTER_ALERT_STRING = "Bạn vừa nhận được thư từ Nữ Hoàng Navari.";
	private static final String NEXT_LETTER_STRING = "Hãy cố gắng ở đây và tập luyện tới level 30.\nVà Nữ Hoàng Navari sẽ gửi bức thư tiếp theo";
	
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
	
	public _10755_LettersFromTheQueen_WindyHill()
	{
		super(false);

		CharListenerList.addGlobal(this);
		
		addTalkId(LEVIAN, PIO);

		addLevelCheck(minLevel, maxLevel);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		if(player == null)
			return null;

		String htmltext = event;

		//System.out.println("quest event " + event.toString());
		
		int classId = player.getClassId().getId();
		if(event.startsWith("UC"))
		{
			if(checkStartCondition(player))
			{
				Quest q = QuestManager.getQuest(10755);
				player.processQuestEvent(q.getName(), "start_quest", null);
			}
			
			return null;
		}
		
		if(event.equalsIgnoreCase("start_quest") || event.equalsIgnoreCase("start_quest_7s"))
		{
			st.setCond(1);
			st.setState(STARTED);
			alertLetterReceived(st);
			st.showQuestHTML(st.getQuest(), "queen_letter.htm");
			
			return null;
		}
		
		if(event.equalsIgnoreCase("start_quest_delay"))
		{
			st.startQuestTimer("start_quest_7s", 7000);
			//only start quest after 7s to avoid crash on enterworld
			return null;
		}
		
		if(event.equalsIgnoreCase("Quest _10755_LettersFromTheQueen_WindyHill to_gludin"))
		{
			if(st.getCond() == 1)
			{
				if(getItemCountById(player, SOE_GLUDIN) > 0)
				{
					st.takeItems(SOE_GLUDIN, 1);
					player.teleToLocation(-79592, 150824, -3066);
					player.sendPacket(TutorialCloseHtmlPacket.STATIC);
				}
				else
				{
					player.sendMessage("Không tìm thấy Scroll of Escape: Gludin Village");
				}
			}
			return null;
		}
		
		if(event.equalsIgnoreCase("Quest _10755_LettersFromTheQueen_WindyHill close_window"))
		{
			player.sendPacket(TutorialCloseHtmlPacket.STATIC);
			return null;
		}
		
		// Question mark clicked
		if(event.startsWith("QM"))
		{
			int MarkId = Integer.valueOf(event.substring(2));
			//System.out.println("Mark id " + MarkId);
			if(MarkId == 107551)
			{
				if(player.getRace() == Race.ERTHEIA)
					st.showQuestHTML(st.getQuest(), "queen_letter.htm");
				return null;
			}
		}

		if(event.equalsIgnoreCase("30037-2.htm"))
		{
			st.giveItems(SOE_WINDY, 1);
			st.setCond(2);
			
			htmltext = "30037-2.htm";
		}
		
		if(event.equalsIgnoreCase("33963-3.htm"))
		{
			st.giveItems(STEEL_DOOR_COIN, 5);
			st.addExpAndSp(120960, 29);
			st.setState(COMPLETED);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
			st.getPlayer().sendPacket(new ExShowScreenMessage(NEXT_LETTER_STRING, 7000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
			
			htmltext = "33963-3.htm";
		}
		
		return htmltext;
	}


	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == LEVIAN)
		{
			if(cond == 1)
			{
				htmltext = "30037-1.htm";
			}
			else if(cond == 1)
				htmltext = "30037-2.htm";
		}
		else if(npcId == PIO && st.getCond() == 2)
		{
			htmltext = "33963-1.htm";
		}
		
		return htmltext;
	}
	
	@Override
	public void onPlayerEnter(Player player)
	{
		
		//System.out.println("Player enter");
		if(checkStartCondition(player))
		{
			//System.out.println("Player enter and fit quest condition");
			Quest q = QuestManager.getQuest(10755);
			player.processQuestEvent(q.getName(), "start_quest_delay", null);
		}
		
	}

	@Override
	public void onLevelChange(Player player, int oldLvl, int newLvl)
	{
		//System.out.println("level change oldLvl " + oldLvl + " newLvl " + newLvl + "checkStartCondition " + checkStartCondition(player));
		if(oldLvl < 20 && newLvl >= 20 && checkStartCondition(player))
		{
			//System.out.println("received_navari_letter_1st " + player.getVarBoolean("@received_navari_letter_1st"));
			if(player.getVarBoolean("@received_navari_letter_1st"))
				return;

			Quest q = QuestManager.getQuest(10755);
			player.processQuestEvent(q.getName(), "start_quest", null);
			
		}
	}
	
	private void alertLetterReceived(QuestState st)
	{
		if(st == null) return;
		
		st.getPlayer().sendPacket(new ExShowScreenMessage(LETTER_ALERT_STRING, 7000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
		
		st.showQuestionMark(107551);
		
		st.playSound(SOUND_TUTORIAL);
		
		st.giveItems(SOE_GLUDIN, 1);
		
		st.getPlayer().setVar("@received_navari_letter_1st", true);
	}
	

	@Override
	public boolean checkStartCondition(Player player)
	{
		return (player.getLevel() >= minLevel && player.getLevel() <= maxLevel && player.getRace() == Race.ERTHEIA && player.getQuestState("_10755_LettersFromTheQueen_WindyHill") == null);
	}

}