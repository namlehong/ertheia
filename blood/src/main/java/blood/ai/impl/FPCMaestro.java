package blood.ai.impl;

import l2s.gameserver.model.Player;

public class FPCMaestro extends WarriorPC
{
	public FPCMaestro(Player actor)
	{
		super(actor);
		//skill 2nd
		_allowSkills.add(190);	//Fatal Strike
		//_allowSkills.add(36);	//Whirlwind
		_allowSkills.add(260);	//Hammer Crush
		_allowSkills.add(320);	//Wrath
		_allowSkills.add(994);	//Rush
		_allowSkills.add(25);	//Summon mechanic golem
		//_allowSkills.add(301);	//Summon big boom
		
		//skill 3rd
		_allowSkills.add(362);	//Armor Crush
		_allowSkills.add(347);	//Earthquake
		_allowSkills.add(457);	//Symbol of Honnor
		_allowSkills.add(917);	//Final Secret
		_allowSkills.add(995);	//Rush Impact
		
		// buff
		_allowSkills.add(1561); //Battle cry
		_allowSkills.add(826); //Spike
		_allowSkills.add(440); // Braveheart
		_allowSkills.add(778); // golden armor
	}
	
}
