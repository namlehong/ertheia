package blood.model;

import java.util.HashSet;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.utils.Location;

public class FarmLocation extends Location {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected final int _min_level;
	protected final int _max_level;
	protected final boolean _party_required;
	protected final HashSet<Integer> _allowClassIds;
	
	public FarmLocation(int minLevel, int maxLevel, boolean partyRequired, HashSet<Integer> allowIds)
	{
		_min_level = minLevel;
		_max_level = maxLevel;
		_party_required = partyRequired;
		_allowClassIds = allowIds;
	}
	
	public int getMinLevel()
	{
		return _min_level;
	}
	
	public int getMaxLevel()
	{
		return _max_level;
	}
	
	public boolean isPartyRequired()
	{
		return _party_required;
	}
	
	public HashSet<Integer> getAllowIds()
	{
		return _allowClassIds;
	}
	
	public boolean isAllowClass(int classId)
	{
		return _allowClassIds == null || _allowClassIds.size() == 0 || _allowClassIds.contains(classId);
	}
	
	public boolean isAllowClass(ClassId classId)
	{
		return isAllowClass(classId.getId());
	}
	
	public boolean isValidLevel(int level)
	{
		return _min_level <= level && level <= _max_level;
	}
	
	public boolean isValidPlayer(Player player)
	{
		return isValidLevel(player.getLevel()) && isAllowClass(player.getClassId()) && _party_required == player.isInParty();
	}
	
	public FarmLocation getAround(int radiusmin, int radiusmax, int geoIndex)
	{
		FarmLocation newLoc = new FarmLocation(_min_level, _max_level, _party_required, _allowClassIds);
		newLoc.set(Location.findPointToStay(this, radiusmin, radiusmax, geoIndex));
		return newLoc;
	}
	
}
