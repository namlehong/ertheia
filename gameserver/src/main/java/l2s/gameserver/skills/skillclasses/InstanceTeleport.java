package l2s.gameserver.skills.skillclasses;

import java.util.List;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.SymbolInstance;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.utils.Location;

public class InstanceTeleport extends Skill
{
	public InstanceTeleport(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		if(!activeChar.isPlayer())
			return;

		Player player = activeChar.getPlayer();
		SymbolInstance symbol = player.getSymbol();
		
		if(symbol == null)
			return;
		
		Location loc = symbol.getLoc();
		player.teleToLocation(loc);
		
		super.useSkill(activeChar, targets);
	}
}
