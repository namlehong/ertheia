package l2s.gameserver.listener.zone.impl;

import l2s.commons.lang.reference.HardReference;
import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.Zone.ZoneType;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExNotifyFlyMoveStart;

/**
 * @author Bonux
 */
public class JumpingZoneListener implements OnZoneEnterLeaveListener
{
	private static class NotifyPacketTask extends RunnableImpl
	{
		private final Zone _zone;
		private final HardReference<Player> _playerRef;

		public NotifyPacketTask(Zone zone, Player player)
		{
			_zone = zone;
			_playerRef = player.getRef();
		}

		@Override
		public void runImpl()
		{
			Player player = _playerRef.get();
			if(player == null)
				return;

			if(!player.isInZone(ZoneType.JUMPING))
				return;

			if(player.getJumpState() != Player.JumpState.NONE)
				return;

			player.sendPacket(ExNotifyFlyMoveStart.STATIC);
			ThreadPoolManager.getInstance().schedule(new NotifyPacketTask(_zone, player), 500L);
		}
	}

	public static final OnZoneEnterLeaveListener STATIC = new JumpingZoneListener();

	@Override
	public void onZoneEnter(Zone zone, Creature actor)
	{
		if(!actor.isPlayer())
			return;

		Player player = actor.getPlayer();
		if(!player.isAwaked() && !player.isGM())
			return;

		//TODO: [Bonux] Добавить условия.
		if(player.getKarma() < 0)
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_USE_SAYUNE_WHILE_IN_A_CHAOTIC_STATE);
			return;
		}

		if(player.getServitors().length > 0)
		{
			player.sendPacket(SystemMsg.YOU_MAY_NOT_USE_SAYUNE_WHILE_PET_OR_SUMMONED_PET_IS_OUT);
			return;
		}

		if(player.isTransformed() || player.isMounted())
			return;

		if(player.getJumpState() != Player.JumpState.NONE)
			return;

		player.sendPacket(ExNotifyFlyMoveStart.STATIC);
		ThreadPoolManager.getInstance().schedule(new NotifyPacketTask(zone, player), 500L);
	}

	@Override
	public void onZoneLeave(Zone zone, Creature cha)
	{}
}
