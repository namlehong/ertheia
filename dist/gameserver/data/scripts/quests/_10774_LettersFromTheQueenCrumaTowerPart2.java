package quests;

import l2s.gameserver.instancemanager.QuestManager;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.items.PcInventory;
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

public class _10774_LettersFromTheQueenCrumaTowerPart2 extends Quest implements ScriptFile, OnPlayerEnterListener, OnLevelChangeListener
{

	private static final int SYLVAIN = 30070;
	private static final int ROMBEL = 30487;
	
	private static final int SOE_DION = 39595;
	private static final int SOE_CRUMA = 39596;
	private static final int STEEL_DOOR_COIN = 37045;
	private static final int SCROLL_EWC = 951;
	private static final int SCROLL_EAC = 952;
	
	private static final int minLevel = 46;
	private static final int maxLevel = 52;
	
	private static final String LETTER_ALERT_STRING = "Bạn vừa nhận được thư từ Nữ Hoàng Navari.";
	private static final String NEXT_LETTER_STRING = "Hãy cố gắng ở đây và tập luyện tới level 52.\nNữ Hoàng Navari sẽ gửi bức thư tiếp theo";
	
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
	
	public _10774_LettersFromTheQueenCrumaTowerPart2()
	{
		super(false);

		CharListenerList.addGlobal(this);
		
		addTalkId(SYLVAIN, ROMBEL);

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
				Quest q = QuestManager.getQuest(10774);
				player.processQuestEvent(q.getName(), "start_quest", null);
			}
			
			htmltext = "";
		}
		
		if(event.equalsIgnoreCase("start_quest") || event.equalsIgnoreCase("start_quest_7s"))
		{
			st.setCond(1);
			st.setState(STARTED);
			alertLetterReceived(st);
			st.showQuestHTML(st.getQuest(), "queen_letter.htm");
			
			htmltext = "";
		}
		
		if(event.equalsIgnoreCase("start_quest_delay"))
		{
			st.startQuestTimer("start_quest_7s", 7000);
			//only start quest after 7s to avoid crash on enterworld
			htmltext = "";
		}
		
		if(event.equalsIgnoreCase("Quest _10774_LettersFromTheQueenCrumaTowerPart2 to_dion"))
		{
			//System.out.println("in Quest _10774_LettersFromTheQueenCrumaTowerPart2 to_dion");
			if(st.getCond() == 1)
			{
				if(getItemCountById(player, SOE_DION) > 0)
				{
					st.takeItems(SOE_DION, 1);
					player.teleToLocation(16376, 142296, -2718);
					player.sendPacket(TutorialCloseHtmlPacket.STATIC);
				}
				else
				{
					player.sendMessage("Không tìm thấy Scroll of Escape: Town of Dion");
				}
			}
			htmltext = "";
		}
		//System.out.println("out " + event);
		if(event.equalsIgnoreCase("Quest _10774_LettersFromTheQueenCrumaTowerPart2 close_window"))
		{
			//System.out.println("in Quest _10774_LettersFromTheQueenCrumaTowerPart2 close_window");
			player.sendPacket(TutorialCloseHtmlPacket.STATIC);
			htmltext = "";
		}
		
		// Question mark clicked
		if(event.startsWith("QM"))
		{
			int MarkId = Integer.valueOf(event.substring(2));
			//System.out.println("Mark id " + MarkId);
			if(MarkId == 10774)
			{
				if(player.getRace() == Race.ERTHEIA)
					st.showQuestHTML(st.getQuest(), "queen_letter.htm");
				htmltext = "";
			}
		}

		if(event.equalsIgnoreCase("30070-3.htm"))
		{
			st.giveItems(SOE_CRUMA, 1);
			st.setCond(2);
			
			htmltext = "30070-3.htm";
		}
		
		if(event.equalsIgnoreCase("30487-3.htm"))
		{
			st.giveItems(STEEL_DOOR_COIN, 46);
			st.giveItems(SCROLL_EAC, 9);
			st.addExpAndSp(4443600, 1066);
			st.setState(COMPLETED);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
			st.getPlayer().sendPacket(new ExShowScreenMessage(NEXT_LETTER_STRING, 7000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
			
			htmltext = "30487-3.htm";
		}
		
		if(html.isEmpty())
			return null;
		else
			return htmltext;
	}


	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == SYLVAIN)
		{
			if(cond == 1)
			{
				htmltext = "30070-1.htm";
			}
			else if(cond == 2)
				htmltext = "30070-3.htm";
		}
		else if(npcId == ROMBEL && st.getCond() == 2)
		{
			htmltext = "30487-1.htm";
		}
		
		return htmltext;
	}
	
	@Override
	public void onPlayerEnter(Player player)
	{
		if(player.getVarBoolean("@received_navari_letter_5th"))
			return;
		
		System.out.println("Player enter");
		if(checkStartCondition(player))
		{
			System.out.println("Player enter and fit quest condition");
			Quest q = QuestManager.getQuest(10774);
			player.processQuestEvent(q.getName(), "start_quest_delay", null);
		}
		
	}

	@Override
	public void onLevelChange(Player player, int oldLvl, int newLvl)
	{
		//System.out.println("level change oldLvl " + oldLvl + " newLvl " + newLvl + "checkStartCondition " + checkStartCondition(player));
		if(oldLvl < minLevel && newLvl >= minLevel && checkStartCondition(player))
		{
			//System.out.println("received_navari_letter_5th " + player.getVarBoolean("@received_navari_letter_5th"));
			if(player.getVarBoolean("@received_navari_letter_5th"))
				return;

			Quest q = QuestManager.getQuest(10774);
			player.processQuestEvent(q.getName(), "start_quest", null);
			
		}
	}
	
	private void alertLetterReceived(QuestState st)
	{
		if(st == null) return;
		
		st.getPlayer().sendPacket(new ExShowScreenMessage(LETTER_ALERT_STRING, 7000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
		
		st.showQuestionMark(10774);
		
		st.playSound(SOUND_TUTORIAL);
		
		st.giveItems(SOE_DION, 1);
		
		st.getPlayer().setVar("@received_navari_letter_5th", true);
	}
	
	private long getItemCountById(Player player, int itemId)
	{
		long itemCount = 0;
		
		PcInventory inventory = player.getInventory();
		
		if(inventory!= null)
		{
			ItemInstance itemInstance = inventory.getItemByItemId(itemId);

			if(itemInstance!= null)
				itemCount = itemInstance.getCount();
		}
		
		return itemCount;
	}

	@Override
	public boolean checkStartCondition(Player player)
	{
		return (player.getLevel() >= minLevel && player.getLevel() <= maxLevel && player.getRace() == Race.ERTHEIA && player.getQuestState("_10774_LettersFromTheQueenCrumaTowerPart2") == null);
	}

}