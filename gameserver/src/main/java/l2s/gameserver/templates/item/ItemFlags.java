package l2s.gameserver.templates.item;

/**
 * @author VISTALL
 * @date 20:51/11.01.2011
 */
public enum ItemFlags
{
	DESTROYABLE(true), // возможность уничтожить
	DROPABLE(true), // возможность дропнуть
	FREIGHTABLE(false), // возможность передать в рамках аккаунта
	AUGMENTABLE(true), // возможность аугментировать
	ENCHANTABLE(true), // возможность заточить
	ATTRIBUTABLE(true), // возможность заточить атрибутом
	SELLABLE(true), // возможность продать
	TRADEABLE(true), // возможность передать
	STOREABLE(true), // Able to put in the BX
	APPEARANCEABLE(true), // Chance to handle the thing
	PRIVATESTOREABLE(true), // Able to sell in the personal store
	COMMISSIONABLE(true); // Opportunity to exhibit item on auction

	public static final ItemFlags[] VALUES = values();

	private final int _mask;
	private final boolean _defaultValue;

	ItemFlags(boolean defaultValue)
	{
		_defaultValue = defaultValue;
		_mask = 1 << ordinal();
	}

	public int mask()
	{
		return _mask;
	}

	public boolean getDefaultValue()
	{
		return _defaultValue;
	}
}
