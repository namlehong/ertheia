package instances;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.utils.Location;

//by other proffesionals + edit iqman
public class FortunaManager extends Reflection
{
	private static final int yarostbeznazavaniya = 23077;
	private static final int yarostbeznazavaniyaSpecial = 23077; //wtf?
	private static final int skovanniyvoitel = 23076;
	private static final int skovanniymag = 23078;
	private static final int skovanniyvoitelSpecial = 23076; //wtf?
	private static final int skovanniymagSpecial = 23078; //wtf?
	private static final int skovanniykrovopiycaSpecial = 23081; //wtf?
	private static final int skovanniykrovopiyca = 23081;
	private static final int elitevoitel = 23082;
	private static final int elitemag = 23084;
	private static final int beshenstvo = 23085;
	private static final int oblako = 23080;
	private static final int providec = 19084;
	private static final int plaksa = 25837;
	private static final int kinnen = 25840;
	private static final int resinda = 25841;
	private static final int mukshu = 25838;
	private static final int hornapi = 25839;
	private static final int yotemak = 25846;
	private static final int konyar = 25845;
	private static final int sfera = 19082;
	private static final int Selphina = 33589;
	private ScheduledFuture<?> firstStageGuardSpawn;
	private DeathListener _deathListener = new DeathListener();
	private ZoneListener _epicZoneListener;
	private boolean _entryLocked;
	private boolean _startLaunched;
	
	public FortunaManager()
	{
		_epicZoneListener = new ZoneListener();
		_entryLocked = false;
		_startLaunched = false;
	}
	
	@Override
	protected void onCollapse()
	{
		super.onCollapse();
		doCleanup();
	}

	protected void onCreate()
	{
		super.onCreate();

		getZone("[fortuna_begin]").addListener(_epicZoneListener);
	}

	private void doCleanup()
	{
		if(firstStageGuardSpawn != null)
			firstStageGuardSpawn.cancel(true);
	}

	public class ZoneListener implements OnZoneEnterLeaveListener
	{
		public ZoneListener()
		{
		}
		
		@Override
		public void onZoneEnter(Zone paramZone, Creature paramCreature)
		{
			if (_startLaunched) 
				return;
			Player localPlayer = paramCreature.getPlayer();
			if(localPlayer == null || !paramCreature.isPlayer())
				return;
			_startLaunched = true;
			ThreadPoolManager.getInstance().schedule(new StartFortunaManager(), 30000L);
		}
		
		@Override
		public void onZoneLeave(Zone paramZone, Creature paramCreature)
		{
		}
	}

	private class DeathListener implements OnDeathListener
	{
		private DeathListener()
		{
		}
		
		@Override
		public void onDeath(Creature paramCreature1, Creature paramCreature2)
		{
			if(paramCreature1.isNpc() && paramCreature1.getNpcId() == 23077)
			{
				ThreadPoolManager.getInstance().schedule(new SelphinaSpawn(), 10000L);
				paramCreature1.deleteMe();
			}
			else if(paramCreature1.isNpc() && paramCreature1.getNpcId() == 23076)
			{
				ThreadPoolManager.getInstance().schedule(new FreeStageMain(), 30000L);
				paramCreature1.deleteMe();
			}
			else if(paramCreature1.isNpc() && paramCreature1.getNpcId() == 25837)
			{
				ThreadPoolManager.getInstance().schedule(new FourStageMain(), 30000L);
				paramCreature1.deleteMe();
			}
			else if(paramCreature1.isNpc() && paramCreature1.getNpcId() == 25840)
			{
				ThreadPoolManager.getInstance().schedule(new FiveStageMain(), 30000L);
				paramCreature1.deleteMe();
			}
			else if(paramCreature1.isNpc() && paramCreature1.getNpcId() == 25845)
			{
				ThreadPoolManager.getInstance().schedule(new BonusStageMain(), 30000L);
				paramCreature1.deleteMe();
			}
			else if(paramCreature1.isNpc() && paramCreature1.getNpcId() == 23081)
			{
				ThreadPoolManager.getInstance().schedule(new SixStageMain(), 30000L);
				paramCreature1.deleteMe();
			}
			else if(paramCreature1.isNpc() && paramCreature1.getNpcId() == 25841)
			{
				ThreadPoolManager.getInstance().schedule(new SevenStageMain(), 30000L);
				paramCreature1.deleteMe();
			}
			else if(paramCreature1.isNpc() && paramCreature1.getNpcId() == 25839)
			{
				ThreadPoolManager.getInstance().schedule(new FinalStage(), 32000L);
				paramCreature1.deleteMe();
			}
			else if(paramCreature1.isNpc() && paramCreature1.getNpcId() == 25846)
			{
				ThreadPoolManager.getInstance().schedule(new CloseFortunaManager(), 35000L);
				paramCreature1.deleteMe();
			}
		}
	}

	private class CloseFortunaManager extends RunnableImpl
	{
		private CloseFortunaManager()
		{
		}
	
		@Override
		public void runImpl()
		{
			startCollapseTimer(300000L);
			doCleanup();
			for(Player p : getPlayers())
				p.sendPacket(new SystemMessagePacket(SystemMsg.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addInteger(5.0D));
		}
	}

	private class FinalStageMainMob3SubStage extends RunnableImpl
	{
		private FinalStageMainMob3SubStage()
		{
		}
		
		@Override
		public void runImpl()  throws Exception
		{
			addSpawnWithoutRespawn(23085, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23082, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(19084, new Location(42104, -176344, -7974), 0);
			addSpawnWithoutRespawn(23082, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23080, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(19084, new Location(42104, -176344, -7974), 0);
		}
	}

	private class FinalStageMainMob2SubStage extends RunnableImpl
	{
		private FinalStageMainMob2SubStage()
		{
		}
		
		@Override	
		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23085, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23082, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(19084, new Location(42104, -176344, -7974), 0);
			addSpawnWithoutRespawn(23082, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23080, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(19084, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new FinalStageMainMob3SubStage(), 12500L);
		}
	}

	private class YotemakSpawn extends RunnableImpl
	{
		private YotemakSpawn()
		{
		}
		
		@Override	
		public void runImpl() throws Exception
		{
			for(Player player : getPlayers())
				player.sendPacket(new ExShowScreenMessage(NpcString.YOTEMAK, 6000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_RIGHT, true, 1, -1, true, new String[0]));
			NpcInstance hornapiraid = addSpawnWithoutRespawn(25846, new Location(42104, -175320, -7974, 15956), 0);
			hornapiraid.addListener(_deathListener);
			NpcInstance ron = addSpawnWithoutRespawn(25825, new Location(42104, -175320, -7974, 15956), 0);
			ron.addListener(_deathListener);			
			ThreadPoolManager.getInstance().schedule(new FinalStageMainMob2SubStage(), 11500L);
		}
	}

	private class FinalStageMainMob1SubStage extends RunnableImpl
	{
		private FinalStageMainMob1SubStage()
		{
		}
	
		@Override
		public void runImpl() throws Exception
		{
			for(Player player : getPlayers())
				player.sendPacket(new ExShowScreenMessage(NpcString.LAST_STAGE, 6000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_RIGHT, true, 1, -1, true, new String[0]));
			addSpawnWithoutRespawn(23085, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23082, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(19084, new Location(42104, -176344, -7974), 0);
			addSpawnWithoutRespawn(23082, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23080, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(19084, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new YotemakSpawn(), 11500L);
		}
	}

	private class FinalStage extends RunnableImpl
	{
		private FinalStage()
		{
		}
		
		@Override
		public void runImpl() throws Exception
		{
			for(Player player : getPlayers())
				player.sendPacket(new ExShowScreenMessage(NpcString.VELIKOPLEPNO, 6000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true, new String[0]));

			ThreadPoolManager.getInstance().schedule(new FinalStageMainMob1SubStage(), 10000L);
		}
	}

	private class SevenStageRaidsSpawn extends RunnableImpl
	{
		private SevenStageRaidsSpawn()
		{
		}
		
		@Override
		public void runImpl() throws Exception
		{
			for(Player player : getPlayers())
				player.sendPacket(new ExShowScreenMessage(NpcString.MUKSHUANDHOPNAP, 6000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_RIGHT, true, 1, -1, true, new String[0]));

			addSpawnWithoutRespawn(25838, new Location(42102, -175325, -7974), 0);
			NpcInstance hornapiraid = addSpawnWithoutRespawn(25839, new Location(42104, -175320, -7974, 15956), 0);
			hornapiraid.addListener(_deathListener);
		}
	}

	private class SevenStageMainMob6SubStage extends RunnableImpl
	{
		private SevenStageMainMob6SubStage()
		{
		}
		
		@Override
		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23085, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23082, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(19084, new Location(42104, -176344, -7974), 0);
			addSpawnWithoutRespawn(23082, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23080, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(19084, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new SevenStageRaidsSpawn(), 19500L);
		}
	}

	private class SevenStageMainMob5SubStage extends RunnableImpl
	{
		private SevenStageMainMob5SubStage()
		{
		}
		
		@Override
		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23085, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23082, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(19084, new Location(42104, -176344, -7974), 0);
			addSpawnWithoutRespawn(23082, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23080, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(19084, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new SevenStageMainMob6SubStage(), 12500L);
		}
	}

	private class SevenStageMainMob4SubStage extends RunnableImpl
	{
		private SevenStageMainMob4SubStage()
		{
		}
		
		@Override
		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23085, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23082, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(19084, new Location(42104, -176344, -7974), 0);
			addSpawnWithoutRespawn(23082, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23080, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(19084, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new SevenStageMainMob5SubStage(), 12500L);
		}
	}

	private class SevenStageMainMob3SubStage extends RunnableImpl
	{
		private SevenStageMainMob3SubStage()
		{
		}
		
		@Override
		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23085, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23082, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(19084, new Location(42104, -176344, -7974), 0);
			addSpawnWithoutRespawn(23082, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23080, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(19084, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new SevenStageMainMob4SubStage(), 12500L);
		}
	}

	private class SevenStageMainMob2SubStage extends RunnableImpl
	{
		private SevenStageMainMob2SubStage()
		{
		}
		
		@Override
		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23085, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23082, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(19084, new Location(42104, -176344, -7974), 0);
			addSpawnWithoutRespawn(23082, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23080, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(19084, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new SevenStageMainMob3SubStage(), 12500L);
		}
	}

	private class SevenStageMainMob1SubStage extends RunnableImpl
	{
		private SevenStageMainMob1SubStage()
		{
		}
		
		@Override
		public void runImpl() throws Exception
		{
			for(Player player : getPlayers())
				player.sendPacket(new ExShowScreenMessage(NpcString.STAGE_7, 6000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_RIGHT, true, 1, -1, true, new String[0]));

			addSpawnWithoutRespawn(23085, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23082, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(19084, new Location(42104, -176344, -7974), 0);
			addSpawnWithoutRespawn(23082, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23080, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(19084, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new SevenStageMainMob2SubStage(), 12500L);
		}
	}

	private class SevenStageMain extends RunnableImpl
	{
		private SevenStageMain()
		{
		}
		
		@Override
		public void runImpl() throws Exception
		{
			for(Player player : getPlayers())
				player.sendPacket(new ExShowScreenMessage(NpcString.POGLOTIVSE, 6000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true, new String[0]));

			ThreadPoolManager.getInstance().schedule(new SevenStageMainMob1SubStage(), 10000L);
		}
	}

	private class ResindaSpawn extends RunnableImpl
	{
		private ResindaSpawn()
		{
		}
		
		@Override
		public void runImpl() throws Exception
		{
			for(Player player : getPlayers())
				player.sendPacket(new ExShowScreenMessage(NpcString.RESINA, 6000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_RIGHT, true, 1, -1, true, new String[0]));
			NpcInstance resindaraid = addSpawnWithoutRespawn(25841, new Location(42104, -175320, -7974, 15956), 0);
			resindaraid.addListener(_deathListener);
		}
	}

	private class SixStageMainMob7SubStage extends RunnableImpl
	{
		private SixStageMainMob7SubStage()
		{
		}
		
		@Override
		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23078, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23081, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23076, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new ResindaSpawn(), 19500L);
		}
	}
	
	private class SixStageMainMob6SubStage extends RunnableImpl
	{
		private SixStageMainMob6SubStage()
		{
		}
		
		@Override
		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23081, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23076, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23081, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new SixStageMainMob7SubStage(), 12500L);
		}
	}

	private class SixStageMainMob5SubStage extends RunnableImpl
	{
		private SixStageMainMob5SubStage()
		{
		}
		
		@Override
		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23076, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23078, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23081, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new SixStageMainMob6SubStage(), 12500L);
		}
	}

	private class SixStageMainMob4SubStage extends RunnableImpl
	{
		private SixStageMainMob4SubStage()
		{
		}
		
		@Override	
		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23081, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23076, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23081, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new SixStageMainMob5SubStage(), 12500L);
		}
	}

	private class SixStageMainMob3SubStage extends RunnableImpl
	{
		private SixStageMainMob3SubStage()
		{
		}
		
		@Override
		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23078, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23081, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23076, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new SixStageMainMob4SubStage(), 12500L);
		}
	}

	private class SixStageMainMob2SubStage extends RunnableImpl
	{
		private SixStageMainMob2SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23076, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23081, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23078, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new SixStageMainMob3SubStage(), 12500L);
		}
	}

	private class SixStageMainMob1SubStage extends RunnableImpl
	{
		private SixStageMainMob1SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			for(Player player : getPlayers())
				player.sendPacket(new ExShowScreenMessage(NpcString.STAGE_6, 6000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_RIGHT, true, 1, -1, true, new String[0]));
			addSpawnWithoutRespawn(23081, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23076, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23081, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new SixStageMainMob2SubStage(), 12500L);
		}
	}

	private class SixStageMain extends RunnableImpl
	{
		private SixStageMain()
		{
		}

		public void runImpl() throws Exception
		{
			for(Player player : getPlayers())
				player.sendPacket(new ExShowScreenMessage(NpcString.VSETOLKONACHINAETSA, 6000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true, new String[0]));
			ThreadPoolManager.getInstance().schedule(new SixStageMainMob1SubStage(), 10000L);
		}
	}

	private class EndBonusStageMob extends RunnableImpl
	{
		private EndBonusStageMob()
		{
		}

		public void runImpl() throws Exception
		{
			NpcInstance krovopiycaSpecial = addSpawnWithoutRespawn(23081, new Location(42104, -176344, -7974, 15956), 0);
			krovopiycaSpecial.addListener(_deathListener);
		}
	}

	private class BonusStageMainMob5SubStage extends RunnableImpl
	{
		private BonusStageMainMob5SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23081, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23081, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23081, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new EndBonusStageMob(), 12500L);
		}
	}

	private class BonusStageMainMob4SubStage extends RunnableImpl
	{
		private BonusStageMainMob4SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23081, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23081, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23081, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new BonusStageMainMob5SubStage(), 12500L);
		}
	}

	private class BonusStageMainMob3SubStage extends RunnableImpl
	{
		private BonusStageMainMob3SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23081, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23081, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23081, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new BonusStageMainMob4SubStage(), 12500L);
		}
	}

	private class BonusStageMainMob2SubStage extends RunnableImpl
	{
		private BonusStageMainMob2SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23081, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23081, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23081, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new BonusStageMainMob3SubStage(), 12500L);
		}
	}

	private class BonusStageMainMob1SubStage extends RunnableImpl
	{
		private BonusStageMainMob1SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			for(Player player : getPlayers())
				player.sendPacket(new ExShowScreenMessage(NpcString.BONUS_STAGE, 6000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_RIGHT, true, 1, -1, true, new String[0]));
			addSpawnWithoutRespawn(23081, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23081, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23081, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new BonusStageMainMob2SubStage(), 12500L);
		}
	}

	private class BonusStageMain extends RunnableImpl
	{
		private BonusStageMain()
		{
		}

		public void runImpl() throws Exception
		{
			ThreadPoolManager.getInstance().schedule(new BonusStageMainMob1SubStage(), 10000L);
		}
	}

	private class KonyarSpawn extends RunnableImpl
	{
		private KonyarSpawn()
		{
		}

		public void runImpl() throws Exception
		{
			for(Player player : getPlayers())
				player.sendPacket(new ExShowScreenMessage(NpcString.KONYAR, 6000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_RIGHT, true, 1, -1, true, new String[0]));
			NpcInstance localObject = addSpawnWithoutRespawn(25845, new Location(42104, -175320, -7974, 15956), 0);
			localObject.addListener(_deathListener);
		}
	}

	private class FiveStageMainMob8SubStage extends RunnableImpl
	{
		private FiveStageMainMob8SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23076, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23078, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23076, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new KonyarSpawn(), 19500L);
		}
	}

	private class FiveStageMainMob7SubStage extends RunnableImpl
	{
		private FiveStageMainMob7SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23078, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23078, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23076, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new FiveStageMainMob8SubStage(), 12500L);
		}
	}

	private class FiveStageMainMob6SubStage extends RunnableImpl
	{
		private FiveStageMainMob6SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23078, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23076, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23078, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new FiveStageMainMob7SubStage(), 12500L);
		}
	}

	private class FiveStageMainMob5SubStage extends RunnableImpl
	{
		private FiveStageMainMob5SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23078, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23078, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23076, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new FiveStageMainMob6SubStage(), 12500L);
		}
	}

	private class FiveStageMainMob4SubStage extends RunnableImpl
	{
		private FiveStageMainMob4SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23076, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23078, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23076, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new FiveStageMainMob5SubStage(), 12500L);
		}
	}

	private class FiveStageMainMob3SubStage extends RunnableImpl
	{
		private FiveStageMainMob3SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23078, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23076, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23078, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new FiveStageMainMob4SubStage(), 12500L);
		}
	}

	private class FiveStageMainMob2SubStage extends RunnableImpl
	{
		private FiveStageMainMob2SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23076, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23078, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23076, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new FiveStageMainMob3SubStage(), 12500L);
		}
	}

	private class FiveStageMainMob1SubStage extends RunnableImpl
	{
		private FiveStageMainMob1SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			for(Player localPlayer : getPlayers())
				localPlayer.sendPacket(new ExShowScreenMessage(NpcString.STAGE_5, 6000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_RIGHT, true, 1, -1, true, new String[0]));
			addSpawnWithoutRespawn(23078, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23076, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23078, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new FiveStageMainMob2SubStage(), 12500L);
		}
	}

	private class FiveStageMain extends RunnableImpl
	{
		private FiveStageMain()
		{
		}

		public void runImpl() throws Exception
		{
			for(Player localPlayer : getPlayers())
				localPlayer.sendPacket(new ExShowScreenMessage(NpcString.PRIDETSAOTPRAVITNEMNOGO, 6000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true, new String[0]));
			ThreadPoolManager.getInstance().schedule(new FiveStageMainMob1SubStage(), 10000L);
		}
	}

	private class KinnenSpawn extends RunnableImpl
	{
		private KinnenSpawn()
		{
		}

		public void runImpl() throws Exception
		{
			for(Player localPlayer : getPlayers())
				localPlayer.sendPacket(new ExShowScreenMessage(NpcString.KINEN, 6000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_RIGHT, true, 1, -1, true, new String[0]));
			NpcInstance localObject = addSpawnWithoutRespawn(25840, new Location(42104, -175320, -7974, 15956), 0);
			localObject.addListener(_deathListener);
		}
	}

	private class FourStageMainMob7SubStage extends RunnableImpl
	{
		private FourStageMainMob7SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23076, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23076, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23076, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new KinnenSpawn(), 19500L);
		}
	}

	private class FourStageMainMob6SubStage extends RunnableImpl
	{
		private FourStageMainMob6SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23078, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23078, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23078, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new FourStageMainMob7SubStage(), 12500L);
		}
	}

	private class FourStageMainMob5SubStage extends RunnableImpl
	{
		private FourStageMainMob5SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23076, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23076, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23076, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new FourStageMainMob6SubStage(), 12500L);
		}
	}

	private class FourStageMainMob4SubStage extends RunnableImpl
	{
		private FourStageMainMob4SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23076, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23076, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23076, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new FourStageMainMob5SubStage(), 12500L);
		}
	}

	private class FourStageMainMob3SubStage extends RunnableImpl
	{
		private FourStageMainMob3SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23078, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23078, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23078, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new FourStageMainMob4SubStage(), 12500L);
		}
	}

	private class FourStageMainMob2SubStage extends RunnableImpl
	{
		private FourStageMainMob2SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23076, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23076, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23076, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new FourStageMainMob3SubStage(), 12500L);
		}
	}

	private class FourStageMainMob1SubStage extends RunnableImpl
	{
		private FourStageMainMob1SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			for(Player localPlayer : getPlayers())
				localPlayer.sendPacket(new ExShowScreenMessage(NpcString.STAGE_4, 6000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_RIGHT, true, 1, -1, true, new String[0]));
			addSpawnWithoutRespawn(23078, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23078, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23078, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new FourStageMainMob2SubStage(), 12500L);
		}
	}

	private class FourStageMain extends RunnableImpl
	{
		private FourStageMain()
		{
		}

		public void runImpl() throws Exception
		{
			for(Player localPlayer : getPlayers())
				localPlayer.sendPacket(new ExShowScreenMessage(NpcString.POGLOTITESVET, 6000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true, new String[0]));
			ThreadPoolManager.getInstance().schedule(new FourStageMainMob1SubStage(), 10000L);
		}
	}

	private class PlaksaSpawn extends RunnableImpl
	{
		private PlaksaSpawn()
		{
		}

		public void runImpl() throws Exception
		{
			for(Player localPlayer : getPlayers())
				localPlayer.sendPacket(new ExShowScreenMessage(NpcString.PLAKSA, 6000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_RIGHT, true, 1, -1, true, new String[0]));
			NpcInstance localObject = addSpawnWithoutRespawn(25837, new Location(42104, -175320, -7974, 15956), 0);
			localObject.addListener(_deathListener);
		}
	}

	private class FreeStageMainMob7SubStage extends RunnableImpl
	{
		private FreeStageMainMob7SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23078, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23078, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23078, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new PlaksaSpawn(), 19500L);
		}
	}

	private class FreeStageMainMob6SubStage extends RunnableImpl
	{
		private FreeStageMainMob6SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23076, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23076, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23076, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new FreeStageMainMob7SubStage(), 12500L);
		}
	}

	private class FreeStageMainMob5SubStage extends RunnableImpl
	{
		private FreeStageMainMob5SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23078, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23078, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23078, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new FreeStageMainMob6SubStage(), 12500L);
		}
	}

	private class FreeStageMainMob4SubStage extends RunnableImpl
	{
		private FreeStageMainMob4SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23076, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23076, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23076, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new FreeStageMainMob5SubStage(), 12500L);
		}
	}

	private class FreeStageMainMob3SubStage extends RunnableImpl
	{
		private FreeStageMainMob3SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23078, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23078, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23078, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new FreeStageMainMob4SubStage(), 12500L);
		}
	}

	private class FreeStageMainMob2SubStage extends RunnableImpl
	{
		private FreeStageMainMob2SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23076, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23076, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23076, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new FreeStageMainMob3SubStage(), 12500L);
		}
	}

	private class FreeStageMainMob1SubStage extends RunnableImpl
	{
		private FreeStageMainMob1SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			for(Player localPlayer : getPlayers())
				localPlayer.sendPacket(new ExShowScreenMessage(NpcString.STAGE_3, 6000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_RIGHT, true, 1, -1, true, new String[0]));
			addSpawnWithoutRespawn(23078, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23078, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23078, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new FreeStageMainMob2SubStage(), 12500L);
		}
	}

	private class FreeStageMain extends RunnableImpl
	{
		private FreeStageMain()
		{
		}

		public void runImpl() throws Exception
		{
			for(Player localPlayer : getPlayers())
				localPlayer.sendPacket(new ExShowScreenMessage(NpcString.POSMOTRIM, 6000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true, new String[0]));
			ThreadPoolManager.getInstance().schedule(new FreeStageMainMob1SubStage(), 10000L);
		}
	}

	private class EndTwoStageMob extends RunnableImpl
	{
		private EndTwoStageMob()
		{
		}

		public void runImpl() throws Exception
		{
			NpcInstance localNpcInstance = addSpawnWithoutRespawn(23076, new Location(42104, -176344, -7974, 15956), 0);
			localNpcInstance.addListener(_deathListener);
		}
	}

	private class TwoStageMainMob6SubStage extends RunnableImpl
	{
		private TwoStageMainMob6SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23076, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23076, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23076, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new EndTwoStageMob(), 1400L);
		}
	}

	private class TwoStageMainMob5SubStage extends RunnableImpl
	{
		private TwoStageMainMob5SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23077, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23077, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23077, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new TwoStageMainMob6SubStage(), 13000L);
		}
	}

	private class TwoStageMainMob4SubStage extends RunnableImpl
	{
		private TwoStageMainMob4SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23076, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23076, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23076, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new TwoStageMainMob5SubStage(), 13000L);
		}
	}

	private class TwoStageMainMob3SubStage extends RunnableImpl
	{
		private TwoStageMainMob3SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23077, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23077, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23077, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new TwoStageMainMob4SubStage(), 13000L);
		}
	}

	private class TwoStageMainMob2SubStage extends RunnableImpl
	{
		private TwoStageMainMob2SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23076, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23076, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23076, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new TwoStageMainMob3SubStage(), 13000L);
		}
	}

	private class TwoStageMainMob1SubStage extends RunnableImpl
	{
		private TwoStageMainMob1SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			for(Player localPlayer : getPlayers())
				localPlayer.sendPacket(new ExShowScreenMessage(NpcString.STAGE_2, 6000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_RIGHT, true, 1, -1, true, new String[0]));
			addSpawnWithoutRespawn(23077, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23077, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23077, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new TwoStageMainMob2SubStage(), 13000L);
		}
	}

	private class TwoStageMain extends RunnableImpl
	{
		private TwoStageMain()
		{
		}

		public void runImpl() throws Exception
		{
			for(Player localPlayer : getPlayers())
				localPlayer.sendPacket(new ExShowScreenMessage(NpcString.AETTEKTO, 6000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true, new String[0]));
			ThreadPoolManager.getInstance().schedule(new TwoStageMainMob1SubStage(), 30000L);
		}
	}

	private class SpawnCubics extends RunnableImpl
	{
		private SpawnCubics()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(19082, new Location(41768, -175432, -7980), 0);
			addSpawnWithoutRespawn(19082, new Location(41928, -175256, -7980), 0);
			addSpawnWithoutRespawn(19082, new Location(42296, -175272, -7980), 0);
			addSpawnWithoutRespawn(19082, new Location(42456, -175432, -7980), 0);
			addSpawnWithoutRespawn(19082, new Location(42456, -175784, -7980), 0);
			addSpawnWithoutRespawn(19082, new Location(42280, -175944, -7980), 0);
			addSpawnWithoutRespawn(19082, new Location(41928, -175944, -7980), 0);
			addSpawnWithoutRespawn(19082, new Location(41768, -175784, -7980), 0);

			ThreadPoolManager.getInstance().schedule(new TwoStageMain(), 22000L);
		}
	}

	private class SelphinaSpawn extends RunnableImpl
	{
		private SelphinaSpawn()
		{
		}

		public void runImpl() throws Exception
		{
			for(Player localPlayer : getPlayers())
				localPlayer.sendPacket(new ExShowScreenMessage(NpcString.ZAVECHNIYPOKOI, 6000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true, new String[0]));
			addSpawnWithoutRespawn(33589, new Location(42104, -175320, -7974), 0);

			ThreadPoolManager.getInstance().schedule(new SpawnCubics(), 11000L);
		}
	}

	private class EndOneStageMob extends RunnableImpl
	{
		private EndOneStageMob()
		{
		}

		public void runImpl() throws Exception
		{
			NpcInstance localNpcInstance = addSpawnWithoutRespawn(23077, new Location(42104, -176344, -7974, 15956), 0);
			localNpcInstance.addListener(_deathListener);
		}
	}

	private class OneStageMainMob7SubStage extends RunnableImpl
	{
		private OneStageMainMob7SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23077, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23077, new Location(42808, -175608, -7974), 0);

			ThreadPoolManager.getInstance().schedule(new EndOneStageMob(), 700L);
		}
	}

	private class OneStageMainMob6SubStage extends RunnableImpl
	{
		private OneStageMainMob6SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23077, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23077, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23077, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new OneStageMainMob7SubStage(), 11400L);
		}
	}

	private class OneStageMainMob5SubStage extends RunnableImpl
	{
		private OneStageMainMob5SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23077, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23077, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23077, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new OneStageMainMob6SubStage(), 11400L);
		}
	}

	private class OneStageMainMob4SubStage extends RunnableImpl
	{
		private OneStageMainMob4SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23077, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23077, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23077, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new OneStageMainMob5SubStage(), 11400L);
		}
	}

	private class OneStageMainMob3SubStage extends RunnableImpl
	{
		private OneStageMainMob3SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23077, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23077, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23077, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new OneStageMainMob4SubStage(), 11400L);
		}
	}

	private class OneStageMainMob2SubStage extends RunnableImpl
	{
		private OneStageMainMob2SubStage()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23077, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23077, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23077, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new OneStageMainMob3SubStage(), 11400L);
		}
	}

	private class OneStageMain extends RunnableImpl
	{
		private OneStageMain()
		{
		}

		public void runImpl() throws Exception
		{
			addSpawnWithoutRespawn(23077, new Location(41448, -175608, -7974), 0);
			addSpawnWithoutRespawn(23077, new Location(42808, -175608, -7974), 0);
			addSpawnWithoutRespawn(23077, new Location(42104, -176344, -7974), 0);
			ThreadPoolManager.getInstance().schedule(new OneStageMainMob2SubStage(), 11400L);
		}
	}

	private class StartInstance extends RunnableImpl
	{
		private StartInstance()
		{
		}

		public void runImpl() throws Exception
		{
			openDoor(21120001);
			for(Player localPlayer : getPlayers())
				localPlayer.sendPacket(new ExShowScreenMessage(NpcString.WHO_POTR_OUR_SAFETY, 6000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true, new String[0]));
			ThreadPoolManager.getInstance().schedule(new OneStageMain(), 15000L);
		}
	}

	private class StartFortunaManager extends RunnableImpl
	{
		private StartFortunaManager()
		{
		}

		public void runImpl() throws Exception
		{
			_entryLocked = true;
			ThreadPoolManager.getInstance().schedule(new StartInstance(), 20000L);
		}
	}
}