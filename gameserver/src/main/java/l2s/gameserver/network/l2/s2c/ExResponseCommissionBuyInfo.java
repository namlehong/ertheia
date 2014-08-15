package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.items.CommissionItem;

/**
 * @author Bonux
 */
public class ExResponseCommissionBuyInfo extends L2GameServerPacket
{
	private CommissionItem _item;

	public ExResponseCommissionBuyInfo(CommissionItem item)
	{
		_item = item;
	}

	protected void writeImpl()
	{
		writeD(0x01); //unk
		writeQ(_item.getCommissionPrice()); //price
		writeQ(_item.getCommissionId()); //bid id
		writeD(0x00); //unk
		writeD(0x00); //unk
		writeD(_item.getItemId()); //item_id
		writeQ(_item.getCount()); //count
		writeH(_item.getItem().getType2ForPackets()); //itemType2 or equipSlot
		writeD(_item.getItem().getBodyPart()); //bodypart
		writeH(_item.getEnchantLevel()); //enchant_lvl
		writeH(_item.getCustomType2()); //custom_type2
		writeD(0x00); //unk
		writeH(_item.getAttackElement()); //atk_element_id
		writeH(_item.getAttackElementValue()); //atk_element_val
		writeH(_item.getDefenceFire()); //fire_defence
		writeH(_item.getDefenceWater()); //water_defence
		writeH(_item.getDefenceWind()); //wind_defence
		writeH(_item.getDefenceEarth()); //earth_defence
		writeH(_item.getDefenceHoly()); //holy_defence
		writeH(_item.getDefenceUnholy()); //unholy_defence
		writeH(_item.getEnchantOptions()[0]); //enchant_opt1
		writeH(_item.getEnchantOptions()[1]); //enchant_opt2
		writeH(_item.getEnchantOptions()[2]); //enchant_opt3
	}
}
