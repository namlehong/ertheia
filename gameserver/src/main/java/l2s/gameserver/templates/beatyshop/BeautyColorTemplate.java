package l2s.gameserver.templates.beatyshop;

/**
 * @author Bonux
 */
public class BeautyColorTemplate
{
	private final int _id;
	private final long _adena;
	private final long _coins;

	public BeautyColorTemplate(int id, long adena, long coins)
	{
		_id = id;
		_adena = adena;
		_coins = coins;
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
}
