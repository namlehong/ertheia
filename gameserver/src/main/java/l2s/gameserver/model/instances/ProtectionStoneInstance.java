package l2s.gameserver.model.instances;

import java.util.concurrent.ScheduledFuture;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.base.EnchantSkillLearn;
import l2s.gameserver.skills.TimeStamp;
import l2s.gameserver.tables.PetSkillsTable;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.tables.SkillTreeTable;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Nam Le
**/
public class ProtectionStoneInstance extends SummonInstance
{
	private class BuffTask extends RunnableImpl
	{
		private Skill _skill;

		public BuffTask(Skill skill)
		{
			_skill = skill;
		}

		@Override
		public void runImpl() throws Exception
		{
			TimeStamp ts = getSkillReuse(_skill);
			if(ts != null && ts.getReuseCurrent() > 0)
				return;

			getAI().Cast(_skill, ProtectionStoneInstance.this, false, false);
		}
	}

	private static final long serialVersionUID = 1L;
	private static final int BUFF_SKILL_ID = 11360;

	private ScheduledFuture<?> _buffTask;

	public ProtectionStoneInstance(int objectId, NpcTemplate template, Player owner, int lifetime, int consumeid, int consumecount, int consumedelay, int summonPoints, Skill skill, boolean saveable)
	{
		super(objectId, template, owner, lifetime, consumeid, consumecount, consumedelay, summonPoints, skill, saveable);
	}

	@Override
	protected void onSpawn()
	{
		super.onSpawn();

		int skillLevel = PetSkillsTable.getInstance().getAvailableLevel(this, BUFF_SKILL_ID);
		if(skillLevel <= 0)
			return;

		Skill skill = SkillTable.getInstance().getInfo(BUFF_SKILL_ID, skillLevel);
		if(skill == null)
			return;

		_buffTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new BuffTask(skill), 0L, 1000L);
	}

	@Override
	protected void onDelete()
	{
		stopBuffTask();
		super.onDelete();
	}

	@Override
	public boolean isImmobilized()
	{
		return true;
	}

	private void stopBuffTask()
	{
		if(_buffTask != null)
		{
			_buffTask.cancel(false);
			_buffTask = null;
		}
	}
}