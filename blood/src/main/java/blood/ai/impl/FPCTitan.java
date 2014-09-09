package blood.ai.impl;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;

public class FPCTitan extends WarriorPC
{
	public FPCTitan(Player actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		super.onEvtAttacked(attacker, damage);
		Player actor = getActor();
		if(actor.isDead() || attacker == null || actor.getDistance(attacker) > 700)
			return;

		if(actor.isMoving)
			return;
		
		int hpLevel	= (int) actor.getCurrentHpPercents();
		
		if(hpLevel < 30)
		{
//			try
//			{
//				//cast Guts
//				if(!actor.getEffectList().containEffectFromSkills(new int[] { 139 }))
//					selfBuff(139);
//				
//				//cast Frenzy
//				if(!actor.getEffectList().containEffectFromSkills(new int[] { 176 }))
//					selfBuff(176);
//				
//				//cast Zealot
//				if(!actor.getEffectList().containEffectFromSkills(new int[] { 420 }))
//					selfBuff(420);
//			}
//			catch(Exception e){}
			
			if(hpLevel < 10)
			{
				try
				{
					//cast Battle Roar
					tryCastSkill(121, actor);
				}
				catch(Exception e){}
			}
		}
		
	}
	
	public void prepareSkillsSetup()
	{
		//skill 2nd
		_allowSkills.add(190);	//Fatal Strike
		//_allowSkills.add(36);	//Whirlwind
		_allowSkills.add(121);	//Battle Roar
		_allowSkills.add(260);	//Hammer Crush
		_allowSkills.add(287);	//Lionheart
		_allowSkills.add(94);	//Rage
		_allowSkills.add(315);	//Crush of Doom
		_allowSkills.add(320);	//Wrath
		_allowSkills.add(994);	//Rush
		
		//skill 3rd
		_allowSkills.add(362);	//Armor Crush
		_allowSkills.add(347);	//Earthquake
		_allowSkills.add(440);	//Brave Heart
		_allowSkills.add(356);	//Over the Body
		_allowSkills.add(456);	//Symbol of Resistance
		_allowSkills.add(917);	//Final Secret
		_allowSkills.add(758);	//Fighter Will
		
		
		super.prepareSkillsSetup();
	}
	

}
