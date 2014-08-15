package l2s.gameserver.model.actor.instances.player;

import l2s.commons.lang.reference.HardReference;
import l2s.commons.lang.reference.HardReferences;
import l2s.gameserver.model.Player;

/**
 * @author Cain
 * @date 22:42/26.03.2012
 */
public class Mentee
{
	private final int _objectId;
	private String _name;
	private int _classId;
	private int _level;
	private boolean _isMentor;

	private HardReference<Player> _playerRef = HardReferences.emptyRef();

	public Mentee(int objectId, String name, int classId, int level, boolean isMentor)
	{
		_objectId = objectId;
		_name = name;
		_classId = classId;
		_level = level;
		_isMentor = isMentor;
	}

	public Mentee(Player player)
	{
		_objectId = player.getObjectId();
		update(player, true);
	}

	public Mentee(Player player, boolean isMentor)
	{
		_objectId = player.getObjectId();
		_isMentor = isMentor;
		update(player, true);
	}

	public void update(Player player, boolean set)
	{
		_level = player.getLevel();
		_name = player.getName();
		_classId = player.getActiveClassId();
		_playerRef = set ? player.getRef() : HardReferences.<Player> emptyRef();
	}

	public String getName()
	{
		Player player = getPlayer();
		return player == null ? _name : player.getName();
	}

	public int getObjectId()
	{
		return _objectId;
	}

	public int getClassId()
	{
		Player player = getPlayer();
		return player == null ? _classId : player.getActiveClassId();
	}

	public int getLevel()
	{
		Player player = getPlayer();
		return player == null ? _level : player.getLevel();
	}

	public boolean isOnline()
	{
		Player player = _playerRef.get();
		return player != null && !player.isInOfflineMode();
	}

	public Player getPlayer()
	{
		Player player = _playerRef.get();
		return player != null && !player.isInOfflineMode() ? player : null;
	}

	public boolean isMentor()
	{
		return _isMentor;
	}
}
