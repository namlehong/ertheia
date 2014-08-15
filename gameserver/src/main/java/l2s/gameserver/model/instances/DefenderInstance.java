package l2s.gameserver.model.instances;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Bonux
**/
public class DefenderInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public DefenderInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return attacker.isMonster();
	}

	@Override
	protected void onReduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp)
	{
		if(attacker.isMonster())
			getAggroList().addDamageHate(attacker, (int) damage, 0);

		super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp);
	}
}