package l2s.gameserver.handler.bypass;

/**
 * @author VISTALL
 * @date 15:51/12.07.2011
 */
public class BypassHandler
{
	private static final BypassHandler _instance = new BypassHandler();

	public static BypassHandler getInstance()
	{
		return _instance;
	}

	public void registerBypass(IBypassHandler bypass)
	{

	}
}
