package blood.ai.impl;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.utils.Location;

/**
 * 
 * @author mylove1412
 *
 * Logic inside Feoh AI
 * 
 * Select stance in situation ? -> not buff -> active by situation
 *
 */

public class FPCFeoh extends MysticPC
{
	public static final int
	SKILL_Hell_Binding				= 11050, // Lv.1 	debuff 	70 	0 	10000 	1100 	- 	Immobilizes the target midair.
	SKILL_Elemental_Destruction		= 11023, // Lv.1 	active 	125 	0 	10000 	1100 	- 	Inflicts a magic attack imbued with elemental properties on the target with 431 power added to attribute damage. Over-hit.
	SKILL_Elemental_Burst			= 11106, // Lv.1 	active 	137 	0 	10000 	1100 	- 	Detonates an Elemental Destruction to inflict damage on nearby enemies with 569 Power. For 15 seconds, M. Def. - 20%.
	SKILL_Ultimate_Body_To_Mind		= 11064, // Lv.1 	active 	0 	0 	10000 	0 	- 	Sacrifices 384 HP to recover 400 MP.
	SKILL_Mass_Devil_Curse 			= 11048, // 	1 	active 	211 	0 	10000 	1100 	- 	Attacks the target and surrounding enemies with 125 power and absorbs 25% of the damage inflicted as HP. Additionally, for 10 seconds, the target's M. Atk. and M. Def. - 30%; P. Def., M. Atk., Casting Spd., M. Def., P. Critical Rate, M. Critical Rate, and Speed - 8%; and Accuracy - 12.
	SKILL_Double_Casting			= 11068, // Lv.1 	buff 	210 	0 	10000 	0 	- 	For 30 sec., you can cast elemental spells with both hands. Decreases MP Consumption by 50% and magic skill Cooldown by 5%. Eliminates magic cancel during magic use.
	SKILL_Wizard_Spirit				= 11046, // Lv.1 	buff 	70 	0 	10000 	0 	- 	For 30 sec., M. Atk. + 20%, M. Critical Rate + 50%, P. Def. and M. Def. + 20%, Evasion + 2, and MP Consumption for magic skills - 50%. When attacked, has a chance of inflicting magic damage on the attacking target.
	SKILL_Death_Breath				= 11029, // Lv.1 	active 	68 	0 	10000 	1100 	- 	For 15 seconds, decreases the target's HP by 480 per second.
	SKILL_Water_Stance				= 11008, // Lv.1 	aura 	0 	0 	0 	0 	- 	Bestows the property of Water to magic spells cast.
	SKILL_Earth_Stance				= 11010, // Lv.1 	aura 	0 	0 	0 	0 	- 	Bestows the property of Earth to magic spells cast.
	SKILL_Wind_Stance				= 11009, // Lv.1 	aura 	0 	0 	0 	0 	- 	Bestows the property of Wind to magic spells cast.
	SKILL_Fire_Stance				= 11007, // Lv.1 	aura 	0 	0 	0 	0 	- 	Bestows the property of Fire to magic spells cast.
	SKILL_Death_Lord				= 11030, // Lv.1 	debuff 	68 	0 	10000 	1100 	- 	For 10 seconds, drains the target's life with 25 Power per second. Absorbs 100% of the damage inflicted as HP.
	SKILL_Mass_Death_Fear			= 11056, // Lv.1 	debuff 	211 	0 	10000 	1100 	- 	For 10 seconds, inflicts Fear on the target and nearby enemies and decreases Speed by 25%.
	SKILL_Arcane_Barrier			= 11065, // Lv.1 	buff 	70 	0 	10000 	0 	- 	For 20 seconds, decreases incoming damage - 90% and defends against the rest by consuming MP. When attacked, reflects 10% of incoming damage. Ineffective when MP drops to 0.
	SKILL_Magical_Charge			= 11094, // Lv.1 	debuff 	70 	0 	10000 	0 	- 	Attacks nearby enemies with 133 Power added to M. Atk., inflicting Stun and canceling their targets. Teleports you to the front.
	SKILL_Elemental_Storm			= 11040, // Lv.1 	active 	264 	0 	10000 	1100 	- 	Inflicts a magic attack imbued with elemental properties on the target and surrounding enemies with 262 Power added to attribute damage. Over-hit.
	SKILL_Magical_Evasion			= 11057, // Lv.1 	debuff 	70 	0 	10000 	0 	- 	Attacks nearby enemies with 133 Power added to M. Atk., inflicting Stun and canceling their targets. Teleports you to the back.
	SKILL_Devil_Sway 				= 11095,// 	1 	debuff 	190 	0 	10000 	1100 	- 	Resets the duration of the target's paralysis, hold, silence, sleep, shock, fear, petrification, and disarm.
	SKILL_Devil_Curse 				= 11047,// 	1 	active 	70 	0 	10000 	1100 	- 	Attacks the target with 157 Power. For 20 sec., the target's M. Atk. and M. Def. - 30%, Casting Spd., P. Critical Rate, M. Critical Rate, P. Def. and Speed - 10%, and P. Evasion - 6.
	SKILL_Death_Howl				= 11032, // Lv.1 	active 	204 	0 	10000 	0 	- 	Attacks nearby targets with 157 Power and pushes them back. Over-hit.
	SKILL_Death_Fear				= 11055, // Lv.1 	debuff 	70 	0 	10000 	1100 	- 	For 10 seconds, inflicts Fear on the target and decreases Speed by 40%.
	SKILL_Elemental_Blast			= 11034, // Lv.1 	active 	213 	0 	10000 	1100 	- 	Inflicts a magic attack imbued with elemental properties on the target and surrounding enemies with 210 Power added to attribute damage. Over-hit.
	SKILL_Elemental_Crash			= 11017, // Lv.1 	active 	53 	0 	10000 	1100 	- 	Inflicts a magic attack imbued with elemental properties on the target with 158 Power added to attribute damage. In addition, Fire inflicts fear, Water decreases Speed, Wind causes Knock Back, and Earth inflicts hold. Double Casting inflicts stun. Over-hit.
	SKILL_Elemental_Spike			= 11011, // Lv.1 	active 	37 	0 	10000 	1100 	- 	Inflicts a magic attack imbued with elemental properties on the target with 133 Power added to attribute damage. In addition, decreases the target's attribute resistance according to the Elemental Stance used. Over-hit.
	SKILL_FEOH_AURA					= 1935;
	
	public final int SKILL_ULTIMATE_BTM 	= 11064;
	public final int SKILL_MAGIC_EVASION 	= 11057;
	public final int SKILL_MAGIC_CHARGE 	= 11094;
	public final int SKILL_DEVIL_CURSE 		= 11047;
	public final int SKILL_DARK_CURSE		= 11150;
	
	public final int SKILL_ELEMENT_SPIKE 	= 11011;
	public final int SKILL_ELEMENT_CRASH 	= 11017;
	public final int SKILL_ELEMENT_DESTRUCTION = 11023;
	public final int SKILL_ELEMENT_BLAST	= 11034;
	public final int SKILL_ELEMENT_STORM	= 11040;
	
	public final int SKILL_DEATH_LORE		= 11030;
	public final int SKILL_DEATH_FEAR		= 11055;
	
	public final int SKILL_ELEMENT_BURST_HU	= 11106;
	public final int SKILL_ELEMENT_BURST_EL	= 11112;
	public final int SKILL_ELEMENT_BURST_DE	= 11118;
	
	public final int SKILL_FIRE_STANCE 	= 11007;
	public final int SKILL_WATER_STANCE	= 11008;
	public final int SKILL_WIND_STANCE 	= 11009;
	public final int SKILL_EARTH_STANCE	= 11010;
	public final int SKILL_ARCANE_POWER = 337;
	
	protected long _darkcureTS = 0;
	protected int _darkcureReuse = 30000;
	
	// should add auto learn skill
	// TODO add AOE logic
	
	public FPCFeoh(Player actor)
	{
		super(actor);
	}
	
	public void prepareSkillsSetup() {
		_allowSelfBuffSkills.add(SKILL_ARCANE_POWER);
		_allowSelfBuffSkills.add(SKILL_EARTH_STANCE);
		_allowSelfBuffSkills.add(SKILL_FEOH_AURA);
	}
	
	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		super.onEvtAttacked(attacker, damage);
		Player actor = getActor();
		
		double distance = actor.getDistance(attacker);
		if(distance < 200 && !actor.isInParty())
		{
			if(attacker.getAroundNpc(200, 200).size() > 3)
			{
				if(canUseSkill(SKILL_Magical_Evasion, attacker, distance) && tryCastSkill(SKILL_Magical_Evasion, attacker, distance))
					return;
				if(canUseSkill(SKILL_Magical_Charge, attacker, distance) && tryCastSkill(SKILL_Magical_Charge, attacker, distance))
					return;
				if(canUseSkill(SKILL_Mass_Death_Fear, attacker, distance) && tryCastSkill(SKILL_Mass_Death_Fear, attacker, distance))
					return;
			}
			else
			{
				if(canUseSkill(SKILL_Death_Fear, attacker, distance) && tryCastSkill(SKILL_Death_Fear, attacker, distance))
					return;
				if(canUseSkill(SKILL_Hell_Binding, attacker, distance) && tryCastSkill(SKILL_Hell_Binding, attacker, distance))
					return;
			}	
		}
	}
	
	protected int _last_target_id = 0;
	protected boolean _last_target_dark_curse = false;
	
	protected boolean feohFightTask(Creature target)
	{
		Player player = getActor();
		double distance = player.getDistance(target);
		
		if(canUseSkill(SKILL_Ultimate_Body_To_Mind, player) && player.getCurrentHpPercents() > 40)
			return tryCastSkill(SKILL_Ultimate_Body_To_Mind, player);
		
		if(target.getAroundNpc(200, 200).size() > 3)
		{
			if(canUseSkill(SKILL_Double_Casting, player))
				return tryCastSkill(SKILL_Double_Casting, player);
			
			if(canUseSkill(SKILL_Wizard_Spirit, player))
				return tryCastSkill(SKILL_Wizard_Spirit, player);
			
			if(canUseSkill(SKILL_Mass_Devil_Curse, target, distance))
				return tryCastSkill(SKILL_Mass_Devil_Curse, target, distance);
			
			if(canUseSkill(SKILL_Elemental_Storm, target, distance))
				return tryCastSkill(SKILL_Elemental_Storm, target, distance);
			
			if(canUseSkill(SKILL_Elemental_Blast, target, distance))
				return tryCastSkill(SKILL_Elemental_Blast, target, distance);
		}
		
		
		// SOLO job
		if(!player.isInParty())
		{
			if(distance < 400 && canUseSkill(SKILL_Death_Fear, target))
				return tryCastSkill(SKILL_Death_Fear, target, distance);
			
			if(_last_target_id != target.getObjectId())
			{
				_last_target_dark_curse = false;
			}
			
			// if we are feoh soul taker we should debuf darkcurse first
			if(!_last_target_dark_curse && canUseSkill(SKILL_DARK_CURSE, target, distance)){
				_last_target_dark_curse = true;
				_last_target_id = target.getObjectId();
				return tryCastSkill(SKILL_DARK_CURSE, target, distance);
			}
		}
			
		// 1st use death curse
		if(canUseSkill(SKILL_Devil_Curse, target, distance))
			return tryCastSkill(SKILL_Devil_Curse, target, distance);
		
		if(player.getCurrentHpPercents() < 50)
		{
			// 1st if we success on destruction
			if(canUseSkill(SKILL_Elemental_Burst, target, distance))
				return tryCastSkill(SKILL_Elemental_Burst, target, distance);
			
			// use destruction for more DPS
			if(canUseSkill(SKILL_Elemental_Destruction, target, distance))
				return tryCastSkill(SKILL_Elemental_Destruction, target, distance);
		}
		
		// save mp skill
		if(canUseSkill(SKILL_Elemental_Spike, target, distance))
			return tryCastSkill(SKILL_Elemental_Spike, target, distance);
		
		// save mp skill
		if(canUseSkill(SKILL_Elemental_Crash, target, distance))
			return tryCastSkill(SKILL_Elemental_Crash, target, distance);
		
		// 1st if we success on destruction
		if(canUseSkill(SKILL_Elemental_Burst, target, distance))
			return tryCastSkill(SKILL_Elemental_Burst, target, distance);
		
		// use destruction for more DPS
		if(canUseSkill(SKILL_Elemental_Destruction, target, distance))
			return tryCastSkill(SKILL_Elemental_Destruction, target, distance);
		
		addTaskMove(Location.findAroundPosition(target, 600), true);
		return false;
	}
	
	protected boolean defaultSubFightTask(Creature target)
	{
		feohFightTask(target);
		return true;
	}
	
}

