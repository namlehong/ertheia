package l2s.gameserver.skills.skillclasses;

import java.util.List;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.stats.Formulas;
import l2s.gameserver.stats.Formulas.AttackInfo;
import l2s.gameserver.templates.StatsSet;

public class MDam extends Skill
{
	public MDam(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		int sps = isSSPossible() ? (isMagic() ? activeChar.getChargedSpiritShot() : activeChar.getChargedSoulShot() ? 2 : 0) : 0;

		Creature realTarget;
		boolean reflected;

		for(Creature target : targets)
			if(target != null)
			{
				if(target.isDead())
					continue;

				reflected = target.checkReflectSkill(activeChar, this);
				realTarget = reflected ? activeChar : target;

				AttackInfo info = Formulas.calcMagicDam(activeChar, realTarget, this, sps);
				realTarget.reduceCurrentHp(info.damage, activeChar, this, true, true, false, true, false, false, true, true, info.crit, false, false, true);
				if(info.damage >= 1)
				{
					double lethalDmg = Formulas.calcLethalDamage(activeChar, realTarget, this);
					if(lethalDmg > 0)
						realTarget.reduceCurrentHp(lethalDmg, activeChar, this, true, true, false, false, false, false, false);
				}

				getEffects(activeChar, target, false, reflected);
			}

		if(isSuicideAttack())
			activeChar.doDie(null);
		else if(isSSPossible())
			activeChar.unChargeShots(isMagic());

		super.useSkill(activeChar, targets);
	}
}