package l2s.gameserver.ai;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;

/**
 * @author Bonux
**/
public class Defender extends Fighter
{
	public Defender(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		if(attacker.isPlayable())
			return;

		super.onEvtAttacked(attacker, damage);
	}

	@Override
	public boolean canAttackCharacter(Creature target)
	{
		return target.isMonster();
	}

	@Override
	public int getMaxAttackTimeout()
	{
		return 0;
	}
}