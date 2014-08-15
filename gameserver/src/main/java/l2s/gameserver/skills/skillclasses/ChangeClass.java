package l2s.gameserver.skills.skillclasses;

import java.util.List;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Zone.ZoneType;
import l2s.gameserver.templates.StatsSet;

public class ChangeClass extends Skill
{
	private int _classIndex;

	public ChangeClass(StatsSet set)
	{
		super(set);
		_classIndex = set.getInteger("class_index");
	}

	@Override
	public void useSkill(Creature caster, List<Creature> targets)
	{
		Player activeChar = caster.getPlayer();
		
		if(activeChar.isInZone(ZoneType.epic))
		{
			activeChar.sendMessage("you cannot change class while in epic zone");
			return;
		}
		
		activeChar.changeClass(_classIndex);

		if(isSSPossible())
			caster.unChargeShots(isMagic());

		super.useSkill(activeChar, targets);
	}
}