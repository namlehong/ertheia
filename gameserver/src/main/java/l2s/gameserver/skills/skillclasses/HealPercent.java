package l2s.gameserver.skills.skillclasses;

import java.util.List;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.templates.StatsSet;

public class HealPercent extends Skill
{
	private final boolean _ignoreHpEff;

	public HealPercent(StatsSet set)
	{
		super(set);
		_ignoreHpEff = set.getBool("ignoreHpEff", true);
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(activeChar.isPlayable() && target.isMonster())
			return false;
		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		for(Creature target : targets)
			if(target != null)
			{
				if(target.isHealBlocked())
					continue;

				getEffects(activeChar, target, false);

				double hp = _power * target.getMaxHp() / 100.;
				double newHp = hp * (!_ignoreHpEff ? target.calcStat(Stats.HEAL_EFFECTIVNESS, 100., activeChar, this) : 100.) / 100.;
				double addToHp = Math.max(0, Math.min(newHp, target.calcStat(Stats.HP_LIMIT, null, null) * target.getMaxHp() / 100. - target.getCurrentHp()));

				if(addToHp > 0)
					target.setCurrentHp(addToHp + target.getCurrentHp(), false);
				if(target.isPlayer())
					if(activeChar != target)
						target.sendPacket(new SystemMessagePacket(SystemMsg.S2_HP_HAS_BEEN_RESTORED_BY_C1).addName(activeChar).addInteger(Math.round(addToHp)));
					else
						activeChar.sendPacket(new SystemMessagePacket(SystemMsg.S1_HP_HAS_BEEN_RESTORED).addInteger(Math.round(addToHp)));
			}

		if(isSSPossible())
			activeChar.unChargeShots(isMagic());

		super.useSkill(activeChar, targets);
	}
}