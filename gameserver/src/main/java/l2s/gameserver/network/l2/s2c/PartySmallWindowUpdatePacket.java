package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;

public class PartySmallWindowUpdatePacket extends L2GameServerPacket
{
	private int obj_id, class_id, level;
	private int curCp, maxCp, curHp, maxHp, curMp, maxMp, vitality;
	private String obj_name;

	public PartySmallWindowUpdatePacket(Player member)
	{
		obj_id = member.getObjectId();
		obj_name = member.getName();
		curCp = (int) member.getCurrentCp();
		maxCp = member.getMaxCp();
		curHp = (int) member.getCurrentHp();
		maxHp = member.getMaxHp();
		curMp = (int) member.getCurrentMp();
		maxMp = member.getMaxMp();
		level = member.getLevel();
		class_id = member.getClassId().getId();
		vitality = member.getVitality();
	}

	@Override
	protected final void writeImpl()
	{
		writeD(obj_id);
		//TODO: [Bonux] Переделать в динамическую структуру.
		writeH(0x3FF);
		//writeS(obj_name);
		writeD(curCp);
		writeD(maxCp);
		writeD(curHp);
		writeD(maxHp);
		writeD(curMp);
		writeD(maxMp);
		writeC(level);
		writeH(class_id);
		writeD(vitality);
	}
}