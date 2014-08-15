package l2s.gameserver.skills.effects;

import java.util.Arrays;
import java.util.List;

import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.skills.AbnormalType;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public class i_dispel_by_slot extends Effect
{
	private final AbnormalType _abnormalType;
	private final int _maxAbnormalLvl;

	public i_dispel_by_slot(Env env, EffectTemplate template)
	{
		super(env, template);

		_abnormalType = template.getParam().getEnum("abnormal_type", AbnormalType.class);
		if(_abnormalType == AbnormalType.none)
			_maxAbnormalLvl = 0;
		else
			_maxAbnormalLvl = template.getParam().getInteger("max_abnormal_level", 0);
	}

	@Override
	public void onStart()
	{
		super.onStart();

		if(_maxAbnormalLvl == 0)
			return;

		for(Effect effect : getEffected().getEffectList().getEffects())
		{
			/*if(!effect.isCancelable())
				continue;*/

			Skill effectSkill = effect.getSkill();
			if(effectSkill == null)
				continue;

			if(effectSkill.isToggle())
				continue;

			if(effectSkill.isPassive())
				continue;

			/*if(getEffected().isSpecialEffect(effectSkill))
				continue;*/

			if(effect.getAbnormalType() != _abnormalType)
				continue;

			if(_maxAbnormalLvl != -1 && effect.getAbnormalLvl() > _maxAbnormalLvl)
				continue;

			effect.exit();
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