package blood.table;

import java.sql.Connection;
import java.sql.PreparedStatement;

import l2s.gameserver.database.DatabaseFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MerchantItem
{
	private static final Logger 		_log = LoggerFactory.getLogger(MerchantItem.class);
	private int 		_db_id;
	private int 		_item_id;
	private long		_price;
	private long		_item_amount;
	private String		_shop_title;
	private int			_owner;
	private String		_status; //SELL, BUY, SOLD, BOUGHT, UNSOLD, UNBOUGHT
	private long		_time_start;
	
	public MerchantItem(int id, int item_id, int price, int item_amount,String shop_title,int owner,String status, long time_start)
	{
		setID(id);
		setItemID(item_id);
		setPrice(price);
		setItemAmount(item_amount);
		setShopTitle(shop_title);
		_owner = owner;
		_status = status;
		setTimeStart(time_start);
	}

	public int getItemID()
	{
		return _item_id;
	}

	public void setItemID(int _item_id)
	{
		this._item_id = _item_id;
	}

	public long getItemAmount()
	{
		return _item_amount;
	}

	public void setItemAmount(int _item_amount)
	{
		this._item_amount = _item_amount;
	}

	public String getShopTitle()
	{
		return _shop_title;
	}

	public void setShopTitle(String _shop_title)
	{
		this._shop_title = _shop_title;
	}

	public int getOwner()
	{
		return _owner;
	}

	public void setOwner(int _owner)
	{
		this._owner = _owner;
		//id=-1 meaning selling own item, doesn't have db record
		if(_db_id == -1) return;
		try
		{
			Connection con = null;
			con = DatabaseFactory.getInstance().getConnection();
	        PreparedStatement stm = con.prepareStatement("UPDATE fpc_merchant SET owner=? WHERE id=?");
	        stm.setInt(1, _owner);
	        stm.setInt(2, getID());
	        stm.execute();
		}
		catch (Exception e) 
		{
			_log.error("Cannot update  Merchant Item Owner:" + e.toString());
		}
	}

	public String getStatus()
	{
		return _status;
	}

	public void setStatus(String _status)
	{
		this._status = _status;
		//id=-1 meaning selling own item, doesn't have db record
		if(_db_id == -1) return;
		try
		{
			Connection con = null;
			con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement stm = con.prepareStatement("UPDATE fpc_merchant SET status=? WHERE id=?");
	        stm.setString(1,_status);
	        stm.setInt(2, getID());
	        stm.execute();
		}
		catch (Exception e) 
		{
			_log.error("Cannot update  Merchant Item Status: " + e.toString());
		}
		
	}

	public int getID()
	{
		return _db_id;
	}

	public void setID(int _db_id)
	{
		this._db_id = _db_id;
	}

	public long getPrice()
	{
		return _price;
	}

	public void setPrice(int _price)
	{
		this._price = _price;
	}
	
	public String toString()
	{
		String returnString = _db_id + ";" + 
							_item_id + ";" + 
							_price + ";" + 
							_item_amount + ";" + 
							_shop_title + ";" + 
							_owner + ";" + 
							_status + ";" +
							_time_start;
		
		return returnString;
		
	}

	public long getTimeStart()
	{
		return _time_start;
	}

	public void setTimeStart(long _time_start)
	{
		this._time_start = _time_start;
	}
	
}
    