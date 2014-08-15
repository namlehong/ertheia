package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.items.Inventory;

/**
 * @reworked by Bonux
**/
public class ExUserInfoEquipSlot extends L2GameServerPacket
{
	private static enum SendType
	{
		FULL,
		UPDATE;
	}

	private final SendType _sendType;
	private final int _objectId;
	private final int[][] _inv;
	private final ItemInstance _item;

	public ExUserInfoEquipSlot(Player player)
	{
		_sendType = SendType.FULL;
		_objectId = player.getObjectId();

		_inv = new int[Inventory.PAPERDOLL_MAX][5];
		for(int PAPERDOLL_ID : Inventory.PAPERDOLL_ORDER)
		{
			_inv[PAPERDOLL_ID][0] = player.getInventory().getPaperdollObjectId(PAPERDOLL_ID);
			_inv[PAPERDOLL_ID][1] = player.getInventory().getPaperdollItemId(PAPERDOLL_ID);
			_inv[PAPERDOLL_ID][2] = player.getInventory().getPaperdollVariation1Id(PAPERDOLL_ID);
			_inv[PAPERDOLL_ID][3] = player.getInventory().getPaperdollVariation2Id(PAPERDOLL_ID);
			_inv[PAPERDOLL_ID][4] = player.getInventory().getPaperdollVisualId(PAPERDOLL_ID);
		}

		_item = null;
	}

	public ExUserInfoEquipSlot(Player player, ItemInstance item)
	{
		this(player);
		/*TODO: Раскомминтить, когда доделается UPDATE
		_sendType = SendType.UPDATE;
		_objectId = player.getObjectId();
		_inv = null;
		_item = item;*/
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_objectId);
		writeH(_inv.length);

		if(_sendType == SendType.FULL)
		{
			writeB(new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
			for(int[] inv : _inv)
			{
				writeH(18); // size
				writeD(inv[0]);
				writeD(inv[1]);
				writeH(inv[2]);
				writeH(inv[3]);
				writeD(inv[4]);
			}
		}
		else if(_sendType == SendType.UPDATE) // TODO
		{
			writeB(new byte[]{(byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00});
			if(_item.isEquipped())
			{
				writeH(18); // size
				writeD(_item.getObjectId());
				writeD(_item.getItemId());
				writeH(_item.getVariation1Id());
				writeH(_item.getVariation2Id());
				writeD(_item.getVisualId());
			}
		}
	}
}