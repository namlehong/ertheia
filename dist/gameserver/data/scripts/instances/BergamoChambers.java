package instances;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;

/**
 * @author Kolobrodik
 * @date 14:56/16.06.13
 *
 * Инстанс "Покои Бергамо"
 * http://l2central.info/wiki/Пространственные_Сундуки_с_Сокровищами
 */
public class BergamoChambers extends Reflection
{

    @Override
    public void onPlayerExit(Player player)
    {
        if (player.getVar("BerOpen") != null)
        {
            player.unsetVar("BerOpen");
        }
        super.onPlayerExit(player);
    }
}
