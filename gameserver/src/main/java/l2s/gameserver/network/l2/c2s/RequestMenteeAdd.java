package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.Config;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Request;
import l2s.gameserver.model.Request.L2RequestType;
import l2s.gameserver.model.World;
import l2s.gameserver.model.actor.instances.player.Mentee;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.instances.FakePlayerInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExMentorAdd;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.utils.Mentoring;

/**
 * @author Cain
 */
public class RequestMenteeAdd extends L2GameClientPacket
{
	private String _newMentee;

	@Override
	protected void readImpl()
	{
		_newMentee = readS();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(Config.MENTOR_ONLY_PA && !activeChar.hasBonus())
		{
			/* На оффе вроде нету сообщения.
			activeChar.sendMessage("This future allowed if you have premium account!");*/
			return;
		}

		// Попытка стать наставником себе
		if(activeChar.getName().equals(_newMentee))
		{
			activeChar.sendPacket(new SystemMessagePacket(SystemMsg.YOU_CANNOT_BECOME_YOUR_OWN_MENTEE));
			return;
		}

		// Только после перерождения можно стать наставником
//		if(!activeChar.isAwaked())
		if(Mentoring.canBecomeMentor(activeChar))
		{
			activeChar.sendPacket(new SystemMessagePacket(SystemMsg.YOU_MUST_AWAKEN_IN_ORDER_TO_BECOME_A_MENTOR));
			return;
		}

		// Уже 3 ученика у наставника
		if(activeChar.getMenteeList().size() >= 3)
		{
			activeChar.sendPacket(new SystemMessagePacket(SystemMsg.A_MENTOR_CAN_HAVE_UP_TO_3_MENTEES_AT_THE_SAME_TIME));
			return;
		}

		long mentorPenalty = activeChar.getVarLong("mentorPenalty", 0L);
		if(mentorPenalty > System.currentTimeMillis())
		{
			long milisPenalty = mentorPenalty - System.currentTimeMillis();
			double numSecs = milisPenalty / 1000 % 60;
			double countDown = (milisPenalty / 1000 - numSecs) / 60;
			int numMins = (int) Math.floor(countDown % 60);
			countDown = (countDown - numMins) / 60;
			int numHours = (int) Math.floor(countDown % 24);
			int numDays = (int) Math.floor((countDown - numHours) / 24);
			activeChar.sendPacket(new SystemMessagePacket(SystemMsg.YOU_CAN_BOND_WITH_A_NEW_MENTEE_IN_S1_DAYS_S2_HOUR_S3_MINUTE).addInteger(numDays).addInteger(numHours).addInteger(numMins));
			return;
		}

		Player newMentee = World.getPlayer(_newMentee);
		if(newMentee == null) // Чар онлайн?
		{
			FakePlayerInstance fakePlayer = GameObjectsStorage.getFakePlayerByName(_newMentee);
			if(fakePlayer != null)
			{
				// Выше 85 лвла
				if(fakePlayer.getLevel() >= Mentoring.MENTEE_GRADUATE_LEVEL)
				{
					activeChar.sendPacket(new SystemMessagePacket(SystemMsg.S1_IS_ABOVE_LEVEL_86_AND_CANNOT_BECOME_A_MENTEE).addName(fakePlayer));
					return;
				}

				new Request(L2RequestType.MENTEE, activeChar, null).setTimeout(10000L);
				activeChar.sendPacket(new SystemMessagePacket(SystemMsg.YOU_HAVE_OFFERED_TO_BECOME_S1_MENTOR).addName(fakePlayer));
				return;
			}
			activeChar.sendPacket(new SystemMessagePacket(SystemMsg.THAT_PLAYER_IS_NOT_ONLINE));
			return;
		}

		if(Config.MENTOR_ONLY_PA && !newMentee.hasBonus())
		{
			/* На оффе вроде нету сообщения.
			activeChar.sendMessage("This future allowed if your mentee has premium account!");
			newMentee.sendMessage("This future allowed if you have premium account!");*/
			return;
		}

		// Уже есть наставник
		if(newMentee.getMenteeList().getMentor() != 0)
		{
			activeChar.sendPacket(new SystemMessagePacket(SystemMsg.S1_ALREADY_HAS_A_MENTOR).addName(newMentee));
			return;
		}

		// Выбраный чар уже в списке у наставника
		for(Mentee m : activeChar.getMenteeList().values())
		{
			if(m.getName().equals(_newMentee))
				return;
		}

		// Выше 85 лвла
		if(newMentee.getLevel() >= Mentoring.MENTEE_GRADUATE_LEVEL)
		{
			activeChar.sendPacket(new SystemMessagePacket(SystemMsg.S1_IS_ABOVE_LEVEL_86_AND_CANNOT_BECOME_A_MENTEE).addName(newMentee));
			return;
		}

		// no sub classes
		if(!newMentee.isBaseClassActive())
		{
			activeChar.sendPacket(new SystemMessagePacket(SystemMsg.INVITATION_CAN_OCCUR_ONLY_WHEN_THE_MENTEE_IS_IN_MAIN_CLASS_STATUS));
			return;
		}

		// У нового ученика нет Сертификата Подопечного
		if(!newMentee.getInventory().validateCapacity(33800, 1))
		{
			activeChar.sendPacket(new SystemMessagePacket(SystemMsg.S1_DOES_NOT_HAVE_THE_ITEM_NEDEED_TO_BECOME_A_MENTEE).addName(newMentee));
			return;
		}

		new Request(L2RequestType.MENTEE, activeChar, newMentee).setTimeout(10000L);
		activeChar.sendPacket(new SystemMessagePacket(SystemMsg.YOU_HAVE_OFFERED_TO_BECOME_S1_MENTOR).addName(newMentee));
		newMentee.sendPacket(new ExMentorAdd(activeChar));
	}
}