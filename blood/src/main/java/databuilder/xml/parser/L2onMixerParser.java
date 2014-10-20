package databuilder.xml.parser;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import databuilder.utils.XmlPretty;
import databuilder.xml.builder.NpcBuilder;
import databuilder.xml.holder.L2onNpcHolder;
import databuilder.xml.holder.L2onNpcHolder.L2onNpcInfo;
import l2s.commons.data.xml.AbstractDirParser;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.NpcHolder;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.base.Element;
import l2s.gameserver.model.reward.RewardData;
import l2s.gameserver.model.reward.RewardGroup;
import l2s.gameserver.model.reward.RewardList;
import l2s.gameserver.model.reward.RewardType;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.templates.TeleportLocation;
import l2s.gameserver.templates.npc.AbsorbInfo;
import l2s.gameserver.templates.npc.Faction;
import l2s.gameserver.templates.npc.MinionData;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.templates.npc.RandomActions;
import l2s.gameserver.templates.npc.WalkerRoute;
import l2s.gameserver.templates.npc.WalkerRoutePoint;
import l2s.gameserver.templates.npc.WalkerRouteType;
import l2s.gameserver.utils.Location;

/**
 * @author VISTALL
 * @date  16:16/14.12.2010
 */
public final class L2onMixerParser extends AbstractDirParser<NpcHolder>
{
	private static final L2onMixerParser _instance = new L2onMixerParser();

	public static L2onMixerParser getInstance()
	{
		return _instance;
	}

	private L2onMixerParser()
	{
		super(NpcHolder.getInstance());
	}

	@Override
	public File getXMLDir()
	{
		return new File(Config.DATAPACK_ROOT, "data/npc/");
	}

	@Override
	public boolean isIgnored(File f)
	{
		return false;
	}

	@Override
	public String getDTDFileName()
	{
		return "npc.dtd";
	}

	@Override
	protected void readData(org.dom4j.Element rootElement) throws Exception
	{
		for(Iterator<org.dom4j.Element> npcIterator = rootElement.elementIterator(); npcIterator.hasNext();)
		{
			org.dom4j.Element npcElement = npcIterator.next();
			int npcId = Integer.parseInt(npcElement.attributeValue("id"));
			
			L2onNpcInfo info = L2onNpcHolder.getInstance().getNpcInfoNoCreate(npcId);
			
			if(info == null || info._level == 0){
				System.out.println("Missing data "+npcId);
				continue;
			}

			for(Iterator<org.dom4j.Element> firstIterator = npcElement.elementIterator(); firstIterator.hasNext();)
			{
				org.dom4j.Element firstElement = firstIterator.next();
				if(firstElement.getName().equalsIgnoreCase("set"))
				{
					if(firstElement.attributeValue("name").equalsIgnoreCase("rewardExp")){
						firstElement.addAttribute("value", String.valueOf(info._exp));
					}
					if(firstElement.attributeValue("name").equalsIgnoreCase("rewardSp")){
						firstElement.addAttribute("value", String.valueOf(info._sp));
					}
					if(firstElement.attributeValue("name").equalsIgnoreCase("level")){
						firstElement.addAttribute("value", String.valueOf(info._level));
					}
					if(firstElement.attributeValue("name").equalsIgnoreCase("baseHpMax")){
						firstElement.addAttribute("value", String.valueOf(info._hp));
					}
				}
				
			}
		}
		
		XmlPretty.writeToFile(getCurrentFileName().replace(".xml", ""), rootElement.asXML(), "npc.dtd", "data/blood_npc/");
	}

	
}
