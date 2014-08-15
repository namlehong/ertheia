package l2s.gameserver.skills.effects;

import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
 */
public final class i_delete_hate_of_me extends Effect
{
	public i_delete_hate_of_me(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public boolean checkCondition()
	{
		return getEffected().isMonster();
	}

	@Override
	public void onStart()
	{
		MonsterInstance monster = (MonsterInstance) getEffected();
		monster.getAggroList().remove(getEffector(), true);
		monster.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
	}

	@Override
	public boolean isHidden()
	{
		return true;
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}