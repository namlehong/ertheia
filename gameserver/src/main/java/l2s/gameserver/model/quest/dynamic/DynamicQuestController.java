package l2s.gameserver.model.quest.dynamic;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.time.cron.SchedulingPattern;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Дмитрий
 * @date 27.10.12  23:54
 */
public class DynamicQuestController
{
	private static final Logger log = LoggerFactory.getLogger(DynamicQuestController.class);
	private static DynamicQuestController ourInstance = new DynamicQuestController();
	private final Map<Integer, DynamicQuest> dynamicQuestsMap;
	private final Map<String, DynamicQuest> dynamicQuestsMapByName;

	private DynamicQuestController()
	{
		//dynamicQuestsMap = new HashMap<>();
		//dynamicQuestsMapByName = new HashMap<>();
		dynamicQuestsMap = new HashMap<Integer, DynamicQuest>();
		dynamicQuestsMapByName = new HashMap<String, DynamicQuest>();
	}

	public static DynamicQuestController getInstance()
	{
		return ourInstance;
	}

	void registerDynamicQuest(DynamicQuest dynamicQuest)
	{
		dynamicQuestsMap.put(dynamicQuest.getQuestId(), dynamicQuest);
		dynamicQuestsMapByName.put(dynamicQuest.getClass().getSimpleName(), dynamicQuest);
	}

	/**
	 * Запускает динамический квест (кампания/локационный квест)
	 * с заданным questId и step равным 1
	 *
	 * @param questId - идентификатор запускаемого квеста
	 */
	public void startQuest(int questId)
	{
		startQuest(questId, 1);
	}

	/**
	 * Запускает динамический квест (кампания/локационный квест)
	 * с заданным questId и step
	 *
	 * @param questId - идентификатор запускаемого квеста
	 * @param step    - шаг квеста, с которого начнется выполнение (для локационных квестов)
	 */
	public void startQuest(int questId, int step)
	{
		DynamicQuest quest = dynamicQuestsMap.get(questId);
		quest.setCurrentStep(step);
		if(quest.isStartCondition())
		{
			quest.start(new QuestEnder(questId));
		}
		//		System.out.println("Condition == "+quest.isStartCondition());
	}

	/**
	 * Завершает выполнение квеста questId с одним из состояний success
	 * Где success = true, успешное досрочное завершение квеста
	 * success = false, завершение квеста по истечению времени
	 * Переводит стадию квеста в 5 минутный отсчет до полного завершения
	 *
	 * @param questId - идентификатор квеста
	 * @param success - флаг заверщения
	 */
	public void endQuest(int questId, boolean success)
	{
		DynamicQuest quest = dynamicQuestsMap.get(questId);
		quest.stop(success, new QuestFinalizer(questId));
	}

	/**
	 * Завершает выполнение квеста questId с одним из состояний success
	 * Где success = true, успешное досрочное завершение квеста
	 * success = false, завершение квеста по истечению времени
	 *
	 * @param questId - идентификатор квеста
	 */
	private void finalizeQuest(int questId)
	{
		DynamicQuest quest = dynamicQuestsMap.get(questId);
		quest.finish();
	}

	/**
	 * Инициализирует запуск квеста по расписанию
	 *
	 * @param questId - идентификатор квеста
	 * @param pattern - шаблон времени в формате cron, по которому будет производиться запуск квеста
	 */
	void initSchedulingPattern(int questId, SchedulingPattern pattern)
	{
		if(!dynamicQuestsMap.containsKey(questId))
		{
			log.warn("DynamicQuestController#initSchedulingPattern(int, SchedulingPattern): Not found quest with id: " + questId);
			return;
		}
		long nextLaunchTime = pattern.next(System.currentTimeMillis());
		ThreadPoolManager.getInstance().schedule(new QuestStarter(questId, pattern), nextLaunchTime - System.currentTimeMillis());
	}

	public void requestDynamicQuestHtml(int id, int step, Player player)
	{
		if(dynamicQuestsMap.containsKey(id))
		{
			DynamicQuest quest = dynamicQuestsMap.get(id);
			quest.requestHtml(step, player);
		}
	}

	public void requestQuestProgressInfo(int id, int step, Player player)
	{
		if(dynamicQuestsMap.containsKey(id))
		{
			DynamicQuest quest = dynamicQuestsMap.get(id);
			quest.requestProgressInfo(step, player);
		}
	}

	public void requestScoreBoard(int id, int step, Player player)
	{
		if(dynamicQuestsMap.containsKey(id))
		{
			DynamicQuest quest = dynamicQuestsMap.get(id);
			quest.requestScoreBoard(step, player);
		}
	}

	public void taskCompleted(int questId, int taskId)
	{
		if(dynamicQuestsMap.containsKey(questId))
		{
			DynamicQuest quest = dynamicQuestsMap.get(questId);
			quest.taskCompleted(taskId);
		}
	}

	public void processDialogEvent(String questName, String event, Player player)
	{
		if(dynamicQuestsMapByName.containsKey(questName))
		{
			DynamicQuest quest = dynamicQuestsMapByName.get(questName);
			quest.processDialogEvent(event, player);
		}
	}

	public void StartCondition(int questId)
	{
		if(dynamicQuestsMap.containsKey(questId))
		{
			DynamicQuest quest = dynamicQuestsMap.get(questId);
			quest.isStartCondition();
		}
	}

	private final class QuestStarter extends RunnableImpl
	{
		private final int questId;
		private final SchedulingPattern pattern;

		public QuestStarter(int questId, SchedulingPattern pattern)
		{
			this.questId = questId;
			this.pattern = pattern;
		}

		@Override
		public void runImpl() throws Exception
		{
			DynamicQuest quest = dynamicQuestsMap.get(questId);
			startQuest(questId);
			long nextLaunchTime = pattern.next(System.currentTimeMillis() + quest.getDuration() * 1000);
			ThreadPoolManager.getInstance().schedule(this, nextLaunchTime - System.currentTimeMillis());
		}
	}

	private final class QuestEnder extends RunnableImpl
	{
		private final int questId;

		public QuestEnder(int questId)
		{
			this.questId = questId;
		}

		@Override
		public void runImpl() throws Exception
		{
			endQuest(questId, false);
		}
	}

	private final class QuestFinalizer extends RunnableImpl
	{
		private final int questId;

		public QuestFinalizer(int questId)
		{
			this.questId = questId;
		}

		@Override
		public void runImpl() throws Exception
		{
			finalizeQuest(questId);
		}
	}
}