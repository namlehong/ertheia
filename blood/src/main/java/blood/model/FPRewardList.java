package blood.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.ClassType;
import l2s.gameserver.model.base.ClassType2;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.templates.item.EtcItemTemplate.EtcItemType;
import l2s.gameserver.utils.ItemFunctions;
import blood.data.holder.FPItemHolder;

public class FPRewardList 
{
	public final static HashSet<Integer> _all_used_items 		= new HashSet<Integer>();
	public final static String PLAYER_VAR_SAVE 					= "_last_fpreward_list";
	
	public final HashSet<ClassType2>		_allow_class_type2 	= new HashSet<ClassType2>();
	public final HashSet<ClassType>			_allow_class_type 	= new HashSet<ClassType>();
	public final HashSet<ClassId>			_allow_class_id		= new HashSet<ClassId>();
	public final HashSet<Integer> 			_remove_items 		= new HashSet<Integer>();
	public final HashMap<Integer, Integer> 	_reward_items 		= new HashMap<Integer, Integer>(); 
	
	public final String						_id;
	public final String						_parent_id;
	public final int						_min_level;
	public final int						_max_level;
	public final int						_weight;
	
	public FPRewardList						_parent = null;
		
	public FPRewardList(String id, int min_level, int max_level, String parent_id, int weight)
	{
		_id = id;
		_min_level = min_level;
		_max_level = max_level;
		_parent_id = parent_id;
		_weight = weight;
	}
	
	public String getId() {
		return _id;
	}
	
	public int getWeight() {
		return _weight;
	}
	
	public FPRewardList getParent(){
		if(_parent_id == null)
			return null;
		
		if(_parent == null)
			_parent = FPItemHolder.get(_parent_id);
		
		if(_parent == null)
			System.out.println(String.format("List %s has parent %s not exists", _id, _parent_id));
		
		return _parent;
	}
	
	public int getMinLevel()
	{
		if (_min_level > 0)
			return _min_level;
		
		if(getParent() != null)
			return getParent().getMinLevel();
		
		return 1;
	}
	
	public int getMaxLevel()
	{
		if (_max_level > 0)
			return _max_level;
		
		if(getParent() != null)
			return getParent().getMaxLevel();
		
		return 1;
	}
	
	public HashSet<ClassId> getClassIds()
	{
		if(_allow_class_id.size() > 0)
			return _allow_class_id;
		
		if(getParent() != null)
			return getParent().getClassIds();
		
		return _allow_class_id;
	}
	
	public HashSet<ClassType> getClassTypes()
	{
		if(_allow_class_type.size() > 0)
			return _allow_class_type;
		
		if(getParent() != null)
			return getParent().getClassTypes();
		
		return _allow_class_type;
	}
	
	public HashSet<ClassType2> getClassTypes2()
	{
		if(_allow_class_type2.size() > 0)
			return _allow_class_type2;
		
		if(getParent() != null)
			return getParent().getClassTypes2();
		
		return _allow_class_type2;
	}
	
	public boolean canUse(){
		return getClassTypes2().size() > 0 || getClassIds().size() > 0 || getClassTypes().size() > 0;
	}
	
	public void addClassId(ClassId classId){
		if(classId == null)
			return;
		_allow_class_id.add(classId);
	}
	
	public void addClassType(ClassType classType){
		if(classType == null)
			return;
		_allow_class_type.add(classType);
	}
	
	public void addClassType2(ClassType2 classType2){
		if(classType2 == null)
			return;
		_allow_class_type2.add(classType2);
	}
	
	public void addItem(int item_id, int amount){
		_reward_items.put(item_id, amount);
		_all_used_items.add(item_id);
	}
	
	public boolean isValidLevel(int level)
	{
		return canUse() && getMinLevel() <= level && level <= getMaxLevel();
	}
	
	public boolean isValidClassId(ClassId classId){
		return classId != null && getClassIds().contains(classId);
	}
	
	public boolean isValidClassId(Player player){
		return isValidLevel(player.getLevel()) && isValidClassId(player.getClassId());
	}
	
	public boolean isValidType(ClassType classType){
		return classType != null && getClassTypes().contains(classType);
	}
	
	public boolean isValidType(ClassId classId){
		return isValidType(classId.getType());
	}
	
	public boolean isValidType(Player player){
		return isValidLevel(player.getLevel()) && isValidType(player.getClassId());
	}
	
	public boolean isValidType2(ClassType2 classType2){
		return classType2 != null && getClassTypes2().contains(classType2);
	}
	
	public boolean isValidType2(ClassId classId){
		return isValidType2(classId.getType2());
	}
	
	public boolean isValidType2(Player player){
		return isValidLevel(player.getLevel()) && isValidType2(player.getClassId());
	}
	
	public boolean isValid(Player player) {
		return isValidType2(player) || isValidClassId(player) || isValidType(player);
	}
	
	public HashMap<Integer, Integer> getRewards()
	{
		HashMap<Integer, Integer> rewards = new HashMap<Integer, Integer>();
		
		FPRewardList parent = getParent();
		
		if(parent != null)
		{
			for(Map.Entry<Integer, Integer> parent_entry: parent.getRewards().entrySet())
			{
				if(!_remove_items.contains(parent_entry.getKey()))
					rewards.put(parent_entry.getKey(), parent_entry.getValue());
			}
		}
		
		for(Map.Entry<Integer, Integer> entry: _reward_items.entrySet())
		{
			rewards.put(entry.getKey(), entry.getValue());
		}
		
		return rewards;
	}

	public void addRemoveItem(int item_id) {
		_remove_items.add(item_id);
	}
	
	public void distributeAll(Player player){
		
		player.setVar(PLAYER_VAR_SAVE, _id);
		
		HashMap<Integer, Integer> rewards = getRewards();
		
		// add cloak
		if(player.getClassId().isOfLevel(ClassLevel.AWAKED))
		{
			rewards.put(player.getClassId().getCloakId(), 1);
		}
		
		Set<Integer> allow_items = rewards.keySet();
		for(ItemInstance remove_item: player.getInventory().getItems())
		{
			int item_id = remove_item.getItemId();
			if(_all_used_items.contains(item_id) && !allow_items.contains(item_id))
				player.getInventory().removeItem(remove_item);
		}
		for(Map.Entry<Integer, Integer> entry: rewards.entrySet())
		{
			distributeItem(player, entry.getKey(), entry.getValue());
		}
		
		player.getInventory().store();
	}
	
	public static void distributeItem(Player player, int item_id, int amount)
	{
		long iventoryCount = player.getInventory().getCountOf(item_id);
		if (iventoryCount < amount)
			ItemFunctions.addItem(player, item_id, amount - iventoryCount, false);
		
		tryToUse(player, item_id);
	}
	
	public static void tryToUse(Player player, int item_id)
	{
		for(ItemInstance item: player.getInventory().getItemsByItemId(item_id))
		{
			useItem(player, item);
		}
	}
	
	public static void useItem(Player player, ItemInstance item)
	{
		if(isGear(item) && !item.isEquipped())
			player.useItem(item, false);
		else if(isShot(item) && !player.getAutoSoulShot().contains(item.getItemId()))
			player.addAutoSoulShot(item.getItemId());
	}
	
	public static boolean isGear(ItemInstance item)
	{
		return item.isWeapon() || item.isArmor() || item.isAccessory();
	}
	
	public static boolean isShot(ItemInstance item)
	{
		return item.getItemType() == EtcItemType.SOULSHOT 
				|| item.getItemType() == EtcItemType.SPIRITSHOT
				|| item.getItemType() == EtcItemType.BLESSED_SPIRITSHOT
				|| item.getItemId() == 6645
				|| item.getItemId() == 6646
				|| item.getItemId() == 6647
				|| item.getItemId() == 20332
				|| item.getItemId() == 20333
				|| item.getItemId() == 20334;
	}

	

	
}
