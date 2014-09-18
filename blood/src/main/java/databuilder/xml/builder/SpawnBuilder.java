package databuilder.xml.builder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import l2s.gameserver.data.xml.holder.BaseStatsBonusHolder;
import l2s.gameserver.data.xml.holder.LevelBonusHolder;
import l2s.gameserver.data.xml.holder.NpcHolder;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.items.ItemInfo;
import l2s.gameserver.stats.conditions.Condition;
import l2s.gameserver.stats.conditions.ConditionLogicAnd;
import l2s.gameserver.stats.conditions.ConditionPlayerMaxLevel;
import l2s.gameserver.stats.conditions.ConditionPlayerMinLevel;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.templates.item.Bodypart;
import l2s.gameserver.templates.item.ItemGrade;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.npc.NpcTemplate;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import apple.laf.JRSUIUtils.Tree;
import databuilder.MainBuilder;
import databuilder.MainBuilder.CustomComparator;
import databuilder.utils.L2String;
import databuilder.utils.XmlPretty;
import databuilder.xml.builder.SkillBuilder.L2SkillInfo;
import databuilder.xml.holder.ItemHolder;

public class SpawnBuilder {
	
	private HashSet<Integer> _npcIds = new HashSet<Integer>(); 
	private HashMap<String, Element> _npcRoot = new HashMap<String, Element>();
	private HashMap<String, Element> _mobRoot = new HashMap<String, Element>();
	
	private static final SpawnBuilder _instance = new SpawnBuilder();

	public SpawnBuilder(){
		dbLoad();
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
	
	public void addElement(String _currentFile, HashSet<Integer> npcSpawnIds,
			Element spawnElement) {
		npcSpawnIds.removeAll(_npcIds);
		
		_currentFile = _currentFile.replace(".xml", "");
		
		Element rootElement = npcSpawnIds.size() > 0 ? getMobRoot(_currentFile) : getNpcRoot(_currentFile);
		
		rootElement.add(spawnElement.detach());
		
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
			e.printStackTrace();
		};
	}
	
	public void store(){
		for(Map.Entry<String, Element> entry: _npcRoot.entrySet())
			XmlPretty.writeToFile(entry.getKey(), entry.getValue().asXML(), "spawn.dtd", "data/spawn_npc/");
		for(Map.Entry<String, Element> entry: _mobRoot.entrySet())
			XmlPretty.writeToFile(entry.getKey(), entry.getValue().asXML(), "spawn.dtd", "data/spawn_mob/");
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

	
}
