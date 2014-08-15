package l2s.gameserver.model.actor.instances.creature;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.Config;
import l2s.gameserver.listener.actor.OnAttackListener;
import l2s.gameserver.listener.actor.OnMagicUseListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Skill.SkillTargetType;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.AbnormalStatusUpdatePacket;
import l2s.gameserver.network.l2.s2c.ExAbnormalStatusUpdateFromTargetPacket;
import l2s.gameserver.network.l2.s2c.ExOlympiadSpelledInfoPacket;
import l2s.gameserver.network.l2.s2c.PartySpelledPacket;
import l2s.gameserver.network.l2.s2c.ShortBuffStatusUpdatePacket;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.skills.AbnormalEffect;
import l2s.gameserver.skills.AbnormalType;
import l2s.gameserver.skills.EffectType;
import l2s.gameserver.skills.combo.SkillComboType;
import l2s.gameserver.templates.skill.EffectTemplate;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.funcs.Func;
import l2s.gameserver.stats.funcs.FuncOwner;
import l2s.gameserver.stats.triggers.TriggerType;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.taskmanager.EffectTaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Effect extends RunnableImpl implements Comparable<Effect>, FuncOwner
{
	protected static final Logger _log = LoggerFactory.getLogger(Effect.class);

	public final static Effect[] EMPTY_L2EFFECT_ARRAY = new Effect[0];

	//Состояние, при котором работает задача запланированного эффекта
	public static int SUSPENDED = -1;

	public static int STARTING = 0;
	public static int STARTED = 1;
	public static int ACTING = 2;
	public static int FINISHED = 3;

	/** Накладывающий эффект */
	protected final Creature _effector;
	/** Тот, на кого накладывают эффект */
	protected final Creature _effected;

	protected final Skill _skill;
	private final int _displayId;
	private final int _displayLevel;

	// the value of an update
	private final double _value;

	// the current state
	private final AtomicInteger _state;

	// counter
	private int _count;

	// period, milliseconds
	private long _period;
	private long _startTimeMillis;
	private long _duration;

	private Effect _next = null;
	private boolean _active = false;

	protected final EffectTemplate _template;

	private Future<?> _effectTask;

	protected Effect(Env env, EffectTemplate template)
	{
		_skill = env.skill;
		_effector = env.character;
		_effected = env.target;

		_template = template;
		_value = template._value;
		_count = template.getCount();
		_period = template.getPeriod();

		_duration = _period * _count;

		_displayId = template._displayId != 0 ? template._displayId : _skill.getDisplayId();
		_displayLevel = template._displayLevel != 0 ? template._displayLevel : _skill.getDisplayLevel();

		_state = new AtomicInteger(STARTING);
	}

	public long getPeriod()
	{
		if(_template.isUnlimPeriod())
			return Integer.MAX_VALUE;
		return _period;
	}

	public void setPeriod(long time)
	{
		_period = time;
		_duration = _period * _count;
	}

	public int getCount()
	{
		if(_template.isUnlimPeriod())
			return 1;
		return _count;
	}

	public void setCount(int count)
	{
		_count = count;
		_duration = _period * _count;
	}

	/**
	 * Возвращает время старта эффекта, если время не установлено, возвращается текущее
	 */
	public long getStartTime()
	{
		if(_startTimeMillis == 0L || _template.isUnlimPeriod())
			return System.currentTimeMillis();
		return _startTimeMillis;
	}

	/** Возвращает общее время действия эффекта в миллисекундах. */
	public long getTime()
	{
		if(_template.isUnlimPeriod())
			return 0;
		return System.currentTimeMillis() - getStartTime();
	}

	/** Возвращает длительность эффекта в миллисекундах. */
	public long getDuration()
	{
		if(_template.isUnlimPeriod())
			return Integer.MAX_VALUE;
		return _duration;
	}

	/** Возвращает оставшееся время в секундах. */
	public int getTimeLeft()
	{
		if(_template.isUnlimPeriod())
			return Integer.MAX_VALUE;
		return (int) ((getDuration() - getTime()) / 1000L);
	}

	/** Возвращает true, если осталось время для действия эффекта */
	public boolean isTimeLeft()
	{
		if(_template.isUnlimPeriod())
			return true;
		return getDuration() - getTime() > 0L;
	}

	public boolean isActive()
	{
		return _active;
	}

	/**
	 * Для неактивных эфектов не вызывается onActionTime.
	 */
	public void setActive(boolean set)
	{
		_active = set;
	}

	public EffectTemplate getTemplate()
	{
		return _template;
	}

	public AbnormalType getAbnormalType()
	{
		return getTemplate().getAbnormalType();
	}

	public int getAbnormalLvl()
	{
		return getTemplate().getAbnormalLvl();
	}

	public boolean checkAbnormalType(AbnormalType abnormal)
	{
		AbnormalType abnormalType = getAbnormalType();
		if(abnormalType == AbnormalType.none)
			return false;

		return abnormal == abnormalType;
	}

	public boolean checkAbnormalType(Effect effect)
	{
		return checkAbnormalType(effect.getAbnormalType());
	}

	public Skill getSkill()
	{
		return _skill;
	}

	public Creature getEffector()
	{
		return _effector;
	}

	public Creature getEffected()
	{
		return _effected;
	}

	public double calc()
	{
		return _value;
	}

	public boolean isFinished()
	{
		return getState() == FINISHED;
	}

	private int getState()
	{
		return _state.get();
	}

	private boolean setState(int oldState, int newState)
	{
		return _state.compareAndSet(oldState, newState);
	}

	private ActionDispelListener _listener;

	private class ActionDispelListener implements OnAttackListener, OnMagicUseListener
	{
		@Override
		public void onMagicUse(Creature actor, Skill skill, Creature target, boolean alt)
		{
			if(getSkill().isDoNotDispelOnSelfBuff() && !skill.isOffensive())
				return;
			exit();
		}

		@Override
		public void onAttack(Creature actor, Creature target)
		{
			exit();
		}
	}

	public boolean checkCondition()
	{
		return true;
	}

	public void singleUse()
	{
		onStart();
		onActionTime();
		onExit();
	}

	/** Notify started */
	protected void onStart()
	{
		//tigger on start
		getEffected().useTriggers(getEffected(), TriggerType.ON_START_EFFECT, null, _skill, getTemplate(), 0);

		// Остальные операции для одноразовых эффектов не нужны.
		if(getTemplate().isSingle())
			return;

		getEffected().addStatFuncs(getStatFuncs());
		getEffected().addTriggers(getTemplate());

		AbnormalEffect[] abnormals = _template.getAbnormalEffects();
		for(AbnormalEffect abnormal : abnormals)
		{
			if(abnormal != AbnormalEffect.NONE)
				getEffected().startAbnormalEffect(abnormal);
		}

		if(_template._cancelOnAction && getEffected().isPlayable())
			getEffected().addListener(_listener = new ActionDispelListener());
		if(getEffected().isPlayer() && !getSkill().canUseTeleport())
			getEffected().getPlayer().getPlayerAccess().UseTeleport = false;

		if(getSkill().getChainIndex() != -1 && getSkill().getChainSkillId() > 0 && getEffector() != null && getEffector().isPlayer() && !getEffector().getPlayer().getSkillChainDetails().containsKey(getSkill().getChainIndex()))
		{
			final Skill known = getEffector().getKnownSkill(getSkill().getChainSkillId());
			if(known != null && !getEffector().isSkillDisabled(known) && !getEffector().isUnActiveSkill(known.getId()))
			{
				getEffector().getPlayer().addChainDetail(getEffected(), getSkill(),getTimeLeft());
			}
		}
	}

	/** Return true for continuation of this effect */
	protected abstract boolean onActionTime();

	/**
	 * Cancel the effect in the the abnormal effect map of the effected L2Character.<BR><BR>
	 */
	protected void onExit()
	{
		//tigger on exit
		getEffected().useTriggers(getEffected(), TriggerType.ON_EXIT_EFFECT, null, _skill, getTemplate(), 0);

		// Остальные операции для одноразовых эффектов не нужны.
		if(getTemplate().isSingle())
			return;

		getEffected().removeStatsOwner(this);

		getEffected().removeTriggers(getTemplate());

		AbnormalEffect[] abnormals = _template.getAbnormalEffects();
		for(AbnormalEffect abnormal : abnormals)
		{
			if(abnormal != AbnormalEffect.NONE)
				getEffected().stopAbnormalEffect(abnormal);
		}

		if(_template._cancelOnAction)
			getEffected().removeListener(_listener);
		if(getEffected().isPlayer() && checkAbnormalType(AbnormalType.hp_recover))
			getEffected().sendPacket(new ShortBuffStatusUpdatePacket());
		if(getEffected().isPlayer() && !getSkill().canUseTeleport() && !getEffected().getPlayer().getPlayerAccess().UseTeleport)
			getEffected().getPlayer().getPlayerAccess().UseTeleport = true;

		if(getSkill().getChainSkillId() > 0 && getSkill().getChainIndex() != -1 && getEffector() != null && getEffector().isPlayer())
			getEffector().getPlayer().removeChainDetail(getSkill().getChainIndex());
	}

	private void stopEffectTask()
	{
		if(_effectTask != null)
			_effectTask.cancel(false);
	}

	private void startEffectTask()
	{
		if(_effectTask == null && !_template.isUnlimPeriod())
		{
			_startTimeMillis = System.currentTimeMillis();
			_effectTask = EffectTaskManager.getInstance().scheduleAtFixedRate(this, _period, _period);
		}
	}

	public void restart()
	{
		stopEffectTask();
		_effectTask = null;
		startEffectTask();
	}

	/**
	 * Добавляет эффект в список эффектов, в случае успешности вызывается метод start
	 */
	public final boolean schedule()
	{
		Creature effected = getEffected();
		if(effected == null)
			return false;

		if(!checkCondition())
			return false;

		return getEffected().getEffectList().addEffect(this);
	}

	/**
	 * Запускает задачу эффекта, в случае если эффект успешно добавлен в список
	 */
	public final void start()
	{
		if(setState(STARTING, STARTED))
		{
			synchronized (this)
			{
				setActive(true);
				onStart();
				startEffectTask();
			}
		}

		run();
	}

	@Override
	public final void runImpl() throws Exception
	{
		if(setState(STARTED, ACTING))
			return;

		if(getState() == SUSPENDED)
		{
			if(isTimeLeft())
			{
				_count--;
				if(isTimeLeft())
					return;
			}

			exit();
			return;
		}

		if(getState() == ACTING)
		{
			if(isTimeLeft())
			{
				_count--;
				if((!isActive() || _count > 0 && onActionTime()) && isTimeLeft())
					return;
			}
		}

		if(setState(ACTING, FINISHED))
		{
			synchronized (this)
			{
				setActive(false);
				stopEffectTask();
				onExit();
			}

			//tigger on finish
			getEffected().useTriggers(getEffected(), TriggerType.ON_FINISH_EFFECT, null, _skill, getTemplate(), 0);

			boolean msg = !isHidden() && getEffected().getEffectList().getEffectsCount(getSkill()) == 1;

			getEffected().getEffectList().removeEffect(this);

			// Отображать сообщение только для последнего оставшегося эффекта скилла
			if(msg)
				getEffected().sendPacket(new SystemMessage(SystemMessage.S1_HAS_WORN_OFF).addSkillName(getDisplayId(), getDisplayLevel()));

			if(getSkill().getDelayedEffect() > 0)
				SkillTable.getInstance().getInfo(getSkill().getDelayedEffect(), 1).getEffects(_effector, _effected, false);

			// Добавляем следующий запланированный эффект
			Effect next = getNext();
			if(next != null && next.setState(SUSPENDED, STARTING))
				next.schedule();
		}
	}

	/**
	 * Завершает эффект и все связанные, удаляет эффект из списка эффектов
	 */
	public final void exit()
	{
		Effect next = getNext();
		if(next != null)
			next.exit();
		removeNext();

		//Эффект запланирован на запуск, удаляем
		if(setState(STARTING, FINISHED))
			getEffected().getEffectList().removeEffect(this);
		//Эффект работает в "фоне", останавливаем задачу в планировщике
		else if(setState(SUSPENDED, FINISHED))
			stopEffectTask();
		else if(setState(STARTED, FINISHED) || setState(ACTING, FINISHED))
		{
			synchronized (this)
			{
				setActive(false);
				stopEffectTask();
				onExit();
			}
			getEffected().getEffectList().removeEffect(this);
		}
	}

	/**
	 * Поставить в очередь эффект
	 * @param e
	 * @return true, если эффект поставлен в очередь
	 */
	private boolean scheduleNext(Effect e)
	{
		if(e == null || e.isFinished())
			return false;

		Effect next = getNext();
		if(next != null && !next.maybeScheduleNext(e))
			return false;

		_next = e;

		return true;
	}

	public Effect getNext()
	{
		return _next;
	}

	private void removeNext()
	{
		_next = null;
	}

	/**
	 * @return false - игнорировать новый эффект, true - использовать новый эффект
	 */
	public boolean maybeScheduleNext(Effect newEffect)
	{
		/*TODO: [Bonux] Починить от овербаффа и сделать, чтобы распостранялось только на эффекты хербов.
		if(newEffect.getAbnormalLvl() < getAbnormalLvl()) // новый эффект слабее
		{
			if(newEffect.getTimeLeft() > getTimeLeft()) // новый эффект длинее
			{
				newEffect.suspend();
				scheduleNext(newEffect); // пробуем пристроить новый эффект в очередь
			}

			return false; // более слабый эффект всегда игнорируется, даже если не попал в очередь
		}
		else // если старый не дольше, то просто остановить его
		if(newEffect.getTimeLeft() >= getTimeLeft())
		{
			// наследуем зашедуленый старому, если есть смысл
			if(getNext() != null && getNext().getTimeLeft() > newEffect.getTimeLeft())
			{
				newEffect.scheduleNext(getNext());
				// отсоединяем зашедуленные от текущего
				removeNext();
			}
			exit();
		}
		else
		// если новый короче то зашедулить старый
		{
			suspend();
			newEffect.scheduleNext(this);
		}*/
		if(newEffect.getAbnormalLvl() < getAbnormalLvl())
			return false;

		exit();
		return true;
	}

	public Func[] getStatFuncs()
	{
		return getTemplate().getStatFuncs(this);
	}

	public void addIcon(AbnormalStatusUpdatePacket abnormalStatus)
	{
		if(!isActive() || isHidden())
			return;
		int duration = (_skill.isToggle() || _template.isHideTime()) ? AbnormalStatusUpdatePacket.INFINITIVE_EFFECT : getTimeLeft();
		abnormalStatus.addEffect(getDisplayId(), getDisplayLevel(), duration);
	}

	public void addIcon(ExAbnormalStatusUpdateFromTargetPacket abnormalStatus)
	{
		if(!isActive() || isHidden())
			return;
		int duration = (_skill.isToggle() || _template.isHideTime()) ? AbnormalStatusUpdatePacket.INFINITIVE_EFFECT : getTimeLeft();
		abnormalStatus.addEffect(_effector.getObjectId(), getDisplayId(), getDisplayLevel(), duration, getSkill().getComboTypeFromCharStatus(getEffector(), getEffected()).getId());
	}

	public void addPartySpelledIcon(PartySpelledPacket ps)
	{
		if(!isActive() || isHidden())
			return;
		int duration = (_skill.isToggle() || _template.isHideTime()) ? AbnormalStatusUpdatePacket.INFINITIVE_EFFECT : getTimeLeft();
		ps.addPartySpelledEffect(getDisplayId(), getDisplayLevel(), duration);
	}

	public void addOlympiadSpelledIcon(Player player, ExOlympiadSpelledInfoPacket os)
	{
		if(!isActive() || isHidden())
			return;
		int duration = (_skill.isToggle() || _template.isHideTime()) ? AbnormalStatusUpdatePacket.INFINITIVE_EFFECT : getTimeLeft();
		os.addSpellRecivedPlayer(player);
		os.addEffect(getDisplayId(), getDisplayLevel(), duration);
	}

	protected int getLevel()
	{
		return _skill.getLevel();
	}

	public EffectType getEffectType()
	{
		return getTemplate()._effectType;
	}

	public boolean isHidden()
	{
		return getDisplayId() < 0;
	}

	@Override
	public int compareTo(Effect obj)
	{
		if(obj.equals(this))
			return 0;
		return 1;
	}

	public boolean isSaveable()
	{
		return getSkill().isSaveable() && getTimeLeft() >= Config.ALT_SAVE_EFFECTS_REMAINING_TIME && !isHidden();
	}

	public boolean isCancelable()
	{
		return getSkill().isCancelable() && !isHidden();
	}

	public boolean isSelfDispellable()
	{
		return getSkill().isSelfDispellable() && !isHidden();
	}

	public int getDisplayId()
	{
		return _displayId;
	}

	public int getDisplayLevel()
	{
		return _displayLevel;
	}

	@Override
	public String toString()
	{
		return "Skill: " + _skill + ", state: " + getState() + ", active : " + _active;
	}

	@Override
	public boolean isFuncEnabled()
	{
		return true;
	}

	@Override
	public boolean overrideLimits()
	{
		return false;
	}
	public int getIndex()
	{
		return _template.getIndex();
	}

	public boolean checkBlockedAbnormalType(AbnormalType abnormal)
	{
		return false;
	}

	public boolean checkDebuffImmunity()
	{
		return false;
	}

	public final boolean isSelf()
	{
		return getTemplate().isSelf();
	}

	public final boolean isOffensive()
	{
		return isSelf() && getSkill().isSelfOffensive() || !isSelf() && getSkill().isOffensive();
	}
}