package l2s.gameserver.model.base;

import l2s.gameserver.data.xml.holder.ClassDataHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.templates.player.ClassData;

public enum ClassId
{
	/*Human Fighter 1st and 2nd class list*/
	/*0*/HUMAN_FIGHTER(ClassType.FIGHTER, Race.HUMAN, null, ClassLevel.NONE, null),
	/*1*/WARRIOR(ClassType.FIGHTER, Race.HUMAN, HUMAN_FIGHTER, ClassLevel.FIRST, null),
	/*2*/GLADIATOR(ClassType.FIGHTER, Race.HUMAN, WARRIOR, ClassLevel.SECOND, ClassType2.WARRIOR),
	/*3*/WARLORD(ClassType.FIGHTER, Race.HUMAN, WARRIOR, ClassLevel.SECOND, ClassType2.WARRIOR),
	/*4*/KNIGHT(ClassType.FIGHTER, Race.HUMAN, HUMAN_FIGHTER, ClassLevel.FIRST, null),
	/*5*/PALADIN(ClassType.FIGHTER, Race.HUMAN, KNIGHT, ClassLevel.SECOND, ClassType2.KNIGHT),
	/*6*/DARK_AVENGER(ClassType.FIGHTER, Race.HUMAN, KNIGHT, ClassLevel.SECOND, ClassType2.KNIGHT),
	/*7*/ROGUE(ClassType.FIGHTER, Race.HUMAN, HUMAN_FIGHTER, ClassLevel.FIRST, null),
	/*8*/TREASURE_HUNTER(ClassType.FIGHTER, Race.HUMAN, ROGUE, ClassLevel.SECOND, ClassType2.ROGUE),
	/*9*/HAWKEYE(ClassType.FIGHTER, Race.HUMAN, ROGUE, ClassLevel.SECOND, ClassType2.ARCHER),

	/*Human Mage 1st and 2nd class list*/
	/*10*/HUMAN_MAGE(ClassType.MYSTIC, Race.HUMAN, null, ClassLevel.NONE, null),
	/*11*/WIZARD(ClassType.MYSTIC, Race.HUMAN, HUMAN_MAGE, ClassLevel.FIRST, null),
	/*12*/SORCERER(ClassType.MYSTIC, Race.HUMAN, WIZARD, ClassLevel.SECOND, ClassType2.WIZARD),
	/*13*/NECROMANCER(ClassType.MYSTIC, Race.HUMAN, WIZARD, ClassLevel.SECOND, ClassType2.WIZARD),
	/*14*/WARLOCK(ClassType.MYSTIC, Race.HUMAN, WIZARD, ClassLevel.SECOND, ClassType2.SUMMONER),
	/*15*/CLERIC(ClassType.MYSTIC, Race.HUMAN, HUMAN_MAGE, ClassLevel.FIRST, null),
	/*16*/BISHOP(ClassType.MYSTIC, Race.HUMAN, CLERIC, ClassLevel.SECOND, ClassType2.HEALER),
	/*17*/PROPHET(ClassType.MYSTIC, Race.HUMAN, CLERIC, ClassLevel.SECOND, ClassType2.ENCHANTER),

	/*Elven Fighter 1st and 2nd class list*/
	/*18*/ELVEN_FIGHTER(ClassType.FIGHTER, Race.ELF, null, ClassLevel.NONE, null),
	/*19*/ELVEN_KNIGHT(ClassType.FIGHTER, Race.ELF, ELVEN_FIGHTER, ClassLevel.FIRST, null),
	/*20*/TEMPLE_KNIGHT(ClassType.FIGHTER, Race.ELF, ELVEN_KNIGHT, ClassLevel.SECOND, ClassType2.KNIGHT),
	/*21*/SWORDSINGER(ClassType.FIGHTER, Race.ELF, ELVEN_KNIGHT, ClassLevel.SECOND, ClassType2.ENCHANTER),
	/*22*/ELVEN_SCOUT(ClassType.FIGHTER, Race.ELF, ELVEN_FIGHTER, ClassLevel.FIRST, null),
	/*23*/PLAIN_WALKER(ClassType.FIGHTER, Race.ELF, ELVEN_SCOUT, ClassLevel.SECOND, ClassType2.ROGUE),
	/*24*/SILVER_RANGER(ClassType.FIGHTER, Race.ELF, ELVEN_SCOUT, ClassLevel.SECOND, ClassType2.ARCHER),

	/*Elven Mage 1st and 2nd class list*/
	/*25*/ELVEN_MAGE(ClassType.MYSTIC, Race.ELF, null, ClassLevel.NONE, null),
	/*26*/ELVEN_WIZARD(ClassType.MYSTIC, Race.ELF, ELVEN_MAGE, ClassLevel.FIRST, null),
	/*27*/SPELLSINGER(ClassType.MYSTIC, Race.ELF, ELVEN_WIZARD, ClassLevel.SECOND, ClassType2.WIZARD),
	/*28*/ELEMENTAL_SUMMONER(ClassType.MYSTIC, Race.ELF, ELVEN_WIZARD, ClassLevel.SECOND, ClassType2.SUMMONER),
	/*29*/ORACLE(ClassType.MYSTIC, Race.ELF, ELVEN_MAGE, ClassLevel.FIRST, null),
	/*30*/ELDER(ClassType.MYSTIC, Race.ELF, ORACLE, ClassLevel.SECOND, ClassType2.HEALER),

	/*Darkelf Fighter 1st and 2nd class list*/
	/*31*/DARK_FIGHTER(ClassType.FIGHTER, Race.DARKELF, null, ClassLevel.NONE, null),
	/*32*/PALUS_KNIGHT(ClassType.FIGHTER, Race.DARKELF, DARK_FIGHTER, ClassLevel.FIRST, null),
	/*33*/SHILLEN_KNIGHT(ClassType.FIGHTER, Race.DARKELF, PALUS_KNIGHT, ClassLevel.SECOND, ClassType2.KNIGHT),
	/*34*/BLADEDANCER(ClassType.FIGHTER, Race.DARKELF, PALUS_KNIGHT, ClassLevel.SECOND, ClassType2.ENCHANTER),
	/*35*/ASSASIN(ClassType.FIGHTER, Race.DARKELF, DARK_FIGHTER, ClassLevel.FIRST, null),
	/*36*/ABYSS_WALKER(ClassType.FIGHTER, Race.DARKELF, ASSASIN, ClassLevel.SECOND, ClassType2.ROGUE),
	/*37*/PHANTOM_RANGER(ClassType.FIGHTER, Race.DARKELF, ASSASIN, ClassLevel.SECOND, ClassType2.ARCHER),

	/*Darkelf Mage 1st and 2nd class list*/
	/*38*/DARK_MAGE(ClassType.MYSTIC, Race.DARKELF, null, ClassLevel.NONE, null),
	/*39*/DARK_WIZARD(ClassType.MYSTIC, Race.DARKELF, DARK_MAGE, ClassLevel.FIRST, null),
	/*40*/SPELLHOWLER(ClassType.MYSTIC, Race.DARKELF, DARK_WIZARD, ClassLevel.SECOND, ClassType2.WIZARD),
	/*41*/PHANTOM_SUMMONER(ClassType.MYSTIC, Race.DARKELF, DARK_WIZARD, ClassLevel.SECOND, ClassType2.SUMMONER),
	/*42*/SHILLEN_ORACLE(ClassType.MYSTIC, Race.DARKELF, DARK_MAGE, ClassLevel.FIRST, null),
	/*43*/SHILLEN_ELDER(ClassType.MYSTIC, Race.DARKELF, SHILLEN_ORACLE, ClassLevel.SECOND, ClassType2.HEALER),

	/*Orc Fighter 1st and 2nd class list*/
	/*44*/ORC_FIGHTER(ClassType.FIGHTER, Race.ORC, null, ClassLevel.NONE, null),
	/*45*/ORC_RAIDER(ClassType.FIGHTER, Race.ORC, ORC_FIGHTER, ClassLevel.FIRST, null),
	/*46*/DESTROYER(ClassType.FIGHTER, Race.ORC, ORC_RAIDER, ClassLevel.SECOND, ClassType2.WARRIOR),
	/*47*/ORC_MONK(ClassType.FIGHTER, Race.ORC, ORC_FIGHTER, ClassLevel.FIRST, null),
	/*48*/TYRANT(ClassType.FIGHTER, Race.ORC, ORC_MONK, ClassLevel.SECOND, ClassType2.WARRIOR),

	/*Orc Mage 1st and 2nd class list*/
	/*49*/ORC_MAGE(ClassType.MYSTIC, Race.ORC, null, ClassLevel.NONE, null),
	/*50*/ORC_SHAMAN(ClassType.MYSTIC, Race.ORC, ORC_MAGE, ClassLevel.FIRST, null),
	/*51*/OVERLORD(ClassType.MYSTIC, Race.ORC, ORC_SHAMAN, ClassLevel.SECOND, ClassType2.ENCHANTER),
	/*52*/WARCRYER(ClassType.MYSTIC, Race.ORC, ORC_SHAMAN, ClassLevel.SECOND, ClassType2.ENCHANTER),

	/*Dwarf Fighter 1st and 2nd class list*/
	/*53*/DWARVEN_FIGHTER(ClassType.FIGHTER, Race.DWARF, null, ClassLevel.NONE, null),
	/*54*/SCAVENGER(ClassType.FIGHTER, Race.DWARF, DWARVEN_FIGHTER, ClassLevel.FIRST, null),
	/*55*/BOUNTY_HUNTER(ClassType.FIGHTER, Race.DWARF, SCAVENGER, ClassLevel.SECOND, ClassType2.ROGUE),
	/*56*/ARTISAN(ClassType.FIGHTER, Race.DWARF, DWARVEN_FIGHTER, ClassLevel.FIRST, null),
	/*57*/WARSMITH(ClassType.FIGHTER, Race.DWARF, ARTISAN, ClassLevel.SECOND, ClassType2.WARRIOR),

	/*Dummy Entries*/
	/*58*/DUMMY_ENTRY_58,
	/*59*/DUMMY_ENTRY_59,
	/*60*/DUMMY_ENTRY_60,
	/*61*/DUMMY_ENTRY_61,
	/*62*/DUMMY_ENTRY_62,
	/*63*/DUMMY_ENTRY_63,
	/*64*/DUMMY_ENTRY_64,
	/*65*/DUMMY_ENTRY_65,
	/*66*/DUMMY_ENTRY_66,
	/*67*/DUMMY_ENTRY_67,
	/*68*/DUMMY_ENTRY_68,
	/*69*/DUMMY_ENTRY_69,
	/*70*/DUMMY_ENTRY_70,
	/*71*/DUMMY_ENTRY_71,
	/*72*/DUMMY_ENTRY_72,
	/*73*/DUMMY_ENTRY_73,
	/*74*/DUMMY_ENTRY_74,
	/*75*/DUMMY_ENTRY_75,
	/*76*/DUMMY_ENTRY_76,
	/*77*/DUMMY_ENTRY_77,
	/*78*/DUMMY_ENTRY_78,
	/*79*/DUMMY_ENTRY_79,
	/*80*/DUMMY_ENTRY_80,
	/*81*/DUMMY_ENTRY_81,
	/*82*/DUMMY_ENTRY_82,
	/*83*/DUMMY_ENTRY_83,
	/*84*/DUMMY_ENTRY_84,
	/*85*/DUMMY_ENTRY_85,
	/*86*/DUMMY_ENTRY_86,
	/*87*/DUMMY_ENTRY_87,

	/*Human Fighter 3th class list*/
	/*88*/DUELIST(ClassType.FIGHTER, Race.HUMAN, GLADIATOR, ClassLevel.THIRD, ClassType2.WARRIOR),
	/*89*/DREADNOUGHT(ClassType.FIGHTER, Race.HUMAN, WARLORD, ClassLevel.THIRD, ClassType2.WARRIOR),
	/*90*/PHOENIX_KNIGHT(ClassType.FIGHTER, Race.HUMAN, PALADIN, ClassLevel.THIRD, ClassType2.KNIGHT),
	/*91*/HELL_KNIGHT(ClassType.FIGHTER, Race.HUMAN, DARK_AVENGER, ClassLevel.THIRD, ClassType2.KNIGHT),
	/*92*/SAGITTARIUS(ClassType.FIGHTER, Race.HUMAN, HAWKEYE, ClassLevel.THIRD, ClassType2.ARCHER),
	/*93*/ADVENTURER(ClassType.FIGHTER, Race.HUMAN, TREASURE_HUNTER, ClassLevel.THIRD, ClassType2.ROGUE),

	/*Human Mage 3th class list*/
	/*94*/ARCHMAGE(ClassType.MYSTIC, Race.HUMAN, SORCERER, ClassLevel.THIRD, ClassType2.WIZARD),
	/*95*/SOULTAKER(ClassType.MYSTIC, Race.HUMAN, NECROMANCER, ClassLevel.THIRD, ClassType2.WIZARD),
	/*96*/ARCANA_LORD(ClassType.MYSTIC, Race.HUMAN, WARLOCK, ClassLevel.THIRD, ClassType2.SUMMONER),
	/*97*/CARDINAL(ClassType.MYSTIC, Race.HUMAN, BISHOP, ClassLevel.THIRD, ClassType2.HEALER),
	/*98*/HIEROPHANT(ClassType.MYSTIC, Race.HUMAN, PROPHET, ClassLevel.THIRD, ClassType2.ENCHANTER),

	/*Elven Fighter 3th class list*/
	/*99*/EVAS_TEMPLAR(ClassType.FIGHTER, Race.ELF, TEMPLE_KNIGHT, ClassLevel.THIRD, ClassType2.KNIGHT),
	/*100*/SWORD_MUSE(ClassType.FIGHTER, Race.ELF, SWORDSINGER, ClassLevel.THIRD, ClassType2.ENCHANTER),
	/*101*/WIND_RIDER(ClassType.FIGHTER, Race.ELF, PLAIN_WALKER, ClassLevel.THIRD, ClassType2.ROGUE),
	/*102*/MOONLIGHT_SENTINEL(ClassType.FIGHTER, Race.ELF, SILVER_RANGER, ClassLevel.THIRD, ClassType2.ARCHER),

	/*Elven Mage 3th class list*/
	/*103*/MYSTIC_MUSE(ClassType.MYSTIC, Race.ELF, SPELLSINGER, ClassLevel.THIRD, ClassType2.WIZARD),
	/*104*/ELEMENTAL_MASTER(ClassType.MYSTIC, Race.ELF, ELEMENTAL_SUMMONER, ClassLevel.THIRD, ClassType2.SUMMONER),
	/*105*/EVAS_SAINT(ClassType.MYSTIC, Race.ELF, ELDER, ClassLevel.THIRD, ClassType2.HEALER),

	/*Darkelf Fighter 3th class list*/
	/*106*/SHILLIEN_TEMPLAR(ClassType.FIGHTER, Race.DARKELF, SHILLEN_KNIGHT, ClassLevel.THIRD, ClassType2.KNIGHT),
	/*107*/SPECTRAL_DANCER(ClassType.FIGHTER, Race.DARKELF, BLADEDANCER, ClassLevel.THIRD, ClassType2.ENCHANTER),
	/*108*/GHOST_HUNTER(ClassType.FIGHTER, Race.DARKELF, ABYSS_WALKER, ClassLevel.THIRD, ClassType2.ROGUE),
	/*109*/GHOST_SENTINEL(ClassType.FIGHTER, Race.DARKELF, PHANTOM_RANGER, ClassLevel.THIRD, ClassType2.ARCHER),

	/*Darkelf Mage 3th class list*/
	/*110*/STORM_SCREAMER(ClassType.MYSTIC, Race.DARKELF, SPELLHOWLER, ClassLevel.THIRD, ClassType2.WIZARD),
	/*111*/SPECTRAL_MASTER(ClassType.MYSTIC, Race.DARKELF, PHANTOM_SUMMONER, ClassLevel.THIRD, ClassType2.SUMMONER),
	/*112*/SHILLIEN_SAINT(ClassType.MYSTIC, Race.DARKELF, SHILLEN_ELDER, ClassLevel.THIRD, ClassType2.HEALER),

	/*Orc Fighter 3th class list*/
	/*113*/TITAN(ClassType.FIGHTER, Race.ORC, DESTROYER, ClassLevel.THIRD, ClassType2.WARRIOR),
	/*114*/GRAND_KHAVATARI(ClassType.FIGHTER, Race.ORC, TYRANT, ClassLevel.THIRD, ClassType2.WARRIOR),

	/*Orc Mage 3th class list*/
	/*115*/DOMINATOR(ClassType.MYSTIC, Race.ORC, OVERLORD, ClassLevel.THIRD, ClassType2.ENCHANTER),
	/*116*/DOOMCRYER(ClassType.MYSTIC, Race.ORC, WARCRYER, ClassLevel.THIRD, ClassType2.ENCHANTER),

	/*Dwarf Fighter 3th class list*/
	/*117*/FORTUNE_SEEKER(ClassType.FIGHTER, Race.DWARF, BOUNTY_HUNTER, ClassLevel.THIRD, ClassType2.ROGUE),
	/*118*/MAESTRO(ClassType.FIGHTER, Race.DWARF, WARSMITH, ClassLevel.THIRD, ClassType2.WARRIOR),

	/*Dummy Entries*/
	/*119*/DUMMY_ENTRY_119,
	/*120*/DUMMY_ENTRY_120,
	/*121*/DUMMY_ENTRY_121,
	/*122*/DUMMY_ENTRY_122,

	/*Kamael Fighter 1st, 2nd and 3th class list*/
	/*123*/KAMAEL_M_SOLDIER(ClassType.FIGHTER, Race.KAMAEL, null, ClassLevel.NONE, null),
	/*124*/KAMAEL_F_SOLDIER(ClassType.FIGHTER, Race.KAMAEL, null, ClassLevel.NONE, null),
	/*125*/TROOPER(ClassType.FIGHTER, Race.KAMAEL, KAMAEL_M_SOLDIER, ClassLevel.FIRST, null),
	/*126*/WARDER(ClassType.FIGHTER, Race.KAMAEL, KAMAEL_F_SOLDIER, ClassLevel.FIRST, null),
	/*127*/BERSERKER(ClassType.FIGHTER, Race.KAMAEL, TROOPER, ClassLevel.SECOND, ClassType2.WARRIOR),
	/*128*/M_SOUL_BREAKER(ClassType.FIGHTER, Race.KAMAEL, TROOPER, ClassLevel.SECOND, ClassType2.WIZARD),
	/*129*/F_SOUL_BREAKER(ClassType.FIGHTER, Race.KAMAEL, WARDER, ClassLevel.SECOND, ClassType2.WIZARD),
	/*130*/ARBALESTER(ClassType.FIGHTER, Race.KAMAEL, WARDER, ClassLevel.SECOND, ClassType2.ARCHER),
	/*131*/DOOMBRINGER(ClassType.FIGHTER, Race.KAMAEL, BERSERKER, ClassLevel.THIRD, ClassType2.WARRIOR),
	/*132*/M_SOUL_HOUND(ClassType.FIGHTER, Race.KAMAEL, M_SOUL_BREAKER, ClassLevel.THIRD, ClassType2.WIZARD),
	/*133*/F_SOUL_HOUND(ClassType.FIGHTER, Race.KAMAEL, F_SOUL_BREAKER, ClassLevel.THIRD, ClassType2.WIZARD),
	/*134*/TRICKSTER(ClassType.FIGHTER, Race.KAMAEL, ARBALESTER, ClassLevel.THIRD, ClassType2.ARCHER),
	/*135*/INSPECTOR(ClassType.FIGHTER, Race.KAMAEL, TROOPER, WARDER, ClassLevel.SECOND, ClassType2.ENCHANTER),
	/*136*/JUDICATOR(ClassType.FIGHTER, Race.KAMAEL, INSPECTOR, ClassLevel.THIRD, ClassType2.ENCHANTER),

	/*Dummy Entries*/
	/*137*/DUMMY_ENTRY_137,
	/*138*/DUMMY_ENTRY_138,

	/*Awakened 4th class list*/
	/*139*/SIGEL_KNIGHT(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.KNIGHT, 30310, null)
	{
		@Override
		public ClassId getAwakeParent(ClassId classId)
		{
			if(classId == null)
				return PHOENIX_KNIGHT;

			switch(classId)
			{
				case HELL_KNIGHT:
				case EVAS_TEMPLAR:
				case SHILLIEN_TEMPLAR:
					return classId;
				default:
					return PHOENIX_KNIGHT;
			}
		}
	},
	/*140*/TYR_WARRIOR(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.WARRIOR, 30311, null)
	{
		@Override
		public ClassId getAwakeParent(ClassId classId)
		{
			if(classId == null)
				return DUELIST;

			switch(classId)
			{
				case DREADNOUGHT:
				case TITAN:
				case GRAND_KHAVATARI:
				case MAESTRO:
				case DOOMBRINGER:
					return classId;
				default:
					return DUELIST;
			}
		}
	},
	/*141*/OTHELL_ROGUE(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.ROGUE, 30312, null)
	{
		@Override
		public ClassId getAwakeParent(ClassId classId)
		{
			if(classId == null)
				return FORTUNE_SEEKER;

			switch(classId)
			{
				case ADVENTURER:
				case WIND_RIDER:
				case GHOST_HUNTER:
					return classId;
				default:
					return FORTUNE_SEEKER;
			}
		}
	},
	/*142*/YR_ARCHER(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.ARCHER, 30313, null)
	{
		@Override
		public ClassId getAwakeParent(ClassId classId)
		{
			if(classId == null)
				return SAGITTARIUS;

			switch(classId)
			{
				case MOONLIGHT_SENTINEL:
				case GHOST_SENTINEL:
				case TRICKSTER:
					return classId;
				default:
					return SAGITTARIUS;
			}
		}
	},
	/*143*/FEOH_WIZARD(ClassType.MYSTIC, ClassLevel.AWAKED, ClassType2.WIZARD, 30314, null)
	{
		@Override
		public ClassId getAwakeParent(ClassId classId)
		{
			if(classId == null)
				return ARCHMAGE;

			switch(classId)
			{
				case SOULTAKER:
				case MYSTIC_MUSE:
				case STORM_SCREAMER:
				case M_SOUL_HOUND:
				case F_SOUL_HOUND:
					return classId;
				default:
					return ARCHMAGE;
			}
		}
	},
	/*144*/ISS_ENCHANTER(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.ENCHANTER, 30316, null)
	{
		@Override
		public ClassId getAwakeParent(ClassId classId)
		{
			if(classId == null)
				return HIEROPHANT;

			switch(classId)
			{
				case SWORD_MUSE:
				case SPECTRAL_DANCER:
				case DOMINATOR:
				case DOOMCRYER:
				case JUDICATOR:
					return classId;
				default:
					return HIEROPHANT;
			}
		}
	},
	/*145*/WYNN_SUMMONER(ClassType.MYSTIC, ClassLevel.AWAKED, ClassType2.SUMMONER, 30315, null)
	{
		@Override
		public ClassId getAwakeParent(ClassId classId)
		{
			if(classId == null)
				return ARCANA_LORD;

			switch(classId)
			{
				case ELEMENTAL_MASTER:
				case SPECTRAL_MASTER:
					return classId;
				default:
					return ARCANA_LORD;
			}
		}
	},
	/*146*/EOLH_HEALER(ClassType.MYSTIC, ClassLevel.AWAKED, ClassType2.HEALER, 30317, null)
	{
		@Override
		public ClassId getAwakeParent(ClassId classId)
		{
			if(classId == null)
				return CARDINAL;

			switch(classId)
			{
				case EVAS_SAINT:
				case SHILLIEN_SAINT:
					return classId;
				default:
					return CARDINAL;
			}
		}
	},

	/*Dummy Entries*/
	/*147*/DUMMY_ENTRY_147,

	/*Sigel Knight*/
	/*148*/SIGEL_PHOENIX_KNIGHT(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.KNIGHT, 30310, SIGEL_KNIGHT)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return PHOENIX_KNIGHT;
		}
	},
	/*149*/SIGEL_HELL_KNIGHT(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.KNIGHT, 30310, SIGEL_KNIGHT)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return HELL_KNIGHT;
		}
	},
	/*150*/SIGEL_EVAS_TEMPLAR(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.KNIGHT, 30310, SIGEL_KNIGHT)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return EVAS_TEMPLAR;
		}
	},
	/*151*/SIGEL_SHILLIEN_TEMPLAR(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.KNIGHT, 30310, SIGEL_KNIGHT)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return SHILLIEN_TEMPLAR;
		}
	},

	/*Tyr Warrior*/
	/*152*/TYR_DUELIST(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.WARRIOR, 30311, TYR_WARRIOR)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return DUELIST;
		}
	},
	/*153*/TYR_DREADNOUGHT(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.WARRIOR, 30311, TYR_WARRIOR)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return DREADNOUGHT;
		}
	},
	/*154*/TYR_TITAN(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.WARRIOR, 30311, TYR_WARRIOR)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return TITAN;
		}
	},
	/*155*/TYR_GRAND_KHAVATARI(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.WARRIOR, 30311, TYR_WARRIOR)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return GRAND_KHAVATARI;
		}
	},
	/*156*/TYR_MAESTRO(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.WARRIOR, 30311, TYR_WARRIOR)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return MAESTRO;
		}
	},
	/*157*/TYR_DOOMBRINGER(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.WARRIOR, 30311, TYR_WARRIOR)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return DOOMBRINGER;
		}
	},

	/*Othell Rogue*/
	/*158*/OTHELL_ADVENTURER(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.ROGUE, 30312, OTHELL_ROGUE)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return ADVENTURER;
		}
	},
	/*159*/OTHELL_WIND_RIDER(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.ROGUE, 30312, OTHELL_ROGUE)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return WIND_RIDER;
		}
	},
	/*160*/OTHELL_GHOST_HUNTER(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.ROGUE, 30312, OTHELL_ROGUE)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return GHOST_HUNTER;
		}
	},
	/*161*/OTHELL_FORTUNE_SEEKER(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.ROGUE, 30312, OTHELL_ROGUE)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return FORTUNE_SEEKER;
		}
	},

	/*Yr Archer*/
	/*162*/YR_SAGITTARIUS(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.ARCHER, 30313, YR_ARCHER)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return SAGITTARIUS;
		}
	},
	/*163*/YR_MOONLIGHT_SENTINEL(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.ARCHER, 30313, YR_ARCHER)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return MOONLIGHT_SENTINEL;
		}
	},
	/*164*/YR_GHOST_SENTINEL(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.ARCHER, 30313, YR_ARCHER)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return GHOST_SENTINEL;
		}
	},
	/*165*/YR_TRICKSTER(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.ARCHER, 30313, YR_ARCHER)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return TRICKSTER;
		}
	},

	/*Feoh Wizard*/
	/*166*/FEOH_ARCHMAGE(ClassType.MYSTIC, ClassLevel.AWAKED, ClassType2.WIZARD, 30314, FEOH_WIZARD)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return ARCHMAGE;
		}
	},
	/*167*/FEOH_SOULTAKER(ClassType.MYSTIC, ClassLevel.AWAKED, ClassType2.WIZARD, 30314, FEOH_WIZARD)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return SOULTAKER;
		}
	},
	/*168*/FEOH_MYSTIC_MUSE(ClassType.MYSTIC, ClassLevel.AWAKED, ClassType2.WIZARD, 30314, FEOH_WIZARD)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return MYSTIC_MUSE;
		}
	},
	/*169*/FEOH_STORM_SCREAMER(ClassType.MYSTIC, ClassLevel.AWAKED, ClassType2.WIZARD, 30314, FEOH_WIZARD)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return STORM_SCREAMER;
		}
	},
	/*170*/FEOH_SOUL_HOUND(ClassType.MYSTIC, ClassLevel.AWAKED, ClassType2.WIZARD, 30314, FEOH_WIZARD)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			//TODO: [Bonux] Пересмотреть зависимость от пола.
			switch(classId)
			{
				case M_SOUL_HOUND:
				case F_SOUL_HOUND:
					return classId;
				default:
					return M_SOUL_HOUND;
			}
		}
	},

	/*Iss Enchanter*/
	/*171*/ISS_HIEROPHANT(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.ENCHANTER, 30316, ISS_ENCHANTER)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return HIEROPHANT;
		}
	},
	/*172*/ISS_SWORD_MUSE(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.ENCHANTER, 30316, ISS_ENCHANTER)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return SWORD_MUSE;
		}
	},
	/*173*/ISS_SPECTRAL_DANCER(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.ENCHANTER, 30316, ISS_ENCHANTER)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return SPECTRAL_DANCER;
		}
	},
	/*174*/ISS_DOMINATOR(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.ENCHANTER, 30316, ISS_ENCHANTER)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return DOMINATOR;
		}
	},
	/*175*/ISS_DOOMCRYER(ClassType.FIGHTER, ClassLevel.AWAKED, ClassType2.ENCHANTER, 30316, ISS_ENCHANTER)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return DOOMCRYER;
		}
	},

	/*Wynn Summoner*/
	/*176*/WYNN_ARCANA_LORD(ClassType.MYSTIC, ClassLevel.AWAKED, ClassType2.SUMMONER, 30315, WYNN_SUMMONER)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return ARCANA_LORD;
		}
	},
	/*177*/WYNN_ELEMENTAL_MASTER(ClassType.MYSTIC, ClassLevel.AWAKED, ClassType2.SUMMONER, 30315, WYNN_SUMMONER)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return ELEMENTAL_MASTER;
		}
	},
	/*178*/WYNN_SPECTRAL_MASTER(ClassType.MYSTIC, ClassLevel.AWAKED, ClassType2.SUMMONER, 30315, WYNN_SUMMONER)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return SPECTRAL_MASTER;
		}
	},

	/*Aeore Healer*/
	/*179*/AEORE_CARDINAL(ClassType.MYSTIC, ClassLevel.AWAKED, ClassType2.HEALER, 30317, EOLH_HEALER)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return CARDINAL;
		}
	},
	/*180*/AEORE_EVAS_SAINT(ClassType.MYSTIC, ClassLevel.AWAKED, ClassType2.HEALER, 30317, EOLH_HEALER)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return EVAS_SAINT;
		}
	},
	/*181*/AEORE_SHILLIEN_SAINT(ClassType.MYSTIC, ClassLevel.AWAKED, ClassType2.HEALER, 30317, EOLH_HEALER)
	{
		@Override
		public ClassId getBaseAwakeParent(ClassId classId)
		{
			return SHILLIEN_SAINT;
		}
	},

	/*Ertheia 1st, 2nd, 3th and 4th class list*/
	/*182*/ERTHEIA_FIGHTER(ClassType.FIGHTER, Race.ERTHEIA, null, ClassLevel.NONE, null), // Воин Артеас
	/*183*/ERTHEIA_MAGE(ClassType.MYSTIC, Race.ERTHEIA, null, ClassLevel.NONE, null), // Маг Артеас
	/*184*/MARAUDER(ClassType.FIGHTER, Race.ERTHEIA, ERTHEIA_FIGHTER, ClassLevel.FIRST, null), // Мародер
	/*185*/SAIHA_MAGE(ClassType.MYSTIC, Race.ERTHEIA, ERTHEIA_MAGE, ClassLevel.FIRST, null), // Маг Сайха
	/*186*/RANGER(ClassType.FIGHTER, Race.ERTHEIA, MARAUDER, ClassLevel.SECOND, ClassType2.WARRIOR), // Рейнджер
	/*187*/STORM_SAIHA_MAGE(ClassType.MYSTIC, Race.ERTHEIA, SAIHA_MAGE, ClassLevel.SECOND, ClassType2.WIZARD), // Маг Шторма Сайха
	/*188*/RANGER_GRAVITY(ClassType.FIGHTER, Race.ERTHEIA, RANGER, ClassLevel.THIRD, ClassType2.WARRIOR), // Рейнджер Гравитации
	/*189*/SAIHA_RULER(ClassType.MYSTIC, Race.ERTHEIA, STORM_SAIHA_MAGE, ClassLevel.THIRD, ClassType2.WIZARD); // Властитель Сайха

	public static final ClassId[] VALUES = values();

	private final Race _race;
	private final ClassId _parent;
	private final ClassId _parent_f;
	private final ClassId _firstParentM;
	private final ClassId _firstParentF;
	private final ClassLevel _level;
	private final ClassType _type;
	private final ClassType2 _type2;
	private final boolean _isDummy;
	private final int _cloakId;
	private final ClassId _baseAwakedClassId;

	private ClassId()
	{
		this(null, null, null, null, null, null, true, 0, null);
	}

	private ClassId(ClassType classType, ClassLevel level, ClassType2 type2, int cloakId, ClassId baseAwakedClassId)
	{
		this(classType, null, null, null, level, type2, false, cloakId, baseAwakedClassId);
	}

	private ClassId(ClassType classType, Race race, ClassId parent, ClassLevel level, ClassType2 type2)
	{
		this(classType, race, parent, null, level, type2, false, 0, null);
	}

	private ClassId(ClassType classType, Race race, ClassId parent, ClassId parent2, ClassLevel level, ClassType2 type2)
	{
		this(classType, race, parent, parent2, level, type2, false, 0, null);
	}

	private ClassId(ClassType classType, Race race, ClassId parent, ClassId parent2, ClassLevel level, ClassType2 type2, boolean isDummy, int cloakId, ClassId baseAwakedClassId)
	{
		_type = classType;
		_race = race;
		_parent = parent;
		_parent_f = parent2;
		_level = level;
		_type2 = type2;
		_isDummy = isDummy;
		_cloakId = cloakId;
		_baseAwakedClassId = baseAwakedClassId;
		_firstParentM = _parent == null ? this : _parent.getFirstParent(0);
		_firstParentF = _parent_f == null ? this : _parent_f.getFirstParent(1);
	}

	public final int getId()
	{
		return ordinal();
	}

	public final Race getRace()
	{
		return _race;
	}

	public final boolean isOfRace(Race race)
	{
		return _race == race;
	}

	public final ClassLevel getClassLevel()
	{
		return _level;
	}

	public final boolean isOfLevel(ClassLevel level)
	{
		return _level == level;
	}

	public final ClassType getType()
	{
		return _type;
	}

	public final boolean isOfType(ClassType type)
	{
		return _type == type;
	}

	public ClassType2 getType2()
	{
		return _type2;
	}

	public final boolean isOfType2(ClassType2 type)
	{
		return _type2 == type;
	}

	public final boolean isMage()
	{
		return _type.isMagician();
	}

	public final boolean isDummy()
	{
		return _isDummy;
	}

	public boolean childOf(ClassId cid)
	{
		if(isOfLevel(ClassLevel.AWAKED))
		{
			if(isOutdated() || cid.isOfLevel(ClassLevel.AWAKED) && cid.isOutdated())
				return cid.getType2() == getType2();

			ClassId parent = getBaseAwakeParent(cid);
			if(parent == cid)
				return true;

			return parent.childOf(cid);
		}

		if(_parent == null)
			return false;

		if(_parent == cid || _parent_f == cid)
			return true;

		return _parent.childOf(cid);

	}

	public final boolean equalsOrChildOf(ClassId cid)
	{
		return this == cid || childOf(cid);
	}

	public final ClassId getParent(int sex)
	{
		return sex == 0 || _parent_f == null ? _parent : _parent_f;
	}

	public final ClassId getFirstParent(int sex)
	{
		return sex == 0 || _firstParentF == null ? _firstParentM : _firstParentF;
	}

	public ClassData getClassData()
	{
		return ClassDataHolder.getInstance().getClassData(getId());
	}

	public double getBaseCp(int level)
	{
		return getClassData().getHpMpCpData(level).getCP();
	}

	public double getBaseHp(int level)
	{
		return getClassData().getHpMpCpData(level).getHP();
	}

	public double getBaseMp(int level)
	{
		return getClassData().getHpMpCpData(level).getMP();
	}

	public ClassId getBaseAwakedClassId()
	{
		return _baseAwakedClassId;
	}

	public ClassId getBaseAwakeParent(ClassId classId)
	{
		return null;
	}

	public ClassId getAwakeParent(ClassId classId)
	{
		if(getBaseAwakedClassId() != null)
			return getBaseAwakedClassId().getAwakeParent(classId);

		return null;
	}

	public int getCloakId()
	{
		return _cloakId;
	}

	public final String getName(Player player)
	{
		return new CustomMessage("l2s.gameserver.model.base.ClassId.name." + getId(), player).toString();
	}

	public boolean isOutdated()
	{
		return isOfLevel(ClassLevel.AWAKED) && getBaseAwakedClassId() == null;
	}

	public ClassId getAwakedClass()
	{
		for(ClassId classId : VALUES)
		{
			if(classId.isDummy())
				continue;

			if(classId.isOutdated())
				continue;

			if(!classId.isOfLevel(ClassLevel.AWAKED))
				continue;

			if(classId.getBaseAwakeParent(this) == this)
				return classId;
		}
		return null;
	}

	public boolean isBowPenaltyClass()
	{
		return getId() == 143 || getId() == 145 || getId() == 146;
	}

	public boolean isLast()
	{
		return isOfLevel(ClassLevel.AWAKED) || this == JUDICATOR || isOfRace(Race.ERTHEIA) && isOfLevel(ClassLevel.THIRD);
	}
}