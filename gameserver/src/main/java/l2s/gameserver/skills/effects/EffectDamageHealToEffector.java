package l2s.gameserver.skills.effects;

import l2s.gameserver.listener.actor.OnCurrentHpDamageListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * DOT effect with additional damahe heal gift: if effected player recieves damage, then effector will recieve HP heal with some percent.
 * Percent of absorbed heal is setted by stat damageHealToEffector.
 *
 * @author Yorie
 */
public class EffectDamageHealToEffector extends EffectDamOverTime
{
	private class DamageListener implements OnCurrentHpDamageListener
	{
		@Override
		public void onCurrentHpDamage(Creature actor, double damage, Creature attacker, Skill skill)
		{
			_effector.setCurrentHp(_effector.getCurrentHp() + (damage * _effected.calcStat(Stats.DAMAGE_HEAL_TO_EFFECTOR, 0.)) / 100, false, true);
		}
	}

	private DamageListener damageListener;

	public EffectDamageHealToEffector(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		_effected.addListener((damageListener = new DamageListener()));
		super.onStart();
	}

	@Override
	public boolean onActionTime()
	{
		if(_effected.isDead())
		{
			if(damageListener != null)
				_effected.removeListener(damageListener);
			return false;
		}

		return super.onActionTime();
	}

	@Override
	public void onExit()
	{
		if(damageListener != null)
			_effected.removeListener(damageListener);
		super.onExit();
	}
}