package l2s.gameserver.skills.skillclasses;

import java.util.List;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.templates.StatsSet;

public class DeleteHate extends Skill
{
	public DeleteHate(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		for(Creature target : targets)
			if(target != null)
			{

				if(target.isRaid())
					continue;

				if(getActivateRate() > 0)
				{
					if(activeChar.isPlayer())
					{
						Player player = (Player) activeChar;
						if(player.isGM())
							player.sendMessage(new CustomMessage("l2s.gameserver.skills.Formulas.Chance", player).addString(getName(player)).addNumber(getActivateRate()));
					}

					if(!Rnd.chance(getActivateRate()))
						return;
				}

				if(target.isNpc())
				{
					NpcInstance npc = (NpcInstance) target;
					npc.getAggroList().clear(false);
					npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
				}

				getEffects(activeChar, target, false);
			}

		super.useSkill(activeChar, targets);
	}
}
