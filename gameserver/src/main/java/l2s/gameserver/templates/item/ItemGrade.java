package l2s.gameserver.templates.item;

/**
 * @author Bonux
 */
public enum ItemGrade
{
	/*0*/NONE(ItemTemplate.CRYSTAL_NONE, 0),
	/*1*/D(ItemTemplate.CRYSTAL_D, 1),
	/*2*/C(ItemTemplate.CRYSTAL_C, 2),
	/*3*/B(ItemTemplate.CRYSTAL_B, 3),
	/*4*/A(ItemTemplate.CRYSTAL_A, 4),
	/*5*/S(ItemTemplate.CRYSTAL_S, 5),
	/*6*/S80(ItemTemplate.CRYSTAL_S, 5),
	/*7*/S84(ItemTemplate.CRYSTAL_S, 5),
	/*8*/R(ItemTemplate.CRYSTAL_R, 6),
	/*9*/R95(ItemTemplate.CRYSTAL_R, 6),
	/*10*/R99(ItemTemplate.CRYSTAL_R, 6);

	public static final ItemGrade[] VALUES = values();

	private final int _crystalId;
	private final int _extOrdinal;

	ItemGrade(int crystalId, int extOrdinal)
	{
		_crystalId = crystalId;
		_extOrdinal = extOrdinal;
	}

	public int getCrystalId()
	{
		return _crystalId;
	}

	public int extOrdinal()
	{
		return _extOrdinal;
	}
}