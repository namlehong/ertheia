package l2s.gameserver.instancemanager;

import gnu.trove.map.TIntObjectMap;

import l2s.commons.util.Rnd;
import l2s.gameserver.GameServer;
import l2s.gameserver.dao.FakePlayerDAO;
import l2s.gameserver.data.xml.holder.FakePlayerPathHolder;
import l2s.gameserver.data.xml.holder.FakePlayersHolder;
import l2s.gameserver.listener.game.OnShutdownListener;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.instances.FakePlayerInstance;
import l2s.gameserver.templates.npc.FakePlayerPath;
import l2s.gameserver.templates.npc.FakePlayerTemplate;
import l2s.gameserver.utils.Location;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bonux
**/
public class FakePlayersSpawnManager
{
	private class ShutdownListener implements OnShutdownListener
	{
		@Override
		public void onShutdown()
		{
			for(FakePlayerInstance player : GameObjectsStorage.getAllFakePlayers())
				player.store();
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(FakePlayersSpawnManager.class);

	private static FakePlayersSpawnManager _instance = new FakePlayersSpawnManager();

	private ShutdownListener _listener = new ShutdownListener();

	public static FakePlayersSpawnManager getInstance()
	{
		return _instance;
	}

	public FakePlayersSpawnManager()
	{
		GameServer.getInstance().addListener(_listener);
	}

	public void spawnAll()
	{
		TIntObjectMap<Location> restored = FakePlayerDAO.getInstance().restore();
		FakePlayerTemplate[] fakePlayers = FakePlayersHolder.getInstance().getAll();
		int spawned = 0;
		for(FakePlayerTemplate template : fakePlayers)
		{
			if(spawn(template, restored.get(template.getPlayerId())))
				spawned++;
		}

		_log.info("FakePlayersSpawnManager: spawned " + spawned + " fake players.");
	}

	private boolean spawn(FakePlayerTemplate template, Location loc)
	{
		if(template.getSpawned() != null)
		{
			_log.warn(getClass().getSimpleName() + ": Dublicat spawn fake player! ID: " + template.getPlayerId());
			return false;
		}

		int pathId = -1;
		if(loc == null)
		{
			FakePlayerPath[] pathes = FakePlayerPathHolder.getInstance().getAll();
			if(pathes == null || pathes.length == 0)
				return false;

			FakePlayerPath path = pathes[Rnd.get(pathes.length)];
			FakePlayerPath.Point[] points = path.getPoints();
			if(points == null || points.length == 0)
				return false;

			FakePlayerPath.Point point = points[Rnd.get(points.length)];
			loc = point.getTerritory().getRandomLoc(ReflectionManager.DEFAULT.getGeoIndex());
			loc = Location.findPointToStay(loc, 100, ReflectionManager.DEFAULT.getGeoIndex());
			pathId = path.getId();
		}
		else
		{
			pathId = loc.h;
			loc = new Location(loc.getX(), loc.getY(), loc.getZ());
		}

		FakePlayerInstance fake = template.getNewInstance();
		fake.setCurrentHp(fake.getMaxHp(), false);
		fake.setCurrentMp(fake.getMaxMp());
		fake.setPathId(pathId);
		fake.setHeading(0);
		fake.setReflection(ReflectionManager.DEFAULT);
		fake.spawnMe(loc);

		return true;
	}
}