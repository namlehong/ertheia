package l2s.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import l2s.commons.dbutils.DbUtils;
import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.Config;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.model.entity.residence.Castle;
import l2s.gameserver.model.entity.residence.ClanHall;
import l2s.gameserver.model.entity.residence.Fortress;
import l2s.gameserver.model.entity.residence.Residence;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.tables.ClanTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Iqman
 * @date 19:23/11.01.2012
 */
public class FreePremiumAccountsDao
{
	private static final Logger _log = LoggerFactory.getLogger(FreePremiumAccountsDao.class);

	private static List<String> _l = new ArrayList<String>();

	public static void LoadTable()
	{
		if(Config.DELETE_TABLE_ON_SERVER_START)
		{
			DeleteAllRecords();
			return;
		}
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			String _account = "";
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM pa_free_table");
			rset = statement.executeQuery();
			while(rset.next())
			{
				_account = rset.getString("account_name");
				if(!_account.equals(""))
				{
					_l.add(_account);
				}
			}
		}	
		catch(Exception e)
		{
			_log.info("ppc?");
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
		startSaveFreePA();
		_log.info("Free PA manager loaded size: "+_l.size()+"");	
	}

	public static void addAccount(String account)
	{
		_l.add(account);
	}

	public static List<String> getFreePAList() 
	{
		return _l;
	}

	public static void startSaveFreePA()
	{
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new SaveFreePA(), 60000, 60000);
	}

	public static class SaveFreePA extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{	
			SaveFreePA0();
		}
		
	}	

	private static void delRecords(boolean pernemant)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM pa_free_table");
			statement.execute();
		}
		catch(Exception e)
		{
			_log.error("", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
		if(!pernemant)
		{	
			for(String _account : _l)
			{
				savePAAccount(_account);
			}
		}	
	}

	public static void SaveFreePA0()
	{
		delRecords(false);
	}

	public static void DeleteAllRecords()
	{
		delRecords(true);
	}

	public static void savePAAccount(String acc)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO pa_free_table (account_name) VALUES(?)");
			statement.setString(1, acc);		
			statement.execute();
		}
		catch(Exception e)
		{
			//fuck the what don't care
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
		
	}
}
