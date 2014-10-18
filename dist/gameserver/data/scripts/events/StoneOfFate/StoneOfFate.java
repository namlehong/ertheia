package events.StoneOfFate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import l2s.commons.util.Rnd;
import l2s.gameserver.Announcements;
import l2s.gameserver.Config;
import l2s.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.SimpleSpawner;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.scripts.ScriptFile;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: Hien Son
 */
public class StoneOfFate extends Functions implements ScriptFile, OnPlayerEnterListener
{
	private static final Logger _log = LoggerFactory.getLogger(StoneOfFate.class);
	
	// Медали
	private static int STONE_OF_FATE = 92020;
	private static int SCROLL_OF_SP = 30275;
	private static int CLASS_MASTER = 31860;
	//loc -114552 -250200 -3018
	private static String EVENT_NAME = "StoneOfFate";

	private static List<SimpleSpawner> _spawns = new ArrayList<SimpleSpawner>();
	private static boolean _active = false;

	@Override
	public void onLoad()
	{
		CharListenerList.addGlobal(this);
		if(isActive())
		{
			_active = true;
			_log.info("Loaded Event: Stone Of Fate [state: activated]");
		}
		else
			_log.info("Loaded Event: Stone Of Fate [state: deactivated]");
		
		
	}

	/**
	 * Читает статус эвента из базы.
	 *
	 * @return
	 */
	private static boolean isActive()
	{
		return IsActive(EVENT_NAME);
	}

	/**
	 * Запускает эвент
	 */
	public void startEvent()
	{
		Player player = getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(SetActive(EVENT_NAME, true))
		{
			spawnEventManagers();
			System.out.println("Event 'Stone Of Fate' started.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.StoneOfFate.AnnounceEventStarted", null);
		}
		else
			player.sendMessage("Event 'Stone Of Fate' already started.");

		_active = true;

		show("admin/events/events.htm", player);
	}

	/**
	 * Останавливает эвент
	 */
	public void stopEvent()
	{
		Player player = getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;
		if(SetActive(EVENT_NAME, false))
		{	
			unSpawnEventManagers();
			System.out.println("Event 'Stone Of Fate' stopped.");
			Announcements.getInstance().announceByCustomMessage("scripts.events.StoneOfFate.AnnounceEventStopped", null);
		}
		else
			player.sendMessage("Event 'Stone Of Fate' not started.");

		_active = false;

		show("admin/events/events.htm", player);
	}

	@Override
	public void onPlayerEnter(Player player)
	{
		if(_active)
		{
			Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.StoneOfFate.AnnounceEventStarted", null);
			String varxp;
			varxp = player.getVar("StoneOfFate");
			if(varxp == null)
			{
				ItemFunctions.addItem(player, STONE_OF_FATE, 1, true);
				player.setVar("StoneOfFate", "received", -1);
			}
			
			String varsp;
			varsp = player.getVar("StoneOfFateSP");
			if(varsp == null)
			{
				ItemFunctions.addItem(player, SCROLL_OF_SP, 3, true);
				player.setVar("StoneOfFateSP", "received", -1);
			}
		}
	}

	private void spawnEventManagers()
	{
		// 1й эвент кот
		final int CLASS_MASTER_SPAWN[][] = {{ -114552, -250200, -3018, 0 } };

		SpawnNPCs(CLASS_MASTER, CLASS_MASTER_SPAWN, _spawns);
	}
	
	private void unSpawnEventManagers()
	{
		deSpawnNPCs(_spawns);
	}
	
	private void reSpawnEventManagers()
	{
		unSpawnEventManagers();
		spawnEventManagers();
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

}