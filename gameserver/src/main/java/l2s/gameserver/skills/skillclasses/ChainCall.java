package l2s.gameserver.skills.skillclasses;

import java.util.List;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.SkillChain;
import l2s.gameserver.templates.StatsSet;

/**
 *
 * @author monithly
 */
public class ChainCall extends Skill
{
	public ChainCall(StatsSet set)
	{
		super(set);
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(!activeChar.isPlayer())
			return false;

		if(!activeChar.getPlayer().getSkillChainDetails().containsKey(getChainIndex()))
			return false;

		if(!activeChar.getPlayer().getSkillChainDetails().get(getChainIndex()).isActive())
			return false;

		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		final SkillChain sc = activeChar.getPlayer().getSkillChainDetails().get(getChainIndex());
		if(sc != null && sc.isActive())
		{
			activeChar.doCast(sc.getChainSkill(), sc.getTarget(), true);
			activeChar.getPlayer().removeChainDetail(getChainIndex());
			sc.getTarget().getEffectList().stopEffects(sc.getCastingSkill());
		}

		super.useSkill(activeChar, targets);
	}
}
