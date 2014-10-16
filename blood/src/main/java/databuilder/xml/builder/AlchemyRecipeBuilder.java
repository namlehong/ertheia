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
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import databuilder.MainBuilder;
import databuilder.utils.XmlPretty;
import databuilder.xml.builder.ItemBuilder.L2ItemInfo;
import databuilder.xml.parser.SkillTestParser;

public class AlchemyRecipeBuilder {
	
	private static final AlchemyRecipeBuilder _instance = new AlchemyRecipeBuilder();
	private HashMap<String, String> _itemName = new HashMap<String, String>(); 
	private Element _list = DocumentHelper.createDocument().addElement("list");

	public AlchemyRecipeBuilder(){		
		dbLoad();
	}
	
	public static AlchemyRecipeBuilder getInstance()
	{
		return _instance;
	}
	
	public void dbLoad(){
		
		try {
			PreparedStatement statement = MainBuilder.connection().prepareStatement("SELECT id, name FROM L2ItemName");
			ResultSet rset = statement.executeQuery();
			while(rset.next()){
				_itemName.put(rset.getString("id"), rset.getString("name"));
			}
		}catch (SQLException e) {
			e.printStackTrace();
		};
		
		try {
			PreparedStatement statement = MainBuilder.connection().prepareStatement("select * from AlchemyData");
			ResultSet rset = statement.executeQuery();
			while(rset.next()){
				int recipe_id = rset.getInt("id");
				int recipe_level = rset.getInt("UNK_0");
				int used_item = rset.getInt("CNT_0");
				_list.addComment(rset.getString("recipe_desc"));
				Element recipe = _list.addElement("recipe");
				recipe.addAttribute("id", Integer.toString(recipe_id*100+recipe_level));
				recipe.addAttribute("name", rset.getString("recipe_name"));
				recipe.addAttribute("level", rset.getString("UNK_2"));
				recipe.addAttribute("mp_consume", "69");
				recipe.addAttribute("success_rate", "100");
				recipe.addAttribute("item_id", "69");
				recipe.addAttribute("is_common", "false");
				Element materials = recipe.addElement("materials");
				for(int i = 0; i < used_item; i++){
					Element item = materials.addElement("item");
					item.addAttribute("id", rset.getString("Unknown_Item_A_["+i+"]"));
					item.addAttribute("count", rset.getString("Unknown_Item_B_["+i+"]"));
					materials.addComment(_itemName.get(rset.getString("Unknown_Item_A_["+i+"]")));
				}
				Element products = recipe.addElement("products");
				Element productItem = products.addElement("item");
				productItem.addAttribute("id", rset.getString("Unknown_[0]"));
				productItem.addAttribute("count", rset.getString("Unknown_[1]"));
				productItem.addAttribute("chance", "100");
				products.addComment(_itemName.get(rset.getString("Unknown_[0]")));
			}
		}catch (SQLException e) {
			e.printStackTrace();
		};
	}
	
	public void save(){
		XmlPretty.writeToFile("alchemy", _list.asXML(), "alchemy.dtd", "data/");
	}

	
}
