package blood.ai.impl;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.Skill;

public class FPCWynn extends SummonerPC
{
	public final int SKILL_MARK_OF_VOID 	= 11260;
	public final int SKILL_MARK_OF_WEAKNESS = 11259;
	public final int SKILL_MARK_OF_TRICK	= 11262;
	public final int SKILL_MARK_OF_PLAGUE	= 11261;
	public final int SKILL_RETRIVE_MARK		= 11271;
	
	public final int SKILL_EXILE			= 11273;
	
	public final int SKILL_SERVITOR_GHASTE	= 11347;
	public final int SKILL_SERVITOR_GDW		= 11348;
	public final int SKILL_SERVITOR_GMIGHT	= 11349;
	
	public final int SKILL_SERVITOR_GIANTS_BLESSING = 11297;
	public final int SKILL_SERVITOR_UD		= 11310;
	public final int SKILL_SERVITOR_BLESSING= 11309;
	public final int SKILL_SERVITOR_EMPOWER	= 11306;
	public final int SKILL_SERVITOR_MIGHT	= 11307;
	public final int SKILL_SERVITOR_WINDWALK= 11308;
	public final int SKILL_SERVITOR_HASTE	= 11304;
	public final int SKILL_SERVITOR_BARRIER = 11303;
	public final int SKILL_SERVITOR_SHIELD	= 11305;
	public final int SKILL_ULTIMATE_TPAIN	= 11270;
	public final int SKILL_TPAIN			= 1262;
	public final int SKILL_ULTIMATE_SHARE	= 11288;
	
	public final int SKILL_SUMMON_KAI		= 11320;
	public final int SKILL_SUMMON_KING		= 11321;
	public final int SKILL_SUMMON_QUEEN		= 11322;
	public final int SKILL_SUMMON_MERROW	= 11329;
	public final int SKILL_SUMMON_NIGHTSHADE= 11338;
	
	public final int SKILL_SERVITOR_HEAL	= 11302;
	public final int SKILL_SERVITOR_MASS_HEAL	= 11269;
	
	public final int SKILL_AVENGING_CUBIC 	= 11268;
	public final int[] SKILL_SMART_CUBIC = new int[] {779, 780, 781, 782, 783};
	
	public final int SKILL_WYNN_AURA 		= 1937;
	
	public FPCWynn(Player actor)
	{
		super(actor);
	}
	
	@Override
	public void prepareSkillsSetup() {
		_allowSkills.add(SKILL_AVENGING_CUBIC);
		_allowSkills.add(SKILL_SUMMON_QUEEN);
		_allowSkills.add(SKILL_SUMMON_MERROW);
		_allowSkills.add(SKILL_SUMMON_NIGHTSHADE);
		
		_allowSelfBuffSkills.add(SKILL_ULTIMATE_SHARE);
		_allowSelfBuffSkills.add(SKILL_ULTIMATE_TPAIN);
		_allowSelfBuffSkills.add(SKILL_WYNN_AURA);
		
		_allowServitorBuffSkills.add(SKILL_SERVITOR_GHASTE);
		_allowServitorBuffSkills.add(SKILL_SERVITOR_GDW);
		_allowServitorBuffSkills.add(SKILL_SERVITOR_GMIGHT);
	}
	
	@Override
	protected int getMaxSummon()
	{
		return 2;
	}
	
	protected boolean defaultSubFightTask(Creature target)
	{
		summonFightTask(target);
		return true;
	}
	
	protected boolean summonFightTask(Creature target)
	{
		Player player = getActor();
		
		boolean doHeal = false;
		boolean doUD = false;
		
		if (player.getServitors().length > 0){
			for (Servitor summon: player.getServitors())
			{
//				summon.getAI().Attack(target, true, false);
				if(summon.getCurrentHpPercents() < 80){
					doHeal = true;
					tryCastSkill(SKILL_SERVITOR_HEAL, summon);
				}
					
				if(summon.getCurrentHpPercents() < 40){
					doUD = true;
				}
					
			}
		}
		
		double distance = player.getDistance(target);
//		double targetHp = target.getCurrentHpPercents();
//		double actorHp = actor.getCurrentHpPercents();
		
		Skill skillMarkOfVoid = player.getKnownSkill(SKILL_MARK_OF_VOID);
		Skill skillMarkOfWeakness = player.getKnownSkill(SKILL_MARK_OF_WEAKNESS);
		Skill markOfTrickSkill = player.getKnownSkill(SKILL_MARK_OF_TRICK);
		Skill markOfPlagueSkill = player.getKnownSkill(SKILL_MARK_OF_PLAGUE);
		Skill retriveMarkSkill = player.getKnownSkill(SKILL_RETRIVE_MARK);
		Skill skillUD = player.getKnownSkill(SKILL_SERVITOR_UD);
		Skill healSkill = player.getKnownSkill(SKILL_SERVITOR_MASS_HEAL);
		Skill exileSkill = player.getKnownSkill(SKILL_EXILE);
		
		int markCount = 0;
		
		if(doUD && canUseSkill(exileSkill, target, distance))
			return chooseTaskAndTargets(exileSkill, target, distance);
		
		if(doUD && canUseSkill(skillUD, target))
			return chooseTaskAndTargets(skillUD, target, distance);
		
		if(doHeal && canUseSkill(healSkill, target))
			return chooseTaskAndTargets(healSkill, target, distance);
		
		if(canUseSkill(skillMarkOfVoid, target, distance))
			return chooseTaskAndTargets(skillMarkOfVoid, target, distance);
		
		if(canUseSkill(skillMarkOfWeakness, target, distance))
			return chooseTaskAndTargets(skillMarkOfWeakness, target, distance);
		
		if(canUseSkill(markOfPlagueSkill, target, distance))
			return chooseTaskAndTargets(markOfPlagueSkill, target, distance);
		
		if(canUseSkill(markOfTrickSkill, target, distance))
			return chooseTaskAndTargets(markOfTrickSkill, target, distance);
		
		if(target.getEffectList().getEffectsCount(markOfPlagueSkill) > 0)
			markCount++;
		if(target.getEffectList().getEffectsCount(markOfTrickSkill) > 0)
			markCount++;
		if(target.getEffectList().getEffectsCount(skillMarkOfWeakness) > 0)
			markCount++;
		
		if(markCount > 1 && canUseSkill(retriveMarkSkill, target, distance))
			return chooseTaskAndTargets(retriveMarkSkill, target, distance);
		
		tryMoveToTarget(target, skillMarkOfWeakness.getCastRange()/2);
		
		return false;
	}
	
}

