package blood.ai.impl;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;

public class FPCGrandKhauatari extends WarriorPC
{
	public FPCGrandKhauatari(Player actor)
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

			//cast Zealot
			tryCastSkill(420, actor);
		}
		
	}
	
	public List<Integer> getAllowSkill()
	{
		List<Integer> SkillList = new ArrayList<Integer>();
		
		//skill 2nd
		_allowSkills.add(280);	//Burning Fist
		//_allowSkills.add(50);	//Focused Force
		_allowSkills.add(54);	//Force Blaster
		_allowSkills.add(284);	//Hurricane Assault
		_allowSkills.add(95);	//Cripple
		_allowSkills.add(282);	//Puma Spirit Totem
		_allowSkills.add(281);	//Soul Breaker
		//_allowSkills.add(17);	//Force Burst
		//_allowSkills.add(35);	//Force Storm
		//_allowSkills.add(420);	//Zealot
		_allowSkills.add(81);	//Punch of Doom
		_allowSkills.add(425);	//Hawk Spirit Totem
		
		//skill 3rd
		_allowSkills.add(346);	//Ranging Force
		_allowSkills.add(443);	//Force Barrier
		_allowSkills.add(458);	//Symbol of Energy
		_allowSkills.add(917);	//Final Secret
		_allowSkills.add(758);	//Fighter Will
		_allowSkills.add(776);	//Force Of Destruction
		_allowSkills.add(918);	//Maximum Force Focus
		
		return SkillList;
	}
	
}

