package databuilder;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Comparator;

import l2s.gameserver.Config;
import l2s.gameserver.data.xml.parser.BaseStatsBonusParser;
import l2s.gameserver.data.xml.parser.ItemParser;
import l2s.gameserver.data.xml.parser.LevelBonusParser;
import l2s.gameserver.data.xml.parser.OptionDataParser;
import l2s.gameserver.data.xml.parser.VariationDataParser;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.templates.item.ItemTemplate;
import databuilder.xml.builder.ItemBuilder;
import databuilder.xml.builder.NpcBuilder;
import databuilder.xml.builder.SkillBuilder;
import databuilder.xml.builder.SpawnBuilder;
import databuilder.xml.parser.L2onDropParser;
import databuilder.xml.parser.NpcDropParser;
import databuilder.xml.parser.NpcGaiParser;
import databuilder.xml.parser.NpcParser;
import databuilder.xml.parser.SkillParser;
import databuilder.xml.parser.SpawnParser;

public class MainBuilder
{
	
	public static MainBuilder _instance;
	public static Connection _conn = null;
	public static final String _datapack_path = "/Users/sontronghien/Workspace/cuuthanh/09_Server/ertheia/dist/gameserver/";
	public static String _jdbc_connection = "jdbc:mysql://localhost/Ertheia_NPC_Nam?user=root&password=enterpri";
	
	private MainBuilder()
	{
		//BloodFakePlayers.getInstance();
	}
	
	public static Connection connection(){
		
		if(_conn == null)
		{
			try {
				_conn = DriverManager.getConnection(_jdbc_connection);

			    // Do something with the Connection

			} catch (SQLException ex) {
			    // handle any errors
			    System.out.println("SQLException: " + ex.getMessage());
			    System.out.println("SQLState: " + ex.getSQLState());
			    System.out.println("VendorError: " + ex.getErrorCode());
			}
		}
		return _conn;
	}
	
	public static void buildNpc(){
		
		SkillTable.getInstance().load();
		BaseStatsBonusParser.getInstance().load();
		LevelBonusParser.getInstance().load();
		NpcBuilder.getInstance();
		NpcParser.getInstance().load();
		NpcGaiParser.getInstance().load();
		NpcBuilder.getInstance().buildAndStore();
	}
	
	public static void buildDropList(){
		SkillTable.getInstance().load();
		OptionDataParser.getInstance().load();
		VariationDataParser.getInstance().load();
		ItemParser.getInstance().load();
		NpcDropParser.getInstance().load();
		L2onDropParser.getInstance().load();
	}
	
	public static void buildItem(){
//		SkillTable.getInstance().load();
//		OptionDataParser.getInstance().load();
//		VariationDataParser.getInstance().load();
//		ItemParser.getInstance().load();
//		buildFated();
		
		ItemBuilder.getInstance().build();
		ItemBuilder.getInstance().store();
	}
	
	public static void buildSkill(){
		SkillParser.getInstance().load();
//		SkillBuilder.getInstance().buildAndStore();
	}
	
	public static void buildSpawn(){
		SpawnParser.getInstance().load();
		SpawnBuilder.getInstance().buildAndStore();
	}
	
	public static void buildRaidSpawn(){
		SpawnBuilder.getInstance().buildAndStoreRaidboss();
	}
	
	
	public static class CustomComparator implements Comparator<ItemTemplate> {
	    @Override
	    public int compare(ItemTemplate o1, ItemTemplate o2) {
	        if(!o1.getGrade().equals(o2.getGrade()))
	        	return o1.getGrade().compareTo(o2.getGrade());
	        
	        return 0;
	    }
	}
	
	public static void main(String[] args)
	{
		System.out.println("Start...");
		try {
			Config.DATAPACK_ROOT = new File(_datapack_path).getCanonicalFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		buildNpc();
//		buildSkill();
//		buildDropList();
//		buildSpawn();
		buildRaidSpawn();
		
		
	}
	
	
	
	
	
}
