package npc.model;

import java.util.StringTokenizer;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.events.impl.ChaosFestivalEvent;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;

/**
 * @author Bonux
 */
public class MysteriousLackeyInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	private static final long PET_PRICE = 8;
	private static final int[] PET_ITEM_IDS = { 34905, 34906, 34907 };

	public MysteriousLackeyInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();
		if(cmd.equals("chaosfestival"))
		{
			if(!st.hasMoreTokens())
				return;

			String cmd2 = st.nextToken();
			if(cmd2.equals("rewardpet"))
			{
				if(ItemFunctions.getItemCount(player, ChaosFestivalEvent.MYSTERIOUS_MARK_ITEM_ID) >= PET_PRICE)
				{
					ItemFunctions.removeItem(player, ChaosFestivalEvent.MYSTERIOUS_MARK_ITEM_ID, PET_PRICE, true);
					ItemFunctions.addItem(player, PET_ITEM_IDS[Rnd.get(PET_ITEM_IDS.length)], 1, true);

					showChatWindow(player, "default/" + getNpcId() + "-rewarded.htm");
				}
				else
					showChatWindow(player, "default/" + getNpcId() + "-no_reward.htm");
			}
			else if(cmd2.equals("bestclan"))
			{
				//TODO: [Bonux] Реализовать.
				showChatWindow(player, "default/" + getNpcId() + "-no_best_clan.htm");
			}
		}
		else
			super.onBypassFeedback(player, command);
	}
}
