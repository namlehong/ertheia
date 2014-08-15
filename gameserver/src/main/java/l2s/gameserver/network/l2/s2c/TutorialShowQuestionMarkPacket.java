package l2s.gameserver.network.l2.s2c;

public class TutorialShowQuestionMarkPacket extends L2GameServerPacket
{
	/**
	 * После клика по знаку вопроса клиент попросит html-ку с этим номером.
	 */
	private int _number;

	public TutorialShowQuestionMarkPacket(int number)
	{
		_number = number;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_number);
	}
}