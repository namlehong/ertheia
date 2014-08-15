package l2s.gameserver.stats;

import java.util.NoSuchElementException;
import l2s.gameserver.Config;

public enum Stats
{
	MAX_HP("maxHp", 0., Double.POSITIVE_INFINITY, 1.),
	MAX_MP("maxMp", 0., Double.POSITIVE_INFINITY, 1.),
	MAX_CP("maxCp", 0., Double.POSITIVE_INFINITY, 1.),

	REGENERATE_HP_RATE("regHp"),
	REGENERATE_CP_RATE("regCp"),
	REGENERATE_MP_RATE("regMp"),

	// Для эффектов типа Seal of Limit
	HP_LIMIT("hpLimit", 1., 100., 100.),
	MP_LIMIT("mpLimit", 1., 100., 100.),
	CP_LIMIT("cpLimit", 1., 100., 100.),

	RUN_SPEED("runSpd"),

	POWER_DEFENCE("pDef"),
	MAGIC_DEFENCE("mDef"),
	POWER_ATTACK("pAtk"),
	MAGIC_ATTACK("mAtk"),
	POWER_ATTACK_SPEED("pAtkSpd"),
	MAGIC_ATTACK_SPEED("mAtkSpd"),

	MAGIC_REUSE_RATE("mReuse"),
	PHYSIC_REUSE_RATE("pReuse"),
	MUSIC_REUSE_RATE("musicReuse"),
	ATK_REUSE("atkReuse"),
	BASE_P_ATK_SPD("basePAtkSpd"),
	BASE_M_ATK_SPD("baseMAtkSpd"),

	P_EVASION_RATE("pEvasRate"),
	M_EVASION_RATE("mEvasRate"),
	P_ACCURACY_COMBAT("pAccCombat"),
	M_ACCURACY_COMBAT("mAccCombat"),
	BASE_P_CRITICAL_RATE("basePCritRate", 0., Double.POSITIVE_INFINITY), // static crit rate. Use it to ADD some crit points. Sample: <add order="0x40" stat="baseCrit" val="27.4" />
	BASE_M_CRITICAL_RATE("baseMCritRate", 0., Double.POSITIVE_INFINITY),
	P_CRITICAL_RATE("pCritRate", 0., Double.POSITIVE_INFINITY, 100.), // dynamic crit rate. Use it to MULTIPLE crit for 1.3, 1.5 etc. Sample: <add order="0x40" stat="rCrit" val="50" /> = (x1.5)
	M_CRITICAL_RATE("mCritRate", 0., Double.POSITIVE_INFINITY, 100.),
	P_CRITICAL_DAMAGE("pCritDamage", 0., Double.POSITIVE_INFINITY, 100.),
	M_CRITICAL_DAMAGE("mCritDamage", 0., 10., 2.5),
	P_CRITICAL_DAMAGE_STATIC("pCritDamageStatic"),
	M_CRITICAL_DAMAGE_STATIC("mCritDamageStatic"),

	PHYSICAL_DAMAGE("physDamage"),
	MAGIC_DAMAGE("magicDamage"),

	CAST_INTERRUPT("concentration", 0., 100.),
	SHIELD_DEFENCE("sDef"),
	SHIELD_RATE("rShld", 0., 90.),
	SHIELD_ANGLE("shldAngle", 0., 360., 60.),

	POWER_ATTACK_RANGE("pAtkRange", 0., 1500.),
	MAGIC_ATTACK_RANGE("mAtkRange", 0., 1500.),
	POLE_ATTACK_ANGLE("poleAngle", 0., 180.),
	POLE_TARGET_COUNT("poleTargetCount"),

	STAT_STR("STR", 1., 200.),
	STAT_CON("CON", 1., 200.),
	STAT_DEX("DEX", 1., 200.),
	STAT_INT("INT", 1., 200.),
	STAT_WIT("WIT", 1., 200.),
	STAT_MEN("MEN", 1., 200.),
	STAT_LUC("LUC", 1., 200.),
	STAT_CHA("CHA", 1., 200.),

	BREATH("breath"),
	FALL("fall"),
	EXP_LOST("expLost"),

	BLEED_RESIST("bleedResist", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	POISON_RESIST("poisonResist", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	STUN_RESIST("stunResist", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	ROOT_RESIST("rootResist", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	MENTAL_RESIST("mentalResist", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	SLEEP_RESIST("sleepResist", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	PARALYZE_RESIST("paralyzeResist", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	CANCEL_RESIST("cancelResist", -200., 300.),
	DEBUFF_RESIST("debuffResist", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	MAGIC_RESIST("magicResist", -200., 300.),

	BLEED_POWER("bleedPower", -200., 200.),
	POISON_POWER("poisonPower", -200., 200.),
	STUN_POWER("stunPower", -200., 200.),
	ROOT_POWER("rootPower", -200., 200.),
	MENTAL_POWER("mentalPower", -200., 200.),
	SLEEP_POWER("sleepPower", -200., 200.),
	PARALYZE_POWER("paralyzePower", -200., 200.),
	CANCEL_POWER("cancelPower", -200., 200.),
	DEBUFF_POWER("debuffPower", -200., 200.),
	MAGIC_POWER("magicPower", -200., 200.),

	FATALBLOW_RATE("blowRate", 0., 10., 1.),
	SKILL_CRIT_CHANCE_MOD("SkillCritChanceMod", 10., 190., 100.),
	DEATH_VULNERABILITY("deathVuln", 10., 190., 100.),

	CRIT_DAMAGE_RECEPTIVE("critDamRcpt", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	CRIT_CHANCE_RECEPTIVE("critChanceRcpt", 10., 190., 100.),

	DEFENCE_FIRE("defenceFire", -600, 600), //Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_WATER("defenceWater", -600, 600), // Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_WIND("defenceWind", -600, 600), // Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_EARTH("defenceEarth", -600, 600), // Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_HOLY("defenceHoly", -600, 600), // Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DEFENCE_UNHOLY("defenceUnholy", -600, 600), // Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),

	ATTACK_FIRE("attackFire", 0., 600), // Double.POSITIVE_INFINITY),
	ATTACK_WATER("attackWater", 0., 600), // Double.POSITIVE_INFINITY),
	ATTACK_WIND("attackWind", 0., 600), // Double.POSITIVE_INFINITY),
	ATTACK_EARTH("attackEarth", 0., 600), // Double.POSITIVE_INFINITY),
	ATTACK_HOLY("attackHoly", 0., 600), // Double.POSITIVE_INFINITY),
	ATTACK_UNHOLY("attackUnholy", 0., 600), // Double.POSITIVE_INFINITY),

	SWORD_WPN_VULNERABILITY("swordWpnVuln", 10., 200., 100.),
	DUAL_WPN_VULNERABILITY("dualWpnVuln", 10., 200., 100.),
	BLUNT_WPN_VULNERABILITY("bluntWpnVuln", 10., 200., 100.),
	DAGGER_WPN_VULNERABILITY("daggerWpnVuln", 10., 200., 100.),
	BOW_WPN_VULNERABILITY("bowWpnVuln", 10., 200., 100.),
	CROSSBOW_WPN_VULNERABILITY("crossbowWpnVuln", 10., 200., 100.),
	POLE_WPN_VULNERABILITY("poleWpnVuln", 10., 200., 100.),
	FIST_WPN_VULNERABILITY("fistWpnVuln", 10., 200., 100.),

	ABSORB_DAMAGE_PERCENT("absorbDam", 0., 100., 0.),
	ABSORB_BOW_DAMAGE_PERCENT("absorbBowDam", 0., 100., 0.),
	ABSORB_PSKILL_DAMAGE_PERCENT("absorbPSkillDam", 0., 100., 0.),
	ABSORB_MSKILL_DAMAGE_PERCENT("absorbMSkillDam", 0., 100., 0.),
	ABSORB_DAMAGEMP_PERCENT("absorbDamMp", 0., 100., 0.),

	TRANSFER_TO_SUMMON_DAMAGE_PERCENT("transferPetDam", 0., 100.),
	TRANSFER_TO_EFFECTOR_DAMAGE_PERCENT("transferToEffectorDam", 0., 100.),
	TRANSFER_TO_MP_DAMAGE_PERCENT("transferToMpDam", 0., 100.),

	// Отражение урона с шансом. Урон получает только атакующий.
	REFLECT_AND_BLOCK_DAMAGE_CHANCE("reflectAndBlockDam", 0., Config.REFLECT_AND_BLOCK_DAMAGE_CHANCE_CAP), // Ближний урон без скиллов
	REFLECT_AND_BLOCK_PSKILL_DAMAGE_CHANCE("reflectAndBlockPSkillDam", 0., Config.REFLECT_AND_BLOCK_PSKILL_DAMAGE_CHANCE_CAP), // Ближний урон скиллами
	REFLECT_AND_BLOCK_MSKILL_DAMAGE_CHANCE("reflectAndBlockMSkillDam", 0., Config.REFLECT_AND_BLOCK_MSKILL_DAMAGE_CHANCE_CAP), // Любой урон магией

	// Отражение урона в процентах. Урон получает и атакующий и цель
	REFLECT_DAMAGE_PERCENT("reflectDam", 0., Config.REFLECT_DAMAGE_PERCENT_CAP), // Ближний урон без скиллов
	REFLECT_BOW_DAMAGE_PERCENT("reflectBowDam", 0., Config.REFLECT_BOW_DAMAGE_PERCENT_CAP), // Урон луком без скиллов
	REFLECT_PSKILL_DAMAGE_PERCENT("reflectPSkillDam", 0., Config.REFLECT_PSKILL_DAMAGE_PERCENT_CAP), // Ближний урон скиллами
	REFLECT_MSKILL_DAMAGE_PERCENT("reflectMSkillDam", 0., Config.REFLECT_MSKILL_DAMAGE_PERCENT_CAP), // Любой урон магией

	REFLECT_PHYSIC_SKILL("reflectPhysicSkill", 0., 60.),
	REFLECT_MAGIC_SKILL("reflectMagicSkill", 0., 60.),

	REFLECT_PHYSIC_DEBUFF("reflectPhysicDebuff", 0., 60.),
	REFLECT_MAGIC_DEBUFF("reflectMagicDebuff", 0., 60.),

	P_SKILL_EVASION("pSkillEvasion", 0., 100.),
	COUNTER_ATTACK("counterAttack", 0., 100.),

	P_SKILL_POWER("pSkillPower"),

	// PvP Dmg bonus
	PVP_PHYS_DMG_BONUS("pvpPhysDmgBonus"),
	PVP_PHYS_SKILL_DMG_BONUS("pvpPhysSkillDmgBonus"),
	PVP_MAGIC_SKILL_DMG_BONUS("pvpMagicSkillDmgBonus"),
	// PvP Def bonus
	PVP_PHYS_DEFENCE_BONUS("pvpPhysDefenceBonus"),
	PVP_PHYS_SKILL_DEFENCE_BONUS("pvpPhysSkillDefenceBonus"),
	PVP_MAGIC_SKILL_DEFENCE_BONUS("pvpMagicSkillDefenceBonus"),

	// PvE Dmg bonus
	PVE_PHYS_DMG_BONUS("pvePhysDmgBonus"),
	PVE_PHYS_SKILL_DMG_BONUS("pvePhysSkillDmgBonus"),
	PVE_MAGIC_SKILL_DMG_BONUS("pveMagicSkillDmgBonus"),
	// PvE Def bonus
	PVE_PHYS_DEFENCE_BONUS("pvePhysDefenceBonus"),
	PVE_PHYS_SKILL_DEFENCE_BONUS("pvePhysSkillDefenceBonus"),
	PVE_MAGIC_SKILL_DEFENCE_BONUS("pveMagicSkillDefenceBonus"),

	HEAL_EFFECTIVNESS("hpEff", 0., 1000.),
	MANAHEAL_EFFECTIVNESS("mpEff", 0., 1000.),
	CPHEAL_EFFECTIVNESS("cpEff", 0., 1000.),
	HEAL_POWER("healPower"),
	MP_MAGIC_SKILL_CONSUME("mpConsum"),
	MP_PHYSICAL_SKILL_CONSUME("mpConsumePhysical"),
	MP_DANCE_SKILL_CONSUME("mpDanceConsume"),

	ACTIVATE_SKILL_MASTERY_INT("activateSkillMasteryINT", 0., 1., 1.),
	ACTIVATE_SKILL_MASTERY_STR("activateSkillMasterySTR", 0., 1., 1.),
	SKILL_MASTERY("skillMastery"),

	CHEAP_SHOT("cheap_shot"),

	MAX_LOAD("maxLoad"),
	MAX_NO_PENALTY_LOAD("maxNoPenaltyLoad"),
	INVENTORY_LIMIT("inventoryLimit"),
	STORAGE_LIMIT("storageLimit"),
	TRADE_LIMIT("tradeLimit"),
	COMMON_RECIPE_LIMIT("CommonRecipeLimit"),
	DWARVEN_RECIPE_LIMIT("DwarvenRecipeLimit"),
	BUFF_LIMIT("buffLimit"),
	SOULS_LIMIT("soulsLimit"),
	SOULS_CONSUME_EXP("soulsExp"),
	TALISMANS_LIMIT("talismansLimit", 0., 6.),
	JEWELS_LIMIT("jewels_limit", 0., 6.),
	CUBICS_LIMIT("cubicsLimit", 0., 3., 1.),

	GRADE_EXPERTISE_LEVEL("gradeExpertiseLevel"),
	EXP("ExpMultiplier"),
	SP("SpMultiplier"),
	REWARD_MULTIPLIER("DropMultiplier"),

	SKILLS_ELEMENT_ID("skills_element_id", -1., 100., -1.),
	SUMMON_POINTS("summon_points", 0., 100., 0.),
	DAMAGE_AGGRO_PERCENT("damageAggroPercent", 0., 300., 0.),
	RECIEVE_DAMAGE_LIMIT("recieveDamageLimit", -1, Double.POSITIVE_INFINITY, -1),
	RECIEVE_DAMAGE_LIMIT_P_SKILL("recieveDamageLimitPSkill", -1, Double.POSITIVE_INFINITY, -1),
	RECIEVE_DAMAGE_LIMIT_M_SKILL("recieveDamageLimitMSkill", -1, Double.POSITIVE_INFINITY, -1),
	DAMAGE_HEAL_TO_EFFECTOR("damageHealToEffector", 0., 100., 0.),
	DAMAGE_HEAL_MP_TO_EFFECTOR("damageHealMpToEffector", 0., 100., 0.),
	KILL_AND_RESTORE_HP("killAndRestoreHp", 0., 100., 0.),
	RESIST_REFLECT_DAM("resistRelectDam", 0., 100., 0.),
	
	AIRJOKE_RESIST("airjokeResist", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	MUTATE_RESIST("mutateResist", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	DISARM_RESIST("disarmResist", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	PULL_RESIST("pullResist", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	KNOCKBACK_RESIST("knockBackResist", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
	KNOCKDOWN_RESIST("knockDownResist", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),	
	//new powers
	MUTATE_POWER("mutatePower", -200., 200.),
	AIRJOKE_POWER("airjokePower", -200., 200.),
	DISARM_POWER("disarmPower", -200., 200.),
	PULL_POWER("pullPower", -200., 200.),
	KNOCKBACK_POWER("knockBackPower", -200., 200.),
	KNOCKDOWN_POWER("knockDownPower", -200., 200.),	
	
	BUFF_TIME_MODIFIER("buff_time_modifier", 1., Double.POSITIVE_INFINITY, 1.),
	DEBUFF_TIME_MODIFIER("debuff_time_modifier", 1., Double.POSITIVE_INFINITY, 1.),

	P_SKILL_CRIT_RATE_DEX_DEPENDENCE("p_skill_crit_rate_dex_dependence", 0., 1., 0.),

	ENCHANT_CHANCE_MODIFIER("enchant_chance_modifier", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1.);

	public static final int NUM_STATS = values().length;

	private final String _value;
	private double _min;
	private double _max;
	private double _init;

	public String getValue()
	{
		return _value;
	}

	public double getInit()
	{
		return _init;
	}

	private Stats(String s)
	{
		this(s, 0., Double.POSITIVE_INFINITY, 0.);
	}

	private Stats(String s, double min, double max)
	{
		this(s, min, max, 0.);
	}

	private Stats(String s, double min, double max, double init)
	{
		_value = s;
		_min = min;
		_max = max;
		_init = init;
	}

	public double validate(double val)
	{
		if(val < _min)
			return _min;
		if(val > _max)
			return _max;
		return val;
	}

	public static Stats valueOfXml(String name)
	{
		for(Stats s : values())
			if(s.getValue().equals(name))
				return s;

		throw new NoSuchElementException("Unknown name '" + name + "' for enum Stats");
	}

	@Override
	public String toString()
	{
		return _value;
	}
}