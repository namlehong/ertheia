package l2s.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Element;
import l2s.commons.data.xml.AbstractDirParser;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.RestorationInfoHolder;
import l2s.gameserver.templates.skill.restoration.RestorationGroup;
import l2s.gameserver.templates.skill.restoration.RestorationInfo;
import l2s.gameserver.templates.skill.restoration.RestorationItem;

/**
 * @author Bonux
 */
public final class RestorationInfoParser extends AbstractDirParser<RestorationInfoHolder>
{
	private static final RestorationInfoParser _instance = new RestorationInfoParser();

	public static RestorationInfoParser getInstance()
	{
		return _instance;
	}

	protected RestorationInfoParser()
	{
		super(RestorationInfoHolder.getInstance());
	}

	@Override
	public File getXMLDir()
	{
		return new File(Config.DATAPACK_ROOT, "data/skills/restoration_info");
	}

	@Override
	public boolean isIgnored(File f)
	{
		return false;
	}

	@Override
	public String getDTDFileName()
	{
		return "restoration_info.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for(Iterator<Element> restorationIterator = rootElement.elementIterator(); restorationIterator.hasNext();)
		{
			Element restorationElement = restorationIterator.next();
			int skillId = Integer.parseInt(restorationElement.attributeValue("skill_id"));
			int skillLvl = Integer.parseInt(restorationElement.attributeValue("skill_level"));
			int consumeItemId = restorationElement.attributeValue("consume_item_id") == null ? -1 : Integer.parseInt(restorationElement.attributeValue("consume_item_id"));
			int consumeItemCount = restorationElement.attributeValue("consume_item_count") == null ? 1 : Integer.parseInt(restorationElement.attributeValue("consume_item_count"));
			RestorationInfo restorationInfo = new RestorationInfo(skillId, skillLvl, consumeItemId, consumeItemCount);
			for(Iterator<Element> groupIterator = restorationElement.elementIterator(); groupIterator.hasNext();)
			{
				Element groupElement = groupIterator.next();
				double chance = Double.parseDouble(groupElement.attributeValue("chance"));
				RestorationGroup restorationGroup = new RestorationGroup(chance);
				for(Iterator<Element> itemIterator = groupElement.elementIterator(); itemIterator.hasNext();)
				{
					Element itemElement = itemIterator.next();
					int id = Integer.parseInt(itemElement.attributeValue("id"));
					int minCount = Integer.parseInt(itemElement.attributeValue("min_count"));
					int maxCount = itemElement.attributeValue("max_count") == null ? minCount : Integer.parseInt(itemElement.attributeValue("max_count"));
					restorationGroup.addRestorationItem(new RestorationItem(id, minCount, maxCount));
				}
				restorationInfo.addRestorationGroup(restorationGroup);
			}
			getHolder().addRestorationInfo(restorationInfo);
		}
	}
}
