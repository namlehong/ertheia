package dynamic_quests;

import l2s.gameserver.listener.actor.player.OnSocialActionListener;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.dynamic.DynamicQuest;
import l2s.gameserver.network.l2.c2s.RequestActionUse;
import l2s.gameserver.network.l2.s2c.SocialActionPacket;
import l2s.gameserver.scripts.ScriptFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Дмитрий
 * @date 24.10.12  16:51
 */
public class MemorialService extends DynamicQuest implements ScriptFile
{
	private static final Logger _log = LoggerFactory.getLogger(MemorialService.class);
	// ID квеста
	private static final int QUEST_ID = 3;
	// Время запуска квеста в формате Cron
	//@Awakeninger для тех кто не знает формат Cron
	//Минуты Часы ДниМесяца Месяцы ДниНедели
	//Запись String переменных по первым 3м буквам
	//private static final String START_TIME = "00 23 * * Mon";
	private static final String START_TIME = "00 23 * * Mon";
	// Настройки доступности квеста по уровням игрока
	private static final int MIN_LEVEL = 40;
	private static final int MAX_LEVEL = 99;
	// Продолжительность квеста
	private static final int DURATION = 30 * 1000;
	//private static final int DURATION = 120;
	// ID задач
	private static final int SORROW_EMOTE = 301;
	private static final int PERFORM_POLITE = 302;
	// NPC, которые "слушают" соц. действия
	private static final int PRIEST_KHYBER = 33330;
	private static final int GUARD_JANSON = 33331;
	// Группа npc, которая заспаунится при старте квеста
	private static final String SPAWN_GROUP = "memorial_service_npc";
	// Количество соц. действий, необходимых для завершения задачи
	private static final int MAX_TASK_POINT = 1;
	private static final int REWARD_1 = 33501;
	private final OnSocialActionListener socialActionListener = new OnSocialActionListenerImpl();

	public MemorialService()
	{
		super(QUEST_ID, DURATION);
		addSpawns(SPAWN_GROUP);
		addTask(SORROW_EMOTE, MAX_TASK_POINT, TASK_INCREASE_MODE_ONCE_PER_CHAR);
		addTask(PERFORM_POLITE, MAX_TASK_POINT, TASK_INCREASE_MODE_ONCE_PER_CHAR);
		addReward(REWARD_1, 1);
		addEliteReward(REWARD_1, 2, 2);
		addLevelCheck(MIN_LEVEL, MAX_LEVEL);
		initSchedulingPattern(START_TIME);
	}

	@Override
	protected boolean onStartCondition()
	{
		return true;
	}

	@Override
	protected void onStart()
	{
	}

	@Override
	protected void onStop(boolean success)
	{
		for(int objectId : getParticipants())
		{
			Player player = GameObjectsStorage.getPlayer(objectId);
			if(player != null)
			{
				removeParticipant(player);
			}
		}
	}

	@Override
	protected void onFinish()
	{
	}

	@Override
	protected String onRequestHtml(Player player, boolean participant)
	{
		if(getCurrentStep() == 1)
		{ // Квест активен
			if(isStarted())
			{ // Квест запущен
				if(!participant)
				{
					return "dc0003_start001.htm";
				}
				else
				{
					return "dc0003_context001.htm";
				}
			}
			else if(isSuccessed())
			{ // Квест завершился успешно
				boolean rewardReceived = rewardReceived(player);
				if(rewardReceived)
				{
					return "dc0003_reward_received001.htm";
				}
				else
				{
					return "dc0003_reward001.htm";
				}
			}
			else
			{ // Квест завершился провалом
				return "dc0003_failed001.htm";
			}
		}
		return null;
	}

	@Override
	protected boolean onPlayerEnter(Player player)
	{
		if(getParticipants().contains(player.getObjectId()))
		{
			addParticipant(player);
			return true;// Restore action listener for participant
		}
		return false;
	}

	@Override
	protected void onTaskCompleted(int taskId)
	{
	}

	@Override
	protected String onDialogEvent(String event, Player player)
	{
		String response = null;
		if(event.equals("Accept"))
		{
			addParticipant(player);
			response = "dc0003_context001.htm";
		}
		else if(event.equals("Reward"))
		{
			tryReward(player);
			response = "dc0003_reward_received001.htm";
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
		player.getListeners().add(socialActionListener);
	}

	@Override
	protected void onRemoveParticipant(Player player)
	{
		player.getListeners().remove(socialActionListener);
	}

	@Override
	protected boolean isZoneQuest()
	{
		return false;
	}

	@Override
	public void onLoad()
	{
		_log.info("Dynamic Quest: Loaded quest ID " + QUEST_ID + ". Name: Memorial Service - Campaign");
	}

	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{
	}

	private final class OnSocialActionListenerImpl implements OnSocialActionListener
	{
		@Override
		public void onSocialAction(Player player, GameObject target, RequestActionUse.Action action)
		{
			if(target != null && target.isNpc() && player.isInRange(target, NpcInstance.INTERACTION_DISTANCE))
			{
				NpcInstance npc = (NpcInstance) target;
				switch(npc.getNpcId())
				{
					case PRIEST_KHYBER:
						if(action.value == SocialActionPacket.SORROW)
						{
							increaseTaskPoint(SORROW_EMOTE, player, 1);
						}
						break;
					case GUARD_JANSON:
						if(action.value == SocialActionPacket.BOW)
						{
							increaseTaskPoint(PERFORM_POLITE, player, 1);
						}
						break;
				}
			}
		}
	}
}
