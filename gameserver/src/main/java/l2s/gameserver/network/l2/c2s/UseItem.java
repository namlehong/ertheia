package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.Config;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;

public class UseItem extends L2GameClientPacket
{
	private int _objectId;
	private boolean _ctrlPressed;

	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_ctrlPressed = readD() == 1;
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		activeChar.setActive();

		ItemInstance item = activeChar.getInventory().getItemByObjectId(_objectId);
		if(item == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		int itemId = item.getItemId();

		if(activeChar.isInStoreMode())
		{
			activeChar.sendPacket(SystemMsg.YOU_MAY_NOT_USE_ITEMS_IN_A_PRIVATE_STORE_OR_PRIVATE_WORK_SHOP);
			return;
		}

		if(activeChar.isFishing() && (itemId < 6535 || itemId > 6540))
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING_2);
			return;
		}

		if(activeChar.isSharedGroupDisabled(item.getTemplate().getReuseGroup()))
		{
			activeChar.sendReuseMessage(item);
			return;
		}

		if(!item.isEquipped() && !item.getTemplate().testCondition(activeChar, item))
			return;

		if(activeChar.getInventory().isLockedItem(item))
			return;

		if(item.getTemplate().isForPet())
		{
			activeChar.sendPacket(SystemMsg.YOU_MAY_NOT_EQUIP_A_PET_ITEM);
			return;
		}

		// Маги не могут вызывать Baby Buffalo Improved
		if(Config.ALT_IMPROVED_PETS_LIMITED_USE && activeChar.isMageClass() && item.getItemId() == 10311)
		{
			activeChar.sendPacket(new SystemMessagePacket(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemId));
			return;
		}

		// Войны не могут вызывать Improved Baby Kookaburra
		if(Config.ALT_IMPROVED_PETS_LIMITED_USE && !activeChar.isMageClass() && item.getItemId() == 10313)
		{
			activeChar.sendPacket(new SystemMessagePacket(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemId));
			return;
		}

		if(activeChar.isOutOfControl())
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.isStunned() || activeChar.isDecontrolled() || activeChar.isSleeping() || activeChar.isAfraid() || activeChar.isAlikeDead()) //to add more conds
		{
			activeChar.sendActionFailed();
			return;
		}

		activeChar.useItem(item, _ctrlPressed);
	}
}