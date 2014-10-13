package databuilder.xml.builder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import l2s.gameserver.data.xml.holder.BaseStatsBonusHolder;
import l2s.gameserver.data.xml.holder.LevelBonusHolder;
import l2s.gameserver.data.xml.holder.NpcHolder;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.Skill;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.templates.npc.NpcTemplate;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import databuilder.MainBuilder;
import databuilder.utils.XmlPretty;

public class NpcBuilder {
	private HashMap<String, Element> _rootHolder = new HashMap<String, Element>();
	private HashMap<Integer, Element> _l2sHolder = new HashMap<Integer, Element>();
	private HashMap<Integer, Element> _gaiHolder = new HashMap<Integer, Element>();
	private TreeMap<Integer, L2NpcInfo> _npcHolder = new TreeMap<Integer, L2NpcInfo>();
	
	private HashMap<String, String> _nameHolder = new HashMap<String, String>();
	
	public void addSkillName(int skill_id, int skill_level, String name){
		_nameHolder.put(skill_id+"-"+skill_level, name);
	}
	
	public String getSkillName(int skill_id, int skill_level){
		return _nameHolder.get(skill_id+"-"+skill_level);
	}
	
	public class L2NpcInfo{
		int _id;
		Element _element = null;
		StatsSet _set = new StatsSet();
		TreeMap<Integer, Skill> _skills = new TreeMap<Integer, Skill>();
		TreeMap<String, String> _attributesDefence = new TreeMap<String, String>();
		TreeMap<String, String> _attributesAttack = new TreeMap<String, String>();
		TreeMap<String, String> _equip = new TreeMap<String, String>();
		
		String[] _keywords = {"name", "title", "collision_height", "collision_radius"};
		
		public L2NpcInfo(int id){
			_id = id;
			_npcHolder.put(_id, this);
		}
		
		public void fromGai(Element npcElement){
			for(Iterator<org.dom4j.Element> firstIterator = npcElement.elementIterator(); firstIterator.hasNext();)
			{
				org.dom4j.Element firstElement = firstIterator.next();
				if(firstElement.getName().equalsIgnoreCase("set"))
				{
					_set.set(firstElement.attributeValue("name"), firstElement.attributeValue("value"));
				}
				else if(firstElement.getName().equalsIgnoreCase("equip"))
				{
					for(Iterator<org.dom4j.Element> eIterator = firstElement.elementIterator(); eIterator.hasNext();)
					{
						org.dom4j.Element eElement = eIterator.next();
						_equip.put(eElement.getName(), eElement.attributeValue("item_id"));
					}
				}
				else if(firstElement.getName().equalsIgnoreCase("attributes"))
				{
					for(Iterator<org.dom4j.Element> eIterator = firstElement.elementIterator(); eIterator.hasNext();)
					{
						org.dom4j.Element eElement = eIterator.next();
						if(eElement.getName().equalsIgnoreCase("defence"))
						{
							_attributesDefence.put(eElement.attributeValue("attribute"), eElement.attributeValue("value"));
						}
						else if(eElement.getName().equalsIgnoreCase("attack"))
						{
							_attributesAttack.put(eElement.attributeValue("attribute"), eElement.attributeValue("value"));
						}
					}
				}
				else if(firstElement.getName().equalsIgnoreCase("skills"))
				{
					for(Iterator<org.dom4j.Element> nextIterator = firstElement.elementIterator(); nextIterator.hasNext();)
					{
						org.dom4j.Element nextElement = nextIterator.next();
						int skill_id = Integer.parseInt(nextElement.attributeValue("id"));
						int skill_level = Integer.parseInt(nextElement.attributeValue("level")); 
						Skill skill = SkillTable.getInstance().getInfo(skill_id, skill_level);
						if(skill != null)
							_skills.put(skill.getId(), skill);
					}
				}
			}
			
			calculateStats();
		}
		
		boolean _flagCalculateStats = false;
		
		public void calculateStats(){
			
			if(_flagCalculateStats)
				return;
			
			_flagCalculateStats = true;
			
			NpcTemplate template = NpcHolder.getInstance().getTemplateNoWarn(_id);
			if(template == null)
				return;
			
			for(Skill skill: _skills.values()){
					template.addSkill(skill);
			}
			
			if(_set.getInteger("baseSTR", 0) == 0)
			{
				System.out.println("Error at "+_id);
				return;
			}
			
			double str_bonus = BaseStatsBonusHolder.getInstance().getBaseStatsBonus(_set.getInteger("baseSTR")).getSTR();
			double int_bonus = BaseStatsBonusHolder.getInstance().getBaseStatsBonus(_set.getInteger("baseINT")).getINT();
			double dex_bonus = BaseStatsBonusHolder.getInstance().getBaseStatsBonus(_set.getInteger("baseDEX")).getDEX();
			double wit_bonus = BaseStatsBonusHolder.getInstance().getBaseStatsBonus(_set.getInteger("baseWIT")).getWIT();
			double con_bonus = BaseStatsBonusHolder.getInstance().getBaseStatsBonus(_set.getInteger("baseCON")).getCON();
			double men_bonus = BaseStatsBonusHolder.getInstance().getBaseStatsBonus(_set.getInteger("baseMEN")).getMEN();
			
			double level_bonus = !template.isInstanceOf(Servitor.class) ? 1. : LevelBonusHolder.getInstance().getLevelBonus(template.level);
						
			double patk_modifier = level_bonus*str_bonus;
			double pdef_modifier = level_bonus;
			double pspd_modifier = dex_bonus;
			double pcrt_modifier = dex_bonus;
			double matk_modifier = level_bonus*level_bonus*int_bonus*int_bonus;
			double mdef_modifier = men_bonus * level_bonus;
			double mspd_modifier = wit_bonus;
			double hp_modifier = con_bonus;
			double mp_modifier = men_bonus;
			
			for(Skill skill: template.getSkills().valueCollection())
			{
				switch (skill.getId()) {
				case 4408: // weak atk
					double[] ef4408 = new double[] {1, 1.1, 1.21, 1.33, 1.46, 1.61, 1.77, 0.25, 0.50, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
					hp_modifier *= ef4408[skill.getLevel()-1];
					break;
				case 4410: // weak atk
					double[] ef4410 = new double[] {0.39, 0.43, 0.47, 0.52, 0.57, 0.63, 0.69, 0.76, 0.83, 0.91, 1, 1.1, 1.21, 1.33, 1.46, 1.61, 1.77, 1.91, 2.14, 2.35, 2.59, 9};
					patk_modifier *= ef4410[skill.getLevel()-1];
					break;
				case 4411: // weak matk
					double[] ef4411 = new double[] {0.15, 0.18, 0.22, 0.27, 0.32, 0.39, 0.47, 0.57, 0.69, 0.83, 1, 1.21, 1.46, 1.77, 2.14, 2.59, 3.13, 3.79, 4.59, 5.55, 6.72, 9.9};
					matk_modifier *= ef4411[skill.getLevel()-1];
					break;
				case 4412: // weak pdef
					double[] ef4412 = new double[] {0.39, 0.43, 0.47, 0.52, 0.57, 0.63, 0.69, 0.76, 0.83, 0.91, 1, 1.1, 1.21, 1.33, 1.46, 1.61, 1.77, 1.91, 2.14, 2.35, 2.59, 9};
					pdef_modifier *= ef4412[skill.getLevel()-1];
					break;
				case 4413: // weak mdef
					double[] ef4413 = new double[] {0.39, 0.43, 0.47, 0.52, 0.57, 0.63, 0.69, 0.76, 0.83, 0.91, 1, 1.1, 1.21, 1.33, 1.46, 1.61, 1.77, 1.91, 2.14, 2.35, 2.59, 9};
					mdef_modifier *= ef4413[skill.getLevel()-1];
					break;
				case 4414: // armor type
					double[] ef4414 = new double[] {1.15, 1, 0.85};
					pdef_modifier *= ef4414[skill.getLevel()-1];
					break;
				case 4415: // bare hand
					double[] ef4415_patk = new double[] {1, 1.38, 1, 1.38, 1, 1.38, 0.87, 1.21, 2.22, 2.5, 1, 1.38, 1.16, 1.62, 1.16, 1.62, 1.16, 1.62, 1.16, 1.62, 1.08, 1.62, 0.93, 1.62, 1.38, 1.62, 1.08, 1.62};
					double[] ef4415_patk_spd = new double[] {1, 0.71, 1, 0.71, 1, 0.71, 1.14, 0.82, 0.89, 0.64, 1, 0.71, 0.85, 0.61, 0.85, 0.61, 0.85, 0.61, 0.85, 0.61, 0.92, 0.61, 1.07, 0.61, 0.62, 0.61, 0.92, 0.61};
					patk_modifier *= ef4415_patk[skill.getLevel()-1];
					pspd_modifier *= ef4415_patk_spd[skill.getLevel()-1];
					break;
				case 11315: // summon def
					mdef_modifier *= 1.5;
					break;

				default:
					break;
				}
			}
			
			_set.set("basePAtk", (int) (_set.getInteger("basePAtk")/patk_modifier));
			_set.set("baseMAtk", (int) (_set.getInteger("baseMAtk")/matk_modifier));
			_set.set("basePDef", (int) (_set.getInteger("basePDef")/pdef_modifier));
			_set.set("baseMDef", (int) (_set.getInteger("baseMDef")/mdef_modifier));
			_set.set("basePAtkSpd", (int) (_set.getInteger("basePAtkSpd")/pspd_modifier));
			_set.set("baseMAtkSpd", (int) (_set.getInteger("baseMAtkSpd")/mspd_modifier));
			_set.set("basePCritRate", (int) (_set.getDouble("basePCritRate")/pcrt_modifier));
			_set.set("baseHpMax", (int) (_set.getInteger("baseHpMax")/hp_modifier));
			_set.set("baseMpMax", (int) (_set.getInteger("baseMpMax")/mp_modifier));
		}
		
		public void fromClient(ResultSet rset){
			try{
				for(String keyword: _keywords){
					_set.set(keyword, rset.getString(keyword));
				}
			}catch (SQLException e){
				System.out.println(e);
			}
		}
		
		public Element getElement()
		{
			
			if(_element == null){
				_element = _l2sHolder.get(_id);
				if(_element != null)
					getRoot(_id).add(_element.detach());
			}
			
			if(_element == null)
			{
				System.out.println("Missing:"+_id);
				getRoot(_id).addComment("Generated by blood");
				_element = getRoot(_id).addElement("npc");
				_element.addAttribute("id", Integer.toString(_id));
				
				_element.addElement("set")
				.addAttribute("name", "collision_height")
				.addAttribute("value", "collision_height_place_holder");
				
				_element.addElement("set")
				.addAttribute("name", "collision_radius")
				.addAttribute("value", "collision_radius_place_holder");
				
				_element.addElement("set")
				.addAttribute("name", "level")
				.addAttribute("value", "70");
				
				_element.addElement("set")
				.addAttribute("name", "rewardExp")
				.addAttribute("value", "0");
				
				_element.addElement("set")
				.addAttribute("name", "rewardSp")
				.addAttribute("value", "0");
				
				_element.addElement("set")
				.addAttribute("name", "rewardRp")
				.addAttribute("value", "0");
				
				_element.addElement("set")
				.addAttribute("name", "aggroRange")
				.addAttribute("value", "0");
				
				_element.addElement("set")
				.addAttribute("name", "baseHpRate")
				.addAttribute("value", "1");
				
				_element.addElement("set")
				.addAttribute("name", "type")
				.addAttribute("value", "Npc");
				
				_element.addElement("set")
				.addAttribute("name", "ai_type")
				.addAttribute("value", "NpcAI");
				
			}
			
			Element skillElement = null;
			
			for(Element firstElement: _element.elements()){
				if(firstElement.getName().equalsIgnoreCase("set"))
				{
					String setName = firstElement.attributeValue("name");
					if(_set.containsKey(setName))
						firstElement.addAttribute("value", _set.getString(setName));
				}
				else if(firstElement.getName().equalsIgnoreCase("attributes"))
				{
					for(Iterator<org.dom4j.Element> eIterator = firstElement.elementIterator(); eIterator.hasNext();)
					{
						org.dom4j.Element eElement = eIterator.next();
						String attributeName = eElement.attributeValue("attribute");
						if(eElement.getName().equalsIgnoreCase("defence"))
						{
							if(_attributesDefence.containsKey(attributeName)){
								eElement.addAttribute("value", _attributesDefence.get(attributeName));
								_attributesDefence.remove(attributeName);
							}
						}
						else if(eElement.getName().equalsIgnoreCase("attack"))
						{
							if(_attributesAttack.containsKey(attributeName)){
								eElement.addAttribute("value", _attributesAttack.get(attributeName));
								_attributesAttack.remove(attributeName);
							}
						}
					}
					
					if(_attributesAttack.size() > 0)
					{
						for(Map.Entry<String, String> entry: _attributesAttack.entrySet())
						{
							firstElement.addElement("attack")
							.addAttribute("attribute", entry.getKey())
							.addAttribute("value", entry.getValue());
						}
					}
				}
				else if(firstElement.getName().equalsIgnoreCase("skills"))
				{
					skillElement = firstElement;
					 
					for(Iterator<org.dom4j.Element> nextIterator = firstElement.elementIterator(); nextIterator.hasNext();)
					{
						org.dom4j.Element nextElement = nextIterator.next();
						int skill_id = Integer.parseInt(nextElement.attributeValue("id"));
						int skill_level = Integer.parseInt(nextElement.attributeValue("level"));
						Skill skill = SkillTable.getInstance().getInfo(skill_id, skill_level);
						
						if(skill == null)
							continue;
						
						if(_skills.containsKey(skill_id))
						{
							if(_skills.get(skill_id).getLevel() < skill_level)
							{
								_skills.put(skill.getId(), skill);
							}
						}
					}
				}
			}
			
			if(_skills.size() > 0)
			{
				if (skillElement == null)
					skillElement = _element.addElement("skills");
				else
					skillElement.clearContent();
				
				for(Skill skill: _skills.values()){
					skillElement.addElement("skill")
					.addAttribute("id", Integer.toString(skill.getId()))
					.addAttribute("level", Integer.toString(skill.getLevel()));
					skillElement.addComment(getSkillName(skill.getId(), skill.getLevel()));
				}
			}
			
			_element.addAttribute("name", _set.getString("name"));
			_element.addAttribute("title", _set.getString("title"));
			return _element;
		}
	}
	
	private static final NpcBuilder _instance = new NpcBuilder();

	public NpcBuilder(){
		dbLoadSkillName();
		dbLoad();
	}
	
	public static NpcBuilder getInstance()
	{
		return _instance;
	}
	
	public void addL2sElement(int npc_id, Element e){
		_l2sHolder.put(npc_id, e);
	}
	
	public void addGaiElement(int npc_id, Element e){
		_gaiHolder.put(npc_id, e);
	}
	
	public Element getRoot(int id){
		int min_id = id - id%100;
		int max_id = min_id + 99;
		return getRoot(String.format("%05d-%05d", min_id, max_id));
	}
	
	public Element getRoot(String file){
		
		Element resultElement = _rootHolder.get(file);
		
		if(resultElement == null)
		{
			Document document = DocumentHelper.createDocument();
			resultElement = document.addElement("list");
			_rootHolder.put(file, resultElement);
		}
		
		return resultElement;
	}
	
	public L2NpcInfo getNpc(int id){
		
		return _npcHolder.get(id);
	}
	
	public void dbLoad(){
		try {
			PreparedStatement statement = MainBuilder.connection().prepareStatement("SELECT * FROM L2NpcName");
			ResultSet rset = statement.executeQuery();
			while(rset.next()){
				int id = rset.getInt("id");
				L2NpcInfo info = new L2NpcInfo(id);
				info.fromClient(rset);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		};
	}
	
	public void dbLoadSkillName(){
		try {
			PreparedStatement statement = MainBuilder.connection().prepareStatement("SELECT id, level, name FROM L2SkillName ORDER BY id ASC, level ASC");
			ResultSet rset = statement.executeQuery();
			while(rset.next()){
				addSkillName(rset.getInt("id"), rset.getInt("level"), rset.getString("name"));
				 
			}
		}catch (SQLException e) {
			e.printStackTrace();
		};
	}
	
	public void store(){
		for(Map.Entry<String, Element> entry: _rootHolder.entrySet())
			XmlPretty.writeToFile(entry.getKey(), entry.getValue().asXML(), "skill.dtd", "data/blood_npc/");
	}
	
	public void build()
	{
		int count = 0;
		for(Map.Entry<Integer, L2NpcInfo> entry: _npcHolder.entrySet())
		{
			entry.getValue().getElement();
			count++;
			if(count%100 == 0){
				System.out.println("processed item: "+count);
			}
		}
//		buildFated();
	}
	
	public void buildAndStore(){
		build();
		store();
	}
}
