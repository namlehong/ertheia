package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;

/**
 * @author Bonux
**/
public class ExPledgeWaitingUser extends L2GameServerPacket
{
	private int _objectId;
	private String _name;

	public ExPledgeWaitingUser(Player player)
	{
		_objectId = player.getObjectId();
		_name = player.getName();
	}

	protected void writeImpl()
	{
		writeD(_objectId);
		writeS(_name);
	}
}