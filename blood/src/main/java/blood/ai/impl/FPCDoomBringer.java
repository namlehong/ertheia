package blood.ai.impl;

import java.util.ArrayList;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.tables.SkillTable;

public class FPCDoomBringer extends WarriorPC
{
	public FPCDoomBringer(Player actor)
	{
		super(actor);
		//skill 2nd
		_allowSkills.add(477);	//Dark Smash
		_allowSkills.add(494);	//Shoulder Charge
		_allowSkills.add(485);	//Disarm
		//_allowSkills.add(503);	//Scorn
		_allowSkills.add(496);	//Slashing Blade
		_allowSkills.add(493);	//Storm Assault
		_allowSkills.add(492);	//Spead Wing
		//_allowSkills.add(483);	//Sword Shield
		_allowSkills.add(501);	//Violent Temper
		_allowSkills.add(495);	//Blade Rush
		_allowSkills.add(497);	//Crushing Pain
		_allowSkills.add(498);	//Contagion
		
		
		//skill 3rd
		_allowSkills.add(939);	//Soul Rage
		_allowSkills.add(526);	//Enuma Elish
		_allowSkills.add(793);	//Rush Impact
		_allowSkills.add(358);	//Final Form
		_allowSkills.add(794);	//Mass Disarm
		_allowSkills.add(917);	//Final Secret
		_allowSkills.add(948);	//Eye for Eye
		
	}

	@Override
	protected boolean thinkBuff()
	{
		if(thinkUseKamaelSoul(502, 10))
			return true;
		
		return super.thinkBuff();
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
		
		if(hpLevel < 40)
		{
			try
			{
				//cast Sword Shield
				actor.doCast(SkillTable.getInstance().getInfo(483, 1), actor, false);
			}
			catch(Exception e){}
		}
	}
	
	@Override
	protected void onEvtClanAttacked(Creature attacked, Creature attacker, int damage)
	{
		Player actor = getActor();
		//check if target is in 1000 range
		if(!attacked.isInRange(actor.getLoc(), 1000)) 
			return; 
		
		//check target critical level, base on HP level
		int hpLevel 					= (int) attacked.getCurrentHpPercents();
		ArrayList<Skill>	SkillList;
		//take action
		if(hpLevel < 80)
		{
			SkillList = getDrawTargetSkill();
			
			actor.doCast(SkillList.get(Rnd.get(SkillList.size())), attacked, false);
		}
	}
	
	protected ArrayList<Skill> getDrawTargetSkill()
	{
		ArrayList<Skill>	SkillList	= new ArrayList<Skill>();
		
		SkillList.add(SkillTable.getInstance().getInfo(503, 1)); //Scorn
		
		return SkillList;
	}
	
}

