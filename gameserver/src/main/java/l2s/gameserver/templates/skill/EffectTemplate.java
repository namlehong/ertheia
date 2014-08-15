package l2s.gameserver.templates.skill;

import java.util.Collection;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.model.actor.instances.creature.EffectList;
import l2s.gameserver.model.Skill;
import l2s.gameserver.skills.AbnormalEffect;
import l2s.gameserver.skills.AbnormalType;
import l2s.gameserver.skills.EffectType;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.StatTemplate;
import l2s.gameserver.stats.conditions.Condition;
import l2s.gameserver.templates.StatsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EffectTemplate extends StatTemplate
{
	private static final Logger _log = LoggerFactory.getLogger(EffectTemplate.class);

	public static final EffectTemplate[] EMPTY_ARRAY = new EffectTemplate[0];

	private final int _index;

	private Condition _attachCond;
	public final double _value;
	public final int _count;
	public final long _period; // in milliseconds

	public final EffectType _effectType;

	public final int _displayId;
	public final int _displayLevel;

	private final AbnormalType _abnormalType;
	private final int _abnormalLvl;
	private final AbnormalEffect[] _abnormalEffects;

	public final boolean _cancelOnAction;
	public final boolean _isReflectable;
	public final boolean _isUnlimPeriod;
	private final boolean _hideTime;

	private final StatsSet _paramSet;
	private final int _chance;
	private final boolean _hasCombo;

	private final boolean _isSelf;

	public EffectTemplate(StatsSet set, Skill skill, boolean self)
	{
		_index = (self ? 100 * skill.getSelfEffectsCount() : skill.getEffectsCount()) + 1;
		_value = set.getDouble("value", 0D);
		_count = set.getInteger("count", 1) < 0 ? Integer.MAX_VALUE : set.getInteger("count", 1);
		_period = Math.min(Integer.MAX_VALUE, Math.max(-1, 1000 * (set.getInteger("time", skill.getAbnormalTime()))));
		_isUnlimPeriod = _period < 0;

		/*long time1 = _count * _period;
		long time2 = 1000L * skill.getAbnormalTime();
		if(!_isUnlimPeriod && _period > 0 && _count > 0 && time1 != time2)
			_log.warn(getClass().getSimpleName() + ": Effect has incorrect time or count (Skill ID: " + skill.getId() + ", LVL: " + skill.getLevel() + ")");*/

		_abnormalType = skill.getAbnormalType();
		_abnormalLvl = skill.getAbnormalLvl();
		_abnormalEffects = skill.getAbnormalEffects();

		_cancelOnAction = set.getBool("cancelOnAction", false);
		_isReflectable = set.getBool("isReflectable", true);
		_displayId = set.getInteger("displayId", 0);
		_displayLevel = set.getInteger("displayLevel", 0);
		_effectType = set.getEnum("name", EffectType.class, EffectType.Buff);
		_chance = set.getInteger("chance", -1);
		_hideTime = set.getBool("hide_time", false);
		_hasCombo = set.getBool("hasCombo", false);
		_paramSet = set;

		_isSelf = self;
	}

	public int getIndex()
	{
		return _index;
	}

	public Effect getEffect(Env env)
	{
		if(_attachCond != null && !_attachCond.test(env))
			return null;
		try
		{
			return _effectType.makeEffect(env, this);
		}
		catch(Exception e)
		{
			_log.error("", e);
		}

		return null;
	}

	public void attachCond(Condition c)
	{
		_attachCond = c;
	}

	public int getCount()
	{
		return _count;
	}

	public long getPeriod()
	{
		return _period;
	}

	public EffectType getEffectType()
	{
		return _effectType;
	}

	public Effect getSameByAbnormalType(Collection<Effect> list)
	{
		for(Effect ef : list)
			if(ef != null && EffectList.checkAbnormalType(ef.getTemplate(), this))
				return ef;
		return null;
	}

	public Effect getSameByAbnormalType(EffectList list)
	{
		return getSameByAbnormalType(list.getEffects());
	}

	public Effect getSameByAbnormalType(Creature actor)
	{
		return getSameByAbnormalType(actor.getEffectList().getEffects());
	}

	public StatsSet getParam()
	{
		return _paramSet;
	}

	public int getChance()
	{
		return _chance;
	}

	public boolean isUnlimPeriod()
	{
		return _isUnlimPeriod;
	}

	public boolean isHideTime()
	{
		return _hideTime || _isUnlimPeriod;
	}

	public AbnormalType getAbnormalType()
	{
		return _abnormalType;
	}

	public int getAbnormalLvl()
	{
		return _abnormalLvl;
	}

	public AbnormalEffect[] getAbnormalEffects()
	{
		return _abnormalEffects;
	}

	public boolean hasCombo()
	{
		return _hasCombo;
	}

	public boolean isSingle()
	{
		return _period == 0;
	}

	public final boolean isSelf()
	{
		return _isSelf;
	}
}