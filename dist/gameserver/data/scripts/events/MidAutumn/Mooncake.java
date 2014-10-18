package events.MidAutumn;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import l2s.commons.util.Rnd;
import l2s.commons.math.random.RndSelector;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.reward.RewardData;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.utils.ItemFunctions;
import handler.items.ScriptItemHandler;

/**
 * Author: Hien Son
 */

public class Mooncake extends ScriptItemHandler
{
	
	protected static final RewardData[] MOONCAKE_REWARD = new RewardData[] {
			
			new RewardData(57, 50000, 100000, 50000), // 50% Adena
			new RewardData(1538, 1, 10, 10000), // 10% Blessed Scroll of Escape
			new RewardData(10649, 1, 5, 12500), // 12.5% Feather of Blessing
			new RewardData(13750, 5, 20, 10000), // 10% Warrior's Quick Healing Potion
			new RewardData(22038, 5, 10, 10000), // 10% Mana Potion
			
			//Apocalypse weapons - 14 records
			new RewardData(18137, 1, 1, 170), //0.17% Unidentified Apocalypse Shaper
			new RewardData(18138, 1, 1, 170), //0.17% Unidentified Apocalypse Cutter
			new RewardData(18139, 1, 1, 170), //0.17% Unidentified Apocalypse Slasher
			new RewardData(18140, 1, 1, 170), //0.17% Unidentified Apocalypse Avenger
			new RewardData(18141, 1, 1, 170), //0.17% Unidentified Apocalypse Fighter
			new RewardData(18142, 1, 1, 170), //0.17% Unidentified Apocalypse Stormer
			new RewardData(18143, 1, 1, 170), //0.17% Unidentified Apocalypse Thrower
			new RewardData(18144, 1, 1, 170), //0.17% Unidentified Apocalypse Shooter
			new RewardData(18145, 1, 1, 170), //0.17% Unidentified Apocalypse Buster
			new RewardData(18146, 1, 1, 170), //0.17% Unidentified Apocalypse Caster
			new RewardData(18147, 1, 1, 170), //0.17% Unidentified Apocalypse Retributer
			new RewardData(18148, 1, 1, 170), //0.17% Unidentified Apocalypse Dualsword
			new RewardData(18149, 1, 1, 170), //0.17% Unidentified Apocalypse Dual Dagger
			new RewardData(18150, 1, 1, 170), //0.17% Unidentified Apocalypse Dual Blunt Weapon
			
			//Twilight Armor - 17 records
			new RewardData(18162, 1, 1, 330), //0.33% Unidentified Twilight Circlet
			new RewardData(18152, 1, 1, 330), //0.33% Unidentified Twilight Breastplate 
			new RewardData(18163, 1, 1, 330), //0.33% Unidentified Twilight Tunic
			new RewardData(18166, 1, 1, 330), //0.33% Unidentified Twilight Shoes
			new RewardData(18159, 1, 1, 330), //0.33% Unidentified Twilight Leather Leggings
			new RewardData(18160, 1, 1, 330), //0.33% Unidentified Twilight Leather Gloves
			new RewardData(18161, 1, 1, 330), //0.33% Unidentified Twilight Leather Boots
			new RewardData(18153, 1, 1, 330), //0.33% Unidentified Twilight Gaiters
			new RewardData(18165, 1, 1, 330), //0.33% Unidentified Twilight Gloves
			new RewardData(18154, 1, 1, 330), //0.33% Unidentified Twilight Gauntlets
			new RewardData(18155, 1, 1, 330), //0.33% Unidentified Twilight Boots
			new RewardData(18164, 1, 1, 330), //0.33% Unidentified Twilight Stockings
			new RewardData(18158, 1, 1, 330), //0.33% Unidentified Twilight Leather Armor 
			new RewardData(18157, 1, 1, 330), //0.33% Unidentified Twilight Leather Helmet
			new RewardData(18167, 1, 1, 330), //0.33% Unidentified Twilight Sigil
			new RewardData(18151, 1, 1, 330), //0.33% Unidentified Twilight Helmet 
			new RewardData(18156, 1, 1, 330) //0.33% Unidentified Twilight Shield
	};
	
	private static final int MOONCAKE_ID = 90004; // Fortune Cube
	
	private static int[] _itemIds = {MOONCAKE_ID};
	protected static final RewardData[][] _dropItem = {MOONCAKE_REWARD};
	
	private static final int SUCCESS_VISUAL_EFF_ID = 5965;
	
	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(!playable.isPlayer())
			return false;
		Player activeChar = playable.getPlayer();
		RewardData[] drop_list = null;
		
		int itemId = item.getItemId();
		for(int i = 0; i < _itemIds.length; i++)
			if(_itemIds[i] == itemId)
			{
				drop_list = _dropItem[i];
				break;
			}
		
		if(drop_list == null)
			return false;

		if(!activeChar.getInventory().destroyItem(item, 1L))
			return false;

		Map<Integer, Long> items = new HashMap<Integer, Long>();
		
		getGroupItem(activeChar, drop_list, items);
		
		activeChar.sendPacket(SystemMessagePacket.removeItems(item.getItemId(), 1L));
		
		for(Entry<Integer, Long> e : items.entrySet())
			activeChar.sendPacket(SystemMessagePacket.obtainItems(e.getKey(), e.getValue(), 0));
		
		activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, SUCCESS_VISUAL_EFF_ID, 1, 500, 1500));
		
		return true;
	}

	public void getGroupItem(Player activeChar, RewardData[] dropData, Map<Integer, Long> report)
	{
		ItemInstance item;
		long count = 0;
		RndSelector<RewardData> reward_group = new RndSelector<RewardData>(); 
		
		for(RewardData d : dropData){
			reward_group.add(d, (int) d.getChance());
		}
		
		RewardData reward_item = reward_group.select();
		count = Rnd.get(reward_item.getMinDrop(), reward_item.getMaxDrop());
		item = ItemFunctions.createItem(reward_item.getItemId());
		item.setCount(count);
		activeChar.getInventory().addItem(item);
		Long old = report.get(reward_item.getItemId());
		report.put(reward_item.getItemId(), old != null ? old + count : count);
	}
}