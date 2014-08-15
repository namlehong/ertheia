package ai.land_of_chaos;

import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.PositionUtils;

/**
 * @author Bonux
**/
public class SummonedZombie extends Fighter
{
	public final static int AGGRESSION_TIMER_ID = 5000;
	private final static int DESPAWN_TIMER_ID = 6000;

	private int _hitCount = 0;

	public SummonedZombie(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();

		addTimer(DESPAWN_TIMER_ID, 25000L);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		_hitCount++;

		super.onEvtAttacked(attacker, damage);
	}

	@Override
	protected void onEvtTimer(int timerId, Object arg1, Object arg2)
	{
		super.onEvtTimer(timerId, arg1, arg2);

		if(timerId == AGGRESSION_TIMER_ID)
		{
			if(arg1 != null && (arg1 instanceof Creature))
			{
				Creature target = (Creature) arg1;
				getActor().setHeading(PositionUtils.calculateHeadingFrom(getActor(), target));
				notifyEvent(CtrlEvent.EVT_AGGRESSION, target, 1000);
			}
		}
		else if(timerId == DESPAWN_TIMER_ID)
		{
			if(_hitCount == 0)
				getActor().deleteMe();
			else
			{
				_hitCount = 0;
				addTimer(DESPAWN_TIMER_ID, 25000L);
			}
		}
	}
}