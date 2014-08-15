package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2s.commons.lang.ArrayUtils;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInfo;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.items.Warehouse.ItemClassComparator;
import l2s.gameserver.model.items.Warehouse.WarehouseType;

public class WareHouseWithdrawListPacket extends L2GameServerPacket
{
	private long _adena;
	private List<ItemInfo> _itemList = new ArrayList<ItemInfo>();
	private int _type;
	private int _inventoryUsedSlots;

	public WareHouseWithdrawListPacket(Player player, WarehouseType type)
	{
		_adena = player.getAdena();
		_type = type.ordinal();

		ItemInstance[] items;
		switch(type)
		{
			case PRIVATE:
				items = player.getWarehouse().getItems();
				break;
			case FREIGHT:
				items = player.getFreight().getItems();
				break;
			case CLAN:
			case CASTLE:
				items = player.getClan().getWarehouse().getItems();
				break;
			default:
				_itemList = Collections.emptyList();
				return;
		}

		_itemList = new ArrayList<ItemInfo>(items.length);
		ArrayUtils.eqSort(items, ItemClassComparator.getInstance());
		for(ItemInstance item : items)
			_itemList.add(new ItemInfo(item));

		_inventoryUsedSlots = player.getInventory().getSize();
	}

	@Override
	protected final void writeImpl()
	{
		writeH(_type);
		writeQ(_adena);
		writeH(_itemList.size());
		writeH(1); //TODO [Bonux]: Неизвестно. На оффе было 1.
		writeD(_itemList.get(0).getItemId()); //первый предмет в списке.
		writeD(_inventoryUsedSlots); //Количество занятых ячеек в инвентаре.
		for(ItemInfo item : _itemList)
		{
			writeItemInfo(item);
			writeD(item.getObjectId());
			writeD(0);
			writeD(0);
		}
	}
}