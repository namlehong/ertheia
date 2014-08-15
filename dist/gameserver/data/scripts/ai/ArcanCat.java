package ai;

import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.SocialActionPacket;

public class ArcanCat extends Fighter
{

	public ArcanCat(NpcInstance actor)
	{
		super(actor);
		AI_TASK_ACTIVE_DELAY = 5000;
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();

		actor.broadcastPacket(new SocialActionPacket(actor.getObjectId(), 2));

		return super.thinkActive();
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

}