package l2s.gameserver.templates.jump;

import gnu.trove.map.hash.TIntObjectHashMap;

import l2s.gameserver.utils.Location;

/**
 * @author Bonux
 */
public class JumpTrack
{
	private final int _id;
	private final TIntObjectHashMap<JumpWay> _trackWays;
	private final Location _startLoc;

	public JumpTrack(int id, Location startLoc)
	{
		_id = id;
		_trackWays = new TIntObjectHashMap<JumpWay>();
		_startLoc = startLoc;
	}

	public int getId()
	{
		return _id;
	}

	public JumpWay getWay(int id)
	{
		return _trackWays.get(id);
	}

	public void addWay(JumpWay way)
	{
		_trackWays.put(way.getId(), way);
	}

	public Location getStartLocation()
	{
		return _startLoc;
	}
}