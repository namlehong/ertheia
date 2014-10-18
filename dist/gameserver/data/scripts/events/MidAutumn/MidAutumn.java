package events.MidAutumn;

import java.util.ArrayList;
import java.util.List;

import l2s.commons.util.Rnd;
import l2s.gameserver.Announcements;
import l2s.gameserver.GameTimeController;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.SimpleSpawner;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.scripts.ScriptFile;
import l2s.gameserver.utils.ItemFunctions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: Hien Son
 * 
 */
public class MidAutumn extends Functions implements ScriptFile, OnDeathListener, OnPlayerEnterListener
{
	private static int EVENT_MANAGER_MERCHANT = 41013; // Thợ Làm Bánh

	private static final Logger _log = LoggerFactory.getLogger(MidAutumn.class);

	private static int MOON_ESSENCE 				= 90003;
	private static int AGATHION_NEOLITHICA 			= 21870;

	private static int MONSTER_LEVEL_MIN 			= 1;
	private static int MIN_DROP						= 0;
	private static int MAX_DROP 					= 1;
	private static double DEFAULT_DROP_CHANCE 		= 10.;
	private static int NEXT_STAGE_MONSTER_LEVEL 	= 85;
	private static int NEXT_STAGE_BONUS 			= 1;
	private static int NIGHT_BONUS_MODIFIER 		= 2;
	private static int AGATHION_BONUS_MODIFIER 		= 2;

	private static String EVENT_NAME = "MidAutumn";

	private static List<SimpleSpawner> _spawns = new ArrayList<SimpleSpawner>();
	
	private static boolean _active = false;

	@Override
	public void onLoad()
	{
		CharListenerList.addGlobal(this);
		if(isActive())
		{
			_active = true;
			spawnEventManagers();
			_log.info("Loaded Event: Mid-Autumn [state: activated]");
		}
		else
			_log.info("Loaded Event: Mid-Autumn [state: deactivated]");
		
		
	}

	
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
			System.out.println("Event 'Seven Sins' started.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.MidAutumn.AnnounceEventStarted", null);
		}
		else
			player.sendMessage("Event 'Seven Sins' already started.");

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
			System.out.println("Event 'Mid Autumn' stopped.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.MidAutumn.AnnounceEventStoped", null);
		}
		else
			player.sendMessage("Event 'Mid Autumn' not started.");

		_active = false;

		show("admin/events/events.htm", player);
	}

	@Override
	public void onPlayerEnter(Player player)
	{
		if(_active)
		{
			Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.MidAutumn.AnnounceEventStarted", null);
		}
	}

	private void spawnEventManagers()
	{
		
		final int EVENT_MANAGER_MERCHANT_SPAWN[][] = {
				{ 147560, 25592, -2016, 16383 }
				 };

		SpawnNPCs(EVENT_MANAGER_MERCHANT, EVENT_MANAGER_MERCHANT_SPAWN, _spawns);
	}
	
	private void unSpawnEventManagers()
	{
		deSpawnNPCs(_spawns);
	}

	@Override
	public void onReload()
	{
		unSpawnEventManagers();
	}

	@Override
	public void onShutdown()
	{

	}

	@Override
	public void onDeath(Creature npc, Creature killer)
	{
		if(!_active)
			return;
		
		if(!SimpleCheckDrop(npc, killer))
			return;

		if(npc.getLevel() < MONSTER_LEVEL_MIN)
			return;
		
		Player player = killer.getPlayer();

		int multiplier = 1;
		double dropChance = DEFAULT_DROP_CHANCE;

		// double reward by special item
		if(player.getInventory().getItemsByItemId(AGATHION_NEOLITHICA).size() > 0)
			multiplier *= AGATHION_BONUS_MODIFIER;

		// double chance by night
		if(GameTimeController.getInstance().isNowNight())
			dropChance *= NIGHT_BONUS_MODIFIER;

		// let roll
		if(!Rnd.chance(dropChance))
			return;

		// calculate drop quatity
		long count = Rnd.get(MIN_DROP, MAX_DROP);

		// if npc is over 85, give more reward
		if(npc.getLevel() >= NEXT_STAGE_MONSTER_LEVEL)
			count += NEXT_STAGE_BONUS;
		
		if(player.isInParty()){
			ItemInstance item = ItemFunctions.createItem(MOON_ESSENCE);
			item.setCount(count);
			player.getParty().distributeItem(player, item, (NpcInstance) npc);
		}else{
			addItem(player, MOON_ESSENCE, count*multiplier);
		}
			
		
	}
}