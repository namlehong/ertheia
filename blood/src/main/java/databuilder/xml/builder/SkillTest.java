package databuilder.xml.builder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import databuilder.MainBuilder;
import databuilder.utils.XmlPretty;
import databuilder.xml.parser.SkillTestParser;

public class SkillTest {
	
	private static final SkillTest _instance = new SkillTest();
	private HashMap<Integer, Integer> _maxLvlHolder = new HashMap<Integer, Integer>(); 

	public SkillTest(){		
		dbLoad();
	}
	
	public static SkillTest getInstance()
	{
		return _instance;
	}
	
	public int getMaxLevel(int skill_id){
		return _maxLvlHolder.get(skill_id) != null ? _maxLvlHolder.get(skill_id) : 1;
	}
	
	public void dbLoad(){
		try {
			PreparedStatement statement = MainBuilder.connection().prepareStatement("select id, `name`, max(level) as max_level from `L2SkillName` where level < 100 group by id;");
			ResultSet rset = statement.executeQuery();
			while(rset.next()){
				int id = rset.getInt("id");
				int max_level = rset.getInt("max_level");
//				System.out.println("skill: "+id+" level:"+max_level);
				try{
					_maxLvlHolder.put(id, max_level);
				}catch(Exception e){
					System.out.println(e);
				}
				 
			}
		}catch (SQLException e) {
			e.printStackTrace();
		};
	}

	public void testSkill() {
		// TODO Auto-generated method stub
		System.out.println("loaded "+_maxLvlHolder.size()+" skill from db");
		SkillTestParser.getInstance().load();
		StringBuilder sb = new StringBuilder();
		for(int skill_id: missMagicLevel){
//			System.out.println("Miss magic level: "+skill_id);
			sb.append("Miss magic level: "+skill_id);
			sb.append("\n");
		}
		for(int skill_id: wrongMaxLevel){
//			System.out.println("Wrong max level: "+skill_id);
			sb.append("Wrong max level: "+skill_id);
			sb.append("\n");
		}
		for(int skill_id: wrongEnchantMaxLevel){
//			System.out.println("Wrong enchant max level: "+skill_id);
			sb.append("Wrong enchant max level: "+skill_id);
			sb.append("\n");
		}
		for(int skill_id: wrongPassiveEffect){
//			System.out.println("Wrong enchant max level: "+skill_id);
			sb.append("Wrong passive effect: "+skill_id);
			sb.append("\n");
		}
		for(Map.Entry<String, TreeSet<Integer>> entry: _orderToStats.entrySet()){
//			System.out.println(entry.getKey()+" ids:"+StringUtils.join(entry.getValue(), ", "));
			sb.append(entry.getKey()+" ids:"+StringUtils.join(entry.getValue(), ", "));
			sb.append("\n");
//			for(String stat: entry.getValue()){
//				System.out.println(entry.getKey()+"-"+ stat);
//			}
		}
		
		XmlPretty.writeToFile("skills.txt", sb.toString());
	}
	
	public TreeSet<Integer> missMagicLevel = new TreeSet<Integer>();
	public TreeSet<Integer> wrongPassiveEffect = new TreeSet<Integer>();
	public TreeSet<Integer> wrongMaxLevel = new TreeSet<Integer>();
	public TreeSet<Integer> wrongEnchantMaxLevel = new TreeSet<Integer>();
	
	private TreeMap<String, TreeSet<Integer>> _orderToStats = new TreeMap<String, TreeSet<Integer>>();
	
	public TreeSet<Integer> getOrderSet(String order){
		TreeSet<Integer> result = _orderToStats.get(order);
		if(result == null){
			result = new TreeSet<Integer>();
			_orderToStats.put(order, result);
		}
		
		return result;
	}
	
	public void validateOrder(org.dom4j.Element elem, int skill_id, String skill_type, String op_type){
		String order = elem.attributeValue("order");
		String stat = elem.attributeValue("stat");
		String value = elem.attributeValue("value");
		String type = elem.getName();
		
		if(type.equalsIgnoreCase("def") 
			|| type.equalsIgnoreCase("player")
			|| type.equalsIgnoreCase("triggers"))
			return;
		
		String key = order+"-"+skill_type+"-"+op_type+"-"+type+"-"+stat;
		
		if(order != null && !order.isEmpty())
			getOrderSet(key).add(skill_id);
		else
			System.out.println(elem.asXML());
		
//		if(order != null && order.equalsIgnoreCase("0x10")){
//			System.out.println("Just test order 0x10 on skill:"+skill_id+" skill_type:"+skill_type+" op:"+op_type+" type:"+type+" stat:"+stat+" value:"+value);
//		}
	}
}
