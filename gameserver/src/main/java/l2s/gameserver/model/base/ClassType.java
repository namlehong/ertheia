package l2s.gameserver.model.base;

public enum ClassType
{
	FIGHTER,
	MYSTIC;

	public static final ClassType[] VALUES = values();

	public boolean isMagician()
	{
		return this != FIGHTER;
	}
}