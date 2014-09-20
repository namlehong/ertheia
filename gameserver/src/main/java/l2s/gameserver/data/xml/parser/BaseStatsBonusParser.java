package l2s.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Element;
import l2s.commons.data.xml.AbstractFileParser;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.BaseStatsBonusHolder;
import l2s.gameserver.templates.BaseStatsBonus;

/**
 * @author Bonux
**/
public final class BaseStatsBonusParser extends AbstractFileParser<BaseStatsBonusHolder>
{
	private static final BaseStatsBonusParser _instance = new BaseStatsBonusParser();

	public static BaseStatsBonusParser getInstance()
	{
		return _instance;
	}

	private BaseStatsBonusParser()
	{
		super(BaseStatsBonusHolder.getInstance());
	}

	@Override
	public File getXMLFile()
	{
		return new File(Config.DATAPACK_ROOT, "data/pc_parameters/base_stats_bonus_data.xml");
	}

	@Override
	public String getDTDFileName()
	{
		return "base_stats_bonus_data.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for(Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext();)
		{
			Element element = iterator.next();
			if("base_stats_bonus".equalsIgnoreCase(element.getName()))
			{
				for(Element e : element.elements())
				{
					int value = Integer.parseInt(e.attributeValue("value"));
					double str = (1000. + Integer.parseInt(e.attributeValue("str"))) / 1000;
					double _int = (1000. + Integer.parseInt(e.attributeValue("int"))) / 1000;
					double dex = (1000. + Integer.parseInt(e.attributeValue("dex"))) / 1000;
					double wit = (1000. + Integer.parseInt(e.attributeValue("wit"))) / 1000;
					double con = (1000. + Integer.parseInt(e.attributeValue("con"))) / 1000;
					double men = (1000. + Integer.parseInt(e.attributeValue("men"))) / 1000;
					double luc = (1000. + Integer.parseInt(e.attributeValue("luc"))) / 1000;
					double cha = (1000. + Integer.parseInt(e.attributeValue("cha"))) / 1000;

					getHolder().addBaseStatsBonus(value, new BaseStatsBonus(_int, str, con, men, dex, wit, luc, cha));
				}
			}
		}
	}
}