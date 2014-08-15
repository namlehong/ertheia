package handler.bypass;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.Location;

/**
 * @author VISTALL
 * @date 16:01/12.07.2011
 */
public class TeleToFantasyIsle extends ScriptBypassHandler
{
	public static final Location[] POINTS =
	{
			new Location(-60695, -56896, -2032),
			new Location(-59716, -55920, -2032),
			new Location(-58752, -56896, -2032),
			new Location(-59716, -57864, -2032)
	};

	@Override
	public String[] getBypasses()
	{
		return new String[] {"teleToFantasyIsle"};
	}

	@Override
	public void onBypassFeedback(NpcInstance npc, Player player, String command)
	{
		player.teleToLocation(Rnd.get(POINTS));
	}
}
