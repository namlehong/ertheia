package l2s.gameserver.model.instances;

import java.util.StringTokenizer;

import l2s.gameserver.model.Player;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.Log;
import l2s.gameserver.utils.WarehouseFunctions;

public class WarehouseInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public WarehouseInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public String getHtmlPath(int npcId, int val, Player player)
	{
		String pom = "";
		if(val == 0)
			pom = "" + npcId;
		else
			pom = npcId + "-" + val;

		if(getTemplate().getHtmRoot() != null)
			return getTemplate().getHtmRoot() + pom + ".htm";
		else
			return "warehouse/" + pom + ".htm";
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(player.getEnchantScroll() != null)
		{
			Log.add("Player " + player.getName() + " trying to use enchant exploit[Warehouse], ban this player!", "illegal-actions");
			player.setEnchantScroll(null);
			return;
		}

		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();
		if(cmd.equalsIgnoreCase("WithdrawP"))
			WarehouseFunctions.showRetrieveWindow(player);
		else if(cmd.equalsIgnoreCase("DepositP"))
			WarehouseFunctions.showDepositWindow(player);
		else if(cmd.equalsIgnoreCase("WithdrawC"))
			WarehouseFunctions.showWithdrawWindowClan(player);
		else if(cmd.equalsIgnoreCase("DepositC"))
			WarehouseFunctions.showDepositWindowClan(player);
		else
			super.onBypassFeedback(player, command);
	}
}