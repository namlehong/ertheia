package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.scripts.ScriptFile;

//By viRUS
public class _10344_DayOfDestinyOrcsFate extends SagasSuperclass implements ScriptFile 
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

	public _10344_DayOfDestinyOrcsFate()
	{
		super(false);

		StartNPC = 30865;
		StartRace = Race.ORC;

		init();
	}
}
