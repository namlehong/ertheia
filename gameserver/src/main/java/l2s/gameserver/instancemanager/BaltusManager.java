package l2s.gameserver.instancemanager;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

/**
 * @Author Awakeninger
 * @Date: 26.04.2013
 **/
public class BaltusManager
{
	private static final Logger _log = LoggerFactory.getLogger(BaltusManager.class);
	private static BaltusManager _instance;
	private static final long _taskDelay = 1 * 1000L;
	private static final int first = 1;
	private static final int second = 15;

	public static BaltusManager getInstance()
	{
		if(_instance == null)
		{
			_instance = new BaltusManager();
		}
		return _instance;
	}

	public BaltusManager()
	{
		ThreadPoolManager.getInstance().schedule(new ChangeStage(), _taskDelay);
	}

	private class ChangeStage extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			if(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 15 || Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 1)
			{
				SpawnManager.getInstance().spawn("antharas_instance");
				_log.info("Open registration for antharas instance ");
			}
			else
			{
				SpawnManager.getInstance().despawn("antharas_instance");
			}
		}
	}

}