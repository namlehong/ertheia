package l2s.gameserver.network.l2.s2c;

public class ExCuriousHouseObserveMode extends L2GameServerPacket
{
	private final boolean _isInObserveMode;

	public ExCuriousHouseObserveMode(boolean isInObserveMode)
	{
		_isInObserveMode = isInObserveMode;
	}

	@Override
	protected void writeImpl()
	{
		writeC(_isInObserveMode ? 0 : 1);
	}
}
