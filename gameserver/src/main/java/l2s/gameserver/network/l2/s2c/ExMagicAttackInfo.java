package l2s.gameserver.network.l2.s2c;

/**
 *
 * @author monithly
 */
public class ExMagicAttackInfo extends L2GameServerPacket
{
	private final int _targetId, _skillId;

	public ExMagicAttackInfo(int targetId, int skillId)
	{
		_targetId = targetId;
		_skillId = skillId;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_targetId);
		writeD(_skillId);
		writeD(0x01);
	}
}
