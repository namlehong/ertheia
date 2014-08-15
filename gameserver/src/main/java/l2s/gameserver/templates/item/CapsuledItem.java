package l2s.gameserver.templates.item;

/**
 * @author Bonux
 */
public class CapsuledItem
{
	private int _itemId;
	private long _minCount;
	private long _maxCount;
	private double _chance;

	public CapsuledItem(int itemId, long minCount, long maxCount, double chance)
	{
		_itemId = itemId;
		_minCount = minCount;
		_maxCount = maxCount;
		_chance = chance;
	}

	public int getItemId()
	{
		return _itemId;
	}

	public long getMinCount()
	{
		return _minCount;
	}

	public long getMaxCount()
	{
		return _maxCount;
	}

	public double getChance()
	{
		return _chance;
	}
}