package ai;

import java.util.List;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.utils.Location;

public class Rooney extends DefaultAI
{
	private static final Location[] LOCATIONS = {
			new Location(175937, -112167, -5550), // 1
			new Location(178896, -112425, -5860), // 2
			new Location(180628, -115992, -6135), // 3
			new Location(183010, -114753, -6135), // 4
			new Location(184496, -116773, -6135), // 5
			new Location(181857, -109491, -5865), // 6
			new Location(178917, -107633, -5853), // 7
			new Location(178804, -110080, -5853), // 8
			new Location(182221, -106806, -6025), // 9
			new Location(186488, -109715, -5915), // 10
			new Location(183847, -119231, -3113), // 11
			new Location(185193, -120342, -3113), // 12
			new Location(188047, -120867, -3113), // 13
			new Location(189734, -120471, -3113), // 14
			new Location(188754, -118940, -3313), // 15
			new Location(190022, -116803, -3313), // 16
			new Location(188443, -115814, -3313), // 17
			new Location(186421, -114614, -3313), // 18
			new Location(185188, -113307, -3313), // 19
			new Location(187378, -112946, -3313), // 20
			new Location(189815, -113425, -3313), // 21
			new Location(189301, -111327, -3313), // 22
			new Location(190289, -109176, -3313), // 23
			new Location(187783, -110478, -3313), // 24
			new Location(185889, -109990, -3313), // 25
			new Location(181881, -109060, -3695), // 26
			new Location(183570, -111344, -3675), // 27
			new Location(182077, -112567, -3695), // 28
			new Location(180127, -112776, -3698), // 29
			new Location(179155, -108629, -3695), // 30
			new Location(176282, -109510, -3698), // 31
			new Location(176071, -113163, -3515), // 32
			new Location(179376, -117056, -3640), // 33
			new Location(179760, -115385, -3640), // 34
			new Location(177950, -119691, -4140), // 35
			new Location(177037, -120820, -4340), // 36
			new Location(181125, -120148, -3702), // 37
			new Location(182212, -117969, -3352), // 38
			new Location(186074, -118154, -3312) // 39
			};

	private static final NpcString[] SHOUTS = {
		NpcString.WELCOME,
		NpcString.HURRY_HURRY,
		NpcString.I_AM_NOT_THAT_TYPE_OF_PERSON_WHO_STAYS_IN_ONE_PLACE_FOR_A_LONG_TIME,
		NpcString.ITS_HARD_FOR_ME_TO_KEEP_STANDING_LIKE_THIS,
		NpcString.WHY_DONT_I_GO_THAT_WAY_THIS_TIME
	};

	private boolean _discoveredByPlayer = false;

	public Rooney(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtThink()
	{
		if(!_discoveredByPlayer)
		{
			final List<Player> players = World.getAroundPlayers(getActor(), 1500, 400);
			if(!players.isEmpty())
			{
				_discoveredByPlayer = true;
				ThreadPoolManager.getInstance().schedule(new ShoutTask(), 100);
				ThreadPoolManager.getInstance().schedule(new TeleportTask(LOCATIONS[Rnd.get(LOCATIONS.length)]), 600000); // 10 min
			}
		}
		super.onEvtThink();
	}

	private final class ShoutTask extends RunnableImpl
	{
		private int _idx = 0;

		@Override
		public final void runImpl()
		{
			if(_idx < SHOUTS.length)
			{
				Functions.npcShout(getActor(), SHOUTS[_idx]);
				_idx++;
				ThreadPoolManager.getInstance().schedule(this, 120000); // 2 min
			}
		}
	}

	private final class TeleportTask extends RunnableImpl
	{
		private final Location _loc;

		public TeleportTask(Location loc)
		{
			_loc = loc;
		}

		@Override
		public final void runImpl()
		{
			getActor().teleToLocation(_loc);
			_discoveredByPlayer = false;
		}
	}
}
