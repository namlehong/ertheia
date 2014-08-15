package ai.incubatorOfEvil;

import java.util.List;

import l2s.commons.util.Rnd;

import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.scripts.Functions;

/**
 * @author Iqman
 */
public class Avanguard_Barton extends DefaultAI
{
	private NpcInstance target = null;

	public Avanguard_Barton(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public boolean isGlobalAI()
	{
		return false;
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if(Rnd.chance(8))
		{
			Functions.npcSay(actor, NpcString.I_HIT_THINGS_THEY_FALL_DEAD);
		}
		return false;
	}
}