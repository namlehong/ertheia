package npc.model;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

public class CrawlerInstance extends MonsterInstance
{
	private static final long serialVersionUID = 1L;

	public CrawlerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	protected void onReduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp)
	{
		if(attacker.isNpc())
			return;
		super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp);	
	}
}