package l2s.gameserver.skills.combo;

import l2s.gameserver.model.Player;

public enum SkillComboType
{
	NO_COMBO(0),
	COMBO_FLY_UP(365) //root
	{
		@Override
		public int getComboSkillId(Player player)
		{
			switch(player.getClassId())
			{
				case SIGEL_KNIGHT:
				case SIGEL_PHOENIX_KNIGHT:
				case SIGEL_HELL_KNIGHT:
				case SIGEL_EVAS_TEMPLAR:
				case SIGEL_SHILLIEN_TEMPLAR:				
					return 10249;
				case TYR_WARRIOR:
				case TYR_DUELIST:
				case TYR_DREADNOUGHT:
				case TYR_TITAN:
				case TYR_GRAND_KHAVATARI:
				case TYR_MAESTRO:
				case TYR_DOOMBRINGER:				
					return 10499;
				case OTHELL_ROGUE:
				case OTHELL_ADVENTURER:
				case OTHELL_WIND_RIDER:
				case OTHELL_GHOST_HUNTER:
				case OTHELL_FORTUNE_SEEKER:				
					return 10749;
				case YR_ARCHER:
				case YR_SAGITTARIUS:
				case YR_MOONLIGHT_SENTINEL:
				case YR_GHOST_SENTINEL:
				case YR_TRICKSTER:				
					return 10999;
				case FEOH_WIZARD:
				case FEOH_ARCHMAGE:
				case FEOH_SOULTAKER:
				case FEOH_MYSTIC_MUSE:
				case FEOH_STORM_SCREAMER:
				case FEOH_SOUL_HOUND:				
					return 11249;
				case ISS_ENCHANTER:
				case ISS_HIEROPHANT:
				case ISS_SWORD_MUSE:
				case ISS_SPECTRAL_DANCER:
				case ISS_DOMINATOR:
				case ISS_DOOMCRYER:				
					return 11749;
				case WYNN_SUMMONER:
				case WYNN_ARCANA_LORD:
				case WYNN_ELEMENTAL_MASTER:
				case WYNN_SPECTRAL_MASTER:				
					return 11499;
				case EOLH_HEALER:
				case AEORE_CARDINAL:
				case AEORE_EVAS_SAINT:
				case AEORE_SHILLIEN_SAINT:				
					return 11999;
			}
			return 0;
		}
	},
	COMBO_KNOCK_DOWN(367) //stun
	{
		@Override
		public int getComboSkillId(Player player)
		{
			switch(player.getClassId())
			{
				case SIGEL_KNIGHT:
				case SIGEL_PHOENIX_KNIGHT:
				case SIGEL_HELL_KNIGHT:
				case SIGEL_EVAS_TEMPLAR:
				case SIGEL_SHILLIEN_TEMPLAR:
					return 10250;
				case TYR_WARRIOR:
				case TYR_DUELIST:
				case TYR_DREADNOUGHT:
				case TYR_TITAN:
				case TYR_GRAND_KHAVATARI:
				case TYR_MAESTRO:
				case TYR_DOOMBRINGER:
					return 10500;
				case OTHELL_ROGUE:
				case OTHELL_ADVENTURER:
				case OTHELL_WIND_RIDER:
				case OTHELL_GHOST_HUNTER:
				case OTHELL_FORTUNE_SEEKER:	
					return 10750;
				case YR_ARCHER:
				case YR_SAGITTARIUS:
				case YR_MOONLIGHT_SENTINEL:
				case YR_GHOST_SENTINEL:
				case YR_TRICKSTER:		
					return 11000;
				case FEOH_WIZARD:
				case FEOH_ARCHMAGE:
				case FEOH_SOULTAKER:
				case FEOH_MYSTIC_MUSE:
				case FEOH_STORM_SCREAMER:
				case FEOH_SOUL_HOUND:
					return 11250;
				case ISS_ENCHANTER:
				case ISS_HIEROPHANT:
				case ISS_SWORD_MUSE:
				case ISS_SPECTRAL_DANCER:
				case ISS_DOMINATOR:
				case ISS_DOOMCRYER:	
					return 11750;
				case WYNN_SUMMONER:
				case WYNN_ARCANA_LORD:
				case WYNN_ELEMENTAL_MASTER:
				case WYNN_SPECTRAL_MASTER:
					return 11500;
				case EOLH_HEALER:
				case AEORE_CARDINAL:
				case AEORE_EVAS_SAINT:
				case AEORE_SHILLIEN_SAINT:
					return 12000;
			}
			return 0;
		}
	},
	LINDVIOR_COMBO(499), //gives some combo for monsters I gues 145xxx skills
	UNK_457(457), //Empty with yul archer
	UNK_459(459); //Empty with yul archer

	private final int _id;

	private SkillComboType(int id)
	{
		_id = id;
	}

	public int getId()
	{
		return _id;
	}

	public int getComboSkillId(Player player)
	{
		return 0;
	}
}