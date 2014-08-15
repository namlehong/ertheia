package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInfo;
import l2s.gameserver.model.items.ItemInstance;

/**
 * @author Bonux
 */
public class ExResponseCommissionItemList extends L2GameServerPacket
{
	private List<ItemInfo> _itemsList = new ArrayList<ItemInfo>();

	public ExResponseCommissionItemList(Player player)
	{
		ItemInstance[] items = player.getInventory().getItems();
		for(ItemInstance item : items)
		{
			if(item.canBeComissioned(player))
				_itemsList.add(new ItemInfo(item, item.getTemplate().isBlocked(player, item)));
		}
	}

	protected void writeImpl()
	{
		writeD(_itemsList.size()); //size
		for(ItemInfo item : _itemsList)
		{
			writeItemInfo(item);
		}
	}
}