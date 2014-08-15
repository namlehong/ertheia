package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Request;
import l2s.gameserver.model.Request.L2RequestType;
import l2s.gameserver.model.instances.FakePlayerInstance;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.AskJoinPledgePacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;

public class RequestJoinPledge extends L2GameClientPacket
{
	private int _objectId;
	private int _pledgeType;

	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_pledgeType = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null || activeChar.getClan() == null)
			return;

		if(activeChar.isOutOfControl())
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.isProcessingRequest())
		{
			activeChar.sendPacket(SystemMsg.WAITING_FOR_ANOTHER_REPLY);
			return;
		}

		Clan clan = activeChar.getClan();
		if(!clan.canInvite())
		{
			activeChar.sendPacket(SystemMsg.AFTER_A_CLAN_MEMBER_IS_DISMISSED_FROM_A_CLAN_THE_CLAN_MUST_WAIT_AT_LEAST_A_DAY_BEFORE_ACCEPTING_A_NEW_MEMBER);
			return;
		}

		if(_objectId == activeChar.getObjectId())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_ASK_YOURSELF_TO_APPLY_TO_A_CLAN);
			return;
		}

		if((activeChar.getClanPrivileges() & Clan.CP_CL_INVITE_CLAN) != Clan.CP_CL_INVITE_CLAN)
		{
			activeChar.sendPacket(SystemMsg.ONLY_THE_LEADER_CAN_GIVE_OUT_INVITATIONS);
			return;
		}

		if(clan.getUnitMembersSize(_pledgeType) >= clan.getSubPledgeLimit(_pledgeType))
		{
			if(_pledgeType == 0)
				activeChar.sendPacket(new SystemMessagePacket(SystemMsg.S1_IS_FULL_AND_CANNOT_ACCEPT_ADDITIONAL_CLAN_MEMBERS_AT_THIS_TIME).addString(clan.getName()));
			else
				activeChar.sendPacket(SystemMsg.THE_ACADEMYROYAL_GUARDORDER_OF_KNIGHTS_IS_FULL_AND_CANNOT_ACCEPT_NEW_MEMBERS_AT_THIS_TIME);
			return;
		}

		GameObject object = activeChar.getVisibleObject(_objectId);
		if(object == null || !object.isPlayer())
		{
			FakePlayerInstance fakePlayer = GameObjectsStorage.getFakePlayerByObjId(_objectId);
			if(fakePlayer != null)
			{
				if(_pledgeType == Clan.SUBUNIT_ACADEMY && (fakePlayer.getLevel() > 75 || fakePlayer.getClassId().getClassLevel().ordinal() >= 3))
				{
					activeChar.sendPacket(SystemMsg.TO_JOIN_A_CLAN_ACADEMY_CHARACTERS_MUST_BE_LEVEL_40_OR_BELOW_NOT_BELONG_ANOTHER_CLAN_AND_NOT_YET_COMPLETED_THEIR_2ND_CLASS_TRANSFER);
					return;
				}

				Request request = new Request(L2RequestType.CLAN, activeChar, null).setTimeout(10000L);
				request.set("pledgeType", _pledgeType);
				return;
			}
			activeChar.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
			return;
		}

		Player member = (Player) object;
		if(member.getClan() == activeChar.getClan())
		{
			activeChar.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
			return;
		}

		if(!member.getPlayerAccess().CanJoinClan)
		{
			activeChar.sendPacket(new SystemMessagePacket(SystemMsg.C1_CANNOT_JOIN_THE_CLAN_BECAUSE_ONE_DAY_HAS_NOT_YET_PASSED_SINCE_THEY_LEFT_ANOTHER_CLAN).addName(member));
			return;
		}

		if(member.getClan() != null)
		{
			activeChar.sendPacket(new SystemMessagePacket(SystemMsg.S1_IS_ALREADY_A_MEMBER_OF_ANOTHER_CLAN).addName(member));
			return;
		}

		if(member.isBusy())
		{
			activeChar.sendPacket(new SystemMessagePacket(SystemMsg.C1_IS_ON_ANOTHER_TASK).addName(member));
			return;
		}

		if(_pledgeType == Clan.SUBUNIT_ACADEMY && (member.getLevel() > 75 || member.getClassLevel() >= 3 || !member.isBaseClassActive()))
		{
			activeChar.sendPacket(SystemMsg.TO_JOIN_A_CLAN_ACADEMY_CHARACTERS_MUST_BE_LEVEL_40_OR_BELOW_NOT_BELONG_ANOTHER_CLAN_AND_NOT_YET_COMPLETED_THEIR_2ND_CLASS_TRANSFER);
			return;
		}

		Request request = new Request(L2RequestType.CLAN, activeChar, member).setTimeout(10000L);
		request.set("pledgeType", _pledgeType);
		member.sendPacket(new AskJoinPledgePacket(activeChar.getObjectId(), activeChar.getClan().getName()));
	}
}