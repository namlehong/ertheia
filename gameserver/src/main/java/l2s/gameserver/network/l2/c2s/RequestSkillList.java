package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.SkillListPacket;

public final class RequestSkillList extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		// this is just a trigger packet. it has no content
	}

	@Override
	protected void runImpl()
	{
		Player cha = getClient().getActiveChar();

		if(cha != null)
			cha.sendPacket(new SkillListPacket(cha));
	}
}
