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
public class VladimirsWarrior extends Fighter
{
	private static int VLADIMIR_EXECUTIONER = 25809;	// Владимир - Разоритель

	private static double EXECUTIONER_SPAWN_CHANCE = 5.0;

	private static long DESPAWN_TIME = 300000L;

	public VladimirsWarrior(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		if(Rnd.chance(EXECUTIONER_SPAWN_CHANCE))
		{
			NpcInstance npc = NpcUtils.spawnSingle(VLADIMIR_EXECUTIONER, getActor().getLoc(), getActor().getReflection(), DESPAWN_TIME);
			npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 1000);
		}
		super.onEvtDead(killer);
	}
}