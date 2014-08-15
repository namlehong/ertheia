package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.Config;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ActionFailPacket;
import l2s.gameserver.tables.ClanTable;

public class RequestStartPledgeWar extends L2GameClientPacket
{
	private String _pledgeName;

	@Override
	protected void readImpl()
	{
		_pledgeName = readS(32);
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		Clan clan = activeChar.getClan();
		if(clan == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		if(!((activeChar.getClanPrivileges() & Clan.CP_CL_CLAN_WAR) == Clan.CP_CL_CLAN_WAR))
		{
			activeChar.sendActionFailed();
			return;
		}

		if(clan.getWarsCount() >= 30)
		{
			activeChar.sendPacket(SystemMsg.A_DECLARATION_OF_WAR_AGAINST_MORE_THAN_30_CLANS_CANT_BE_MADE_AT_THE_SAME_TIME, ActionFailPacket.STATIC);
			return;
		}

		if(clan.getLevel() < Config.CLAN_WAR_MINIMUM_CLAN_LEVEL || clan.getAllSize() < Config.CLAN_WAR_MINIMUM_PLAYERS_DECLARE)
		{
			activeChar.sendPacket(SystemMsg.A_CLAN_WAR_CAN_ONLY_BE_DECLARED_IF_THE_CLAN_IS_LEVEL_3_OR_ABOVE_AND_THE_NUMBER_OF_CLAN_MEMBERS_IS_FIFTEEN_OR_GREATER, ActionFailPacket.STATIC);
			return;
		}

		Clan targetClan = ClanTable.getInstance().getClanByName(_pledgeName);
		if(targetClan == null)
		{
			activeChar.sendPacket(SystemMsg.A_CLAN_WAR_CANNOT_BE_DECLARED_AGAINST_A_CLAN_THAT_DOES_NOT_EXIST, ActionFailPacket.STATIC);
			return;
		}

		else if(clan.equals(targetClan))
		{
			activeChar.sendPacket(SystemMsg.FOOL_YOU_CANNOT_DECLARE_WAR_AGAINST_YOUR_OWN_CLAN, ActionFailPacket.STATIC);
			return;
		}

		else if(clan.isAtWarWith(targetClan.getClanId()))
		{
			activeChar.sendPacket(SystemMsg.WAR_HAS_ALREADY_BEEN_DECLARED_AGAINST_THAT_CLAN_BUT_ILL_MAKE_NOTE_THAT_YOU_REALLY_DONT_LIKE_THEM, ActionFailPacket.STATIC);
			return;
		}

		else if(clan.getAllyId() == targetClan.getAllyId() && clan.getAllyId() != 0)
		{
			activeChar.sendPacket(SystemMsg.A_DECLARATION_OF_CLAN_WAR_AGAINST_AN_ALLIED_CLAN_CANT_BE_MADE, ActionFailPacket.STATIC);
			return;
		}

		else if(targetClan.getLevel() < Config.CLAN_WAR_MINIMUM_CLAN_LEVEL || targetClan.getAllSize() < Config.CLAN_WAR_MINIMUM_PLAYERS_DECLARE)
		{
			activeChar.sendPacket(SystemMsg.A_CLAN_WAR_CAN_ONLY_BE_DECLARED_IF_THE_CLAN_IS_LEVEL_3_OR_ABOVE_AND_THE_NUMBER_OF_CLAN_MEMBERS_IS_FIFTEEN_OR_GREATER, ActionFailPacket.STATIC);
			return;
		}

		ClanTable.getInstance().startClanWar(activeChar.getClan(), targetClan);
	}
}