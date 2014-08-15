package l2s.gameserver.model;

import l2s.gameserver.instancemanager.WorldStatisticsManager;
import l2s.gameserver.model.worldstatistics.CharacterStatistic;
import l2s.gameserver.network.l2.s2c.ExLoadStatHotLink;
import l2s.gameserver.network.l2.s2c.L2GameServerPacket;
import l2s.gameserver.network.l2.s2c.ServerObjectInfoPacket;
import l2s.gameserver.templates.StatuesSpawnTemplate;
import l2s.gameserver.utils.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Дмитрий
 * @date 17.10.12 0:16
 */
public final class WinnerStatueInstance extends GameObject
{
	private StatuesSpawnTemplate template;
	private int _heading = 0;

	public WinnerStatueInstance(int objectId, StatuesSpawnTemplate template)
	{
		super(objectId);
		this.template = template;
	}

	@Override
	public boolean isAttackable(Creature creature)
	{
		return false;
	}

	@Override
	public double getCollisionRadius()
	{
		return 30.0;
	}

	@Override
	public double getCollisionHeight()
	{
		return 40.0;
	}

	@Override
	public void spawnMe(Location loc)
	{
		_heading = loc.h;
		super.spawnMe(loc);
	}

	@Override
	public final int getHeading()
	{
		return _heading;
	}

	@Override
	public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper)
	{
		List<L2GameServerPacket> list = new ArrayList<L2GameServerPacket> (1);
		list.add(new ServerObjectInfoPacket(this, forPlayer));
		return list;
	}

	@Override
	public void onAction(Player player, boolean shift)
	{
		List<CharacterStatistic> globalStatistic = WorldStatisticsManager.getInstance().getWinners(template.getCategoryType(), true, WorldStatisticsManager.STATUES_TOP_PLAYER_LIMIT);
		List<CharacterStatistic> monthlyStatistic = WorldStatisticsManager.getInstance().getWinners(template.getCategoryType(), false, WorldStatisticsManager.STATUES_TOP_PLAYER_LIMIT);
		player.sendPacket(new ExLoadStatHotLink(template.getCategoryType().getClientId(), template.getCategoryType().getSubcat(), globalStatistic, monthlyStatistic));
		player.sendActionFailed();
	}

	public StatuesSpawnTemplate getTemplate()
	{
		return template;
	}
}
