package l2s.gameserver.network.l2.s2c;

public class ExAttackInAirShip extends L2GameServerPacket
{
	/*
	 * заготовка!!!
	 * Format: dddcddddh[ddc]
	 * ExAttackInAirShip AttackerID:%d DefenderID:%d Damage:%d bMiss:%d bCritical:%d AirShipID:%d
	 */

	@Override
	protected final void writeImpl()
	{
	}
}