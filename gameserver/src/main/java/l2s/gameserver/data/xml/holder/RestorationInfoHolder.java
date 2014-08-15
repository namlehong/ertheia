package l2s.gameserver.data.xml.holder;

import gnu.trove.map.hash.TIntObjectHashMap;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.model.Skill;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.templates.skill.restoration.RestorationInfo;

/**
 * @author Bonux
 */
public final class RestorationInfoHolder extends AbstractHolder
{
	private static final RestorationInfoHolder _instance = new RestorationInfoHolder();

	private TIntObjectHashMap<RestorationInfo> _restorationInfoList = new TIntObjectHashMap<RestorationInfo>();

	public static RestorationInfoHolder getInstance()
	{
		return _instance;
	}

	public void addRestorationInfo(RestorationInfo info)
	{
		_restorationInfoList.put(SkillTable.getSkillHashCode(info.getSkillId(), info.getSkillLvl()), info);
	}

	public RestorationInfo getRestorationInfo(Skill skill)
	{
		return getRestorationInfo(skill.getId(), skill.getLevel());
	}

	public RestorationInfo getRestorationInfo(int skillId, int skillLvl)
	{
		return _restorationInfoList.get(SkillTable.getSkillHashCode(skillId, skillLvl));
	}

	@Override
	public int size()
	{
		return _restorationInfoList.size();
	}

	@Override
	public void clear()
	{
		_restorationInfoList.clear();
	}
}
