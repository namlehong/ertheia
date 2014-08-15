package l2s.gameserver.listener.actor.player.impl;

import l2s.commons.lang.reference.HardReference;
import l2s.gameserver.listener.actor.player.OnAnswerListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.model.base.TeamType;

/**
 * @author Cain
 * @date 17:15/26.04.2012
 */
public class MentorAnswerListener implements OnAnswerListener
{
	private HardReference<Player> _playerRef;
	private String _mentee;

	public MentorAnswerListener(Player mentor, String mentee)
	{
		_playerRef = mentor.getRef();
		_mentee = mentee;
	}

	@Override
	public void sayYes()
	{
		Player player = _playerRef.get();
		if(player == null)
			return;

		if(player.isDead() || !player.getReflection().isDefault() || player.isInOlympiadMode() || player.isInObserverMode() || player.isTeleporting() || player.getTeam() != TeamType.NONE)
			return;

		player.teleToLocation(World.getPlayer(_mentee).getLoc());
	}

	@Override
	public void sayNo()
	{}
}
