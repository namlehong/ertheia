package npc.model;

import instances.GoddessOfDarkness;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */

public final class GoddessOfDarknessRefugeeInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public GoddessOfDarknessRefugeeInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.startsWith("goddessofdarkness"))
		{
			int val = Integer.parseInt(command.substring(18));
			
			Reflection r = player.getActiveReflection();
			if(r != null)
			{
				if(player.canReenterInstance(val))
					player.teleToLocation(r.getTeleportLoc(), r);
			}
			else if(player.canEnterInstance(val))
			{
				ReflectionUtils.enterReflection(player, new GoddessOfDarkness(), val);
			}
		}
		else if(command.startsWith("escape"))
		{
			if(player.getParty() == null || !player.getParty().isLeader(player))
			{
				showChatWindow(player, "not_party_leader.htm");
				return;
			}
			player.getReflection().collapse();
		}
		else if(command.startsWith("return"))
		{
			Reflection r = player.getReflection();
			if(r.getReturnLoc() != null)
				player.teleToLocation(r.getReturnLoc(), ReflectionManager.DEFAULT);
			else
				player.setReflection(ReflectionManager.DEFAULT);
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public String getHtmlPath(int npcId, int val, Player player)
	{
		String pom;
		if(val == 0)
			pom = "" + npcId;
		else
			pom = npcId + "-" + val;

		return "instance/goddessofdarkness/" + pom + ".htm";
	}
}