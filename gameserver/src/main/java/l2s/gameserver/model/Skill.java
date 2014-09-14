package l2s.gameserver.model;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import l2s.gameserver.skills.skillclasses.*;
import org.apache.commons.lang3.math.NumberUtils;
import l2s.commons.collections.LazyArrayList;
import l2s.commons.geometry.Polygon;
import l2s.commons.lang.ArrayUtils;
import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.data.StringHolder;
import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.instancemanager.games.HandysBlockCheckerManager;
import l2s.gameserver.instancemanager.games.HandysBlockCheckerManager.ArenaParticipantsHolder;
import l2s.gameserver.model.Zone.ZoneType;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.model.actor.instances.creature.EffectList;
import l2s.gameserver.model.base.BaseStats;
import l2s.gameserver.model.base.Element;
import l2s.gameserver.model.base.MountType;
import l2s.gameserver.model.base.PledgeRank;
import l2s.gameserver.model.base.SkillTrait;
import l2s.gameserver.model.base.TeamType;
import l2s.gameserver.model.entity.events.GlobalEvent;
import l2s.gameserver.model.instances.ChestInstance;
import l2s.gameserver.model.instances.DecoyInstance;
import l2s.gameserver.model.instances.FeedableBeastInstance;
import l2s.gameserver.model.items.Inventory;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.FlyToLocationPacket.FlyType;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.skills.combo.SkillComboType;
import l2s.gameserver.skills.AbnormalEffect;
import l2s.gameserver.skills.AbnormalType;
import l2s.gameserver.skills.EffectType;
import l2s.gameserver.skills.effects.EffectDebuffImmunity;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.Formulas;
import l2s.gameserver.stats.StatTemplate;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.stats.conditions.Condition;
import l2s.gameserver.stats.funcs.Func;
import l2s.gameserver.stats.funcs.FuncTemplate;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.templates.skill.EffectTemplate;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.PositionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Skill extends StatTemplate implements Cloneable
{
	public static class AddedSkill
	{
		public static final AddedSkill[] EMPTY_ARRAY = new AddedSkill[0];

		public int id;
		public int level;
		private Skill _skill;

		public AddedSkill(int id, int level)
		{
			this.id = id;
			this.level = level;
		}

		public Skill getSkill()
		{
			if(_skill == null)
				_skill = SkillTable.getInstance().getInfo(id, level);
			return _skill;
		}
	}

	public static enum EnchantGrade
	{
		SECOND,
		THIRD,
		AWEKE
	}

	public static enum NextAction
	{
		ATTACK,
		CAST,
		DEFAULT,
		MOVE,
		NONE
	}

	public static enum SkillOpType
	{
		OP_ACTIVE,
		OP_PASSIVE,
		OP_TOGGLE
	}

	public static enum Ternary
	{
		TRUE,
		FALSE,
		DEFAULT
	}

	public static enum SkillMagicType
	{
		PHYSIC,
		MAGIC,
		SPECIAL,
		MUSIC,
		ITEM,
		UNK_MAG_TYPE_21,
		UNK_MAG_TYPE_22
	}

	public static enum SkillTargetType
	{
		TARGET_ALLY,
		TARGET_AREA,
		TARGET_AREA_AIM_CORPSE,
		TARGET_AURA,
		TARGET_AURA_EXCLUDE_PLAYER,
		TARGET_SERVITOR_AURA,
		TARGET_CHEST,
		TARGET_FEEDABLE_BEAST,
		TARGET_CLAN,
		TARGET_CLAN_ONE,
		TARGET_CLAN_ONLY,
		TARGET_CORPSE,
		TARGET_CORPSE_PLAYER,
		TARGET_ENEMY_PET,
		TARGET_ENEMY_SUMMON,
		TARGET_ENEMY_SERVITOR,
		TARGET_EVENT,
		TARGET_FLAGPOLE,
		TARGET_COMMCHANNEL,
		TARGET_HOLY,
		TARGET_ITEM,
		TARGET_MENTEE,
		TARGET_MULTIFACE,
		TARGET_MULTIFACE_AURA,
		TARGET_TUNNEL,
		TARGET_NONE,
		TARGET_ONE,
		TARGET_OWNER,
		TARGET_PARTY,
		TARGET_PARTY_ONE,
		TARGET_PARTY_WITHOUT_ME,
		TARGET_SERVITORS,
		TARGET_SUMMONS,
		TARGET_PET,
		TARGET_ONE_SERVITOR,
		TARGET_SUMMON_AND_ME,
		TARGET_ONE_SUMMON,
		TARGET_ONE_SUMMON_NO_TARGET,
		TARGET_SELF,
		TARGET_SIEGE,
		TARGET_UNLOCKABLE,
		TARGET_GROUND,
		TARGET_ELEMENTAL_DES
	}

	public static enum SkillType
	{
		AGGRESSION(Aggression.class),
		AIEFFECTS(AIeffects.class),
		BALANCE(Balance.class),
		BEAST_FEED(BeastFeed.class),
		BLEED(Continuous.class),
		BUFF(Continuous.class),
		BUFF_CHARGER(BuffCharger.class),
		CALL(Call.class),
		CHAIN_HEAL(ChainHeal.class),
		CHANGE_CLASS(ChangeClass.class),
		CHARGE(Charge.class),
		CHARGE_SOUL(ChargeSoul.class),
		CLAN_GATE(ClanGate.class),
		COMBATPOINTHEAL(CombatPointHeal.class),
		CONT(Toggle.class),
		CPDAM(CPDam.class),
		CPHOT(Continuous.class),
		CRAFT(Craft.class),
		DEATH_PENALTY(DeathPenalty.class),
		DEBUFF_RENEWAL(DebuffRenewal.class),
		DECOY(Decoy.class),
		DEBUFF(Continuous.class),
		DELETE_HATE(DeleteHate.class),
		DESTROY_SUMMON(DestroySummon.class),
		DEFUSE_TRAP(DefuseTrap.class),
		DETECT_TRAP(DetectTrap.class),
		DISCORD(Continuous.class),
		DOT(Continuous.class),
		DRAIN(Drain.class),
		DRAIN_SOUL(DrainSoul.class),
		EFFECT(l2s.gameserver.skills.skillclasses.Effect.class),
		EFFECTS_FROM_SKILLS(EffectsFromSkills.class),
		ENERGY_REPLENISH(EnergyReplenish.class),
		ENCHANT_ARMOR,
		ENCHANT_WEAPON,
		EXTRACT_STONE(ExtractStone.class),
		FISHING(FishingSkill.class),
		HARDCODED(l2s.gameserver.skills.skillclasses.Effect.class),
		HARVESTING(Harvesting.class),
		HEAL(Heal.class),
		HEAL_HP_CP(HealHpCp.class),
		HEAL_HP_CP_PERCENT(HealHpCpPercent.class),
		HEAL_PERCENT(HealPercent.class),
		HOT(Continuous.class),
		KAMAEL_WEAPON_EXCHANGE(KamaelWeaponExchange.class),
		LETHAL_SHOT(LethalShot.class),
		LUCK,
		MANADAM(ManaDam.class),
		MANAHEAL(ManaHeal.class),
		MANAHEAL_PERCENT(ManaHealPercent.class),
		MDAM(MDam.class),
		MDOT(Continuous.class),
		MPHOT(Continuous.class),
		MUTE(Disablers.class),
		ADD_PC_BANG(PcBangPointsAdd.class),
		NOTDONE,
		NOTUSED,
		PARALYZE(Disablers.class),
		PASSIVE,
		PDAM(PDam.class),
		PET_FEED(PetFeed.class),
		PET_SUMMON(PetSummon.class),
		POISON(Continuous.class),
		PUMPING(ReelingPumping.class),
		RECALL(Recall.class),
		REELING(ReelingPumping.class),
		REFILL(Refill.class),
		RESURRECT(Resurrect.class),
		REPLACE(Replace.class),
		RIDE(Ride.class),
		ROOT(Disablers.class),
		SHIFT_AGGRESSION(ShiftAggression.class),
		CHAIN_CALL(ChainCall.class),
		SLEEP(Disablers.class),
		SOULSHOT,
		SOWING(Sowing.class),
		EXPHEAL(EXPHeal.class),
		SPHEAL(SPHeal.class),
		SPIRITSHOT,
		SPOIL(Spoil.class),
		PLUNDER(Plunder.class),
        SACRIFICE(Sacrifice.class),
		STEAL_BUFF(StealBuff.class),
		STUN(Disablers.class),
		SUMMON(Summon.class),
		SUMMON_FLAG(SummonSiegeFlag.class),
		RESTORATION(Restoration.class),
		SWEEP(Sweep.class),
		TAKECASTLE(TakeCastle.class),
		TAKEFORTRESS(TakeFortress.class),
		TAMECONTROL(TameControl.class),
		TELEPORT_NPC(TeleportNpc.class),
		TRAP_ACTIVATION(TrapActivation.class),
		UNLOCK(Unlock.class),
		WATCHER_GAZE(Continuous.class),
		VITALITY_HEAL(VitalityHeal.class);

		private final Class<? extends Skill> clazz;

		private SkillType()
		{
			clazz = Default.class;
		}

		private SkillType(Class<? extends Skill> clazz)
		{
			this.clazz = clazz;
		}

		public Skill makeSkill(StatsSet set)
		{
			try
			{
				Constructor<? extends Skill> c = clazz.getConstructor(StatsSet.class);
				return c.newInstance(set);
			}
			catch(Exception e)
			{
				_log.error("", e);
				throw new RuntimeException(e);
			}
		}

		/**
		 * Работают только против npc
		 */
		public final boolean isPvM()
		{
			switch(this)
			{
				case DISCORD:
					return true;
				default:
					return false;
			}
		}

		/**
		 * Такие скиллы не аггрят цель, и не флагают чара, но являются "плохими"
		 */
		public boolean isAI()
		{
			switch(this)
			{
				case AGGRESSION:
				case AIEFFECTS:
				case SOWING:
				case DELETE_HATE:
					return true;
				default:
					return false;
			}
		}

		public final boolean isPvpSkill()
		{
			switch(this)
			{
				case BLEED:
				case AGGRESSION:
				case DEBUFF:
				case DOT:
				case MDOT:
				case MUTE:
				case PARALYZE:
				case POISON:
				case ROOT:
				case SLEEP:
				case MANADAM:
				case DESTROY_SUMMON:
				case STEAL_BUFF:
				case DELETE_HATE:
				case DEBUFF_RENEWAL:
					return true;
				default:
					return false;
			}
		}

		public boolean isOffensive()
		{
			switch(this)
			{
				case AGGRESSION:
				case AIEFFECTS:
				case BLEED:
				case DEBUFF:
				case DOT:
				case DRAIN:
				case DRAIN_SOUL:
				case LETHAL_SHOT:
				case MANADAM:
				case MDAM:
				case MDOT:
				case MUTE:
				case PARALYZE:
				case PDAM:
				case CPDAM:
				case POISON:
				case ROOT:
				case SLEEP:
				case SOULSHOT:
				case SPIRITSHOT:
				case SPOIL:
				case PLUNDER:
				case STUN:
				case SWEEP:
				case HARVESTING:
				case TELEPORT_NPC:
				case SOWING:
				case DELETE_HATE:
				case DESTROY_SUMMON:
				case STEAL_BUFF:
				case DISCORD:
				case DEBUFF_RENEWAL:
					return true;
				default:
					return false;
			}
		}
	}

	protected static final Logger _log = LoggerFactory.getLogger(Skill.class);

	public static final Skill[] EMPTY_ARRAY = new Skill[0];

	private List<EffectTemplate> _effectTemplates = new ArrayList<EffectTemplate>();
	private List<EffectTemplate> _singleEffectTemplates = new ArrayList<EffectTemplate>();
	private List<EffectTemplate> _selfEffectTemplates = new ArrayList<EffectTemplate>();
	private List<EffectTemplate> _selfSingleEffectTemplates = new ArrayList<EffectTemplate>();

	private boolean _isSelfEffectsOffensive = false;

	protected AddedSkill[] _addedSkills = AddedSkill.EMPTY_ARRAY;

	protected final int[] _itemConsume;
	protected final int[] _itemConsumeId;
	protected final int[] _relationSkillsId;
	protected final int _referenceItemId; // для талисманов
	protected final int _referenceItemMpConsume; // количество потребляемой мп талисмана

	//public static final int SKILL_CUBIC_MASTERY = 143;
	public static final int SKILL_CRAFTING = 172;
	public static final int SKILL_COMMON_CRAFTING = 1320;
	public static final int SKILL_POLEARM_MASTERY = 216;
	public static final int SKILL_CRYSTALLIZE = 248;
	public static final int SKILL_WEAPON_MAGIC_MASTERY1 = 249;
	public static final int SKILL_WEAPON_MAGIC_MASTERY2 = 250;
	public static final int SKILL_BLINDING_BLOW = 321;
	public static final int SKILL_STRIDER_ASSAULT = 325;
	public static final int SKILL_WYVERN_AEGIS = 327;
	public static final int SKILL_BLUFF = 358;
	public static final int SKILL_HEROIC_MIRACLE = 395;
	public static final int SKILL_HEROIC_BERSERKER = 396;
	public static final int SKILL_SOUL_MASTERY = 467;
	public static final int SKILL_TRANSFORM_DISPEL = 619;
	public static final int SKILL_FINAL_FLYING_FORM = 840;
	public static final int SKILL_AURA_BIRD_FALCON = 841;
	public static final int SKILL_AURA_BIRD_OWL = 842;
	public static final int SKILL_DETECTION = 933;
	public static final int SKILL_DETECTION2 = 10785;
	public static final int SKILL_RECHARGE = 1013;
	public static final int SKILL_TRANSFER_PAIN = 1262;
	public static final int SKILL_FISHING_MASTERY = 1315;
	public static final int SKILL_NOBLESSE_BLESSING = 1323;
	public static final int SKILL_SUMMON_CP_POTION = 1324;
	public static final int SKILL_FORTUNE_OF_NOBLESSE = 1325;
	public static final int SKILL_HARMONY_OF_NOBLESSE = 1326;
	public static final int SKILL_SYMPHONY_OF_NOBLESSE = 1327;
	public static final int SKILL_HEROIC_VALOR = 1374;
	public static final int SKILL_HEROIC_GRANDEUR = 1375;
	public static final int SKILL_HEROIC_DREAD = 1376;
	public static final int SKILL_MYSTIC_IMMUNITY = 1411;
	public static final int SKILL_RAID_BLESSING = 2168;
	public static final int SKILL_HINDER_STRIDER = 4258;
	public static final int SKILL_WYVERN_BREATH = 4289;
	public static final int SKILL_RAID_CURSE = 4515;
	public static final int SKILL_CHARM_OF_COURAGE = 5041;
	public static final int SKILL_EVENT_TIMER = 5239;
	public static final int SKILL_BATTLEFIELD_DEATH_SYNDROME = 5660;
	public static final int SKILL_SERVITOR_SHARE = 1557;
	public static final int SKILL_CONFUSION = 1570;
	public static final int[] CHANGE_CLASS_SKILLS = { 1566, 1567, 1568, 1569 };
	public static final int SKILL_TRUE_FIRE = 11007; // Истинный Огонь
	public static final int SKILL_TRUE_WATER = 11008; // Истинная Вода
	public static final int SKILL_TRUE_WIND = 11009; // Истинный Ветер
	public static final int SKILL_TRUE_EARTH = 11010; // Истинная Земля
	public static final int SKILL_CRAFTING_MASTERY = 11010; // Истинная Земля

	protected boolean _isAltUse;
	protected boolean _isBehind;
	protected boolean _isCancelable;
	protected boolean _isCorpse;
	protected boolean _isItemHandler;
	protected boolean _isOffensive;
	protected boolean _isPvpSkill;
	protected boolean _isNotUsedByAI;
	protected boolean _isFishingSkill;
	protected boolean _isPvm;
	protected boolean _isForceUse;
	protected boolean _isNewbie;
	protected boolean _isPreservedOnDeath;
	protected boolean _isHeroic;
	protected boolean _isSaveable;
	protected boolean _isSkillTimePermanent;
	protected boolean _isReuseDelayPermanent;
	protected boolean _isReflectable;
	protected boolean _isSuicideAttack;
	protected boolean _isShieldignore;
	protected double _shieldIgnoreProcent;
	protected boolean _isUndeadOnly;
	protected Ternary _isUseSS;
	protected boolean _isOverhit;
	protected boolean _isSoulBoost;
	protected boolean _isChargeBoost;
	protected boolean _isUsingWhileCasting;
	protected boolean _isIgnoreResists;
	protected boolean _isIgnoreInvul;
	protected boolean _isTrigger;
	protected boolean _isNotAffectedByMute;
	protected boolean _basedOnTargetDebuff;
	protected boolean _deathlink;
	protected boolean _hideStartMessage;
	protected boolean _hideUseMessage;
	protected boolean _skillInterrupt;
	protected boolean _flyingTransformUsage;
	protected boolean _canUseTeleport;
	protected boolean _isProvoke;
	protected boolean _isCubicSkill = false;
	protected boolean _isSelfDispellable;
	protected boolean _abortable;
	protected boolean _isRelation = false;
	protected double _decreaseOnNoPole;
	protected double _increaseOnPole;
	protected boolean _canUseWhileAbnormal;
	protected boolean _isCertification;
	protected boolean _isDualCertification;
	protected int _lethal2SkillDepencensyAddon = 0;
	protected double _lethal2Addon = 0.;
	protected int _lethal1SkillDepencensyAddon = 0;
	protected double _lethal1Addon = 0.;
	protected boolean _isCancel = false;
	protected int _onCastSkill = 0;
	protected int _onCastSkillLevel = 0;
	public boolean _onlyOnCastEffect = false;

	protected SkillType _skillType;
	protected SkillOpType _operateType;
	protected SkillTargetType _targetType;
	protected SkillMagicType _magicType;
	protected SkillTrait _traitType;
	protected BaseStats _saveVs;
	protected boolean _dispelOnDamage;
	protected NextAction _nextAction;
	protected Element[] _elements;

	private FlyType _flyType;
	private boolean _flyDependsOnHeading;
	private boolean _flyToBack;
	private int _flyRadius;
	private int _flyPositionDegree;
	private int _flySpeed;
	private int _flyDelay;
	private int _flyAnimationSpeed;

	protected Condition[] _preCondition = Condition.EMPTY_ARRAY;

	protected int _id;
	protected int _level;
	protected int _baseLevel;
	protected int _displayId;
	protected int _displayLevel;

	protected int _activateRate;
	protected int _castRange;
	protected int _cancelTarget;
	protected int _condCharges;
	protected int _coolTime;
	protected int _delayedEffect;
	protected int _effectPoint;
	protected int _energyConsume;
	protected int _cprConsume;
	protected int _fameConsume;
	protected int _elementsPower;
	protected int _hitTime;
	protected int _hpConsume;
	protected int _levelModifier;
	protected int _magicLevel;
	protected int _matak;
	protected PledgeRank _minPledgeRank;
	protected boolean _clanLeaderOnly;
	protected int _npcId;
	protected int _numCharges;
	protected int _skillInterruptTime;
	protected int _skillRadius;
	protected int _soulsConsume;
	protected int _castCount;
	protected int _enchantLevelCount;
	protected int _criticalRate;

	protected int _reuseDelay;

	protected double _power;
	protected double _chargeEffectPower;
	protected double _chargeDefectPower;
	protected double _powerPvP;
	protected double _chargeEffectPowerPvP;
	protected double _chargeDefectPowerPvP;
	protected double _powerPvE;
	protected double _chargeEffectPowerPvE;
	protected double _chargeDefectPowerPvE;
	protected double _mpConsume1;
	protected double _mpConsume2;
	protected double _lethal1;
	protected double _lethal2;
	protected double _absorbPart;
	protected final double _ignorePDef;

	protected String _name;
	protected String _baseValues;
	protected String _icon;

	public boolean _isStandart = false;

	private TIntSet _analogSkillIds = null;

	private final int _hashCode;

	private final int _fireSkillId;
	private final int _waterSkillId;
	private final int _windSkillId;
	private final int _earthSkillId;
	private final int _holySkillId;
	private final int _unholySkillId;
	private final int _multiElementalSkillId;

	private final int _reuseSkillId;
	private final int _reuseHash;

	private final boolean _switchable;
	private final boolean _isNotDispelOnSelfBuff;

	private final int _abnormalTime;
	private final int _abnormalLvl;
	private final AbnormalType _abnormalType;
	private final AbnormalEffect[] _abnormalEffects;
	private int _chainIndex;
	private int _chainSkillId;
	private boolean _detectPcHide;
	

	private final boolean _isVitalityLimited;

	private final int _rideState;

	private final boolean _isSelfOffensive;
	private final boolean _applyEffectsOnSummon;
	private final boolean _applyEffectsOnPet;

	/**
	 * Внимание!!! У наследников вручную надо поменять тип на public
	 * @param set парамерты скилла
	 */
	protected Skill(StatsSet set)
	{
		//_set = set;
		_id = set.getInteger("skill_id");
		_level = set.getInteger("level");
		_displayId = set.getInteger("displayId", _id);
		_displayLevel = set.getInteger("displayLevel", _level);
		_baseLevel = set.getInteger("base_level");
		_name = set.getString("name");
		_operateType = set.getEnum("operateType", SkillOpType.class);
		_isNewbie = set.getBool("isNewbie", false);
		_isSelfDispellable = set.getBool("isSelfDispellable", true);
		_isPreservedOnDeath = set.getBool("isPreservedOnDeath", false);
		_isHeroic = set.getBool("isHeroic", false);
		_isAltUse = set.getBool("altUse", false);
		_mpConsume1 = set.getInteger("mpConsume1", 0);
		_mpConsume2 = set.getInteger("mpConsume2", 0);
		_energyConsume = set.getInteger("energyConsume", 0);
		_cprConsume = set.getInteger("clanRepConsume", 0);
		_fameConsume = set.getInteger("fameConsume", 0);
		_hpConsume = set.getInteger("hpConsume", 0);
		_soulsConsume = set.getInteger("soulsConsume", 0);
		_isSoulBoost = set.getBool("soulBoost", false);
		_isChargeBoost = set.getBool("chargeBoost", false);
		_isProvoke = set.getBool("provoke", false);
		_isUsingWhileCasting = set.getBool("isUsingWhileCasting", false);
		_matak = set.getInteger("mAtk", 0);
		_isUseSS = Ternary.valueOf(set.getString("useSS", Ternary.DEFAULT.toString()).toUpperCase());
		_magicLevel = set.getInteger("magicLevel", 0);
		_castCount = set.getInteger("castCount", 0);
		_castRange = set.getInteger("castRange", 40);
		_baseValues = set.getString("baseValues", null);

		_isVitalityLimited = set.getBool("is_vitality_limited", false);

		_abnormalTime = set.getInteger("abnormal_time", 0);
		_abnormalLvl = set.getInteger("abnormal_level", 0);

		_abnormalType = set.getEnum("abnormal_type", AbnormalType.class, AbnormalType.none);

		String[] abnormalEffects = set.getString("abnormal_effect", AbnormalEffect.NONE.toString()).split(";");
		_abnormalEffects = new AbnormalEffect[abnormalEffects.length];
		for(int i = 0; i < abnormalEffects.length; i++)
			_abnormalEffects[i] = AbnormalEffect.valueOf(abnormalEffects[i].toUpperCase());

		String[] ride_state = set.getString("ride_state", MountType.NONE.toString()).split(";");
		int rideState = 0;
		for(int i = 0; i < ride_state.length; i++)
			rideState |= (1 << MountType.valueOf(ride_state[i].toUpperCase()).ordinal());
		_rideState = rideState;

		_fireSkillId = set.getInteger("fire_skill_id", _id);
		_waterSkillId = set.getInteger("water_skill_id", _id);
		_windSkillId = set.getInteger("wind_skill_id", _id);
		_earthSkillId = set.getInteger("earth_skill_id", _id);
		_holySkillId = set.getInteger("holy_skill_id", _id);
		_unholySkillId = set.getInteger("unholy_skill_id", _id);
		_multiElementalSkillId = set.getInteger("multi_elemental_skill_id", _id);

		_switchable = set.getBool("switchable", true);
		_isNotDispelOnSelfBuff = set.getBool("doNotDispelOnSelfBuff", false);

		int[] analogSkills = set.getIntegerArray("analog_skills", new int[0]);

		_analogSkillIds = new TIntHashSet(analogSkills.length);
		_analogSkillIds.addAll(analogSkills);

		String s1 = set.getString("itemConsumeCount", null);
		if(s1 == null)
			_itemConsume = new int[] { 0 };
		else
		{
			String[] s = s1.split(" ");
			_itemConsume = new int[s.length];
			for(int i = 0; i < s.length; i++)
				_itemConsume[i] = Integer.parseInt(s[i]);
		}

		String s2 = set.getString("itemConsumeId", null);
		if(s2 == null)
			_itemConsumeId = new int[] { 0 };
		else
		{
			String[] s = s2.split(" ");
			_itemConsumeId = new int[s.length];
			for(int i = 0; i < s.length; i++)
				_itemConsumeId[i] = Integer.parseInt(s[i]);
		}
		String s3 = set.getString("relationSkillsId", "");
		if(s3.length() == 0)
			_relationSkillsId = new int[] { 0 };
		else
		{
			_isRelation = true;
			String[] s = s3.split(";");
			_relationSkillsId = new int[s.length];
			for(int i = 0; i < s.length; i++)
				_relationSkillsId[i] = Integer.parseInt(s[i]);
		}

		_referenceItemId = set.getInteger("referenceItemId", 0);
		_referenceItemMpConsume = set.getInteger("referenceItemMpConsume", 0);

		_isItemHandler = set.getBool("isHandler", false);
		_isSaveable = set.getBool("isSaveable", true);
		_coolTime = set.getInteger("coolTime", 0);
		_skillInterruptTime = set.getInteger("hitCancelTime", 0);
		_reuseDelay = set.getInteger("reuseDelay", 0);
		_hitTime = set.getInteger("hitTime", 0);
		_skillRadius = set.getInteger("skillRadius", 80);
		_targetType = set.getEnum("target", SkillTargetType.class);
		_magicType = set.getEnum("magicType", SkillMagicType.class, SkillMagicType.PHYSIC);
		_traitType = set.getEnum("trait", SkillTrait.class, null);
		_saveVs = set.getEnum("saveVs", BaseStats.class, null);
		_dispelOnDamage = set.getBool("dispelOnDamage", false);
		_hideStartMessage = set.getBool("isHideStartMessage", false);
		_hideUseMessage = set.getBool("isHideUseMessage", false);
		_isUndeadOnly = set.getBool("undeadOnly", false);
		_isCorpse = set.getBool("corpse", false);
		_power = set.getDouble("power", 0.);
		_chargeEffectPower = set.getDouble("chargeEffectPower", _power);
		_chargeDefectPower = set.getDouble("chargeDefectPower", _power);
		_powerPvP = set.getDouble("powerPvP", 0.);
		_chargeEffectPowerPvP = set.getDouble("chargeEffectPowerPvP", _powerPvP);
		_chargeDefectPowerPvP = set.getDouble("chargeDefectPowerPvP", _powerPvP);
		_powerPvE = set.getDouble("powerPvE", 0.);
		_chargeEffectPowerPvE = set.getDouble("chargeEffectPowerPvE", _powerPvE);
		_chargeDefectPowerPvE = set.getDouble("chargeDefectPowerPvE", _powerPvE);
		_effectPoint = set.getInteger("effectPoint", 0);
		_nextAction = NextAction.valueOf(set.getString("nextAction", "DEFAULT").toUpperCase());
		_skillType = set.getEnum("skillType", SkillType.class);
		_isSuicideAttack = set.getBool("isSuicideAttack", false);
		_isSkillTimePermanent = set.getBool("isSkillTimePermanent", false);
		_isReuseDelayPermanent = set.getBool("isReuseDelayPermanent", false);
		_deathlink = set.getBool("deathlink", false);
		_basedOnTargetDebuff = set.getBool("basedOnTargetDebuff", false);
		_isNotUsedByAI = set.getBool("isNotUsedByAI", false);
		_isIgnoreResists = set.getBool("isIgnoreResists", false);
		_isIgnoreInvul = set.getBool("isIgnoreInvul", false);
		_isTrigger = set.getBool("isTrigger", false);
		_isNotAffectedByMute = set.getBool("isNotAffectedByMute", false);
		_flyingTransformUsage = set.getBool("flyingTransformUsage", false);
		_canUseTeleport = set.getBool("canUseTeleport", true);

		String[] elements = set.getString("elements", "NONE").split(";");
		_elements = new Element[elements.length];

		for(int i = 0; i < _elements.length; i++)
		{
			String element = elements[i];
			if(NumberUtils.isNumber(element))
				_elements[i] = Element.getElementById(Integer.parseInt(element));
			else
				_elements[i] = Element.getElementByName(element.toUpperCase());
		}

		_elementsPower = set.getInteger("elementsPower", 0);

		_activateRate = set.getInteger("activateRate", -1);
		_levelModifier = set.getInteger("levelModifier", 1);
		_isCancelable = set.getBool("cancelable", true);
		_isReflectable = set.getBool("reflectable", true);
		_isShieldignore = set.getBool("shieldignore", false);
		_shieldIgnoreProcent = set.getDouble("shield_ignore_procent", 100.);
		_criticalRate = set.getInteger("criticalRate", 0);
		_isOverhit = set.getBool("overHit", false);
		_minPledgeRank = set.getEnum("min_pledge_rank", PledgeRank.class, PledgeRank.VAGABOND);
		_clanLeaderOnly = set.getBool("clan_leader_only", false);
		_isOffensive = set.getBool("isOffensive", _skillType.isOffensive());
		_isPvpSkill = set.getBool("isPvpSkill", _skillType.isPvpSkill());
		_isFishingSkill = set.getBool("isFishingSkill", false);
		_isPvm = set.getBool("isPvm", _skillType.isPvM());
		_isForceUse = set.getBool("isForceUse", false);
		_isBehind = set.getBool("behind", false);
		_npcId = set.getInteger("npcId", 0);

		_flyType = FlyType.valueOf(set.getString("fly_type", "NONE").toUpperCase());
		_flyDependsOnHeading = set.getBool("fly_depends_on_heading", false);
		_flyToBack = set.getBool("fly_to_back", false);		
		_flySpeed = set.getInteger("fly_speed", 0);
		_flyDelay = set.getInteger("fly_delay", 0);
		_flyAnimationSpeed = set.getInteger("fly_animation_speed", 0);
		_flyRadius = set.getInteger("fly_radius", 200);
		_flyPositionDegree = set.getInteger("fly_position_degree", 0);

		_numCharges = set.getInteger("num_charges", 0);
		_condCharges = set.getInteger("cond_charges", 0);
		_delayedEffect = set.getInteger("delayedEffect", 0);
		_cancelTarget = set.getInteger("cancelTarget", 0);
		_skillInterrupt = set.getBool("skillInterrupt", false);
		_lethal1 = set.getDouble("lethal1", 0.);
		_decreaseOnNoPole = set.getDouble("decreaseOnNoPole", 0.);
		_increaseOnPole = set.getDouble("increaseOnPole", 0.);
		_lethal2 = set.getDouble("lethal2", 0.);
		_lethal2Addon = set.getDouble("lethal2DepensencyAddon", 0.);
		_lethal2SkillDepencensyAddon = set.getInteger("lethal2SkillDepencensyAddon", 0);
		_lethal1Addon = set.getDouble("lethal1DepensencyAddon", 0.);
		_isCancel = set.getBool("isCancel", false);
		_lethal1SkillDepencensyAddon = set.getInteger("lethal1SkillDepencensyAddon", 0);
		_absorbPart = set.getDouble("absorbPart", 0.);
		_icon = set.getString("icon", "");
		_canUseWhileAbnormal = set.getBool("canUseWhileAbnormal", false);
		_isCertification = set.getBool("isCertification", false);
		_isDualCertification = set.getBool("is_dual_certification", false);
		_abortable = set.getBool("is_abortable", true);
		_ignorePDef = set.getDouble("ignorePDef", 0.);
		_onCastSkill = set.getInteger("onCastSkill", 0); 
		_onCastSkillLevel = set.getInteger("onCastSkillLevel", 0);
		_onlyOnCastEffect = set.getBool("onlyOnCastEffect", false);
		
		StringTokenizer st = new StringTokenizer(set.getString("addSkills", ""), ";");
		while(st.hasMoreTokens())
		{
			int id = Integer.parseInt(st.nextToken());
			int level = Integer.parseInt(st.nextToken());
			if(level == -1)
				level = _level;
			_addedSkills = ArrayUtils.add(_addedSkills, new AddedSkill(id, level));
		}

		if(_nextAction == NextAction.DEFAULT)
		{
			switch(_skillType)
			{
				case PDAM:
				case CPDAM:
				case LETHAL_SHOT:
				case SPOIL:
				case PLUNDER:
				case SOWING:
				case STUN:
				case DRAIN_SOUL:
					_nextAction = NextAction.ATTACK;
					break;
				default:
					_nextAction = NextAction.NONE;
			}
		}

		_reuseSkillId = set.getInteger("reuse_skill_id", _id);
		_reuseHash = SkillTable.getSkillHashCode(_reuseSkillId, _level);;
		_chainIndex = set.getInteger("chainIndex",-1);
		_chainSkillId = set.getInteger("chainSkillId",-1);
		_detectPcHide = set.getBool("detectPcHide", false);
		_hashCode = SkillTable.getSkillHashCode(_id, _level);

		_isSelfOffensive = set.getBool("is_self_offensive", _isOffensive);
		_applyEffectsOnSummon = set.getBool("apply_effects_on_summon", true);
		_applyEffectsOnPet = set.getBool("apply_effects_on_pet", false);
	}

	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		return checkCondition(activeChar, target, forceUse, dontMove, first, false);
	}

	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first, boolean trigger)
	{
		Player player = activeChar.getPlayer();

		if(activeChar.isDead())
			return false;

		if(!isHandler() && activeChar.isMuted(this))
			return false;

		if(activeChar.isUnActiveSkill(_id))
			return false;

		if(target != null && activeChar.getReflection() != target.getReflection())
		{
			activeChar.sendPacket(SystemMsg.CANNOT_SEE_TARGET);
			return false;
		}

		if(!trigger && (player != null && player.isInZone(ZoneType.JUMPING) || target != null && target.isInZone(ZoneType.JUMPING)))
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_USE_SKILLS_IN_THE_CORRESPONDING_REGION);
			return false;
		}

		if(player != null)
		{
			if(isVitalityLimited() && player.getVitalityPotionsLeft() <= 0)
			{
				player.sendPacket(new SystemMessagePacket(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
				return false;
			}
		}

		if(first && activeChar.isSkillDisabled(this))
		{
			activeChar.sendReuseMessage(this);
			return false;
		}

		// DS: Clarity не влияет на mpConsume1 
		if(first && activeChar.getCurrentMp() < (isMagic() ? _mpConsume1 + activeChar.calcStat(Stats.MP_MAGIC_SKILL_CONSUME, _mpConsume2, target, this) : _mpConsume1 + activeChar.calcStat(Stats.MP_PHYSICAL_SKILL_CONSUME, _mpConsume2, target, this)))
		{
			activeChar.sendPacket(SystemMsg.NOT_ENOUGH_MP);
			return false;
		}

		if(activeChar.getCurrentHp() < _hpConsume + 1)
		{
			activeChar.sendPacket(SystemMsg.NOT_ENOUGH_HP);
			return false;
		}

		//recheck the sys messages, this are the suitible ones.
		if(getFameConsume() > 0)
		{
			if(activeChar.getPlayer().getFame() < _fameConsume)
			{
				activeChar.sendPacket(SystemMsg.YOU_DONT_HAVE_ENOUGH_REPUTATION_TO_DO_THAT);
				return false;
			}
		}

		//must be in clan - no need to check it again
		if(getClanRepConsume() > 0)
		{
			if(activeChar.getPlayer().getClan().getReputationScore() < _cprConsume)
			{
				activeChar.sendPacket(SystemMsg.THE_CLAN_REPUTATION_SCORE_IS_TOO_LOW);
				return false;
			}
		}

		if(_soulsConsume > activeChar.getConsumedSouls())
		{
			activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SOULS);
			return false;
		}

		// TODO перенести потребление из формул сюда
		if(activeChar.getIncreasedForce() < _numCharges)
		{
			activeChar.sendPacket(SystemMsg.YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY_);
			return false;
		}

		if(_targetType == SkillTargetType.TARGET_GROUND)
		{
			if(!activeChar.isPlayer())
				return false;

			if(player.getGroundSkillLoc() == null)
				return false;
		}

		if(isNotTargetAoE() && isOffensive() && activeChar.isInZonePeace())
		{
			activeChar.sendPacket(SystemMsg.A_MALICIOUS_SKILL_CANNOT_BE_USED_IN_A_PEACE_ZONE);
			return false;
		}

		if(player != null)
		{
			if(player.isInFlyingTransform() && isHandler() && !flyingTransformUsage())
			{
				player.sendPacket(new SystemMessagePacket(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(getItemConsumeId()[0]));
				return false;
			}

			if(!checkRideState(player.getMountType()))
			{
				player.sendPacket(new SystemMessagePacket(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
				return false;
			}

			if(player.isInBoat())
			{
				// На воздушных кораблях можно использовать скилы-хэндлеры
				if(player.getBoat().isAirShip() && !isHandler())
					return false;

				// С морских кораблей можно ловить рыбу
				if(player.getBoat().isVehicle() && !(this instanceof FishingSkill || this instanceof ReelingPumping))
					return false;
			}

			if(player.isInObserverMode())
			{
				activeChar.sendPacket(SystemMsg.OBSERVERS_CANNOT_PARTICIPATE);
				return false;
			}

			if(!isHandler() && first && _itemConsume[0] > 0)
			{
				for(int i = 0; i < _itemConsume.length; i++)
				{
					Inventory inv = ((Playable) activeChar).getInventory();
					if(inv == null)
						inv = player.getInventory();

					ItemInstance requiredItems = inv.getItemByItemId(_itemConsumeId[i]);
					if(requiredItems == null || requiredItems.getCount() < _itemConsume[i])
					{
						if(activeChar == player)
							player.sendPacket(SystemMsg.THERE_ARE_NOT_ENOUGH_NECESSARY_ITEMS_TO_USE_THE_SKILL);
						return false;
					}
				}
			}

			if(player.isFishing() && !isFishingSkill() && !altUse() && !activeChar.isServitor())
			{
				if(activeChar == player)
					player.sendPacket(SystemMsg.ONLY_FISHING_SKILLS_MAY_BE_USED_AT_THIS_TIME);
				return false;
			}
		}

		// Warp (628) && Shadow Step (821) can be used while rooted
		if(getFlyType() != FlyType.NONE && getId() != 628 && getId() != 821 && (activeChar.isImmobilized() || activeChar.isRooted()))
		{
			activeChar.getPlayer().sendPacket(SystemMsg.YOUR_TARGET_IS_OUT_OF_RANGE);
			return false;
		}

		SystemMsg msg = checkTarget(activeChar, target, target, forceUse, first);
		if(msg != null && activeChar.getPlayer() != null)
		{
			activeChar.getPlayer().sendPacket(msg);
			return false;
		}

		if(_preCondition.length == 0)
			return true;

		Env env = new Env();
		env.character = activeChar;
		env.skill = this;
		env.target = target;

		if(first)
			for(Condition с : _preCondition)
				if(!с.test(env))
				{
					SystemMsg cond_msg = с.getSystemMsg();
					if(cond_msg != null)
						if(cond_msg.size() > 0)
							activeChar.sendPacket(new SystemMessagePacket(cond_msg).addSkillName(this));
						else
							activeChar.sendPacket(cond_msg);
					return false;
				}

		return true;
	}

	public SystemMsg checkTarget(Creature activeChar, Creature target, Creature aimingTarget, boolean forceUse, boolean first)
	{
		if(target == activeChar && isNotTargetAoE() || target != null && activeChar.isMyServitor(target.getObjectId()) && _targetType == SkillTargetType.TARGET_SERVITOR_AURA)
			return null;
		if(target == null || isOffensive() && target == activeChar)
			return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;
		if(isPvpSkill() && target.isPeaceNpc()) // TODO: [Bonux] Запретить юзать только дебафф скиллы (оффлайк).
			return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;
		if(activeChar.getReflection() != target.getReflection())
			return SystemMsg.CANNOT_SEE_TARGET;
		// Попадает ли цель в радиус действия в конце каста
		if(!first && target != activeChar && target == aimingTarget && getCastRange() > 0 && getCastRange() != 32767 && !activeChar.isInRange(target.getLoc(), getCastRange() + (getCastRange() < 200 ? 400 : 500)))
			return SystemMsg.YOUR_TARGET_IS_OUT_OF_RANGE;
		// Для этих скиллов дальнейшие проверки не нужны
		if(_skillType == SkillType.TAKECASTLE || _skillType == SkillType.TAKEFORTRESS)
			return null;
		// Конусообразные скиллы
		if(!first && target != activeChar && (_targetType == SkillTargetType.TARGET_MULTIFACE || _targetType == SkillTargetType.TARGET_MULTIFACE_AURA || _targetType == SkillTargetType.TARGET_TUNNEL) && (_isBehind ? PositionUtils.isFacing(activeChar, target, 120) : !PositionUtils.isFacing(activeChar, target, 60)))
			return SystemMsg.YOUR_TARGET_IS_OUT_OF_RANGE;
		// Проверка на каст по трупу
		if(target.isDead() != _isCorpse && _targetType != SkillTargetType.TARGET_AREA_AIM_CORPSE || _isUndeadOnly && !target.isUndead())
			return SystemMsg.INVALID_TARGET;
		// Для различных бутылок, и для скилла кормления, дальнейшие проверки не нужны
		if(altUse() || _targetType == SkillTargetType.TARGET_FEEDABLE_BEAST || _targetType == SkillTargetType.TARGET_UNLOCKABLE || _targetType == SkillTargetType.TARGET_CHEST)
			return null;
		if(isOffensive() &&	target.isFakePlayer() && target.isInZonePeace())
			return SystemMsg.YOU_MAY_NOT_ATTACK_THIS_TARGET_IN_A_PEACEFUL_ZONE;
		Player player = activeChar.getPlayer();
		if(player != null)
		{
			// Запрет на атаку мирных NPC в осадной зоне на TW. Иначе таким способом набивают очки.
			//if(player.getTerritorySiege() > -1 && target.isNpc() && !(target instanceof L2TerritoryFlagInstance) && !(target.getAI() instanceof DefaultAI) && player.isInZone(ZoneType.Siege))
			//	return SystemMsg.INVALID_TARGET;

			Player pcTarget = target.getPlayer();
			if(pcTarget != null)
			{
				if(isPvM())
					return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;

				if(player.isInZone(ZoneType.epic) != pcTarget.isInZone(ZoneType.epic))
					return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;

				if(pcTarget.isInOlympiadMode() && (!player.isInOlympiadMode() || player.getOlympiadGame() != pcTarget.getOlympiadGame())) // На всякий случай
					return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;

				//TODO [VISTALL] что за?
				if(player.getTeam() != TeamType.NONE && pcTarget.getTeam() == TeamType.NONE) // Запрет на атаку/баф участником эвента незарегистрированного игрока
					return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;
				if(pcTarget.getTeam() != TeamType.NONE && player.getTeam() == TeamType.NONE) // Запрет на атаку/баф участника эвента незарегистрированным игроком
					return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;
				if(player.getBlockCheckerArena() > -1 && pcTarget.getBlockCheckerArena() > -1 && _targetType == SkillTargetType.TARGET_EVENT)
					return null;

				if(isOffensive())
				{
					if(player.isInOlympiadMode() && !player.isOlympiadCompStart()) // Бой еще не начался
						return SystemMsg.INVALID_TARGET;
					if(player.isInOlympiadMode() && player.isOlympiadCompStart() && player.getOlympiadSide() == pcTarget.getOlympiadSide() && !forceUse) // Свою команду атаковать нельзя
						return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;
					//TODO [VISTALL] что за?
					if(player.getTeam() != TeamType.NONE && pcTarget.getTeam() != TeamType.NONE && player.getTeam() == pcTarget.getTeam() && player != pcTarget && !player.isInLastHero()) // Свою команду атаковать нельзя
						return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;
					if(pcTarget.getNonPvpTime() > System.currentTimeMillis())
						return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;					
					if(isAoE() && getCastRange() < Integer.MAX_VALUE && !GeoEngine.canSeeTarget(activeChar, target, activeChar.isFlying()))
						return SystemMsg.CANNOT_SEE_TARGET;
					if(activeChar.isInZoneBattle() != target.isInZoneBattle() && !player.getPlayerAccess().PeaceAttack)
						return SystemMsg.YOU_MAY_NOT_ATTACK_THIS_TARGET_IN_A_PEACEFUL_ZONE;
					if((activeChar.isInZonePeace() || target.isInZonePeace()) && !player.getPlayerAccess().PeaceAttack)
						return SystemMsg.YOU_MAY_NOT_ATTACK_THIS_TARGET_IN_A_PEACEFUL_ZONE;

					if(activeChar.isInZoneBattle())
					{
						if(!forceUse && !isForceUse() && player.getParty() != null && player.getParty() == pcTarget.getParty())
							return SystemMsg.INVALID_TARGET;
						return null; // Остальные условия на аренах и на олимпиаде проверять не требуется
					}

					// Только враг и только если он еше не проиграл.
					/*Duel duel1 = player.getDuel();
					Duel duel2 = pcTarget.getDuel();
					if(player != pcTarget && duel1 != null && duel1 == duel2)
					{
						if(duel1.getTeamForPlayer(pcTarget) == duel1.getTeamForPlayer(player))
							return SystemMsg.INVALID_TARGET;
						if(duel1.getDuelState(player.getStoredId()) != Duel.DuelState.Fighting)
							return SystemMsg.INVALID_TARGET;
						if(duel1.getDuelState(pcTarget.getStoredId()) != Duel.DuelState.Fighting)
							return SystemMsg.INVALID_TARGET;
						return null;
					}  */

					SystemMsg msg = null;
					for(GlobalEvent e : player.getEvents())
						if((msg = e.checkForAttack(target, activeChar, this, forceUse)) != null)
							return msg;

					for(GlobalEvent e : player.getEvents())
						if(e.canAttack(target, activeChar, this, forceUse))
							return null;

					if(isProvoke())
					{
						if(!forceUse && player.getParty() != null && player.getParty() == pcTarget.getParty())
							return SystemMsg.INVALID_TARGET;
						return null;
					}

					if(isPvpSkill() || !forceUse || isAoE())
					{
						if(player == pcTarget)
							return SystemMsg.INVALID_TARGET;

						if(player.getParty() != null && player.getParty() == pcTarget.getParty())
							return SystemMsg.INVALID_TARGET;
						if(player.isInParty() && player.getParty().getCommandChannel() != null && pcTarget.isInParty() && pcTarget.getParty().getCommandChannel() != null && player.getParty().getCommandChannel() == pcTarget.getParty().getCommandChannel())
							return SystemMsg.INVALID_TARGET;

						if(player.getClanId() != 0 && player.getClanId() == pcTarget.getClanId())
							return SystemMsg.INVALID_TARGET;
						if(player.getClan() != null && player.getClan().getAlliance() != null && pcTarget.getClan() != null && pcTarget.getClan().getAlliance() != null && player.getClan().getAlliance() == pcTarget.getClan().getAlliance())
							return SystemMsg.INVALID_TARGET;

						/*if(player.getDuel() != null && pcTarget.getDuel() != player.getDuel())
							return SystemMsg.INVALID_TARGET;   */
					}

					if(activeChar.isInZone(ZoneType.SIEGE) && target.isInZone(ZoneType.SIEGE))
						return null;

					if(player.atMutualWarWith(pcTarget))
						return null;
					if(isForceUse())
						return null;
					// DS: Убрано. Защита от развода на флаг с копьем
					/*if(!forceUse && player.getPvpFlag() == 0 && pcTarget.getPvpFlag() != 0 && aimingTarget != target)
						return SystemMsg.INVALID_TARGET;*/
					if(pcTarget.getPvpFlag() != 0)
						return null;
					if(pcTarget.isPK())
						return null;
					if(forceUse && !isPvpSkill() && (!isAoE() || aimingTarget == target))
						return null;

					return SystemMsg.INVALID_TARGET;
				}

				if(pcTarget == player)
					return null;

				if(player.isInOlympiadMode() && !forceUse && player.getOlympiadSide() != pcTarget.getOlympiadSide()) // Чужой команде помогать нельзя
					return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;
				//TODO [VISTALL] что за?
				if(player.getTeam() != TeamType.NONE && pcTarget.getTeam() != TeamType.NONE && player.getTeam() != pcTarget.getTeam()) // Чужой команде помогать нельзя
					return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;

				if(!activeChar.isInZoneBattle() && target.isInZoneBattle())
					return SystemMsg.INVALID_TARGET;
				// DS: на оффе можно использовать неатакующие скиллы из мирной зоны в поле.
				/*if(activeChar.isInZonePeace() && !target.isInZonePeace())
					return SystemMsg.INVALID_TARGET;*/

				if(forceUse || isForceUse())
					return null;

				/*if(player.getDuel() != null && pcTarget.getDuel() != player.getDuel())
					return SystemMsg.INVALID_TARGET;
				if(player != pcTarget && player.getDuel() != null && pcTarget.getDuel() != null && pcTarget.getDuel() == pcTarget.getDuel())
					return SystemMsg.INVALID_TARGET;*/

				if(player.getParty() != null && player.getParty() == pcTarget.getParty())
					return null;
				if(player.getClanId() != 0 && player.getClanId() == pcTarget.getClanId())
					return null;

				if(player.atMutualWarWith(pcTarget))
					return SystemMsg.INVALID_TARGET;
				if(pcTarget.getPvpFlag() != 0)
					return SystemMsg.INVALID_TARGET;
				if(pcTarget.isPK())
					return SystemMsg.INVALID_TARGET;

				return null;
			}
		}

		if(isAoE() && isOffensive() && getCastRange() < Integer.MAX_VALUE && !GeoEngine.canSeeTarget(activeChar, target, activeChar.isFlying()))
			return SystemMsg.CANNOT_SEE_TARGET;
		if(!forceUse && !isForceUse() && !isOffensive() && target.isAutoAttackable(activeChar))
			return SystemMsg.INVALID_TARGET;
		if(!forceUse && !isForceUse() && isOffensive() && !target.isAutoAttackable(activeChar))
			return SystemMsg.INVALID_TARGET;
		if(!target.isAttackable(activeChar))
			return SystemMsg.INVALID_TARGET;

		return null;
	}

	public final Creature getAimingTarget(Creature activeChar, GameObject obj)
	{
		Creature target = obj == null || !obj.isCreature() ? null : (Creature) obj;
		switch(_targetType)
		{
			case TARGET_ALLY:
			case TARGET_CLAN:
			case TARGET_PARTY:
			case TARGET_PARTY_WITHOUT_ME:
			case TARGET_CLAN_ONLY:
			case TARGET_SELF:
				return activeChar;
			case TARGET_AURA:
			case TARGET_AURA_EXCLUDE_PLAYER:
			case TARGET_COMMCHANNEL:
			case TARGET_MULTIFACE_AURA:
			case TARGET_GROUND:
				return activeChar;
			case TARGET_HOLY:
				return target != null && activeChar.isPlayer() && target.isArtefact() ? target : null;
			case TARGET_ELEMENTAL_DES:
				target = (target != null && (
							target.getEffectList().containsEffects(11101) ||
							target.getEffectList().containsEffects(11102) ||
							target.getEffectList().containsEffects(11103) ||
							target.getEffectList().containsEffects(11104) ||
							target.getEffectList().containsEffects(11105))
						) ? target : null;
				return target;
			case TARGET_FLAGPOLE:
				return activeChar;
			case TARGET_UNLOCKABLE:
				return target != null && target.isDoor() || target instanceof ChestInstance ? target : null;
			case TARGET_CHEST:
				return target instanceof ChestInstance ? target : null;
			case TARGET_FEEDABLE_BEAST:
				return target instanceof FeedableBeastInstance ? target : null;
			case TARGET_SERVITORS:
			case TARGET_SUMMONS:
			case TARGET_SUMMON_AND_ME:
				return activeChar;
			case TARGET_ONE_SERVITOR:
			case TARGET_SERVITOR_AURA:
				return target != null && target.isServitor() && activeChar.isMyServitor(target.getObjectId()) && target.isDead() == _isCorpse ? target : null;
			case TARGET_ONE_SUMMON:
				return target != null && target.isSummon() && activeChar.isMyServitor(target.getObjectId()) && target.isDead() == _isCorpse ? target : null;
			case TARGET_ONE_SUMMON_NO_TARGET:
				Servitor[] servitors = activeChar.getPlayer().getSummons();
				if(servitors.length == 0)
					return null;
				return activeChar;
			case TARGET_PET:
				return target != null && target.isPet() && activeChar.isMyServitor(target.getObjectId()) && target.isDead() == _isCorpse ? target : null;
			case TARGET_OWNER:
				if(activeChar.isServitor())
					target = activeChar.getPlayer();
				else
					return null;
				return target != null && target.isDead() == _isCorpse ? target : null;
			case TARGET_ENEMY_PET:
				if(target == null || activeChar.isMyServitor(target.getObjectId()) || !target.isPet())
					return null;
				return target;
			case TARGET_ENEMY_SUMMON:
				if(target == null || activeChar.isMyServitor(target.getObjectId()) || !target.isSummon())
					return null;
				return target;
			case TARGET_ENEMY_SERVITOR:
				if(target == null || activeChar.isMyServitor(target.getObjectId()) || !target.isServitor())
					return null;
				return target;
			case TARGET_EVENT:
				return target != null && !target.isDead() && target.isPlayer() && target.getPlayer().getBlockCheckerArena() > -1 ? target : null;
			case TARGET_MENTEE:
				return target != null && target != activeChar && target.isDead() == _isCorpse && target.isPlayer() && target.getPlayer().getMenteeList().getMentor() == activeChar.getObjectId() ? target : null;
			case TARGET_ONE:
				return target != null && target.isDead() == _isCorpse && !(target == activeChar && isOffensive()) && (!_isUndeadOnly || target.isUndead()) ? target : null;
			case TARGET_CLAN_ONE:
//				return target != null && target.isDead() == _isCorpse && !(target == activeChar && isOffensive()) && (!_isUndeadOnly || target.isUndead()) && activeChar.getPlayer().isInSameClan(target.getPlayer()) ? target : null;
				if(target == null)
					return null;
				Player cplayer = activeChar.getPlayer();
				Player cptarget = target.getPlayer();
				// self or self pet.
				if(cptarget != null && cptarget == activeChar)
					return target;
				// olympiad party member or olympiad party member pet.
				if(cplayer != null && cplayer.isInOlympiadMode() && cptarget != null && cplayer.getOlympiadSide() == cptarget.getOlympiadSide() && cplayer.getOlympiadGame() == cptarget.getOlympiadGame() && target.isDead() == _isCorpse && !(target == activeChar && isOffensive()) && (!_isUndeadOnly || target.isUndead()))
					return target;
				// party member or party member pet.
				if(cptarget != null && cplayer != null && cplayer.getClan() != null && cplayer.isInSameClan(cptarget) && target.isDead() == _isCorpse && !(target == activeChar && isOffensive()) && (!_isUndeadOnly || target.isUndead()))
					return target;
				return null;
			case TARGET_PARTY_ONE:
				if(target == null)
					return null;
				Player player = activeChar.getPlayer();
				Player ptarget = target.getPlayer();
				// self or self pet.
				if(ptarget != null && ptarget == activeChar)
					return target;
				// olympiad party member or olympiad party member pet.
				if(player != null && player.isInOlympiadMode() && ptarget != null && player.getOlympiadSide() == ptarget.getOlympiadSide() && player.getOlympiadGame() == ptarget.getOlympiadGame() && target.isDead() == _isCorpse && !(target == activeChar && isOffensive()) && (!_isUndeadOnly || target.isUndead()))
					return target;
				// party member or party member pet.
				if(ptarget != null && player != null && player.getParty() != null && player.getParty().containsMember(ptarget) && target.isDead() == _isCorpse && !(target == activeChar && isOffensive()) && (!_isUndeadOnly || target.isUndead()))
					return target;
				return null;
			case TARGET_AREA:
			case TARGET_MULTIFACE:
			case TARGET_TUNNEL:
				return target != null && target.isDead() == _isCorpse && !(target == activeChar && isOffensive()) && (!_isUndeadOnly || target.isUndead()) ? target : null;
			case TARGET_AREA_AIM_CORPSE:
				return target != null && target.isDead() ? target : null;
			case TARGET_CORPSE:
				if(target == null || !target.isDead())
					return null;
				if(target.isSummon() && !activeChar.isMyServitor(target.getObjectId())) // использовать собственного мертвого самона нельзя
					return target;
				return target.isNpc() ? target : null;
			case TARGET_CORPSE_PLAYER:
				return target != null && target.isPlayable() && target.isDead() ? target : null;
			case TARGET_SIEGE:
				return target != null && !target.isDead() && target.isDoor() ? target : null;
			default:
				activeChar.sendMessage("Target type of skill is not currently handled");
				return null;
		}
	}

	public List<Creature> getTargets(Creature activeChar, Creature aimingTarget, boolean forceUse)
	{
		List<Creature> targets;
		if(oneTarget())
		{
			targets = new LazyArrayList<Creature>(1);
			targets.add(aimingTarget);
			return targets;
		}
		else
			targets = new LazyArrayList<Creature>();

		switch(_targetType)
		{
			case TARGET_SUMMON_AND_ME:
			{
				if(!activeChar.isPlayer())
					break;

				Servitor[] servitors = activeChar.getPlayer().getSummons();
				if(servitors.length == 0)
					break;

				for(Servitor servitor : servitors)
					targets.add(servitor);
				targets.add(activeChar);
				break;
			}
			case TARGET_EVENT:
			{
				if(activeChar.isPlayer())
				{
					Player player = activeChar.getPlayer();
					int playerArena = player.getBlockCheckerArena();

					if(playerArena != -1)
					{
						ArenaParticipantsHolder holder = HandysBlockCheckerManager.getInstance().getHolder(playerArena);
						int team = holder.getPlayerTeam(player);
						// Aura attack
						for(Player actor : World.getAroundPlayers(activeChar, 250, 100))
							if(holder.getAllPlayers().contains(actor) && holder.getPlayerTeam(actor) != team)
								targets.add(actor);
					}
				}
				break;
			}
			case TARGET_AREA_AIM_CORPSE:
			case TARGET_AREA:
			case TARGET_MULTIFACE:
			case TARGET_TUNNEL:
			{
				if(aimingTarget.isDead() == _isCorpse && (!_isUndeadOnly || aimingTarget.isUndead()))
					targets.add(aimingTarget);
				addTargetsToList(targets, aimingTarget, activeChar, forceUse);
				break;
			}
			case TARGET_AURA:
			case TARGET_GROUND:
			case TARGET_MULTIFACE_AURA:
			{
				addTargetsToList(targets, activeChar, activeChar, forceUse);
				break;
			}
			case TARGET_AURA_EXCLUDE_PLAYER:
				/*
				List<Creature> targets_exclude_playable = new LazyArrayList<Creature>();
				
				for(Creature target : targets)
				{
					if(!target.isPlayer())
						targets_exclude_playable.add(target);
				}
				//System.out.println("targets_exclude_playable " + targets_exclude_playable.size());
				addTargetsToList(targets_exclude_playable, activeChar, activeChar, forceUse);
				*/
				//System.out.println("targets " + targets.size());
				addTargetsToList(targets, activeChar, activeChar, false);
				break;
			case TARGET_COMMCHANNEL:
			{
				if(activeChar.getPlayer() != null)
				{
					if(activeChar.getPlayer().isInParty())
					{
						if(activeChar.getPlayer().getParty().isInCommandChannel())
						{
							for(Player p : activeChar.getPlayer().getParty().getCommandChannel())
							{
								if(!p.isDead() && p.isInRange(activeChar, _skillRadius == 0 ? 600 : _skillRadius))
									targets.add(p);
							}
							addTargetAndPetToList(targets, activeChar.getPlayer(), activeChar.getPlayer());
							break;
						}
						for(Player p : activeChar.getPlayer().getParty().getPartyMembers())
							if(!p.isDead() && p.isInRange(activeChar, _skillRadius == 0 ? 600 : _skillRadius))
								targets.add(p);
						addTargetAndPetToList(targets, activeChar.getPlayer(), activeChar.getPlayer());
						break;
					}
					targets.add(activeChar);
					addTargetAndPetToList(targets, activeChar.getPlayer(), activeChar.getPlayer());
				}
				break;
			}
			case TARGET_SERVITORS:
			{
				Servitor[] servitors = activeChar.getServitors();
				if(servitors.length == 0)
					break;

				for(Servitor servitor : servitors)
					targets.add(servitor);

				break;
			}
			case TARGET_SUMMONS:
			{
				if(!activeChar.isPlayer())
					break;

				Servitor[] servitors = activeChar.getPlayer().getSummons();
				if(servitors.length == 0)
					break;

				for(Servitor servitor : servitors)
					targets.add(servitor);

				break;
			}
			case TARGET_SERVITOR_AURA:
			{
				addTargetsToList(targets, aimingTarget, activeChar, forceUse);
				break;
			}
			case TARGET_PARTY:
			case TARGET_PARTY_WITHOUT_ME:
			case TARGET_CLAN:
			case TARGET_CLAN_ONLY:
			case TARGET_ALLY:
			{
				if(activeChar.isMonster() || activeChar.isSiegeGuard())
				{
					if(_targetType != SkillTargetType.TARGET_PARTY_WITHOUT_ME)
						targets.add(activeChar);
					for(Creature c : World.getAroundCharacters(activeChar, _skillRadius, 600))
						if(!c.isDead() && (c.isMonster() || c.isSiegeGuard()) /*&& ((L2MonsterInstance) c).getFactionId().equals(mob.getFactionId())*/)
							targets.add(c);
					break;
				}
				Player player = activeChar.getPlayer();
				if(player == null)
					break;
				for(Player target : World.getAroundPlayers(activeChar, _skillRadius, 600))
				{
					boolean check = false;
					switch(_targetType)
					{
						case TARGET_PARTY_WITHOUT_ME:
						case TARGET_PARTY:
							check = player.getParty() != null && player.getParty() == target.getParty();
							break;
						case TARGET_CLAN:
							check = player.getClanId() != 0 && target.getClanId() == player.getClanId() || player.getParty() != null && target.getParty() == player.getParty();
							break;
						case TARGET_CLAN_ONLY:
							check = player.getClanId() != 0 && target.getClanId() == player.getClanId();
							break;
						case TARGET_ALLY:
							check = player.getClanId() != 0 && target.getClanId() == player.getClanId() || player.getAllyId() != 0 && target.getAllyId() == player.getAllyId();
							break;
					}
					if(!check)
						continue;
					// игнорируем противника на олимпиаде
					if(player.isInOlympiadMode() && target.isInOlympiadMode() && player.getOlympiadSide() != target.getOlympiadSide())
						continue;
					if(checkTarget(player, target, aimingTarget, forceUse, false) != null)
						continue;
					addTargetAndPetToList(targets, activeChar, target);
				}
				addTargetAndPetToList(targets, activeChar, player);
				break;
			}
		}
		return targets;
	}

	private void addTargetAndPetToList(List<Creature> targets, Creature actor, Creature target)
	{
		if((actor == target || actor.isInRange(target, _skillRadius)) && target.isDead() == _isCorpse)
			targets.add(target);

		Servitor[] servitors = target.getServitors();
		if(servitors.length > 0)
		{
			for(Servitor servitor : servitors)
			{
				if(actor.isInRange(servitor, _skillRadius) && servitor.isDead() == _isCorpse)
					targets.add(servitor);
			}
		}
	}

	private void addTargetsToList(List<Creature> targets, Creature aimingTarget, Creature activeChar, boolean forceUse)
	{
		List<Creature> arround = aimingTarget.getAroundCharacters(_skillRadius, 300);

		int count = 0;
		Polygon terr = null;
		if(_targetType == SkillTargetType.TARGET_TUNNEL)
		{
			// Создаем параллелепипед ("косой" по вертикали)

			int radius = 100;
			int zmin1 = activeChar.getZ() - 200;
			int zmax1 = activeChar.getZ() + 200;
			int zmin2 = aimingTarget.getZ() - 200;
			int zmax2 = aimingTarget.getZ() + 200;

			double angle = PositionUtils.convertHeadingToDegree(activeChar.getHeading());
			double radian1 = Math.toRadians(angle - 90);
			double radian2 = Math.toRadians(angle + 90);

			terr = new Polygon().add(activeChar.getX() + (int) (Math.cos(radian1) * radius), activeChar.getY() + (int) (Math.sin(radian1) * radius)).add(activeChar.getX() + (int) (Math.cos(radian2) * radius), activeChar.getY() + (int) (Math.sin(radian2) * radius)).add(aimingTarget.getX() + (int) (Math.cos(radian2) * radius), aimingTarget.getY() + (int) (Math.sin(radian2) * radius)).add(aimingTarget.getX() + (int) (Math.cos(radian1) * radius), aimingTarget.getY() + (int) (Math.sin(radian1) * radius)).setZmin(Math.min(zmin1, zmin2)).setZmax(Math.max(zmax1, zmax2));
		}
		else if(_targetType == SkillTargetType.TARGET_GROUND)
		{
			if(!activeChar.isPlayer())
				return;

			Location loc = activeChar.getPlayer().getGroundSkillLoc();
			if(loc == null)
				return;

			arround = World.getAroundCharacters(loc, aimingTarget.getObjectId(), aimingTarget.getReflectionId(), _skillRadius, 300);
		}

		for(Creature target : arround)
		{
			if(terr != null && !terr.isInside(target.getX(), target.getY(), target.getZ()))
				continue;

			if(target == null || activeChar == target || activeChar.getPlayer() != null && activeChar.getPlayer() == target.getPlayer())
				continue;

			//FIXME [G1ta0] тупой хак
			if(getId() == SKILL_DETECTION || getId() == SKILL_DETECTION2)
				target.checkAndRemoveInvisible();

			if(checkTarget(activeChar, target, aimingTarget, forceUse, false) != null)
				continue;

			//if(!(activeChar instanceof DecoyInstance) && activeChar.isNpc() && target.isNpc())
				//continue;

			targets.add(target);
			count++;

			if(isOffensive() && count >= 20 && !activeChar.isRaid())
				break;
		}
	}

	public final void getEffects(Creature effector, Creature effected, boolean self)
	{
		getEffects(effector, effected, self, false);
	}

	public final void getEffects(Creature effector, Creature effected, boolean self, boolean reflectedSkill)
	{
		double timeMult = 1.0;

		if(isMusic())
			timeMult = Config.SONGDANCETIME_MODIFIER;
		else if(getId() >= 4342 && getId() <= 4360)
			timeMult = Config.CLANHALL_BUFFTIME_MODIFIER;
		else if(Config.BUFFTIME_MODIFIER_SKILLS.length > 0)
		{
			for(int i : Config.BUFFTIME_MODIFIER_SKILLS)
			{
				if(i == getId())
					timeMult = Config.BUFFTIME_MODIFIER;
			}
		}

		getEffects(effector, effected, self, 0, timeMult, reflectedSkill);
	}

	/**
	 * Применить эффекты скилла
	 * 
	 * @param effector персонаж, со стороны которого идет действие скилла, кастующий
	 * @param effected персонаж, на которого действует скилл
	 * @param calcChance если true, то расчитывать шанс наложения эффекта
	 * @param self если true, накладывать только эффекты предназанченные для кастующего  
	 * @param timeConst изменить время действия эффектов до данной константы (в миллисекундах)
	 * @param timeMult изменить время действия эффектов с учетом данного множителя
	 * @param reflected означает что скилл был отражен и эффекты тоже нужно отразить
	 */
	public final void getEffects(final Creature effector, final Creature effected, final boolean self, final long timeConst, final double timeMult, boolean reflectedSkill)
	{
		if(isPassive() || effector == null || effector.isAlikeDead())
			return;

		if(!self)
		{
			if(!hasEffects() || effected == null || effected.isDoor() || effected.isDead() && !isPreservedOnDeath()) //why alike dead??
				return;

			if((effected.isEffectImmune() || isOffensive() && effected.isInvul()) && effector != effected)
				return;

			int chance = getActivateRate();
			if(chance >= 0)
			{
				if(!Formulas.calcSkillSuccess(effector, effected, this, chance))
				{
					effector.sendPacket(new SystemMessagePacket(SystemMsg.S1_HAS_FAILED).addSkillName(_displayId, _displayLevel));
					return;
				}
				effector.sendPacket(new SystemMessage(SystemMessage.S1_HAS_SUCCEEDED).addSkillName(_displayId, _displayLevel));
			}
		}
		else if(!hasSelfEffects())
			return;

		if(!isReflectable())
			reflectedSkill = false;

		boolean reflected = false;
		if(reflectedSkill)
		{
			if(!self && isOffensive() && effected != effector && !effector.isTrap())
			{
				if(!effected.isDebuffImmune() && Rnd.chance(effected.calcStat(isMagic() ? Stats.REFLECT_MAGIC_DEBUFF : Stats.REFLECT_PHYSIC_DEBUFF, 0, effector, Skill.this)))
					reflected = true;
			}
		}

		List<Creature> targets = new LazyArrayList<Creature>(1);
		if(!self)
		{
			targets.add(effected);
			if(reflected)
				targets.add(effector);
		}
		else
			targets.add(effector);

		if(applyEffectsOnSummon() || applyEffectsOnPet())
		{
			Creature owner;
			if(self)
				owner = effector;
			else
			{
				if(reflected)
					owner = effector;
				else
					owner = effected;
			}

			if(owner.isPlayer() && !isOffensive() && !isToggle() && !isCubicSkill())
			{
				if(!owner.isMyServitor(owner.getObjectId())) // Если баффает саммон, то этот бафф не даем самому саммону.
				{
					Servitor[] servitors = owner.getPlayer().getServitors();
					if(servitors.length > 0)
					{
						for(Servitor servitor : servitors)
						{
							if(applyEffectsOnSummon() && servitor.isSummon())
								targets.add(servitor);
							else if(applyEffectsOnPet() && servitor.isPet())
								targets.add(servitor);
						}
					}
				}
			}
		}

		boolean skillMastery = false;

		// Check for skill mastery duration time increase
		if(effector.getSkillMastery(getId()) == 2)
		{
			skillMastery = true;
			effector.removeSkillMastery(getId());
		}

		final EffectTemplate[] effectTemplates;
		if(self)
			effectTemplates = getSelfEffectTemplates();
		else
			effectTemplates = getEffectTemplates();

		for(Creature target : targets)
		{
			if(target.isDead() && !isPreservedOnDeath()) //why alike dead?
				continue;

			int scheduledCount = 0;

			boolean resistedDebuff = false;
			boolean resistedBuff = false;

			int reflectedEffectCount = 0;

			boolean success = false;
			for(EffectTemplate et : effectTemplates)
			{
				if(et == null)
				{
					_log.warn(getClass().getSimpleName() + ": EffectTemplate is null in skill ID[" + getId() + "], [" + getLevel() + "]");
					continue;
				}

				if(et._count == 0)
					continue;

				if(reflected)
				{
					if(target == effector && !et._isReflectable)
						continue;

					if(target == effected && et._isReflectable)
						continue;

					reflectedEffectCount++;
				}

				if(target.isRaid() && et.getEffectType().isRaidImmune())
					continue;

				if(et.getChance() >= 0 && !Rnd.chance(et.getChance()))
					continue;

				if(!et.isSingle() && ((target.isBuffImmune() || resistedBuff) && !isOffensive() || (target.isDebuffImmune() || resistedDebuff) && isOffensive()) && effector != target)
				{
					if(!resistedDebuff)
					{
						for(Effect effect : target.getEffectList().getEffects())
						{
							if(effect.checkDebuffImmunity())
								break;
						}
					}

					if(isOffensive())
						resistedDebuff = true;
					else
						resistedBuff = true;

					continue;
				}

				Env env = new Env(effector, target, Skill.this);

				if(success) // больше это значение не используется, поэтому заюзываем его для ConditionFirstEffectSuccess
					env.value = Integer.MAX_VALUE;

				double abnormalTimeModifier = Math.max(1., timeMult);
				if(!isToggle() && !isCubicSkill()) // TODO: [Bonux] Добавить условия, на какие баффы множитель не влияет.
					abnormalTimeModifier *= target.calcStat(isOffensive() ? Stats.DEBUFF_TIME_MODIFIER : Stats.BUFF_TIME_MODIFIER, null, null);

				final Effect e = et.getEffect(env);
				if(e == null)
					continue;

				if(et.getChance() > 0)
					success = true;

				if(et.isSingle())
					continue;

				int count = et.getCount();
				long period = et.getPeriod();

				// Check for skill mastery duration time increase
				if(skillMastery)
				{
					if(count > 1)
						count *= 2;
					else
						period *= 2;
				}

				if(timeConst > 0L)
				{
					if(count > 1)
						period = timeConst / count;
					else
						period = timeConst;
				}
				else if(abnormalTimeModifier > 1.0)
				{
					if(count > 1)
						count *= abnormalTimeModifier;
					else
						period *= abnormalTimeModifier;
				}

				e.setCount(count);
				e.setPeriod(period);
				if(e.schedule())
					scheduledCount++;
			}

			if(scheduledCount > 0 && !isHideStartMessage())
				target.sendPacket(new SystemMessagePacket(SystemMsg.S1S_EFFECT_CAN_BE_FELT).addSkillName(getDisplayId(), getDisplayLevel()));

			if(target == effected)
			{
				if(resistedDebuff || resistedBuff)
					effector.sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_RESISTED_YOUR_S2).addName(target).addSkillName(_displayId, _displayLevel));

				if(reflectedEffectCount > 0)
				{
					target.sendPacket(new SystemMessage(SystemMessage.YOU_COUNTERED_C1S_ATTACK).addName(effector));
					effector.sendPacket(new SystemMessage(SystemMessage.C1_DODGES_THE_ATTACK).addName(target));
				}
			}
		}
	}

	public final void attach(EffectTemplate effect, boolean self)
	{
		if(self)
		{
			if(effect.isSingle())
				_selfSingleEffectTemplates.add(effect);
			else
				_selfEffectTemplates.add(effect);
		}
		else
		{
			if(effect.isSingle())
				_singleEffectTemplates.add(effect);
			else
				_effectTemplates.add(effect);
		}
	}

	public EffectTemplate[] getEffectTemplates()
	{
		return _effectTemplates.toArray(new EffectTemplate[_effectTemplates.size()]);
	}

	public EffectTemplate[] getSingleEffectTemplates()
	{
		return _singleEffectTemplates.toArray(new EffectTemplate[_singleEffectTemplates.size()]);
	}

	public EffectTemplate[] getSelfEffectTemplates()
	{
		return _selfEffectTemplates.toArray(new EffectTemplate[_selfEffectTemplates.size()]);
	}

	public EffectTemplate[] getSelfSingleEffectTemplates()
	{
		return _selfSingleEffectTemplates.toArray(new EffectTemplate[_selfSingleEffectTemplates.size()]);
	}

	public int getEffectsCount()
	{
		return _effectTemplates.size();
	}

	public int getSingleEffectsCount()
	{
		return _singleEffectTemplates.size();
	}

	public int getSelfEffectsCount()
	{
		return _selfEffectTemplates.size();
	}

	public int getSelfSingleEffectsCount()
	{
		return _selfSingleEffectTemplates.size();
	}

	public boolean hasEffects()
	{
		return !_effectTemplates.isEmpty();
	}

	public boolean hasSingleEffects()
	{
		return !_singleEffectTemplates.isEmpty();
	}

	public boolean hasSelfEffects()
	{
		return !_selfEffectTemplates.isEmpty();
	}

	public boolean hasSelfSingleEffects()
	{
		return !_selfSingleEffectTemplates.isEmpty();
	}

	public boolean hasEffect(EffectType type)
	{
		for(EffectTemplate et : _effectTemplates)
		{
			if(et.getEffectType() == type)
				return true;
		}
		return false;
	}

	public boolean hasSingleEffect(EffectType type)
	{
		for(EffectTemplate et : _singleEffectTemplates)
		{
			if(et.getEffectType() == type)
				return true;
		}
		return false;
	}

	public boolean hasSelfEffect(EffectType type)
	{
		for(EffectTemplate et : _selfEffectTemplates)
		{
			if(et.getEffectType() == type)
				return true;
		}
		return false;
	}

	public boolean hasSelfSingleEffect(EffectType type)
	{
		for(EffectTemplate et : _selfSingleEffectTemplates)
		{
			if(et.getEffectType() == type)
				return true;
		}
		return false;
	}

	public final Func[] getStatFuncs()
	{
		return getStatFuncs(this);
	}

	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;

		return hashCode() == ((Skill) obj).hashCode();
	}

	public int getReuseSkillId()
	{
		return _reuseSkillId;
	}

	public int getReuseHash()
	{
		return _reuseHash;
	}

	@Override
	public int hashCode()
	{
		return _hashCode;
	}

	public final void attach(Condition c)
	{
		_preCondition = ArrayUtils.add(_preCondition, c);
	}

	public final boolean altUse()
	{
		return _isItemHandler && _hitTime <= 0;
	}

	public final int getActivateRate()
	{
		return _activateRate;
	}

	public AddedSkill[] getAddedSkills()
	{
		return _addedSkills;
	}

	/**
	 * @return Returns the castRange.
	 */
	public final int getCastRange()
	{
		return _castRange;
	}

	public final int getAOECastRange()
	{
		return Math.max(_castRange, _skillRadius);
	}

	public int getCondCharges()
	{
		return _condCharges;
	}

	public final int getCoolTime()
	{
		return _coolTime;
	}

	public boolean getCorpse()
	{
		return _isCorpse;
	}

	public int getDelayedEffect()
	{
		return _delayedEffect;
	}

	public final int getDisplayId()
	{
		return _displayId;
	}

	public int getDisplayLevel()
	{
		return _displayLevel;
	}

	public int getEffectPoint()
	{
		return _effectPoint;
	}

	public Effect getSameByAbnormalType(Collection<Effect> list)
	{
		//TODO: [Bonux] Разобраться что за хрень и с чем ее едят.
		Effect ret;
		for(EffectTemplate et : getEffectTemplates())
		{
			if(et != null && (ret = et.getSameByAbnormalType(list)) != null)
				return ret;
		}

		for(EffectTemplate et : getSelfEffectTemplates())
		{
			if(et != null && (ret = et.getSameByAbnormalType(list)) != null)
				return ret;
		}
		return null;
	}

	public Effect getSameByAbnormalType(EffectList list)
	{
		return getSameByAbnormalType(list.getEffects());
	}

	public Effect getSameByAbnormalType(Creature actor)
	{
		return getSameByAbnormalType(actor.getEffectList().getEffects());
	}

	public final Element[] getElements()
	{
		return _elements;
	}

	public final int getElementsPower()
	{
		return _elementsPower;
	}

	public Skill getFirstAddedSkill()
	{
		if(_addedSkills.length == 0)
			return null;
		return _addedSkills[0].getSkill();
	}

	public int getFlyRadius()
	{
		return _flyRadius;
	}

	public int getFlyPositionDegree()
	{
		return _flyPositionDegree;
	}

	public FlyType getFlyType()
	{
		return _flyType;
	}

	public boolean isFlyToBack()
	{
		return _flyToBack;
	}

	public boolean isFlyDependsOnHeading()
	{
		return _flyDependsOnHeading;
	}

	public int getFlySpeed()
	{
		return _flySpeed;
	}

	public int getFlyDelay()
	{
		return _flyDelay;
	}

	public int getFlyAnimationSpeed()
	{
		return _flyAnimationSpeed;
	}

	public final int getHitTime()
	{
		if(_hitTime < Config.MIN_HIT_TIME)
			return Config.MIN_HIT_TIME;
		return _hitTime;
	}

	/**
	 * @return Returns the hpConsume.
	 */
	public final int getHpConsume()
	{
		return _hpConsume;
	}

	/**
	 * @return Returns the id.
	 */
	public int getId()
	{
		return _id;
	}

	public void setId(int id)
	{
		_id = id;
	}

	/**
	 * @return Returns the itemConsume.
	 */
	public final int[] getItemConsume()
	{
		return _itemConsume;
	}

	/**
	 * @return Returns the itemConsumeId.
	 */
	public final int[] getItemConsumeId()
	{
		return _itemConsumeId;
	}

	/**
	 * @return Возвращает ид предмета(талисмана)
	 * ману которого надо использовать
	 */
	public final int getReferenceItemId()
	{
		return _referenceItemId;
	}

	/**
	 * @return Возвращает используемое для каста количество маны
	 * предмета(талисмана) 
	 */
	public final int getReferenceItemMpConsume()
	{
		return _referenceItemMpConsume;
	}

	/**
	 * @return Returns the level.
	 */
	public final int getLevel()
	{
		return _level;
	}

	public final int getBaseLevel()
	{
		return _baseLevel;
	}

	public final void setBaseLevel(int baseLevel)
	{
		_baseLevel = baseLevel;
	}

	public final int getLevelModifier()
	{
		return _levelModifier;
	}

	public final int getMagicLevel()
	{
		return _magicLevel;
	}

	public int getMatak()
	{
		return _matak;
	}

	public PledgeRank getMinPledgeRank()
	{
		return _minPledgeRank;
	}

	public boolean clanLeaderOnly()
	{
		return _clanLeaderOnly;
	}

	/**
	 * @return Returns the mpConsume as _mpConsume1 + _mpConsume2.
	 */
	public final double getMpConsume()
	{
		return _mpConsume1 + _mpConsume2;
	}

	/**
	 * @return Returns the mpConsume1.
	 */
	public final double getMpConsume1()
	{
		return _mpConsume1;
	}

	/**
	 * @return Returns the mpConsume2.
	 */
	public final double getMpConsume2()
	{
		return _mpConsume2;
	}

	/**
	 * @return Returns the name.
	 */
	public final String getName()
	{
		return _name;
	}

	public final String getName(Player player)
	{
		return StringHolder.getInstance().getSkillName(player, this);
	}

	public NextAction getNextAction()
	{
		return _nextAction;
	}

	public int getNpcId()
	{
		return _npcId;
	}

	public int getNumCharges()
	{
		return _numCharges;
	}

	public final double getPower(Creature target)
	{
		if(target != null)
		{
			if(target.isPlayable())
				return getPowerPvP();
			if(target.isMonster())
				return getPowerPvE();
		}
		return getPower();
	}

	public final double getPower()
	{
		return _power;
	}

	public final double getPowerPvP()
	{
		return _powerPvP != 0 ? _powerPvP : _power;
	}

	public final double getPowerPvE()
	{
		return _powerPvE != 0 ? _powerPvE : _power;
	}

	public final int getReuseDelay()
	{
		return _reuseDelay;
	}

	/**
	 * для изменения времени отката из скриптов
	 */
	public final void setReuseDelay(int newReuseDelay)
	{
		_reuseDelay = newReuseDelay;
	}

	public final boolean getShieldIgnore()
	{
		return _isShieldignore;
	}

	public final double getShieldIgnoreProcent()
	{
		return _shieldIgnoreProcent;
	}

	public final boolean isReflectable()
	{
		return _isReflectable;
	}

	public final int getSkillInterruptTime()
	{
		return _skillInterruptTime;
	}

	public final int getSkillRadius()
	{
		return _skillRadius;
	}

	public final SkillType getSkillType()
	{
		return _skillType;
	}

	public int getSoulsConsume()
	{
		return _soulsConsume;
	}

	public final SkillTargetType getTargetType()
	{
		return _targetType;
	}

	public final SkillTrait getTraitType()
	{
		return _traitType;
	}

	public final BaseStats getSaveVs()
	{
		return _saveVs;
	}

	public final boolean isDispelOnDamage()
	{
		return _dispelOnDamage;
	}

	public double getLethal1(Creature self)
	{
		return _lethal1 + getAddedLethal1(self);
	}

	public double getIncreaseOnPole()
	{
		return _increaseOnPole;
	}

	public double getDecreaseOnNoPole()
	{
		return _decreaseOnNoPole;
	}
	
	public boolean isCancelSkill()
	{
		return _isCancel;
	}

	public boolean isDetectPC()
	{
		return _detectPcHide;
	}
	
	public double getLethal2(Creature self)
	{
		return _lethal2 + getAddedLethal2(self);
	}

	public double getAddedLethal2(Creature self)
	{
		Player player = self.getPlayer();
		if(player == null)
			return 0.;

		if(_lethal2Addon == 0. || _lethal2SkillDepencensyAddon == 0)
			return 0.;

		if(player.getEffectList().containsEffects(_lethal2SkillDepencensyAddon))
			return _lethal2Addon;

		return 0.;
	}

	public double getAddedLethal1(Creature self)
	{
		Player player = self.getPlayer();
		if(player == null)
			return 0.;

		if(_lethal1Addon == 0. || _lethal1SkillDepencensyAddon == 0)
			return 0.;

		if(player.getEffectList().containsEffects(_lethal1SkillDepencensyAddon))
			return _lethal1Addon;

		return 0.;
	}

	public String getBaseValues()
	{
		return _baseValues;
	}

	public final boolean isCancelable()
	{
		return _isCancelable && _isSelfDispellable && !hasEffect(EffectType.Transformation) && !isToggle() && !isVitalityLimited();
	}

	public final boolean isSelfDispellable()
	{
		return _isSelfDispellable && !hasEffect(EffectType.Transformation) && !isToggle() && !isOffensive() && !isMusic();
	}

	public final int getCriticalRate()
	{
		return _criticalRate;
	}

	public final boolean isHandler()
	{
		return _isItemHandler;
	}

	public final boolean isMagic()
	{
		return _magicType == SkillMagicType.MAGIC;
	}

	public final boolean isPhysic()
	{
		return _magicType == SkillMagicType.PHYSIC;
	}

	public final SkillMagicType getMagicType()
	{
		return _magicType;
	}

	public final boolean isNewbie()
	{
		return _isNewbie;
	}

	public final boolean isPreservedOnDeath()
	{
		return _isPreservedOnDeath;
	}

	public final boolean isHeroic()
	{
		return _isHeroic;
	}

	public void setOperateType(SkillOpType type)
	{
		_operateType = type;
	}

	public final boolean isOverhit()
	{
		return _isOverhit;
	}

	public final boolean isActive()
	{
		return _operateType == SkillOpType.OP_ACTIVE;
	}

	public final boolean isPassive()
	{
		return _operateType == SkillOpType.OP_PASSIVE;
	}

	public boolean isSaveable()
	{
		if(!Config.ALT_SAVE_UNSAVEABLE && (isMusic() || _name.startsWith("Herb of")))
			return false;
		return _isSaveable;
	}

	/**
	 * На некоторые скиллы и хендлеры предметов скорости каста/атаки не влияет
	 */
	public final boolean isSkillTimePermanent()
	{
		return _isSkillTimePermanent || isHandler() || _name.contains("Talisman");
	}

	public final boolean isReuseDelayPermanent()
	{
		return _isReuseDelayPermanent || isHandler();
	}

	public boolean isDeathlink()
	{
		return _deathlink;
	}

	public boolean isBasedOnTargetDebuff()
	{
		return _basedOnTargetDebuff;
	}

	public boolean isSoulBoost()
	{
		return _isSoulBoost;
	}

	public boolean isChargeBoost()
	{
		return _isChargeBoost;
	}

	public boolean isUsingWhileCasting()
	{
		return _isUsingWhileCasting;
	}

	public boolean isBehind()
	{
		return _isBehind;
	}

	public boolean isHideStartMessage()
	{
		return _hideStartMessage;
	}

	public boolean isHideUseMessage()
	{
		return _hideUseMessage;
	}

	/**
	 * Может ли скилл тратить шоты, для хендлеров всегда false
	 */
	public boolean isSSPossible()
	{
		return _isUseSS == Ternary.TRUE || _isUseSS == Ternary.DEFAULT && !isHandler() && !isMusic() && isActive() && !(getTargetType() == SkillTargetType.TARGET_SELF && !isMagic());
	}

	public final boolean isSuicideAttack()
	{
		return _isSuicideAttack;
	}

	public final boolean isToggle()
	{
		return _operateType == SkillOpType.OP_TOGGLE;
	}

	public void setCastRange(int castRange)
	{
		_castRange = castRange;
	}

	public void setDisplayLevel(int lvl)
	{
		_displayLevel = lvl;
	}

	public void setHitTime(int hitTime)
	{
		_hitTime = hitTime;
	}

	public void setHpConsume(int hpConsume)
	{
		_hpConsume = hpConsume;
	}

	public void setMagicType(SkillMagicType type)
	{
		_magicType = type;
	}

	public final void setMagicLevel(int newlevel)
	{
		_magicLevel = newlevel;
	}

	public void setMpConsume1(double mpConsume1)
	{
		_mpConsume1 = mpConsume1;
	}

	public void setMpConsume2(double mpConsume2)
	{
		_mpConsume2 = mpConsume2;
	}

	public void setName(String name)
	{
		_name = name;
	}

	public void setOverhit(final boolean isOverhit)
	{
		_isOverhit = isOverhit;
	}

	public final void setPower(double power)
	{
		_power = power;
	}

	public void setSkillInterruptTime(int skillInterruptTime)
	{
		_skillInterruptTime = skillInterruptTime;
	}

	public boolean isItemSkill()
	{
		return _name.contains("Item Skill") || _name.contains("Talisman");
	}

	@Override
	public String toString()
	{
		return _name + "[id=" + _id + ",lvl=" + _level + "]";
	}

	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		if(isVitalityLimited() && activeChar.isPlayer())
		{
			Player player = activeChar.getPlayer();
			player.setUsedVitalityPotions(player.getUsedVitalityPotions() + 1, true);
			player.sendPacket(new SystemMessagePacket(SystemMsg.VITALITY_EFFECT_IS_APPLIED_THERES_S1_VITALITY_EFFECT_LEFT_THAT_MAY_BE_APPLIED_UNTIL_THE_NEXT_CYCLE).addInteger(player.getVitalityPotionsLeft()));
		}

		for(EffectTemplate et : getSelfSingleEffectTemplates())
			useSingleEffect(et, activeChar, activeChar);

		for(Creature target : targets)
		{
			for(EffectTemplate et : getSingleEffectTemplates())
				useSingleEffect(et, activeChar, target);
		}
	}

	private boolean useSingleEffect(EffectTemplate et, Creature activeChar, Creature target)
	{
		if(et == null)
		{
			_log.warn(getClass().getSimpleName() + ": EffectTemplate is null in skill ID[" + getId() + "], [" + getLevel() + "]");
			return false;
		}

		if(!et.isSingle())
			return false;

		if(et._count == 0)
			return false;

		if(target.isRaid() && et.getEffectType().isRaidImmune())
			return false;

		if(et.getChance() >= 0 && !Rnd.chance(et.getChance()))
			return false;

		final Effect e = et.getEffect(new Env(activeChar, target, Skill.this));
		if(e == null)
			return false;

		if(!e.checkCondition())
			return false;

		e.singleUse();
		return true;
	}

	public boolean isAoE()
	{
		switch(_targetType)
		{
			case TARGET_AREA:
			case TARGET_AREA_AIM_CORPSE:
			case TARGET_AURA:
			case TARGET_AURA_EXCLUDE_PLAYER:
			case TARGET_SERVITOR_AURA:
			case TARGET_MULTIFACE:
			case TARGET_MULTIFACE_AURA:
			case TARGET_TUNNEL:
			case TARGET_GROUND:
				return true;
			default:
				return false;
		}
	}

	public boolean isNotTargetAoE()
	{
		switch(_targetType)
		{
			case TARGET_AURA:
			case TARGET_AURA_EXCLUDE_PLAYER:
			case TARGET_MULTIFACE_AURA:
			case TARGET_ALLY:
			case TARGET_CLAN:
			case TARGET_CLAN_ONLY:
			case TARGET_PARTY:
			case TARGET_PARTY_WITHOUT_ME:
			case TARGET_GROUND:
				return true;
			default:
				return false;
		}
	}

	public boolean isOffensive()
	{
		return _isOffensive;
	}

	public final boolean isForceUse()
	{
		return _isForceUse;
	}

	public boolean isAI()
	{
		return _skillType.isAI();
	}

	public boolean isPvM()
	{
		return _isPvm;
	}

	public final boolean isPvpSkill()
	{
		return _isPvpSkill;
	}

	public final boolean isFishingSkill()
	{
		return _isFishingSkill;
	}

	public boolean isMusic()
	{
		return _magicType == SkillMagicType.MUSIC;
	}

	public boolean isTrigger()
	{
		return _isTrigger;
	}

	public boolean oneTarget()
	{
		switch(_targetType)
		{
			case TARGET_CORPSE:
			case TARGET_CORPSE_PLAYER:
			case TARGET_HOLY:
			case TARGET_FLAGPOLE:
			case TARGET_ITEM:
			case TARGET_NONE:
			case TARGET_MENTEE:
			case TARGET_ONE:
			case TARGET_CLAN_ONE:
			case TARGET_PARTY_ONE:
			case TARGET_ONE_SERVITOR:
			case TARGET_ONE_SUMMON:
			case TARGET_ONE_SUMMON_NO_TARGET:
			case TARGET_PET:
			case TARGET_OWNER:
			case TARGET_ENEMY_PET:
			case TARGET_ENEMY_SUMMON:
			case TARGET_ENEMY_SERVITOR:
			case TARGET_SELF:
			case TARGET_UNLOCKABLE:
			case TARGET_CHEST:
			case TARGET_FEEDABLE_BEAST:
			case TARGET_SIEGE:
			case TARGET_ELEMENTAL_DES:
				return true;
			default:
				return false;
		}
	}

	public int getCancelTarget()
	{
		return _cancelTarget;
	}

	public boolean isSkillInterrupt()
	{
		return _skillInterrupt;
	}

	public boolean isNotUsedByAI()
	{
		return _isNotUsedByAI;
	}

	/**
	 * Игнорирование резистов
	 */
	public boolean isIgnoreResists()
	{
		return _isIgnoreResists;
	}

	/**
	 * Игнорирование неуязвимости
	 */
	public boolean isIgnoreInvul()
	{
		return _isIgnoreInvul;
	}

	public boolean isNotAffectedByMute()
	{
		return _isNotAffectedByMute;
	}

	public boolean flyingTransformUsage()
	{
		return _flyingTransformUsage;
	}

	public boolean canUseTeleport()
	{
		return _canUseTeleport;
	}

	public int getCastCount()
	{
		return _castCount;
	}

	public int getEnchantLevelCount()
	{
		return _enchantLevelCount;
	}

	public void setEnchantLevelCount(int count)
	{
		_enchantLevelCount = count;
	}

	public double getSimpleDamage(Creature attacker, Creature target)
	{
		if(isMagic())
		{
			// магический урон
			double mAtk = attacker.getMAtk(target, this);
			double mdef = target.getMDef(null, this);
			double power = getPower();
			int sps = attacker.getChargedSpiritShot() > 0 && isSSPossible() ? attacker.getChargedSpiritShot() * 2 : 1;
			return 91 * power * Math.sqrt(sps * mAtk) / mdef;
		}
		// физический урон
		double pAtk = attacker.getPAtk(target);
		double pdef = target.getPDef(attacker);
		double power = getPower();
		int ss = attacker.getChargedSoulShot() && isSSPossible() ? 2 : 1;
		return ss * (pAtk + power) * 70. / pdef;
	}

	public long getReuseForMonsters()
	{
		long min = 1000;
		switch(_skillType)
		{
			case PARALYZE:
			case DEBUFF:
			case STEAL_BUFF:
				min = 10000;
				break;
			case MUTE:
			case ROOT:
			case SLEEP:
			case STUN:
				min = 5000;
				break;
		}
		return Math.max(Math.max(_hitTime + _coolTime, _reuseDelay), min);
	}

	public double getAbsorbPart()
	{
		return _absorbPart;
	}

	public boolean isProvoke()
	{
		return _isProvoke;
	}

	public String getIcon()
	{
		return _icon;
	}

	public int getEnergyConsume()
	{
		return _energyConsume;
	}

	public int getClanRepConsume()
	{
		return _cprConsume;
	}

	public int getFameConsume()
	{
		return _fameConsume;
	}

	public void setCubicSkill(boolean value)
	{
		_isCubicSkill = value;
	}

	public boolean isCubicSkill()
	{
		return _isCubicSkill;
	}

	public int[] getRelationSkills()
	{
		return _relationSkillsId;
	}

	public boolean isRelationSkill()
	{
		return _isRelation;
	}

	public int getEnchantLevel()
	{
		if(getDisplayLevel() <= 100)
			return 0;

		return (getDisplayLevel() % 100);
	}

	public EnchantGrade getEnchantGrade()
	{
		switch(getEnchantLevelCount())
		{
			case 15:
				return EnchantGrade.THIRD;
			case 10:
				return EnchantGrade.AWEKE;
		}
		return EnchantGrade.SECOND;
	}

	public boolean isAbortable()
	{
		return _abortable;
	}
	
	public int getOnCastSkill()
	{
		return _onCastSkill;
	}
	
	public int getOnCastSkillLevel()
	{
		return _onCastSkillLevel;
	}
	
	public boolean isOnlyOnCastSkillEffect()
	{
		return _onlyOnCastEffect;
	}
	
	public boolean isCanUseWhileAbnormal()
	{
		return _canUseWhileAbnormal;
	}

	public boolean haveAnalogSkills()
	{
		return _analogSkillIds.size() > 0;
	}

	public int[] getAnalogSkillIDs()
	{
		return _analogSkillIds.toArray();
	}

	public boolean isAnalogSkill(int skillId)
	{
		return _analogSkillIds.contains(skillId);
	}

	public Skill[] getAnalogSkills(Player player)
	{
		if(!haveAnalogSkills())
			return new Skill[0];

		List<Skill> analogSkills = new ArrayList<Skill>();
		for(int analogId : getAnalogSkillIDs())
		{
			Skill analogSkill = player.getKnownSkill(analogId);
			if(analogSkill == null)
				continue;
			analogSkills.add(analogSkill);
		}
		return analogSkills.toArray(new Skill[analogSkills.size()]);
	}

	public Skill getElementalSkill(Player player)
	{
		int elementId = player.getSkillsElementID();
		if(elementId < 0)
			return this;

		int elementalSkillId;
		switch(elementId)
		{
			case 0: // fire
				elementalSkillId = _fireSkillId;
				break;
			case 1: // water
				elementalSkillId = _waterSkillId;
				break;
			case 2: // wind
				elementalSkillId = _windSkillId;
				break;
			case 3: // earth
				elementalSkillId = _earthSkillId;
				break;
			case 4: // holy
				elementalSkillId = _holySkillId;
				break;
			case 5: // unholy
				elementalSkillId = _unholySkillId;
				break;
			case 100: // multi elemental
				elementalSkillId = _multiElementalSkillId;
				break;
			default:
				return this;
		}

		if(elementalSkillId == getId())
			return this;

		Skill elementalSkill = SkillTable.getInstance().getInfo(elementalSkillId, getLevel());
		if(elementalSkill == null)
			return this;

		return elementalSkill;
	}

	/**
	 * Используется TOGGLE-скиллами. Отключает возможность отключения тугла.
	**/
	public boolean isSwitchable()
	{
		return _switchable;
	}

	public boolean isDoNotDispelOnSelfBuff()
	{
		return _isNotDispelOnSelfBuff;
	}

	public int getAbnormalTime()
	{
		return _abnormalTime;
	}

	public int getAbnormalLvl()
	{
		return _abnormalLvl;
	}

	public AbnormalType getAbnormalType()
	{
		return _abnormalType;
	}

	public AbnormalEffect[] getAbnormalEffects()
	{
		return _abnormalEffects;
	}

	public final boolean isCertification()
	{
		return _isCertification;
	}

	public final boolean isDualCertification()
	{
		return _isDualCertification;
	}

	public int getChainIndex()
	{
		return _chainIndex;
	}

	public int getChainSkillId()
	{
		return _chainSkillId;
	}

	public boolean isVitalityLimited()
	{
		return _isVitalityLimited;
	}

	public boolean checkRideState(MountType mountType)
	{
		int v = 1 << mountType.ordinal();
		return (_rideState & v) == v;
	}
	
	public SkillComboType getComboTypeFromCharStatus(Creature cha, Creature creature)
	{
		if(creature.getNpcId() == 19477 && !creature.isChargeBlocked())
			return SkillComboType.LINDVIOR_COMBO;
		if(cha.getPlayer() == null)  //for now we'll do only player chain skills
			return SkillComboType.NO_COMBO; 			
		switch(cha.getPlayer().getClassId())
		{
			case SIGEL_KNIGHT:
			case TYR_WARRIOR:
			case OTHELL_ROGUE:
			case YR_ARCHER:
			case SIGEL_PHOENIX_KNIGHT:
			case SIGEL_HELL_KNIGHT:
			case SIGEL_EVAS_TEMPLAR:
			case SIGEL_SHILLIEN_TEMPLAR:
			case TYR_DUELIST:
			case TYR_DREADNOUGHT:
			case TYR_TITAN:
			case TYR_GRAND_KHAVATARI:
			case TYR_MAESTRO:
			case TYR_DOOMBRINGER:
			case OTHELL_ADVENTURER:
			case OTHELL_WIND_RIDER:
			case OTHELL_GHOST_HUNTER:
			case OTHELL_FORTUNE_SEEKER:
			case YR_SAGITTARIUS:
			case YR_MOONLIGHT_SENTINEL:
			case YR_GHOST_SENTINEL:
			case YR_TRICKSTER:
				if(creature.isFlyUp())
					return SkillComboType.COMBO_FLY_UP;
				if(creature.isKnockDowned())
				{
					return SkillComboType.COMBO_KNOCK_DOWN;
				}	
				break;	
			case FEOH_WIZARD:
			case ISS_ENCHANTER:
			case WYNN_SUMMONER:
			case EOLH_HEALER:
			case FEOH_ARCHMAGE:
			case FEOH_SOULTAKER:
			case FEOH_MYSTIC_MUSE:
			case FEOH_STORM_SCREAMER:
			case FEOH_SOUL_HOUND:
			case ISS_HIEROPHANT:
			case ISS_SWORD_MUSE:
			case ISS_SPECTRAL_DANCER:
			case ISS_DOMINATOR:
			case ISS_DOOMCRYER:
			case WYNN_ARCANA_LORD:
			case WYNN_ELEMENTAL_MASTER:
			case WYNN_SPECTRAL_MASTER:
			case AEORE_CARDINAL:
			case AEORE_EVAS_SAINT:
			case AEORE_SHILLIEN_SAINT:
				if(creature.isFlyUp())
					return SkillComboType.COMBO_FLY_UP;
				if(creature.isKnockDowned())
				{
					return SkillComboType.COMBO_KNOCK_DOWN;	
				}	
				break;
			default:
				return SkillComboType.NO_COMBO;	
		}
		return SkillComboType.NO_COMBO; 	
	}

	public final boolean applyEffectsOnSummon()
	{
		return _applyEffectsOnSummon;
	}

	public final boolean applyEffectsOnPet()
	{
		return _applyEffectsOnPet;
	}

	public final boolean isSelfOffensive()
	{
		return _isSelfOffensive;
	}
	
	public boolean canBeEvaded()
	{
		switch(getSkillType())
		{
			case CHARGE:
			case PDAM:
				return true;
		}
		return false;
	}
}