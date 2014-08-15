package l2s.gameserver.ai;

import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.ArrayList;
import java.util.List;

import l2s.commons.util.Rnd;
import l2s.gameserver.data.xml.holder.FakePlayerPathHolder;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.model.instances.FakePlayerInstance;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.templates.npc.FakePlayerPath;
import l2s.gameserver.templates.npc.FakePlayerTemplate;
import l2s.gameserver.utils.Location;

/**
 * @author Bonux
 */
public class FakePlayerAI extends CharacterAI
{
	private static final int MOVE_TIMER_ID = -1000;
	private static final int SIT_TIMER_ID = -2000;
	private static final int BUFF_TIMER_ID = -3000;
	private static final double CHANGE_PATH_CHANCE = 5.; // 5%

	private boolean _toChangePath = false;
	private FakePlayerPath.Point _currentPoint = null;

	public FakePlayerAI(FakePlayerInstance actor)
	{
		super(actor);
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	public boolean isActive()
	{
		return true;
	}

	@Override
	protected void onEvtSpawn()
	{
		checkBuffs();
		continueMove(-1);
	}

	@Override
	protected void onEvtArrived()
	{
		if(_currentPoint != null && _currentPoint.sitting())
			addTimer(SIT_TIMER_ID, null, Rnd.get(5, 10) * 1000L);
		else
			continueMove(-1);
	}

	@Override
	protected void onIntentionRest()
	{
		changeIntention(CtrlIntention.AI_INTENTION_REST, null, null);
		setAttackTarget(null);
		clientStopMoving();
	}

	@Override
	protected void onEvtTimer(int timerId, Object arg1, Object arg2)
	{
		if(timerId == MOVE_TIMER_ID)
			findAndMoveToPoint();
		else if(timerId == SIT_TIMER_ID)
		{
			getActor().sitDown();
			continueMove(-1);
		}
		else if(timerId == BUFF_TIMER_ID)
		{
			checkBuffs();
		}
	}

	private void checkBuffs()
	{
		final FakePlayerInstance actor = getActor();
		if(actor == null)
			return;

		final TIntIntMap buffs = new TIntIntHashMap(actor.getTemplate().getBuffs());
		if(buffs.isEmpty())
			return;

		for(Effect effect : actor.getEffectList().getEffects())
		{
			effect.restart();
			buffs.remove(effect.getSkill().getId());
		}

		for(TIntIntIterator iterator = buffs.iterator(); iterator.hasNext();)
		{
			iterator.advance();

			Skill skill = SkillTable.getInstance().getInfo(iterator.key(), iterator.value());
			if(skill == null)
				continue;

			skill.getEffects(actor, actor, false);
		}

		addTimer(BUFF_TIMER_ID, null, Rnd.get(3, 20) * 60 * 1000L);
	}

	public void onTeleported()
	{
		continueMove(-1);
	}

	private void continueMove(double delay)
	{
		if(getActor().getPathId() > 0)
		{
			double rndDelay = Rnd.get(15, 30);
			if(delay >= 0)
				rndDelay = delay;
			else if(_currentPoint != null)
				rndDelay = Rnd.get(_currentPoint.getMinDelay(), _currentPoint.getMaxDelay());

			if(rndDelay == 0)
				findAndMoveToPoint();
			else
				addTimer(MOVE_TIMER_ID, null, (long) (rndDelay * 1000L));
		}
	}

	public void findAndMoveToPoint()
	{
		if(getActor().sittingTaskLaunched)
		{
			continueMove(5);
			return;
		}
		else if(getActor().isSitting())
		{
			getActor().standUp();
			continueMove(5);
			return;
		}

		if(_toChangePath)
		{
			FakePlayerPath path = FakePlayerPathHolder.getInstance().getPath(getActor().getPathId());
			if(path == null)
			{
				getActor().deleteMe();
				return;
			}

			List<FakePlayerPath> pathes = new ArrayList<FakePlayerPath>();
			for(FakePlayerPath temp : FakePlayerPathHolder.getInstance().getAll())
			{
				if(path.isAvailNextPath(-1) || path.isAvailNextPath(temp.getId()))
					pathes.add(temp);
			}

			_currentPoint = null;
			_toChangePath = false;
			FakePlayerPath nextPath = pathes.get(Rnd.get(pathes.size()));
			getActor().setPathId(nextPath.getId());
			getActor().setSpawnedLoc(nextPath.getLocation());
			getActor().teleToLocation(nextPath.getLocation());
			return;
		}

		int pathId = getActor().getPathId();
		if(pathId > 0)
		{
			FakePlayerPath path = FakePlayerPathHolder.getInstance().getPath(pathId);
			if(path == null)
			{
				getActor().deleteMe();
				return;
			}

			if(_currentPoint != null && _currentPoint.getNextPointId() > 0)
			{
				FakePlayerPath.Point point = path.getPoint(_currentPoint.getNextPointId());
				if(point != null)
				{
					_currentPoint = point;

					Location loc = point.getTerritory().getRandomLoc(getActor().getGeoIndex());
					loc = Location.findPointToStay(loc, 100, getActor().getGeoIndex());
					getActor().setSpawnedLoc(loc);
					moveToLocation(loc);
					return;
				}
			}

			if(Rnd.chance(CHANGE_PATH_CHANCE) && FakePlayerPathHolder.getInstance().size() > 1)
			{
				_toChangePath = true;
				
				FakePlayerPath.Point point = path.getPoint(0);

				_currentPoint = point;

				Location loc = point.getTerritory().getRandomLoc(getActor().getGeoIndex());
				loc = Location.findPointToStay(loc, 100, getActor().getGeoIndex());
				getActor().setSpawnedLoc(loc);
				moveToLocation(loc);
				return;
			}

			FakePlayerPath.Point[] points = path.getPoints();
			FakePlayerPath.Point point = points[Rnd.get(1, points.length - 1)];

			_currentPoint = point;

			Location loc = point.getTerritory().getRandomLoc(getActor().getGeoIndex());
			loc = Location.findPointToStay(loc, 100, getActor().getGeoIndex());
			getActor().setSpawnedLoc(loc);
			moveToLocation(loc);
		}
	}

	private void moveToLocation(Location loc)
	{
		FakePlayerInstance actor = getActor();
		if(actor == null)
			return;

		if(!actor.moveToLocation(loc, 0, true))
		{
			clientStopMoving();
			findAndMoveToPoint();
		}
	}

	@Override
	public FakePlayerInstance getActor()
	{
		return (FakePlayerInstance) super.getActor();
	}
}