package instances;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.model.*;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.templates.InstantZone;
import l2s.gameserver.utils.Location;

import java.util.*;
import java.util.concurrent.Future;


/**
 * @author HienSon
 */
public class GoddessOfDarkness extends Reflection
{
    private static final int MAIDEN_OF_DARKNESS = 41000;
    private static final int PRINCESS_OF_DARKNESS = 41001;
    private static final int QUEEN_OF_DARKNESS = 41002;
    private static final int GODDESS_OF_DARKNESS = 41003;
    
    private DeathListener _deathListener = new DeathListener();
    private int delay_after_spawn = 0;
    private Future<?> _expireTask;
    private Future<?> _collapseTask;
    
    @Override
    protected void onCreate()
    {
        super.onCreate();
        
        int bossID = GODDESS_OF_DARKNESS; //default spawn goddess
        Location bossLoc = new Location(48152, -12248, -9120, 0);
        
        switch (getInstancedZoneId())
        {
            case 300:
            	bossID = MAIDEN_OF_DARKNESS;
            break;
            case 301:
            	bossID = PRINCESS_OF_DARKNESS;
            break;
            case 302:
            	bossID = QUEEN_OF_DARKNESS;
            break;
            case 303:
            	bossID = GODDESS_OF_DARKNESS;
            break;
        }
        NpcInstance boss = addSpawnWithoutRespawn(bossID, bossLoc, 0);
        boss.addListener(_deathListener);
        
        InstantZone iz = getInstancedZone();
		if(iz != null)
		{
			int time_limit = iz.getTimelimit() * 1000 * 60;
			delay_after_spawn = time_limit / 3;
			startInstanceTimer(time_limit - delay_after_spawn); 
		}
        
    }

	public void startInstanceTimer(long timeInMillis)
	{
		if(_expireTask != null)
		{
			_expireTask.cancel(false);
			_expireTask = null;
		}

		_expireTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl()
		{
			@Override
			public void runImpl()
			{
				try
				{
					for(Spawner s : GoddessOfDarkness.this.getSpawns().toArray(new Spawner[GoddessOfDarkness.this.getSpawns().size()]))
						s.deleteAll();

					getSpawns().clear();

					List<GameObject> delete = new ArrayList<GameObject>();
					lock.lock();
					try
					{
						for(GameObject o : _objects)
							if(!o.isPlayable())
								delete.add(o);
					}
					finally
					{
						lock.unlock();
					}

					for(GameObject o : delete)
						o.deleteMe();

					for(Player p : getPlayers())
					{
						if(p != null)
						{
							p.getPlayer().sendPacket(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(delay_after_spawn / 60000));
	
						}
						else
							collapse();
					}
					
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}, timeInMillis);
	}

	public void stopInstanceTimer()
	{
		if(_expireTask != null)
		{
			_expireTask.cancel(false);
			_expireTask = null;
		}
	}
    
	public class DeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature self, Creature killer)
		{
			int minuteToCollapse = 1;
			broadcastCollapseInstance(killer, minuteToCollapse);
			stopInstanceTimer();
			_collapseTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl()
			{
				@Override
				public void runImpl()
				{
					try
					{
						collapse();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}, minuteToCollapse*60000);
		}
		
		private void broadcastCollapseInstance(Creature _killer, int duration)
		{
			Player killer = (Player) _killer;
			
			if(killer != null)
			{
				Party party = killer.getParty();
				if(party != null)
				{
					for(Player p : party.getPartyMembers())
					{
						p.sendPacket(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(duration));

					}
				}
			}
			
			
		}
	}


}