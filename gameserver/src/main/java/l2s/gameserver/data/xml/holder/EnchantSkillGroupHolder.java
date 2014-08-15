package l2s.gameserver.data.xml.holder;

import gnu.trove.map.hash.TIntObjectHashMap;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.templates.EnchantSkillGroup;

/**
 * @author Bonux
**/
public final class EnchantSkillGroupHolder extends AbstractHolder
{
	private static final EnchantSkillGroupHolder _instance = new EnchantSkillGroupHolder();

	private TIntObjectHashMap<EnchantSkillGroup> _groups = new TIntObjectHashMap<EnchantSkillGroup>();

	public static EnchantSkillGroupHolder getInstance()
	{
		return _instance;
	}

	public void addGroup(EnchantSkillGroup group)
	{
		_groups.put(group.getId(), group);
	}

	public EnchantSkillGroup getGroup(int id)
	{
		return _groups.get(id);
	}

	@Override
	public int size()
	{
		return _groups.size();
	}

	@Override
	public void clear()
	{
		_groups.clear();
	}
}
