package npc.model;

import java.util.Calendar;
import instances.CrystalHall;
import instances.Vullock;
import instances.Baylor;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.network.l2.s2c.NpcHtmlMessagePacket;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ReflectionUtils;
import l2s.gameserver.model.Zone;

/**
 * @author Awakeninger
 *
 */

public final class ParnaceTPInstance extends NpcInstance 
{

	private static final int VullockInstance = 167;
    private static final int BaylorInstance = 166;

    public ParnaceTPInstance(int objectId, NpcTemplate template) 
	{
        super(objectId, template);
    }
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(command.startsWith("request_vallock"))
		{
            Reflection r = player.getActiveReflection();
            if(r != null) 
			{
                if(player.canReenterInstance(VullockInstance))
				{
                    player.teleToLocation(r.getTeleportLoc(), r);
				}	
			}		
			else if(player.canEnterInstance(VullockInstance))
			{
                ReflectionUtils.enterReflection(player, new Vullock(), VullockInstance);
			}	
        } 
		else if(command.startsWith("request_Baylor"))
		{
            Reflection r = player.getActiveReflection();
            if(r != null) 
			{
                if(player.canReenterInstance(BaylorInstance))
				{
                    player.teleToLocation(r.getTeleportLoc(), r);
				}	
			}		
			else if(player.canEnterInstance(BaylorInstance))
			{
                ReflectionUtils.enterReflection(player, new Baylor(), BaylorInstance);
			}	
		}
		else
		{
			super.onBypassFeedback(player, command);
		}	
	}
}