package l2s.gameserver.taskmanager.tasks;

import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.ExWorldChatCnt;
import l2s.gameserver.taskmanager.Task;
import l2s.gameserver.taskmanager.TaskManager;
import l2s.gameserver.taskmanager.TaskManager.ExecutedTask;
import l2s.gameserver.taskmanager.TaskTypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bonux
**/
public class TaskWorldChat extends Task
{
	private static final Logger _log = LoggerFactory.getLogger(TaskWorldChat.class);

	private static final String NAME = "sp_worldchat";

	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public void onTimeElapsed(ExecutedTask task)
	{
		_log.info("World Chat Global Task: launched.");
		for(Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			player.setUsedWorldChatPoints(0);
			player.sendPacket(new ExWorldChatCnt(player));
		}
		_log.info("World Chat Global Task: completed.");
	}

	@Override
	public void initializate()
	{
		TaskManager.addUniqueTask(NAME, TaskTypes.TYPE_GLOBAL_TASK, "1", "06:30:00", "");
	}
}