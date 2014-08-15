package npc.model;

import instances.Tauti;

import l2s.gameserver.instancemanager.SoHManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.NpcHtmlMessagePacket;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.ReflectionUtils;

/**
 * @author KilRoy & Bonux
 * FIXME[K] - Deprecated method getMembers()
 */
public class AkuInstance extends NpcInstance
{
	private static final long serialVersionUID = -5672768757660962094L;

	private static final int TAUTI_EXTREME_INSTANCE_ID = 219;
	private static final Location TAUTI_ROOM_TELEPORT = new Location(-147262, 211318, -10040);

	public AkuInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.startsWith("request_tauti_extreme_battle"))
		{
			if(SoHManager.getCurrentStage() != 2)
			{
				showChatWindow(player, "tauti_keeper/sofa_aku002h.htm");
				return;
			}
			if(player.getParty() == null)
			{
				player.sendPacket(new SystemMessage(SystemMessage.ONLY_A_PARTY_LEADER_CAN_TRY_TO_ENTER));
				return;
			}
			if(player.getParty().getCommandChannel() == null)
			{
				showChatWindow(player, "tauti_keeper/sofa_aku002e.htm");
				return;
			}
			if(!player.getParty().getCommandChannel().isLeaderCommandChannel(player))
			{
				showChatWindow(player, "tauti_keeper/sofa_aku002d.htm");
				return;
			}

			int channelMemberCount = player.getParty().getCommandChannel().getMemberCount();
			if(channelMemberCount > 35 || channelMemberCount < 21)
			{
				showChatWindow(player, "tauti_keeper/sofa_aku002c.htm");
				return;
			}
			for(Player commandChannel : player.getParty().getCommandChannel().getMembers())
			{
				if(commandChannel.getLevel() < 97)
					showChatWindow(player, "tauti_keeper/sofa_aku002b.htm");
				return;
			}

			Reflection reflection = player.getActiveReflection();
			if(reflection != null)
			{
				if(player.canReenterInstance(TAUTI_EXTREME_INSTANCE_ID))
					showChatWindow(player, "tauti_keeper/sofa_aku002g.htm");
			}
			else if(player.canEnterInstance(TAUTI_EXTREME_INSTANCE_ID))
			{
				ReflectionUtils.enterReflection(player, new Tauti(), TAUTI_EXTREME_INSTANCE_ID);
				showChatWindow(player, "tauti_keeper/sofa_aku002a.htm");
			}
		}
		if(command.startsWith("reenter_tauti_extreme_battle"))
		{
			Reflection reflection = player.getActiveReflection();
			if(reflection != null)
			{
				if(player.canReenterInstance(TAUTI_EXTREME_INSTANCE_ID))
				{
					Tauti instance = (Tauti) reflection;
					if(instance.getInstanceStage() == 2)
						player.teleToLocation(TAUTI_ROOM_TELEPORT, reflection);
					else
						player.teleToLocation(reflection.getTeleportLoc(), reflection);
					showChatWindow(player, "tauti_keeper/sofa_aku002f.htm");
				}
			}
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		player.sendPacket(new NpcHtmlMessagePacket(player, this, "tauti_keeper/sofa_aku001.htm", val));
		return;
	}
}