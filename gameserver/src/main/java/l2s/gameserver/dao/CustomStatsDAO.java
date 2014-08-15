package l2s.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.CopyOnWriteArrayList;
import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.database.DatabaseFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Iqman
 * @date 00:42/14.05.2013
 */
public class CustomStatsDAO
{
	private static final Logger _log = LoggerFactory.getLogger(CustomStatsDAO.class);

	public static CopyOnWriteArrayList<StatInfo> info_monsters = new CopyOnWriteArrayList<StatInfo>();
	public static CopyOnWriteArrayList<StatInfo> info_raid = new CopyOnWriteArrayList<StatInfo>();
	
	public static void LoadCustomValues()
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM custom_stats_addon");
			rset = statement.executeQuery();
			while(rset.next())
			{
				int level = rset.getInt("level");
				double mulHP = rset.getDouble("mulHP");
				double mulMP = rset.getDouble("mulMP");
				double mulPAtkSpd = rset.getDouble("mulPAtkSpd");
				double mulMAtkSpd = rset.getDouble("mulMAtkSpd");
				double mulPAtk = rset.getDouble("mulPAtk");
				double mulMAtk = rset.getDouble("mulMAtk");
				double mulPDef = rset.getDouble("mulPDef");
				double mulMDef = rset.getDouble("mulMDef");
				
				int minAttrHPMP = rset.getInt("minAttrHPMP");
				int minAttrPAtkMAtk = rset.getInt("minAttrPAtkMAtk");
				int minAttrPatkMAtkSpd = rset.getInt("minAttrPatkMAtkSpd");
				int minAttrPDef = rset.getInt("minAttrPDef");
				int minAttrMDef = rset.getInt("minAttrMDef");
				
				int randomAddonPatkMatkSpd = rset.getInt("randomAddonPatkMatkSpd");
				int randomAddonPatkMAtk = rset.getInt("randomAddonPatkMAtk");
				int randomAddonPdef = rset.getInt("randomAddonPdef");
				int randomAddonMdef = rset.getInt("randomAddonMdef");
				boolean forRaid = rset.getInt("forRaid") == 1 ? true : false;
				
				StatInfo info = new StatInfo(level, mulHP, mulMP, mulPAtkSpd, mulMAtkSpd, mulPAtk, mulMAtk, mulPDef, mulMDef, minAttrHPMP, minAttrPAtkMAtk, minAttrPatkMAtkSpd, minAttrPDef, minAttrMDef, randomAddonPatkMatkSpd, randomAddonPatkMAtk, randomAddonPdef, randomAddonMdef);
				if(forRaid)
					info_raid.add(info);
				else
					info_monsters.add(info);
			}	
				
		}
		catch(Exception e)
		{
			_log.error("", e);
		}
		finally
		{
			
			DbUtils.closeQuietly(con, statement, rset);
			_log.info("Custom Mobs: Loaded with "+info_raid.size()+" raid stats");
			_log.info("Custom Mobs: Loaded with "+info_monsters.size()+" monster stats");
		}
	
	}
	
	public static StatInfo getRecord(int level, boolean isForRaid)
	{
		if(isForRaid)
		{
			for(StatInfo mobInfo : info_raid)
			{
				if(mobInfo.getLevel() == level)
					return mobInfo;
			}
		}
		else
		{
			for(StatInfo mobInfo : info_monsters)
			{
				if(mobInfo.getLevel() == level)
					return mobInfo;
			}		
		}
		return null;	
	}
	
	public static class StatInfo
	{
		public final int _level;
		
		public final double _mulHP;
		public final double _mulMP;
		public final double _mulPAtkSpd;
		public final double _mulMAtkSpd;
		public final double _mulPAtk;
		public final double _mulMAtk;
		public final double _mulPDef;
		public final double _mulMDef;
		
		public final int _minAttrHPMP;
		public final int _minAttrPAtkMAtk;
		public final int _minAttrPatkMAtkSpd;
		public final int _minAttrPDef;
		public final int _minAttrMDef;
		
		public final int _randomAddonPatkMatkSpd;
		public final int _randomAddonPatkMAtk;
		public final int _randomAddonPdef;
		public final int _randomAddonMdef;		
		
		public StatInfo(int level, double mulHP, double mulMP, double mulPAtkSpd, double mulMAtkSpd, double mulPAtk, double mulMAtk, double mulPDef, double mulMDef, int minAttrHPMP, int minAttrPAtkMAtk, int minAttrPatkMAtkSpd, int minAttrPDef, int minAttrMDef, int randomAddonPatkMatkSpd, int randomAddonPatkMAtk, int randomAddonPdef, int randomAddonMdef)
		{
			_level = level;
			_mulHP = mulHP;
			_mulMP = mulMP;
			_mulPAtkSpd = mulPAtkSpd;
			_mulMAtkSpd = mulMAtkSpd;
			_mulPAtk = mulPAtk;
			_mulMAtk = mulMAtk;
			_mulPDef = mulPDef;
			_mulMDef = mulMDef; 
			_minAttrHPMP = minAttrHPMP;
			_minAttrPAtkMAtk = minAttrPAtkMAtk;
			_minAttrPatkMAtkSpd = minAttrPatkMAtkSpd;
			_minAttrPDef = minAttrPDef;
			_minAttrMDef = minAttrMDef;
			_randomAddonPatkMatkSpd = randomAddonPatkMatkSpd;
			_randomAddonPatkMAtk = randomAddonPatkMAtk;
			_randomAddonPdef = randomAddonPdef;
			_randomAddonMdef = randomAddonMdef;
		}
		
		public int getLevel()
		{
			return _level;
		}
		public double getMultHP()
		{
			return _mulHP;
		}
		
		public int getMinAttrHPMP()
		{
			return _minAttrHPMP;
		}		
		
		public double getMultMatk()
		{
			return _mulMAtk;
		}

		public int getMinAttrpAtkmAtk()
		{
			return _minAttrPAtkMAtk;
		}

		public int getRandomAddonpAtkmAtk()
		{
			return _randomAddonPatkMAtk;
		}

		public double getMultMatkSpd()
		{
			return _mulMAtkSpd;
		}

		public int getMinAttrpAtkmAtkSpd()
		{
			return _minAttrPatkMAtkSpd;
		}

		public int getRandomAddonpAtkmAtkSpd()
		{
			return _randomAddonPatkMatkSpd;
		}

		public double getMultMdef()
		{
			return _mulMDef;
		}

		public int getMinAttrMdef()
		{
			return _minAttrMDef;
		}

		public int getRandomAddonMdef()
		{
			return _randomAddonMdef;
		}

		public double getMultMP()
		{
			return _mulMP;
		}
		
		public double getMultPatk()
		{
			return _mulPAtk;
		}		
		public double getMultPatkSpd()
		{
			return _mulPAtkSpd;
		}	
		public double getMultPdef()
		{
			return _mulPDef;
		}	
		public int getMinAttrPdef()
		{
			return _minAttrPDef;
		}	
		public int getRandomAddonPdef()
		{
			return _randomAddonPdef;
		}	
	}	
	
}
