package l2s.gameserver.network.l2.c2s;

import java.util.List;

import l2s.gameserver.instancemanager.WorldStatisticsManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.worldstatistics.CharacterStatisticElement;
import l2s.gameserver.network.l2.s2c.ExLoadStatUser;

/**
 * @author ALF
 * @modified KilRoy
 * @data 08.08.2012
 */
public class RequestUserStatistics extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		//
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		/* TODO: [Bonux] Пересмотреть.
		List<CharacterStatisticElement> stat = WorldStatisticsManager.getInstance().getCurrentStatisticsForPlayer(activeChar.getObjectId());
		activeChar.sendPacket(new ExLoadStatUser(stat));
		*/
	}
}