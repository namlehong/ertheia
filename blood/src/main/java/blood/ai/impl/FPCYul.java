package blood.ai.impl;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;

public class FPCYul extends ArcherPC
{
	public final int SKILL_SNIPING				= 10810; // Lv.1 	buff 	62 	0 	10000 	0 	- 	For 30 minutes when a bow or crossbow is equipped, P. Atk. + 5%, Atk. Spd. + 5%, and P. Def./M. Def. + 5%.

	public final int SKILL_FLASH				= 10793; // Lv.1 	buff 	77 	0 	10000 	0 	- 	Cancels the targets of nearby enemies. Increases Speed + 50 for 5 seconds and prevents enemies from targeting you.
	public final int SKILL_FINAL_ULTIMATE_ESCAPE= 10776; // Lv.1 	buff 	58 	0 	10000 	0 	- 	For 30 sec., P. Evasion + 60, M. Evasion + 60, M. Def. + 20%, Debuff Resistance + 95%, and P. Skill Evasion Rate + 80%.
	public final int SKILL_QUICK_FIRE			= 10779; // Lv.1 	buff 	39 	0 	10000 	0 	- 	For 20 seconds, increases Atk. Spd. by 50% and decreases P. Atk. by 15% when equipped with a bow/crossbow.
	public final int SKILL_RAPID_FIRE_STANCE	= 10758; // Lv.1 	buff 	10 	0 	10000 	0 	- 	For 30 min., P. Atk. + 10% and P. Skill Cooldown - 5% when a bow or crossbow is equipped.
	public final int SKILL_DEAD_EYE_STANCE		= 10757; // Lv.1 	buff 	10 	0 	10000 	0 	- 	For 30 min., P. Atk. + 10%, and skill power + 5% when equipped with a bow or crossbow.
	public final int SKILL_SNIPER_STANCE		= 10759; // Lv.1 	buff 	10 	0 	10000 	0 	- 	For 30 min., P. Atk. + 10% and Atk. Spd. + 5% when a bow or crossbow is equipped.
	public final int SKILL_MIND_EYE				= 10783; // Lv.1 	buff 	54 	0 	10000 	0 	- 	For 1 minute, opens the eye of infinity to increase Accuracy + 25, Critical Rate + 200, and Critical Damage + 70%.

	public final int SKILL_GRAVITY_TRAP			= 10792; // Lv.1 	debuff 	39 	0 	10000 	0 	- 	Plants a Gravity Trap, immobilizing the enemy.
	public final int SKILL_FROST_TRAP			= 10791; // Lv.1 	debuff 	39 	0 	10000 	0 	- 	Plants a Frost Trap, decreasing the enemy's Speed, Atk. Spd. and Casting Spd.
	public final int SKILL_SLOW_SHOT			= 10780; // Lv.1 	debuff 	94 	0 	10000 	1100 	- 	Attacks the target with 9338 Power added to P. Atk. For 10 sec., Speed - 70. Requires a bow or crossbow to be equipped.
	public final int SKILL_BULLSEYE				= 10801; // Lv.1 	debuff 	20 	0 	10000 	1100 	- 	Decreases the target's Bow/Crossbow Resistance by 40% and M. Def. by 5%.

	public final int SKILL_LURE_SHOT			= 10777; // Lv.1 	active 	77 	0 	10000 	1100 	- 	Draws in the enemy's attention.
	public final int SKILL_TORNADO_SHOT			= 10760; // Lv.1 	active 	98 	0 	10000 	1100 	- 	Attacks the target with 20814 Power added to P. Atk. Requires a bow or crossbow to be equipped. Over-hit. Critical.
	public final int SKILL_FLARE				= 10785; // Lv.1 	active 	70 	0 	10000 	0 	- 	Fires a Flare to reveal nearby hiding enemies or traps. Requires a bow or crossbow to be equipped.
	public final int SKILL_IMPACT_SHOT			= 10769; // Lv.1 	active 	98 	0 	10000 	1100 	- 	Fires an arrow at the enemy with 9947 Power added to P. Atk. Stuns the enemy for 9 seconds. Requires a bow or crossbow to be equipped. Over-hit. Chance of canceling the enemy's target.
	public final int SKILL_RECOIL_SHOT			= 10770; // Lv.1 	active 	102 	0 	10000 	1100 	- 	Shoots an arrow with 24888 Power added to P. Atk. to Knock Back the target. Requires a bow or crossbow to be equipped. Over-hit. Critical. Chance of canceling the enemy's target.
	public final int SKILL_QUICK_EVASION		= 10774; // Lv.1 	active 	27 	0 	10000 	300 	- 	Momentarily teleports 300 backward.
	public final int SKILL_DIVERSION			= 10778; // Lv.1 	active 	85 	0 	10000 	1100 	- 	Transfer the aggression of nearby enemies around the target to another party member.
	public final int SKILL_SUMMON_THUNDER_HAWK	= 10787; // Lv.1 	active 	123 	0 	10000 	0 	- 	Summons a Thunder Hawk.
	public final int SKILL_HEAVY_ARROW_RAIN		= 10772; // Lv.1 	active 	125 	0 	10000 	1100 	- 	Showers the enemy and nearby enemies with a rain of arrows, to inflict damage with 22526 damage added to P. Atk. Requires a bow or crossbow to be equipped. Over-hit. Critical.
	public final int SKILL_QUICK_SHOT			= 10762; // Lv.1 	active 	79 	0 	10000 	1100 	- 	Attacks the target with 15634 Power added to P. Atk. Requires a bow or crossbow to be equipped. Over-hit. Critical.
	public final int SKILL_PINPOINT_SHOT		= 10763; // Lv.1 	active 	117 	0 	10000 	1100 	- 	Aims for the target's weak points to attack with 22198 Power added to P. Atk. Disregards 30% of the target's P. Def. Requires a bow or crossbow to be equipped. Over-hit. Critical. Half-kill.
	public final int SKILL_MULTIPLE_ARROW		= 10771; // Lv.1 	active 	124 	0 	10000 	1100 	- 	Attacks frontal enemies with 23391 Power added to P. Atk. Requires a bow or crossbow to be equipped. Over-hit. Critical.
	public final int SKILL_BOW_STRIKE			= 10761; // Lv.1 	active 	60 	0 	10000 	40 	- 	Knocks back frontal enemies. Attacks the target with 2681 Power added to P. Atk. Requires a bow or crossbow to be equipped. Over-hit. Critical.
	public final int SKILL_YUL_AURA				= 1933;
	
	public FPCYul(Player actor)
	{
		super(actor);
	}
	
	public void prepareSkillsSetup() {
		_allowSelfBuffSkills.add(SKILL_DEAD_EYE_STANCE);
		_allowSelfBuffSkills.add(SKILL_SNIPING);
		_allowSelfBuffSkills.add(SKILL_YUL_AURA);
		// should move to situation skill
		_allowSelfBuffSkills.add(SKILL_QUICK_FIRE);
		_allowSelfBuffSkills.add(SKILL_MIND_EYE);
	}
	
	protected boolean defaultSubFightTask(Creature target)
	{
		archerFightTask(target);
		return true;
	}
	
	protected boolean criticalFight(Creature target)
	{
		double distance = getActor().getDistance(target);
		
		if(canUseSkill(SKILL_IMPACT_SHOT, target, distance))
			return tryCastSkill(SKILL_IMPACT_SHOT, target, distance);
		
		if(canUseSkill(SKILL_RECOIL_SHOT, target, distance))
			return tryCastSkill(SKILL_RECOIL_SHOT, target, distance);
		
		return false;
	}
	
	protected boolean archerFightTask(Creature target)
	{
		Player actor = getActor();
		
		double distance = actor.getDistance(target);
		
		if(distance < 300 && canUseSkill(SKILL_QUICK_EVASION, target, distance))
			return tryCastSkill(SKILL_QUICK_EVASION, target, distance);
		
		if(canUseSkill(SKILL_BULLSEYE, target, distance))
			return tryCastSkill(SKILL_BULLSEYE, target, distance);
		
		// AOE
		if(target.getAroundNpc(200, 200).size() > 3)
		{
			if(canUseSkill(SKILL_HEAVY_ARROW_RAIN, target, distance))
				return tryCastSkill(SKILL_HEAVY_ARROW_RAIN, target, distance);
			
			if(canUseSkill(SKILL_MULTIPLE_ARROW, target, distance))
				return tryCastSkill(SKILL_MULTIPLE_ARROW, target, distance);
		}
		
		// single
		
		if(canUseSkill(SKILL_QUICK_SHOT, target, distance))
			return tryCastSkill(SKILL_QUICK_SHOT, target, distance);
		
		if(canUseSkill(SKILL_PINPOINT_SHOT, target, distance))
			return tryCastSkill(SKILL_PINPOINT_SHOT, target, distance);
		
		return chooseTaskAndTargets(null, target, distance);
	}
	
	@Override
	protected void onEvtClanAttacked(Creature attacked, Creature attacker, int damage)
	{
		if(!attacked.isPlayer())
			return;
		
		Player member = attacked.getPlayer();
		
		if(member.getCurrentHpPercents() < 50)
		{
			criticalFight(attacker);
		}
	}
	
}

