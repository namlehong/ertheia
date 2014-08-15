package npc.model;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ReflectionUtils;
import java.util.Calendar;

import instances.LindviorBoss;

public class KatoInstance extends NpcInstance
{	
	public static int LINDVIOR_INSTANCE = 247;
	
	public KatoInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this)) 
			return;
		
		if(command.startsWith("requestLindvior"))
		{
            Reflection r = player.getActiveReflection();
            if (r != null) 
			{
                if(player.canReenterInstance(LINDVIOR_INSTANCE))
                    player.teleToLocation(r.getTeleportLoc(), r);
            } 
			else if(player.canEnterInstance(LINDVIOR_INSTANCE)) 
			{
				ReflectionUtils.enterReflection(player, new LindviorBoss(), LINDVIOR_INSTANCE);
            }
        }
		super.onBypassFeedback(player, command);
	}
}
