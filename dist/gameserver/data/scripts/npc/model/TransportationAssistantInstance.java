package npc.model;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author KilRoy & Mangol
 */
public final class TransportationAssistantInstance extends NpcInstance
{
	private static final long serialVersionUID = -6027309212963370840L;

	public TransportationAssistantInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equalsIgnoreCase("goto1ln"))
		{
			if(!checkTeleport(player))
				return;
	
			player.teleToLocation(-147711, 152768, -14056);
		}
		else if(command.equalsIgnoreCase("goto1ls"))
		{
			if(!checkTeleport(player))
				return;
	
			player.teleToLocation(-147867, 250710, -14024);
		}
		else if(command.equalsIgnoreCase("goto2ln"))
		{
			if(!checkTeleport(player))
				return;
	
			player.teleToLocation(-150131, 143145, -11960);
		}
		else if(command.equalsIgnoreCase("goto2ls"))
		{
			if(!checkTeleport(player))
				return;
	
			player.teleToLocation(-150169, 241022, -11928);
		}
		else
			super.onBypassFeedback(player, command);
	}

	private static boolean checkTeleport(Player player)
	{
		if(player.isCursedWeaponEquipped())
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_IN_A_CHAOTIC_STATE);
			return false;
		}
		if(player.isDead())
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_DEAD);
			return false;
		}
		if(player.isCastingNow() || player.isInCombat() || player.isAttackingNow())
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_MOVE_DURING_COMBAT);
			return false;
		}
		return true;
	}
}