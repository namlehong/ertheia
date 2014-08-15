package ai.instances;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;

public class DeadNpcs extends DefaultAI
{
	public DeadNpcs(NpcInstance actor)
	{
		super(actor);
		actor.startImmobilized();
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{}

	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{}

	@Override
	protected boolean randomAnimation()
	{
		return false;
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}
}