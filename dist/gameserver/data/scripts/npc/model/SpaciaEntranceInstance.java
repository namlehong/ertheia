package npc.model;

import instances.SpaciaNormal;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.ReflectionUtils;

/**
 * @author cruel
 */
public final class SpaciaEntranceInstance extends NpcInstance
{
	public SpaciaEntranceInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equalsIgnoreCase("normal_spacia"))
		{
			Reflection r = player.getActiveReflection();
			if(r != null)
			{
				if(player.canReenterInstance(159))
					player.teleToLocation(r.getTeleportLoc(), r);
			}
			else if(player.canEnterInstance(159))
				ReflectionUtils.enterReflection(player, new SpaciaNormal(), 159);
		}
		else
			super.onBypassFeedback(player, command);
	}
}