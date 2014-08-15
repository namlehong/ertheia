package l2s.gameserver.model;

import gnu.trove.iterator.TIntObjectIterator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.ServitorAI;
import l2s.gameserver.dao.EffectsDAO;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Zone.ZoneType;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.model.actor.recorder.ServitorStatsChangeRecorder;
import l2s.gameserver.model.base.Experience;
import l2s.gameserver.model.base.TeamType;
import l2s.gameserver.model.entity.events.GlobalEvent;
import l2s.gameserver.model.entity.events.impl.DuelEvent;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.instances.PetInstance;
import l2s.gameserver.model.instances.SummonInstance;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.items.PetInventory;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ActionFailPacket;
import l2s.gameserver.network.l2.s2c.AutoAttackStartPacket;
import l2s.gameserver.network.l2.s2c.ExChangeNPCState;
import l2s.gameserver.network.l2.s2c.ExPartyPetWindowAdd;
import l2s.gameserver.network.l2.s2c.ExPartyPetWindowDelete;
import l2s.gameserver.network.l2.s2c.ExPartyPetWindowUpdate;
import l2s.gameserver.network.l2.s2c.L2GameServerPacket;
import l2s.gameserver.network.l2.s2c.NpcInfoPacket;
import l2s.gameserver.network.l2.s2c.PartySpelledPacket;
import l2s.gameserver.network.l2.s2c.PetDeletePacket;
import l2s.gameserver.network.l2.s2c.PetInfoPacket;
import l2s.gameserver.network.l2.s2c.PetItemListPacket;
import l2s.gameserver.network.l2.s2c.PetStatusShowPacket;
import l2s.gameserver.network.l2.s2c.PetStatusUpdatePacket;
import l2s.gameserver.network.l2.s2c.RelationChangedPacket;
import l2s.gameserver.network.l2.s2c.StatusUpdatePacket;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.scripts.Events;
import l2s.gameserver.skills.TimeStamp;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.taskmanager.DecayTaskManager;
import l2s.gameserver.templates.item.WeaponTemplate;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.templates.skill.EffectTemplate;
import l2s.gameserver.utils.Location;

public abstract class Servitor extends Playable
{
	public static class ServitorComparator implements Comparator<Servitor>
	{
		private static final ServitorComparator _instance = new ServitorComparator();

		public static final ServitorComparator getInstance()
		{
			return _instance;
		}

		@Override
		public int compare(Servitor o1, Servitor o2)
		{
			if(o1 == null)
				return -1;

			if(o2 == null)
				return 1;

			return o1.getSummonTime() - o2.getSummonTime();
		}
	}

	public static class UsedSkill
	{
		private final Skill _skill;
		private final int _actionId;

		public UsedSkill(Skill skill, int actionId)
		{
			_skill = skill;
			_actionId = actionId;
		}

		public Skill getSkill()
		{
			return _skill;
		}

		public int getActionId()
		{
			return _actionId;
		}
	}

	public static enum AttackMode
	{
		PASSIVE,
		DEFENCE;
	}

	private static final int SUMMON_DISAPPEAR_RANGE = 2500;

	private final static int BASE_CORPSE_TIME = 30;

	private final Player _owner;

	private int _spawnAnimation = 2;
	protected long _exp = 0;
	protected int _sp = 0;
	private int _maxLoad, _spsCharged;
	private boolean _follow = true, _depressed = false, _ssCharged = false;
	private AttackMode _attackMode = AttackMode.PASSIVE;
	private UsedSkill _usedSkill;

	private Future<?> _decayTask;

	private int _summonTime = 0;

	private int _index = 0;

	private final int _corpseTime;

	public Servitor(int objectId, NpcTemplate template, Player owner)
	{
		super(objectId, template);
		_owner = owner;

		if(template.getSkills().size() > 0)
		{
			for(TIntObjectIterator<Skill> iterator = template.getSkills().iterator(); iterator.hasNext();)
			{
				iterator.advance();
				addSkill(iterator.value());
			}
		}

		setXYZ(owner.getX() + Rnd.get(-100, 100), owner.getY() + Rnd.get(-100, 100), owner.getZ());

		_corpseTime = template.getAIParams().getInteger(NpcInstance.CORPSE_TIME, BASE_CORPSE_TIME);
	}

	@Override
	protected void onSpawn()
	{
		super.onSpawn();

		_spawnAnimation = 0;

		Player owner = getPlayer();
		Party party = owner.getParty();
		if(party != null)
			party.broadcastToPartyMembers(owner, new ExPartyPetWindowAdd(this));
		getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);

		EffectsDAO.getInstance().restoreEffects(this);

		if(owner.isInOlympiadMode())
			getEffectList().stopAllEffects();

		transferOwnerBuffs();

		_summonTime = (int) (System.currentTimeMillis() / 1000);
		_index = owner.getServitorsCount();
	}

	@Override
	public ServitorAI getAI()
	{
		if(_ai == null)
			synchronized (this)
			{
				if(_ai == null)
					_ai = new ServitorAI(this);
			}

		return (ServitorAI) _ai;
	}

	@Override
	public NpcTemplate getTemplate()
	{
		return (NpcTemplate) super.getTemplate();
	}

	@Override
	public boolean isUndead()
	{
		return getTemplate().isUndead();
	}

	// this defines the action buttons, 1 for Summon, 2 for Pets
	public abstract int getServitorType();

	public abstract int getEffectIdentifier();

	/**
	 * @return Returns the mountable.
	 */
	public boolean isMountable()
	{
		return false;
	}

	@Override
	public void onAction(final Player player, boolean shift)
	{
		Player owner = getPlayer();
		if(!isTargetable(player) && player != owner)
		{
			player.sendActionFailed();
			return;
		}

		if(isFrozen())
		{
			player.sendActionFailed();
			return;
		}

		if(Events.onAction(player, this, shift))
		{
			player.sendActionFailed();
			return;
		}

		if(player.getTarget() != this)
		{
			player.setTarget(this);
			if(player.getTarget() == this)
				player.sendPacket(makeStatusUpdate(StatusUpdatePacket.CUR_HP, StatusUpdatePacket.MAX_HP, StatusUpdatePacket.CUR_MP, StatusUpdatePacket.MAX_MP));
			else
				player.sendPacket(ActionFailPacket.STATIC);
		}
		else if(player == owner)
		{
			player.sendPacket(new PetInfoPacket(this).update());

			if(!player.isActionsDisabled())
				player.sendPacket(new PetStatusShowPacket(this));

			player.sendPacket(ActionFailPacket.STATIC);
		}
		else if(isAutoAttackable(player))
			player.getAI().Attack(this, false, shift);
		else
		{
			if(player.getAI().getIntention() != CtrlIntention.AI_INTENTION_FOLLOW)
			{
				if(!shift)
					player.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, this, Config.FOLLOW_RANGE);
				else
					player.sendActionFailed();
			}
			else
				player.sendActionFailed();
		}
	}

	public long getExpForThisLevel()
	{
		return Experience.getExpForLevel(getLevel());
	}

	public long getExpForNextLevel()
	{
		return Experience.getExpForLevel(getLevel() + 1);
	}

	@Override
	public int getNpcId()
	{
		return getTemplate().getId();
	}

	public final long getExp()
	{
		return _exp;
	}

	public final void setExp(final long exp)
	{
		_exp = exp;
	}

	public final int getSp()
	{
		return _sp;
	}

	public void setSp(final int sp)
	{
		_sp = sp;
	}

	@Override
	public int getMaxLoad()
	{
		return _maxLoad;
	}

	public void setMaxLoad(final int maxLoad)
	{
		_maxLoad = maxLoad;
	}

	@Override
	public int getBuffLimit()
	{
		Player owner = getPlayer();
		return (int) calcStat(Stats.BUFF_LIMIT, owner.getBuffLimit(), null, null);
	}

	public abstract int getCurrentFed();

	public abstract int getMaxFed();

	@Override
	protected void onDeath(Creature killer)
	{
		super.onDeath(killer);

		startDecay(getCorpseTime() * 1000L);

		Player owner = getPlayer();

		if(killer == null || killer == owner || killer == this || isInZoneBattle() || killer.isInZoneBattle())
			return;

		if(killer.isServitor())
			killer = killer.getPlayer();

		if(killer == null)
			return;

		if(killer.isPlayer())
		{
			if(killer.isMyServitor(getObjectId()))
				return;

			Player pk = (Player) killer;

			if(isInZone(ZoneType.SIEGE))
				return;

			if(getPvpFlag() > 0)
				return; //wtf every time killing the summon we'll get PK even if we're flaged?

			// Если убиваем саммона вара, то не даем ему карму.
			if(getPlayer().atMutualWarWith(pk))
				return;

			DuelEvent duelEvent = getEvent(DuelEvent.class);
			if((duelEvent == null || duelEvent != pk.getEvent(DuelEvent.class)) && !isPK())
			{
				int pkCountMulti = Math.max(pk.getPkKills() / 2, 1);
				pk.decreaseKarma(Config.KARMA_MIN_KARMA * pkCountMulti);
			}

			// Send a Server->Client UserInfo packet to attacker with its PK Kills Counter
			pk.sendChanges();
		}
	}

	protected void startDecay(long delay)
	{
		stopDecay();
		_decayTask = DecayTaskManager.getInstance().addDecayTask(this, delay);
	}

	protected void stopDecay()
	{
		if(_decayTask != null)
		{
			_decayTask.cancel(false);
			_decayTask = null;
		}
	}

	@Override
	protected void onDecay()
	{
		deleteMe();
	}

	public void endDecayTask()
	{
		stopDecay();
		doDecay();
	}

	@Override
	public void broadcastStatusUpdate()
	{
		if(!needStatusUpdate())
			return;

		Player owner = getPlayer();

		sendStatusUpdate();

		broadcastPacket(makeStatusUpdate(StatusUpdatePacket.MAX_HP, StatusUpdatePacket.CUR_HP));

		Party party = owner.getParty();
		if(party != null)
			party.broadcastToPartyMembers(owner, new ExPartyPetWindowUpdate(this));

		for(GlobalEvent e : getEvents())
			e.broadcastStatusUpdate(this);
	}

	public void sendStatusUpdate()
	{
		Player owner = getPlayer();
		owner.sendPacket(new PetStatusUpdatePacket(this));
	}

	@Override
	protected void onDelete()
	{
		Player owner = getPlayer();

		Party party = owner.getParty();
		if(party != null)
			party.broadcastToPartyMembers(owner, new ExPartyPetWindowDelete(this));
		owner.sendPacket(new PetDeletePacket(getObjectId(), getServitorType()));
		owner.deleteServitor(getObjectId());

		if(isSummon())
		{
			SummonInstance[] summons = owner.getSummons();
			if(summons.length > 0)
			{
				for(int i = (summons.length - 1); i >= 0; i--)
					// Посылаем задом наперед, чтобы не менялся порядок.
					owner.sendPacket(new PetInfoPacket(summons[i]));
			}
		}

		Servitor[] servitors = owner.getServitors();
		if(servitors.length > 0)
		{
			for(Servitor servitor : servitors)
			{
				if(_index < servitor.getIndex()) // Индекс теперь свободен, уменьшаем остальным саммонам.
					servitor.setIndex(servitor.getIndex() - 1);
			}
		}

		stopDecay();
		super.onDelete();
	}

	public void unSummon(boolean logout)
	{
		storeEffects(!logout);
		deleteMe();
	}

	public void storeEffects(boolean clean)
	{
		Player owner = getPlayer();
		if(owner == null)
			return;

		if(clean || owner.isInOlympiadMode())
			getEffectList().stopAllEffects();

		EffectsDAO.getInstance().insert(this);
	}

	public void setFollowMode(boolean state)
	{
		Player owner = getPlayer();

		_follow = state;

		if(_follow)
		{
			if(getAI().getIntention() == CtrlIntention.AI_INTENTION_ACTIVE)
				getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, owner, Config.FOLLOW_RANGE);
		}
		else if(getAI().getIntention() == CtrlIntention.AI_INTENTION_FOLLOW)
			getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
	}

	public boolean isFollowMode()
	{
		return _follow;
	}

	@Override
	public void updateEffectIconsImpl()
	{
		Player owner = getPlayer();
		PartySpelledPacket ps = new PartySpelledPacket(this, true);
		Party party = owner.getParty();
		if(party != null)
			party.broadCast(ps);
		else
			owner.sendPacket(ps);

		super.updateEffectIconsImpl();
	}

	public int getControlItemObjId()
	{
		return 0;
	}

	@Override
	public PetInventory getInventory()
	{
		return null;
	}

	@Override
	public void doPickupItem(final GameObject object)
	{}

	@Override
	public void doRevive()
	{
		super.doRevive();
		setRunning();
		getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
		setFollowMode(true);
	}

	/**
	 * Return null.<BR><BR>
	 */
	@Override
	public ItemInstance getActiveWeaponInstance()
	{
		return null;
	}

	@Override
	public WeaponTemplate getActiveWeaponTemplate()
	{
		return null;
	}

	@Override
	public ItemInstance getSecondaryWeaponInstance()
	{
		return null;
	}

	@Override
	public WeaponTemplate getSecondaryWeaponTemplate()
	{
		return null;
	}

	@Override
	public void displayGiveDamageMessage(Creature target, int damage, Servitor servitorTransferedDamage, int transferedDamage, boolean crit, boolean miss, boolean shld, boolean magic)
	{
		super.displayGiveDamageMessage(target, damage, servitorTransferedDamage, transferedDamage, crit, miss, shld, magic);
		if(crit)
			getPlayer().sendPacket(SystemMsg.SUMMONED_MONSTERS_CRITICAL_HIT);
		if(miss)
			getPlayer().sendPacket(new SystemMessage(SystemMessage.C1S_ATTACK_WENT_ASTRAY).addName(this));
		else if(!target.isInvul())
			getPlayer().sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_DONE_S3_POINTS_OF_DAMAGE_TO_C2).addName(this).addName(target).addInteger(damage).addHpChange(target.getObjectId(), getObjectId(), -damage));
	}

	@Override
	public void displayReceiveDamageMessage(Creature attacker, int damage)
	{
		if(attacker != this)
			getPlayer().sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_RECEIVED_S3_DAMAGE_FROM_C2).addName(this).addName(attacker).addInteger(damage).addHpChange(getObjectId(), attacker.getObjectId(), -damage));
	}

	@Override
	public boolean unChargeShots(final boolean spirit)
	{
		Player owner = getPlayer();

		if(spirit)
		{
			if(_spsCharged != 0)
			{
				_spsCharged = 0;
				owner.autoShot();
				return true;
			}
		}
		else if(_ssCharged)
		{
			_ssCharged = false;
			owner.autoShot();
			return true;
		}

		return false;
	}

	@Override
	public boolean getChargedSoulShot()
	{
		return _ssCharged;
	}

	@Override
	public int getChargedSpiritShot()
	{
		return _spsCharged;
	}

	public void chargeSoulShot()
	{
		_ssCharged = true;
	}

	public void chargeSpiritShot(final int state)
	{
		_spsCharged = state;
	}

	public int getSoulshotConsumeCount()
	{
		return 1;
	}

	public int getSpiritshotConsumeCount()
	{
		return 1;
	}

	public boolean isDepressed()
	{
		return _depressed;
	}

	public void setDepressed(final boolean depressed)
	{
		_depressed = depressed;
	}

	public boolean isInRange()
	{
		Player owner = getPlayer();
		return getDistance(owner) < SUMMON_DISAPPEAR_RANGE;
	}

	public void teleportToOwner()
	{
		Player owner = getPlayer();

		setNonAggroTime(System.currentTimeMillis() + Config.NONAGGRO_TIME_ONTELEPORT);
		setNonPvpTime(System.currentTimeMillis() + Config.NONPVP_TIME_ONTELEPORT);
		if(owner.isInOlympiadMode() || owner.getLfcGame() != null)
			teleToLocation(owner.getLoc(), owner.getReflection());
		else
			teleToLocation(Location.findPointToStay(owner, 50, 150), owner.getReflection());

		if(!isDead() && _follow)
			getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, owner, Config.FOLLOW_RANGE);
	}

	private ScheduledFuture<?> _broadcastCharInfoTask;

	public class BroadcastCharInfoTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			broadcastCharInfoImpl();
			_broadcastCharInfoTask = null;
		}
	}

	@Override
	public void broadcastCharInfo()
	{
		if(_broadcastCharInfoTask != null)
			return;

		_broadcastCharInfoTask = ThreadPoolManager.getInstance().schedule(new BroadcastCharInfoTask(), Config.BROADCAST_CHAR_INFO_INTERVAL);
	}

	@Override
	public void broadcastCharInfoImpl()
	{
		Player owner = getPlayer();

		for(Player player : World.getAroundPlayers(this))
		{
			if(player == owner)
				player.sendPacket(new PetInfoPacket(this).update());
			else if(!owner.isInvisible())
				player.sendPacket(new NpcInfoPacket(this, player).update());
		}
	}

	private Future<?> _petInfoTask;

	private class PetInfoTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			sendPetInfoImpl();
			_petInfoTask = null;
		}
	}

	private void sendPetInfoImpl()
	{
		Player owner = getPlayer();
		owner.sendPacket(new PetInfoPacket(this).update());
	}

	public void sendPetInfo()
	{
		sendPetInfo(false);
	}

	public void sendPetInfo(boolean force)
	{
		if(Config.USER_INFO_INTERVAL == 0 || force)
		{
			if(_petInfoTask != null)
			{
				_petInfoTask.cancel(false);
				_petInfoTask = null;
			}
			sendPetInfoImpl();
			return;
		}

		if(_petInfoTask != null)
			return;

		_petInfoTask = ThreadPoolManager.getInstance().schedule(new PetInfoTask(), Config.USER_INFO_INTERVAL);
	}

	/**
	 * Нужно для отображения анимации спауна, используется в пакете NpcInfo, PetInfo:
	 * 0=false, 1=true, 2=summoned (only works if model has a summon animation)
	 **/
	public int getSpawnAnimation()
	{
		return _spawnAnimation;
	}

	@Override
	public void startPvPFlag(Creature target)
	{
		Player owner = getPlayer();
		owner.startPvPFlag(target);
	}

	@Override
	public int getPvpFlag()
	{
		Player owner = getPlayer();
		return owner.getPvpFlag();
	}

	@Override
	public int getKarma()
	{
		Player owner = getPlayer();
		return owner.getKarma();
	}

	@Override
	public TeamType getTeam()
	{
		Player owner = getPlayer();
		return owner.getTeam();
	}

	@Override
	public Player getPlayer()
	{
		return _owner;
	}

	public abstract double getExpPenalty();

	@Override
	public ServitorStatsChangeRecorder getStatsRecorder()
	{
		if(_statsRecorder == null)
			synchronized (this)
			{
				if(_statsRecorder == null)
					_statsRecorder = new ServitorStatsChangeRecorder(this);
			}

		return (ServitorStatsChangeRecorder) _statsRecorder;
	}

	@Override
	public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper)
	{
		List<L2GameServerPacket> list = new ArrayList<L2GameServerPacket>();
		Player owner = getPlayer();

		if(owner == forPlayer)
		{
			list.add(new PetInfoPacket(this));
			list.add(new PartySpelledPacket(this, true));
			if(getNpcState() != 101)
				list.add(new ExChangeNPCState(getObjectId(), getNpcState()));

			if(isPet())
				list.add(new PetItemListPacket((PetInstance) this));
		}
		else if(!getPlayer().isInvisible())
		{
			Party party = forPlayer.getParty();
			if(getReflection() == ReflectionManager.GIRAN_HARBOR && (owner == null || party == null || party != owner.getParty()))
				return list;
			list.add(new NpcInfoPacket(this, forPlayer));
			if(owner != null && party != null && party == owner.getParty())
				list.add(new PartySpelledPacket(this, true));
			list.add(RelationChangedPacket.update(forPlayer, this, forPlayer));
		}
		else
			return Collections.emptyList();

		if(isInCombat())
			list.add(new AutoAttackStartPacket(getObjectId()));

		if(isInBoat())
			list.add(getBoat().getOnPacket(this, getInBoatPosition()));
		else
		{
			if(isMoving || isFollow)
				list.add(movePacket());
		}
		return list;
	}

	@Override
	public void startAttackStanceTask()
	{
		startAttackStanceTask0();
		Player player = getPlayer();
		if(player != null)
			player.startAttackStanceTask0();
	}

	@Override
	public <E extends GlobalEvent> E getEvent(Class<E> eventClass)
	{
		Player player = getPlayer();
		if(player != null)
			return player.getEvent(eventClass);
		else
			return super.getEvent(eventClass);
	}

	@Override
	public Set<GlobalEvent> getEvents()
	{
		Player player = getPlayer();
		if(player != null)
			return player.getEvents();
		else
			return super.getEvents();
	}

	@Override
	public void sendReuseMessage(Skill skill)
	{
		Player player = getPlayer();
		if(player != null && isSkillDisabled(skill))
		{
			TimeStamp sts = getSkillReuse(skill);
			if(sts == null || !sts.hasNotPassed())
				return;
			long timeleft = sts.getReuseCurrent();
			if(!Config.ALT_SHOW_REUSE_MSG && timeleft < 10000 || timeleft < 500)
				return;
			long hours = timeleft / 3600000;
			long minutes = (timeleft - hours * 3600000) / 60000;
			long seconds = (long) Math.ceil((timeleft - hours * 3600000 - minutes * 60000) / 1000.);
			if(hours > 0)
				player.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S2_HOURS_S3_MINUTES_AND_S4_SECONDS_REMAINING_IN_S1S_REUSE_TIME).addSkillName(skill.getId(), skill.getDisplayLevel()).addNumber(hours).addNumber(minutes).addNumber(seconds));
			else if(minutes > 0)
				player.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S2_MINUTES_S3_SECONDS_REMAINING_IN_S1S_REUSE_TIME).addSkillName(skill.getId(), skill.getDisplayLevel()).addNumber(minutes).addNumber(seconds));
			else
				player.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S2_SECONDS_REMAINING_IN_S1S_REUSE_TIME).addSkillName(skill.getId(), skill.getDisplayLevel()).addNumber(seconds));
		
		}
	}

	public boolean isServitor()
	{
		return true;
	}

	public boolean isHungry()
	{
		return false;
	}

	public boolean isNotControlled()
	{
		return false;
	}

	public int getNpcState()
	{
		return 101;
	}

	public void onAttacked(Creature attacker)
	{
		if(isAttackingNow())
			return;

		if(attacker == null || getPlayer() == null)
			return;

		if(getAttackMode() == AttackMode.DEFENCE)
		{
			setTarget(attacker);
			getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
		}
	}

	public void onOwnerGotAttacked(Creature attacker)
	{
		onAttacked(attacker);
	}

	public void onOwnerOfAttacks(Creature target)
	{
		if(isAttackingNow())
			return;

		if(target == null || getPlayer() == null)
			return;

		if(getAttackMode() == AttackMode.DEFENCE)
		{
			setTarget(target);
			getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
		}
	}

	public void setAttackMode(AttackMode mode)
	{
		_attackMode = mode;
	}

	public AttackMode getAttackMode()
	{
		return _attackMode;
	}

	public void transferOwnerBuffs()
	{
		Collection<Effect> effects = getPlayer().getEffectList().getEffects();
		if(!effects.isEmpty())
		{
			for(Effect e : effects)
			{
				if(e == null)
					continue;

				Skill skill = e.getSkill();
				if(e.isOffensive() || skill.isToggle() || skill.isCubicSkill())
					continue;

				if(isSummon() && !skill.applyEffectsOnSummon())
					continue;

				if(isPet() && !skill.applyEffectsOnPet())
					continue;

				Env env = new Env(e.getEffector(), this, skill);
				Effect effect = e.getTemplate().getEffect(env);
				if(effect == null || effect.getTemplate().isSingle())
					continue;

				effect.setCount(e.getCount());
				effect.setPeriod(e.getCount() == 1 ? (e.getPeriod() - e.getTime()) : e.getPeriod());

				getEffectList().addEffect(effect);
			}
		}
	}

	@Override
	public boolean checkPvP(final Creature target, Skill skill)
	{
		if(target != this && target.isServitor() && getPlayer().isMyServitor(target.getObjectId()))
		{
			if(skill == null || skill.isOffensive())
				return true;
		}

		return super.checkPvP(target, skill);
	}

	public UsedSkill getUsedSkill()
	{
		return _usedSkill;
	}

	public void setUsedSkill(Skill skill, int actionId)
	{
		_usedSkill = new UsedSkill(skill, actionId);
	}

	public void setUsedSkill(UsedSkill usedSkill)
	{
		_usedSkill = usedSkill;
	}

	public void notifyMasterDeath()
	{
		getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
		setFollowMode(true);		
	}

	public int getSummonTime()
	{
		return _summonTime;
	}

	@Override
	public boolean isSpecialEffect(Skill skill)
	{
		if(getPlayer() != null)
			return getPlayer().isSpecialEffect(skill);

		return false;
	}

	protected int getCorpseTime()
	{
		return _corpseTime;
	}

	public void setIndex(int index)
	{
		_index = index;
	}

	public int getIndex()
	{
		return _index;
	}
}