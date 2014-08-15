package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.templates.skill.EffectTemplate;

public class EffectHealPercent extends Effect
{
	private final boolean _ignoreHpEff;

	public EffectHealPercent(Env env, EffectTemplate template)
	{
		super(env, template);
		_ignoreHpEff = template.getParam().getBool("ignoreHpEff", true);
	}

	@Override
	public boolean checkCondition()
	{
		if(getEffected().isHealBlocked())
			return false;
		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		super.onStart();

		if(getEffected().isHealBlocked())
			return;

		double hp = calc() * getEffected().getMaxHp() / 100.;
		double newHp = hp * (!_ignoreHpEff ? getEffected().calcStat(Stats.HEAL_EFFECTIVNESS, 100., getEffector(), getSkill()) : 100.) / 100.;
		double addToHp = Math.max(0, Math.min(newHp, getEffected().calcStat(Stats.HP_LIMIT, null, null) * getEffected().getMaxHp() / 100. - getEffected().getCurrentHp()));

		if(getEffected() != getEffector())
			getEffected().sendPacket(new SystemMessagePacket(SystemMsg.S2_HP_HAS_BEEN_RESTORED_BY_C1).addName(getEffector()).addInteger(Math.round(addToHp)));
		else
			getEffected().sendPacket(new SystemMessagePacket(SystemMsg.S1_HP_HAS_BEEN_RESTORED).addInteger(Math.round(addToHp)));

		if(addToHp > 0)
			getEffected().setCurrentHp(addToHp + getEffected().getCurrentHp(), false);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}