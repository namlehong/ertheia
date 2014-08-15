package instances;

import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.utils.Location;

/**
 * Класс контролирует инстанс Teredor
 *
 * @author coldy
 */
public class Teredor extends Reflection
{
	private static final int TEREDOR = 19160;
	private static final Location _teredorCoords = new Location(176160,-185200,-3800);

	@Override
	protected void onCreate()
	{
		super.onCreate();
		this.addSpawnWithoutRespawn(TEREDOR, _teredorCoords, 0);
	}
}