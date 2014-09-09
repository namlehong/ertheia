package blood.data.parser;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import l2s.commons.data.xml.AbstractFileParser;
import l2s.gameserver.Config;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassType;
import l2s.gameserver.model.base.ClassType2;

import org.dom4j.Element;

import blood.data.holder.FPItemHolder;
import blood.model.FPRewardList;

public final class FPItemParser extends AbstractFileParser<FPItemHolder> 
{
	/**
	 * Field _instance.
	 */
	private static final FPItemParser _instance = new FPItemParser();
	
	/**
	 * Method getInstance.
	 * @return LevelBonusParser
	 */
	public static FPItemParser getInstance()
	{
		return _instance;
	}
	
	/**
	 * Constructor for LevelBonusParser.
	 */
	private FPItemParser()
	{
		super(FPItemHolder.getInstance());
	}
	
	/**
	 * Method getXMLFile.
	 * @return File
	 */
	@Override
	public File getXMLFile()
	{
		return new File(Config.DATAPACK_ROOT, "data/blood/fp_item.xml");
	}
	
	/**
	 * Method getDTDFileName.
	 * @return String
	 */
	@Override
	public String getDTDFileName()
	{
		return "fp_item.dtd";
	}
	
	/**
	 * Method readData.
	 * @param rootElement Element
	 * @throws Exception
	 */
	@Override
	protected void readData(Element rootElement) throws Exception
	{
		HashMap<String, ClassId> classIdMap = new HashMap<String, ClassId>();
		HashMap<String, ClassType> classTypeMap = new HashMap<String, ClassType>();
		HashMap<String, ClassType2> classType2Map = new HashMap<String, ClassType2>();
		
		for(ClassId classId: ClassId.VALUES)
			classIdMap.put(classId.toString(), classId);
		
		for(ClassType classType: ClassType.VALUES)
			classTypeMap.put(classType.toString(), classType);
		
		for(ClassType2 classType2: ClassType2.VALUES)
			classType2Map.put(classType2.toString(), classType2);
		
		for (Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext();)
		{
			Element rewardElement = iterator.next();
			
			String reward_name = rewardElement.attributeValue("name");
			String parent_name = rewardElement.attributeValue("parent");
			int min_level = rewardElement.attributeValue("min_level") != null ? Integer.parseInt(rewardElement.attributeValue("min_level")): 0;
			int max_level = rewardElement.attributeValue("max_level") != null ? Integer.parseInt(rewardElement.attributeValue("max_level")): 0;
			int weight = rewardElement.attributeValue("weight") != null ? Integer.parseInt(rewardElement.attributeValue("weight")) : 1;
			
			
			FPRewardList rewardList = new FPRewardList(reward_name, min_level, max_level, parent_name, weight);
			
			for (Element subElement: rewardElement.elements())
			{
				if(subElement.getName().equalsIgnoreCase("item")){
					int item_id = Integer.parseInt(subElement.attributeValue("id"));
					int item_count = Integer.parseInt(subElement.attributeValue("count"));
					rewardList.addItem(item_id, item_count);
				}
				
				if(subElement.getName().equalsIgnoreCase("remove_item")){
					int remove_item_id = Integer.parseInt(subElement.attributeValue("id"));
					rewardList.addRemoveItem(remove_item_id);
				}
				
				if(subElement.getName().equalsIgnoreCase("classid")){
					rewardList.addClassId(classIdMap.get(subElement.attributeValue("value")));
				}
				
				if(subElement.getName().equalsIgnoreCase("classtype")){
					rewardList.addClassType(classTypeMap.get(subElement.attributeValue("value")));
				}
				
				if(subElement.getName().equalsIgnoreCase("classtype2")){
					rewardList.addClassType2(classType2Map.get(subElement.attributeValue("value")));
				}	
			}
			
			getHolder().add(reward_name, rewardList);
		}
	}
}
