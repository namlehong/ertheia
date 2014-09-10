package blood.ai.impl;

import blood.utils.ClassFunctions;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;

public class FPCSigel extends TankerPC
{
	public final int SKILL_FOCUS_SHIELD				= 10020; // Lv.1 	aura 	50 	0 	10000 	0 	- 	P. Def. + 350, Shield Defense + 700, and shield defense + 35%. MP is continuously consumed.
	public final int SKILL_RAGE_AURA				= 10028; // Lv.1 	aura 	0 	0 	10000 	0 	- 	When equipped with a sword or blunt weapon, increases P. Atk. Atk. Spd. and P. Critical Damage by 10%, and Speed by 10. Decreases nearby enemies' P. Atk. and Atk. Spd. by 20%. Only one Sigel Knight's Aura can be active at any time.
	public final int SKILL_CHALLENGE_AURA			= 10030; // Lv.1 	aura 	0 	0 	10000 	0 	- 	When a sword or blunt weapon is equipped, increases aggression power. Additionally, increases party members' P. Atk. and Atk. Spd. by 5% and decreases skill MP Consumption by 20%. Only one Sigel Knight's Aura can be active at any time.
	public final int SKILL_IRON_AURA				= 10032; // Lv.1 	aura 	0 	0 	10000 	0 	- 	P. Atk. and Atk. Spd. + 5%. P. Def. + 215, Shield Defense + 50%, shield defense rate + 100% when equipped with heavy armor. Bow/Crossbow Resistance + 20%. Damage from critical attack - 15%. Only one Sigel Knight's Aura can be active at any time.
	public final int SKILL_AURA_RESISTANCE			= 10034; // Lv.1 	aura 	0 	0 	10000 	0 	- 	P. Atk. and Atk. Spd. + 5%, M. Def. + 10%, Fire/Water/Wind/Earth Resistance + 20. Only one Sigel Knight's Aura can be active at any time.
	public final int SKILL_RECOVERY_AURA			= 10036; // Lv.1 	aura 	0 	0 	10000 	0 	- 	P. Atk. and Atk. Spd. + 5%, and P. Mez/Debuff Resistance + 20. Additionally, Heal Amount by healing skills + 10% and HP Recovery Bonus + 10%. Only one Sigel Knight's Aura can be active at any time.
	public final int SKILL_SPIRIT_AURA				= 10038; // Lv.1 	aura 	0 	0 	10000 	0 	- 	P. Atk. and Atk. Spd. + 5%, and M. Mez/Debuff Resistance + 20. Additionally, MP Recovery Amount and MP Recovery Bonus + 10%. Only one Sigel Knight's Aura can be active at any time.
	public final int SKILL_TRUE_VANGUARD			= 10244; // Lv.1 	aura 	0 	0 	10000 	0 	- 	Damage inflicted on monsters + 50%, and P. Skill MP Consumption + 30%.
	public final int SKILL_SOUL_OF_THE_PHOENIX		= 438; 	 // Lv.2 	buff 	250 	0 	10000 	0 	- 	Revives from death with completely recovered status. Buff/de-buff doesn't disappear at death but is maintained. But, the Noblesse Blessing and the Amulet of Luck disappear. Consumes 1 Blood of the Phoneix.
	public final int SKILL_FINAL_ULTIMATE_DEFENSE	= 10017; // Lv.1 	buff 	58 	0 	10000 	0 	- 	For 30 sec., P. Def. + 4000 and M. Def. + 4000. Damage received is capped at 300 and 40% of it is reflected back to the attacker. Additionally, + 80% Buff-canceling Attack Resistance.
	public final int SKILL_PROTECTION_OF_FAITH		= 10019; // Lv.1 	buff 	127 	0 	10000 	0 	- 	For 15 seconds, all party members' P. Def. + 5000, M. Def. + 5000, and Debuff Resistance + 50%. Receives 90% of the damage inflicted to each party member.
	public final int SKILL_SPIKE_SHIELD				= 10021; // Lv.1 	buff 	20 	0 	10000 	0 	- 	For 10 seconds, reflects 10% of received damage back to the attacker. Also has a 50% chance of reflecting magic damage back to the attacker. Requires a shield. 
	public final int SKILL_KNIGHT_FRENZY			= 10025; // Lv.1 	buff 	20 	0 	10000 	0 	- 	For 1 minute, P. Def. and M. Def. + 50%, Speed + 10, Accuracy + 4 and Atk. Spd. + 10% when equipped with a sword/blunt weapon. Additionally, Critical Rate + 30 when equipped with a sword, and Critical Damage + 30% when equipped with a blunt weapon.
	
	public final int SKILL_SACRIFICE				= 69; 	 // Lv.31 	active 	0 	0 	10000 	600 	- 	Consumes your HP to recover the target's HP + 2272.
	public final int SKILL_SUPERIOR_AGGRESSION		= 10026; // Lv.1 	active 	0 	0 	10000 	400 	- 	Provokes the selected target into attacking you.
	public final int SKILL_SUPERIOR_AGGRESSION_AURA	= 10027; // Lv.1 	active 	0 	0 	10000 	0 	- 	Provokes nearby enemies into attacking you.
	public final int SKILL_GUST_BLADE				= 10013; // Lv.1 	active 	121 	0 	10000 	40 	- 	Attacks nearby targets with 10314 Power added to P. Atk. Can be used while a sword/dualsword/blunt weapon is equipped. Over-hit. Critical.
	public final int SKILL_SUMMON_KNIGHT_CUBIC		= 10043; // Lv.1 	active 	96 	0 	10000 	0 	- 	Summons a Knight Cubic. The summoned cubic removes debuffs from you.
	public final int SKILL_SUMMON_GOLDEN_LION		= 10040; // Lv.1 	active 	154 	0 	10000 	0 	- 	Summons a golden lion to fight alongside you.
	public final int SKILL_CHAIN_STRIKE				= 10015; // Lv.1 	active 	58 	0 	10000 	600 	- 	Pulls selected target to make them attack you. Requires a shield.
	public final int SKILL_CHAIN_GALAXY				= 10014; // Lv.1 	active 	159 	0 	10000 	0 	- 	Attacks nearby targets with 14300 Power added to P. Atk. Healing Power received - 90%.
	
	public final int SKILL_MASS_SHACKLING			= 404; 	 // Lv.11 	debuff 	59 	0 	10000 	0 	- 	Provokes nearby enemies to attack, then immobilizes them for 10 seconds.
	public final int SKILL_SHIELD_CHARGE			= 10008; // Lv.1 	debuff 	53 	0 	10000 	40 	- 	Uses a shield to attack the selected target with 10821 Power added to P. Atk. and also P. Def. - 10%, P. Evasion - 2, and P. Critical Damage + 10%. Can be used while a shield is equipped.
	public final int SKILL_LAST_JUDGMENT			= 10009; // Lv.1 	debuff 	64 	0 	10000 	40 	- 	Attacks the selected target with 13673 Power added to P. Atk, and Speed - 70. Can be used while a sword/blunt weapon is equipped. Over-hit. Critical.
	public final int SKILL_JUSTICE_PUNISHMENT		= 10010; // Lv.1 	debuff 	80 	0 	10000 	40 	- 	Attacks the selected target with 17476 Power added to P. Atk, and P. Atk./M. Atk. - 20%. Blocks the use of magic skills. Requires a sword or blunt weapon to be equipped. Over-hit. Critical.
	public final int SKILL_SHIELD_IMPACT			= 10011; // Lv.1 	debuff 	49 	0 	10000 	40 	- 	Attacks the selected target with 9870 Power added to P. Atk. and Stuns for 5 seconds. Can be used while a shield is equipped.
	public final int SKILL_SIGEL_AURA 				= 1927;
	public final int SKILL_PROVOKING_SHAKLE			= 10090;
	
	public FPCSigel(Player actor)
	{
		super(actor);
	}
	
	public void prepareSkillsSetup() {
		_allowSkills.add(SKILL_SUMMON_KNIGHT_CUBIC);
		_allowSelfBuffSkills.add(SKILL_IRON_AURA);
		_allowSelfBuffSkills.add(SKILL_FOCUS_SHIELD);
		_allowSelfBuffSkills.add(SKILL_SIGEL_AURA);
	}
	
	protected boolean defaultSubFightTask(Creature target)
	{
		tankerFightTask(target);
		return true;
	}

	protected boolean tankerFightTask(Creature target)
	{
		Player actor = getActor();
		
		double distance = actor.getDistance(target);
		
		if(distance > 300 && canUseSkill(SKILL_CHAIN_STRIKE, target, distance))
			return tryCastSkill(SKILL_CHAIN_STRIKE, target, distance);
		
		if(canUseSkill(SKILL_SUPERIOR_AGGRESSION, target, distance))
			return tryCastSkill(SKILL_SUPERIOR_AGGRESSION, target, distance);
		
		if(canUseSkill(SKILL_SUPERIOR_AGGRESSION_AURA, target, distance))
			return tryCastSkill(SKILL_SUPERIOR_AGGRESSION_AURA, target, distance);
		
		if(canUseSkill(SKILL_SHIELD_CHARGE, target, distance))
			return tryCastSkill(SKILL_SHIELD_CHARGE, target, distance);
		
		if(canUseSkill(SKILL_SUPERIOR_AGGRESSION_AURA, target, distance))
			return tryCastSkill(SKILL_SUPERIOR_AGGRESSION_AURA, target, distance);
		
		if(canUseSkill(SKILL_PROVOKING_SHAKLE, target, distance))
			return tryCastSkill(SKILL_PROVOKING_SHAKLE, target, distance);
		
		if(canUseSkill(SKILL_MASS_SHACKLING, target, distance))
			return tryCastSkill(SKILL_MASS_SHACKLING, target, distance);
		
		if(canUseSkill(SKILL_SHIELD_IMPACT, target, distance))
			return tryCastSkill(SKILL_SHIELD_IMPACT, target, distance);
		
		return false;
	}
	
	@Override
	protected void onEvtClanAttacked(Creature attacked, Creature attacker, int damage)
	{
		super.onEvtClanAttacked(attacked, attacker, damage);
		if(!attacked.isPlayer())
			return;
		
		Player player = getActor();
		
		thinkBuff();
		
		double distance = player.getDistance(attacker);
		
		if(distance > 1000)
			return;
		
		Player member = attacked.getPlayer();
		
		if(!ClassFunctions.isHealer(member) && member.getCurrentHpPercents() > 80)
			return;
		
		if(ClassFunctions.isHealer(member))
			setAttackTarget(attacker);
		
		if(distance > 300 && canUseSkill(SKILL_CHAIN_STRIKE, attacker, distance) && tryCastSkill(SKILL_CHAIN_STRIKE, attacker, distance))
			return;
		
		if(canUseSkill(SKILL_SUPERIOR_AGGRESSION, attacker, distance) && tryCastSkill(SKILL_SUPERIOR_AGGRESSION, attacker, distance))
			return;
		
		if(canUseSkill(SKILL_SUPERIOR_AGGRESSION_AURA, attacker, distance) && tryCastSkill(SKILL_SUPERIOR_AGGRESSION_AURA, attacker, distance))
			return;
	}
	
}

