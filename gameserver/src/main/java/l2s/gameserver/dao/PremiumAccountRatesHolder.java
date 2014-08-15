package l2s.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.database.DatabaseFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Iqman
 * @date 00:42/16.013.2012
 */
public class PremiumAccountRatesHolder
{
	private static final Logger _log = LoggerFactory.getLogger(PremiumAccountRatesHolder.class);

	public static CopyOnWriteArrayList<PremiumInfo> all_info = new CopyOnWriteArrayList<PremiumInfo>();
	
	public static void loadLists()
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM premium_account_table");
			rset = statement.executeQuery();
			while(rset.next())
			{
				int grp_id = rset.getInt("groupId");
				String grp_name_ru = rset.getString("groupName_ru");
				String grp_name_en = rset.getString("groupName_en");
				double exp = rset.getDouble("exp");
				double sp = rset.getDouble("sp");
				double adena = rset.getDouble("adena");
				double drop = rset.getDouble("drop");
				double spoil = rset.getDouble("spoil");
				double qDrop = rset.getDouble("qdrop");
				double qReward = rset.getDouble("qreward");
				int delay = rset.getInt("delay");
				boolean isHours = rset.getInt("isHours") == 1 ? true : false;
				int itemId = rset.getInt("itemId");
				long itemCount = rset.getLong("itemCount");
				double enchant_add = rset.getDouble("enchant_add");
				PremiumInfo premium_info = new PremiumInfo(grp_id, grp_name_ru, grp_name_en, exp, sp, adena, drop, spoil, qDrop, qReward, delay, isHours, itemId, itemCount, enchant_add);
				all_info.add(premium_info);
			}	
				
		}
		catch(Exception e)
		{
			_log.error("", e);
		}
		finally
		{
			
			DbUtils.closeQuietly(con, statement, rset);
			_log.info("Premium Info: Loaded with "+all_info.size()+" premium options");
		}
	
	}

	public static class PremiumInfo
	{
		public final int _groupId;
		public final String _groupNameRu;
		public final String _groupNameEn;
		public final double _exp;
		public final double _sp;
		public final double _adena;
		public final double _drop;
		public final double _spoil;
		public final double _qdrop;
		public final double _qreward;
		public final int _delay;
		public final boolean _isHours;
		public final int _itemId;
		public final long _itemCount;
		public final double _enchant_add;
		

		public PremiumInfo(int groupId, String groupNameRu, String groupNameEn, double exp, double sp, double adena, double drop, double spoil, double qdrop, double qreward, int delay, boolean isHours, int itemId, long itemCount, double enchant_add)
		{
			_groupId = groupId;
			_groupNameRu = groupNameRu;
			_groupNameEn = groupNameEn;
			_exp = exp;
			_sp = sp;
			_adena = adena;
			_drop = drop;
			_spoil = spoil; 
			_qdrop = qdrop;
			_qreward = qreward;
			_delay = delay;
			_isHours = isHours;
			_itemId = itemId;
			_itemCount = itemCount;
			_enchant_add = enchant_add;
		}

		public int getGroupNumber()
		{
			return _groupId;
		}

		public String getGroupNameRu()
		{
			if(_groupNameRu == null)
				return "";
				
			return _groupNameRu;
		}

		public String getGroupNameEn()
		{
			if(_groupNameEn == null)
				return "";
			return _groupNameEn;
		}		

		public double getExp()
		{
			if(_exp < 1.)
				return 1.;
			return _exp;
		}	

		public double getSp()
		{
			if(_sp < 1.)
				return 1.;
			return _sp;
		}

		public double getAdena()
		{
			if(_adena < 1.)
				return 1.;
				
			return _adena;
		}

		public double getDrop()
		{
			if(_drop < 1.)
				return 1.;
				
			return _drop;
		}

		public double getSpoil()
		{
			if(_spoil < 1.)
				return 1.;
				
			return _spoil;
		}

		public double getQDrop()
		{
			if(_qdrop < 1.)
				return 1.;
			return _qdrop;
		}

		public double getQReward()
		{
			if(_qreward < 1.)
				return 1.;
				
			return _qreward;
		}

		public int getDays()
		{
			if(_delay < 1)
				return 0;
				
			return _delay;
		}

		public boolean isHours()
		{
			return _isHours;
		}

		public int getItemId()
		{
			return _itemId;
		}

		public long getItemCount()
		{
			if(_itemCount <= 0)
				return 1;

			return _itemCount;
		}
		public double getEnchantAdd()
		{
			return _enchant_add;
		}
	}

	public static CopyOnWriteArrayList<PremiumInfo> getAllAquisions()
	{
		if(all_info == null)
			return null;
		return all_info;
	}

	public static boolean getIfDelayHours(int groupId)
	{
		for(PremiumInfo info : all_info)
		{
			if(info._groupId == groupId)
			{
				return info._isHours;
			}
		}
		return true;
	}

	public static boolean validateGroup(int groupId, int period, int itemId, int itemCount)
	{
		boolean validated = false;
		for(PremiumInfo info : all_info)
		{
			if(info._groupId == groupId)
			{
				if(info._delay != period || info._itemId != itemId || info._itemCount != itemCount)
					return false;
				else
					validated = true;
			}
		}
		if(!validated)
			return false;
		return true;
	}

	public static PremiumInfo getPremiumByGroupId(int groupId)
	{
		for(PremiumInfo info : all_info)
		{
			if(info._groupId == groupId)
			{
				return info;
			}	
		}
		return null;	
	}
}
