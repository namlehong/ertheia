package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.items.ItemInfo;

public class TradeOtherAddPacket extends L2GameServerPacket
{
	private ItemInfo _temp;
	private long _amount;

	public TradeOtherAddPacket(ItemInfo item, long amount)
	{
		_temp = item;
		_amount = amount;
	}

	@Override
	protected final void writeImpl()
	{
		writeH(1); // item count
		writeH(_temp.getItem().getType1());
		writeD(_temp.getObjectId());
		writeD(_temp.getItemId());
		writeQ(_amount);
		writeH(_temp.getItem().getType2ForPackets());
		writeH(_temp.getCustomType1());
		writeD(_temp.getItem().getBodyPart());
		writeH(_temp.getEnchantLevel());
		writeH(0x00);
		writeH(_temp.getCustomType2());
		writeD(_temp.getVisualId());
		writeH(_temp.getAttackElement());
		writeH(_temp.getAttackElementValue());
		writeH(_temp.getDefenceFire());
		writeH(_temp.getDefenceWater());
		writeH(_temp.getDefenceWind());
		writeH(_temp.getDefenceEarth());
		writeH(_temp.getDefenceHoly());
		writeH(_temp.getDefenceUnholy());
		writeH(_temp.getEnchantOptions()[0]);
		writeH(_temp.getEnchantOptions()[1]);
		writeH(_temp.getEnchantOptions()[2]);
	}
}