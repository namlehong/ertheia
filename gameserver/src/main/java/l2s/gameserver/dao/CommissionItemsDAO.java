package l2s.gameserver.dao;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.model.items.CommissionItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bonux
 */
public class CommissionItemsDAO
{
	private static final Logger _log = LoggerFactory.getLogger(CommissionItemsDAO.class);
	private static final CommissionItemsDAO _instance = new CommissionItemsDAO();

	public static final String SELECT_SQL_QUERY = "SELECT * FROM `commission_items`";
	public static final String DELETE_SQL_QUERY = "DELETE FROM `commission_items` WHERE id=?";
	public static final String INSERT_SQL_QUERY = "INSERT INTO `commission_items` (id, owner_id, item_id, item_price, count, period_days, register_date, enchant_level, attribute_fire, attribute_water, attribute_wind, attribute_earth, attribute_holy, attribute_unholy) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	public static final String UPDATE_BID_SQL_QUERY = "UPDATE `commission_items` SET item_bid=? WHERE id=?";

	public static CommissionItemsDAO getInstance()
	{
		return _instance;
	}

	public TIntObjectHashMap<CommissionItem> restore()
	{
		TIntObjectHashMap<CommissionItem> result = new TIntObjectHashMap<CommissionItem>();
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(SELECT_SQL_QUERY);
			rset = statement.executeQuery();
			while(rset.next())
			{
				CommissionItem item = new CommissionItem(rset.getInt("owner_id"));
				item.setCommissionId(rset.getInt("id"));
				item.setItemId(rset.getInt("item_id"));
				item.setCommissionPrice(rset.getLong("item_price"));
				item.setCount(rset.getLong("count"));
				item.setPeriodDays(rset.getInt("period_days"));
				item.setRegisterDate(rset.getInt("register_date"));
				item.setEnchantLevel(rset.getInt("enchant_level"));
				item.setAttributeFire(rset.getInt("attribute_fire"));
				item.setAttributeWater(rset.getInt("attribute_water"));
				item.setAttributeWind(rset.getInt("attribute_wind"));
				item.setAttributeEarth(rset.getInt("attribute_earth"));
				item.setAttributeHoly(rset.getInt("attribute_holy"));
				item.setAttributeUnholy(rset.getInt("attribute_unholy"));
				result.put(item.getCommissionId(), item);
			}
		}
		catch(Exception e)
		{
			_log.info("CommissionItemsDAO.restore: " + e, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
		return result;
	}

	public void delete(int id)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(DELETE_SQL_QUERY);
			statement.setInt(1, id);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.info("CommissionItemsDAO.delete(int): " + e, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public boolean insert(CommissionItem item)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(INSERT_SQL_QUERY);
			statement.setInt(1, item.getCommissionId());
			statement.setInt(2, item.getOwnerId());
			statement.setInt(3, item.getItemId());
			statement.setLong(4, item.getCommissionPrice());
			statement.setLong(5, item.getCount());
			statement.setInt(6, item.getPeriodDays());
			statement.setInt(7, item.getRegisterDate());
			statement.setInt(8, item.getEnchantLevel());
			statement.setInt(9, item.getAttributeFire());
			statement.setInt(10, item.getAttributeWater());
			statement.setInt(11, item.getAttributeWind());
			statement.setInt(12, item.getAttributeEarth());
			statement.setInt(13, item.getAttributeHoly());
			statement.setInt(14, item.getAttributeUnholy());
			statement.execute();
		}
		catch(Exception e)
		{
			_log.info("CommissionItemsDAO.insert(CommissionItem): " + e, e);
			return false;
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
		return true;
	}

	public void updateBid(int bid, int id)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(UPDATE_BID_SQL_QUERY);
			statement.setInt(1, bid);
			statement.setInt(2, id);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.info("CommissionItemsDAO.updateBid(int, int): " + e, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}
}
