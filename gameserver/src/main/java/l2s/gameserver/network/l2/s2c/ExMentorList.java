package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.Mentee;

public class ExMentorList extends L2GameServerPacket
{
	private Mentee[] _list;
	private int _mentor;

	public ExMentorList(Player player)
	{
		_mentor = player.getMenteeList().getMentor();
		_list = player.getMenteeList().values();
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_mentor == 0 ? 0x01 : 0x02); // 02 приходит ученику, 01 - наставнику
		writeD(0x00); // UNK
		writeD(_list.length); // Размер  следующего списка
		for(Mentee m : _list)
		{
			writeD(m.getObjectId()); // objectId
			writeS(m.getName()); // nickname
			writeD(m.getClassId());//classId
			writeD(m.getLevel());// level
			writeD(m.isOnline()); //online
		}
	}
}