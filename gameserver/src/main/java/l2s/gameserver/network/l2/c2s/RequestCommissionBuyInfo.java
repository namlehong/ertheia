package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.instancemanager.CommissionManager;
import l2s.gameserver.model.Player;

/**
 * @author Bonux
 */
public class RequestCommissionBuyInfo extends L2GameClientPacket
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

		CommissionManager.getInstance().sendCommissionBuyInfo(activeChar, (int) _bidId);
		//System.out.println("RequestCommissionBuyInfo: Q(" + _bidId + "), D(" + _unk2 + ")");
	}
}
