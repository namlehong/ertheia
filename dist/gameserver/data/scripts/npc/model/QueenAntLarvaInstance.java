package npc.model;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

public class QueenAntLarvaInstance extends MonsterInstance
{
	private static final long serialVersionUID = 1L;

	public QueenAntLarvaInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void reduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflectAndAbsorb, boolean transferDamage, boolean isDot, boolean sendReceiveMessage)
	{
		damage = getCurrentHp() - damage > 1 ? damage : getCurrentHp() - 1;
		super.reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflectAndAbsorb, transferDamage, isDot, sendReceiveMessage);
	}

	@Override
	public boolean canChampion()
	{
		return false;
	}
	
	@Override
	public boolean isImmobilized()
	{
		return true;
	}
}