package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2s.gameserver.Config;
import l2s.gameserver.model.entity.events.impl.FightBattleEvent;
import l2s.gameserver.model.entity.events.objects.FightBattleArenaObject;
import l2s.gameserver.model.entity.olympiad.Olympiad;
import l2s.gameserver.model.entity.olympiad.OlympiadGame;
import l2s.gameserver.model.entity.olympiad.OlympiadManager;
import l2s.gameserver.model.entity.olympiad.OlympiadMember;
import l2s.gameserver.network.l2.ServerPacketOpcodes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author VISTALL
 * @date 0:50/09.04.2011
 */
public abstract class ExReceiveOlympiadPacket extends L2GameServerPacket
{
	private static final Logger _log = LoggerFactory.getLogger(ExReceiveOlympiadPacket.class);

	public static class MatchList extends ExReceiveOlympiadPacket
	{
		private List<ArenaInfo> _arenaList = Collections.emptyList();

		public MatchList()
		{
			super(0);
			OlympiadManager manager = Olympiad._manager;
			if(manager != null)
			{
				_arenaList = new ArrayList<ArenaInfo>();
				for(int i = 0; i < Olympiad.STADIUMS.length; i++)
				{
					OlympiadGame game = manager.getOlympiadInstance(i);
					if(game != null && game.getState() > 0)
						_arenaList.add(new ArenaInfo(i, game.getState(), game.getType().ordinal(), game.getMemberName1(), game.getMemberName2()));
				}
			}
		}

		public MatchList(FightBattleEvent event)
		{
			super(0);
			_arenaList = new ArrayList<ArenaInfo>();
			for(FightBattleArenaObject arena : event.getArenas())
			{
				if(!arena.isBattleBegin())
					continue;

				_arenaList.add(new ArenaInfo(arena.getId(), 2, 1, arena.getMember1().getName(), arena.getMember2().getName()));
			}
		}

		@Override
		protected void writeImpl()
		{
			super.writeImpl();
			writeD(_arenaList.size());
			writeD(0x00); //unknown
			for(ArenaInfo arena : _arenaList)
			{
				writeD(arena._id);
				writeD(arena._matchType);
				writeD(arena._status);
				writeS(arena._name1);
				writeS(arena._name2);
			}
		}

		private static class ArenaInfo
		{
			private int _id, _status, _matchType;
			private String _name1, _name2;

			public ArenaInfo(int id, int status, int match_type, String name1, String name2)
			{
				_id = id;
				_status = status;
				_matchType = match_type;
				_name1 = name1;
				_name2 = name2;
			}
		}
	}

	public static class MatchResult extends ExReceiveOlympiadPacket
	{
		private final static int TEAMS_COUNT = 2;

		private List<PlayerInfo> _team1_players;
		private List<PlayerInfo> _team2_players;
		private boolean _tie;
		private String _name;

		public MatchResult(boolean tie, String name)
		{
			super(1);
			_tie = tie;
			_name = name;
		}

		public void addPlayer(int team, OlympiadMember member, int gameResultPoints, int dealOutDamage)
		{
			int points = Config.OLYMPIAD_OLDSTYLE_STAT ? 0 : member.getStat().getInteger(Olympiad.POINTS, 0);

			PlayerInfo playerInfo = new PlayerInfo(member.getName(), member.getClanName(), member.getClassId(), points, gameResultPoints, dealOutDamage);
			switch(team)
			{
				case 1:
					if(_team1_players == null)
						_team1_players = new ArrayList<PlayerInfo>(2);

					_team1_players.add(playerInfo);
					break;
				case 2:
					if(_team2_players == null)
						_team2_players = new ArrayList<PlayerInfo>(2);

					_team2_players.add(playerInfo);
					break;
			}
		}

		@Override
		protected void writeImpl()
		{
			super.writeImpl();
			writeD(_tie);
			writeS(_name);
			for(int i = 1; i <= TEAMS_COUNT; i++)
			{
				writeD(i);
				List<PlayerInfo> players = (i == 1 ? _team1_players : (i == 2 ? _team2_players : null));
				if(players != null && !players.isEmpty())
				{
					writeD(players.size());
					for(PlayerInfo playerInfo : players)
					{
						writeS(playerInfo._name);
						writeS(playerInfo._clanName);
						writeD(0x00);
						writeD(playerInfo._classId);
						writeD(playerInfo._damage);
						writeD(playerInfo._currentPoints);
						writeD(playerInfo._gamePoints);
					}
				}
				else
					writeD(0x00);
			}
		}

		private static class PlayerInfo
		{
			private String _name, _clanName;
			private int _classId, _currentPoints, _gamePoints, _damage;

			public PlayerInfo(String name, String clanName, int classId, int currentPoints, int gamePoints, int damage)
			{
				_name = name;
				_clanName = clanName;
				_classId = classId;
				_currentPoints = currentPoints;
				_gamePoints = gamePoints;
				_damage = damage;
			}
		}
	}

	private int _type;

	public ExReceiveOlympiadPacket(int type)
	{
		_type = type;
	}

	@Override
	protected ServerPacketOpcodes getOpcodes()
	{
		return ServerPacketOpcodes.ExReceiveOlympiadPacket;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_type);
	}
}
