package l2s.gameserver.skills.skillclasses;

import java.util.List;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.ChestInstance;
import l2s.gameserver.model.instances.DoorInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.templates.StatsSet;

public class Unlock extends Skill
{
	private final int _unlockPower;

	public Unlock(StatsSet set)
	{
		super(set);
		_unlockPower = set.getInteger("unlockPower", 0) + 100;
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(target == null || target instanceof ChestInstance && target.isDead())
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return false;
		}

		if(target instanceof ChestInstance && activeChar.isPlayer())
			return super.checkCondition(activeChar, target, forceUse, dontMove, first);

		if(!target.isDoor() || _unlockPower == 0)
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return false;
		}

		DoorInstance door = (DoorInstance) target;

		if(door.isOpen())
		{
			activeChar.sendPacket(SystemMsg.IT_IS_NOT_LOCKED);
			return false;
		}

		if(!door.isUnlockable())
		{
			activeChar.sendPacket(SystemMsg.THIS_DOOR_CANNOT_BE_UNLOCKED);
			return false;
		}

		if(door.getKey() > 0) // ключ не подходит к двери
		{
			activeChar.sendPacket(SystemMsg.THIS_DOOR_CANNOT_BE_UNLOCKED);
			return false;
		}

		if(_unlockPower - door.getLevel() * 100 < 0) // Дверь слишком высокого уровня
		{
			activeChar.sendPacket(SystemMsg.THIS_DOOR_CANNOT_BE_UNLOCKED);
			return false;
		}

		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		for(Creature targ : targets)
			if(targ != null)
			{
				if(targ.isDoor())
				{
					DoorInstance target = (DoorInstance) targ;
					if(!target.isOpen() && (target.getKey() > 0 || Rnd.chance(_unlockPower - target.getLevel() * 100)))
					{
						target.openMe((Player) activeChar, true);
					}
					else
						activeChar.sendPacket(SystemMsg.YOU_HAVE_FAILED_TO_UNLOCK_THE_DOOR);
				}
				else if(targ instanceof ChestInstance)
				{
					ChestInstance target = (ChestInstance) targ;
					if(!target.isDead())
						target.tryOpen((Player) activeChar, this);
				}
			}

		super.useSkill(activeChar, targets);
	}
}