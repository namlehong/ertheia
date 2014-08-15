package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.scripts.ScriptFile;

//By viRUS
public class _10343_DayOfDestinyDarkElfsFate extends SagasSuperclass implements ScriptFile 
{
	@Override
	public void onLoad()
	{
	}

	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{
	}

	public _10343_DayOfDestinyDarkElfsFate()
	{
		super(false);

		StartNPC = 30862;
		StartRace = Race.DARKELF;

		init();
	}
}