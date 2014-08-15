package l2s.gameserver.model.entity.events.objects;

import java.io.Serializable;

import l2s.gameserver.data.xml.holder.StaticObjectHolder;
import l2s.gameserver.model.entity.events.GlobalEvent;
import l2s.gameserver.model.instances.StaticObjectInstance;

/**
 * @author Bonux
 */
public class RewardObject implements Serializable
{
	private static final long serialVersionUID = 1L;

	private final int _itemId;
	private final int _minCount;
	private final int _maxCount;
	private final double _chance;

	public RewardObject(int itemId, int minCount, int maxCount, double chance)
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

	public int getMinCount()
	{
		return _minCount;
	}

	public int getMaxCount()
	{
		return _maxCount;
	}

	public double getChance()
	{
		return _chance;
	}
}
