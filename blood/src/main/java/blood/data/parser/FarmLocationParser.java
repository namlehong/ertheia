package blood.data.parser;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;

import l2s.commons.data.xml.AbstractFileParser;
import l2s.gameserver.Config;

import org.dom4j.Element;

import blood.data.holder.FarmLocationHolder;
import blood.model.FarmLocation;

public final class FarmLocationParser  extends AbstractFileParser<FarmLocationHolder>{
	/**
	 * Field _instance.
	 */
	private static final FarmLocationParser _instance = new FarmLocationParser();
	
	/**
	 * Method getInstance.
	 * @return LevelBonusParser
	 */
	public static FarmLocationParser getInstance()
	{
		return _instance;
	}
	
	/**
	 * Constructor for LevelBonusParser.
	 */
	private FarmLocationParser()
	{
		super(FarmLocationHolder.getInstance());
	}
	
	/**
	 * Method getXMLFile.
	 * @return File
	 */
	@Override
	public File getXMLFile()
	{
		return new File(Config.DATAPACK_ROOT, "data/blood/fp_loc.xml");
	}
	
	/**
	 * Method getDTDFileName.
	 * @return String
	 */
	@Override
	public String getDTDFileName()
	{
		return "fp_loc.dtd";
	}
	
	/**
	 * Method readData.
	 * @param rootElement Element
	 * @throws Exception
	 */
	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for (Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext();)
		{
			Element groupElement = iterator.next();
			int min_level = Integer.parseInt(groupElement.attributeValue("min_level"));
			int max_level = Integer.parseInt(groupElement.attributeValue("max_level"));
			boolean is_party = Boolean.parseBoolean(groupElement.attributeValue("is_party"));
			String[] class_ids_str = groupElement.attributeValue("class_ids") != null ? groupElement.attributeValue("class_ids").split(",") : new String[]{};
			HashSet<Integer> class_ids = new HashSet<Integer>();
			
			for(int i = 0;i < class_ids_str.length;i++)
			{
				class_ids.add(Integer.parseInt(class_ids_str[i]));
			}
						 
			for (Element locElement : groupElement.elements())
			{
				int x = Integer.parseInt(locElement.attributeValue("x"));
				int y = Integer.parseInt(locElement.attributeValue("y"));
				int z = Integer.parseInt(locElement.attributeValue("z"));
				
				FarmLocation farmLoc = new FarmLocation(min_level, max_level, is_party, class_ids);
				farmLoc.set(x, y, z);
				getHolder().addLoc(farmLoc);
			}
		}
	}

}
