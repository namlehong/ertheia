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
 * @author Bonux
**/
public class TreeInstance extends SummonInstance
{
	private class HealTask extends RunnableImpl
	{
		private Skill _skill;

		public HealTask(Skill skill)
		{
			_skill = skill;
		}

		@Override
		public void runImpl() throws Exception
		{
			TimeStamp ts = getSkillReuse(_skill);
			if(ts != null && ts.getReuseCurrent() > 0)
				return;

			getAI().Cast(_skill, TreeInstance.this, false, false);
		}
	}

	private static final long serialVersionUID = 1L;
	private static final int HEAL_SKILL_ID = 11806;

	private ScheduledFuture<?> _healTask;

	public TreeInstance(int objectId, NpcTemplate template, Player owner, int lifetime, int consumeid, int consumecount, int consumedelay, int summonPoints, Skill skill, boolean saveable)
	{
		super(objectId, template, owner, lifetime, consumeid, consumecount, consumedelay, summonPoints, skill, saveable);
	}

	@Override
	protected void onSpawn()
	{
		super.onSpawn();

		int skillLevel = PetSkillsTable.getInstance().getAvailableLevel(this, HEAL_SKILL_ID);
		if(skillLevel <= 0)
			return;

		int serverSkillLevel = skillLevel;
		if(skillLevel >= 100)
		{
			EnchantSkillLearn sl = SkillTreeTable.getSkillEnchant(HEAL_SKILL_ID, skillLevel);
			if(sl == null)
				return;

			serverSkillLevel = SkillTreeTable.convertEnchantLevel(sl.getBaseLevel(), skillLevel, sl.getMaxLevel());
		}

		Skill skill = SkillTable.getInstance().getInfo(HEAL_SKILL_ID, serverSkillLevel);
		if(skill == null)
			return;

		_healTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new HealTask(skill), 0L, 1000L);
	}

	@Override
	protected void onDelete()
	{
		stopHealTask();
		super.onDelete();
	}

	@Override
	public boolean isImmobilized()
	{
		return true;
	}

	private void stopHealTask()
	{
		if(_healTask != null)
		{
			_healTask.cancel(false);
			_healTask = null;
		}
	}
}