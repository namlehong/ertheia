package databuilder.xml.parser;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.StringUtils;

import databuilder.xml.builder.ItemBuilder;
import databuilder.xml.builder.SkillBuilder;
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
public final class SkillParser extends StatParser<ItemHolder>
{
	private static final SkillParser _instance = new SkillParser();

	public static SkillParser getInstance()
	{
		return _instance;
	}

	protected SkillParser()
	{
		super(ItemHolder.getInstance());
		ItemHolder.getInstance().info("Start parsing..");
	}

	@Override
	public File getXMLDir()
	{
		return new File(Config.DATAPACK_ROOT, "data/skills/");
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
			
			int item_id = Integer.parseInt(skillElement.attributeValue("id"));
			SkillBuilder.getInstance().addElement(item_id, skillElement);
		}
	}

	@Override
	protected Object getTableValue(String name)
	{
		return null;
	}
}
