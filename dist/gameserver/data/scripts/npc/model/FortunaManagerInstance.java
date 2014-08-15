package npc.model;

import instances.FortunaManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ReflectionUtils;

public final class FortunaManagerInstance extends NpcInstance
{
	private static final int fortunaId = 179;
	
	public FortunaManagerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;
		if(command.equalsIgnoreCase("begin"))
		{
			Reflection localReflection = player.getActiveReflection();
			if(localReflection != null)
			{
				if(player.canReenterInstance(fortunaId))
				{
					player.teleToLocation(localReflection.getTeleportLoc(), localReflection);
				}	
			}	
			else if(player.canEnterInstance(fortunaId))
			{
				ReflectionUtils.enterReflection(player, new FortunaManager(), fortunaId);
			}	
		}
		super.onBypassFeedback(player, command);
	}	
}