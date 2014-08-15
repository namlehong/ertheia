package ai;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.Location;

/**
 * @Author Awakeninger
 * Класс бегунов
 * Для каждого бегуна свои координаты и общие параметры.
 **/
public class OrbisScout extends Fighter
{
	static final Location[] points1 = {
		new Location(207784, 53656, -8574),
		new Location(207784, 54664, -8574),
		new Location(209048, 54664, -8574),
		new Location(209800, 54664, -8702),
		new Location(209048, 54664, -8574),
		new Location(207784, 54664, -8574),
		new Location(207784, 53656, -8574)
	};

	static final Location[] points2 = {
		new Location(207784, 53352, -8638),
		new Location(207784, 51640, -8671),
		new Location(207784, 50056, -8606),
		new Location(207784, 48712, -8606),
		new Location(207784, 46424, -8574),
		new Location(207784, 48712, -8606),
		new Location(207784, 50056, -8606),
		new Location(207784, 51640, -8671),
		new Location(207784, 53352, -8638)
	};

	/**
	 Current Points + порядковый номер от points
	 **/
	private int current_point1 = -1;
	private int current_point2 = -1;
	/**===========================================**/

	/**
	 wait_timeout + порядковый номер от points
	 **/
	private long wait_timeout1 = 0;
	private long wait_timeout2 = 0;
	/**===========================================**/

	//private Location Loc1 = new Location(207784, 53656, -8574);
	private boolean wait1 = false;
	private boolean wait2 = false;

	public OrbisScout(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	public boolean checkAggression(Creature target)
	{
		// Продолжит идти с предыдущей точки
		// Опять же для каждого свое
		if(getIntention() != CtrlIntention.AI_INTENTION_ACTIVE && current_point1 > -1)
		{
			current_point1--;
		}

		if(getIntention() != CtrlIntention.AI_INTENTION_ACTIVE && current_point2 > -1)
		{
			current_point2--;
		}
		return super.checkAggression(target);
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if(actor.isDead())
		{
			return true;
		}

		if(_def_think)
		{
			if(doTask())
			{
				clearTasks();
			}
			return true;
		}

		// BUFF
		if(super.thinkActive())
		{
			return true;
		}

		if(System.currentTimeMillis() > wait_timeout1 && (current_point1 > -1 || Rnd.chance(5)))
		{
			if(!wait1 && current_point1 == 4)
			{
				wait_timeout1 = System.currentTimeMillis() + 60000;
				wait1 = true;
				return true;
			}
			wait_timeout1 = 0;
			wait1 = false;
			current_point1++;
			if(current_point1 >= points1.length)
			{
				current_point1 = 0;
			}
			actor.setWalking();
			addTaskMove(points1[current_point1], true);
			doTask();
			return true;
		}

		if(System.currentTimeMillis() > wait_timeout2 && (current_point2 > -1 || Rnd.chance(5)))
		{
			if(!wait2 && current_point2 == 5)
			{
				wait_timeout2 = System.currentTimeMillis() + 60000;
				wait2 = true;
				return true;
			}
			wait_timeout2 = 0;
			wait2 = false;
			current_point2++;
			if(current_point2 >= points2.length)
			{
				current_point2 = 0;
			}
			actor.setWalking();
			addTaskMove(points2[current_point2], true);
			doTask();
			return true;
		}

		if(randomAnimation())
		{
			return false;
		}

		return false;
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}