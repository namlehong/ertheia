package npc.model;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.Location;

/**
 * @author Kolobrodik
 * @date 17:27/16.06.13
 */
public class BergamoInstance extends NpcInstance
{
    public BergamoInstance(int objectId, NpcTemplate template)
    {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command)
    {
        if (!canBypassCheck(player, this))
        {
            return;
        }
        if ((command.equalsIgnoreCase("start")))
        {
            getReflection().addSpawnWithoutRespawn(33693, new Location(75063, -213640, -3738, 3242), 0);
            getReflection().addSpawnWithoutRespawn(33694, new Location(74888, -213512, -3738, 32767), 0);
            getReflection().addSpawnWithoutRespawn(33695, new Location(75000, -213352, -3738, 32767), 0);
            getReflection().startCollapseTimer(3 * 60000L);
            player.sendPacket(new SystemMessage(SystemMsg.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(3));
			this.deleteMe(); //must
        }

    }
}
