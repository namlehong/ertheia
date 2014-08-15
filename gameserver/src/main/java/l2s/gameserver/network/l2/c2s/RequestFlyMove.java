package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.ExFlyMove;
import l2s.gameserver.network.l2.s2c.ExFlyMoveBroadcast;
import l2s.gameserver.templates.jump.JumpPoint;
import l2s.gameserver.templates.jump.JumpTrack;
import l2s.gameserver.templates.jump.JumpWay;
import l2s.gameserver.utils.Location;

/**
 * @author Bonux
 */
public final class RequestFlyMove extends L2GameClientPacket
{
	private int _nextWayId;

	@Override
	protected void readImpl()
	{
		_nextWayId = readD();
	}

	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		JumpWay way = activeChar.getCurrentJumpWay();
		if(way == null)
		{
			activeChar.onJumpingBreak();
			return;
		}

		JumpPoint point = way.getJumpPoint(_nextWayId);
		if(point == null)
		{
			activeChar.onJumpingBreak();
			return;
		}

		Location destLoc = point.getLocation();
		activeChar.broadcastPacketToOthers(new ExFlyMoveBroadcast(activeChar, destLoc));
		activeChar.setLoc(destLoc);

		JumpTrack track = activeChar.getCurrentJumpTrack();
		if(track == null)
		{
			activeChar.onJumpingBreak();
			return;
		}

		JumpWay nextWay = track.getWay(_nextWayId);
		if(nextWay == null)
		{
			activeChar.onJumpingBreak();
			return;
		}

		activeChar.sendPacket(new ExFlyMove(activeChar.getObjectId(), nextWay.getPoints(), track.getId()));
		activeChar.setCurrentJumpWay(nextWay);
	}
}