package l2s.gameserver.network.l2.s2c;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Element;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.templates.item.support.AttributeStone;

/**
 * @author Bonux
 */
public class ExChooseInventoryAttributeItemPacket extends L2GameServerPacket
{
	private final TIntSet _attributableItems = new TIntHashSet();
	private final int _itemId;
	private final boolean _isFireStone;
	private final boolean _isWaterStone;
	private final boolean _isWindStone;
	private final boolean _isEarthStone;
	private final boolean _isHolyStone;
	private final boolean _isDarkStone;
	private final int _stoneLvl;
	private final long _stonesCount;

	public ExChooseInventoryAttributeItemPacket(Player player, AttributeStone stone, long stonesCount)
	{
		ItemInstance[] items = player.getInventory().getItems();
		for(ItemInstance i : items)
		{
			if(stone.getItemType() != null && stone.getItemType() != i.getTemplate().getQuality())
				continue;

			//TODO: [Bonux] На оффе показываются все шмотки, кроме НГ, но зачем? о.0 если атрибутить можно начиная только с S ранга.
			if(i.canBeAttributed())
				_attributableItems.add(i.getObjectId());
		}

		_itemId = stone.getItemId();
		_stoneLvl = stone.getStoneLevel();

		Element stoneElement = stone.getElement(true);
		_isFireStone = stoneElement == Element.FIRE;
		_isWaterStone = stoneElement == Element.WATER;
		_isWindStone = stoneElement == Element.WIND;
		_isEarthStone = stoneElement == Element.EARTH;
		_isHolyStone = stoneElement == Element.HOLY;
		_isDarkStone = stoneElement == Element.UNHOLY;

		_stonesCount = stonesCount;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_itemId);
		writeQ(_stonesCount); // Ertheia UNK.
		writeD(_isFireStone ? 1 : 0); //fire
		writeD(_isWaterStone ? 1 : 0); // water
		writeD(_isWindStone ? 1 : 0); //wind
		writeD(_isEarthStone ? 1 : 0); //earth
		writeD(_isHolyStone ? 1 : 0); //holy
		writeD(_isDarkStone ? 1 : 0); //dark
		writeD(_stoneLvl); //max enchant lvl
		writeD(_attributableItems.size()); //equipable items count
		for(int itemObjId : _attributableItems.toArray())
		{
			writeD(itemObjId); //itemObjId
		}
	}
}