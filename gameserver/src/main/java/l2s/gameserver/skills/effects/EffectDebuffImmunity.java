package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public final class EffectDebuffImmunity extends Effect
{
	private final int _maxDebuffsDisabled;

	private int _disabledDebuffs = 0;

	public EffectDebuffImmunity(Env env, EffectTemplate template)
	{
		super(env, template);
		_maxDebuffsDisabled = getTemplate().getParam().getInteger("max_disabled_debuffs", -1);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		getEffected().startDebuffImmunity();
	}

	@Override
	public void onExit()
	{
		super.onExit();
		getEffected().stopDebuffImmunity();
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}

	@Override
	public boolean checkDebuffImmunity()
	{
		if(_maxDebuffsDisabled > 0)
		{
			_disabledDebuffs++;

			if(getEffected().isPlayer() && getEffected().getPlayer().isGM())
				getEffected().sendMessage("DebuffImmunity: disabled_debuffs: " + _disabledDebuffs + " max_disabled_debuffs: " + _maxDebuffsDisabled);
			if(_disabledDebuffs >= _maxDebuffsDisabled)
			{
				getEffected().getEffectList().stopEffects(getSkill());
				if(getEffected().isPlayer() && getEffected().getPlayer().isGM())
					getEffected().sendMessage("DebuffImmunity: All disabled. Effect canceled.");
			}
		}
		return true;
	}
}