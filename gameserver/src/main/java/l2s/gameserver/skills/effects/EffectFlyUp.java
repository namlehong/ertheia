package l2s.gameserver.skills.effects;

import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

//TODO: [Bonux] Пересмотреть.
public class EffectFlyUp extends Effect
{
	public EffectFlyUp(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	public void onStart()
	{
		super.onStart();

		getEffected().startFlyUp();

		getEffected().abortAttack(true, true);
		getEffected().abortCast(true, true);
		getEffected().stopMove();

		getEffected().getAI().notifyEvent(CtrlEvent.EVT_FLY_UP, getEffected());

		if(!getEffected().isSummon())
			getEffected().getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
	}

	public void onExit()
	{
		super.onExit();

		if(getEffected().isFlyUp())
		{
			getEffected().stopFlyUp();

			if(!getEffected().isPlayer())
				getEffected().getAI().notifyEvent(CtrlEvent.EVT_THINK);
		}
	}

	public boolean onActionTime()
	{
		return false;
	}
}