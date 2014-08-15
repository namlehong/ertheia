package l2s.gameserver.skills.effects;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.skills.EffectType;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.Formulas;
import l2s.gameserver.stats.Formulas.AttackInfo;
import l2s.gameserver.templates.skill.EffectTemplate;

public class EffectNegateMark extends Effect
{
	private static final int MARK_OF_WEAKNESS = 11259;
	private static final int MARK_OF_PLAGUE = 11261;
	private static final int MARK_OF_TRICK = 11262;

	public EffectNegateMark(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		Creature effected = getEffected();
		Creature effector = getEffector();
		Skill skill = getSkill();

		byte markCount = 0;

		for(Effect effect : effected.getEffectList().getEffects())
		{
			if(effect.getEffectType() == EffectType.DamOverTime)
			{
				int skillId = effect.getSkill().getId();
				if((skillId == 11259) || (skillId == 11261) || (skillId == 11262))
				{
					markCount = (byte) (markCount + 1);
					effected.getEffectList().stopEffects(skillId);
				}
			}
		}

		int ss = 0;

		ItemInstance weaponInst = effector.getActiveWeaponInstance();
		if(weaponInst != null)
		{
			switch(weaponInst.getChargedSpiritshot())
			{
				case 2:
					weaponInst.setChargedSpiritshot(0);
					ss = 2;
					break;
				case 1:
					weaponInst.setChargedSpiritshot(0);
					ss = 1;
			}
		}

		if(markCount > 0)
		{
			AttackInfo info = Formulas.calcMagicDam(effector, effected, skill, ss);
			effected.reduceCurrentHp(info.damage * markCount, effector, skill, true, true, false, true, false, false, true, true, info.crit, false, false, true);
		}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
