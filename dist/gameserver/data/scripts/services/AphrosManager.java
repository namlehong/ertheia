package services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;

import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.instances.DoorInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.scripts.ScriptFile;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;
import l2s.gameserver.utils.ReflectionUtils;

public class AphrosManager extends Functions implements ScriptFile
{
	//aphros raid details Iqman
	private static final Location AphrosLoc = new Location(213732, 115288, -856);
	
	public static Zone _effectZone = null;

	private static final int[] NpcDoors = { 33133, 33134, 33135, 33136 };

	private static final Location[] DoorsLocations = { new Location(213700, 115925, -864), new Location(214354, 115265, -864), new Location(213700, 114595, -800), new Location(213030, 115265, -800) };

	private static final int[] Doors = { 26210041, 26210042, 26210043, 26210044 };
	
	private static List<NpcInstance> activeDoors = new ArrayList<NpcInstance>();

	private static ScheduledFuture<?> _activeCheck = null;
	
	public static NpcInstance AphrosNpc = null;
  
	public static void startRaid()
	{
		for (int doorId : Doors)
		{
			DoorInstance door = ReflectionUtils.getDoor(doorId);
			door.openMe();		
		}

		AphrosNpc = NpcUtils.spawnSingle(25775, AphrosLoc);

		_activeCheck = ThreadPoolManager.getInstance().scheduleAtFixedRate(new ThinkStopBattleTask(), 600000L, 600000L);
	}
 
	public static class ThinkStopBattleTask extends RunnableImpl
	{
		public void runImpl() throws Exception
		{
			if (AphrosNpc != null)
			{
				if (!AphrosNpc.isInCombat())
				{
					AphrosNpc.deleteMe();
					stopRaid();
				}
			}
		}
	} 
	
	public static void stopRaid()
	{
		for (NpcInstance door : activeDoors)
		{
			door.teleToLocation(door.getX(), door.getY(), door.getZ() + 1095);
		}

		if (_activeCheck != null)
		{
			_activeCheck.cancel(false);
			_activeCheck = null;
		}

		for (int doorId : Doors)
		{
			DoorInstance door = ReflectionUtils.getDoor(doorId);
			door.closeMe();		
		}	
		_effectZone.setActive(false);	
	}
	
	private void initializeRaid()
	{
		_effectZone = ReflectionUtils.getZone("[aphros_zone]");
		if (activeDoors.isEmpty())
		{
			List<Location> temp = new ArrayList<Location>(4);
			for (Location loc : DoorsLocations)
				temp.add(new Location(loc.getX(), loc.getY(), loc.getZ()));

			for (int door : NpcDoors)
			{
				int index = Rnd.get(0, temp.size() - 1);
				activeDoors.add(NpcUtils.spawnSingle(door, temp.get(index)));
				temp.remove(index);
			}
		}
		else
		{
			for (NpcInstance door : activeDoors)
				door.teleToLocation(door.getX(), door.getY(), door.getZ() - 1095);
		}	
	}
	
	@Override
	public void onLoad()
	{
		initializeRaid();
		if(_effectZone == null)
			System.out.println("IQMAN NULL ZONE");
	}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}
}