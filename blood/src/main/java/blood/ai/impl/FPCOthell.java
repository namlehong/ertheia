package blood.ai.impl;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;

public class FPCOthell extends WarriorPC
{
	public static final int
	SKILL_Angel_of_Death			= 10531, // Lv.1 	buff 	45 	0 	10000 	0 	- 	Momentarily increases Speed by 30, P. Evasion by 40, skill Evasion by 75, Vital Spot Attack Rate by 40%, and Debuff Resistance by 80%. Casts Crippling Poison on enemies attacking you. Requires a dagger/dual dagger to be equipped.
	SKILL_Silent_Step				= 10543, // Lv.1 	aura 	1 	0 	10000 	0 	- 	Allows you to move unnoticed by most monsters. Decreases P. Def. by 20% and increases Speed by 20%. MP is consumed continuously.
	SKILL_Dagger_Explosion			= 10512, // Lv.1 	active 	125 	0 	10000 	600 	- 	Detonates a blade to attack nearby enemies with 11903 Power added to P. Atk. Requires a dagger/dual dagger. Over-hit. Critical.
	SKILL_Critical_Wound			= 531, // Lv.2 	debuff 	46 	0 	10000 	40 	- 	Increases the target's Critical Damage + 32%. Requires a dagger.
	SKILL_Blood_Stab				= 10508, // Lv.1 	active 	59 	0 	10000 	40 	- 	Strike's the enemy's unprotected back with 32401 Power and additionally inflicts bleed. Over-hit. Critical hit. Half-kill. Requires a dagger or dual dagger.
	SKILL_Final_Ultimate_Evasion	= 10520, // Lv.1 	buff 	38 	0 	10000 	0 	- 	For 30 sec., increases P. Evasion by 60, M. Evasion by 60, M. Def. by 20%, P. Skill Evasion Rate by 60% and Debuff Resistance. Has a 40% chance of canceling the enemy's targeting when attacked.
	SKILL_Maximum_Blow				= 10560, // Lv.1 	buff 	38 	0 	10000 	0 	- 	For 20 sec., Vital Spot Attack Rate + 10%. Requires a dagger or dual dagger.
	SKILL_Throw_Dagger				= 10539, // Lv.1 	debuff 	71 	0 	10000 	800 	- 	Throws a dagger at the enemy with 7396 Power added to P. Atk. For 10 sec., Speed - 180. Requires a dagger/dual dagger to be equipped. Over-hit.
	SKILL_Elusive_Mirage			= 10558, // Lv.1 	buff 	72 	0 	10000 	0 	- 	For 30 sec., has a chance of cancelling the target of the enemy that attacked you. Requires a dagger or dual dagger.
	SKILL_Power_Bluff				= 10554, // Lv.1 	debuff 	40 	0 	10000 	40 	- 	Reveals the enemy's back and for 5 seconds, stuns and prevents targeting. Weakens the enemy against Critical Hits and increases his aggression toward you. Requires a dagger or dual dagger.
	SKILL_Poison_Zone				= 10522, // Lv.1 	debuff 	76 	0 	10000 	900 	- 	Sprays poison onto nearby targets for 15 seconds. Decreases P. Atk., Speed, Shield Defense, P. Def., and M. Def. Inflicts Poison that blocks the use of magic.
	SKILL_Throw_Sand				= 10540, // Lv.1 	debuff 	38 	0 	10000 	800 	- 	Hurls poisonous sand at the enemy. For 5 seconds, greatly decreases P. accuracy and lures the enemy to you. Requires a dagger/dual dagger to be equipped.
	SKILL_Shadow_Chase				= 10516, // Lv.1 	debuff 	38 	0 	10000 	600 	- 	Moves behind an enemy, cancels his target, and inflicts Stun.
	SKILL_Heart_Breaker				= 10509, // Lv.1 	active 	67 	0 	10000 	40 	- 	Stabs the target's heart with 21344 Power added to P. Atk. to inflict a powerful vital spot attack. Increases Spd. + 40, Evasion + 5, and Rear Critical Damage + 30%. Over-hit. Critical hit. Half-kill. Requires a dagger or dual dagger.
	SKILL_Shadow_Dash				= 10525, // Lv.1 	buff 	38 	0 	10000 	0 	- 	For 15 seconds, increases Speed by 66, P. Evasion by 4, and HP Recovery Bonus by 10.
	SKILL_Critical_Adventures		= 10562, // Lv.1 	buff 	36 	0 	10000 	0 	- 	For 5 min., Critical Damage + 304 and vital spot attack success rate + 10%. Front Critical Damage - 30%, Side Critical Rate + 20% and Critical Damage + 20%. Rear Critical Rate + 40% and Critical Damage + 40%. Requires a dagger-type weapon.
	SKILL_Shadow_Hide				= 10517, // Lv.1 	buff 	38 	0 	10000 	0 	- 	You go into Hide state, cancel debuffs and Hold effects on you, increase HP Recovery Bonus, and eliminate enemy enmity toward you.
	SKILL_Dark_Paralysis			= 10514, // Lv.1 	debuff 	38 	0 	10000 	40 	- 	Seals an enemy with the power of darkness. Causes the enemy's body to go completely rigid for 10 seconds and causes paralysis for 5 seconds.
	SKILL_Kick						= 10549, // Lv.1 	debuff 	38 	0 	10000 	40 	- 	Powerfully Knock Back the target with a kick.
	SKILL_Reverse					= 10511, // Lv.1 	active 	90 	0 	10000 	40 	- 	Attempts a vital spot attack with 26320 Power added to P. Atk. Over-hit. Critical. Half-kill. Requires a dagger or dual dagger to be equipped.
	SKILL_Chain_Blow				= 10510, // Lv.1 	active 	71 	0 	10000 	40 	- 	Inflicts damage with 22718 Power added to P. Atk. Additional Skill Power 20% to damage if used during bleed. Over-hit. Critical hit. Half-kill. Requires a dagger or dual dagger.
	SKILL_Mischief					= 10526, // Lv.1 	debuff 	38 	0 	10000 	600 	- 	Inflicts confusion on the target to stop him from attacking the target with the highest hostility. Speed - 70.
	SKILL_Throw_Poison_Needle		= 10541, // Lv.1 	debuff 	38 	0 	10000 	800 	- 	Throws a poison needle to cancel the enemy's skill casting. Requires a dagger/dual dagger to be equipped.
	SKILL_OTHELL_AURA				= 1931;
	
	public final int SKILL_ANGEL_OF_DEATH 	= 10531;
	public final int SKILL_FINAL_UE			= 10520;
	public final int SKILL_MAXIMUM_BLOW		= 10560; // human
	public final int SKILL_ELUSIVE_MIRAGE	= 10558; // human
	public final int SKILL_MORTAL_STRIKE	= 410; // ELF, DE
	public final int SKILL_SHADOW_DODGE		= 10606; // ELF
	public final int SKILL_MELEE_REFLECT	= 10653; // DE
	public final int SKILL_CRITICAL_ADVEN	= 10562; // valliance, not done
	public final int SKILL_SHADOW_DASH		= 10525;
	public final int SKILL_SHADOW_HIDE		= 10517;
	public final int SKILL_EVASION_COUNTER	= 10524;
	
	public final int SKILL_DAGGER_EXPLOSION	= 10512;
	public final int SKILL_BLOOD_STAB		= 10508;
	public final int SKILL_HEART_BREAKER	= 10509;
	public final int SKILL_REVERSE			= 10511;
	public final int SKILL_CHAIN_BLOW		= 10510;
	public final int SKILL_RAZOR_RAIN		= 10513; // 
	public final int SKILL_CLONE_ATTACT		= 10532;
	public final int SKILL_MUG 				= 10700;
	public final int SKILL_PLUNDER			= 10702;

	public final int SKILL_CRITICAL_WOUND	= 531;
	public final int SKILL_THROW_DAGGER		= 10539; // damage, -spd
	public final int SKILL_POWER_BLUFF		= 10554;
	public final int SKILL_THROW_SAND		= 10540;
	public final int SKILL_SHADOW_CHASE		= 10516;
	public final int SKILL_DARK_PARALYSIS	= 10514;
	public final int SKILL_KICK				= 10549;
	public final int SKILL_UPPERCUT			= 10546; // lv 89
	
	

	public FPCOthell(Player actor)
	{
		super(actor);
	}
	
	public void prepareSkillsSetup() {
		_allowSelfBuffSkills.add(SKILL_MORTAL_STRIKE);
		_allowSelfBuffSkills.add(SKILL_OTHELL_AURA);
	}
	
	protected boolean defaultSubFightTask(Creature target)
	{
		othellFightTask(target);
		return true;
	}
	
	protected boolean othellFightTask(Creature target)
	{
		Player player = getActor();
		
		double distance = player.getDistance(target);
		
		if(distance > 300 && canUseSkill(SKILL_SHADOW_CHASE, target, distance))
			return tryCastSkill(SKILL_SHADOW_CHASE, target, distance);
		
		// AOE
		if(canUseSkill(SKILL_Poison_Zone, target, distance))
			return tryCastSkill(SKILL_Poison_Zone, target, distance);
		
		if(target.getAroundNpc(200, 200).size() > 3)
		{
			if(canUseSkill(SKILL_Dagger_Explosion, target, distance))
				return tryCastSkill(SKILL_Dagger_Explosion, target, distance);
			
			if(canUseSkill(SKILL_RAZOR_RAIN, target, distance))
				return tryCastSkill(SKILL_RAZOR_RAIN, target, distance);
		}
		
		// single target
		if(canUseSkill(SKILL_HEART_BREAKER, target, distance))
			return tryCastSkill(SKILL_HEART_BREAKER, target, distance);
		
		if(canUseSkill(SKILL_BLOOD_STAB, target, distance))
			return tryCastSkill(SKILL_BLOOD_STAB, target, distance);
		
		if(canUseSkill(SKILL_CHAIN_BLOW, target, distance))
			return tryCastSkill(SKILL_CHAIN_BLOW, target, distance);
		
		if(canUseSkill(SKILL_REVERSE, target, distance))
			return tryCastSkill(SKILL_REVERSE, target, distance);
			
		return chooseTaskAndTargets(null, target, distance);
	}
	
}

