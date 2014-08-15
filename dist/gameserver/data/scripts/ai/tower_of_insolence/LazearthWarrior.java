package ai.tower_of_insolence;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.NpcUtils;

/**
 * @author Bonux
**/
public class LazearthWarrior extends Fighter
{
	private static int LAZEARTH_DESTROYER = 25811;	// Раджуос - Разрушитель

	private static double DESTROYER_SPAWN_CHANCE = 5.0;

	private static long DESPAWN_TIME = 300000L;

	public LazearthWarrior(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		if(Rnd.chance(DESTROYER_SPAWN_CHANCE))
		{
			NpcInstance npc = NpcUtils.spawnSingle(LAZEARTH_DESTROYER, getActor().getLoc(), getActor().getReflection(), DESPAWN_TIME);
			npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 1000);
		}
		super.onEvtDead(killer);
	}
}