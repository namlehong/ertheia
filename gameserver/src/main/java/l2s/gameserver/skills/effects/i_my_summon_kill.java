package l2s.gameserver.skills.effects;

import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public final class i_my_summon_kill extends Effect
{
	public i_my_summon_kill(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();

		Servitor[] servitors = getEffected().getServitors();
		if(servitors.length > 0)
		{
			for(Servitor servitor : servitors)
			{
				if(servitor.isSummon())
					servitor.unSummon(false);
			}
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