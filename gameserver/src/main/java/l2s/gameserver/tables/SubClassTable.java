package l2s.gameserver.tables;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.Arrays;
import java.util.Collection;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.SubClass;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.base.Sex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bonux
 */
public final class SubClassTable
{
	private static final Logger _log = LoggerFactory.getLogger(SubClassTable.class);

	private static SubClassTable _instance;

	private TIntObjectMap<TIntSet> _subClasses;

	public SubClassTable()
	{
		init();
	}

	public static SubClassTable getInstance()
	{
		if(_instance == null)
			_instance = new SubClassTable();
		return _instance;
	}

	private void init()
	{
		_subClasses = new TIntObjectHashMap<TIntSet>();

		for(ClassId baseClassId : ClassId.VALUES)
		{
			if(baseClassId.isDummy())
				continue;

			if(baseClassId.isOutdated())
				continue;

			if(baseClassId.isOfLevel(ClassLevel.NONE))
				continue;

			if(baseClassId.isOfLevel(ClassLevel.FIRST))
				continue;

			TIntSet availSubs = new TIntHashSet();
			for(ClassId subClassId : ClassId.VALUES)
			{
				if(subClassId.isDummy())
					continue;

				if(subClassId.isOutdated())
					continue;

				if(subClassId.isOfLevel(ClassLevel.NONE))
					continue;

				if(subClassId.isOfLevel(ClassLevel.FIRST))
					continue;

				if(!areClassesComportable(baseClassId, subClassId))
					continue;

				availSubs.add(subClassId.getId());
			}
			//availSubs.sort();
			_subClasses.put(baseClassId.getId(), availSubs);
		}
		_log.info("SubClassTable: Loaded " + _subClasses.size() + " sub-classes variations.");
	}

	public int[] getAvailableSubClasses(Player player, int classId, ClassLevel classLevel)
	{
		TIntSet subClassesList = _subClasses.get(classId);
		if(subClassesList == null || subClassesList.isEmpty())
			return new int[0];

		TIntSet tempSubClassesList = new TIntHashSet(subClassesList.size());
		tempSubClassesList.addAll(subClassesList);

		ClassId currClassId = ClassId.VALUES[classId];
		loop: for(int clsId : tempSubClassesList.toArray())
		{
			ClassId subClassId = ClassId.VALUES[clsId];
			if(subClassId.getClassLevel() != classLevel)
			{
				tempSubClassesList.remove(clsId);
				continue;
			}

			Collection<SubClass> playerSubClasses = player.getSubClassList().values();
			for(SubClass playerSubClass : playerSubClasses)
			{
				ClassId playerSubClassId = ClassId.VALUES[playerSubClass.getClassId()];
				if(!areClassesComportable(playerSubClassId, subClassId))
				{
					tempSubClassesList.remove(clsId);
					continue loop;
				}
			}

			if(classLevel == ClassLevel.AWAKED)
				continue;

			// Особенности саб-классов расы Камаэль.
			if(player.getRace() == Race.KAMAEL)
			{
				// Камаэлям доступны сабы только Камаэлевских проф.
				if(!subClassId.isOfRace(Race.KAMAEL))
				{
					tempSubClassesList.remove(clsId);
					continue;
				}

				// Удаляем саб-классы, которые не соответствуют полу.
				if(player.getSex() == Sex.FEMALE && subClassId == ClassId.M_SOUL_BREAKER || player.getSex() == Sex.MALE && subClassId == ClassId.F_SOUL_BREAKER)
				{
					tempSubClassesList.remove(clsId);
					continue;
				}

				// Удаляем саб-классы, которые не соответствуют полу.
				if(player.getSex() == Sex.FEMALE && subClassId == ClassId.M_SOUL_HOUND || player.getSex() == Sex.MALE && subClassId == ClassId.F_SOUL_HOUND)
				{
					tempSubClassesList.remove(clsId);
					continue;
				}
			}
			else if(player.getRace() == Race.ERTHEIA)
			{
				// Артеасам доступны сабы только Артеаские проф.
				if(!subClassId.isOfRace(Race.ERTHEIA))
				{
					tempSubClassesList.remove(clsId);
					continue;
				}
			}
			else if(subClassId.isOfRace(Race.KAMAEL))
			{
				tempSubClassesList.remove(clsId);
				continue;
			}
			else if(subClassId.isOfRace(Race.ERTHEIA))
			{
				tempSubClassesList.remove(clsId);
				continue;
			}
		}

		int[] result = tempSubClassesList.toArray();
		Arrays.sort(result);
		return result;
	}

	public static boolean areClassesComportable(ClassId baseClassId, ClassId subClassId)
	{
		if(baseClassId == subClassId)
			return false;

		if(baseClassId.getType2() == subClassId.getType2())
			return false; // Однотипные.

		if(subClassId == ClassId.OVERLORD || subClassId == ClassId.WARSMITH)
			return false; // Данные классы запрещены к получению его как саб-класса.

		if(subClassId == ClassId.MAESTRO || subClassId == ClassId.DOMINATOR)
			return false; // Данные классы запрещены к получению его как саб-класса.

		if(baseClassId.getRace() == null || subClassId.getRace() == null)
			return true;

		if(baseClassId.isOfRace(Race.KAMAEL) != subClassId.isOfRace(Race.KAMAEL))
			return false; // Камаэлям доступны классы только камаэлей.

		if(baseClassId.isOfRace(Race.ELF) && subClassId.isOfRace(Race.DARKELF) || baseClassId.isOfRace(Race.DARKELF) && subClassId.isOfRace(Race.ELF))
			return false; // Эльфам не доступны классы темных эльфов, и на оборот.

		return true;
	}
}