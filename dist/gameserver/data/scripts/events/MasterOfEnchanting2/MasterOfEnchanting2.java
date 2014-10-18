package events.MasterOfEnchanting2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.time.cron.SchedulingPattern;
import l2s.commons.util.Rnd;
import l2s.gameserver.Announcements;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.data.xml.holder.NpcHolder;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.SimpleSpawner;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.items.Inventory;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.s2c.EarthQuakePacket;
import l2s.gameserver.network.l2.s2c.ExRedSky;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.L2GameServerPacket;
import l2s.gameserver.network.l2.s2c.SocialActionPacket;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.scripts.ScriptFile;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Location;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: Hien Son
 * Date: 22.09.2014
 **/
public class MasterOfEnchanting2 extends Functions implements ScriptFile, OnDeathListener, OnPlayerEnterListener
{
	private static final Logger _log = LoggerFactory.getLogger(MasterOfEnchanting2.class);
	private static final String EVENT_NAME = "MasterOfEnchanting2";
	
	private static int EVENT_MANAGER_ID 			= 41014;  //Panmot - Mê Đồ Sáng
	private static int ADEN_BOSS					= 29218; //Balok - Hell's Warden
	
	private static List<SimpleSpawner> _spawns 		= new ArrayList<SimpleSpawner>();
	
	private static boolean _active = false;

	private static int MASTER_YOGI_STAFF 			= 13539;
	private static int ENCHANTING_SCROLL 			= 13540;
	private static int CUBE_OF_BLESSING 			= 13542;
	private static int ADENA 						= 57;
	private static int STAFF_PRICE 					= 500000;

	private static int MONSTER_LEVEL_MIN 			= 85;
	private static int MIN_DROP						= 1;
	private static int MAX_DROP 					= 1;
	private static double DEFAULT_DROP_CHANCE 		= 1;
	
	private static NpcInstance aden_boss;
	private static int bodyCount 					= 0;
	private static int dropScrollCount				= 100;
	private static int maxBonusScroll				= 500;
	private ScheduledFuture<?> boss_spawn_task;
	
	//spawn every 3 hours
	private static SchedulingPattern boss_spawn_pattern = new SchedulingPattern("0 9 * * *|5 12 * * *|00 15 * * *|00 18 * * *|00 21 * * *");
	
	protected static final int[] JEWEL_85_REWARD = {
		
		//Twilight Jewel - 3 records
		18168, //Unidentified Twilight Necklace
		18169, //Unidentified Twilight Earring
		18170 //Unidentified Twilight Ring
	};
	
	protected static final int[] ARMOR_85_REWARD = {
		
		//Twilight Armors - 17 records
		18151, //Unidentified Twilight Helmet 
		18152, //Unidentified Twilight Breastplate 
		18153, //Unidentified Twilight Gaiters
		18154, //Unidentified Twilight Gauntlets
		18155, //Unidentified Twilight Boots
		18156, //Unidentified Twilight Shield
		18157, //Unidentified Twilight Leather Helmet
		18158, //Unidentified Twilight Leather Armor 
		18159, //Unidentified Twilight Leather Leggings
		18160, //Unidentified Twilight Leather Gloves
		18161, //Unidentified Twilight Leather Boots
		18162, //Unidentified Twilight Circlet
		18163, //Unidentified Twilight Tunic
		18164, //Unidentified Twilight Stockings
		18165, //Unidentified Twilight Gloves
		18166, //Unidentified Twilight Shoes
		18167 //Unidentified Twilight Sigil
	};
	
	protected static final int[] WEAPON_85_REWARD = {
		//Apocalypse weapons - 14 records
		18137, //Unidentified Apocalypse Shaper
		18138, //Unidentified Apocalypse Cutter
		18139, //Unidentified Apocalypse Slasher
		18140, //Unidentified Apocalypse Avenger
		18141, //Unidentified Apocalypse Fighter
		18142, //Unidentified Apocalypse Stormer
		18143, //Unidentified Apocalypse Thrower
		18144, //Unidentified Apocalypse Shooter
		18145, //Unidentified Apocalypse Buster
		18146, //Unidentified Apocalypse Caster
		18147, //Unidentified Apocalypse Retributer
		18148, //Unidentified Apocalypse Dualsword
		18149, //Unidentified Apocalypse Dual Dagger
		18150 //Unidentified Apocalypse Dual Blunt Weapon
	};
	
	protected static final int[] JEWEL_95_REWARD = {
		
		//Seraph Jewel - 3 records
		18202, //Unidentified Seraph Necklace
		18203, //Unidentified Seraph Earring
		18204 //Unidentified Seraph Ring
	};
	
	protected static final int[] ARMOR_95_REWARD = {
		
		//Seraph Armors - 17 records
		18185, //Unidentified Seraph Helmet 
		18186, //Unidentified Seraph Breastplate 
		18187, //Unidentified Seraph Gaiters
		18188, //Unidentified Seraph Gauntlets
		18189, //Unidentified Seraph Boots
		18190, //Unidentified Seraph Shield
		18191, //Unidentified Seraph Leather Helmet
		18192, //Unidentified Seraph Leather Armor 
		18193, //Unidentified Seraph Leather Leggings
		18194, //Unidentified Seraph Leather Gloves
		18195, //Unidentified Seraph Leather Boots
		18196, //Unidentified Seraph Circlet
		18197, //Unidentified Seraph Tunic
		18198, //Unidentified Seraph Stockings
		18199, //Unidentified Seraph Gloves
		18200, //Unidentified Seraph Shoes
		18201 //Unidentified Seraph Sigil
	};
	
	protected static final int[] SOUL_CRYSTAL_851_REWARD = {
		
		//Soul Crystal (R-Grade) - 3 records
		18551, //Red Soul Crystal (R-Grade)
		18552, //Green Soul Crystal (R-Grade)
		18553 //Blue Soul Crystal (R-Grade)
	};
	
	protected static final int[] SOUL_CRYSTAL_2_REWARD = {
		
		//Soul Crystal (R-Grade) - 3 records
		19502, //Yellow Soul Crystal (R-Grade)
		19503, //Teal Soul Crystal (R-Grade)
		19504, //Purple Soul Crystal (R-Grade)
		
		//Soul Crystal (R95-Grade) - 3 records
		19505, //Yellow Soul Crystal (R95-Grade)
		19506, //Teal Soul Crystal (R95-Grade)
		19507 //Purple Soul Crystal (R95-Grade)
	};

	protected static final int[] SOUL_CRYSTAL_951_REWARD = {
		
		//Soul Crystal (R95-Grade) - 3 records
		18554, //Red Soul Crystal (R95-Grade)
		18555, //Green Soul Crystal (R95-Grade)
		18556 //Blue Soul Crystal (R95-Grade)
	};

	protected static final int[] BOSS_JEWEL = {
		
		//Boss Jewel - 3 records
		6658, //Baium's Ring
		6659, //Zaken's Earring
		6660, //Queen Ant's Ring
		6661 //Orfen's Earring
	};
	
	private void spawnEventManagers()
	{
		final int EVENT_MANAGERS[][] = { { 147704, 25992, -2016, 32767 }}; //Town of Aden
				

		SpawnNPCs(EVENT_MANAGER_ID, EVENT_MANAGERS, _spawns);
	}
	
	private void spawnEventBoss()
	{
		if(aden_boss != null) return;
		
		Location boss_loc = new Location( 145896, 16824, -1552, 0); //Town of Aden - Flower Garden
		
		//reset counting variables
		bodyCount 					= 0;
		dropScrollCount				= 100;
		
		aden_boss = Functions.spawn(boss_loc, ADEN_BOSS);
		
		if(aden_boss != null) //spawn successfully
		{
			announceBossStatus();

			// RedSky and EarthQuakePacket
			L2GameServerPacket redSky = new ExRedSky(10);
			L2GameServerPacket eq = new EarthQuakePacket(aden_boss.getLoc(), 30, 12);
			for(Player player : GameObjectsStorage.getAllPlayersForIterate())
				player.sendPacket(redSky, eq);
		}
		
	}
	
	private void scheduleNextBossSpawn()
	{
		long nextLaunchTime = boss_spawn_pattern.next(System.currentTimeMillis());
		boss_spawn_task = ThreadPoolManager.getInstance().schedule(new BossSpawnTask(), nextLaunchTime - System.currentTimeMillis());
	}
	
	public static void SpawnNPCs(int npcId, int[][] locations, List<SimpleSpawner> list)
	{
		NpcTemplate template = NpcHolder.getInstance().getTemplate(npcId);
		if(template == null)
		{
			System.out.println("WARNING! Functions.SpawnNPCs template is null for npc: " + npcId);
			Thread.dumpStack();
			return;
		}
		for(int[] location : locations)
		{
			SimpleSpawner sp = new SimpleSpawner(template);
			sp.setLoc(new Location(location[0], location[1], location[2], location[3]));
			sp.setAmount(1);
			sp.setRespawnDelay(0);
			sp.init();
			if(list != null)
				list.add(sp);
		}
	}

	/**
	 * Removes spawn Event Manager
	 */
	private void unSpawnEventManagers()
	{
		deSpawnNPCs(_spawns);
	}

	/**
	 * Status of the opening event.
	 * @return
	 */
	private static boolean isActive()
	{
		return IsActive(EVENT_NAME);
	}

	public void startEvent()
	{
		Player player = getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(SetActive(EVENT_NAME, true))
		{
			spawnEventManagers();
			System.out.println("Event: Master of Enchanting 2 started.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.MasterOfEnchanting2.AnnounceEventStarted", null);
		}
		else
			player.sendMessage("Event 'Master of Enchanting 2' already started.");

		_active = true;
		show("admin/events/events.htm", player);
	}

	public void stopEvent()
	{
		Player player = getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;
		if(SetActive(EVENT_NAME, false))
		{
			unSpawnEventManagers();
			System.out.println("Event: Master of Enchanting stopped.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.MasterOfEnchanting2.AnnounceEventStoped", null);
		}
		else
			player.sendMessage("Event 'Master of Enchanting 2' not started.");

		_active = false;
		show("admin/events/events.htm", player);
	}
	
	public void buy_staff()
	{
		Player player = getSelf();
		
		if(player == null) return;
		
		//if(getItemCount(player, MASTER_YOGI_STAFF) == 0 && getItemCount(player, ADENA) >= STAFF_PRICE)
		if(getItemCount(player, ADENA) >= STAFF_PRICE)
		{
			removeItem(player, ADENA, STAFF_PRICE);
			addItem(player, MASTER_YOGI_STAFF, 1);
			show("scripts/events/MasterOfEnchanting2/41014-staffbuyed.htm", player);
		}
		else
		{
			show("scripts/events/MasterOfEnchanting2/41014-staffcant.htm", player);
		}
	}

	public void receive_reward()
	{
		Player player = getSelf();
		
		if(player == null) return;
		
		ItemInstance enchantedItem = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
		
		if(enchantedItem == null)
		{
			show("scripts/events/MasterOfEnchanting2/41014-rewardnostaff.htm", player);
			return;
		}
		
		int Ench_Lvl = enchantedItem.getEnchantLevel();
		int Equip_Id = enchantedItem.getItemId();
		int itemId = 0;
		
		if(Equip_Id != MASTER_YOGI_STAFF)
		{
			show("scripts/events/MasterOfEnchanting2/41014-rewardnostaff.htm", player);
			return;
		}
		
		int itemRandomIndex = 0;
		switch(Ench_Lvl)
		{
			case 0:
			case 1:
			case 2:
			case 3:
				show("scripts/events/MasterOfEnchanting2/41014-rewardnostaff.htm", player);
				return;
			case 4:
				addItem(player, 13750, 5); //Warrior's Quick Healing Potion
				addItem(player, 6406, 2); // Firework
				break;
			case 5:
				addItem(player, 22038, 5); //Mana Potion
				addItem(player, 6407, 2); // Large Firework
				break;
			case 6:
				addItem(player, 1538, 3); //Blessed Scroll of Escape
				addItem(player, 6407, 2); // Large Firework
				break;
			case 7:
				addItem(player, 1538, 5); //Blessed Scroll of Escape
				addItem(player, 10649, 3); //Feather of Blessing
				break;
			case 8:
				addItem(player, 17527, 3); //Enchant Armor (R-grade)
				break;
			case 9:
				addItem(player, 17526, 3); //Enchant Weapon (R-grade)
				break;
			case 10:
				itemRandomIndex = (int) Math.round(Math.random()*SOUL_CRYSTAL_851_REWARD.length);
				itemId = SOUL_CRYSTAL_851_REWARD[itemRandomIndex];
				addItem(player, itemId, 1);
				break;
			case 11:
				itemRandomIndex = (int) Math.round(Math.random()*SOUL_CRYSTAL_951_REWARD.length);
				itemId = SOUL_CRYSTAL_951_REWARD[itemRandomIndex];
				addItem(player, itemId, 1);
				break;
			case 12:
				addItem(player, 19448, 3); //Blessed Enchant Armor (R-grade)
				break;
			case 13:
				addItem(player, 19447, 3); //Blessed Enchant Weapon (R-grade)
				break;
			case 14:
				itemRandomIndex = (int) Math.round(Math.random()*JEWEL_85_REWARD.length);
				itemId = JEWEL_85_REWARD[itemRandomIndex];
				addItem(player, itemId, 1);
				break;
			case 15:
				itemRandomIndex = (int) Math.round(Math.random()*JEWEL_95_REWARD.length);
				itemId = JEWEL_95_REWARD[itemRandomIndex];
				addItem(player, itemId, 1);
				announceWinner(player, itemId);
				player.broadcastPacket(new SocialActionPacket(player.getObjectId(), 20));
				break;
			case 16:
				itemRandomIndex = (int) Math.round(Math.random()*SOUL_CRYSTAL_2_REWARD.length);
				itemId = SOUL_CRYSTAL_2_REWARD[itemRandomIndex];
				addItem(player, itemId, 1);
				announceWinner(player, itemId);
				player.broadcastPacket(new SocialActionPacket(player.getObjectId(), 20));
				break;
			case 17:
				itemRandomIndex = (int) Math.round(Math.random()*ARMOR_85_REWARD.length);
				itemId = ARMOR_85_REWARD[itemRandomIndex];
				addItem(player, itemId, 1);
				break;
			case 18:
				itemRandomIndex = (int) Math.round(Math.random()*ARMOR_95_REWARD.length);
				itemId = ARMOR_95_REWARD[itemRandomIndex];
				addItem(player, itemId, 1);
				announceWinner(player, itemId);
				player.broadcastPacket(new SocialActionPacket(player.getObjectId(), 20));
				break;
			case 19:
				itemRandomIndex = (int) Math.round(Math.random()*WEAPON_85_REWARD.length);
				itemId = WEAPON_85_REWARD[itemRandomIndex];
				addItem(player, itemId, 1);
				announceWinner(player, itemId);
				player.broadcastPacket(new SocialActionPacket(player.getObjectId(), 20));
				break;
			case 20:
				itemRandomIndex = (int) Math.round(Math.random()*BOSS_JEWEL.length);
				itemId = BOSS_JEWEL[itemRandomIndex];
				addItem(player, itemId, 1);
				announceWinner(player, itemId);
				player.broadcastPacket(new SocialActionPacket(player.getObjectId(), 20));
				break;
			default:
				itemRandomIndex = (int) Math.round(Math.random()*BOSS_JEWEL.length);
				itemId = BOSS_JEWEL[itemRandomIndex];
				addItem(player, itemId, 1);
				announceWinner(player, itemId);
				player.broadcastPacket(new SocialActionPacket(player.getObjectId(), 20));
				break;
		}
		ItemFunctions.removeItem(player, enchantedItem, 1, true);
		show("scripts/events/MasterOfEnchanting2/41014-rewardok.htm", player);
		
	}

	public void announceWinner(Player winner, int itemID)
	{
		if(winner == null) return;
		
		ItemTemplate t = ItemHolder.getInstance().getTemplate(itemID);
		
		if(t == null) return;
		
		String msg = "Chúc mừng " + winner.getName() + " vừa nhận được:" + t.getName();		

		announceAllServer(msg);
	}

	public void announceBossKiller(Player winner, int itemCount)
	{
		if(winner == null) return;
		
		String msg = winner.getName() + " vừa giết Balok tại vườn hoa Aden.\n             Balok làm rơi " + itemCount + " Enchant Scroll of Luck";		
		
		announceAllServer(msg);
	}
	
	public void announceBossStatus()
	{
		String msg = "Balok đang xuất hiện tại vườn hoa Aden. Mang trên người " + dropScrollCount + " Enchant Scroll of Luck";
		
		announceAllServer(msg);
	}
	
	public void announceAllServer(String msg)
	{
		for(Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			player.sendPacket(new ExShowScreenMessage(msg, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
		}
	}
	
	public void show_cube()
	{
		Player player = getSelf();
		ItemInstance Cube_Blessing = player.getInventory().getItemByItemId(CUBE_OF_BLESSING);
		
		if(Cube_Blessing == null)
		{
			show("scripts/events/MasterOfEnchanting2/41014-rewardnocube.htm", player);
			return;
		}
		
		if(Cube_Blessing.getCount() >= 10)
		{
			show("scripts/events/MasterOfEnchanting2/41014-cube10.htm", player);
		}
		else if(Cube_Blessing.getCount() >= 8)
		{
			show("scripts/events/MasterOfEnchanting2/41014-cube8.htm", player);
		}
		else if(Cube_Blessing.getCount() >= 7)
		{
			show("scripts/events/MasterOfEnchanting2/41014-cube7.htm", player);
		}
		else if(Cube_Blessing.getCount() >= 6)
		{
			show("scripts/events/MasterOfEnchanting2/41014-cube6.htm", player);
		}
		else if(Cube_Blessing.getCount() >= 5)
		{
			show("scripts/events/MasterOfEnchanting2/41014-cube5.htm", player);
		}
		else if(Cube_Blessing.getCount() >= 3)
		{
			show("scripts/events/MasterOfEnchanting2/41014-cube3.htm", player);
		}
		else if(Cube_Blessing.getCount() >= 1)
		{
			show("scripts/events/MasterOfEnchanting2/41014-cube1.htm", player);
		}
	}

	public void show_item(String[] arg)
	{
		int item_category = Integer.parseInt(arg[0]);
		
		Player player = getSelf();
		
		ItemInstance Cube_Blessing = player.getInventory().getItemByItemId(CUBE_OF_BLESSING);
		
		if(Cube_Blessing == null)
		{
			show("scripts/events/MasterOfEnchanting2/41014-rewardnocube.htm", player);
			return;
		}
		
		switch(item_category)
		{
			case 1:
				if(Cube_Blessing.getCount() >= 1)
					show("scripts/events/MasterOfEnchanting2/41014-jewel85.htm", player);
				else
					show("scripts/events/MasterOfEnchanting2/41014-rewardnocube.htm", player);
			break;
			case 2:
				if(Cube_Blessing.getCount() >= 3)
					show("scripts/events/MasterOfEnchanting2/41014-jewel95.htm", player);
				else
					show("scripts/events/MasterOfEnchanting2/41014-rewardnocube.htm", player);
			break;
			case 3:
				if(Cube_Blessing.getCount() >= 5)
					show("scripts/events/MasterOfEnchanting2/41014-soulcrystal.htm", player);
				else
					show("scripts/events/MasterOfEnchanting2/41014-rewardnocube.htm", player);
			break;
			case 4:
				if(Cube_Blessing.getCount() >= 6)
					show("scripts/events/MasterOfEnchanting2/41014-armor85.htm", player);
				else
					show("scripts/events/MasterOfEnchanting2/41014-rewardnocube.htm", player);
			break;
			case 5:
				if(Cube_Blessing.getCount() >= 7)
					show("scripts/events/MasterOfEnchanting2/41014-armor95.htm", player);
				else
					show("scripts/events/MasterOfEnchanting2/41014-rewardnocube.htm", player);
			break;
			case 6:
				if(Cube_Blessing.getCount() >= 8)
					show("scripts/events/MasterOfEnchanting2/41014-weapon85.htm", player);
				else
					show("scripts/events/MasterOfEnchanting2/41014-rewardnocube.htm", player);
			break;
			case 7:
				if(Cube_Blessing.getCount() >= 10)
					show("scripts/events/MasterOfEnchanting2/41014-jewelboss.htm", player);
				else
					show("scripts/events/MasterOfEnchanting2/41014-rewardnocube.htm", player);
			break;
		}
			
	}
	
	public void buy_gear_cube(String[] arg)
	{
		Player player = getSelf();

		if(player == null) return;
		
		if(!_active || player.isActionsDisabled() || player.isSitting() || player.getLastNpc() == null || player.getLastNpc().getDistance(player) > 300)
			return;

		int item_choice = Integer.parseInt(arg[0]) - 1;
		
		ItemInstance Cube_Blessing = player.getInventory().getItemByItemId(CUBE_OF_BLESSING);
		
		if(Cube_Blessing == null)
		{
			show("scripts/events/MasterOfEnchanting2/41014-rewardnocube.htm", player);
			return;
		}
		
		int cube_count = (int) Cube_Blessing.getCount();
		
		ItemInstance enchantedItem = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
		
		if(enchantedItem == null)
		{
			show("scripts/events/MasterOfEnchanting2/41014-rewardnostaff.htm", player);
			return;
		}
		
		int Ench_Lvl = enchantedItem.getEnchantLevel();
		int Equip_Id = enchantedItem.getItemId();
		int itemId = 0;
		
		if(Equip_Id != MASTER_YOGI_STAFF || Ench_Lvl < 14)
		{
			show("scripts/events/MasterOfEnchanting2/41014-rewardnostaff.htm", player);
			return;
		}
		
		switch(Ench_Lvl)
		{
			case 14:
				if(item_choice < 0 || item_choice >= JEWEL_85_REWARD.length)
					return;
				ItemFunctions.removeItem(player, enchantedItem, 1, true);
				removeItem(player, CUBE_OF_BLESSING, 1);
				itemId = JEWEL_85_REWARD[item_choice];
				addItem(player, itemId, 1);
				announceWinner(player, itemId);
				show("scripts/events/MasterOfEnchanting2/41014-rewardok.htm", player);

				player.broadcastPacket(new SocialActionPacket(player.getObjectId(), 20));
			break;
			case 15:
				if(item_choice < 0 || item_choice >= JEWEL_95_REWARD.length)
					return;

				if(cube_count < 3)
				{
					show("scripts/events/MasterOfEnchanting2/41014-rewardnocube.htm", player);
					return;
				}
				
				ItemFunctions.removeItem(player, enchantedItem, 1, true);
				removeItem(player, CUBE_OF_BLESSING, 3);
				itemId = JEWEL_95_REWARD[item_choice];
				addItem(player, itemId, 1);
				announceWinner(player, itemId);
				show("scripts/events/MasterOfEnchanting2/41014-rewardok.htm", player);

				player.broadcastPacket(new SocialActionPacket(player.getObjectId(), 20));
			break;
			case 16:
				if(item_choice < 0 || item_choice >= SOUL_CRYSTAL_2_REWARD.length)
					return;
				
				if(cube_count < 5)
				{
					show("scripts/events/MasterOfEnchanting2/41014-rewardnocube.htm", player);
					return;
				}
				ItemFunctions.removeItem(player, enchantedItem, 1, true);
				removeItem(player, CUBE_OF_BLESSING, 5);
				itemId = SOUL_CRYSTAL_2_REWARD[item_choice];
				addItem(player, itemId, 1);
				announceWinner(player, itemId);
				show("scripts/events/MasterOfEnchanting2/41014-rewardok.htm", player);

				player.broadcastPacket(new SocialActionPacket(player.getObjectId(), 20));
			break;
			case 17:
				if(item_choice < 0 || item_choice >= ARMOR_85_REWARD.length)
					return;
				
				if(cube_count < 6)
				{
					show("scripts/events/MasterOfEnchanting2/41014-rewardnocube.htm", player);
					return;
				}
				ItemFunctions.removeItem(player, enchantedItem, 1, true);
				removeItem(player, CUBE_OF_BLESSING, 6);
				itemId = ARMOR_85_REWARD[item_choice];
				addItem(player, itemId, 1);
				announceWinner(player, itemId);
				show("scripts/events/MasterOfEnchanting2/41014-rewardok.htm", player);

				player.broadcastPacket(new SocialActionPacket(player.getObjectId(), 20));
			break;
			case 18:
				if(item_choice < 0 || item_choice >= ARMOR_95_REWARD.length)
					return;
				
				if(cube_count < 7)
				{
					show("scripts/events/MasterOfEnchanting2/41014-rewardnocube.htm", player);
					return;
				}
				ItemFunctions.removeItem(player, enchantedItem, 1, true);
				removeItem(player, CUBE_OF_BLESSING, 7);
				itemId = ARMOR_95_REWARD[item_choice];
				addItem(player, itemId, 1);
				announceWinner(player, itemId);
				show("scripts/events/MasterOfEnchanting2/41014-rewardok.htm", player);

				player.broadcastPacket(new SocialActionPacket(player.getObjectId(), 20));
			break;
			case 19:
				if(item_choice < 0 || item_choice >= WEAPON_85_REWARD.length)
					return;
				
				if(cube_count < 8)
				{
					show("scripts/events/MasterOfEnchanting2/41014-rewardnocube.htm", player);
					return;
				}
				
				ItemFunctions.removeItem(player, enchantedItem, 1, true);
				removeItem(player, CUBE_OF_BLESSING, 7);
				itemId = WEAPON_85_REWARD[item_choice];
				addItem(player, itemId, 1);
				announceWinner(player, itemId);
				show("scripts/events/MasterOfEnchanting2/41014-rewardok.htm", player);

				player.broadcastPacket(new SocialActionPacket(player.getObjectId(), 20));
			break;
			case 20:
				if(item_choice < 0 || item_choice >= BOSS_JEWEL.length)
					return;
				
				if(cube_count < 10)
				{
					show("scripts/events/MasterOfEnchanting2/41014-rewardnocube.htm", player);
					return;
				}
				
				ItemFunctions.removeItem(player, enchantedItem, 1, true);
				removeItem(player, CUBE_OF_BLESSING, 10);
				itemId = BOSS_JEWEL[item_choice];
				addItem(player, itemId, 1);
				announceWinner(player, itemId);
				show("scripts/events/MasterOfEnchanting2/41014-rewardok.htm", player);

				player.broadcastPacket(new SocialActionPacket(player.getObjectId(), 20));
			break;
				
		}

	}

	@Override
	public void onLoad()
	{
		CharListenerList.addGlobal(this);
		if(isActive())
		{
			_active = true;
			spawnEventManagers();
			spawnEventBoss();
			scheduleNextBossSpawn();
			
			_log.info("Event Master of Enchanting 2 has been ACTIVATED");
		}
		else
			_log.info("Event Master of Enchanting 2 has been DEACTIVATED");
	}

	@Override
	public void onReload()
	{
		unSpawnEventManagers();
	}

	@Override
	public void onShutdown()
	{
		unSpawnEventManagers();
	}

	@Override
	public void onPlayerEnter(Player player)
	{
		if(_active)
			Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.MasterOfEnchanting2.AnnounceEventStarted", null);
	}

	@Override
	public void onDeath(Creature victim, Creature killer)
	{
		if(!_active)
			return;
		
		if(victim == null || killer == null)
		{
			//System.out.println("victim or killer is null");
			return;
		}
			
			
		//the normal case: player hunts monster to get scrolls
		if(victim.isMonster() && killer.isPlayable())
		{

			//the case when the boss die
			if(victim.getNpcId() == ADEN_BOSS)
			{
				calculateRewards((MonsterInstance)victim, killer);
				
				aden_boss = null;
			}
			else
			{
				if(!SimpleCheckDrop(victim, killer))
					return;
		
				if(victim.getLevel() < MONSTER_LEVEL_MIN)
					return;
				
				Player player = killer.getPlayer();
				
				if(player == null) return;
		
				double dropChance = DEFAULT_DROP_CHANCE;
		
				//calculate drop chance
				if(!Rnd.chance(dropChance))
					return;
		
				// calculate drop quantity
				long count = Rnd.get(MIN_DROP, MAX_DROP);
		
				if(player.isInParty()){
					ItemInstance item = ItemFunctions.createItem(ENCHANTING_SCROLL);
					item.setCount(count);
					player.getParty().distributeItem(player, item, (NpcInstance) victim);
				}else{
					addItem(player, ENCHANTING_SCROLL, count);
				}
			}
		}
		
		
		//the case when player die
		if(victim.isPlayer() && !victim.isServitor() && !victim.isPet())
		{
			if(aden_boss == null)
				return;
			double distance = victim.getDistance(aden_boss);
			
			if(distance > 1200)
				return;
			
			int minScrollDrop = 1;
			int maxScrollDrop = 10;
			
			//boss kill player
			if(killer.isMonster() && killer.getNpcId() == ADEN_BOSS)
			{
				minScrollDrop = 1;
				maxScrollDrop = 1;
			}
			else if(killer.isPlayer())
			{
				//player pvp player
				if(killer.getKarma() >= 0)
				{
					//no pk
					minScrollDrop = 1;
					maxScrollDrop = 5;
				}
				else //player pk player
				{
					//pk
					minScrollDrop = 5;
					maxScrollDrop = 10;
				}
			}
			else
				return;
			
			bodyCount++;

			//heal 5% of the boss' HP
			aden_boss.setCurrentHp(aden_boss.getCurrentHp() + aden_boss.getMaxHp()*0.1, false);
			
			if(dropScrollCount < maxBonusScroll)
			{
				
				dropScrollCount += (int)(minScrollDrop + Math.random()*(maxScrollDrop - minScrollDrop));
				
				if(dropScrollCount > maxBonusScroll) 
					dropScrollCount = maxBonusScroll;
			}
			
			String msg = "Nạn nhân thứ " + bodyCount + " của Balok vừa ngã xuống.\n           Số Enchant Scroll of Luck: " + dropScrollCount;		
			
			announceAllServer(msg);
				
		}
	}
	
	public static boolean SimpleCheckDrop(Creature mob, Creature killer)
	{
		return mob != null && mob.isMonster() && !mob.isRaid() && killer != null && killer.getPlayer() != null && killer.getLevel() - mob.getLevel() < 4;
	}
	
	public void calculateRewards(MonsterInstance monster, Creature lastAttacker)
	{
		//System.out.println("calculateRewards " + lastAttacker.getName());
		Creature topDamager = monster.getAggroList().getTopDamager();
		if(lastAttacker == null || !lastAttacker.isPlayable())
			lastAttacker = topDamager;

		if(lastAttacker == null || !lastAttacker.isPlayable())
			return;

		Player killer = lastAttacker.getPlayer();
		if(killer == null)
			return;

		if(topDamager != null && topDamager.isPlayable())
		{
			dropItem(monster, killer, ENCHANTING_SCROLL, dropScrollCount);
			announceBossKiller(killer, dropScrollCount);
		}

	}
	
	public void dropItem(MonsterInstance monster, Player lastAttacker, int itemId, long itemCount)
	{
		//System.out.println("dropItem " + itemCount);
		if(itemCount == 0 || lastAttacker == null)
			return;

		ItemInstance item;

		for(long i = 0; i < itemCount; i++)
		{
			item = ItemFunctions.createItem(itemId);

			// Set the Item quantity dropped if ItemInstance is stackable
			//lastAttacker.doAutoLootOrDrop(item, monster);
			
			item.dropToTheGround(monster, Location.coordsRandomize(monster.getLoc(), 50, 500));
		}
	}
	

	public class BossSpawnTask extends RunnableImpl
	{
		public BossSpawnTask()
		{
			//
		}

		@Override
		public void runImpl()
		{
			if(aden_boss == null)
			{
				spawnEventBoss();
			}
			
			scheduleNextBossSpawn();
		}

	}

}
