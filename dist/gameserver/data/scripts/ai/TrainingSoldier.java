package ai;

import l2s.gameserver.ai.Defender;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;

/**
 * @author Bonux
 */
public class TrainingSoldier extends Defender
{
	public TrainingSoldier(NpcInstance actor)
	{
		super(actor);
		actor.setAggroRange(100);
	}

	@Override
	public boolean canAttackCharacter(Creature target)
	{
		return target.isNpc() && ((NpcInstance) target).getNpcId() == 33023;
	}
}