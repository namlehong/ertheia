package ai;

import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.scripts.Functions;

/**
 * AI рейдбосса Tiberias
 * любит поговорить после смерти
 * @author n0nam3
 */
public class Tiberias extends Fighter
{
	public Tiberias(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();
		Functions.npcShoutCustomMessage(actor, "scripts.ai.Tiberias.kill");
		super.onEvtDead(killer);
	}
}