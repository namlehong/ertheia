package blood.ai;

import java.util.List;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.MultiSellEntry;
import l2s.gameserver.model.base.MultiSellIngredient;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.items.TradeItem;
import l2s.gameserver.utils.ItemFunctions;
import blood.FPCInfo;
import blood.table.FPCMerchantTable;
import blood.table.MerchantItem;
import blood.utils.LocationFunctions;


public class MarketFPC extends FPCDefaultAI
{
	
	public MarketFPC(Player actor)
	{
		super(actor);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onEvtThink()
	{
		thinkActive();
	}
	
	@Override
	protected void thinkActive()
	{
		debug("fpcmarket think active");
		Player 	actor 		= getActor();
		
		//if (actor.isActionsDisabled())
			//return;
		
		FPCInfo player		= FPCInfo.getInstance(actor);
		
		if(player == null)
		{
			debug("actor " + actor);
			debug("player null");
			return;
		}
		
		debug("player not null");
		
		player.increaseAILoopCount();
		
		if(player.getAILoopCount() < 50) return;
		
		if(player.getShopStatus() != null && player.getShopStatus().equalsIgnoreCase("noshop"))
		{
			debug("no shop, stop checking");
			return;
		}
		
		//get the current buylist and selllist
		List<TradeItem> buyList = actor.getBuyList();
		List<TradeItem> sellList = actor.getSellList();
		
		//check if there is any shop on going
		if(!buyList.isEmpty() || !sellList.isEmpty())
		{
			//_log.info("player " + actor + " ongoing shop, buylist " + buyList.size() + " selllist " + sellList.size());
			return;
		}
		
		debug("no ongoing shop");
		
		if(player.getShopStatus().equalsIgnoreCase("sell") || player.getShopStatus().equalsIgnoreCase("buy"))
		{
			//this is the indication that shop has just finished buying/selling
			//purge the data of the recently sold/bought items
			MerchantItem prevItem  = player.getMerchantItem(); 
			
			 //check in case getID() == -1, mean FPC sold its own items, so no item destroying needed. 
			if(prevItem != null  && prevItem.getID() != -1)
			{
				if(prevItem.getStatus().equalsIgnoreCase("sell"))
				{
					_log.info("Shop finished, item sold.");
					prevItem.setStatus("sold");
					//clear the amount of adena has just gained via selling
					actor.reduceAdena(prevItem.getPrice()*prevItem.getItemAmount());
					
				}
				if(prevItem.getStatus().equalsIgnoreCase("buy"))
				{
					_log.info("Shop finished, item bought.");
					prevItem.setStatus("bought");
					//delete the item that has just bought
					ItemFunctions.removeItem(actor, prevItem.getItemID(), prevItem.getItemAmount(), false);
				}
				
			}
			actor.setPrivateStoreType(Player.STORE_PRIVATE_NONE);
			//actor.standUp();
			_log.info("close shop, kick player " + actor.getName());
			actor.broadcastCharInfo();
			player.setMerchantItem(null);
			player.setShopStatus("none");
			player.kick();
			return;
			
		}
		debug("no recent shop");
		MerchantItem item = null;
		
		//try to get previous unfinished shop session, if there is any. 
		//this case happens when character re-logs
		item = player.getMerchantItem();
		
		//if the previous MerchantItem has expired the 72 hours limit, then nullify it
		if(item!=null && (item.getTimeStart() + 259200000L) < System.currentTimeMillis())
		{
			if(item.getStatus().equalsIgnoreCase("sell"))
			{
				//remove the added item for sale
				ItemFunctions.removeItem(actor, item.getItemID(), item.getItemAmount(), false);
				item.setStatus("unsold");
			}
			
			if(item.getStatus().equalsIgnoreCase("buy"))
			{
				//remove the added amount of adena for buying
				actor.reduceAdena(item.getPrice()*item.getItemAmount());
				item.setStatus("unbought");
			}
			
			item = null;
		}
		debug("no unsold/bought shop");
		if(item == null)
		{
			//if there was no unfinished shop
			//get new shop from the Merchant List
			item = FPCMerchantTable.getUnsedItem();
			
			if(item == null) 
			{
				//in case there is no more item left in the list, sell what having in the inventory
				item = getInventorySellItem();
				
				//if there is even no item sellable in the inventory, it's time to give up
				if(item == null)
				{
					//sell random Chest level 1
					//MerchantItem(int id, int item_id, int price, int item_amout,String shop_title,int owner,String status, long time_start)
					int item_id 			= 40000;
					int price 				= 50 + 10 - Rnd.get(20);
					int item_count			= 5 + Rnd.get(5);
					_log.info("item " + item_id + " price " + price);
					item = new MerchantItem(-1, item_id, price, item_count, "", 0, "sell",0);
				}
			}
			
			//mark the time start setting shop
			item.setTimeStart(System.currentTimeMillis()); 
		}
		
		//teleport to random town
		LocationFunctions.randomTown(actor);
		
		String itemStatus = item.getStatus();
		
		switch(itemStatus.toLowerCase())
		{
			case "sell":
				player.setSellShop(item);				
			break;
			case "buy":
				player.setBuyShop(item);
			break;
		}
		
		
	}
	
	private MerchantItem getInventorySellItem()
	{
		Player 	actor 		= getActor();
		
		MerchantItem item = null;
		
		//get multisell list
		List<MultiSellEntry> sellList = FPCMerchantTable.getSellableItemList(); 
		
		if(sellList.isEmpty() || sellList == null)
		{
			_log.info("Multisell list 400000000 is empty.");
			return null;
		}
		
		ItemInstance[] inventoryList = actor.getInventory().getItems();
		
		if(inventoryList.length == 0)
		{
			_log.info("Inventory is empty");
			return null;
		}
		
		for(int i =0;i<inventoryList.length;i++)
		{
			//_log.info("inventoryList" + inventoryList[i].getName() + " isEquipped() " + inventoryList[i].isEquipped());
			if(!inventoryList[i].isEquipped()) //make sure we don't sell what we're wearing
			{
				//_log.info(inventoryList[i].getName() + " is not equipped");
				ItemInstance invItem = inventoryList[i];
				//loop though sellist to check if the item has its price set
				for(int j=0;j< sellList.size();j++)
				{
					MultiSellIngredient productionEntry = sellList.get(j).getProduction().get(0);
					MultiSellIngredient ingredientEntry = sellList.get(j).getIngredients().get(0);
					
					debug("check item " + productionEntry.getItemId() + " price of item " + ingredientEntry.getItemId() + " of value " + ingredientEntry.getItemCount());
					
					//also need to check if that item is sold using Adena unit
					if(invItem.getItemId() == productionEntry.getItemId() && ingredientEntry.getItemId() == 57 && Rnd.chance(30))
					{
						_log.info("check item " + productionEntry.getItemId() + " price of item " + ingredientEntry.getItemId() + " of value " + ingredientEntry.getItemCount());
						//MerchantItem(int id, int item_id, int price, int item_amout,String shop_title,int owner,String status, long time_start)
						int item_id 			= invItem.getItemId();
						
						double ratio 			= (Rnd.get(80,100));
						
						if(invItem.getEnchantLevel() > 0)
						{
							if(invItem.getEnchantLevel() <= 3)
							{
								ratio = 100;
							}
							else
							{
								int unsafe_modifier = invItem.getEnchantLevel() - 3; 
								ratio = 100 + 30*unsafe_modifier;
							}
						}
						
						
						int price 				= (int) Math.ceil(ingredientEntry.getItemCount()*ratio)/100;
						int item_count			= (int) invItem.getCount(); //sell every piece(s) we've got
						
						//_log.info("item: " + productionEntry.getItemId() + " price: " + ingredientEntry.getItemCount() + " calculated price: " + price + " ratio: " +ratio);
						
						item = new MerchantItem(-1, item_id, price, item_count, "", 0, "sell",0);
						
						//stop looping and return the recently found item
						return item;
					}
				}
			}
		}
		
		return item;
	}
	
}
