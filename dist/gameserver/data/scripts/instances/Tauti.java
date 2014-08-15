package instances;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.instancemanager.WorldStatisticsManager;
import l2s.gameserver.listener.actor.OnCurrentHpDamageListener;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.worldstatistics.CategoryType;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.components.SceneMovie;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.utils.Location;

/**
 * @author KilRoy
 * http://l2on.net/?c=npc&id=29234 - Extreme Mode Tauti
 * http://l2on.net/?c=npc&id=29233 - Normal Mode Tauti
 * http://l2on.net/?c=npc&id=19287 - Normal Mode Jahak
 * 
 * http://l2on.net/?c=npc&id=29237 - Tauti Axe Extreme Mode
 * http://l2on.net/?c=npc&id=29236 - Tauti Axe Normal Mode
*/
public class Tauti extends Reflection
{
	private static final int INSTANCE_ID_LIGHT = 218;
	private static final int INSTANCE_ID_HARD = 219;

	//private static final int TAUTI_EXTREME = 29234;
	//private static final int TAUTI_EXTREME_AXE = 29237;
	private static final int TAUTI_EXTREME = 29233;
	private static final int TAUTI_EXTREME_AXE = 29236;

	private static final int TAUTI_NORMAL = 29233;
	private static final int TAUTI_NORMAL_AXE = 29236;
	private static final int JAHAK = 19287;

	private static final String FIRST_ROOM_GROUP = "tauti_3rd_stage_1st_room";
	private static final String SECOND_ROOM_GROUP = "tauti_3rd_stage_2nd_room";

	private static final int DOOR_HALL = 15240001;
	private static final int DOOR_TAUTI_ROOM = 15240002;

	private static final Location TAUTI_SPAWN = new Location(-147264, 212896, -10056);

	private static int[] KUNDAS = { 19262, 19263, 19264 };
	private static int[] SOFAS = { 33679, 33680 };
	private static int[] SAY_TIMER = { 5000, 8000, 12000, 14000 };

	private NpcInstance _tauti;
	private NpcInstance _tautiAxe;

	private ZoneListener _epicZoneListener = new ZoneListener();
	private DeathListener _deathListener = new DeathListener();
	private CurrentHpListener _currentHpListener = new CurrentHpListener();

	private boolean _entryLocked = false;
	private boolean _startLaunched = false;
	private boolean _sayLocked = false;
	private boolean _hpListenerLocked = false;
	private boolean _reenterLocked = false;
	private boolean _done = false;

	private boolean _isExtremeMode = false;
	
	private int _stage = 0;

	private AtomicInteger raidplayers = new AtomicInteger();

	private static final NpcString[] KUNDAS_MESSAGES = {
		NpcString.EVERYONE_DIE, 
		NpcString.FOR_TAUTI, 
		NpcString.EVEN_RATS_STRUNGGLE_WHEN_YOU_STEP_ON_THEM, 
		NpcString.YOU_RAT_LIKE_CREATURES, 
		NpcString.TODAY_MY_WEAPON_WILL_FEAST_ON_YOUR_PETRAS, 
		NpcString.HAHAHAHA_HAHAHAHA_PUNY_INSECTS, 
		NpcString.I_WILL_PUNISH_YOU_IN_THE_NAME_TAUTI_THE_CRIME_IS_STEALING_THE_PUNISHMENT_IS_DEATH, 
		NpcString.FIGHT_FOR_THE_SAKE_OF_OUR_FUTURE
	};

	private static final NpcString[] SOFAS_MESSAGES = {
		NpcString.FOR_OUR_FRIENDS_AND_FAMILY, 
		NpcString.YOU_KUNDANOMUS_MY_WEAPON_ISNT_GREAT_BUT_I_WILL_STILL_CUT_OFF_YOUR_HEADS_WITH_IT, 
		NpcString.GIVE_ME_FREEDOM_OR_GIVE_ME_DEATH, 
		NpcString.US_TODAY_HERE_WE_SHALL_WRITE_HISTORY_BY_DEFEATING_TAUTI_FOR_FREEDOM_AND_HAPPINESS, 
		NpcString.WE_ARE_NOT_YOUR_PETS_OR_CATTLE, 
		NpcString.YOU_WILL_DIE_AND_I_WILL_LIVE, 
		NpcString.WE_CANNOT_FORGIVE_TAUTI_FOR_FEEDING_ON_US_ANYMORE, 
		NpcString.FIGHT_FOR_THE_SAKE_OF_OUR_FUTURE, 
		NpcString.IF_WE_ALL_FALL_HERE_OUR_PLAN_WILL_CERTAINLY_FAIL_PLEASE_PROTECT_MY_FRIENDS
	};

	@Override
	protected void onCreate()
	{
		super.onCreate();

		_isExtremeMode = getInstancedZoneId() == INSTANCE_ID_HARD;
		getZone("[tauti_epic]").addListener(_epicZoneListener);
	}

	@Override
	public void onPlayerEnter(Player player)
	{
		if(!_reenterLocked)
		{
			spawnByGroup(FIRST_ROOM_GROUP);
			spawnByGroup(SECOND_ROOM_GROUP);

			_stage = 1;

			ThreadPoolManager.getInstance().schedule(new SayChatTask(Rnd.get(KUNDAS_MESSAGES), Rnd.get(KUNDAS)), 15000);
			ThreadPoolManager.getInstance().schedule(new SayChatTask(Rnd.get(SOFAS_MESSAGES), Rnd.get(SOFAS)), 8000);

			_reenterLocked = true;
		}
	}

	private boolean checkstartCond(int raidplayers)
	{
		return !(raidplayers < getInstancedZone().getMinParty() || _startLaunched);
	}

	public class ZoneListener implements OnZoneEnterLeaveListener
	{
		@Override
		public void onZoneEnter(Zone zone, Creature cha)
		{
			if(_entryLocked)
				return;

			Player player = cha.getPlayer();
			if(player == null || !cha.isPlayer())
				return;

			if(checkstartCond(raidplayers.incrementAndGet()))
			{
				ThreadPoolManager.getInstance().schedule(new StartNormalTauti(), 60000L);
				_startLaunched = true;
			}
		}

		@Override
		public void onZoneLeave(Zone zone, Creature cha)
		{
			//
		}
	}

	private class StartNormalTauti extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			_entryLocked = true;
			_sayLocked = true;
			_stage = 2;

			closeDoor(DOOR_HALL);
			closeDoor(DOOR_TAUTI_ROOM);

			for(Player player : getPlayers())
				player.startScenePlayer(SceneMovie.SCENE_TAUTI_OPENING);

			ThreadPoolManager.getInstance().schedule(new RunnableImpl(){
				@Override
				public void runImpl() throws Exception
				{
					_tauti = addSpawnWithoutRespawn(_isExtremeMode ? TAUTI_EXTREME : TAUTI_NORMAL, TAUTI_SPAWN, 0);
					_tauti.addListener(_currentHpListener);
				}
			}, 42000L);
		}
	}

	private class DeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature self, Creature killer)
		{
			if(self.isNpc() && (self.getNpcId() == TAUTI_NORMAL_AXE || self.getNpcId() == TAUTI_EXTREME_AXE))
				ThreadPoolManager.getInstance().schedule(new TautiDeath(), 1000L);
		}
	}

	private class TautiDeath extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for(Player player : getPlayers())
			{
				player.startScenePlayer(SceneMovie.SCENE_TAUTI_ENDING);
				WorldStatisticsManager.getInstance().updateStat(player, CategoryType.RESURRECTED_CHAR_COUNT, 1);
			}
			clearReflection(5, true);
		}
	}

	private class SayChatTask extends RunnableImpl
	{
		private NpcString _msg;
		private int _npcId;

		public SayChatTask(NpcString msg, int npcId)
		{
			_msg = msg;
			_npcId = npcId;
		}

		@Override
		public void runImpl() throws Exception
		{
			List<NpcInstance> npc = getAllByNpcId(_npcId, true);
			if(!npc.isEmpty())
				Functions.npcSay(npc.get(0), _msg);

			if(!_sayLocked && !npc.isEmpty())
				ThreadPoolManager.getInstance().schedule(this, Rnd.get(SAY_TIMER));
		}
	}

	public class CurrentHpListener implements OnCurrentHpDamageListener
	{
		@Override
		public void onCurrentHpDamage(final Creature actor, final double damage, final Creature attacker, Skill skill)
		{
			if(actor.getNpcId() == TAUTI_NORMAL || actor.getNpcId() == TAUTI_EXTREME)
			{
				double HpPercent = actor.getCurrentHpPercents();
				if(HpPercent <= 50. && !_hpListenerLocked)
				{
					_hpListenerLocked = true;

					for(Player player : getPlayers())
						player.sendPacket(new ExShowScreenMessage(NpcString.JAHAK_IS_INFUSING_ITS_PETRA_TO_TAUTI, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, ExShowScreenMessage.STRING_TYPE, 0, true, 0));

					ThreadPoolManager.getInstance().schedule(new RunnableImpl(){
						@Override
						public void runImpl() throws Exception
						{
							addSpawnWithoutRespawn(JAHAK, _tauti.getLoc(), 100);
							addSpawnWithoutRespawn(JAHAK, _tauti.getLoc(), 100);

							for(Player player : getPlayers())
								player.sendPacket(new ExShowScreenMessage(NpcString.LORD_TAUTI_REVEIVE_MY_PETRA_AND_BE_STRENGTHENED_THEN_DEFEAT_THESE_FEEBLE_WRETCHES, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, ExShowScreenMessage.STRING_TYPE, 0, true, 0));

							for(NpcInstance npc : getAllByNpcId(JAHAK, true))
							{
								Skill skill = SkillTable.getInstance().getInfo(14625, 1);
								npc.doCast(skill, actor, false);
								npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 300);
							}
						}
					}, 5000L);
				}
				if(!_done)
				{
					if(HpPercent <= 15.)
					{
						_done = true;
						for(Player player : getPlayers())
							player.startScenePlayer(SceneMovie.SCENE_TAUTI_PHASE);

						_tauti.teleToLocation(-149244, 209882, -10199, _tauti.getReflection());

						ThreadPoolManager.getInstance().schedule(new RunnableImpl(){
							@Override
							public void runImpl() throws Exception
							{
								_tautiAxe = addSpawnWithoutRespawn(_isExtremeMode ? TAUTI_EXTREME_AXE : TAUTI_NORMAL_AXE, TAUTI_SPAWN, 0);
								_tautiAxe.addListener(_deathListener);
								_tautiAxe.setCurrentHp(_tauti.getCurrentHp(), false);
								_tauti.removeListener(_currentHpListener);
								_tauti.deleteMe();
							}
						}, 24000L);
					}
				}	
			}
		}
	}

	public int getInstanceStage()
	{
		return _stage;
	}
}