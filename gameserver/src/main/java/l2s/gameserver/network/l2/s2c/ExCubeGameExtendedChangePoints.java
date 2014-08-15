package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;

/**
 * Format: (chd) dddddd
 * d: time left
 * d: blue points
 * d: red points
 * d: team
 * d: player object id
 * d: player points
 */
public class ExCubeGameExtendedChangePoints extends L2GameServerPacket
{
	private int _timeLeft;
	private int _bluePoints;
	private int _redPoints;
	private boolean _isRedTeam;
	private int _objectId;
	private int _playerPoints;

	public ExCubeGameExtendedChangePoints(int timeLeft, int bluePoints, int redPoints, boolean isRedTeam, Player player, int playerPoints)
	{
		_timeLeft = timeLeft;
		_bluePoints = bluePoints;
		_redPoints = redPoints;
		_isRedTeam = isRedTeam;
		_objectId = player.getObjectId();
		_playerPoints = playerPoints;
	}

	@Override
	protected void writeImpl()
	{
		writeD(0x00);

		writeD(_timeLeft);
		writeD(_bluePoints);
		writeD(_redPoints);

		writeD(_isRedTeam ? 0x01 : 0x00);
		writeD(_objectId);
		writeD(_playerPoints);
	}
}