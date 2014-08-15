package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.utils.Location;

public class ObserverEnd extends L2GameServerPacket
{
	// ddSS
	private Location _loc;

	public ObserverEnd(Location loc)
	{
		_loc = loc;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z);
	}
}