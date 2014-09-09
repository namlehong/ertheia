package blood.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.database.DatabaseFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import blood.FPCInfo;

public class FakePlayerDAO {
	private static final Logger 		_log = LoggerFactory.getLogger(FakePlayerDAO.class);

	public static void loadStoredFPC() {
	    _log.info("storeFakePlayers: get data from DB");
	    Connection con = null;
	    try {
	        con = DatabaseFactory.getInstance().getConnection();
	        PreparedStatement stm = con.prepareStatement("SELECT obj_id FROM fpc");
	        ResultSet rs = stm.executeQuery();
	        while (rs.next()) {
	        	new FPCInfo(rs.getInt("obj_Id"));
	        }
	        rs.close();
	        stm.close();
	        
	    } catch (Exception e) {
	        _log.error("Fake Players Engine : Error while loading player: ", e);
	    } finally {
	        DbUtils.closeQuietly(con);
	    }
	}
	
	public static void addFPC(int obj_id){
		Connection con = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("INSERT INTO fpc(obj_id) VALUES (?)");
			statement.setInt(1, obj_id);
			statement.execute();
			statement.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbUtils.closeQuietly(con);
		}
	}
}
