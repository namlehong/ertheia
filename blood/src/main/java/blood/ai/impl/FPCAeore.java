package blood.ai.impl;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Party;
import l2s.gameserver.model.Player;
import blood.utils.ClassFunctions;

public class FPCAeore extends HealerPC
{
	public static final int
	SKILL_Blessing_of_Saha			= 11830, // Lv.1 	buff 	35 	0 	10000 	600 	- 	For 30 minutes, the selected target's Max MP - 20%, HP Recovery Bonus - 20%, M. Critical Rate - 20, Critical Damage during a normal attack - 20%, P. Atk. - 10%, P. Def. - 20%, Atk. Spd. - 20%, M. Atk. - 20%, M. Def. - 20%, Casting Spd. - 20%, Debuff Resistance - 10%, damage received from a critical attack - 30% and Speed - 15%.
	SKILL_Brilliant_Heal			= 11757, // Lv.1 	active 	277 	0 	10000 	0 	- 	Heals yourself and nearby party members with 1191 Power added to M. Atk. If HP is full, CP is restored.
	SKILL_Superior_Inquisition		= 11746, // Lv.1 	aura 	0 	0 	10000 	0 	- 	Damage inflicted on monsters + 50%, and M. Skill MP Consumption + 30%.
	SKILL_Fatal_Sleep				= 11778, // Lv.1 	debuff 	26 	0 	10000 	600 	- 	For 20 seconds, inflicts Sleep on the target. When Sleep ends, decreases Atk. Spd., Casting Spd., and Speed by 50%.
	SKILL_Giant_Favor 				= 11772,// 	1 	active 	17 	0 	10000 	0 	- 	Performs an ancient secret technique of the Giants to increase P. Def., M. Def., and Healing power by 1000 for 30 seconds.
	SKILL_Speed_of_Saha				= 11823, // Lv.1 	buff 	17 	0 	10000 	600 	- 	For 30 minutes, the selected target's P. Accuracy + 5, P. and M. Evasion + 5, Speed + 34 and received Critical Damage - 30%.
	SKILL_Dissolve					= 11770, // Lv.1 	buff 	10 	0 	10000 	0 	- 	Removes the aggression of nearby enemies toward you and increases speed by 100.
	SKILL_Mark_of_Lumi				= 11777, // Lv.1 	debuff 	40 	0 	10000 	900 	- 	Leaves a mark that decreases the target's P. Def. and M. Def. by 10%, and consumes 113 HP every second for 60 seconds.
	SKILL_Crystal_Regeneration		= 11765, // Lv.1 	buff 	73 	0 	10000 	0 	- 	Crystallize yourself and become immune to damage and debuffs for 10 seconds. Recover 266 HP and 74 MP every second.
	SKILL_Critical_of_Saha			= 11821, // Lv.1 	buff 	17 	0 	10000 	600 	- 	For 30 minutes, the selected target's P. Critical Rate + 32%, P. Critical Damage + 35%, M. Critical Rate + 20, MP Consumption for physical skills - 20% and MP Consumption for magic skills - 10%.
	SKILL_Mass_Fatal_Sleep			= 11832, // Lv.1 	debuff 	52 	0 	10000 	0 	- 	Casts Sleep on nearby enemies for 30 seconds.
	SKILL_Party_Return				= 11819, // Lv.1 	active 	91 	8 	10000 	0 	- 	Teleports party members to the nearest village. Cannot be used in a specially designated place such as the GM Consultation Service.
	SKILL_Blessed_Resurrection		= 11784, // Lv.1 	active 	35 	0 	10000 	400 	- 	Resurrects a dead target and restores 85% of the XP lost.
	SKILL_Force_of_Saha				= 11822, // Lv.1 	buff 	17 	0 	10000 	600 	- 	For 30 minutes, the selected target's Atk. Spd. + 34% and Casting Spd. + 31%. Bestows a 9% Vampiric Rage effect at a certain rate.
	SKILL_Celestial_Protection		= 11758, // Lv.1 	buff 	17 	0 	10000 	600 	- 	Renders the selected party member invincible for 10 seconds.
	SKILL_Rebirth					= 11768, // Lv.1 	active 	66 	0 	10000 	0 	- 	Restores all party members' HP/MP/CP and has a chance of dispelling their debuffs.
	SKILL_Summon_Tree_of_Life		= 11774, // Lv.1 	active 	185 	0 	10000 	600 	- 	Summons a Tree of Life. The summoned tree heals nearby party members for 30 seconds. Can also cast a debuff canceling blessing.
	SKILL_Summon_Lumi				= 11776, // Lv.1 	active 	96 	0 	10000 	0 	- 	Summons a Spirit of Light.
	SKILL_Clarity_of_Saha			= 11825, // Lv.1 	buff 	17 	0 	10000 	600 	- 	For 30 minutes, Hold/Sleep/Mental Attack Resistance + 50, Buff-canceling Attack Resistance + 30 and Debuff Resistance + 20.
	SKILL_Panic_Heal				= 11756, // Lv.1 	active 	207 	0 	10000 	600 	- 	Heals the target with 1488 Power added to M. Atk. If HP is full, CP is restored.
	SKILL_Radiant_Recharge			= 11760, // Lv.1 	active 	183 	0 	10000 	600 	- 	Instantly recovers the target's MP with a maximum of 166 Power depending on the target's level. Also, for 10 seconds, recovers 12 MP per second. Ineffective on classes that have the Recharge skill.
	SKILL_Radiant_Heal				= 11755, // Lv.1 	active 	138 	0 	10000 	600 	- 	Heals the target with 1488 Power added to M. Atk. If HP is full, CP is restored.
	SKILL_Radiant_Purge				= 11763, // Lv.1 	buff 	35 	0 	10000 	600 	- 	Dispels the target's debuffs. Allies only.
	SKILL_Balance_Heal				= 11762, // Lv.1 	buff 	178 	0 	10000 	0 	- 	Equalizes all party members' HP and restores 333 HP every second for 15 seconds.
	SKILL_Divine_Prayer				= 11851, // Lv.1 	buff 	185 	0 	10000 	0 	- 	For 30 min., party members' HP heal amount + 5%.
	SKILL_Sustain					= 11752, // Lv.1 	active 	53 	0 	10000 	600 	- 	For 15 seconds, Heals the selected party member by 172 per second. Further increases incoming Healing power by 10%. Also increases your Healing power.
	SKILL_Power_of_Saha				= 11820, // Lv.1 	buff 	17 	0 	10000 	600 	- 	For 30 minutes, the selected target's P. Atk. + 17%, P. Def. + 15%, M. Atk. + 79%, M. Def. + 30%.
	SKILL_Resistance_of_Saha		= 11824, // Lv.1 	buff 	17 	0 	10000 	600 	- 	For 30 minutes, Stun Resistance + 40%, Poison Resistance + 50% and Holy/Dark Attack Resistance + 30.
	SKILL_Dark_Force				= 11814, // Lv.1 	active 	87 	0 	10000 	400 	- 	Inflicts damage on the target with 95 Power added to M. Atk. Has a chance of pushing back the target.
	SKILL_Dark_Backfire				= 11769, // Lv.1 	debuff 	35 	0 	10000 	600 	- 	For 30 sec., the target's MP Consumption + 50%, and Casting Spd. and Atk. Spd. - 20%.
	SKILL_Dark_Rain					= 11817, // Lv.1 	active 	282 	0 	10000 	0 	- 	Inflicts damage on nearby enemies with 102 Power added to M. Atk. and decreases aggression toward yourself.
	SKILL_Mass_Dark_Veil			= 11767, // Lv.1 	active 	423 	0 	10000 	900 	- 	Inflicts damage on the target and nearby enemies with 117 Power and decreases aggression toward yourself.
	SKILL_Dark_Devour				= 11780, // Lv.1 	debuff 	35 	0 	10000 	600 	- 	Decreases the target's Recovery by 50%.
	SKILL_Dark_Blast				= 11766, // Lv.1 	active 	70 	0 	10000 	900 	- 	Inflicts damage on the target with 128 Power.
	SKILL_Erase_Impact				= 11831, // Lv.1 	active 	373 	0 	10000 	600 	- 	Banishes the enemy's servitor into another dimension.
	SKILL_Fairy_of_Life				= 11754, // Lv.1 	active 	43 	0 	10000 	600 	- 	Summons a fairy that follows and periodically heals the selected party member for 60 seconds. Has a chance of recovering HP when attacked.
	SKILL_Progressive_Heal			= 11828, // Lv.1 	active 	573 	0 	10000 	900 	- 	Heals the target and the 10 most injured allies around the target by up to 40% of their Max HP. The recovery amount decreases starting from the most injured person.
	SKILL_Miraculous_Benediction	= 11850, // Lv.1 	active 	185 	0 	10000 	0 	- 	Restores clan members' CP/HP. Can only be used when MP is 50% or below.
	SKILL_Emblem_of_Salvation		= 11826, // Lv.1 	buff 	70 	0 	10000 	600 	- 	Resurrects from death fully recovered. Buffs/debuffs remain in death, but Noblesse Blessing and Lucky Charm disappear.
	SKILL_AEORE_AURA 				= 1939;
	
	
	public final int SKILL_DARK_BACKFIRE		= 11769;
	public final int SKILL_MARK_LUMI			= 11777;
	public final int SKILL_FATAL_SLEEP			= 11778;
	public final int SKILL_MASS_FATAL_SLEEP		= 11832;
	
	public final int SKILL_SUSTAIN				= 11752;
	public final int SKILL_FAIRY_OF_LIFE		= 11754;
	public final int SKILL_RADIANT_HEAL			= 11755;
	public final int SKILL_PANIC_HEAL			= 11756;
	public final int SKILL_BRILLIANT_HEAL		= 11757; // party
	public final int SKILL_RADIANT_RECHARGE		= 11760; // mp
	public final int SKILL_REBIRTH				= 11768; // party full recover - 10mins
	public final int SKILL_GIANT_FLAVOR			= 11772; // buff ud - 10 mins
	public final int SKILL_BLESS_RES			= 11784;
	
	public final int SKILL_CRYSTAL_REGENERATION = 11765;
	
	
	public FPCAeore(Player actor)
	{
		super(actor);
	}
	
	public void prepareSkillsSetup() {
		_allowSelfBuffSkills.add(SKILL_AEORE_AURA);
		_allowSelfBuffSkills.add(SKILL_Divine_Prayer);
		_allowPartyBuffSkills.add(SKILL_Divine_Prayer);
	}
	
	protected boolean defaultSubFightTask(Creature target)
	{
		healerSoloFightTask(target);
		return true;
	}
	
	public boolean healerSoloFightTask(Creature target)
	{
		Player player = getActor();
		
		if(player.getCurrentMpPercents() < 90 && canUseSkill(SKILL_CRYSTAL_REGENERATION, player))
			return tryCastSkill(SKILL_CRYSTAL_REGENERATION, player);
		
		double distance = player.getDistance(target);
		
		Party party = player.getParty();
		
		double lowestHpPercent = 100d;
		@SuppressWarnings("unused")
		Player lowestHpMember = null;
		double lowestMpPercent = 100d;
		Player lowestMpMember = null;
		
		int hp75 = 0;
		@SuppressWarnings("unused")
		int mp75 = 0;
		int hp50 = 0;
		int mp50 = 0;
		@SuppressWarnings("unused")
		int hp25 = 0;
		int mp25 = 0;
		
		
		if(party != null)
		{
			for(Player member: party.getPartyMembers())
			{
				double currentMemberHpPercent = member.getCurrentHpPercents(); 
				double currentMemberMpPercent = member.getCurrentMpPercents();
				
				if(currentMemberHpPercent < 75) hp75++;
				if(currentMemberHpPercent < 50) hp50++;
				if(currentMemberHpPercent < 25) hp25++;
				
				if(currentMemberMpPercent < 75) mp75++;
				if(currentMemberMpPercent < 50) mp50++;
				if(currentMemberMpPercent < 25) mp25++;
				
				if(member.equals(player))
					continue;
				
				if(currentMemberHpPercent < lowestHpPercent)
				{
					lowestHpPercent = currentMemberHpPercent;
					lowestHpMember = member;
				}
				
				if(currentMemberMpPercent < lowestMpPercent)
				{
					lowestMpPercent = currentMemberMpPercent;
					lowestMpMember = member;
				}
			}
			
			if((mp50 >= 3 || mp25 > 1) && canUseSkill(SKILL_Rebirth, player, 0))
				return tryCastSkill(SKILL_Rebirth, player, 0);	
			
			if(hp50 > 0 && canUseSkill(SKILL_Balance_Heal, player, 0, true))
				return tryCastSkill(SKILL_Balance_Heal, player, 0);
			
			if(hp75 >= 3 && player.getServitorsCount() == 0 && canUseSkill(SKILL_Summon_Tree_of_Life, player, 0))
				return tryCastSkill(SKILL_Summon_Tree_of_Life, player, 0);
			
			if(hp75 >= 3 && canUseSkill(SKILL_Brilliant_Heal, player, 0))
				return tryCastSkill(SKILL_Brilliant_Heal, player, 0);
			
			if(lowestMpMember != null && lowestMpPercent < 90 && canUseSkill(SKILL_Radiant_Recharge, lowestMpMember, 0))
				return tryCastSkill(SKILL_Radiant_Recharge, lowestMpMember, 0);
		}
		
		if(canUseSkill(SKILL_MARK_LUMI, target, distance))
			return tryCastSkill(SKILL_MARK_LUMI, target, distance);
		
		return true;
	}
	
	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		super.onEvtAttacked(attacker, damage);
		Player actor = getActor();
		//check if target is in 1000 range
		
		if(canUseSkill(SKILL_Dissolve, actor, 0))
		{
			tryCastSkill(SKILL_Dissolve, actor, 0);
			return;
		}
		
		if(canUseSkill(SKILL_Fatal_Sleep, attacker, 0))
		{
			tryCastSkill(SKILL_Fatal_Sleep, attacker, 0);
			return;
		}
		
	}
	
	@Override
	protected void onEvtClanAttacked(Creature attacked, Creature attacker, int damage)
	{
		super.onEvtClanAttacked(attacked, attacker, damage);
		
		if(!attacked.isPlayer())
			return;
		
		Player member = attacked.getPlayer();
		
		if(ClassFunctions.isTanker(member))
			return;
		
		if(member.getCurrentHpPercents() > 80)
			return;
		
		// btw, your task is not protect member, you should protect your self
		
//		if(canUseSkill(SKILL_Fatal_Sleep, attacker, 0))
//		{
//			tryCastSkill(SKILL_Fatal_Sleep, attacker, 0);
//			return;
//		}
		
	}
	
	
	
}

