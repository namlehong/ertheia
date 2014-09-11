package blood;

import java.io.File;
import java.io.IOException;

import l2s.gameserver.Config;
import l2s.gameserver.data.xml.parser.BaseStatsBonusParser;
import l2s.gameserver.data.xml.parser.LevelBonusParser;
import l2s.gameserver.data.xml.parser.NpcBloodParser;
import l2s.gameserver.data.xml.parser.NpcParser;
import l2s.gameserver.tables.SkillTable;


import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class BloodTest
{
	
	public static BloodTest _instance;
	
	private BloodTest()
	{
		//BloodFakePlayers.getInstance();
	}
	
	public static String prettyFormat(String input, int indent) {
        try
        {
            Source xmlInput = new StreamSource(new StringReader(input));
            StringWriter stringWriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            // This statement works with JDK 6
            transformerFactory.setAttribute("indent-number", indent);
             
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString();
        }
        catch (Throwable e)
        {
            // You'll come here if you are using JDK 1.5
            // you are getting an the following exeption
            // java.lang.IllegalArgumentException: Not supported: indent-number
            // Use this code (Set the output property in transformer.
            try
            {
                Source xmlInput = new StreamSource(new StringReader(input));
                StringWriter stringWriter = new StringWriter();
                StreamResult xmlOutput = new StreamResult(stringWriter);
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(indent));
                transformer.transform(xmlInput, xmlOutput);
                return xmlOutput.getWriter().toString();
            }
            catch(Throwable t)
            {
                return input;
            }
        }
    }
 
    public static String prettyFormat(String input) {
        return prettyFormat(input, 4);
    }
	
	
	public static void main(String[] args)
	{
//		System.exit(0);
		System.out.println("haha");
		try {
			Config.DATAPACK_ROOT = new File("/Users/mylove1412/Workspace/ertheia/dist/gameserver/").getCanonicalFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SkillTable.getInstance().load();
		BaseStatsBonusParser.getInstance().load();
		LevelBonusParser.getInstance().load();
		NpcParser.getInstance().load();
		NpcBloodParser.getInstance().load();
//		NpcGaiParser.writeToFile();
//		System.out.println(BloodTest.prettyFormat("<npc id='36697' name='' title=''><set name='level' value='75'/><set name='rewardExp' value='0'/><set name='rewardSp' value='0'/><set name='baseSTR' value='89'/><set name='baseINT' value='79'/><set name='baseDEX' value='55'/><set name='baseWIT' value='78'/><set name='baseCON' value='82'/><set name='baseMEN' value='78'/><set name='baseHpMax' value='4630'/><set name='baseMpMax' value='1840'/><set name='basePAtk' value='1564'/><set name='baseMAtk' value='958'/><set name='basePDef' value='524'/><set name='baseMDef' value='468'/><set name='basePCritRate' value='40.0'/><set name='baseAtkRange' value='40'/><set name='basePAtkSpd' value='253'/><set name='baseMAtkSpd' value='500'/><set name='baseWalkSpd' value='60'/><set name='baseRunSpd' value='120'/><attributes><defence attribute='fire' value='150'/><defence attribute='water' value='150'/><defence attribute='wind' value='150'/><defence attribute='earth' value='150'/><defence attribute='holy' value='150'/><defence attribute='unholy' value='150'/></attributes></npc>"));
	}
	
}
