package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public class EffectMuteAttack extends Effect
{
	public EffectMuteAttack(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();

		if(!_effected.startAMuted())
		{
			_effected.abortCast(true, true);
			_effected.abortAttack(true, true);
		}
	}

	@Override
	public void onExit()
	{
		super.onExit();
		_effected.stopAMuted();
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}