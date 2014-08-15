package l2s.gameserver.model.entity.events.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.TeamType;
import l2s.gameserver.stats.Env;
import l2s.gameserver.utils.Location;

/**
 * @author VISTALL
 * @date 2:17/26.06.2011
 */
public class DuelSnapshotObject implements Serializable
{
	private static final long serialVersionUID = 1L;

	private final Player _player;
	private final TeamType _team;

	private int _classIndex;

	private Location _returnLoc;

	private double _currentHp;
	private double _currentMp;
	private double _currentCp;

	private List<Effect> _effects;

	private boolean _isDead;

	public DuelSnapshotObject(Player player, TeamType team)
	{
		_player = player;
		_team = team;
	}

	public void store()
	{
		_classIndex = _player.getActiveSubClass().getIndex();

		_returnLoc = _player.getReflection().getReturnLoc() == null ? _player.getLoc() : _player.getReflection().getReturnLoc();

		_currentCp = _player.getCurrentCp();
		_currentHp = _player.getCurrentHp();
		_currentMp = _player.getCurrentMp();

		Collection<Effect> effectList = _player.getEffectList().getEffects();
		_effects = new ArrayList<Effect>(effectList.size());
		for(Effect $effect : effectList)
		{
			Effect effect = $effect.getTemplate().getEffect(new Env($effect.getEffector(), $effect.getEffected(), $effect.getSkill()));
			effect.setCount($effect.getCount());
			effect.setPeriod($effect.getCount() == 1 ? $effect.getPeriod() - $effect.getTime() : $effect.getPeriod());

			_effects.add(effect);
		}
	}

	public void restore(boolean abnormal)
	{
		if(!abnormal)
		{
			if(_classIndex == _player.getActiveSubClass().getIndex())
			{
				/*_player.getEffectList().writeLock();
				try
				{*/
					_player.getEffectList().stopAllEffects();
					for(Effect e : _effects)
						e.schedule();
				/*}
				finally
				{
					_player.getEffectList().writeUnlock();
				}*/

				_player.setCurrentCp(_currentCp);
				_player.setCurrentHpMp(_currentHp, _currentMp);
			}
			else
			{
				_player.setCurrentCp(_player.getMaxCp());
				_player.setCurrentHpMp(_player.getMaxHp(), _player.getMaxMp());
			}
		}
	}

	public void teleport()
	{
		_player.setStablePoint(null);
		if(_player.isFrozen())
			_player.stopFrozen();

		ThreadPoolManager.getInstance().schedule(new RunnableImpl(){
			@Override
			public void runImpl() throws Exception
			{
				_player.teleToLocation(_returnLoc, ReflectionManager.DEFAULT);
			}
		}, 5000L);
	}

	public Player getPlayer()
	{
		return _player;
	}

	public boolean isDead()
	{
		return _isDead;
	}

	public void setDead()
	{
		_isDead = true;
	}

	public Location getLoc()
	{
		return _returnLoc;
	}

	public TeamType getTeam()
	{
		return _team;
	}
}
