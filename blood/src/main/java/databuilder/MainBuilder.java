package databuilder;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import databuilder.utils.XmlPretty;
import databuilder.xml.parser.NpcGaiParser;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.parser.BaseStatsBonusParser;
import l2s.gameserver.data.xml.parser.LevelBonusParser;
import l2s.gameserver.data.xml.parser.NpcParser;
import l2s.gameserver.tables.SkillTable;

public class MainBuilder
{
	
	public static MainBuilder _instance;
	public static Connection _conn = null;
	
	private MainBuilder()
	{
		//BloodFakePlayers.getInstance();
	}
	
	public static Connection connection(){
		
		if(_conn == null)
		{
			try {
				_conn = DriverManager.getConnection("jdbc:mysql://localhost/l2_raw?" +
			                                   "user=root&password=");

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
		NpcParser.getInstance().load();
		NpcGaiParser.getInstance().load();
	}
	
	public static HashMap<Integer, String> _keywords = new HashMap<Integer, String>();
	public static HashMap<Integer, String> _keywordsTop = new HashMap<Integer, String>();
	public static HashMap<Integer, String> _keywordsNpc = new HashMap<Integer, String>();
	
	public static void buildKeyWord(){
		
		try {
			PreparedStatement statement = connection().prepareStatement("SELECT id, words FROM L2KeyWord");
			ResultSet rset = statement.executeQuery();
			while(rset.next()){
				int id = rset.getInt("id");
				String keyword = rset.getString("words"); 
				if(keyword.contains("."))
					_keywordsTop.put(id, keyword);
				else
					_keywords.put(id, keyword);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		
	}
	
	public static void buildKeyWordNpc(){
		int count = 0;
		try {
			PreparedStatement statement = connection().prepareStatement("SELECT id, name FROM L2NpcName");
			ResultSet rset = statement.executeQuery();
			while(rset.next()){
				int id = rset.getInt("id");
				String keyword = rset.getString("name"); 
				_keywordsNpc.put(id, keyword);
				buildL2Text(keyword, "{{ kw_npc"+id+" }}");
				count++;
				if(count%100 == 0){
					System.out.println("process item: "+count);
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		
	}
	
	public static String keywordTranslate(String input)
	{
		for(Map.Entry<Integer, String> entry: _keywordsNpc.entrySet()){
			input = input.replace(entry.getValue(), "{{ kw_npc"+entry.getKey()+" }}");
		}
		
		for(Map.Entry<Integer, String> entry: _keywordsTop.entrySet()){
			input = input.replace(entry.getValue(), "{{ kw_item"+entry.getKey()+" }}");
		}
		
		for(Map.Entry<Integer, String> entry: _keywords.entrySet()){
			input = input.replace(entry.getValue(), "{{ kw_item"+entry.getKey()+" }}");
		}
		
		return input;
	}
	
	public static String keywordTranslateNoKey(String input)
	{
		for(Map.Entry<Integer, String> entry: _keywordsNpc.entrySet()){
			input = input.replace(entry.getValue(), "{{ kw_npc }}");
		}
		
		for(Map.Entry<Integer, String> entry: _keywordsTop.entrySet()){
			input = input.replace(entry.getValue(), "{{ kw_item }}");
		}
		
		for(Map.Entry<Integer, String> entry: _keywords.entrySet()){
			input = input.replace(entry.getValue(), "{{ kw_item }}");
		}
		
		return input;
	}
	
	public static void buildL2Text(String input, String replace){
		try {
			PreparedStatement statement = connection().prepareStatement("update L2Text set translate_1 = Replace(translate_1, ?, ?)");
			statement.setString(1, input);
			statement.setString(2, replace);
			statement.execute();
//			ResultSet rset = statement.executeQuery();
//			while(rset.next()){
//				System.out.println(keywordTranslateNoKey(rset.getString("content")));
//			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		
	}
	
	public static void main(String[] args)
	{
//		System.currentTimeMillis();
		System.out.println("Start...");
		try {
			Config.DATAPACK_ROOT = new File("/Users/mylove1412/Workspace/ertheia/dist/gameserver/").getCanonicalFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		buildKeyWordNpc();
//		buildKeyWord();
//		buildItem();
		
//		System.out.println(XmlPretty.prettyFormat("<test></test>", "npc.dtd"));
		
	}
	
}
