package spawns;

import java.util.concurrent.Future;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.instancemanager.SpawnManager;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author Bonux
 */
public class MammonsManager implements ScriptFile
{
	private static final long RESPAWN_DELAY = 1800000L; // Через каждые 30 минут меняем локацию. Проверить на оффе.
	private static final String[] SPAWN_GROUPS = {
			"gludin_mammons",
			"gludio_mammons",
			"dion_mammons",
			"giran_mammons",
			"oren_mammons",
			"hunters_mammons",
			"aden_mammons",
			"rune_mammons",
			"goddard_mammons",
			"heine_mammons",
			"schuttgart_mammons"
		};

	private int _currentSpawnedGroup = -1;
	private Future<?> _respawnTask;

	@Override
	public void onLoad()
	{
		init();
	}

	@Override
	public void onReload()
	{
		stopRespawnTask();
		init();
	}

	@Override
	public void onShutdown()
	{}

	private void init()
	{
		_currentSpawnedGroup = Rnd.get(SPAWN_GROUPS.length);

		String groupName = SPAWN_GROUPS[_currentSpawnedGroup];
		SpawnManager.getInstance().spawn(groupName);
		SpawnManager.getInstance().spawn(groupName + "_priest_" + Rnd.get(1, 2));
		_respawnTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new RespawnTask(), RESPAWN_DELAY, RESPAWN_DELAY);
	}

	private void stopRespawnTask()
	{
		if(_respawnTask != null)
		{
			_respawnTask.cancel(false);
			_respawnTask = null;
		}
	}

	private class RespawnTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			int newSpawnGroup = 0;
			if(SPAWN_GROUPS.length > 1) // Чтобы группы спавна не повторялись.
			{
				newSpawnGroup = Rnd.get(SPAWN_GROUPS.length);
				while(newSpawnGroup == _currentSpawnedGroup)
				{
					newSpawnGroup = Rnd.get(SPAWN_GROUPS.length);
				}
			}

			String groupName = SPAWN_GROUPS[_currentSpawnedGroup];
			SpawnManager.getInstance().despawn(groupName);
			SpawnManager.getInstance().despawn(groupName + "_priest_1");
			SpawnManager.getInstance().despawn(groupName + "_priest_2");

			groupName = SPAWN_GROUPS[newSpawnGroup];
			SpawnManager.getInstance().spawn(groupName);
			SpawnManager.getInstance().spawn(groupName + "_priest_" + Rnd.get(1, 2));

			_currentSpawnedGroup = newSpawnGroup;
		}
	}
}