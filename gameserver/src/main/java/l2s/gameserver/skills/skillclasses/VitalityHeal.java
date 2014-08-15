package l2s.gameserver.skills.skillclasses;

import java.util.List;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.templates.StatsSet;

public class VitalityHeal extends Skill
{
	public VitalityHeal(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		double percent = _power;

		for(Creature target : targets)
		{
			if(target.isPlayer())
			{
				Player player = target.getPlayer();
				int points = (int) (Player.MAX_VITALITY_POINTS / 100 * percent);
				player.setVitality(player.getVitality() + points);
			}
			getEffects(activeChar, target, false);
		}

		if(isSSPossible())
			activeChar.unChargeShots(isMagic());

		super.useSkill(activeChar, targets);
	}
}