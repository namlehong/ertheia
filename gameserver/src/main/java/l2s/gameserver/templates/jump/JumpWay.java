package l2s.gameserver.templates.jump;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.Collection;

/**
 * @author Bonux
 */
public class JumpWay
{
	private final int _id;
	private final TIntObjectHashMap<JumpPoint> _points;

	public JumpWay(int id)
	{
		_id = id;
		_points = new TIntObjectHashMap<JumpPoint>();
	}

	public int getId()
	{
		return _id;
	}

	public Collection<JumpPoint> getPoints()
	{
		return _points.valueCollection();
	}

	public JumpPoint getJumpPoint(int nextWayId)
	{
		return _points.get(nextWayId);
	}

	public void addPoint(JumpPoint point)
	{
		_points.put(point.getNextWayId(), point);
	}
}