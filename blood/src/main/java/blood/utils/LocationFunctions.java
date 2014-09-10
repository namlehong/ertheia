package blood.utils;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.utils.Location;

public class LocationFunctions {

	public static final String[][]	spawnLoc = {
//			{"87358","-141982","-1341", "Schuttgart Town Center"},
			//{"44070","-50243","-796","Rune Town Center"},
			{"82321","55139","-1529","Oren Town Center"},
//			{"116589","76268","-2734","Hunters Village Town Center"},
			//{"111115","219017","-3547","Heine Town Center"},
			//{"147725","-56517","-2780","Goddard Town Center"},
			//{"147705","-53066","-2731","Goddard Einhasad Temple"},
			{"-14225","123540","-3121","Gludio Town Center"},
//			{"-83063","150791","-3120","Gludin Town Center 1"},
//			{"-81784","150840","-3120","Gludin Town Center 2"},
			{"82698","148638","-3473","Giran Town Center"},
			{"18748","145437","-3132","Dion Town Center"},
			{"147450","27064","-2208","Aden Town Center"}
		};

	public static void randomTown(Player player)
	{
		String[] randomLoc = spawnLoc[Rnd.get(spawnLoc.length)];
		
		Location baseLoc = Location.findPointToStay(
												        Integer.parseInt(randomLoc[0]), 
												        Integer.parseInt(randomLoc[1]), 
												        Integer.parseInt(randomLoc[2]),
														100, 850, player.getGeoIndex());
		player.setLoc(baseLoc);
	}

}
