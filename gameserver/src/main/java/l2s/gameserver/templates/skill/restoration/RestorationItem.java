package l2s.gameserver.templates.skill.restoration;

import l2s.commons.util.Rnd;

/**
 * @author Bonux
 */
public final class RestorationItem
{
	private final int _id;
	private final int _minCount;
	private final int _maxCount;

	public RestorationItem(int id, int minCount, int maxCount)
	{
		_id = id;
		_minCount = minCount;
		_maxCount = maxCount;
	}

	public int getId()
	{
		return _id;
	}

	public int getMinCount()
	{
		return _minCount;
	}

	public int getMaxCount()
	{
		return _maxCount;
	}

	public int getRandomCount()
	{
		return Rnd.get(_minCount, _maxCount);
	}
}
