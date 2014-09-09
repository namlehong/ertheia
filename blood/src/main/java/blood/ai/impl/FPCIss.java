package blood.ai.impl;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;

public class FPCIss extends WarriorPC
{
	public final int SKILL_HORN_MELODY				= 11517; // Lv.1 	buff 	140 	0 	10000 	0 	- 	For 20 minutes, all party members' P. Atk. + 17%, P. Def. + 15%, M. Atk. + 79%, and M. Def. + 30%.
	public final int SKILL_DRUM_MELODY				= 11518; // Lv.1 	buff 	140 	0 	10000 	0 	- 	For 30 minutes, party members' Max MP + 20%, HP Recovery Bonus + 20%, M. Critical Rate + 20, Critical Damage during a normal attack + 20%, P. Atk. + 10%, P. Def. + 20%, Atk. Spd. + 20%, M. Atk. + 20%, M. Def. + 20%, Casting Spd. + 20%, Debuff Resistance + 10%, damage received from critical attacks - 30% and Speed - 15%. Party members equipped with melee weapons/bows/crossbows have increased Critical Damage after receiving an amount of damage.
	public final int SKILL_PIPE_ORGAN_MELODY		= 11519; // Lv.1 	buff 	140 	0 	10000 	0 	- 	For 30 minutes, party members' Max HP + 37%, Max MP + 37%, Max CP + 37%, and HP Recovery Bonus + 20%.
	public final int SKILL_GUITAR_MELODY			= 11520; // Lv.1 	buff 	140 	0 	10000 	0 	- 	For 30 minutes, party members' P. Critical Rate + 32%, P. Critical Damage + 35%, M. Critical Rate + 20 , and MP Consumption - 20% for physical skills and - 10% for magic skills. Iss Enchanter's Enchant Skill MP Consumption - 20%.
	public final int SKILL_HARP_MELODY				= 11521; // Lv.1 	buff 	140 	0 	10000 	0 	- 	For 30 minutes, party members' P. Accuracy + 5, P. and M. Evasion + 5, Speed + 34 and received Critical Damage - 30%.
	public final int SKILL_LUTE_MELODY				= 11522; // Lv.1 	buff 	140 	0 	10000 	0 	- 	For 30 minutes, party members' Atk. Spd. + 34% and Casting Spd. + 31%. Also has a chance of triggering a 9% Vampiric Rage effect during a normal attack.

	public final int SKILL_KNIGHT_HARMONY			= 11523; // Lv.1 	buff 	140 	0 	10000 	900 	- 	For 30 minutes, the selected party member's P. Atk. + 12%, P. Critical Damage + 35%, P. Critical Rate + 100%, P. Def. + 45%, Shield Defense Rate + 30% and Shield Defense + 50%.
	public final int SKILL_WARRIOR_HARMONY			= 11524; // Lv.1 	buff 	140 	0 	10000 	900 	- 	For 30 minutes, the selected party member's P. Atk. + 35%, P. Critical Damage + 35%, P. Critical Rate + 100%, Atk. Spd. + 8%, M. Atk. + 16%, Casting Spd. + 8% and Speed + 8.
	public final int SKILL_WIZARD_HARMONY			= 11525; // Lv.1 	buff 	140 	0 	10000 	900 	- 	For 30 minutes, the selected party member's M. Atk. + 40%, M. Critical Rate + 100%, Casting Spd. + 8%, P. Atk. + 8%, Atk. Spd. + 8%, Speed + 8, M. Def. + 14%, MP Recovery Bonus + 20%, MP Consumption for magic skills - 15% and physical skills -5%, and Cooldown - 20%.

	public final int SKILL_PREVAILING_SONATA		= 11529; // Lv.1 	buff 	140 	0 	10000 	0 	- 	Increases party members' Atk. Spd. by 15%, Casting Spd. by 30%, Max HP by 30%, MP recovery bonus by 20% and decreases MP Consumption for magic skill by 10% and the magic cancel rate for 5 minutes.
	public final int SKILL_DARING_SONATA			= 11530; // Lv.1 	buff 	140 	0 	10000 	0 	- 	Increases party members' P. Atk. by 8%, M. Atk. by 16%, Atk. Spd. and Casting Spd. by 8% and decreases P. Def. by 3%, M. Def. by 11% and P. Evasion by 4 for 5 minutes. Also bestows an 8% Vampiric Rage effect and a chance of reflecting 10% of the damage received.
	public final int SKILL_REFRESHING_SONATA		= 11532; // Lv.1 	buff 	140 	0 	10000 	0 	- 	Increases party members' HP Recovery Bonus by 20%, P. Accuracy by 4, P. Evasion by 3, Speed by 20, and decreases MP Consumption for skills by 20% and skill Cooldown by 10% for 5 minutes.

	public final int SKILL_CELESTIAL_AEGIS			= 11560; // Lv.1 	buff 	423 	0 	10000 	0 	- 	Makes all party members invincible for 30 seconds.
	public final int SKILL_DEVIL_MOVEMENT			= 11562; // Lv.1 	buff 	60 	0 	10000 	0 	- 	Prevents all party members from being attacked preemptively by monsters for 2 minutes. But if any party member initiates an attack, the effect will be canceled.
	
	public final int SKILL_ELEMENTAL_RESISTANCE		= 11565; // Lv.1 	buff 	140 	0 	10000 	0 	- 	Increases party members' Fire/Water/Wind/Earth Resistance by 30 for 30 minutes.
	public final int SKILL_HOLY_ATTACK_RESISTANCE	= 11566; // Lv.1 	buff 	140 	0 	10000 	0 	- 	Increases party members' Holy/Dark Attack Resistance by 30, and Poison/Bleed Resistance by 50% for 30 minutes.
	public final int SKILL_MENTAL_ATTACK_RESISTANCE	= 11567; // Lv.1 	buff 	140 	0 	10000 	0 	- 	Increases party members' Hold/Sleep/Mental Attack Resistance by 50, Buff-canceling Attack Resistance by 30 and Debuff Resistance by 20 for 30 minutes.
	
	public final int SKILL_DEATH_STRIKE				= 11511; // Lv.1 	active 	73 	0 	10000 	40 	- 	Attacks the target with 14326 Power added to P. Atk. Requires a sword, blunt, dualsword, or dual blunt weapon. Over-hit. Critical. 
	public final int SKILL_MASS_SHADOW_BLADE		= 11514; // Lv.1 	active 	146 	0 	10000 	40 	- 	Attacks the target and nearby enemies with 11722 Power added to P. Atk. For 20 seconds, decreases P. Atk. and M. Atk. by 40%. Requires a sword, dualsword or dual blunt to be equipped. Over-hit. Critical.
	public final int SKILL_MASS_CRIPPLING_ATTACK	= 11513; // Lv.1 	active 	130 	0 	10000 	40 	- 	Attacks the target and nearby enemies with 10263 Power added to P. Atk. For 20 seconds, decreases the target's P. Def. and M. Def. by 40% and P. Evasion by 15. Requires a sword, dualsword or dual blunt to be equipped. Over-hit. Critical.
	public final int SKILL_HEALING_MELODY			= 11570; // Lv.1 	active 	582 	0 	10000 	900 	- 	Heals the target and the 10 most injured allies around the target by up to 35% of their Max HP. The recovery amount decreases starting from the most injured person.
	public final int SKILL_RECOVERY_MELODY			= 11571; // Lv.1 	active 	120 	0 	10000 	900 	- 	Restores the selected target's CP by 1000.
	public final int SKILL_ASSAULT_RUSH				= 11508; // Lv.1 	active 	38 	0 	10000 	600 	- 	Rushes the enemy to inflict Stun for 1 second. Requires a sword, dualsword or dual blunt to be equipped.
	public final int SKILL_QUICK_RETURN				= 11563; // Lv.1 	active 	60 	0 	10000 	0 	- 	Teleports to a town. Cannot be used in a specially designated place such as the GM Consultation Service.
	public final int SKILL_ANGEL_RESURRECTION		= 11564; // Lv.1 	active 	60 	0 	10000 	400 	- 	Resurrects a dead target and restores 40% of the XP lost.
	public final int SKILL_DIVINE_CANCEL			= 11536; // Lv.1 	active 	70 	0 	10000 	900 	- 	Has a chance of canceling up to 2 of the buff effects on the target.
	public final int SKILL_POLYMORPH				= 11541; // Lv.1 	active 	70 	0 	10000 	0 	- 	Transforms yourself into a stone for 15 seconds to eliminate nearby enemies' aggression and keep them from targeting you. Greatly decreases Speed while transformed. Cannot use while Disparition is active.
	public final int SKILL_QUICK_ESCAPE				= 11540; // Lv.1 	active 	70 	0 	10000 	0 	- 	Transforms yourself into a cute pig for 15 seconds. P. Def. and M. Def. + 3000, and Speed + 30%.

	public final int SKILL_CRIPPLING_ATTACK			= 11509; // Lv.1 	debuff 	46 	0 	10000 	40 	- 	Attacks the target with 8268 Power added to P. Atk. For 20 seconds, P. Def. and M. Def. - 40% and P. Evasion - 15. Requires a sword, dualsword or dual blunt to be equipped. Over-hit. Critical.
	public final int SKILL_PETRIFY					= 11539; // Lv.1 	debuff 	70 	0 	10000 	900 	- 	Petrifies the target for 15 seconds.
	public final int SKILL_ULTIMATE_SUSPENSION		= 11559; // Lv.1 	debuff 	423 	0 	10000 	0 	- 	Increases nearby enemies' physical/magic skill Cooldown by 200% for 2 minutes.
	public final int SKILL_ULTIMATE_OBLIVION		= 11558; // Lv.1 	debuff 	423 	0 	10000 	0 	- 	Cancels nearby enemies' targets.
	public final int SKILL_ULTIMATE_SCOURGE			= 11557; // Lv.1 	debuff 	423 	0 	10000 	0 	- 	Disables nearby enemies' natural HP Recovery for 2 minutes.
	public final int SKILL_MASS_TRANSFORM			= 11580; // Lv.1 	debuff 	126 	0 	10000 	900 	- 	For 10 sec., mutates enemies to reduce their Speed by 20% and render them unable to attack. Mutated enemies return to normal when inflicted with damage.
	public final int SKILL_MASS_GIANT_ROOT			= 11538; // Lv.1 	debuff 	252 	0 	10000 	900 	- 	Inflicts Hold on nearby enemies for 15 seconds.
	public final int SKILL_SHADOW_BLADE				= 11510; // Lv.1 	debuff 	50 	0 	10000 	40 	- 	Attacks the target with 9133 Power added to P. Atk. For 20 seconds, P. Atk. and M. Atk. - 40%. Requires a sword, dualsword or dual blunt to be equipped. Over-hit. Critical.
	public final int SKILL_GIANT_ROOT				= 11561; // Lv.1 	debuff 	126 	0 	10000 	900 	- 	Inflicts Hold on the enemy for 15 seconds.
	public final int SKILL_TRANSFORM				= 11537; // Lv.1 	debuff 	126 	0 	10000 	900 	- 	Transforms the target into a harmless creature and decrease its Speed by 20% for 15 seconds. The effect is canceled if the transformed target is attacked.
	
	public FPCIss(Player actor)
	{
		super(actor);
	}
	
	public void prepareSkillsSetup() {
		_allowSelfBuffSkills.add(SKILL_ELEMENTAL_RESISTANCE);
		_allowSelfBuffSkills.add(SKILL_HOLY_ATTACK_RESISTANCE);
		_allowSelfBuffSkills.add(SKILL_MENTAL_ATTACK_RESISTANCE);
		
		_allowPartyBuffSkills.add(SKILL_ELEMENTAL_RESISTANCE);
		_allowPartyBuffSkills.add(SKILL_HOLY_ATTACK_RESISTANCE);
		_allowPartyBuffSkills.add(SKILL_MENTAL_ATTACK_RESISTANCE);
	}
	
	protected boolean defaultSubFightTask(Creature target)
	{
		issFightTask(target);
		return true;
	}
	
	protected void criticalFight(Creature target)
	{
		double distance = getActor().getDistance(target);
		
		if(canUseSkill(SKILL_GIANT_ROOT, target, distance))
			tryCastSkill(SKILL_GIANT_ROOT, target, distance);
	}
	
	protected boolean issFightTask(Creature target)
	{
		Player player = getActor();
		
		double distance = player.getDistance(target);
		
		if(distance > 300 && canUseSkill(SKILL_ASSAULT_RUSH, target, distance))
			tryCastSkill(SKILL_ASSAULT_RUSH, target, distance);
		
		if(target.getAroundNpc(200, 200).size() > 3)
		{
			if(canUseSkill(SKILL_MASS_GIANT_ROOT, target, distance))
				tryCastSkill(SKILL_MASS_GIANT_ROOT, target, distance);
			
			if(canUseSkill(SKILL_ULTIMATE_SUSPENSION, target, distance))
				tryCastSkill(SKILL_ULTIMATE_SUSPENSION, target, distance);
			
			if(canUseSkill(SKILL_MASS_SHADOW_BLADE, target, distance))
				tryCastSkill(SKILL_MASS_SHADOW_BLADE, target, distance);
			
			if(canUseSkill(SKILL_MASS_CRIPPLING_ATTACK, target, distance))
				tryCastSkill(SKILL_MASS_CRIPPLING_ATTACK, target, distance);
		}
		
		if(canUseSkill(SKILL_SHADOW_BLADE, target, distance))
			tryCastSkill(SKILL_SHADOW_BLADE, target, distance);
		
		if(canUseSkill(SKILL_CRIPPLING_ATTACK, target, distance))
			tryCastSkill(SKILL_CRIPPLING_ATTACK, target, distance);
		
		if(canUseSkill(SKILL_DEATH_STRIKE, target, distance))
			tryCastSkill(SKILL_DEATH_STRIKE, target, distance);
		
			
		chooseTaskAndTargets(null, target, distance);
		
		return false;
	}
	
	@Override 
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		super.onEvtAttacked(attacker, damage);
		
		Player player = getActor();
		
		// TODO should move to evt attacked
		if(player.getCurrentHpPercents() < 30)
		{
			if(canUseSkill(SKILL_QUICK_ESCAPE, player, 0))
				tryCastSkill(SKILL_QUICK_ESCAPE, player, 0);
			if(canUseSkill(SKILL_POLYMORPH, player, 0))
				tryCastSkill(SKILL_POLYMORPH, player, 0);
		}
	}
	
	@Override
	protected void onEvtClanAttacked(Creature attacked, Creature attacker, int damage)
	{
		super.onEvtClanAttacked(attacked, attacker, damage);
		if(!attacked.isPlayer())
			return;
		
		Player player = getActor();
		Player member = attacked.getPlayer();
		
		if(member.getCurrentHpPercents() < 20)
		{
			if(canUseSkill(SKILL_CELESTIAL_AEGIS, player) && tryCastSkill(SKILL_CELESTIAL_AEGIS, player))
				return;
		}
		
		if(member.getCurrentHpPercents() < 50)
		{
			criticalFight(attacker);
		}
	}
	
}

