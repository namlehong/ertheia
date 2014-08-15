package handler.bypass;

import l2s.gameserver.handler.bypass.BypassHandler;
import l2s.gameserver.handler.bypass.IBypassHandler;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author VISTALL
 * @date 15:53/12.07.2011
 */
public abstract class ScriptBypassHandler implements ScriptFile, IBypassHandler
{
	@Override
	public void onLoad()
	{
		BypassHandler.getInstance().registerBypass(this);
	}

	@Override
	public void onReload()
	{

	}

	@Override
	public void onShutdown()
	{

	}
}
