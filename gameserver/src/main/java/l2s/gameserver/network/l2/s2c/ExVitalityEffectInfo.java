package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;

/**
 * @author Bonux
 */
public class ExVitalityEffectInfo extends L2GameServerPacket
{
	private final int _vitalityPoints;
	private final int _vitalityItemsAllowed;
	private final int _totalVitalityItemsAllowed;
	private final int _bonusPercent;

	public ExVitalityEffectInfo(Player player)
	{
		_vitalityPoints = player.getVitality();
		_totalVitalityItemsAllowed = player.getVitalityPotionsLimit();
		_vitalityItemsAllowed = _totalVitalityItemsAllowed - player.getUsedVitalityPotions();
		_bonusPercent = (int) (player.getVitalityBonus() * 100);
	}

	@Override
	protected void writeImpl()
	{
		writeD(_vitalityPoints); // Vitality points (account's)
		writeD(_bonusPercent); // XP Bonus
		writeD(_vitalityItemsAllowed); // Vitality items allowed???
		writeD(_totalVitalityItemsAllowed); // Total vitality items allowed???
	}
}
