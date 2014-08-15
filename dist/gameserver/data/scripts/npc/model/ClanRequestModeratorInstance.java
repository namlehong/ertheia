package npc.model;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Bonux
 */
public final class ClanRequestModeratorInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public ClanRequestModeratorInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... replace)
	{
		if(val == 0)
		{
			if(player.isLangRus())
				player.sendMessage("Система клановых заказов в стадии реализации.");
			else
				player.sendMessage("Clan request system in progress.");
			return;
		}
		super.showChatWindow(player, val, replace);
	}
}