package ai.freya;

import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;

/**
 * @author pchayka
 */

public class IceCaptainKnight extends Fighter
{
	public IceCaptainKnight(NpcInstance actor)
	{
		super(actor);
		MAX_PURSUE_RANGE = 6000;
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		Reflection r = getActor().getReflection();
		for(Player p : r.getPlayers())
			this.notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 5);
	}

	@Override
	protected void teleportHome()
	{
		return;
	}
}