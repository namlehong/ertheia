package l2s.gameserver.network.l2.s2c;

public class ExLoginVitalityEffectInfo extends L2GameServerPacket
{
	private boolean _hasPremium;
	private int _vitalityItemsAllowed;

	public ExLoginVitalityEffectInfo(boolean hasPremium, int vitalityItemsAllowed)
	{
		_hasPremium = hasPremium;
		_vitalityItemsAllowed = vitalityItemsAllowed;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_hasPremium ? 300 : 200); // XP Bonus
		writeD(_vitalityItemsAllowed); // Vitality items allowed
	}
}
