package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.scripts.ScriptFile;

//By viRUS
public class _10342_DayOfDestinyElvenFate extends SagasSuperclass implements ScriptFile 
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

	public _10342_DayOfDestinyElvenFate()
	{
		super(false);

		StartNPC = 30856;
		StartRace = Race.ELF;

		init();
	}
}
