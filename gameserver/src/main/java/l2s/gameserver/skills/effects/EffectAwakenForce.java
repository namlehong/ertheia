package l2s.gameserver.skills.effects;

import java.util.List;

import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.skills.EffectType;
import l2s.gameserver.stats.Env;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.templates.skill.EffectTemplate;

public class EffectAwakenForce extends Effect
{
	private static final int SIGEL_SKILL_ID = 1928;
	private static final int TYR_SKILL_ID = 1930;
	private static final int ODAL_SKILL_ID = 1932;
	private static final int AEORE_SKILL_ID = 1934;
	private static final int FEO_SKILL_ID = 1936;
	private static final int WYNN_SKILL_ID = 1938;
	private static final int ALGIZA_SKILL_ID = 1940;
	
	private static final int SOLIDARITY_SKILL_ID = 1955;
	
	public EffectAwakenForce(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	public void checkSolidarity()
	{
		int skillId = getSkill().getId();
		int solidarityLevel = 0;
		int i = 0;
		for(Effect effect : _effected.getEffectList().getEffects())
		{
			if(effect.getEffectType() != EffectType.AwakenForce)
				continue;

			int effectSkillId = effect.getSkill().getId();
			if(effectSkillId == SIGEL_SKILL_ID || effectSkillId == TYR_SKILL_ID || effectSkillId == ODAL_SKILL_ID || effectSkillId == AEORE_SKILL_ID || effectSkillId == FEO_SKILL_ID || effectSkillId == WYNN_SKILL_ID || effectSkillId == ALGIZA_SKILL_ID)
				i++;
		}
		
		_effected.getEffectList().stopEffects(SOLIDARITY_SKILL_ID); //first we stop it then compute the new one
		
		if(i >= 0 && i <= 2)
		{
			solidarityLevel = 0; //nothing
			return;
		}	
		else if(i == 3 || i == 4)
			solidarityLevel = 1;
		else if(i == 5)
			solidarityLevel = 2;
		else if(i >= 6)
			solidarityLevel = 3;	
		
		SkillTable.getInstance().getInfo(SOLIDARITY_SKILL_ID, solidarityLevel).getEffects(getEffector(), getEffected(), false);		
	}

	@Override
	public void onStart()
	{
		super.onStart();
		checkSolidarity();
	}

	@Override
	public void onExit()
	{
		super.onExit();
		checkSolidarity();
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
	
}