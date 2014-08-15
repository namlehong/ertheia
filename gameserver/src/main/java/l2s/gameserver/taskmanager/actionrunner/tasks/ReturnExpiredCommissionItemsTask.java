package l2s.gameserver.taskmanager.actionrunner.tasks;

import l2s.gameserver.instancemanager.CommissionManager;

/**
 * @author Bonux
 */
public class ReturnExpiredCommissionItemsTask extends AutomaticTask
{
	public ReturnExpiredCommissionItemsTask()
	{
		super();
	}

	@Override
	public void doTask() throws Exception
	{
		CommissionManager.getInstance().returnExpiredItems();
	}

	@Override
	public long reCalcTime(boolean start)
	{
		// Выполняем таск каждые 60 секунд.
		return System.currentTimeMillis() + 60000L;
	}
}
