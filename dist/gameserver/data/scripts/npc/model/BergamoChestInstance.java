package npc.model;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.NpcHtmlMessagePacket;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Kolobrodik
 * @date 16:21/16.06.13
 */
public class BergamoChestInstance extends NpcInstance
{
    private static final int[] _B_Grade_Drop = { 947, 948, 6571, 6572 };
    private static final int[] _A_Grade_Drop = { 6569, 6570, 729, 730 };
    private static final int[] _S_Grade_Drop = { 6577, 6578, 959, 960 };
    private static final int[] _R_Grade_Drop = { 19447, 19448, 17526, 17527 };
    private static final int[] _Stones_Drop = { 9546, 9547, 9548, 9549, 9550, 9551, 9552, 9553, 9554, 9555, 9556, 9557 };

    public BergamoChestInstance(int objectId, NpcTemplate template)
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
        if ((command.equalsIgnoreCase("start")) && (player.getVar("BerOpen") == null))
        {
            player.setVar("BerOpen", "true", System.currentTimeMillis() + 180000L);
            int rewardId = rollDrop(getReflection().getInstancedZoneId());
            if (rewardId > 0)
				Functions.addItem(player, rewardId, 1);
                //dropItem(player, rewardId, 1);
			doDie(null);	
        }
        else
        {
            super.onBypassFeedback(player, command);
        }
    }

    @Override
    public void showChatWindow(Player player, int val, Object... replace)
    {
        if (player.getVar("BerOpen") != null)
        {
			showChatWindow(player, "openBergamoChest.htm");
			return;
        }
        super.showChatWindow(player, val, replace);
    }

    private int rollDrop(int id)
    {
        switch (id)
        {
            case 212:
                return _Stones_Drop[Rnd.get(0,_Stones_Drop.length)];
            case 213:
                return Rnd.nextBoolean() ? _B_Grade_Drop[Rnd.get(0,_B_Grade_Drop.length - 1)] : _Stones_Drop[Rnd.get(0,_Stones_Drop.length - 1)];
            case 214:
                return Rnd.nextBoolean() ? _A_Grade_Drop[Rnd.get(0,_A_Grade_Drop.length - 1)] : _Stones_Drop[Rnd.get(0,_Stones_Drop.length - 1)];
            case 215:
                return Rnd.nextBoolean() ? _S_Grade_Drop[Rnd.get(0,_S_Grade_Drop.length - 1)] : _Stones_Drop[Rnd.get(0,_Stones_Drop.length - 1)];
            case 216:
                return Rnd.nextBoolean() ? _R_Grade_Drop[Rnd.get(0,_R_Grade_Drop.length - 1)] : _Stones_Drop[Rnd.get(0,_Stones_Drop.length - 1)];
            default:
                return 0;
        }
    }
}
