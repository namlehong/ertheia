package services;

import java.util.ArrayList;

import l2s.gameserver.Config;
import l2s.gameserver.model.SimpleSpawner;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.scripts.ScriptFile;

public class FightClub extends Functions implements ScriptFile
{
	private static final ArrayList<SimpleSpawner> _spawns_fight_club_manager = new ArrayList<SimpleSpawner>();

	public static int FIGHT_CLUB_MANAGER = 32500;

	private void spawnFightClub()
	{
		final int FIGHT_CLUB_MANAGER_SPAWN[][] = {

		{ 82042, 149711, -3356, 58312 }, // Giran
		{ 146408, 28536, -2255, 33600 }, // Aden 1
		{ 148504, 28536, -2255, 33600 }, // Aden 2
		{ 145800, -57112, -2966, 32500 }, //Goddard 1
		{ 150120, -56504, -2966, 32500 }, //Goddard 2
		{ 43656, -46792, -784, 17000 }, //Rune
		{ 19448, 145048, -3094, 16500 }, //Dion 1
		{ 17832, 144312, -3037, 16500 }, //Dion 2
		{ 82888, 55304, -1511, 16500 }, //Oren 1
		{ 80104, 53608, -1547, 16500 }, //Oren 2
		{ -15064, 124296, -3104, 16500 }, //Gludio 1
		{ -12184, 122552, -3086, 16500 }, //Gludio 2
		{ -82648, 149896, -3115, 33600 }, //Gludin 1
		{ -81800, 155368, -3163, 58312 }, //Gludin 2
		{ 89272, -141592, -1525, 32500 }, //Shuttgart 1
		{ 87672, -140776, -1525, 32500 }, //Shuttgart 2
		{ 115496, 218728, -3648, 16500 }, //Heine 1
		{ 107384, 217704, -3661, 16500 }, //Heine 2
		{ 116808, 75448, -2748, 16500 }, //Hunter's Village 
		};

		SpawnNPCs(FIGHT_CLUB_MANAGER, FIGHT_CLUB_MANAGER_SPAWN, _spawns_fight_club_manager);
	}

	@Override
	public void onLoad()
	{
		if(Config.FIGHT_CLUB_ENABLED)
			spawnFightClub();
	}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}
}