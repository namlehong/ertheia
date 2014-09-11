package databuilder;

import java.io.File;
import java.io.IOException;

import databuilder.utils.XmlPretty;
import databuilder.xml.parser.NpcGaiParser;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.parser.BaseStatsBonusParser;
import l2s.gameserver.data.xml.parser.LevelBonusParser;
import l2s.gameserver.data.xml.parser.NpcParser;
import l2s.gameserver.tables.SkillTable;

public class MainBuilder
{
	
	public static MainBuilder _instance;
	
	private MainBuilder()
	{
		//BloodFakePlayers.getInstance();
	}
	
	public static void buildNpc(){
		
		SkillTable.getInstance().load();
		BaseStatsBonusParser.getInstance().load();
		LevelBonusParser.getInstance().load();
		NpcParser.getInstance().load();
		NpcGaiParser.getInstance().load();
	}
	
	public static void main(String[] args)
	{
//		System.currentTimeMillis();
		try {
			Config.DATAPACK_ROOT = new File("/Users/mylove1412/Workspace/ertheia/dist/gameserver/").getCanonicalFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		buildNpc();
//		System.out.println(XmlPretty.prettyFormat("<test></test>", "npc.dtd"));
		
	}
	
}
