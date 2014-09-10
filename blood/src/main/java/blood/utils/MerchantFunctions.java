package blood.utils;

import l2s.commons.util.Rnd;

public class MerchantFunctions {
	public static String shortenItemName(String itemName) {
		String[][] itemNameList = { { "Destroyer Hammer", "Des Hammer" },
				{ "Dasparion's Staff", "Daspa Staff" },
				{ "Infernal Master", "Infe Master" },
				{ "Meteor Shower", "Meteor" }, { "Spiritual Eye", "SpirEye" },
				{ "White Lightning", "A rapier" },
				{ "Elemental Sword", "A Msword" },
				{ "Keshanberk*Keshanberk", "kes*kes" },
				{ "Carnage Bow", "carnage" },
				{ "Branch of the Mother Tree", "BoMT" },
				{ "Dragon Slayer", "DraSlayer" },
				{ "Flaming Dragon Skull", "FlameDraSkull" },
				{ "Dragon Grinder", "DraGrinder" },
				{ "Soul Separator", "SoulSep" },
				{ "Dark Legion's Edge", "DLE" },
				{ "Sword of Miracles", "SoM" },
				{ "Keshanberk*Damascus", "Kes*Dam" },
				{ "Behemoth's Tuning Fork", "BTFork" },
				{ "Sword of Ipos", "Ipos" }, { "Barakiel's Axe", "Bara Axe" },
				{ "Cabrio's Hand", "Cabri Hand" },
				{ "Screaming Vengeance", "Best A xbow" },
				{ "Sobekk's Hurricane", "Sobekk Fist" },
				{ "Damascus*Damascus", "Dam*Dam" },
				{ "Damascus * Tallum Blade", "Dam*Tal" },
				{ "Dragon Hunter Axe", "Dra Axe" },
				{ "Heaven's Divider", "Heaven Div" }, { "Arcana Mace", "AM" },
				{ "Basalt Battlehammer", "Basalt" },
				{ "Draconic Bow", "Drac Bow" }, { "Forgotten Blade", "FB" },
				{ "Tallum Blade*Dark Legion's Edge", "Tal*DLE" },
				{ "Majestic Necklace", "MJT Neck" },
				{ "Majestic Earring", "MJT Earring" },
				{ "Majestic Ring", "MJT Ring" },
				{ "Tateossian Necklace", "Tat Neck" },
				{ "Tateossian Earring", "Tat Earring" },
				{ "Tateossian Ring", "Tat Ring" },
				{ "Material Chest Lv.1", "Chest lv1" },
				{ "Material Chest Lv.2", "Chest lv2" },
				{ "Material Chest Lv.3", "Chest lv3" },
				{ "Material Chest Lv.4", "Chest lv4" },
				{ "Material Chest Lv.5", "Chest lv5" },
				{ "Material Chest Lv.6", "Chest lv6" },
				{ "Material Chest Lv.7", "Chest lv7" } };
		for (int i = 0; i < itemNameList.length; i++)
			if (itemName.indexOf(itemNameList[i][0]) > -1)
				return itemNameList[i][1];
		return itemName;
	}

	public static String shortenItemPrice(long price) {
		String itemPrice;
		try {
			if (price > 1000) {
				if (price % 1000 == 0)
					itemPrice = price / 1000 + "k";
				else
					itemPrice = String.format("%.1f", price / 1000) + "k";
			} else
				itemPrice = price + "a";
			return itemPrice;
		} catch (Exception e) {
			return "";
		}
	}

	public static String generateShopTitle(String name, long price)
	{
		int maxTitleLength = 29;
		
		String shopName;
		
		if(Rnd.chance(50))
			shopName = "Cheap ";
		else if(Rnd.chance(50))
			shopName = "Best ";
		else 
			shopName = "";
	
		String itemName = shortenItemName(name);
		
		String itemPrice = (Rnd.chance(50))? shortenItemPrice(price):"";
		
		shopName = shopName.concat(itemName);
		shopName = shopName.concat(" ");
		
		if(shopName.length() + itemPrice.length() <= maxTitleLength)
			shopName = shopName.concat(itemPrice);
		
		return shopName;
	}
}
