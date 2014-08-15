package npc.model;

import instances.CrystalHall;
import instances.SteamCorridor;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.network.l2.s2c.NpcHtmlMessagePacket;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ReflectionUtils;
import java.util.Calendar;

public class CrystalPrisonInstance extends NpcInstance
{	
	public CrystalPrisonInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this)) 
			return;
		
		if(command.startsWith("request_CrystalHall"))
		{
            Reflection r = player.getActiveReflection();
            if (r != null) 
			{
                if (player.canReenterInstance(163))
                    player.teleToLocation(r.getTeleportLoc(), r);
            } 
			else if (player.canEnterInstance(163)) 
			{
                ReflectionUtils.enterReflection(player, new CrystalHall(), 163);
            }
        }
		
		if (command.startsWith("request_SteamCorridor"))
		{
			Reflection r = player.getActiveReflection();
			if(r != null)
			{
				if(player.canReenterInstance(164))
				{
					player.teleToLocation(r.getTeleportLoc(), r);
				}
			}
			else if(player.canEnterInstance(164))
			{
				ReflectionUtils.enterReflection(player, new SteamCorridor(), 164);
			}
            
        }
		
		if (command.startsWith("request_CoralGarden"))
		{

        }
	}
	
	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		NpcHtmlMessagePacket msg = new NpcHtmlMessagePacket(player, this);
		if(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 0 || Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 2 || Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 4)
		{
			msg.setFile("default/33522.htm");
			msg.replace("%instance%", "Steam Corridor");
			msg.replace("%enter%", "request_SteamCorridor");
		}
		if(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 1 || Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 3 || Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 5)
		{
			msg.setFile("default/33522.htm");
			msg.replace("%instance%", "Emerald Square");
			msg.replace("%enter%", "request_CrystalHall");
		}
		if(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 6)
		{
			msg.setFile("default/33522.htm");
			msg.replace("%instance%", "Coral Garden");
			msg.replace("%enter%", "request_CoralGarden");
		}	
		player.sendPacket(msg);
	}
}
