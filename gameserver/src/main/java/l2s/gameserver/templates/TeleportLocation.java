package l2s.gameserver.templates;

import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.utils.Location;

public class TeleportLocation extends Location
{
	private static final long serialVersionUID = 1L;

	private final int _itemId;
	private final long _price;
	private final int _name;
	private final int _castleId;
	private final boolean _primeHours;

	public TeleportLocation(int itemId, long price, int name, int castleId, boolean primeHours)
	{
		_itemId = itemId;
		_price = price;
		_name = name;
		_castleId = castleId;
		_primeHours = primeHours;
	}

	public int getItemId()
	{
		return _itemId;
	}

	public long getPrice()
	{
		return _price;
	}

	public int getName()
	{
		return _name;
	}

	public int getCastleId()
	{
		return _castleId;
	}

	public boolean isPrimeHours()
	{
		return _primeHours;
	}
}
