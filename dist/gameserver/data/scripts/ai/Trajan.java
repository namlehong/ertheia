package ai;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.data.xml.holder.NpcHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Skill.SkillType;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.PositionUtils;

public class Trajan extends Fighter
{
	private static final int TRAJAN = 25785;
	private static final int[][] WALKING_ROUTES = { { 175528, -186334, -3801 }, { 175259, -185337, -3785 }, { 175748, -184471, -3801 }, { 176522, -184277, -3785 }, { 177048, -184824, -3801 }, { 176728, -186056, -3801 } };

	private static final int[] TRAJAN_BEETLES = { 18993, 18995 };
	private ScheduledFuture<?> _moveTask;
	private static ScheduledFuture<?> _eggWalkTask;
	private ScheduledFuture<?> _aggroTask;
	private ScheduledFuture<?> _doomTask;

	public Trajan(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();
		if(_moveTask != null)
			_moveTask.cancel(true);
		if(_eggWalkTask != null)
			_eggWalkTask.cancel(true);
		if(_aggroTask != null)
			_aggroTask.cancel(true);
		if(_doomTask != null)	
			_doomTask.cancel(true);
		
		if(killer.isPlayer() && killer.getPlayer().getParty() != null)
			for(Player member : killer.getPlayer().getParty().getPartyMembers())
				member.getInventory().addItem(17739, 1);
				
		actor.getReflection().startCollapseTimer(5 * 60 * 1000L);	
		setReenterTime(actor);
		actor.getReflection().addSpawnWithoutRespawn(33385, actor.getLoc(), 100);
		
		super.onEvtDead(killer);
	}
	
	public void setReenterTime(NpcInstance actor)
	{
		Calendar reenter;
		Calendar now = Calendar.getInstance();
		Calendar reenterNextDay = (Calendar)now.clone();
		reenterNextDay.add(5, 1);
		reenter = (Calendar)reenterNextDay.clone();
		for(Player player : actor.getReflection().getPlayers())
		{
			player.setInstanceReuse(160, reenter.getTimeInMillis());
		}		
	}

	@Override
	protected void onEvtSeeSpell(Skill skill, Creature caster)
	{
		NpcInstance actor = getActor();
		if((skill.isOffensive() || skill.getSkillType() == SkillType.AGGRESSION) && Rnd.get() <= 0.1)
		{
			for(Player player : actor.getReflection().getPlayers())
				if (PositionUtils.calculateDistance(player, actor, false) < 900.)
				{
					actor.getAggroList().addDamageHate(player, 999, 999);
					if(_moveTask != null)
						_moveTask.cancel(true);	
					_moveTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new TrajanWalkTask(actor), 120000L, 120000L);		
				}
		}
		super.onEvtSeeSpell(skill, caster);
	}	


	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		final NpcInstance actor = getActor();
		actor.setRandomWalk(false);
		_moveTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new TrajanWalkTask(actor), 0, 1000);
		
		_aggroTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl()
        {
			@Override
			public void runImpl()
			{
				for(Player player : actor.getReflection().getPlayers())
					if (PositionUtils.calculateDistance(player, actor, false) < 2500.)
					{
						if(_moveTask != null)
							_moveTask.cancel(true);	
						if(_eggWalkTask != null)
							_eggWalkTask.cancel(true);								
						_moveTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new TrajanWalkTask(actor), 60000, 1000);
						actor.getAggroList().addDamageHate(player, 999, 999);
						actor.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, player, 1000);

						break;
					}
			}
		}, 60000L, 120000L);
		
		_doomTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl()
        {
			@Override
			public void runImpl()
			{
				for(Player player : actor.getReflection().getPlayers())
					if (PositionUtils.calculateDistance(player, actor, false) < 2500.)
					{
						if((Rnd.get() <= 0.1D) && (!actor.isAttackingNow()) && (!actor.isCastingNow()))
						{
							if(_moveTask != null)
								_moveTask.cancel(true);	
							if(_eggWalkTask != null)
								_eggWalkTask.cancel(true);	
							actor.setTarget(player);
							actor.doCast(SkillTable.getInstance().getInfo(6268, 1), player, true);
							_moveTask = ThreadPoolManager.getInstance().schedule(new Trajan.TrajanWalkTask(actor), 10000L);
						}		
						else if (Rnd.get() <= 0.2D)
						{
							final NpcInstance doom = actor.getReflection().addSpawnWithoutRespawn(18998, new Location(player.getX(), player.getY(), player.getZ()), 0);
							doom.setTarget(player);
							doom.doCast(SkillTable.getInstance().getInfo(14113, 1), player, true);
							ThreadPoolManager.getInstance().schedule(new RunnableImpl()
							{
								@Override
								public void runImpl()
								{
									if(doom != null)
									{
										doom.setNpcState(2);
										doom.setNpcState(0);
										doom.deleteMe();
									}	
								}
							}
								, 15000L);
						}					
					}
			}} , 30000L, 10000L);	
	
	}


	public static class TrajanWalkTask extends RunnableImpl
	{
		private int _currentRoute = 0;
		public static NpcInstance _trajan;
		private static List<NpcInstance> _minions = new CopyOnWriteArrayList<NpcInstance>();

		public TrajanWalkTask(NpcInstance trajan)
		{
			_trajan = trajan;
			eggy();
		}

		public void eggy()
		{
			byte spawned = 0;
			for (NpcInstance npc : _trajan.getReflection().getNpcs())
			{
				if((PositionUtils.calculateDistance(_trajan, npc, false) < 2000.0D) && (npc.getNpcId() == 19023) && (spawned < 2) && (_minions.size() < 16))
				{
					NpcInstance eggy = _trajan.getReflection().addSpawnWithoutRespawn(TRAJAN_BEETLES[Rnd.get(TRAJAN_BEETLES.length)], new Location(npc.getX(), npc.getY(), npc.getZ()), 0);
					
					eggy.setRunning();
					_minions.add(eggy);
					spawned = (byte)(spawned + 1);
				}
			}

			if (_minions.size() > 0)
				_eggWalkTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new EggyWalkTask(), 0L, 1000L);
		}

		@Override
		public void runImpl()
		{
			if (_trajan == null || _trajan.isMoving)
				return;
			_trajan.setRunning();

			int[] route = WALKING_ROUTES[_currentRoute];
			DefaultAI ai = (DefaultAI) _trajan.getAI();
			ai.addTaskMove(Location.findPointToStay(new Location(route[0], route[1], route[2]), 250, _trajan.getGeoIndex()), true);

			_currentRoute += 1;
			if(_currentRoute > WALKING_ROUTES.length - 1)
				_currentRoute = 0;
		}
	}	

    public static class EggyWalkTask extends RunnableImpl
	{
		EggyWalkTask()
		{}
		
		@Override
		public synchronized void runImpl()
		{

			for(NpcInstance minion : TrajanWalkTask._minions)
				if ((minion != null) && (!minion.isAttackingNow()) && (!minion.isCastingNow()))
				{
					DefaultAI ai = (DefaultAI) minion.getAI();
					ai.addTaskMove(Location.findPointToStay(new Location(TrajanWalkTask._trajan.getX() + Rnd.get(-50, 50), TrajanWalkTask._trajan.getY() + Rnd.get(-50, 50), Trajan.TrajanWalkTask._trajan.getZ()), 50, minion.getGeoIndex()), true);	
				}	
		}
    }
}
