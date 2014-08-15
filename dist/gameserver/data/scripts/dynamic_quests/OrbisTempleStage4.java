package dynamic_quests;

import l2s.gameserver.instancemanager.SpawnManager;
import l2s.gameserver.listener.actor.OnKillListener;
import l2s.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.quest.dynamic.DynamicQuest;
import l2s.gameserver.network.l2.s2c.ExDynamicQuestPacket;
import l2s.gameserver.network.l2.s2c.ExDynamicQuestPacket.DynamicQuestInfo;
import l2s.gameserver.scripts.ScriptFile;
import l2s.gameserver.utils.ReflectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Awakeninger
 * Проблема: таких квестов должно быть 6
 * Рекомендую почитать содержание DynamicQuest. Там расписаны все методы более подробно
 * Запускается квест, длинной полчаса, по истечению времени, или досрочному завершенею
 * начинается пятиминутный перерыв, в течении которого мобы деспаунятся(onStop - перед пятью минутами, onFinish - по истечению этих пяти минут)
 * После истечения пяти минут, запускается второй этап квеста в котором меняются KILL_OTOH_MOB на 202 и т.д вплоть до 206
 * и MAX_TASK_POINT на 60k,90k,42k,60k,90k. После 6го этапа начинается заного 1й этап.
 * и параметры вновь меняются.
 * TODO.
 */
public class OrbisTempleStage4 extends DynamicQuest implements ScriptFile
{
	private static final Logger _log = LoggerFactory.getLogger(OrbisTempleStage4.class);

	private static final int QUEST_ID = 2;
	private static final int MIN_LEVEL = 95;
	private static final int MAX_LEVEL = 99;
	private static final String SPAWN_GROUP = "Orbis";

	private static final int DURATION = 30 * 1000; //WTF?!
	private static final String START_TIME1 = "45 1 * * *";
	private static final String START_TIME2 = "15 5 * * *";
	private static final String START_TIME3 = "45 8 * * *";
	private static final String START_TIME4 = "15 12 * * *";
	private static final String START_TIME5 = "45 15 * * *";
	private static final String START_TIME6 = "15 19 * * *";
	private static final String START_TIME7 = "45 22 * * *";
	//private static final int START_TIME = 11_115;
	private static final int REWARD = 32604;
	private static final int ELITE_REWARD = 32605;
	private static final int KILL_OTOH_MOB = 204; // Клиентский параметр
	private static final int MAX_TASK_POINT = 42000;
	private static final String QUEST_ZONE_FIRST_SECOND = "[orbis_temple_1_2]";
	private static final String QUEST_ZONE_THIRD = "[orbis_temple_3]";
	private ZoneListener _zoneListener;
	private Zone zoneFirstSecond;
	private Zone zoneThird;
	private static final long delayS = 1 * 60 * 1000L;
	private static final long delayB = 5 * 60 * 1000L;
	private final KillListenerImpl _killListener = new KillListenerImpl();
	private static final int ORBIS1 = 22911; // 
	private static final int ORBIS2 = 22912; // 
	private static final int ORBIS3 = 22913; // 
	private static final int ORBIS4 = 22914; // 
	private static final int ORBIS5 = 22915; // 
	private static final int ORBIS6 = 22916; // 
	private static final int ORBIS7 = 22917; // 
	private static final int ORBIS8 = 22918; // 
	private static final int ORBIS9 = 22919; // 
	private static final int ORBIS10 = 22920; // 
	private static final int ORBIS11 = 25833; // 
	private static final int ORBIS12 = 18979; // 
	private static final int ORBIS13 = 25834; // 
	private static final int ORBIS14 = 18980; // 
	private static final int ORBIS15 = 25835; // 
	private static final int ORBIS16 = 22921; // 
	private static final int ORBIS17 = 22922; // 
	private static final int ORBIS18 = 22923; // 
	private static final int ORBIS19 = 22924; // 
	private static final int ORBIS20 = 22925; // 
	private static final int ORBIS21 = 22926; // 
	private static final int ORBIS22 = 22927; //
	private static final int[] LOH_MOBS = {
		22911,
		22912,
		22913,
		22914,
		22915,
		22916,
		22917,
		22918,
		22919,
		22920,
		25833,
		18979,
		25834,
		18980,
		25835
	};

	/**
	 * Конструктор класса DynamicQuest являющийся суперклассом
	 *
	 * @param questId  - идентификатор квеста
	 * @param duration - продолжительность квеста (в секундах)
	 */
	public OrbisTempleStage4()
	{
		super(QUEST_ID, DURATION);
		addTask(KILL_OTOH_MOB, MAX_TASK_POINT, TASK_INCREASE_MODE_NO_LIMIT);
		addReward(REWARD, 1);
		addEliteReward(ELITE_REWARD, 1, 3);
		addLevelCheck(MIN_LEVEL, MAX_LEVEL);
		addZoneCheck(QUEST_ZONE_FIRST_SECOND);
		addZoneCheck(QUEST_ZONE_THIRD);
		initSchedulingPattern(START_TIME1);
		initSchedulingPattern(START_TIME2);
		initSchedulingPattern(START_TIME3);
		initSchedulingPattern(START_TIME4);
		initSchedulingPattern(START_TIME5);
		initSchedulingPattern(START_TIME6);
		initSchedulingPattern(START_TIME7);
	}

	@Override
	protected boolean isZoneQuest()
	{
		return true;
	}

	@Override
	public void onLoad()
	{
		_zoneListener = new ZoneListener();
		zoneFirstSecond = ReflectionUtils.getZone(QUEST_ZONE_FIRST_SECOND);
		zoneFirstSecond.addListener(_zoneListener);
		zoneThird = ReflectionUtils.getZone(QUEST_ZONE_THIRD);
		zoneThird.addListener(_zoneListener);
		_log.info("Dynamic Quest: Loaded quest ID " + QUEST_ID + ". Name: OrbisTemple - Zone Quest");
		//OrbisManager.setCurrentStage(1);

	}

	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{
	}

	/**
	 * Вызывается после инициализации квеста (спауны, задачи, отсчет)
	 */
	@Override
	protected void onStart()
	{
		SpawnManager.getInstance().spawn(SPAWN_GROUP);
	}

	/**
	 * Вызывается после завершения квеста (деспаун, статистика),
	 * но перед 5 минутным отсчетом до полного завершения квеста
	 * success - флаг завершения квеста.
	 * Если квест завершился по истечению времени, и задачи не выполнены - success = false
	 * Если квеста завершился по выполнению всех задач, success = true
	 *
	 * @param success - флаг с которым завершился квест
	 */
	@Override
	protected void onStop(boolean success)
	{
		SpawnManager.getInstance().despawn(SPAWN_GROUP);
		for(int objectId : getParticipants())
		{
			Player player = GameObjectsStorage.getPlayer(objectId);
			if(player != null)
			{
				removeParticipant(player);
			}
		}
	}

	/**
	 * Вызывается при полной остановке квеста, (после 5 минутного отсчета)
	 * <p/>
	 * Здесь удобно очищать переменные, устанавливать параметрам значения по умолчанию,
	 * производить подготовку к новому запуску квеста
	 */
	@Override
	protected void onFinish()
	{

	}

	@Override
	protected String onRequestHtml(Player player, boolean participant)
	{
		if(getCurrentStep() == 1)
		{
			if(isStarted())
			{
				if(!participant)
				{
					return "dc0002_01_start001.htm";
				}
				else
				{
					return "dc0002_01_context001.htm";
				}
			}
			else if(isSuccessed())
			{
				boolean rewardReceived = rewardReceived(player);
				if(rewardReceived)
				{
					return null;
				}
				else
				{
					return "dc0002_01_reward001.htm";
				}
			}
			else
			{
				return "dc0002_01_failed001.htm";
			}
		}
		return null;
	}

	/**
	 * Вызывается при входе игрока в мир (только если квест не завершен окончательно)
	 *
	 * @param player - вошедший игрок
	 */
	@Override
	protected boolean onPlayerEnter(Player player)
	{
		if(player.isInZone(zoneFirstSecond) || player.isInZone(zoneThird))
		{
			return true;
		}
		return false;
	}

	/**
	 * Вызывается при завершении одной из задач
	 *
	 * @param taskId - завершенная задача
	 */
	@Override
	protected void onTaskCompleted(int taskId)
	{
		onStop(true);
		/**ThreadPoolManager.getInstance().schedule(new Runnable()
		 {
		 @Override public void run()
		 {
		 onStart();
		 }
		 },delayS);
		 ThreadPoolManager.getInstance().schedule(new Runnable()
		 {
		 @Override public void run()
		 {
		 SpawnManager.getInstance().despawn(SPAWN_GROUP);
		 if(Orbis.getCurrentStage() == 1)
		 {
		 OrbisManager.setCurrentStage(2);
		 //MAX_TASK_POINT = 60000;
		 //KILL_OTOH_MOB = 202;
		 }else if(Orbis.getCurrentStage() == 2)
		 {
		 OrbisManager.setCurrentStage(3);
		 }else if(Orbis.getCurrentStage() == 3)
		 {
		 OrbisManager.setCurrentStage(4);
		 }else if(Orbis.getCurrentStage() == 4)
		 {
		 OrbisManager.setCurrentStage(5);
		 }else if(Orbis.getCurrentStage() == 5)
		 {
		 OrbisManager.setCurrentStage(6);
		 }else if(Orbis.getCurrentStage() == 6)
		 {
		 OrbisManager.setCurrentStage(1);
		 }
		 _log.info("WaitState");
		 ThreadPoolManager.getInstance().schedule(new Runnable() //пятиминутный передых
		 {
		 @Override public void run()
		 {
		 _log.info("NextState");
		 SpawnManager.getInstance().spawn(SPAWN_GROUP);
		 }

		 },delayB);



		 }
		 },delayS); //полчаса каждая стадия, но ставим 35 т.к. учитываем 5 минут передыха
		 }*/
	}

	@Override
	protected String onDialogEvent(String event, Player player)
	{
		String response = null;
		if(event.equals("Reward"))
		{
			tryReward(player);
			response = null;
		}
		else if(event.endsWith(".htm"))
		{
			response = event;
		}
		return response;
	}

	@Override
	protected void onAddParticipant(Player player)
	{
		player.getListeners().add(_killListener);
	}

	@Override
	protected void onRemoveParticipant(Player player)
	{
		player.getListeners().remove(_killListener);
	}

	@Override
	protected boolean onStartCondition()
	{
		return true;
	}

	private final class ZoneListener implements OnZoneEnterLeaveListener
	{
		@Override
		public void onZoneEnter(Zone zone, Creature character)
		{
			if(zone == null)
			{
				return;
			}

			if(!character.isPlayer())
			{
				return;
			}

			Player player = character.getPlayer();
			if(isStarted() && !isSuccessed())
			{
				if(!getParticipants().contains(player.getObjectId()))
				{
					addParticipant(player);
				}
				else
				{
					sendQuestInfoParticipant(player);
				}
			}
		}

		@Override
		public void onZoneLeave(Zone zone, Creature character)
		{
			if(!character.isPlayer())
			{
				return;
			}

			Player player = character.getPlayer();
			if(isStarted() && !isSuccessed())
			{
				if(getParticipants().contains(player.getObjectId()))
				{
					DynamicQuestInfo questInfo = new DynamicQuestInfo(1);
					questInfo.questType = isZoneQuest() ? 1 : 0;
					questInfo.questId = getQuestId();
					questInfo.step = getCurrentStep();
					player.sendPacket(new ExDynamicQuestPacket(questInfo));
				}
			}
		}
	}

	/**
	 * Увеличивает очки выполнения задачи taskId на значение points
	 *
	 * @param taskId - Идентификатор задачи
	 * @param player - Игрок, в чей зачет пойдут очки выполнения
	 * @param points - Кол-во очков
	 */
	private final class KillListenerImpl implements OnKillListener
	{
		@Override
		public void onKill(Creature actor, Creature victim)
		{
			if(victim.isPlayer())
			{
				return;
			}

			if(!actor.isPlayer())
			{
				return;
			}

			if(victim.isNpc() && isStarted() && ArrayUtils.contains(LOH_MOBS, victim.getNpcId()))
			{
				switch(victim.getNpcId())
				{
					case ORBIS4:
						increaseTaskPoint(KILL_OTOH_MOB, actor.getPlayer(), 1);
						break;
					case ORBIS5:
						increaseTaskPoint(KILL_OTOH_MOB, actor.getPlayer(), 1);
						break;
					case ORBIS6:
						increaseTaskPoint(KILL_OTOH_MOB, actor.getPlayer(), 1);
						break;
					case ORBIS7:
						increaseTaskPoint(KILL_OTOH_MOB, actor.getPlayer(), 1);
						break;
					case ORBIS8:
						increaseTaskPoint(KILL_OTOH_MOB, actor.getPlayer(), 1);
						break;
					case ORBIS9:
						increaseTaskPoint(KILL_OTOH_MOB, actor.getPlayer(), 1);
						break;
					case ORBIS16:
						increaseTaskPoint(KILL_OTOH_MOB, actor.getPlayer(), 3);
						break;
					case ORBIS17:
						increaseTaskPoint(KILL_OTOH_MOB, actor.getPlayer(), 3);
						break;
					case ORBIS18:
						increaseTaskPoint(KILL_OTOH_MOB, actor.getPlayer(), 3);
						break;
				}
			}
		}

		@Override
		public boolean ignorePetOrSummon()
		{
			return true;
		}
	}
}