package l2s.gameserver.skills;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.skills.effects.EffectAddSkills;
import l2s.gameserver.skills.effects.EffectAgathionRes;
import l2s.gameserver.skills.effects.EffectAggression;
import l2s.gameserver.skills.effects.EffectAwakenForce;
import l2s.gameserver.skills.effects.EffectBetray;
import l2s.gameserver.skills.effects.EffectBlessNoblesse;
import l2s.gameserver.skills.effects.EffectBluff;
import l2s.gameserver.skills.effects.EffectBuff;
import l2s.gameserver.skills.effects.EffectCPDamPercent;
import l2s.gameserver.skills.effects.EffectCallSkills;
import l2s.gameserver.skills.effects.EffectCharge;
import l2s.gameserver.skills.effects.EffectCharmOfCourage;
import l2s.gameserver.skills.effects.EffectCombatPointHealOverTime;
import l2s.gameserver.skills.effects.EffectConsumeSoulsOverTime;
import l2s.gameserver.skills.effects.EffectCubic;
import l2s.gameserver.skills.effects.EffectCurseOfLifeFlow;
import l2s.gameserver.skills.effects.EffectDamOverTime;
import l2s.gameserver.skills.effects.EffectDamOverTimeLethal;
import l2s.gameserver.skills.effects.EffectDamageHealToEffector;
import l2s.gameserver.skills.effects.EffectDamageHealToEffectorAndPets;
import l2s.gameserver.skills.effects.EffectDeathImmunity;
import l2s.gameserver.skills.effects.EffectDebuffImmunity;
import l2s.gameserver.skills.effects.EffectDestroySummon;
import l2s.gameserver.skills.effects.EffectDisarm;
import l2s.gameserver.skills.effects.EffectDiscord;
import l2s.gameserver.skills.effects.EffectDispelOnHit;
import l2s.gameserver.skills.effects.EffectEnervation;
import l2s.gameserver.skills.effects.EffectFakeDeath;
import l2s.gameserver.skills.effects.EffectFear;
import l2s.gameserver.skills.effects.EffectFlyUp;
import l2s.gameserver.skills.effects.EffectGrow;
import l2s.gameserver.skills.effects.EffectHPDamPercent;
import l2s.gameserver.skills.effects.EffectHpToOne;
import l2s.gameserver.skills.effects.EffectHate;
import l2s.gameserver.skills.effects.EffectHeal;
import l2s.gameserver.skills.effects.EffectHealBlock;
import l2s.gameserver.skills.effects.EffectHealCPPercent;
import l2s.gameserver.skills.effects.EffectHealHPCP;
import l2s.gameserver.skills.effects.EffectHealOverTime;
import l2s.gameserver.skills.effects.EffectHealPercent;
import l2s.gameserver.skills.effects.EffectHourglass;
import l2s.gameserver.skills.effects.EffectImmobilize;
import l2s.gameserver.skills.effects.EffectInterrupt;
import l2s.gameserver.skills.effects.EffectInvisible;
import l2s.gameserver.skills.effects.EffectInvulnerable;
import l2s.gameserver.skills.effects.EffectKnockBack;
import l2s.gameserver.skills.effects.EffectKnockDown;
import l2s.gameserver.skills.effects.EffectLaksis;
import l2s.gameserver.skills.effects.EffectLDManaDamOverTime;
import l2s.gameserver.skills.effects.EffectLockInventory;
import l2s.gameserver.skills.effects.EffectMPDamPercent;
import l2s.gameserver.skills.effects.EffectManaDamOverTime;
import l2s.gameserver.skills.effects.EffectManaHeal;
import l2s.gameserver.skills.effects.EffectManaHealOverTime;
import l2s.gameserver.skills.effects.EffectManaHealPercent;
import l2s.gameserver.skills.effects.EffectMeditation;
import l2s.gameserver.skills.effects.EffectMoveToEffector;
import l2s.gameserver.skills.effects.EffectMutation;
import l2s.gameserver.skills.effects.EffectMute;
import l2s.gameserver.skills.effects.EffectMuteAll;
import l2s.gameserver.skills.effects.EffectMuteAttack;
import l2s.gameserver.skills.effects.EffectMutePhisycal;
import l2s.gameserver.skills.effects.EffectNegateMark;
import l2s.gameserver.skills.effects.EffectParalyze;
import l2s.gameserver.skills.effects.EffectPetrification;
import l2s.gameserver.skills.effects.EffectRandomHate;
import l2s.gameserver.skills.effects.EffectRelax;
import l2s.gameserver.skills.effects.EffectRemoveTarget;
import l2s.gameserver.skills.effects.EffectRoot;
import l2s.gameserver.skills.effects.EffectSalvation;
import l2s.gameserver.skills.effects.EffectServitorShare;
import l2s.gameserver.skills.effects.EffectShadowStep;
import l2s.gameserver.skills.effects.EffectSilentMove;
import l2s.gameserver.skills.effects.EffectSleep;
import l2s.gameserver.skills.effects.EffectStun;
import l2s.gameserver.skills.effects.EffectTargetableDisable;
import l2s.gameserver.skills.effects.EffectTargetLock;
import l2s.gameserver.skills.effects.EffectThrowHorizontal;
import l2s.gameserver.skills.effects.EffectThrowUp;
import l2s.gameserver.skills.effects.EffectTransformation;
import l2s.gameserver.skills.effects.EffectUnAggro;
import l2s.gameserver.skills.effects.EffectVisualTransformation;
import l2s.gameserver.skills.effects.i_delete_hate_of_me;
import l2s.gameserver.skills.effects.i_dispel_all;
import l2s.gameserver.skills.effects.i_dispel_by_category;
import l2s.gameserver.skills.effects.i_dispel_by_slot;
import l2s.gameserver.skills.effects.i_dispel_by_slot_probability;
import l2s.gameserver.skills.effects.i_dual_cast;
import l2s.gameserver.skills.effects.i_elemental_type;
import l2s.gameserver.skills.effects.i_my_summon_kill;
import l2s.gameserver.skills.effects.p_block_buff_slot;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public enum EffectType
{
	// Основные эффекты
	AddSkills(EffectAddSkills.class, false),
	AgathionResurrect(EffectAgathionRes.class, true),
	Aggression(EffectAggression.class, true),
	AwakenForce(EffectAwakenForce.class, true),
	Betray(EffectBetray.class, true),
	BlessNoblesse(EffectBlessNoblesse.class, true),
	Buff(EffectBuff.class, false),
	Bluff(EffectBluff.class, true),
	DebuffImmunity(EffectDebuffImmunity.class, true),
	CallSkills(EffectCallSkills.class, false),
	CombatPointHealOverTime(EffectCombatPointHealOverTime.class, true),
	ConsumeSoulsOverTime(EffectConsumeSoulsOverTime.class, true),
	Charge(EffectCharge.class, false),
	CharmOfCourage(EffectCharmOfCourage.class, true),
	CPDamPercent(EffectCPDamPercent.class, true),
	Cubic(EffectCubic.class, true),
	DamageHealToEffector(EffectDamageHealToEffector.class, false),
	DamageHealToEffectorAndPets(EffectDamageHealToEffectorAndPets.class, false),
	DamOverTime(EffectDamOverTime.class, false),
	DamOverTimeLethal(EffectDamOverTimeLethal.class, false),
	DestroySummon(EffectDestroySummon.class, true),
	DeathImmunity(EffectDeathImmunity.class, false),
	Disarm(EffectDisarm.class, true),
	Discord(EffectDiscord.class, true),
	DispelOnHit(EffectDispelOnHit.class, true),
	Enervation(EffectEnervation.class, false),
	FakeDeath(EffectFakeDeath.class, true),
	Fear(EffectFear.class, true),
	MoveToEffector(EffectMoveToEffector.class, true),
	Grow(EffectGrow.class, false),
	Hate(EffectHate.class, false),
	Heal(EffectHeal.class, false),
	HealBlock(EffectHealBlock.class, true),
	HealCPPercent(EffectHealCPPercent.class, true),
	HealHPCP(EffectHealHPCP.class, true),
	HealOverTime(EffectHealOverTime.class, false),
	HealPercent(EffectHealPercent.class, false),
	HPDamPercent(EffectHPDamPercent.class, true),
	HpToOne(EffectHpToOne.class, true),
	IgnoreSkill(EffectBuff.class, false),
	Immobilize(EffectImmobilize.class, true),
	Interrupt(EffectInterrupt.class, true),
	Invulnerable(EffectInvulnerable.class, false),
	Invisible(EffectInvisible.class, false),
	LockInventory(EffectLockInventory.class, false),
	CurseOfLifeFlow(EffectCurseOfLifeFlow.class, true),
	Laksis(EffectLaksis.class, true),
	LDManaDamOverTime(EffectLDManaDamOverTime.class, true),
	ManaDamOverTime(EffectManaDamOverTime.class, true),
	ManaHeal(EffectManaHeal.class, false),
	ManaHealOverTime(EffectManaHealOverTime.class, false),
	ManaHealPercent(EffectManaHealPercent.class, false),
	Meditation(EffectMeditation.class, false),
	MPDamPercent(EffectMPDamPercent.class, true),
	Mute(EffectMute.class, true),
	MuteAll(EffectMuteAll.class, true),
	Mutation(EffectMutation.class, true),
	MuteAttack(EffectMuteAttack.class, true),
	MutePhisycal(EffectMutePhisycal.class, true),
	NegateMark(EffectNegateMark.class, false),
	Paralyze(EffectParalyze.class, true),
	Petrification(EffectPetrification.class, true),
	RandomHate(EffectRandomHate.class, true),
	Relax(EffectRelax.class, true),
	RemoveTarget(EffectRemoveTarget.class, true),
	Root(EffectRoot.class, true),
	Hourglass(EffectHourglass.class, true),
	Salvation(EffectSalvation.class, true),
	ServitorShare(EffectServitorShare.class, true),
	SilentMove(EffectSilentMove.class, true),
	Sleep(EffectSleep.class, true),
	Stun(EffectStun.class, true),
	TeleportBlock(EffectBuff.class, true),
	ResurrectBlock(EffectBuff.class, true),
	KnockDown(EffectKnockDown.class, true),
	KnockBack(EffectKnockBack.class, true),
	FlyUp(EffectFlyUp.class, true),
	ThrowHorizontal(EffectThrowHorizontal.class, true),
	ThrowUp(EffectThrowUp.class, true),
	Transformation(EffectTransformation.class, true),
	VisualTransformation(EffectVisualTransformation.class, true),
	UnAggro(EffectUnAggro.class, true),
	TargetableDisable(EffectTargetableDisable.class, true),
	TargetLock(EffectTargetLock.class, true),
	Vitality(EffectBuff.class, true),
	ShadowStep(EffectShadowStep.class, false),

	// Производные от основных эффектов
	Poison(EffectDamOverTime.class, false),
	PoisonLethal(EffectDamOverTimeLethal.class, false),
	Bleed(EffectDamOverTime.class, false),
	Debuff(EffectBuff.class, false),
	WatcherGaze(EffectBuff.class, false),
	StunWithoutAbnormal(EffectStun.class, true),

	AbsorbDamageToEffector(EffectBuff.class, false), // абсорбирует часть дамага к еффектора еффекта
	AbsorbDamageToMp(EffectBuff.class, false), // абсорбирует часть дамага в мп
	AbsorbDamageToSummon(EffectLDManaDamOverTime.class, true), // абсорбирует часть дамага к сумону
	i_dispel_all(i_dispel_all.class, false),
	i_dispel_by_category(i_dispel_by_category.class, false),
	i_dispel_by_slot(i_dispel_by_slot.class, false),
	i_dispel_by_slot_probability(i_dispel_by_slot_probability.class, false),
	i_delete_hate_of_me(i_delete_hate_of_me.class, false),
	i_dual_cast(i_dual_cast.class, false),
	i_elemental_type(i_elemental_type.class, false),
	i_my_summon_kill(i_my_summon_kill.class, false),
	p_block_buff_slot(p_block_buff_slot.class, false);

	private final Constructor<? extends Effect> _constructor;
	private final boolean _isRaidImmune;

	private EffectType(Class<? extends Effect> clazz, boolean isRaidImmune)
	{
		try
		{
			_constructor = clazz.getConstructor(Env.class, EffectTemplate.class);
		}
		catch(NoSuchMethodException e)
		{
			throw new Error(e);
		}
		_isRaidImmune = isRaidImmune;
	}

	public boolean isRaidImmune()
	{
		return _isRaidImmune;
	}

	public Effect makeEffect(Env env, EffectTemplate template) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException
	{
		return _constructor.newInstance(env, template);
	}
}