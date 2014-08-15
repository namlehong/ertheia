package l2s.gameserver.skills.skillclasses;

import java.util.List;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.templates.StatsSet;

public class Toggle extends Skill
{
	public Toggle(StatsSet set)
	{
		super(set);

		_isSaveable = set.getBool("isSaveable", false);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		getEffects(activeChar, activeChar, false);

		super.useSkill(activeChar, targets);
	}
}
