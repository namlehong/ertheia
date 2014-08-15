package l2s.gameserver.data.xml.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import l2s.commons.data.xml.AbstractFileParser;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.StatuesSpawnHolder;
import l2s.gameserver.model.worldstatistics.CategoryType;
import l2s.gameserver.utils.Location;

/**
 * @author Дмитрий
 * @modified KilRoy
 * @date 17.10.12 9:39
 */
public final class StatuesSpawnParser extends AbstractFileParser<StatuesSpawnHolder>
{
	private static final StatuesSpawnParser _instance = new StatuesSpawnParser();

	public static StatuesSpawnParser getInstance()
	{
		return _instance;
	}

	protected StatuesSpawnParser()
	{
		super(StatuesSpawnHolder.getInstance());
	}

	@Override
	public File getXMLFile()
	{
		return new File(Config.DATAPACK_ROOT, "data/statues_spawns.xml");
	}

	@Override
	public String getDTDFileName()
	{
		return "statues_spawns.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for(Element statuesElement : rootElement.elements())
		{
			int type = Integer.parseInt(statuesElement.attributeValue("type"));
			CategoryType categoryType = CategoryType.getCategoryById(type, 0);

			List<Location> locations = new ArrayList<Location>();
			for (Element spawnElement : statuesElement.elements())
			{
				String[] loc = spawnElement.attributeValue("loc").split(",");
				locations.add(new Location(Integer.parseInt(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2]), Integer.parseInt(loc[3])));
			}
			getHolder().addSpawnInfo(categoryType, locations);
		}
	}
}
