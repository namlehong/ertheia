package ai;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.scripts.Functions;

/**
 * AI для Karul Bugbear ID: 20438
 *
 * @author Diamond
 */
public class OlMahumGeneral extends Fighter
{
	private boolean _firstTimeAttacked = true;

	public OlMahumGeneral(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
		if(_firstTimeAttacked)
		{
			_firstTimeAttacked = false;
			if(Rnd.chance(25))
				Functions.npcSay(actor, "We shall see about that!");
		}
		else if(Rnd.chance(10))
			Functions.npcSay(actor, "I will definitely repay this humiliation!");
		super.onEvtAttacked(attacker, damage);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		_firstTimeAttacked = true;
		super.onEvtDead(killer);
	}
}