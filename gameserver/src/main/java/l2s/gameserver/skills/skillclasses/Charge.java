package l2s.gameserver.skills.skillclasses;

import java.util.List;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
import l2s.gameserver.stats.Formulas;
import l2s.gameserver.stats.Formulas.AttackInfo;
import l2s.gameserver.templates.StatsSet;

public class Charge extends Skill
{
	public static final int MAX_CHARGE = 10;

	private int _charges;
	private boolean _fullCharge;

	public Charge(StatsSet set)
	{
		super(set);
		_charges = set.getInteger("charges", getLevel());
		_fullCharge = set.getBool("fullCharge", false);
	}

	@Override
	public boolean checkCondition(final Creature activeChar, final Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(!activeChar.isPlayer())
			return false;

		Player player = (Player) activeChar;
		
		if(_charges == 10 && player.getSkillLevel(10301) != -1) //maybe something more practical? right now overrides any level of force before that.
			_charges = 15;
		
		//Камушки можно юзать даже если заряд > 7, остальное только если заряд < уровень скила
		if(getPower() <= 0 && getId() != 2165 && player.getIncreasedForce() >= _charges)
		{
			activeChar.sendPacket(SystemMsg.YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY_);
			return false;
		}
		else if(getId() == 2165)
			player.sendPacket(new MagicSkillUse(player, player, 2165, 1, 0, 0));

		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
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
		{
			if(target.isDead())
				continue;

			if(target != activeChar)
			{
				reflected = target.checkReflectSkill(activeChar, this);
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

				getEffects(activeChar, target, false, reflected);
			}
			else
				getEffects(activeChar, target, false);
		}

		chargePlayer((Player) activeChar, getId());

		super.useSkill(activeChar, targets);
	}

	public void chargePlayer(Player player, Integer skillId)
	{
		if(player.getIncreasedForce() >= _charges)
		{
			player.sendPacket(SystemMsg.YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY_);
			return;
		}
		if(_fullCharge)
			player.setIncreasedForce(_charges);
		else
			player.setIncreasedForce(player.getIncreasedForce() + 1);
	}
}