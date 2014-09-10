package blood.base;

import blood.FPCInfo;

public enum FPCSpawnStatus {
	ONLINE("online"),
	OFFLINE("offline");
	
	@SuppressWarnings("unused")
	private String _name;
	private FPCBase _players = new FPCBase();
	private static int _population = 260;
	private static int _online_ratio = 1;
	
	private FPCSpawnStatus(String name)
	{
		_name = name;
	}
	
	public int getSize()
	{
		return _players.getPs().size();
	}
	
	public void add(FPCInfo player)
	{
		_players.addInfo(player);
	}
	
	public void remove(FPCInfo player)
	{
		_players.deleteInfo(player);
	}
	
	public FPCInfo getRandom()
	{
		return _players.getRandom();
	}
	
	public static void setPopulation(int population)
	{
		_population = population;
	}
	
	public int getPopulation()
	{
		return _population;
	}
	
	public static int getDiff()
	{
		return _population - (ONLINE.getSize() + OFFLINE.getSize());
	}

	public static int getOnlineRatio()
	{
		return _online_ratio;
	}

	public static void setOnlineRatio(int _online_ratio)
	{
		FPCSpawnStatus._online_ratio = _online_ratio;
	}
}