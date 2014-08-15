package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.scripts.ScriptFile;

//By viRUS
public class _10345_DayOfDestinyDwarfsFate extends SagasSuperclass implements ScriptFile 
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

	public _10345_DayOfDestinyDwarfsFate()
	{
		super(false);

		StartNPC = 30847;
		StartRace = Race.DWARF;

		init();
	}
}

