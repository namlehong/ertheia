package l2s.gameserver.skills.skillclasses;

import java.util.List;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.Skill;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.stats.Formulas;
import l2s.gameserver.templates.StatsSet;

public class DestroySummon extends Skill
{
	public DestroySummon(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		for(Creature target : targets)
			if(target != null)
			{

				if(getActivateRate() > 0 && !Formulas.calcSkillSuccess(activeChar, target, this, getActivateRate()))
				{
					activeChar.sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_RESISTED_YOUR_S2).addName(target).addSkillName(getId(), getLevel()));
					continue;
				}

				if(target.isSummon())
				{
					((Servitor) target).unSummon(false);
					getEffects(activeChar, target, false);
				}
			}

		if(isSSPossible())
			activeChar.unChargeShots(isMagic());

		super.useSkill(activeChar, targets);
	}
}