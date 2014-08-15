package l2s.gameserver.skills.effects;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
import l2s.gameserver.stats.Env;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.templates.skill.EffectTemplate;

public class EffectCallSkills extends Effect
{
	public EffectCallSkills(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		int[] skillIds = getTemplate().getParam().getIntegerArray("skillIds");
		int[] skillLevels = getTemplate().getParam().getIntegerArray("skillLevels");

		for(int i = 0; i < skillIds.length; i++)
		{
			Skill skill = SkillTable.getInstance().getInfo(skillIds[i], skillLevels[i]);
			for(Creature cha : skill.getTargets(getEffector(), getEffected(), false))
				getEffector().broadcastPacket(new MagicSkillUse(getEffector(), cha, skillIds[i], skillLevels[i], 0, 0));
			getEffector().callSkill(skill, skill.getTargets(getEffector(), getEffected(), false), false);
		}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}