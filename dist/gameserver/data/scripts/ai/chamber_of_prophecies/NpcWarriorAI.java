package ai.chamber_of_prophecies;

import java.util.List;

import org.dom4j.tree.LazyList;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.DecoyInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.Location;
/**
 * @author Hien Son
 */
public class NpcWarriorAI extends DefaultAI
{
	Creature attackTarget = null;
	List<Player> targetPlayers = null;
	int maxFollowDistance = 300;
	int minFollowDistance = 100;
	int followTargetIndex = 0;
	
	public NpcWarriorAI(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		Player player;
		
		//follow the player
		double distance;
		
		if(targetPlayers != null && targetPlayers.size() > 0 && followTargetIndex > 0)
		{
			player = targetPlayers.get(followTargetIndex - 1);
			
			//in case the player set to follow is null, fall back to the first player in the list
			if(player == null)
				player = targetPlayers.get(0);
			
			distance = (int)actor.getDistance(player);
			
			if(distance > maxFollowDistance)
			{
				if(GeoEngine.canSeeTarget(actor, player, false))
				{
					//in case the NPC can see the player
					actor.setRunning();
					Location loc = new Location(player.getX() + Rnd.get(-60, 60), player.getY() + Rnd.get(-60, 60), player.getZ());
					actor.followToCharacter(loc, player, minFollowDistance, false);
				}
				else
				{
					//in case the NPC cannot see the player, then teleport straight to him
					actor.teleToLocation(player.getLoc().getRandomLoc(100));
				}
				return true;
			}
		}
		
		
		return startAttack();
			
	}

	@Override
	protected boolean createNewTask()
	{
		return defaultFightTask();
	}
	
	private boolean startAttack()
	{
		NpcInstance actor = getActor();
		
		if(attackTarget !=null && (attackTarget.isDead() || !GeoEngine.canSeeTarget(actor, attackTarget, false))) 
			attackTarget = null;
		
		if(attackTarget == null)
		{
			//set new attack target
			List<NpcInstance> around = actor.getAroundNpc(2000, 150);
			if(around != null && !around.isEmpty())
			{
				for(NpcInstance npc : around)
				{
					if(checkAttackTarget(npc))
					{
						if(attackTarget == null)
							attackTarget = npc;
						else if(actor.getDistance3D(npc) < actor.getDistance3D(attackTarget))
							attackTarget = npc;
						
					}
				}
			}
			else
			{
				return false;
			}
			
			if(attackTarget != null && !actor.isAttackingNow() && !actor.isCastingNow())
			{
				actor.getAggroList().addDamageHate(attackTarget, 10, 10);
				actor.setAggressionTarget(attackTarget);
				actor.setRunning();
				setIntention(CtrlIntention.AI_INTENTION_ATTACK, attackTarget);
				return true;
			}
			
		}
		
		return false;
	}
	
	@Override
	protected void thinkAttack()
	{
		NpcInstance actor = getActor();
		if(actor.isDead())
			return;

		if(doTask() && !actor.isAttackingNow() && !actor.isCastingNow())
		{
			if(!createNewTask())
			{
				if(System.currentTimeMillis() > getAttackTimeout() && !(actor instanceof DecoyInstance))
					returnHome();
			}
		}
	}

	@Override
	protected boolean isGlobalAggro()
	{
		return true;
	}
	
	private boolean checkAttackTarget(NpcInstance attackTarget)
	{
		if(attackTarget == null)
			return false;
		
		if (((NpcInstance) attackTarget).isInFaction(getActor()) ||
			!attackTarget.isVisible() || 
			attackTarget.isDead() || 
			!GeoEngine.canSeeTarget(getActor(), attackTarget, false))
		{
			return false;
		}
		
		return true;
	}

	@Override
	public int getMaxAttackTimeout()
	{
		return 0;
	}

	@Override
	protected boolean randomWalk()
	{
		return true;
	}
	

	@Override
	public int getRateDOT()
	{
		return 0;
	}

	@Override
	public int getRateDEBUFF()
	{
		return 30;
	}

	@Override
	public int getRateDAM()
	{
		return 30;
	}

	@Override
	public int getRateSTUN()
	{
		return 30;
	}

	@Override
	public int getRateBUFF()
	{
		return 90;
	}

	@Override
	public int getRateHEAL()
	{
		return 90;
	}
	
	public List<Player> getTargetPlayer()
	{
		return targetPlayers;
	}
	
	public void setTargetPlayer(Player... _targetPlayers)
	{
		targetPlayers = new LazyList<Player>();
		
		for(Player _targetPlayer : _targetPlayers)
			targetPlayers.add(_targetPlayer);
	}
	
	public int getMaxFollowDistance()
	{
		return maxFollowDistance;
	}
	
	public void setMaxFollowDistance(int distance)
	{
		maxFollowDistance = distance;
	}
	
	public int getMinFollowDistance()
	{
		return minFollowDistance;
	}
	
	public void setMinFollowDistance(int distance)
	{
		minFollowDistance = distance;
	}
	
	public int getFollow()
	{
		return followTargetIndex;
	}
	
	public void setFollow(int targetIndex)
	{
		followTargetIndex = targetIndex;
	}
}