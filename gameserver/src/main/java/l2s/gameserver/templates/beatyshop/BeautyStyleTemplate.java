package l2s.gameserver.templates.beatyshop;

import gnu.trove.map.TIntObjectMap;

/**
 * @author Bonux
 */
public class BeautyStyleTemplate
{
	private final int _id;
	private final long _adena;
	private final long _coins;
	private final long _resetPrice;
	private final TIntObjectMap<BeautyColorTemplate> _colors;

	public BeautyStyleTemplate(int id, long adena, long coins, long resetPrice, TIntObjectMap<BeautyColorTemplate> colors)
	{
		_id = id;
		_adena = adena;
		_coins = coins;
		_resetPrice = resetPrice;
		_colors = colors;
	}

	public int getId()
	{
		return _id;
	}

	public long getAdena()
	{
		return _adena;
	}

	public long getCoins()
	{
		return _coins;
	}

	public long getResetPrice()
	{
		return _resetPrice;
	}

	public BeautyColorTemplate getColor(int id)
	{
		if(_colors == null)
			return null;
		return _colors.get(id);
	}
}
