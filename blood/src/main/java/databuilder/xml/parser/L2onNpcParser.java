package databuilder.xml.parser;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
public final class L2onNpcParser extends AbstractDirParser<L2onNpcHolder>
{
	private static final L2onNpcParser _instance = new L2onNpcParser();

	public static L2onNpcParser getInstance()
	{
		return _instance;
	}

	private L2onNpcParser()
	{
		super(L2onNpcHolder.getInstance());
	}

	@Override
	public File getXMLDir()
	{
		return new File(Config.DATAPACK_ROOT, "data/l2on_npc/");
	}

	@Override
	public boolean isIgnored(File f)
	{
		return false;
	}

	@Override
	public String getDTDFileName()
	{
		return "l2on_npc.dtd";
	}

	@Override
	protected void readData(org.dom4j.Element rootElement) throws Exception
	{
		for(Iterator<org.dom4j.Element> npcIterator = rootElement.elementIterator(); npcIterator.hasNext();)
		{
			org.dom4j.Element npcElement = npcIterator.next();
			int npcId = Integer.parseInt(npcElement.attributeValue("id"));
			
			L2onNpcInfo info = getHolder().getNpcInfo(npcId);
			
			for(org.dom4j.Element setElement: npcElement.elements()){
				if(setElement.getName().equalsIgnoreCase("set")){
					
					String value = setElement.attributeValue("value").replace(" ", "");
					if(value.equalsIgnoreCase("?")){
						value = "0";
						System.out.println("id:"+npcId+" ????? "+setElement.attributeValue("name"));
					}
					
					if(setElement.attributeValue("name").equalsIgnoreCase("Level")){
						info.setLevel(Integer.parseInt(value));
					}
					if(setElement.attributeValue("name").equalsIgnoreCase("Exp")){
						info.setExp(Long.parseLong(value));
					}
					if(setElement.attributeValue("name").equalsIgnoreCase("Sp")){
						info.setSp(Integer.parseInt(value));
					}
					if(setElement.attributeValue("name").equalsIgnoreCase("HP")){
						info.setHp(Integer.parseInt(value));
					}
				}
			}
		}
	}

	
}
