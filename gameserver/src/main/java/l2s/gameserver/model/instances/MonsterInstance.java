package l2s.gameserver.model.instances;

import gnu.trove.set.hash.TIntHashSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.dom4j.tree.LazyList;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.instancemanager.CursedWeaponsManager;
import l2s.gameserver.model.AggroList.HateInfo;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Manor;
import l2s.gameserver.model.MinionList;
import l2s.gameserver.model.Party;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.Experience;
import l2s.gameserver.model.base.TeamType;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestEventType;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.model.reward.RewardItem;
import l2s.gameserver.model.reward.RewardList;
import l2s.gameserver.model.reward.RewardType;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SocialActionPacket;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.templates.npc.Faction;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.Location;

/**
 * This class manages all Monsters.
 *
 * L2MonsterInstance :<BR><BR>
 * <li>L2MinionInstance</li>
 * <li>L2RaidBossInstance </li>
 */
public class MonsterInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	private static final String LONG_RANGE_GUARD_RATE = "LongRangeGuardRate";
	public static final String UNSOWING = "unsowing";

	private static final int MONSTER_MAINTENANCE_INTERVAL = 1000;

	protected static final class RewardInfo
	{
		protected Creature _attacker;
		protected int _dmg = 0;

		public RewardInfo(final Creature attacker, final int dmg)
		{
			_attacker = attacker;
			_dmg = dmg;
		}

		public void addDamage(int dmg)
		{
			if(dmg < 0)
				dmg = 0;

			_dmg += dmg;
		}

		@Override
		public int hashCode()
		{
			return _attacker.getObjectId();
		}
	}

	private ScheduledFuture<?> minionMaintainTask;
	private MinionList minionList;

	/** crops */
	private boolean _isSeeded;
	private int _seederId;
	private boolean _altSeed;
	private RewardItem _harvestItem;

	private final Lock harvestLock = new ReentrantLock();

	private int overhitAttackerId;
	/** Stores the extra (over-hit) damage done to the L2NpcInstance when the attacker uses an over-hit enabled skill */
	private double _overhitDamage;

	/** The table containing all players objectID that successfully absorbed the soul of this L2NpcInstance */
	private TIntHashSet _absorbersIds;
	private final Lock absorbLock = new ReentrantLock();

	/** True if a Dwarf has used Spoil on this L2NpcInstance */
	private boolean _isSpoiled;
	private int _isRobbed = 0; // 0 - not robbed, 1 - fail robbed, 2 - success robbed
	private int spoilerId;
	/** Table containing all Items that a Dwarf can Sweep on this L2NpcInstance */
	private List<RewardItem> _sweepItems;
	private final Lock sweepLock = new ReentrantLock();

	private int _isChampion;

	private final int _longRangeGuardRate;
	private final boolean _isUnsowing;

	public MonsterInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);

		minionList = new MinionList(this);

		_longRangeGuardRate = getParameter(LONG_RANGE_GUARD_RATE, 0);
		_isUnsowing = getParameter(UNSOWING, false);
	}

	@Override
	public boolean isMovementDisabled()
	{
		// Невозможность ходить для этих мобов
		return getNpcId() == 18344 || getNpcId() == 18345 || super.isMovementDisabled();
	}

	@Override
	public boolean isLethalImmune()
	{
		return _isChampion > 0 || getNpcId() == 22215 || getNpcId() == 22216 || getNpcId() == 22217 || super.isLethalImmune();
	}

	@Override
	public boolean isFearImmune()
	{
		return _isChampion > 0 || super.isFearImmune();
	}

	@Override
	public boolean isParalyzeImmune()
	{
		return _isChampion > 0 || super.isParalyzeImmune();
	}

	/**
	 * Return True if the attacker is not another L2MonsterInstance.<BR><BR>
	 */
	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return !attacker.isMonster();
	}

	public int getChampion()
	{
		return _isChampion;
	}

	public void setChampion()
	{
		if(getReflection().canChampions() && canChampion())
		{
			double random = Rnd.nextDouble();
			if(Config.ALT_CHAMPION_CHANCE2 / 100. >= random)
				setChampion(2);
			else if((Config.ALT_CHAMPION_CHANCE1 + Config.ALT_CHAMPION_CHANCE2) / 100. >= random)
				setChampion(1);
			else
				setChampion(0);
		}
		else
			setChampion(0);
	}

	public void setChampion(int level)
	{
		if(level == 0)
		{
			removeSkillById(4407);
			_isChampion = 0;
		}
		else
		{
			addSkill(SkillTable.getInstance().getInfo(4407, level));
			_isChampion = level;
		}
	}

	public boolean canChampion()
	{
		return getTemplate().rewardExp > 0 && getTemplate().level <= Config.ALT_CHAMPION_TOP_LEVEL;
	}

	@Override
	public TeamType getTeam()
	{
		return getChampion() == 2 ? TeamType.RED : getChampion() == 1 ? TeamType.BLUE : TeamType.NONE;
	}

	@Override
	protected void onSpawn()
	{
		super.onSpawn();

		setCurrentHpMp(getMaxHp(), getMaxMp(), true);

		if(getMinionList().hasMinions())
		{
			if(minionMaintainTask != null)
			{
				minionMaintainTask.cancel(false);
				minionMaintainTask = null;
			}
			minionMaintainTask = ThreadPoolManager.getInstance().schedule(new MinionMaintainTask(), 1000L);
		}
	}

	@Override
	protected void onDespawn()
	{
		setOverhitDamage(0);
		setOverhitAttacker(null);
		clearSweep();
		clearHarvest();
		clearAbsorbers();

		super.onDespawn();
	}

	@Override
	public MinionList getMinionList()
	{
		return minionList;
	}

	public class MinionMaintainTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			if(isDead())
				return;
			getMinionList().spawnMinions();
		}
	}

	public Location getMinionPosition()
	{
		return Location.findPointToStay(this, 100, 150);
	}

	public void notifyMinionDied(MinionInstance minion)
	{

	}

	public void spawnMinion(MonsterInstance minion)
	{
		minion.setReflection(getReflection());
		if(getChampion() == 2)
			minion.setChampion(1);
		else
			minion.setChampion(0);
		minion.setHeading(getHeading());
		minion.setCurrentHpMp(minion.getMaxHp(), minion.getMaxMp(), true);
		minion.spawnMe(getMinionPosition());
	}

	@Override
	public boolean hasMinions()
	{
		return getMinionList().hasMinions();
	}

	@Override
	public void setReflection(Reflection reflection)
	{
		super.setReflection(reflection);

		if(hasMinions())
			for(MinionInstance m : getMinionList().getAliveMinions())
				m.setReflection(reflection);
	}

	@Override
	protected void onDelete()
	{
		if(minionMaintainTask != null)
		{
			minionMaintainTask.cancel(false);
			minionMaintainTask = null;
		}

		getMinionList().deleteMinions();

		super.onDelete();
	}

	@Override
	protected void onDeath(Creature killer)
	{
		if(minionMaintainTask != null)
		{
			minionMaintainTask.cancel(false);
			minionMaintainTask = null;
		}

		calculateRewards(killer);
		
		if(getLevel() >= 85)
		{
			calculateEnergyofDestruction(killer);
			if(isOrbisMonster())
				calculateMarkEndurity(killer);
		}	

		super.onDeath(killer);
	}

	@Override
	protected void onReduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp)
	{
		if(skill != null && skill.isOverhit())
		{
			// Calculate the over-hit damage
			// Ex: mob had 10 HP left, over-hit skill did 50 damage total, over-hit damage is 40
			double overhitDmg = (getCurrentHp() - damage) * -1;
			if(overhitDmg <= 0)
			{
				setOverhitDamage(0);
				setOverhitAttacker(null);
			}
			else
			{
				setOverhitDamage(overhitDmg);
				setOverhitAttacker(attacker);
			}
		}

		super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp);
	}

	public void calculateRewards(Creature lastAttacker)
	{
		Creature topDamager = getAggroList().getTopDamager();
		if(lastAttacker == null || !lastAttacker.isPlayable())
			lastAttacker = topDamager;

		if(lastAttacker == null || !lastAttacker.isPlayable())
			return;

		Player killer = lastAttacker.getPlayer();
		if(killer == null)
			return;

		Map<Playable, HateInfo> aggroMap = getAggroList().getPlayableMap();

		Quest[] quests = getTemplate().getEventQuests(QuestEventType.MOB_KILLED_WITH_QUEST);
		if(quests != null && quests.length > 0)
		{
			List<Player> players = null; // массив с игроками, которые могут быть заинтересованы в квестах
			if(isRaid() && Config.ALT_NO_LASTHIT) // Для альта на ластхит берем всех игроков вокруг
			{
				players = new ArrayList<Player>();
				for(Playable pl : aggroMap.keySet())
					if(!pl.isDead() && (isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE) || killer.isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE)))
						if(!players.contains(pl.getPlayer())) // не добавляем дважды если есть пет
							players.add(pl.getPlayer());
			}
			else if(killer.getParty() != null) // если пати то собираем всех кто подходит
			{
				players = new ArrayList<Player>(killer.getParty().getMemberCount());
				for(Player pl : killer.getParty().getPartyMembers())
					if(!pl.isDead() && (isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE) || killer.isInRangeZ(pl, Config.ALT_PARTY_DISTRIBUTION_RANGE)))
						players.add(pl);
			}

			for(Quest quest : quests)
			{
				Player toReward = killer;
				if(quest.getParty() != Quest.PARTY_NONE && players != null)
					if(isRaid() || quest.getParty() == Quest.PARTY_ALL) // если цель рейд или квест для всей пати награждаем всех участников
					{
						for(Player pl : players)
						{
							QuestState qs = pl.getQuestState(quest.getName());
							if(qs != null && !qs.isCompleted())
								quest.notifyKill(this, qs);
						}
						toReward = null;
					}
					else
					{ // иначе выбираем одного
						List<Player> interested = new ArrayList<Player>(players.size());
						for(Player pl : players)
						{
							QuestState qs = pl.getQuestState(quest.getName());
							if(qs != null && !qs.isCompleted()) // из тех, у кого взят квест
								interested.add(pl);
						}

						if(interested.isEmpty())
							continue;

						toReward = interested.get(Rnd.get(interested.size()));
						if(toReward == null)
							toReward = killer;
					}

				if(toReward != null)
				{
					QuestState qs = toReward.getQuestState(quest.getName());
					if(qs != null && !qs.isCompleted())
						quest.notifyKill(this, qs);
				}
			}
		}

		Map<Player, RewardInfo> rewards = new HashMap<Player, RewardInfo>();
		for(HateInfo info : aggroMap.values())
		{
			if(info.damage <= 1)
				continue;
			Playable attacker = (Playable) info.attacker;
			Player player = attacker.getPlayer();
			RewardInfo reward = rewards.get(player);
			if(reward == null)
				rewards.put(player, new RewardInfo(player, info.damage));
			else
				reward.addDamage(info.damage);
		}

		if(topDamager != null && topDamager.isPlayable())
		{
			for(Map.Entry<RewardType, RewardList> entry : getTemplate().getRewards().entrySet())
				rollRewards(entry, lastAttacker, topDamager);
		}

		Player[] attackers = rewards.keySet().toArray(new Player[rewards.size()]);
		double[] xpsp = new double[2];

		for(Player attacker : attackers)
		{
			if(attacker.isDead())
				continue;

			RewardInfo reward = rewards.get(attacker);

			if(reward == null)
				continue;

			Party party = attacker.getParty();
			int maxHp = getMaxHp();

			xpsp[0] = 0.;
			xpsp[1] = 0.;

			if(party == null)
			{
				int damage = Math.min(reward._dmg, maxHp);
				if(damage > 0)
				{
					if(isInRangeZ(attacker, Config.ALT_PARTY_DISTRIBUTION_RANGE))
						xpsp = calculateExpAndSp(attacker.getLevel(), damage);

					xpsp[0] = applyOverhit(killer, xpsp[0]);

					attacker.addExpAndCheckBonus(this, (long) xpsp[0], (long) xpsp[1], 1.);
				}
				rewards.remove(attacker);
			}
			else
			{
				int partyDmg = 0;
				int partylevel = 1;
				List<Player> rewardedMembers = new ArrayList<Player>();
				for(Player partyMember : party.getPartyMembers())
				{
					RewardInfo ai = rewards.remove(partyMember);
					if(partyMember.isDead() || !isInRangeZ(partyMember, Config.ALT_PARTY_DISTRIBUTION_RANGE))
						continue;
					if(ai != null)
						partyDmg += ai._dmg;

					rewardedMembers.add(partyMember);
					if(partyMember.getLevel() > partylevel)
						partylevel = partyMember.getLevel();
				}
				partyDmg = Math.min(partyDmg, maxHp);
				if(partyDmg > 0)
				{
					xpsp = calculateExpAndSp(partylevel, partyDmg);
					double partyMul = (double) partyDmg / maxHp;
					xpsp[0] *= partyMul;
					xpsp[1] *= partyMul;
					xpsp[0] = applyOverhit(killer, xpsp[0]);
					party.distributeXpAndSp(xpsp[0], xpsp[1], rewardedMembers, lastAttacker, this);
				}
			}
		}

		// Check the drop of a cursed weapon
		CursedWeaponsManager.getInstance().dropAttackable(this, killer);
	}

	@Override
	public void onRandomAnimation()
	{
		if(System.currentTimeMillis() - _lastSocialAction > 10000L)
		{
			broadcastPacket(new SocialActionPacket(getObjectId(), 1));
			_lastSocialAction = System.currentTimeMillis();
		}
	}

	@Override
	public void startRandomAnimation()
	{
		//У мобов анимация обрабатывается в AI
	}

	@Override
	public int getKarma()
	{
		return 0;
	}

	public void addAbsorber(final Player attacker)
	{
		// The attacker must not be null
		if(attacker == null)
			return;

		if(getCurrentHpPercents() > 50)
			return;

		absorbLock.lock();
		try
		{
			if(_absorbersIds == null)
				_absorbersIds = new TIntHashSet();

			_absorbersIds.add(attacker.getObjectId());
		}
		finally
		{
			absorbLock.unlock();
		}
	}

	public boolean isAbsorbed(Player player)
	{
		absorbLock.lock();
		try
		{
			if(_absorbersIds == null)
				return false;
			if(!_absorbersIds.contains(player.getObjectId()))
				return false;
		}
		finally
		{
			absorbLock.unlock();
		}
		return true;
	}

	public void clearAbsorbers()
	{
		absorbLock.lock();
		try
		{
			if(_absorbersIds != null)
				_absorbersIds.clear();
		}
		finally
		{
			absorbLock.unlock();
		}
	}

	public RewardItem takeHarvest()
	{
		harvestLock.lock();
		try
		{
			RewardItem harvest;
			harvest = _harvestItem;
			clearHarvest();
			return harvest;
		}
		finally
		{
			harvestLock.unlock();
		}
	}

	public void clearHarvest()
	{
		harvestLock.lock();
		try
		{
			_harvestItem = null;
			_altSeed = false;
			_seederId = 0;
			_isSeeded = false;
		}
		finally
		{
			harvestLock.unlock();
		}
	}

	public boolean setSeeded(Player player, int seedId, boolean altSeed)
	{
		harvestLock.lock();
		try
		{
			if(isSeeded())
				return false;
			_isSeeded = true;
			_altSeed = altSeed;
			_seederId = player.getObjectId();
			_harvestItem = new RewardItem(Manor.getInstance().getCropType(seedId));
			// Количество всходов от xHP до (xHP + xHP/2)
			if(getTemplate().rateHp > 1)
				_harvestItem.count = Rnd.get(Math.round(getTemplate().rateHp), Math.round(1.5 * getTemplate().rateHp));
		}
		finally
		{
			harvestLock.unlock();
		}

		return true;
	}

	public boolean isSeeded(Player player)
	{
		//засиден этим игроком, и смерть наступила не более 20 секунд назад
		return isSeeded() && _seederId == player.getObjectId() && getDeadTime() < 20000L;
	}

	public boolean isSeeded()
	{
		return _isSeeded;
	}

	/**
	 * Return True if this L2NpcInstance has drops that can be sweeped.<BR><BR>
	 */
	public boolean isSpoiled()
	{
		return _isSpoiled;
	}

	public boolean isSpoiled(Player player)
	{
		if(!isSpoiled()) // если не заспойлен то false
			return false;

		//заспойлен этим игроком, и смерть наступила не более 20 секунд назад
		if(player.getObjectId() == spoilerId && getDeadTime() < 20000L)
			return true;

		if(player.isInParty())
			for(Player pm : player.getParty().getPartyMembers())
				if(pm.getObjectId() == spoilerId && getDistance(pm) < Config.ALT_PARTY_DISTRIBUTION_RANGE)
					return true;

		return false;
	}

	/**
	 * Set the spoil state of this L2NpcInstance.<BR><BR>
	 * @param player
	 */
	public boolean setSpoiled(Player player)
	{
		sweepLock.lock();
		try
		{
			if(isSpoiled())
				return false;
			_isSpoiled = true;
			spoilerId = player.getObjectId();
		}
		finally
		{
			sweepLock.unlock();
		}
		return true;
	}

	/**
	 * Return True if a Dwarf use Sweep on the L2NpcInstance and if item can be spoiled.<BR><BR>
	 */
	public boolean isSweepActive()
	{
		sweepLock.lock();
		try
		{
			return _sweepItems != null && _sweepItems.size() > 0;
		}
		finally
		{
			sweepLock.unlock();
		}
	}

	public List<RewardItem> takeSweep()
	{
		sweepLock.lock();
		try
		{
			List<RewardItem> sweep = _sweepItems;
			clearSweep();
			return sweep;
		}
		finally
		{
			sweepLock.unlock();
		}
	}

	public void clearSweep()
	{
		sweepLock.lock();
		try
		{
			_isSpoiled = false;
			spoilerId = 0;
			_sweepItems = null;
			_isRobbed = 0;
		}
		finally
		{
			sweepLock.unlock();
		}
	}

	public void rollRewards(Map.Entry<RewardType, RewardList> entry, final Creature lastAttacker, Creature topDamager)
	{
		rollRewards(entry.getKey(), entry.getValue(), lastAttacker, topDamager);
	}

	public void rollRewards(RewardType type, RewardList list, final Creature lastAttacker, Creature topDamager)
	{
		if(type == RewardType.SWEEP && !isSpoiled())
			return;

		final Creature activeChar = type == RewardType.SWEEP ? lastAttacker : topDamager;
		final Player activePlayer = activeChar.getPlayer();

		if(activePlayer == null)
			return;

		final int diff = calculateLevelDiffForDrop(topDamager.getLevel());
		double mod = calcStat(Stats.REWARD_MULTIPLIER, 1., activeChar, null);
		mod *= Experience.penaltyModifier(diff, 9);

		boolean isLuckTrigger = activePlayer.isLuckTrigger();
		
		List<RewardItem> rewardItems = list.roll(activePlayer, mod, this instanceof RaidBossInstance);
		switch(type)
		{
			case SWEEP:
				int countModifier = 1;
				if(isLuckTrigger)
					countModifier = 2;
				List<RewardItem> sweepItems = new LazyList<RewardItem>();
				for(RewardItem rewardItem : rewardItems)
				{
					rewardItem.count *= countModifier;
					
					sweepItems.add(rewardItem);
				}
				_sweepItems = sweepItems;
				break;
			default:
				for(RewardItem drop : rewardItems)
				{
					// Если в моба посеяно семя, причем не альтернативное - не давать никакого дропа, кроме адены.
					if(isSeeded() && !_altSeed && !drop.isAdena)
						continue;

					if(!Config.DROP_ONLY_THIS.isEmpty() && !Config.DROP_ONLY_THIS.contains(drop.itemId))
					{
						if(!(Config.INCLUDE_RAID_DROP && isRaid()))
							return;
					}
					dropItem(activePlayer, drop.itemId, drop.count);
				}
				if(isLuckTrigger)
				{
					//Give Fortune Pocket Lv1
					dropItem(activePlayer, 39629, 1);
				}
				break;
		}
	}

	//TODO: [Bonux] Сверить с оффом GoD: Harmony.
	private double[] calculateExpAndSp(int level, long damage)
	{
		int diff = level - getLevel();
		if(Math.abs(diff) > 10)
			return new double[] { 0, 0 };

		if(level > 77 && diff > 3 && diff <= 5) // kamael exp penalty
			diff += 3;

		double xp = getExpReward() * damage / getMaxHp();
		double sp = getSpReward() * damage / getMaxHp();

		if(diff > 5)
		{
			double mod = Math.pow(.83, diff - 5);
			xp *= mod;
			sp *= mod;
		}

		if((level > 84) && (diff < -2))
		{
			double mod = Math.pow(.83, Math.abs(diff + 3));
			xp *= mod;
			sp *= mod;
		}

		xp = Math.max(0., xp);
		sp = Math.max(0., sp);

		return new double[] { xp, sp };
	}

	private double applyOverhit(Player killer, double xp)
	{
		if(xp > 0 && killer.getObjectId() == overhitAttackerId)
		{
			int overHitExp = calculateOverhitExp(xp);
			killer.sendPacket(SystemMsg.OVERHIT, new SystemMessage(SystemMessage.ACQUIRED_S1_BONUS_EXPERIENCE_THROUGH_OVER_HIT).addNumber(overHitExp));
			xp += overHitExp;
		}
		return xp;
	}

	@Override
	public void setOverhitAttacker(Creature attacker)
	{
		overhitAttackerId = attacker == null ? 0 : attacker.getObjectId();
	}

	public double getOverhitDamage()
	{
		return _overhitDamage;
	}

	@Override
	public void setOverhitDamage(double damage)
	{
		_overhitDamage = damage;
	}

	public int calculateOverhitExp(final double normalExp)
	{
		double overhitPercentage = getOverhitDamage() * 100 / getMaxHp();
		if(overhitPercentage > 25)
			overhitPercentage = 25;
		double overhitExp = overhitPercentage / 100 * normalExp;
		setOverhitAttacker(null);
		setOverhitDamage(0);
		return (int) Math.round(overhitExp);
	}

	@Override
	public boolean isAggressive()
	{
		return (Config.ALT_CHAMPION_CAN_BE_AGGRO || getChampion() == 0) && super.isAggressive();
	}

	@Override
	public Faction getFaction()
	{
		return Config.ALT_CHAMPION_CAN_BE_SOCIAL || getChampion() == 0 ? super.getFaction() : Faction.NONE;
	}

	@Override
	public void reduceCurrentHp(double i, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflectAndAbsorb, boolean transferDamage, boolean isDot, boolean sendReceiveMessage)
	{
		checkUD(attacker);
		super.reduceCurrentHp(i, attacker, skill, awake, standUp, directHp, canReflectAndAbsorb, transferDamage, isDot, sendReceiveMessage);
	}

	private final double MIN_DISTANCE_FOR_USE_UD = 150.0;
	private final double MIN_DISTANCE_FOR_CANCEL_UD = 50.0;

	private void checkUD(Creature attacker)
	{
		if(_longRangeGuardRate <= 0)
			return;

		int skillId = 5044;
		int skillLvl = 1;
		if(getLevel() >= 41 || getLevel() <= 60)
			skillLvl = 2;
		else if(getLevel() > 60)
			skillLvl = 3;

		double distance = getDistance(attacker);
		if(distance <= MIN_DISTANCE_FOR_CANCEL_UD)
			getEffectList().stopEffects(skillId);
		else if(distance >= MIN_DISTANCE_FOR_USE_UD)
		{
			if(Rnd.chance(_longRangeGuardRate))
			{
				Skill skill = SkillTable.getInstance().getInfo(skillId, skillLvl);
				if(skill != null)
					skill.getEffects(this, this, false);
			}
		}
	}

	@Override
	public boolean isMonster()
	{
		return true;
	}

	@Override
	public Clan getClan()
	{
		return null;
	}

	@Override
	public boolean isPeaceNpc()
	{
		return false;
	}

	public void setRobbed(int val)
	{
		_isRobbed = val;
	}

	public int isRobbed()
	{
		return _isRobbed;
	}
	
	private void calculateEnergyofDestruction(Creature killer)
	{
		if(killer == null || killer.getPlayer() == null)
			return;
		
		if(!Rnd.chance(Config.ENERGY_OF_DESTRUCTION_CHANCE))
			return;
		if(killer.getPlayer().getDestructionCount() == 2)
			return;
			
		long count = 1;
		if(Rnd.chance(Config.ENERGY_OF_DESTRUCTION_DOUBLE_CHANCE))
			count = 2;
		
		killer.getPlayer().addDestructionCount(count);
			
		if(killer.getPlayer().getDestructionCount() == 1)
			killer.getPlayer().sendPacket(SystemMsg.YOU_HAVE_OBTAINED_THE_FIRST_ENERGY_OF_DESTRUCTION_YOU_CAN_OBTAIN_UP_TO_2_OF_THESE_A_DAY_AND_CAN_BEGIN_OBTAINING_THEM_AGAIN_AT_630AM_EVERY_DAY);
		else
			killer.getPlayer().sendPacket(SystemMsg.YOU_HAVE_OBTAINED_THE_SECOND_ENERGY_OF_DESTRUCTION_YOU_CAN_OBTAIN_UP_TO_2_OF_THESE_A_DAY_AND_CAN_BEGIN_OBTAINING_THEM_AGAIN_AT_630AM_EVERY_DAY);
			
		Functions.addItem(killer.getPlayer(), 35562, count); //Energy of Destruction		
	}
	
	public void calculateMarkEndurity(Creature killer)
	{
		if(killer == null || killer.getPlayer() == null)
			return;
		
		if(!Rnd.chance(Config.ENERGY_OF_DESTRUCTION_CHANCE)) //the same for now
			return;
		if(killer.getPlayer().getMarkEndureCount() > 0)
			return;
			
		long count = 1;
		
		killer.getPlayer().addMarkEndureCount(count);
						
		Functions.addItem(killer.getPlayer(), 17742, count); //Energy of Destruction		
	}
	
	public boolean isOrbisMonster()
	{
		return false;
	}

	public final boolean isUnsowing()
	{
		return _isUnsowing;
	}
	
	/**
	 * New method
	 */
	
	/**
	 * Method takeSweep.
	 * @return List<RewardItem>
	 */
	public List<RewardItem> takePlunder(Player activeChar)
	{
		//System.out.println("takePlunder");
		sweepLock.lock();
		try
		{
			//System.out.println("test level");
			final int diff = activeChar.getLevel() - getLevel();
			
			if(diff > 9 || diff < -11)
			{
				//System.out.println("level is not appropriated " + diff);
				return null;
			}
			
			boolean isLuckTrigger = activeChar.isLuckTrigger();
			
			for (Map.Entry<RewardType, RewardList> entry : getTemplate().getRewards().entrySet())
			{
				RewardType type = entry.getKey();
				RewardList list = entry.getValue();
				//System.out.println("Reward type " + type);
				if ((type == RewardType.SWEEP))
				{
					//System.out.println("Found sweep");
					double mod = calcStat(Stats.REWARD_MULTIPLIER, 1., activeChar, null);
					mod *= Experience.penaltyModifier(diff, 9);
					List<RewardItem> rewardItems = list.roll(activeChar, mod, this instanceof RaidBossInstance);
					//System.out.println("rewardItems " + rewardItems.size());
					
					if(isLuckTrigger)
					{
						List<RewardItem> sweepItems = new LazyList<RewardItem>();
						for(RewardItem rewardItem : rewardItems)
						{
							rewardItem.count *= 2;
							
							sweepItems.add(rewardItem);
						}
						return sweepItems;
					}
					else
						return rewardItems;
				}
			}
			return null;
			
		}
		finally
		{
			sweepLock.unlock();
		}
	}
}