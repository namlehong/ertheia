package npc.model;

import gnu.trove.map.hash.TIntObjectHashMap;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.Location;

import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Iqman + friends ;)
 */

public final class HarnakTeleportDeviceInstance extends NpcInstance
{

	private TIntObjectHashMap<Location> teleCoords = new TIntObjectHashMap<Location>();
	
	private static final int[] cycleStartTeleports = { 33306, 33314, 33322 };
	private static final int[] cycleEndTeleports = { 33313, 33321, 33329 };	
	
	public HarnakTeleportDeviceInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);

		teleCoords.put(33306, new Location(-114700, 145282, -7680));
		teleCoords.put(33307, new Location(-112811, 146063, -7680));
		teleCoords.put(33308, new Location(-111346, 147920, -7680));
		teleCoords.put(33309, new Location(-112716, 151218, -7688));
		teleCoords.put(33310, new Location(-114711, 150314, -7680));
		teleCoords.put(33311, new Location(-116710, 151179, -7680));
		teleCoords.put(33312, new Location(-118080, 147916, -7680));
		teleCoords.put(33313, new Location(-116610, 146042, -7680));

		teleCoords.put(33314, new Location(-116577, 147429, -10744));
		teleCoords.put(33315, new Location(-114712, 146657, -10744));
		teleCoords.put(33316, new Location(-112806, 147393, -10744));
		teleCoords.put(33317, new Location(-111339, 149262, -10744));
		teleCoords.put(33318, new Location(-112718, 152414, -10752));
		teleCoords.put(33319, new Location(-114713, 151610, -10744));
		teleCoords.put(33320, new Location(-116706, 152441, -10744));
		teleCoords.put(33321, new Location(-118069, 149265, -10744));

		teleCoords.put(33322, new Location(-114689, 180723, -13808));
		teleCoords.put(33323, new Location(-112841, 181530, -13808));
		teleCoords.put(33324, new Location(-111350, 183341, -13800));
		teleCoords.put(33325, new Location(-112714, 186547, -13808));
		teleCoords.put(33326, new Location(-114706, 185708, -13808));
		teleCoords.put(33327, new Location(-116702, 186551, -13808));
		teleCoords.put(33328, new Location(-118084, 183353, -13800));
		teleCoords.put(33329, new Location(-116591, 181492, -13808));		
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.startsWith("tproom"))
		{
			int direction_id = 0;
			try
			{
				direction_id = Integer.parseInt(command.substring(7));
			}	
			catch(Exception e)
			{
				System.out.println("NPC ID: " + getNpcId() + " has not numeral teleport direction FIX IT!");
			}

			if(direction_id == 0)
			{
				if(ArrayUtils.contains(cycleStartTeleports, getNpcId()))
					player.teleToLocation(teleCoords.get(getNpcId() + 7));
				else
					player.teleToLocation(teleCoords.get(getNpcId() - 1));
			}
			
			else if (direction_id == 1)
			{
				if(ArrayUtils.contains(cycleEndTeleports, getNpcId()))
					player.teleToLocation(teleCoords.get(getNpcId() - 7));
				else
					player.teleToLocation(teleCoords.get(getNpcId() + 1));
			}			
		}
		else
			super.onBypassFeedback(player, command);
	}
}