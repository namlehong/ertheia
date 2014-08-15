package zones;

import l2s.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.scripts.ScriptFile;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.ReflectionUtils;

import instances.Sansililion;

public class SoaSansilion implements ScriptFile
{
	private static final String TELEPORT_ZONE_NAME1 = "[Birthing_room_0]";
	private static final String TELEPORT_ZONE_NAME2 = "[Birthing_room_1]";
	private static final String TELEPORT_ZONE_NAME3 = "[SOA_3]";

	private static ZoneListener _zoneListener1;
	private static ZoneListener _zoneListener2;
	private static ZoneListener _zoneListener3;
	private static final int soaSansilion = 171;

	private void init()
	{
		_zoneListener1 = new ZoneListener();
		_zoneListener2 = new ZoneListener();
		_zoneListener3 = new ZoneListener();
		Zone zone1 = ReflectionUtils.getZone(TELEPORT_ZONE_NAME1);
		Zone zone2 = ReflectionUtils.getZone(TELEPORT_ZONE_NAME2);
		Zone zone3 = ReflectionUtils.getZone(TELEPORT_ZONE_NAME3);
		zone1.addListener(_zoneListener1);
		zone2.addListener(_zoneListener2);
		zone3.addListener(_zoneListener3);
	}

	@Override
	public void onLoad()
	{
		init();
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
			if(zone == null)
				return;

			if(cha == null)
				return;

			Player player = cha.getPlayer();
			if(player == null)
				return;

			if(zone.getName().equalsIgnoreCase("[Birthing_room_0]") || zone.getName().equalsIgnoreCase("[Birthing_room_1]"))
			{
				Reflection r = player.getActiveReflection();
				if(r != null)
				{
					if(player.canReenterInstance(soaSansilion))
						player.teleToLocation(-185853, 147878, -15313, r);
				}
				else if(player.canEnterInstance(soaSansilion))
					ReflectionUtils.enterReflection(player, new Sansililion(), soaSansilion);
			}
			else if(zone.getName().equalsIgnoreCase("[SOA_3]"))
			{
				Reflection r = player.getActiveReflection();
				if(r != null)
				{
					if(r instanceof Sansililion)
					{
						Sansililion sInst = (Sansililion) r;
						NpcInstance npc = null;
						for(NpcInstance tay : sInst.getNpcs())
						{
							if(tay.getNpcId() == 33152)
								npc = tay;
						}

						if(sInst.getStatus() == 1)
							Functions.npcShout(npc, NpcString.ITS_NOT_OVER_YET__IT_WONT_BE__OVER__LIKE_THIS__NEVER);
					}
				}
			}
		}

		@Override
		public void onZoneLeave(Zone zone, Creature cha)
		{
			//
		}
	}
}