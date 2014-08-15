package l2s.gameserver.templates.jump;

import l2s.gameserver.utils.Location;

/**
 * @author Bonux
 */
public class JumpPoint
{
	private final Location _loc;
	private final int _nextWayId;

	public JumpPoint(Location loc, int nextWayId)
	{
		_loc = loc;
		_nextWayId = nextWayId;
	}

	public Location getLocation()
	{
		return _loc;
	}

	public int getNextWayId()
	{
		return _nextWayId;
	}
}