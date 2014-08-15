package l2s.gameserver.skills.effects;

import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public final class EffectGrow extends Effect
{
	public EffectGrow(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(_effected.isNpc())
		{
			NpcInstance npc = (NpcInstance) _effected;
			npc.setCollisionHeightModifier(1.24);
			npc.setCollisionRadiusModifier(1.19);
		}
	}

	@Override
	public void onExit()
	{
		super.onExit();
		if(_effected.isNpc())
		{
			NpcInstance npc = (NpcInstance) _effected;
			npc.setCollisionHeightModifier(1.0);
			npc.setCollisionRadiusModifier(1.0);
		}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}