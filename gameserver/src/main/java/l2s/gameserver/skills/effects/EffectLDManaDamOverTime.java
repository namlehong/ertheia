package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public class EffectLDManaDamOverTime extends Effect
{
	public EffectLDManaDamOverTime(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public boolean onActionTime()
	{
		if(_effected.isDead())
			return false;

		double manaDam = calc();
		manaDam *= _effected.getLevel() / 2.4;

		if(manaDam > _effected.getCurrentMp() && getSkill().isToggle())
		{
			_effected.sendPacket(SystemMsg.NOT_ENOUGH_MP);
			_effected.sendPacket(new SystemMessagePacket(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(getSkill().getId(), getSkill().getDisplayLevel()));
			return false;
		}

		_effected.reduceCurrentMp(manaDam, null);
		return true;
	}
}