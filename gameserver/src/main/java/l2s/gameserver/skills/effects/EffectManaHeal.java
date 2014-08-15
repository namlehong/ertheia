package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.templates.skill.EffectTemplate;

public class EffectManaHeal extends Effect
{
	private final boolean _ignoreMpEff;

	public EffectManaHeal(Env env, EffectTemplate template)
	{
		super(env, template);
		_ignoreMpEff = template.getParam().getBool("ignoreMpEff", false);
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

		double mp = calc();
		double newMp = Math.min(mp * 1.7, mp * (!_ignoreMpEff ? getEffected().calcStat(Stats.MANAHEAL_EFFECTIVNESS, 100., getEffector(), getSkill()) : 100.) / 100.);
		double addToMp = Math.max(0, Math.min(newMp, getEffected().calcStat(Stats.MP_LIMIT, null, null) * getEffected().getMaxMp() / 100. - getEffected().getCurrentMp()));

		if(getEffector() != getEffected())
			getEffected().sendPacket(new SystemMessagePacket(SystemMsg.S2_MP_HAS_BEEN_RESTORED_BY_C1).addName(getEffector()).addInteger(Math.round(addToMp)));
		else
			getEffected().sendPacket(new SystemMessagePacket(SystemMsg.S1_MP_HAS_BEEN_RESTORED).addInteger(Math.round(addToMp)));

		if(addToMp > 0)
			getEffected().setCurrentMp(addToMp + getEffected().getCurrentMp());
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}