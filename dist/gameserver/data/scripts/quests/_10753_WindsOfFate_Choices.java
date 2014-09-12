package quests;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.instancemanager.QuestManager;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.items.PcInventory;
import l2s.gameserver.listener.actor.player.OnLevelChangeListener;
import l2s.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.SocialActionPacket;
import l2s.gameserver.network.l2.s2c.TutorialCloseHtmlPacket;
import l2s.gameserver.scripts.ScriptFile;
import l2s.gameserver.utils.Location;

/**
 * @author Hien Son
 */

public class _10753_WindsOfFate_Choices extends Quest implements ScriptFile, OnPlayerEnterListener, OnLevelChangeListener
{
	private static final int KATALIN = 33943;
	private static final int AYANTHE = 33942;
	private static final int GERETH = 33932;
	private static final int ARKENIAS = 30174;
	private static final int MIXING_URN = 31149;
	private static final int HARDIN = 30832;
	private static final int LICH_KING_ICARUS = 30835;
	private static final int ATHREA = 30758;
	private static final int ATHREA_BOX = 33997;
	private static final int FERIN_NPC = 34001;
	private static final int KAIN_VAN_HALTER_NPC = 33979;
	private static final int MYSTERIOUS_WIZARD = 33980;
	private static final int GRAIL = 33996;
	private static final int NAVARI = 33931;
	
	private static final int KAIN_VAN_HALTER_FIGHTER = 33999;
	private static final int FERIN_HEALER = 34000;
	
	private static final int ERTHEIA_PROPHECY_MACHINE1 = 39539;
	private static final int ERTHEIA_PROPHECY_MACHINE2 = 39540;
	private static final int ATELIA1 = 39542;
	private static final int ATELIA2 = 39543;
	private static final int CRUDE_PHYLOSOPHY_STONE = 39544;
	private static final int CRYSTAL_EYE = 39545;
	private static final int BROKEN_STONE_OF_PURITY = 39546;
	private static final int MIRACLE_DRUG_OF_ENCHANTMENT = 39547;
	private static final int EMPTY_REAGENT_FLASK = 39548;
	private static final int RESTORATION_REAGENT = 39549;
	private static final int ATHREA_BELONGINGS = 39550;
	private static final int WHITE_ROSE = 39551;
	private static final int CRIMSON_ROSE = 39552;
	
	private static final int CHAOS_POMANDER = 37374;
	private static final int SAYHA_BOX_FIGHTER = 40268;
	private static final int SAYHA_BOX_WIZARD = 40269;
	private static final int STEEL_DOOR_COIN = 37045;

	private static final int NEBULITE_EYE = 27544;
	private static final int NEBULITE_WATCH = 27545;
	private static final int NEBULITE_GOLEM = 27546;
	private static final int SACRED_WIZARD = 19568;
	private static final int SACRED_SOLDIER = 19569;
	private static final int SACRED_SLAYER = 19570;
	private static final int MAKKUM = 19571;
	private static final int ABBYSAL_SHADOW = 19572;
	private static final int SECLUDED_SHADOW = 19573;
	
	private static final int minLevel = 85;
	private static final int maxLevel = 99;
	
	private static final String KATALIN_LETTER_ALERT_STRING = "Bạn vừa nhận được thư từ Katalin";
	private static final String AYANTHE_LETTER_ALERT_STRING = "Bạn vừa nhận được thư từ Ayanthe";
	private static final String TALK_TO_WIZARD = "Hãy nói chuyện với Mysterious Wizard";
	private static final String CHOICE_NOT_REVERSABLE = "Hãy chọn kỹ, lựa chọn này không thể thay đổi";
	
	NpcInstance ferin_healer_instance = null;
	NpcInstance kain_fighter_instance = null;
	NpcInstance mysterious_wizard_instance = null;
	List<NpcInstance> athrea_boxes = new ArrayList<NpcInstance>();
	
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
	
	public _10753_WindsOfFate_Choices()
	{
		super(false);

		CharListenerList.addGlobal(this);
		
		addTalkId(	KATALIN, 
					AYANTHE, 
					GERETH, 
					ARKENIAS, 
					MIXING_URN, 
					HARDIN, 
					LICH_KING_ICARUS, 
					ATHREA, 
					ATHREA_BOX, 
					KAIN_VAN_HALTER_NPC, 
					MYSTERIOUS_WIZARD, 
					NAVARI);
		
		addKillId(	NEBULITE_EYE, 
					NEBULITE_WATCH, 
					NEBULITE_GOLEM, 
					SACRED_WIZARD, 
					SACRED_SOLDIER, 
					SACRED_SLAYER, 
					ABBYSAL_SHADOW, 
					SECLUDED_SHADOW, 
					MAKKUM);
		
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
				Quest q = QuestManager.getQuest(10753);
				player.processQuestEvent(q.getName(), "start_quest", null);
			}
			
			return null;
		}
		
		if(event.equalsIgnoreCase("start_quest") || event.equalsIgnoreCase("start_quest_7s"))
		{
			
			alertLetterReceived(st);
			if(player.isMageClass())
				st.showQuestHTML(st.getQuest(), "ayanthe_letter.htm");
			else
				st.showQuestHTML(st.getQuest(), "katalin_letter.htm");
			
			return null;
		}
		
		if(event.equalsIgnoreCase("start_quest_delay"))
		{
			st.startQuestTimer("start_quest_7s", 7000);
			//only start quest after 7s to avoid crash on enterworld
			
			return null;
		}
		
		if(event.equalsIgnoreCase("Quest _10753_WindsOfFate_Choices to_faeron"))
		{
			if(st.getCond() == 0)
			{
				player.teleToLocation(-82056, 249800, -3392);
				player.sendPacket(TutorialCloseHtmlPacket.STATIC);
				
			}
			return null;
		}
		
		if(event.equalsIgnoreCase("Quest _10753_WindsOfFate_Choices close_window"))
		{
			player.sendPacket(TutorialCloseHtmlPacket.STATIC);
			return null;
		}
		
		// Question mark clicked
		if(event.startsWith("QM"))
		{
			int MarkId = Integer.valueOf(event.substring(2));
			//System.out.println("Mark id " + MarkId);
			if(MarkId == 10753)
			{
				if(player.getRace() == Race.ERTHEIA)
				{
					if(player.isMageClass())
						st.showQuestHTML(st.getQuest(), "ayanthe_letter.htm");
					else
						st.showQuestHTML(st.getQuest(), "katalin_letter.htm");
				}
				return null;
			}
		}
		
		/*	1	Ask Grand Magister Arkenias for help at the 3rd floor of Ivory Tower.
			2	ingredients for making a Restoration Reagent for fixing the Prophecy Machine.
				Monsters to Hunt: Nubulite Eye, Nebulite Watch, Nebulite Golem\
			3	Return to Arkenias on the 3rd floor of Ivory Tower
			4	Go to the urn. Empty Regeant Flask and a Crude Philosopher's Stone
			5	Tell Arkenias of your success.\
			6	Go to Hardin. Go to Hardin's Academy.\\n\0
			7	Lich King Icarus can help you with this
			8	Go outside of the academy as instructed and go to Sorceress Ritasha for the rose you were told about.
			9	Help her find her stuff.
			10	You found everything you needed. Take them to Ritasha.\\n
			11	A Crimson Rose\0	a,The rose is stained crimson with Eva's blood. Return to Icarus at Hardin's Academy.\\n\0		
			12	Maybe Magister Ayanthe can decipher something out of this.\\n\0
			13	Maybe Master Katalin can decipher something out of this.\\n\
			14	Magister Ayanthe suggested that you visit Gereth for further advice
			15	Master Katalin suggested that you visit Gereth for further advice
			16	The Chamber of Prophecies
			17	Report back to Gereth.\\n					
			18	The Queen should be notified of this.
		 */
		
		//Ayanthe or Katalin
		if(event.equalsIgnoreCase("33942-4.htm") || event.equalsIgnoreCase("33943-4.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
			st.giveItems(ERTHEIA_PROPHECY_MACHINE1, 1);
			st.playSound(SOUND_ITEMGET);
		}
		
		//Katalin
		if(event.equalsIgnoreCase("33942-9.htm"))
		{
			st.setCond(15);
		}
		
		//Ayanthe
		if(event.equalsIgnoreCase("33943-9.htm"))
		{
			st.setCond(14);
		}
		
		//Arkenias
		if(event.equalsIgnoreCase("30174-3.htm"))
		{
			st.setCond(2);
		}
		
		if(event.equalsIgnoreCase("30174-8.htm"))
		{
			st.setCond(6);
			st.takeItems(RESTORATION_REAGENT, 1);
			//this useless Ivory Wizard cannot do a damned thing :(
			st.takeItems(ERTHEIA_PROPHECY_MACHINE1, 1);
			st.giveItems(ERTHEIA_PROPHECY_MACHINE1, 1);
			st.playSound(SOUND_ITEMGET);
		}
		
		//Mixing Urn
		if(event.equalsIgnoreCase("31149-3.htm"))
		{
			st.setCond(5);

			st.takeAllItems(CRUDE_PHYLOSOPHY_STONE);
			st.takeAllItems(CRYSTAL_EYE);
			st.takeAllItems(BROKEN_STONE_OF_PURITY);
			st.takeAllItems(MIRACLE_DRUG_OF_ENCHANTMENT);
			st.takeAllItems(EMPTY_REAGENT_FLASK);
			
			st.giveItems(RESTORATION_REAGENT, 1);
			st.playSound(SOUND_ITEMGET);
		}
		
		//Hardin
		if(event.equalsIgnoreCase("30832-3.htm"))
		{
			st.setCond(7);
		}
		
		//Icarus
		if(event.equalsIgnoreCase("30835-5.htm"))
		{
			st.setCond(8);
			st.giveItems(WHITE_ROSE, 1);
			st.playSound(SOUND_ITEMGET);
		}
		
		if(event.equalsIgnoreCase("30835-7.htm"))
		{
			if(player.isMageClass())
				st.setCond(12);
			else
				st.setCond(13);
			
			st.takeItems(CRIMSON_ROSE, 1);
			st.takeItems(ERTHEIA_PROPHECY_MACHINE1, 1);
			st.giveItems(ERTHEIA_PROPHECY_MACHINE2, 1);
			st.playSound(SOUND_ITEMGET);
			
		}
		
		//Athrea
		if(event.equalsIgnoreCase("30758-4.htm"))
		{
			st.setCond(9);
			spawnAthreaBoxes(npc, st);
			//start a count down of 3 minutes
			st.set("box_duration", 180);
			st.set("box_checked", 0);
			st.startQuestTimer("check_box_timer", 1000);
		}
		
		if(event.equalsIgnoreCase("check_box_timer"))
		{
			int remaining_time = st.getInt("box_duration");
			
			remaining_time--;

			st.set("box_duration", remaining_time);
			
			if(remaining_time > 0)
			{
				if(getItemCountById(player, ATHREA_BELONGINGS) < 4)
				{
					//continue the clock
					st.startQuestTimer("check_box_timer", 1000);
					
					//show the time on screen
					int min_left = (int)Math.floor(remaining_time/60);
					int sec_left = remaining_time%60;
					String time_report = min_left + ":" + sec_left;
					
					player.sendPacket(new ExShowScreenMessage(time_report, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				}
			}
			else
			{
				//time is up
				player.sendPacket(new ExShowScreenMessage("Hết giờ!", 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				st.unset("box_duration");
				st.takeAllItems(ATHREA_BELONGINGS);
			}
			
			return null;
		}
		
		if(event.equalsIgnoreCase("30758-7.htm"))
		{
			st.setCond(11);
			st.takeAllItems(ATHREA_BELONGINGS);
			st.takeItems(WHITE_ROSE, 1);
			st.giveItems(CRIMSON_ROSE, 1);
			st.playSound(SOUND_ITEMGET);
		}
		
		if(event.equalsIgnoreCase("check_box"))
		{
			int checked_box_count = st.getInt("box_checked");
			checked_box_count++;
			st.set("box_checked", checked_box_count);
			int belonging_count = (int) getItemCountById(player, ATHREA_BELONGINGS);
			
			String[] belonging_location = st.get("belonging_location").split(",");
			
			if(Integer.parseInt(belonging_location[belonging_count]) == checked_box_count)
			{
				st.giveItems(ATHREA_BELONGINGS, 1);
				st.playSound(SOUND_ITEMGET);
				
				if(belonging_count + 1 < 4)
					htmltext = "33997-1.htm";
				else
				{
					st.unset("box_duration");
					st.unset("box_checked");
					st.unset("belonging_location");
					st.setCond(10);
					htmltext = "33997-3.htm";
					st.stopQuestTimers();
				}
			}
			else
			{

				htmltext = "33997-2.htm";
			}
				
		}
		
		if(event.equalsIgnoreCase("enter_instance"))
		{
			st.setCond(16);
			enterInstance(st, 255);

			return null;
		}
		
		if(event.equalsIgnoreCase("start_fighting"))
		{
			Reflection prophecies_chamber = player.getReflection();
			
			prophecies_chamber.getDoor(17230101).openMe();
			
			kain_fighter_instance = prophecies_chamber.addSpawnWithoutRespawn(KAIN_VAN_HALTER_FIGHTER, new Location(-88408, 186824, -10476), 0);
			ferin_healer_instance = prophecies_chamber.addSpawnWithoutRespawn(FERIN_HEALER, new Location(-88552, 186840, -10476), 0);
			
			st.startQuestTimer("npc_follow_timer", 10000);
			st.set("follow", 1);
			npc.deleteMe();
			
			return null;
		}
		
		if(event.equalsIgnoreCase("npc_follow_timer"))
		{
			if(st.getInt("follow") == 1)
			{
				st.startQuestTimer("npc_follow_timer", 2000);
				
				if(kain_fighter_instance!= null)
				{
					if(kain_fighter_instance.getLoc().distance(player.getLoc()) > 400)
					{
						kain_fighter_instance.moveToLocation(Location.coordsRandomize(player.getLoc(), 50, 150), 100, true);
					}
				}
				
				if(ferin_healer_instance!= null)
				{
					if(ferin_healer_instance.getLoc().distance(player.getLoc()) > 400)
					{
						ferin_healer_instance.moveToLocation(Location.coordsRandomize(player.getLoc(), 50, 150), 100, true);
					}
				}
				
			}
			
			return null;
		}
		
		if(event.equalsIgnoreCase("leave_instance"))
		{
			st.setCond(17);
			player.getReflection().collapse();
			return null;
		}
		
		if(event.equalsIgnoreCase("33943-10.htm") || event.equalsIgnoreCase("33942-10.htm"))
		{
			ClassId newClassId;
			
			if(player.isMageClass())
			{
				newClassId = ClassId.SAIHA_RULER;
				st.giveItems(SAYHA_BOX_WIZARD, 1);
			}
			else
			{
				newClassId = ClassId.RANGER_GRAVITY;
				st.giveItems(SAYHA_BOX_FIGHTER, 1);
			}
			
			st.takeItems(ATELIA1, 1);
			st.takeItems(ATELIA2, 1);
			
			st.giveItems(STEEL_DOOR_COIN, 400);
			st.giveItems(CHAOS_POMANDER, 2);
			
			st.setState(COMPLETED);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
			
			player.broadcastPacket(new SocialActionPacket(player.getObjectId(), 20));
			player.sendPacket(SystemMsg.CONGRATULATIONS__YOUVE_COMPLETED_A_CLASS_TRANSFER);
			player.setClassId(newClassId.getId(), false);
			player.broadcastCharInfo();
		}
		
		return htmltext;
	}

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
			else if(cond == 13)
			{
				htmltext = "33943-5.htm";
			}
		}
		else if(npcId == AYANTHE && player.isMageClass())
		{
			if(cond == 0)
			{
				htmltext = "33942-1.htm";
			}
			else if(cond == 12)
			{
				htmltext = "33942-5.htm";
			}
		}
		else if(npcId == ARKENIAS)
		{
			if(cond == 1)
			{
				htmltext = "30174-1.htm";
			}
			else if(cond == 2)
			{
				htmltext = "30174-3.htm";
			}
			else if(cond == 3)
			{
				htmltext = "30174-4.htm";
				st.setCond(4);
				st.giveItems(CRUDE_PHYLOSOPHY_STONE, 1);
				st.giveItems(EMPTY_REAGENT_FLASK, 1);
				st.playSound(SOUND_ITEMGET);
			}
			else if(cond == 4)
			{
				htmltext = "30174-5.htm";
			}
			else if(cond == 5)
			{
				htmltext = "30174-5.htm";
			}
			
		}
		else if(npcId == MIXING_URN)
		{
			if(cond == 4)
			{
				htmltext = "31149-1.htm";
			}
			else if(cond == 5)
			{
				htmltext = "31149-3.htm";
			}
		}
		else if(npcId == HARDIN)
		{
			if(cond == 6)
			{
				htmltext = "30832-1.htm";
			}
			else if(cond == 7)
			{
				htmltext = "30832-3.htm";
			}
		}
		else if(npcId == LICH_KING_ICARUS)
		{
			if(cond == 7)
			{
				htmltext = "30835-1.htm";
			}
			else if(cond == 8)
			{
				htmltext = "30835-5.htm";
			}
			else if(cond == 11)
			{
				htmltext = "30835-6.htm";
			}
		}
		else if(npcId == ATHREA)
		{
			if(cond == 8)
			{
				htmltext = "30758-1.htm";
			}
			else if(cond == 9)
			{
				htmltext = "30758-3.htm";
			}
			else if(cond == 10)
			{
				htmltext = "30758-5.htm";
			}
			else if(cond == 11)
			{
				htmltext = "30758-7.htm";
			}
		}
		else if(npcId == GERETH)
		{
			if(cond == 14 || cond == 15)
			{
				htmltext = "33932-1.htm";
			}
			if(cond == 16)
			{
				htmltext = "33932-6.htm";
			}
			else if(cond == 17)
			{
				htmltext = "33932-7.htm";
			}
			else if(cond == 18)
			{
				htmltext = "33932-8.htm";
			}
		}
		else if(npcId == KAIN_VAN_HALTER_NPC)
		{
			if(cond == 16)
			{
				//if the player has Atelia (the real one or the fake one that scumbag Mysterious Wizard gave)
				//meaning the player has finish the instance or not
				if(getItemCountById(player, ATELIA1) == 0 && getItemCountById(player, ATELIA1) == 0)
					htmltext = "33979-1.htm";
				else
					htmltext = "33979-2.htm";
			}
		}
		else if(npcId == GRAIL)
		{
			if(cond == 16)
			{
				htmltext = "33996-1.htm";
			}
		}
		else if(npcId == MYSTERIOUS_WIZARD)
		{
			if(cond == 18)
			{
				htmltext = "33980-1.htm";
			}
		}
		else if(npcId == NAVARI)
		{
			if(cond == 8)
			{
				htmltext = "33931-1.htm";
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
			Quest q = QuestManager.getQuest(10753);
			player.processQuestEvent(q.getName(), "start_quest_delay", null);
		}
		
	}

	@Override
	public void onLevelChange(Player player, int oldLvl, int newLvl)
	{
		//System.out.println("level change oldLvl " + oldLvl + " newLvl " + newLvl + "checkStartCondition " + checkStartCondition(player));
		if(oldLvl < minLevel && newLvl >= minLevel && checkStartCondition(player))
		{
			Quest q = QuestManager.getQuest(10753);
			player.processQuestEvent(q.getName(), "start_quest", null);
			
		}
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		Player player = st.getPlayer();
		
		if(npcId == NEBULITE_EYE && getItemCountById(player, CRYSTAL_EYE) < 3)
			st.giveItems(CRYSTAL_EYE, 1);
		
		if(npcId == NEBULITE_WATCH && getItemCountById(player, BROKEN_STONE_OF_PURITY) < 3)
			st.giveItems(BROKEN_STONE_OF_PURITY, 1);
		
		if(npcId == NEBULITE_GOLEM && getItemCountById(player, MIRACLE_DRUG_OF_ENCHANTMENT) < 3)
			st.giveItems(MIRACLE_DRUG_OF_ENCHANTMENT, 1);
		
		if(	getItemCountById(player, CRYSTAL_EYE) >= 3 && 
			getItemCountById(player, BROKEN_STONE_OF_PURITY) >= 3 && 
			getItemCountById(player, MIRACLE_DRUG_OF_ENCHANTMENT) >= 3)
			st.setCond(3);
		
		return null;
	}
	
	private void spawnAthreaBoxes(NpcInstance npc, QuestState st)
	{
		NpcInstance athrea_box = null;
		Location[] boxes_location = {	new Location(102085, 103258, -3522),
										new Location(102085, 103322, -3520),
										new Location(102085, 103385, -3517),
										new Location(102085, 103450, -3515),
										new Location(102022, 103450, -3511),
										new Location(102022, 103258, -3516),
										new Location(102022, 103322, -3515),
										new Location(102022, 103385, -3514),
										new Location(101957, 103450, -3504),
										new Location(101957, 103258, -3514),
										new Location(101957, 103322, -3513),
										new Location(101957, 103385, -3510),
										new Location(101892, 103450, -3495),
										new Location(101892, 103258, -3512),
										new Location(101892, 103322, -3508),
										new Location(101892, 103385, -3502)};
		
		int belonging_index_1 = (int)Math.round(Math.random()*3) + 1;
		int belonging_index_2 = (int)Math.round(Math.random()*3) + 5;
		int belonging_index_3 = (int)Math.round(Math.random()*3) + 9;
		int belonging_index_4 = (int)Math.round(Math.random()*3) + 13;
		
		String belonging_index = belonging_index_1 + "," + belonging_index_2 + "," + belonging_index_3 + "," + belonging_index_4;
		
		st.set("belonging_location", belonging_index);
		
		for(Location box_location : boxes_location)
		{
			//add the box and set despawn after 3 minutes
			athrea_box = st.addSpawn(ATHREA_BOX, box_location.getX(), box_location.getY(), box_location.getZ(), 180000);
			athrea_boxes.add(athrea_box);
		}
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
	
	private void alertLetterReceived(QuestState st)
	{
		if(st == null) return;
		
		Player player = st.getPlayer();
		
		if(player == null) return;
		
		if(player.isMageClass())
			st.getPlayer().sendPacket(new ExShowScreenMessage(AYANTHE_LETTER_ALERT_STRING, 7000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
		else
			st.getPlayer().sendPacket(new ExShowScreenMessage(KATALIN_LETTER_ALERT_STRING, 7000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
		
		st.showQuestionMark(10753);
		
		st.playSound(SOUND_TUTORIAL);
		
	}
	
	@Override
	public boolean checkStartCondition(Player player)
	{
		QuestState st = player.getQuestState("_10753_WindsOfFate_Choices");
		
		boolean result = (player.getLevel() >= minLevel && 
				player.getLevel() <= maxLevel && 
				player.getRace() == Race.ERTHEIA && 
				(player.getClassId() == ClassId.RANGER || player.getClassId() == ClassId.STORM_SAIHA_MAGE) && 
				(st == null || (st != null && st.getCond() == 0)));
		
		//System.out.println("checkStartCondition Q10753 " + result);
		return result;
	}

}