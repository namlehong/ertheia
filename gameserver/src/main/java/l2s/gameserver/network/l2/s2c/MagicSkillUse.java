package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Creature;

/**
 * Format:   dddddddddh [h] h [ddd]
 * Пример пакета:
 * 48
 * 86 99 00 4F  86 99 00 4F
 * EF 08 00 00  01 00 00 00
 * 00 00 00 00  00 00 00 00
 * F9 B5 FF FF  7D E0 01 00  68 F3 FF FF
 * 00 00 00 00
 */
public class MagicSkillUse extends L2GameServerPacket
{
	public static final int NONE = -1;

	private final int _targetId;
	private final int _skillId;
	private final int _skillLevel;
	private final int _hitTime;
	private final int _reuseDelay;
	private final int _chaId, _x, _y, _z, _tx, _ty, _tz;
	private final boolean _isDoubleCasting;
	private int _reuseSkillId;
	private boolean _isServitorSkill;
	private int _actionId;

	public MagicSkillUse(Creature cha, Creature target, int skillId, int skillLevel, int hitTime, long reuseDelay, boolean isDoubleCasting, boolean isServitorSkill, int actionId)
	{
		_chaId = cha.getObjectId();
		_targetId = target.getObjectId();
		_skillId = skillId;
		_skillLevel = skillLevel;
		_hitTime = hitTime;
		_reuseDelay = (int) reuseDelay;
		_x = cha.getX();
		_y = cha.getY();
		_z = cha.getZ();
		_tx = target.getX();
		_ty = target.getY();
		_tz = target.getZ();
		_reuseSkillId = skillId; //TODO: [Bonux]
		_isServitorSkill = isServitorSkill;
		_actionId = actionId;
		_isDoubleCasting = isDoubleCasting;
	}

	public MagicSkillUse(Creature cha, Creature target, int skillId, int skillLevel, int hitTime, long reuseDelay, boolean isDoubleCasting)
	{
		this(cha, target, skillId, skillLevel, hitTime, reuseDelay, isDoubleCasting, false, 0);
	}

	public MagicSkillUse(Creature cha, Creature target, int skillId, int skillLevel, int hitTime, long reuseDelay)
	{
		this(cha, target, skillId, skillLevel, hitTime, reuseDelay, false, false, 0);
	}

	public MagicSkillUse(Creature cha, int skillId, int skillLevel, int hitTime, long reuseDelay)
	{
		this(cha, cha, skillId, skillLevel, hitTime, reuseDelay, false, false, 0);
	}

	public void setReuseSkillId(int id)
	{
		_reuseSkillId = id;
	}

	public void setServitorSkillInfo(int actionId)
	{
		_isServitorSkill = true;
		_actionId = actionId;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_isDoubleCasting ? 1 : 0);
		writeD(_chaId);
		writeD(_targetId);
		writeD(_skillId);
		writeD(_skillLevel);
		writeD(_hitTime);
		writeD(_reuseSkillId);
		writeD(_reuseDelay);
		writeD(_x);
		writeD(_y);
		writeD(_z);

		writeD(0x00); // unknown size TODO
		/*for(int i = 0; i < size; i++)
		{
			writeH(0); //???
		}*/

		writeD(_tx);
		writeD(_ty);
		writeD(_tz);
		writeD(_isServitorSkill ? 0x01 : 0x00); // is Pet Skill
		writeD(_actionId); // Social Action ID
	}
}