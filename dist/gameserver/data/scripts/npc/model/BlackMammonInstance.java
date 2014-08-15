package npc.model;

import java.util.StringTokenizer;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.MerchantInstance;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.templates.item.ItemTemplate;

/**
 * @author Bonux
 */
public final class BlackMammonInstance extends MerchantInstance
{
	private static final long serialVersionUID = 1L;

	// Итемы.
	private static final int ANCIENT_ADENA_ID = 5575;
	private static final int ADENA_ID = ItemTemplate.ITEM_ID_ADENA;

	public BlackMammonInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		StringTokenizer st = new StringTokenizer(command, " ");
		String cmd = st.nextToken();
		if(cmd.equalsIgnoreCase("exchange_aa"))
		{
			if(!st.hasMoreTokens())
				return;

			int count = Integer.parseInt(st.nextToken());
			if(player.getInventory().destroyItemByItemId(ANCIENT_ADENA_ID, count))
			{
				player.addAdena(count);
				player.sendPacket(SystemMessagePacket.removeItems(ANCIENT_ADENA_ID, count));
				player.sendPacket(SystemMessagePacket.obtainItems(ADENA_ID, count, 0));
			}
		}
		else
			super.onBypassFeedback(player, command);
	}
}