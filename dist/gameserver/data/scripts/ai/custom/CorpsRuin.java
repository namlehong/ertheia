package ai.custom;

import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;


public class CorpsRuin extends DefaultAI {


	public CorpsRuin(NpcInstance actor) {
		super(actor);
	}


	protected void onEvtDead(Creature killer) {
		return;
	}

}