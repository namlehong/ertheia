package l2s.gameserver.skills.skillclasses;

import java.util.List;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.templates.StatsSet;

public class ManaHealPercent extends Skill
{
	private final boolean _ignoreMpEff;

	public ManaHealPercent(StatsSet set)
	{
		super(set);
		_ignoreMpEff = set.getBool("ignoreMpEff", true);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		for(Creature target : targets)
			if(target != null)
			{
				if(target.isDead() || target.isHealBlocked())
					continue;

				getEffects(activeChar, target, false);

				double mp = _power * target.getMaxMp() / 100.;
				double newMp = mp * (!_ignoreMpEff ? target.calcStat(Stats.MANAHEAL_EFFECTIVNESS, 100., activeChar, this) : 100.) / 100.;
				double addToMp = Math.max(0, Math.min(newMp, target.calcStat(Stats.MP_LIMIT, null, null) * target.getMaxMp() / 100. - target.getCurrentMp()));

				if(addToMp > 0)
					target.setCurrentMp(target.getCurrentMp() + addToMp);
				if(target.isPlayer())
					if(activeChar != target)
						target.sendPacket(new SystemMessagePacket(SystemMsg.S2_MP_HAS_BEEN_RESTORED_BY_C1).addString(activeChar.getName()).addInteger(Math.round(addToMp)));
					else
						activeChar.sendPacket(new SystemMessagePacket(SystemMsg.S1_MP_HAS_BEEN_RESTORED).addInteger(Math.round(addToMp)));
			}

		if(isSSPossible())
			activeChar.unChargeShots(isMagic());

		super.useSkill(activeChar, targets);
	}
}