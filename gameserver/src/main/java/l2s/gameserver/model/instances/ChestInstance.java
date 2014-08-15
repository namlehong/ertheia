package l2s.gameserver.model.instances;

import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.templates.npc.NpcTemplate;

public class ChestInstance extends MonsterInstance
{
	private static final long serialVersionUID = 1L;

	public ChestInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	public void tryOpen(Player opener, Skill skill)
	{
		getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, opener, 100);
	}

	@Override
	public boolean canChampion()
	{
		return false;
	}
}