package l2s.gameserver.templates.item;

/**
 * @author Bonux
 */
public final class StartItem
{
	private final int _id;
	private final long _count;
	private final boolean _equiped;
	private final int _enchantLevel;

	public StartItem(int id, long count, boolean equiped, int enchantLevel)
	{
		_id = id;
		_count = count;
		_equiped = equiped;
		_enchantLevel = enchantLevel;
	}

	public int getItemId()
	{
		return _id;
	}

	public long getCount()
	{
		return _count;
	}

	public boolean isEquiped()
	{
		return _equiped;
	}

	public int getEnchantLevel()
	{
		return _enchantLevel;
	}
}
