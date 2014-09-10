package blood.data.parser;

import java.io.File;
import java.util.HashSet;

import l2s.commons.data.xml.AbstractFileParser;
import l2s.gameserver.Config;

import org.dom4j.Element;

import blood.data.holder.NamePatternHolder;

public final class NamePatternParser extends AbstractFileParser<NamePatternHolder> 
{
	/**
	 * Field _instance.
	 */
	private static final NamePatternParser _instance = new NamePatternParser();
	
	/**
	 * Method getInstance.
	 * @return LevelBonusParser
	 */
	public static NamePatternParser getInstance()
	{
		return _instance;
	}
	
	/**
	 * Constructor for LevelBonusParser.
	 */
	private NamePatternParser()
	{
		super(NamePatternHolder.getInstance());
	}
	
	/**
	 * Method getXMLFile.
	 * @return File
	 */
	@Override
	public File getXMLFile()
	{
		return new File(Config.DATAPACK_ROOT, "data/blood/fp_name_pattern.xml");
	}
	
	/**
	 * Method getDTDFileName.
	 * @return String
	 */
	@Override
	public String getDTDFileName()
	{
		return "fp_name_pattern.dtd";
	}
	
	/**
	 * Method readData.
	 * @param rootElement Element
	 * @throws Exception
	 */
	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for (Element categoryElement: rootElement.elements())
		{
			String category = categoryElement.attributeValue("value");
			HashSet<String> names = new HashSet<String>(); 
						
			for (Element wordElement: categoryElement.elements())
			{
				names.add(wordElement.attributeValue("value"));
			}
			
			getHolder().add(category, names);
		}
	}
}
