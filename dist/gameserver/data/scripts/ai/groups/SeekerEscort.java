package ai.groups;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.ChatType;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExRotation;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.scripts.Functions;

import quests._10365_SeekerEscort;

/**
 * Ai для ищейки по квесту Охрана рейнджера
 */
public class SeekerEscort extends DefaultAI
{
	private final static int SeekerEscort = 32988;

	//private final static NpcString SeekerEscort_STRING = NpcString.RUFF_RUFF_RRRRRR;

	private static final int SAY_INTERVAL = 3000;
	private static final int SAY_RAFF = 50000;
	private final static int[][] SMP_COORDS = {
			{-110648, 238936, -2950},
			{-110840, 239240, -2951},
			{-111000, 239512, -2950},
			{-111096, 239800, -2950},
			{-110728, 240248, -2950},
			{-110776, 240584, -2951},
			{-111064, 240616, -2950},
			{-111304, 240360, -2950},
			{-111672, 239848, -2950},
			{-111944, 239736, -2950},
			{-112296, 239816, -2950},
			{-112616, 239928, -2950},
			{-112696, 240264, -2950},
			{-112456, 240536, -2950},
			{-112120, 240536, -2950},
			{-112024, 240264, -2950},
			{-112216, 240168, -2950},
			{-112328, 240232, -2950}, //Последняя координата в 4ой зоне

	};

	private final static int[][] SMP_COORDS2 = {
			{-112776, 234072, -3097},
			{-112376, 233656, -3137},
			{-112184, 233480, -3156},
			{-112152, 233112, -3159},
			{-112472, 232920, -3128},
			{-112712, 232536, -3104},
			{-112504, 232040, -3125},
			{-112248, 232072, -3149},
			{-112088, 232360, -3165},
			{-111720, 232584, -3202},
			{-111240, 232728, -3250},
			{-110792, 232424, -3294},
			{-110776, 232072, -3295},
			{-111144, 231864, -3259},
			{-111512, 231992, -3223},
			{-111784, 231976, -3196},
			{-111736, 231816, -3200}, //Последняя координата в 5ой зоне
	};

	private int currentState;
	private long lastSayTime = 0;
	private long lastSayTimer = 0;
	private int currentState1;

	public SeekerEscort(NpcInstance actor)
	{
		super(actor);
		currentState = 0;
		lastSayTime = 0;
		lastSayTimer = 0;
		currentState1 = 0;
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		Creature target = actor.getFollowTarget();

		int[][] coords;
		if(target == null || !(target instanceof Player))
		{
			actor.deleteMe();
			return false;
		}

		Player player = target.getPlayer();
		QuestState st = player.getQuestState(_10365_SeekerEscort.class);
		if(st == null)
		{
			actor.deleteMe();
			return false;
		}

		int zone = st.getInt("zone");
		int saytimes = st.getInt("saytimes");
		int cond = st.getCond();

		actor.setRunning();

		if(saytimes == 9 || cond == 0)
		{
			actor.deleteMe();
			st.set("seeksp", 0, false);
			st.set("zone", 1, false);
			st.unset("saytimes");
			target.sendPacket(new ExShowScreenMessage(NpcString.KING_HAS_RETURNED_TO_DEF_RETURN_TO_DEF_AND_START_AGAIN, 5500, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
			return false;
		}

		if(lastSayTimer + SAY_RAFF < System.currentTimeMillis())
		{
			lastSayTimer = System.currentTimeMillis();
			Functions.npcSay(actor, NpcString.RUFF_RUFF_RRRRRR, ChatType.NPC_ALL, 800, st.getPlayer().getName());
		}

		if(!actor.isMoving)
		{
			if(zone == 1)
			{
				coords = SMP_COORDS;
				if(actor.getDistance(target) < 100 || currentState >= coords.length || currentState == 0)
				{
					st.unset("saytimes");
					if(currentState < coords.length)
					{
						actor.moveToLocation(coords[currentState][0], coords[currentState][1], coords[currentState][2], Rnd.get(0, 50), true);
						currentState++;
					}
					else
					{
						actor.teleToLocation(-110906, 233763, -3200);
						st.set("zone", 2, false);
					}
				}
				else if(lastSayTime + SAY_INTERVAL < System.currentTimeMillis())
				{
					int heading = actor.calcHeading(target.getX(), target.getY());
					actor.setHeading(heading);
					actor.broadcastPacket(new ExRotation(actor.getObjectId(), heading));
					lastSayTime = System.currentTimeMillis();
					target.sendPacket(new ExShowScreenMessage(NpcString.CATCH_UP_TO_KING_HES_WAITING, 1500, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
					st.set("saytimes", ++saytimes, false);
				}
			}
			else if(zone == 2)
			{
				if(actor.getDistance(target) >= 100 && lastSayTime + SAY_INTERVAL < System.currentTimeMillis()) 
				{
					lastSayTime = System.currentTimeMillis();
					target.sendPacket(new ExShowScreenMessage(NpcString.YOU_MUST_MOVE_TO_EXPLORATION_AREA_5_IN_ORDER_TO_CONTINUE, 2000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
					st.set("saytimes", ++saytimes, false);
				}
				else if(actor.getDistance(target) < 100)
					st.set("zone", 3, false);
				st.unset("saytimes");
			}
			else if(zone == 3)
			{
				coords = SMP_COORDS2;
				if(actor.getDistance(target) < 100 || currentState1 >= coords.length)
				{
					if(currentState1 < coords.length)
					{
						st.unset("saytimes");
						actor.moveToLocation(coords[currentState1][0], coords[currentState1][1], coords[currentState1][2], Rnd.get(0, 50), true);
						++currentState1;
					}
					else
					{
						actor.deleteMe();
						st.set("seeksp", 0, false);
						st.set("zone", 1, false);
						st.setCond(2);
					}
				}
				else if(lastSayTime + SAY_INTERVAL < System.currentTimeMillis())
				{
					int heading = actor.calcHeading(target.getX(), target.getY());
					actor.setHeading(heading);
					actor.broadcastPacket(new ExRotation(actor.getObjectId(), heading));
					lastSayTime = System.currentTimeMillis();
					target.sendPacket(new ExShowScreenMessage(NpcString.CATCH_UP_TO_KING_HES_WAITING, 1500, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
					st.set("saytimes", ++saytimes, false);
				}
			}
		}
		return true;
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}