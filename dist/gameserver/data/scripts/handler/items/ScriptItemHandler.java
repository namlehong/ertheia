package handler.items;

import l2s.gameserver.handler.items.ItemHandler;
import l2s.gameserver.handler.items.impl.DefaultItemHandler;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author Bonux
 */
public abstract class ScriptItemHandler extends DefaultItemHandler implements ScriptFile
{
	@Override
	public void onLoad()
	{
		ItemHandler.getInstance().registerItemHandler(this);
	}

	@Override
	public void onReload()
	{
		//
	}

	@Override
	public void onShutdown()
	{
		//
	}
}
