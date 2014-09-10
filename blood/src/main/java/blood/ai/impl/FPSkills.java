package blood.ai.impl;

public class FPSkills {
	public class HumanFigter {
		public static final int
		//======= Start Skill list of HUMAN_FIGHTER =======
		SKILL_POWER_STRIKE							= 3, // Lv.9
		SKILL_MORTAL_BLOW							= 16, // Lv.9
		SKILL_POWER_SHOT							= 56, // Lv.9
		SKILL_RELAX									= 226, // Lv.1
		//======= End Skill list of HUMAN_FIGHTER =======
		SKILL_DUMMY = 1;
	}
	
	public class Warrior extends HumanFigter{
		public static final int
		//======= Start Skill list of WARRIOR =======
		SKILL_DETECT_INSECT_WEAKNESS				= 75, // Lv.1
		SKILL_WAR_CRY								= 78, // Lv.1
		SKILL_STUN_ATTACK							= 100, // Lv.15
		SKILL_BATTLE_ROAR							= 121, // Lv.1
		SKILL_WILD_SWEEP							= 245, // Lv.15
		SKILL_POWER_SMASH							= 255, // Lv.15
		SKILL_ACCURACY								= 256, // Lv.1
		SKILL_LIONHEART								= 287, // Lv.1
		SKILL_VICIOUS_STANCE						= 312, // Lv.5
		//======= End Skill list of WARRIOR =======
		SKILL_DUMMY = 1;
	}
	
	public class Gladiator extends Warrior{
		public static final int
		//======= Start Skill list of GLADIATOR =======
		SKILL_TRIPLE_SLASH							= 1, // Lv.37
		SKILL_DOUBLE_SONIC_SLASH					= 5, // Lv.31
		SKILL_SONIC_BLASTER							= 6, // Lv.37
		SKILL_SONIC_STORM							= 7, // Lv.28
		SKILL_SONIC_FOCUS							= 8, // Lv.7
		SKILL_SONIC_BUSTER							= 9, // Lv.34
		SKILL_WAR_CRY								= 78, // Lv.2
		SKILL_DETECT_BEAST_WEAKNESS					= 80, // Lv.1
		SKILL_DETECT_ANIMAL_WEAKNESS				= 87, // Lv.1
		SKILL_DETECT_DRAGON_WEAKNESS				= 88, // Lv.1
		SKILL_DETECT_PLANT_WEAKNESS					= 104, // Lv.1
		SKILL_FATAL_STRIKE							= 190, // Lv.37
		SKILL_HAMMER_CRUSH							= 260, // Lv.37
		SKILL_TRIPLE_SONIC_SLASH					= 261, // Lv.22
		SKILL_LIONHEART								= 287, // Lv.3
		SKILL_DUELIST_SPIRIT						= 297, // Lv.2
		SKILL_VICIOUS_STANCE						= 312, // Lv.20
		SKILL_WAR_FRENZY							= 424, // Lv.3
		SKILL_SONIC_MOVE							= 451, // Lv.2
		SKILL_RUSH									= 994, // Lv.1
		//======= End Skill list of GLADIATOR =======
		SKILL_DUMMY = 1;
	}
	
	public class Warlord extends Warrior{
		public static final int
		//======= Start Skill list of WARLORD =======
		SKILL_WHIRLWIND								= 36, // Lv.37
		SKILL_THUNDER_STORM							= 48, // Lv.37
		SKILL_DETECT_BEAST_WEAKNESS					= 80, // Lv.1
		SKILL_DETECT_ANIMAL_WEAKNESS				= 87, // Lv.1
		SKILL_DETECT_DRAGON_WEAKNESS				= 88, // Lv.1
		SKILL_DETECT_PLANT_WEAKNESS					= 104, // Lv.1
		SKILL_HOWL									= 116, // Lv.14
		SKILL_BATTLE_ROAR							= 121, // Lv.6
		SKILL_THRILL_FIGHT							= 130, // Lv.2
		SKILL_REVIVAL								= 181, // Lv.1
		SKILL_PROVOKE								= 286, // Lv.3
		SKILL_LIONHEART								= 287, // Lv.3
		SKILL_VICIOUS_STANCE						= 312, // Lv.20
		SKILL_FOCUS_ATTACK							= 317, // Lv.5
		SKILL_WRATH									= 320, // Lv.10
		SKILL_FELL_SWOOP							= 421, // Lv.5
		SKILL_POLEARM_ACCURACY						= 422, // Lv.3
		SKILL_WAR_FRENZY							= 424, // Lv.3
		SKILL_SHOCK_STOMP							= 452, // Lv.5
		SKILL_POWER_CRUSH							= 920, // Lv.37
		SKILL_RUSH									= 994, // Lv.1
		//======= End Skill list of WARLORD =======
		SKILL_DUMMY = 1;
	}
	
	public class Knight extends HumanFigter {
		public static final int
		//======= Start Skill list of KNIGHT =======
		SKILL_AGGRESSION							= 28, // Lv.12
		SKILL_DIVINE_HEAL							= 45, // Lv.9
		SKILL_DRAIN_HEALTH							= 70, // Lv.13
		SKILL_MAJESTY								= 82, // Lv.1
		SKILL_SHIELD_STUN							= 92, // Lv.15
		SKILL_ULTIMATE_DEFENSE						= 110, // Lv.1
		SKILL_DEFLECT_ARROW							= 112, // Lv.2
		//======= End Skill list of KNIGHT =======
		SKILL_DUMMY = 1;
	}
	
	public class Paladin extends Knight{
		public static final int
		//======= Start Skill list of PALADIN =======
		SKILL_AURA_OF_HATE							= 18, // Lv.37
		SKILL_AGGRESSION							= 28, // Lv.49
		SKILL_REMEDY								= 44, // Lv.3
		SKILL_HOLY_STRIKE							= 49, // Lv.26
		SKILL_SACRIFICE								= 69, // Lv.25
		SKILL_IRON_WILL								= 72, // Lv.3
		SKILL_MAJESTY								= 82, // Lv.3
		SKILL_SHIELD_STUN							= 92, // Lv.52
		SKILL_SANCTUARY								= 97, // Lv.11
		SKILL_ULTIMATE_DEFENSE						= 110, // Lv.2
		SKILL_DEFLECT_ARROW							= 112, // Lv.4
		SKILL_HOLY_BLADE							= 196, // Lv.1
		SKILL_HOLY_ARMOR							= 197, // Lv.2
		SKILL_HOLY_BLESSING							= 262, // Lv.37
		SKILL_AEGIS_STANCE							= 318, // Lv.1
		SKILL_SHIELD_FORTRESS						= 322, // Lv.6
		SKILL_TRIBUNAL								= 400, // Lv.10
		SKILL_SHACKLE								= 403, // Lv.10
		SKILL_MASS_SHACKLING						= 404, // Lv.5
		SKILL_BANISH_UNDEAD							= 405, // Lv.10
		SKILL_ANGELIC_ICON							= 406, // Lv.3
		SKILL_VANGUARD								= 810, // Lv.1
		SKILL_SHIELD_DEFLECT_MAGIC					= 916, // Lv.4
		SKILL_COMBAT_AURA							= 982, // Lv.3
		SKILL_SHIELD_STRIKE							= 984, // Lv.15
		//======= End Skill list of PALADIN =======
		SKILL_DUMMY = 1;
	}
	
	public class DarkAvenger extends Knight{
		public static final int
		//======= Start Skill list of DARK_AVENGER =======
		SKILL_AURA_OF_HATE							= 18, // Lv.37
		SKILL_AGGRESSION							= 28, // Lv.49
		SKILL_LIFE_SCAVENGE							= 46, // Lv.15
		SKILL_HORROR								= 65, // Lv.13
		SKILL_DRAIN_HEALTH							= 70, // Lv.53
		SKILL_IRON_WILL								= 72, // Lv.3
		SKILL_MAJESTY								= 82, // Lv.3
		SKILL_REFLECT_DAMAGE						= 86, // Lv.3
		SKILL_SHIELD_STUN							= 92, // Lv.52
		SKILL_CORPSE_PLAGUE							= 103, // Lv.4
		SKILL_ULTIMATE_DEFENSE						= 110, // Lv.2
		SKILL_DEFLECT_ARROW							= 112, // Lv.4
		SKILL_HAMSTRING								= 127, // Lv.14
		SKILL_SUMMON_DARK_PANTHER					= 283, // Lv.7
		SKILL_AEGIS_STANCE							= 318, // Lv.1
		SKILL_SHIELD_FORTRESS						= 322, // Lv.6
		SKILL_JUDGMENT								= 401, // Lv.10
		SKILL_SHACKLE								= 403, // Lv.10
		SKILL_BANISH_SERAPH							= 450, // Lv.10
		SKILL_VANGUARD								= 811, // Lv.1
		SKILL_SHIELD_DEFLECT_MAGIC					= 916, // Lv.4
		SKILL_COMBAT_AURA							= 982, // Lv.3
		SKILL_SHIELD_STRIKE							= 984, // Lv.15
		//======= End Skill list of DARK_AVENGER =======
		SKILL_DUMMY = 1;
	}
	
	public class Rogue extends HumanFigter{
		public static final int
		//======= Start Skill list of ROGUE =======
		SKILL_DASH									= 4, // Lv.1
		SKILL_MORTAL_BLOW							= 16, // Lv.24
		SKILL_UNLOCK								= 27, // Lv.5
		SKILL_POWER_SHOT							= 56, // Lv.24
		SKILL_BLEED									= 96, // Lv.2
		SKILL_RAPID_SHOT							= 99, // Lv.1
		SKILL_STUNNING_SHOT							= 101, // Lv.3
		SKILL_ULTIMATE_EVASION						= 111, // Lv.1
		SKILL_ACCURACY								= 256, // Lv.1
		SKILL_VICIOUS_STANCE						= 312, // Lv.5
		//======= End Skill list of ROGUE =======
		SKILL_DUMMY = 1;
	}
	
	public class TreasureHunter extends Rogue {
		public static final int
		//======= Start Skill list of TREASURE_HUNTER =======
		SKILL_DASH									= 4, // Lv.2
		SKILL_TRICK									= 11, // Lv.12
		SKILL_SWITCH								= 12, // Lv.14
		SKILL_UNLOCK								= 27, // Lv.14
		SKILL_BACKSTAB								= 30, // Lv.37
		SKILL_LURE									= 51, // Lv.1
		SKILL_FAKE_DEATH							= 60, // Lv.1
		SKILL_BLEED									= 96, // Lv.6
		SKILL_VEIL									= 106, // Lv.14
		SKILL_ULTIMATE_EVASION						= 111, // Lv.2
		SKILL_SILENT_MOVE							= 221, // Lv.1
		SKILL_DEADLY_BLOW							= 263, // Lv.37
		SKILL_VICIOUS_STANCE						= 312, // Lv.20
		SKILL_CRITICAL_BLOW							= 409, // Lv.10
		SKILL_STEALTH								= 411, // Lv.3
		SKILL_SAND_BOMB								= 412, // Lv.10
		SKILL_SUMMON_TREASURE_KEY					= 419, // Lv.4
		SKILL_ESCAPE_SHACKLE						= 453, // Lv.1
		SKILL_FIND_TRAP								= 623, // Lv.1
		SKILL_REMOVE_TRAP							= 624, // Lv.1
		SKILL_SHADOW_STEP							= 821, // Lv.1
		//======= End Skill list of TREASURE_HUNTER =======
		SKILL_DUMMY = 1;
	}
	
	public class Hawkeye extends Rogue {
		public static final int
		//======= Start Skill list of HAWKEYE =======
		SKILL_DOUBLE_SHOT							= 19, // Lv.37
		SKILL_BURST_SHOT							= 24, // Lv.31
		SKILL_RAPID_SHOT							= 99, // Lv.2
		SKILL_STUNNING_SHOT							= 101, // Lv.40
		SKILL_HAWK_EYE								= 131, // Lv.3
		SKILL_SOUL_OF_SAGITTARIUS					= 303, // Lv.4
		SKILL_VICIOUS_STANCE						= 312, // Lv.20
		SKILL_SNIPE									= 313, // Lv.8
		SKILL_QUIVER_OF_ARROW_GRADE_A				= 323, // Lv.1
		SKILL_QUIVER_OF_ARROW_GRADE_S				= 324, // Lv.1
		SKILL_SPIRIT_OF_SAGITTARIUS					= 415, // Lv.3
		SKILL_BLESSING_OF_SAGITTARIUS				= 416, // Lv.3
		SKILL_PAIN_OF_SAGITTARIUS					= 417, // Lv.5
		SKILL_DETECTION								= 933, // Lv.1
		//======= End Skill list of HAWKEYE =======
		SKILL_DUMMY = 1;
	}
	
	public class HumanMage {
		public static final int
		//======= Start Skill list of HUMAN_MAGE =======
		SKILL_HEAL									= 1011, // Lv.6
		SKILL_CURE_POISON							= 1012, // Lv.1
		SKILL_BATTLE_HEAL							= 1015, // Lv.3
		SKILL_GROUP_HEAL							= 1027, // Lv.3
		SKILL_SHIELD								= 1040, // Lv.1
		SKILL_MIGHT									= 1068, // Lv.1
		SKILL_VAMPIRIC_TOUCH						= 1147, // Lv.2
		SKILL_CURSE_WEAKNESS						= 1164, // Lv.1
		SKILL_CURSE_POISON							= 1168, // Lv.1
		SKILL_WIND_STRIKE							= 1177, // Lv.5
		SKILL_ICE_BOLT								= 1184, // Lv.4
		//======= End Skill list of HUMAN_MAGE =======
		SKILL_DUMMY = 1;
	}
	
	public class Wizzard extends HumanMage {
		public static final int
		//======= Start Skill list of WIZARD =======
		SKILL_SLEEP									= 1069, // Lv.9
		SKILL_CONCENTRATION							= 1078, // Lv.2
		SKILL_SURRENDER_TO_FIRE						= 1083, // Lv.3
		SKILL_SUMMON_KAT_THE_CAT					= 1111, // Lv.4
		SKILL_SERVITOR_RECHARGE						= 1126, // Lv.6
		SKILL_SERVITOR_HEAL							= 1127, // Lv.12
		SKILL_SERVITOR_WIND_WALK					= 1144, // Lv.1
		SKILL_VAMPIRIC_TOUCH						= 1147, // Lv.6
		SKILL_CORPSE_LIFE_DRAIN						= 1151, // Lv.2
		SKILL_BODY_TO_MIND							= 1157, // Lv.1
		SKILL_SLOW									= 1160, // Lv.1
		SKILL_CURSE_WEAKNESS						= 1164, // Lv.5
		SKILL_POISONOUS_CLOUD						= 1167, // Lv.2
		SKILL_CURSE_POISON							= 1168, // Lv.3
		SKILL_AURA_BURN								= 1172, // Lv.8
		SKILL_FLAME_STRIKE							= 1181, // Lv.3
		SKILL_ICE_BOLT								= 1184, // Lv.6
		SKILL_BLAZE									= 1220, // Lv.8
		SKILL_CURSE_CHAOS							= 1222, // Lv.1
		SKILL_SUMMON_MEW_THE_CAT					= 1225, // Lv.4
		SKILL_ENERGY_BOLT							= 1274, // Lv.4
		//======= End Skill list of WIZARD =======
		SKILL_DUMMY = 1;
	}
	
	public class Sorcerer extends Wizzard {
		public static final int
		//======= Start Skill list of SORCERER =======
		SKILL_CANCELLATION							= 1056, // Lv.12
		SKILL_SLEEP									= 1069, // Lv.42
		SKILL_SLEEPING_CLOUD						= 1072, // Lv.5
		SKILL_SURRENDER_TO_WIND						= 1074, // Lv.14
		SKILL_CONCENTRATION							= 1078, // Lv.6
		SKILL_SURRENDER_TO_FIRE						= 1083, // Lv.17
		SKILL_SLOW									= 1160, // Lv.15
		SKILL_CURSE_FEAR							= 1169, // Lv.14
		SKILL_BLAZING_CIRCLE						= 1171, // Lv.19
		SKILL_PROMINENCE							= 1230, // Lv.28
		SKILL_AURA_FLARE							= 1231, // Lv.28
		SKILL_BLAZING_SKIN							= 1232, // Lv.3
		SKILL_DECAY									= 1233, // Lv.4
		SKILL_AURA_BOLT								= 1275, // Lv.14
		SKILL_SEED_OF_FIRE							= 1285, // Lv.1
		SKILL_AURA_SYMPHONY							= 1288, // Lv.1
		SKILL_INFERNO								= 1289, // Lv.1
		SKILL_ELEMENTAL_ASSAULT						= 1292, // Lv.1
		SKILL_RAIN_OF_FIRE							= 1296, // Lv.9
		SKILL_AURA_FLASH							= 1417, // Lv.5
		//======= End Skill list of SORCERER =======
		SKILL_DUMMY = 1;
	}

	public class Necromancer extends Wizzard {
		public static final int
		//======= Start Skill list of NECROMANCER =======
		SKILL_SILENCE								= 1064, // Lv.14
		SKILL_SLEEP									= 1069, // Lv.42
		SKILL_SUMMON_REANIMATED_MAN					= 1129, // Lv.7
		SKILL_DEATH_SPIKE							= 1148, // Lv.13
		SKILL_CORPSE_LIFE_DRAIN						= 1151, // Lv.16
		SKILL_SUMMON_CORRUPTED_MAN					= 1154, // Lv.6
		SKILL_CORPSE_BURST							= 1155, // Lv.15
		SKILL_FORGET								= 1156, // Lv.13
		SKILL_BODY_TO_MIND							= 1157, // Lv.5
		SKILL_CURSE_DEATH_LINK						= 1159, // Lv.22
		SKILL_CURSE_DISCORD							= 1163, // Lv.14
		SKILL_CURSE_WEAKNESS						= 1164, // Lv.19
		SKILL_POISONOUS_CLOUD						= 1167, // Lv.6
		SKILL_CURSE_POISON							= 1168, // Lv.7
		SKILL_CURSE_FEAR							= 1169, // Lv.14
		SKILL_ANCHOR								= 1170, // Lv.13
		SKILL_CURSE_CHAOS							= 1222, // Lv.15
		SKILL_VAMPIRIC_CLAW							= 1234, // Lv.28
		SKILL_TRANSFER_PAIN							= 1262, // Lv.5
		SKILL_CURSE_GLOOM							= 1263, // Lv.13
		SKILL_CURSE_DISEASE							= 1269, // Lv.9
		SKILL_MASS_SLOW								= 1298, // Lv.14
		SKILL_SUMMON_CURSED_MAN						= 1334, // Lv.7
		SKILL_MASS_FEAR								= 1381, // Lv.5
		SKILL_MASS_GLOOM							= 1382, // Lv.5
		//======= End Skill list of NECROMANCER =======
		SKILL_DUMMY = 1;
	}
	
	public class Warlock extends Wizzard {
		public static final int
		//======= Start Skill list of WARLOCK =======
		SKILL_SUMMON_STORM_CUBIC					= 10, // Lv.8
		SKILL_SUMMON_KAT_THE_CAT					= 1111, // Lv.18
		SKILL_SERVITOR_RECHARGE						= 1126, // Lv.34
		SKILL_SERVITOR_HEAL							= 1127, // Lv.45
		SKILL_SERVITOR_MAGIC_SHIELD					= 1139, // Lv.2
		SKILL_SERVITOR_PHYSICAL_SHIELD				= 1140, // Lv.3
		SKILL_SERVITOR_HASTE						= 1141, // Lv.2
		SKILL_SERVITOR_WIND_WALK					= 1144, // Lv.2
		SKILL_SUMMON_MEW_THE_CAT					= 1225, // Lv.18
		SKILL_TRANSFER_PAIN							= 1262, // Lv.5
		SKILL_SUMMON_KAI_THE_CAT					= 1276, // Lv.14
		SKILL_SUMMON_BINDING_CUBIC					= 1279, // Lv.9
		SKILL_SERVITOR_EMPOWERMENT					= 1299, // Lv.2
		SKILL_SERVITOR_CURE							= 1300, // Lv.3
		SKILL_SERVITOR_BLESSING						= 1301, // Lv.1
		SKILL_MASS_SUMMON_STORM_CUBIC				= 1328, // Lv.8
		SKILL_SUMMON_FELINE_QUEEN					= 1331, // Lv.10
		SKILL_BETRAY								= 1380, // Lv.10
		SKILL_MASS_SURRENDER_TO_FIRE				= 1383, // Lv.5
		SKILL_ARCANE_DISRUPTION						= 1386, // Lv.10
		SKILL_SUMMON_FRIEND							= 1403, // Lv.1
		SKILL_SPIRIT_SHARING						= 1547, // Lv.3
		SKILL_DIMENSION_SPIRAL						= 1558, // Lv.14
		//======= End Skill list of WARLOCK =======
		SKILL_DUMMY = 1;
	}

	public class Cleric extends HumanMage {
		public static final int
		//======= Start Skill list of CLERIC =======
		SKILL_HEAL									= 1011, // Lv.18
		SKILL_CURE_POISON							= 1012, // Lv.2
		SKILL_BATTLE_HEAL							= 1015, // Lv.15
		SKILL_RESURRECTION							= 1016, // Lv.2
		SKILL_GROUP_HEAL							= 1027, // Lv.15
		SKILL_DISRUPT_UNDEAD						= 1031, // Lv.8
		SKILL_MENTAL_SHIELD							= 1035, // Lv.1
		SKILL_SHIELD								= 1040, // Lv.2
		SKILL_HOLY_WEAPON							= 1043, // Lv.1
		SKILL_REGENERATION							= 1044, // Lv.1
		SKILL_BERSERKER_SPIRIT						= 1062, // Lv.1
		SKILL_MIGHT									= 1068, // Lv.2
		SKILL_SLEEP									= 1069, // Lv.9
		SKILL_KISS_OF_EVA							= 1073, // Lv.1
		SKILL_PEACE									= 1075, // Lv.1
		SKILL_FOCUS									= 1077, // Lv.1
		SKILL_CONCENTRATION							= 1078, // Lv.2
		SKILL_ACUMEN								= 1085, // Lv.2
		SKILL_RESIST_FIRE							= 1191, // Lv.1
		SKILL_DRYAD_ROOT							= 1201, // Lv.9
		SKILL_WIND_WALK								= 1204, // Lv.2
		//======= End Skill list of CLERIC =======
		SKILL_DUMMY = 1;
	}

	public class Bishop extends Cleric {
		public static final int
		//======= Start Skill list of BISHOP =======
		SKILL_CURE_POISON							= 1012, // Lv.3
		SKILL_RESURRECTION							= 1016, // Lv.9
		SKILL_PURIFY								= 1018, // Lv.3
		SKILL_VITALIZE								= 1020, // Lv.27
		SKILL_MIGHT_OF_HEAVEN						= 1028, // Lv.19
		SKILL_REPOSE								= 1034, // Lv.13
		SKILL_HOLD_UNDEAD							= 1042, // Lv.12
		SKILL_REQUIEM								= 1049, // Lv.14
		SKILL_SLEEP									= 1069, // Lv.42
		SKILL_PEACE									= 1075, // Lv.15
		SKILL_GREATER_HEAL							= 1217, // Lv.33
		SKILL_GREATER_BATTLE_HEAL					= 1218, // Lv.33
		SKILL_GREATER_GROUP_HEAL					= 1219, // Lv.33
		SKILL_MASS_RESURRECTION						= 1254, // Lv.6
		SKILL_RESTORE_LIFE							= 1258, // Lv.4
		SKILL_BENEDICTION							= 1271, // Lv.1
		SKILL_PRAYER								= 1307, // Lv.3
		SKILL_BODY_OF_AVATAR						= 1311, // Lv.6
		SKILL_TRANCE								= 1394, // Lv.10
		SKILL_ERASE									= 1395, // Lv.10
		SKILL_MAGICAL_BACKFIRE						= 1396, // Lv.10
		SKILL_MANA_BURN								= 1398, // Lv.10
		SKILL_MANA_STORM							= 1399, // Lv.5
		SKILL_TURN_UNDEAD							= 1400, // Lv.10
		SKILL_MAJOR_HEAL							= 1401, // Lv.11
		SKILL_MAJOR_GROUP_HEAL						= 1402, // Lv.5
		SKILL_CELESTIAL_SHIELD						= 1418, // Lv.1
		SKILL_INVOCATION							= 1430, // Lv.5
		SKILL_DIVINE_PUNISHMENT						= 1523, // Lv.13
		SKILL_SURRENDER_TO_THE_HOLY					= 1524, // Lv.42
		SKILL_DIVINE_CURSE							= 1525, // Lv.13
		SKILL_DIVINE_FLASH							= 1528, // Lv.13
		//======= End Skill list of BISHOP =======
		SKILL_DUMMY = 1;
	}
	
	public class Prophet extends Cleric {
		public static final int
		//======= Start Skill list of PROPHET =======
		SKILL_INVIGOR								= 1032, // Lv.3
		SKILL_RESIST_POISON							= 1033, // Lv.3
		SKILL_MENTAL_SHIELD							= 1035, // Lv.4
		SKILL_MAGIC_BARRIER							= 1036, // Lv.2
		SKILL_SHIELD								= 1040, // Lv.3
		SKILL_REGENERATION							= 1044, // Lv.3
		SKILL_BLESSED_BODY							= 1045, // Lv.6
		SKILL_BLESSED_SOUL							= 1048, // Lv.6
		SKILL_RETURN								= 1050, // Lv.2
		SKILL_BERSERKER_SPIRIT						= 1062, // Lv.2
		SKILL_MIGHT									= 1068, // Lv.3
		SKILL_KISS_OF_EVA							= 1073, // Lv.2
		SKILL_FOCUS									= 1077, // Lv.3
		SKILL_CONCENTRATION							= 1078, // Lv.6
		SKILL_ACUMEN								= 1085, // Lv.3
		SKILL_HASTE									= 1086, // Lv.2
		SKILL_RESIST_AQUA							= 1182, // Lv.3
		SKILL_RESIST_WIND							= 1189, // Lv.3
		SKILL_RESIST_FIRE							= 1191, // Lv.3
		SKILL_DRYAD_ROOT							= 1201, // Lv.33
		SKILL_GUIDANCE								= 1240, // Lv.3
		SKILL_DEATH_WHISPER							= 1242, // Lv.3
		SKILL_BLESS_SHIELD							= 1243, // Lv.3
		SKILL_WORD_OF_FEAR							= 1272, // Lv.13
		SKILL_GREATER_MIGHT							= 1388, // Lv.3
		SKILL_GREATER_SHIELD						= 1389, // Lv.3
		SKILL_HOLY_RESISTANCE						= 1392, // Lv.3
		SKILL_UNHOLY_RESISTANCE						= 1393, // Lv.3
		SKILL_ERASE									= 1395, // Lv.10
		SKILL_MANA_BURN								= 1398, // Lv.10
		SKILL_IMPROVED_COMBAT						= 1499, // Lv.1
		SKILL_IMPROVED_CONDITION					= 1501, // Lv.1
		SKILL_RESIST_EARTH							= 1548, // Lv.3
		//======= End Skill list of PROPHET =======
		SKILL_DUMMY = 1;
	}	

	public class ElvenFighter {
		public static final int
		//======= Start Skill list of ElvenFighter ID:18=======
		SKILL_POWER_STRIKE							= 3, // Lv.9
		SKILL_MORTAL_BLOW							= 16, // Lv.9
		SKILL_POWER_SHOT							= 56, // Lv.9
		SKILL_ELEMENTAL_HEAL						= 58, // Lv.3
		SKILL_ATTACK_AURA							= 77, // Lv.1
		SKILL_DEFENSE_AURA							= 91, // Lv.1
		//======= End Skill list of ElvenFighter ID:18=======
		SKILL_DUMMY = 1;
	};

	public class ElvenKnight extends ElvenFighter {
		public static final int
		//======= Start Skill list of ElvenKnight ID:19=======
		SKILL_CHARM									= 15, // Lv.15
		SKILL_POISON_RECOVERY						= 21, // Lv.1
		SKILL_AGGRESSION							= 28, // Lv.12
		SKILL_ELEMENTAL_HEAL						= 58, // Lv.18
		SKILL_CURE_BLEEDING							= 61, // Lv.1
		SKILL_ATTACK_AURA							= 77, // Lv.2
		SKILL_DEFENSE_AURA							= 91, // Lv.2
		SKILL_ENTANGLE								= 102, // Lv.1
		SKILL_ULTIMATE_DEFENSE						= 110, // Lv.1
		SKILL_DEFLECT_ARROW							= 112, // Lv.2
		SKILL_SPRINT								= 230, // Lv.1
		//======= End Skill list of ElvenKnight ID:19=======
		SKILL_DUMMY = 1;
	};

	public class TempleKnight extends ElvenKnight {
		public static final int
		//======= Start Skill list of TempleKnight ID:20=======
		SKILL_SUMMON_STORM_CUBIC					= 10, // Lv.8
		SKILL_CHARM								= 15, // Lv.52
		SKILL_AURA_OF_HATE							= 18, // Lv.37
		SKILL_POISON_RECOVERY						= 21, // Lv.3
		SKILL_AGGRESSION							= 28, // Lv.49
		SKILL_ELEMENTAL_HEAL						= 58, // Lv.55
		SKILL_CURE_BLEEDING						= 61, // Lv.3
		SKILL_SUMMON_LIFE_CUBIC					= 67, // Lv.7
		SKILL_ENTANGLE								= 102, // Lv.16
		SKILL_HOLY_AURA							= 107, // Lv.9
		SKILL_ULTIMATE_DEFENSE						= 110, // Lv.2
		SKILL_DEFLECT_ARROW						= 112, // Lv.4
		SKILL_SPIRIT_BARRIER						= 123, // Lv.3
		SKILL_HOLY_ARMOR							= 197, // Lv.2
		SKILL_SPRINT								= 230, // Lv.2
		SKILL_GUARD_STANCE							= 288, // Lv.4
		SKILL_SHIELD_FORTRESS						= 322, // Lv.6
		SKILL_TRIBUNAL								= 400, // Lv.10
		SKILL_ARREST								= 402, // Lv.10
		SKILL_SUMMON_ATTRACTIVE_CUBIC				= 449, // Lv.4
		SKILL_VANGUARD								= 812, // Lv.1
		SKILL_SHIELD_DEFLECT_MAGIC					= 916, // Lv.4
		SKILL_COMBAT_AURA							= 982, // Lv.3
		SKILL_SHIELD_STRIKE						= 984, // Lv.15
		//======= End Skill list of TempleKnight ID:20=======
		SKILL_DUMMY = 1;
	};

	public class Swordsinger extends ElvenKnight {
		public static final int
		//======= Start Skill list of Swordsinger ID:21=======
		SKILL_CHARM								= 15, // Lv.52
		SKILL_POISON_RECOVERY						= 21, // Lv.3
		SKILL_ELEMENTAL_HEAL						= 58, // Lv.55
		SKILL_CURE_BLEEDING						= 61, // Lv.3
		SKILL_SWORD_SYMPHONY						= 98, // Lv.5
		SKILL_ENTANGLE								= 102, // Lv.16
		SKILL_SPIRIT_BARRIER						= 123, // Lv.3
		SKILL_HOLY_BLADE							= 196, // Lv.1
		SKILL_SPRINT								= 230, // Lv.2
		SKILL_SONG_OF_EARTH						= 264, // Lv.1
		SKILL_SONG_OF_LIFE							= 265, // Lv.1
		SKILL_SONG_OF_WATER						= 266, // Lv.1
		SKILL_SONG_OF_WARDING						= 267, // Lv.1
		SKILL_SONG_OF_WIND							= 268, // Lv.1
		SKILL_SONG_OF_HUNTER						= 269, // Lv.1
		SKILL_SONG_OF_INVOCATION					= 270, // Lv.1
		SKILL_SONG_OF_VITALITY						= 304, // Lv.1
		SKILL_SONG_OF_VENGEANCE					= 305, // Lv.1
		SKILL_SONG_OF_FLAME_GUARD					= 306, // Lv.1
		SKILL_SONG_OF_STORM_GUARD					= 308, // Lv.1
		SKILL_SONG_OF_MEDITATION					= 363, // Lv.1
		SKILL_ARREST								= 402, // Lv.10
		SKILL_PSYCHO_SYMPHONY						= 407, // Lv.10
		SKILL_DEADLY_STRIKE						= 986, // Lv.15
		SKILL_BATTLE_WHISPER						= 988, // Lv.3
		SKILL_РИТМ_КРИТИЧЕСКОГО_УДАРА				= 1586, // Lv.1
		SKILL_РИТМ_МАГИИ							= 1588, // Lv.1
		SKILL_РИТМ_СРАЖЕНИЯ						= 1590, // Lv.1
		SKILL_РИТМ_БОЙЦА							= 1592, // Lv.1
		SKILL_РИТМ_МАГА							= 1599, // Lv.1
		//======= End Skill list of Swordsinger ID:21=======
		SKILL_DUMMY = 1;
	};
	public class ElvenScout extends ElvenFighter {
		public static final int
		//======= Start Skill list of ElvenScout ID:22=======
		SKILL_CHARM								= 15, // Lv.15
		SKILL_MORTAL_BLOW							= 16, // Lv.24
		SKILL_POISON_RECOVERY						= 21, // Lv.1
		SKILL_UNLOCK								= 27, // Lv.5
		SKILL_POWER_SHOT							= 56, // Lv.24
		SKILL_ELEMENTAL_HEAL						= 58, // Lv.18
		SKILL_CURE_BLEEDING						= 61, // Lv.1
		SKILL_ATTACK_AURA							= 77, // Lv.2
		SKILL_DEFENSE_AURA							= 91, // Lv.2
		SKILL_BLEED								= 96, // Lv.2
		SKILL_RAPID_SHOT							= 99, // Lv.1
		SKILL_STUNNING_SHOT						= 101, // Lv.3
		SKILL_ENTANGLE								= 102, // Lv.1
		SKILL_ULTIMATE_EVASION						= 111, // Lv.1
		SKILL_SPRINT								= 230, // Lv.1
		SKILL_ACCURACY								= 256, // Lv.1
		SKILL_VICIOUS_STANCE						= 312, // Lv.5
		//======= End Skill list of ElvenScout ID:22=======
		SKILL_DUMMY = 1;
	};

	public class PlainWalker extends ElvenScout {
		public static final int
		//======= Start Skill list of PlainWalker ID:23=======
		SKILL_SWITCH								= 12, // Lv.14
		SKILL_CHARM								= 15, // Lv.52
		SKILL_POISON_RECOVERY						= 21, // Lv.3
		SKILL_UNLOCK								= 27, // Lv.14
		SKILL_BACKSTAB								= 30, // Lv.37
		SKILL_LURE									= 51, // Lv.1
		SKILL_ELEMENTAL_HEAL						= 58, // Lv.55
		SKILL_FAKE_DEATH							= 60, // Lv.1
		SKILL_CURE_BLEEDING						= 61, // Lv.3
		SKILL_BLEED								= 96, // Lv.6
		SKILL_ENTANGLE								= 102, // Lv.16
		SKILL_ULTIMATE_EVASION						= 111, // Lv.2
		SKILL_SPIRIT_BARRIER						= 123, // Lv.3
		SKILL_SILENT_MOVE							= 221, // Lv.1
		SKILL_SPRINT								= 230, // Lv.2
		SKILL_DEADLY_BLOW							= 263, // Lv.37
		SKILL_CHAMELEON_REST						= 296, // Lv.1
		SKILL_VICIOUS_STANCE						= 312, // Lv.20
		SKILL_BLINDING_BLOW						= 321, // Lv.10
		SKILL_MORTAL_STRIKE						= 410, // Lv.3
		SKILL_SAND_BOMB							= 412, // Lv.10
		SKILL_SUMMON_TREASURE_KEY					= 419, // Lv.4
		SKILL_ESCAPE_SHACKLE						= 453, // Lv.1
		SKILL_FIND_TRAP							= 623, // Lv.1
		SKILL_REMOVE_TRAP							= 624, // Lv.1
		SKILL_SHADOW_STEP							= 821, // Lv.1
		//======= End Skill list of PlainWalker ID:23=======
		SKILL_DUMMY = 1;
	};
	public class SilverRanger extends ElvenScout {
		public static final int
		//======= Start Skill list of SilverRanger ID:24=======
		SKILL_CHARM								= 15, // Lv.52
		SKILL_DOUBLE_SHOT							= 19, // Lv.37
		SKILL_POISON_RECOVERY						= 21, // Lv.3
		SKILL_BURST_SHOT							= 24, // Lv.31
		SKILL_ELEMENTAL_HEAL						= 58, // Lv.55
		SKILL_CURE_BLEEDING						= 61, // Lv.3
		SKILL_RAPID_SHOT							= 99, // Lv.2
		SKILL_STUNNING_SHOT						= 101, // Lv.40
		SKILL_ENTANGLE								= 102, // Lv.16
		SKILL_SPIRIT_BARRIER						= 123, // Lv.3
		SKILL_SPRINT								= 230, // Lv.2
		SKILL_SOUL_OF_SAGITTARIUS					= 303, // Lv.4
		SKILL_VICIOUS_STANCE						= 312, // Lv.20
		SKILL_QUIVER_OF_ARROW_GRADE_A				= 323, // Lv.1
		SKILL_QUIVER_OF_ARROW_GRADE_S				= 324, // Lv.1
		SKILL_RAPID_FIRE							= 413, // Lv.8
		SKILL_SPIRIT_OF_SAGITTARIUS				= 415, // Lv.3
		SKILL_BLESSING_OF_SAGITTARIUS				= 416, // Lv.3
		SKILL_DETECTION							= 933, // Lv.1
		//======= End Skill list of SilverRanger ID:24=======
		SKILL_DUMMY = 1;
	};

	public class ElvenMage {
		public static final int
		//======= Start Skill list of ElvenMage ID:25=======
		SKILL_HEAL									= 1011, // Lv.6
		SKILL_CURE_POISON							= 1012, // Lv.1
		SKILL_BATTLE_HEAL							= 1015, // Lv.3
		SKILL_GROUP_HEAL							= 1027, // Lv.3
		SKILL_SHIELD								= 1040, // Lv.1
		SKILL_MIGHT								= 1068, // Lv.1
		SKILL_CURSE_WEAKNESS						= 1164, // Lv.1
		SKILL_WIND_STRIKE							= 1177, // Lv.5
		SKILL_ICE_BOLT								= 1184, // Lv.4
		SKILL_WIND_SHACKLE							= 1206, // Lv.1
		//======= End Skill list of ElvenMage ID:25=======
		SKILL_DUMMY = 1;
	};
	public class ElvenWizard extends ElvenMage {
		public static final int
		//======= Start Skill list of ElvenWizard ID:26=======
		SKILL_SLEEP								= 1069, // Lv.9
		SKILL_CONCENTRATION						= 1078, // Lv.2
		SKILL_SERVITOR_RECHARGE					= 1126, // Lv.6
		SKILL_SERVITOR_HEAL						= 1127, // Lv.12
		SKILL_BRIGHT_SERVITOR						= 1145, // Lv.1
		SKILL_CURSE_WEAKNESS						= 1164, // Lv.5
		SKILL_AURA_BURN							= 1172, // Lv.8
		SKILL_AQUA_SWIRL							= 1175, // Lv.8
		SKILL_FLAME_STRIKE							= 1181, // Lv.3
		SKILL_RESIST_AQUA							= 1182, // Lv.1
		SKILL_ICE_BOLT								= 1184, // Lv.6
		SKILL_WIND_SHACKLE							= 1206, // Lv.5
		SKILL_SURRENDER_TO_EARTH					= 1223, // Lv.1
		SKILL_SUMMON_BOXER_THE_UNICORN				= 1226, // Lv.4
		SKILL_SUMMON_MIRAGE_THE_UNICORN			= 1227, // Lv.4
		SKILL_SOLAR_SPARK							= 1264, // Lv.3
		SKILL_ENERGY_BOLT							= 1274, // Lv.4
		//======= End Skill list of ElvenWizard ID:26=======
		SKILL_DUMMY = 1;
	};
	public class Spellsinger extends ElvenWizard {
		public static final int
		//======= Start Skill list of Spellsinger ID:27=======
		SKILL_MANA_REGENERATION					= 1047, // Lv.4
		SKILL_CANCELLATION							= 1056, // Lv.12
		SKILL_SLEEP								= 1069, // Lv.42
		SKILL_SURRENDER_TO_WATER					= 1071, // Lv.14
		SKILL_SLEEPING_CLOUD						= 1072, // Lv.5
		SKILL_CURSE_WEAKNESS						= 1164, // Lv.19
		SKILL_CURSE_FEAR							= 1169, // Lv.14
		SKILL_FROST_WALL							= 1174, // Lv.22
		SKILL_RESIST_AQUA							= 1182, // Lv.3
		SKILL_FREEZING_SHACKLE						= 1183, // Lv.4
		SKILL_SURRENDER_TO_EARTH					= 1223, // Lv.15
		SKILL_AURA_FLARE							= 1231, // Lv.28
		SKILL_HYDRO_BLAST							= 1235, // Lv.28
		SKILL_FROST_BOLT							= 1236, // Lv.19
		SKILL_ICE_DAGGER							= 1237, // Lv.17
		SKILL_FREEZING_SKIN						= 1238, // Lv.3
		SKILL_SOLAR_FLARE							= 1265, // Lv.14
		SKILL_AURA_BOLT							= 1275, // Lv.14
		SKILL_SEED_OF_WATER						= 1286, // Lv.1
		SKILL_AURA_SYMPHONY						= 1288, // Lv.1
		SKILL_BLIZZARD								= 1290, // Lv.1
		SKILL_ELEMENTAL_SYMPHONY					= 1293, // Lv.1
		SKILL_AQUA_SPLASH							= 1295, // Lv.9
		SKILL_AURA_FLASH							= 1417, // Lv.5
		//======= End Skill list of Spellsinger ID:27=======
		SKILL_DUMMY = 1;
	};
	public class ElementalSummoner extends ElvenWizard {
		public static final int
		//======= Start Skill list of ElementalSummoner ID:28=======
		SKILL_SUMMON_LIFE_CUBIC					= 67, // Lv.7
		SKILL_SERVITOR_RECHARGE					= 1126, // Lv.34
		SKILL_SERVITOR_HEAL						= 1127, // Lv.45
		SKILL_SERVITOR_MAGIC_SHIELD				= 1139, // Lv.2
		SKILL_SERVITOR_PHYSICAL_SHIELD				= 1140, // Lv.3
		SKILL_SERVITOR_HASTE						= 1141, // Lv.2
		SKILL_BRIGHT_SERVITOR						= 1145, // Lv.3
		SKILL_WIND_SHACKLE							= 1206, // Lv.19
		SKILL_SUMMON_BOXER_THE_UNICORN				= 1226, // Lv.18
		SKILL_SUMMON_MIRAGE_THE_UNICORN			= 1227, // Lv.18
		SKILL_TRANSFER_PAIN						= 1262, // Lv.5
		SKILL_SUMMON_MERROW_THE_UNICORN			= 1277, // Lv.14
		SKILL_SUMMON_AQUA_CUBIC					= 1280, // Lv.9
		SKILL_SERVITOR_EMPOWERMENT					= 1299, // Lv.2
		SKILL_SERVITOR_CURE						= 1300, // Lv.3
		SKILL_SERVITOR_BLESSING					= 1301, // Lv.1
		SKILL_MASS_SUMMON_AQUA_CUBIC				= 1329, // Lv.9
		SKILL_SUMMON_UNICORN_SERAPHIM				= 1332, // Lv.10
		SKILL_BETRAY								= 1380, // Lv.10
		SKILL_MASS_SURRENDER_TO_WATER				= 1384, // Lv.5
		SKILL_SUMMON_FRIEND						= 1403, // Lv.1
		SKILL_SPIRIT_SHARING						= 1547, // Lv.3
		SKILL_DIMENSION_SPIRAL						= 1558, // Lv.14
		//======= End Skill list of ElementalSummoner ID:28=======
		SKILL_DUMMY = 1;
	};
	public class Oracle extends ElvenMage {
		public static final int
		//======= Start Skill list of Oracle ID:29=======
		SKILL_HEAL									= 1011, // Lv.18
		SKILL_CURE_POISON							= 1012, // Lv.2
		SKILL_RECHARGE								= 1013, // Lv.4
		SKILL_BATTLE_HEAL							= 1015, // Lv.15
		SKILL_RESURRECTION							= 1016, // Lv.2
		SKILL_GROUP_HEAL							= 1027, // Lv.15
		SKILL_DISRUPT_UNDEAD						= 1031, // Lv.8
		SKILL_RESIST_POISON						= 1033, // Lv.1
		SKILL_MENTAL_SHIELD						= 1035, // Lv.1
		SKILL_SHIELD								= 1040, // Lv.2
		SKILL_HOLY_WEAPON							= 1043, // Lv.1
		SKILL_REGENERATION							= 1044, // Lv.1
		SKILL_MIGHT								= 1068, // Lv.2
		SKILL_SLEEP								= 1069, // Lv.9
		SKILL_KISS_OF_EVA							= 1073, // Lv.1
		SKILL_CONCENTRATION						= 1078, // Lv.2
		SKILL_AGILITY								= 1087, // Lv.1
		SKILL_DRYAD_ROOT							= 1201, // Lv.9
		SKILL_WIND_WALK							= 1204, // Lv.2
		SKILL_WIND_SHACKLE							= 1206, // Lv.5
		SKILL_DECREASE_WEIGHT						= 1257, // Lv.1
		//======= End Skill list of Oracle ID:29=======
		SKILL_DUMMY = 1;
	};
	public class Elder extends Oracle {
		public static final int
		//======= Start Skill list of Elder ID:30=======
		SKILL_CURE_POISON							= 1012, // Lv.3
		SKILL_RECHARGE								= 1013, // Lv.32
		SKILL_RESURRECTION							= 1016, // Lv.7
		SKILL_VITALIZE								= 1020, // Lv.27
		SKILL_MIGHT_OF_HEAVEN						= 1028, // Lv.19
		SKILL_RESIST_POISON							= 1033, // Lv.3
		SKILL_MENTAL_SHIELD							= 1035, // Lv.4
		SKILL_SHIELD								= 1040, // Lv.3
		SKILL_REGENERATION							= 1044, // Lv.3
		SKILL_RETURN								= 1050, // Lv.2
		SKILL_MIGHT									= 1068, // Lv.3
		SKILL_SLEEP									= 1069, // Lv.42
		SKILL_KISS_OF_EVA							= 1073, // Lv.2
		SKILL_CONCENTRATION							= 1078, // Lv.6
		SKILL_AGILITY								= 1087, // Lv.3
		SKILL_WIND_SHACKLE							= 1206, // Lv.19
		SKILL_GREATER_HEAL							= 1217, // Lv.33
		SKILL_GREATER_GROUP_HEAL					= 1219, // Lv.29
		SKILL_BLESS_SHIELD							= 1243, // Lv.6
		SKILL_PARTY_RECALL							= 1255, // Lv.2
		SKILL_DECREASE_WEIGHT						= 1257, // Lv.3
		SKILL_RESIST_SHOCK							= 1259, // Lv.4
		SKILL_SERENADE_OF_EVA						= 1273, // Lv.13
		SKILL_WILD_MAGIC							= 1303, // Lv.2
		SKILL_ADVANCED_BLOCK						= 1304, // Lv.3
		SKILL_UNHOLY_RESISTANCE						= 1393, // Lv.3
		SKILL_TRANCE								= 1394, // Lv.10
		SKILL_ERASE									= 1395, // Lv.10
		SKILL_CLARITY								= 1397, // Lv.3
		SKILL_MANA_BURN								= 1398, // Lv.10
		SKILL_TURN_UNDEAD							= 1400, // Lv.10
		SKILL_MAJOR_HEAL							= 1401, // Lv.11
		SKILL_INVOCATION							= 1430, // Lv.5
		SKILL_IMPROVED_SHIELD_DEFENSE				= 1503, // Lv.1
		SKILL_IMPROVED_MOVEMENT						= 1504, // Lv.1
		SKILL_DIVINE_PUNISHMENT						= 1523, // Lv.13
		SKILL_SURRENDER_TO_THE_HOLY					= 1524, // Lv.42
		SKILL_DIVINE_CURSE							= 1525, // Lv.13
		SKILL_DIVINE_FLASH							= 1528, // Lv.13
		//======= End Skill list of Elder ID:30=======
		SKILL_DUMMY = 1;
	};
	
	public class DarkFighter {
		public static final int
		//======= Start Skill list of DarkFighter ID:31=======
		SKILL_POWER_STRIKE							= 3, // Lv.9
		SKILL_MORTAL_BLOW							= 16, // Lv.9
		SKILL_POWER_SHOT							= 56, // Lv.9
		SKILL_DRAIN_HEALTH							= 70, // Lv.2
		SKILL_ATTACK_AURA							= 77, // Lv.1
		SKILL_DEFENSE_AURA							= 91, // Lv.1
		//======= End Skill list of DarkFighter ID:31=======
		SKILL_DUMMY = 1;
	};

	public class PalusKnight extends DarkFighter {
		public static final int
		//======= Start Skill list of PalusKnight ID:32=======
		SKILL_CONFUSION							= 2, // Lv.4
		SKILL_AGGRESSION							= 28, // Lv.12
		SKILL_DRAIN_HEALTH							= 70, // Lv.16
		SKILL_ATTACK_AURA							= 77, // Lv.2
		SKILL_DEFENSE_AURA							= 91, // Lv.2
		SKILL_FREEZING_STRIKE						= 105, // Lv.2
		SKILL_ULTIMATE_DEFENSE						= 110, // Lv.1
		SKILL_DEFLECT_ARROW						= 112, // Lv.2
		SKILL_POWER_BREAK							= 115, // Lv.2
		SKILL_POISON								= 129, // Lv.1
		SKILL_STING								= 223, // Lv.12
		//======= End Skill list of PalusKnight ID:32=======
		SKILL_DUMMY = 1;
	};

	public class ShillenKnight extends PalusKnight {
		public static final int
		//======= Start Skill list of ShillenKnight ID:33=======
		SKILL_CONFUSION							= 2, // Lv.19
		SKILL_AURA_OF_HATE							= 18, // Lv.37
		SKILL_SUMMON_VAMPIRIC_CUBIC				= 22, // Lv.7
		SKILL_AGGRESSION							= 28, // Lv.49
		SKILL_SUMMON_PHANTOM_CUBIC					= 33, // Lv.8
		SKILL_DRAIN_HEALTH							= 70, // Lv.53
		SKILL_CORPSE_PLAGUE						= 103, // Lv.4
		SKILL_FREEZING_STRIKE						= 105, // Lv.24
		SKILL_ULTIMATE_DEFENSE						= 110, // Lv.2
		SKILL_DEFLECT_ARROW						= 112, // Lv.4
		SKILL_POWER_BREAK							= 115, // Lv.17
		SKILL_HEX									= 122, // Lv.15
		SKILL_POISON								= 129, // Lv.5
		SKILL_STING								= 223, // Lv.49
		SKILL_SUMMON_VIPER_CUBIC					= 278, // Lv.6
		SKILL_LIGHTNING_STRIKE						= 279, // Lv.5
		SKILL_GUARD_STANCE							= 288, // Lv.4
		SKILL_LIFE_LEECH							= 289, // Lv.15
		SKILL_SHIELD_FORTRESS						= 322, // Lv.6
		SKILL_JUDGMENT								= 401, // Lv.10
		SKILL_ARREST								= 402, // Lv.10
		SKILL_BANISH_SERAPH						= 450, // Lv.10
		SKILL_VANGUARD								= 813, // Lv.1
		SKILL_SHIELD_DEFLECT_MAGIC					= 916, // Lv.4
		SKILL_COMBAT_AURA							= 982, // Lv.3
		SKILL_SHIELD_STRIKE						= 984, // Lv.15
		//======= End Skill list of ShillenKnight ID:33=======
		SKILL_DUMMY = 1;
	};

	public class Bladedancer extends PalusKnight {
		public static final int
		//======= Start Skill list of Bladedancer ID:34=======
		SKILL_CONFUSION							= 2, // Lv.19
		SKILL_DRAIN_HEALTH							= 70, // Lv.53
		SKILL_POISON_BLADE_DANCE					= 84, // Lv.3
		SKILL_FREEZING_STRIKE						= 105, // Lv.24
		SKILL_POWER_BREAK							= 115, // Lv.17
		SKILL_HEX									= 122, // Lv.15
		SKILL_POISON								= 129, // Lv.5
		SKILL_STING								= 223, // Lv.49
		SKILL_DANCE_OF_THE_WARRIOR					= 271, // Lv.1
		SKILL_DANCE_OF_INSPIRATION					= 272, // Lv.1
		SKILL_DANCE_OF_THE_MYSTIC					= 273, // Lv.1
		SKILL_DANCE_OF_FIRE						= 274, // Lv.1
		SKILL_DANCE_OF_FURY						= 275, // Lv.1
		SKILL_DANCE_OF_CONCENTRATION				= 276, // Lv.1
		SKILL_DANCE_OF_LIGHT						= 277, // Lv.1
		SKILL_DANCE_OF_AQUA_GUARD					= 307, // Lv.1
		SKILL_DANCE_OF_EARTH_GUARD					= 309, // Lv.1
		SKILL_DANCE_OF_THE_VAMPIRE					= 310, // Lv.1
		SKILL_DANCE_OF_PROTECTION					= 311, // Lv.1
		SKILL_ARREST								= 402, // Lv.10
		SKILL_DEMONIC_BLADE_DANCE					= 408, // Lv.10
		SKILL_DEADLY_STRIKE						= 986, // Lv.15
		SKILL_DEFENSE_MOTION						= 989, // Lv.1
		SKILL_ТЕМНЫЙ_РИТМ_КРИТИЧЕСКОГО_УДАРА		= 1585, // Lv.1
		SKILL_ТЕМНЫЙ_РИТМ_МАГИИ					= 1587, // Lv.1
		SKILL_ТЕМНЫЙ_РИТМ_СРАЖЕНИЯ					= 1589, // Lv.1
		SKILL_ТЕМНЫЙ_РИТМ_БОЙЦА					= 1591, // Lv.1
		SKILL_ТЕМНЫЙ_РИТМ_МАГА						= 1598, // Lv.1
		//======= End Skill list of Bladedancer ID:34=======
		SKILL_DUMMY = 1;
	};

	public class Assasin extends DarkFighter {
		public static final int
		//======= Start Skill list of Assasin ID:35=======
		SKILL_CONFUSION							= 2, // Lv.4
		SKILL_MORTAL_BLOW							= 16, // Lv.24
		SKILL_UNLOCK								= 27, // Lv.5
		SKILL_POWER_SHOT							= 56, // Lv.24
		SKILL_DRAIN_HEALTH							= 70, // Lv.16
		SKILL_ATTACK_AURA							= 77, // Lv.2
		SKILL_DEFENSE_AURA							= 91, // Lv.2
		SKILL_BLEED								= 96, // Lv.2
		SKILL_RAPID_SHOT							= 99, // Lv.1
		SKILL_STUNNING_SHOT						= 101, // Lv.3
		SKILL_FREEZING_STRIKE						= 105, // Lv.2
		SKILL_ULTIMATE_EVASION						= 111, // Lv.1
		SKILL_POWER_BREAK							= 115, // Lv.2
		SKILL_POISON								= 129, // Lv.1
		SKILL_STING								= 223, // Lv.12
		SKILL_ACCURACY								= 256, // Lv.1
		SKILL_VICIOUS_STANCE						= 312, // Lv.5
		//======= End Skill list of Assasin ID:35=======
		SKILL_DUMMY = 1;
	};

	public class AbyssWalker extends Assasin {
		public static final int
		//======= Start Skill list of AbyssWalker ID:36=======
		SKILL_CONFUSION							= 2, // Lv.19
		SKILL_TRICK								= 11, // Lv.12
		SKILL_UNLOCK								= 27, // Lv.14
		SKILL_BACKSTAB								= 30, // Lv.37
		SKILL_LURE									= 51, // Lv.1
		SKILL_DRAIN_HEALTH							= 70, // Lv.53
		SKILL_BLEED								= 96, // Lv.6
		SKILL_FREEZING_STRIKE						= 105, // Lv.24
		SKILL_VEIL									= 106, // Lv.14
		SKILL_ULTIMATE_EVASION						= 111, // Lv.2
		SKILL_POWER_BREAK							= 115, // Lv.17
		SKILL_HEX									= 122, // Lv.15
		SKILL_POISON								= 129, // Lv.5
		SKILL_SILENT_MOVE							= 221, // Lv.1
		SKILL_STING								= 223, // Lv.49
		SKILL_DEADLY_BLOW							= 263, // Lv.37
		SKILL_VICIOUS_STANCE						= 312, // Lv.20
		SKILL_BLINDING_BLOW						= 321, // Lv.10
		SKILL_MORTAL_STRIKE						= 410, // Lv.3
		SKILL_SAND_BOMB							= 412, // Lv.10
		SKILL_SUMMON_TREASURE_KEY					= 419, // Lv.4
		SKILL_ESCAPE_SHACKLE						= 453, // Lv.1
		SKILL_FIND_TRAP							= 623, // Lv.1
		SKILL_REMOVE_TRAP							= 624, // Lv.1
		SKILL_SHADOW_STEP							= 821, // Lv.1
		//======= End Skill list of AbyssWalker ID:36=======
		SKILL_DUMMY = 1;
	};

	public class PhantomRanger extends Assasin {
		public static final int
		//======= Start Skill list of PhantomRanger ID:37=======
		SKILL_CONFUSION							= 2, // Lv.19
		SKILL_DOUBLE_SHOT							= 19, // Lv.37
		SKILL_DRAIN_HEALTH							= 70, // Lv.53
		SKILL_RAPID_SHOT							= 99, // Lv.2
		SKILL_STUNNING_SHOT						= 101, // Lv.40
		SKILL_FREEZING_STRIKE						= 105, // Lv.24
		SKILL_POWER_BREAK							= 115, // Lv.17
		SKILL_HEX									= 122, // Lv.15
		SKILL_POISON								= 129, // Lv.5
		SKILL_STING								= 223, // Lv.49
		SKILL_SOUL_OF_SAGITTARIUS					= 303, // Lv.4
		SKILL_VICIOUS_STANCE						= 312, // Lv.20
		SKILL_FATAL_COUNTER						= 314, // Lv.16
		SKILL_QUIVER_OF_ARROW_GRADE_A				= 323, // Lv.1
		SKILL_QUIVER_OF_ARROW_GRADE_S				= 324, // Lv.1
		SKILL_DEAD_EYE								= 414, // Lv.8
		SKILL_SPIRIT_OF_SAGITTARIUS				= 415, // Lv.3
		SKILL_PAIN_OF_SAGITTARIUS					= 417, // Lv.5
		SKILL_DETECTION							= 933, // Lv.1
		//======= End Skill list of PhantomRanger ID:37=======
		SKILL_DUMMY = 1;
	};

	public class DarkMage {
		public static final int
		//======= Start Skill list of DarkMage ID:38=======
		SKILL_HEAL									= 1011, // Lv.6
		SKILL_CURE_POISON							= 1012, // Lv.1
		SKILL_BATTLE_HEAL							= 1015, // Lv.3
		SKILL_GROUP_HEAL							= 1027, // Lv.3
		SKILL_SHIELD								= 1040, // Lv.1
		SKILL_MIGHT								= 1068, // Lv.1
		SKILL_VAMPIRIC_TOUCH						= 1147, // Lv.2
		SKILL_CURSE_POISON							= 1168, // Lv.1
		SKILL_WIND_STRIKE							= 1177, // Lv.5
		SKILL_ICE_BOLT								= 1184, // Lv.4
		SKILL_WIND_SHACKLE							= 1206, // Lv.1
		//======= End Skill list of DarkMage ID:38=======
		SKILL_DUMMY = 1;
	};

	public class DarkWizard extends DarkMage {
		public static final int
		//======= Start Skill list of DarkWizard ID:39=======
		SKILL_SLEEP								= 1069, // Lv.9
		SKILL_CONCENTRATION						= 1078, // Lv.2
		SKILL_SERVITOR_RECHARGE					= 1126, // Lv.6
		SKILL_SERVITOR_HEAL						= 1127, // Lv.12
		SKILL_SUMMON_SHADOW						= 1128, // Lv.4
		SKILL_MIGHTY_SERVITOR						= 1146, // Lv.1
		SKILL_VAMPIRIC_TOUCH						= 1147, // Lv.6
		SKILL_CORPSE_LIFE_DRAIN					= 1151, // Lv.2
		SKILL_BODY_TO_MIND							= 1157, // Lv.1
		SKILL_SLOW									= 1160, // Lv.1
		SKILL_POISONOUS_CLOUD						= 1167, // Lv.2
		SKILL_CURSE_POISON							= 1168, // Lv.3
		SKILL_AURA_BURN							= 1172, // Lv.8
		SKILL_TWISTER								= 1178, // Lv.8
		SKILL_FLAME_STRIKE							= 1181, // Lv.3
		SKILL_ICE_BOLT								= 1184, // Lv.6
		SKILL_WIND_SHACKLE							= 1206, // Lv.5
		SKILL_CURSE_CHAOS							= 1222, // Lv.1
		SKILL_SURRENDER_TO_POISON					= 1224, // Lv.3
		SKILL_SUMMON_SILHOUETTE					= 1228, // Lv.4
		SKILL_SHADOW_SPARK							= 1266, // Lv.3
		//======= End Skill list of DarkWizard ID:39=======
		SKILL_DUMMY = 1;
	};

	public class Spellhowler extends DarkWizard {
		public static final int
		//======= Start Skill list of Spellhowler ID:40=======
		SKILL_SILENCE								= 1064, // Lv.14
		SKILL_SLEEP								= 1069, // Lv.42
		SKILL_SURRENDER_TO_WIND					= 1074, // Lv.14
		SKILL_DEATH_SPIKE							= 1148, // Lv.13
		SKILL_CORPSE_LIFE_DRAIN					= 1151, // Lv.16
		SKILL_BODY_TO_MIND							= 1157, // Lv.5
		SKILL_CURSE_DEATH_LINK						= 1159, // Lv.22
		SKILL_SLOW									= 1160, // Lv.15
		SKILL_POISONOUS_CLOUD						= 1167, // Lv.6
		SKILL_CURSE_POISON							= 1168, // Lv.7
		SKILL_CURSE_FEAR							= 1169, // Lv.14
		SKILL_TEMPEST								= 1176, // Lv.15
		SKILL_CURSE_CHAOS							= 1222, // Lv.15
		SKILL_SURRENDER_TO_POISON					= 1224, // Lv.17
		SKILL_VAMPIRIC_CLAW						= 1234, // Lv.28
		SKILL_HURRICANE							= 1239, // Lv.28
		SKILL_SHADOW_FLARE							= 1267, // Lv.14
		SKILL_SEED_OF_WIND							= 1287, // Lv.1
		SKILL_AURA_SYMPHONY						= 1288, // Lv.1
		SKILL_DEMON_WIND							= 1291, // Lv.1
		SKILL_ELEMENTAL_STORM						= 1294, // Lv.1
		SKILL_AURA_FLASH							= 1417, // Lv.5
		//======= End Skill list of Spellhowler ID:40=======
		SKILL_DUMMY = 1;
	};

	public class PhantomSummoner extends DarkWizard {
		public static final int
		//======= Start Skill list of PhantomSummoner ID:41=======
		SKILL_SUMMON_PHANTOM_CUBIC					= 33, // Lv.8
		SKILL_SERVITOR_RECHARGE					= 1126, // Lv.34
		SKILL_SERVITOR_HEAL						= 1127, // Lv.45
		SKILL_SUMMON_SHADOW						= 1128, // Lv.18
		SKILL_SERVITOR_MAGIC_SHIELD				= 1139, // Lv.2
		SKILL_SERVITOR_PHYSICAL_SHIELD				= 1140, // Lv.3
		SKILL_SERVITOR_HASTE						= 1141, // Lv.2
		SKILL_MIGHTY_SERVITOR						= 1146, // Lv.3
		SKILL_CURSE_POISON							= 1168, // Lv.7
		SKILL_WIND_SHACKLE							= 1206, // Lv.19
		SKILL_SUMMON_SILHOUETTE					= 1228, // Lv.18
		SKILL_TRANSFER_PAIN						= 1262, // Lv.5
		SKILL_SUMMON_SOULLESS						= 1278, // Lv.14
		SKILL_SUMMON_SPARK_CUBIC					= 1281, // Lv.9
		SKILL_SERVITOR_EMPOWERMENT					= 1299, // Lv.2
		SKILL_SERVITOR_CURE						= 1300, // Lv.3
		SKILL_SERVITOR_BLESSING					= 1301, // Lv.1
		SKILL_MASS_SUMMON_PHANTOM_CUBIC			= 1330, // Lv.8
		SKILL_SUMMON_NIGHTSHADE					= 1333, // Lv.10
		SKILL_BETRAY								= 1380, // Lv.10
		SKILL_MASS_SURRENDER_TO_WIND				= 1385, // Lv.5
		SKILL_SUMMON_FRIEND						= 1403, // Lv.1
		SKILL_DEATH_SPIKE							= 1530, // Lv.13
		SKILL_SPIRIT_SHARING						= 1547, // Lv.3
		SKILL_DIMENSION_SPIRAL						= 1558, // Lv.14
		//======= End Skill list of PhantomSummoner ID:41=======
		SKILL_DUMMY = 1;
	};

	public class ShillenOracle extends DarkMage {
		public static final int
		//======= Start Skill list of ShillenOracle ID:42=======
		SKILL_HEAL									= 1011, // Lv.18
		SKILL_CURE_POISON							= 1012, // Lv.2
		SKILL_RECHARGE								= 1013, // Lv.4
		SKILL_BATTLE_HEAL							= 1015, // Lv.15
		SKILL_RESURRECTION							= 1016, // Lv.2
		SKILL_GROUP_HEAL							= 1027, // Lv.15
		SKILL_DISRUPT_UNDEAD						= 1031, // Lv.8
		SKILL_MENTAL_SHIELD						= 1035, // Lv.1
		SKILL_SHIELD								= 1040, // Lv.2
		SKILL_EMPOWER								= 1059, // Lv.1
		SKILL_MIGHT								= 1068, // Lv.2
		SKILL_SLEEP								= 1069, // Lv.9
		SKILL_KISS_OF_EVA							= 1073, // Lv.1
		SKILL_FOCUS								= 1077, // Lv.1
		SKILL_CONCENTRATION						= 1078, // Lv.2
		SKILL_RESIST_WIND							= 1189, // Lv.1
		SKILL_DRYAD_ROOT							= 1201, // Lv.9
		SKILL_WIND_WALK							= 1204, // Lv.2
		SKILL_WIND_SHACKLE							= 1206, // Lv.5
		SKILL_VAMPIRIC_RAGE						= 1268, // Lv.1
		//======= End Skill list of ShillenOracle ID:42=======
		SKILL_DUMMY = 1;
	};

	public class ShillenElder extends ShillenOracle {
		public static final int
		//======= Start Skill list of ShillenElder ID:43=======
		SKILL_CURE_POISON							= 1012, // Lv.3
		SKILL_RECHARGE								= 1013, // Lv.32
		SKILL_PURIFY								= 1018, // Lv.3
		SKILL_MENTAL_SHIELD						= 1035, // Lv.4
		SKILL_SHIELD								= 1040, // Lv.3
		SKILL_EMPOWER								= 1059, // Lv.3
		SKILL_MIGHT								= 1068, // Lv.3
		SKILL_KISS_OF_EVA							= 1073, // Lv.2
		SKILL_FOCUS								= 1077, // Lv.3
		SKILL_CONCENTRATION						= 1078, // Lv.6
		SKILL_RESIST_WIND							= 1189, // Lv.3
		SKILL_DRYAD_ROOT							= 1201, // Lv.33
		SKILL_WIND_SHACKLE							= 1206, // Lv.19
		SKILL_GREATER_HEAL							= 1217, // Lv.33
		SKILL_GREATER_GROUP_HEAL					= 1219, // Lv.29
		SKILL_GUIDANCE								= 1240, // Lv.3
		SKILL_DEATH_WHISPER						= 1242, // Lv.3
		SKILL_VAMPIRIC_RAGE						= 1268, // Lv.4
		SKILL_WILD_MAGIC							= 1303, // Lv.2
		SKILL_HOLY_RESISTANCE						= 1392, // Lv.3
		SKILL_ERASE								= 1395, // Lv.10
		SKILL_MANA_BURN							= 1398, // Lv.10
		SKILL_INVOCATION							= 1430, // Lv.5
		SKILL_IMPROVED_MAGIC						= 1500, // Lv.1
		SKILL_IMPROVED_CRITICAL_ATTACK				= 1502, // Lv.1
		SKILL_DIVINE_PUNISHMENT					= 1523, // Lv.13
		SKILL_DIVINE_CURSE							= 1525, // Lv.13
		SKILL_DIVINE_FLASH							= 1528, // Lv.13
		SKILL_BLESS_THE_BLOOD						= 1531, // Lv.7
		SKILL_STIGMA_OF_SHILIEN					= 1539, // Lv.4
		//======= End Skill list of ShillenElder ID:43=======
		SKILL_DUMMY = 1;
	};

	public class OrcFighter {
		public static final int
		//======= Start Skill list of OrcFighter ID:44=======
		SKILL_POWER_STRIKE							= 3, // Lv.9
		SKILL_IRON_PUNCH							= 29, // Lv.9
		SKILL_RELAX								= 226, // Lv.1
		//======= End Skill list of OrcFighter ID:44=======
		SKILL_DUMMY = 1;
	};

	public class OrcRaider extends OrcFighter {
		public static final int
		//======= Start Skill list of OrcRaider ID:45=======
		SKILL_BANDAGE								= 34, // Lv.1
		SKILL_RAGE									= 94, // Lv.1
		SKILL_STUN_ATTACK							= 100, // Lv.15
		SKILL_BATTLE_ROAR							= 121, // Lv.1
		SKILL_GUTS									= 139, // Lv.1
		SKILL_FRENZY								= 176, // Lv.1
		SKILL_WILD_SWEEP							= 245, // Lv.15
		SKILL_POWER_SMASH							= 255, // Lv.15
		SKILL_ACCURACY								= 256, // Lv.1
		SKILL_LIONHEART							= 287, // Lv.1
		SKILL_VICIOUS_STANCE						= 312, // Lv.5
		//======= End Skill list of OrcRaider ID:45=======
		SKILL_DUMMY = 1;
	};

	public class Destroyer extends OrcRaider {
		public static final int
		//======= Start Skill list of Destroyer ID:46=======
		SKILL_BANDAGE								= 34, // Lv.3
		SKILL_WHIRLWIND							= 36, // Lv.37
		SKILL_RAGE									= 94, // Lv.2
		SKILL_BATTLE_ROAR							= 121, // Lv.6
		SKILL_GUTS									= 139, // Lv.3
		SKILL_FRENZY								= 176, // Lv.3
		SKILL_FATAL_STRIKE							= 190, // Lv.37
		SKILL_HAMMER_CRUSH							= 260, // Lv.37
		SKILL_LIONHEART							= 287, // Lv.3
		SKILL_VICIOUS_STANCE						= 312, // Lv.20
		SKILL_CRUSH_OF_DOOM						= 315, // Lv.16
		SKILL_WRATH								= 320, // Lv.10
		SKILL_ZEALOT								= 420, // Lv.3
		SKILL_POLEARM_ACCURACY						= 422, // Lv.3
		SKILL_DARK_FORM							= 423, // Lv.3
		SKILL_WAR_FRENZY							= 424, // Lv.3
		SKILL_RUSH									= 994, // Lv.1
		//======= End Skill list of Destroyer ID:46=======
		SKILL_DUMMY = 1;
	};

	public class OrcMonk extends OrcFighter {
		public static final int
		//======= Start Skill list of OrcMonk ID:47=======
		SKILL_IRON_PUNCH							= 29, // Lv.24
		SKILL_FOCUSED_FORCE						= 50, // Lv.2
		SKILL_FORCE_BLASTER						= 54, // Lv.12
		SKILL_BEAR_SPIRIT_TOTEM					= 76, // Lv.1
		SKILL_WOLF_SPIRIT_TOTEM					= 83, // Lv.1
		SKILL_CRIPPLE								= 95, // Lv.5
		SKILL_STUNNING_FIST						= 120, // Lv.15
		SKILL_HURRICANE_ASSAULT					= 284, // Lv.3
		//======= End Skill list of OrcMonk ID:47=======
		SKILL_DUMMY = 1;
	};

	public class Tyrant extends OrcMonk {
		public static final int
		//======= Start Skill list of Tyrant ID:48=======
		SKILL_FORCE_BURST							= 17, // Lv.34
		SKILL_FORCE_STORM							= 35, // Lv.28
		SKILL_FOCUSED_FORCE						= 50, // Lv.7
		SKILL_FORCE_BLASTER						= 54, // Lv.49
		SKILL_PUNCH_OF_DOOM						= 81, // Lv.3
		SKILL_CRIPPLE								= 95, // Lv.20
		SKILL_OGRE_SPIRIT_TOTEM					= 109, // Lv.1
		SKILL_FURY_FISTS							= 222, // Lv.1
		SKILL_BURNING_FIST							= 280, // Lv.37
		SKILL_SOUL_BREAKER							= 281, // Lv.37
		SKILL_PUMA_SPIRIT_TOTEM					= 282, // Lv.1
		SKILL_HURRICANE_ASSAULT					= 284, // Lv.40
		SKILL_BISON_SPIRIT_TOTEM					= 292, // Lv.1
		SKILL_RABBIT_SPIRIT_TOTEM					= 298, // Lv.1
		SKILL_ZEALOT								= 420, // Lv.3
		SKILL_DARK_FORM							= 423, // Lv.3
		SKILL_WAR_FRENZY							= 424, // Lv.3
		SKILL_HAWK_SPIRIT_TOTEM					= 425, // Lv.1
		SKILL_BREAK_DURESS							= 461, // Lv.2
		SKILL_RUSH									= 994, // Lv.1
		//======= End Skill list of Tyrant ID:48=======
		SKILL_DUMMY = 1;
	};

	public class OrcMage {
		public static final int
		//======= Start Skill list of OrcMage ID:49=======
		SKILL_SOUL_CRY								= 1001, // Lv.2
		SKILL_CHANT_OF_BATTLE						= 1007, // Lv.1
		SKILL_SOUL_SHIELD							= 1010, // Lv.1
		SKILL_LIFE_DRAIN							= 1090, // Lv.2
		SKILL_FEAR									= 1092, // Lv.1
		SKILL_VENOM								= 1095, // Lv.2
		SKILL_DREAMING_SPIRIT						= 1097, // Lv.2
		SKILL_CHILL_FLAME							= 1100, // Lv.2
		//======= End Skill list of OrcMage ID:49=======
		SKILL_DUMMY = 1;
	};

	public class OrcShaman extends OrcMage {
		public static final int
		//======= Start Skill list of OrcShaman ID:50=======
		SKILL_STUN_ATTACK							= 100, // Lv.12
		SKILL_SOUL_CRY								= 1001, // Lv.4
		SKILL_FLAME_CHANT							= 1002, // Lv.1
		SKILL_PAAGRIAN_GIFT						= 1003, // Lv.1
		SKILL_BLESSINGS_OF_PAAGRIO					= 1005, // Lv.1
		SKILL_CHANT_OF_FIRE						= 1006, // Lv.1
		SKILL_CHANT_OF_BATTLE						= 1007, // Lv.2
		SKILL_CHANT_OF_SHIELDING					= 1009, // Lv.2
		SKILL_SOUL_SHIELD							= 1010, // Lv.3
		SKILL_LIFE_DRAIN							= 1090, // Lv.6
		SKILL_FEAR									= 1092, // Lv.5
		SKILL_VENOM								= 1095, // Lv.3
		SKILL_SEAL_OF_CHAOS						= 1096, // Lv.2
		SKILL_DREAMING_SPIRIT						= 1097, // Lv.6
		SKILL_SEAL_OF_SLOW							= 1099, // Lv.1
		SKILL_BLAZE_QUAKE							= 1101, // Lv.2
		SKILL_AURA_SINK							= 1102, // Lv.2
		SKILL_MADNESS								= 1105, // Lv.4
		SKILL_FROST_FLAME							= 1107, // Lv.2
		SKILL_SEAL_OF_BINDING						= 1208, // Lv.3
		SKILL_SEAL_OF_POISON						= 1209, // Lv.2
		SKILL_CHANT_OF_LIFE						= 1229, // Lv.4
		//======= End Skill list of OrcShaman ID:50=======
		SKILL_DUMMY = 1;
	};

	public class Overlord extends OrcShaman {
		public static final int
		//======= Start Skill list of Overlord ID:51=======
		SKILL_HAMMER_CRUSH							= 260, // Lv.35
		SKILL_BURNING_CHOP							= 927, // Lv.14
		SKILL_SOUL_CRY								= 1001, // Lv.10
		SKILL_PAAGRIAN_GIFT						= 1003, // Lv.3
		SKILL_THE_WISDOM_OF_PAAGRIO				= 1004, // Lv.3
		SKILL_BLESSINGS_OF_PAAGRIO					= 1005, // Lv.3
		SKILL_THE_GLORY_OF_PAAGRIO					= 1008, // Lv.3
		SKILL_FEAR									= 1092, // Lv.19
		SKILL_SEAL_OF_CHAOS						= 1096, // Lv.16
		SKILL_DREAMING_SPIRIT						= 1097, // Lv.20
		SKILL_SEAL_OF_SLOW							= 1099, // Lv.15
		SKILL_SEAL_OF_WINTER						= 1104, // Lv.14
		SKILL_MADNESS								= 1105, // Lv.18
		SKILL_SEAL_OF_FLAME							= 1108, // Lv.4
		SKILL_SEAL_OF_BINDING						= 1208, // Lv.17
		SKILL_SEAL_OF_POISON						= 1209, // Lv.6
		SKILL_SEAL_OF_GLOOM							= 1210, // Lv.4
		SKILL_SEAL_OF_MIRAGE						= 1213, // Lv.13
		SKILL_STEAL_ESSENCE							= 1245, // Lv.14
		SKILL_SEAL_OF_SILENCE						= 1246, // Lv.12
		SKILL_SEAL_OF_SCOURGE						= 1247, // Lv.14
		SKILL_SEAL_OF_SUSPENSION					= 1248, // Lv.12
		SKILL_THE_VISION_OF_PAAGRIO					= 1249, // Lv.3
		SKILL_UNDER_THE_PROTECTION_OF_PAAGRIO		= 1250, // Lv.3
		SKILL_THE_HEART_OF_PAAGRIO					= 1256, // Lv.13
		SKILL_THE_TACT_OF_PAAGRIO					= 1260, // Lv.3
		SKILL_THE_RAGE_OF_PAAGRIO					= 1261, // Lv.2
		SKILL_PAAGRIAN_HASTE						= 1282, // Lv.2
		SKILL_SOUL_GUARD							= 1283, // Lv.13
		SKILL_THE_HONOR_OF_PAAGRIO					= 1305, // Lv.5
		SKILL_RITUAL_OF_LIFE						= 1306, // Lv.6
		SKILL_COMBAT_OF_PAAGRIO						= 1536, // Lv.1
		SKILL_CRITICAL_OF_PAAGRIO					= 1537, // Lv.1
		SKILL_CONDITION_OF_PAAGRIO					= 1538, // Lv.1
		SKILL_FURY_OF_PAAGRIO						= 1563, // Lv.2
		//======= End Skill list of Overlord ID:51=======
		SKILL_DUMMY = 1;
	};

	public class Warcryer extends OrcShaman {
		public static final int
		//======= Start Skill list of Warcryer ID:52=======
		SKILL_HAMMER_CRUSH							= 260, // Lv.35
		SKILL_BURNING_CHOP							= 927, // Lv.14
		SKILL_SOUL_CRY								= 1001, // Lv.10
		SKILL_FLAME_CHANT							= 1002, // Lv.3
		SKILL_CHANT_OF_FIRE							= 1006, // Lv.3
		SKILL_CHANT_OF_BATTLE						= 1007, // Lv.3
		SKILL_CHANT_OF_SHIELDING					= 1009, // Lv.3
		SKILL_FEAR									= 1092, // Lv.19
		SKILL_VENOM									= 1095, // Lv.5
		SKILL_DREAMING_SPIRIT						= 1097, // Lv.20
		SKILL_AURA_SINK								= 1102, // Lv.6
		SKILL_MADNESS								= 1105, // Lv.18
		SKILL_CHANT_OF_LIFE							= 1229, // Lv.18
		SKILL_FREEZING_FLAME						= 1244, // Lv.4
		SKILL_STEAL_ESSENCE							= 1245, // Lv.14
		SKILL_CHANT_OF_FURY							= 1251, // Lv.2
		SKILL_CHANT_OF_EVASION						= 1252, // Lv.3
		SKILL_CHANT_OF_RAGE							= 1253, // Lv.3
		SKILL_CHANT_OF_REVENGE						= 1284, // Lv.3
		SKILL_CHANT_OF_PREDATOR						= 1308, // Lv.3
		SKILL_CHANT_OF_EAGLE						= 1309, // Lv.3
		SKILL_CHANT_OF_VAMPIRE						= 1310, // Lv.4
		SKILL_WAR_CHANT								= 1390, // Lv.3
		SKILL_EARTH_CHANT							= 1391, // Lv.3
		SKILL_CHANT_OF_COMBAT						= 1517, // Lv.1
		SKILL_CHANT_OF_CRITICAL_ATTACK				= 1518, // Lv.1
		SKILL_CHANT_OF_BLOOD_AWAKENING				= 1519, // Lv.1
		SKILL_CHANT_OF_MOVEMENT						= 1535, // Lv.1
		SKILL_CHANT_OF_BERSERKER					= 1562, // Lv.2
		//======= End Skill list of Warcryer ID:52=======
		SKILL_DUMMY = 1;
	};

	public class DwarvenFighter {
		public static final int
		//======= Start Skill list of DwarvenFighter ID:53=======
		SKILL_SWEEPER								= 42, // Lv.1
		SKILL_SPOIL									= 254, // Lv.1
		//======= End Skill list of DwarvenFighter ID:53=======
		SKILL_DUMMY = 1;
	};

	public class Scavenger extends DwarvenFighter {
		public static final int
		//======= Start Skill list of Scavenger ID:54=======
		SKILL_BANDAGE								= 34, // Lv.1
		SKILL_STUN_ATTACK							= 100, // Lv.15
		SKILL_WILD_SWEEP							= 245, // Lv.15
		SKILL_SPOIL									= 254, // Lv.4
		SKILL_SPOIL_FESTIVAL						= 302, // Lv.2
		SKILL_FESTIVE_SWEEPER						= 444, // Lv.1
		//======= End Skill list of Scavenger ID:54=======
		SKILL_DUMMY = 1;
	};

	public class BountyHunter extends Scavenger {
		public static final int
		//======= Start Skill list of BountyHunter ID:55=======
		SKILL_BACKSTAB								= 30, // Lv.37
		SKILL_BANDAGE								= 34, // Lv.3
		SKILL_WHIRLWIND								= 36, // Lv.37
		SKILL_FAKE_DEATH							= 60, // Lv.1
		SKILL_FATAL_STRIKE							= 190, // Lv.37
		SKILL_SPOIL									= 254, // Lv.11
		SKILL_HAMMER_CRUSH							= 260, // Lv.37
		SKILL_DEADLY_BLOW							= 263, // Lv.37
		SKILL_SPOIL_FESTIVAL						= 302, // Lv.9
		SKILL_WRATH									= 320, // Lv.10
		SKILL_POLEARM_ACCURACY						= 422, // Lv.3
		SKILL_WAR_FRENZY							= 424, // Lv.3
		SKILL_CRUSHING_STRIKE						= 997, // Lv.15
		SKILL_BLAZING_BOOST							= 998, // Lv.1
		//======= End Skill list of BountyHunter ID:55=======
		SKILL_DUMMY = 1;
	};

	public class Artisan extends DwarvenFighter {
		public static final int
		//======= Start Skill list of Artisan ID:56=======
		SKILL_SUMMON_MECHANIC_GOLEM					= 25, // Lv.2
		SKILL_BANDAGE								= 34, // Lv.1
		SKILL_STUN_ATTACK							= 100, // Lv.15
		SKILL_WILD_SWEEP							= 245, // Lv.15
		//======= End Skill list of Artisan ID:56=======
		SKILL_DUMMY = 1;
	};

	public class Warsmith extends Artisan {
		public static final int
		//======= Start Skill list of Warsmith ID:57=======
		SKILL_SUMMON_SIEGE_GOLEM					= 13, // Lv.1
		SKILL_SUMMON_MECHANIC_GOLEM					= 25, // Lv.9
		SKILL_BANDAGE								= 34, // Lv.3
		SKILL_WHIRLWIND								= 36, // Lv.37
		SKILL_FATAL_STRIKE							= 190, // Lv.37
		SKILL_HAMMER_CRUSH							= 260, // Lv.37
		SKILL_SUMMON_WILD_HOG_CANNON				= 299, // Lv.1
		SKILL_SUMMON_BIG_BOOM						= 301, // Lv.5
		SKILL_WRATH									= 320, // Lv.10
		SKILL_POLEARM_ACCURACY						= 422, // Lv.3
		SKILL_WAR_FRENZY							= 424, // Lv.3
		SKILL_SUMMON_SWOOP_CANNON					= 448, // Lv.1
		SKILL_REPAIR_GOLEM							= 822, // Lv.3
		SKILL_STRENGTHEN_GOLEM						= 823, // Lv.3

		SKILL_SHARP_EDGE							= 825, // Lv.1
		SKILL_SPIKE									= 826, // Lv.1
		SKILL_RESTRING								= 827, // Lv.1
		SKILL_CASE_HARDEN							= 828, // Lv.1
		SKILL_HARD_TANNING							= 829, // Lv.1
		SKILL_EMBROIDER								= 830, // Lv.1
		SKILL_SUMMON_MERCHANT_GOLEM					= 831, // Lv.1
		SKILL_RUSH									= 994, // Lv.1
		SKILL_BATTLE_CRY							= 1561, // Lv.5
		//======= End Skill list of Warsmith ID:57=======
		SKILL_DUMMY = 1;
	};

	public class Duelist extends Gladiator {
		public static final int
		//======= Start Skill list of Duelist ID:88=======
		SKILL_SONIC_FOCUS							= 8, // Lv.8
		SKILL_RIPOSTE_STANCE						= 340, // Lv.1
		SKILL_SONIC_RAGE							= 345, // Lv.1
		SKILL_EYE_OF_HUNTER						= 359, // Lv.1
		SKILL_EYE_OF_SLAYER						= 360, // Lv.1
		SKILL_BRAVEHEART							= 440, // Lv.1
		SKILL_SONIC_BARRIER						= 442, // Lv.1
		SKILL_SYMBOL_OF_ENERGY						= 458, // Lv.1
		SKILL_WEAPON_BLOCKADE						= 775, // Lv.1
		SKILL_FINAL_SECRET							= 917, // Lv.1
		SKILL_MAXIMUM_FOCUS_SONIC					= 919, // Lv.1
		SKILL_RUSH_IMPACT							= 995, // Lv.1
		//======= End Skill list of Duelist ID:88=======
		SKILL_DUMMY = 1;
	};




	public class Dreadnought extends Warlord {
		public static final int
		//======= Start Skill list of Dreadnought ID:89=======
		SKILL_PARRY_STANCE							= 339, // Lv.1
		SKILL_EARTHQUAKE							= 347, // Lv.1
		SKILL_EYE_OF_HUNTER						= 359, // Lv.1
		SKILL_EYE_OF_SLAYER						= 360, // Lv.1
		SKILL_SHOCK_BLAST							= 361, // Lv.1
		SKILL_BRAVEHEART							= 440, // Lv.1
		SKILL_SYMBOL_OF_HONOR						= 457, // Lv.1
		SKILL_DREAD_POOL							= 774, // Lv.1
		SKILL_FINAL_SECRET							= 917, // Lv.1
		SKILL_CURSED_PIERCE						= 921, // Lv.1
		SKILL_RUSH_IMPACT							= 995, // Lv.1
		//======= End Skill list of Dreadnought ID:89=======
		SKILL_DUMMY = 1;
	};

	public class PhoenixKnight extends Paladin {
		public static final int
		//======= Start Skill list of PhoenixKnight ID:90=======
		SKILL_FORTITUDE							= 335, // Lv.1
		SKILL_TOUCH_OF_LIFE						= 341, // Lv.1
		SKILL_PHYSICAL_MIRROR						= 350, // Lv.1
		SKILL_SHIELD_SLAM							= 353, // Lv.1
		SKILL_VENGEANCE							= 368, // Lv.1
		SKILL_SOUL_OF_THE_PHOENIX					= 438, // Lv.1
		SKILL_SYMBOL_OF_DEFENSE					= 454, // Lv.1
		SKILL_IRON_SHIELD							= 527, // Lv.1
		SKILL_SHIELD_OF_FAITH						= 528, // Lv.1
		SKILL_ANTI_MAGIC_ARMOR						= 760, // Lv.1
		SKILL_SPIRIT_OF_PHOENIX					= 784, // Lv.1
		SKILL_SUMMON_IMPERIAL_PHOENIX				= 912, // Lv.1
		SKILL_DEFLECT_MAGIC						= 913, // Lv.1
		SKILL_SHIELD_STRIKE						= 984, // Lv.25
		SKILL_CHALLENGE_FOR_FATE					= 985, // Lv.1
		//======= End Skill list of PhoenixKnight ID:90=======
		SKILL_DUMMY = 1;
	};

	public class HellKnight extends DarkAvenger {
		public static final int
		//======= Start Skill list of HellKnight ID:91=======
		SKILL_FORTITUDE							= 335, // Lv.1
		SKILL_TOUCH_OF_DEATH						= 342, // Lv.1
		SKILL_PHYSICAL_MIRROR						= 350, // Lv.1
		SKILL_SHIELD_SLAM							= 353, // Lv.1
		SKILL_VENGEANCE							= 368, // Lv.1
		SKILL_SHIELD_OF_REVENGE					= 439, // Lv.1
		SKILL_SYMBOL_OF_DEFENSE					= 454, // Lv.1
		SKILL_IRON_SHIELD							= 527, // Lv.1
		SKILL_SHIELD_OF_FAITH						= 528, // Lv.1
		SKILL_ANTI_MAGIC_ARMOR						= 760, // Lv.1
		SKILL_SEED_OF_REVENGE						= 761, // Lv.1
		SKILL_HELL_SCREAM							= 763, // Lv.1
		SKILL_DEFLECT_MAGIC						= 913, // Lv.1
		SKILL_SHIELD_STRIKE						= 984, // Lv.25
		SKILL_CHALLENGE_FOR_FATE					= 985, // Lv.1
		//======= End Skill list of HellKnight ID:91=======
		SKILL_DUMMY = 1;
	};

	public class Sagittarius extends Hawkeye {
		public static final int
		//======= Start Skill list of Sagittarius ID:92=======
		SKILL_FOCUS_SKILL_MASTERY					= 334, // Lv.1
		SKILL_LETHAL_SHOT							= 343, // Lv.1
		SKILL_HAMSTRING_SHOT						= 354, // Lv.1
		SKILL_SYMBOL_OF_THE_SNIPER					= 459, // Lv.1
		SKILL_FLAME_HAWK							= 771, // Lv.1
		SKILL_SEVEN_ARROW							= 924, // Lv.1
		SKILL_MULTIPLE_SHOT						= 987, // Lv.1
		SKILL_DEATH_SHOT							= 990, // Lv.1
		//======= End Skill list of Sagittarius ID:92=======
		SKILL_DUMMY = 1;
	};

	public class Adventurer extends TreasureHunter {
		public static final int
		//======= Start Skill list of Adventurer ID:93=======
		SKILL_FOCUS_SKILL_MASTERY					= 334, // Lv.1
		SKILL_LETHAL_BLOW							= 344, // Lv.1
		SKILL_FOCUS_CHANCE							= 356, // Lv.1
		SKILL_FOCUS_POWER							= 357, // Lv.1
		SKILL_BLUFF								= 358, // Lv.1
		SKILL_MIRAGE								= 445, // Lv.1
		SKILL_SYMBOL_OF_THE_ASSASSIN				= 460, // Lv.1
		SKILL_CRITICAL_WOUND						= 531, // Lv.1
		SKILL_EXCITING_ADVENTURE					= 768, // Lv.1
		SKILL_HIDE									= 922, // Lv.1
		SKILL_DUEL_BLOW							= 928, // Lv.1
		SKILL_THROWING_DAGGER						= 991, // Lv.1
		//======= End Skill list of Adventurer ID:93=======
		SKILL_DUMMY = 1;
	};

	public class Archmage extends Sorcerer {
		public static final int
		//======= Start Skill list of Archmage ID:94=======
		SKILL_ARCANE_POWER							= 337, // Lv.1
		SKILL_ARCANE_CHAOS							= 1338, // Lv.1
		SKILL_FIRE_VORTEX							= 1339, // Lv.1
		SKILL_VOLCANO								= 1419, // Lv.1
		SKILL_FIRE_VORTEX_BUSTER					= 1451, // Lv.1
		SKILL_COUNT_OF_FIRE						= 1452, // Lv.1
		SKILL_METEOR								= 1467, // Lv.1
		SKILL_FLAME_ARMOR							= 1492, // Lv.1
		SKILL_AURA_BLAST							= 1554, // Lv.1
		SKILL_AURA_CANNON							= 1555, // Lv.1
		SKILL_ARCANE_SHIELD						= 1556, // Lv.1
		//======= End Skill list of Archmage ID:94=======
		SKILL_DUMMY = 1;
	};

	public class Soultaker extends Necromancer {
		public static final int
		//======= Start Skill list of Soultaker ID:95=======
		SKILL_ARCANE_POWER							= 337, // Lv.1
		SKILL_CURSE_OF_DOOM						= 1336, // Lv.1
		SKILL_CURSE_OF_ABYSS						= 1337, // Lv.1
		SKILL_DARK_VORTEX							= 1343, // Lv.1
		SKILL_MASS_WARRIOR_BANE					= 1344, // Lv.1
		SKILL_MASS_MAGE_BANE						= 1345, // Lv.1
		SKILL_DAY_OF_DOOM							= 1422, // Lv.1
		SKILL_GEHENNA								= 1423, // Lv.1
		SKILL_METEOR								= 1467, // Lv.1
		SKILL_VAMPIRIC_MIST						= 1495, // Lv.1
		SKILL_SERVITOR_SHARE						= 1557, // Lv.1
		//======= End Skill list of Soultaker ID:95=======
		SKILL_DUMMY = 1;
	};

	public class ArcanaLord extends Warlock {
		public static final int
		//======= Start Skill list of ArcanaLord ID:96=======
		SKILL_ARCANE_AGILITY						= 338, // Lv.1
		SKILL_SUMMON_SMART_CUBIC					= 781, // Lv.1
		SKILL_SPIRIT_OF_THE_CAT					= 929, // Lv.1
		SKILL_WARRIOR_SERVITOR						= 1346, // Lv.1
		SKILL_FINAL_SERVITOR						= 1349, // Lv.1
		SKILL_WARRIOR_BANE							= 1350, // Lv.1
		SKILL_MAGE_BANE							= 1351, // Lv.1
		SKILL_SUMMON_FELINE_KING					= 1406, // Lv.1
		SKILL_ANTI_SUMMONING_FIELD					= 1424, // Lv.1
		SKILL_SERVITOR_BARRIER						= 1496, // Lv.1
		SKILL_SERVITOR_SHARE						= 1557, // Lv.1
		SKILL_DIMENSION_SPIRAL						= 1558, // Lv.24
		//======= End Skill list of ArcanaLord ID:96=======
		SKILL_DUMMY = 1;
	};

	public class Cardinal extends Bishop {
		public static final int
		//======= Start Skill list of Cardinal ID:97=======
		SKILL_ARCANE_WISDOM						= 336, // Lv.1
		SKILL_BALANCE_LIFE							= 1335, // Lv.1
		SKILL_DIVINE_PROTECTION					= 1353, // Lv.1
		SKILL_MASS_BLOCK_SHIELD					= 1360, // Lv.1
		SKILL_MASS_BLOCK_WIND_WALK					= 1361, // Lv.1
		SKILL_CLEANSE								= 1409, // Lv.1
		SKILL_SALVATION							= 1410, // Lv.1
		SKILL_PURIFICATION_FIELD					= 1425, // Lv.1
		SKILL_MIRACLE								= 1426, // Lv.1
		SKILL_DIVINE_POWER							= 1459, // Lv.1
		SKILL_SUBLIME_SELF_SACRIFICE				= 1505, // Lv.1
		SKILL_SURRENDER_TO_THE_HOLY				= 1524, // Lv.42
		SKILL_TURN_TO_STONE						= 1540, // Lv.1
		SKILL_CHAIN_HEAL							= 1553, // Lv.1
		//======= End Skill list of Cardinal ID:97=======
		SKILL_DUMMY = 1;
	};




	public class Hierophant extends Prophet {
		public static final int
		//======= Start Skill list of Hierophant ID:98=======
		SKILL_ARCANE_WISDOM						= 336, // Lv.1
		SKILL_ELEMENTAL_PROTECTION					= 1352, // Lv.1
		SKILL_PROPHECY_OF_FIRE						= 1356, // Lv.1
		SKILL_BLOCK_SHIELD							= 1358, // Lv.1
		SKILL_BLOCK_WIND_WALK						= 1359, // Lv.1
		SKILL_MYSTIC_IMMUNITY						= 1411, // Lv.1
		SKILL_SPELL_TURNING						= 1412, // Lv.1
		SKILL_TURN_TO_STONE						= 1540, // Lv.1
		SKILL_COUNTER_CRITICAL						= 1542, // Lv.1
		//======= End Skill list of Hierophant ID:98=======
		SKILL_DUMMY = 1;
	};

	public class EvasTemplar extends TempleKnight {
		public static final int
		//======= Start Skill list of EvasTemplar ID:99=======
		SKILL_FORTITUDE							= 335, // Lv.1
		SKILL_TOUCH_OF_LIFE						= 341, // Lv.1
		SKILL_MAGICAL_MIRROR						= 351, // Lv.1
		SKILL_SHIELD_BASH							= 352, // Lv.1
		SKILL_VENGEANCE							= 368, // Lv.1
		SKILL_SYMBOL_OF_DEFENSE					= 454, // Lv.1
		SKILL_IRON_SHIELD							= 527, // Lv.1
		SKILL_SHIELD_OF_FAITH						= 528, // Lv.1
		SKILL_ANTI_MAGIC_ARMOR						= 760, // Lv.1
		SKILL_SUMMON_SMART_CUBIC					= 779, // Lv.1
		SKILL_EVAS_WILL							= 786, // Lv.1
		SKILL_DEFLECT_MAGIC						= 913, // Lv.1
		SKILL_SHIELD_STRIKE						= 984, // Lv.25
		SKILL_CHALLENGE_FOR_FATE					= 985, // Lv.1
		//======= End Skill list of EvasTemplar ID:99=======
		SKILL_DUMMY = 1;
	};

	public class SwordMuse extends Swordsinger {
		public static final int
		//======= Start Skill list of SwordMuse ID:100=======
		SKILL_SONG_OF_RENEWAL						= 349, // Lv.1
		SKILL_SONG_OF_CHAMPION						= 364, // Lv.1
		SKILL_SONG_OF_SILENCE						= 437, // Lv.1
		SKILL_SYMBOL_OF_NOISE						= 455, // Lv.1
		SKILL_SONG_OF_ELEMENTAL					= 529, // Lv.1
		SKILL_DEFLECT_MAGIC						= 913, // Lv.1
		SKILL_SONG_OF_PURIFICATION					= 914, // Lv.1
		SKILL_DEADLY_STRIKE						= 986, // Lv.25
		SKILL_РИТМ_МЕСТИ							= 1594, // Lv.1
		SKILL_РИТМ_СТАРТА							= 1596, // Lv.1
		//======= End Skill list of SwordMuse ID:100=======
		SKILL_DUMMY = 1;
	};

	public class WindRider extends PlainWalker {
		public static final int
		//======= Start Skill list of WindRider ID:101=======
		SKILL_FOCUS_SKILL_MASTERY					= 334, // Lv.1
		SKILL_LETHAL_BLOW							= 344, // Lv.1
		SKILL_FOCUS_DEATH							= 355, // Lv.1
		SKILL_FOCUS_CHANCE							= 356, // Lv.1
		SKILL_BLUFF								= 358, // Lv.1
		SKILL_DODGE								= 446, // Lv.1
		SKILL_SYMBOL_OF_THE_ASSASSIN				= 460, // Lv.1
		SKILL_CRITICAL_WOUND						= 531, // Lv.1
		SKILL_WIND_RIDING							= 769, // Lv.1
		SKILL_HIDE									= 922, // Lv.1
		SKILL_DUEL_BLOW							= 928, // Lv.1
		SKILL_THROWING_DAGGER						= 991, // Lv.1
		//======= End Skill list of WindRider ID:101=======
		SKILL_DUMMY = 1;
	};

	public class MoonlightSentinel extends SilverRanger {
		public static final int
		//======= Start Skill list of MoonlightSentinel ID:102=======
		SKILL_FOCUS_SKILL_MASTERY					= 334, // Lv.1
		SKILL_LETHAL_SHOT							= 343, // Lv.1
		SKILL_HAMSTRING_SHOT						= 354, // Lv.1
		SKILL_EVADE_SHOT							= 369, // Lv.1
		SKILL_SYMBOL_OF_THE_SNIPER					= 459, // Lv.1
		SKILL_ARROW_RAIN							= 772, // Lv.1
		SKILL_MULTIPLE_SHOT						= 987, // Lv.1
		SKILL_DEATH_SHOT							= 990, // Lv.1
		//======= End Skill list of MoonlightSentinel ID:102=======
		SKILL_DUMMY = 1;
	};

	public class MysticMuse extends Spellsinger {
		public static final int
		//======= Start Skill list of MysticMuse ID:103=======
		SKILL_ARCANE_POWER							= 337, // Lv.1
		SKILL_ARCANE_CHAOS							= 1338, // Lv.1
		SKILL_ICE_VORTEX							= 1340, // Lv.1
		SKILL_LIGHT_VORTEX							= 1342, // Lv.1
		SKILL_RAGING_WAVES							= 1421, // Lv.1
		SKILL_ICE_VORTEX_CRUSHER					= 1453, // Lv.1
		SKILL_DIAMOND_DUST							= 1454, // Lv.1
		SKILL_THRONE_OF_ICE						= 1455, // Lv.1
		SKILL_STAR_FALL							= 1468, // Lv.1
		SKILL_FROST_ARMOR							= 1493, // Lv.1
		SKILL_AURA_BLAST							= 1554, // Lv.1
		SKILL_AURA_CANNON							= 1555, // Lv.1
		SKILL_ARCANE_SHIELD						= 1556, // Lv.1
		//======= End Skill list of MysticMuse ID:103=======
		SKILL_DUMMY = 1;
	};

	public class ElementalMaster extends ElementalSummoner {
		public static final int
		//======= Start Skill list of ElementalMaster ID:104=======
		SKILL_ARCANE_AGILITY						= 338, // Lv.1
		SKILL_SUMMON_SMART_CUBIC					= 782, // Lv.1
		SKILL_SPIRIT_OF_THE_UNICORN				= 931, // Lv.1
		SKILL_WIZARD_SERVITOR						= 1347, // Lv.1
		SKILL_FINAL_SERVITOR						= 1349, // Lv.1
		SKILL_WARRIOR_BANE							= 1350, // Lv.1
		SKILL_SUMMON_MAGNUS_THE_UNICORN			= 1407, // Lv.1
		SKILL_ANTI_SUMMONING_FIELD					= 1424, // Lv.1
		SKILL_SERVITOR_BARRIER						= 1496, // Lv.1
		SKILL_SERVITOR_SHARE						= 1557, // Lv.1
		SKILL_DIMENSION_SPIRAL						= 1558, // Lv.24
		//======= End Skill list of ElementalMaster ID:104=======
		SKILL_DUMMY = 1;
	};

	public class EvasSaint extends Elder {
		public static final int
		//======= Start Skill list of EvasSaint ID:105=======
		SKILL_ARCANE_WISDOM						= 336, // Lv.1
		SKILL_DIVINE_PROTECTION					= 1353, // Lv.1
		SKILL_ARCANE_PROTECTION					= 1354, // Lv.1
		SKILL_PROPHECY_OF_WATER					= 1355, // Lv.1
		SKILL_BLOCK_WIND_WALK						= 1359, // Lv.1
		SKILL_MASS_RECHARGE						= 1428, // Lv.1
		SKILL_MANA_GAIN							= 1460, // Lv.1
		SKILL_BLESSING_OF_EVA						= 1506, // Lv.1
		SKILL_SURRENDER_TO_THE_HOLY				= 1524, // Lv.42
		SKILL_TURN_TO_STONE						= 1540, // Lv.1
		SKILL_MASS_CURE_POISON						= 1550, // Lv.1
		SKILL_MASS_VITALIZE						= 1552, // Lv.1
		SKILL_CHAIN_HEAL							= 1553, // Lv.1
		//======= End Skill list of EvasSaint ID:105=======
		SKILL_DUMMY = 1;
	};

	public class ShillienTemplar extends ShillenKnight {
		public static final int
		//======= Start Skill list of ShillienTemplar ID:106=======
		SKILL_FORTITUDE							= 335, // Lv.1
		SKILL_TOUCH_OF_DEATH						= 342, // Lv.1
		SKILL_MAGICAL_MIRROR						= 351, // Lv.1
		SKILL_SHIELD_BASH							= 352, // Lv.1
		SKILL_VENGEANCE							= 368, // Lv.1
		SKILL_SYMBOL_OF_DEFENSE					= 454, // Lv.1
		SKILL_IRON_SHIELD							= 527, // Lv.1
		SKILL_SHIELD_OF_FAITH						= 528, // Lv.1
		SKILL_ANTI_MAGIC_ARMOR						= 760, // Lv.1
		SKILL_SUMMON_SMART_CUBIC					= 780, // Lv.1
		SKILL_PAIN_OF_SHILEN						= 788, // Lv.1
		SKILL_DEFLECT_MAGIC						= 913, // Lv.1
		SKILL_SHIELD_STRIKE						= 984, // Lv.25
		SKILL_CHALLENGE_FOR_FATE					= 985, // Lv.1
		//======= End Skill list of ShillienTemplar ID:106=======
		SKILL_DUMMY = 1;
	};

	public class SpectralDancer extends Bladedancer {
		public static final int
		//======= Start Skill list of SpectralDancer ID:107=======
		SKILL_SIRENS_DANCE							= 365, // Lv.1
		SKILL_DANCE_OF_SHADOWS						= 366, // Lv.1
		SKILL_DANCE_OF_MEDUSA						= 367, // Lv.1
		SKILL_SYMBOL_OF_NOISE						= 455, // Lv.1
		SKILL_DANCE_OF_ALIGNMENT					= 530, // Lv.1
		SKILL_DEFLECT_MAGIC						= 913, // Lv.1
		SKILL_DANCE_OF_BERSERKER					= 915, // Lv.1
		SKILL_DEADLY_STRIKE						= 986, // Lv.25
		SKILL_ТЕМНЫЙ_РИТМ_МЕСТИ					= 1593, // Lv.1
		SKILL_ТЕМНЫЙ_РИТМ_СТАРТА					= 1595, // Lv.1
		//======= End Skill list of SpectralDancer ID:107=======
		SKILL_DUMMY = 1;
	};

	public class GhostHunter extends AbyssWalker {
		public static final int
		//======= Start Skill list of GhostHunter ID:108=======
		SKILL_FOCUS_SKILL_MASTERY					= 334, // Lv.1
		SKILL_LETHAL_BLOW							= 344, // Lv.1
		SKILL_FOCUS_DEATH							= 355, // Lv.1
		SKILL_FOCUS_POWER							= 357, // Lv.1
		SKILL_BLUFF								= 358, // Lv.1
		SKILL_COUNTERATTACK						= 447, // Lv.1
		SKILL_SYMBOL_OF_THE_ASSASSIN				= 460, // Lv.1
		SKILL_CRITICAL_WOUND						= 531, // Lv.1
		SKILL_GHOST_WALKING						= 770, // Lv.1
		SKILL_HIDE									= 922, // Lv.1
		SKILL_DUEL_BLOW							= 928, // Lv.1
		SKILL_THROWING_DAGGER						= 991, // Lv.1
		//======= End Skill list of GhostHunter ID:108=======
		SKILL_DUMMY = 1;
	};

	public class GhostSentinel extends PhantomRanger {
		public static final int
		//======= Start Skill list of GhostSentinel ID:109=======
		SKILL_FOCUS_SKILL_MASTERY					= 334, // Lv.1
		SKILL_LETHAL_SHOT							= 343, // Lv.1
		SKILL_HAMSTRING_SHOT						= 354, // Lv.1
		SKILL_EVADE_SHOT							= 369, // Lv.1
		SKILL_SYMBOL_OF_THE_SNIPER					= 459, // Lv.1
		SKILL_GHOST_PIERCING						= 773, // Lv.1
		SKILL_SEVEN_ARROW							= 924, // Lv.1
		SKILL_MULTIPLE_SHOT						= 987, // Lv.1
		SKILL_DEATH_SHOT							= 990, // Lv.1
		//======= End Skill list of GhostSentinel ID:109=======
		SKILL_DUMMY = 1;
	};

	public class StormScreamer extends Spellhowler {
		public static final int
		//======= Start Skill list of StormScreamer ID:110=======
		SKILL_ARCANE_POWER							= 337, // Lv.1
		SKILL_ARCANE_CHAOS							= 1338, // Lv.1
		SKILL_WIND_VORTEX							= 1341, // Lv.1
		SKILL_DARK_VORTEX							= 1343, // Lv.1
		SKILL_CYCLONE								= 1420, // Lv.1
		SKILL_WIND_VORTEX_SLUG						= 1456, // Lv.1
		SKILL_EMPOWERING_ECHO						= 1457, // Lv.1
		SKILL_THRONE_OF_WIND						= 1458, // Lv.1
		SKILL_STAR_FALL							= 1468, // Lv.1
		SKILL_HURRICANE_ARMOR						= 1494, // Lv.1
		SKILL_AURA_BLAST							= 1554, // Lv.1
		SKILL_AURA_CANNON							= 1555, // Lv.1
		SKILL_ARCANE_SHIELD						= 1556, // Lv.1
		//======= End Skill list of StormScreamer ID:110=======
		SKILL_DUMMY = 1;
	};

	public class SpectralMaster extends PhantomSummoner {
		public static final int
		//======= Start Skill list of SpectralMaster ID:111=======
		SKILL_ARCANE_AGILITY						= 338, // Lv.1
		SKILL_SUMMON_SMART_CUBIC					= 783, // Lv.1
		SKILL_SPIRIT_OF_THE_DEMON					= 930, // Lv.1
		SKILL_ASSASSIN_SERVITOR					= 1348, // Lv.1
		SKILL_FINAL_SERVITOR						= 1349, // Lv.1
		SKILL_MAGE_BANE							= 1351, // Lv.1
		SKILL_SUMMON_SPECTRAL_LORD					= 1408, // Lv.1
		SKILL_ANTI_SUMMONING_FIELD					= 1424, // Lv.1
		SKILL_SERVITOR_BARRIER						= 1496, // Lv.1
		SKILL_SERVITOR_SHARE						= 1557, // Lv.1
		SKILL_DIMENSION_SPIRAL						= 1558, // Lv.24
		//======= End Skill list of SpectralMaster ID:111=======
		SKILL_DUMMY = 1;
	};

	public class ShillienSaint extends ShillenElder {
		public static final int
		//======= Start Skill list of ShillienSaint ID:112=======
		SKILL_ARCANE_WISDOM						= 336, // Lv.1
		SKILL_ARCANE_PROTECTION					= 1354, // Lv.1
		SKILL_PROPHECY_OF_WIND						= 1357, // Lv.1
		SKILL_BLOCK_SHIELD							= 1358, // Lv.1
		SKILL_MASS_RECHARGE						= 1428, // Lv.1
		SKILL_MANA_GAIN							= 1460, // Lv.1
		SKILL_LORD_OF_VAMPIRE						= 1507, // Lv.1
		SKILL_THRONE_ROOT							= 1508, // Lv.1
		SKILL_TURN_TO_STONE						= 1540, // Lv.1
		SKILL_MASS_CURE_POISON						= 1550, // Lv.1
		SKILL_MASS_PURIFY							= 1551, // Lv.1
		SKILL_CHAIN_HEAL							= 1553, // Lv.1
		//======= End Skill list of ShillienSaint ID:112=======
		SKILL_DUMMY = 1;
	};

	public class Titan extends Destroyer {
		public static final int
		//======= Start Skill list of Titan ID:113=======
		SKILL_FORTITUDE							= 335, // Lv.1
		SKILL_PARRY_STANCE							= 339, // Lv.1
		SKILL_EARTHQUAKE							= 347, // Lv.1
		SKILL_ARMOR_CRUSH							= 362, // Lv.1
		SKILL_BRAVEHEART							= 440, // Lv.1
		SKILL_SYMBOL_OF_RESISTANCE					= 456, // Lv.1
		SKILL_OVER_THE_BODY						= 536, // Lv.1
		SKILL_DEMOLITION_IMPACT					= 777, // Lv.1
		SKILL_FINAL_SECRET							= 917, // Lv.1
		SKILL_RUSH_IMPACT							= 995, // Lv.1
		//======= End Skill list of Titan ID:113=======
		SKILL_DUMMY = 1;
	};

	public class GrandKhavatari extends Tyrant {
		public static final int
		//======= Start Skill list of GrandKhavatari ID:114=======
		SKILL_FOCUSED_FORCE						= 50, // Lv.8
		SKILL_FORTITUDE							= 335, // Lv.1
		SKILL_RIPOSTE_STANCE						= 340, // Lv.1
		SKILL_RAGING_FORCE							= 346, // Lv.1
		SKILL_FORCE_MEDITATION						= 441, // Lv.1
		SKILL_FORCE_BARRIER						= 443, // Lv.1
		SKILL_SYMBOL_OF_ENERGY						= 458, // Lv.1
		SKILL_FORCE_OF_DESTRUCTION					= 776, // Lv.1
		SKILL_FINAL_SECRET							= 917, // Lv.1
		SKILL_MAXIMUM_FOCUS_FORCE					= 918, // Lv.1
		SKILL_RUSH_IMPACT							= 995, // Lv.1
		//======= End Skill list of GrandKhavatari ID:114=======
		SKILL_DUMMY = 1;
	};

	public class Dominator extends Overlord {
		public static final int
		//======= Start Skill list of Dominator ID:115=======
		SKILL_ARCANE_POWER							= 337, // Lv.1
		SKILL_ONSLAUGHT_OF_PAAGRIO					= 949, // Lv.1
		SKILL_EYE_OF_PAAGRIO						= 1364, // Lv.1
		SKILL_SOUL_OF_PAAGRIO						= 1365, // Lv.1
		SKILL_SEAL_OF_DESPAIR						= 1366, // Lv.1
		SKILL_SEAL_OF_DISEASE						= 1367, // Lv.1
		SKILL_VICTORY_OF_PAAGRIO					= 1414, // Lv.1
		SKILL_PAAGRIOS_EMBLEM						= 1415, // Lv.1
		SKILL_PAAGRIOS_FIST						= 1416, // Lv.1
		SKILL_FLAMES_OF_INVINCIBILITY				= 1427, // Lv.1
		SKILL_SEAL_OF_BLOCKADE						= 1462, // Lv.1
		SKILL_SEAL_OF_LIMIT						= 1509, // Lv.1
		SKILL_TURN_TO_STONE						= 1540, // Lv.1
		SKILL_CHAIN_HEAL							= 1553, // Lv.1
		//======= End Skill list of Dominator ID:115=======
		SKILL_DUMMY = 1;
	};

	public class Doomcryer extends Warcryer {
		public static final int
		//======= Start Skill list of Doomcryer ID:116=======
		SKILL_ARCANE_WISDOM						= 336, // Lv.1
		SKILL_CHANT_OF_SPIRIT						= 1362, // Lv.1
		SKILL_CHANT_OF_VICTORY						= 1363, // Lv.1
		SKILL_MAGNUS_CHANT							= 1413, // Lv.1
		SKILL_GATE_CHANT							= 1429, // Lv.1
		SKILL_CHANT_OF_PROTECTION					= 1461, // Lv.1
		SKILL_TURN_TO_STONE						= 1540, // Lv.1
		SKILL_CHANT_OF_ELEMENTS					= 1549, // Lv.1
		//======= End Skill list of Doomcryer ID:116=======
		SKILL_DUMMY = 1;
	};

	public class FortuneSeeker extends BountyHunter {
		public static final int
		//======= Start Skill list of FortuneSeeker ID:117=======
		SKILL_PARRY_STANCE							= 339, // Lv.1
		SKILL_RIPOSTE_STANCE						= 340, // Lv.1
		SKILL_EARTHQUAKE							= 347, // Lv.1
		SKILL_SPOIL_CRUSH							= 348, // Lv.1
		SKILL_ARMOR_CRUSH							= 362, // Lv.1
		SKILL_BRAVEHEART							= 440, // Lv.1
		SKILL_SYMBOL_OF_RESISTANCE					= 456, // Lv.1
		SKILL_SPOIL_BOMB							= 537, // Lv.1
		SKILL_FINAL_SECRET							= 917, // Lv.1
		SKILL_LUCKY_STRIKE							= 947, // Lv.1
		SKILL_CRUSHING_STRIKE						= 997, // Lv.25
		SKILL_LUCKY_BLOW							= 1560, // Lv.1
		//======= End Skill list of FortuneSeeker ID:117=======
		SKILL_DUMMY = 1;
	};

	public class Maestro extends Warsmith {
		public static final int
		//======= Start Skill list of Maestro ID:118=======
		SKILL_PARRY_STANCE							= 339, // Lv.1
		SKILL_RIPOSTE_STANCE						= 340, // Lv.1
		SKILL_EARTHQUAKE							= 347, // Lv.1
		SKILL_ARMOR_CRUSH							= 362, // Lv.1
		SKILL_BRAVEHEART							= 440, // Lv.1
		SKILL_SYMBOL_OF_HONOR						= 457, // Lv.1
		SKILL_GOLEM_ARMOR							= 778, // Lv.1
		SKILL_FINAL_SECRET							= 917, // Lv.1
		SKILL_RUSH_IMPACT							= 995, // Lv.1
		//======= End Skill list of Maestro ID:118=======
		SKILL_DUMMY = 1;
	};
	
	public class KamaelFSoldier {
		public static final int
		//======= Start Skill list of KamaelFSoldier ID:124=======
		SKILL_FALLEN_ATTACK						= 468, // Lv.9
		SKILL_RAPID_ATTACK							= 469, // Lv.1
		SKILL_DETECT_TRAP							= 470, // Lv.1
		SKILL_DEFUSE_TRAP							= 471, // Lv.1
		SKILL_FALLEN_ARROW							= 1431, // Lv.6
		//======= End Skill list of KamaelFSoldier ID:124=======
		SKILL_DUMMY = 1;
	};
	
	public class Warder extends KamaelFSoldier {
		public static final int
		//======= Start Skill list of Warder ID:126=======
		SKILL_DETECT_TRAP							= 470, // Lv.3
		SKILL_DEFUSE_TRAP							= 471, // Lv.3
		SKILL_DOUBLE_THRUST						= 478, // Lv.15
		SKILL_HARD_MARCH							= 479, // Lv.1
		SKILL_DARK_BLADE							= 480, // Lv.1
		SKILL_DARK_ARMOR							= 481, // Lv.1
		SKILL_FURIOUS_SOUL							= 482, // Lv.1
		SKILL_PENETRATING_SHOT						= 487, // Lv.15
		SKILL_FAST_SHOT							= 490, // Lv.1
		SKILL_FIRE_TRAP							= 514, // Lv.2
		SKILL_ULTIMATE_ESCAPE						= 622, // Lv.1
		SKILL_SOUL_SHOCK							= 627, // Lv.3
		SKILL_WARP									= 628, // Lv.1
		SKILL_ABYSSAL_BLAZE						= 1433, // Lv.10
		SKILL_DARK_EXPLOSION						= 1434, // Lv.4
		SKILL_DEATH_MARK							= 1435, // Lv.2
		SKILL_SURRENDER_TO_THE_UNHOLY				= 1445, // Lv.3
		SKILL_CHANGE_WEAPON						= 1473, // Lv.1
		SKILL_ERASE_MARK							= 1475, // Lv.1
		//======= End Skill list of Warder ID:126=======
		SKILL_DUMMY = 1;
	};
	
	public class Arbalester extends Warder {
		public static final int
		//======= Start Skill list of Arbalester ID:130=======
		SKILL_DEFUSE_TRAP							= 471, // Lv.7
		SKILL_SHIFT_TARGET							= 489, // Lv.1
		SKILL_FAST_SHOT							= 490, // Lv.2
		SKILL_LIFE_TO_SOUL							= 502, // Lv.5
		SKILL_TWIN_SHOT							= 507, // Lv.37
		SKILL_RISING_SHOT							= 508, // Lv.31
		SKILL_BLEEDING_SHOT						= 509, // Lv.34
		SKILL_DEADLY_ROULETTE						= 510, // Lv.5
		SKILL_TEMPTATION							= 511, // Lv.1
		SKILL_CREATE_DARK_SEED						= 513, // Lv.1
		SKILL_FIRE_TRAP							= 514, // Lv.9
		SKILL_POISON_TRAP							= 515, // Lv.6
		SKILL_SLOW_TRAP							= 516, // Lv.6
		SKILL_FLASH_TRAP							= 517, // Lv.5
		SKILL_BINDING_TRAP							= 518, // Lv.8
		SKILL_QUIVER_OF_BOLTS_A_GRADE				= 519, // Lv.1
		SKILL_QUIVER_OF_BOLTS_S_GRADE				= 520, // Lv.1
		SKILL_SHARPSHOOTING						= 521, // Lv.8
		SKILL_REAL_TARGET							= 522, // Lv.4
		SKILL_IMBUE_DARK_SEED						= 523, // Lv.7
		SKILL_CURE_DARK_SEED						= 524, // Lv.1
		SKILL_DECOY								= 525, // Lv.6
		SKILL_QUIVER_OF_BOLTS_B_GRADE			= 620, // Lv.1
		SKILL_CREATE_SPECIAL_BOLT					= 621, // Lv.1
		SKILL_ULTIMATE_ESCAPE						= 622, // Lv.2
		SKILL_SOUL_GATHERING						= 625, // Lv.1
		SKILL_SOUL_SHOCK							= 627, // Lv.40
		SKILL_IMBUE_SEED_OF_DESTRUCTION			= 835, // Lv.4
		SKILL_OBLIVION_TRAP						= 836, // Lv.3
		SKILL_SOUL_CLEANSE							= 1510, // Lv.1
		SKILL_SOUL_BARRIER							= 1514, // Lv.1
		//======= End Skill list of Arbalester ID:130=======
		SKILL_DUMMY = 1;
	};
	
	public class Trickster extends Arbalester {
		public static final int
		//======= Start Skill list of Trickster ID:134=======
		SKILL_FOCUS_SKILL_MASTERY					= 334, // Lv.1
		SKILL_FINAL_FORM							= 538, // Lv.1
		SKILL_WILD_SHOT							= 790, // Lv.1
		SKILL_BETRAYAL_MARK						= 792, // Lv.1
		SKILL_SOUL_RAGE							= 939, // Lv.1
		SKILL_MULTIPLE_SHOT						= 987, // Lv.1
		SKILL_DEATH_SHOT							= 990, // Lv.1
		SKILL_PRAHNAH								= 1470, // Lv.1
		//======= End Skill list of Trickster ID:134=======
		SKILL_DUMMY = 1;
	};
	
	public class FSoulBreaker extends Warder {
		public static final int
		//======= Start Skill list of FSoulBreaker ID:129=======
		SKILL_DARK_ARMOR							= 481, // Lv.2
		SKILL_SPREAD_WING							= 492, // Lv.25
		SKILL_LIFE_TO_SOUL							= 502, // Lv.5
		SKILL_TRIPLE_THRUST						= 504, // Lv.37
		SKILL_SHINING_EDGE							= 505, // Lv.28
		SKILL_CHECKMATE							= 506, // Lv.4
		SKILL_SOUL_GATHERING						= 625, // Lv.1
		SKILL_PAINKILLER							= 837, // Lv.1
		SKILL_DEATH_MARK							= 1435, // Lv.10
		SKILL_SOUL_OF_PAIN							= 1436, // Lv.30
		SKILL_DARK_FLAME							= 1437, // Lv.26
		SKILL_ANNIHILATION_CIRCLE					= 1438, // Lv.9
		SKILL_CURSE_OF_DIVINITY					= 1439, // Lv.5
		SKILL_STEAL_DIVINITY						= 1440, // Lv.5
		SKILL_SOUL_TO_EMPOWER						= 1441, // Lv.3
		SKILL_PROTECTION_FROM_DARKNESS				= 1442, // Lv.3
		SKILL_DARK_WEAPON							= 1443, // Lv.1
		SKILL_PRIDE_OF_KAMAEL						= 1444, // Lv.1
		SKILL_SURRENDER_TO_THE_UNHOLY				= 1445, // Lv.18
		SKILL_SHADOW_BIND							= 1446, // Lv.11
		SKILL_VOICE_BIND							= 1447, // Lv.9
		SKILL_BLINK								= 1448, // Lv.1
		SKILL_ERASE_MARK							= 1475, // Lv.3
		SKILL_SOUL_CLEANSE							= 1510, // Lv.1
		SKILL_CURSE_OF_LIFE_FLOW					= 1511, // Lv.8
		SKILL_SOUL_WEB								= 1529, // Lv.7
		//======= End Skill list of FSoulBreaker ID:129=======
		SKILL_DUMMY = 1;
	};
	
	public class FSoulHound extends FSoulBreaker {
		public static final int
		//======= Start Skill list of FSoulHound ID:133=======
		SKILL_FINAL_FORM							= 538, // Lv.1
		SKILL_LIGHTNING_SHOCK						= 791, // Lv.1
		SKILL_SOUL_RAGE							= 939, // Lv.1
		SKILL_LEOPOLD								= 1469, // Lv.1
		SKILL_SOUL_VORTEX							= 1512, // Lv.1
		SKILL_SOUL_VORTEX_EXTINCTION				= 1513, // Lv.1
		SKILL_LIGHTNING_BARRIER					= 1515, // Lv.1
		SKILL_SOUL_STRIKE							= 1516, // Lv.1
		SKILL_AURA_BLAST							= 1554, // Lv.1
		SKILL_AURA_CANNON							= 1555, // Lv.1
		SKILL_ARCANE_SHIELD						= 1556, // Lv.1
		//======= End Skill list of FSoulHound ID:133=======
		SKILL_DUMMY = 1;
	};


}
