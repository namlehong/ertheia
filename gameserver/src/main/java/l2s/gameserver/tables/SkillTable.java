package l2s.gameserver.tables;

import gnu.trove.map.hash.TIntIntHashMap;

import java.util.Map;

import l2s.gameserver.model.Skill;
import l2s.gameserver.skills.SkillsEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkillTable
{
	private static final Logger _log = LoggerFactory.getLogger(SkillTable.class);

	private static final SkillTable _instance = new SkillTable();

	private Map<Integer, Skill> _skills;
	private TIntIntHashMap _maxLevelsTable;
	private TIntIntHashMap _baseLevelsTable;

	public static final SkillTable getInstance()
	{
		return _instance;
	}

	public void load()
	{
		_skills = SkillsEngine.getInstance().loadAllSkills();
		makeLevelsTable();
	}

	public void reload()
	{
		load();
	}

	public Skill getInfo(int skillId, int level)
	{
		return _skills.get(getSkillHashCode(skillId, level));
	}

	public int getMaxLevel(int skillId)
	{
		return _maxLevelsTable.get(skillId);
	}

	public int getBaseLevel(int skillId)
	{
		return _baseLevelsTable.get(skillId);
	}

	public static int getSkillHashCode(Skill skill)
	{
		return SkillTable.getSkillHashCode(skill.getId(), skill.getLevel());
	}

	public static int getSkillHashCode(int skillId, int skillLevel)
	{
		return skillId * 1023 + skillLevel;
	}

	private void makeLevelsTable()
	{
		_maxLevelsTable = new TIntIntHashMap();
		_baseLevelsTable = new TIntIntHashMap();
		for(Skill s : _skills.values())
		{
			int skillId = s.getId();
			int level = s.getLevel();
			int maxLevel = _maxLevelsTable.get(skillId);
			if(level > maxLevel)
				_maxLevelsTable.put(skillId, level);
			if(_baseLevelsTable.get(skillId) == 0)
				_baseLevelsTable.put(skillId, s.getBaseLevel());
		}
	}
}