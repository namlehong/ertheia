package databuilder.xml.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import databuilder.xml.holder.NpcGaiHolder;
import l2s.commons.data.xml.AbstractDirParser;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.BaseStatsBonusHolder;
import l2s.gameserver.data.xml.holder.LevelBonusHolder;
import l2s.gameserver.data.xml.holder.NpcHolder;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.base.Element;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.templates.npc.Faction;
import l2s.gameserver.templates.npc.NpcTemplate;
import blood.BloodTest;

/**
 * @author VISTALL
 * @date  16:16/14.12.2010
 */
public final class NpcGaiParser extends AbstractDirParser<NpcGaiHolder>
{
	private static final NpcGaiParser _instance = new NpcGaiParser();

	public static NpcGaiParser getInstance()
	{
		return _instance;
	}

	private NpcGaiParser()
	{
		super(NpcGaiHolder.getInstance());
	}

	@Override
	public File getXMLDir()
	{
		return new File(Config.DATAPACK_ROOT, "data/gai_npc/");
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
			int templateId = npcElement.attributeValue("template_id") == null ? 0 : Integer.parseInt(npcElement.attributeValue("id"));
			String name = npcElement.attributeValue("name");
			String title = npcElement.attributeValue("title");

			StatsSet set = new StatsSet();
			set.set("npcId", npcId);
			set.set("displayId", templateId);
			set.set("name", name);
			set.set("title", title);
			set.set("baseCpReg", 0);
			set.set("baseCpMax", 0);

			for(Iterator<org.dom4j.Element> firstIterator = npcElement.elementIterator(); firstIterator.hasNext();)
			{
				org.dom4j.Element firstElement = firstIterator.next();
				if(firstElement.getName().equalsIgnoreCase("set"))
				{
					set.set(firstElement.attributeValue("name"), firstElement.attributeValue("value"));
				}
				else if(firstElement.getName().equalsIgnoreCase("equip"))
				{
					for(Iterator<org.dom4j.Element> eIterator = firstElement.elementIterator(); eIterator.hasNext();)
					{
						org.dom4j.Element eElement = eIterator.next();
						set.set(eElement.getName(), eElement.attributeValue("item_id"));
					}
				}
				else if(firstElement.getName().equalsIgnoreCase("ai_params"))
				{
					StatsSet ai = new StatsSet();
					for(Iterator<org.dom4j.Element> eIterator = firstElement.elementIterator(); eIterator.hasNext();)
					{
						org.dom4j.Element eElement = eIterator.next();
						ai.set(eElement.attributeValue("name"), eElement.attributeValue("value"));
					}
					set.set("aiParams", ai);
				}
				else if(firstElement.getName().equalsIgnoreCase("attributes"))
				{
					int[] attributeAttack = new int[6];
					int[] attributeDefence = new int[6];
					for(Iterator<org.dom4j.Element> eIterator = firstElement.elementIterator(); eIterator.hasNext();)
					{
						org.dom4j.Element eElement = eIterator.next();
						Element element;
						if(eElement.getName().equalsIgnoreCase("defence"))
						{
							element = Element.getElementByName(eElement.attributeValue("attribute"));
							attributeDefence[element.getId()] = Integer.parseInt(eElement.attributeValue("value"));
						}
						else if(eElement.getName().equalsIgnoreCase("attack"))
						{
							element = Element.getElementByName(eElement.attributeValue("attribute"));
							attributeAttack[element.getId()] = Integer.parseInt(eElement.attributeValue("value"));
						}
					}

					set.set("baseAttributeAttack", attributeAttack);
					set.set("baseAttributeDefence", attributeDefence);
				}
			}

//			NpcTemplate template = new NpcTemplate(set);
			NpcTemplate template = NpcHolder.getInstance().getTemplateNoWarn(npcId);
			if(template == null)
				continue;
			
			template.update(set); // MY CUSTOM
//			if(template.isInstanceOf(Servitor.class))
//			{
//				for(Skill skill: template.getSkills().valueCollection())
//				{
//					if(skill.getId() == 4415 && skill.getLevel() == 9)
//					{
//						
//					}
//				}
//			}
			
			double str_bonus = BaseStatsBonusHolder.getInstance().getBaseStatsBonus(template.getBaseSTR()).getSTR();
			double int_bonus = BaseStatsBonusHolder.getInstance().getBaseStatsBonus(template.getBaseINT()).getINT();
			double dex_bonus = BaseStatsBonusHolder.getInstance().getBaseStatsBonus(template.getBaseDEX()).getDEX();
			double wit_bonus = BaseStatsBonusHolder.getInstance().getBaseStatsBonus(template.getBaseWIT()).getWIT();
			double con_bonus = BaseStatsBonusHolder.getInstance().getBaseStatsBonus(template.getBaseCON()).getCON();
			double men_bonus = BaseStatsBonusHolder.getInstance().getBaseStatsBonus(template.getBaseMEN()).getMEN();
			
			double level_bonus = !template.isInstanceOf(Servitor.class) ? 1. : LevelBonusHolder.getInstance().getLevelBonus(template.level);
			String newValue;
			
			double patk_modifier = level_bonus*str_bonus;
			double pdef_modifier = level_bonus;
			double pspd_modifier = dex_bonus;
			double pcrt_modifier = dex_bonus;
			double matk_modifier = level_bonus*level_bonus*int_bonus*int_bonus;
			double mdef_modifier = men_bonus * level_bonus;
			double mspd_modifier = wit_bonus;
			double hp_modifier = con_bonus;
			double mp_modifier = men_bonus;
			
			for(Skill skill: template.getSkills().valueCollection())
			{
				switch (skill.getId()) {
				case 4410: // weak atk
					double[] ef4410 = new double[] {0.39, 0.43, 0.47, 0.52, 0.57, 0.63, 0.69, 0.76, 0.83, 0.91, 1, 1.1, 1.21, 1.33, 1.46, 1.61, 1.77, 1.91, 2.14, 2.35, 2.59, 9};
					patk_modifier *= ef4410[skill.getLevel()-1];
					break;
				case 4411: // weak matk
					double[] ef4411 = new double[] {0.15, 0.18, 0.22, 0.27, 0.32, 0.39, 0.47, 0.57, 0.69, 0.83, 1, 1.21, 1.46, 1.77, 2.14, 2.59, 3.13, 3.79, 4.59, 5.55, 6.72, 9.9};
					matk_modifier *= ef4411[skill.getLevel()-1];
					break;
				case 4412: // weak pdef
					double[] ef4412 = new double[] {0.39, 0.43, 0.47, 0.52, 0.57, 0.63, 0.69, 0.76, 0.83, 0.91, 1, 1.1, 1.21, 1.33, 1.46, 1.61, 1.77, 1.91, 2.14, 2.35, 2.59, 9};
					pdef_modifier *= ef4412[skill.getLevel()-1];
					break;
				case 4413: // weak mdef
					double[] ef4413 = new double[] {0.39, 0.43, 0.47, 0.52, 0.57, 0.63, 0.69, 0.76, 0.83, 0.91, 1, 1.1, 1.21, 1.33, 1.46, 1.61, 1.77, 1.91, 2.14, 2.35, 2.59, 9};
					mdef_modifier *= ef4413[skill.getLevel()-1];
					break;
				case 4414: // armor type
					double[] ef4414 = new double[] {1.15, 1, 0.85};
					pdef_modifier *= ef4414[skill.getLevel()-1];
					break;
				case 4415: // bare hand
					double[] ef4415_patk = new double[] {1, 1.38, 1, 1.38, 1, 1.38, 0.87, 1.21, 2.22, 2.5, 1, 1.38, 1.16, 1.62, 1.16, 1.62, 1.16, 1.62, 1.16, 1.62, 1.08, 1.62, 0.93, 1.62, 1.38, 1.62, 1.08, 1.62};
					double[] ef4415_patk_spd = new double[] {1, 0.71, 1, 0.71, 1, 0.71, 1.14, 0.82, 0.89, 0.64, 1, 0.71, 0.85, 0.61, 0.85, 0.61, 0.85, 0.61, 0.85, 0.61, 0.92, 0.61, 1.07, 0.61, 0.62, 0.61, 0.92, 0.61};
					patk_modifier *= ef4415_patk[skill.getLevel()-1];
					pspd_modifier *= ef4415_patk_spd[skill.getLevel()-1];
					break;
				case 11315: // summon def
					getHolder().info("NPC "+npcId+" has summon def");
					mdef_modifier *= 1.5;
					break;

				default:
					break;
				}
			}
			
			npcElement.addAttribute("name", template.getName());
			npcElement.addAttribute("title", template.title);
			
			for(Iterator<org.dom4j.Element> modIterator = npcElement.elementIterator(); modIterator.hasNext();)
			{
				org.dom4j.Element modElement = modIterator.next();
				if(modElement.getName().equalsIgnoreCase("set"))
				{
					if(modElement.attributeValue("name").equalsIgnoreCase("basePAtk")){
						newValue = String.valueOf((int) (template.getBasePAtk()/patk_modifier));
						modElement.addAttribute("value", newValue);
					}else if(modElement.attributeValue("name").equalsIgnoreCase("baseMAtk")){
						newValue = String.valueOf((int) (template.getBaseMAtk()/matk_modifier));
						modElement.addAttribute("value", newValue);
					}else if(modElement.attributeValue("name").equalsIgnoreCase("baseMDef")){
						newValue = String.valueOf((int) (template.getBaseMDef()/mdef_modifier));
						modElement.addAttribute("value", newValue);
					}else if(modElement.attributeValue("name").equalsIgnoreCase("basePDef")){
						newValue = String.valueOf((int) (template.getBasePDef()/pdef_modifier));
						modElement.addAttribute("value", newValue);
					}else if(modElement.attributeValue("name").equalsIgnoreCase("basePAtkSpd")){
						newValue = String.valueOf((int) (template.getBasePAtkSpd()/pspd_modifier));
						modElement.addAttribute("value", newValue);
					}else if(modElement.attributeValue("name").equalsIgnoreCase("basePCritRate")){
						newValue = String.valueOf((int) (template.getBasePCritRate()/pcrt_modifier));
						modElement.addAttribute("value", newValue);
					}else if(modElement.attributeValue("name").equalsIgnoreCase("baseMAtkSpd")){
						newValue = String.valueOf((int) (template.getBaseMAtkSpd()/mspd_modifier));
						modElement.addAttribute("value", newValue);
					}else if(modElement.attributeValue("name").equalsIgnoreCase("baseHpMax")){
						newValue = String.valueOf((int) (template.getBaseHpMax(template.level)/hp_modifier));
						modElement.addAttribute("value", newValue);
					}else if(modElement.attributeValue("name").equalsIgnoreCase("baseMpMax")){
						newValue = String.valueOf((int) (template.getBaseMpMax(template.level)/mp_modifier));
						modElement.addAttribute("value", newValue);
					}
				}
			}
			
//			addToBuilder(npcId, npcElement.asXML());

			for(Iterator<org.dom4j.Element> secondIterator = npcElement.elementIterator(); secondIterator.hasNext();)
			{
				org.dom4j.Element secondElement = secondIterator.next();
				String nodeName = secondElement.getName();
				if(nodeName.equalsIgnoreCase("faction"))
				{
					String factionNames = secondElement.attributeValue("names");
					int factionRange = Integer.parseInt(secondElement.attributeValue("range"));
					Faction faction = new Faction(factionNames, factionRange);
					for(Iterator<org.dom4j.Element> nextIterator = secondElement.elementIterator(); nextIterator.hasNext();)
					{
						final org.dom4j.Element nextElement = nextIterator.next();
						int ignoreId = Integer.parseInt(nextElement.attributeValue("npc_id"));
						faction.addIgnoreNpcId(ignoreId);
					}
					template.setFaction(faction);
				}
				else if(nodeName.equalsIgnoreCase("skills"))
				{
					for(Iterator<org.dom4j.Element> nextIterator = secondElement.elementIterator(); nextIterator.hasNext();)
					{
						org.dom4j.Element nextElement = nextIterator.next();
						int id = Integer.parseInt(nextElement.attributeValue("id"));
						int level = Integer.parseInt(nextElement.attributeValue("level"));

						// Для определения расы используется скилл 4416
						if(id == 4416)
						{
							template.setRace(level);
						}

						Skill skill = SkillTable.getInstance().getInfo(id, level);

						//TODO
						//if(skill == null || skill.getSkillType() == L2Skill.SkillType.NOTDONE)
						//	unimpl.add(Integer.valueOf(skillId));
						if(skill == null)
						{
							continue;
						}

						template.addSkill(skill);
					}
				}
			}

			getHolder().addTemplate(template);
		}
		
		writeToFile(_currentFile, rootElement.asXML());
		
	}
	
	public static HashMap<String, BufferedWriter> _bufferHolder = new HashMap<String, BufferedWriter>();
	public static HashMap<String, StringBuilder> _stringBuilderHolder = new HashMap<String, StringBuilder>();
	
	public static void addToBuilder(int npc_id, String xmlInput)
	{
		int min_id = (int) Math.floor(npc_id/100) * 100;
        int max_id = min_id + 99;
		String fileName = String.format("%05d-%05d.xml", min_id, max_id);
		
		StringBuilder builder = _stringBuilderHolder.get(fileName);
		
		if(builder == null)
		{
			builder = new StringBuilder();
			_stringBuilderHolder.put(fileName, builder);
		}
		
		builder.append(xmlInput);
	}
	
	public static void writeToFile()
	{
		for(Map.Entry<String, StringBuilder> entry: _stringBuilderHolder.entrySet())
		{
			String fileName = entry.getKey();
			String xmlInput = entry.getValue().toString();
			writeToFile(fileName, xmlInput);
		}
	}
	
	public static void writeToFile(String fileName, String xmlInput){
		String xmlOut = BloodTest.prettyFormat(xmlInput);
		
		try {
			File file = new File("/Users/mylove1412/Workspace/ertheia/dist/gameserver/data/blood_npc/"+fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
		
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter buffer = new BufferedWriter(fw);
			buffer.write(xmlOut);
			buffer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			System.out.print("error on "+fileName);
		}
	}
}
