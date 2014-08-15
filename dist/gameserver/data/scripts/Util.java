import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.utils.Location;

public class Util extends Functions
{
	public void PayPage(String[] param)
	{
		if(param.length < 2)
			throw new IllegalArgumentException();

		Player player = getSelf();
		if(player == null)
			return;

		String page = param[0];
		int item = Integer.parseInt(param[1]);
		long price = Long.parseLong(param[2]);

		if(getItemCount(player, item) < price)
		{
			player.sendPacket(item == 57 ? SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA : SystemMsg.INCORRECT_ITEM_COUNT);
			return;
		}

		removeItem(player, item, price);
		show(page, player);
	}

	public void MakeEchoCrystal(String[] param)
	{
		if(param.length < 2)
			throw new IllegalArgumentException();

		Player player = getSelf();
		if(player == null)
			return;

		if(!NpcInstance.canBypassCheck(player, player.getLastNpc()))
			return;

		int crystal = Integer.parseInt(param[0]);
		if(crystal < 4411 || crystal > 4417)
			return;

		int score = Integer.parseInt(param[1]);

		if(getItemCount(player, score) == 0)
		{
			player.getLastNpc().onBypassFeedback(player, "Chat 1");
			return;
		}

		if(getItemCount(player, 57) < 200)
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return;
		}

		removeItem(player, 57, 200);
		addItem(player, crystal, 1);
	}

	public void enter_dc()
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;

		if(!NpcInstance.canBypassCheck(player, npc))
			return;

		player.setVar("DCBackCoords", player.getLoc().toXYZString(), -1);
		player.teleToLocation(-114582, -152635, -6742);
	}

	public void exit_dc()
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;

		if(!NpcInstance.canBypassCheck(player, npc))
			return;

		String var = player.getVar("DCBackCoords");
		if(var == null || var.isEmpty())
		{
			player.teleToLocation(new Location(43768, -48232, -800), 0);
			return;
		}
		player.teleToLocation(Location.parseLoc(var), 0);
		player.unsetVar("DCBackCoords");
	}
}