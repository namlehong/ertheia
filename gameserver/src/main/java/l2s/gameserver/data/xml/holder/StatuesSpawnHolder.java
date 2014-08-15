package l2s.gameserver.data.xml.holder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.model.worldstatistics.CategoryType;
import l2s.gameserver.utils.Location;

public class StatuesSpawnHolder extends AbstractHolder
{
	private static StatuesSpawnHolder _instance;

	private Map<CategoryType, List<Location>> _spawnLocations;

	private StatuesSpawnHolder()
	{
		_spawnLocations = new HashMap<CategoryType, List<Location>>();
	}

	public static StatuesSpawnHolder getInstance()
	{
		if(_instance == null)
			_instance = new StatuesSpawnHolder();
		return _instance;
	}

	@Override
	public int size()
	{
		return _spawnLocations.size();
	}

	@Override
	public void clear()
	{
		_spawnLocations.clear();
	}

	public void addSpawnInfo(CategoryType categoryType, List<Location> locations)
	{
		_spawnLocations.put(categoryType, locations);
	}

	public Map<CategoryType, List<Location>> getSpawnLocations()
	{
		return Collections.unmodifiableMap(_spawnLocations);
	}
}
