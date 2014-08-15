package handler.items;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInfo;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExChangeAttributeItemList;
import l2s.gameserver.templates.item.ItemGrade;

/**
 * @author Bonux
 */
public class ChangeAttributeCrystal extends ScriptItemHandler
{
	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return false;

		Player player = (Player) playable;
		if(player.getPrivateStoreType() != Player.STORE_PRIVATE_NONE)
		{
			player.sendPacket(SystemMsg.YOU_CAN_NOT_CHANGE_THE_ATTRIBUTE_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
			return false;
		}

		switch(item.getItemId())
		{
			case 33502: // Кристалл Изменения Стихий - Оружие (S) 
			case 33833: // Кристалл Изменения Стихий - Оружие (S) Event
				sendAttributeItemList(item.getItemId(), player, ItemGrade.S, ItemGrade.S);
				break;
			case 33834: // Кристалл Смены Стихии - S80	Ивент	u,Позволяет сменить стихию оружия ранга S80.
				sendAttributeItemList(item.getItemId(), player, ItemGrade.S80);
				break;
			case 33835: // Кристалл Смены Стихии - R	Ивент	u,Позволяет сменить стихию оружия ранга R.
				sendAttributeItemList(item.getItemId(), player, ItemGrade.R);
				break;
			case 33836: // Кристалл Смены Стихии - R95	Ивент	u,Позволяет сменить стихию оружия ранга R95.
				sendAttributeItemList(item.getItemId(), player, ItemGrade.R95);
				break;
			case 33837: // Кристалл Смены Стихии - R99	Ивент	u,Позволяет сменить стихию оружия ранга R99.
				sendAttributeItemList(item.getItemId(), player, ItemGrade.R99);
				break;
		}
		return true;
	}

	private boolean sendAttributeItemList(int itemId, Player player, ItemGrade... grades)
	{
		List<ItemInfo> itemsList = new ArrayList<ItemInfo>();
		ItemInstance[] items = player.getInventory().getItems();
		for(ItemInstance item : items)
		{
			if(item.isWeapon() && item.getAttackElementValue() > 0)
			{
				if(ArrayUtils.contains(grades, item.getGrade()))
					itemsList.add(new ItemInfo(item));
			}
		}
		if(itemsList.size() == 0)
		{
			player.sendPacket(SystemMsg.THE_ITEM_FOR_CHANGING_AN_ATTRIBUTE_DOES_NOT_EXIST);
			return false;
		}
		player.sendPacket(new ExChangeAttributeItemList(itemId, itemsList.toArray(new ItemInfo[itemsList.size()])));
		return true;
	}
}