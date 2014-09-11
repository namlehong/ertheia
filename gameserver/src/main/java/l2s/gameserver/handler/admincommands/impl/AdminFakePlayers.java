package l2s.gameserver.handler.admincommands.impl;

import java.util.StringTokenizer;

import l2s.gameserver.data.xml.holder.FakePlayersHolder;
import l2s.gameserver.handler.admincommands.IAdminCommandHandler;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.FakePlayerInstance;
import l2s.gameserver.templates.npc.FakePlayerTemplate;

/**
 * @author Bonux
**/
public class AdminFakePlayers implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_spawn_fp
	}

	@Override
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.isGM())
			return false;

		StringTokenizer st;
		switch(command)
		{
			case admin_spawn_fp:
				st = new StringTokenizer(fullString, " ");
				try
				{
					st.nextToken();
					spawnFakePlayer(activeChar, Integer.parseInt(st.nextToken()));
				}
				catch(Exception e)
				{
					// Case of wrong monster data
				}
				break;
		}
		return true;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	private void spawnFakePlayer(Player activeChar, int id)
	{
		GameObject target = activeChar.getTarget();
		if(target == null)
			target = activeChar;

		FakePlayerTemplate template = FakePlayersHolder.getInstance().getTemplate(id);
		if(template == null)
		{
			activeChar.sendMessage("Incorrect fake player template.");
			return;
		}

		if(template.getSpawned() != null)
		{
			activeChar.sendMessage("Fail! Fake player already spawned in other location!");
			return;
		}

		try
		{
			FakePlayerInstance fake = template.getNewInstance();
			fake.setCurrentHp(fake.getMaxHp(), false);
			fake.setCurrentMp(fake.getMaxMp());
			fake.setHeading(activeChar.getHeading());
			fake.setReflection(activeChar.getReflection());
			fake.spawnMe(activeChar.getLoc());
			activeChar.sendMessage("Created " + template.name + " on " + target.getObjectId() + ".");
		}
		catch(Exception e)
		{
			activeChar.sendMessage("Target is not ingame.");
		}
	}
}