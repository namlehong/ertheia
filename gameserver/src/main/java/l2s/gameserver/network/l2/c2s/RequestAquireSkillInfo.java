package l2s.gameserver.network.l2.c2s;

import l2s.commons.lang.ArrayUtils;
import l2s.gameserver.data.xml.holder.SkillAcquireHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.SkillLearn;
import l2s.gameserver.model.base.AcquireType;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.AcquireSkillInfoPacket;
import l2s.gameserver.network.l2.s2c.ExAcquireSkillInfo;
import l2s.gameserver.tables.SkillTable;

/**
 * Reworked: VISTALL
 */
public class RequestAquireSkillInfo extends L2GameClientPacket
{
	private int _id;
	private int _level;
	private AcquireType _type;

	@Override
	protected void readImpl()
	{
		_id = readD();
		_level = readD();
		_type = ArrayUtils.valid(AcquireType.VALUES, readD());
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null || player.isTransformed() || SkillTable.getInstance().getInfo(_id, _level) == null || _type == null)
			return;

		if(_type != AcquireType.NORMAL)
		{
			NpcInstance trainer = player.getLastNpc();
			if((trainer == null || player.getDistance(trainer.getX(), trainer.getY()) > Creature.INTERACTION_DISTANCE) && !player.isGM())
				return;
		}

		SkillLearn skillLearn = SkillAcquireHolder.getInstance().getSkillLearn(player, _id, _level, _type);
		if(skillLearn == null)
			return;

		if(_type == AcquireType.NORMAL)
			sendPacket(new ExAcquireSkillInfo(player, skillLearn));
		else
			sendPacket(new AcquireSkillInfoPacket(_type, skillLearn));
	}
}