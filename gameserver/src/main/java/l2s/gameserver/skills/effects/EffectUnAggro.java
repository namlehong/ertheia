package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public class EffectUnAggro extends Effect
{
	public EffectUnAggro(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(_effected.isNpc())
			((NpcInstance) _effected).setUnAggred(true);
	}

	@Override
	public void onExit()
	{
		super.onExit();
		if(_effected.isNpc())
			((NpcInstance) _effected).setUnAggred(false);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}