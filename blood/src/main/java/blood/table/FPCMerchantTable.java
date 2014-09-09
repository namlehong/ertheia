package blood.table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2s.gameserver.data.xml.holder.MultiSellHolder;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.model.base.MultiSellEntry;

public class FPCMerchantTable
{
	private static final Logger 				_log 				= LoggerFactory.getLogger(FPCMerchantTable.class);
	private static ArrayList<MerchantItem>		_unused_item_list	= new ArrayList<MerchantItem>();
	private static ArrayList<MerchantItem>		_used_item_list		= new ArrayList<MerchantItem>();
	private static FPCMerchantTable 			_instance;
	private static List<MultiSellEntry> 		_sellList;
	
	public static FPCMerchantTable getInstance() 
	{
		if (_instance == null) 
		{
			_instance =
			new FPCMerchantTable();
		}
		return _instance;
    }
	 
	FPCMerchantTable()
	{
		GetMerchantItemListFromDB();
		GetSellableItemList();
	}
	
	public static void GetMerchantItemListFromDB()
	{
		Connection con = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
	        PreparedStatement stm = con.prepareStatement("SELECT * FROM fpc_merchant");
	        ResultSet rs = stm.executeQuery();
	        
	        while (rs.next()) 
	        {
	        	MerchantItem item = new MerchantItem(	rs.getInt("id"),
														rs.getInt("item_id"),
														rs.getInt("price"),
														rs.getInt("amount"),
														rs.getString("shop_title"),
														rs.getInt("owner"),
														rs.getString("status"),
														0);
	        	if(rs.getInt("owner") != 0)
		        	_used_item_list.add(item);
	        	else
	        		_unused_item_list.add(item );
	        	
	        }
	        
	        _log.info("Merchant List - Used Items: "+_used_item_list.size());
	        _log.info("Merchant List - Unused Items: "+_unused_item_list.size());
		}
		catch (Exception e) 
		{
			_log.error("Merchant Item List loading error.");
		}
		
	}
	
	public static void GetSellableItemList()
	{
		//get multisell list
		//400000000 is the id of the temp list in multisell: \data\multisell\400000000.xml that contains everything
		setSellableItemList(MultiSellHolder.getInstance().getList(400000000).getEntries()); 
		
	}
	
	public static MerchantItem getUnsedItem()
	{
		if(_unused_item_list.size()<1) return null;
		
		MerchantItem item = _unused_item_list.get(0);
		
		_unused_item_list.remove(0);
		_used_item_list.add(item);
		return item;
	}

	public static List<MultiSellEntry> getSellableItemList()
	{
		return _sellList;
	}

	public static void setSellableItemList(List<MultiSellEntry> _sellList)
	{
		if(_sellList == null)
		{
			_log.info("Sell List is null");
			return;
		}
		_log.info("Sell List Length: " + _sellList.size());
		FPCMerchantTable._sellList = _sellList;
	}
	
}
