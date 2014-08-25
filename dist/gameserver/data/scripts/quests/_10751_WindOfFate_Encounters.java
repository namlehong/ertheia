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

public class _10751_WindOfFate_Encounters extends Quest implements ScriptFile, OnPlayerEnterListener, OnLevelChangeListener
{

	private static final int NAVARI = 33931;
	private static final int KATALIN = 33943;
	private static final int RAYMOND = 33943;
	private static final int TELESHA_CORPSE = 33981;
	private static final int MYSTERIOUS_WIZARD = 33980;

	private static final int NAVARI_BOX_MARAUDER = 40266;
	private static final int WIND_SPIRIT_RELIC = 39535;

	private static final int SKELETON_WARRIOR = 27528;
	private static final int SKELETON_ARCHER = 27529;
	
	private static final int minLevel = 38;
	private static final int maxLevel = 99;
	
	private static final String LETTER_ALERT_STRING = "Bạn vừa nhận được thư từ Nữ Hoàng Navari";
	private static final String CHECK_TELESHA_CORPSE = "Kiểm tra xác của Telesha";
	private static final String TALK_TO_WIZARD = "Hãy nói chuyện với Mysterious Wizard";
	private static final String RETURN_GLUDIO = "Hãy trở về thị trấn Gludio";

	private static final String SKELETON_KILL_LIST = "skeleton_kill_list";
	
	NpcInstance telesha_corpse_instance = null;
	NpcInstance mysterious_wizard_instance = null;
	
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
	
	/*
	 cond 1: accept quest, teleport to Faeron, talk to Navari
	 cond 2: talk to Katalin
	 cond 3: talk to Raymond
	 cond 4: kill skeleton
	 cond 4: spawn Telesha corpse, check corpse
	 cond 5: tak to Mysterious Wizard, get Wind Relic
	 cond 6: talk to Raymond
	 cond 7: talk to Katalin, change class
	 */
	
	public _10751_WindOfFate_Encounters()
	{
		super(false);

		CharListenerList.addGlobal(this);
		
		addTalkId(NAVARI, KATALIN, RAYMOND, TELESHA_CORPSE, MYSTERIOUS_WIZARD);
		
		addKillNpcWithLog(4, SKELETON_KILL_LIST, 5, SKELETON_WARRIOR, SKELETON_ARCHER);
		
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
		
		if(event.equalsIgnoreCase("Quest _10751_WindOfFate_Encounters to_faeron"))
		{
			if(st.getCond() == 1)
			{
				player.teleToLocation(-80565, 251763, -3080);
				player.sendPacket(TutorialCloseHtmlPacket.STATIC);
				
			}
			return null;
		}
		
		if(event.equalsIgnoreCase("Quest _10751_WindOfFate_Encounters close_window"))
		{
			player.sendPacket(TutorialCloseHtmlPacket.STATIC);
			return null;
		}
		
		// Question mark clicked
		if(event.startsWith("QM"))
		{
			int MarkId = Integer.valueOf(event.substring(2));
			//System.out.println("Mark id " + MarkId);
			if(MarkId == 10751)
			{
				if(player.getRace() == Race.ERTHEIA)
					st.showQuestHTML(st.getQuest(), "queen_letter.htm");
				return null;
			}
		}
		
		if(event.equalsIgnoreCase("33931-2.htm"))
		{
			st.setCond(1);
		}
		
		if(event.equalsIgnoreCase("33943-2.htm"))
		{
			st.setCond(2);
		}
		
		if(event.equalsIgnoreCase("30289-3.htm"))
		{
			st.giveItems(WIND_SPIRIT_RELIC, 1);
			st.setCond(3);
		}
		
		if(event.equalsIgnoreCase("check_body"))
		{
			if(telesha_corpse_instance != null)
			{
				//remove telesha corpse
				telesha_corpse_instance.deleteMe();
				//spawn Mysterious Wizard and despawn after 3 minutes
				mysterious_wizard_instance = st.addSpawn(MYSTERIOUS_WIZARD, player.getLoc().getX() + 100, player.getLoc().getY(), player.getLoc().getZ(), 180000);
				
				player.sendPacket(new ExShowScreenMessage(TALK_TO_WIZARD, 7000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				
			}
			
			st.setCond(5);
		}
		
		if(event.equalsIgnoreCase("33980-2.htm"))
		{
			st.giveItems(WIND_SPIRIT_RELIC, 1);
			player.sendPacket(new ExShowScreenMessage(RETURN_GLUDIO, 7000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
			
			st.setCond(6);
		}
		
		if(event.equalsIgnoreCase("33980-2.htm"))
		{
			st.setCond(7);
		}
		
		if(event.equalsIgnoreCase("33943-10.htm"))
		{
			st.takeItems(WIND_SPIRIT_RELIC, 2);
			
			st.giveItems(NAVARI_BOX_MARAUDER, 1);
			st.addExpAndSp(2700000, 648);
			st.setState(COMPLETED);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
			
		}
		
		return htmltext;
	}


	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == NAVARI)
		{
			if(cond == 1)
			{
				htmltext = "33931-1.htm";
			}
			else if(cond == 2)
				htmltext = "33931-3.htm";
		}
		else if(npcId == KATALIN && st.getCond() == 2)
		{
			htmltext = "33943-1.htm";
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
			Quest q = QuestManager.getQuest(10751);
			player.processQuestEvent(q.getName(), "start_quest_delay", null);
		}
		
	}

	@Override
	public void onLevelChange(Player player, int oldLvl, int newLvl)
	{
		//System.out.println("level change oldLvl " + oldLvl + " newLvl " + newLvl + "checkStartCondition " + checkStartCondition(player));
		if(oldLvl < 20 && newLvl >= 20 && checkStartCondition(player))
		{
			//System.out.println("received_navari_letter_3rd " + player.getVarBoolean("@received_navari_letter_3rd"));
			if(player.getVarBoolean("@received_navari_letter_3rd"))
				return;

			Quest q = QuestManager.getQuest(10755);
			player.processQuestEvent(q.getName(), "start_quest", null);
			
		}
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		
		if(updateKill(npc, st))
		{
			st.playSound(SOUND_MIDDLE);
			st.setCond(cond+1);
			
			//spawn Telesha's corpse and despawn after 3 minutes
			telesha_corpse_instance = st.addSpawn(TELESHA_CORPSE, npc.getLoc().getX(), player.npc().getY(), npc.getLoc().getZ(), 180000);
			
			player.sendPacket(new ExShowScreenMessage(CHECK_TELESHA_CORPSE, 7000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
			
		}
		
		return null;
	}
	
	private void alertLetterReceived(QuestState st)
	{
		if(st == null) return;
		
		st.getPlayer().sendPacket(new ExShowScreenMessage(LETTER_ALERT_STRING, 7000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
		
		st.showQuestionMark(10751);
		
		st.playSound(SOUND_TUTORIAL);
		
		st.giveItems(SOE_GLUDIN, 1);
		
		st.getPlayer().setVar("@received_navari_letter_3rd", true);
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
		return (player.getLevel() >= minLevel && player.getLevel() <= maxLevel && player.getRace() == Race.ERTHEIA && player.getQuestState("_10751_WindOfFate_Encounters") == null);
	}

}