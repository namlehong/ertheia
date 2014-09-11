package l2s.gameserver.model.actor.instances.player;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import l2s.gameserver.dao.MentoringDAO;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExMentorList;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.utils.Mentoring;

/**
 * @author Cain
 * TODO: [Bonux] Переписать.
 */
public class MenteeList
{
	private TIntObjectHashMap<Mentee> _menteeList = new TIntObjectHashMap<Mentee>(0);
	private final Player _owner;

	public MenteeList(Player owner)
	{
		_owner = owner;
	}

	public void restore()
	{
		if(Mentoring.canBecomeMentor(_owner))
			_menteeList = MentoringDAO.getInstance().selectMentorList(_owner);
		
		if(Mentoring.canBecomeMentee(_owner))
			_menteeList = MentoringDAO.getInstance().selectMenteeList(_owner);
	}

	public Mentee get(int objectId)
	{
		return _menteeList.get(objectId);
	}

	public int size()
	{
		return _menteeList.size();
	}

	public Mentee[] values()
	{
		return _menteeList.values(new Mentee[_menteeList.size()]);
	}

	public Collection<Mentee> valueCollection()
	{
		return _menteeList.valueCollection();
	}

	// [FIX] Cain переделать эту пиздаболию, чтобы не вызывалось 2 раза при удалении наставника или ученика.
	public void remove(String name, boolean isMentor, boolean notify)
	{
		if(StringUtils.isEmpty(name))
			return;

		int objectId = removeMentee0(name);
		if(objectId > 0 && notify)
		{
			Player otherSideMentee = World.getPlayer(name);

			if(otherSideMentee != null)
				otherSideMentee.sendPacket(new SystemMessagePacket(SystemMsg.THE_MENTORING_RELATIONSHIP_WITH_S1_HAS_BEEN_CANCELED).addString(isMentor ? name : _owner.getName()));

			_owner.sendPacket(new SystemMessagePacket(SystemMsg.THE_MENTORING_RELATIONSHIP_WITH_S1_HAS_BEEN_CANCELED).addString(isMentor ? name : _owner.getName()));
		}
	}

	public void notify(boolean login)
	{
		for(Mentee mentee : values())
		{
			Player menteePlayer = World.getPlayer(mentee.getObjectId());
			if(menteePlayer != null)
			{
				Mentee thisMentee = menteePlayer.getMenteeList().get(_owner.getObjectId());
				if(thisMentee == null)
					continue;

				thisMentee.update(_owner, login);

				if(login)
					menteePlayer.sendPacket(new SystemMessagePacket(mentee.isMentor() ? SystemMsg.YOU_MENTEE_S1_HAS_CONNECTED : SystemMsg.YOU_MENTOR_S1_HAS_CONNECTED).addString(_owner.getName()));
				else
					menteePlayer.sendPacket(new SystemMessagePacket(mentee.isMentor() ? SystemMsg.YOU_MENTEE_S1_HAS_DISCONNECTED : SystemMsg.YOU_MENTOR_S1_HAS_DISCONNECTED).addString(_owner.getName()));

				menteePlayer.sendPacket(new ExMentorList(menteePlayer));

				mentee.update(menteePlayer, login);
			}
		}
	}

	public void addMentee(Player menteePlayer)
	{
		// it should reject here, but for safety
		if(!Mentoring.canBecomeMentor(_owner))
			return;
		_menteeList.put(menteePlayer.getObjectId(), new Mentee(menteePlayer));
		MentoringDAO.getInstance().insert(_owner, menteePlayer);
	}

	public void addMentor(Player mentorPlayer)
	{
		if(!Mentoring.canBecomeMentee(_owner))
			return;
		_menteeList.put(mentorPlayer.getObjectId(), new Mentee(mentorPlayer, true));
		Mentoring.addMentoringSkills(mentorPlayer);
	}

	private int removeMentee0(String name)
	{
		if(name == null)
			return 0;

		int objectId = 0;
		for(Mentee m : values())
		{
			if(name.equalsIgnoreCase(m.getName()))
			{
				objectId = m.getObjectId();
				break;
			}
		}

		if(objectId > 0)
		{
			_menteeList.remove(objectId);
			MentoringDAO.getInstance().delete(_owner.getObjectId(), objectId);
			return objectId;
		}
		return 0;
	}

	public boolean someOneOnline(boolean login)
	{
		for(Mentee mentee : values())
		{
			Player menteePlayer = World.getPlayer(mentee.getObjectId());
			if(menteePlayer != null)
			{
				Mentee thisMentee = menteePlayer.getMenteeList().get(_owner.getObjectId());
				if(thisMentee == null)
					continue;

				thisMentee.update(_owner, login);

				if(menteePlayer.isOnline())
					return true;
			}
		}
		return false;
	}

	public int getMentor()
	{
		for(Mentee m : values())
		{
			if(m.isMentor())
				return m.getObjectId();
		}
		return 0;
	}

	@Override
	public String toString()
	{
		return "MenteeList[owner=" + _owner.getName() + "]";
	}
}
