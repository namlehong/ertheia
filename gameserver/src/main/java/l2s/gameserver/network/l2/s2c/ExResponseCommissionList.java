package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.items.CommissionItem;

/**
 * @author Bonux
 */
public class ExResponseCommissionList extends L2GameServerPacket
{
	public final static int MY_COMMISSION_LIST = 2;
	public final static int COMMON_COMMISSION_LIST = 3;

	private int _currentTimeInSeconds;
	private int _listType;
	private CommissionItem[] _items;

	public ExResponseCommissionList(int listType, CommissionItem[] items)
	{
		_currentTimeInSeconds = (int) (System.currentTimeMillis() / 1000L);
		_listType = listType;
		_items = items;
	}

	protected void writeImpl()
	{
		writeD(_listType); //?? На оффе посылается 3, если простой список; 2, если мой список
		writeD(_currentTimeInSeconds); //Current time in seconds
		writeD(0); //??
		writeD(_items.length); //items count
		for(CommissionItem item : _items)
		{
			writeQ(item.getCommissionId()); //bid id
			writeQ(item.getCommissionPrice()); //price
			writeD(0); //??
			writeD(0); //??
			writeD(item.getEndPeriodDate()); //end date in seconds
			writeS(item.getOwnerName()); //Owner Name
			writeD(0x00); //unk
			writeD(item.getItemId()); //item_id
			writeQ(item.getCount()); //count
			writeH(item.getItem().getType2ForPackets()); //itemType2 or equipSlot
			writeD(item.getItem().getBodyPart()); //bodypart
			writeH(item.getEnchantLevel()); //enchant_lvl
			writeH(item.getCustomType2()); //custom_type2
			writeD(0x00); //unk
			writeH(item.getAttackElement()); //atk_element_id
			writeH(item.getAttackElementValue()); //atk_element_val
			writeH(item.getDefenceFire()); //fire_defence
			writeH(item.getDefenceWater()); //water_defence
			writeH(item.getDefenceWind()); //wind_defence
			writeH(item.getDefenceEarth()); //earth_defence
			writeH(item.getDefenceHoly()); //holy_defence
			writeH(item.getDefenceUnholy()); //unholy_defence
			writeH(item.getEnchantOptions()[0]); //enchant_opt1
			writeH(item.getEnchantOptions()[1]); //enchant_opt2
			writeH(item.getEnchantOptions()[2]); //enchant_opt3
		}
	}
}
