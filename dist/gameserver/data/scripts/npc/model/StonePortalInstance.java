package npc.model;

//import instances.AltarShilen;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ReflectionUtils;

/**
 * @author Awakeninger
 */

public final class StonePortalInstance extends NpcInstance 
{
    private static final int VullockInstance = 193;

    public StonePortalInstance(int objectId, NpcTemplate template) 
	{
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) 
	{
        if (!canBypassCheck(player, this))
            return;

      /*  if (command.equalsIgnoreCase("start")) 
		{
            Reflection r = player.getActiveReflection();
            if (r != null) 
			{
                if (player.canReenterInstance(VullockInstance))
                    player.teleToLocation(r.getTeleportLoc(), r);
            } 
			else if (player.canEnterInstance(VullockInstance))
                ReflectionUtils.enterReflection(player, new AltarShilen(), VullockInstance);
        }
		*/ //no use right now
		else
            super.onBypassFeedback(player, command);
    }
}