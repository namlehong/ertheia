package npc.model;

import instances.SpaciaNormal;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cruel
 */
public final class PortalCubeInstance extends NpcInstance
{
	Map<Integer, Integer> players = new HashMap<Integer, Integer>();
	public PortalCubeInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equalsIgnoreCase("register"))
		{
			players.put(player.getObjectId(), player.getObjectId());
		} else if (command.equalsIgnoreCase("exit"))
		{
			for(Player p : ((SpaciaNormal) getReflection()).getPlayers()) {
				if (players.get(p.getObjectId()) == null)
					return;
				players.clear();
				((SpaciaNormal) getReflection()).SecondRoom();
			}
		}
		else if (command.equalsIgnoreCase("opengate"))
		{
			if (getNpcId() == 32951)
				((SpaciaNormal) getReflection()).openGate(26190001);
			else if (getNpcId() == 32952)
				((SpaciaNormal) getReflection()).openGate(26190006);
			else if (getNpcId() == 32953)
				((SpaciaNormal) getReflection()).openGate(26190005);
		}
		else if (command.equalsIgnoreCase("stage_third"))
		{
				((SpaciaNormal) getReflection()).thirdStage();
		}
		else if (command.equalsIgnoreCase("spawn_spezion"))
		{
				((SpaciaNormal) getReflection()).spazionSpawn();
		}
		else
			super.onBypassFeedback(player, command);
	}
}