package blood.data.holder;

import java.util.HashMap;

import l2s.commons.data.xml.AbstractHolder;
import l2s.commons.math.random.RndSelector;
import l2s.gameserver.model.Player;
import blood.model.FPRewardList;

public final class FPItemHolder extends AbstractHolder
{
	/**
	 * Field _instance.
	 */
	private static final FPItemHolder _instance = new FPItemHolder();
	/**
	 * Field _bonusList.
	 */
	private static final HashMap<String, FPRewardList> _rewards = new HashMap<String, FPRewardList>();
	
	/**
	 * Method getInstance.
	 * @return LevelBonusHolder
	 */
	public static FPItemHolder getInstance()
	{
		return _instance;
	}
	
	public void add(String id, FPRewardList rewardList)
	{
		_rewards.put(id, rewardList);
	}
	
	public static FPRewardList get(String id)
	{
		return _rewards.get(id);
	}
	
	public static FPRewardList getRewardList(Player player, boolean useOldList)
	{
		if(useOldList)
		{
			FPRewardList oldList = _rewards.get(player.getVar(FPRewardList.PLAYER_VAR_SAVE));
			if(oldList != null && oldList.isValid(player))
				return oldList;
		}
		
		RndSelector<FPRewardList> rndFactor = new RndSelector<FPRewardList>(); 
		int count = 0;
		
		if(count == 0)
			for(FPRewardList reward_list: _rewards.values())
			{
//				if(reward_list.isValidLevel(player.getLevel()))
//				{
//					System.out.println(String.format("List:%s min:%d max:%d pass levelcheck", reward_list._id, reward_list.getMinLevel(), reward_list.getMaxLevel()));
//					
//					if(reward_list.isValidClassId(player))
//						System.out.println(String.format("List:%s pass class check", reward_list._id));
//					
//					if(reward_list.isValidType(player))
//						System.out.println(String.format("List:%s pass class type", reward_list._id));
//					
//					if(reward_list.isValidType2(player))
//						System.out.println(String.format("List:%s pass class type2", reward_list._id));
//				}	
//				
//				System.out.println(String.format("List:%s min:%d max:%d id.size:%d type.size:%d type2.size:%d", 
//						reward_list._id, 
//						reward_list.getMinLevel(), 
//						reward_list.getMaxLevel(), 
//						reward_list.getClassIds().size(), 
//						reward_list.getClassTypes().size(), 
//						reward_list.getClassTypes2().size()));
				if(reward_list.isValidClassId(player)){
					rndFactor.add(reward_list, reward_list.getWeight());
					count++;
				}
			}
		
		if(count == 0)
			for(FPRewardList reward_list: _rewards.values())
				if(reward_list.isValidType2(player)){
					rndFactor.add(reward_list, reward_list.getWeight());
					count++;
				}
		
		if(count == 0)
			for(FPRewardList reward_list: _rewards.values())
				if(reward_list.isValidType(player)){
					rndFactor.add(reward_list, reward_list.getWeight());
					count++;
				}
		
		return rndFactor.select();
	}
	
	public static void equip(Player player, boolean useOldList)
	{
		FPRewardList rewardList = getRewardList(player, useOldList);
		
		if(rewardList != null)
			rewardList.distributeAll(player);
	}
	
	/**
	 * Method size.
	 * @return int
	 */
	@Override
	public int size()
	{
		return _rewards.size();
	}
	
	/**
	 * Method clear.
	 */
	@Override
	public void clear()
	{
		_rewards.clear();
	}
}
