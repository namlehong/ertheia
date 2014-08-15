package l2s.gameserver.skills.effects;

import l2s.gameserver.listener.actor.OnCurrentHpDamageListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.Skill;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * DOT effect with additional damahe heal gift: if effected player recieves damage, then effector and his pets will recieve HP heal with some percent.
 * Percent of absorbed heal is setted by stat damageHealToEffector.
 *
 * @author Yorie
 */
public class EffectDamageHealToEffectorAndPets extends EffectDamOverTime
{
	private class DamageListener implements OnCurrentHpDamageListener
	{
		@Override
		public void onCurrentHpDamage(Creature actor, double damage, Creature attacker, Skill skill)
		{
			int effectorCount = _effector.getPlayer().getServitors().length + 1;
			double forEachHeal = (damage * _effected.calcStat(Stats.DAMAGE_HEAL_TO_EFFECTOR, 0.) / 100) / effectorCount;
			double forEachHealMp = (damage * _effected.calcStat(Stats.DAMAGE_HEAL_MP_TO_EFFECTOR, 0.) / 100) / effectorCount;
			for(Servitor pet : _effector.getPlayer().getServitors())
			{
				if(forEachHeal > 0)
					pet.setCurrentHp(pet.getCurrentHp() + forEachHeal, false, true);
				
				if(forEachHealMp > 0)
					pet.setCurrentMp(pet.getCurrentMp() + forEachHealMp);
			}	
			if(forEachHeal > 0)
			{
				_effector.setCurrentHp(_effector.getCurrentHp() + forEachHeal, false, true);
				_effector.sendPacket(new SystemMessagePacket(SystemMsg.S1_HP_HAS_BEEN_RESTORED).addInteger(Math.round(forEachHeal)));
			}	
			if(forEachHealMp > 0)
			{
				_effector.setCurrentMp(_effector.getCurrentMp() + forEachHealMp);
				_effector.sendPacket(new SystemMessagePacket(SystemMsg.S1_MP_HAS_BEEN_RESTORED).addInteger(Math.round(forEachHealMp)));
			}
		}
	}

	private DamageListener damageListener;

	public EffectDamageHealToEffectorAndPets(Env env, EffectTemplate template)
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