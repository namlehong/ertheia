package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.ExShape_Shifting_Result;

/**
 * @author Bonux
**/
public class RequestExCancelShapeShiftingItem extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		//
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null)
			return;

		player.setAppearanceStone(null);
		player.setAppearanceExtractItem(null);
		player.sendPacket(ExShape_Shifting_Result.FAIL);
	}
}