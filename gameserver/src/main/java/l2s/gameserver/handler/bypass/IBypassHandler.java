package l2s.gameserver.handler.bypass;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;

/**
 * @author VISTALL
 * @date 15:54/12.07.2011
 */
public interface IBypassHandler
{
	String[] getBypasses();

	void onBypassFeedback(NpcInstance npc, Player player, String command);
}
