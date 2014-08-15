package l2s.gameserver.templates.player;

import java.util.ArrayList;
import java.util.List;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.base.BaseStats;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.base.Sex;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.templates.item.StartItem;
import l2s.gameserver.utils.Location;

/**
 * @author Bonux
**/
public final class PlayerTemplate extends PCTemplate
{
	private final Race _race;
	private final Sex _sex;

	private final int _baseLUC;
	private final int _baseCHA;

	private final int _minINT;
	private final int _minSTR;
	private final int _minCON;
	private final int _minMEN;
	private final int _minDEX;
	private final int _minWIT;
	private final int _minLUC;
	private final int _minCHA;

	private final int _maxINT;
	private final int _maxSTR;
	private final int _maxCON;
	private final int _maxMEN;
	private final int _maxDEX;
	private final int _maxWIT;
	private final int _maxLUC;
	private final int _maxCHA;

	private final double _baseSafeFallHeight;
	private final double _baseBreathBonus;

	private final List<Location> _startLocs = new ArrayList<Location>();
	private final List<StartItem> _startItems = new ArrayList<StartItem>();

	public PlayerTemplate(StatsSet set, Race race, Sex sex)
	{
		super(set);

		_race = race;
		_sex = sex;

		_baseLUC = set.getInteger("baseLUC", 1);
		_baseCHA = set.getInteger("baseCHA", 1);

		_minINT = set.getInteger("minINT", 1);
		_minSTR = set.getInteger("minSTR", 1);
		_minCON = set.getInteger("minCON", 1);
		_minMEN = set.getInteger("minMEN", 1);
		_minDEX = set.getInteger("minDEX", 1);
		_minWIT = set.getInteger("minWIT", 1);
		_minLUC = set.getInteger("minLUC", 1);
		_minCHA = set.getInteger("minCHA", 1);

		_maxINT = set.getInteger("maxINT", BaseStats.MAX_STAT_VALUE);
		_maxSTR = set.getInteger("maxSTR", BaseStats.MAX_STAT_VALUE);
		_maxCON = set.getInteger("maxCON", BaseStats.MAX_STAT_VALUE);
		_maxMEN = set.getInteger("maxMEN", BaseStats.MAX_STAT_VALUE);
		_maxDEX = set.getInteger("maxDEX", BaseStats.MAX_STAT_VALUE);
		_maxWIT = set.getInteger("maxWIT", BaseStats.MAX_STAT_VALUE);
		_maxLUC = set.getInteger("maxLUC", BaseStats.MAX_STAT_VALUE);
		_maxCHA = set.getInteger("maxCHA", BaseStats.MAX_STAT_VALUE);

		_baseSafeFallHeight = set.getDouble("baseSafeFallHeight");
		_baseBreathBonus = set.getDouble("baseBreathBonus");
	}

	public Race getRace()
	{
		return _race;
	}

	public Sex getSex()
	{
		return _sex;
	}

	public int getBaseLUC()
	{
		return _baseLUC;
	}

	public int getBaseCHA()
	{
		return _baseCHA;
	}

	public int getMinINT()
	{
		return _minINT;
	}

	public int getMinSTR()
	{
		return _minSTR;
	}

	public int getMinCON()
	{
		return _minCON;
	}

	public int getMinMEN()
	{
		return _minMEN;
	}

	public int getMinDEX()
	{
		return _minDEX;
	}

	public int getMinWIT()
	{
		return _minWIT;
	}

	public int getMinLUC()
	{
		return _minLUC;
	}

	public int getMinCHA()
	{
		return _minCHA;
	}

	public int getMaxINT()
	{
		return _maxINT;
	}

	public int getMaxSTR()
	{
		return _maxSTR;
	}

	public int getMaxCON()
	{
		return _maxCON;
	}

	public int getMaxMEN()
	{
		return _maxMEN;
	}

	public int getMaxDEX()
	{
		return _maxDEX;
	}

	public int getMaxWIT()
	{
		return _maxWIT;
	}

	public int getMaxLUC()
	{
		return _maxLUC;
	}

	public int getMaxCHA()
	{
		return _maxCHA;
	}

	public double getBaseBreathBonus()
	{
		return _baseBreathBonus;
	}

	public double getBaseSafeFallHeight()
	{
		return _baseSafeFallHeight;
	}

	public void addStartItem(StartItem item)
	{
		_startItems.add(item);
	}

	public StartItem[] getStartItems()
	{
		return _startItems.toArray(new StartItem[_startItems.size()]);
	}

	public void addStartLocation(Location loc)
	{
		_startLocs.add(loc);
	}

	public Location getStartLocation()
	{
		return _startLocs.get(Rnd.get(_startLocs.size()));
	}
}