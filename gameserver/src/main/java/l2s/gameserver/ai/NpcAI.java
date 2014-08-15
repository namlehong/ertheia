package l2s.gameserver.ai;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.SocialActionPacket;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.templates.npc.RandomActions;
import l2s.gameserver.templates.npc.WalkerRoute;
import l2s.gameserver.templates.npc.WalkerRoutePoint;
import l2s.gameserver.utils.Location;

/**
 * @author Bonux
 */
public class NpcAI extends CharacterAI
{
	public static final String WALKER_ROUTE_PARAM = "walker_route_id";

	private static final int WALKER_ROUTE_TIMER_ID = -1000;
	private static final int RANDOM_ACTION_TIMER_ID = -2000;

	//random actions params
	private final RandomActions _randomActions;
	private final boolean _haveRandomActions;
	private int _currentActionId;

	//Walker Routes params
	private final WalkerRoute _walkerRoute;
	private final boolean _haveWalkerRoute;
	private boolean _toBackWay;
	private int _currentWalkerPoint;
	private boolean _delete;

	private boolean _isActive;

	public NpcAI(NpcInstance actor)
	{
		super(actor);

		//initialize random actions params
		_randomActions = actor.getTemplate().getRandomActions();
		_haveRandomActions = _randomActions != null && _randomActions.getActionsCount() > 0;
		_currentActionId = 0; //При спавне начинаем действия с 1го действия.

		//initialize Walker Routes params
		int walkerRouteId = actor.getParameter(WALKER_ROUTE_PARAM, -1);
		_walkerRoute = actor.getTemplate().getWalkerRoute(walkerRouteId);
		_haveWalkerRoute = _walkerRoute != null && _walkerRoute.isValid();
		_toBackWay = false;
		_currentWalkerPoint = -1;
		_delete = false;
		_isActive = false;
	}

	@Override
	protected void onEvtArrived()
	{
		if(!_isActive)
			return;

		continueWalkerRoute();
	}

	@Override
	protected void onEvtTimer(int timerId, Object arg1, Object arg2)
	{
		if(timerId == WALKER_ROUTE_TIMER_ID)
		{
			if(_haveWalkerRoute)
			{
				if(!(arg1 instanceof Location))
					return;

				if(((Boolean) arg2).booleanValue())
				{
					NpcInstance actor = getActor();
					if(actor == null)
						return;

					actor.teleToLocation((Location) arg1);

					if(_isActive)
						continueWalkerRoute();
				}
				else
					moveToLocation((Location) arg1);
			}
		}
		else if(timerId == RANDOM_ACTION_TIMER_ID)
		{
			if(_haveRandomActions)
				makeRandomAction();
		}
	}

	@Override
	public boolean isActive()
	{
		return _isActive;
	}

	@Override
	public void stopAITask()
	{
		_isActive = false;
		if(_haveWalkerRoute)
		{
			if(_toBackWay)
				_currentWalkerPoint++;
			else
				_currentWalkerPoint--;
		}
	}

	@Override
	public void startAITask()
	{
		_isActive = true;
		if(_haveWalkerRoute)
			moveToNextPoint(0);

		if(_haveRandomActions)
		{
			RandomActions.Action action = _randomActions.getAction(1);
			if(action == null)
				return; //todo

			//При спауне начинаем делать действия через случайное время, иначе все нпс будут одновременно начинать, что будет не очень красиво.
			addTimer(RANDOM_ACTION_TIMER_ID, Rnd.get(0, action.getDelay()) * 1000L);
		}
	}

	private void continueWalkerRoute()
	{
		//Когда дошли, говорим фразу, делаем социальное действие, и через указаный промежуток времени начием идти дальше
		if(_haveWalkerRoute)
		{
			WalkerRoutePoint route = _walkerRoute.getPoint(_currentWalkerPoint);
			if(route == null)
				return; //todo

			NpcInstance actor = getActor();
			int socialActionId = route.getSocialActionId();
			if(socialActionId >= 0)
				actor.broadcastPacket(new SocialActionPacket(actor.getObjectId(), socialActionId));

			NpcString phrase = route.getPhrase();
			if(phrase != null)
				Functions.npcSay(actor, phrase);

			moveToNextPoint(route.getDelay());
		}
	}

	private void moveToNextPoint(int delay)
	{
		if(!_isActive)
			return;

		NpcInstance actor = getActor();
		if(actor == null)
			return;

		switch(_walkerRoute.getType())
		{
			case LENGTH:
			{
				if(_toBackWay)
					_currentWalkerPoint--;
				else
					_currentWalkerPoint++;

				if(_currentWalkerPoint >= _walkerRoute.size() - 1)
					_toBackWay = true;

				if(_currentWalkerPoint == 0)
					_toBackWay = false;
				break;
			}
			case ROUND:
			{
				_currentWalkerPoint++;

				if(_currentWalkerPoint >= _walkerRoute.size() - 1)
					_currentWalkerPoint = 0;
				break;
			}
			case RANDOM:
			{
				if(_walkerRoute.size() > 1)
				{
					int oldPoint = _currentWalkerPoint;
					while(oldPoint == _currentWalkerPoint)
					{
						_currentWalkerPoint = Rnd.get(_walkerRoute.size() - 1);
					}
				}
				break;
			}
			case DELETE:
			{
				if(_delete)
				{
					actor.deleteMe(); // TODO: [Bonux] Мб сделать, чтобы он респаунился? Если респаун указан в спавне.
					return;
				}
				_currentWalkerPoint++;
				if(_currentWalkerPoint >= _walkerRoute.size() - 1)
					_delete = true;
				break;
			}
			case FINISH:
			{
				_currentWalkerPoint++;
				if(_currentWalkerPoint >= _walkerRoute.size() - 1)
					actor.stopMove();
				break;
			}
		}

		WalkerRoutePoint route = _walkerRoute.getPoint(_currentWalkerPoint);
		if(route == null)
			return; //todo

		if(route.isRunning())
			actor.setRunning();
		else
			actor.setWalking();

		if(delay > 0)
			addTimer(WALKER_ROUTE_TIMER_ID, route.getLocation(), route.isTeleport(), delay * 1000L);
		else if(route.isTeleport())
		{
			actor.teleToLocation(route.getLocation());

			if(_isActive)
				continueWalkerRoute();
		}
		else
			moveToLocation(route.getLocation());
	}

	private void makeRandomAction()
	{
		if(!_isActive)
			return;

		NpcInstance actor = getActor();
		if(actor == null)
			return;

		_currentActionId++;
		if(_currentActionId > _randomActions.getActionsCount())
			_currentActionId = 1;

		RandomActions.Action action = _randomActions.getAction(_currentActionId);
		if(action == null)
			return; //todo

		int socialActionId = action.getSocialActionId();
		if(socialActionId >= 0)
			actor.broadcastPacket(new SocialActionPacket(actor.getObjectId(), socialActionId));

		NpcString phrase = action.getPhrase();
		if(phrase != null)
			Functions.npcSay(actor, phrase);

		addTimer(RANDOM_ACTION_TIMER_ID, action.getDelay() * 1000L);
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
			continueWalkerRoute();
		}
	}

	@Override
	public NpcInstance getActor()
	{
		return (NpcInstance) super.getActor();
	}
}