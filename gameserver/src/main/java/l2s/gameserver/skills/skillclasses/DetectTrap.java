package l2s.gameserver.skills.skillclasses;

import java.util.List;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.World;
import l2s.gameserver.model.instances.TrapInstance;
import l2s.gameserver.network.l2.s2c.NpcInfoPacket;
import l2s.gameserver.skills.EffectType;
import l2s.gameserver.templates.StatsSet;

public class DetectTrap extends Skill
{
	public DetectTrap(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		for(Creature target : targets)
		{
			if(target != null && target.isTrap())
			{
				TrapInstance trap = (TrapInstance) target;
				if(trap.getLevel() <= getPower())
				{
					trap.setDetected(true);
					for(Player player : World.getAroundPlayers(trap))
						player.sendPacket(new NpcInfoPacket(trap, player));
				}
			}
			else if(isDetectPC() && target.isInvisible())
				target.getEffectList().stopEffects(EffectType.Invisible);
		}	

		if(isSSPossible())
			activeChar.unChargeShots(isMagic());

		super.useSkill(activeChar, targets);
	}
}