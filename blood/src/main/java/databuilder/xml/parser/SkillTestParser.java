package databuilder.xml.parser;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.StringUtils;

import databuilder.xml.builder.ItemBuilder;
import databuilder.xml.builder.SkillBuilder;
import databuilder.xml.builder.SkillTest;
import databuilder.xml.holder.ItemHolder;
import l2s.commons.data.xml.helpers.SimpleDTDEntityResolver;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.OptionDataHolder;
import l2s.gameserver.data.xml.parser.StatParser;
import l2s.gameserver.handler.items.impl.SkillsItemHandler;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.base.Element;
import l2s.gameserver.stats.conditions.Condition;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.templates.OptionDataTemplate;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.templates.item.ArmorTemplate;
import l2s.gameserver.templates.item.Bodypart;
import l2s.gameserver.templates.item.CapsuledItem;
import l2s.gameserver.templates.item.EtcItemTemplate;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.item.WeaponTemplate;

/**
 * @author VISTALL
 * @date 11:26/15.01.2011
 */
public final class SkillTestParser extends StatParser<ItemHolder>
{
	private static final SkillTestParser _instance = new SkillTestParser();

	public static SkillTestParser getInstance()
	{
		return _instance;
	}

	protected SkillTestParser()
	{
		super(ItemHolder.getInstance());
		ItemHolder.getInstance().info("Start parsing..");
	}

	@Override
	public File getXMLDir()
	{
		return new File(Config.DATAPACK_ROOT, "data/blood_skills/");
	}

	@Override
	public boolean isIgnored(File f)
	{
		return false;
	}

	@Override
	public String getDTDFileName()
	{
		return "item.dtd";
	}
	
	protected void initDTD(File f)
	{
//		_reader.setEntityResolver(new SimpleDTDEntityResolver(f));
	}

	@Override
	protected void readData(org.dom4j.Element rootElement) throws Exception
	{
		for(Iterator<org.dom4j.Element> skillIterator = rootElement.elementIterator(); skillIterator.hasNext();)
		{
			org.dom4j.Element skillElement = skillIterator.next();
			
			if(!skillElement.getName().equalsIgnoreCase("skill"))
				continue;
			
			int skill_id = Integer.parseInt(skillElement.attributeValue("id"));
			int max_level = Integer.parseInt(skillElement.attributeValue("levels"));
			int client_level = SkillTest.getInstance().getMaxLevel(skill_id);
			
			// Test max level of skill
			if(max_level != client_level){
//				System.out.println("Wrong max level ===> ID: "+skill_id+" xml:"+max_level+" table:"+client_level);
				SkillTest.getInstance().wrongMaxLevel.add(skill_id);
			}
			
			String magic_level = "";
			String skill_type = "";
			String operate_type = "";
			int max_enchant = 0;
			int magic_max_level = 0;
			HashMap<String, String> tables_data = new HashMap<String, String>(); 
			
			// collect first round data
			for(org.dom4j.Element firstElement: skillElement.elements()){
				if(firstElement.getName().equalsIgnoreCase("set")){
					if(firstElement.attributeValue("name").equalsIgnoreCase("magicLevel")){
						magic_level = firstElement.attributeValue("value"); 
					}
					if(firstElement.attributeValue("name").equalsIgnoreCase("skillType")){
						skill_type = firstElement.attributeValue("value"); 
					}
					if(firstElement.attributeValue("name").equalsIgnoreCase("operateType")){
						operate_type = firstElement.attributeValue("value"); 
					}
				}
				if(firstElement.getName().equalsIgnoreCase("enchant")){
					String route_levels = firstElement.attributeValue("levels");
					if(route_levels != null && Integer.parseInt(route_levels) > max_enchant){
						max_enchant = Integer.parseInt(route_levels);
					}
				}
				if(firstElement.getName().equalsIgnoreCase("table")){
					String table_name = firstElement.attributeValue("name");
					String table_data = firstElement.getStringValue();
					tables_data.put(table_name, table_data);
				}
			}
			
			// test not done
			if(skill_type.equalsIgnoreCase("NOTDONE")){
//				System.out.println("NOT DONE ===> ID: "+skill_id);
				continue;
			}
			
			// test enchant route
			if(magic_level.equalsIgnoreCase("")){
				if(max_enchant > 0)
					SkillTest.getInstance().missMagicLevel.add(skill_id);
//					System.out.println("Miss magic level ===> ID: "+skill_id+" max_enchant: "+max_enchant);
			}
			else if(!magic_level.startsWith("#")){
				magic_max_level = Integer.parseInt(magic_level);
			}else{
				String table_data_str = tables_data.get(magic_level);
				if(table_data_str != null){
					String[] table_data_split = table_data_str.split(" ");
					magic_max_level = Integer.parseInt(table_data_split[table_data_split.length - 1]);
				}
			}
			
			if(magic_max_level > 85 && max_enchant > 10){
//				System.out.println("Wrong max enchant level ===> ID: "+skill_id+" xml:"+max_enchant+" max: 10");
				SkillTest.getInstance().wrongEnchantMaxLevel.add(skill_id);
			}
			
			for(org.dom4j.Element secondElement: skillElement.elements()){
				if(secondElement.getName().equalsIgnoreCase("for")){
					for(org.dom4j.Element forChildElement: secondElement.elements()){
						if(operate_type.equalsIgnoreCase("OP_PASSIVE") && forChildElement.getName().equalsIgnoreCase("effect")){
							SkillTest.getInstance().wrongPassiveEffect.add(skill_id);
						}
						
						if(forChildElement.getName().equalsIgnoreCase("effect") || forChildElement.getName().equalsIgnoreCase("self_effect")){
							for(org.dom4j.Element effectChildElement: forChildElement.elements()){
								SkillTest.getInstance().validateOrder(effectChildElement, skill_id, skill_type, operate_type);
							}
						}else{
							SkillTest.getInstance().validateOrder(forChildElement, skill_id, skill_type, operate_type);
						}
					}
				}
			}
			
//			SkillBuilder.getInstance().addElement(item_id, skillElement);
		}
	}
	

	@Override
	protected Object getTableValue(String name)
	{
		return null;
	}
}
