package handler.items;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;

public class NevitVoice extends SimpleItemHandler
{
	@Override
	protected boolean useItemImpl(Player player, ItemInstance item, boolean ctrl)
	{
		int itemId = item.getItemId();

		if(!reduceItem(player, item))
			return false;

		sendUseMessage(player, item);

		switch(itemId)
		{
			case 17094:
				player.addRecomHave(10);
				break;
			default:
				return false;
		}

		return true;
	}
}
