package blood.ai.impl;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.skills.skillclasses.Charge;

public class FPCTyrr extends WarriorPC
{
	public static final int
	SKILL_Momentum_Charge 		= 10280, // Lv.1 	aura 	58 	0 	10000 	0 	- 	Continuously consumes HP to produce Momentum.
	
	SKILL_Berserker 			= 10274, // Lv.1 	buff 	38 	0 	10000 	0 	- 	You have reached your physical limit. Increases debuff resistance, Speed, P. Atk., Accuracy, P. Def., Atk. Spd., and Critical Damage. When the target is killed, recovers HP by 20%. When HP falls below 60%, increases P. Atk. by 10% and Critical Rate by 100. When HP falls below 30%, increases P. Atk. by 30% and Critical Rate by an additional 300. Can be used when HP is below 70%.
	SKILL_Feral_Rabbit_Cry 		= 10294, // Lv.1 	buff 	38 	0 	10000 	0 	- 	Shouts loudly to invoke the power of the Rabbit. For 5 minutes, increases your Atk. Spd. by 30%, Speed by 35% and P. Evasion by 15.
	SKILL_Feral_Ogre_Cry	 	= 10292, // Lv.1 	buff 	38 	0 	10000 	0 	- 	Shouts loudly to invoke the power of the Ogre. For 5 minutes, increases your P. Atk., P. Def. And M. Def. by 35% and P. Critical Damage and Max HP by 10%.
	SKILL_Spirit_of_the_Hunter 	= 10296, // Lv.1 	buff 	38 	0 	10000 	0 	- 	For 30 minutes, increases damage against insects/plants/animals + 10%.
	SKILL_Spirit_of_the_Slayer 	= 10297, // Lv.1 	buff 	38 	0 	10000 	0 	- 	For 30 minutes, increases damage against monsters/magic creatures/giants/dragons + 10%.
	SKILL_Hawk_Cry 				= 10295, // Lv.1 	buff 	38 	0 	10000 	0 	- 	Shouts loudly to invoke the power of the Hawk. For 5 minutes, increases your P. Accuracy by 8, Critical Rate by 120 and Critical Damage by 30%.
	SKILL_Brave_Spear 			= 10289, // Lv.1 	buff 	143 	0 	10000 	0 	- 	Increases skills' effect by 30% for 60 seconds and normal/skill attack damage by 5% during PvP.
	SKILL_Reduce_Anger 			= 10278, // Lv.1 	buff 	76 	0 	10000 	0 	- 	Decreases the aggression of nearby enemies toward the caster.
	SKILL_Feral_Bear_Cry 		= 10291, // Lv.1 	buff 	38 	0 	10000 	0 	- 	Shouts loudly to invoke the power of the Bear. For 5 minutes, increases your P. Atk. and P. Critical Damage by 30%.
	SKILL_Feral_Puma_Cry 		= 10293, // Lv.1 	buff 	38 	0 	10000 	0 	- 	Shouts loudly to invoke the power of the Puma. For 5 minutes, increases your P. Accuracy by 10 and Atk. Spd. by 20%, and decreases Critical Damage received by 40%.
	SKILL_Force_of_Nature 		= 10276, // Lv.1 	buff 	38 	0 	10000 	0 	- 	Immediately increases Momentum to its maximum level.
	
	SKILL_Triple_Sonic_Slash 	= 261, 	 // Lv.28 	active 	98 	0 	10000 	40 	- 	Gathers Momentum to attack the target with 22531 power. Damage increases as Momentum is used up. Damage + 30% when the maximum Momentum 3 are consumed. Requires a sword, blunt, spear, fist weapon, dual blunt, or dualsword.
	SKILL_Hurricane_Rush 		= 10267, // Lv.1 	active 	38 	0 	10000 	600 	- 	Shoulder charge the target to Stun it for 3 seconds. Requires a sword, blunt, spear, fist, dual blunt weapon, or dualsword weapon to be equipped.
	SKILL_Eruption 				= 10265, // Lv.1 	active 	122 	0 	10000 	40 	- 	Attacks nearby enemies with 11557 Power added to P. Atk., Stuns them for 2 seconds and remove their target. Power is decreased by 10% when equipped with a sword/blunt/fist weapon and increased by 50% when equipped with a spear. Requires a sword, blunt, spear, fist, dual blunt weapon, or dualsword weapon to be equipped. Over-hit. Critical.
	SKILL_Power_Revival 		= 10298, // Lv.1 	active 	38 	0 	10000 	0 	- 	Recovers all of your HP.
	SKILL_Sonic_Rage 			= 345,   // Lv.5 	active 	58 	0 	10000 	600 	- 	Attacks the target with a deadly energy storm with 551 Power, lowers P. Def., and absorbs Momentum. Requires a sword, blunt, fist weapon, dual blunt, or dualsword.
	SKILL_Power_Bomber 			= 10262, // Lv.1 	active 	84 	0 	10000 	40 	- 	Inflicts a powerful strike on the target with 20483 Power added to P. Atk., knocking them down for 3 sec., and increases damage as Momentum increases. Increases damage by 30% when Momentum is used up to 3. Requires a sword, blunt, spear, fist, dual blunt, or dualsword. Critical. Ignores Shield Defense.
	SKILL_Sonic_Flash 			= 10318, // Lv.1 	active 	98 	0 	0 	600 	- 	Launches a wave of sword energy to inflict 17348 damage added to P. Atk., increasing the damage as sword energy is consumed. Can consume up to 3. Requires a dual sword, sword, or blunt. Over-hit. Critical.
	SKILL_Mega_Strike 			= 10260, // Lv.1 	active 	65 	0 	10000 	40 	- 	Attacks the target with 19893 added to P. Atk. and increases damage as Momentum increases. Increases damage by 30% when Momentum is used up to 3. Requires a sword, blunt, spear, fist, dual blunt, or dualsword Over-hit. Critical. Ignores Shield Defense.
	SKILL_Armor_Destruction 	= 10258, // Lv.1 	active 	50 	0 	10000 	40 	- 	Attacks the target with 11159 Power added to P. Atk. Weakens P. Def. and absorbs Momentum. Requires a sword, blunt, spear, fist, dual blunt weapon, or dualsword weapon to be equipped. Over-hit. Critical.
	SKILL_Sonic_Storm 			= 7,	 //	34 	active 	161 	0 	10000 	500 	- 	Attacks near the enemy with 15024 Power added to P. Atk. Requires a sword, blunt, spear, fist weapon, dual blunt, or dualsword. Over-hit. Critical hit.

	SKILL_Lightning_Disarm 		= 10273, // Lv.1 	debuff 	92 	0 	10000 	0 	- 	Disarms nearby enemies for 5 seconds. Lowers P. Atk. and M. Atk. by 10%.
	SKILL_Giant_Punch 			= 10266, // Lv.1 	debuff 	76 	0 	10000 	0 	- 	Strikes down at the ground with a fist to Knock Down nearby enemies for 5 sec. Requires a sword, blunt, spear, fist, dual blunt weapon, or dualsword weapon to be equipped.
	
	SKILL_HURRICANE_BLASTER		= 10263,
	SKILL_RUSH_IMPACT			= 793,
	SKILL_TYRR_AURA				= 1929;
	
	
	public FPCTyrr(Player actor)
	{
		super(actor);
	}
	
	public void prepareSkillsSetup() {
//		_allowSelfBuffSkills.add(SKILL_Momentum_Charge);
		_allowSelfBuffSkills.add(SKILL_TYRR_AURA);
		_allowSelfBuffSkills.add(SKILL_Feral_Ogre_Cry);
		_allowSelfBuffSkills.add(SKILL_Spirit_of_the_Slayer);
	}
	
	protected boolean defaultSubFightTask(Creature target)
	{
		tyrrFightTask(target);
		return true;
	}
	
	protected boolean tyrrFightTask(Creature target)
	{
		Player player = getActor();
		
		double distance = player.getDistance(target);
		
		/* charge task */
		int maxCharge = player.getSkillLevel(10301) != -1 ? 15 : Charge.MAX_CHARGE;
		
		if(player.getIncreasedForce() < maxCharge && canUseSkill(SKILL_Force_of_Nature, target, distance))
			return tryCastSkill(SKILL_Force_of_Nature, target, distance);
		
		if(player.getIncreasedForce() < maxCharge && !player.getEffectList().containsEffects(SKILL_Momentum_Charge))
			return tryCastSkill(SKILL_Momentum_Charge, player, 0);
		
		if(player.getIncreasedForce() == maxCharge && player.getEffectList().containsEffects(SKILL_Momentum_Charge))
			player.getEffectList().stopEffects(SKILL_Momentum_Charge);
		
		/* rush task */
		
		if(distance > 200 && canUseSkill(SKILL_Hurricane_Rush, target, distance))
			return tryCastSkill(SKILL_Hurricane_Rush, target, distance);
		
		if(distance > 200 && canUseSkill(SKILL_RUSH_IMPACT, target, distance))
			return tryCastSkill(SKILL_RUSH_IMPACT, target, distance);
		
		if(player.getIncreasedForce() < 3 && canUseSkill(SKILL_HURRICANE_BLASTER, target, distance))
			return tryCastSkill(SKILL_HURRICANE_BLASTER, target, distance);
		
		if(canUseSkill(SKILL_Armor_Destruction, target, distance))
			return tryCastSkill(SKILL_Armor_Destruction, target, distance);
		
		if(player.getIncreasedForce() >= 3 && canUseSkill(SKILL_Power_Bomber, target, distance))
			return tryCastSkill(SKILL_Power_Bomber, target, distance);
		
		if(player.getIncreasedForce() >= 3 && canUseSkill(SKILL_Triple_Sonic_Slash, target, distance))
			return tryCastSkill(SKILL_Triple_Sonic_Slash, target, distance);
		
		if(player.getIncreasedForce() >= 3 && canUseSkill(SKILL_Sonic_Flash, target, distance))
			return tryCastSkill(SKILL_Sonic_Flash, target, distance);
			
		return chooseTaskAndTargets(null, target, distance);
	}
	
	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		super.onEvtAttacked(attacker, damage);
		
		Player player = getActor();
		
		if(canUseSkill(SKILL_Brave_Spear, player, 0) && tryCastSkill(SKILL_Brave_Spear, player, 0))
			return;
		
		if(player.getCurrentHpPercents() < 70 && canUseSkill(SKILL_Berserker, player, 0) && tryCastSkill(SKILL_Berserker, player, 0))
			return;
		
		if(player.getCurrentHpPercents() < 30 && canUseSkill(SKILL_Power_Revival, player, 0) && tryCastSkill(SKILL_Power_Revival, player, 0))
			return;
	}
}

