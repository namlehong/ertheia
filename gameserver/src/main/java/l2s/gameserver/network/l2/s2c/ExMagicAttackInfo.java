package l2s.gameserver.network.l2.s2c;

/**
 *
 * @author monithly
 */
public class ExMagicAttackInfo extends L2GameServerPacket
{
	private final int _casterObjID, _targetObjID;

	public ExMagicAttackInfo(int casterObjID, int targetObjID)
	{
		_casterObjID = casterObjID;
		_targetObjID = targetObjID;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_casterObjID);
		writeD(_targetObjID);
		writeD(0x03);
	}
}
