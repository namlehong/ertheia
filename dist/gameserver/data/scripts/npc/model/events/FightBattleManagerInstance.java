package npc.model.events;

import java.util.List;
import java.util.StringTokenizer;

import l2s.gameserver.data.xml.holder.EventHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.events.impl.FightBattleEvent;
import l2s.gameserver.model.entity.events.objects.FightBattleArenaObject;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.ExReceiveOlympiadPacket.MatchList;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Bonux
**/
public class FightBattleManagerInstance extends NpcInstance
{
	public FightBattleManagerInstance(int objectId, NpcTemplate template)
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
		if(cmd.equalsIgnoreCase("event"))
		{
			List<FightBattleEvent> events = EventHolder.getInstance().getEvents(FightBattleEvent.class);
			if(events.isEmpty())
			{
				showChatWindow(player, "events/fight_battle/" + getNpcId() + "-no_event.htm");
				return;
			}

			FightBattleEvent event = events.get(0);
			if(event == null)
			{
				showChatWindow(player, "events/fight_battle/" + getNpcId() + "-no_event.htm");
				return;
			}

			String cmd2 = st.nextToken();
			if(cmd2.equalsIgnoreCase("register"))
			{
				if(!event.isRegistrationActive())
				{
					showChatWindow(player, "events/fight_battle/" + getNpcId() + "-registration_over.htm");
					return;
				}

				if(event.isParticle(player))
				{
					showChatWindow(player, "events/fight_battle/" + getNpcId() + "-already_registered.htm");
					return;
				}

				if(!event.checkParticipationCond(player))
				{
					showChatWindow(player, "events/fight_battle/" + getNpcId() + "-no_cond.htm");
					return;
				}

				if(event.getParticlePlayers().size() >= event.getMaxParticipants())
				{
					showChatWindow(player, "events/fight_battle/" + getNpcId() + "-max_participants.htm");
					return;
				}

				if(!event.tryAddParticipant(player))
				{
					showChatWindow(player, "events/fight_battle/" + getNpcId() + "-reg_error.htm");
					return;
				}

				showChatWindow(player, "events/fight_battle/" + getNpcId() + "-succ_registered.htm");
			}
			else if(cmd2.equalsIgnoreCase("unregister"))
			{
				if(!event.isRegistrationActive())
				{
					showChatWindow(player, "events/fight_battle/" + getNpcId() + "-registration_over.htm");
					return;
				}

				if(!event.isParticle(player) || !event.removeParticipant(player))
				{
					showChatWindow(player, "events/fight_battle/" + getNpcId() + "-no_registered.htm");
					return;
				}

				showChatWindow(player, "events/fight_battle/" + getNpcId() + "-succ_unregistered.htm");
			}
			else if(cmd2.equalsIgnoreCase("observ"))
			{
				if(!event.isInProgress())
				{
					showChatWindow(player, "events/fight_battle/" + getNpcId() + "-no_event.htm");
					return;
				}

				if(!event.isAllowObserv())
				{
					showChatWindow(player, "events/fight_battle/" + getNpcId() + "-no_observ.htm");
					return;
				}

				player.sendPacket(new MatchList(event));
			}
		}
		else
			super.onBypassFeedback(player, command);
	}
}