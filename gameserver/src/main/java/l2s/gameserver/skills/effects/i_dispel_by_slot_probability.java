package l2s.gameserver.skills.effects;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.skills.AbnormalType;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public class i_dispel_by_slot_probability extends Effect
{
	private final AbnormalType _abnormalType;
	private final int _dispelChance;

	public i_dispel_by_slot_probability(Env env, EffectTemplate template)
	{
		super(env, template);

		_abnormalType = template.getParam().getEnum("abnormal_type", AbnormalType.class);
		if(_abnormalType == AbnormalType.none)
			_dispelChance = 0;
		else
			_dispelChance = template.getParam().getInteger("dispel_chance", 100);
	}

	@Override
	public void onStart()
	{
		//TODO: [Bonux] Проверить и добавить резисты кансила.
		super.onStart();

		if(_dispelChance == 0)
			return;

		TIntSet dispelledSkillIds = new TIntHashSet();
		TIntSet notDispelledSkillIds = new TIntHashSet();

		for(Effect effect : getEffected().getEffectList().getEffects())
		{
			if(!effect.isCancelable())
				continue;

			Skill effectSkill = effect.getSkill();
			if(effectSkill == null)
				continue;

			if(notDispelledSkillIds.contains(effectSkill.getId()))
				continue;

			if(effectSkill.isToggle())
				continue;

			if(effectSkill.isPassive())
				continue;

			/*if(getEffected().isSpecialEffect(effectSkill))
				continue;*/

			if(effect.getAbnormalType() != _abnormalType)
				continue;

			if(dispelledSkillIds.contains(effectSkill.getId()) || Rnd.chance(_dispelChance))
			{
				effect.exit();
				dispelledSkillIds.add(effectSkill.getId());
			}
			else
				notDispelledSkillIds.add(effectSkill.getId());
		}
	}

	@Override
	public void onExit()
	{
		super.onExit();
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}