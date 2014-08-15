package l2s.gameserver.templates.item.support;

import l2s.gameserver.model.base.Element;
import l2s.gameserver.templates.item.ItemQuality;

/**
 * @author Bonux
 */
public class AttributeStone
{
	private final int _itemId;
	private final int _chance;
	private final Element _element;
	private final int _frstEnchPowerWeapon;
	private final int _frstEnchPowerArmor;
	private final int _enchPowerWeapon;
	private final int _enchPowerArmor;
	private final int _maxEnchWeapon;
	private final int _maxEnchArmor;
	private final ItemQuality _itemType;

	public AttributeStone(int itemId, int chance, Element element, int frstEnchPowerWeapon, int frstEnchPowerArmor, int enchPowerWeapon, int enchPowerArmor, int maxEnchWeapon, int maxEnchArmor, ItemQuality itemType)
	{
		_itemId = itemId;
		_chance = chance;
		_element = element;
		_frstEnchPowerWeapon = frstEnchPowerWeapon;
		_frstEnchPowerArmor = frstEnchPowerArmor;
		_enchPowerWeapon = enchPowerWeapon;
		_enchPowerArmor = enchPowerArmor;
		_maxEnchWeapon = maxEnchWeapon;
		_maxEnchArmor = maxEnchArmor;
		_itemType = itemType;
	}

	public int getItemId()
	{
		return _itemId;
	}

	public int getChance()
	{
		return _chance;
	}

	public Element getElement(boolean weapon)
	{
		return weapon ? _element : Element.getReverseElement(_element);
	}

	public int getFirstEnchantPower(boolean weapon)
	{
		return weapon ? _frstEnchPowerWeapon : _frstEnchPowerArmor;
	}

	public int getEnchantPower(boolean weapon)
	{
		return weapon ? _enchPowerWeapon : _enchPowerArmor;
	}

	public int getMaxEnchant(boolean weapon)
	{
		return weapon ? _maxEnchWeapon : _maxEnchArmor;
	}

	public ItemQuality getItemType()
	{
		return _itemType;
	}

	public int getStoneLevel()
	{
		return _maxEnchArmor / 20;
	}
}
