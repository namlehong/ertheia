package blood.ai.impl;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;

public class FPCDreadnought extends WarriorPC
{
	public static final int SKILL_WARCRY = 78;
	
	public FPCDreadnought(Player actor)
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
		
		if(hpLevel < 70)
			//cast Battle Roar
			tryCastSkill(121, actor);
		
		if(hpLevel < 30)
			//Final Fenzy
			tryCastSkill(290, actor); 
		
		if(hpLevel < 10)
			//cast Revival
			tryCastSkill(181, actor);
		
	}
	
	public void prepareSkillsSetup()
	{
		//skill 2nd
		_allowSkills.add(920);	//Power Crush
		_allowSkills.add(36);	//Whirlwind
		//_allowSkills.add(121);	//Battle Roar
		_allowSkills.add(48);	//Thunder storm
		//_allowSkills.add(286);	//Provoke
		_allowSkills.add(116);	//Howl
		//_allowSkills.add(130);	//Thrill Fight
		_allowSkills.add(287);	//Lionheart
		//_allowSkills.add(181);	//Revival
		_allowSkills.add(452);	//Shock Stomp
		_allowSkills.add(421);	//Fell Swoop
				
		//skill 3rd
		_allowSkills.add(921);	//Cursed Pierce
		_allowSkills.add(361);	//Shock Blast
		_allowSkills.add(347);	//Earthquake
		_allowSkills.add(440);	//Brave Heart
		_allowSkills.add(360);	//Eye of Slayer
		_allowSkills.add(457);	//Symbol of Honor
		_allowSkills.add(774);	//Dread Pool
		
		super.prepareSkillsSetup();
	}
	

}

