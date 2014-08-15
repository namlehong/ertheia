package npc.model;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ReflectionUtils;

import instances.Octavis;

/**
 * Для работы с инстами - octavis
 */
public final class LydiaNpcInstance extends NpcInstance
{
	private static final long serialVersionUID = 5984176213940365077L;
	
	private static final int normalOctavisInstId = 180;
	private static final int hardOctavisInstId = 181;

	public LydiaNpcInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;
		
		if(command.equalsIgnoreCase("request_normaloctavis"))
		{
			Reflection r = player.getActiveReflection();
			if (r != null)
			{
				if(player.canReenterInstance(normalOctavisInstId))
					player.teleToLocation(208404, 120572, -10014, r);
			}
			else if(player.canEnterInstance(normalOctavisInstId))
				ReflectionUtils.enterReflection(player, new Octavis(), normalOctavisInstId);
		}
		else if(command.equalsIgnoreCase("request_hardoctavis"))
		{
			Reflection r = player.getActiveReflection();
			if(r != null)
			{
				if(player.canReenterInstance(hardOctavisInstId))
					player.teleToLocation(208404, 120572, -10014, r);
			}
			else if(player.canEnterInstance(hardOctavisInstId))
				ReflectionUtils.enterReflection(player, new Octavis(), hardOctavisInstId);
		}
		else
			super.onBypassFeedback(player, command);
	}
}
