package instances;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.components.SceneMovie;
import l2s.gameserver.network.l2.s2c.ExSendUIEventPacket;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExStartScenePlayer;
import l2s.gameserver.tables.SkillTable;

/**
 * @author Camelion
 * @modified KilRoy
 */
public class HarnakUndergroundRuins extends Reflection
{
	// Door's
	private static final int DOOR1_ID = 16240100;
	private static final int DOOR2_ID = 16240102;

	// Zone's
	private static final String ZONE_1 = "[harnak_underground_4pf_1]";
	private static final String ZONE_2 = "[harnak_underground_4pf_2]";

	// NPC's
	private static final int RAKZAN_ID = 27440;

	// Skill's
	private static final int DEFENSE_SKILL_ID = 14700;

	// Spawn Group's
	private static final String FIRST_ROOM_FIRST_GROUP = "harnak_u_1r_1";
	private static final String FIRST_ROOM_SECOND_GROUP = "harnak_u_1r_2";
	private static final String SECOND_ROOM_FIRST_GROUP = "harnak_u_2r_1";
	private static final String SECOND_ROOM_SOURCE_POWER = "harnak_u_2r_3_sp";
	private static final String THIRD_ROOM_GROUP = "harnak_u_3r";
	private static final String THIRD_ROOM_SEALS = "harnak_u_3r_seal";
	private static final String THIRD_ROOM_MINIONS = "harnak_u_3r_minion";
	private static final String HERMUNKUS_GROUP = "harnak_u_hermunkus";

	private boolean _introShowed = false;
	private volatile int _1stRoomMobsCount = 8;
	private int _secondRoomGroup = 0;
	private int _classId = -1;

	private ScheduledFuture<?> _failTask;

	private int _state;

	public HarnakUndergroundRuins(int state)
	{
		_state = state;
	}

	@Override
	protected void onCreate()
	{
		super.onCreate();
	}

	@Override
	public void onPlayerEnter(Player player)
	{
		super.onPlayerEnter(player);
		if(_state == 1) // Начало инстанса
		{
			if(!_introShowed)
			{
				ThreadPoolManager.getInstance().schedule(new ScreenMessageTask(NpcString.AN_INTRUDER_INTERESTING), 2500);
				ThreadPoolManager.getInstance().schedule(new ScreenMessageTask(NpcString.PROVE_YOUR_WORTH), 5000);
				ThreadPoolManager.getInstance().schedule(new ScreenMessageTask(NpcString.PROVE_YOUR_WORTH), 7500);
				ThreadPoolManager.getInstance().schedule(new ScreenMessageTask(NpcString.ONLY_THOSE_STRONG_ENOUGH_SHALL_PROCEED), 8500);
				ThreadPoolManager.getInstance().schedule(new SpawnNpcTask(), 7500);
				for(ClassId classId : ClassId.VALUES)
				{
					if(classId.isOutdated() && classId.isOfLevel(ClassLevel.AWAKED) && classId.childOf(player.getClassId()))
					{
						_classId = classId.getId();
						break;
					}
				}
				_introShowed = true;
			}
		}
		else if(_state == 2) // Спаун только Гермункуса
		{
			spawnByGroup(HERMUNKUS_GROUP);
			openDoor(DOOR1_ID);
			openDoor(DOOR2_ID);
		}
	}

	public void decreaseFirstRoomMobsCount()
	{
		if(--_1stRoomMobsCount == 0)
		{
			openDoor(DOOR1_ID);
			spawnByGroup(FIRST_ROOM_SECOND_GROUP);
			Zone z = getZone(ZONE_1);
			if(z != null)
			{
				z.setActive(true);
				z.addListener(new ZoneListener(1));
			}
		}
	}

	public void increaseSecondRoomGroup()
	{
		_secondRoomGroup++;
		if(_secondRoomGroup == 2)
		{
			ThreadPoolManager.getInstance().schedule(new ScreenMessageTask(NpcString.I_MUST_GO_HELP_SOME_MORE), 100);
			Skill skill = SkillTable.getInstance().getInfo(DEFENSE_SKILL_ID, 1);
			for(Player player : getPlayers())
				skill.getEffects(player, player, false, false);
			spawnByGroup(SECOND_ROOM_SOURCE_POWER);
		}
		else if(_secondRoomGroup == 4)
		{
			openDoor(DOOR2_ID);
			Zone z = getZone(ZONE_2);
			if(z != null)
			{
				z.setActive(true);
				z.addListener(new ZoneListener(2));
			}
		}
	}

	public void startLastStage()
	{
		for(Player player : getPlayers())
		{
			player.sendPacket(new ExSendUIEventPacket(player, 0, 0, 60, 0, NpcString.REMAINING_TIME));
			player.sendPacket(new ExShowScreenMessage(NpcString.NO_THE_SEAL_CONTROLS_HAVE_BEEN_EXPOSED_GUARDS_PROTECT_THE_SEAL_CONTROLS, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, ExShowScreenMessage.STRING_TYPE, 0, false, 0));
		}
		ThreadPoolManager.getInstance().schedule(new SpawnNpcByPlayerClass(THIRD_ROOM_MINIONS), 1);
		_failTask = ThreadPoolManager.getInstance().schedule(new FailTask(), 60000);
		spawnByGroup(THIRD_ROOM_SEALS);
	}

	public void successEndInstance()
	{
		if(_failTask != null)
			_failTask.cancel(true);
		despawnByGroup(THIRD_ROOM_SEALS);
		despawnByGroup(THIRD_ROOM_GROUP);
		despawnByGroup(THIRD_ROOM_MINIONS + "_" + _classId);
		despawnByGroup(HERMUNKUS_GROUP);

		for(Player p : getPlayers())
			p.startScenePlayer(SceneMovie.SCENE_AWAKENING_BOSS_ENDING_A);

		ThreadPoolManager.getInstance().schedule(new Runnable(){
			@Override
			public void run()
			{
				spawnByGroup(HERMUNKUS_GROUP);
			}
		}, 25050L);
	}

	private class ScreenMessageTask extends RunnableImpl
	{
		private NpcString msg;

		public ScreenMessageTask(NpcString msg)
		{
			this.msg = msg;
		}

		@Override
		public void runImpl() throws Exception
		{
			for(Player player : getPlayers())
				player.sendPacket(new ExShowScreenMessage(msg, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, ExShowScreenMessage.STRING_TYPE, 0, true, 0));
		}
	}

	private class SpawnNpcTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			spawnByGroup(FIRST_ROOM_FIRST_GROUP);
			ThreadPoolManager.getInstance().schedule(new Runnable(){
				@Override
				public void run()
				{
					List<NpcInstance> npcs = getAllByNpcId(RAKZAN_ID, true);
					if(!npcs.isEmpty())
						npcs.get(0).getAI().notifyEvent(CtrlEvent.EVT_SCRIPT_EVENT, "SELECT_ME", "empty", "empty");
				}
			}, 3000);

		}
	}

	private class SpawnNpcByPlayerClass extends RunnableImpl
	{
		private String group;

		public SpawnNpcByPlayerClass(String group)
		{
			this.group = group;
		}

		@Override
		public void runImpl() throws Exception
		{
			spawnByGroup(group + "_" + _classId);
		}
	}

	private class ZoneListener implements OnZoneEnterLeaveListener
	{
		private int state;

		public ZoneListener(int state)
		{
			this.state = state;
		}

		@Override
		public void onZoneEnter(Zone zone, Creature actor)
		{
			if(actor.isPlayer())
			{
				if(state == 1)
				{
					ThreadPoolManager.getInstance().schedule(new ScreenMessageTask(NpcString.PROVE_YOUR_WORTH), 100);
					ThreadPoolManager.getInstance().schedule(new ScreenMessageTask(NpcString.ARE_YOU_STRONG_OR_WEAK_OF_THE_LIGHT_OR_DARKNESS), 2600);
					ThreadPoolManager.getInstance().schedule(new ScreenMessageTask(NpcString.ONLY_THOSE_OF_LIGHT_MAY_PASS_OTHERS_MUST_PROVE_THEIR_STRENGTH), 5100);
					ThreadPoolManager.getInstance().schedule(new SpawnNpcByPlayerClass(SECOND_ROOM_FIRST_GROUP), 6900);
				}
				else if(state == 2)
				{
					ThreadPoolManager.getInstance().schedule(new SpawnThirdRoom(), 28000);
					for(Player p : getPlayers())
						p.startScenePlayer(SceneMovie.SCENE_AWAKENING_BOSS_OPENING);
				}
			}
			zone.setActive(false);
			zone.removeListener(this);
		}

		@Override
		public void onZoneLeave(Zone zone, Creature actor)
		{
			//
		}
	}

	private class SpawnThirdRoom extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			spawnByGroup(THIRD_ROOM_GROUP);
		}
	}

	private class FailTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for(NpcInstance npc : getNpcs())
				npc.getAI().notifyEvent(CtrlEvent.EVT_SCRIPT_EVENT, "FAIL_INSTANCE", "empty", "empty");

			for(Player p : getPlayers())
				p.startScenePlayer(SceneMovie.SCENE_AWAKENING_BOSS_ENDING_B);

			ThreadPoolManager.getInstance().schedule(new EndTask(), 13500);
		}
	}

	private class EndTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for(Player p : getPlayers())
				p.teleToLocation(getReturnLoc(), ReflectionManager.DEFAULT);
		}
	}
}
