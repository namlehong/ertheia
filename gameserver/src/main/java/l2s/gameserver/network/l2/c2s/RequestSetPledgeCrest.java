package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.cache.CrestCache;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.network.l2.components.SystemMsg;

public class RequestSetPledgeCrest extends L2GameClientPacket
{
	private int _length;
	private byte[] _data;

	@Override
	protected void readImpl()
	{
		_length = readD();
		if(_length == CrestCache.CREST_SIZE && _length == _buf.remaining())
		{
			_data = new byte[_length];
			readB(_data);
		}
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		Clan clan = activeChar.getClan();
		if((activeChar.getClanPrivileges() & Clan.CP_CL_EDIT_CREST) == Clan.CP_CL_EDIT_CREST)
		{
			if(clan.getLevel() < 3)
			{
				activeChar.sendPacket(SystemMsg.A_CLAN_CREST_CAN_ONLY_BE_REGISTERED_WHEN_THE_CLANS_SKILL_LEVEL_IS_3_OR_ABOVE);
				return;
			}

			int crestId = 0;

			if(_data != null)
				crestId = CrestCache.getInstance().savePledgeCrest(clan.getClanId(), _data);
			else if(clan.hasCrest())
				CrestCache.getInstance().removePledgeCrest(clan.getClanId());

			clan.setCrestId(crestId);
			clan.broadcastClanStatus(false, true, false);
		}
	}
}