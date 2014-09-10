package blood.data.holder;

import java.util.HashSet;

import l2s.commons.data.xml.AbstractHolder;
import l2s.commons.math.random.RndSelector;
import l2s.gameserver.model.Player;
import blood.model.FarmLocation;

public final class FarmLocationHolder  extends AbstractHolder{
	
	/**
	 * Field _instance.
	 */
	private static final FarmLocationHolder _instance = new FarmLocationHolder();
	
	/**
	 * Field _bonusList.
	 */
//	private final List<FarmZone> _lists = new ArrayList<FarmZone>();
	
	private final HashSet<FarmLocation> _lists = new HashSet<FarmLocation>();
	
	/**
	 * Method getInstance.
	 * @return LevelBonusHolder
	 */
	public static FarmLocationHolder getInstance()
	{
		return _instance;
	}
	
	public void addLoc(FarmLocation farmLoc) {
		_lists.add(farmLoc);
	}
	
	public FarmLocation getLocation(Player player)
	{
		RndSelector<FarmLocation> tmp = new RndSelector<FarmLocation>();
		for(FarmLocation loc: _lists)
			if(loc.isValidPlayer(player))
				tmp.add(loc, 1);
		
		FarmLocation result = tmp.select();
		
		return result == null ? null : result.getAround(300, 500, player.getGeoIndex());
	}
	
	public FarmLocation getPartyLocation(int level, int geoIndex)
	{
		RndSelector<FarmLocation> tmp = new RndSelector<FarmLocation>();
		for(FarmLocation loc: _lists)
			if(loc.isPartyRequired() && loc.isValidLevel(level))
				tmp.add(loc, 1);
		
		FarmLocation result = tmp.select();
		
		return result == null ? null : result.getAround(300, 500, geoIndex);
	}
	

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return _lists.size();
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		_lists.clear();
	}
}
