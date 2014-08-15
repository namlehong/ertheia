package l2s.gameserver.data.xml.parser;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Element;
import l2s.commons.data.xml.AbstractFileParser;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.EnchantSkillGroupHolder;
import l2s.gameserver.templates.EnchantSkillGroup;
import l2s.gameserver.templates.EnchantSkillGroup.EnchantSkillLevel;

/**
 * @author Bonux
**/
public final class EnchantSkillGroupParser extends AbstractFileParser<EnchantSkillGroupHolder>
{
	private static final EnchantSkillGroupParser _instance = new EnchantSkillGroupParser();

	public static EnchantSkillGroupParser getInstance()
	{
		return _instance;
	}

	protected EnchantSkillGroupParser()
	{
		super(EnchantSkillGroupHolder.getInstance());
	}

	@Override
	public File getXMLFile()
	{
		return new File(Config.DATAPACK_ROOT, "data/skill_enchant_groups.xml");
	}

	@Override
	public String getDTDFileName()
	{
		return "skill_enchant_groups.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for(Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext();)
		{
			Element groupElement = iterator.next();
			int id = Integer.parseInt(groupElement.attributeValue("id"));

			TIntObjectHashMap<EnchantSkillLevel> levels = new TIntObjectHashMap<EnchantSkillLevel>();
			for(Iterator<Element> levelIterator = groupElement.elementIterator("enchant"); levelIterator.hasNext();)
			{
				Element levelElement = levelIterator.next();
				int level = Integer.parseInt(levelElement.attributeValue("level"));
				int adena = Integer.parseInt(levelElement.attributeValue("adena"));
				int sp = Integer.parseInt(levelElement.attributeValue("sp"));
				String[] successRates = levelElement.attributeValue("success_rate").split(",");
				int[] chances = new int[successRates.length];
				for(int i = 0; i < successRates.length; i++)
					chances[i] = Integer.parseInt(successRates[i]);

				levels.put(level, new EnchantSkillLevel(level, adena, sp, chances));
			}

			getHolder().addGroup(new EnchantSkillGroup(id, levels));
		}
	}
}
