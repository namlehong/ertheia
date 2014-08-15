package ai.land_of_chaos;

import l2s.gameserver.ai.Mystic;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.NpcUtils;

/**
 * @author Bonux
**/
public class Legah extends Mystic
{
	private final static int ONE_ARMED_ZOMBIE = 23332;	// One-armed Zombie

	private final static int HIT_COUNT_RESET_TIMER_ID = 8000;

	private int _hitCount = 0;

	public Legah(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		_hitCount++;

		stopTask(HIT_COUNT_RESET_TIMER_ID);

		if(_hitCount >= 5)
		{
			NpcInstance npc = NpcUtils.spawnSingle(ONE_ARMED_ZOMBIE, getActor().getLoc(), getActor().getReflection());
			npc.getAI().addTimer(SummonedZombie.AGGRESSION_TIMER_ID, attacker, 1500L);

			getActor().doDie(attacker);
			return;
		}

		addTask(HIT_COUNT_RESET_TIMER_ID, 10000L);

		super.onEvtAttacked(attacker, damage);
	}

	@Override
	protected void onEvtTimer(int timerId, Object arg1, Object arg2)
	{
		super.onEvtTimer(timerId, arg1, arg2);

		if(timerId == HIT_COUNT_RESET_TIMER_ID)
			_hitCount = 0;
	}
}