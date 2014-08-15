package l2s.gameserver.skills.effects;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.List;

import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public final class i_dual_cast extends Effect
{
	private final TIntSet _elementalSkills;

	public i_dual_cast(Env env, EffectTemplate template)
	{
		super(env, template);
		_elementalSkills = new TIntHashSet(template.getParam().getIntegerArray("elemental_skills", new int[0]));
	}

	@Override
	public void onStart()
	{
		getEffected().setDualCastEnable(true);
		for(int skillId : _elementalSkills.toArray())
		{
			Skill skill = getEffected().getKnownSkill(skillId);
			if(skill == null)
				continue;

			skill.getEffects(getEffector(), getEffected(), false);
		}
		super.onStart();
	}

	@Override
	public void onExit()
	{
		getEffected().setDualCastEnable(false);

		int previousElementalSkill = 0;
		for(Effect e : getEffected().getEffectList().getEffects())
		{
			if(!_elementalSkills.contains(e.getSkill().getId()))
				continue;

			if(!e.isSaveable())
				continue;

			previousElementalSkill = e.getSkill().getId();
			break;
		}

		for(int skillId : _elementalSkills.toArray())
		{
			if(skillId != previousElementalSkill)
				getEffected().getEffectList().stopEffects(skillId);
		}

		super.onExit();
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}