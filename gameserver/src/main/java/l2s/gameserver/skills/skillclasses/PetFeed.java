package l2s.gameserver.skills.skillclasses;

import java.util.List;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.player.Mount;
import l2s.gameserver.model.instances.PetInstance;
import l2s.gameserver.templates.StatsSet;

public class PetFeed extends Skill
{
	private int[] _feedPower;

	public PetFeed(StatsSet set)
	{
		super(set);
		_feedPower = set.getIntegerArray("feed_power");
	}

	@Override
	public boolean checkCondition(final Creature activeChar, final Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		//TODO: [Bonux] проверить условия.
		if(!target.isPet() && !(target.isPlayer() && target.getPlayer().isMounted()))
			return false;

		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		for(Creature target : targets)
		{
			if(target == null)
				continue;

			if(target.isPet())
			{
				PetInstance pet = (PetInstance) target;
				int feedPowerIndx = Math.min(_feedPower.length - 1, pet.getFormId());
				int power = _feedPower[feedPowerIndx];
				pet.setCurrentFed(pet.getCurrentFed() + power, true);
				pet.sendStatusUpdate();
			}
			else if(target.isPlayer() && target.getPlayer().isMounted())
			{
				Mount mount = target.getPlayer().getMount();
				int feedPowerIndx = Math.min(_feedPower.length - 1, mount.getFormId());
				int power = _feedPower[feedPowerIndx];
				mount.setCurrentFeed(mount.getCurrentFeed() + power);
				mount.updateStatus();
			}
			else
				continue;

			getEffects(activeChar, target, false);
		}

		if(isSSPossible())
			activeChar.unChargeShots(isMagic());

		super.useSkill(activeChar, targets);
	}
}
