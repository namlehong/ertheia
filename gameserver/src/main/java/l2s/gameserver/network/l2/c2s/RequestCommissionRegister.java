package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.instancemanager.CommissionManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.IStaticPacket;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExResponseCommissionItemList;
import l2s.gameserver.utils.CommissionUtils;

/**
 * @author Bonux
 */
public class RequestCommissionRegister extends L2GameClientPacket
{
	public int _itemObjectId;
	public String _itemName;
	public long _oneItemPrice;
	public long _itemsCount;
	public int _period;

	@Override
	protected void readImpl()
	{
		_itemObjectId = readD();
		_itemName = readS();
		_oneItemPrice = readQ();
		_itemsCount = readQ();
		_period = readD(); //period 0 - 1day, 1 - 3days, 2 - 5days, 3 - 7days

	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		int periodDays = CommissionUtils.getPeriodDays(_period);
		long commissionPrice = CommissionUtils.getCommissionPrice(_oneItemPrice * _itemsCount, periodDays);
		if(activeChar.getAdena() < commissionPrice)
		{
			activeChar.sendPacket(SystemMsg.NOT_ENOUGH_ADENA_FOR_REGISTER_THIS_ITEM);
			return;
		}

		IStaticPacket msg = CommissionManager.getInstance().registerItem(activeChar, _itemObjectId, _oneItemPrice, _itemsCount, periodDays);
		if(msg != null)
		{
			activeChar.sendPacket(msg);
			return;
		}

		activeChar.reduceAdena(commissionPrice);
		activeChar.sendPacket(new ExResponseCommissionItemList(activeChar));
		CommissionManager.getInstance().sendMyCommissionList(activeChar);
		activeChar.sendPacket(SystemMsg.ITEM_REGISTER_WAS_SUCCESSFUL);
	}
}
