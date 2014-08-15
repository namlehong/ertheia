package l2s.gameserver.data.xml.holder;

import gnu.trove.map.hash.TIntObjectHashMap;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.templates.item.ItemGrade;
import l2s.gameserver.templates.item.support.CrystallizationInfo;

/**
 * @author Bonux
**/
public final class CrystallizationDataHolder extends AbstractHolder
{
	private static final CrystallizationDataHolder _instance = new CrystallizationDataHolder();

	private TIntObjectHashMap<TIntObjectHashMap<CrystallizationInfo>> _data = new TIntObjectHashMap<TIntObjectHashMap<CrystallizationInfo>>();

	public static CrystallizationDataHolder getInstance()
	{
		return _instance;
	}

	public void addData(ItemGrade grade, TIntObjectHashMap<CrystallizationInfo> data)
	{
		_data.put(grade.ordinal(), data);
	}

	public CrystallizationInfo getCrystallizationInfo(ItemGrade grade, int crystalCount)
	{
		TIntObjectHashMap<CrystallizationInfo> gradeData = _data.get(grade.ordinal());
		if(gradeData == null)
			return null;

		return gradeData.get(crystalCount);
	}

	@Override
	public int size()
	{
		return _data.size();
	}

	@Override
	public void clear()
	{
		_data.clear();
	}
}
