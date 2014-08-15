package instances;

import java.util.ArrayList;
import java.util.List;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.DoorInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SceneMovie;
import l2s.gameserver.network.l2.components.UsmVideo;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.PositionUtils;

/**
 * @author Iqman + GW
 */
public class Octavis extends Reflection
{
	private static final Location ENTRANCE = new Location(210651, 119052, -9996);
	private static final Location LAIR_ENTRANCE = new Location(208404, 120572, -10014);
	private static final Location OCTAVIS_SPAWN = new Location(207069, 120580, -10008);
	private static final Location LAIR_CENTER = new Location(207190, 120574, -10009);
	private static final int INSTANCE_ID_LIGHT = 180;
	private static final int INSTANCE_ID_HARD = 181;
	private static final int VOLCANO_ZONE = 19161;
	private static final int OCTAVIS_POWER = 18984;
	private static final int OCTAVIS_LIGHT_FIRST = 29191;
	private static final int OCTAVIS_LIGHT_BEAST = 29192;
	private static final int OCTAVIS_LIGHT_SECOND = 29193;
	private static final int OCTAVIS_LIGHT_THIRD = 29194;
	private static final int OCTAVIS_HARD_FIRST = 29209;
	private static final int OCTAVIS_HARD_BEAST = 29210;
	private static final int OCTAVIS_HARD_SECOND = 29211;
	private static final int OCTAVIS_HARD_THIRD = 29212;
	private static final int OCTAVIS_NPC = 32949;
	private static final int OCTAVIS_GLADIATOR = 22928;
	private static final int ARENA_BEAST = 22929;
	private static final int OCTAVIS_SCIENTIST = 22930;
	private static final int[] BEAST_DOORS = { 26210101, 26210102, 26210103, 26210104, 26210105, 26210106 };

	private static final int[][] OCTAVIS_SCIENTIST_SPAWN = { { 207820, 120312, -10008, 28144 }, { 207450, 119936, -10008, 19504 }, { 207817, 120832, -10008, 36776 }, { 206542, 120306, -10008, 4408 }, { 206923, 119936, -10008, 12008 }, { 207458, 121218, -10008, 44440 }, { 206923, 121216, -10008, 53504 }, { 206620, 120568, -10008, 800 }, { 207194, 121082, -10008, 49000 }, { 207197, 120029, -10008, 17080 }, { 207776, 120577, -10008, 33016 }, { 206541, 120848, -10008, 60320 } };

	private static final int[][] OCTAVIS_GLADIATOR_SPAWN = { { 206519, 118937, -9976, 12416 }, { 207865, 118937, -9976, 19232 }, { 208829, 119896, -9976, 28264 }, { 208825, 121260, -9976, 38080 }, { 207875, 122209, -9976, 44144 }, { 206507, 122208, -9976, 54680 } };

	private static final int[][] ARENA_BEAST_SPAWN = { { 206692, 119375, -10008, 0 }, { 208418, 120065, -10008, 0 }, { 207700, 121810, -10008, 0 } };

	private static final int[][] OUTROOM_LOCATIONS = { { 206849, 119744, -10014 }, { 207524, 119765, -10014 }, { 208002, 120238, -10016 }, { 207995, 120911, -10016 }, { 207524, 121377, -10014 }, { 206861, 121375, -10014 } };

	private ZoneListener _epicZoneListener = new ZoneListener();
	public boolean isHardInstance = false;

	public NpcInstance octavis = null;
	public NpcInstance octavisBeast = null;
	public List<NpcInstance> volcanos = new ArrayList<NpcInstance>();
	public NpcInstance octavisPower = null;
	public int arenaBeastSpawnNumber = 0;
	public int status = 0;
	
	@Override
	protected void onCreate()
	{
		super.onCreate();
		ThreadPoolManager.getInstance().schedule(new RunnableImpl()
		{
			@Override
			public void runImpl()
			{
				for(DoorInstance door : getDoors())
					door.openMe();
			}
		}
		, 10000L);		
		isHardInstance = getInstancedZoneId() == INSTANCE_ID_HARD;
		getZone("[Octavis_epic]").addListener(_epicZoneListener);
	}

	public class ZoneListener implements OnZoneEnterLeaveListener
	{
		@Override
		public void onZoneEnter(Zone zone, Creature cha)
		{
			if(cha.isPlayer() && status < 4)
			{
				if(status == 0)
				{
					ThreadPoolManager.getInstance().schedule(new RunnableImpl()
					{
						@Override
						public void runImpl()
						{
							for(Player player : getPlayers())
								player.startScenePlayer(SceneMovie.SCENE_OCTABIS_OPENING);
						}
					}
					, 15000L);

					ThreadPoolManager.getInstance().schedule(new RunnableImpl()
					{
						@Override
						public void runImpl()
						{
							for(DoorInstance door : getDoors())
								door.closeMe();

							int octavisId = isHardInstance ? OCTAVIS_HARD_FIRST : OCTAVIS_LIGHT_FIRST;
							int beastId = isHardInstance ? OCTAVIS_LIGHT_BEAST : OCTAVIS_HARD_BEAST;
							octavisBeast = addSpawnWithoutRespawn(beastId, new Location(207244, 120579, -10008, 0), 0);
							octavis = addSpawnWithoutRespawn(octavisId, OCTAVIS_SPAWN, 0);

							for(byte i = 0; i < 4; i = (byte)(i + 1))
								volcanos.add(addSpawnWithoutRespawn(VOLCANO_ZONE, OCTAVIS_SPAWN, 0));
							
							octavisPower = addSpawnWithoutRespawn(OCTAVIS_POWER, OCTAVIS_SPAWN, 0);
						}
					}
					, 42000L);

					status = 1;
				}
			}
		}
		
		@Override
		public void onZoneLeave(Zone zone, Creature cha) 
		{
		}
	}
	

	public void nextSpawn()
	{
		switch(status)
		{
			case 1:
				if(status == 1)
				{
					status = 2;
					for(Player player : getPlayers())
						player.startScenePlayer(SceneMovie.SCENE_OCTABIS_phasech_A);
					if(octavisBeast != null)
						octavisBeast.deleteMe();
					ThreadPoolManager.getInstance().schedule(new RunnableImpl()
					{
						@Override
						public void runImpl()
						{
							int npcId = isHardInstance ? OCTAVIS_HARD_SECOND : OCTAVIS_LIGHT_SECOND;
							octavis = addSpawnWithoutRespawn(npcId, LAIR_CENTER, 0);

							for(int doorId : BEAST_DOORS)
							{
								DoorInstance door = getDoor(doorId);
								door.openMe();
							}

							for(int[] loc : OCTAVIS_GLADIATOR_SPAWN)
							{
								NpcInstance gladiator = addSpawnWithoutRespawn(OCTAVIS_GLADIATOR, new Location(loc[0], loc[1], loc[2], loc[3]), 0);
								//gladiator.getSpawn().setRespawnDelay(120);
								gladiator.setRandomWalk(false);

								int[] selectedLoc = null;
								double selectedDistance = 0.0D;
								Location currentLoc = gladiator.getLoc();
								for(int[] outloc : OUTROOM_LOCATIONS)
								{
									if((selectedLoc == null) || (selectedDistance > PositionUtils.calculateDistance(currentLoc.getX(), currentLoc.getY(), 0, outloc[0], outloc[1], 0, false)))
									{
										selectedLoc = outloc;
										selectedDistance = PositionUtils.calculateDistance(currentLoc.getX(), currentLoc.getY(), 0, selectedLoc[0], selectedLoc[1], 0, false);
									}
								}

								gladiator.setRunning();
								DefaultAI ai = (DefaultAI) gladiator.getAI();
								ai.addTaskMove(Location.findPointToStay(new Location(selectedLoc[0], selectedLoc[1], selectedLoc[2]), 50, gladiator.getGeoIndex()), true);	
							}

							ThreadPoolManager.getInstance().schedule(new RunnableImpl()
							{
								@Override
								public void runImpl()
								{
									if(octavis != null && (octavis.getNpcId() == OCTAVIS_LIGHT_SECOND || octavis.getNpcId() == OCTAVIS_HARD_SECOND))
									{
										int offset = arenaBeastSpawnNumber % 3;
										for(int i = offset; i < offset + 7; i++)
										{
											int[] loc = ARENA_BEAST_SPAWN[offset];
											NpcInstance beast = addSpawnWithoutRespawn(ARENA_BEAST, new Location(loc[offset], loc[1], loc[2], loc[3]), 0);
											beast.setRunning();
											beast.setRandomWalk(false);
											DefaultAI ai = (DefaultAI) beast.getAI();
											ai.addTaskMove(Location.findPointToStay(octavis.getLoc(), 50, beast.getGeoIndex()), true);												
										}
										arenaBeastSpawnNumber += 1;

										ThreadPoolManager.getInstance().schedule(this, 180000L);
									}
								}
							}
							, 1000L);
						}
					}
					, 10000L); 
				} 
				break;
			case 2:
				if(status == 2)
				{
					status = 3;

					for(NpcInstance npc : getNpcs())
					{
						if((npc.getNpcId() == OCTAVIS_GLADIATOR) || (npc.getNpcId() == ARENA_BEAST))
						{
							//npc.getSpawn().stopRespawn();
							npc.deleteMe();
						}
					}

					for(Player player : getPlayers())
						player.startScenePlayer(SceneMovie.SCENE_OCTABIS_phasech_B);

					ThreadPoolManager.getInstance().schedule(new RunnableImpl()
					{
						@Override
						public void runImpl()
						{
							int npcId = isHardInstance ? OCTAVIS_HARD_THIRD : OCTAVIS_LIGHT_THIRD;
							octavis = addSpawnWithoutRespawn(npcId, LAIR_CENTER, 0);

							for(int[] loc : OCTAVIS_SCIENTIST_SPAWN)
							{
									NpcInstance scientist = addSpawnWithoutRespawn(OCTAVIS_SCIENTIST, new Location(loc[0], loc[1], loc[2], loc[3]), 0);
									//scientist.getSpawn().setRespawnDelay(120);
							}
							arenaBeastSpawnNumber += 1;
						}
					}
					, 15000L);
				}	
				break;
			case 3:
				if(status == 3)
				{
					status = 4;
					for(NpcInstance npc : getNpcs())
						if(npc.getNpcId() == OCTAVIS_SCIENTIST)
						{
							//npc.getSpawn().stopRespawn();
							npc.deleteMe();
						}

					for(Player player : getPlayers())
						player.startScenePlayer(SceneMovie.SCENE_OCTABIS_ENDING);

					ThreadPoolManager.getInstance().schedule(new RunnableImpl()
					{
						@Override
						public void runImpl()
						{
							for(Player player : getPlayers())
								player.sendPacket(UsmVideo.Q005.packet(player));
						}
					}
					, 50000L);

					ThreadPoolManager.getInstance().schedule(new RunnableImpl()
					{
						@Override
						public void runImpl()
						{
							octavis = addSpawnWithoutRespawn(OCTAVIS_NPC, LAIR_CENTER, 0);
							octavis.doDie(null);
							clearReflection(5, true);
						}	
					}	
					, 70000L);
				}	
				break;
		}
	}	

}	
