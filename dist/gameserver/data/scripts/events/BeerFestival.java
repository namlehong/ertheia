package events;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.instancemanager.SpawnManager;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.scripts.ScriptFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bonux
**/
public class BeerFestival extends Functions implements ScriptFile, OnDeathListener
{
	private static final Logger _log = LoggerFactory.getLogger(BeerFestival.class);

	private static final String EM_SPAWN_GROUP = "beer_fest_event";
	private static final int BEER_TANKARD = 23318; // Пивной Бочонок
	private static final double TANKARD_CHANCE = 0.5; // Пивной Бочонок
	private static final int[][] DROP_LIST = new int[][] {
		{ 23314, 10 }, // Ингредиенты для Пива - Солод
		{ 23315, 10 }, // Ингредиенты для Пива - Хмель
		{ 23316, 5 }   // Ингредиенты для Пива - Дрожжи
	};
	private static boolean _active = false;

	/**
	 * Читает статус эвента из базы.
	 * @return
	 */
	private static boolean isActive()
	{
		return IsActive("BeerFestival");
	}

	/**
	 * Обработчик смерти мобов, управляющий эвентовым дропом
	 */
	@Override
	public void onDeath(Creature cha, Creature killer)
	{
		if(!_active)
			return;

		if(SimpleCheckDrop(cha, killer))
		{
			if(Rnd.chance(TANKARD_CHANCE * Config.EVENT_BEER_FESTIVAL_DROP_MOD))
			{
				((NpcInstance) cha).dropItem(killer.getPlayer(), BEER_TANKARD, 1);
				return;
			}

			TIntList drop = new TIntArrayList();
			for(int[] d : DROP_LIST)
			{
				if(Rnd.chance(d[1] * Config.EVENT_BEER_FESTIVAL_DROP_MOD))
					drop.add(d[0]);
			}

			if(!drop.isEmpty())
				((NpcInstance) cha).dropItem(killer.getPlayer(), drop.get(Rnd.get(drop.size())), 1);
		}
	}

	/**
	 * Запускает эвент
	 */
	public void startEvent()
	{
		Player player = getSelf();
		if(!player.getPlayerAccess().IsEventGm)
			return;

		if(SetActive("BeerFestival", true))
		{
			_log.info("Event: 'Beer Festival' started.");

			SpawnManager.getInstance().spawn(EM_SPAWN_GROUP);
		}
		else
			player.sendMessage("Event 'Beer Festival' already started.");

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

		if(SetActive("BeerFestival", false))
		{
			_log.info("Event: 'Beer Festival' stopped.");

			SpawnManager.getInstance().despawn(EM_SPAWN_GROUP);
		}
		else
			player.sendMessage("Event: 'Beer Festival' not started.");

		_active = false;
		show("admin/events/events.htm", player);
	}

	@Override
	public void onLoad()
	{
		CharListenerList.addGlobal(this);
		if(isActive())
		{
			_active = true;
			_log.info("Loaded Event: Beer Festival [state: activated]");

			SpawnManager.getInstance().spawn(EM_SPAWN_GROUP);
		}
		else
			_log.info("Loaded Event: Beer Festival [state: deactivated]");
	}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}
}