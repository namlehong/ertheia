package l2s.gameserver.network.l2.c2s;

import l2s.commons.dao.JdbcEntityState;
import l2s.commons.util.Rnd;
import l2s.gameserver.data.xml.holder.AttributeStoneHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Element;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.items.PcInventory;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ActionFailPacket;
import l2s.gameserver.network.l2.s2c.ExAttributeEnchantResultPacket;
import l2s.gameserver.network.l2.s2c.InventoryUpdatePacket;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.templates.item.support.AttributeStone;

/**
 * @author SYS
 * Format: d
 */
public class RequestEnchantItemAttribute extends L2GameClientPacket
{
	private int _objectId;
	private long _useStones;

	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_useStones = readQ();
	}

	@Override
	protected void runImpl()
	{
		final Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(_objectId == -1)
		{
			activeChar.setEnchantScroll(null);
			activeChar.sendPacket(SystemMsg.ATTRIBUTE_ITEM_USAGE_HAS_BEEN_CANCELLED);
			return;
		}

		if(activeChar.isActionsDisabled())
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.isInStoreMode())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_ADD_ELEMENTAL_POWER_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP, ActionFailPacket.STATIC);
			return;
		}

		if(activeChar.isInTrade())
		{
			activeChar.sendActionFailed();
			return;
		}

		final PcInventory inventory = activeChar.getInventory();
		final ItemInstance itemToEnchant = inventory.getItemByObjectId(_objectId);

		ItemInstance stone = activeChar.getEnchantScroll();

		activeChar.setEnchantScroll(null);

		if(itemToEnchant == null || stone == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		if(stone.getCount() < _useStones)
		{
			activeChar.sendActionFailed();
			return;
		}

		if(!itemToEnchant.canBeAttributed())
		{
			activeChar.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS, ActionFailPacket.STATIC);
			return;
		}

		if(itemToEnchant.getLocation() != ItemInstance.ItemLocation.INVENTORY && itemToEnchant.getLocation() != ItemInstance.ItemLocation.PAPERDOLL)
		{
			activeChar.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS, ActionFailPacket.STATIC);
			return;
		}

		if(itemToEnchant.isStackable() || (stone = inventory.getItemByObjectId(stone.getObjectId())) == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		final AttributeStone attrStone = AttributeStoneHolder.getInstance().getAttributeStone(stone.getItemId());
		if(attrStone == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		if(attrStone.getItemType() != null && attrStone.getItemType() != itemToEnchant.getTemplate().getQuality())
		{
			activeChar.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS, ActionFailPacket.STATIC);
			return;
		}

		final Element element = attrStone.getElement(itemToEnchant.isWeapon());
		if(itemToEnchant.isArmor())
		{
			if(itemToEnchant.getAttributeElementValue(Element.getReverseElement(element), false) != 0)
			{
				activeChar.sendPacket(SystemMsg.ANOTHER_ELEMENTAL_POWER_HAS_ALREADY_BEEN_ADDED, ActionFailPacket.STATIC);
				return;
			}
		}
		else if(itemToEnchant.isWeapon())
		{
			if(itemToEnchant.getAttributeElement() != Element.NONE && itemToEnchant.getAttributeElement() != element)
			{
				activeChar.sendPacket(SystemMsg.ANOTHER_ELEMENTAL_POWER_HAS_ALREADY_BEEN_ADDED, ActionFailPacket.STATIC);
				return;
			}
		}
		else
		{
			activeChar.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS, ActionFailPacket.STATIC);
			return;
		}

		// Запрет на заточку чужих вещей, баг может вылезти на серверных лагах
		if(itemToEnchant.getOwnerId() != activeChar.getObjectId())
		{
			activeChar.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS, ActionFailPacket.STATIC);
			return;
		}

		final int maxValue = attrStone.getMaxEnchant(itemToEnchant.isWeapon());
		if(itemToEnchant.getAttributeElementValue(element, false) >= maxValue)
		{
			activeChar.sendPacket(SystemMsg.ATTRIBUTE_ITEM_USAGE_HAS_BEEN_CANCELLED, ActionFailPacket.STATIC);
			return;
		}

		final int oldValue = itemToEnchant.getAttributeElementValue(element, true);

		int successCount = 0;
		int failCount = 0;
		for(int i = 0; i < _useStones; i++)
		{
			if(itemToEnchant.getAttributeElementValue(element, false) >= maxValue)
				break;

			if(!inventory.destroyItem(stone, 1L))
				break;

			if(Rnd.chance(attrStone.getChance()))
			{
				int value = attrStone.getEnchantPower(itemToEnchant.isWeapon());
				if(itemToEnchant.getAttributeElementValue(element, false) == 0)
					value = attrStone.getFirstEnchantPower(itemToEnchant.isWeapon());

				final int newElementValue = Math.min(itemToEnchant.getAttributeElementValue(element, false) + value, maxValue);
				itemToEnchant.setAttributeElement(element, newElementValue);

				successCount++;
			}
			else
				failCount++;
		}

		if(successCount > 0)
		{
			if(itemToEnchant.getEnchantLevel() == 0)
			{
				SystemMessage sm = new SystemMessage(SystemMessage.S2_ELEMENTAL_POWER_HAS_BEEN_ADDED_SUCCESSFULLY_TO_S1);
				sm.addItemName(itemToEnchant.getItemId());
				sm.addItemName(stone.getItemId());
				activeChar.sendPacket(sm);
			}
			else
			{
				SystemMessage sm = new SystemMessage(SystemMessage.S3_ELEMENTAL_POWER_HAS_BEEN_ADDED_SUCCESSFULLY_TO__S1S2);
				sm.addNumber(itemToEnchant.getEnchantLevel());
				sm.addItemName(itemToEnchant.getItemId());
				sm.addItemName(stone.getItemId());
				activeChar.sendPacket(sm);
			}

			boolean equipped = false;
			if(equipped = itemToEnchant.isEquipped())
			{
				activeChar.getInventory().isRefresh = true;
				activeChar.getInventory().unEquipItem(itemToEnchant);
			}

			itemToEnchant.setJdbcState(JdbcEntityState.UPDATED);
			itemToEnchant.update();

			if(equipped)
			{
				activeChar.getInventory().equipItem(itemToEnchant);
				activeChar.getInventory().isRefresh = false;
			}

			activeChar.sendPacket(new InventoryUpdatePacket().addModifiedItem(activeChar, itemToEnchant));
		}
		else
			activeChar.sendPacket(SystemMsg.YOU_HAVE_FAILED_TO_ADD_ELEMENTAL_POWER);

		activeChar.sendPacket(new ExAttributeEnchantResultPacket(itemToEnchant.isWeapon(), element, oldValue, itemToEnchant.getAttributeElementValue(element, true), successCount, failCount));
		activeChar.sendPacket(SystemMessagePacket.removeItems(stone.getItemId(), successCount + failCount));

		activeChar.setEnchantScroll(null);
		activeChar.updateStats();
	}
}