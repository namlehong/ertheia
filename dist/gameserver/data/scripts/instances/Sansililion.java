package instances;

import java.util.concurrent.ScheduledFuture;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExSendUIEventPacket;
import l2s.gameserver.utils.Location;

/**
 * Класс контролирует инстанс Sansililion
 *
 * @author coldy
 */
public class Sansililion extends Reflection
{
	public long _startedTime = 0;
	public long _endTime = 0;
	public static int _points = 0;
	public int _lastBuff = 0;
	private static int _status = 0;
	private ScheduledFuture<?> _updateUITask;
	private ScheduledFuture<?> _stopInstance;

	public void startWorld()
	{
		_status = 1;
		_startedTime = System.currentTimeMillis();
		_endTime = _startedTime + 1800000L;
		_stopInstance = ThreadPoolManager.getInstance().schedule(new StopInstance(), 1800000L);
		_updateUITask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new updateUITask(), 100L, 1000L);
		_points = 0;
	}

	public void updateTimer()
	{
		int timerStatus = 3;
		if(_status == 2)
			timerStatus = 1;

		int timeLeft = (int)((_endTime - System.currentTimeMillis()) / 1000L);
		timeLeft = Math.max(0, timeLeft);
		for(Player player : getPlayers())
			player.sendPacket(new ExSendUIEventPacket(player, timerStatus, timeLeft, _points, 60, NpcString.ELAPSED_TIME__)); //[TODO] not updating _points for some reason =/
		//System.out.println("points = "+_points+" status = "+_status+" ");
			
	}

	protected class updateUITask extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if(this == null)
				return;
			updateTimer();
		}
	}

	protected class StopInstance extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if(this == null)
				return;

			if(_status == 1)
			{
				_status = 2;
				for(NpcInstance npc : getNpcs())
				{
					if(npc.getNpcId() == 33152)
						continue;
					npc.deleteMe();
				}

				if(_updateUITask != null)
				{
					_updateUITask.cancel(false);
					_updateUITask = null;
				}
			}
		}
	}

	public int getStatus()
	{
		return _status;
	}
}