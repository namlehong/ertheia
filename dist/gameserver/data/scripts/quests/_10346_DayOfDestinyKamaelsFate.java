package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.scripts.ScriptFile;

//By viRUS
public class _10346_DayOfDestinyKamaelsFate extends SagasSuperclass implements ScriptFile 
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

	public _10346_DayOfDestinyKamaelsFate()
	{
		super(false);

		StartNPC = 32221;
		StartRace = Race.KAMAEL;

		init();
	}
}
