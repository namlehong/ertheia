package blood.data.parser;

import java.io.File;
import java.util.Iterator;

import l2s.commons.data.xml.AbstractFileParser;
import l2s.gameserver.Config;

import org.dom4j.Element;

import blood.data.holder.NpcHelper;

public final class NpcHelperParser extends AbstractFileParser<NpcHelper> 
{
	/**
	 * Field _instance.
	 */
	private static final NpcHelperParser _instance = new NpcHelperParser();
	
	/**
	 * Method getInstance.
	 * @return LevelBonusParser
	 */
	public static NpcHelperParser getInstance()
	{
		return _instance;
	}
	
	/**
	 * Constructor for LevelBonusParser.
	 */
	private NpcHelperParser()
	{
		super(NpcHelper.getInstance());
	}
	
	/**
	 * Method getXMLFile.
	 * @return File
	 */
	@Override
	public File getXMLFile()
	{
		return new File(Config.DATAPACK_ROOT, "data/blood/npc_helper.xml");
	}
	
	/**
	 * Method getDTDFileName.
	 * @return String
	 */
	@Override
	public String getDTDFileName()
	{
		return "npc_helper.dtd";
	}
	
	/**
	 * Method readData.
	 * @param rootElement Element
	 * @throws Exception
	 */
	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for (Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext();)
		{
			Element rewardElement = iterator.next();
			int npcId = Integer.parseInt(rewardElement.attributeValue("id"));
			
			if(rewardElement.getName().equalsIgnoreCase("gatekeeper"))
				NpcHelper.addGatekeeper(npcId);
			else if(rewardElement.getName().equalsIgnoreCase("buffer"))
				NpcHelper.addBufffer(npcId);
			else
				NpcHelper.addInterester(npcId);
		}
		
		NpcHelper.updateNpcInstance();
	}
}
