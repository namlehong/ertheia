package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.instancemanager.CommissionManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.IStaticPacket;
import l2s.gameserver.network.l2.s2c.ExResponseCommissionBuyItem;

public class RequestCommissionBuyItem extends L2GameClientPacket
{
	public long _bidId;
	public int _unk2;

	@Override
	protected void readImpl()
	{
		_bidId = readQ();
		_unk2 = readD();

	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		IStaticPacket msg = CommissionManager.getInstance().buyItem(activeChar, (int) _bidId);
		if(msg != null)
		{
			activeChar.sendPacket(msg);
			activeChar.sendPacket(ExResponseCommissionBuyItem.FAILED);
			return;
		}
		activeChar.sendActionFailed();
	}
}
