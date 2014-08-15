package l2s.gameserver.instancemanager;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import l2s.gameserver.dao.CommissionItemsDAO;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.CommissionItem;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.items.PcInventory;
import l2s.gameserver.network.l2.components.IStaticPacket;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExResponseCommissionBuyInfo;
import l2s.gameserver.network.l2.s2c.ExResponseCommissionBuyItem;
import l2s.gameserver.network.l2.s2c.ExResponseCommissionList;
import l2s.gameserver.utils.CommissionUtils;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bonux
 */
public class CommissionManager
{
	private static final Logger _log = LoggerFactory.getLogger(CommissionManager.class);

	private static final int MIN_ID = 1;
	private static final int MAX_ID = Integer.MAX_VALUE; //TODO

	//Лимит зарегистрированных предметов для одного игрока.
	private static final int MAX_BIDS_COUNT = 10;
	private static final int MAX_BIDS_GM_COUNT = 99999;

	private static final CommissionManager _instance = new CommissionManager();

	/** Блокировка для чтения/записи вещей из списка и внешних операций */
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock readLock = lock.readLock();
	private final Lock writeLock = lock.writeLock();

	private TIntObjectMap<CommissionItem> _commissionItems = new TIntObjectHashMap<CommissionItem>();

	public static CommissionManager getInstance()
	{
		return _instance;
	}

	public CommissionManager()
	{
		//
	}

	public final void writeLock()
	{
		writeLock.lock();
	}

	public final void writeUnlock()
	{
		writeLock.unlock();
	}

	public final void readLock()
	{
		readLock.lock();
	}

	public final void readUnlock()
	{
		readLock.unlock();
	}

	public void init()
	{
		_commissionItems = CommissionItemsDAO.getInstance().restore();
		_log.info("CommissionManager: Loaded " + _commissionItems.size() + " items in commission.");
	}

	public CommissionItem getCommissionItem(int id)
	{
		readLock();
		try
		{
			return _commissionItems.get(id);
		}
		finally
		{
			readUnlock();
		}
	}

	public CommissionItem[] getCommissionItems()
	{
		readLock();
		try
		{
			return _commissionItems.values(new CommissionItem[_commissionItems.size()]);
		}
		finally
		{
			readUnlock();
		}
	}

	public boolean containsCommissionItem(int id)
	{
		readLock();
		try
		{
			return _commissionItems.containsKey(id);
		}
		finally
		{
			readUnlock();
		}
	}

	public boolean addCommissionItem(CommissionItem item)
	{
		writeLock();
		try
		{
			if(containsCommissionItem(item.getCommissionId())) //На всякий случей.
				return false;

			if(!CommissionItemsDAO.getInstance().insert(item))
				return false;

			_commissionItems.put(item.getCommissionId(), item);
			return true;
		}
		finally
		{
			writeUnlock();
		}
	}

	public void deleteCommissionItem(int id)
	{
		writeLock();
		try
		{
			_commissionItems.remove(id);
			CommissionItemsDAO.getInstance().delete(id);
		}
		finally
		{
			writeUnlock();
		}
	}

	public void updateBid(int bid, int id)
	{
		writeLock();
		try
		{
			if(!containsCommissionItem(id))
				return;
			CommissionItemsDAO.getInstance().updateBid(bid, id);
		}
		finally
		{
			writeUnlock();
		}
	}

	public int getFreeId()
	{
		writeLock();
		try
		{
			//TODO: [Bonux] Пересмотреть.
			for(int newId = MIN_ID; newId <= MAX_ID; newId++)
			{
				if(containsCommissionItem(newId))
					continue;
				return newId;
			}
			return -1; //Врядли такое произойдет.
		}
		finally
		{
			writeUnlock();
		}
	}

	public IStaticPacket registerItem(Player player, int itemObjId, long itemPrice, long itemCount, int periodDays)
	{
		writeLock();
		try
		{
			CommissionItem[] items = getMyCommissionItems(player.getObjectId());
			if(items.length >= getMaxBidsCount(player))
				return SystemMsg.FAILED_TO_REGISTER_ITEM;

			PcInventory inventory = player.getInventory();
			inventory.writeLock();
			try
			{
				ItemInstance item = inventory.getItemByObjectId(itemObjId);
				if(item == null)
					return SystemMsg.REGISTRATION_IS_NOT_AVAILABLE_BECAUSE_THE_CORRESPONDING_ITEM_DOES_NOT_EXIST;

				if(!item.canBeComissioned(player))
					return SystemMsg.ITEMS_THAT_CANNOT_BE_EXCHANGEDDROPPEDUSE_A_PRIVATE_STORE_OR_THAT_ARE_FOR_A_LIMITED_PERIODAUGMENTING_CANNOT_BE_REGISTERED;

				CommissionItem commissionItem = new CommissionItem(item, player.getObjectId(), player.getName(), getFreeId(), itemPrice, periodDays);
				commissionItem.setCount(itemCount);
				if(!addCommissionItem(commissionItem))
					return SystemMsg.FAILED_TO_REGISTER_ITEM;

				ItemFunctions.removeItem(player, item, itemCount, false);

				Log.LogItem(player, Log.CommissionRegistered, item, itemCount);
			}
			finally
			{
				inventory.writeUnlock();
			}
			return null;
		}
		finally
		{
			writeUnlock();
		}
	}

	public IStaticPacket unregisterItem(Player player, int bidId)
	{
		writeLock();
		try
		{
			if(player.getWeightPenalty() >= 3 || player.getInventoryLimit() * 0.8 < player.getInventory().getSize())
				return SystemMsg.IF_THE_WEIGHT_IS_80_OR_MORE_AND_THE_INVENTORY_NUMBER_IS_90_OR_MORE_PURCHASECANCELLATION_IS_NOT_POSSIBLE;

			CommissionItem commissionItem = getCommissionItem(bidId);
			if(commissionItem == null)
				return SystemMsg.CURRENTLY_THERE_ARE_NO_REGISTERED_ITEMS;

			if(commissionItem.getOwnerId() != player.getObjectId())
				return SystemMsg.CURRENTLY_THERE_ARE_NO_REGISTERED_ITEMS; //TODO: [Bonux] Найти другое сообщение.

			deleteCommissionItem(bidId);

			ItemInstance item = CommissionUtils.getItemFromCommissionItem(commissionItem);
			player.getInventory().addItem(item);

			Log.LogItem(player, Log.CommissionUnregister, item);
			return null;
		}
		finally
		{
			writeUnlock();
		}
	}

	public IStaticPacket buyItem(Player player, int bidId)
	{
		writeLock();
		try
		{
			if(player.getWeightPenalty() >= 3 || player.getInventoryLimit() * 0.8 < player.getInventory().getSize())
				return SystemMsg.IF_THE_WEIGHT_IS_80_OR_MORE_AND_THE_INVENTORY_NUMBER_IS_90_OR_MORE_PURCHASECANCELLATION_IS_NOT_POSSIBLE;

			CommissionItem commissionItem = getCommissionItem(bidId);
			if(commissionItem == null)
				return SystemMsg.ITEM_PURCHASE_IS_NOT_AVAILABLE_BECAUSE_THE_CORRESPONDING_ITEM_DOES_NOT_EXIST;

			if(commissionItem.getOwnerId() == player.getObjectId())
				return SystemMsg.ITEM_PURCHASE_HAS_FAILED;

			long commissionPrice = commissionItem.getCommissionPrice();
			if(player.getAdena() < commissionPrice)
				return SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA;

			deleteCommissionItem(bidId);

			ItemInstance item = CommissionUtils.getItemFromCommissionItem(commissionItem);
			player.getInventory().addItem(item);
			player.reduceAdena(commissionPrice);
			CommissionUtils.sendBuyMailToOwner(player, commissionItem);
			player.sendPacket(new ExResponseCommissionBuyItem(item.getItemId(), item.getCount()));

			Log.LogItem(player, Log.CommissionBuy, item);
			Log.LogItem("Player", commissionItem.getOwnerId(), Log.CommissionSell, item);
			return null;
		}
		finally
		{
			writeUnlock();
		}
	}

	private CommissionItem[] getMyCommissionItems(int playerObjectId)
	{
		List<CommissionItem> items = new ArrayList<CommissionItem>();
		for(CommissionItem item : getCommissionItems())
		{
			if(item.getOwnerId() == playerObjectId)
				items.add(item);
		}

		CommissionItem[] itemsArray = items.toArray(new CommissionItem[items.size()]);
		Arrays.sort(itemsArray, new Comparator<CommissionItem>(){
			@Override
			public int compare(CommissionItem o1, CommissionItem o2)
			{
				return o1.getRegisterDate() - o2.getRegisterDate(); //Сортируем от самых старых до новых.
			}
		});
		return itemsArray;
	}

	private CommissionItem[] getCommissionItems(int tree, int type, int quality, int grade, String searchWords)
	{
		List<CommissionItem> items = new ArrayList<CommissionItem>();
		for(CommissionItem item : getCommissionItems())
		{
			if(CommissionUtils.checkItem(item, tree, type, quality, grade, searchWords))
				items.add(item);
		}

		CommissionItem[] itemsArray = items.toArray(new CommissionItem[items.size()]);
		Arrays.sort(itemsArray, new Comparator<CommissionItem>(){
			@Override
			public int compare(CommissionItem o1, CommissionItem o2)
			{
				return o2.getRegisterDate() - o1.getRegisterDate(); //Сортируем от самых новых до старых.
			}
		});
		return itemsArray;
	}

	public void sendMyCommissionList(Player player)
	{
		player.sendPacket(new ExResponseCommissionList(ExResponseCommissionList.MY_COMMISSION_LIST, getMyCommissionItems(player.getObjectId())));
	}

	public void sendCommissionBuyInfo(Player player, int bidId)
	{
		CommissionItem item = getCommissionItem(bidId);
		if(item == null)
			return;

		player.sendPacket(new ExResponseCommissionBuyInfo(item));
	}

	public void sendCommissionList(Player player, int tree, int type, int quality, int grade, String searchWords)
	{
		player.sendPacket(new ExResponseCommissionList(ExResponseCommissionList.COMMON_COMMISSION_LIST, getCommissionItems(tree, type, quality, grade, searchWords)));
	}

	private static int getMaxBidsCount(Player player)
	{
		if(player.isGM())
			return MAX_BIDS_GM_COUNT;
		return MAX_BIDS_COUNT;
	}

	public void returnExpiredItems()
	{
		writeLock();
		try
		{
			for(CommissionItem item : getCommissionItems())
			{
				if(item.getEndPeriodDate() >= ((int) (System.currentTimeMillis() / 1000L) + 60))
					continue;

				deleteCommissionItem(item.getCommissionId());
				CommissionUtils.sendReturnMailToOwner(item);
			}
		}
		finally
		{
			writeUnlock();
		}
	}
}