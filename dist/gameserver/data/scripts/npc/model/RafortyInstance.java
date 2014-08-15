package npc.model;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;

/**
 * @author B0nux
 */
public class RafortyInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	private static final int FREYA_NECKLACE = 16025;
	private static final int BLESSED_FREYA_NECKLACE = 16026;
	private static final int BOTTLE_OF_FREYAS_SOUL = 16027;
	//
	private static final int ANTHARAS_EARING = 6656;
	private static final int BLESSED_ANTHARAS_EARING = 19463;
	private static final int BOTTLE_OF_ANTHARAS_SOUL = 19465;

	//
	private static final int VALAKAS_NECKLACE = 6657;
	private static final int BLESSED_VALAKAS_NECKLACE = 19464;
	private static final int BOTTLE_OF_VALAKAS_SOUL = 19466;

	public RafortyInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equalsIgnoreCase("exchange_necklace_1"))
		{
			if(ItemFunctions.getItemCount(player, FREYA_NECKLACE) > 0)
				showChatWindow(player, "default/" + getNpcId() + "-ex4.htm");
			else if(ItemFunctions.getItemCount(player, ANTHARAS_EARING) > 0)
				showChatWindow(player, "default/" + getNpcId() + "-ex20.htm");
			else if(ItemFunctions.getItemCount(player, VALAKAS_NECKLACE) > 0)
				showChatWindow(player, "default/" + getNpcId() + "-ex30.htm");
			else
				showChatWindow(player, "default/" + getNpcId() + "-ex6.htm");
		}
		else if(command.equalsIgnoreCase("exchange_necklace_2"))
		{
			if(ItemFunctions.getItemCount(player, BOTTLE_OF_FREYAS_SOUL) > 0)
				showChatWindow(player, "default/" + getNpcId() + "-ex8.htm");
			else
				showChatWindow(player, "default/" + getNpcId() + "-ex7.htm");
		}
		else if(command.equalsIgnoreCase("exchange_necklace_3"))
		{
			if(ItemFunctions.getItemCount(player, FREYA_NECKLACE) > 0 && ItemFunctions.getItemCount(player, BOTTLE_OF_FREYAS_SOUL) > 0)
			{
				exchange(player, FREYA_NECKLACE, BLESSED_FREYA_NECKLACE);
				player.getInventory().destroyItemByItemId(BOTTLE_OF_FREYAS_SOUL, 1L);
				//ItemFunctions.removeItem(player, BOTTLE_OF_FREYAS_SOUL, 1, true);
				showChatWindow(player, "default/" + getNpcId() + "-ex9.htm");
			}
			else
				showChatWindow(player, "default/" + getNpcId() + "-ex11.htm");
		}
		else if(command.equalsIgnoreCase("exchange_necklace_antharas_2"))
		{
			if(ItemFunctions.getItemCount(player, BOTTLE_OF_ANTHARAS_SOUL) > 0)
				showChatWindow(player, "default/" + getNpcId() + "-ex21.htm");
			else
				showChatWindow(player, "default/" + getNpcId() + "-ex7.htm");
		}
		else if(command.equalsIgnoreCase("exchange_necklace_antharas_3"))
		{
			if(ItemFunctions.getItemCount(player, ANTHARAS_EARING) > 0 && ItemFunctions.getItemCount(player, BOTTLE_OF_ANTHARAS_SOUL) > 0)
			{
				exchange(player, ANTHARAS_EARING, BLESSED_ANTHARAS_EARING);
				player.getInventory().destroyItemByItemId(BOTTLE_OF_ANTHARAS_SOUL, 1L);
				//ItemFunctions.removeItem(player, BOTTLE_OF_ANTHARAS_SOUL, 1, true);
				showChatWindow(player, "default/" + getNpcId() + "-ex9.htm");
			}
			else
				showChatWindow(player, "default/" + getNpcId() + "-ex11.htm");
		}
		else if(command.equalsIgnoreCase("exchange_necklace_valakas_2"))
		{
			if(ItemFunctions.getItemCount(player, BOTTLE_OF_VALAKAS_SOUL) > 0)
				showChatWindow(player, "default/" + getNpcId() + "-ex31.htm");
			else
				showChatWindow(player, "default/" + getNpcId() + "-ex7.htm");
		}
		else if(command.equalsIgnoreCase("exchange_necklace_valakas_3"))
		{
			if(ItemFunctions.getItemCount(player, VALAKAS_NECKLACE) > 0 && ItemFunctions.getItemCount(player, BOTTLE_OF_VALAKAS_SOUL) > 0)
			{
				exchange(player, VALAKAS_NECKLACE, BLESSED_VALAKAS_NECKLACE);
				player.getInventory().destroyItemByItemId(BOTTLE_OF_VALAKAS_SOUL, 1L);
				//ItemFunctions.removeItem(player, BOTTLE_OF_VALAKAS_SOUL, 1, true);
				showChatWindow(player, "default/" + getNpcId() + "-ex9.htm");
			}
			else
				showChatWindow(player, "default/" + getNpcId() + "-ex11.htm");
		}
		else
			super.onBypassFeedback(player, command);
	}

	private void exchange(Player player, int itemGet, int itemSet)
	{
		List<ItemInstance> list = player.getInventory().getItemsByItemId(itemGet);
		List<ItemData> items = new ArrayList<ItemData>();
		ItemInstance itemToTake = null;
		for(ItemInstance item : list)
			if(!items.contains(new ItemData(item.getItemId(), item.getCount(), item)) && (itemToTake == null || item.getEnchantLevel() < itemToTake.getEnchantLevel()) && ItemFunctions.checkIfCanDiscard(player, item))
			{
				itemToTake = item;
				if(itemToTake.getEnchantLevel() == 0)
					break;
			}

		if(itemToTake == null)
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
			return;
		}
		items.add(new ItemData(itemToTake.getItemId(), 1, itemToTake));
		int enchantLevel = 0;
		for(ItemData id : items)
		{
			long count = id.getCount();
			if(count > 0)
				if(player.getInventory().destroyItem(id.getItem(), count))
				{
					if(id.getItem().getEnchantLevel() > 0)
						enchantLevel = id.getItem().getEnchantLevel();
					player.sendPacket(SystemMessagePacket.removeItems(id.getId(), count));
					continue;
				}
			return;
		}
		ItemInstance product = ItemFunctions.createItem(itemSet);
		product.setEnchantLevel(enchantLevel);
		player.sendPacket(SystemMessagePacket.obtainItems(product));
		player.getInventory().addItem(product);
	}

	private class ItemData
	{
		private final int _id;
		private final long _count;
		private final ItemInstance _item;

		public ItemData(int id, long count, ItemInstance item)
		{
			_id = id;
			_count = count;
			_item = item;
		}

		public int getId()
		{
			return _id;
		}

		public long getCount()
		{
			return _count;
		}

		public ItemInstance getItem()
		{
			return _item;
		}

		@Override
		public boolean equals(Object obj)
		{
			if(!(obj instanceof ItemData))
				return false;

			ItemData i = (ItemData) obj;

			return _id == i._id && _count == i._count && _item == i._item;
		}
	}
}