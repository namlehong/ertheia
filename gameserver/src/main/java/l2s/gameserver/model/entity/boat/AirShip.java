package l2s.gameserver.model.entity.boat;

import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.ExAirShipInfo;
import l2s.gameserver.network.l2.s2c.ExGetOffAirShip;
import l2s.gameserver.network.l2.s2c.ExGetOnAirShip;
import l2s.gameserver.network.l2.s2c.ExMoveToLocationAirShipPacket;
import l2s.gameserver.network.l2.s2c.ExMoveToLocationInAirShip;
import l2s.gameserver.network.l2.s2c.ExStopMoveAirShip;
import l2s.gameserver.network.l2.s2c.ExStopMoveInAirShip;
import l2s.gameserver.network.l2.s2c.ExValidateLocationInAirShip;
import l2s.gameserver.network.l2.s2c.L2GameServerPacket;
import l2s.gameserver.templates.CreatureTemplate;
import l2s.gameserver.utils.Location;

/**
 * @author VISTALL
 * @date  17:45/26.12.2010
 */
public class AirShip extends Boat
{
	private static final long serialVersionUID = 1L;

	public AirShip(int objectId, CreatureTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public L2GameServerPacket infoPacket()
	{
		return new ExAirShipInfo(this);
	}

	@Override
	public L2GameServerPacket movePacket()
	{
		return new ExMoveToLocationAirShipPacket(this);
	}

	@Override
	public L2GameServerPacket inMovePacket(Player player, Location src, Location desc)
	{
		return new ExMoveToLocationInAirShip(player, this, src, desc);
	}

	@Override
	public L2GameServerPacket stopMovePacket()
	{
		return new ExStopMoveAirShip(this);
	}

	@Override
	public L2GameServerPacket inStopMovePacket(Player player)
	{
		return new ExStopMoveInAirShip(player);
	}

	@Override
	public L2GameServerPacket startPacket()
	{
		return null;
	}

	@Override
	public L2GameServerPacket checkLocationPacket()
	{
		return null;
	}

	@Override
	public L2GameServerPacket validateLocationPacket(Player player)
	{
		return new ExValidateLocationInAirShip(player);
	}

	@Override
	public L2GameServerPacket getOnPacket(Playable playable, Location location)
	{
		if(!playable.isPlayer())
			return null;

		return new ExGetOnAirShip(playable.getPlayer(), this, location);
	}

	@Override
	public L2GameServerPacket getOffPacket(Playable playable, Location location)
	{
		if(!playable.isPlayer())
			return null;

		return new ExGetOffAirShip(playable.getPlayer(), this, location);
	}

	@Override
	public boolean isAirShip()
	{
		return true;
	}

	@Override
	public void oustPlayers()
	{
		for(Player player : _players)
		{
			oustPlayer(player, getReturnLoc(), true);
		}
	}
}
