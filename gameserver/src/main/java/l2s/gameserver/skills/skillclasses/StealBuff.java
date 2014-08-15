package l2s.gameserver.skills.skillclasses;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.skills.AbnormalType;
import l2s.gameserver.skills.EffectType;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.templates.skill.EffectTemplate;
import l2s.gameserver.utils.EffectsComparator;

/**
 * @author pchayka, reworked by Bonux
 */
public class StealBuff extends Skill
{
	private final int _stealCount;

	public StealBuff(StatsSet set)
	{
		super(set);
		_stealCount = set.getInteger("stealCount", 1);
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(target == null || !target.isPlayer())
		{
			activeChar.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
			return false;
		}

		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		for(Creature target : targets)
		{
			if(target == null)
				continue;

			if(!target.isPlayer())
				continue;

			if(calcStealChance(target, activeChar))
			{
				int stealCount = Rnd.get(1, _stealCount); // ToCheck

				TIntSet stelledSkillIds = new TIntHashSet();

				List<Effect> effects = new ArrayList<Effect>(target.getEffectList().getEffects());
				Collections.sort(effects, EffectsComparator.getInstance()); // ToFix: Comparator to HF
				Collections.reverse(effects);
				for(Effect effect : effects)
				{
					if(effect.isOffensive())
						continue;
	
					if(effect.isSelf())
						continue;

					if(!effect.isCancelable())
						continue;

					Skill effectSkill = effect.getSkill();
					if(effectSkill == null)
						continue;

					if(!stelledSkillIds.contains(effectSkill.getId()) && stelledSkillIds.size() < stealCount)
						continue;

					if(effectSkill.isToggle())
						continue;

					if(effectSkill.isPassive())
						continue;

					if(target.isSpecialEffect(effectSkill))
						continue;

					stealEffect(activeChar, effect);
					effect.exit();

					stelledSkillIds.add(effectSkill.getId());
				}
			}
			else
			{
				activeChar.sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_RESISTED_YOUR_S2).addName(target).addSkillName(getId(), getLevel()));
				continue;
			}
			getEffects(activeChar, target, false);
		}

		if(isSSPossible())
			activeChar.unChargeShots(isMagic());

		super.useSkill(activeChar, targets);
	}

	private boolean calcStealChance(Creature effected, Creature effector) // TODO: [Bonux] Пересмотреть эту формулу.
	{
		double cancel_res_multiplier = effected.calcStat(Stats.CANCEL_RESIST, 1, null, null);
		int dml = effector.getLevel() - effected.getLevel(); // to check: magicLevel or player level? Since it's magic skill setting player level as default
		double prelimChance = (dml + 50) * (1 - cancel_res_multiplier * .01); // 50 is random reasonable constant which gives ~50% chance of steal success while else is equal
		return Rnd.chance(prelimChance);
	}

	private void stealEffect(Creature character, Effect effect)
	{
		Effect e = effect.getTemplate().getEffect(new Env(character, character, effect.getSkill()));
		if(e == null)
			return;

		e.setCount(effect.getCount());
		e.setPeriod(effect.getCount() == 1 ? effect.getPeriod() - effect.getTime() : effect.getPeriod());

		character.getEffectList().addEffect(e);
	}
}