package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;

/**
 * @author Cain
 */
public class ExMentorAdd extends L2GameServerPacket
{
	private String _newMentorName;
	private int _newMentorClassId, _newMentorLvl;

	public ExMentorAdd(Player newMentor)
	{
		_newMentorName = newMentor.getName();
		_newMentorClassId = newMentor.getClassId().getId();
		_newMentorLvl = newMentor.getLevel();
	}

	@Override
	protected final void writeImpl()
	{
		writeS(_newMentorName);
		writeD(_newMentorClassId);
		writeD(_newMentorLvl);
	}
}