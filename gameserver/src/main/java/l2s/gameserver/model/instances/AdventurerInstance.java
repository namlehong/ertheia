package l2s.gameserver.model.instances;

import l2s.gameserver.instancemanager.RaidBossSpawnManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Spawner;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.s2c.ExShowQuestInfoPacket;
import l2s.gameserver.network.l2.s2c.RadarControlPacket;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdventurerInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	private static final Logger _log = LoggerFactory.getLogger(AdventurerInstance.class);

	public AdventurerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equalsIgnoreCase("questlist"))
			player.sendPacket(ExShowQuestInfoPacket.STATIC);
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public String getHtmlPath(int npcId, int val, Player player)
	{
		String pom;
		if(val == 0)
			pom = "" + npcId;
		else
			pom = npcId + "-" + val;

		return "adventurer_guildsman/" + pom + ".htm";
	}
}