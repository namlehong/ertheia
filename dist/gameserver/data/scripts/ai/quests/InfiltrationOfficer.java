package ai.quests;

import java.util.List;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.Location;

/**
 * @author Bonux
 */
public class InfiltrationOfficer extends DefaultAI
{
	private static final int MOVE_TIMER_ID = -1000;

	private List<Location> _moveWay = null;
	private int _moveDelay = 0;

	public InfiltrationOfficer(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	protected void onEvtArrived()
	{
		NpcInstance actor = getActor();
		if(actor == null)
			return;

		actor.setSpawnedLoc(actor.getLoc());

		nextMove();
	}

	@Override
	protected void onEvtTimer(int timerId, Object arg1, Object arg2)
	{
		if(timerId == MOVE_TIMER_ID)
		{
			if(!(arg1 instanceof Location))
				return;

			moveToLocation((Location) arg1);
		}
	}

	public void moveByWay(List<Location> way, int delay, boolean running)
	{
		NpcInstance actor = getActor();
		if(actor == null)
			return;

		if(running)
			actor.setRunning();

		_moveWay = way;
		_moveDelay = delay;

		nextMove();
	}

	private void nextMove()
	{
		if(_moveWay != null)
		{
			if(!_moveWay.isEmpty())
			{
				Location loc = _moveWay.remove(0);
				if(_moveDelay > 0)
					addTimer(MOVE_TIMER_ID, loc, _moveDelay * 1000L);
				else
					moveToLocation(loc);
			}
			else
			{
				_moveWay = null;
				_moveDelay = 0;
			}
		}
	}

	private void moveToLocation(Location loc)
	{
		NpcInstance actor = getActor();
		if(actor == null)
			return;

		if(!actor.moveToLocation(loc, 0, false))
		{
			clientStopMoving();
			actor.teleToLocation(loc);
		}
	}
}