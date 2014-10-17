package l2s.gameserver.data.xml.holder;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntLongMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntLongHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.SkillLearn;
import l2s.gameserver.model.base.AcquireType;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.model.pledge.SubUnit;
import l2s.gameserver.tables.SkillTable;

/**
 * @author: VISTALL
 * @date:  20:55/30.11.2010
 */
public final class SkillAcquireHolder extends AbstractHolder
{
	private static final SkillAcquireHolder _instance = new SkillAcquireHolder();

	public static SkillAcquireHolder getInstance()
	{
		return _instance;
	}

	// классовые зависимости
	private TIntObjectMap<List<SkillLearn>> _normalSkillTree = new TIntObjectHashMap<List<SkillLearn>>();
	private TIntObjectMap<List<SkillLearn>> _forgottenScrollsSkillTree = new TIntObjectHashMap<List<SkillLearn>>();
	private TIntObjectMap<List<SkillLearn>> _transferSkillTree = new TIntObjectHashMap<List<SkillLearn>>();
	private TIntObjectMap<List<SkillLearn>> _dualClassSkillTree = new TIntObjectHashMap<List<SkillLearn>>();
	private TIntObjectMap<TIntObjectMap<List<SkillLearn>>> _awakeParentSkillTree = new TIntObjectHashMap<TIntObjectMap<List<SkillLearn>>>();
	// без зависимостей
	private List<SkillLearn> _fishingSkillTree = new ArrayList<SkillLearn>();
	private List<SkillLearn> _transformationSkillTree = new ArrayList<SkillLearn>();
	private List<SkillLearn> _certificationSkillTree = new ArrayList<SkillLearn>();
	private List<SkillLearn> _dualCertificationSkillTree = new ArrayList<SkillLearn>();
	private List<SkillLearn> _collectionSkillTree = new ArrayList<SkillLearn>();
	private List<SkillLearn> _pledgeSkillTree = new ArrayList<SkillLearn>();
	private List<SkillLearn> _subUnitSkillTree = new ArrayList<SkillLearn>();
	private List<SkillLearn> _noblesseSkillTree = new ArrayList<SkillLearn>();
	private List<SkillLearn> _heroSkillTree = new ArrayList<SkillLearn>();
	private List<SkillLearn> _gmSkillTree = new ArrayList<SkillLearn>();
	private List<SkillLearn> _chaosSkillTree = new ArrayList<SkillLearn>();
	private List<SkillLearn> _dualChaosSkillTree = new ArrayList<SkillLearn>();
	private List<SkillLearn> _abilitySkillTree = new ArrayList<SkillLearn>();
	private List<SkillLearn> _alchemySkillTree = new ArrayList<SkillLearn>();

	// Abilities properties
	private long _abilitiesRefreshPrice = 0L;
	private int _maxAbilitiesPoints = 0;
	private final TIntLongMap _abilitiesPointsPrices = new TIntLongHashMap();

	public Collection<SkillLearn> getAvailableSkills(Player player, AcquireType type)
	{
		return getAvailableSkills(player, type, null);
	}

	public Collection<SkillLearn> getAvailableSkills(Player player, AcquireType type, SubUnit subUnit)
	{
		Collection<SkillLearn> skills;
		switch(type)
		{
			case NORMAL:
				skills = getNormalSkillTree(player);
				if(skills == null)
				{
					info("skill tree for class " + player.getActiveClassId() + " is not defined !");
					return Collections.emptyList();
				}
				return getAvaliableList(skills, player.getAllSkillsArray(), player.getLevel(), player.getDualClassLevel(), player.getRace());
			case COLLECTION:
				skills = _collectionSkillTree;
				if(skills == null)
				{
					info("Collection skill tree is not defined !");
					return Collections.emptyList();
				}
				return getAvaliableList(skills, player.getAllSkillsArray(), player.getLevel(), 0, player.getRace());
			case TRANSFORMATION:
				skills = _transformationSkillTree;
				if(skills == null)
				{
					info("Transformation skill tree is not defined !");
					return Collections.emptyList();
				}
				return getAvaliableList(skills, player.getAllSkillsArray(), player.getLevel(), 0, player.getRace());
			case TRANSFER_EVA_SAINTS:
			case TRANSFER_SHILLIEN_SAINTS:
			case TRANSFER_CARDINAL:
				skills = _transferSkillTree.get(type.transferClassId());
				if(skills == null)
				{
					info("skill tree for class " + type.transferClassId() + " is not defined !");
					return Collections.emptyList();
				}
				if(player == null)
					return skills;
				else
				{
					Map<Integer, SkillLearn> skillLearnMap = new TreeMap<Integer, SkillLearn>();
					for(SkillLearn temp : skills)
						if(temp.isOfRace(player.getRace()) && temp.getMinLevel() <= player.getLevel())
						{
							int knownLevel = player.getSkillLevel(temp.getId());
							if(knownLevel == -1)
								skillLearnMap.put(temp.getId(), temp);
						}

					return skillLearnMap.values();
				}
			case FISHING:
				skills = _fishingSkillTree;
				if(skills == null)
				{
					info("Fishing skill tree is not defined !");
					return Collections.emptyList();
				}
				return getAvaliableList(skills, player.getAllSkillsArray(), player.getLevel(), 0, player.getRace());
			case CLAN:
				skills = _pledgeSkillTree;
				Collection<Skill> skls = player.getClan().getSkills(); //TODO [VISTALL] придумать другой способ

				return getAvaliableList(skills, skls.toArray(new Skill[skls.size()]), player.getClan().getLevel(), 0, null);
			case SUB_UNIT:
				skills = _subUnitSkillTree;
				Collection<Skill> st = subUnit.getSkills(); //TODO [VISTALL] придумать другой способ

				return getAvaliableList(skills, st.toArray(new Skill[st.size()]), player.getClan().getLevel(), 0, null);
			case CERTIFICATION:
				skills = _certificationSkillTree;
				if(player == null)
					return skills;
				else
					return getAvaliableList(skills, player.getAllSkillsArray(), player.getLevel(), 0, player.getRace());
			case DUAL_CERTIFICATION:
				skills = _dualCertificationSkillTree;
				if(player == null || player.getDualClass() == null)
					return skills;
				else
					return getAvaliableList(skills, player.getAllSkillsArray(), player.getLevel(), 0, player.getRace());
			case NOBLESSE:
				skills = _noblesseSkillTree;
				if(player == null)
					return skills;
				else
					return getAvaliableList(skills, player.getAllSkillsArray(), player.getLevel(), 0, player.getRace());
			case HERO:
				skills = _heroSkillTree;
				if(player == null)
					return skills;
				else
					return getAvaliableList(skills, player.getAllSkillsArray(), player.getLevel(), 0, player.getRace());
			case GM:
				skills = _gmSkillTree;
				if(player == null)
					return skills;
				else
					return getAvaliableList(skills, player.getAllSkillsArray(), player.getLevel(), 0, player.getRace());
			case CHAOS:
				skills = _chaosSkillTree;
				if(skills == null)
				{
					info("Chaos skill tree is not defined !");
					return Collections.emptyList();
				}
				if(player == null)
					return skills;
				return getAvaliableList(skills, player.getAllSkillsArray(), player.getLevel(), 0, player.getRace());
			case DUAL_CHAOS:
				skills = _dualChaosSkillTree;
				if(skills == null)
				{
					info("Dual chaos skill tree is not defined !");
					return Collections.emptyList();
				}
				if(player == null)
					return skills;
				return getAvaliableList(skills, player.getAllSkillsArray(), player.getLevel(), 0, player.getRace());
			case ABILITY:
				skills = _abilitySkillTree;
				if(skills == null)
				{
					info("Ability skill tree is not defined !");
					return Collections.emptyList();
				}
				if(player == null)
					return skills;
				return getAvaliableList(skills, player.getAllSkillsArray(), player.getLevel(), 0, player.getRace());
			case ALCHEMY:
				skills = _alchemySkillTree;
				if(player == null)
					return skills;
				return getAvaliableAlchemySkillList(skills, player.getAllSkillsArray(), player.getLevel(), 0, player.getRace()); 
				
			default:
				return Collections.emptyList();
		}
	}

	private Collection<SkillLearn> getAvaliableList(Collection<SkillLearn> skillLearns, Skill[] skills, int level, int dualClassLevel, Race race)
	{
		TIntIntMap skillLvls = new TIntIntHashMap();
		for(Skill skill : skills)
		{
			if(skill == null)
				continue;
			skillLvls.put(skill.getId(), skill.getLevel());
		}
		Map<Integer, SkillLearn> skillLearnMap = new TreeMap<Integer, SkillLearn>();
		for(SkillLearn temp : skillLearns)
		{
			if(temp.getMinLevel() > level)
				continue;

			if(temp.getDualClassMinLvl() > dualClassLevel)
				continue;

			if(!temp.isOfRace(race))
				continue;

			int skillId = temp.getId();
			int skillLvl = temp.getLevel();
			if(!skillLvls.containsKey(skillId) && skillLvl == 1 || skillLvls.containsKey(skillId) && (skillLvl - skillLvls.get(skillId)) == 1)
				skillLearnMap.put(temp.getId(), temp);
		}

		return skillLearnMap.values();
	}

	public Collection<SkillLearn> getAvailableMaxLvlSkills(Player player, AcquireType type)
	{
		return getAvailableMaxLvlSkills(player, type, null);
	}

	public Collection<SkillLearn> getAvailableMaxLvlSkills(Player player, AcquireType type, SubUnit subUnit)
	{
		Collection<SkillLearn> skills;
		switch(type)
		{
			case NORMAL:
				skills = getNormalSkillTree(player);
				if(skills == null)
				{
					info("skill tree for class " + player.getActiveClassId() + " is not defined !");
					return Collections.emptyList();
				}
				return getAvaliableMaxLvlSkillList(skills, player.getAllSkillsArray(), player.getLevel(), player.getDualClassLevel(), player.getRace());
			case COLLECTION:
				skills = _collectionSkillTree;
				if(skills == null)
				{
					info("Collection skill tree is not defined !");
					return Collections.emptyList();
				}
				return getAvaliableMaxLvlSkillList(skills, player.getAllSkillsArray(), player.getLevel(), 0, player.getRace());
			case TRANSFORMATION:
				skills = _transformationSkillTree;
				if(skills == null)
				{
					info("Transformation skill tree is not defined !");
					return Collections.emptyList();
				}
				return getAvaliableMaxLvlSkillList(skills, player.getAllSkillsArray(), player.getLevel(), 0, player.getRace());
			case TRANSFER_EVA_SAINTS:
			case TRANSFER_SHILLIEN_SAINTS:
			case TRANSFER_CARDINAL:
				skills = _transferSkillTree.get(type.transferClassId());
				if(skills == null)
				{
					info("skill tree for class " + type.transferClassId() + " is not defined !");
					return Collections.emptyList();
				}
				if(player == null)
					return skills;
				else
				{
					Map<Integer, SkillLearn> skillLearnMap = new TreeMap<Integer, SkillLearn>();
					for(SkillLearn temp : skills)
						if(temp.isOfRace(player.getRace()) && temp.getMinLevel() <= player.getLevel())
						{
							int knownLevel = player.getSkillLevel(temp.getId());
							if(knownLevel == -1)
								skillLearnMap.put(temp.getId(), temp);
						}

					return skillLearnMap.values();
				}
			case FISHING:
				skills = _fishingSkillTree;
				if(skills == null)
				{
					info("Fishing skill tree is not defined !");
					return Collections.emptyList();
				}
				return getAvaliableMaxLvlSkillList(skills, player.getAllSkillsArray(), player.getLevel(), 0, player.getRace());
			case CLAN:
				skills = _pledgeSkillTree;
				Collection<Skill> skls = player.getClan().getSkills(); //TODO [VISTALL] придумать другой способ

				return getAvaliableMaxLvlSkillList(skills, skls.toArray(new Skill[skls.size()]), player.getClan().getLevel(), 0, null);
			case SUB_UNIT:
				skills = _subUnitSkillTree;
				Collection<Skill> st = subUnit.getSkills(); //TODO [VISTALL] придумать другой способ

				return getAvaliableMaxLvlSkillList(skills, st.toArray(new Skill[st.size()]), player.getClan().getLevel(), 0, null);
			case CERTIFICATION:
				skills = _certificationSkillTree;
				if(player == null)
					return skills;
				else
					return getAvaliableMaxLvlSkillList(skills, player.getAllSkillsArray(), player.getLevel(), 0, player.getRace());
			case DUAL_CERTIFICATION:
				skills = _dualCertificationSkillTree;
				if(player == null || player.getDualClass() == null)
					return skills;
				else
					return getAvaliableMaxLvlSkillList(skills, player.getAllSkillsArray(), player.getLevel(), 0, player.getRace());
			case NOBLESSE:
				skills = _noblesseSkillTree;
				if(player == null)
					return skills;
				else
					return getAvaliableMaxLvlSkillList(skills, player.getAllSkillsArray(), player.getLevel(), 0, player.getRace());
			case HERO:
				skills = _heroSkillTree;
				if(player == null)
					return skills;
				else
					return getAvaliableMaxLvlSkillList(skills, player.getAllSkillsArray(), player.getLevel(), 0, player.getRace());
			case GM:
				skills = _gmSkillTree;
				if(player == null)
					return skills;
				else
					return getAvaliableMaxLvlSkillList(skills, player.getAllSkillsArray(), player.getLevel(), 0, player.getRace());
			case CHAOS:
				skills = _chaosSkillTree;
				if(skills == null)
				{
					info("Chaos skill tree is not defined !");
					return Collections.emptyList();
				}
				if(player == null)
					return skills;
				return getAvaliableMaxLvlSkillList(skills, player.getAllSkillsArray(), player.getLevel(), 0, player.getRace());
			case DUAL_CHAOS:
				skills = _dualChaosSkillTree;
				if(skills == null)
				{
					info("Dual chaos skill tree is not defined !");
					return Collections.emptyList();
				}
				if(player == null)
					return skills;
				return getAvaliableMaxLvlSkillList(skills, player.getAllSkillsArray(), player.getLevel(), 0, player.getRace());
			case ABILITY:
				skills = _abilitySkillTree;
				if(skills == null)
				{
					info("Ability skill tree is not defined !");
					return Collections.emptyList();
				}
				if(player == null)
					return skills;
				return getAvaliableMaxLvlSkillList(skills, player.getAllSkillsArray(), player.getLevel(), 0, player.getRace());
			case ALCHEMY:
				skills = _alchemySkillTree;
				if(skills == null)
				{
					info("Alchemy skill tree is not defined !");
					return Collections.emptyList();
				}
				if(player == null)
					return skills;
				return getAvaliableAlchemySkillList(skills, player.getAllSkillsArray(), player.getLevel(), 0, player.getRace());
			default:
				return Collections.emptyList();
		}
	}

	private Collection<SkillLearn> getAvaliableMaxLvlSkillList(Collection<SkillLearn> skillLearns, Skill[] skills, int level, int dualClassLevel, Race race)
	{
		Map<Integer, SkillLearn> skillLearnMap = new TreeMap<Integer, SkillLearn>();
		for(SkillLearn temp : skillLearns)
		{
			if(temp.getMinLevel() > level)
				continue;

			if(temp.getDualClassMinLvl() > dualClassLevel)
				continue;

			if(!temp.isOfRace(race))
				continue;

			int skillId = temp.getId();
			int skillLvl = temp.getLevel();
			if(!skillLearnMap.containsKey(skillId) || skillLearnMap.containsKey(skillId) && skillLvl > skillLearnMap.get(skillId).getLevel())
				skillLearnMap.put(skillId, temp);
		}

		for(Skill skill : skills)
		{
			int skillId = skill.getId();
			if(!skillLearnMap.containsKey(skillId))
				continue;

			SkillLearn temp = skillLearnMap.get(skillId);
			if(temp == null)
				continue;

			if(temp.getLevel() <= skill.getLevel())
				skillLearnMap.remove(skillId);
		}

		return skillLearnMap.values();
	}

	private Collection<SkillLearn> getAvaliableAlchemySkillList(Collection<SkillLearn> skillLearns, Skill[] skills, int level, int dualClassLevel, Race race)
	{
		TIntIntMap skillLvls = new TIntIntHashMap();
		for(Skill skill : skills)
		{
			if(skill == null)
				continue;
			skillLvls.put(skill.getId(), skill.getLevel());
		}
		Map<Integer, SkillLearn> skillLearnMap = new TreeMap<Integer, SkillLearn>();
		for(SkillLearn temp : skillLearns)
		{
			if(temp.getDualClassMinLvl() > dualClassLevel)
				continue;

			if(!temp.isOfRace(race))
				continue;

			int skillId = temp.getId();
			int skillLvl = temp.getLevel();
			if(!skillLvls.containsKey(skillId) && skillLvl == 1 || skillLvls.containsKey(skillId) && (skillLvl - skillLvls.get(skillId)) == 1)
				skillLearnMap.put(temp.getId(), temp);
		}

		return skillLearnMap.values();
	}

	public Collection<Skill> getLearnedSkills(Player player, AcquireType type)
	{
		Collection<SkillLearn> skills;
		switch(type)
		{
			case ABILITY:
				skills = _abilitySkillTree;
				if(skills == null)
				{
					info("Ability skill tree is not defined !");
					return Collections.emptyList();
				}
				if(player == null)
					return Collections.emptyList();
				return getLearnedList(skills, player.getAllSkillsArray());
			default:
				return Collections.emptyList();
		}
	}

	private Collection<Skill> getLearnedList(Collection<SkillLearn> skillLearns, Skill[] skills)
	{
		TIntSet skillLvls = new TIntHashSet();
		for(SkillLearn temp : skillLearns)
			skillLvls.add(SkillTable.getSkillHashCode(temp.getId(), temp.getLevel()));

		List<Skill> learned = new ArrayList<Skill>();
		for(Skill skill : skills)
		{
			if(skill == null)
				continue;

			if(skillLvls.contains(skill.hashCode()))
				learned.add(skill);
		}

		return learned;
	}

	public Collection<SkillLearn> getAcquirableSkillListByClass(Player player)
	{
		Map<Integer, SkillLearn> skillListMap = new TreeMap<Integer, SkillLearn>();

		Collection<SkillLearn> skills = getNormalSkillTree(player);
		Collection<SkillLearn> currentLvlSkills = getAvaliableList(skills, player.getAllSkillsArray(), player.getLevel(), player.getDualClassLevel(), player.getRace());
		for(SkillLearn temp : currentLvlSkills)
		{
			if(!temp.isFreeAutoGet())
				skillListMap.put(temp.getId(), temp);
		}

		Collection<SkillLearn> nextLvlsSkills = getAvaliableList(skills, player.getAllSkillsArray(), player.getMaxLevel(), player.getMaxLevel(), player.getRace());
		for(SkillLearn temp : nextLvlsSkills)
		{
			if(!temp.isFreeAutoGet() && !skillListMap.containsKey(temp.getId()))
				skillListMap.put(temp.getId(), temp);
		}

		return skillListMap.values();
	}

	private Collection<SkillLearn> getNormalSkillTree(Player player)
	{
		Collection<SkillLearn> skills = new ArrayList<SkillLearn>();
		skills.addAll(_normalSkillTree.get(player.getActiveClassId()));
		if((player.isBaseClassActive() || player.isDualClassActive()) && player.getDualClassLevel() != -1) //last addon to appear the dual skills only if one has dual class
		{
			if(_dualClassSkillTree.containsKey(player.getActiveClassId()))
				skills.addAll(_dualClassSkillTree.get(player.getActiveClassId()));
		}
		skills.addAll(getAwakeParentSkillTree(player));
		return skills;
	}

	public Collection<SkillLearn> getAwakeParentSkillTree(Player player)
	{
		ClassId classId = player.getClassId().getBaseAwakedClassId();
		return getAwakeParentSkillTree(classId, ClassId.VALUES[player.getActiveDefaultClassId()]);
	}

	public Collection<SkillLearn> getAwakeParentSkillTree(ClassId classId, ClassId parentClassId)
	{
		if(classId == null || parentClassId == null)
			return Collections.emptyList();

		TIntObjectMap<List<SkillLearn>> awakeParentSkillTree = _awakeParentSkillTree.get(classId.getId());
		if(awakeParentSkillTree == null || awakeParentSkillTree.isEmpty())
			return Collections.emptyList();

		ClassId awakeParentId = classId.getAwakeParent(parentClassId);
		if(awakeParentId == null || !awakeParentSkillTree.containsKey(awakeParentId.getId()))
			return Collections.emptyList();

		return awakeParentSkillTree.get(awakeParentId.getId());
	}

	public SkillLearn getSkillLearn(Player player, int id, int level, AcquireType type)
	{
		Collection<SkillLearn> skills;
		switch(type)
		{
			case NORMAL:
				skills = getNormalSkillTree(player);
				break;
			case COLLECTION:
				skills = _collectionSkillTree;
				break;
			case TRANSFORMATION:
				skills = _transformationSkillTree;
				break;
			case TRANSFER_CARDINAL:
			case TRANSFER_SHILLIEN_SAINTS:
			case TRANSFER_EVA_SAINTS:
				skills = _transferSkillTree.get(player.getActiveClassId());
				break;
			case FISHING:
				skills = _fishingSkillTree;
				break;
			case CLAN:
				skills = _pledgeSkillTree;
				break;
			case SUB_UNIT:
				skills = _subUnitSkillTree;
				break;
			case CERTIFICATION:
				skills = _certificationSkillTree;
				break;
			case DUAL_CERTIFICATION:
				skills = _dualCertificationSkillTree;
				break;
			case NOBLESSE:
				skills = _noblesseSkillTree;
				break;
			case HERO:
				skills = _heroSkillTree;
				break;
			case GM:
				skills = _gmSkillTree;
				break;
			case CHAOS:
				skills = _chaosSkillTree;
				break;
			case DUAL_CHAOS:
				skills = _dualChaosSkillTree;
				break;
			case ABILITY:
				skills = _abilitySkillTree;
				break;
			case ALCHEMY:
				skills = _alchemySkillTree;
				break;
			default:
				return null;
		}

		if(skills == null)
			return null;

		for(SkillLearn temp : skills)
		{
			if(temp.isOfRace(player.getRace()) && temp.getLevel() == level && temp.getId() == id)
				return temp;
		}

		return null;
	}

	public boolean isSkillPossible(Player player, Skill skill, AcquireType type)
	{
		Clan clan = null;
		Collection<SkillLearn> skills;
		switch(type)
		{
			case NORMAL:
				skills = getNormalSkillTree(player);
				break;
			case COLLECTION:
				skills = _collectionSkillTree;
				break;
			case TRANSFORMATION:
				skills = _transformationSkillTree;
				break;
			case FISHING:
				skills = _fishingSkillTree;
				break;
			case TRANSFER_CARDINAL:
			case TRANSFER_EVA_SAINTS:
			case TRANSFER_SHILLIEN_SAINTS:
				int transferId = type.transferClassId();
				if(player.getActiveClassId() != transferId)
					return false;

				skills = _transferSkillTree.get(transferId);
				break;
			case CLAN:
				clan = player.getClan();
				if(clan == null)
					return false;
				skills = _pledgeSkillTree;
				break;
			case SUB_UNIT:
				clan = player.getClan();
				if(clan == null)
					return false;

				skills = _subUnitSkillTree;
				break;
			case CERTIFICATION:
				skills = _certificationSkillTree;
				break;
			case DUAL_CERTIFICATION:
				skills = _dualCertificationSkillTree;
				break;
			case FORGOTTEN_SCROLL:
				skills = _forgottenScrollsSkillTree.get(player.getActiveClassId());
				break;
			case NOBLESSE:
				if(!player.isNoble())
					return false;

				if(skill.getId() == Skill.SKILL_WYVERN_AEGIS && !(player.isClanLeader() && player.getClan().getCastle() > 0))
					return false;

				skills = _noblesseSkillTree;
				break;
			case HERO:
				if(!player.isHero() || !player.isBaseClassActive())
					return false;

				skills = _heroSkillTree;
				break;
			case GM:
				if(!player.isGM())
					return false;

				skills = _gmSkillTree;
				break;
			case CHAOS:
				if(!player.isBaseClassActive())
					return false;

				skills = _chaosSkillTree;
				break;
			case DUAL_CHAOS:
				if(!player.isDualClassActive())
					return false;

				skills = _dualChaosSkillTree;
				break;
			case ABILITY:
				if(!player.isAllowAbilities())
					return false;

				skills = _abilitySkillTree;
				break;
			case ALCHEMY:
				if(!player.isAllowAlchemy())
					return false;

				skills = _alchemySkillTree;
				break;
			default:
				return false;
		}

		return isSkillPossible(skills, skill, player.getRace());
	}

	private boolean isSkillPossible(Collection<SkillLearn> skills, Skill skill, Race race)
	{
		for(SkillLearn learn : skills)
		{
			if(learn.isOfRace(race) && learn.getId() == skill.getId() && learn.getLevel() <= skill.getLevel())
				return true;
		}
		return false;
	}

	public boolean isSkillPossible(Player player, Skill skill)
	{
		for(AcquireType aq : AcquireType.VALUES)
		{
			if(isSkillPossible(player, skill, aq))
				return true;
		}
		return false;
	}

	public List<SkillLearn> getSkillLearnListByItemId(Player player, int itemId)
	{
		List<SkillLearn> learns = _forgottenScrollsSkillTree.get(player.getActiveClassId());
		if(learns == null)
			return Collections.emptyList();

		List<SkillLearn> l = new ArrayList<SkillLearn>(1);
		for(SkillLearn $i : learns)
		{
			if($i.getItemId() == itemId)
				l.add($i);
		}
		return l;
	}

	public List<SkillLearn> getForgottenScrollsSkills()
	{
		List<SkillLearn> a = new ArrayList<SkillLearn>();
		for(TIntObjectIterator<List<SkillLearn>> i = _forgottenScrollsSkillTree.iterator(); i.hasNext();)
		{
			i.advance();
			for(SkillLearn learn : i.value())
			{
				a.add(learn);
			}
		}
		return a;
	}

	public void addAllNormalSkillLearns(TIntObjectMap<List<SkillLearn>> map)
	{
		_normalSkillTree.putAll(map);
	}

	//TODO: [Bonux] Добавить гендерные различия.
	public void initNormalSkillLearns()
	{
		TIntObjectMap<List<SkillLearn>> map = new TIntObjectHashMap<List<SkillLearn>>(_normalSkillTree);

		_normalSkillTree.clear();

		for(ClassId classId : ClassId.VALUES)
		{
			if(classId.isDummy())
				continue;

			int classID = classId.getId();

			List<SkillLearn> tempList = map.get(classID);
			if(tempList == null)
			{
				info("Not found NORMAL skill learn for class " + classID);
				continue;
			}

			List<SkillLearn> skills = new ArrayList<SkillLearn>();
			_normalSkillTree.put(classID, skills);

			if(!classId.isOfLevel(ClassLevel.AWAKED))
			{
				ClassId secondparent = classId.getParent(1);
				if(secondparent == classId.getParent(0))
					secondparent = null;

				classId = classId.getParent(0);
				while(classId != null)
				{
					if(_normalSkillTree.containsKey(classId.getId()))
						tempList.addAll(_normalSkillTree.get(classId.getId()));

					classId = classId.getParent(0);
					if(classId == null && secondparent != null)
					{
						classId = secondparent;
						secondparent = secondparent.getParent(1);
					}
				}
			}

			for(SkillLearn sk : tempList)
				skills.add(sk);
		}
	}

	public void addAllForgottenScrollsSkillLearns(TIntObjectMap<List<SkillLearn>> map)
	{
		_forgottenScrollsSkillTree.putAll(map);
	}

	//TODO: [Bonux] Добавить гендерные различия.
	public void initForgottenScrollsSkillLearns()
	{
		TIntObjectMap<List<SkillLearn>> map = new TIntObjectHashMap<List<SkillLearn>>(_forgottenScrollsSkillTree);
		List<SkillLearn> globalList = map.remove(-1); // Скиллы которые принадлежат любому классу.

		_forgottenScrollsSkillTree.clear();

		for(ClassId classId : ClassId.VALUES)
		{
			if(classId.isDummy())
				continue;

			int classID = classId.getId();

			List<SkillLearn> tempList = map.get(classID);
			if(tempList == null)
				tempList = new ArrayList<SkillLearn>();

			List<SkillLearn> skills = new ArrayList<SkillLearn>();
			_forgottenScrollsSkillTree.put(classID, skills);

			if(!classId.isOfLevel(ClassLevel.AWAKED))
			{
				ClassId secondparent = classId.getParent(1);
				if(secondparent == classId.getParent(0))
					secondparent = null;

				classId = classId.getParent(0);
				while(classId != null)
				{
					if(_forgottenScrollsSkillTree.containsKey(classId.getId()))
						tempList.addAll(_forgottenScrollsSkillTree.get(classId.getId()));

					classId = classId.getParent(0);
					if(classId == null && secondparent != null)
					{
						classId = secondparent;
						secondparent = secondparent.getParent(1);
					}
				}
			}

			tempList.addAll(globalList);

			for(SkillLearn sk : tempList)
			{
				if(sk.getItemId() > 0)
					skills.add(sk);
			}
		}
	}

	public void addAllDualClassSkillLearns(int classId, List<SkillLearn> s)
	{
		_dualClassSkillTree.put(classId, s);
	}

	public void addAllAwakeParentSkillLearns(TIntObjectMap<TIntObjectMap<List<SkillLearn>>> map)
	{
		_awakeParentSkillTree.putAll(map);
	}

	public void addAllTransferLearns(int classId, List<SkillLearn> s)
	{
		_transferSkillTree.put(classId, s);
	}

	public void addAllTransformationLearns(List<SkillLearn> s)
	{
		_transformationSkillTree.addAll(s);
	}

	public void addAllFishingLearns(List<SkillLearn> s)
	{
		_fishingSkillTree.addAll(s);
	}

	public void addAllCertificationLearns(List<SkillLearn> s)
	{
		_certificationSkillTree.addAll(s);
	}

	public void addAllDualCertificationLearns(List<SkillLearn> s)
	{
		_dualCertificationSkillTree.addAll(s);
	}

	public void addAllCollectionLearns(List<SkillLearn> s)
	{
		_collectionSkillTree.addAll(s);
	}

	public void addAllSubUnitLearns(List<SkillLearn> s)
	{
		_subUnitSkillTree.addAll(s);
	}

	public void addAllPledgeLearns(List<SkillLearn> s)
	{
		_pledgeSkillTree.addAll(s);
	}

	public void addAllNoblesseLearns(List<SkillLearn> s)
	{
		_noblesseSkillTree.addAll(s);
	}

	public void addAllHeroLearns(List<SkillLearn> s)
	{
		_heroSkillTree.addAll(s);
	}

	public void addAllGMLearns(List<SkillLearn> s)
	{
		_gmSkillTree.addAll(s);
	}

	public void addAllChaosSkillLearns(List<SkillLearn> s)
	{
		_chaosSkillTree.addAll(s);
	}

	public void addAllDualChaosSkillLearns(List<SkillLearn> s)
	{
		_dualChaosSkillTree.addAll(s);
	}

	public void addAllAbilitySkillLearns(List<SkillLearn> s)
	{
		_abilitySkillTree.addAll(s);
	}
	
	public void addAllAlchemySkillLearns(List<SkillLearn> s)
	{
		_alchemySkillTree.addAll(s);
	}

	public void setAbilitiesRefreshPrice(long value)
	{
		_abilitiesRefreshPrice = value;
	}

	public long getAbilitiesRefreshPrice()
	{
		return _abilitiesRefreshPrice;
	}

	public void setMaxAbilitiesPoints(int value)
	{
		_maxAbilitiesPoints = value;
	}

	public int getMaxAbilitiesPoints()
	{
		return _maxAbilitiesPoints;
	}

	public void addAbilitiesPointPrice(int ordinal, long price)
	{
		_abilitiesPointsPrices.put(ordinal, price);
	}

	public long getAbilitiesPointPrice(int ordinal)
	{
		return _abilitiesPointsPrices.get(ordinal);
	}

	@Override
	public void log()
	{
		info("load " + sizeTroveMap(_normalSkillTree) + " normal learns for " + _normalSkillTree.size() + " classes.");
		info("load " + sizeTroveMapMap(_awakeParentSkillTree) + " awake parent learns for " + _awakeParentSkillTree.size() + " classes.");
		info("load " + sizeTroveMap(_transferSkillTree) + " transfer learns for " + _transferSkillTree.size() + " classes.");
		info("load " + sizeTroveMap(_forgottenScrollsSkillTree) + " forgotten scroll skills learns for " + _forgottenScrollsSkillTree.size() + " classes.");
		info("load " + sizeTroveMap(_dualClassSkillTree) + " dual class skills learns for " + _dualClassSkillTree.size() + " classes.");

		//
		info("load " + _transformationSkillTree.size() + " transformation learns.");
		info("load " + _fishingSkillTree.size() + " fishing learns.");
		info("load " + _certificationSkillTree.size() + " certification learns.");
		info("load " + _dualCertificationSkillTree.size() + " dual certification learns.");
		info("load " + _collectionSkillTree.size() + " collection learns.");
		info("load " + _pledgeSkillTree.size() + " pledge learns.");
		info("load " + _subUnitSkillTree.size() + " sub unit learns.");
		info("load " + _noblesseSkillTree.size() + " noblesse skills learns.");
		info("load " + _heroSkillTree.size() + " hero skills learns.");
		info("load " + _gmSkillTree.size() + " GM skills learns.");
		info("load " + _chaosSkillTree.size() + " chaos skill learns.");
		info("load " + _dualChaosSkillTree.size() + " dual-chaos skill learns.");
		info("load " + _abilitySkillTree.size() + " abilities skill learns.");
		info("load " + _alchemySkillTree.size() + " alchemy skill learns.");
	}

	//@Deprecated
	@Override
	public int size()
	{
		return 0;
	}

	@Override
	public void clear()
	{
		_normalSkillTree.clear();
		_fishingSkillTree.clear();
		_transferSkillTree.clear();
		_dualClassSkillTree.clear();
		_certificationSkillTree.clear();
		_dualCertificationSkillTree.clear();
		_collectionSkillTree.clear();
		_pledgeSkillTree.clear();
		_subUnitSkillTree.clear();
		_forgottenScrollsSkillTree.clear();
		_awakeParentSkillTree.clear();
		_noblesseSkillTree.clear();
		_heroSkillTree.clear();
		_gmSkillTree.clear();
		_chaosSkillTree.clear();
		_dualChaosSkillTree.clear();
	}

	private int sizeTroveMapMap(TIntObjectMap<TIntObjectMap<List<SkillLearn>>> a)
	{
		int i = 0;
		for(TIntObjectIterator<TIntObjectMap<List<SkillLearn>>> iterator = a.iterator(); iterator.hasNext();)
		{
			iterator.advance();
			i += sizeTroveMap(iterator.value());
		}

		return i;
	}

	private int sizeTroveMap(TIntObjectMap<List<SkillLearn>> a)
	{
		int i = 0;
		for(TIntObjectIterator<List<SkillLearn>> iterator = a.iterator(); iterator.hasNext();)
		{
			iterator.advance();
			i += iterator.value().size();
		}

		return i;
	}
}