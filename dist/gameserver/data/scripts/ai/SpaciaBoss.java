package ai;

import java.util.concurrent.ScheduledFuture;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.Skill;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.ThreadPoolManager;
import l2s.commons.threading.RunnableImpl;

/**
 * @author cruel
 */
public class SpaciaBoss extends Fighter
{
	private ScheduledFuture<?> DeadTask;
	public SpaciaBoss(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}
	
	@Override
	protected void onEvtSpawn()
	{
		NpcInstance actor = getActor();
		DeadTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new SpawnMinion(),1000,30000);
		Reflection r = actor.getReflection();
		for(Player p : r.getPlayers())
			notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 2);
		super.onEvtSpawn();
		Skill fp = SkillTable.getInstance().getInfo(14190, 1);
		fp.getEffects(actor, actor, false, false);
	}
	
	@Override
	protected void onEvtDead(Creature killer)
	{
		if(DeadTask != null)
			DeadTask.cancel(true);
		getActor().getReflection().addSpawnWithoutRespawn(33385, getActor().getLoc(), 100);
		super.onEvtDead(killer);
	}
	
	public class SpawnMinion extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			NpcInstance actor = getActor();
			NpcInstance minion = actor.getReflection().addSpawnWithoutRespawn(25780, actor.getLoc(), 250);
			for(Player p : actor.getReflection().getPlayers())
				minion.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 2);
		}
	}
}
