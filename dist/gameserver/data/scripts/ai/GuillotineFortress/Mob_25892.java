package ai.GuillotineFortress;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import l2s.commons.threading.RunnableImpl;

import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.components.UsmVideo;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;

/*
 * User: Iqman
 * Date: 21.05.13
 * Time: 19:39
 * Location: Guillotine Fortress raid stage 1
 */
//TODO find the manument ID and their skill when they ressurect the raid boss
public class Mob_25892 extends Fighter
{
	private static final Location loc1 = new Location(43992, 155928, -1079, 0);
	private static final Location loc2 = new Location(44024, 156072, -1079, 0);
	private static final Location loc3 = new Location(44056, 156072, -1079, 0);
	private static final Location loc4 = new Location(44168, 156536, -1079, 0);
	private static final Location loc5 = new Location(44296, 156632, -1079, 0);
	private static final Location loc6 = new Location(44440, 156760, -1079, 0);
	private static final Location loc7 = new Location(44680, 156856, -1079, 0);
	private static final Location loc8 = new Location(44808, 156872, -1079, 0);
	private static final Location loc9 = new Location(44952, 156888, -1079, 0);
	private static final Location loc10 = new Location(45256, 156856, -1079, 0);
	private static final Location loc11 = new Location(45432, 156744, -1079, 0);
	private static final Location loc12 = new Location(45592, 156664, -1079, 0);
	private static final Location loc13 = new Location(45752, 156504, -1079, 0);
	private static final Location loc14 = new Location(45800, 156376, -1079, 0);
	private static final Location loc15 = new Location(45880, 156232, -1079, 0);
	private static final Location loc16 = new Location(45896, 155928, -1079, 0);
	private static final Location loc17 = new Location(45880, 155768, -1079, 0);
	private static final Location loc18 = new Location(45864, 155608, -1079, 0);
	private static final Location loc19 = new Location(45752, 155368, -1079, 0);
	private static final Location loc20 = new Location(45608, 155240, -1079, 0);
	private static final Location loc21 = new Location(45480, 155128, -1079, 0);
	private static final Location loc22 = new Location(45240, 155032, -1079, 0);
	private static final Location loc23 = new Location(45016, 155016, -1079, 0);
	private static final Location loc24 = new Location(44856, 155000, -1079, 0);
	private static final Location loc25 = new Location(44632, 155064, -1079, 0);
	private static final Location loc26 = new Location(44520, 155144, -1079, 0);
	private static final Location loc27 = new Location(44344, 155208, -1079, 0);
	private static final Location loc28 = new Location(44168, 155416, -1079, 0);
	private static final Location loc29 = new Location(44120, 155544, -1079, 0);
	private static final Location loc30 = new Location(44040, 155688, -1079, 0);
	private static int i = 1;
	
	private List<Location> _list = new ArrayList<Location>();
	private ScheduledFuture<?> _task = null;
	
    public Mob_25892(NpcInstance actor)
	{
        super(actor);
    }

	@Override
	protected void onEvtSpawn()
	{
		_list = new ArrayList<Location>(); //better to init again
		//maybe better method?
		_list.add(loc1);
		_list.add(loc2);
		_list.add(loc3);
		_list.add(loc4);
		_list.add(loc5);
		_list.add(loc6);
		_list.add(loc7);
		_list.add(loc8);
		_list.add(loc9);
		_list.add(loc10);
		_list.add(loc11);
		_list.add(loc12);
		_list.add(loc13);
		_list.add(loc14);
		_list.add(loc15);
		_list.add(loc16);
		_list.add(loc17);
		_list.add(loc18);
		_list.add(loc19);
		_list.add(loc20);
		_list.add(loc21);
		_list.add(loc22);
		_list.add(loc23);
		_list.add(loc24);
		_list.add(loc25);
		_list.add(loc26);
		_list.add(loc27);
		_list.add(loc28);
		_list.add(loc29);
		_list.add(loc30);
		i = 1;
		super.onEvtSpawn();
		_task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new MassAttack(), 60000, 10000);
		ThreadPoolManager.getInstance().schedule(new Despawn(), 7200000L); //2 hr for raid should be enough
	}

	public class MassAttack extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if(getActor() == null) //in case the rb is dead :)
				return;
			if(i > 6) //6 quick waves acc the video one on each loc ; it's around 180 mobs :) in video also planty
			{
				if(_task != null)
					_task.cancel(false);			
				return;
			}	
			for(Location loc : _list)
				NpcUtils.spawnSingle(25893, loc);
			if(i == 1)
			{
				for(Player player : World.getAroundPlayers(getActor(), 1200, 1200))
					player.sendPacket(UsmVideo.Q012.packet(player));
			}	
			i++;
				
		}
	}
	
	private class Despawn extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if(getActor() != null)
				getActor().deleteMe();
		}
	}
	
	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}
