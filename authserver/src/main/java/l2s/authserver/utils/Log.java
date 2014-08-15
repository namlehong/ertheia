package l2s.authserver.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;

import l2s.authserver.Config;
import l2s.authserver.accounts.Account;
import l2s.authserver.database.DatabaseFactory;
import l2s.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Log
{
	private final static Logger _log = LoggerFactory.getLogger(Log.class);
	
	public static void LogAccount(Account account)
	{
		if(!Config.LOGIN_LOG)
			return;
			
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO account_log (time, login, ip) VALUES(?,?,?)");
			statement.setInt(1, account.getLastAccess());
			statement.setString(2, account.getLogin());
			statement.setString(3, account.getLastIP());			
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
	}
}
