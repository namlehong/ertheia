package l2s.gameserver.tables;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.model.GameObjectsStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FakePlayersTable
{
	public static class Task implements Runnable
	{
		public void run()
		{
			try
			{
				if(_activeFakePlayers.size() < Math.max(0, GameObjectsStorage.getAllPlayersCount() - GameObjectsStorage.getAllOfflineCount()) * Config.FAKE_PLAYERS_PERCENT / 100 && _activeFakePlayers.size() < _fakePlayers.length)
				{
					if(Rnd.chance(10))
					{
						String player = _fakePlayers[Rnd.get(_fakePlayers.length)];
						if(player != null && !_activeFakePlayers.contains(player))
							_activeFakePlayers.add(player);
					}
				}
				else if(_activeFakePlayers.size() > 0)
					_activeFakePlayers.remove(Rnd.get(_activeFakePlayers.size()));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private static Logger _log = LoggerFactory.getLogger(FakePlayersTable.class);

	private static String[] _fakePlayers;
	private static List<String> _activeFakePlayers = new ArrayList<String>();

	private static FakePlayersTable _instance;

	public static FakePlayersTable getInstance()
	{
		if(_instance == null)
			new FakePlayersTable();
		return _instance;
	}

	public FakePlayersTable()
	{
		_instance = this;
		if(Config.ALLOW_FAKE_PLAYERS)
		{
			parseData();
			ThreadPoolManager.getInstance().scheduleAtFixedRate(new Task(), 180000, 1000);
		}
	}

	private static void parseData()
	{
		LineNumberReader lnr = null;
		try
		{
			File doorData = new File("./config/fake_players.list");
			lnr = new LineNumberReader(new BufferedReader(new FileReader(doorData)));
			String line;
			List<String> players_list = new ArrayList<String>();
			while((line = lnr.readLine()) != null)
			{
				if(line.trim().length() == 0 || line.startsWith("#"))
					continue;
				players_list.add(line);
			}
			_fakePlayers = players_list.toArray(new String[players_list.size()]);
			_log.info("FakePlayersTable: Loaded " + _fakePlayers.length + " Fake Players.");
		}
		catch(Exception e)
		{
			_log.warn("FakePlayersTable: Lists could not be initialized.");
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(lnr != null)
					lnr.close();
			}
			catch(Exception e1)
			{}
		}
	}

	public static int getFakePlayersCount()
	{
		return _activeFakePlayers.size();
	}

	public static List<String> getActiveFakePlayers()
	{
		return _activeFakePlayers;
	}
}