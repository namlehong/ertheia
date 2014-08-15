package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.PremiumItem;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExGetPremiumItemListPacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;

//FIXME [G1ta0] item-API
public final class RequestWithDrawPremiumItem extends L2GameClientPacket
{
	private int _index;
	private int _charId;
	private long _itemCount;

	@Override
	protected void readImpl()
	{
		_index = readD();
		_charId = readD();
		_itemCount = readQ();
	}

	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(_itemCount <= 0)
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.getObjectId() != _charId) // audit
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.getPremiumItemList().isEmpty())// audit
		{
			activeChar.sendActionFailed();
			return;
		}

		NpcInstance merchant = activeChar.getLastNpc();
		boolean isValidMerchant = merchant != null && merchant.isMerchantNpc();
		if(!activeChar.isGM() && (merchant == null || !isValidMerchant || !activeChar.isInRange(merchant, Creature.INTERACTION_DISTANCE)))
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.getWeightPenalty() >= 3 || activeChar.getInventoryLimit() * 0.8 <= activeChar.getInventory().getSize())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_RECEIVE_THE_VITAMIN_ITEM_BECAUSE_YOU_HAVE_EXCEED_YOUR_INVENTORY_WEIGHTQUANTITY_LIMIT);
			return;
		}

		if(activeChar.isProcessingRequest())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_RECEIVE_A_VITAMIN_ITEM_DURING_AN_EXCHANGE);
			return;
		}

		PremiumItem item = activeChar.getPremiumItemList().get(_index);
		if(item == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		if(!activeChar.getPremiumItemList().remove(item, _itemCount))
		{
			activeChar.sendActionFailed();
			return;
		}

		boolean stackable = ItemHolder.getInstance().getTemplate(item.getItemId()).isStackable();
		if(!stackable)
		{
			for(int i = 0; i < _itemCount; i++)
				addItem(activeChar, item.getItemId(), 1);
		}
		else
			addItem(activeChar, item.getItemId(), _itemCount);


		if(activeChar.getPremiumItemList().isEmpty())
			activeChar.sendPacket(SystemMsg.THERE_ARE_NO_MORE_VITAMIN_ITEMS_TO_BE_FOUND);
		else
			activeChar.sendPacket(new ExGetPremiumItemListPacket(activeChar));
	}

	private void addItem(Player player, int itemId, long count)
	{
		player.getInventory().addItem(itemId, count);
		player.sendPacket(SystemMessagePacket.obtainItems(itemId, count, 0));
	}
}