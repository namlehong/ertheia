package events;

import l2s.gameserver.instancemanager.SpawnManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.scripts.ScriptFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bonux
**/
public class HeroesPower extends Functions implements ScriptFile
{
	private static final Logger _log = LoggerFactory.getLogger(HeroesPower.class);

	private static final String EM_SPAWN_GROUP = "heroes_power_event";
	private static boolean _active = false;

	/**
	 * Читает статус эвента из базы.
	 * @return
	 */
	private static boolean isActive()
	{
		return IsActive("HeroesPower");
	}

	/**
	 * Запускает эвент
	 */
	public void startEvent()
	{
		Player player = getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(SetActive("HeroesPower", true))
		{
			_log.info("Event: 'Heroes Power' started.");

			SpawnManager.getInstance().spawn(EM_SPAWN_GROUP);
		}
		else
			player.sendMessage("Event 'Heroes Power' already started.");

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

		if(SetActive("HeroesPower", false))
		{
			_log.info("Event: 'Heroes Power' stopped.");

			SpawnManager.getInstance().despawn(EM_SPAWN_GROUP);
		}
		else
			player.sendMessage("Event: 'Heroes Power' not started.");

		_active = false;
		show("admin/events/events.htm", player);
	}

	@Override
	public void onLoad()
	{
		if(isActive())
		{
			_active = true;
			_log.info("Loaded Event: Heroes Power [state: activated]");

			SpawnManager.getInstance().spawn(EM_SPAWN_GROUP);
		}
		else
			_log.info("Loaded Event: Heroes Power [state: deactivated]");
	}

	@Override
	public void onReload()
	{
		//
	}

	@Override
	public void onShutdown()
	{
		//
	}
}