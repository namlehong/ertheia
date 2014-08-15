package npc.model;

import java.util.StringTokenizer;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.olympiad.Olympiad;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExResponseBeautyListPacket;
import l2s.gameserver.network.l2.s2c.ExResponseResetList;
import l2s.gameserver.network.l2.s2c.ExShowBeautyMenuPacket;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Bonux
 */
public class BeautyShopInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public BeautyShopInstance(int objectId, NpcTemplate template)
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
		if(cmd.equals("beautyshop"))
		{
			if(!st.hasMoreTokens())
				return;

			if(Olympiad.isRegisteredInComp(player))
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_USE_THE_BEAUTY_SHOP_WHILE_REGISTERED_IN_THE_OLYMPIAD);
				return;
			}

			if(player.isRegisteredInChaosFestival())
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_USE_THE_BEAUTY_SHOP_WHILE_REGISTERED_IN_THE_CEREMONY_OF_CHAOS);
				return;
			}

			String cmd2 = st.nextToken();
			if(cmd2.equals("change"))
			{
				player.block();
				player.sendPacket(ExShowBeautyMenuPacket.CHANGE_STYLE_PACKET);
				player.sendPacket(new ExResponseBeautyListPacket(player, ExResponseBeautyListPacket.HAIR_LIST));
				return;
			}
			else if(cmd2.equals("cancel"))
			{
				if(player.getBeautyHairStyle() > 0 || player.getBeautyFace() > 0)
				{
					player.block();
					player.sendPacket(ExShowBeautyMenuPacket.CANCEL_STYLE_PACKET);
					player.sendPacket(new ExResponseResetList(player));
					return;
				}
				showChatWindow(player, "default/" + getNpcId() + "-no_cancel.htm");
			}
		}
		else
			super.onBypassFeedback(player, command);
	}
}
