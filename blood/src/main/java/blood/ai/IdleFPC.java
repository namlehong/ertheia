package blood.ai;

import java.util.List;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;

public class IdleFPC extends FPCDefaultAI
{

	public IdleFPC(Player actor)
	{
		super(actor);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onEvtThink()
	{
		Player actor = getActor();
		if(actor.isDead()) /* TODO or res your self */
		{
			//return to village
			actor.teleToClosestTown();
			actor.doRevive();
		}
		thinkActive();
	}
	
	@Override
	protected void thinkActive()
	{
		Player 	actor 	= getActor();
		//FPCInfo player	= FPCInfo.getInstance(actor);
		
		if (actor.isActionsDisabled())
		{
			return;
		}
		
		if (_def_think)
		{
			if (doTask())
			{
				clearTasks();
			}
			return;
		}
		
		//player.cancelShop();
		
		if(Rnd.chance(0.5))
		{
			actor.standUp();
			if(Rnd.chance(20))	randomWalk(1000);
			else
			{
				List<GameObject> chars 	= World.getAroundObjects(actor, 2000, 500);
				if(chars.size() == 0) return;
				GameObject randomChar		= chars.get(Rnd.get(chars.size()));
				if(!randomChar.isDoor())
					actor.moveToLocation(randomChar.getLoc(), 50, true);
			}
		}
		
	}
	
}
