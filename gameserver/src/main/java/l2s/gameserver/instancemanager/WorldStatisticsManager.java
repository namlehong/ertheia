package l2s.gameserver.instancemanager;

import l2s.gameserver.Config;
import l2s.gameserver.dao.WorldStatisticDAO;
import l2s.gameserver.data.xml.holder.StatuesSpawnHolder;
import l2s.gameserver.idfactory.IdFactory;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.WinnerStatueInstance;
import l2s.gameserver.model.worldstatistics.CategoryType;
import l2s.gameserver.model.worldstatistics.CharacterStatistic;
import l2s.gameserver.model.worldstatistics.CharacterStatisticElement;
import l2s.gameserver.templates.StatuesSpawnTemplate;
import l2s.gameserver.utils.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class WorldStatisticsManager
{
	public static final int STATISTIC_TOP_PLAYER_LIMIT = 100;
	public static final int STATUES_TOP_PLAYER_LIMIT = 5;
	private static WorldStatisticsManager _instance;
	private final List<WinnerStatueInstance> spawnedStatues;

	private WorldStatisticsManager()
	{
		if(!Config.ALLOW_WORLD_STATISTIC)
		{
			spawnedStatues = Collections.emptyList();
			return;
		}

		spawnedStatues = new ArrayList<WinnerStatueInstance>();
		spawnStatues();
	}

	public static WorldStatisticsManager getInstance()
	{
		if(_instance == null)
			_instance = new WorldStatisticsManager();
		return _instance;
	}

	private void spawnStatues()
	{
		if(!Config.ALLOW_WORLD_STATISTIC)
			return;

		Map<CategoryType, List<Location>> spawnLocations = StatuesSpawnHolder.getInstance().getSpawnLocations();
		List<StatuesSpawnTemplate> templates = WorldStatisticDAO.getInstance().getStatueTemplates(spawnLocations.keySet());
		for(StatuesSpawnTemplate template : templates)
		{
			List<Location> locations = spawnLocations.get(template.getCategoryType());
			for(Location loc : locations)
			{
				WinnerStatueInstance statue = new WinnerStatueInstance(IdFactory.getInstance().getNextId(), template);
				statue.spawnMe(loc);
				spawnedStatues.add(statue);
			}
		}
	}

	private void despawnStatues()
	{
		for(WinnerStatueInstance statue : spawnedStatues)
		{
			statue.deleteMe();
		}
		spawnedStatues.clear();
	}

	public final void updateStat(Player player, CategoryType categoryType, int subCategory, long valueAdd)
	{
		if(!Config.ALLOW_WORLD_STATISTIC)
			return;

		if(player.isGM())
			return;

		categoryType = CategoryType.getCategoryById(categoryType.getClientId(), subCategory);
		if(categoryType != null)
			WorldStatisticDAO.getInstance().updateStatisticFor(player, categoryType, valueAdd);
	}

	public void updateStat(Player player, CategoryType categoryType, long valueAdd)
	{
		if(!Config.ALLOW_WORLD_STATISTIC)
			return;

		updateStat(player, categoryType, 0, valueAdd);
	}

	public List<CharacterStatisticElement> getCurrentStatisticsForPlayer(int charId)
	{
		if(!Config.ALLOW_WORLD_STATISTIC)
			return Collections.emptyList();

		return WorldStatisticDAO.getInstance().getPersonalStatisticFor(charId);
	}

	public List<CharacterStatistic> getStatisticTop(CategoryType cat, boolean global, int limit)
	{
		if(!Config.ALLOW_WORLD_STATISTIC)
			return Collections.emptyList();

		return WorldStatisticDAO.getInstance().getStatisticForCategory(cat, global, limit);
	}

	public void resetMonthlyStatistic()
	{
		if(!Config.ALLOW_WORLD_STATISTIC)
			return;

		despawnStatues();
		WorldStatisticDAO.getInstance().recalculateWinners();
		spawnStatues();
	}

	public List<CharacterStatistic> getWinners(CategoryType categoryType, boolean global, int limit)
	{
		if(!Config.ALLOW_WORLD_STATISTIC)
			return Collections.emptyList();

		return WorldStatisticDAO.getInstance().getWinners(categoryType, global, limit);
	}
}
