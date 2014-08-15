package services;

import l2s.gameserver.Config;
import l2s.gameserver.scripts.Functions;

public class Pushkin extends Functions
{
	public String DialogAppend_30300(Integer val)
	{
		if(val != 0 || !Config.ALT_SIMPLE_SIGNS)
			return "";

		StringBuilder append = new StringBuilder();

		if((getSelf()).isLangRus())
		{
			if(Config.ALT_SIMPLE_SIGNS)
			{
				append.append("<br><center>*Опции Кузнеца Маммона:*</center><br>");
				append.append("<center>[npc_%objectId%_Multisell 778|Изготовить парные мечи Ранга R]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 779|Инкрустировать Кристалл Души в оружие Ранга R]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 216|Создать Парные Мечи Ранга S]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 215|Зачаровать оружие Ранга S]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 217|Снять печать с доспеха Ранга S]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 218|Снять печать с аксессуара Ранга S]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 211|Изготовить парные мечи Ранга A]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 204|Придать особый эффект оружию Ранга А]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 252|Удалить особые эффекты с оружия]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 637|Обработать Редкий предмет]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 700|Обменять Редкий Верхний Доспех на Обычный Верхний Доспех]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 776|Снять оковы с предмета]</center><br1>");
				append.append("<br><center>*Опции Торговца Маммона:*</center><br>");
				append.append("<center>[npc_%objectId%_Multisell 207|Купить предмет]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 777|Обменять Редкий Кристалл Души]</center><br1>");
				append.append("<br><center>*Опции Жреца Маммона:*</center><br>");
				append.append("<center>[npc_%objectId%_Multisell 208|Купить бакалею]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 2021|Обменять Камень Печати]</center><br1>");
			}
		}
		else
		{
			if(Config.ALT_SIMPLE_SIGNS)
			{
				append.append("<br><center>*Blacksmith of Mammon:*</center><br>");
				append.append("<center>[npc_%objectId%_Multisell 778|Craft R-Grade Dualsword]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 779|Bestow legendary capability on R-Grade weapon]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 216|Craft S-Grade Dualsword]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 215|Bestow special capability on S-Grade weapon]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 217|Release seal on S-Grade armor]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 218|Release seal on S-Grade accessory]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 211|Craft A-Grade Dualsword]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 204|Bestow special capability on A-Grade weapon]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 252|Remove a weapon's special capability]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 637|Complete a Foundation Item]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 700|Exchange rare upper armor for regular upper armor]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 776|Remove the shackles from item]</center><br1>");
				append.append("<br><center>*Merchant of Mammon:*</center><br>");
				append.append("<center>[npc_%objectId%_Multisell 207|Buy Something]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 777|Exchange Rare Soul Crystal]</center><br1>");
				append.append("<br><center>*Priest of Mammon:*</center><br>");
				append.append("<center>[npc_%objectId%_Multisell 208|Purchase consumable items]</center><br1>");
				append.append("<center>[npc_%objectId%_Multisell 2021|Exchange Seal Stones]</center><br1>");
			}
		}

		return append.toString();
	}

	public String DialogAppend_30086(Integer val)
	{
		return DialogAppend_30300(val);
	}
}