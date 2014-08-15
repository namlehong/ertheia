package ai.land_of_chaos;

import org.apache.commons.lang3.ArrayUtils;

import l2s.commons.geometry.Circle;
import l2s.commons.util.Rnd;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Territory;
import l2s.gameserver.model.World;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.FlyToLocationPacket;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;

/**
 * @author Bonux
**/
public class ChaosHorn extends Fighter
{
	private static enum HornType
	{
		NORMAL,
		RED,
		GREEN,
		BLUE;
	}

	private static final Skill CHAOTIC_REIGN_SKILL = SkillTable.getInstance().getInfo(15569, 1);	// Chaotic Reign

	private final static int HIT_COUNT_RESET_TIMER_ID = 8000;
	private final static int NORMAL_HORN_DESPAWN_TIMER_ID = 9000;
	private final static int NORMAL_HORN_WALK_TIMER_ID = 10000;

	private final static int NORMAL_HORN_NPC_ID = 23348;	// Chaos Horn
	private final static int NORMAL_HORN_SPAWN_MIN_COUNT = 5;	// TODO: Check this.
	private final static int NORMAL_HORN_SPAWN_MAX_COUNT = 5;	// TODO: Check this.

	private final static int PUTREFIED_ZOMBIE_NPC_ID = 23333;	// Putrefied Zombie
	private final static int PUTREFIED_ZOMBIE_SPAWN_MIN_COUNT = 8;	// TODO: Check this.
	private final static int PUTREFIED_ZOMBIE_SPAWN_MAX_COUNT = 8;	// TODO: Check this.

	private final static int GREEN_HORN_MIN_PULL_COUNT = 2;	// TODO: Check this.
	private final static int GREEN_HORN_MAX_PULL_COUNT = 5;	// TODO: Check this.

	private final static int[] PULL_NPC_IDS_LIST = {
		23334,	// Shelop
		23335,	// Poras
		23336,	// Death Worm
		23337	// Large Rubble
	};

	private final HornType _type;

	private int _hitCount = 0;

	public ChaosHorn(NpcInstance actor)
	{
		super(actor);

		_type = HornType.valueOf(actor.getParameter("horn_type", "NORMAL").toUpperCase());
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();

		if(_type == HornType.NORMAL)
		{
			addTimer(NORMAL_HORN_WALK_TIMER_ID, 1500L);
			addTimer(NORMAL_HORN_DESPAWN_TIMER_ID, 25000L);
		}
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		_hitCount++;

		if(_type != HornType.NORMAL)
		{
			stopTask(HIT_COUNT_RESET_TIMER_ID);

			if(_type == HornType.RED)
			{
				if(_hitCount >= 2)
				{
					Location loc = getActor().getLoc();
					Reflection reflection = getActor().getReflection();
					int count = Rnd.get(PUTREFIED_ZOMBIE_SPAWN_MIN_COUNT, PUTREFIED_ZOMBIE_SPAWN_MAX_COUNT);

					for(int i = 0; i < count; i++)
					{
						NpcInstance npc = NpcUtils.spawnSingle(PUTREFIED_ZOMBIE_NPC_ID, loc, reflection);
						npc.getAI().addTimer(SummonedZombie.AGGRESSION_TIMER_ID, attacker, 1500L);
					}

					getActor().doDie(attacker);
					return;
				}
			}
			else if(_type == HornType.GREEN)
			{
				if(_hitCount >= 5)
				{
					getActor().doCast(CHAOTIC_REIGN_SKILL, null, false); // Кастуем скилл чисто для анимации, остальное захардкодено ниже.

					Territory territory = new Territory();
					territory.add(new Circle(getActor().getX(), getActor().getY(), 100).setZmin(getActor().getZ() - 50).setZmax(getActor().getZ() + 50));

					final int maxPullCount = Rnd.get(GREEN_HORN_MIN_PULL_COUNT, GREEN_HORN_MAX_PULL_COUNT);

					int pulled = 0;
					for(NpcInstance npc : World.getAroundNpc(getActor(), 1000, 200))
					{
						if(!ArrayUtils.contains(PULL_NPC_IDS_LIST, npc.getNpcId()))
							continue;

						Location loc = territory.getRandomLoc(getActor().getGeoIndex());
						npc.broadcastPacket(new FlyToLocationPacket(npc, loc, FlyToLocationPacket.FlyType.THROW_HORIZONTAL, 800, 0, 0));
						npc.setLoc(loc);
						npc.validateLocation(1);

						pulled++;

						if(pulled >= maxPullCount)
							break;
					}
					getActor().doDie(attacker);
				}
				return;
			}
			else if(_type == HornType.BLUE)
			{
				if(_hitCount >= 2)
				{
					Location loc = getActor().getLoc();
					Reflection reflection = getActor().getReflection();
					int count = Rnd.get(NORMAL_HORN_SPAWN_MIN_COUNT, NORMAL_HORN_SPAWN_MAX_COUNT);

					for(int i = 0; i < count; i++)
						NpcUtils.spawnSingle(NORMAL_HORN_NPC_ID, loc, reflection);

					getActor().doDie(attacker);
					return;
				}
			}

			addTask(HIT_COUNT_RESET_TIMER_ID, 10000L);
		}

		super.onEvtAttacked(attacker, damage);
	}

	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{
		if(_type == HornType.GREEN)
			return;

		super.onEvtAggression(target, aggro);
	}

	@Override
	protected boolean randomWalk()
	{
		if(_type == HornType.NORMAL)
			return super.randomWalk();

		return false;
	}

	@Override
	protected void onEvtTimer(int timerId, Object arg1, Object arg2)
	{
		super.onEvtTimer(timerId, arg1, arg2);

		if(timerId == HIT_COUNT_RESET_TIMER_ID)
			_hitCount = 0;
		else if(timerId == NORMAL_HORN_DESPAWN_TIMER_ID)
		{
			if(_hitCount == 0)
				getActor().deleteMe();
			else
			{
				_hitCount = 0;
				addTimer(NORMAL_HORN_DESPAWN_TIMER_ID, 25000L);
			}
		}
		else if(timerId == NORMAL_HORN_WALK_TIMER_ID)
		{
			Territory territory = new Territory();
			territory.add(new Circle(getActor().getX(), getActor().getY(), 100).setZmin(getActor().getZ() - 50).setZmax(getActor().getZ() + 50));
			
			getActor().moveToLocation(territory.getRandomLoc(getActor().getGeoIndex()), 0, true);
		}
	}
}