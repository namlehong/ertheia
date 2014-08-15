package l2s.gameserver.data.xml.parser;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Element;
import l2s.commons.data.xml.AbstractFileParser;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.CrystallizationDataHolder;
import l2s.gameserver.templates.item.ItemGrade;
import l2s.gameserver.templates.item.support.CrystallizationInfo;

/**
 * @author Bonux
**/
public final class CrystallizationDataParser extends AbstractFileParser<CrystallizationDataHolder>
{
	private static final CrystallizationDataParser _instance = new CrystallizationDataParser();

	public static CrystallizationDataParser getInstance()
	{
		return _instance;
	}

	protected CrystallizationDataParser()
	{
		super(CrystallizationDataHolder.getInstance());
	}

	@Override
	public File getXMLFile()
	{
		return new File(Config.DATAPACK_ROOT, "data/crystallization_data.xml");
	}

	@Override
	public String getDTDFileName()
	{
		return "crystallization_data.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for(Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext();)
		{
			Element gradeElement = iterator.next();
			ItemGrade grade = ItemGrade.valueOf(gradeElement.attributeValue("type").toUpperCase());

			TIntObjectHashMap<CrystallizationInfo> crystals = new TIntObjectHashMap<CrystallizationInfo>();
			for(Iterator<Element> crystalIterator = gradeElement.elementIterator("crystal"); crystalIterator.hasNext();)
			{
				Element crystalElement = crystalIterator.next();
				int crystal_count = Integer.parseInt(crystalElement.attributeValue("count"));
				CrystallizationInfo info = new CrystallizationInfo();

				for(Iterator<Element> itemIterator = crystalElement.elementIterator("item"); itemIterator.hasNext();)
				{
					Element itemElement = itemIterator.next();
					int id = Integer.parseInt(itemElement.attributeValue("id"));
					int count = Integer.parseInt(itemElement.attributeValue("count"));
					double chance = Double.parseDouble(itemElement.attributeValue("chance"));

					info.addItem(new CrystallizationInfo.CrystallizationItem(id, count, chance));
				}
				crystals.put(crystal_count, info);
			}

			getHolder().addData(grade, crystals);
		}
	}
}
