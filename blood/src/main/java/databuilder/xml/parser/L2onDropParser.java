package databuilder.xml.parser;

import java.io.File;
import java.util.Iterator;

import l2s.commons.data.xml.AbstractDirParser;
import l2s.gameserver.Config;
import l2s.gameserver.model.reward.RewardData;
import databuilder.xml.holder.L2onDropHolder;
import databuilder.xml.holder.L2onDropHolder.L2DropInfo;

/**
 * @author VISTALL
 * @date  16:16/14.12.2010
 */
public final class L2onDropParser extends AbstractDirParser<L2onDropHolder>
{
	private static final L2onDropParser _instance = new L2onDropParser();

	public static L2onDropParser getInstance()
	{
		return _instance;
	}

	private L2onDropParser()
	{
		super(L2onDropHolder.getInstance());
	}

	@Override
	public File getXMLDir()
	{
		return new File(Config.DATAPACK_ROOT, "data/l2on_ertheia/");
	}

	@Override
	public boolean isIgnored(File f)
	{
		return false;
	}

	@Override
	public String getDTDFileName()
	{
		return "DropListData.dtd";
	}

	@Override
	protected void readData(org.dom4j.Element rootElement) throws Exception
	{
		for(Iterator<org.dom4j.Element> npcIterator = rootElement.elementIterator(); npcIterator.hasNext();)
		{
			org.dom4j.Element npcElement = npcIterator.next();
			int npcId = Integer.parseInt(npcElement.attributeValue("id"));
			
			L2DropInfo info = getHolder().getDropInfo(npcId);
			
			for(Iterator<org.dom4j.Element> firstIterator = npcElement.elementIterator(); firstIterator.hasNext();)
			{
				org.dom4j.Element firstElement = firstIterator.next();
				if(firstElement.getName().equalsIgnoreCase("droplist"))
				{
					for(org.dom4j.Element item: firstElement.elements())
					{
						info.addDrop(parseReward(item));
					}
				}
				else if(firstElement.getName().equalsIgnoreCase("spoillist"))
				{
					for(org.dom4j.Element item: firstElement.elements())
					{
						info.addSpoil(parseReward(item));
					}
				}
				
			}
		}
	}

	private RewardData parseReward(org.dom4j.Element rewardElement)
	{
		int itemId = Integer.parseInt(rewardElement.attributeValue("id"));
		int min = Integer.parseInt(rewardElement.attributeValue("min"));
		int max = Integer.parseInt(rewardElement.attributeValue("max"));
		// переводим в системный вид
		double orig_chance = Double.parseDouble(rewardElement.attributeValue("chance"));
//		if(orig_chance > 1.)
//			orig_chance = Math.round(orig_chance*4)/4;
		int chance = (int) (orig_chance * 10000);

		RewardData data = new RewardData(itemId);
		data.setChance(chance);

		data.setMinDrop(min);
		data.setMaxDrop(max);

		return data;
	}
}
