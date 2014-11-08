package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2s.commons.util.Rnd;
import l2s.gameserver.data.xml.holder.ProductDataHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.templates.item.product.ProductItem;
import l2s.gameserver.templates.item.product.ProductItemComponent;

/**
 * upgradet to Glory Days [488] by Bonux
**/
public class ExBR_ProductListPacket extends L2GameServerPacket
{
	private final List<ProductItem> _products = new ArrayList<ProductItem>();

	public ExBR_ProductListPacket(Player player)
	{
		_products.addAll(ProductDataHolder.getInstance().getProductsOnSale(player));
		Collections.sort(_products);
	}

	@Override
	protected void writeImpl()
	{
		writeD(4457953); // UNK
		writeC(0x00); // blank
		writeD(0x00); // blank
		writeD(0x00); // blank
		writeD(0x00); // blank
		
		writeD(_products.size());

		for(ProductItem product : _products)
		{
			writeD(product.getId()); //product id
			writeH(product.getCategory()); //category 1 - enchant 2 - supplies  3 - decoration 4 - package 5 - other
			writeD(product.getPoints(true)); //points
			writeC(product.getTabId()); // show product isNew, isEvent, isSale, isBest
			writeC(product.getTabId()); // show product isNew, isEvent, isSale, isBest
			writeC(0x00); //UNK
			writeC(0x00); //UNK
			writeC(0x00); //UNK
			//writeD(Rnd.get(0,4)); // Categories Home (0 - do not show on the main (default), 1 - top window, 2 - Featured Products 3 - unknown 4 - Top Sellers) // Glory Days 488
			writeD((int) (product.getStartTimeSale() / 1000)); // start sale unix date in seconds
			writeD((int) (product.getEndTimeSale() / 1000)); // end sale unix date in seconds
			writeC(127); // day week (127 = not daily goods)
			writeC(product.getStartHour()); // start hour
			writeC(product.getStartMin()); // start min
			writeC(product.getEndHour()); // end hour
			writeC(product.getEndMin()); // end min
			writeD(0); // stock
			writeD(-1); // max stock
			
			writeC(0x00); //UNK
			writeC(0x00); //level restriction
			writeC(0x00); //UNK
			writeC(0x00); //UNK
			
			writeD(0x00); //UNK
			writeD(0x00); //UNK
			writeD(0x00); //UNK
			
			writeC(0x00);
			writeC(0x00);
			writeC(0x00);
			writeC(product.getComponents().size()); // Number item in the product.

			for(ProductItemComponent component : product.getComponents())
			{
				writeD(component.getItemId()); //item id
				writeD(component.getCount()); //quality
				writeD(component.getWeight()); //weight
				writeD(component.isDropable() ? 1 : 0); //0 - dont drop/trade
			}
		}
	}
}