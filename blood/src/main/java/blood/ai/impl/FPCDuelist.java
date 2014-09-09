package blood.ai.impl;

import l2s.gameserver.model.Player;

public class FPCDuelist extends WarriorPC
{
	public static final int SKILL_MAJESTY = 78;
	public static final int SKILL_DEFKECT_ARROW = 287;
	
	public FPCDuelist(Player actor)
	{
		super(actor);
	}
	
	public void prepareSkillsSetup()
	{
		
		//skill 2nd
		_allowSkills.add(190);	//Fatal Strike
		_allowSkills.add(8);	//Sonic Focus
		_allowSkills.add(1);	//Tripple Slash
		//_allowSkills.add(260);	//Hammer Crush
		//_allowSkills.add(6);	//Sonic Blaster
		//_allowSkills.add(9);	//Sonic Buster
		//_allowSkills.add(78);	//War Cry
		_allowSkills.add(5);	//Double Sonic Slash
		_allowSkills.add(7);	//Sonic Storm
		//_allowSkills.add(287);	//Lion Heart
		_allowSkills.add(261);	//Triple Sonic Slash
		//_allowSkills.add(424);	//War Frenzy
		_allowSkills.add(451);	//Sonic Move
		_allowSkills.add(297);	//Duelist Spirit
		
		//skill 3rd
		_allowSkills.add(340);	//Riposte Stance
		_allowSkills.add(345);	//Sonic Rage
		_allowSkills.add(440);	//Braveheart
		_allowSkills.add(360);	//Eye of Slayer
		_allowSkills.add(442);	//Sonic Barrier
		_allowSkills.add(458);	//Symbol of Energy
		_allowSkills.add(917);	//Final Secret
		_allowSkills.add(775);	//Weapon Blockade
		_allowSkills.add(758);	//Fighter Will
		_allowSkills.add(919);	//Maximum Sonic Focus
		
		super.prepareSkillsSetup();
	}
	
}

