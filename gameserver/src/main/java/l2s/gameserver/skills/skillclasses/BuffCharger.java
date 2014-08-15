package l2s.gameserver.skills.skillclasses;

import java.util.List;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.templates.StatsSet;

public class BuffCharger extends Skill
{
	private int _target;

	public BuffCharger(StatsSet set)
	{
		super(set);
		_target = set.getInteger("targetBuff", 0);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		for(Creature target : targets)
		{
			int level = 0;
			for(Effect effect : target.getEffectList().getEffects())
			{
				if(effect.getSkill().getId() == _target)
				{
					level = effect.getSkill().getLevel();
					break;
				}
			}

			Skill next = SkillTable.getInstance().getInfo(_target, level + 1);
			if(next != null)
				next.getEffects(activeChar, target, false);
		}

		super.useSkill(activeChar, targets);
	}
}