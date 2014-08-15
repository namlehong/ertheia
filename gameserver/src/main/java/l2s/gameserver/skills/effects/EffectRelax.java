package l2s.gameserver.skills.effects;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public class EffectRelax extends Effect
{
	private boolean _isWereSitting;

	public EffectRelax(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public boolean checkCondition()
	{
		Player player = _effected.getPlayer();
		if(player == null)
			return false;
		if(player.isMounted())
		{
			player.sendPacket(new SystemMessagePacket(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(_skill.getId(), _skill.getLevel()));
			return false;
		}
		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		super.onStart();
		Player player = _effected.getPlayer();
		if(player.isMoving)
			player.stopMove();
		_isWereSitting = player.isSitting();
		player.sitDown(null);
	}

	@Override
	public void onExit()
	{
		super.onExit();
		if(!_isWereSitting)
			_effected.getPlayer().standUp();
	}

	@Override
	public boolean onActionTime()
	{
		Player player = _effected.getPlayer();
		if(player.isAlikeDead() || player == null)
			return false;

		if(!player.isSitting())
			return false;

		if(player.isCurrentHpFull() && getSkill().isToggle())
		{
			getEffected().sendPacket(SystemMsg.THAT_SKILL_HAS_BEEN_DEACTIVATED_AS_HP_WAS_FULLY_RECOVERED);
			return false;
		}

		double manaDam = calc();
		if(manaDam > _effected.getCurrentMp())
			if(getSkill().isToggle())
			{
				player.sendPacket(SystemMsg.NOT_ENOUGH_MP, new SystemMessagePacket(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(getSkill().getId(), getSkill().getDisplayLevel()));
				return false;
			}

		_effected.reduceCurrentMp(manaDam, null);

		return true;
	}
}