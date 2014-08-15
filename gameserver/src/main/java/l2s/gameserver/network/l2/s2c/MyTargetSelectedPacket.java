package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;

public class MyTargetSelectedPacket extends L2GameServerPacket
{
	private int _objectId;
	private int _color;

	/**
	 * @param int objectId of the target
	 * @param int level difference to the target. name color is calculated from that
	 */
	public MyTargetSelectedPacket(int objectId, int color)
	{
		_objectId = objectId;
		_color = color;
	}

	public MyTargetSelectedPacket(Player player, GameObject target)
	{
		_objectId = target.getObjectId();
		if(target.isCreature())
			_color = player.getLevel() - ((Creature) target).getLevel();
		else
			_color = 0;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_objectId);
		writeH(_color);
		writeD(0x00);
	}
}