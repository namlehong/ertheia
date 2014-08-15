package l2s.gameserver.data.xml.parser;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.dom4j.Element;
import l2s.commons.data.xml.AbstractDirParser;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.SkillAcquireHolder;
import l2s.gameserver.model.SkillLearn;
import l2s.gameserver.model.base.Race;

/**
 * @author: VISTALL
 * @date:  20:55/30.11.2010
 */
public final class SkillAcquireParser extends AbstractDirParser<SkillAcquireHolder>
{
	private static final SkillAcquireParser _instance = new SkillAcquireParser();

	public static SkillAcquireParser getInstance()
	{
		return _instance;
	}

	protected SkillAcquireParser()
	{
		super(SkillAcquireHolder.getInstance());
	}

	@Override
	public File getXMLDir()
	{
		return new File(Config.DATAPACK_ROOT, "data/skill_tree/");
	}

	@Override
	public boolean isIgnored(File b)
	{
		return false;
	}

	@Override
	public String getDTDFileName()
	{
		return "tree.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for(Iterator<Element> iterator = rootElement.elementIterator("certification_skill_tree"); iterator.hasNext();)
			getHolder().addAllCertificationLearns(parseSkillLearn(iterator.next()));

		for(Iterator<Element> iterator = rootElement.elementIterator("dual_certification_skill_tree"); iterator.hasNext();)
			getHolder().addAllDualCertificationLearns(parseSkillLearn(iterator.next()));

		for(Iterator<Element> iterator = rootElement.elementIterator("sub_unit_skill_tree"); iterator.hasNext();)
			getHolder().addAllSubUnitLearns(parseSkillLearn(iterator.next()));

		for(Iterator<Element> iterator = rootElement.elementIterator("pledge_skill_tree"); iterator.hasNext();)
			getHolder().addAllPledgeLearns(parseSkillLearn(iterator.next()));

		for(Iterator<Element> iterator = rootElement.elementIterator("collection_skill_tree"); iterator.hasNext();)
			getHolder().addAllCollectionLearns(parseSkillLearn(iterator.next()));

		for(Iterator<Element> iterator = rootElement.elementIterator("transformation_skill_tree"); iterator.hasNext();)
			getHolder().addAllTransformationLearns(parseSkillLearn(iterator.next()));

		for(Iterator<Element> iterator = rootElement.elementIterator("fishing_skill_tree"); iterator.hasNext();)
			getHolder().addAllFishingLearns(parseSkillLearn(iterator.next()));

		for(Iterator<Element> iterator = rootElement.elementIterator("noblesse_skill_tree"); iterator.hasNext();)
			getHolder().addAllNoblesseLearns(parseSkillLearn(iterator.next()));

		for(Iterator<Element> iterator = rootElement.elementIterator("hero_skill_tree"); iterator.hasNext();)
			getHolder().addAllHeroLearns(parseSkillLearn(iterator.next()));

		for(Iterator<Element> iterator = rootElement.elementIterator("gm_skill_tree"); iterator.hasNext();)
			getHolder().addAllGMLearns(parseSkillLearn(iterator.next()));

		for(Iterator<Element> iterator = rootElement.elementIterator("transfer_skill_tree"); iterator.hasNext();)
		{
			Element nxt = iterator.next();
			for(Iterator<Element> classIterator = nxt.elementIterator("class"); classIterator.hasNext();)
			{
				Element classElement = classIterator.next();
				int classId = Integer.parseInt(classElement.attributeValue("id"));
				List<SkillLearn> learns = parseSkillLearn(classElement);
				getHolder().addAllTransferLearns(classId, learns);
			}
		}

		for(Iterator<Element> iterator = rootElement.elementIterator("normal_skill_tree"); iterator.hasNext();)
		{
			TIntObjectMap<List<SkillLearn>> map = new TIntObjectHashMap<List<SkillLearn>>();
			Element nxt = iterator.next();
			for(Iterator<Element> classIterator = nxt.elementIterator("class"); classIterator.hasNext();)
			{
				Element classElement = classIterator.next();
				int classId = Integer.parseInt(classElement.attributeValue("id"));
				List<SkillLearn> learns = parseSkillLearn(classElement);

				map.put(classId, learns);
			}

			getHolder().addAllNormalSkillLearns(map);
		}

		for(Iterator<Element> iterator = rootElement.elementIterator("forgotten_book_skill_tree"); iterator.hasNext();)
		{
			TIntObjectMap<List<SkillLearn>> map = new TIntObjectHashMap<List<SkillLearn>>();

			Element nxt = iterator.next();
			map.put(-1, parseSkillLearn(nxt)); // Парсим скиллы которые принадлежат любому классу.
			for(Iterator<Element> classIterator = nxt.elementIterator("class"); classIterator.hasNext();)
			{
				Element classElement = classIterator.next();
				int classId = Integer.parseInt(classElement.attributeValue("id"));
				List<SkillLearn> learns = parseSkillLearn(classElement);

				map.put(classId, learns);
			}

			getHolder().addAllForgottenScrollsSkillLearns(map);
		}

		for(Iterator<Element> iterator = rootElement.elementIterator("dual_class_skill_tree"); iterator.hasNext();)
		{
			Element nxt = iterator.next();
			for(Iterator<Element> classIterator = nxt.elementIterator("class"); classIterator.hasNext();)
			{
				Element classElement = classIterator.next();
				int classId = Integer.parseInt(classElement.attributeValue("id"));
				List<SkillLearn> learns = parseSkillLearn(classElement);

				getHolder().addAllDualClassSkillLearns(classId, learns);
			}
		}

		for(Iterator<Element> iterator = rootElement.elementIterator("awake_parent_skill_tree"); iterator.hasNext();)
		{
			TIntObjectMap<TIntObjectMap<List<SkillLearn>>> map = new TIntObjectHashMap<TIntObjectMap<List<SkillLearn>>>();
			Element nxt = iterator.next();
			for(Iterator<Element> awakeClassIterator = nxt.elementIterator("awake_class"); awakeClassIterator.hasNext();)
			{
				TIntObjectMap<List<SkillLearn>> parentsMap = new TIntObjectHashMap<List<SkillLearn>>();
				Element awakeClassElement = awakeClassIterator.next();
				int awakeClassId = Integer.parseInt(awakeClassElement.attributeValue("id"));
				for(Iterator<Element> parentClassIterator = awakeClassElement.elementIterator("parent_class"); parentClassIterator.hasNext();)
				{
					Element parentClassElement = parentClassIterator.next();
					int parentClassId = Integer.parseInt(parentClassElement.attributeValue("id"));
					List<SkillLearn> learns = parseSkillLearn(parentClassElement);
						parentsMap.put(parentClassId, learns);
				}
				map.put(awakeClassId, parentsMap);
			}

			getHolder().addAllAwakeParentSkillLearns(map);
		}

		for(Iterator<Element> iterator = rootElement.elementIterator("chaos_skill_tree"); iterator.hasNext();)
			getHolder().addAllChaosSkillLearns(parseSkillLearn(iterator.next()));

		for(Iterator<Element> iterator = rootElement.elementIterator("dual_chaos_skill_tree"); iterator.hasNext();)
			getHolder().addAllDualChaosSkillLearns(parseSkillLearn(iterator.next()));

		for(Iterator<Element> iterator = rootElement.elementIterator("ability_skill_tree"); iterator.hasNext();)
			getHolder().addAllAbilitySkillLearns(parseSkillLearn(iterator.next()));

		for(Iterator<Element> iterator = rootElement.elementIterator("abilities_settings"); iterator.hasNext();)
		{
			Element element = iterator.next();

			getHolder().setMaxAbilitiesPoints(Integer.parseInt(element.attributeValue("maximun_points")));
			getHolder().setAbilitiesRefreshPrice(Long.parseLong(element.attributeValue("refresh_price")));

			for(Iterator<Element> secondIterator = element.elementIterator("point"); secondIterator.hasNext();)
			{
				Element secondElement = secondIterator.next();

				StringTokenizer ordinals = new StringTokenizer(secondElement.attributeValue("ordinal"), "-");

				int firstOrdinal = Integer.parseInt(ordinals.nextToken());
				int lastOrdinal = firstOrdinal;
				if(ordinals.hasMoreTokens())
					lastOrdinal = Integer.parseInt(ordinals.nextToken());

				for(int ordinal = firstOrdinal; ordinal <= lastOrdinal; ordinal++)
					getHolder().addAbilitiesPointPrice(ordinal, Long.parseLong(secondElement.attributeValue("sp_cost")));
			}
		}
	}

	@Override
	protected void afterParseActions()
	{
		getHolder().initNormalSkillLearns();
		getHolder().initForgottenScrollsSkillLearns();
	}

	private List<SkillLearn> parseSkillLearn(Element tree)
	{
		List<SkillLearn> skillLearns = new ArrayList<SkillLearn>();
		for(Iterator<Element> iterator = tree.elementIterator("skill"); iterator.hasNext();)
		{
			Element element = iterator.next();

			int id = Integer.parseInt(element.attributeValue("id"));
			int level = element.attributeValue("level") == null ? 1 : Integer.parseInt(element.attributeValue("level"));
			int cost = element.attributeValue("cost") == null ? 0 : Integer.parseInt(element.attributeValue("cost"));
			int min_level = element.attributeValue("min_level") == null ? 1 : Integer.parseInt(element.attributeValue("min_level"));
			int item_id = element.attributeValue("item_id") == null ? 0 : Integer.parseInt(element.attributeValue("item_id"));
			long item_count = element.attributeValue("item_count") == null ? 1 : Long.parseLong(element.attributeValue("item_count"));
			boolean auto_get = element.attributeValue("auto_get") == null ? true : Boolean.parseBoolean(element.attributeValue("auto_get"));
			Race race = element.attributeValue("race") == null ? null : Race.valueOf(element.attributeValue("race"));
			int dual_class_min_level = element.attributeValue("dual_class_min_level") == null ? 0 : Integer.parseInt(element.attributeValue("dual_class_min_level"));

			skillLearns.add(new SkillLearn(id, level, min_level, cost, item_id, item_count, auto_get, race, dual_class_min_level));
		}

		return skillLearns;
	}
}