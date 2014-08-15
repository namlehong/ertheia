package l2s.gameserver.handler.items.impl;

import l2s.gameserver.data.xml.holder.AttributeStoneHolder;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExChooseInventoryAttributeItemPacket;
import l2s.gameserver.templates.item.support.AttributeStone;

/**
 * @author SYS
 */
public class AttributeStoneItemHandler extends DefaultItemHandler
{
	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return false;

		Player player = (Player) playable;
		if(player.getPrivateStoreType() != Player.STORE_PRIVATE_NONE)
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_ADD_ELEMENTAL_POWER_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
			return false;
		}

		if(player.getEnchantScroll() != null)
			return false;

		AttributeStone attrStone = AttributeStoneHolder.getInstance().getAttributeStone(item.getItemId());
		if(attrStone == null)
			return false;

		player.setEnchantScroll(item);
		player.sendPacket(new ExChooseInventoryAttributeItemPacket(player, attrStone, item.getCount()));
		return true;
	}
}