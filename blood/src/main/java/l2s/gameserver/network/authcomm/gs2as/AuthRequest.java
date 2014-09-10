package l2s.gameserver.network.authcomm.gs2as;

import blood.BloodConfig;
import jonelo.jacksum.algorithm.Union;
import l2s.gameserver.Config;
import l2s.gameserver.GameServer;
import l2s.gameserver.network.authcomm.SendablePacket;

public class AuthRequest extends SendablePacket
{
	protected void writeImpl()
	{
		writeC(0x00);
		writeD(GameServer.AUTH_SERVER_PROTOCOL);
		writeC(Config.REQUEST_ID);
		writeC(Config.ACCEPT_ALTERNATE_ID ? 0x01 : 0x00);
		writeD(Config.AUTH_SERVER_SERVER_TYPE);
		writeD(Config.AUTH_SERVER_AGE_LIMIT);
		writeC(Config.AUTH_SERVER_GM_ONLY ? 0x01 : 0x00);
		writeC(Config.AUTH_SERVER_BRACKETS ? 0x01 : 0x00);
		writeC(Config.AUTH_SERVER_IS_PVP ? 0x01 : 0x00);
		writeS(Union.Unite(Config.EXTERNAL_HOSTNAME)); // TODO union must return external host and that's all
		writeS(Config.INTERNAL_HOSTNAME);
		writeH(Config.PORTS_GAME.length);
		for(int PORT_GAME : Config.PORTS_GAME)
			writeH(PORT_GAME);
		writeD(Config.MAXIMUM_ONLINE_USERS);
		writeD(BloodConfig.FENCE_PARENT_ID);
	}
}
