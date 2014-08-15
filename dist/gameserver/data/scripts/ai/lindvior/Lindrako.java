package ai.lindvior;

import java.util.List;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.World;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.network.l2.components.ChatType;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.ai.CtrlEvent;
import org.apache.commons.lang3.ArrayUtils;

import instances.LindviorBoss;

/**
Lindrako by iqman
 */

public class Lindrako extends Fighter
{
	private static final int[] ATTACK_IDS = {19479, 19477};
	
	public Lindrako(NpcInstance actor)
	{
		super(actor);
	}

	//@Override
/*	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		List<Creature> attackers = World.getAroundCharacters(getActor(), 800, 300); // MUST BE someone
		for(Creature attacker : attackers)
		{
			if(attacker == null)
				continue;
			if(attacker.getNpcId() == 19479 || attacker.getNpcId() == 19477)
			{
				getActor().getAggroList().addDamageHate(attacker, 100000, 0);
				break;
			}
			else if(attacker.isNpc() || attacker.isMonster()) //only above can attack
				continue;
			getActor().getAggroList().addDamageHate(attacker, 100000, 0);
			break;			
		} //will take only the first one priority on those two
		
	}
	*/
/*	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if(actor.isDead())
			return false;
		List<Creature> list = World.getAroundCharacters(getActor(), getActor().getAggroRange(), getActor().getAggroRange());
		for(Creature target : list)
		{
			if(target != null && !target.isDead() && ArrayUtils.contains(ATTACK_IDS, target.getNpcId()))
			{
				clearTasks();
				actor.setRunning();
				addTaskAttack(target);
				return super.thinkActive();
			}	
		}
		return super.thinkActive();	
	}	
	*/
	@Override
	protected void onEvtDead(Creature killer)
	{
		super.onEvtDead(killer);
		int scheduledId = 0;
		int num = Rnd.get(1, 3);
		if(num == 1)
			scheduledId = 29241;
		else if(num == 2)
			scheduledId = 29242;
		else
			scheduledId = 29243;
		Reflection r = getActor().getReflection();	
		if (r != null)
		{
			if(r instanceof LindviorBoss)
			{
				LindviorBoss lInst = (LindviorBoss) r;	
				lInst.scheduleNextSpawnFor(scheduledId, 60000L, getActor().getSpawnedLoc(), Rnd.get(1,3));
			}	
		}		
	}	
}