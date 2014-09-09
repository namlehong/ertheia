package blood.utils;

import java.util.ArrayList;

import blood.data.holder.NamePatternHolder;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;

public class ClassFunctions {

	public static boolean tryUpClass(Player player)
	{
		if(player == null)
			return false;
		
		ArrayList<ClassId> classList = new ArrayList<ClassId>();
		ArrayList<ClassId> dreamList = new ArrayList<ClassId>();
		
		ClassLevel nextClassLevel = null;
		ClassId currentClassId = player.getClassId();
		ClassId nextClassId = null;
		String name = player.getName();
		
		if (player.getLevel() >= 20 && player.getClassId().isOfLevel(ClassLevel.NONE))
			nextClassLevel = ClassLevel.FIRST;
		
		if (player.getLevel() >= 40 && player.getClassId().isOfLevel(ClassLevel.FIRST))
			nextClassLevel = ClassLevel.SECOND;
		
		if (player.getLevel() >= 76 && player.getClassId().isOfLevel(ClassLevel.SECOND))
			nextClassLevel = ClassLevel.THIRD;
			
		if (player.getLevel() >= 85 && player.getClassId().isOfLevel(ClassLevel.THIRD))
			nextClassLevel = ClassLevel.AWAKED;
		
		if(nextClassLevel == null)
			return false;
		
		for(ClassId classid: ClassId.VALUES)
		{
			if(!classid.isOfLevel(nextClassLevel))
				continue;
			
			if(!classid.childOf(currentClassId))
				continue;
			
			if(classid == ClassId.JUDICATOR || classid == ClassId.INSPECTOR)
				continue;
			
			if(classid.getId() > 138 && classid.getId() < 147) // remove old GOD 4th class
				continue;
			
			if(NamePatternHolder.checkName(name, classid))
				dreamList.add(classid);
			
			classList.add(classid);
		}
		
		if(classList.size() <= 0)
			return false;
		
		if(classList.size() == 1) // apply for 3rd and 4th
		{
			// look like we have no choice
			nextClassId = classList.get(0);
		}
		else if(dreamList.size() == 1)
		{
			nextClassId = dreamList.get(0);
		}
		else if(dreamList.size() > 1)
		{
			nextClassId = dreamList.get(Rnd.get(dreamList.size()));
		}
		else
		{
			nextClassId = classList.get(Rnd.get(classList.size()));
		}
		
		if(nextClassId == null)
			return false;
		
		player.setClassId(nextClassId.getId(), true);
		return true;
	}

	public static boolean upClass(Player player)
	{
		if (player == null)
			return false;
		
		boolean tryUpClass = true;
		int count = 0;
		while(tryUpClass)
		{
			tryUpClass = tryUpClass(player);
			if(tryUpClass)
				count++;
		}
		
		return count > 0;
	}	

	public static boolean canPveSolo(Player player){
		// reject null
		if(player == null)
			return false;
		
		// we can solo until 3rd
		if(player.getClassLevel() < 3)
			return true;
		
		// we can't solo after level 90
		if(player.getLevel() > 90)
			return false;
		
		// 30% damage dealer go solo
		if(isDamageDealer(player))
			return Rnd.chance(30);
		
		return false;
	}

	public static boolean isTanker(Player player){
		if(player == null)
			return false;
		
		switch(player.getClassId()){
		case PHOENIX_KNIGHT:
		case HELL_KNIGHT:
		case EVAS_TEMPLAR:
		case SHILLIEN_TEMPLAR:
		case SIGEL_PHOENIX_KNIGHT:
		case SIGEL_HELL_KNIGHT:
		case SIGEL_EVAS_TEMPLAR:
		case SIGEL_SHILLIEN_TEMPLAR:
		case SIGEL_KNIGHT:
			return true;
		
		default:
			return false;
		}
	}

	public static boolean isIss(Player player){
		if(player == null)
			return false;
		
		switch(player.getClassId()){
		case HIEROPHANT:
		case SWORD_MUSE:
		case SPECTRAL_DANCER:
		case DOOMCRYER:
		case DOMINATOR:
		case ISS_HIEROPHANT:
		case ISS_SWORD_MUSE:
		case ISS_SPECTRAL_DANCER:
		case ISS_DOOMCRYER:
		case ISS_DOMINATOR:
		case ISS_ENCHANTER:
			return true;
		
		default:
			return false;
		}
	}

	public static boolean isHealer(Player player){
		if(player == null)
			return false;
		
		switch(player.getClassId()){
		case CARDINAL:
		case EVAS_SAINT:
		case SHILLIEN_SAINT:
		case AEORE_CARDINAL:
		case AEORE_EVAS_SAINT:
		case AEORE_SHILLIEN_SAINT:
			return true;
		
		default:
			return false;
		}
	}

	public static boolean isDamageDealer(Player player){
		if(player == null)
			return false;
		
		if(isTanker(player))
			return false;
		
		if(isIss(player))
			return false;
		
		if(isHealer(player))
			return false;
		
		return true;
	}

}
