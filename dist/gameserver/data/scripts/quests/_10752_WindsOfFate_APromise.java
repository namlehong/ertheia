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
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExCallToChangeClass;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.TutorialCloseHtmlPacket;
import l2s.gameserver.scripts.ScriptFile;
import l2s.gameserver.utils.Location;

/**
 * @author Hien Son
 */

public class _10752_WindsOfFate_APromise extends Quest implements ScriptFile, OnPlayerEnterListener, OnLevelChangeListener
{
	private static final int KATALIN = 33943;
	private static final int AYANTHE = 33942;
	private static final int KARLA = 33933;
	private static final int LOMBERT = 31317;
	private static final int SIEGMUND = 31321;
	private static final int KAIN_VAN_HALTER = 33979;
	private static final int MYSTERIOUS_WIZARD1 = 31522;
	private static final int MYSTERIOUS_WIZARD2 = 33980;
	private static final int BROKEN_BOOKSHELF = 31526;
	private static final int GHOST_VON_HELLMANN = 31524;
	private static final int TOMBSTONE = 31523;

	private static final int NAVARI_MARK = 39536;
	private static final int PROPHERCY_MACHINE_FRAGMENT = 39537;
	private static final int KAIN_PROPHERCY_MACHINE_FRAGMENT = 39537;
	private static final int SOUL_CRYSTAL_14_RED = 9570;
	private static final int SOUL_CRYSTAL_14_BLUE = 9571;
	private static final int SOUL_CRYSTAL_14_GREEN = 9572;
	private static final int STEEL_DOOR_COIN = 37045;

	private static final int VON_HELLMANN = 19566;
	private static final int VAMPIRIC_SOLDIER = 19567;
	
	private static final int RIPPER_CLASS_ID = 186;
	private static final int STRATOMANCER_CLASS_ID = 187;
	
	private static final int minLevel = 76;
	private static final int maxLevel = 99;
	
	private static final String KATALIN_LETTER_ALERT_STRING = "Bạn vừa nhận được thư từ Katalin";
	private static final String AYANTHE_LETTER_ALERT_STRING = "Bạn vừa nhận được thư từ Ayanthe";
	private static final String TALK_TO_GHOST = "Hãy nói chuyện với hồn ma của Von Hellmann";
	private static final String MOVE_TO_NEXT_PLACE = "Hãy tới nơi mà Von Hellmann đã kể";
	private static final String TALK_TO_WIZARD = "Hãy nói chuyện với Mysterious Wizard";

	private static final String VAMPIRIC_SOLDIER_KILL_LIST = "vamp_kill_list";
	
	NpcInstance giselle_instance = null;
	NpcInstance kain_instance = null;
	NpcInstance von_hellmann_ghost_instance = null;
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
	
	
	public _10752_WindsOfFate_APromise()
	{
		super(false);

		CharListenerList.addGlobal(this);
		
		addTalkId(KATALIN, AYANTHE, KARLA, SIEGMUND, LOMBERT, MYSTERIOUS_WIZARD1, TOMBSTONE, GHOST_VON_HELLMANN, BROKEN_BOOKSHELF, KAIN_VAN_HALTER, MYSTERIOUS_WIZARD2);
		
		addAttackId(VON_HELLMANN);
		
		addKillNpcWithLog(8, VAMPIRIC_SOLDIER_KILL_LIST, 4, VAMPIRIC_SOLDIER);
		
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
				Quest q = QuestManager.getQuest(10752);
				player.processQuestEvent(q.getName(), "start_quest", null);
			}
			
			htmltext = "";
			return null;
		}
		
		if(event.equalsIgnoreCase("start_quest") || event.equalsIgnoreCase("start_quest_7s"))
		{
			
			alertLetterReceived(st);
			if(player.isMageClass())
				st.showQuestHTML(st.getQuest(), "ayanthe_letter.htm");
			else
				st.showQuestHTML(st.getQuest(), "katalin_letter.htm");
			
			htmltext = "";
			return null;
		}
		
		if(event.equalsIgnoreCase("start_quest_delay"))
		{
			st.startQuestTimer("start_quest_7s", 7000);
			//only start quest after 7s to avoid crash on enterworld
			htmltext = "";
			return null;
		}
		
		if(event.equalsIgnoreCase("Quest _10752_WindsOfFate_APromise to_faeron"))
		{
			if(st.getCond() == 0)
			{
				player.teleToLocation(-82056, 249800, -3392);
				player.sendPacket(TutorialCloseHtmlPacket.STATIC);
				
			}
			htmltext = "";
			return null;
		}
		
		if(event.equalsIgnoreCase("Quest _10752_WindsOfFate_APromise close_window"))
		{
			player.sendPacket(TutorialCloseHtmlPacket.STATIC);
			htmltext = "";
			return null;
		}
		
		// Question mark clicked
		if(event.startsWith("QM"))
		{
			int MarkId = Integer.valueOf(event.substring(2));
			//System.out.println("Mark id " + MarkId);
			if(MarkId == 10752)
			{
				if(player.getRace() == Race.ERTHEIA)
				{
					if(player.isMageClass())
						st.showQuestHTML(st.getQuest(), "ayanthe_letter.htm");
					else
						st.showQuestHTML(st.getQuest(), "katalin_letter.htm");
				}
				htmltext = "";
				return null;
			}
		}
		
		if(event.equalsIgnoreCase("33942-5.htm") || event.equalsIgnoreCase("33943-5.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		
		if(event.equalsIgnoreCase("33933-4.htm"))
		{
			st.setCond(2);
			st.giveItems(NAVARI_MARK, 1);
			st.giveItems(PROPHERCY_MACHINE_FRAGMENT, 1);
		}
		
		
		if(event.equalsIgnoreCase("31321-3.htm"))
		{
			st.setCond(3);
		}
		
		if(event.equalsIgnoreCase("31317-5.htm"))
		{
			st.setCond(4);
		}
		
		if(event.equalsIgnoreCase("31522-5.htm"))
		{
			st.setCond(5);
		}
		
		if(event.equalsIgnoreCase("31523-3.htm"))
		{
			st.setCond(6);
			//spawn Von Hellmann's ghost and despawn after 3 minutes
			von_hellmann_ghost_instance = st.addSpawn(GHOST_VON_HELLMANN, npc.getLoc().getX() + 100, npc.getLoc().getY() + 100, npc.getLoc().getZ(), 180000);
			
			player.sendPacket(new ExShowScreenMessage(TALK_TO_GHOST, 7000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
		}
		
		if(event.equalsIgnoreCase("31524-3.htm"))
		{
			st.setCond(7);
		}
		
		if(event.equalsIgnoreCase("enter_instance"))
		{
			st.setCond(8);
			enterInstance(st, 254);

			return null;
		}
		
		if(event.equalsIgnoreCase("33979-11.htm"))
		{
			st.giveItems(KAIN_PROPHERCY_MACHINE_FRAGMENT, 1);
			player.sendPacket(new ExShowScreenMessage(TALK_TO_WIZARD, 7000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
			mysterious_wizard_instance = player.getReflection().addSpawnWithoutRespawn(MYSTERIOUS_WIZARD2, new Location(npc.getLoc().getX() + 200, npc.getLoc().getY() + 200, npc.getLoc().getZ(), 0), 0);
		}
		
		if(event.equalsIgnoreCase("ending_scene"))
		{
			//Cutscene video, when Giselle goes aggro and summon minions
			//Kain tells player to leave
			player.startScenePlayer(111);
			
			//destroy instance after 5s
			st.startQuestTimer("leave_instance", 30000);
		}
		
		if(event.equalsIgnoreCase("leave_instance"))
		{
			st.setCond(9);
			player.getReflection().collapse();
			return null;
		}
		
		if(event.equalsIgnoreCase("33933-7.htm"))
		{
			if(player.isMageClass())
			{
				htmltext = "33933-8.htm";
				st.setCond(10);
			}
			else
			{
				htmltext = "33933-9.htm";
				st.setCond(11);
			}
			
			st.takeItems(NAVARI_MARK, 1);
			st.takeItems(PROPHERCY_MACHINE_FRAGMENT, 1);
			st.takeItems(KAIN_PROPHERCY_MACHINE_FRAGMENT, 1);
		}
		
		if(event.startsWith("soul_"))
		{
			if(event.equalsIgnoreCase("soul_red"))
				st.set("soulcrystal", 9570);
			else if(event.equalsIgnoreCase("soul_blue"))
				st.set("soulcrystal", 9571);
			else if(event.equalsIgnoreCase("soul_green"))
				st.set("soulcrystal", 9572);
			
			if(player.isMageClass())
			{
				htmltext = "33942-7.htm";
			}
			else
			{
				htmltext = "33943-7.htm";
			}
			
		}
		
		if(event.equalsIgnoreCase("33943-10.htm") || event.equalsIgnoreCase("33942-10.htm"))
		{
			int newClassId;
			
			if(player.isMageClass())
				newClassId = STRATOMANCER_CLASS_ID;
			else
				newClassId = RIPPER_CLASS_ID;
			
			st.takeItems(KAIN_PROPHERCY_MACHINE_FRAGMENT, 1);
			
			st.giveItems(STEEL_DOOR_COIN, 87);
			st.giveItems(ADENA_ID, 5000000);
			st.giveItems(st.getInt("soulcrystal"), 1);
			st.addExpAndSp(2050000, 0);
			st.setState(COMPLETED);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
			
			player.sendPacket(SystemMsg.CONGRATULATIONS__YOUVE_COMPLETED_A_CLASS_TRANSFER);
			player.setClassId(newClassId, false);
			player.broadcastCharInfo();
		}
		
		return htmltext;
	}

	/*
 	1	Find Karla.
	2	Go to Grand Master Siegmund of the Warrior Guild in the Town of Rune.
	3	Go to Lombert, the head blacksmith of the Town of Rune
	4	Find Mysterious Wizard in Forest of the Dead
	5	Find An Old Tombstone
	6	Talk to Von Hellmann Ghost
	7	Go to the Broken Bookshelf
	8	Enter instance
	9	Get out of instance after talking to Mysterious Wizard
	10	Go to Magister Ayanthe for further training.\\n\
	11	Go to Master Katalin for further training.\\n
	-1	a,Winds of Fate: A Promise\0
	-2	a,Winds of Fate: A Promise\0
	 */

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		Player player = st.getPlayer();
		
		if(npcId == KATALIN && !player.isMageClass())
		{
			if(cond == 0)
			{
				htmltext = "33943-1.htm";
			}
			else if(cond == 11)
			{
				htmltext = "33943-6.htm";
			}
		}
		else if(npcId == AYANTHE && player.isMageClass())
		{
			if(cond == 0)
			{
				htmltext = "33942-1.htm";
			}
			else if(cond == 10)
			{
				htmltext = "33942-6.htm";
			}
		}
		else if(npcId == KARLA)
		{
			if(cond == 1)
			{
				htmltext = "33933-1.htm";
			}
			else if(cond == 9)
			{
				htmltext = "33933-6.htm";
			}
		}
		else if(npcId == SIEGMUND)
		{
			if(cond == 2)
			{
				htmltext = "31321-1.htm";
			}
			else if(cond == 4)
			{
				htmltext = "31321-3.htm";
			}
		}
		else if(npcId == LOMBERT)
		{
			if(cond == 3)
			{
				htmltext = "31317-1.htm";
			}
			else if(cond == 4)
			{
				htmltext = "31317-5.htm";
			}
			
		}
		else if(npcId == MYSTERIOUS_WIZARD1)
		{
			if(cond == 4)
			{
				htmltext = "31522-1.htm";
			}
			else if(cond == 5)
			{
				htmltext = "31522-5.htm";
			}
		}
		else if(npcId == TOMBSTONE)
		{
			if(cond == 5)
			{
				htmltext = "31523-1.htm";
			}
			else if(cond == 6)
			{
				htmltext = "31523-3.htm";
			}
		}
		else if(npcId == GHOST_VON_HELLMANN)
		{
			if(cond == 6)
			{
				htmltext = "31524-1.htm";
			}
			else if(cond == 7)
			{
				htmltext = "31524-3.htm";
			}
		}
		else if(npcId == BROKEN_BOOKSHELF)
		{
			if(cond == 7)
			{
				htmltext = "31526-1.htm";
			}
		}
		else if(npcId == KAIN_VAN_HALTER)
		{
			if(cond == 8)
			{
				htmltext = "33979-1.htm";
			}
		}
		else if(npcId == MYSTERIOUS_WIZARD2)
		{
			if(cond == 8)
			{
				htmltext = "33980-1.htm";
			}
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
			Quest q = QuestManager.getQuest(10752);
			player.processQuestEvent(q.getName(), "start_quest_delay", null);
		}
		
	}

	@Override
	public void onLevelChange(Player player, int oldLvl, int newLvl)
	{
		//System.out.println("level change oldLvl " + oldLvl + " newLvl " + newLvl + "checkStartCondition " + checkStartCondition(player));
		if(oldLvl < minLevel && newLvl >= minLevel && checkStartCondition(player))
		{
			Quest q = QuestManager.getQuest(10752);
			player.processQuestEvent(q.getName(), "start_quest", null);
			
		}
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		Player player = st.getPlayer();
		
		if(updateKill(npc, st))
		{
			st.playSound(SOUND_MIDDLE);
			
			//spawn Giselle Von Hellmann and despawn after 5 minutes
			giselle_instance = player.getReflection().addSpawnWithoutRespawn(VON_HELLMANN, new Location(57960, -28376, 544, 0), 0);
			giselle_instance.getAggroList().addDamageHate(player, 50000, 50000);
			giselle_instance.setAggressionTarget(player);
			giselle_instance.getAI().Attack(player, false, false);
		}
		

		//need to add this onKill just in case player too powerful, kill Giselle too quick for the onAttack event triggers
		if(npcId == VON_HELLMANN && giselle_instance != null && kain_instance == null)
		{
			spawnKainVanHalter(npc, st);
		}
		
		return null;
	}

	@Override
	public String onAttack(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		Player player = st.getPlayer();
		
		if(npcId == VON_HELLMANN && giselle_instance != null && kain_instance == null && npc.getCurrentHpPercents() < 50)
		{
			spawnKainVanHalter(npc, st);
		}
		
		return null;
	}
	
	public void spawnKainVanHalter(NpcInstance npc, QuestState st)
	{
		Player player = st.getPlayer();
		
		//Cutscene video, when Kain came to save Giselle
		player.startScenePlayer(110);
		
		giselle_instance.deleteMe();
		
		giselle_instance = null;
		
		//spawn Kain Van Halter right at the position of Giselle Von Hellmann. Despawn him after 5 mins
		kain_instance = player.getReflection().addSpawnWithoutRespawn(KAIN_VAN_HALTER, new Location(npc.getLoc().getX(), npc.getLoc().getY(), npc.getLoc().getZ(), 0), 0);
	
	}
	
	private void alertLetterReceived(QuestState st)
	{
		if(st == null) return;
		
		Player player = st.getPlayer();
		
		if(player == null) return;
		
		if(player.isMageClass())
			st.getPlayer().sendPacket(new ExShowScreenMessage(AYANTHE_LETTER_ALERT_STRING, 7000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
		else
			st.getPlayer().sendPacket(new ExShowScreenMessage(KATALIN_LETTER_ALERT_STRING, 7000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
		
		st.showQuestionMark(10752);
		
		st.playSound(SOUND_TUTORIAL);
		
	}
	
	@Override
	public boolean checkStartCondition(Player player)
	{
		QuestState st = player.getQuestState("_10752_WindsOfFate_APromise");
		
		boolean result = (player.getLevel() >= minLevel && 
				player.getLevel() <= maxLevel && 
				player.getRace() == Race.ERTHEIA && 
				(player.getClassId() == ClassId.MARAUDER || player.getClassId() == ClassId.SAIHA_MAGE ) && 
				(st == null || (st != null && st.getCond() == 0)));
		
		//System.out.println("checkStartCondition Q10752 " + result);
		return result;
	}

}