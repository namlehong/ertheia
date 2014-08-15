package l2s.gameserver.network.l2.c2s;

import org.apache.commons.lang3.StringUtils;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.network.l2.components.IStaticPacket;
import l2s.gameserver.network.l2.components.SystemMsg;

public class RequestFriendInvite extends L2GameClientPacket
{
	private String _name;

	@Override
	protected void readImpl()
	{
		_name = readS(16);
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null || StringUtils.isEmpty(_name))
			return;

		if(activeChar.isOutOfControl())
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.isChaosFestivalParticipant())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_INVITE_A_FRIEND_OR_PARTY_WHILE_PARTICIPATING_IN_THE_CEREMONY_OF_CHAOS);
			return;
		}

		GameObject target = World.getPlayer(_name);
		if(target == null)
			target = GameObjectsStorage.getFakePlayerByName(_name);

		IStaticPacket msg = activeChar.getFriendList().requestFriendInvite(target);
		if(msg != null)
		{
			activeChar.sendPacket(msg);
			activeChar.sendPacket(SystemMsg.YOU_HAVE_FAILED_TO_ADD_A_FRIEND_TO_YOUR_FRIENDS_LIST);
		}
	}
}