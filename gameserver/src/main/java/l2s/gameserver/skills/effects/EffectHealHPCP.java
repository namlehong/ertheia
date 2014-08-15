package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExRegenMaxPacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.templates.skill.EffectTemplate;

public class EffectHealHPCP extends Effect
{
	public EffectHealHPCP(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();

		if(getEffected().isPlayer() && getCount() > 0 && getPeriod() > 0)
			getEffected().sendPacket(new ExRegenMaxPacket(calc(), (int) (getCount() * getPeriod() / 1000), Math.round(getPeriod() / 1000)));

		/**
		 switch(getSkill().getId().intValue())
		 {
		 case 2031: // Lesser Healing Potion
		 getEffected().sendPacket(new ExRegenMaxPacket(ExRegenMaxPacket.POTION_HEALING_LESSER));
		 break;
		 case 2032: // Healing Potion
		 getEffected().sendPacket(new ExRegenMaxPacket(ExRegenMaxPacket.POTION_HEALING_MEDIUM));
		 break;
		 case 2037: // Greater Healing Potion
		 getEffected().sendPacket(new ExRegenMaxPacket(ExRegenMaxPacket.POTION_HEALING_GREATER));
		 break;
		 }
		 */
	}

	@Override
	public boolean onActionTime()
	{
		if(getEffected().isDead() || getEffected().isHealBlocked())
			return false;

		double newHp = calc() * getEffected().calcStat(Stats.HEAL_EFFECTIVNESS, 100, getEffector(), getSkill()) / 100;
		double addToHp = Math.max(0, Math.min(newHp, getEffected().calcStat(Stats.HP_LIMIT, getEffector(), getSkill()) * getEffected().getMaxHp() / 100. - getEffected().getCurrentHp()));

		if(getEffected() != getEffector())
			getEffected().sendPacket(new SystemMessagePacket(SystemMsg.S2_HP_HAS_BEEN_RESTORED_BY_C1).addName(getEffector()).addInteger(Math.round(addToHp)));
		else
			getEffected().sendPacket(new SystemMessagePacket(SystemMsg.S1_HP_HAS_BEEN_RESTORED).addInteger(Math.round(addToHp)));

		if(addToHp > 0)
			getEffected().setCurrentHp(addToHp + getEffected().getCurrentHp(), false);

		else
		{
			double newCp = calc() * getEffected().getMaxCp() / 100;
			double addToCp = Math.max(0, Math.min(newCp, getEffected().calcStat(Stats.CP_LIMIT, null, null) * getEffected().getMaxCp() / 100. - getEffected().getCurrentCp()));
			getEffected().sendPacket(new SystemMessagePacket(SystemMsg.S2_CP_HAS_BEEN_RESTORED_BY_C1).addLong((long) addToCp).addName(getEffector()));
			if(addToCp > 0)
				getEffected().setCurrentCp(addToCp + getEffected().getCurrentCp());
		}
		return true;
	}
}
