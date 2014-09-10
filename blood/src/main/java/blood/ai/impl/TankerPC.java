package blood.ai.impl;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.ClassType2;
import blood.ai.EventFPC;
import blood.ai.impl.FPSkills.EvasTemplar;
import blood.ai.impl.FPSkills.HellKnight;
import blood.ai.impl.FPSkills.PhoenixKnight;
import blood.ai.impl.FPSkills.ShillienTemplar;

public class TankerPC extends EventFPC
{
	public TankerPC(Player actor)
	{
		super(actor);
	}
	
	protected boolean isAllowClass()
	{
		return getActor().getClassId().isOfLevel(ClassLevel.SECOND) || getActor().getClassId().isOfLevel(ClassLevel.THIRD);
	}

	public void prepareSkillsSetup() {
		_allowSelfBuffSkills.add(PhoenixKnight.SKILL_MAJESTY);
		_allowSelfBuffSkills.add(PhoenixKnight.SKILL_DEFLECT_ARROW);
		_allowSelfBuffSkills.add(PhoenixKnight.SKILL_DEFLECT_MAGIC);
		_allowSelfBuffSkills.add(PhoenixKnight.SKILL_PHYSICAL_MIRROR);
		_allowSelfBuffSkills.add(PhoenixKnight.SKILL_IRON_SHIELD);
		_allowSelfBuffSkills.add(PhoenixKnight.SKILL_SPIRIT_OF_PHOENIX);
		_allowSelfBuffSkills.add(HellKnight.SKILL_SEED_OF_REVENGE);
		_allowSelfBuffSkills.add(HellKnight.SKILL_SHIELD_OF_REVENGE);
		_allowSelfBuffSkills.add(HellKnight.SKILL_REFLECT_DAMAGE);
		_allowSkills.add(EvasTemplar.SKILL_EVAS_WILL);
		_allowSelfBuffSkills.add(ShillienTemplar.SKILL_MAGICAL_MIRROR);
		_allowSelfBuffSkills.add(ShillienTemplar.SKILL_PAIN_OF_SHILEN);
		
		_allowSkills.add(ShillienTemplar.SKILL_SUMMON_VIPER_CUBIC);
		_allowSkills.add(ShillienTemplar.SKILL_SUMMON_VAMPIRIC_CUBIC);
		_allowSkills.add(ShillienTemplar.SKILL_SUMMON_PHANTOM_CUBIC);
		_allowSkills.add(EvasTemplar.SKILL_SUMMON_LIFE_CUBIC);
		_allowSkills.add(EvasTemplar.SKILL_SUMMON_ATTRACTIVE_CUBIC);
		_allowSkills.add(EvasTemplar.SKILL_SUMMON_STORM_CUBIC);
	}
	
	protected boolean defaultSubFightTask(Creature target)
	{
		tankerFightTask(target);
		return true;
	}

	protected boolean tankerFightTask(Creature target)
	{
		Player actor = getActor();
		
		double distance = actor.getDistance(target);
		
		return chooseTaskAndTargets(null, target, distance);
	}
	
	@Override
	protected void protectMember(Creature attacked, Creature attacker, int damage)
	{
		if(!attacked.isPlayer())
			return;
		
		Player player = getActor();
		
		thinkBuff();
		
		double distance = player.getDistance(attacker);
		
		if(distance > 1000)
			return;
		
		Player member = attacked.getPlayer();
		
		if(!member.getClassId().isOfType2(ClassType2.HEALER) && member.getCurrentHpPercents() > 80)
			return;
		
		if(member.getClassId().isOfType2(ClassType2.HEALER))
			setAttackTarget(attacker);
		
		if(canUseSkill(PhoenixKnight.SKILL_AGGRESSION, attacker, distance) 
				&& tryCastSkill(PhoenixKnight.SKILL_AGGRESSION, attacker, distance))
			return;
		
		if(canUseSkill(PhoenixKnight.SKILL_SHACKLE, attacker, distance) 
				&& tryCastSkill(PhoenixKnight.SKILL_SHACKLE, attacker, distance))
			return;
		
		if(canUseSkill(PhoenixKnight.SKILL_SHIELD_STUN, attacker, distance) 
				&& tryCastSkill(PhoenixKnight.SKILL_SHIELD_STUN, attacker, distance))
			return;
		
		if(canUseSkill(PhoenixKnight.SKILL_SHIELD_SLAM, attacker, distance) 
				&& tryCastSkill(PhoenixKnight.SKILL_SHIELD_SLAM, attacker, distance))
			return;
		
		if(canUseSkill(ShillienTemplar.SKILL_SHIELD_BASH, attacker, distance) 
				&& tryCastSkill(ShillienTemplar.SKILL_SHIELD_BASH, attacker, distance))
			return;
		
	}
	
}