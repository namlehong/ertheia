package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.data.xml.holder.EnchantItemHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.items.PcInventory;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExPutEnchantTargetItemResult;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.item.support.EnchantScroll;
import l2s.gameserver.utils.Log;

public class RequestExTryToPutEnchantTargetItem extends L2GameClientPacket
{
	private int _objectId;

	@Override
	protected void readImpl()
	{
		_objectId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null)
			return;

		System.out.println("RequestExTryToPutEnchantTargetItem db 1");
		if(player.isActionsDisabled() || player.isInStoreMode() || player.isInTrade())
		{
			player.sendPacket(ExPutEnchantTargetItemResult.FAIL);
			player.setEnchantScroll(null);
			return;
		}
		System.out.println("RequestExTryToPutEnchantTargetItem db 2");
		PcInventory inventory = player.getInventory();
		ItemInstance itemToEnchant = inventory.getItemByObjectId(_objectId);
		ItemInstance scroll = player.getEnchantScroll();

		if(itemToEnchant == null || scroll == null)
		{
			player.sendPacket(ExPutEnchantTargetItemResult.FAIL);
			player.setEnchantScroll(null);
			return;
		}
		System.out.println("RequestExTryToPutEnchantTargetItem db 3");
		Log.add(player.getName() + "|Trying to put enchant|" + itemToEnchant.getItemId() + "|+" + itemToEnchant.getEnchantLevel() + "|" + itemToEnchant.getObjectId(), "enchants");

		int scrollId = scroll.getItemId();
		int itemId = itemToEnchant.getItemId();

		EnchantScroll enchantScroll = EnchantItemHolder.getInstance().getEnchantScroll(scrollId);
		System.out.println("RequestExTryToPutEnchantTargetItem db 4");
		if(!itemToEnchant.canBeEnchanted() || itemToEnchant.isStackable())
		{
			player.sendPacket(ExPutEnchantTargetItemResult.FAIL);
			player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
			player.setEnchantScroll(null);
			return;
		}
		System.out.println("RequestExTryToPutEnchantTargetItem db 5");
		if(itemToEnchant.getLocation() != ItemInstance.ItemLocation.INVENTORY && itemToEnchant.getLocation() != ItemInstance.ItemLocation.PAPERDOLL)
		{
			player.sendPacket(ExPutEnchantTargetItemResult.FAIL);
			player.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS);
			player.setEnchantScroll(null);
			return;
		}
		System.out.println("RequestExTryToPutEnchantTargetItem db 6");
		if(player.isInStoreMode())
		{
			player.sendPacket(ExPutEnchantTargetItemResult.FAIL);
			player.sendPacket(SystemMsg.YOU_CANNOT_ENCHANT_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
			player.setEnchantScroll(null);
			return;
		}
		System.out.println("RequestExTryToPutEnchantTargetItem db 7");
		if((scroll = inventory.getItemByObjectId(scroll.getObjectId())) == null)
		{
			player.sendPacket(ExPutEnchantTargetItemResult.FAIL);
			player.setEnchantScroll(null);
			return;
		}
		System.out.println("RequestExTryToPutEnchantTargetItem db 8");
		if(enchantScroll == null)
		{
			player.sendPacket(ExPutEnchantTargetItemResult.FAIL);
			player.setEnchantScroll(null);
			return;
		}
		System.out.println("RequestExTryToPutEnchantTargetItem db 9");
		if(enchantScroll.getItems().size() > 0)
		{
			if(!enchantScroll.getItems().contains(itemId))
			{
				System.out.println("RequestExTryToPutEnchantTargetItem db 10");
				player.sendPacket(ExPutEnchantTargetItemResult.FAIL);
				player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
				player.setEnchantScroll(null);
				return;
			}
		}
		else
		{
			if(enchantScroll.getGrade().extOrdinal() != itemToEnchant.getGrade().extOrdinal())
			{
				System.out.println("RequestExTryToPutEnchantTargetItem db 11");
				player.sendPacket(ExPutEnchantTargetItemResult.FAIL);
				player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
				player.sendActionFailed();
				return;
			}

			int itemType = itemToEnchant.getTemplate().getType2();
			switch(enchantScroll.getType())
			{
				case ARMOR:
					if(itemType == ItemTemplate.TYPE2_WEAPON)
					{
						System.out.println("RequestExTryToPutEnchantTargetItem db 12");
						player.sendPacket(ExPutEnchantTargetItemResult.FAIL);
						player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
						player.sendActionFailed();
						return;
					}
					break;
				case WEAPON:
					if(itemType == ItemTemplate.TYPE2_SHIELD_ARMOR || itemType == ItemTemplate.TYPE2_ACCESSORY)
					{
						System.out.println("RequestExTryToPutEnchantTargetItem db 13");
						player.sendPacket(ExPutEnchantTargetItemResult.FAIL);
						player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
						player.sendActionFailed();
						return;
					}
					break;
			}
		}

		if(enchantScroll.getMaxEnchant() != -1 && itemToEnchant.getEnchantLevel() >= enchantScroll.getMaxEnchant())
		{
			System.out.println("RequestExTryToPutEnchantTargetItem db 14");
			player.sendPacket(ExPutEnchantTargetItemResult.FAIL);
			player.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS);
			player.setEnchantScroll(null);
			return;
		}

		// Запрет на заточку чужих вещей, баг может вылезти на серверных лагах
		if(itemToEnchant.getOwnerId() != player.getObjectId())
		{
			System.out.println("RequestExTryToPutEnchantTargetItem db 15");
			player.sendPacket(ExPutEnchantTargetItemResult.FAIL);
			player.setEnchantScroll(null);
			return;
		}

		player.sendPacket(ExPutEnchantTargetItemResult.SUCCESS);
	}
}
