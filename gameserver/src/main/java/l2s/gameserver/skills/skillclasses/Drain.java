package l2s.gameserver.skills.skillclasses;

import java.util.List;

import l2s.commons.util.Rnd;

import l2s.gameserver.Config;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.stats.Formulas;
import l2s.gameserver.stats.Formulas.AttackInfo;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.templates.StatsSet;

public class Drain extends Skill
{
	private double _absorbAbs;

	public Drain(StatsSet set)
	{
		super(set);
		_absorbAbs = set.getDouble("absorbAbs", 0.f);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		int sps = isSSPossible() ? activeChar.getChargedSpiritShot() : 0;
		boolean ss = isSSPossible() && activeChar.getChargedSoulShot();
		Creature realTarget;
		boolean reflected;
		final boolean corpseSkill = _targetType == SkillTargetType.TARGET_CORPSE;

		for(Creature target : targets)
			if(target != null && canAbsorb(target, activeChar))
			{
				reflected = !corpseSkill && target.checkReflectSkill(activeChar, this);
				realTarget = reflected ? activeChar : target;

				if(getPower() > 0 || _absorbAbs > 0) // Если == 0 значит скилл "отключен"
				{
					if(realTarget.isDead() && !corpseSkill)
						continue;

					double hp = 0.;
					double targetHp = realTarget.getCurrentHp();

					if(!corpseSkill)
					{
						double damage;
						if(isMagic())
						{
							AttackInfo info = Formulas.calcMagicDam(activeChar, realTarget, this, sps);
							realTarget.reduceCurrentHp(info.damage, activeChar, this, true, true, false, true, false, false, true, true, info.crit, false, false, true);
							if(info.damage >= 1)
							{
								double lethalDmg = Formulas.calcLethalDamage(activeChar, realTarget, this);
								if(lethalDmg > 0)
									realTarget.reduceCurrentHp(lethalDmg, activeChar, this, true, true, false, false, false, false, false);
							}

							damage = info.damage;
						}
						else
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

							damage = info.damage;
						}

						double targetCP = realTarget.getCurrentCp();

						// Нельзя восстанавливать HP из CP
						if(damage > targetCP || !realTarget.isPlayer())
							hp = (damage - targetCP) * _absorbPart;
					}

					if(_absorbAbs == 0 && _absorbPart == 0)
						continue;

					hp += _absorbAbs;

					// Нельзя восстановить больше hp, чем есть у цели.
					if(hp > targetHp && !corpseSkill)
						hp = targetHp;

					double addToHp = Math.max(0, Math.min(hp, activeChar.calcStat(Stats.HP_LIMIT, null, null) * activeChar.getMaxHp() / 100. - activeChar.getCurrentHp()));

					if(addToHp > 0 && !activeChar.isHealBlocked() && Rnd.chance(Config.ALT_VAMPIRIC_CHANCE))
						activeChar.setCurrentHp(activeChar.getCurrentHp() + addToHp, false);

					if(realTarget.isDead() && corpseSkill && realTarget.isNpc())
					{
						activeChar.getAI().setAttackTarget(null);
						((NpcInstance) realTarget).endDecayTask();
					}
				}

				getEffects(activeChar, target, false, reflected);
			}

		if(isMagic() ? sps != 0 : ss)
			activeChar.unChargeShots(isMagic());

		super.useSkill(activeChar, targets);
	}
	
	private boolean canAbsorb(Creature attacked, Creature attacker)
	{
		if(attacked.isPlayable() || !Config.DISABLE_VAMPIRIC_VS_MOB_ON_PVP)
			return true;
		return attacker.getPvpFlag() == 0;		
	}	
}