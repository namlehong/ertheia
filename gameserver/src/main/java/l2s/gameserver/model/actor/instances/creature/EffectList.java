package l2s.gameserver.model.actor.instances.creature;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
//import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
/*import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;*/

import org.apache.commons.lang3.ArrayUtils;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.skills.AbnormalType;
import l2s.gameserver.skills.EffectType;
import l2s.gameserver.templates.skill.EffectTemplate;
import l2s.gameserver.skills.skillclasses.Transformation;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.stats.funcs.FuncTemplate;

public final class EffectList
{
	public static final int NONE_SLOT_TYPE = -1;
	public static final int BUFF_SLOT_TYPE = 0;
	public static final int MUSIC_SLOT_TYPE = 1;
	public static final int TRIGGER_SLOT_TYPE = 2;
	public static final int DEBUFF_SLOT_TYPE = 3;

	public static final int DEBUFF_LIMIT = 8;
	public static final int MUSIC_LIMIT = 12;
	public static final int TRIGGER_LIMIT = 12;

	private final Collection<Effect> _effects = new ConcurrentLinkedQueue<Effect>();

	/** Блокировка для чтения/записи вещей из списка и внешних операций */
	/*private final ReadWriteLock _lock = new ReentrantReadWriteLock();
	private final Lock _readLock = _lock.readLock();
	private final Lock _writeLock = _lock.writeLock();
	private Lock _lock = new ReentrantLock();*/

	private Creature _owner;

	public EffectList(Creature owner)
	{
		_owner = owner;
	}

	public boolean containsEffects(int skillId)
	{
		/*readLock();
		try
		{*/
			if(_effects.isEmpty())
				return false;

			for(Effect e : _effects)
			{
				if(e.getSkill().getId() == skillId)
					return true;
			}
		/*}
		finally
		{
			readUnlock();
		}*/
		return false;
	}

	public boolean containsEffects(Skill skill)
	{
		if(skill == null)
			return false;
		return containsEffects(skill.getId());
	}


	public boolean containsEffects(EffectType et)
	{
		if(et == null)
			return false;

		/*readLock();
		try
		{*/
			for(Effect e : _effects)
			{
				if(e.getEffectType() == et)
					return true;
			}
		/*}
		finally
		{
			readUnlock();
		}*/
		return false;
	}

	public boolean containsEffects(AbnormalType type)
	{
		if(type == null)
			return false;

		/*readLock();
		try
		{*/
			for(Effect e : _effects)
			{
				if(e.getAbnormalType() == type)
					return true;
			}
		/*}
		finally
		{
			readUnlock();
		}*/
		return false;
	}

	/*public Collection<Effect> getEffects(int skillId)
	{
		List<Effect> result;

		readLock();
		try
		{
			if(!_effects.isEmpty())
			{
				result = new ArrayList<Effect>();
				for(Effect e : _effects)
				{
					if(e.getSkill().getId() == skillId)
						result.add(e);
				}
			}
			else
				result = Collections.emptyList();
		}
		finally
		{
			readUnlock();
		}
		return result;
	}

	public Collection<Effect> getEffects(Skill skill)
	{
		if(skill == null)
			return Collections.emptyList();
		return getEffects(skill.getId());
	}

	public Collection<Effect> getEffects(AbnormalType type)
	{
		List<Effect> result;

		readLock();
		try
		{
			if(!_effects.isEmpty())
			{
				result = new ArrayList<Effect>();
				for(Effect e : _effects)
				{
					if(type == e.getAbnormalType())
						result.add(e);
				}
			}
			else
				result = Collections.emptyList();
		}
		finally
		{
			readUnlock();
		}
		return result;
	}

	public Collection<Effect> getEffects(EffectType et)
	{
		List<Effect> result;

		readLock();
		try
		{
			if(!_effects.isEmpty())
			{
				result = new ArrayList<Effect>();
				for(Effect e : _effects)
				{
					if(e.getEffectType() == et)
						result.add(e);
				}
			}
			else
				result = Collections.emptyList();
		}
		finally
		{
			readUnlock();
		}
		return result;
	}*/

	public Collection<Effect> getEffects()
	{
		/*List<Effect> result;

		readLock();
		try
		{
			if(!_effects.isEmpty())
				result = new ArrayList<Effect>(_effects);
			else
				result = Collections.emptyList();
		}
		finally
		{
			readUnlock();
		}*/
		return _effects;
	}

	public int getEffectsCount(int skillId)
	{
		int result = 0;

		/*readLock();
		try
		{*/
			if(_effects.isEmpty())
				return 0;

			for(Effect e : _effects)
			{
				if(e.getSkill().getId() == skillId)
					result++;
			}
		/*}
		finally
		{
			readUnlock();
		}*/
		return result;
	}

	public int getEffectsCount(Skill skill)
	{
		if(skill == null)
			return 0;
		return getEffectsCount(skill.getId());
	}

	public int getEffectsCount(AbnormalType type)
	{
		int result = 0;

		/*readLock();
		try
		{*/
			if(_effects.isEmpty())
				return 0;

			for(Effect e : _effects)
			{
				if(type == e.getAbnormalType())
					result++;
			}
		/*}
		finally
		{
			readUnlock();
		}*/
		return result;
	}

	public int getEffectsCount(EffectType et)
	{
		int result = 0;

		/*readLock();
		try
		{*/
			if(_effects.isEmpty())
				return 0;

			for(Effect e : _effects)
			{
				if(e.getEffectType() == et)
					result++;
			}
		/*}
		finally
		{
			readUnlock();
		}*/
		return result;
	}

	public int getEffectsCount()
	{
		return _effects.size();
	}

	public boolean isEmpty()
	{
		return _effects.isEmpty();
	}

	/**
	 * Возвращает первые эффекты для всех скиллов. Нужно для отображения не
	 * более чем 1 иконки для каждого скилла.
	 */
	public Effect[] getFirstEffects()
	{
		Effect[] result;

		/*readLock();
		try
		{*/
			if(!_effects.isEmpty())
			{
				TIntObjectMap<Effect> map = new TIntObjectHashMap<Effect>();
				for(Effect e : _effects)
				{
					if(!e.isHidden())
						map.put(e.getSkill().getId(), e);
				}

				result = map.values(new Effect[map.size()]);
			}
			else
				result = Effect.EMPTY_L2EFFECT_ARRAY;
		/*}
		finally
		{
			readUnlock();
		}*/
		return result;
	}

	private void checkSlotLimit(Effect newEffect)
	{
		/*readLock();
		try
		{*/
			if(_effects.isEmpty())
				return;

			int slotType = getSlotType(newEffect);
			if(slotType == NONE_SLOT_TYPE)
				return;

			int size = 0;
			TIntSet skillIds = new TIntHashSet();
			for(Effect e : _effects)
			{
				if(e.getSkill().equals(newEffect.getSkill())) // мы уже имеем эффект от этого скилла
					return;

				if(!skillIds.contains(e.getSkill().getId()))
				{
					int subType = getSlotType(e);
					if(subType == slotType)
					{
						size++;
						skillIds.add(e.getSkill().getId());
					}
				}
			}

			int limit = 0;
			switch(slotType)
			{
				case BUFF_SLOT_TYPE:
					limit = _owner.getBuffLimit();
					break;
				case MUSIC_SLOT_TYPE:
					limit = MUSIC_LIMIT;
					break;
				case DEBUFF_SLOT_TYPE:
					limit = DEBUFF_LIMIT;
					break;
				case TRIGGER_SLOT_TYPE:
					limit = TRIGGER_LIMIT;
					break;
			}

			if(size < limit)
				return;

			for(Effect e : _effects)
			{
				if(getSlotType(e) == slotType)
				{
					stopEffects(e.getSkill().getId());
					break;
				}
			}
		/*}
		finally
		{
			readUnlock();
		}*/
	}

	public static int getSlotType(Effect e)
	{
		if(e.getSkill().isPassive() || e.getSkill().isToggle() || e.getSkill() instanceof Transformation || e.checkAbnormalType(AbnormalType.hp_recover) || e.getEffectType() == EffectType.Cubic)
			return NONE_SLOT_TYPE;
		else if(e.isOffensive())
			return DEBUFF_SLOT_TYPE;
		else if(e.getSkill().isMusic())
			return MUSIC_SLOT_TYPE;
		else if(e.getSkill().isTrigger())
			return TRIGGER_SLOT_TYPE;
		else
			return BUFF_SLOT_TYPE;
	}

	public static boolean checkAbnormalType(EffectTemplate ef1, EffectTemplate ef2)
	{
		AbnormalType abnormalType1 = ef1.getAbnormalType();
		if(abnormalType1 == AbnormalType.none)
			return false;

		AbnormalType abnormalType2 = ef2.getAbnormalType();
		if(abnormalType2 == AbnormalType.none)
			return false;

		return abnormalType1 == abnormalType2;
	}

	public synchronized boolean addEffect(Effect effect)
	{
		//TODO [G1ta0] затычка на статы повышающие HP/MP/CP
		double hp = _owner.getCurrentHp();
		double mp = _owner.getCurrentMp();
		double cp = _owner.getCurrentCp();

		boolean success = false;

		_owner.getStatsRecorder().block(); // Для того, чтобы не флудить пакетами.

		/*_lock.lock();
		/writeLock();
		try
		{*/
			if(!_effects.isEmpty())
			{
				AbnormalType abnormalType = effect.getAbnormalType();
				if(abnormalType == AbnormalType.none)
				{
					// Удаляем такие же эффекты
					for(Effect e : _effects)
					{
						if(e.getAbnormalType() == AbnormalType.none && e.getSkill().getId() == effect.getSkill().getId() && e.getIndex() == effect.getIndex())
						{
							// Если оставшаяся длительность старого эффекта больше чем длительность нового, то оставляем старый.
							if(effect.getTimeLeft() > e.getTimeLeft())
								e.exit();
							else
							{
								_owner.getStatsRecorder().unblock();
								return false;
							}
						}
					}
				}
				else
				{
					// Проверяем, нужно ли накладывать эффект, при совпадении StackType.
					// Новый эффект накладывается только в том случае, если у него больше StackOrder и больше длительность.
					// Если условия подходят - удаляем старый.
					for(Effect e : _effects)
					{
						if(e.checkBlockedAbnormalType(abnormalType))
						{
							_owner.getStatsRecorder().unblock();
							return false;
						}

						if(effect.checkBlockedAbnormalType(e.getAbnormalType()))
						{
							e.exit();
							continue;
						}

						if(e.getEffector() != effect.getEffector() && effect.getAbnormalType().isStackable())
							continue;

						if(!checkAbnormalType(e.getTemplate(), effect.getTemplate()))
							continue;

						if(e.getSkill() == effect.getSkill() && e.getIndex() != effect.getIndex())
							break;

						// Эффекты со StackOrder == -1 заменить нельзя (например, Root).
						if(e.getAbnormalLvl() == -1)
						{
							_owner.getStatsRecorder().unblock();
							return false;
						}

						if(!e.maybeScheduleNext(effect))
						{
							_owner.getStatsRecorder().unblock();
							return false;
						}
					}
				}

				// Проверяем на лимиты бафов/дебафов
				checkSlotLimit(effect);
			}

			if(_effects.add(effect))
			{
				effect.start(); // Запускаем эффект
				success = true;
			}
		/*}
		finally
		{
			_lock.unlock();
		}*/

		//TODO [G1ta0] затычка на статы повышающие HP/MP/CP
		for(FuncTemplate ft : effect.getTemplate().getAttachedFuncs())
		{
			if(ft._stat == Stats.MAX_HP)
				_owner.setCurrentHp(hp, false);
			else if(ft._stat == Stats.MAX_MP)
				_owner.setCurrentMp(mp);
			else if(ft._stat == Stats.MAX_CP)
				_owner.setCurrentCp(cp);
		}

		_owner.getStatsRecorder().unblock();

		// Обновляем иконки
		_owner.updateStats();
		_owner.updateEffectIcons();

		return success;
	}

	/**
	 * Удаление эффекта из списка
	 *
	 * @param effect эффект для удаления
	 */
	public void removeEffect(Effect effect)
	{
		if(effect == null)
			return;

		if(_effects.remove(effect))
		{
			_owner.updateStats();
			_owner.updateEffectIcons();
		}
	}

	public int stopAllEffects()
	{
		if(_effects.isEmpty())
			return 0;

		int removed = 0;
		for(Effect e : _effects)
		{
			if(_owner.isSpecialEffect(e.getSkill()))
				continue;

			e.exit();
			removed++;
		}

		if(removed > 0)
		{
			_owner.updateStats();
			_owner.updateEffectIcons();
		}

		return removed;
	}

	public int stopEffects(int skillId)
	{
		if(_effects.isEmpty())
			return 0;

		int removed = 0;
		for(Effect e : _effects)
		{
			if(e.getSkill().getId() == skillId)
			{
				e.exit();
				removed++;
			}
		}

		if(removed > 0)
		{
			_owner.updateStats();
			_owner.updateEffectIcons();
		}

		return removed;
	}

	public int stopEffects(Skill skill)
	{
		if(skill == null)
			return 0;

		return stopEffects(skill.getId());
	}

	/**
	 * Находит скиллы с указанным эффектом, и останавливает у этих скиллов все эффекты (не только указанный).
	 */
	public int stopEffects(EffectType type)
	{
		return stopEffects(type, null);
	}

	public int stopEffects(EffectType type, Skill ignoreSkill)
	{
		if(_effects.isEmpty())
			return 0;

		TIntSet skillIds = new TIntHashSet();
		for(Effect e : _effects)
		{
			if((ignoreSkill == null || e.getSkill() != ignoreSkill) && e.getEffectType() == type)
				skillIds.add(e.getSkill().getId());
		}

		int removed = 0;
		for(Effect e : _effects)
		{
			if(skillIds.contains(e.getSkill().getId()))
			{
				e.exit();
				removed++;
			}
		}

		if(removed > 0)
		{
			_owner.updateStats();
			_owner.updateEffectIcons();
		}

		return removed;
	}

	/*public final void writeLock()
	{
		_writeLock.lock();
	}

	public final void writeUnlock()
	{
		_writeLock.unlock();
	}

	public final void readLock()
	{
		_readLock.lock();
	}

	public final void readUnlock()
	{
		_readLock.unlock();
	}*/
}