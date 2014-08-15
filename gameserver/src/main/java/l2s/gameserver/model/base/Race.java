package l2s.gameserver.model.base;

/**
 * This class defines all races (human, elf, darkelf, orc, dwarf) that a player can chose.<BR><BR>
 */
public enum Race
{
	HUMAN,
	ELF,
	DARKELF,
	ORC,
	DWARF,
	KAMAEL,
	ERTHEIA;

	public static final Race[] VALUES = values();
}