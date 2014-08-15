package l2s.gameserver.model.actor.basestats;

import l2s.gameserver.model.Playable;
import l2s.gameserver.stats.Stats;

/**
 * @author Bonux
**/
public class PlayableBaseStats extends CreatureBaseStats
{
	public PlayableBaseStats(Playable owner)
	{
		super(owner);
	}

	@Override
	public Playable getOwner()
	{
		return (Playable) _owner;
	}

	@Override
	public double getPAtk()
	{
		return getOwner().getActiveWeaponInstance() == null ? super.getPAtk() : 0.;
	}

	@Override
	public double getMAtk()
	{
		return getOwner().getActiveWeaponInstance() == null ? super.getMAtk() : 0.;
	}

	@Override
	public double getPAtkSpd()
	{
		return getOwner().calcStat(Stats.BASE_P_ATK_SPD, super.getPAtkSpd(), null, null);
	}
}