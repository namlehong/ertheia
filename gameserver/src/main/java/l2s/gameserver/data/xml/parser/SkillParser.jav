package l2s.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.data.xml.holder.OptionDataHolder;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.base.Element;
import l2s.gameserver.stats.conditions.Condition;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.templates.OptionDataTemplate;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.templates.item.ArmorTemplate;
import l2s.gameserver.templates.item.Bodypart;
import l2s.gameserver.templates.item.EtcSkillTemplate;
import l2s.gameserver.templates.item.SkillTemplate;
import l2s.gameserver.templates.item.WeaponTemplate;

/**
 * @author Bonux
 * @date 23:52/15.12.2011
 */
public final class SkillParser extends StatParser<SkillHolder>
{
	private static final SkillParser _instance = new SkillParser();

	public static SkillParser getInstance()
	{
		return _instance;
	}

	protected SkillParser()
	{
		super(SkillHolder.getInstance());
	}

	@Override
	public File getXMLDir()
	{
		return new File(Config.DATAPACK_ROOT, "data/skills/");
	}

	@Override
	public boolean isIgnored(File f)
	{
		return false;
	}

	@Override
	public String getDTDFileName()
	{
		return "skill.dtd";
	}

	@Override
	protected void readData(org.dom4j.Element rootElement) throws Exception
	{
		for(Iterator itemIterator = rootElement.elementIterator(); itemIterator.hasNext();)
		{
			org.dom4j.Element itemElement = (org.dom4j.Element)itemIterator.next();
			StatsSet set = new StatsSet();
			set.set("item_id", itemElement.attributeValue("id"));
			set.set("name", itemElement.attributeValue("name"));
			set.set("add_name", itemElement.attributeValue("add_name", StringUtils.EMPTY));

			int slot = 0;
			for(Iterator subIterator = itemElement.elementIterator(); subIterator.hasNext();)
			{
				org.dom4j.Element subElement = (org.dom4j.Element)subIterator.next();
				String subName = subElement.getName();
				if(subName.equalsIgnoreCase("set"))
				{
					set.set(subElement.attributeValue("name"), subElement.attributeValue("value"));
				}
				else if(subName.equalsIgnoreCase("equip"))
				{
					for(Iterator slotIterator = subElement.elementIterator(); slotIterator.hasNext();)
					{
						org.dom4j.Element slotElement = (org.dom4j.Element)slotIterator.next();
						Bodypart bodypart = Bodypart.valueOf(slotElement.attributeValue("id"));
						if(bodypart.getReal() != null)
							slot = bodypart.mask();
						else
							slot |= bodypart.mask();
					}
				}
			}

			set.set("bodypart", slot);

			SkillTemplate template = null;
			try
			{
				if(itemElement.getName().equalsIgnoreCase("weapon"))
				{
					if (!set.containsKey("class"))
					{
						if ((slot & SkillTemplate.SLOT_L_HAND) > 0) // щиты
							set.set("class", SkillTemplate.SkillClass.ARMOR);
						else
							set.set("class", SkillTemplate.SkillClass.WEAPON);
					}
					template = new WeaponTemplate(set);
				}
				else if(itemElement.getName().equalsIgnoreCase("armor"))
				{
					if (!set.containsKey("class"))
					{
						if ((slot & SkillTemplate.SLOTS_ARMOR) > 0)
							set.set("class", SkillTemplate.SkillClass.ARMOR);
						else if ((slot & SkillTemplate.SLOTS_JEWELRY) > 0)
							set.set("class", SkillTemplate.SkillClass.JEWELRY);
						else
							set.set("class", SkillTemplate.SkillClass.ACCESSORY);
					}
					template = new ArmorTemplate(set);
				}
				else //if(itemElement.getName().equalsIgnoreCase("etcitem"))
					template = new EtcSkillTemplate(set);
			}
			catch(Exception e)
			{
				//for(Map.Entry<String, Object> entry : set.entrySet())
				//{
				//	info("set " + entry.getKey() + ":" + entry.getValue());
				//}
				warn("Fail create item: " + set.get("item_id"), e);
				continue;
			}

			for(Iterator subIterator = itemElement.elementIterator(); subIterator.hasNext();)
			{
				org.dom4j.Element subElement = (org.dom4j.Element)subIterator.next();
				String subName = subElement.getName();
				if(subName.equalsIgnoreCase("for"))
				{
					parseFor(subElement, template);
				}
				else if(subName.equalsIgnoreCase("triggers"))
				{
					parseTriggers(subElement, template);
				}
				else if(subName.equalsIgnoreCase("skills"))
				{
					for(Iterator<org.dom4j.Element> nextIterator = subElement.elementIterator(); nextIterator.hasNext();)
					{
						org.dom4j.Element nextElement =  nextIterator.next();
						int id = Integer.parseInt(nextElement.attributeValue("id"));
						int level = Integer.parseInt(nextElement.attributeValue("level"));

						Skill skill = SkillTable.getInstance().getInfo(id, level);

						if(skill != null)
							template.attachSkill(skill);
						else
							warn("Skill not found(" + id + "," + level + ") for item:" + set.getObject("item_id") + "; file:" + getCurrentFileName());
					}
				}
				else if(subName.equalsIgnoreCase("enchant4_skill"))
				{
					int id = Integer.parseInt(subElement.attributeValue("id"));
					int level = Integer.parseInt(subElement.attributeValue("level"));

					Skill skill = SkillTable.getInstance().getInfo(id, level);
					if(skill != null)
						template.setEnchant4Skill(skill);
				}
				else if(subName.equalsIgnoreCase("cond"))
				{
					Condition condition = parseFirstCond(subElement);
					if(condition != null)
					{
						int msgId = parseNumber(subElement.attributeValue("msgId")).intValue();
						condition.setSystemMsg(msgId);

						template.setCondition(condition);
					}
				}
				else if(subName.equalsIgnoreCase("attributes"))
				{
					int[] attributes = new int[6];
					for(Iterator nextIterator = subElement.elementIterator(); nextIterator.hasNext();)
					{
						org.dom4j.Element nextElement = (org.dom4j.Element) nextIterator.next();
						Element element;
						if(nextElement.getName().equalsIgnoreCase("attribute"))
						{
							element = Element.getElementByName(nextElement.attributeValue("element"));
							attributes[element.getId()] = Integer.parseInt(nextElement.attributeValue("value"));
						}
					}
					template.setBaseAtributeElements(attributes);
				}
				else if(subName.equalsIgnoreCase("capsuled_items"))
				{
					for(Iterator nextIterator = subElement.elementIterator(); nextIterator.hasNext();)
					{
						org.dom4j.Element nextElement = (org.dom4j.Element) nextIterator.next();
						if(nextElement.getName().equalsIgnoreCase("capsuled_item"))
						{
							int c_item_id = Integer.parseInt(nextElement.attributeValue("id"));
							int c_min_count = Integer.parseInt(nextElement.attributeValue("min_count"));
							int c_max_count = Integer.parseInt(nextElement.attributeValue("max_count"));
							double c_chance = Double.parseDouble(nextElement.attributeValue("chance"));
							template.addCapsuledSkill(new SkillTemplate.CapsuledSkill(c_item_id, c_min_count, c_max_count, c_chance));
						}
					}
				}
				else if(subName.equalsIgnoreCase("enchant_options"))
				{
					for(Iterator<org.dom4j.Element> nextIterator = subElement.elementIterator(); nextIterator.hasNext();)
					{
						org.dom4j.Element nextElement = nextIterator.next();

						if(nextElement.getName().equalsIgnoreCase("level"))
						{
							int val = Integer.parseInt(nextElement.attributeValue("val"));

							int i = 0;
							int[] options = new int[3];
							for(org.dom4j.Element optionElement : nextElement.elements())
							{
								OptionDataTemplate optionData = OptionDataHolder.getInstance().getTemplate(Integer.parseInt(optionElement.attributeValue("id")));
								if(optionData == null)
								{
									error("Not found option_data for id: " + optionElement.attributeValue("id") + "; item_id: " + set.get("item_id"));
									continue;
								}
								options[i++] = optionData.getId();
							}
							template.addEnchantOptions(val, options);
						}
					}
				}
			}
			getHolder().addSkill(template);
		}
	}

	@Override
	protected Object getTableValue(String name)
	{
		return null;
	}
}
