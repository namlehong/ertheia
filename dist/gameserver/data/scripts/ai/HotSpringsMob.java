package ai;

import java.util.List;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.Mystic;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.tables.SkillTable;

/**
 * AI for:
 * Hot Springs Atrox (id 21321)
 * Hot Springs Atroxspawn (id 21317)
 * Hot Springs Bandersnatch (id 21322)
 * Hot Springs Bandersnatchling (id 21314)
 * Hot Springs Flava (id 21316)
 * Hot Springs Nepenthes (id 21319)
 *
 * @author Diamond
 */
public class HotSpringsMob extends Mystic
{
	private static final int[] DEBUFF_IDS = { 4554, 4552 };

	public HotSpringsMob(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
		if(attacker != null && Rnd.chance(5))
		{
			int debuffId = DEBUFF_IDS[Rnd.get(DEBUFF_IDS.length)];
			int level = 0;

			for(Effect effect : attacker.getEffectList().getEffects())
			{
				if(effect.getSkill().getId() == debuffId)
				{
					level = effect.getSkill().getLevel();
					break;
				}
			}

			if(level == 0)
			{
				Skill skill = SkillTable.getInstance().getInfo(debuffId, 1);
				if(skill != null)
					skill.getEffects(actor, attacker, false, false);
			}
			else if(level < 10)
			{
				Skill skill = SkillTable.getInstance().getInfo(debuffId, level + 1);
				if(skill != null)
					skill.getEffects(actor, attacker, false, false);
			}
		}
		super.onEvtAttacked(attacker, damage);
	}
}