package l2s.gameserver.model.base;

import l2s.gameserver.model.Skill.EnchantGrade;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.Stats;

public enum SkillTrait
{
	NONE,
	BLEED
	{
		@Override
		public final double calcVuln(Env env)
		{
			return env.target.calcStat(Stats.BLEED_RESIST, env.character, env.skill);
		}

		@Override
		public final double calcProf(Env env)
		{
			return env.character.calcStat(Stats.BLEED_POWER, env.target, env.skill);
		}
	},
	BOSS,
	DEATH,
	DERANGEMENT
	{
		@Override
		public final double calcVuln(Env env)
		{
			return env.target.calcStat(Stats.MENTAL_RESIST, env.character, env.skill);
		}

		@Override
		public final double calcProf(Env env)
		{
			return Math.min(40., env.character.calcStat(Stats.MENTAL_POWER, env.target, env.skill) + calcEnchantMod(env));
		}
	},
	ETC,
	GUST,
	HOLD
	{
		@Override
		public final double calcVuln(Env env)
		{
			return env.target.calcStat(Stats.ROOT_RESIST, env.character, env.skill);
		}

		@Override
		public final double calcProf(Env env)
		{
			return env.character.calcStat(Stats.ROOT_POWER, env.target, env.skill);
		}
	},
	PARALYZE
	{
		@Override
		public final double calcVuln(Env env)
		{
			return env.target.calcStat(Stats.PARALYZE_RESIST, env.character, env.skill);
		}

		@Override
		public final double calcProf(Env env)
		{
			return env.character.calcStat(Stats.PARALYZE_POWER, env.target, env.skill);
		}
	},
	PHYSICAL_BLOCKADE,
	AIRJOKE
	{
		@Override
		public final double calcVuln(Env env)
		{
			return env.target.calcStat(Stats.AIRJOKE_RESIST, env.character, env.skill);
		}
		
		@Override
		public final double calcProf(Env env)
		{
			return env.character.calcStat(Stats.AIRJOKE_POWER, env.target, env.skill);
		}
	},
	MUTATE
	{
		@Override
		public final double calcVuln(Env env)
		{
			return env.target.calcStat(Stats.MUTATE_RESIST, env.character, env.skill);
		}
		
		@Override
		public final double calcProf(Env env)
		{
			return env.character.calcStat(Stats.MUTATE_POWER, env.target, env.skill);
		}
	},
	DISARM
	{
		@Override
		public final double calcVuln(Env env)
		{
			return env.target.calcStat(Stats.DISARM_RESIST, env.character, env.skill);
		}
		
		@Override
		public final double calcProf(Env env)
		{
			return env.character.calcStat(Stats.DISARM_POWER, env.target, env.skill);
		}
	},
	PULL
	{
		@Override
		public final double calcVuln(Env env)
		{
			return env.target.calcStat(Stats.PULL_RESIST, env.character, env.skill);
		}
		
		@Override
		public final double calcProf(Env env)
		{
			return env.character.calcStat(Stats.PULL_POWER, env.target, env.skill);
		}
	},	
	POISON
	{
		@Override
		public final double calcVuln(Env env)
		{
			return env.target.calcStat(Stats.POISON_RESIST, env.character, env.skill);
		}

		@Override
		public final double calcProf(Env env)
		{
			return env.character.calcStat(Stats.POISON_POWER, env.target, env.skill);
		}
	},
	SHOCK
	{
		@Override
		public final double calcVuln(Env env)
		{
			return env.target.calcStat(Stats.STUN_RESIST, env.character, env.skill);
		}

		@Override
		public final double calcProf(Env env)
		{
			return Math.min(40., env.character.calcStat(Stats.STUN_POWER, env.target, env.skill) + calcEnchantMod(env));
		}
	},
	SLEEP
	{
		@Override
		public final double calcVuln(Env env)
		{
			return env.target.calcStat(Stats.SLEEP_RESIST, env.character, env.skill);
		}

		@Override
		public final double calcProf(Env env)
		{
			return env.character.calcStat(Stats.SLEEP_POWER, env.target, env.skill);
		}
	},
	VALAKAS,
	KNOCKBACK
	{
		@Override
		public final double calcVuln(Env env)
		{
			return env.target.calcStat(Stats.KNOCKBACK_RESIST, env.character, env.skill);
		}
		
		@Override
		public final double calcProf(Env env)
		{
			return env.character.calcStat(Stats.KNOCKBACK_POWER, env.target, env.skill);
		}
	},
	/**
	 * Field KNOCKDOWN.
	 */
	KNOCKDOWN
	{
		@Override
		public final double calcVuln(Env env)
		{
			return env.target.calcStat(Stats.KNOCKDOWN_RESIST, env.character, env.skill);
		}
		
		@Override
		public final double calcProf(Env env)
		{
			return env.character.calcStat(Stats.KNOCKDOWN_POWER, env.target, env.skill);
		}
	};	

	public double calcVuln(Env env)
	{
		return 0;
	}

	public double calcProf(Env env)
	{
		return 0;
	}

	/*
		public double calcResistMod(Env env)
		{
			final double vulnMod = calcVuln(env);
			final double profMod = calcProf(env);
			final double maxResist = 90. + Math.max(calcEnchantMod(env), profMod * 0.85);
			return (maxResist - vulnMod) / 60.;
		}
	*/
	public static double calcEnchantMod(Env env)
	{
		int enchantLevel = env.skill.getDisplayLevel();
		if(enchantLevel <= 100)
			return 0;
		enchantLevel = enchantLevel % 100;
		return env.skill.getEnchantGrade() == EnchantGrade.THIRD ? enchantLevel * 2 : enchantLevel;
	}
}