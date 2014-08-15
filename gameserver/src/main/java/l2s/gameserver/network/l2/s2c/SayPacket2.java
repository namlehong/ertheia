package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.network.l2.components.ChatType;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.components.SysString;
import l2s.gameserver.network.l2.components.SystemMsg;

public class SayPacket2 extends NpcStringContainer
{
	private ChatType _type;
	private SysString _sysString;
	private SystemMsg _systemMsg;

	private int _objectId;
	private String _charName;

	public SayPacket2(int objectId, ChatType type, SysString st, SystemMsg sm)
	{
		super(NpcString.NONE);
		_objectId = objectId;
		_type = type;
		_sysString = st;
		_systemMsg = sm;
	}

	public SayPacket2(int objectId, ChatType type, String charName, String text)
	{
		this(objectId, type, charName, NpcString.NONE, text);
	}

	public SayPacket2(int objectId, ChatType type, String charName, NpcString npcString, String... params)
	{
		super(npcString, params);
		_objectId = objectId;
		_type = type;
		_charName = charName;
	}

	public void setCharName(String name)
	{
		_charName = name;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_objectId);
		writeD(_type.ordinal());
		switch(_type)
		{
			case SYSTEM_MESSAGE:
				writeD(_sysString.getId());
				writeD(_systemMsg.getId());
				break;
			default:
				writeS(_charName);
				writeElements();
				break;
		}
	}
}