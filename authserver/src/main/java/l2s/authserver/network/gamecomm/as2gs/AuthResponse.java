package l2s.authserver.network.gamecomm.as2gs;

import l2s.authserver.network.gamecomm.GameServer;
import l2s.authserver.network.gamecomm.SendablePacket;

public class AuthResponse extends SendablePacket
{
	private int serverId;
	private String name;

	public AuthResponse(GameServer gs)
	{
		serverId = gs.getId();
		name = gs.getName();
	}

	@Override
	protected void writeImpl()
	{
		writeC(0x00);
		writeC(serverId);
		writeS(name);
	}
}