package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.data.xml.holder.JumpTracksHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.Zone.ZoneType;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.network.l2.s2c.ExFlyMove;
import l2s.gameserver.network.l2.s2c.ExFlyMoveBroadcast;
import l2s.gameserver.network.l2.s2c.FlyToLocationPacket;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.templates.jump.JumpTrack;
import l2s.gameserver.templates.jump.JumpWay;
import l2s.gameserver.utils.Location;

/**
 * @author Bonux
 */
public class RequestFlyMoveStart extends L2GameClientPacket
{
	protected void readImpl()
	{
		//
	}

	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(!activeChar.isAwaked() && !activeChar.isGM())
			return;

		//TODO: [Bonux] Добавить условия.
		if(activeChar.getServitors().length > 0)
		{
			activeChar.sendPacket(SystemMsg.YOU_MAY_NOT_USE_SAYUNE_WHILE_PET_OR_SUMMONED_PET_IS_OUT);
			return;
		}

		if(activeChar.isTransformed() || activeChar.isMounted())
			return;

		Zone zone = activeChar.getZone(ZoneType.JUMPING);
		if(zone == null)
			return;

		JumpTrack track = JumpTracksHolder.getInstance().getTrack(zone.getTemplate().getJumpTrackId());
		if(track == null)
			return;

		Location destLoc = track.getStartLocation();
		activeChar.sendPacket(new FlyToLocationPacket(activeChar, destLoc, FlyToLocationPacket.FlyType.DUMMY, 0, 0, 0));

		JumpWay way = track.getWay(0);
		if(way == null)
			return;

		activeChar.setJumpState(Player.JumpState.IN_PROGRESS);
		activeChar.block();
		activeChar.sendPacket(new ExFlyMove(activeChar.getObjectId(), way.getPoints(), track.getId()));
		activeChar.broadcastPacketToOthers(new ExFlyMoveBroadcast(activeChar, destLoc));
		activeChar.setVar("@safe_jump_loc", activeChar.getLoc().toXYZString(), -1);
		activeChar.setCurrentJumpTrack(track);
		activeChar.setCurrentJumpWay(way);
	}
}