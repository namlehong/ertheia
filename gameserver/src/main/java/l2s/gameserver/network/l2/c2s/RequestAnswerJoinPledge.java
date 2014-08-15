package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.data.xml.holder.EventHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Request;
import l2s.gameserver.model.Request.L2RequestType;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.model.pledge.SubUnit;
import l2s.gameserver.model.pledge.UnitMember;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.JoinPledgePacket;
import l2s.gameserver.network.l2.s2c.PledgeShowInfoUpdatePacket;
import l2s.gameserver.network.l2.s2c.PledgeShowMemberListAddPacket;
import l2s.gameserver.network.l2.s2c.PledgeSkillListPacket;
import l2s.gameserver.network.l2.s2c.SkillListPacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;

public class RequestAnswerJoinPledge extends L2GameClientPacket
{
	private int _response;

	@Override
	protected void readImpl()
	{
		_response = _buf.hasRemaining() ? readD() : 0;
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null)
			return;

		Request request = player.getRequest();
		if(request == null || !request.isTypeOf(L2RequestType.CLAN))
			return;

		if(!request.isInProgress())
		{
			request.cancel();
			player.sendActionFailed();
			return;
		}

		if(player.isOutOfControl())
		{
			request.cancel();
			player.sendActionFailed();
			return;
		}

		Player requestor = request.getRequestor();
		if(requestor == null)
		{
			request.cancel();
			player.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
			player.sendActionFailed();
			return;
		}

		if(requestor.getRequest() != request)
		{
			request.cancel();
			player.sendActionFailed();
			return;
		}

		Clan clan = requestor.getClan();
		if(clan == null)
		{
			request.cancel();
			player.sendActionFailed();
			return;
		}

		if(_response == 0)
		{
			request.cancel();
			requestor.sendPacket(new SystemMessagePacket(SystemMsg.S1_DECLINED_YOUR_CLAN_INVITATION).addName(player));
			return;
		}

		if(!player.canJoinClan())
		{
			request.cancel();
			player.sendPacket(SystemMsg.AFTER_LEAVING_OR_HAVING_BEEN_DISMISSED_FROM_A_CLAN_YOU_MUST_WAIT_AT_LEAST_A_DAY_BEFORE_JOINING_ANOTHER_CLAN);
			return;
		}

		int pledgeType = request.getInteger("pledgeType");
		if(pledgeType == Clan.SUBUNIT_ACADEMY)
		{
			if(player.getLevel() > 75 || player.getClassLevel() >= 3 || !player.isBaseClassActive())
				return;
		}

		try
		{
			player.sendPacket(new JoinPledgePacket(requestor.getClanId()));

			SubUnit subUnit = clan.getSubUnit(pledgeType);
			if(subUnit == null)
				return;

			UnitMember member = new UnitMember(clan, player.getName(), player.getTitle(), player.getLevel(), player.getClassId().getId(), player.getObjectId(), pledgeType, player.getPowerGrade(), player.getApprentice(), player.getSex().ordinal(), Clan.SUBUNIT_NONE);
			subUnit.addUnitMember(member);

			player.setPledgeType(pledgeType);
			player.setClan(clan);

			member.setPlayerInstance(player, false);

			if(pledgeType == Clan.SUBUNIT_ACADEMY)
				player.setLvlJoinedAcademy(player.getLevel());

			member.setPowerGrade(clan.getAffiliationRank(player.getPledgeType()));

			clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListAddPacket(member), player);
			clan.broadcastToOnlineMembers(new SystemMessagePacket(SystemMsg.S1_HAS_JOINED_THE_CLAN).addString(player.getName()), new PledgeShowInfoUpdatePacket(clan));

			// this activates the clan tab on the new member
			player.sendPacket(SystemMsg.ENTERED_THE_CLAN);
			player.sendPacket(player.getClan().listAll());
			player.setLeaveClanTime(0);
			player.updatePledgeRank();

			// добавляем скилы игроку, ток тихо
			clan.addSkillsQuietly(player);
			// отображем
			player.sendPacket(new PledgeSkillListPacket(clan));
			player.sendPacket(new SkillListPacket(player));

			EventHolder.getInstance().findEvent(player);

			player.broadcastCharInfo();

			clan.onEnterClan(player);
			player.store(false);
		}
		finally
		{
			request.done();
		}
	}
}