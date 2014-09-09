package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.BuyListHolder.NpcTradeList;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.items.TradeItem;
import l2s.gameserver.network.l2.ServerPacketOpcodes;

public abstract class ExBuySellListPacket extends L2GameServerPacket
{
	@Override
	protected ServerPacketOpcodes getOpcodes()
	{
		return ServerPacketOpcodes.ExBuySellListPacket;
	}

	public static class BuyList extends ExBuySellListPacket
	{
		private final int _listId;
		private final List<TradeItem> _buyList;
		private final long _adena;
		private final double _taxRate;

		public BuyList(NpcTradeList tradeList, Player activeChar, double taxRate)
		{
			_adena = activeChar.getAdena();
			_taxRate = taxRate;

			if(tradeList != null)
			{
				_listId = tradeList.getListId();
				_buyList = tradeList.getItems();
				activeChar.setBuyListId(_listId);
			}
			else
			{
				_listId = 0;
				_buyList = Collections.emptyList();
				activeChar.setBuyListId(0);
			}
		}

		@Override
		protected void writeImpl()
		{
			writeD(0x00); // BUY LIST TYPE
			writeQ(_adena); // current money
			writeD(_listId);
			writeD(0x00); //TODO [Bonux] Awakening
			writeH(_buyList.size());
			for(TradeItem item : _buyList)
			{
				writeItemInfo(item, item.getCurrentValue());
				writeQ((long) (item.getOwnersPrice() * (1. + _taxRate)));
			}
		}
	}

	public static class SellRefundList extends ExBuySellListPacket
	{
		private final List<TradeItem> _sellList;
		private final List<TradeItem> _refundList;
		private int _done;

		public SellRefundList(Player activeChar, boolean done)
		{
			_done = done ? 1 : 0;
			if(done)
			{
				_refundList = Collections.emptyList();
				_sellList = Collections.emptyList();
			}
			else
			{
				ItemInstance[] items = activeChar.getRefund().getItems();
				_refundList = new ArrayList<TradeItem>(items.length);
				for(ItemInstance item : items)
					_refundList.add(new TradeItem(item));

				items = activeChar.getInventory().getItems();
				_sellList = new ArrayList<TradeItem>(items.length);
				for(ItemInstance item : items)
					if(item.canBeSold(activeChar))
						_sellList.add(new TradeItem(item, item.getTemplate().isBlocked(activeChar, item)));
			}
		}

		@Override
		protected void writeImpl()
		{
			writeD(0x01); // SELL/REFUND LIST TYPE
			writeD(0x00); //TODO [Bonux] Awakening
			writeH(_sellList.size());
			for(TradeItem item : _sellList)
			{
				writeItemInfo(item);
				if(Config.ALT_SELL_ITEM_ONE_ADENA)
					writeQ(1);
				else	
				{
					long price = item.getReferencePrice() > 2000000 ? 100000 : item.getReferencePrice()/20;
					writeQ(price / 2);
				}
			}
			writeH(_refundList.size());
			for(TradeItem item : _refundList)
			{
				writeItemInfo(item);
				writeD(item.getObjectId());
				if(Config.ALT_SELL_ITEM_ONE_ADENA)
					writeQ(item.getCount());
				else	
					writeQ(item.getCount() * item.getReferencePrice() / 2);
			}
			writeC(_done);
		}
	}
}