package blood.ai.impl;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Party;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.base.ClassLevel;
import blood.ai.EventFPC;
import blood.ai.impl.FPSkills.Cardinal;
import blood.ai.impl.FPSkills.Elder;

public class HealerPC extends EventFPC
{
	public HealerPC(Player actor)
	{
		super(actor);
	}
	
	protected boolean isAllowClass()
	{
		return getActor().getClassId().isOfLevel(ClassLevel.SECOND) || getActor().getClassId().isOfLevel(ClassLevel.THIRD);
	}
	
	protected Skill getNpcSuperiorBuff()
	{
//		return getSkill(15648, 1); //tank
		if(isUseBow())
			return getSkill(15649, 1); //warrior
		else
			return getSkill(15650, 1); //wizzard
	}

	protected boolean defaultSubFightTask(Creature target)
	{
		Player player = getActor();
		
		if(player.isInParty())
			healerPartyFightTask(target);
		else
			healerSoloFightTask(target);
		
		return true;
	}
	
	@SuppressWarnings("unused")
	private boolean healerPartyFightTask(Creature target) 
	{
		Player player = getActor();
		// heal task
		Party party = player.getParty();
		
		if(party != null)
		{
			double lowestHpPercent = 100d;
			
			Player lowestHpMember = null;
			double lowestMpPercent = 100d;
			Player lowestMpMember = null;
			
			int hp75 = 0;
			int mp75 = 0;
			int hp50 = 0;
			int mp50 = 0;
			int hp25 = 0;
			int mp25 = 0;
			
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
			
			if(hp50 > 0 && canUseSkill(Cardinal.SKILL_BALANCE_LIFE, player, 0, true))
				return tryCastSkill(Cardinal.SKILL_BALANCE_LIFE, player, 0);
			
			if(hp75 >= 3 && canUseSkill(Cardinal.SKILL_MAJOR_GROUP_HEAL, player, 0))
				return tryCastSkill(Cardinal.SKILL_MAJOR_GROUP_HEAL, player, 0);
			
			if(lowestMpPercent < 70 && canUseSkill(Cardinal.SKILL_RESTORE_LIFE, lowestHpMember, player.getDistance(lowestHpMember)))
				return tryCastSkill(Cardinal.SKILL_RESTORE_LIFE, lowestHpMember, player.getDistance(lowestHpMember));
			
			if(lowestMpPercent < 80 && canUseSkill(Cardinal.SKILL_MAJOR_HEAL, lowestHpMember, player.getDistance(lowestHpMember)))
				return tryCastSkill(Cardinal.SKILL_MAJOR_HEAL, lowestHpMember, player.getDistance(lowestHpMember));
				
			if(lowestMpMember != null && lowestMpPercent < 90 && canUseSkill(Elder.SKILL_RECHARGE, lowestMpMember, 0))
				return tryCastSkill(Elder.SKILL_RECHARGE, lowestMpMember, 0);
		}
		
		return false;
	}

	
	public boolean healerSoloFightTask(Creature target)
	{
		Player player = getActor();
		
		double distance = player.getDistance(target);
		double playerHp = player.getCurrentHpPercents();
		boolean useBow = isUseBow();
		
		if(playerHp < 80 && canUseSkill(Cardinal.SKILL_MAJOR_HEAL, player))
			return tryCastSkill(Cardinal.SKILL_MAJOR_HEAL, player);
		
		if(useBow)
			return chooseTaskAndTargets(null, target, distance);
		
		if(canUseSkill(Cardinal.SKILL_DIVINE_PUNISHMENT, target, distance))
			return tryCastSkill(Cardinal.SKILL_DIVINE_PUNISHMENT, target, distance);
		
		if(canUseSkill(Cardinal.SKILL_MIGHT_OF_HEAVEN, target, distance))
			return tryCastSkill(Cardinal.SKILL_MIGHT_OF_HEAVEN, target, distance);
		
		return chooseTaskAndTargets(null, target, distance);
	}
	
}

