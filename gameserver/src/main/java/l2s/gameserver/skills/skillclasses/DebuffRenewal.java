package l2s.gameserver.skills.skillclasses;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.List;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.skills.EffectType;
import l2s.gameserver.templates.StatsSet;

public class DebuffRenewal extends Skill
{
	public DebuffRenewal(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		for(Creature target : targets)
		{
			if(target != null)
			{
				// Renew paralyze
				renewEffect(target, EffectType.Paralyze);
				// Renew root
				renewEffect(target, EffectType.Root);
				// Renew silence
				renewEffect(target, EffectType.Mute);
				renewEffect(target, EffectType.MuteAll);
				renewEffect(target, EffectType.MuteAttack);
				renewEffect(target, EffectType.MutePhisycal);
				// Renew sleep
				renewEffect(target, EffectType.Sleep);
				// Renew stun
				renewEffect(target, EffectType.Stun);
				// Renew fear
				renewEffect(target, EffectType.Fear);
				// Renew paralyzations like Dance of Medusa
				renewEffect(target, EffectType.Petrification);
				// Renew disarm
				renewEffect(target, EffectType.Disarm);
				// Renew mutation effects (new debuff transformations)
				renewEffect(target, EffectType.Mutation);
				//basic debuffs
				renewEffect(target, EffectType.Debuff);

				getEffects(activeChar, target, getActivateRate() > 0, false);
				target.updateEffectIcons();
			}
		}

		if(isSSPossible())
			activeChar.unChargeShots(isMagic());

		super.useSkill(activeChar, targets);
	}

	private void renewEffect(Creature target, EffectType type)
	{
		TIntSet skillsToRefresh = new TIntHashSet();

		for(Effect effect : target.getEffectList().getEffects())
		{
			if(effect.getEffectType() == type)
				skillsToRefresh.add(effect.getSkill().getId());
		}

		for(Effect effect : target.getEffectList().getEffects())
		{
			if(skillsToRefresh.contains(effect.getSkill().getId()))
				effect.restart();
		}
	}
}