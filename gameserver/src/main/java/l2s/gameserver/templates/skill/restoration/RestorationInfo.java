package l2s.gameserver.templates.skill.restoration;

import java.util.ArrayList;
import java.util.List;

import l2s.commons.util.Rnd;

/**
 * @author Bonux
 */
public final class RestorationInfo
{
	private final int _skillId;
	private final int _skillLvl;
	private final int _itemConsumeId;
	private final int _itemConsumeCount;
	private final List<RestorationGroup> _restorationGroups;

	public RestorationInfo(int skillId, int skillLvl, int itemConsumeId, int itemConsumeCount)
	{
		_skillId = skillId;
		_skillLvl = skillLvl;
		_itemConsumeId = itemConsumeId;
		_itemConsumeCount = itemConsumeCount;
		_restorationGroups = new ArrayList<RestorationGroup>();
	}

	public int getSkillId()
	{
		return _skillId;
	}

	public int getSkillLvl()
	{
		return _skillLvl;
	}

	public int getItemConsumeId()
	{
		return _itemConsumeId;
	}

	public int getItemConsumeCount()
	{
		return _itemConsumeCount;
	}

	public void addRestorationGroup(RestorationGroup group)
	{
		_restorationGroups.add(group);
	}

	public List<RestorationItem> getRandomGroupItems()
	{
		double chancesAmount = 0;
		for(RestorationGroup group : _restorationGroups)
			chancesAmount += group.getChance();

		if(Rnd.chance(chancesAmount))
		{
			double chanceMod = (100. - chancesAmount) / _restorationGroups.size();
			List<RestorationGroup> successGroups = new ArrayList<RestorationGroup>();
			int tryCount = 0;
			while(successGroups.isEmpty())
			{
				tryCount++;
				for(RestorationGroup group : _restorationGroups)
				{
					if((tryCount % 10) == 0) //Немного теряем шанс, но зато зацикливания будут меньше.
						chanceMod += 1.;
					if(Rnd.chance(group.getChance() + chanceMod))
						successGroups.add(group);
				}
			}
			RestorationGroup[] groupsArray = successGroups.toArray(new RestorationGroup[successGroups.size()]);
			return groupsArray[Rnd.get(groupsArray.length)].getRestorationItems();
		}
		return new ArrayList<RestorationItem>(0);
	}
}
