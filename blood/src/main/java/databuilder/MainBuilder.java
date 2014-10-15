package databuilder;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Comparator;

import l2s.commons.math.random.RndSelector;
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
import databuilder.xml.builder.SkillTest;
import databuilder.xml.builder.SpawnBuilder;
import databuilder.xml.parser.L2onDropParser;
import databuilder.xml.parser.NpcDropParser;
import databuilder.xml.parser.NpcGaiParser;
import databuilder.xml.parser.NpcParser;
import databuilder.xml.parser.SkillParser;
import databuilder.xml.parser.SkillTestParser;
import databuilder.xml.parser.SpawnParser;

public class MainBuilder
{
	
	public static MainBuilder _instance;
	public static Connection _conn = null;
	public static final String _datapack_path = "/Users/mylove1412/Workspace/ertheia/dist/gameserver/";
	public static String _jdbc_connection = "jdbc:mysql://localhost/l2_raw?user=root&password=";
	
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
	
	enum TalishMan{
		Destruction(100, 0, 0, 25, 30),
		Annihilation(30, 50, 20, 25, 30),
		Hellfire(20, 60, 20, 25, 30),
		Desire(10, 50, 40, 30, 60),
		Longing(100, 0, 0, 35, 70);
		
		RndSelector<Integer> _rndSelector = new RndSelector<Integer>();
		RndSelector<Integer> _safeSelector = new RndSelector<Integer>();
		public int _destruction;
		public int _giant;
		
		TalishMan(Integer rateUp, Integer rateStay, Integer rateDown, Integer destruction, Integer giant){
			_rndSelector.add(1, rateUp);
			_rndSelector.add(-1, rateDown);
			_rndSelector.add(0, rateStay);
			_safeSelector.add(1, rateUp);
			_safeSelector.add(0, 100 - rateUp);
			_destruction = destruction;
			_giant = giant;
		}
		
		public TalishMan exchange(){
			int nextOrd = this.ordinal() + _rndSelector.select();
			return TalishMan.values()[nextOrd];
		}
		
		public TalishMan safe_exchange(){
			int nextOrd = this.ordinal() + _safeSelector.select();
			return TalishMan.values()[nextOrd];
		}
	}
	
	public static void testLonging(){
		int total_destruction = 0;
		int total_giant = 0;
		int total_kaliel = 0;
		DecimalFormat df2 = new DecimalFormat( "#,###,###,000" );
		int maxTry = 1000;
		
		for(int i = 0; i < maxTry; i++){
			int destruction = 0;
			int giant = 0;
			int kaliel = 0;
			TalishMan myTM = TalishMan.Destruction;
			while(myTM != TalishMan.Longing){
				destruction += myTM._destruction;
				giant += myTM._giant;
				kaliel += 1;
				myTM = myTM.safe_exchange();
			}
			total_destruction += destruction;
			total_giant += giant;
			total_kaliel += kaliel;
//			System.out.println("Tried: "+i);
//			System.out.println("Used destruction: "+destruction);
//			System.out.println("Used giant: "+giant);
//			System.out.println("VND convert: "+df2.format((destruction+giant)*5000));
//			System.out.println("========================");
		}
		System.out.println("Average");
		System.out.println("Used destruction: "+total_destruction/maxTry);
		System.out.println("Used giant: "+total_giant/maxTry);
		System.out.println("Used kaliel: "+total_kaliel/maxTry);
		System.out.println("VND convert: "+df2.format((total_destruction/maxTry+total_giant/maxTry)*5000));
		System.out.println("Adena convert: "+df2.format((total_destruction/maxTry+total_giant/maxTry)*10));
		
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
//		testLonging();
//		SkillTest.getInstance();
		SkillTest.getInstance().testSkill();
//		buildRaidSpawn();
	}
	
	
	
	
	
}
