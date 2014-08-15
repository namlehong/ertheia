package l2s.gameserver.network.l2.s2c;

/**
 * Format: (chd) ddd
 * d: time left
 * d: blue points
 * d: red points
 */
public class ExCubeGameChangePoints extends L2GameServerPacket
{
	int _timeLeft;
	int _bluePoints;
	int _redPoints;

	public ExCubeGameChangePoints(int timeLeft, int bluePoints, int redPoints)
	{
		_timeLeft = timeLeft;
		_bluePoints = bluePoints;
		_redPoints = redPoints;
	}

	@Override
	protected void writeImpl()
	{
		writeD(0x02);

		writeD(_timeLeft);
		writeD(_bluePoints);
		writeD(_redPoints);
	}
}