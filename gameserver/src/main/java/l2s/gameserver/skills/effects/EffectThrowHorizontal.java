package l2s.gameserver.skills.effects;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.events.impl.SiegeEvent;
import l2s.gameserver.model.instances.SummonInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.FlyToLocationPacket;
import l2s.gameserver.network.l2.s2c.FlyToLocationPacket.FlyType;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;
import l2s.gameserver.utils.Location;

/**
 * @author Bonux
**/
public class EffectThrowHorizontal extends EffectFlyAbstract
{
	private Location _flyLoc = null;

	public EffectThrowHorizontal(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if(getEffected().isThrowAndKnockImmune())
		{
			getEffected().sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
			return false;
		}

		// Тычок/отброс нельзя наложить на осадных саммонов
		Player player = getEffected().getPlayer();
		if(player != null)
		{
			SiegeEvent<?, ?> siegeEvent = player.getEvent(SiegeEvent.class);
			if(getEffected().isSummon() && siegeEvent != null && siegeEvent.containsSiegeSummon((SummonInstance) getEffected()))
			{
				getEffector().sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
				return false;
			}
		}

		if(getEffected().isInZonePeace())
		{
			getEffector().sendPacket(SystemMsg.YOU_MAY_NOT_ATTACK_IN_A_PEACEFUL_ZONE);
			return false;
		}

		_flyLoc = getEffected().getFlyLocation(_effector, getSkill());
		if(_flyLoc == null)
			return false;

		return true;
	}

	@Override
	public void onStart()
	{
		getEffected().abortAttack(true, true);
		getEffected().abortCast(true, true);
		getEffected().stopMove();
		getEffected().block();
		getEffected().broadcastPacket(new FlyToLocationPacket(getEffected(), _flyLoc, FlyType.THROW_HORIZONTAL, getFlySpeed(), getFlyDelay(), getFlyAnimationSpeed()));
		getEffected().setLoc(_flyLoc);
		getEffected().validateLocation(1);

		super.onStart();
	}

	@Override
	public void onExit()
	{
		getEffected().unblock();

		super.onExit();
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}