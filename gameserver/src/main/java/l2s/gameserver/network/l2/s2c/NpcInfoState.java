package l2s.gameserver.network.l2.s2c;

/**
 * @author Bonux
**/
public class NpcInfoState extends L2GameServerPacket
{
	private final int _objectId, _state;

	public NpcInfoState(int objectId, int state)
	{
		_objectId = objectId;
		_state = state;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_objectId);
		writeC(_state);
	}
}
