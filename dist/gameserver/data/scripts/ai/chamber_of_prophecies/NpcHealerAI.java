package ai.chamber_of_prophecies;

import l2s.commons.util.Rnd;
import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.Location;
/**
 * @author Hien Son
 */
public class NpcHealerAI extends NpcSupportAI
{
	
	public NpcHealerAI(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		Player player;
		
		//follow the player
		double distance;
		
		if(targetPlayers != null && targetPlayers.size() > 0 && followTargetIndex > 0)
		{
			player = targetPlayers.get(followTargetIndex - 1);
			
			//in case the player set to follow is null, fall back to the first player in the list
			if(player == null)
				player = targetPlayers.get(0);
			
			distance = (int)actor.getDistance(player);
			
			if(distance > maxFollowDistance)
			{
				if(GeoEngine.canSeeTarget(actor, player, false))
				{
					//in case the NPC can see the player
					actor.setRunning();
					Location loc = new Location(player.getX() + Rnd.get(-60, 60), player.getY() + Rnd.get(-60, 60), player.getZ());
					actor.followToCharacter(loc, player, minFollowDistance, false);
				}
				else
				{
					//in case the NPC cannot see the player, then teleport straight to him
					actor.teleToLocation(player.getLoc().getRandomLoc(100));
				}
				return true;
			}
		}
		
		return startHeal();
	}

	@Override
	public int getRateDOT()
	{
		return 0;
	}

	@Override
	public int getRateDEBUFF()
	{
		return 30;
	}

	@Override
	public int getRateDAM()
	{
		return 30;
	}

	@Override
	public int getRateSTUN()
	{
		return 30;
	}

	@Override
	public int getRateBUFF()
	{
		return 90;
	}

	@Override
	public int getRateHEAL()
	{
		return 90;
	}
	
}