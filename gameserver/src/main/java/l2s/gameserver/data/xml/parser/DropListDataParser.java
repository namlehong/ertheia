/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2s.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;

import l2s.commons.data.xml.AbstractDirParser;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.NpcHolder;
import l2s.gameserver.model.reward.RewardData;
import l2s.gameserver.model.reward.RewardGroup;
import l2s.gameserver.model.reward.RewardList;
import l2s.gameserver.model.reward.RewardType;
import l2s.gameserver.templates.npc.NpcTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mobius
 * @version $Revision: 1.0 $
 */
public final class DropListDataParser extends AbstractDirParser<NpcHolder>
{
	/**
	 * Field _log.
	 */
	private static final Logger _log = LoggerFactory.getLogger(DropListDataParser.class);
	
	/**
	 * Field _dropsParsed.
	 */
	private int _dropsParsed;
	/**
	 * Field _spoilsParsed.
	 */
	private int _spoilsParsed;
	
	/**
	 * Field _instance.
	 */
	private static final DropListDataParser _instance = new DropListDataParser();
	
	/**
	 * Method getInstance.
	 * @return NpcParser
	 */
	public static DropListDataParser getInstance()
	{
		return _instance;
	}
	
	/**
	 * Constructor for NpcParser.
	 */
	private DropListDataParser()
	{
		super(NpcHolder.getInstance());
	}
	
	/**
	 * Method getXMLDir.
	 * @return File
	 */
	@Override
	public File getXMLDir()
	{
		return new File(Config.DATAPACK_ROOT, "data/l2on_droplist/");
	}
	
	/**
	 * Method isIgnored.
	 * @param f File
	 * @return boolean
	 */
	@Override
	public boolean isIgnored(File f)
	{
		return false;
	}
	
	/**
	 * Method getDTDFileName.
	 * @return String
	 */
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
			
			NpcTemplate template = NpcHolder.getInstance().getTemplate(npcId);
			if (template == null)
			{
				_log.warn("Omitted NPC ID: " + npcId + " - NPC template does not exists!");
				continue;
			}
			
			RewardList list = null;
			RewardType type = null;
			
			for (Iterator<org.dom4j.Element> firstIterator = npcElement.elementIterator(); firstIterator.hasNext();)
			{
				org.dom4j.Element firstElement = firstIterator.next();
				if (firstElement.getName().equalsIgnoreCase("droplist"))
				{
					type = RewardType.RATED_GROUPED;
					list = new RewardList(type, false);
					for (Iterator<org.dom4j.Element> eIterator = firstElement.elementIterator(); eIterator.hasNext();)
					{
						org.dom4j.Element eElement = eIterator.next();
						if(eElement.getName().equalsIgnoreCase("category"))
						{
//							int chance = (int) (parseDouble(attrs, "chance") * 10000.0D);
							double enterChance = eElement.attributeValue("chance") == null ? RewardList.MAX_CHANCE : Double.parseDouble(eElement.attributeValue("chance")) * 10000;
							RewardGroup group = new RewardGroup(enterChance);
							for (Iterator<org.dom4j.Element> itemIterator = eElement.elementIterator(); itemIterator.hasNext();)
							{
								org.dom4j.Element itemElement = itemIterator.next();
								if(itemElement.getName().equalsIgnoreCase("item"))
								{
									RewardData data = parseReward(itemElement);
									group.addData(data);
								}
							}
							list.add(group);
						}
					}
				}
				else if (firstElement.getName().equalsIgnoreCase("spoillist"))
				{
					RewardGroup g = new RewardGroup(RewardList.MAX_CHANCE);
					type = RewardType.SWEEP;
					list = new RewardList(type, false);
					
					for (Iterator<org.dom4j.Element> eIterator = firstElement.elementIterator(); eIterator.hasNext();)
					{
						org.dom4j.Element eElement = eIterator.next();
						RewardData data = parseReward(eElement);
						g.addData(data);
					}
					list.add(g);
				}
				
				template.putRewardList(type, list);
			}
			
		}
	}
	
//	/**
//	 * Method parseDocument.
//	 */
//	@Override
//	protected void parseDocument()
//	{
//		for (Node globalNode = getCurrentDocument().getFirstChild(); globalNode != null; globalNode = globalNode.getNextSibling())
//		{
//			if (!"list".equalsIgnoreCase(globalNode.getNodeName()))
//			{
//				continue;
//			}
//			for (Node npcNode = globalNode.getFirstChild(); npcNode != null; npcNode = npcNode.getNextSibling())
//			{
//				if (!"npc".equalsIgnoreCase(npcNode.getNodeName()))
//				{
//					continue;
//				}
//				NamedNodeMap attrs = npcNode.getAttributes();
//				RewardList list = null;
//				RewardType type = null;
//				int npcId = parseInt(attrs, "id");
//				NpcTemplate template = NpcHolder.getInstance().getTemplate(npcId);
//				if (template == null)
//				{
//					_log.warn("Omitted NPC ID: " + npcId + " - NPC template does not exists!");
//				}
//				else
//				{
//					for (Node dropNode = npcNode.getFirstChild(); dropNode != null; dropNode = dropNode.getNextSibling())
//					{
//						if ("droplist".equalsIgnoreCase(dropNode.getNodeName()))
//						{
//							type = RewardType.RATED_GROUPED;
//							list = new RewardList(type, false);
//							for (Node catNode = dropNode.getFirstChild(); catNode != null; catNode = catNode.getNextSibling())
//							{
//								if (!"category".equalsIgnoreCase(catNode.getNodeName()))
//								{
//									continue;
//								}
//								attrs = catNode.getAttributes();
//								int chance = (int) (parseDouble(attrs, "chance") * 10000.0D);
//								RewardGroup group = new RewardGroup(chance);
//								for (Node itemNode = catNode.getFirstChild(); itemNode != null; itemNode = itemNode.getNextSibling())
//								{
//									if (!"item".equalsIgnoreCase(itemNode.getNodeName()))
//									{
//										continue;
//									}
//									_dropsParsed += 1;
//									attrs = itemNode.getAttributes();
//									RewardData data = parseReward(attrs);
//									group.addData(data);
//								}
//								list.add(group);
//							}
//						}
//						else
//						{
//							if (!"spoillist".equalsIgnoreCase(dropNode.getNodeName()))
//							{
//								continue;
//							}
//							RewardGroup g = new RewardGroup(RewardList.MAX_CHANCE);
//							type = RewardType.SWEEP;
//							list = new RewardList(type, false);
//							for (Node itemNode = dropNode.getFirstChild(); itemNode != null; itemNode = itemNode.getNextSibling())
//							{
//								if (!"item".equalsIgnoreCase(itemNode.getNodeName()))
//								{
//									continue;
//								}
//								_spoilsParsed += 1;
//								attrs = itemNode.getAttributes();
//								RewardData data = parseReward(attrs);
//								g.addData(data);
//							}
//							list.add(g);
//						}
//						template.putRewardList(type, list);
//					}
//				}
//			}
//		}
//	}
	
	/**
	 * Method parseReward.
	 * @param attrs NamedNodeMap
	 * @return RewardData
	 */
//	private RewardData parseReward(NamedNodeMap attrs)
//	{
//		int itemId = parseInt(attrs, "id");
//		int min = parseInt(attrs, "min");
//		int max = parseInt(attrs, "max");
//		double chance = parseDouble(attrs, "chance") * 10000.0D;
//		RewardData data = new RewardData(itemId);
//		if (data.getItem().isCommonItem())
//		{
//			data.setChance(chance * Config.RATE_DROP_COMMON_ITEMS);
//		}
//		else
//		{
//			data.setChance(chance);
//		}
//		data.setMinDrop(min);
//		data.setMaxDrop(max);
//		return data;
//	}
	
	private RewardData parseReward(org.dom4j.Element rewardElement)
	{
		int itemId = Integer.parseInt(rewardElement.attributeValue("id"));
		int min = Integer.parseInt(rewardElement.attributeValue("min"));
		int max = Integer.parseInt(rewardElement.attributeValue("max"));
		int chance = (int) (Double.parseDouble(rewardElement.attributeValue("chance")) * 10000);
		RewardData data = new RewardData(itemId);
		if (data.getItem().isCommonItem())
		{
			data.setChance(chance * Config.RATE_DROP_COMMON_ITEMS);
		}
		else
		{
			data.setChance(chance);
		}
		data.setMinDrop(min);
		data.setMaxDrop(max);
		return data;
	}
}
