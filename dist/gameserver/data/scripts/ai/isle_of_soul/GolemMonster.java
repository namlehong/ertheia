package ai.isle_of_soul;

import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.instances.NpcInstance;

/**
 * @author Bonux
 */
public class GolemMonster extends Fighter
{
	public GolemMonster(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();

		getActor().setNpcState(1);
	}
}