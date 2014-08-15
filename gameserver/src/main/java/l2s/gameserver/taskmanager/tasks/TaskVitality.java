package l2s.gameserver.taskmanager.tasks;

import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.taskmanager.Task;
import l2s.gameserver.taskmanager.TaskManager;
import l2s.gameserver.taskmanager.TaskManager.ExecutedTask;
import l2s.gameserver.taskmanager.TaskTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskVitality extends Task
{
	private static final Logger _log = LoggerFactory.getLogger(TaskVitality.class);
	private static final String NAME = "sp_vitality";

	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public void onTimeElapsed(ExecutedTask task)
	{
		_log.info("Vitality Global Task: launched.");
		for(Player player : GameObjectsStorage.getAllPlayersForIterate())
			player.restartVitality(false);
		_log.info("Vitality Global Task: completed.");
	}

	@Override
	public void initializate()
	{
		TaskManager.addUniqueTask(NAME, TaskTypes.TYPE_GLOBAL_TASK, "7", "06:30:00", "4");
	}
}