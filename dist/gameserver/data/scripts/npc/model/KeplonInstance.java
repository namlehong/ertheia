package npc.model;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */

public final class KeplonInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public KeplonInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equalsIgnoreCase("buygreen"))
		{
			if(ItemFunctions.removeItem(player, 57, 10000, true) >= 10000)
			{
				ItemFunctions.addItem(player, 4401, 1, true);
				return;
			}
			else
			{
				player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}
		}
		else if(command.startsWith("buyblue"))
		{
			if(ItemFunctions.removeItem(player, 57, 10000, true) >= 10000)
			{
				ItemFunctions.addItem(player, 4402, 1, true);
				return;
			}
			else
			{
				player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}
		}
		else if(command.startsWith("buyred"))
		{
			if(ItemFunctions.removeItem(player, 57, 10000, true) >= 10000)
			{
				ItemFunctions.addItem(player, 4403, 1, true);
				return;
			}
			else
			{
				player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}
		}
		else
			super.onBypassFeedback(player, command);
	}
}