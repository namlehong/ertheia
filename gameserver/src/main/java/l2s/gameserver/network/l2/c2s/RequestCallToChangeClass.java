package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExCallToChangeClass;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;

public class RequestCallToChangeClass extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		//Trigger
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.getClassId().isOfRace(Race.ERTHEIA))
			return;

		if(activeChar.getVarBoolean("GermunkusUSM"))
			return;

		int _cId = 0;
		for(ClassId classId : ClassId.VALUES)
		{
			if(!classId.isOutdated() && classId.isOfLevel(ClassLevel.AWAKED) && activeChar.getClassId().childOf(classId))
			{
				_cId = classId.getId();
				break;
			}
		}

		if(activeChar.isDead())
		{
			sendPacket(new ExShowScreenMessage(NpcString.YOU_CANNOT_TELEPORT_WHILE_YOU_ARE_DEAD, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false));
			sendPacket(new ExCallToChangeClass(_cId, false));
			return;
		}

		activeChar.processQuestEvent("_10338_SeizeYourDestiny", "MemoryOfDisaster", null);
	}
}