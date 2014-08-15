package instances;

import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.*;
import l2s.gameserver.network.l2.s2c.*;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.utils.Location;

/**
 * Класс инстанса на парнасе.
 *
 * @author Awakeninger
 */

public class CrystalHall extends Reflection {
    private static final int RB1 = 25881;
	private static final int RB2 = 25881;
	private static final int Сannon1 = 19008;
	private static final int Cannon2 = 19008;   
	private static final int Cannon3 = 19008;
	private static final int Cannon4 = 19008; 
	private static final int Сannon5 = 19008;
	private static final int Cannon6 = 19008;   
	private static final int Cannon7 = 19008;
	private static final int Cannon8 = 19009;//После нее открываем двери
	private static final int Exchanger = 33388;
	private static final int DoorOutside = 24220005;
	private static final int DoorInside = 24220006;
	private static int i;
	private long _savedTime;
	private boolean _lockedTurn = false;
	private Location Сannon1Loc = new Location(143144, 145832, -12061);
	private Location Сannon2Loc = new Location(141912, 144200, -11949);
	private Location Сannon3Loc = new Location(143368, 143768, -11976);
	private Location Сannon4Loc = new Location(145544, 143746, -11841);
	private Location Сannon5Loc = new Location(147544, 144872, -12251);
	private Location Сannon6Loc = new Location(148952, 145224, -12326);
	private Location Сannon7Loc = new Location(148152, 146136, -12305);
	private Location Сannon8Loc = new Location(149096, 146872, -12369);
    private Location RB1Loc = new Location(152984, 145960, -12609, 15640);
	private Location RB2Loc = new Location(152536, 145960, -12609, 15640);
	NpcInstance can8 = null;
	private static Player play;
	private DeathListener _deathListener = new DeathListener();
	
	@Override
	protected void onCreate()
	{
		super.onCreate();
		_savedTime = System.currentTimeMillis();
		
		can8 = addSpawnWithoutRespawn(Cannon8, Сannon8Loc, 0);
		can8.addListener(_deathListener);
		can8.setIsInvul(true);
		NpcInstance can1 = addSpawnWithoutRespawn(Сannon1, Сannon1Loc, 0);
		can1.addListener(_deathListener);
		NpcInstance can2 = addSpawnWithoutRespawn(Cannon2, Сannon2Loc, 0);
		can2.addListener(_deathListener);
		NpcInstance can3 = addSpawnWithoutRespawn(Cannon3, Сannon3Loc, 0);
		can3.addListener(_deathListener);
		NpcInstance can4 = addSpawnWithoutRespawn(Cannon4, Сannon4Loc, 0);
		can4.addListener(_deathListener);
		NpcInstance can5 = addSpawnWithoutRespawn(Сannon5, Сannon5Loc, 0);
		can5.addListener(_deathListener);
		NpcInstance can6 = addSpawnWithoutRespawn(Cannon6, Сannon6Loc, 0);
		can6.addListener(_deathListener);
		NpcInstance can7 = addSpawnWithoutRespawn(Cannon7, Сannon7Loc, 0);
		can7.addListener(_deathListener);
		NpcInstance RB1N = addSpawnWithoutRespawn(RB1, RB1Loc, 0);
		RB1N.addListener(_deathListener);
		NpcInstance RB2N = addSpawnWithoutRespawn(RB2, RB2Loc, 0);
		RB2N.addListener(_deathListener);
	}	
	@Override
	public void onPlayerEnter(Player player) 
	{
        super.onPlayerEnter(player);
		player.sendPacket(new ExSendUIEventPacket(player, 0, 1, (int) (System.currentTimeMillis() - _savedTime) / 1000, 0, NpcString.ELAPSED_TIME));
		player.setVar("Cannon0", "true", -1);
		player.setVar("ED1", "true", -1);		

    }
	@Override
	public void onPlayerExit(Player player)
	{
		super.onPlayerExit(player);
		player.sendPacket(new ExSendUIEventPacket(player, 1, 1, 0, 0));
	}	
	
	
	private class DeathListener implements OnDeathListener 
	{
        private NpcInstance exchange;

		@Override
        public void onDeath(Creature self, Creature killer) 
		{
            if (self.isNpc() && self.getNpcId() == Сannon1) 
			{
				for (Player p : getPlayers())
				{
					if(p.getVar("Cannon0") != null)
					{
						p.sendPacket(new ExShowScreenMessage(NpcString.Success_destroying, 12000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER, true, 1, -1, true, "1"));
						p.setVar("Cannon1", "true", -1);
						p.unsetVar("Cannon0");
					}
					else if(p.getVar("Cannon1") != null)
					{
						p.sendPacket(new ExShowScreenMessage(NpcString.Success_destroying, 12000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER, true, 1, -1, true, "2"));
						p.setVar("Cannon2", "true", -1);
						p.unsetVar("Cannon1");
					}
					else if(p.getVar("Cannon2") != null)
					{
						p.sendPacket(new ExShowScreenMessage(NpcString.Success_destroying, 12000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER, true, 1, -1, true, "3"));
						p.setVar("Cannon3", "true", -1);
						p.unsetVar("Cannon2");
					}
					else if(p.getVar("Cannon3") != null)
					{
						p.sendPacket(new ExShowScreenMessage(NpcString.Success_destroying, 12000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER, true, 1, -1, true, "4"));
						p.setVar("Cannon4", "true", -1);
						p.unsetVar("Cannon3");
					}
					else if(p.getVar("Cannon4") != null)
					{
						p.sendPacket(new ExShowScreenMessage(NpcString.Success_destroying, 12000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER, true, 1, -1, true, "5"));
						p.setVar("Cannon5", "true", -1);
						p.unsetVar("Cannon4");
					}
					else if(p.getVar("Cannon5") != null)
					{
						p.sendPacket(new ExShowScreenMessage(NpcString.Success_destroying, 12000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER, true, 1, -1, true, "6"));
						p.setVar("Cannon6", "true", -1);
						p.unsetVar("Cannon5");
					}
					else if(p.getVar("Cannon6") != null)
					{
						p.sendPacket(new ExShowScreenMessage(NpcString.Success_destroying, 12000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER, true, 1, -1, true, "7"));
						p.setVar("Cannon7", "true", -1);
						p.unsetVar("Cannon6");
						if(can8 != null)
							can8.setIsInvul(false);
					}
				}
			}
			else if (self.isNpc() && self.getNpcId() == Cannon8) 
			{
				for (Player p : getPlayers())
				{
					p.sendPacket(new ExShowScreenMessage(NpcString.Success_destroying_open_door, 12000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_CENTER, true, 1, -1, true));
					p.unsetVar("Cannon7");
				}
				getDoor(DoorOutside).openMe();
				getDoor(DoorInside).openMe();
			}
			else if (self.isNpc() && self.getNpcId() == RB1) 
			{
				for (Player p : getPlayers())
				{
					if(p.getVar("ED1") != null && p.getVar("Emam Dead") == null)
					{
						p.setVar("Emam Dead", "true", -1);
						p.unsetVar("ED1");
					}
					else if(p.getVar("Emam Dead") != null && p.getVar("ED1") == null)
					{
						p.sendPacket(new SystemMessagePacket(SystemMsg.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addInteger(5));
						startCollapseTimer(5 * 60 * 1000L);	
						exchange = addSpawnWithoutRespawn(Exchanger, RB2Loc, 0);
						p.unsetVar("Emam Dead");	
						
					}
				}
			}
		}
	}
}