package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;


public final class EffectHpToOne extends Effect
{
	public EffectHpToOne(Env env, EffectTemplate template)
	{
		super(env, template);
	}
		
	@Override
	public void onStart()
	{
		super.onStart();
		_effected.setCurrentHp(1, false);
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
