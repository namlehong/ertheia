package l2s.gameserver.model.quest.dynamic;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.time.cron.SchedulingPattern;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.instancemanager.SpawnManager;
import l2s.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.network.l2.s2c.ExDynamicQuestPacket;
import l2s.gameserver.network.l2.s2c.NpcHtmlMessagePacket;
import l2s.gameserver.utils.ItemFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static l2s.gameserver.network.l2.s2c.ExDynamicQuestPacket.DynamicQuestInfo;
import static l2s.gameserver.network.l2.s2c.ExDynamicQuestPacket.ScoreBoardInfo;
import static l2s.gameserver.network.l2.s2c.ExDynamicQuestPacket.StartedQuest;

/**
 * @author Дмитрий
 * @modified KilRoy
 * @date 27.10.12  23:48
 */
public abstract class DynamicQuest
{
	protected static final int TASK_INCREASE_MODE_NO_LIMIT = 0;
	protected static final int TASK_INCREASE_MODE_ONCE_PER_CHAR = 1;
	private static final Logger log = LoggerFactory.getLogger(DynamicQuest.class);
	//private static final int QUEST_FINALIZE_TIME = 300_000;// это 5 минут. 5*60*1000L
	private static final long QUEST_FINALIZE_TIME = 5 * 60 * 1000L;
	private static final int QUEST_STARTED = 0;
	private static final int QUEST_ENDED = 1;
	private static final int QUEST_STATE_PROGRESS = 0;
	private static final int QUEST_STATE_RECEIVE_REWARD = 1;
	private static final int QUEST_STATE_CHECK_RESULT = 2;
	private static final int QUEST_STATE_CAMPAIGN_FAILED = 3;
	private final int questId;
	private final int duration;
	private final ReentrantLock lock = new ReentrantLock();
	private final Map<Integer, DynamicQuestParticipant> participants;
	private final Map<Integer, DynamicQuestTask> tasks;
	private final List<String> spawnGroups;
	private final List<DynamicQuestReward> reward;
	private final List<DynamicQuestReward> eliteReward;
	private final Map<DynamicQuestParticipant, List<DynamicQuestReward>> rewardReceiver;
	private List<ICheckStartCondition> startStartCondition = new ArrayList<ICheckStartCondition>();
	private int currentStep;
	private boolean started;
	private ScheduledFuture<?> endTask;
	private OnPlayerEnterListener playerEnterListener;
	private boolean successed;
	private boolean startCondition;

	/**
	 * Конструктор класса DynamicQuest
	 *
	 * @param questId  - идентификатор квеста
	 * @param duration - продолжительность квеста (в секундах)
	 */
	public DynamicQuest(int questId, int duration)
	{
		this.questId = questId;
		this.duration = duration;
		participants = new HashMap<Integer, DynamicQuestParticipant>();
		playerEnterListener = new OnPlayerEnterListenerImpl();
		tasks = new HashMap<Integer, DynamicQuestTask>();
		spawnGroups = new ArrayList<String>();
		reward = new ArrayList<DynamicQuestReward>();
		eliteReward = new ArrayList<DynamicQuestReward>();
		rewardReceiver = new HashMap<DynamicQuestParticipant, List<DynamicQuestReward>>();
		DynamicQuestController.getInstance().registerDynamicQuest(this);
	}

	public final int getQuestId()
	{
		return questId;
	}

	public final int getCurrentStep()
	{
		return currentStep;
	}

	void setCurrentStep(int currentStep)
	{
		this.currentStep = currentStep;
	}

	void start(RunnableImpl finisher)
	{
		lock.lock();
		started = true;
		// Размещение всех NPC при начале квеста
		for(String group : spawnGroups)
		{
			SpawnManager.getInstance().spawn(group);
		}
		CharListenerList.addGlobal(playerEnterListener);
		for(Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			if(isAvailableFor(player))
			{
				sendQuestInfo(player);
			}
		}
		lock.unlock();
		onStart();
		endTask = ThreadPoolManager.getInstance().schedule(finisher, getDuration() * 1000);
	}

	void stop(boolean success, RunnableImpl finalizer)
	{
		if(endTask != null)
		{
			endTask.cancel(false);
		}
		endTask = ThreadPoolManager.getInstance().schedule(finalizer, QUEST_FINALIZE_TIME);
		lock.lock();
		if(started)
		{
			onStop(success);
		}
		started = false;
		successed = success;
		// Удаление всех NPC при завершении квеста
		for(String group : spawnGroups)
		{
			SpawnManager.getInstance().despawn(group);
		}
		if(success)
		{ // Подсчет победителей, если квест завершился успешно
			List<DynamicQuestParticipant> ps = new ArrayList<DynamicQuestParticipant>(participants.values());
			//Collections.sort(ps);
			for(int i = 0; i < ps.size(); i++)
			{
				//List<DynamicQuestReward> rs = new ArrayList<>();
				List<DynamicQuestReward> rs = new ArrayList<DynamicQuestReward>();
				for(DynamicQuestReward elite : eliteReward)
				{
					if(i < elite.firstPlayersCount)
					{
						rs.add(elite);
					}
				}
				rs.addAll(reward);
				rewardReceiver.put(ps.get(i), rs);
			}
		}
		lock.unlock();
	}

	void finish()
	{
		lock.lock();
		if(!started)
		{
			// Очистка переменных
			CharListenerList.removeGlobal(playerEnterListener);
			setCurrentStep(0);
			participants.clear();
			rewardReceiver.clear();
			for(DynamicQuestTask task : tasks.values())
			{
				task.clear();
			}
			successed = false;
			endTask = null;
			onFinish();
		}
		lock.unlock();
	}

	public final int getDuration()
	{
		return duration;
	}

	public final Collection<Integer> getParticipants()
	{
		return Collections.unmodifiableCollection(participants.keySet());
	}

	protected final void addTask(int taskId, int maxCount, int addMode)
	{
		tasks.put(taskId, new DynamicQuestTask(taskId, questId, maxCount, addMode));
	}

	protected final void addSpawns(String... spawns)
	{
		Collections.addAll(spawnGroups, spawns);
	}

	/**
	 * Должен вызываться в конструкторе квестов-кампаний,
	 * иначе квест не будет автоматически запускаться по заданному времени
	 *
	 * @param pattern - cron-шаблон запуска
	 */
	protected final void initSchedulingPattern(String pattern)
	{
		StartConditionInit();
		DynamicQuestController.getInstance().initSchedulingPattern(getQuestId(), new SchedulingPattern(pattern));
	}

	/**
	 * Увеличивает очки выполнения задачи taskId на значение points
	 *
	 * @param taskId - Идентификатор задачи
	 * @param player - Игрок, в чей зачет пойдут очки выполнения
	 * @param points - Кол-во очков
	 */
	protected final void increaseTaskPoint(int taskId, Player player, int points)
	{
		if(participants.containsKey(player.getObjectId()))
		{
			if(tasks.containsKey(taskId))
			{
				tasks.get(taskId).increasePoints(participants.get(player.getObjectId()), points);
			}
			else
			{
				log.warn("DynamicQuest#increaseTaskPoint(int, Player, int): Unknown task with id: " + taskId + " questId: " + questId);
			}
		}
	}

	/**
	 * Добавляет участника к выполнению квеста
	 *
	 * @param player - добавляемый игрок
	 */
	protected final void addParticipant(Player player)
	{
		lock.lock();
		if(started)
		{
			participants.put(player.getObjectId(), new DynamicQuestParticipant(player.getName()));
			onAddParticipant(player);
			sendQuestInfo(player);
		}
		lock.unlock();
	}

	/**
	 * Удаляет участника с выполнения квеста
	 *
	 * @param player - удаляемый игрок
	 */
	protected final void removeParticipant(Player player)
	{
		lock.lock();
		if(started)
		{
			participants.remove(player.getName());
			sendQuestInfo(player);
			onRemoveParticipant(player);
		}
		lock.unlock();
	}

	/**
	 * Отправляет информацию игроку, о текущем состоянии квеста (только локационные)
	 *
	 * @param player  - игрок
	 */
	protected final void sendQuestInfoParticipant(Player player)
	{
		sendQuestInfo(player);
	}

	/**
	 * Добавляет в список требований минимальный/максимальный уровень для участия в квесте
	 *
	 * @param itemId - id предмета
	 * @param count  - количество
	 */
	protected final void addLevelCheck(int minLvl, int maxLvl)
	{
		startStartCondition.add(new PlayerCheckLevel(minLvl, maxLvl));
	}

	/**
	 * Добавляет в список требований проверку зоны, для выдачи квест инфы персонажу
	 *
	 * @param itemId - id предмета
	 * @param count  - количество
	 */
	protected final void addZoneCheck(String... zoneName)
	{
		startStartCondition.add(new PlayerCheckZone(zoneName));
	}

	/**
	 * Метод проверки доступности квеста для игрока, по его уровню
	 *
	 * @param player - проверяемый игрок
	 */
	public final boolean isAvailableFor(Player player)
	{
		for(ICheckStartCondition startCondition : startStartCondition)
		{
			if(!startCondition.checkCondition(player))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Добавляет награду в список наград
	 *
	 * @param itemId - id предмета
	 * @param count  - количество
	 */
	protected final void addReward(int itemId, long count)
	{
		reward.add(new DynamicQuestReward(itemId, count, 0));
	}

	/**
	 * Добавляет награду в список элитных наград
	 *
	 * @param itemId            - id предмета
	 * @param count             - количество
	 * @param firstPlayersCount - кол-во игроков, "топа", которые получат элитную награду
	 */
	protected final void addEliteReward(int itemId, long count, int firstPlayersCount)
	{
		eliteReward.add(new DynamicQuestReward(itemId, count, firstPlayersCount));
	}

	/**
	 * Попытка выдать награду игроку (Выдается только если игрок есть в списке участников, игрок не получал награду ранее, итд)
	 *
	 * @param player - игрок, для которого выдается награда
	 */
	protected final void tryReward(Player player)
	{
		lock.lock();
		if(participants.containsKey(player.getObjectId()))
		{
			DynamicQuestParticipant participant = participants.get(player.getObjectId());
			if(rewardReceiver.containsKey(participant))
			{
				List<DynamicQuestReward> rewardList = rewardReceiver.get(participant);
				for(DynamicQuestReward reward : rewardList)
				{
					ItemFunctions.addItem(player, reward.itemId, reward.count, true);
				}
				rewardReceiver.remove(participant);
			}
		}
		lock.unlock();
	}

	/**
	 * Возвращает true, если игрок уже получил награду. В противном случае возвращает false
	 *
	 * @param player - игрок, для которого выполняется проверка
	 * @return - true - игрок получил награду ранее, false - игрок не получал награды
	 */
	protected final boolean rewardReceived(Player player)
	{
		lock.lock();
		boolean response = true;
		if(participants.containsKey(player.getObjectId()))
		{
			DynamicQuestParticipant participant = participants.get(player.getObjectId());
			if(rewardReceiver.containsKey(participant))
			{
				response = false;
			}
		}
		lock.unlock();
		return response;
	}

	void requestHtml(int step, Player player)
	{
		lock.lock();
		if(currentStep == step)
		{
			String response = onRequestHtml(player, participants.containsKey(player.getObjectId()));
			if(response != null)
			{
				NpcHtmlMessagePacket packet = new NpcHtmlMessagePacket(5);
				packet.setFile("dynamic_quests/" + getClass().getSimpleName() + "/" + response);
				player.sendPacket(packet);
			}
		}
		lock.unlock();
	}

	void requestProgressInfo(int step, Player player)
	{
		sendQuestInfo(player);
	}

	void requestScoreBoard(int step, Player player)
	{
		sendScoreBoard(player);
	}

	void playerEnter(Player player)
	{
		lock.lock();
		if(isAvailableFor(player))
		{
			boolean enterRequest = onPlayerEnter(player);

			if(enterRequest == true)
			{
				sendQuestInfo(player);
			}
		}
		lock.unlock();
	}

	void taskCompleted(int taskId)
	{
		onTaskCompleted(taskId);
		lock.lock();
		int completedTasks = 0;
		for(DynamicQuestTask task : tasks.values())
		{
			if(task.isCompleted())
			{
				completedTasks++;
			}
		}
		if(completedTasks == tasks.size())
		{
			// Выполнены все задачи квеста - успешно завершаем квест
			DynamicQuestController.getInstance().endQuest(questId, true);
		}
		lock.unlock();
	}

	void processDialogEvent(String event, Player player)
	{
		if(event.equals("Score"))
		{
			sendScoreBoard(player);
		}
		else
		{
			String response = onDialogEvent(event, player);
			if(response != null && response.endsWith(".htm"))
			{
				NpcHtmlMessagePacket packet = new NpcHtmlMessagePacket(5);
				packet.setFile("dynamic_quests/" + getClass().getSimpleName() + "/" + response);
				player.sendPacket(packet);
			}
		}
	}

	private void sendQuestInfo(Player player)
	{
		lock.lock();
		DynamicQuestInfo questInfo;
		if(currentStep > 0)
		{// Квест запущен
			if(started)
			{ // Квест в процессе выполнения
				if(participants.containsKey(player.getObjectId()) && endTask != null)
				{ // Игрок является участником Отображаем текущую информацию по квесту
					questInfo = new StartedQuest(QUEST_STATE_PROGRESS, (int) endTask.getDelay(TimeUnit.SECONDS), participants.size(), tasks.values());
				}
				else
				{ // Игрок не является участником, отображается информация о том, что запущен квест
					questInfo = new DynamicQuestInfo(QUEST_STARTED);
				}
			}
			else
			{ // Квест завершился, идет 5 минутный отсчет
				if(participants.containsKey(player.getObjectId()))
				{
					if(successed)
					{ // Квест завершился успешно
						questInfo = new StartedQuest(QUEST_STATE_RECEIVE_REWARD, (int) endTask.getDelay(TimeUnit.SECONDS), participants.size(), tasks.values());
					}
					else
					{ // квест завершился неуспешно
						questInfo = new StartedQuest(QUEST_STATE_CAMPAIGN_FAILED, (int) endTask.getDelay(TimeUnit.SECONDS), participants.size(), tasks.values());
					}
				}
				else
				{
					questInfo = new StartedQuest(QUEST_STATE_CHECK_RESULT, (int) endTask.getDelay(TimeUnit.SECONDS), participants.size(), tasks.values());
				}
			}
		}
		else
		{ // Квест завершен полностью
			questInfo = new DynamicQuestInfo(QUEST_ENDED);
		}
		questInfo.questType = isZoneQuest() ? 1 : 0;
		questInfo.questId = getQuestId();
		questInfo.step = currentStep;
		lock.unlock();
		player.sendPacket(new ExDynamicQuestPacket(questInfo));
	}

	private void sendScoreBoard(Player player)
	{
		if(currentStep > 0)
		{// Квест запущен
			lock.lock();

			List<DynamicQuestParticipant> ps = new ArrayList<DynamicQuestParticipant>(participants.values());
			//Collections.sort(ps);
			DynamicQuestInfo questInfo = new ScoreBoardInfo((int) endTask.getDelay(TimeUnit.SECONDS), 0, ps);

			questInfo.questType = isZoneQuest() ? 1 : 0;
			questInfo.questId = getQuestId();
			questInfo.step = currentStep;
			lock.unlock();
			player.sendPacket(new ExDynamicQuestPacket(questInfo));
		}
	}

	protected void StartConditionInit()
	{
		startCondition = onStartCondition();
	}

	public boolean isStarted()
	{
		return started;
	}

	public boolean isSuccessed()
	{
		return successed;
	}

	public boolean isStartCondition()
	{
		return startCondition;
	}

	protected boolean isZoneQuest()
	{
		return true;
	}

	/**
	 * Вызывается после инициализации квеста (спауны, задачи, отсчет)
	 */
	protected abstract void onStart();

	/**
	 * Вызывается после завершения квеста (деспаун, статистика),
	 * но перед 5 минутным отсчетом до полного завершения квеста
	 * success - флаг завершения квеста.
	 * Если квест завершился по истечению времени, и задачи не выполнены - success = false
	 * Если квеста завершился по выполнению всех задач, success = true
	 *
	 * @param success - флаг с которым завершился квест
	 */
	protected abstract void onStop(boolean success);

	/**
	 * Вызывается при полной остановке квеста, (после 5 минутного отсчета)
	 * <p/>
	 * Здесь удобно очищать переменные, устанавливать параметрам значения по умолчанию,
	 * производить подготовку к новому запуску квеста
	 */
	protected abstract void onFinish();

	// Event controllers

	/**
	 * Вызывается при запросе игроком html файла, соответствующего стадии квеста
	 * Например, после старта квеста всем игрокам предлагается принять в нём участие, итд..
	 *
	 * @param player      - игрок, запросивший html
	 * @param participant - true, если игрок зарегистрировался на квест
	 * @return - имя html файла, хранящегося в папке html/campaigns/QUEST_NAME, либо null если ничего возвращать не нужно
	 */
	protected abstract String onRequestHtml(Player player, boolean participant);

	/**
	 * Вызывается при входе игрока в мир (только если квест не завершен окончательно)
	 *
	 * @param player - вошедший игрок
	 */
	protected abstract boolean onPlayerEnter(Player player);

	/**
	 * Вызывается при завершении одной из задач
	 *
	 * @param taskId - завершенная задача
	 */
	protected abstract void onTaskCompleted(int taskId);

	/**
	 * Вызывается при нажатии игроком какой-либо из кнопок\ссылок в диалоговом окне квеста
	 *
	 * @param event  - строка - событие, переданное из диалога
	 * @param player - игрок, вызвавший событие
	 * @return - htm файл - ответ на событие, либо null если ничего отвечать не нужно
	 */
	protected abstract String onDialogEvent(String event, Player player);

	/**
	 * Вызывается при добавлении нового игрока в список зарегистрированных участников квеста
	 *
	 * @param player - добавленный игрок
	 */
	protected abstract void onAddParticipant(Player player);

	/**
	 * Вызывается при удалении игрока из списка зарегистрированных участников квеста
	 *
	 * @param player - удаляемый игрок игрок
	 */
	protected abstract void onRemoveParticipant(Player player);

	/**
	 * Вызывается на квестах, которые имеют условие старта
	 */
	protected abstract boolean onStartCondition();

	private class DynamicQuestReward
	{
		private int itemId;
		private long count;
		private long firstPlayersCount;

		public DynamicQuestReward(int itemId, long count, int firstPlayersCount)
		{
			this.itemId = itemId;
			this.count = count;
			this.firstPlayersCount = firstPlayersCount;
		}
	}

	private class OnPlayerEnterListenerImpl implements OnPlayerEnterListener
	{
		@Override
		public void onPlayerEnter(Player player)
		{
			playerEnter(player);
		}
	}

	private class PlayerCheckLevel implements ICheckStartCondition
	{
		private int min;
		private int max;

		public PlayerCheckLevel(int min, int max)
		{
			this.min = min;
			this.max = max;
		}

		@Override
		public final boolean checkCondition(Player player)
		{
			if(player.getActiveSubClass() == null)
				return false;
			return player.getLevel() >= min && player.getLevel() <= max;
		}
	}

	private class PlayerCheckZone implements ICheckStartCondition
	{
		private String[] name;

		public PlayerCheckZone(String[] name)
		{
			this.name = name;
		}

		@Override
		public final boolean checkCondition(Player player)
		{
			for(String zone : name)
			{
				if(player.isInZone(zone))
				{
					return true;
				}
			}
			return false;
		}
	}
}
