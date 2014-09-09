package blood.data.holder;

import java.util.HashMap;
import java.util.HashSet;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.Location;

public class NpcHelper extends AbstractHolder {
	
	private static final NpcHelper _instance = new NpcHelper();
	public static NpcHelper getInstance()
	{
		return _instance;
	}
	
	public static HashSet<Integer> 				_buffer_ids 	= new HashSet<Integer>();
	public static HashSet<Integer> 				_gatekeeper_ids = new HashSet<Integer>();
	public static HashSet<Integer> 				_interested_npc_ids = new HashSet<Integer>();
	
	public static HashMap<Integer, NpcInstance>	_buffers 		= new HashMap<Integer, NpcInstance>();
	public static HashMap<Integer, NpcInstance> _gatekeepers 	= new HashMap<Integer, NpcInstance>();
	public static HashMap<Integer, NpcInstance> _interested_npc = new HashMap<Integer, NpcInstance>();
	
	public static void addBufffer(int npcId)
	{
		_buffer_ids.add(npcId);
		addInterester(npcId);
	}
	
	public static void addGatekeeper(int npcId)
	{
		_gatekeeper_ids.add(npcId);
		addInterester(npcId);
	}
	
	public static void addInterester(int npcId)
	{
		_interested_npc_ids.add(npcId);
	}
	
	public static void updateNpcInstance()
	{
		int[] npc_ids = new int[_interested_npc_ids.size()];
		
		int index = 0;
		for(Integer npcId: _interested_npc_ids)
		{
			npc_ids[index++] = npcId;
		}
		
		for(NpcInstance npc: GameObjectsStorage.getAllByNpcId(npc_ids, true))
		{
			if(_buffer_ids.contains(npc.getNpcId()))
				_buffers.put(npc.getObjectId(), npc);
			if(_gatekeeper_ids.contains(npc.getNpcId()))
				_gatekeepers.put(npc.getObjectId(), npc);
			
			_interested_npc.put(npc.getObjectId(), npc);
		}
	}
	
	public static NpcInstance getClosestNpc(Location loc, HashMap<Integer, NpcInstance> map)
	{
		double minDistance = Double.MAX_VALUE;
		NpcInstance nearestNpc = null;
		for(NpcInstance npc: map.values())
		{
			double disatance = npc.getDistance(loc); 
			if(disatance < minDistance)
			{
				minDistance = disatance;
				nearestNpc = npc;
			}
		}
		
		return nearestNpc;
	}
	
	public static NpcInstance getClosestBuffer(Location loc)
	{
		return getClosestNpc(loc, _buffers);
	}
	
	public static NpcInstance getClosestBuffer(GameObject obj)
	{
		return getClosestBuffer(obj.getLoc());
	}
	
	public static NpcInstance getClosestGatekeeper(Location loc)
	{
		return getClosestNpc(loc, _gatekeepers);
	}
	
	public static NpcInstance getClosestGatekeeper(GameObject obj)
	{
		return getClosestGatekeeper(obj.getLoc());
	}
	
	public static NpcInstance getClosestInterestedNpc(Location loc)
	{
		return getClosestNpc(loc, _gatekeepers);
	}
	
	public static NpcInstance getClosestInterestedNpc(GameObject obj)
	{
		return getClosestGatekeeper(obj.getLoc());
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return _interested_npc_ids.size();
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		_interested_npc_ids.clear();
	}
	

}
