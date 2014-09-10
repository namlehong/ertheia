package blood.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.database.DatabaseFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FakeNameDAO {
	private static final Logger _log = LoggerFactory.getLogger(FakeNameDAO.class);

	private static FakeNameDAO _instance ;
	
	public static FakeNameDAO getInstance()
	{
		if(_instance == null)
		{
			_instance = new FakeNameDAO();
		}
		return _instance;
	}
	
	private FakeNameDAO()
	{
		// TODO
	}
	
	public String getName()
	{
		return getName(1).get(0);
	}
	
	public List<String> getName(int limit)
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		List<String> list_name = new ArrayList<String>();
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT char_name FROM fake_name WHERE is_used = ?  ORDER BY RAND() LIMIT ?");
			statement.setInt(1, 0);
			statement.setInt(2, limit);
			rset = statement.executeQuery();
			while(rset.next())
			{
				list_name.add(rset.getString("char_name"));
			}
		}
		catch(final Exception e)
		{
			_log.error("", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
		return list_name;
	}
	
	public void useName(String char_name)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE fake_name SET is_used = ? WHERE char_name = ?");
			statement.setInt(1, 1);
			statement.setString(2, char_name);
			statement.execute();
			
		}
		catch(final Exception e)
		{
			_log.error("error on query ad update used name:"+char_name, e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}
}
