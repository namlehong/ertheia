package blood.model;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import l2s.commons.collections.LazyArrayList;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;

public class AggroListPC
{
	private abstract class DamageHate
	{
		public int hate;
		public int damage;
	}

	public class HateInfoPC extends DamageHate
	{
		public final Creature attacker;

		HateInfoPC(Creature attacker, AggroInfoPC ai)
		{
			this.attacker = attacker;
			this.hate = ai.hate;
			this.damage = ai.damage;
		}
	}

	public class AggroInfoPC extends DamageHate
	{
		public final int attackerId;

		AggroInfoPC(Creature attacker)
		{
			this.attackerId = attacker.getObjectId();
		}
	}

	public static class DamageComparator implements Comparator<DamageHate>
	{
		private static Comparator<DamageHate> instance = new DamageComparator();

		public static Comparator<DamageHate> getInstance()
		{
			return instance;
		}

		DamageComparator()
		{}

		@Override
		public int compare(DamageHate o1, DamageHate o2)
		{
			return o2.damage - o1.damage;
		}
	}

	public static class HateComparator implements Comparator<DamageHate>
	{
		private static Comparator<DamageHate> instance = new HateComparator();

		public static Comparator<DamageHate> getInstance()
		{
			return instance;
		}

		HateComparator()
		{}

		@Override
		public int compare(DamageHate o1, DamageHate o2)
		{
			int diff = o2.hate - o1.hate;
			return diff == 0 ? o2.damage - o1.damage : diff;
		}
	}

	private final Player player;
	private final TIntObjectHashMap<AggroInfoPC> hateList = new TIntObjectHashMap<AggroInfoPC>();
	/** Блокировка для чтения/записи объектов списка */
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock readLock = lock.readLock();
	private final Lock writeLock = lock.writeLock();

	public AggroListPC(Player player)
	{
		this.player = player;
	}

	public void addDamageHate(Creature attacker, int damage, int aggro)
	{
		damage = Math.max(damage, 0);

		if(damage == 0 && aggro == 0)
			return;
			
		writeLock.lock();
		try
		{
			AggroInfoPC agroinfo;
			
			if ((agroinfo = hateList.get(attacker.getObjectId())) == null)
				hateList.put(attacker.getObjectId(), agroinfo = new AggroInfoPC(attacker));
				
			agroinfo.damage += damage;
			agroinfo.hate += aggro;
			agroinfo.damage = Math.max(agroinfo.damage, 0);
			agroinfo.hate = Math.max(agroinfo.hate, 0);
		}
		finally
		{
			writeLock.unlock();
		}
	}

	public AggroInfoPC get(Creature attacker)
	{
		readLock.lock();
		try
		{
			return hateList.get(attacker.getObjectId());
		}
		finally
		{
			readLock.unlock();
		}
	}

	public void remove(Creature attacker, boolean onlyHate)
	{
		writeLock.lock();
		try
		{
			if(!onlyHate)
			{
				hateList.remove(attacker.getObjectId());
				return;
			}

			AggroInfoPC ai = hateList.get(attacker.getObjectId());
			if(ai != null)
				ai.hate = 0;
		}
		finally
		{
			writeLock.unlock();
		}
	}

	public void clear()
	{
		clear(false);
	}

	public void clear(boolean onlyHate)
	{
		writeLock.lock();
		try
		{
			if(hateList.isEmpty())
				return;

			if(!onlyHate)
			{
				hateList.clear();
				return;
			}

			AggroInfoPC ai;
			for(TIntObjectIterator<AggroInfoPC> itr = hateList.iterator(); itr.hasNext();)
			{
				itr.advance();
				ai = itr.value();
				ai.hate = 0;
				if(ai.damage == 0)
					itr.remove();
			}
		}
		finally
		{
			writeLock.unlock();
		}
	}

	public boolean isEmpty()
	{
		readLock.lock();
		try
		{
			return hateList.isEmpty();
		}
		finally
		{
			readLock.unlock();
		}
	}

	public List<Creature> getHateList()
	{
		AggroInfoPC[] hated;

		readLock.lock();
		try
		{

			if(hateList.isEmpty())
				return Collections.emptyList();

			hated = hateList.values(new AggroInfoPC[hateList.size()]);
		}
		finally
		{
			readLock.unlock();
		}

		Arrays.sort(hated, HateComparator.getInstance());
		if(hated[0].hate == 0)
			return Collections.emptyList();

		List<Creature> hateList = new LazyArrayList<Creature>();
		List<Creature> chars = World.getAroundCharacters(player);
		AggroInfoPC ai;
		for(int i = 0; i < hated.length; i++)
		{
			ai = hated[i];
			if(ai.hate == 0)
				continue;
			for(Creature cha : chars)
				if(cha.getObjectId() == ai.attackerId)
				{
					hateList.add(cha);
					break;
				}
		}

		return hateList;
	}

	public Creature getMostHated()
	{
		AggroInfoPC[] hated;

		readLock.lock();
		try
		{

			if(hateList.isEmpty())
				return null;

			hated = hateList.values(new AggroInfoPC[hateList.size()]);
		}
		finally
		{
			readLock.unlock();
		}

		Arrays.sort(hated, HateComparator.getInstance());
		if(hated[0].hate == 0)
			return null;
		
		List<Creature> chars = World.getAroundCharacters(player);

		AggroInfoPC ai;
		loop: for(int i = 0; i < hated.length; i++)
		{
			ai = hated[i];
			if(ai.hate == 0)
				continue;
			for(Creature cha : chars)
				if(cha.getObjectId() == ai.attackerId)
				{
					if(cha.isDead())
						continue loop;
					return cha;
				}
		}

		return null;
	}

	public Creature getRandomHated()
	{
		AggroInfoPC[] hated;

		readLock.lock();
		try
		{
			if(hateList.isEmpty())
				return null;

			hated = hateList.values(new AggroInfoPC[hateList.size()]);
		}
		finally
		{
			readLock.unlock();
		}

		Arrays.sort(hated, HateComparator.getInstance());
		if(hated[0].hate == 0)
			return null;
		
		List<Creature> chars = World.getAroundCharacters(player);

		LazyArrayList<Creature> randomHated = LazyArrayList.newInstance();

		AggroInfoPC ai;
		Creature mostHated;
		loop: for(int i = 0; i < hated.length; i++)
		{
			ai = hated[i];
			if(ai.hate == 0)
				continue;
			for(Creature cha : chars)
				if(cha.getObjectId() == ai.attackerId)
				{
					if(cha.isDead())
						continue loop;
					randomHated.add(cha);
					break;
				}
		}

		if(randomHated.isEmpty())
			mostHated = null;
		else
			mostHated = randomHated.get(Rnd.get(randomHated.size()));

		LazyArrayList.recycle(randomHated);

		return mostHated;
	}

	public Creature getTopDamager()
	{
		AggroInfoPC[] hated;

		readLock.lock();
		try
		{
			if(hateList.isEmpty())
				return null;

			hated = hateList.values(new AggroInfoPC[hateList.size()]);
		}
		finally
		{
			readLock.unlock();
		}

		Creature topDamager = null;
		Arrays.sort(hated, DamageComparator.getInstance());
		if(hated[0].damage == 0)
			return null;
		
		List<Creature> chars = World.getAroundCharacters(player);
		AggroInfoPC ai;
		for(int i = 0; i < hated.length; i++)
		{
			ai = hated[i];
			if(ai.damage == 0)
				continue;
			for(Creature cha : chars)
				if(cha.getObjectId() == ai.attackerId)
				{
					topDamager = cha;
					return topDamager;
				}
		}
		return null;
	}

	public Map<Creature, HateInfoPC> getCharMap()
	{
		if(isEmpty())
			return Collections.emptyMap();

		Map<Creature, HateInfoPC> aggroMap = new HashMap<Creature, HateInfoPC>();
		List<Creature> chars = World.getAroundCharacters(player);
		readLock.lock();
		try
		{
			AggroInfoPC ai;
			for(TIntObjectIterator<AggroInfoPC> itr = hateList.iterator(); itr.hasNext();)
			{
				itr.advance();
				ai = itr.value();
				if(ai.damage == 0 && ai.hate == 0)
					continue;
				for(Creature attacker : chars)
					if(attacker.getObjectId() == ai.attackerId)
					{
						aggroMap.put(attacker, new HateInfoPC(attacker, ai));
						break;
					}
			}
		}
		finally
		{
			readLock.unlock();
		}

		return aggroMap;
	}

	public Map<Playable, HateInfoPC> getPlayableMap()
	{
		if(isEmpty())
			return Collections.emptyMap();

		Map<Playable, HateInfoPC> aggroMap = new HashMap<Playable, HateInfoPC>();
		List<Playable> chars = World.getAroundPlayables(player);
		readLock.lock();
		try
		{
			AggroInfoPC ai;
			for(TIntObjectIterator<AggroInfoPC> itr = hateList.iterator(); itr.hasNext();)
			{
				itr.advance();
				ai = itr.value();
				if(ai.damage == 0 && ai.hate == 0)
					continue;
				for(Playable attacker : chars)
					if(attacker.getObjectId() == ai.attackerId)
					{
						aggroMap.put(attacker, new HateInfoPC(attacker, ai));
						break;
					}
			}
		}
		finally
		{
			readLock.unlock();
		}

		return aggroMap;
	}
}
