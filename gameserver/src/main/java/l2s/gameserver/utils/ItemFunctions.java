package l2s.gameserver.utils;

import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.data.xml.holder.PetDataHolder;
import l2s.gameserver.idfactory.IdFactory;
import l2s.gameserver.instancemanager.CursedWeaponsManager;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.ClassType;
import l2s.gameserver.model.base.PledgeRank;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.entity.olympiad.Olympiad;
import l2s.gameserver.model.instances.PetInstance;
import l2s.gameserver.model.items.Inventory;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.items.ItemInstance.ItemLocation;
import l2s.gameserver.model.items.attachment.PickableAttachment;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.network.l2.components.IStaticPacket;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.templates.item.ArmorTemplate.ArmorType;
import l2s.gameserver.templates.item.ItemGrade;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.item.WeaponTemplate.WeaponType;

public final class ItemFunctions
{
	private ItemFunctions()
	{}

	public static ItemInstance createItem(int itemId)
	{
		ItemInstance item = new ItemInstance(IdFactory.getInstance().getNextId(), itemId);
		item.setLocation(ItemLocation.VOID);
		item.setCount(1L);

		return item;
	}

	/**
	 * Добавляет предмет в инвентарь игрока, корректно обрабатывает нестыкуемые вещи
	 *
	 * @param playable Владелец инвентаря
	 * @param itemId   ID предмета
	 * @param count	количество
	 */
	public static void addItem(Playable playable, int itemId, long count, boolean notify)
	{
		if(playable == null || count < 1)
			return;

		Playable player;
		if(playable.isSummon())
			player = playable.getPlayer();
		else
			player = playable;

		if(itemId > 0)
		{
			ItemTemplate t = ItemHolder.getInstance().getTemplate(itemId);
			if(t.isStackable())
				player.getInventory().addItem(itemId, count);
			else
				for(long i = 0; i < count; i++)
					player.getInventory().addItem(itemId, 1);

			if(notify)
				player.sendPacket(SystemMessagePacket.obtainItems(itemId, count, 0));
		}
		else if(itemId == -100 && player.isPlayer())
		{
			player.getPlayer().addPcBangPoints((int) count, false);
		}
		else if(itemId == -200 && player.isPlayer())
		{
			if(player.getPlayer().getClan() != null)
				player.getPlayer().getClan().incReputation((int) count, false, "itemFunction");
		}
		else if(itemId == -300 && player.isPlayer())
		{
			player.getPlayer().setFame((int) count + player.getPlayer().getFame(), "itemFunction");
		}
	}

	/**
	 * Возвращает количество предметов в инвентаре игрока
	 *
	 * @param playable Владелец инвентаря
	 * @param itemId   ID предмета
	 * @return количество
	 */
	public static long getItemCount(Playable playable, int itemId)
	{
		if(playable == null)
			return 0;
		Playable player = playable.getPlayer();
		return player.getInventory().getCountOf(itemId);
	}

	/**
	 * Удаляет предметы из инвентаря игрока, корректно обрабатывает нестыкуемые предметы
	 *
	 * @param playable Владелец инвентаря
	 * @param itemId   ID предмета
	 * @param count	количество
	 * @return количество удаленных
	 */
	public static long removeItem(Playable playable, int itemId, long count, boolean notify)
	{
		long removed = 0;
		if(playable == null || count < 1)
			return removed;

		Playable player = playable.getPlayer();

		ItemTemplate t = ItemHolder.getInstance().getTemplate(itemId);
		if(t.isStackable())
		{
			if(player.getInventory().destroyItemByItemId(itemId, count))
				removed = count;
		}
		else
			for(long i = 0; i < count; i++)
				if(player.getInventory().destroyItemByItemId(itemId, 1))
					removed++;

		if(removed > 0 && notify)
			player.sendPacket(SystemMessagePacket.removeItems(itemId, removed));

		return removed;
	}

	public static long removeItem(Playable playable, ItemInstance item, long count, boolean notify)
	{
		long removed = 0;
		if(playable == null || count < 1)
			return removed;

		Playable player = playable.getPlayer();
		if(item.getTemplate().isStackable())
		{
			if(player.getInventory().destroyItem(item, count))
				removed = count;
		}
		else
			for(long i = 0; i < count; i++)
				if(player.getInventory().destroyItem(item, 1))
					removed++;

		if(removed > 0 && notify)
			player.sendPacket(SystemMessagePacket.removeItems(item.getItemId(), removed));

		return removed;
	}

	public final static boolean isClanApellaItem(int itemId)
	{
		return itemId >= 7860 && itemId <= 7879 || itemId >= 9830 && itemId <= 9839;
	}

	public final static IStaticPacket checkIfCanEquip(PetInstance pet, ItemInstance item)
	{
		if(!item.isEquipable())
			return SystemMsg.YOUR_PET_CANNOT_CARRY_THIS_ITEM;

		int petId = pet.getNpcId();

		if(item.getTemplate().isPendant() //
				|| PetDataHolder.isWolf(petId) && item.getTemplate().isForWolf() //
				|| PetDataHolder.isHatchling(petId) && item.getTemplate().isForHatchling() //
				|| PetDataHolder.isStrider(petId) && item.getTemplate().isForStrider() //
				|| PetDataHolder.isGreatWolf(petId) && item.getTemplate().isForGWolf() //
				|| PetDataHolder.isBabyPet(petId) && item.getTemplate().isForPetBaby() //
				|| PetDataHolder.isImprovedBabyPet(petId) && item.getTemplate().isForPetBaby() //
		)
			return null;

		return SystemMsg.YOUR_PET_CANNOT_CARRY_THIS_ITEM;
	}

	/**
	 * Проверяет возможность носить эту вещь.
	 *
	 * @return null, если вещь носить можно, либо SystemMessage, который можно показать игроку
	 */
	public final static IStaticPacket checkIfCanEquip(Player player, ItemInstance item)
	{
		//FIXME [G1ta0] черезмерный хардкод, переделать на условия
		int itemId = item.getItemId();
		int targetSlot = item.getTemplate().getBodyPart();
		Clan clan = player.getClan();

		//TODO: [Bonux] проверить, могут ли носить Камаэли щиты и сигили.
		if(!player.isAwaked() && item.getGrade() != ItemGrade.R && item.getGrade() != ItemGrade.R95 && item.getGrade() != ItemGrade.R99)
		{
			// не камаэли и рапиры/арбалеты/древние мечи
			if(player.getRace() != Race.KAMAEL && (item.getItemType() == WeaponType.CROSSBOW || item.getItemType() == WeaponType.RAPIER || item.getItemType() == WeaponType.ANCIENTSWORD))
				return SystemMsg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM;
		}

		// Маги Артеас не могут носить Сигили и Щиты.
		if(player.getClassId().getRace() == Race.ERTHEIA && player.getClassId().isOfType(ClassType.MYSTIC) && (item.getItemType() == ArmorType.SIGIL || item.getItemType() == WeaponType.NONE))
			return SystemMsg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM;

		if(itemId >= 7850 && itemId <= 7859 && player.getLvlJoinedAcademy() == 0) // Clan Oath Armor
			return SystemMsg.THIS_ITEM_CAN_ONLY_BE_WORN_BY_A_MEMBER_OF_THE_CLAN_ACADEMY;

		if(isClanApellaItem(itemId) && player.getPledgeRank().ordinal() < PledgeRank.WISEMAN.ordinal())
			return SystemMsg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM;

		if(item.getItemType() == WeaponType.DUALDAGGER && player.getSkillLevel(923) < 1 && player.getSkillLevel(10502) < 1)
			return SystemMsg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM;

		// Корона лидера клана, владеющего замком
		if(itemId == 6841 && (clan == null || !player.isClanLeader() || clan.getCastle() == 0))
			return SystemMsg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM;

		// Нельзя одевать оружие, если уже одето проклятое оружие. Проверка двумя способами, для надежности.
		if(targetSlot == ItemTemplate.SLOT_LR_HAND || targetSlot == ItemTemplate.SLOT_L_HAND || targetSlot == ItemTemplate.SLOT_R_HAND)
		{
			if(itemId != player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_RHAND) && CursedWeaponsManager.getInstance().isCursed(player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_RHAND)))
				return SystemMsg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM;
			if(player.isCursedWeaponEquipped() && itemId != player.getCursedWeaponEquippedId())
				return SystemMsg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM;
		}

		if(targetSlot == ItemTemplate.SLOT_DECO)
		{
			int count = player.getTalismanCount();
			if(count <= 0)
				return new SystemMessagePacket(SystemMsg.YOU_CANNOT_WEAR_S1_BECAUSE_YOU_ARE_NOT_WEARING_A_BRACELET).addItemName(itemId);

			ItemInstance deco;
			for(int slot = Inventory.PAPERDOLL_DECO1; slot <= Inventory.PAPERDOLL_DECO6; slot++)
			{
				deco = player.getInventory().getPaperdollItem(slot);
				if(deco != null)
				{
					if(deco == item)
						return null; // талисман уже одет и количество слотов больше нуля
					// Проверяем на количество слотов и одинаковые талисманы
					if(--count <= 0 || deco.getItemId() == itemId)
						return new SystemMessagePacket(SystemMsg.YOU_CANNOT_EQUIP_S1_BECAUSE_YOU_DO_NOT_HAVE_ANY_AVAILABLE_SLOTS).addItemName(itemId);
				}
			}
		}

		if(targetSlot == ItemTemplate.SLOT_JEWEL)
		{
			int count = player.getJewelsLimit();
			if(count <= 0)
				return new SystemMessagePacket(SystemMsg.YOU_CANNOT_EQUIP_S1_BECAUSE_YOU_DO_NOT_HAVE_ANY_AVAILABLE_SLOTS).addItemName(itemId); // TODO: Найти правильное сообщение.

			ItemInstance jewel;
			for(int slot = Inventory.PAPERDOLL_JEWEL1; slot <= Inventory.PAPERDOLL_JEWEL6; slot++)
			{
				jewel = player.getInventory().getPaperdollItem(slot);
				if(jewel != null)
				{
					if(jewel == item)
						return null; // камень уже одет и количество слотов больше нуля
					// Проверяем на количество слотов и одинаковые камни
					if(--count <= 0 || jewel.getItemId() == itemId)
						return new SystemMessagePacket(SystemMsg.YOU_CANNOT_EQUIP_S1_BECAUSE_YOU_DO_NOT_HAVE_ANY_AVAILABLE_SLOTS).addItemName(itemId); // TODO: Найти правильное сообщение.
				}
			}
		}
		return null;
	}

	public static boolean checkIfCanPickup(Playable playable, ItemInstance item)
	{
		Player player = playable.getPlayer();
		return item.getDropTimeOwner() <= System.currentTimeMillis() || item.getDropPlayers().contains(player.getObjectId());
	}

	public static boolean canAddItem(Player player, ItemInstance item)
	{
		if(!player.getInventory().validateWeight(item))
		{
			player.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
			return false;
		}

		if(!player.getInventory().validateCapacity(item))
		{
			player.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
			return false;
		}

		if(!item.getTemplate().getHandler().pickupItem(player, item))
			return false;

		PickableAttachment attachment = item.getAttachment() instanceof PickableAttachment ? (PickableAttachment) item.getAttachment() : null;
		if(attachment != null && !attachment.canPickUp(player))
			return false;

		return true;
	}

	/**
	 * Проверяет возможность передачи вещи
	 *
	 * @param player
	 * @param item
	 * @return
	 */
	public final static boolean checkIfCanDiscard(Player player, ItemInstance item)
	{
		if(item.isHeroItem())
			return false;

		if(player.getMountControlItemObjId() == item.getObjectId())
			return false;

		if(player.getPetControlItem() == item)
			return false;

		if(player.getEnchantScroll() == item)
			return false;

		if(item.isCursed())
			return false;

		if(item.getTemplate().isQuest())
			return false;

		return true;
	}

	public static final int[][] catalyst = {
			// enchant catalyst list
			{ 12362, 14078, 14702 }, // 0 - W D
			{ 12363, 14079, 14703 }, // 1 - W C
			{ 12364, 14080, 14704 }, // 2 - W B
			{ 12365, 14081, 14705 }, // 3 - W A
			{ 12366, 14082, 14706 }, // 4 - W S
			{ 30381 }, // 5 - W R
			{ 12367, 14083, 14707 }, // 6 - A D
			{ 12368, 14084, 14708 }, // 7 - A C
			{ 12369, 14085, 14709 }, // 8 - A B
			{ 12370, 14086, 14710 }, // 9 - A A
			{ 12371, 14087, 14711 }, // 10 - A S
			{ 30382 } // 11 - A R
	};

	public final static int[] getEnchantCatalystId(ItemInstance item)
	{
		if(item.getTemplate().isWeapon())
			switch(item.getGrade().getCrystalId())
			{
				case ItemTemplate.CRYSTAL_A:
					return catalyst[3];
				case ItemTemplate.CRYSTAL_B:
					return catalyst[2];
				case ItemTemplate.CRYSTAL_C:
					return catalyst[1];
				case ItemTemplate.CRYSTAL_D:
					return catalyst[0];
				case ItemTemplate.CRYSTAL_S:
					return catalyst[4];
				case ItemTemplate.CRYSTAL_R:
					return catalyst[5];
			}
		else if(item.getTemplate().isArmor() || item.getTemplate().isAccessory())
			switch(item.getGrade().getCrystalId())
			{
				case ItemTemplate.CRYSTAL_A:
					return catalyst[9];
				case ItemTemplate.CRYSTAL_B:
					return catalyst[8];
				case ItemTemplate.CRYSTAL_C:
					return catalyst[7];
				case ItemTemplate.CRYSTAL_D:
					return catalyst[6];
				case ItemTemplate.CRYSTAL_S:
					return catalyst[10];
				case ItemTemplate.CRYSTAL_R:
					return catalyst[11];
			}
		return new int[] { 0 };
	}

	public final static int getCatalystPower(int itemId)
	{
		/*
		14702	Agathion Auxiliary Stone: Enchant Weapon (D-Grade)	The Agathion Auxilary Stone raises the ability to enchant a D-Grade weapon by 20%
		14703	Agathion Auxiliary Stone: Enchant Weapon (C-Grade)	The Agathion Auxilary Stone raises the ability to enchant a C-Grade weapon by 18%
		14704	Agathion Auxiliary Stone: Enchant Weapon (B-Grade)	The Agathion Auxilary Stone raises the ability to enchant a B-Grade weapon by 15%
		14705	Agathion Auxiliary Stone: Enchant Weapon (A-Grade)	The Agathion Auxilary Stone raises the ability to enchant a A-Grade weapon by 12%
		14706	Agathion Auxiliary Stone: Enchant Weapon (S-Grade)	The Agathion Auxilary Stone raises the ability to enchant a S-Grade weapon by 10%
		14707	Agathion Auxiliary Stone: Enchant Armor (D-Grade)		The Agathion Auxilary Stone raises the ability to enchant a D-Grade armor by 35%
		14708	Agathion Auxiliary Stone: Enchant Armor (C-Grade)		The Agathion Auxilary Stone raises the ability to enchant a C-Grade armor by 27%
		14709	Agathion Auxiliary Stone: Enchant Armor (B-Grade)		The Agathion Auxilary Stone raises the ability to enchant a B-Grade armor by 23%
		14710	Agathion Auxiliary Stone: Enchant Armor (A-Grade)		The Agathion Auxilary Stone raises the ability to enchant a A-Grade armor by 18%
		14711	Agathion Auxiliary Stone: Enchant Armor (S-Grade)		The Agathion Auxilary Stone raises the ability to enchant a S-Grade armor by 15%
		 */
		for(int i = 0; i < catalyst.length; i++)
			for(int id : catalyst[i])
				if(id == itemId)
					switch(i)
					{
						case 0:
							return 20;
						case 1:
							return 18;
						case 2:
							return 15;
						case 3:
							return 12;
						case 4:
						case 5:
							return 10;
						case 6:
							return 35;
						case 7:
							return 27;
						case 8:
							return 23;
						case 9:
							return 18;
						case 10:
						case 11:
							return 15;
					}

		return 0;
		/*
		switch(_itemId)
		{
			case 14702:
			case 14078:
			case 12362:
				return 20;
			case 14703:
			case 14079:
			case 12363:
				return 18;
			case 14704:
			case 14080:
			case 12364:
				return 15;
			case 14705:
			case 14081:
			case 12365:
				return 12;
			case 14706:
			case 14082:
			case 12366:
				return 10;
			case 14707:
			case 14083:
			case 12367:
				return 35;
			case 14708:
			case 14084:
			case 12368:
				return 27;
			case 14709:
			case 14085:
			case 12369:
				return 23;
			case 14710:
			case 14086:
			case 12370:
				return 18;
			case 14711:
			case 14087:
			case 12371:
				return 15;
			default:
				return 0;
		}
		 */
	}

	/**
	 * Проверяет соответствие уровня заточки и вообще катализатор ли это или левый итем
	 *
	 * @param item
	 * @param catalyst
	 * @return true если катализатор соответствует
	 */
	public static final boolean checkCatalyst(ItemInstance item, ItemInstance catalyst)
	{
		if(item == null || catalyst == null)
			return false;

		int current = item.getEnchantLevel();
		if(current < (item.getTemplate().getBodyPart() == ItemTemplate.SLOT_FULL_ARMOR ? 4 : 3) || current > 9)
			return false;

		for(int catalystRequired : getEnchantCatalystId(item))
			if(catalystRequired == catalyst.getItemId())
				return true;

		return false;
	}

	public static int getCrystallizeCrystalAdd(ItemInstance item)
	{
		int result = 0;
		int crystalsAdd = 0;
		if(item.isWeapon())
		{
			switch(item.getGrade())
			{
				case D:
					crystalsAdd = 90;
					break;
				case C:
					crystalsAdd = 45;
					break;
				case B:
					crystalsAdd = 67;
					break;
				case A:
					crystalsAdd = 145;
					break;
				case S:
				case S80:
				case S84:
					crystalsAdd = 250;
					break;
				case R:
				case R95:
				case R99:
					crystalsAdd = 500;
					break;
			}
		}
		else
		{
			switch(item.getGrade())
			{
				case D:
					crystalsAdd = 11;
					break;
				case C:
					crystalsAdd = 6;
					break;
				case B:
					crystalsAdd = 11;
					break;
				case A:
					crystalsAdd = 20;
					break;
				case S:
				case S80:
				case S84:
					crystalsAdd = 25;
					break;
				case R:
				case R95:
				case R99:
					crystalsAdd = 30;
					break;
			}
		}

		if(item.getEnchantLevel() > 3)
		{
			result = crystalsAdd * 3;
			if(item.isWeapon())
				crystalsAdd *= 2;
			else
				crystalsAdd *= 3;
			result += crystalsAdd * (item.getEnchantLevel() - 3);
		}
		else
			result = crystalsAdd * item.getEnchantLevel();

		return result;
	}
}
