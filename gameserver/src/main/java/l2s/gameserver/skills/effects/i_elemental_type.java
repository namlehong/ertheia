package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.skills.EffectType;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public final class i_elemental_type extends Effect
{
	private boolean _isSaveable = true;

	public i_elemental_type(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		if(!getEffected().isDualCastEnable())
			getEffected().getEffectList().stopEffects(EffectType.i_elemental_type, getSkill());
		else if(getEffected().getEffectList().containsEffects(EffectType.i_elemental_type))
			_isSaveable = false;

		super.onStart();
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}

	@Override
	public boolean isSaveable()
	{
		if(!_isSaveable)
			return false;

		return super.isSaveable();
	}
}