package l2s.gameserver.skills.skillclasses;

import java.util.List;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.stats.Formulas;
import l2s.gameserver.stats.Formulas.AttackInfo;
import l2s.gameserver.templates.StatsSet;

public class ChargeSoul extends Skill
{
	private int _numSouls;

	public ChargeSoul(StatsSet set)
	{
		super(set);
		_numSouls = set.getInteger("numSouls", getLevel());
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		if(!activeChar.isPlayer())
			return;

		boolean ss = activeChar.getChargedSoulShot() && isSSPossible();
		if(ss && getTargetType() != SkillTargetType.TARGET_SELF)
			activeChar.unChargeShots(false);

		Creature realTarget;
		boolean reflected;

		for(Creature target : targets)
			if(target != null)
			{
				if(target.isDead())
					continue;

				reflected = target != activeChar && target.checkReflectSkill(activeChar, this);
				realTarget = reflected ? activeChar : target;

				if(getPower() > 0) // Если == 0 значит скилл "отключен"
				{
					AttackInfo info = Formulas.calcPhysDam(activeChar, realTarget, this, false, false, ss, false);
					realTarget.reduceCurrentHp(info.damage, activeChar, this, true, true, false, true, false, false, true, true, info.crit || info.blow, false, false, false);
					if(!info.miss || info.damage >= 1)
					{
						double lethalDmg = Formulas.calcLethalDamage(activeChar, realTarget, this);
						if(lethalDmg > 0)
							realTarget.reduceCurrentHp(lethalDmg, activeChar, this, true, true, false, false, false, false, false);
						else if(!reflected)
							realTarget.doCounterAttack(this, activeChar, false);
					}
				}

				if(realTarget.isPlayable() || realTarget.isMonster())
					activeChar.setConsumedSouls(activeChar.getConsumedSouls() + _numSouls, null);

				getEffects(activeChar, target, false, reflected);
			}

		if(isSSPossible())
			activeChar.unChargeShots(isMagic());

		super.useSkill(activeChar, targets);
	}
}