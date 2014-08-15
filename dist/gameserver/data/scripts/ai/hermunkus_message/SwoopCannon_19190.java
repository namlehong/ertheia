package ai.hermunkus_message;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.tables.SkillTable;

/**
 * @author : Ragnarok
 * @date : 30.03.12  18:58
 */
public class SwoopCannon_19190 extends DefaultAI
{
	private static final int SKILL_ID = 16023;

	public SwoopCannon_19190(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		addTimer(1, 1000 + Rnd.get(500));
	}

	@Override
	protected void onEvtTimer(int timer_id, Object arg1, Object arg2)
	{
		super.onEvtTimer(timer_id, arg1, arg2);

		if(timer_id == 1)
		{
			if(!isActive())
				return;

			Skill skill = SkillTable.getInstance().getInfo(SKILL_ID, 1);
			addTaskBuff(getActor(), skill);
			addTimer(1, skill.getHitTime() + 10000);
		}
	}
}