package l2s.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Element;
import l2s.commons.data.xml.AbstractFileParser;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.AttributeStoneHolder;
import l2s.gameserver.templates.item.ItemQuality;
import l2s.gameserver.templates.item.support.AttributeStone;

/**
 * @author Bonux
 */
public class AttributeStoneParser extends AbstractFileParser<AttributeStoneHolder>
{
	private static AttributeStoneParser _instance = new AttributeStoneParser();

	public static AttributeStoneParser getInstance()
	{
		return _instance;
	}

	private AttributeStoneParser()
	{
		super(AttributeStoneHolder.getInstance());
	}

	@Override
	public File getXMLFile()
	{
		return new File(Config.DATAPACK_ROOT, "data/attribute_stones.xml");
	}

	@Override
	public String getDTDFileName()
	{
		return "attribute_stones.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		int defaultChance = 0;
		int defaultEnchPowerWeapon = 0;
		int defaultEnchPowerArmor = 0;
		int defaultMaxEnchWeapon = 0;
		int defaultMaxEnchArmor = 0;
		int defaultFrstEnchPowerWeapon = 0;
		int defaultFrstEnchPowerArmor = 0;

		Element defaultElement = rootElement.element("default");
		if(defaultElement != null)
		{
			defaultChance = Integer.parseInt(defaultElement.attributeValue("chance"));
			defaultEnchPowerWeapon = Integer.parseInt(defaultElement.attributeValue("enchant_power_weapon"));
			defaultEnchPowerArmor = Integer.parseInt(defaultElement.attributeValue("enchant_power_armor"));
			defaultMaxEnchWeapon = Integer.parseInt(defaultElement.attributeValue("max_enchant_weapon"));
			defaultMaxEnchArmor = Integer.parseInt(defaultElement.attributeValue("max_enchant_armor"));
			defaultFrstEnchPowerWeapon = defaultElement.attributeValue("first_enchant_power_weapon") == null ? defaultEnchPowerWeapon : Integer.parseInt(defaultElement.attributeValue("first_enchant_power_weapon"));
			defaultFrstEnchPowerArmor = defaultElement.attributeValue("first_enchant_power_armor") == null ? defaultEnchPowerArmor : Integer.parseInt(defaultElement.attributeValue("first_enchant_power_armor"));
		}

		for(Iterator<Element> iterator = rootElement.elementIterator("attribute_stone"); iterator.hasNext();)
		{
			Element attributeStoneElement = iterator.next();
			int itemId = Integer.parseInt(attributeStoneElement.attributeValue("id"));
			l2s.gameserver.model.base.Element attrElement = l2s.gameserver.model.base.Element.valueOf(attributeStoneElement.attributeValue("element").toUpperCase());
			int chance = attributeStoneElement.attributeValue("chance") == null ? defaultChance : Integer.parseInt(attributeStoneElement.attributeValue("chance"));
			int enchPowerWeapon = attributeStoneElement.attributeValue("enchant_power_weapon") == null ? defaultEnchPowerWeapon : Integer.parseInt(attributeStoneElement.attributeValue("enchant_power_weapon"));
			int enchPowerArmor = attributeStoneElement.attributeValue("enchant_power_armor") == null ? defaultEnchPowerArmor : Integer.parseInt(attributeStoneElement.attributeValue("enchant_power_armor"));
			int maxEnchWeapon = attributeStoneElement.attributeValue("max_enchant_weapon") == null ? defaultMaxEnchWeapon : Integer.parseInt(attributeStoneElement.attributeValue("max_enchant_weapon"));
			int maxEnchArmor = attributeStoneElement.attributeValue("max_enchant_armor") == null ? defaultMaxEnchArmor : Integer.parseInt(attributeStoneElement.attributeValue("max_enchant_armor"));
			int frstEnchPowerWeapon = attributeStoneElement.attributeValue("first_enchant_power_weapon") == null ? defaultFrstEnchPowerWeapon : Integer.parseInt(attributeStoneElement.attributeValue("first_enchant_power_weapon"));
			int frstEnchPowerArmor = attributeStoneElement.attributeValue("first_enchant_power_armor") == null ? defaultFrstEnchPowerArmor : Integer.parseInt(attributeStoneElement.attributeValue("first_enchant_power_armor"));
			ItemQuality itemType = attributeStoneElement.attributeValue("item_type") == null ? null : ItemQuality.valueOf(attributeStoneElement.attributeValue("item_type").toUpperCase());

			AttributeStone item = new AttributeStone(itemId, chance, attrElement, frstEnchPowerWeapon, frstEnchPowerArmor, enchPowerWeapon, enchPowerArmor, maxEnchWeapon, maxEnchArmor, itemType);

			getHolder().addAttributeStone(item);
		}
	}
}
