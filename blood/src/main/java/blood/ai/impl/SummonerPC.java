package blood.ai.impl;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.base.ClassLevel;
import blood.ai.EventFPC;
import blood.ai.impl.FPSkills.ArcanaLord;
import blood.ai.impl.FPSkills.ElementalMaster;
import blood.ai.impl.FPSkills.SpectralMaster;

public class SummonerPC extends EventFPC
{
	public SummonerPC(Player actor)
	{
		super(actor);
	}
	
	protected boolean isAllowClass()
	{
		return getActor().getClassId().isOfLevel(ClassLevel.SECOND) || getActor().getClassId().isOfLevel(ClassLevel.THIRD);
	}
	
	@Override
	public void prepareSkillsSetup() {
		_allowSelfBuffSkills.add(ArcanaLord.SKILL_SUMMON_BINDING_CUBIC);
		_allowSelfBuffSkills.add(ArcanaLord.SKILL_SERVITOR_SHARE);
		_allowSelfBuffSkills.add(ArcanaLord.SKILL_SPIRIT_SHARING);
		_allowSkills.add(ArcanaLord.SKILL_SUMMON_KAI_THE_CAT);
		_allowSkills.add(ElementalMaster.SKILL_SUMMON_MERROW_THE_UNICORN);
		_allowSelfBuffSkills.add(ElementalMaster.SKILL_SUMMON_LIFE_CUBIC);
		_allowSelfBuffSkills.add(SpectralMaster.SKILL_SUMMON_SPARK_CUBIC);
		_allowSkills.add(SpectralMaster.SKILL_SUMMON_SOULLESS);
		
	}
	
	protected Skill getNpcSuperiorBuff()
	{
		return getSkill(15649, 1); //warrior
	}

	protected boolean defaultSubFightTask(Creature target)
	{
		summonFightTask(target);
		return true;
	}
	
	protected boolean summonFightTask(Creature target)
	{
		Player player = getActor();
		
		if (player.getServitors().length > 0){
			for (Servitor summon: player.getServitors())
			{
				summon.getAI().Attack(target, true, false);
				double petDisatance = player.getDistance(summon);
				if(summon.getCurrentHpPercents() < 40){
					if(canUseSkill(ArcanaLord.SKILL_SERVITOR_BARRIER, summon, petDisatance))
						return tryCastSkill(ArcanaLord.SKILL_SERVITOR_BARRIER, target, petDisatance);
					if(canUseSkill(ArcanaLord.SKILL_SERVITOR_EMPOWERMENT, summon, petDisatance))
						return tryCastSkill(ArcanaLord.SKILL_SERVITOR_EMPOWERMENT, target, petDisatance);
				}
				if(summon.getCurrentHpPercents() < 80){
					if(canUseSkill(ArcanaLord.SKILL_SERVITOR_HEAL, summon, petDisatance))
						return tryCastSkill(ArcanaLord.SKILL_SERVITOR_HEAL, target, petDisatance);
				}
			}
		}
		
		double distance = player.getDistance(target);		
		
		if(canUseSkill(SpectralMaster.SKILL_DEATH_SPIKE, target, distance))
			return tryCastSkill(SpectralMaster.SKILL_DEATH_SPIKE, target, distance);
		
		return chooseTaskAndTargets(null, target, distance);
	}
	
}