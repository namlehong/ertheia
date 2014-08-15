package ai.lindvior;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.Guard;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.network.l2.components.ChatType;
import l2s.gameserver.network.l2.components.NpcString;

/**
generator protector by iqman
 */

public class GeneratorProtector extends Guard
{
	private static final int SAY_RAFF = 12000;

	private long lastSayTimer = 0;


	public GeneratorProtector(NpcInstance actor)
	{
		super(actor);
		lastSayTimer = 0;

	}

	@Override
	protected boolean thinkActive()
	{

		NpcInstance actor = getActor();
		boolean statement1 = Rnd.chance(50);
		if(lastSayTimer + SAY_RAFF < System.currentTimeMillis())
		{
			lastSayTimer = System.currentTimeMillis();
			if(statement1)
				Functions.npcSay(actor, NpcString.YOU_MUST_ACTIVATE_THE_4_GENERATORS, ChatType.NPC_ALL, 5000);
			else	
				Functions.npcSay(actor, NpcString.PROTECT_THE_GENERATOR, ChatType.NPC_ALL, 5000);
		}
		return super.thinkActive();
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}