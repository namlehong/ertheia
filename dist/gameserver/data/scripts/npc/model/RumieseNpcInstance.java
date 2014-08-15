package npc.model;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ReflectionUtils;

import instances.Isthina;

/**
 * Для работы с инстами - истхины
 * У НПСов 33151, 33293 (Rumiese)
 * TODO[K] - Раскоментить всё если допишу инст+Аи хард мода Истхины + Сообразить награду при её смерти
 */
public final class RumieseNpcInstance extends NpcInstance
{
	private static final long serialVersionUID = 5984176213940365077L;

	private static final int NORMAL_MODE_IZ_ID = 169;
	private static final int HARD_MODE_IZ_ID = 170;

	public RumieseNpcInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;
		
		if(command.equalsIgnoreCase("request_normalisthina"))
		{
			Reflection r = player.getActiveReflection();
			if (r != null)
			{
				if(player.canReenterInstance(NORMAL_MODE_IZ_ID))
					player.teleToLocation(-177104, 146452, -11389, r);
			}
			else if(player.canEnterInstance(NORMAL_MODE_IZ_ID))
				ReflectionUtils.enterReflection(player, new Isthina(), NORMAL_MODE_IZ_ID);
		}
		else if(command.equalsIgnoreCase("request_hardisthina"))
		{
			Reflection r = player.getActiveReflection();
			if(r != null)
			{
				if(player.canReenterInstance(HARD_MODE_IZ_ID))
					player.teleToLocation(-177104, 146452, -11389, r);
			}
			else if(player.canEnterInstance(HARD_MODE_IZ_ID))
				ReflectionUtils.enterReflection(player, new Isthina(), HARD_MODE_IZ_ID);
		}
		else if(command.equalsIgnoreCase("request_takemyprize"))
		{
			if (player.getInventory().getItemByItemId(17608) == null)
			{
				player.getInventory().addItem(17608, 1L);
				showChatWindow(player, "default/33151-ok.htm");
			}
			else
				showChatWindow(player, "default/33151-no.htm");
		}
		else
			super.onBypassFeedback(player, command);
	}
}
