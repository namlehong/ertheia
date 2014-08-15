package l2s.gameserver.skills.skillclasses;

import java.util.List;

import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.data.xml.holder.NpcHolder;
import l2s.gameserver.idfactory.IdFactory;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.FakePlayer;
import l2s.gameserver.model.GameObjectTasks;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.base.Experience;
import l2s.gameserver.model.entity.events.impl.SiegeEvent;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.instances.SummonInstance;
import l2s.gameserver.model.instances.SummonInstance.RestoredSummon;
import l2s.gameserver.model.instances.SymbolInstance;
import l2s.gameserver.model.instances.TrapInstance;
import l2s.gameserver.model.instances.TreeInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.stats.funcs.FuncAdd;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Summon extends Skill
{
	private static final Logger _log = LoggerFactory.getLogger(Summon.class);

	private final SummonType _summonType;

	private final double _expPenalty;
	private final int _itemConsumeIdInTime;
	private final int _itemConsumeCountInTime;
	private final int _itemConsumeDelay;
	private final int _lifeTime;
	private final int _summonPoints;
	private final int _summonsCount;
	private final boolean _isSaveableSummon;

	private static enum SummonType
	{
		PET,
		SIEGE_SUMMON,
		AGATHION,
		TRAP,
		NPC,
		TREE,
		SYMBOL,
		CLONE
	}

	public Summon(StatsSet set)
	{
		super(set);

		_summonType = Enum.valueOf(SummonType.class, set.getString("summonType", "PET").toUpperCase());
		_expPenalty = set.getDouble("expPenalty", 0.f);
		_itemConsumeIdInTime = set.getInteger("itemConsumeIdInTime", 0);
		_itemConsumeCountInTime = set.getInteger("itemConsumeCountInTime", 0);
		_itemConsumeDelay = set.getInteger("itemConsumeDelay", 240) * 1000;
		_lifeTime = set.getInteger("lifeTime", 2000000) * 1000;
		_summonPoints = Math.max(set.getInteger("summon_points", 0), 0);
		_summonsCount = Math.max(set.getInteger("summon_count", 1), 1);
		_isSaveableSummon = set.getBool("is_saveable_summon", true);
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		Player player = activeChar.getPlayer();
		if(player == null)
			return false;

		if(player.isProcessingRequest())
		{
			player.sendPacket(SystemMsg.PETS_AND_SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
			return false;
		}

		switch(_summonType)
		{
			case TRAP:
				if(player.isInZonePeace())
				{
					player.sendPacket(SystemMsg.A_MALICIOUS_SKILL_CANNOT_BE_USED_IN_A_PEACE_ZONE);
					return false;
				}
				break;
			case CLONE:
				if(player.isAlikeDead()) // only TARGET_SELF
					return false;
				break;
			case PET:
			case SIEGE_SUMMON:
				if(!checkSummonCondition(player))
				{
					player.sendPacket(new SystemMessagePacket(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
					return false;
				}
				break;
			case AGATHION:
				if(player.getAgathionId() > 0 && _npcId != 0)
				{
					player.sendPacket(SystemMsg.AN_AGATHION_HAS_ALREADY_BEEN_SUMMONED);
					return false;
				}
				break;
			case SYMBOL:
				if(player.getSymbol() != null)
				{
					player.sendPacket(new SystemMessagePacket(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
					return false;
				}
				break;
		}

		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void useSkill(Creature caster, List<Creature> targets)
	{
		Player activeChar = caster.getPlayer();
		if(activeChar == null)
			return;

		NpcTemplate npcTemplate;
		NpcInstance npc;
		switch(_summonType)
		{
			case AGATHION:
				activeChar.setAgathion(getNpcId());
				break;
			case TRAP:
				Skill trapSkill = getFirstAddedSkill();

				if(activeChar.getTrapsCount() > 0) //GOD updated only one trap
					activeChar.destroyFirstTrap();
				TrapInstance trap = new TrapInstance(IdFactory.getInstance().getNextId(), NpcHolder.getInstance().getTemplate(getNpcId()), activeChar, trapSkill);
				activeChar.addTrap(trap);
				trap.spawnMe();
				break;
			case CLONE:
				FakePlayer fp;
				for(int i = 0; i < _summonsCount; i++)
				{
					fp = new FakePlayer(IdFactory.getInstance().getNextId(), activeChar.getTemplate(), activeChar);
					fp.setReflection(activeChar.getReflection());
					fp.spawnMe(Location.findAroundPosition(activeChar, 50, 70));
					fp.setFollowMode(true);
				}
				break;
			case PET:
			case SIEGE_SUMMON:
			case TREE:
				summon(activeChar, targets, null);
				break;
			case NPC:
				if(activeChar.getSummons().length > 0 || activeChar.isMounted())
					return;

				npcTemplate = NpcHolder.getInstance().getTemplate(getNpcId());
				npc = npcTemplate.getNewInstance();

				npc.setCurrentHp(npc.getMaxHp(), false);
				npc.setCurrentMp(npc.getMaxMp());
				npc.setHeading(activeChar.getHeading());
				npc.setReflection(activeChar.getReflection());
				npc.spawnMe(activeChar.getLoc());

				ThreadPoolManager.getInstance().schedule(new GameObjectTasks.DeleteTask(npc), _lifeTime);
				break;
			case SYMBOL:
				SymbolInstance symbol = activeChar.getSymbol();
				if(symbol != null)
				{
					activeChar.setSymbol(null);
					symbol.deleteMe();
				}

				npcTemplate = NpcHolder.getInstance().getTemplate(getNpcId());
				npc = npcTemplate.getNewInstance();

				npc.setCurrentHp(npc.getMaxHp(), false);
				npc.setCurrentMp(npc.getMaxMp());
				npc.setHeading(activeChar.getHeading());

				if(npc instanceof SymbolInstance)
				{
					symbol = (SymbolInstance) npc;
					activeChar.setSymbol(symbol);
					symbol.setOwner(activeChar);
				}

				Location loc = activeChar.getLoc();
				if(activeChar.getGroundSkillLoc() != null)
				{
					loc = activeChar.getGroundSkillLoc();
					activeChar.setGroundSkillLoc(null);
				}
				npc.spawnMe(loc);
				npc.setReflection(activeChar.getReflection());
				break;
		}

		if(isSSPossible())
			caster.unChargeShots(isMagic());

		for(Creature target : targets)
		{
			if(target == null)
				continue;

			getEffects(activeChar, target, false);
		}

		super.useSkill(activeChar, targets);
	}

	@Override
	public boolean isOffensive()
	{
		return _targetType == SkillTargetType.TARGET_CORPSE;
	}

	private boolean checkSummonCondition(Player player)
	{
		int summonsCount = player.getSummons().length;
		if(summonsCount >= 4)
			return false;

		if(_summonPoints == 0 && summonsCount > 0)
			return false;

		int availPoints = player.getAvailableSummonPoints();
		if(_summonPoints > availPoints)
			return false;

		if(_summonPoints > 0 && availPoints == player.getMaxSummonPoints() && summonsCount > 0)
			return false;

		return true;
	}

	public void summon(Player player, List<Creature> targets, RestoredSummon restored)
	{
		// Удаление трупа, если идет суммон из трупа.
		Location loc = null;
		if(restored == null)
		{
			if(_targetType == SkillTargetType.TARGET_CORPSE)
			{
				for(Creature target : targets)
				{
					if(target != null && target.isDead())
					{
						player.getAI().setAttackTarget(null);
						loc = target.getLoc();
						if(target.isNpc())
							((NpcInstance) target).endDecayTask();
						else if(target.isSummon())
							((SummonInstance) target).endDecayTask();
						else
							return; // кто труп ?
					}
				}
			}

			if(_summonType == SummonType.TREE)
			{
				SummonInstance[] summons = player.getSummons();
				if(summons.length > 0)
				{
					for(SummonInstance summon : summons)
						summon.unSummon(false);
				}
			}
			else if(!checkSummonCondition(player))
				return;
		}
		else
		{
			if(!checkSummonCondition(player))
				return;

			if(player.getSkillLevel(restored.skillId, 0) < restored.skillLvl)
				return;
		}

		NpcTemplate summonTemplate = NpcHolder.getInstance().getTemplate(getNpcId());
		if(summonTemplate == null)
		{
			_log.warn("Summon: Template ID " + getNpcId() + " is NULL FIX IT!");
			return;
		}

		SummonInstance summon = null;
		if(_summonType == SummonType.TREE)
			summon = new TreeInstance(IdFactory.getInstance().getNextId(), summonTemplate, player, _lifeTime, _itemConsumeIdInTime, _itemConsumeCountInTime, _itemConsumeDelay, _summonPoints, this, _isSaveableSummon);
		else
			summon = new SummonInstance(IdFactory.getInstance().getNextId(), summonTemplate, player, _lifeTime, _itemConsumeIdInTime, _itemConsumeCountInTime, _itemConsumeDelay, _summonPoints, this, _isSaveableSummon);

		player.addSummon(summon);

		summon.setTitle(player.getName());
		summon.setExpPenalty(_expPenalty);
		summon.setExp(Experience.LEVEL[Math.min(summon.getLevel(), Experience.LEVEL.length - 1)]);
		summon.setHeading(player.getHeading());
		summon.setReflection(player.getReflection());
		summon.spawnMe(loc == null ? Location.findAroundPosition(player, 50, 70) : loc);
		summon.setRunning();
		summon.setFollowMode(true);

		if(summon.getSkillLevel(4140) > 0)
			summon.altUseSkill(SkillTable.getInstance().getInfo(4140, summon.getSkillLevel(4140)), player);

		if(summon.getName().equalsIgnoreCase("Shadow"))//FIXME [G1ta0] идиотский хардкод
			summon.addStatFunc(new FuncAdd(Stats.ABSORB_DAMAGE_PERCENT, 0x40, this, 15));

		if(restored == null)
			summon.setCurrentHpMp(summon.getMaxHp(), summon.getMaxMp(), false);
		else
		{
			summon.setCurrentHpMp(restored.curHp, restored.curMp, false);
			summon.setConsumeCountdown(restored.time);
		}

		if(_summonType == SummonType.SIEGE_SUMMON)
		{
			SiegeEvent<?, ?> siegeEvent = player.getEvent(SiegeEvent.class);
			siegeEvent.addSiegeSummon(summon);
		}
	}
}