package ai.tower_of_insolence;

import java.util.ArrayList;
import java.util.List;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;

/**
 * @author Bonux
**/
public class LazearthDestroyer extends Fighter
{
	private static int LAZEARTH_DESTROYER = 25812;	// Раджуос - Разрушитель

	private static int SPAWN_TIMER_ID = 10000;

	private static long SPAWN_DELAY = 120000L;

	private final List<NpcInstance> _fakes = new ArrayList<NpcInstance>();

	public LazearthDestroyer(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();

		unblockTimer(SPAWN_TIMER_ID);
		addTimer(SPAWN_TIMER_ID, SPAWN_DELAY);
	}

	@Override
	public void onEvtDeSpawn()
	{
		super.onEvtDeSpawn();

		blockTimer(SPAWN_TIMER_ID);

		for(NpcInstance fake : _fakes)
		{
			if(fake != null)
				fake.deleteMe();
		}
	}

	@Override
	protected void onEvtTimer(int timerId, Object arg1, Object arg2)
	{
		super.onEvtTimer(timerId, arg1, arg2);
		if(timerId == SPAWN_TIMER_ID)
		{
			NpcInstance actor = getActor();
			if(actor == null || actor.isDead())
				return;

			NpcInstance npc = NpcUtils.spawnSingle(LAZEARTH_DESTROYER, Location.findPointToStay(actor, 50, 100), actor.getReflection());
			npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, actor.getAggroList().getMostHated(), 1000);

			_fakes.add(npc);

			addTimer(SPAWN_TIMER_ID, SPAWN_DELAY);
		}
	}
}