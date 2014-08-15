package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.Request;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExMentorList;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.utils.Mentoring;

/**
 * @author Cain
 */
public class ConfirmMenteeAdd extends L2GameClientPacket
{
	private int _answer;
	private String _mentorName;

	@Override
	protected void readImpl()
	{
		_answer = readD();
		_mentorName = readS();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		Request request = activeChar.getRequest();
		if(request == null || !request.isTypeOf(Request.L2RequestType.MENTEE))
		{
			activeChar.sendActionFailed();
			return;
		}

		if(!request.isInProgress())
		{
			request.cancel();
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.isOutOfControl())
		{
			request.cancel();
			activeChar.sendActionFailed();
			return;
		}

		Player requestor = request.getRequestor();
		if(activeChar == requestor)
		{
			request.cancel();
			activeChar.sendActionFailed();
			return;
		}

		if(requestor == null)
		{
			request.cancel();
			activeChar.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
			activeChar.sendActionFailed();
			return;
		}

		if(requestor.getRequest() != request)
		{
			request.cancel();
			activeChar.sendActionFailed();
			return;
		}

		// отказ
		if(_answer == 0)
		{
			request.cancel();
			requestor.sendPacket(new SystemMessagePacket(SystemMsg.S1_HAS_DECLINED_BECOMING_YOUR_MENTEE).addString(activeChar.getName()));
			return;
		}

		if(requestor.isActionsDisabled())
		{
			request.cancel();
			activeChar.sendPacket(new SystemMessagePacket(SystemMsg.C1_IS_ON_ANOTHER_TASK).addString(requestor.getName()));
			activeChar.sendActionFailed();
			return;
		}

		try
		{
			requestor.getMenteeList().addMentee(activeChar);
			activeChar.getMenteeList().addMentor(requestor);
			activeChar.sendPacket(new SystemMessagePacket(SystemMsg.FROM_NOW_ON_S1_WILL_BE_YOUR_MENTOR).addName(requestor), new ExMentorList(activeChar));
			requestor.sendPacket(new SystemMessagePacket(SystemMsg.FROM_NOW_ON_S1_WILL_BE_YOUR_MENTEE).addName(activeChar), new ExMentorList(requestor));
			Mentoring.applyMentoringCond(requestor, true);
			Mentoring.addMentoringSkills(requestor);
		}
		finally
		{
			request.done();
		}
	}
}