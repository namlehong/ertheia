package npc.model;

import instances.Teredor;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ReflectionUtils;

/**
 * @author coldy
 */
public final class TeredorGatekeeperInstance extends NpcInstance
{
	private static final int teredorInstanceId = 160;

	public TeredorGatekeeperInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command) 
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equalsIgnoreCase("teredor_enter"))
		{
			Reflection r = player.getActiveReflection();
			if(r != null)
			{
				if(player.canReenterInstance(teredorInstanceId))
					player.teleToLocation(r.getTeleportLoc(), r);
			}
			else if(player.canEnterInstance(teredorInstanceId))
			{
				ReflectionUtils.enterReflection(player, new Teredor(),teredorInstanceId);
			}
		}
		else
			super.onBypassFeedback(player, command);
	}
}