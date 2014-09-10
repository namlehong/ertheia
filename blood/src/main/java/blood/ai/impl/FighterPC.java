package blood.ai.impl;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import blood.ai.EventFPC;

public class FighterPC extends EventFPC
{
	public FighterPC(Player actor)
	{
		super(actor);
	}
	
	protected boolean isAllowClass()
	{
		return getActor().getClassId().isOfLevel(ClassLevel.NONE) || getActor().getClassId().isOfLevel(ClassLevel.FIRST);
	}
	
	protected boolean defaultSubFightTask(Creature target)
	{
		lameFightTask(target);
		return true;
	}
	
}