package zones;

import l2s.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone;
import l2s.gameserver.scripts.ScriptFile;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.ReflectionUtils;

public class OrbisTeleport implements ScriptFile
{
	private static ZoneListener _zoneListener;
	private static final String[] zones = {
			"[orbis_to_1st]",
			"[orbis_to_enter]",
			"[orbis_from_1st_to_2nd]",
			"[orbis_from_2nd_to_1st]",
			"[orbis_from_2nd_to_3rd]",
			"[orbis_from_3rd_to_2nd]"
	};
	private static final Location ORBIS_TO_1ST_POINT = new Location(213983, 53250, -8176);
	private static final Location ORBIS_TO_ENTER_POINT = new Location(197784, 90584, -325);
	private static final Location ORBIS_FROM_1ST_TO_2ND_POINT = new Location(213799, 53253, -14432);
	private static final Location ORBIS_FROM_2ND_TO_1ST_POINT = new Location(215056, 50467, -8416);
	private static final Location ORBIS_FROM_2ND_TO_3RD_POINT = new Location(211641, 115547, -12736);
	private static final Location ORBIS_FROM_3RD_TO_2ND_POINT = new Location(211137, 50501, -14624);

	@Override
	public void onLoad()
	{
		_zoneListener = new ZoneListener();

		for(String s : zones)
		{
			Zone zone = ReflectionUtils.getZone(s);
			zone.addListener(_zoneListener);
		}
	}

	@Override
	public void onReload()
	{
		//
	}

	@Override
	public void onShutdown()
	{
		//
	}

	public class ZoneListener implements OnZoneEnterLeaveListener
	{

		@Override
		public void onZoneEnter(Zone zone, Creature cha)
		{
			Player player = cha.getPlayer();
			if(player == null)
				return;

			if(zone.isActive())
			{
				if(zone.getName().equalsIgnoreCase("[orbis_to_1st]"))
					player.teleToLocation(ORBIS_TO_1ST_POINT);
				else if(zone.getName().equalsIgnoreCase("[orbis_to_enter]"))
					player.teleToLocation(ORBIS_TO_ENTER_POINT);
				else if(zone.getName().equalsIgnoreCase("[orbis_from_1st_to_2nd]"))
					player.teleToLocation(ORBIS_FROM_1ST_TO_2ND_POINT);
				else if(zone.getName().equalsIgnoreCase("[orbis_from_2nd_to_1st]"))
					player.teleToLocation(ORBIS_FROM_2ND_TO_1ST_POINT);
				else if(zone.getName().equalsIgnoreCase("[orbis_from_2nd_to_3rd]"))
					player.teleToLocation(ORBIS_FROM_2ND_TO_3RD_POINT);
				else if(zone.getName().equalsIgnoreCase("[orbis_from_3rd_to_2nd]"))
					player.teleToLocation(ORBIS_FROM_3RD_TO_2ND_POINT);

			}
		}

		@Override
		public void onZoneLeave(Zone zone, Creature cha)
		{
			//
		}
	}
}
