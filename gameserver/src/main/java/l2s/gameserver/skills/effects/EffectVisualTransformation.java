package l2s.gameserver.skills.effects;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.skills.skillclasses.Transformation;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public final class EffectVisualTransformation extends Effect
{
	public EffectVisualTransformation(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if(getEffected().isTransformImmune())
			return false;

		if(getEffected().isInFlyingTransform())
			return false;

		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		super.onStart();

		getEffected().startMuted();
		getEffected().startAMuted();
		getEffected().startPMuted();
		getEffected().abortCast(true, true);
		getEffected().abortAttack(true, true);

		getEffected().setVisualTransform((int) calc());
	}

	@Override
	public void onExit()
	{
		super.onExit();

		getEffected().setVisualTransform(null);

		getEffected().stopMuted();
		getEffected().stopAMuted();
		getEffected().stopPMuted();
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}