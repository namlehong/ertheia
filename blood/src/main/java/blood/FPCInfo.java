package blood;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.Experience;
//import l2s.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.items.TradeItem;
import l2s.gameserver.network.l2.s2c.PrivateStoreMsgBuy;
import l2s.gameserver.network.l2.s2c.PrivateStoreMsg;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Location;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import blood.ai.FPCDefaultAI;
import blood.base.FPCBase;
import blood.base.FPCParty;
import blood.base.FPCPveStyle;
import blood.base.FPCRole;
import blood.base.FPCSpawnStatus;
import blood.data.holder.FPItemHolder;
import blood.data.holder.FarmLocationHolder;
import blood.table.MerchantItem;
import blood.utils.ClassFunctions;
import blood.utils.LocationFunctions;
import blood.utils.MerchantFunctions;


public class FPCInfo
{
	private static final Object _lock = new Object();
	private static final Logger 		_log = LoggerFactory.getLogger(FPCInfo.class);
	// Main variables
	private Player _actor;
	private int _obj_id;
	private FPCSpawnStatus _status;
	private FPCRole	_role;
	private boolean _isMage;
	private ClassId _classId;
	
	private String	_shop_status = "none";
	private MerchantItem merchantItem;
	
	private int AILoopCount = 0;
	private FPCPveStyle _pveStyle = FPCPveStyle.PARTY;
	private FPCParty _party = null;
	private FPCDefaultAI _ai = null;
	
	private static FPCBase _instances = new FPCBase();
	
	public FPCInfo(int obj_id)
	{
		_obj_id = obj_id;
		setStatus(FPCSpawnStatus.OFFLINE);
		_instances.addInfo(this);
	}
	
	public FPCInfo(Player player)
	{
		int obj_id = player.getObjectId();
		_obj_id = obj_id;
		setStatus(FPCSpawnStatus.OFFLINE);
		_instances.addInfo(this);
		_actor = player;
	}
	
	public static FPCInfo getInstance(int obj_id)
	{
		return _instances.getPlayer(obj_id) != null ? _instances.getPlayer(obj_id) : new FPCInfo(obj_id);
	}
	
	public static FPCInfo getInstance(Player player)
	{
		int obj_id = player.getObjectId();
		return _instances.getPlayer(obj_id) != null ? _instances.getPlayer(obj_id) : new FPCInfo(player);
	}
	
	public Player getActor()
	{
		return _actor;
	}
	
	public int getObjectId()
	{
		return _obj_id;
	}
	
	public void setStatus(FPCSpawnStatus status)
	{
		//_log.info("set status function");
		if(_status == status)
			return;
		
		//_log.info(getActor()+": change status from " + _status + " to "+status);
		
		if(_status != null)
			_status.remove(this);
		
		//_log.info("BEFORE: _status " + _status + " status " + status);
		
		_status = status;
		
		//_log.info("AFTER: _status " + _status + " status " + status);
		
		if(_status != null)
			_status.add(this);
		
		switch(_status)
		{
		case OFFLINE:
			if(_role != null)
			{
				_role.remove(this);
				_role = null;
			}
			break;
		case ONLINE:
			setRole(FPCRole.IDLE);
			break;
		}
			
	}
	
	public FPCSpawnStatus getStatus()
	{
		return _status;
	}
	
	public void setRole(FPCRole role)
	{
		if(_role == role)
		{
//			_log.info(getActor()+": same old role "+role);
		}
		else
		{
//			_log.info(getActor()+": change role from " + _role + " to "+role);

			if(_role != null)
				_role.remove(this);
			
			_role = role;
			
			_role.add(this);
		}
		
		if(_role != null)
		{
			updateAI();
		}
	}
	
	public void updateAI()
	{
		Player player = getActor();
		FPCDefaultAI newAI = _role != null ? _role.getAI(player) : FPCRole.NEXUS_EVENT.getAI(player);
		setAI(newAI);
		if(_role == FPCRole.NEXUS_EVENT)
		{
			if(ClassFunctions.canPveSolo(player))
            {
	        	_pveStyle = FPCPveStyle.SOLO;
            }
            else
            {
            	setParty();
            }

		}
	}
	
	public FPCRole getRole()
	{
		return _role;
	}
	
	public boolean isMage()
	{
		return _isMage;
	}
	
	public ClassId getClassId()
	{
		return _classId;
	}
	
	public void setParty()
	{
		_party = FPCPartyManager.getInstance().getParty(this);;
	}
	
	public FPCParty getParty()
	{
		return _party;
	}
	
	public static void fullRestore(Player actor)
	{
		actor.setCurrentHpMp(actor.getMaxHp(), actor.getMaxMp());
		if(actor.isPlayer())
			actor.setCurrentCp(actor.getMaxCp());
	}
	
	public void teleToNextFarmZone()
	{
		if(_pveStyle == FPCPveStyle.PARTY)
			return;
		
		Player player = getActor();
		Location nextLoc;
		nextLoc = FarmLocationHolder.getInstance().getLocation(player);
		if(nextLoc != null)
			player.teleToLocation(nextLoc);
	}
	
	public void setAI(FPCDefaultAI ai)
	{
		Player actor = getActor();
		
		if(actor == null)
			return;
		
		_ai = ai;
					
		//if(ai instanceof MarketFPC) cancelShop();
		actor.setAI(_ai);
	}	
	
	@SuppressWarnings("unchecked")
	public void setAI(String ai)
	{
		Player actor = getActor();
		
		if(actor == null)
			return;
		
		Class<FPCDefaultAI> classAI = null;
		try {
			classAI = (Class<FPCDefaultAI>) Class.forName("blood.ai." + ai);
		}catch(Exception e){
			_log.error("Get ai class for ai: " + ai + ". FakePlayer: " + actor);
		}
		
		if(classAI == null)
			return;
		
		Constructor<FPCDefaultAI> constructorAI = (Constructor<FPCDefaultAI>)classAI.getConstructors()[0];
		try
		{
			setAI(constructorAI.newInstance(actor));
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			// Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public FPCDefaultAI getAI()
	{
		return _ai;
	}
	
	public void setPVEStyle(FPCPveStyle style)
	{
		_pveStyle = style;
	}
	
	public FPCPveStyle getPveStyle()
	{
		return _pveStyle;
	}
	
	public void spawn()
	{
		//_log.info("spawn function");
		Player player = null;
    	try{
    		player = Player.restore(getObjectId());
    		
    		if(player == null)
    		{
    			return;
    		}
    		
    		int MyObjectId = player.getObjectId();
    		Long MyStoreId = player.getStoredId();

    		synchronized (_lock)//TODO [G1ta0] че это за хуйня, и почему она тут
    		{
    			for(Player cha : GameObjectsStorage.getAllPlayersForIterate())
    			{
    				if(MyStoreId == cha.getStoredId())
    					continue;
    				try
    				{
    					if(cha.getObjectId() == MyObjectId)
    					{
    						_log.warn("Double EnterWorld for char: " + player.getName());
    						cha.kick();
    					}
    				}
    				catch(Exception e)
    				{
    					_log.error("", e);
    				}
    			}
    		}
    		
    		player.setOnlineStatus(true);
    		player.setNonAggroTime(Long.MAX_VALUE);
    		player.setNonPvpTime(System.currentTimeMillis() + Config.NONPVP_TIME_ONTELEPORT);
    		
            player.setFakePlayer();
            player.spawnMe();
    		player.setRunning();
    		player.standUp();
    		player.startTimers();
    		player.setHeading(Rnd.get(0, 9000));
            player.restoreExp();
            
            _isMage = player.isMageClass();
            _classId = player.getClassId();
            
            if(player.getLevel() < 20)
            {
            	int newLevel = Rnd.get(20, 85);
        		
            	Long exp_add = Experience.LEVEL[newLevel] - player.getExp();
            	player.addExpAndSp(exp_add, 0, true);
            	
            	ClassFunctions.upClass(player);
            	player.rewardSkills(false, false, true, false);
            }
            
            FPItemHolder.equip(player, true);
            
            if(player.isInRange(BloodConfig.FPC_CREATE_LOC, 500))
            	LocationFunctions.randomTown(player);
            
            // bind player to actor
            _actor = player;
            
            player.setOnlineStatus(true);
            player.broadcastCharInfo();
            
//            cancelShop();
            
            // set online status
            setStatus(FPCSpawnStatus.ONLINE);
    	}catch (Exception e) {
            _log.error("Fake Players Engine: Error loading player: " + player, e);
            if (player != null) {
                player.deleteMe();
            }
        }
	}
	
	public void cancelShop()
	{
		//if(_actor.getPrivateStoreType() == Player.STORE_PRIVATE_NONE)
		//	return;
		
		List<TradeItem> list = new CopyOnWriteArrayList<TradeItem>();
		list.clear();
        _actor.setPrivateStoreType(Player.STORE_PRIVATE_NONE);
        _actor.standUp();
        _actor.setSellList(false, list);
        _log.info("cancel shop, list " + list.size());
        _actor.setBuyList(list);
		
	}
	
	public void kick()
	{
		Player actor = getActor();
		if(actor == null)
			return;
		
		setStatus(FPCSpawnStatus.OFFLINE);
		
		if(actor.isInParty())
		{
			getParty().kick(actor);
		}
		
		_log.info(actor +": kicked");
		actor.kick();
	}
	
	public void setSellShop(MerchantItem item)
	{
		setSellShop(getActor(), item);
	}
	
	public void setSellShop(Player player, MerchantItem item)
	{
		//_log.info("player " + _actor + " item: " + item.getItemID() + " price: " + item.getPrice());
		if(item.getPrice() <= 0) 
			return;
		
		if(player == null)
			return;
		
		//save it for later references
		setMerchantItem(item);
		
		//check amount of item if available, if not, generate more
		ItemFunctions.addItem(player, item.getItemID(), item.getItemAmount() - ItemFunctions.getItemCount(player, item.getItemID()), false);
		ItemInstance sellItem = player.getInventory().getItemByItemId(item.getItemID());
		
		if(item.getShopTitle().isEmpty())
			item.setShopTitle(MerchantFunctions.generateShopTitle(sellItem.getTemplate().getName(), item.getPrice()));
		
		TradeItem tradeItem = new TradeItem(sellItem);
		List<TradeItem> list = new CopyOnWriteArrayList<TradeItem>();	
		
		tradeItem.setItemId(item.getItemID());
		tradeItem.setCount(item.getItemAmount());
		tradeItem.setOwnersPrice(item.getPrice());
		
		list.add(tradeItem);
    	
		if(!list.isEmpty())
		{
			
			player.setSellList(false, list);
			player.setSellStoreName(item.getShopTitle());
			player.saveTradeList();
			player.setPrivateStoreType(Player.STORE_PRIVATE_SELL);
			player.broadcastPacket(new PrivateStoreMsg(_actor));
			player.sitDown(null);
			player.broadcastCharInfo();
			
			//set owner for the MerchantItem, and write into db, table fpc_merchant
			item.setOwner(_obj_id);
			
		}
		//set the current character as shop, so stop asking it to do anything else
		setShopStatus(item.getStatus());
		
	}
	
	public void setBuyShop(MerchantItem item)
	{
		//save it for later references
		setMerchantItem(item);
		
		//add adena for buying
		_actor.addAdena(item.getPrice()*item.getItemAmount());
		
		TradeItem tradeItem = new TradeItem();
		List<TradeItem> list = new CopyOnWriteArrayList<TradeItem>();	
		
		
		tradeItem.setItemId(item.getItemID());
		tradeItem.setCount(item.getItemAmount());
		tradeItem.setOwnersPrice(item.getPrice());
		
		list.add(tradeItem);
		
		if(!list.isEmpty())
		{
			
			_actor.setBuyList(list);
			_actor.setBuyStoreName(item.getShopTitle());
			_actor.saveTradeList();
			_actor.setPrivateStoreType(Player.STORE_PRIVATE_BUY);
			_actor.broadcastPacket(new PrivateStoreMsgBuy(_actor));
			_actor.sitDown(null);
			_actor.broadcastCharInfo();
			
			//set owner for the MerchantItem, and write into db, table fpc_merchant
			item.setOwner(_obj_id);
			
		}
		//set the current character as shop, so stop asking it to do anything else
		setShopStatus(item.getStatus());
	}
		
	public MerchantItem getMerchantItem()
	{
		if(merchantItem == null)
		{
			//try to get from the Character Variables
			String rs = _actor.getVar("merchant_item");
			if(rs != null && !rs.isEmpty())
			{
				String[] choppedString = rs.split(";");
				merchantItem = new MerchantItem(Integer.parseInt(choppedString[0]),
												Integer.parseInt(choppedString[1]),
												Integer.parseInt(choppedString[2]),
												Integer.parseInt(choppedString[3]),
												choppedString[4],
												Integer.parseInt(choppedString[5]),
												choppedString[6],
												Long.parseLong(choppedString[7]));
			}
		}
		return merchantItem;
	}

	public void setMerchantItem(MerchantItem merchantItem)
	{
		if(merchantItem == null) return;
		
		if(merchantItem.getID() != -1)
			_actor.setVar("merchant_item", merchantItem.toString(), -1 );
	
		//_log.info("set Var: " + merchantItem.toString());
		this.merchantItem = merchantItem;
		
	}
	
	public String getShopStatus()
	{
		return _shop_status;
	}

	public void setShopStatus(String shop_status)
	{
		
		this._shop_status = shop_status;
	}
	
	public int getAILoopCount()
	{
		return AILoopCount;
	}

	public void increaseAILoopCount()
	{
		AILoopCount++;
	}
}
