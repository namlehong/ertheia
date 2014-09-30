package databuilder.xml.builder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import databuilder.MainBuilder;
import databuilder.utils.XmlPretty;

public class SpawnBuilder {
	
	private HashSet<Integer> _npcIds = new HashSet<Integer>(); 
//	private HashMap<String, Element> _terRoot = new HashMap<String, Element>();
	private HashMap<String, Element> _npcRoot = new HashMap<String, Element>();
	private HashMap<String, Element> _mobRoot = new HashMap<String, Element>();
	private int[] _bossIds = {29001, 29006, 29014, 29068, 29020, 29022, 29028, 29047, 25004, 25007, 25010, 25016, 25019, 25023, 25026, 25032, 25038, 25041, 25044, 25047, 25050, 25051, 25057, 25060, 25063, 25064, 25067, 25070, 25076, 25079, 25082, 25085, 25088, 25089, 25092, 25095, 25098, 25099, 25102, 25103, 25106, 25112, 25115, 25119, 25122, 25125, 25127, 25128, 25131, 25134, 25143, 25146, 25149, 25152, 25155, 25158, 25159, 25163, 25166, 25169, 25170, 25179, 25185, 25188, 25189, 25192, 25208, 25211, 25214, 25226, 25229, 25230, 25233, 25235, 25238, 25241, 25244, 25245, 25248, 25249, 25252, 25255, 25256, 25260, 25263, 25269, 25272, 25273, 25276, 25293, 25299, 25306, 25309, 25316, 25319, 25322, 25325, 25328, 25339, 25342, 25346, 25349, 25352, 25354, 25357, 25360, 25362, 25365, 25366, 25369, 25373, 25375, 25378, 25380, 25388, 25391, 25392, 25394, 25395, 25398, 25404, 25410, 25415, 25418, 25420, 25423, 25426, 25429, 25431, 25434, 25437, 25438, 25441, 25444, 25447, 25450, 25453, 25456, 25460, 25463, 25478, 25493, 25496, 25498, 25501, 25504, 25506, 29060, 29096, 29062, 25523, 25524, 29056, 29054, 25527, 29065, 29095, 25528, 29213, 25603, 29118, 29129, 29132, 29135, 29138, 29141, 29144, 29147, 25643, 25644, 25645, 25646, 25647, 25648, 25649, 25650, 25651, 25652, 29150, 25623, 25624, 25625, 25626, 29163, 25665, 25666, 25667, 25671, 25674, 25677, 25680, 25681, 25684, 25687, 29179, 25725, 25726, 25727, 25718, 25719, 25720, 25721, 25722, 25723, 25724, 25710, 29181, 25020, 25173, 29195, 29196, 25696, 25697, 25698, 25859, 25862, 25856, 25855, 25796, 25797, 25799, 29197, 25745, 25758, 25785, 25875, 25779, 25775, 25870, 25871, 29194, 29218, 25809, 25811, 25813, 25815, 25816, 25818, 25820, 25882, 25883, 25884, 25886, 25887, 25888, 25892, 25902, 25901, 25922, 25928, 25929, 25930, 25931, 25927, 25932, 25933, 25937, 29240, 29236, 25283, 25286, 25837, 25838, 25839, 25840, 25841, 25843, 25846, 25824, 26000, 26001, 26002, 26003, 26004, 26005, 25978, 25979, 25980, 25981, 25982, 25983, 26077, 26078, 26079, 26080, 26081, 26082, 26011, 26012, 26013, 26014, 26015, 26016, 26055, 26056, 26057, 26058, 26059, 26060, 26066, 26067, 26068, 26069, 26070, 26071, 26034, 26035, 26036, 26037, 26038, 26033, 25989, 25990, 25991, 25992, 25993, 25994, 26044, 26045, 26046, 26047, 26048, 26049, 25967, 25968, 25969, 25970, 25971, 25972, 25956, 25957, 25958, 25959, 25960, 25961, 26022, 26023, 26024, 26025, 26026, 26027, 25945, 25946, 25947, 25948, 25949, 25950, 25942, 25943, 25944, 8551, 8557 };
	private ArrayList<Integer> _bossCollection =  new ArrayList<Integer>();
	
	private static final SpawnBuilder _instance = new SpawnBuilder();

	public SpawnBuilder(){
		for(int id: _bossIds){
			_bossCollection.add(id);
		}
//		dbLoad();
	}
	
	public static SpawnBuilder getInstance()
	{
		return _instance;
	}
	
	public Element getNpcRoot(String file){
		
		Element resultElement = _npcRoot.get(file);
		
		if(resultElement == null)
		{
			Document document = DocumentHelper.createDocument();
			resultElement = document.addElement("list");
			_npcRoot.put(file, resultElement);
		}
		
		return resultElement;
	}
	
	public Element getMobRoot(String file){
		
		Element resultElement = _mobRoot.get(file);
		
		if(resultElement == null)
		{
			Document document = DocumentHelper.createDocument();
			resultElement = document.addElement("list");
			_mobRoot.put(file, resultElement);
		}
		
		return resultElement;
	}
	
//	public Element getTerRoot(String file){
//		
//		Element resultElement = _terRoot.get(file);
//		
//		if(resultElement == null)
//		{
//			Document document = DocumentHelper.createDocument();
//			resultElement = document.addElement("list");
//			_terRoot.put(file, resultElement);
//		}
//		
//		return resultElement;
//	}
	
	public void addTer(String _currentFile, Element terElement){
		getMobRoot(_currentFile.replace(".xml", "")).add(terElement.detach());
	}
	
	public void addElement(String _currentFile, HashSet<Integer> npcSpawnIds,
			Element spawnElement) {
		npcSpawnIds.removeAll(_npcIds);
		npcSpawnIds.removeAll(_bossCollection);
		
		_currentFile = _currentFile.replace(".xml", "");
		
		if(npcSpawnIds.size() == 0) // dont have mob inside
			return;
		
//		Element rootElement = npcSpawnIds.size() > 0 ? getMobRoot(_currentFile) : getNpcRoot(_currentFile);
		
		getMobRoot(_currentFile).add(spawnElement.detach());
		
	}
	
	public void dbUpdate(String file_name, int npc_id){
		try {
			PreparedStatement statement = MainBuilder.connection().prepareStatement("update npc_data set file_spawn = ? where npc_id = ?");
			statement.setString(1, file_name);
			statement.setInt(2, npc_id);
			statement.execute();
		}catch (SQLException e) {
			e.printStackTrace();
		};
	}
	
	public void dbLoad(){
		try {
			PreparedStatement statement = MainBuilder.connection().prepareStatement("SELECT npc_id FROM npc_data");
			ResultSet rset = statement.executeQuery();
			while(rset.next()){
				int id = rset.getInt("npc_id");
				_npcIds.add(id);
			}
		}catch (SQLException e) {
			System.out.println(e);
		};
	}
	
	public void store(){
//		for(Map.Entry<String, Element> entry: _npcRoot.entrySet())
//			XmlPretty.writeToFile(entry.getKey(), entry.getValue().asXML(), "spawn.dtd", "data/spawn_npc/");
		for(Map.Entry<String, Element> entry: _mobRoot.entrySet())
			XmlPretty.writeToFile(entry.getKey(), entry.getValue().asXML(), "spawn.dtd", "data/spawn_mob/");
//		for(Map.Entry<String, Element> entry: _terRoot.entrySet())
//			XmlPretty.writeToFile(entry.getKey(), entry.getValue().asXML(), "spawn.dtd", "data/spawn_ter/");
	}
	
	public void build()
	{
//		int count = 0;
//		for(Map.Entry<Integer, L2NpcInfo> entry: _npcHolder.entrySet())
//		{
//			entry.getValue().getElement();
//			count++;
//			if(count%100 == 0){
//				System.out.println("processed item: "+count);
//			}
//		}
//		buildFated();
	}
	
	public void buildAndStore(){
		build();
		store();
		
		System.out.println("Loaded NpcIds:"+_npcIds.size());
	}

	public void buildAndStoreRaidboss() {
		buildRB();
		storeRB();
		
	}

	private void storeRB() {
		// TODO Auto-generated method stub
		XmlPretty.writeToFile("raidboss", _raidbossElementList.asXML(), "spawn.dtd", "data/spawn/");
	}
	
	private Element _raidbossElementList = null;

	private void buildRB() {
		// TODO Auto-generated method stub
		Document document = DocumentHelper.createDocument();
		_raidbossElementList = document.addElement("list");
		try {
			PreparedStatement statement = MainBuilder.connection().prepareStatement("SELECT * FROM `RaidData-e`");
			ResultSet rset = statement.executeQuery();
			while(rset.next()){
				/*
				 * <spawn count="1" respawn="10" respawn_random="0" period_of_day="none">
		<point x="-82137" y="250187" z="-3360" h="0" />
		<npc id="19546" /><!--Чучело-->
	</spawn>
				 */
				
				int loc_x = rset.getInt("loc_x");
				int loc_y = rset.getInt("loc_y");
				int loc_z = rset.getInt("loc_z");
				
				if((loc_x + loc_y + loc_z) == 0)
					continue;
				
				_raidbossElementList.addComment(String.format("\n\t\t%s - %s\n\t\tLevel: %s\n\t\t%s\n\t", rset.getString("name"), rset.getString("title"), rset.getString("npc_level"), rset.getString("raid_desc").replace("--", "-")));
				Element spawn = _raidbossElementList.addElement("spawn");
				spawn.addAttribute("count", "1");
				spawn.addAttribute("respawn", Integer.toString(60*60*9));
				spawn.addAttribute("respawn_random", Integer.toString(60*60*3));
				spawn.addAttribute("period_of_day", "None");
				Element point = spawn.addElement("point");
				point.addAttribute("x", Integer.toString(loc_x));
				point.addAttribute("y", Integer.toString(loc_y));
				point.addAttribute("z", Integer.toString(loc_z*100));
				point.addAttribute("h", "0");
				Element npc = spawn.addElement("npc");
				npc.addAttribute("id", rset.getString("npc_id"));
			}
		}catch (SQLException e) {
			System.out.println(e);
		};
		
	}

	
}
