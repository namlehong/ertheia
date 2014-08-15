package l2s.gameserver.handler.admincommands.impl;

import java.util.List;

import l2s.gameserver.data.xml.holder.EventHolder;
import l2s.gameserver.handler.admincommands.IAdminCommandHandler;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.events.impl.FightBattleEvent;
import l2s.gameserver.network.l2.s2c.NpcHtmlMessagePacket;

public class AdminEvents implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_events,
		admin_fb_event
	}

	@Override
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().IsEventGm)
			return false;

		switch(command)
		{
			case admin_events:
				if(wordList.length == 1)
					activeChar.sendPacket(new NpcHtmlMessagePacket(5).setFile("admin/events/events.htm"));
				else
					activeChar.sendPacket(new NpcHtmlMessagePacket(5).setFile("admin/events/" + wordList[1].trim()));
				break;
			case admin_fb_event:
				if(wordList[1].equalsIgnoreCase("start"))
				{
					List<FightBattleEvent> events = EventHolder.getInstance().getEvents(FightBattleEvent.class);
					if(events.isEmpty())
					{
						activeChar.sendMessage("Event not found!");
						break;
					}
					events.get(0).forceStartEvent();
				}
				else if(wordList[1].equalsIgnoreCase("stop"))
					activeChar.sendMessage("Event can not be stopped!");
				break;
		}

		return true;
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}