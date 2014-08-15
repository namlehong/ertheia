package l2s.gameserver.network.l2.s2c;

import java.util.Map;

import l2s.gameserver.model.actor.instances.player.PremiumItem;

/**
 * @author VISTALL
 * @date 23:37/23.03.2011
 */
public class ExGoodsInventoryInfo extends L2GameServerPacket
{
	private Map<Integer, PremiumItem> _premiumItemMap;

	public ExGoodsInventoryInfo(Map<Integer, PremiumItem> premiumItemMap)
	{
		_premiumItemMap = premiumItemMap;

	}

	@Override
	protected void writeImpl()
	{
		if(!_premiumItemMap.isEmpty())
		{
			writeH(this._premiumItemMap.size());
			for(Map.Entry<Integer, PremiumItem> entry : _premiumItemMap.entrySet())
			{
				PremiumItem item = entry.getValue();
				writeQ(entry.getKey());
				writeC(0);
				writeD(10003);
				writeS(item.getSender());
				writeS(item.getSender());//((PremiumItem)entry.getValue()).getSenderMessage());
				writeQ(0);
				writeC(2);
				writeC(0);

				writeS(null);
				writeS(null);

				writeH(1);
				writeD(item.getItemId());
				writeD((int) item.getItemCount());
			}
		}
		else
		{
			writeH(0);
		}
	}
}
