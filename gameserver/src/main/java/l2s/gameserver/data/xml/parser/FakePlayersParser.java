package l2s.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;

import l2s.commons.data.xml.AbstractDirParser;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.FakePlayersHolder;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.templates.npc.FakePlayerTemplate;

import org.dom4j.Element;

/**
 * @author Bonux
**/
public final class FakePlayersParser extends AbstractDirParser<FakePlayersHolder>
{
	private static final FakePlayersParser _instance = new FakePlayersParser();

	public static FakePlayersParser getInstance()
	{
		return _instance;
	}

	private FakePlayersParser()
	{
		super(FakePlayersHolder.getInstance());
	}

	@Override
	public File getXMLDir()
	{
		return new File(Config.DATAPACK_ROOT, "data/fake_players/players/");
	}

	@Override
	public boolean isIgnored(File f)
	{
		return false;
	}

	@Override
	public String getDTDFileName()
	{
		return "fake_player.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for(Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext();)
		{
			Element element = iterator.next();

			int id = Integer.parseInt(element.attributeValue("id"));
			String name = element.attributeValue("name");
			String title = element.attributeValue("title") != null ? element.attributeValue("title") : "";

			StatsSet set = FakePlayerTemplate.getEmptyStatsSet();
			set.set("id", id);
			set.set("npcId", id * -10000);
			set.set("displayId", id * -10000);
			set.set("name", name);
			set.set("title", title);

			set.set("rewardExp", 0);
			set.set("rewardSp", 0);
			set.set("rewardRp", 0);
			set.set("aggroRange", 0);
			set.set("baseHpRate", 1.0);

			for(Iterator<Element> firstIterator = element.elementIterator(); firstIterator.hasNext();)
			{
				Element firstElement = firstIterator.next();
				if(firstElement.getName().equalsIgnoreCase("set"))
				{
					set.set(firstElement.attributeValue("name"), firstElement.attributeValue("value"));
				}
				else if(firstElement.getName().equalsIgnoreCase("equip"))
				{
					for(Iterator<Element> eIterator = firstElement.elementIterator(); eIterator.hasNext();)
					{
						Element eElement = eIterator.next();
						set.set(eElement.getName(), eElement.attributeValue("item_id"));
					}
				}
				else if(firstElement.getName().equalsIgnoreCase("ai_params"))
				{
					StatsSet ai = new StatsSet();
					for(Iterator<Element> eIterator = firstElement.elementIterator(); eIterator.hasNext();)
					{
						Element eElement = eIterator.next();
						ai.set(eElement.attributeValue("name"), eElement.attributeValue("value"));
					}
					set.set("aiParams", ai);
				}
			}

			FakePlayerTemplate template = new FakePlayerTemplate(set);

			for(Iterator<Element> firstIterator = element.elementIterator(); firstIterator.hasNext();)
			{
				Element firstElement = firstIterator.next();
				if(firstElement.getName().equalsIgnoreCase("buffs"))
				{
					for(Iterator<Element> eIterator = firstElement.elementIterator(); eIterator.hasNext();)
					{
						Element eElement = eIterator.next();

						int skillId = Integer.parseInt(eElement.attributeValue("id"));
						int skillLvl = Integer.parseInt(eElement.attributeValue("level"));
						template.addBuff(skillId, skillLvl);
					}
				}
			}

			getHolder().addTemplate(template);
		}
	}
}
