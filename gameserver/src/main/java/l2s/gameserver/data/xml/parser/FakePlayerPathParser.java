package l2s.gameserver.data.xml.parser;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.io.File;
import java.util.Iterator;

import l2s.commons.data.xml.AbstractDirParser;
import l2s.commons.string.StringArrayUtils;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.FakePlayerPathHolder;
import l2s.gameserver.templates.npc.FakePlayerPath;
import l2s.gameserver.utils.Location;

import org.dom4j.Element;

/**
 * @author Bonux
**/
public final class FakePlayerPathParser extends AbstractDirParser<FakePlayerPathHolder>
{
	private static final FakePlayerPathParser _instance = new FakePlayerPathParser();

	public static FakePlayerPathParser getInstance()
	{
		return _instance;
	}

	private FakePlayerPathParser()
	{
		super(FakePlayerPathHolder.getInstance());
	}

	@Override
	public File getXMLDir()
	{
		return new File(Config.DATAPACK_ROOT, "data/fake_players/pathes/");
	}

	@Override
	public boolean isIgnored(File f)
	{
		return false;
	}

	@Override
	public String getDTDFileName()
	{
		return "fake_player_path.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for(Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext();)
		{
			Element element = iterator.next();

			int id = Integer.parseInt(element.attributeValue("id"));
			Location loc = Location.parse(element);
			TIntSet availNextPathes = new TIntHashSet(StringArrayUtils.stringToIntArray(element.attributeValue("avail_next_pathes") == null ? "-1" : element.attributeValue("avail_next_pathes"), ";"));

			FakePlayerPath path = new FakePlayerPath(id, loc, availNextPathes);
			path.addPoint(new FakePlayerPath.Point(0, loc, -1, 5, 10, false, 50));

			for(Iterator<Element> firstIterator = element.elementIterator(); firstIterator.hasNext();)
			{
				Element firstElement = firstIterator.next();
				if(firstElement.getName().equalsIgnoreCase("point"))
				{
					int pointId = Integer.parseInt(firstElement.attributeValue("id"));
					Location pointLoc = Location.parse(firstElement);
					int nextPointId = firstElement.attributeValue("next_point_id") == null ? -1 : Integer.parseInt(firstElement.attributeValue("next_point_id"));
					int minDelay = firstElement.attributeValue("min_delay") == null ? 15 : Integer.parseInt(firstElement.attributeValue("min_delay"));
					int maxDelay = firstElement.attributeValue("max_delay") == null ? 30 : Integer.parseInt(firstElement.attributeValue("max_delay"));
					boolean sitting = firstElement.attributeValue("sitting") == null ? false : Boolean.parseBoolean(firstElement.attributeValue("sitting"));
					int offset = firstElement.attributeValue("offset") == null ? 50 : Integer.parseInt(firstElement.attributeValue("offset"));

					path.addPoint(new FakePlayerPath.Point(pointId, pointLoc, nextPointId, minDelay, maxDelay, sitting, offset));
				}
			}

			getHolder().addPath(path);
		}
	}
}
