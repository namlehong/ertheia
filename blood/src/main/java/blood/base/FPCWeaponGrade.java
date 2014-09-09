package blood.base;

import java.util.ArrayList;
import java.util.List;

import javolution.util.FastMap;
import l2s.gameserver.templates.item.ItemGrade;
import l2s.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2s.commons.util.Rnd;

public class FPCWeaponGrade
{
	public static class FPCWeaponType 
	{
		private List<Integer> _items = new ArrayList<Integer>(); 

		public void addItem(int item_id)
		{
			_items.add(item_id);
		}
		
		public Integer getRandom()
		{
			return _items.get(Rnd.get(_items.size()));
		}
		
		public List<Integer> getAll()
		{
			return _items;
		}
	}
	
	private FastMap<WeaponType, FPCWeaponType> _map = new FastMap<WeaponType, FPCWeaponType>(); 
	private FPCWeaponType _mage = new FPCWeaponType();
	private static FastMap<ItemGrade, FPCWeaponGrade> _instances = new FastMap<ItemGrade, FPCWeaponGrade>();  
	
	public static FPCWeaponGrade getInstance(ItemGrade grade)
	{
		if(_instances.get(grade) == null)
		{
			_instances.put(grade, new FPCWeaponGrade());
		}
		
		return _instances.get(grade);
	}
	
	private FPCWeaponGrade()
	{
		
	}
	
	public FPCWeaponType getType(WeaponType type)
	{
		if(_map.get(type) == null)
		{
			_map.put(type, new FPCWeaponType());
		}
		return _map.get(type);
	}
	
	public FPCWeaponType getType(String type)
	{
		if(type.equalsIgnoreCase("mage"))
			return _mage;
		
		for(WeaponType _type: WeaponType.VALUES)
		{
			if(_type.toString().equalsIgnoreCase(type))
				return getType(_type);
		}
		
		return _mage;
	}
}
