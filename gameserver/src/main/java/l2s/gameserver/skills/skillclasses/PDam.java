package l2s.gameserver.skills.skillclasses;

import java.util.List;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.FinishRotatingPacket;
import l2s.gameserver.network.l2.s2c.StartRotatingPacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.stats.Formulas;
import l2s.gameserver.stats.Formulas.AttackInfo;
import l2s.gameserver.templates.StatsSet;

public class PDam extends Skill
{
	private final boolean _onCrit;
	private final boolean _directHp;
	private final boolean _turner;
	private final boolean _blow;

	public PDam(StatsSet set)
	{
		super(set);
		_onCrit = set.getBool("onCrit", false);
		_directHp = set.getBool("directHp", false);
		_turner = set.getBool("turner", false);
		_blow = set.getBool("blow", false);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		boolean ss = activeChar.getChargedSoulShot() && isSSPossible();

		Creature realTarget;
		boolean reflected;

		for(Creature target : targets)
			if(target != null && !target.isDead())
			{
				if(_turner && !target.isInvul())
				{
					target.broadcastPacket(new StartRotatingPacket(target, target.getHeading(), 1, 65535));
					target.broadcastPacket(new FinishRotatingPacket(target, activeChar.getHeading(), 65535));
					target.setHeading(activeChar.getHeading());
					target.sendPacket(new SystemMessagePacket(SystemMsg.S1S_EFFECT_CAN_BE_FELT).addSkillName(_displayId, _displayLevel));
				}

				reflected = target.checkReflectSkill(activeChar, this);
				realTarget = reflected ? activeChar : target;

				AttackInfo info = Formulas.calcPhysDam(activeChar, realTarget, this, false, _blow, ss, _onCrit);
				realTarget.reduceCurrentHp(info.damage, activeChar, this, true, true, _directHp, true, false, false, getPower() != 0, true, info.crit || info.blow, false, false, false);
				if(!info.miss || info.damage >= 1)
				{
					double lethalDmg = Formulas.calcLethalDamage(activeChar, realTarget, this);
					if(lethalDmg > 0)
						realTarget.reduceCurrentHp(lethalDmg, activeChar, this, true, true, false, false, false, false, false);
					else if(!reflected)
						realTarget.doCounterAttack(this, activeChar, _blow);
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