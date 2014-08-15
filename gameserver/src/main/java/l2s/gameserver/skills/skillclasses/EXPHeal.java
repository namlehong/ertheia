package l2s.gameserver.skills.skillclasses;

import java.util.List;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.templates.StatsSet;

/**
 * @author cruel
 */
public class EXPHeal extends Skill
{
	private final int _percentPower;
	private final int _percentPowerMaxLvl;

	public EXPHeal(StatsSet set)
	{
		super(set);

		_percentPower = set.getInteger("percent_power", 0);
		_percentPowerMaxLvl = set.getInteger("percent_power_max_lvl", 0);
	}

	@Override
	public boolean checkCondition(final Creature activeChar, final Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(!activeChar.isPlayer())
			return false;

		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		if(!activeChar.isPlayer())
			return;
		
		for(Creature target : targets)
		{
			if(target != null && target.isPlayer())
			{
				Player player = target.getPlayer();

				long power = (long) _power;
				if(_percentPowerMaxLvl != 0 && player.getLevel() < _percentPowerMaxLvl)
					power = (long) (player.getExp() / 100. * _percentPower);

				player.addExpAndSp(power, 0);

				getEffects(activeChar, target, false);
			}
		}

		if(isSSPossible())
			activeChar.unChargeShots(isMagic());

		super.useSkill(activeChar, targets);
	}
}
