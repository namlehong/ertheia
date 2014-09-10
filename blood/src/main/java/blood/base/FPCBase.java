package blood.base;

import java.util.ArrayList;
import java.util.List;

import blood.FPCInfo;
import javolution.util.FastMap;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;

/**
 * @author hNoke
 *
 */
public class FPCBase
{
	private FastMap<Integer, FPCInfo> players;
	
	//TODO: offline players
	
	public FPCBase()
	{
		players = (new FastMap<Integer, FPCInfo>());
	}
	
	public FPCInfo getPlayer(int id)
	{
		return players.get(id);
	}
	
	public FastMap<Integer, FPCInfo> getPs()
	{
		return players;
	}
	
	public FPCInfo getRandom()
	{
		List<FPCInfo> valuesList = new ArrayList<FPCInfo>(players.values());
		if(valuesList.size() <= 0)
			return null;
		return valuesList.get(Rnd.get(valuesList.size()));
	}
	
	protected FPCInfo getPlayer(Player player)
	{
		return getPlayer(player.getObjectId());
	}
	
	public FPCInfo addInfo(FPCInfo player)
	{
		players.put(player.getObjectId(), player);
		return player;
	}
	
	public void deleteInfo(int player)
	{
		players.remove(player);
	}
	
	public void deleteInfo(FPCInfo player)
	{
		players.remove(player.getObjectId());
	}
	
	public static final FPCBase getInstance()
	{
		return SingletonHolder._instance;
	}
	
	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final FPCBase _instance = new FPCBase();
	}
}
